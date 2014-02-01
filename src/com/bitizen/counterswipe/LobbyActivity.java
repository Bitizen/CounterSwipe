package com.bitizen.counterswipe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.bitizen.camera.CameraActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class LobbyActivity extends Activity {

	private RadioButton myRb;
	private RadioButton rbRed, rbBlue, rbGreen, rbYellow, rbOrange;
	private RadioGroup teamARg, teamBRg, rgColors;
	private Button setColorB;

	private Boolean isReady = false;
	private String result;
	private String message;
	private String myColor;
	private String username, match, team;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	private final String KEY_LOBBY = "LOBBY";
	private final String KEY_IAMIDLE = "IAMIDLE";
	private final String KEY_IAMREADY = "IAMREADY";
	private final String KEY_EMPTY_MATCH = "LOBBY-[]-[]";
	private final String KEY_CHANGEMYCOLOR = "CHANGEMYCOLOR";
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	
	private static final String KEY_GET_USERNAME	= "username: ";
	private static final String KEY_USERNAME_AVAIL 	= "uname available!";
	private static final String KEY_USERNAME_TAKEN 	= "uname taken";
	private static final String KEY_MATCH_AVAIL 	= "available";
	private static final String KEY_MATCH_FULL 		= "full";
	private static final String KEY_TEAM_AVAIL 		= "team available";
	private static final String KEY_TEAM_FULL 		= "team full";
	private static final String KEY_INVALID			= "invalid";
	private static final String KEY_READY_USER 		= "waiting for user ready...";
	private static final String KEY_READY_MATCH 	= "waiting for match ready...";
	private static final String KEY_START_GAME 		= "start game";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		initializeElements();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);
			team = extras.getString(KEY_TEAM);
		}
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
        
		new Thread(new CheckerThread()).start();
	}
	
	private void initializeElements() {
		result = new String();
		message = new String();
		username = new String();
		match = new String();
		myColor = new String();
		
		teamARg = (RadioGroup) findViewById(R.id.rgTeamA);
		teamBRg = (RadioGroup) findViewById(R.id.rgTeamB);
		
		mBoundService = new SocketService();
		mConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBoundService = ((SocketService.LocalBinder)service).getService();
				mBoundService.registerHandler(serviceHandler);
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			     mBoundService = null;
			}
		};
		
		serviceHandler = new Handler() {
		    @Override
		    public void handleMessage(Message msg) {
		    	updateUI(msg);
		    }
		};
		
		// Wait for server reply
		Thread buffer = new Thread() {
			@Override
			public void run() {
				try {
					this.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					mBoundService.sendMessage("NEXT");
				}
				super.run();
			}
		};
		buffer.start();
	}
	
	class CheckerThread implements Runnable {
		 @Override
		public void run() {
			try{
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				checkForFlag();
			}
		}
	}
	
	private void toggleReady(RadioButton rb) {
		if (rb.isChecked()) {
        	mBoundService.sendMessage(KEY_IAMIDLE);
			rb.setChecked(false);	
			isReady = false;
		} else if (!rb.isChecked() ){
        	mBoundService.sendMessage(KEY_IAMREADY);
			rb.setChecked(true);
			isReady = true;
		}
		
	}
	
	private void checkForFlag() {
		//if (playerA1.isChecked() && playerB1.isChecked()) {
		//	Intent intent = new Intent(this, CameraActivity.class);
	    //	startActivityForResult(intent, REQ_CAMERA_IMAGE);
		mBoundService.sendMessage("NEXT");
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_player, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.mi_ready:
	            //Toast.makeText(LobbyActivity.this, "Ready is Selected", Toast.LENGTH_SHORT).show();
	        	toggleReady(myRb);
	            return true;
	        case R.id.mi_setSelfColor:
	        	popupColorDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }    
	
	private void popupColorDialog() {
		final Dialog dialog = new Dialog(CONTEXT);
	    dialog.setContentView(R.layout.dialog_changemycolor);
	    dialog.setTitle("Select your color:");
	    dialog.setCancelable(true);

	    rgColors = (RadioGroup) dialog.findViewById(R.id.rgColors);
	    rbRed = (RadioButton) dialog.findViewById(R.id.rbRed);
	    rbBlue = (RadioButton) dialog.findViewById(R.id.rbBlue);
	    rbOrange = (RadioButton) dialog.findViewById(R.id.rbOrange);
	    rbYellow = (RadioButton) dialog.findViewById(R.id.rbYellow);
	    rbGreen = (RadioButton) dialog.findViewById(R.id.rbGreen);
	    setColorB = (Button) dialog.findViewById(R.id.bSetMyColor);
	    
	    setColorB.setOnClickListener(new OnClickListener() {
	    	@Override
            public void onClick(View v) {
	    		int rbID = rgColors.getCheckedRadioButtonId();
	    		
				if (rbID > 0) {
					RadioButton rb = (RadioButton) rgColors.findViewById(rbID);
					myColor = rb.getText().toString();
					
		        	mBoundService.sendMessage(KEY_CHANGEMYCOLOR + "-" + myColor);
		        	Toast.makeText(CONTEXT, myColor + " is now your color.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					
				} else {
					Toast.makeText(CONTEXT, "Please select a color.", Toast.LENGTH_SHORT).show();
				}
	    	}
	    });
	    
	    dialog.show();
	}
	
	private void updateUI(Message msg) {
	    String str = msg.obj.toString();
	    	
    	if (str.equalsIgnoreCase(KEY_START_GAME)) {
    		Intent newIntent = new Intent(CONTEXT, CameraActivity.class);
        	Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			newIntent.putExtras(extras);
			startActivity(newIntent);
    	}
    	// LOBBY-[]-[]
    	if (!str.equalsIgnoreCase(KEY_EMPTY_MATCH)) {
		    /*
		    if (list[0].equalsIgnoreCase(KEY_LOBBY)) {
		    	String[] listA = list[1].split("[,\\s]+");
			    createRadioButtons(teamARg, listA);
		 
		    	String[] listB = list[2].split("[,\\s]+");
			    createRadioButtons(teamBRg, listB);
		    }
		    */
    	} else {
    		Toast.makeText(CONTEXT, "No other players detected.", Toast.LENGTH_SHORT).show();
    	}
    }
	    
    private void createRadioButtons(RadioGroup r, String[] l) {
    	int number = l.length;
    	final RadioButton[] rb = new RadioButton[number];
        
    	r.removeAllViews();
    	
        for(int i=1; i<number; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(l[i]);
            rb[i].setClickable(false);
            rb[i].setTextSize(15.0f);
            LinearLayout.LayoutParams params = new LinearLayout
            		.LayoutParams(LayoutParams.MATCH_PARENT
            		, LayoutParams.MATCH_PARENT, Gravity.TOP);
            params.setMargins(80,5,5,5);
            rb[i].setLayoutParams(params);
            r.addView(rb[i]);
            
        	if (l[i].equalsIgnoreCase(username)) {
        		myRb = rb[i];
        	}
        	
        }
    }
    
	private void doBindService() {
	   bindService(new Intent(CONTEXT, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
	   mIsBound = true;
	   if(mBoundService != null){
		   mBoundService.IsBoundable();
	   } else {
		   System.out.println("NOT BOUNDABLE");
	   }
	}

	private void doUnbindService() {
	   if (mIsBound) {
	       unbindService(mConnection);
	       mIsBound = false;
	   }
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
} // end of class
