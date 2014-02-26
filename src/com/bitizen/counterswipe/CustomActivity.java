package com.bitizen.counterswipe;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;
import edu.dhbw.andar.pub.CustomObject;
import edu.dhbw.andar.pub.CustomRenderer;
//import edu.dhbw.andopenglcam.R;

import com.bitizen.R;


/**
 * Example of an application that makes use of the AndAR toolkit.
 * @author Tobi
 *
 */
public class CustomActivity extends AndARActivity implements View.OnClickListener, OnGestureListener {
	
	private final int MENU_SCREENSHOT = 0;
	private ARToolkit artoolkit;
	private Context CONTEXT = this;
	private String username, match, team;
	private CustomObject marker1, marker2, marker3, marker4, marker5, marker6;
	private Button mHitBtn;
	private int ammo = 7;
	private GestureDetector detector;
	
    private Handler serviceHandler;
	private SocketService mBoundService;
	private Boolean mIsBound;
	private ServiceConnection mConnection;

	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";

	private static final String KEY_HIT				= "HIT-";
	private static final String KEY_GET_HIT			= "GETHIT";
	private static final String KEY_GAMEOVER		= "GAMEOVER";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initializeElements();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString(KEY_USERNAME);
			match = extras.getString(KEY_MATCH);
			team = extras.getString(KEY_TEAM);
		}
		
		startService(new Intent(CONTEXT, SocketService.class));
        doBindService();
		
		CustomRenderer renderer = new CustomRenderer();//optional, may be set to null
		super.setNonARRenderer(renderer);//or might be omited
		try {
			ARToolkit artoolkit = getArtoolkit();
			float[] red = new float[] {0.9f, 0.0f, 0.0f};
			float[] blue = new float[] {0.0f, 0.0f, 0.9f};
			float[] yellow = new float[] {0.9f, 0.9f, 0.0f};
			float[] green = new float[] {0.0f, 0.9f, 0.0f};		
			float[] purple = new float[] {0.9f, 0.0f, 0.9f};
			float[] orange = new float[] {0.9f, 0.4f, 0.0f};
			

			// RED
			marker1 = new CustomObject("test_marker1", "m1.pat"
					, 80.0
					, new double[] {0, 0}
					, red);
			artoolkit.registerARObject(marker1);

			// BLUE
			marker2 = new CustomObject("test_marker2", "m2.pat"
					, 80.0
					, new double[] {0, 0}
					, blue);
			artoolkit.registerARObject(marker2);

			// YELLOW
			marker3 = new CustomObject("test_marker3", "m3.pat"
					, 80.0
					, new double[] {0, 0}
					, yellow);
			artoolkit.registerARObject(marker3);
			
			// GREEN
			marker4 = new CustomObject("test_marker4", "m4.pat"
					, 80.0
					, new double[] {0, 0}
					, green);
			artoolkit.registerARObject(marker4);
			
			// ORANGE
			marker5 = new CustomObject("test_marker5", "m5.pat"
					, 80.0
					, new double[] {0, 0}
					, orange);
			artoolkit.registerARObject(marker5);
			
			// PURPLE
			marker6 = new CustomObject("test_marker6", "m6.pat"
					, 80.0
					, new double[] {0, 0}
					, purple);
			artoolkit.registerARObject(marker6);
			
		} catch (AndARException ex){
			//handle the exception, that means: show the user what happened
		}		
		
		mHitBtn = hitBtn;
		mHitBtn.setOnClickListener(this);
	}
	
	public void initializeElements() {

        detector = new GestureDetector(this);
		
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
	
	
	/**
	 * Inform the user about exceptions that occurred in background threads.
	 * This exception is rather severe and can not be recovered from.
	 * Inform the user and shut down the application.
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e("AndAR EXCEPTION", ex.getMessage());
		finish();
	}	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		//menu.add(0, MENU_SCREENSHOT, 0, getResources().getText(R.string.takescreenshot))
		//.setIcon(R.drawable.screenshoticon);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*if(item.getItemId()==1) {
			artoolkit.unregisterARObject(someObject);
		} else if(item.getItemId()==0) {
			try {
				someObject = new CustomObject
				("test", "patt.hiro", 80.0, new double[]{0,0});
				artoolkit.registerARObject(someObject);
			} catch (AndARException e) {
				e.printStackTrace();
			}
		}*/
		switch(item.getItemId()) {
		case MENU_SCREENSHOT:
			//new TakeAsyncScreenshot().execute();
			break;
		}
		return true;
	}
	
	class TakeAsyncScreenshot extends AsyncTask<Void, Void, Void> {
		
		private String errorMsg = null;

		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bm = takeScreenshot();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream("/sdcard/AndARScreenshot"+new Date().getTime()+".png");
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();					
			} catch (FileNotFoundException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			}	
			return null;
		}
		
		protected void onPostExecute(Void result) {
			/*if(errorMsg == null)
				Toast.makeText(CustomActivity.this, getResources().getText(R.string.screenshotsaved), Toast.LENGTH_SHORT ).show();
			else
				Toast.makeText(CustomActivity.this, getResources().getText(R.string.screenshotfailed)+errorMsg, Toast.LENGTH_SHORT ).show();*/
		};
		
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == mHitBtn.getId()
				&& ammo > 0) {
			
			MediaPlayer mp = MediaPlayer.create(CONTEXT, R.raw.hit);
			mp.start();
			--ammo;
			
			if (marker1.isVisible() && isWithinCrosshairs(marker1)) {
				Toast.makeText(this, marker1.getPoint().toString() + " - RED [M1] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-1");
			} else if (marker2.isVisible() && isWithinCrosshairs(marker2)) {
				Toast.makeText(this, marker2.getPoint().toString() + " - BLUE [M2] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-2");
			} else if (marker3.isVisible() && isWithinCrosshairs(marker3)) {
				Toast.makeText(this, marker3.getPoint().toString() + " - YELLOW [M3] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-3");
			} else if (marker4.isVisible() && isWithinCrosshairs(marker4)) {
				Toast.makeText(this, marker4.getPoint().toString() + " - GREEN [M4] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-4");
			} else if(marker5.isVisible() && isWithinCrosshairs(marker5)) {
				Toast.makeText(this, marker5.getPoint().toString() + " - ORANGE [M5] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-5");
			} else if(marker6.isVisible() && isWithinCrosshairs(marker6)) {
				Toast.makeText(this, marker6.getPoint().toString() + " - PURPLE [M6] HIT", Toast.LENGTH_SHORT).show();
				mBoundService.sendMessage(KEY_HIT + username + "-6");
			} else {
				Toast.makeText(this, "MISS", Toast.LENGTH_SHORT).show();
				//String text = Float.toString(marker1.getPoint().x);
				//String text2 = Float.toString(marker1.getPoint().y);
				//String text3 = text + " : " + text2;
				//Toast.makeText(this, text3, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "RELOAD!", Toast.LENGTH_SHORT).show();
		}
		
	}

	private static final int maxX = 1130;//445; //1130 //430
	private static final int maxY = 730;//300; //730 //280
	private static final int minX = 830;//322; //830 //361
	private static final int minY = 166;//166; //400 //207
	
	public Boolean isWithinCrosshairs(CustomObject o) {
		if (o.getPoint().x >= minX
			&& o.getPoint().x <= maxX
			&& o.getPoint().y >= minY
			&& o.getPoint().y <= maxY)
			return true;
		return false;
	}
	
	private void updateUI(Message msg) {
	    String str = msg.obj.toString();

	    if (str.equalsIgnoreCase(KEY_GET_HIT)) {
			getHit();
		} else if (str.equalsIgnoreCase(KEY_GAMEOVER)) {
    		Intent newIntent = new Intent(CONTEXT, BlackSplashActivity.class);
    		Bundle extras = new Bundle();
			extras.putString(KEY_USERNAME, username);
			extras.putString(KEY_MATCH, match);
			extras.putString(KEY_TEAM, team);
			newIntent.putExtras(extras);
			startActivity(newIntent);
		}
	}
	
	private int healthBars = 3;
	private void getHit() {
		Toast.makeText(CONTEXT, "GOT HIT", Toast.LENGTH_SHORT).show();
		if (healthBars > 0) {
			--healthBars;
		}
		
		switch(healthBars) {
			case 2:
				Toast.makeText(CONTEXT, "life-2", Toast.LENGTH_SHORT).show();
				health[2].setVisibility(View.INVISIBLE);
				break;
			case 1:
				Toast.makeText(CONTEXT, "life-1", Toast.LENGTH_SHORT).show();
				health[1].setVisibility(View.INVISIBLE);
				//health[2].setVisibility(View.INVISIBLE);
				break;
			default:
				break;
		}
		
	}

	@Override
    public boolean onDown(MotionEvent e) {
        //Toast.makeText(getApplicationContext(), "OnDown Gesture", Toast.LENGTH_SHORT).show();
        return false;
    }
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Toast.makeText(getApplicationContext(), "Fling Gesture", 100).show();
        if (ammo == 0) {
        	ammo = 7;
        	Toast.makeText(CONTEXT, "AMMO : " + ammo, Toast.LENGTH_SHORT).show();
        } else if (ammo > 0) {
        	Toast.makeText(CONTEXT, "STILL ARMED!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

	@Override
	public void onLongPress(MotionEvent e) {
		//Toast.makeText(getApplicationContext(), "Long Press Gesture", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//Toast.makeText(getApplicationContext(), "Scroll Gesture", Toast.LENGTH_SHORT).show();
        return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		 //Toast.makeText(getApplicationContext(), "Show Press gesture", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//Toast.makeText(getApplicationContext(), "Single Tap Gesture", Toast.LENGTH_SHORT).show();
        return true;
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
