package com.bitizen.counterswipe;

import com.bitizen.camera.CameraActivity;
import com.bitizen.camera.util.BitmapHelper;
import com.bitizen.camera.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

	private TextView usernameTv;
	private RadioButton teammate1;
	private Button beginBtn;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	
    private static final int REQ_CAMERA_IMAGE = 123;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hostlobby);
		initializeElements();
	}

	private void initializeElements() {
		usernameTv = (TextView) findViewById(R.id.tvUsernameInLobby);
		teammate1 = (RadioButton) findViewById(R.id.rbHTeammate1);
		teammate1.setClickable(false);
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
		Boolean allReady = teammate1.isChecked();
		
		if (allReady) {
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
		} else {
			Toast.makeText(CONTEXT, "Some players still idle.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_host, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.mi_begin:
        	// Single menu item is selected do something
        	// Ex: launching new activity/screen or show alert message
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
