package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.bitizen.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AvailableMatchesActivity extends Activity {

	private Typeface typeFace;
	private LinearLayout matchesLl;
	private TextView usernameTv, titleTv;
	private RadioGroup matchesRg;
	private Button logoutBtn;
	
	private String result;
	private String message;
	private String username;
	private String match;
	private Boolean interrupted = false;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	private Thread buffer;
	
	private final Context CONTEXT 					= this; 
	private final String KEY_USERNAME 				= "username";
	private final String KEY_MATCH 					= "match";
	private final String KEY_LIST_MATCHES 			= "Matches";
	private final String KEY_PICKMATCH				= "PICKMATCH-";
	private final String KEY_LOGOUT					= "LOGOUT";
	
	private static final String KEY_SHOWMATCHES		= "SHOWMATCHES";
	private static final String KEY_MATCH_AVAIL 	= "available";
	private static final String KEY_MATCH_FULL 		= "full";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		typeFace = Typeface.createFromAsset(getAssets(),"fonts/Antipasto_extrabold.otf");

		setContentView(R.layout.activity_availablematches);
	    initializeElements();
		
		Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        Intent intent = getIntent();
	        String str = intent.getStringExtra(KEY_USERNAME);
	        usernameTv.setText("USERNAME [ " + str + " ]");
	        username = str;
	    }
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
	}

	private void initializeElements() {
		result = new String();
		message = new String();
		username = new String();
		match = new String();
		
		matchesLl = (LinearLayout) findViewById(R.id.llAvailableMatches);
		usernameTv = (TextView) findViewById(R.id.tvUsernameInAM);
	    matchesRg = (RadioGroup) findViewById(R.id.rgAvailableMatches);
		titleTv = (TextView) findViewById(R.id.tvAMtitle);

		usernameTv.setTypeface(typeFace);
		titleTv.setTypeface(typeFace);

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

		buffer = new Thread() {
			@Override
			public void run() {
				while(!interrupted) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						interrupted = true;
					} finally {
						mBoundService.sendMessage(KEY_SHOWMATCHES);
					}
				}
			}
		};
		buffer.start();
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
	    		buffer.interrupt();
	    		mBoundService.sendMessage(KEY_LOGOUT);
	    		
				Intent intent = ResultsActivity.getIntent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
	    	}
	    });

	    dialog.show();
	}
	
    private void updateUI(Message msg) {
    	String str = msg.obj.toString();
	    String[] list = str.replaceAll("[\\:\\[\\]]+", "").split("[,\\s]+");
	    
	    if (list[0].equalsIgnoreCase(KEY_LIST_MATCHES)) {
		    createRadioButtons(list);
	    } else if (list[1].equalsIgnoreCase(KEY_MATCH_AVAIL)) {
		    buffer.interrupt();
		    
        	Intent newIntent = new Intent(CONTEXT, TeamSelectActivity.class);
			Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			newIntent.putExtras(extras);
			startActivity(newIntent);
        } else if (list[1].equalsIgnoreCase(KEY_MATCH_FULL)) {
        	Toast.makeText(CONTEXT, "Match is full.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void createRadioButtons(String[] l) {
    	int number = l.length;
    	final RadioButton[] rb = new RadioButton[number];
        
    	matchesRg.removeAllViews();
    	
        for(int i=1; i<number; i++){
            rb[i]  = new RadioButton(this);
            matchesRg.addView(rb[i]);
            rb[i].setText(l[i]);
            rb[i].setTypeface(typeFace);
            rb[i].setTextColor(Color.BLACK);
            final String msg = rb[i].getText().toString();
            rb[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					message = msg;
					match = message;
				    mBoundService.sendMessage(KEY_PICKMATCH + match);
				}
            });
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
	public void onBackPressed() {
		popupLogoutDialog();
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	    buffer.interrupt();
	}
	
} //end of class 
