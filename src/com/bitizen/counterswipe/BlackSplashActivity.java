package com.bitizen.counterswipe;

import com.bitizen.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

public class BlackSplashActivity extends Activity {

	private SoundPoolPlayer sound;
	
	private final Context CONTEXT = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        sound = new SoundPoolPlayer(this);

        Thread timer = new Thread() {
			@Override
			public void run() {
				super.run();
				try{
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent ni = new Intent(CONTEXT, ResultsActivity.class);
					startActivity(ni);
				}
			}
        };
        
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

        timer.start();
        t1.start();
    }

	@Override
	protected void onPause() {
		super.onPause();
		sound.release();
		finish();
	}
    
}