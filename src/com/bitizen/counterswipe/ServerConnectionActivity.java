package com.bitizen.counterswipe;

import java.net.ConnectException;

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
import android.widget.EditText;
import android.widget.Toast;

public class ServerConnectionActivity extends Activity implements View.OnClickListener{

	private EditText serverIpEt;
	private Button connectBtn;
	
	private String result;
	private String message;
	protected static String serverIp;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	
	private final Context CONTEXT 					= this;
	private final int MAXIMUM_IP_ADD_LENGTH			= 15;

	private static final String KEY_GET_USERNAME	= "username: ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecttoserver);
		initializeElements();
        
		connectBtn.setOnClickListener(this);
		
	}

	private void initializeElements() {
		result = new String();
		message = new String();
		serverIp = new String();
		
		connectBtn = (Button) findViewById(R.id.btnConnectServer);
		serverIpEt = (EditText) findViewById(R.id.etServerIp);

	}

	private void setUpService() {
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
		switch (view.getId()) {
			case R.id.btnConnectServer:
				message = serverIpEt.getText().toString().trim(); 
				
				// Checks if IP has exactly 3 dots and are all digits
				char dot = '.';
			    int iDots = 0;
			    int iDigits = 0;
				for (int i = 0; i < message.length(); i++) {
				    if (message.charAt(i) == dot) iDots++;
				    if (Character.isDigit(message.charAt(i))) iDigits++;
				}

			    int expectedNumDots = 3;
			    int expectedNumDigits = message.length()-3;
				if(message.length() <= MAXIMUM_IP_ADD_LENGTH
						&& iDots == expectedNumDots
						&& iDigits == expectedNumDigits) {
					serverIp = message;
					
					setUpService();
					startService(new Intent(CONTEXT, SocketService.class));
			        doBindService();
				} else {
					Toast.makeText(CONTEXT, "Invalid Server IP format.\n[ex. 192.168.0.20]", Toast.LENGTH_LONG).show();
				}
				break;
					
		}
	}

	protected static String getIp() {
		return serverIp;
	}
	
    private void updateUI(Message msg) {
    	result = msg.obj.toString();
    	
    	if (result.equalsIgnoreCase(KEY_GET_USERNAME)) {
        	Intent newIntent = new Intent(CONTEXT, LoginActivity.class);
			startActivity(newIntent);
    	} else {
        	Toast.makeText(CONTEXT, "Cannot connect to server.", Toast.LENGTH_SHORT).show();
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
