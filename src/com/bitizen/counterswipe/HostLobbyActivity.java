package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.bitizen.camera.CameraActivity;
import com.bitizen.camera.util.BitmapHelper;
import com.bitizen.camera.util.Log;
import com.bitizen.counterswipe.HostLobbyActivity.CommunicationThread;
import com.bitizen.counterswipe.HostLobbyActivity.ServerThread;
import com.bitizen.counterswipe.HostLobbyActivity.updateUIThread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class HostLobbyActivity extends Activity {

	private String username;
	private RadioButton teammate1, opponent1;
	
	private ServerSocket serverSocket;
	private Handler updateConversationHandler;
	private Thread serverThread = null;

	public static final int SERVERPORT = 6000;

	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	private final String KEY_DEFAULT_OPPONENT_USERNAME = "opponent_username";
	
    private static final int REQ_CAMERA_IMAGE = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hostlobby);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    username = extras.getString(KEY_USERNAME);
		} else {
			username = "player_username";
		}
		
		initializeElements();
		teammate1.setText(username);
		
		updateConversationHandler = new Handler();

		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();

	}

	private void initializeElements() {
		teammate1 = (RadioButton) findViewById(R.id.rbHTeammate1);
		opponent1 = (RadioButton) findViewById(R.id.rbHOpponent1);
		teammate1.setClickable(false);
		opponent1.setClickable(false);
	}

	private void toggleReady() {
		if (teammate1.isChecked()) {
			teammate1.setChecked(false);
		} else if (!teammate1.isChecked() ){
			teammate1.setChecked(true);
			checkForFlag();
		}
	}
	
	private void checkForFlag() {
		if (teammate1.isChecked()
			&& !opponent1.getText().toString().equals(KEY_DEFAULT_OPPONENT_USERNAME)
			&& opponent1.isChecked()) {
			/*
			 try {
			 
				Class requestedClass = Class.forName("com.bitizen.camera.PreCameraActivity");
				Intent newIntent = new Intent(CONTEXT, requestedClass);
				startActivity(newIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			*/
			Intent intent = new Intent(this, CameraActivity.class);
	    	startActivityForResult(intent, REQ_CAMERA_IMAGE);

			teammate1.setChecked(false);
			opponent1.setChecked(false);
		} else {
			Toast.makeText(CONTEXT, "Some players still idle.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_host, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.mi_begin:
        	Toast.makeText(HostLobbyActivity.this, "Begin is Selected", Toast.LENGTH_SHORT).show();
            toggleReady();
            return true;

        case R.id.mi_quit:
        	Toast.makeText(HostLobbyActivity.this, "Quit is Selected", Toast.LENGTH_SHORT).show();
            return true;

        case R.id.mi_kick:
        	Toast.makeText(HostLobbyActivity.this, "Kick is Selected", Toast.LENGTH_SHORT).show();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	

	@Override
	protected void onStop() {
		super.onStop();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ServerThread implements Runnable {
		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {
				try {
					socket = serverSocket.accept();

					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;
		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {
			this.clientSocket = clientSocket;

			try {
				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					String read = input.readLine();

					updateConversationHandler.post(new updateUIThread(read));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class updateUIThread implements Runnable {
		private String opponentUsername;

		public updateUIThread(String str) {
			this.opponentUsername = str;
		}

		@Override
		public void run() {
			opponent1.setText(opponentUsername);
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
