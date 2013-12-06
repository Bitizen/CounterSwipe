package com.bitizen.counterswipe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.bitizen.camera.CameraActivity;
import com.bitizen.counterswipe.LoginActivity.ClientThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class LobbyActivity extends Activity {

	private String username;
	
	private RadioButton playerA1, playerB1;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";

    private static final int REQ_CAMERA_IMAGE = 123;

    private Socket socket;
    private static final int SERVERPORT = 6000;
    private static final String SERVER_IP = "10.0.2.33";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		initializeElements();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = extras.getString(KEY_USERNAME);
		} else {
			username = "player_username";
		}
		
		new Thread(new ClientThread()).start();
		new Thread(new CheckerThread()).start();
	}

	private void initializeElements() {
		playerA1 = (RadioButton) findViewById(R.id.rbPlayerA1);
		playerB1 = (RadioButton) findViewById(R.id.rbPlayerB1);
		playerA1.setClickable(false);
		playerB1.setClickable(false);
	}

	class ClientThread implements Runnable {
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class CheckerThread implements Runnable {
		 @Override
		public void run() {
			try{
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				checkForFlag();
			}
		}
	}
	
	private void toggleReady(RadioButton rb) {
		String isReady = "false";
		
		if (rb.isChecked()) {
			rb.setChecked(false);			
		} else if (!rb.isChecked() ){
			rb.setChecked(true);
			isReady = "true";
		}
		
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			out.println(isReady);
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void checkForFlag() {
		if (playerA1.isChecked()
				&& playerB1.isChecked()) {
			Intent intent = new Intent(this, CameraActivity.class);
	    	startActivityForResult(intent, REQ_CAMERA_IMAGE);

			playerA1.setChecked(false);
			playerB1.setChecked(false);
		}
	}

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_player, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.mi_ready:
        	// Single menu item is selected do something
        	// Ex: launching new activity/screen or show alert message
            Toast.makeText(LobbyActivity.this, "Ready is Selected", Toast.LENGTH_SHORT).show();
            toggleReady(playerB1);
            checkForFlag();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_OK){
			String imgPath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
			Log.i("Got image path: "+ imgPath);
			displayImage(imgPath);
		} else
		if(requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_CANCELED){
			Log.i("User didn't take an image");
		}
		*/
	}

	private void displayImage(String path) {
		//ImageView imageView = (ImageView) findViewById(R.id.image_view_captured_image);
		//imageView.setImageBitmap(BitmapHelper.decodeSampledBitmap(path, 300, 250));
	}
}
