package com.bitizen.counterswipe;

import com.bitizen.R;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class SplashActivity extends Activity {

	private MediaPlayer splashSound;
	private final Context CONTEXT = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        //splashSound = MediaPlayer.create(CONTEXT, R.raw.splashsound);
        //splashSound.start();
        
        Thread timer = new Thread() {
			@Override
			public void run() {
				super.run();
				try{
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent(CONTEXT, ServerConnectionActivity.class);
					startActivity(intent);
				}
			}
        };
        timer.start();
    }

	@Override
	protected void onPause() {
		super.onPause();
		//splashSound.release();
		finish();
	}
    
}
