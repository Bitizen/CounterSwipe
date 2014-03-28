package com.bitizen.counterswipe;

import com.bitizen.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.GestureDetector;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BlackSplashActivity extends Activity {

	private SoundPoolPlayer sound;
	private String username, match, team;
	private Boolean interrupted 					 = false;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;
	private Thread buffer;
	
	private final Context CONTEXT 					 = this;
	private final String KEY_USERNAME				 = "username";
	private final String KEY_MATCH 					 = "match";
	private final String KEY_TEAM					 = "team";
	private final String KEY_WIN_TEAM				 = "winteam";
	
	private static final String KEY_MATCHONGOING	 = "MATCHNOTOVER";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacksplash);
        initializeElements();

        sound = new SoundPoolPlayer(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);
			team = extras.getString(KEY_TEAM);
		}
        
        startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
        
        final Thread t3 = new Thread() {
			@Override
			public void run() {
				super.run();
				try{
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
			        sound.playShortResource(R.raw.gameover);
				}
			}
        };
        
        final Thread t2 = new Thread() {
			@Override
			public void run() {
				super.run();
				try{
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
			        sound.playShortResource(R.raw.gameover);
			        t3.start();
				}
			}
        };
        
        Thread t1 = new Thread() {
			@Override
			public void run() {
				super.run();
				try{
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
			        sound.playShortResource(R.raw.gameover);
			        t2.start();
				}
			}
        };

        t1.start();
    }
    
    private void initializeElements() {
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
		    	updateUI(msg);
		    }
		};
		
		buffer = new Thread() {
			@Override
			public void run() {
				while(!interrupted) {
					try {
						sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
			    	    interrupted = true;
					} finally {
						interrupted = true;
						mBoundService.sendMessage("");
					}
				}
			}
		};
		buffer.start();
	}

	private void updateUI(Message msg) {
	    String str = msg.obj.toString();
	    	
    	if (!(str == null) && !str.equalsIgnoreCase(KEY_MATCHONGOING)) {
    		buffer.interrupt();
		    
    		Intent newIntent = new Intent(CONTEXT, ResultsActivity.class);
    		Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			extras.putString(KEY_WIN_TEAM, str);
			newIntent.putExtras(extras);
			startActivity(newIntent);
    	} 
    }

	@Override
	protected void onPause() {
		super.onPause();
		sound.release();
		finish();
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
	
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    doUnbindService();
	    interrupted = true;
	}
    
}