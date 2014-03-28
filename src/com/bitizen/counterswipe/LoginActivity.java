package com.bitizen.counterswipe;

import com.bitizen.R;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{

	private Typeface typeFace;
	private EditText usernameEt;
	private TextView dialogTv;
	private Button joinBtn, hostBtn, quitGameBtn;
	
	private String result;
	private String message;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;

	private final Context CONTEXT 					= this; 
	private final String KEY_USERNAME 				= "username";
	private final String KEY_MATCH 					= "match";
	private final String KEY_TEAM 					= "team";
	private final String KEY_IAMHOST 				= "HOST-";
	private final String KEY_IAMREG 				= "REG-";
	private final int MINIMUM_USERNAME 				= 7;
	private final int MAXIMUM_USERNAME 				= 14;
	
	private static final String KEY_ERR_CONNECT		= "server conn error";
	private static final String KEY_GET_USERNAME	= "username: ";
	private static final String KEY_USERNAME_AVAIL 	= "uname available!";
	private static final String KEY_USERNAME_TAKEN 	= "uname taken";
	private static final String KEY_HOST_AVAIL 		= "host ok";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		typeFace = Typeface.createFromAsset(getAssets(),"fonts/Antipasto_extrabold.otf");

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
		dialogTv = (TextView) findViewById(R.id.tvDialogBird);
		
		joinBtn.setTypeface(typeFace);
		hostBtn.setTypeface(typeFace);
		usernameEt.setTypeface(typeFace);
		dialogTv.setTypeface(typeFace);
		
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
		message = usernameEt.getText().toString().trim(); 
		
		if(message.length() < MINIMUM_USERNAME) {
			Toast.makeText(CONTEXT, "Your username should have\nat least 7 characters.", Toast.LENGTH_SHORT).show();
		} else if(message.length() > MAXIMUM_USERNAME) {
			Toast.makeText(CONTEXT, "Your username shouldn't have\nmore than 14 characters.", Toast.LENGTH_SHORT).show();
		} else {
			switch (view.getId()) {
				case R.id.btnJoin:
					if (mBoundService != null)
					    mBoundService.sendMessage(KEY_IAMREG + message);
					break;
					
				case R.id.btnHost:
					if (mBoundService != null)
					    mBoundService.sendMessage(KEY_IAMHOST + message);
					break;
			}
		}
	}
	
	// TODO quit
	private void popupQuitDialog() {
		final Dialog dialog = new Dialog(CONTEXT);
	    dialog.setContentView(R.layout.dialog_quit);
	    dialog.setTitle("Are you sure you want to quit?");
	    dialog.setCancelable(true);

	    quitGameBtn = (Button) dialog.findViewById(R.id.bQuitGame);
	    
	    quitGameBtn.setOnClickListener(new OnClickListener() {
	    	@Override
            public void onClick(View v) {
	    		finish();
	    	}
	    });

	    dialog.show();
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
	
	// TODO
	@Override
	public void onBackPressed() {
		popupQuitDialog();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	
} // end of class
