package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.app.Service;
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
	
	protected String result;
	protected String message;
    protected Socket socket;
    protected InputStreamReader isr;
    protected BufferedReader reader;
    protected PrintWriter writer;
    protected ExecutorService es;
    protected Runnable updateRunnable;
	
	private final Handler UIHandler = new Handler();
	protected final Context CONTEXT = this;
	protected static final int SERVERPORT = 5559;
	protected static final String SERVERHOST = "192.168.0.16";   
	private final String KEY_USERNAME = "username";
	private final String KEY_LOGIN_GOOD = "good";
	private final String KEY_LOGIN_BAD = "bad"; 

	private static final String KEY_GET_USERNAME	= "username";
	private static final String KEY_USERNAME_AVAIL 	= "unameavailable";
	private static final String KEY_USERNAME_TAKEN 	= "unametaken";
	private static final String KEY_MATCH_AVAIL 	= "matchavailable";
	private static final String KEY_MATCH_FULL 		= "matchfull";
	private static final String KEY_TEAM_AVAIL 		= "teamavailable";
	private static final String KEY_TEAM_FULL 		= "teamfull";
	private static final String KEY_INVALID			= "invalid";
	private static final String KEY_READY_USER 		= "waitingforuserready";
	private static final String KEY_READY_MATCH 	= "waitingformatchready";
	private static final String KEY_START_GAME 		= "startgame";

	/// SERVICE TRIAL
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeElements();
        
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);
		
		result = new String();
		message = new String();
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
        
		//es = Executors.newFixedThreadPool(10);
		//es.execute(new ClientReaderThread());
	}



	private void doBindService() {
	   bindService(new Intent(CONTEXT, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
	   mIsBound = true;
	   if(mBoundService != null){
		   mBoundService.IsBoundable();
	   } else {
		   System.out.println("NOTBOUNDABLE");
	   }
	}


	private void doUnbindService() {
	   if (mIsBound) {
	       // Detach our existing connection.
	       unbindService(mConnection);
	       mIsBound = false;
	   }
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	}
	////////////////////
	
	private void initializeElements() {
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);
		
		mBoundService = new SocketService();
		mConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBoundService = ((SocketService.LocalBinder)service).getService();
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			     mBoundService = null;
			}
		};
		
	    updateRunnable = new Runnable() {
	        public void run() {
	            updateUI();
	        }
	    };
	    
	}

	private void setupNetworking() {
		try {
			socket = new Socket(SERVERHOST, SERVERPORT);
			isr = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(isr);
			writer = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
     
    private void updateUI() {
    	if (result.equalsIgnoreCase(KEY_USERNAME_AVAIL)) {
        	Intent newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
			newIntent.putExtra(KEY_USERNAME, message);
			startActivity(newIntent);
        } else if (result.equalsIgnoreCase(KEY_USERNAME_TAKEN)) {
        	Toast.makeText(CONTEXT, "Username already exists.", Toast.LENGTH_SHORT).show();
        }
    }
        
	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnJoin:
				message = usernameEt.getText().toString(); 
				
				if (mBoundService != null) {
				    mBoundService.sendMessage(message);
					result = mBoundService.receiveMessage();
				}
				//es.execute(new ClientWriterThread());
				break;
				
			case R.id.btnHost:
				newIntent = new Intent(CONTEXT, HostLobbyActivity.class);
				newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//es.shutdown();
	}
	
	protected class ClientReaderThread implements Runnable {
		@Override
		public void run() {
			//setupNetworking();
			
			try {
				while((result=reader.readLine())!=null) {
					System.out.println("server: " + result);
					UIHandler.post(updateRunnable);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}		

	protected class ClientWriterThread implements Runnable {
		@Override
		public void run() {
			try{
				writer.println(message);
				writer.flush();
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}



