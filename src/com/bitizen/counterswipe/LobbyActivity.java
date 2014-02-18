package com.bitizen.counterswipe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.bitizen.R;
import com.bitizen.camera.CSCameraActivity;

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
	private RadioButton rbM1, rbM2, rbM3, rbM4, rbM5, rbM6;
	private RadioGroup teamARg, teamBRg, rgMarkers;
	private Button setMarkerB;

	private String result;
	private String message;
	private String myMarker;
	private String username, match, team;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	private final String KEY_LOBBY = "LOBBY";
	private final String KEY_IAMIDLE = "IAMIDLE";
	private final String KEY_IAMREADY = "IAMREADY";
	private final String KEY_EMPTY_MATCH = "[]";
	private final String KEY_CHANGEMYMARKER = "CHANGEMYMARKER";
	
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
        
		//new Thread(new CheckerThread()).start();
	}
	
	private void initializeElements() {
		result = new String();
		message = new String();
		username = new String();
		match = new String();
		myMarker = new String();
		
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
					this.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					//mBoundService.sendMessage("NEXT");
					checkForFlag();
				}
				super.run();
			}
		};
		buffer.start();
	}
	
	/*
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
	*/
	
	private void toggleReady(RadioButton rb) {
		try {
			if (rb.isChecked()) {
				rb.setChecked(false);	
	        	mBoundService.sendMessage(KEY_IAMIDLE);
			} else if (!rb.isChecked() ){
				rb.setChecked(true);
	        	mBoundService.sendMessage(KEY_IAMREADY);
			}
		} catch (NullPointerException e) {
			
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
	        	checkForFlag();
	            return true;
	        case R.id.mi_setSelfMarker:
	        	popupMarkerDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }    
	
	private void popupMarkerDialog() {
		final Dialog dialog = new Dialog(CONTEXT);
	    dialog.setContentView(R.layout.dialog_changemymarker);
	    dialog.setTitle("Select your marker:");
	    dialog.setCancelable(true);

	    rgMarkers = (RadioGroup) dialog.findViewById(R.id.rgMarkers);
	    rbM1 = (RadioButton) dialog.findViewById(R.id.rbM1);
	    rbM2 = (RadioButton) dialog.findViewById(R.id.rbM2);
	    rbM3 = (RadioButton) dialog.findViewById(R.id.rbM3);
	    rbM4 = (RadioButton) dialog.findViewById(R.id.rbM4);
	    rbM5 = (RadioButton) dialog.findViewById(R.id.rbM5);
	    rbM6 = (RadioButton) dialog.findViewById(R.id.rbM6);
	    setMarkerB = (Button) dialog.findViewById(R.id.bSetMyMarker);
	    
	    setMarkerB.setOnClickListener(new OnClickListener() {
	    	@Override
            public void onClick(View v) {
	    		int rbID = rgMarkers.getCheckedRadioButtonId();
	    		
				if (rbID > 0) {
					RadioButton rb = (RadioButton) rgMarkers.findViewById(rbID);
					myMarker = rb.getText().toString();
					
		        	mBoundService.sendMessage(KEY_CHANGEMYMARKER + "-" + myMarker);
		        	Toast.makeText(CONTEXT, myMarker + " is now your marker.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					
				} else {
					Toast.makeText(CONTEXT, "Please select a marker.", Toast.LENGTH_SHORT).show();
				}
	    	}
	    });
	    
	    dialog.show();
	}
	
	private void updateUI(Message msg) {
	    String str = msg.obj.toString();
	    	
    	if (str.equalsIgnoreCase(KEY_START_GAME)) {
    		Intent newIntent = new Intent(CONTEXT, CSCameraActivity.class);
    		Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			newIntent.putExtras(extras);
			startActivity(newIntent);
    	}
    	
    	// LOBBY-[]-[] >> "[]"
    	if (!str.contains(KEY_EMPTY_MATCH)) {
    		String[] list = str.replaceAll("[\\:\\[\\]]+", "").split("[\\-]+");
		    
		    if (list[0].equalsIgnoreCase(KEY_LOBBY)) {
			    String[] listA = list[1].split("[,\\s]+");
			    String[] listB = list[2].split("[,\\s]+");
			
			    createRadioButtons(teamARg, listA);
			    createRadioButtons(teamBRg, listB);
		    }
    	} else {
    		Toast.makeText(CONTEXT, "Waiting for other players.", Toast.LENGTH_SHORT).show();
    	}
    }
	    
    private void createRadioButtons(RadioGroup r, String[] l) {
    	int number = l.length;
    	final RadioButton[] rb = new RadioButton[number];
        
    	r.removeAllViews();
    	
        for(int i=0; i<number; i++){
            rb[i] = new RadioButton(this);
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
