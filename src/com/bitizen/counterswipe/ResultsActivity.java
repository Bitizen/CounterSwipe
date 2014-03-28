package com.bitizen.counterswipe;

import java.util.Random;

import com.bitizen.R;

import android.R.layout;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResultsActivity extends Activity implements View.OnClickListener{

	private Typeface typeFace;
	private TextView resultsTv;
	private Button replayBtn, leaveBtn, logoutBtn;

	private String username, match, team, winTeam;
	private Boolean interrupted 					= false;

    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	
	private final Context CONTEXT 					 = this;
	private final String KEY_USERNAME				 = "username";
	private final String KEY_MATCH 					 = "match";
	private final String KEY_TEAM					 = "team";
	private final String KEY_WIN_TEAM				 = "winteam";
	private final String KEY_LEAVEMATCH				 = "leavematch";
	private final String KEY_REPLAYMATCH			 = "replaymatch";
	private final String KEY_LOGOUT					 = "LOGOUT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		typeFace=Typeface.createFromAsset(getAssets(),"fonts/Antipasto_extrabold.otf");
		
		setContentView(R.layout.activity_results);
		initializeElements();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);
			team = extras.getString(KEY_TEAM);
			winTeam = extras.getString(KEY_WIN_TEAM);
		}
        
		if (winTeam != null && winTeam.equalsIgnoreCase(team)) {
			resultsTv.setText("you win!");
		} else if (winTeam != null && !winTeam.equalsIgnoreCase(team)) {
			resultsTv.setText("you lose.");
		} else {
			resultsTv.setText("you win!");
		}
		
        startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
        
		replayBtn.setOnClickListener(this);
		leaveBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		resultsTv = (TextView) findViewById(R.id.tvResults);
		replayBtn = (Button) findViewById(R.id.btnReplay);
		leaveBtn = (Button) findViewById(R.id.btnLeave);

		resultsTv.setTypeface(typeFace);
		replayBtn.setTypeface(typeFace);
		leaveBtn.setTypeface(typeFace);

		username = new String();
		match = new String();
		
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

		    }
		};
		
	}
	
	private void popupLogoutDialog() {
		final Dialog dialog = new Dialog(CONTEXT);
	    dialog.setContentView(R.layout.dialog_logout);
	    dialog.setTitle("Are you sure you want to logout?");
	    dialog.setCancelable(true);

	    logoutBtn = (Button) dialog.findViewById(R.id.bLogout);
	    logoutBtn.setTypeface(typeFace);

	    logoutBtn.setOnClickListener(new OnClickListener() {
	    	@Override
            public void onClick(View v) {
	    		mBoundService.sendMessage(KEY_LOGOUT);
	    		
				Intent intent = ResultsActivity.getIntent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
	    	}
	    });

	    dialog.show();
	}
		
	@Override
	public void onClick(View view) {
		
		Bundle extras = new Bundle();
		extras.putString(KEY_USERNAME, username);
		
		switch (view.getId()) {
			case R.id.btnReplay:
				mBoundService.sendMessage(KEY_REPLAYMATCH);
				
				// If Player is Host, redirect to Host Lobby
				if(match.equalsIgnoreCase(username)) {
					Intent intent = getIntent(getApplicationContext(), HostLobbyActivity.class);
					intent.putExtras(extras);
					startActivity(intent);
				} else {
					Intent intent = getIntent(getApplicationContext(), LobbyActivity.class);
					intent.putExtras(extras);
					startActivity(intent);
				}
				break;
				
			case R.id.btnLeave:
				mBoundService.sendMessage(KEY_LEAVEMATCH);
				
				// If Player is Host, quit hosting then redirect to Available Matches
				Intent intent2 = getIntent(getApplicationContext(), AvailableMatchesActivity.class);
				intent2.putExtras(extras);
				startActivity(intent2);
				break;
		}
	}

	protected static Intent getIntent(Context context, Class<?> cls) {
	    Intent intent = new Intent(context, cls);
	    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    return intent;
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
	public void onBackPressed() {
		//popupLogoutDialog();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
    
}
