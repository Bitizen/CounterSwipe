package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.bitizen.R;

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
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HostLobbyActivity extends Activity {

	private RadioButton myRb;
	private RadioButton rbM1, rbM2, rbM3, rbM4, rbM5, rbM6;
	private RadioGroup teamARg, teamBRg, rgMarkers;
	private Button setMarkerB;
	private Spinner sTeamA, sTeamB;
	private RadioButton rbRed, rbBlue, rbGreen, rbYellow, rbOrange;
	private Button setColorB, setTeamColorB;

	private Boolean isReady = false;
	private String result;
	private String message;
	private String username, match, team;
	private String myMarker;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	private final String KEY_LOBBY = "LOBBY";
	private final String KEY_HOST_LOBBY = "HOSTLOBBY";
	private final String KEY_IAMIDLE = "IAMIDLE";
	private final String KEY_IAMREADY = "IAMREADY";
	private final String KEY_EMPTY_MATCH = "LOBBY-[]-[]";
	private static final String KEY_CHANGEMYMARKER 	= "CHANGEMYMARKER";
	
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
	
    private static final int REQ_CAMERA_IMAGE = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hostlobby);
		initializeElements();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);
			team = extras.getString(KEY_TEAM);
		}
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
        

	}

	private void initializeElements() {
		result = new String();
		message = new String();
		username = new String();
		match = new String();
		
		teamARg = (RadioGroup) findViewById(R.id.rgHTeamA);
		teamBRg = (RadioGroup) findViewById(R.id.rgHTeamB);
		
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
	}

	private void toggleReady(RadioButton rb) {
		try {
			if (isReady) {
				if (rb != null ) rb.setChecked(false);	
	        	mBoundService.sendMessage(KEY_IAMIDLE);
	        	isReady = false;
			} else if (!isReady){
				if (rb != null ) rb.setChecked(true);
	        	mBoundService.sendMessage(KEY_IAMREADY);
	        	isReady = true;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_host, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.mi_begin:
        	Toast.makeText(HostLobbyActivity.this, "Begin is Selected", Toast.LENGTH_SHORT).show();
    		toggleReady(myRb);
        	return true;

        case R.id.mi_setHSelfMarker:
        	popupMarkerDialog();
        	return true;

        case R.id.mi_quit:
        	Toast.makeText(HostLobbyActivity.this, "Quit is Selected", Toast.LENGTH_SHORT).show();
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
					myMarker = rb.getTag().toString();
					
		        	mBoundService.sendMessage(KEY_CHANGEMYMARKER + "-" + myMarker);
		        	Toast.makeText(CONTEXT, "Marker " + myMarker + " is now your marker.", Toast.LENGTH_SHORT).show();
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
    		Intent newIntent = new Intent(CONTEXT, CustomActivity.class);
        	Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			newIntent.putExtras(extras);
			startActivity(newIntent);
    	}
    	// LOBBY-[]-[]
    	if (!str.equalsIgnoreCase(KEY_EMPTY_MATCH)) {
    		String[] list = str.replaceAll("[\\:\\[\\]]+", "").split("[\\-]+");
		    
		    if (list[0].equalsIgnoreCase(KEY_HOST_LOBBY)) {
			    String[] listA = list[1].split("[,\\s]+");
			    String[] listB = list[2].split("[,\\s]+");
			
			    createRadioButtons(teamARg, listA);
			    createRadioButtons(teamBRg, listB);
		    }
    		/*
    		String[] list = str.replaceAll("[\\:\\[\\]]+", "").split("[\\-]+");
		    System.out.println("0: " + list[0]);
		    System.out.println("1: " + list[1]);
		    System.out.println("2: " + list[2]);
		    if (list[1] != null && list[0].equalsIgnoreCase(KEY_LOBBY)) {
		    	String[] listA = list[1].split("[,\\s]+");
			    createRadioButtons(teamARg, listA);
		    }
		    
		    if (list[2] != null && list[0].equalsIgnoreCase(KEY_LOBBY)) {
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
