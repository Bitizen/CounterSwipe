package com.bitizen.counterswipe;

import com.bitizen.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{

	private EditText usernameEt;
	private Button joinBtn;
	private Button hostBtn;
	
	private String result;
	private String message;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;

	private final Context CONTEXT = this; 
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	private final String KEY_IAMHOST = "HOST-";
	private final String KEY_IAMREG = "REG-";
	
	private static final String KEY_ERR_CONNECT		= "server conn error";
	private static final String KEY_GET_USERNAME	= "username: ";
	private static final String KEY_USERNAME_AVAIL 	= "uname available!";
	private static final String KEY_USERNAME_TAKEN 	= "uname taken";
	private static final String KEY_HOST_AVAIL 		= "host ok";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeElements();
        
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
	}

	private void initializeElements() {
		result = new String();
		message = new String();
		
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);

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

	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnJoin:
				message = usernameEt.getText().toString(); 
				
				if (mBoundService != null) {
				    mBoundService.sendMessage(KEY_IAMREG + message);
				}
				break;
				
			case R.id.btnHost:
				message = usernameEt.getText().toString(); 
				
				if (mBoundService != null) {
				    mBoundService.sendMessage(KEY_IAMHOST + message);
				}
				break;
		}
	}

    private void updateUI(Message msg) {
    	this.result = msg.obj.toString();
    	System.out.println("R: " + result);
    	
    	if (result.equalsIgnoreCase(KEY_HOST_AVAIL)) {
        	Intent newIntent = new Intent(CONTEXT, HostLobbyActivity.class);
			Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, message);
			extras.putString(KEY_MATCH, message);
			extras.putString(KEY_TEAM, "A");
			newIntent.putExtras(extras);
			startActivity(newIntent);
    	} else if (result.equalsIgnoreCase(KEY_USERNAME_AVAIL)) {
        	Intent newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
			newIntent.putExtra(KEY_USERNAME, message);
			startActivity(newIntent);
        } else if (result.equalsIgnoreCase(KEY_USERNAME_TAKEN)) {
        	Toast.makeText(CONTEXT, "Username already exists.", Toast.LENGTH_SHORT).show();
        } else if (result.equalsIgnoreCase(KEY_GET_USERNAME)) {
        	Toast.makeText(CONTEXT, "Please try again.", Toast.LENGTH_SHORT).show();
        } else if (result.equalsIgnoreCase(KEY_ERR_CONNECT)) {
        	Toast.makeText(CONTEXT, "Problem with server connection.", Toast.LENGTH_SHORT).show();
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
