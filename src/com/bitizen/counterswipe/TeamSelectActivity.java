package com.bitizen.counterswipe;

import com.bitizen.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class TeamSelectActivity extends Activity implements View.OnClickListener {
	
	private TextView usernameTv, matchTv;
	private Button teamABtn, teamBBtn;

	private String team;
	private Boolean readyToClick;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";

	private String result;
	private String message;
	private String username, match;
	
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
		setContentView(R.layout.activity_teamselect);
		initializeElements();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);

			usernameTv.setText("USERNAME: " + username);
			matchTv.setText("MATCH : " + match);
		}
		
		teamABtn.setOnClickListener(this);
		teamBBtn.setOnClickListener(this);
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
	}

	private void initializeElements() {
		result = new String();
		message = new String();
		username = new String();
		match = new String();
		readyToClick = false;
		
		usernameTv = (TextView) findViewById(R.id.tvUsernameTS);
		matchTv = (TextView) findViewById(R.id.tvMatchTS);
		teamABtn = (Button) findViewById(R.id.btnTeamA);
		teamBBtn = (Button) findViewById(R.id.btnTeamB);
		
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
					mBoundService.sendMessage("NEXT");
					readyToClick = true;
				}
				super.run();
			}
		};
		buffer.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnTeamA:
				team = "A";
				break;
				
			case R.id.btnTeamB:
				team = "B";
				break;
		}

		message = team; 
		if (readyToClick && mBoundService != null) {
		    mBoundService.sendMessage(message);
		}
	}
	
	private void updateUI(Message msg) {
    	String str = msg.obj.toString();

	    if (str.equalsIgnoreCase(KEY_TEAM_AVAIL)) {
        	Intent newIntent = new Intent(CONTEXT, LobbyActivity.class);
        	Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			newIntent.putExtras(extras);
			startActivity(newIntent);
        } else if (str.equalsIgnoreCase(KEY_TEAM_FULL)) {
        	Toast.makeText(CONTEXT, "Team is full.", Toast.LENGTH_SHORT).show();
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
