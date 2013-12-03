package com.bitizen.counterswipe;

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

public class HostLobbyActivity extends Activity {

	private TextView usernameTv;
	private RadioButton teammate1;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	
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
			try {
				Class requestedClass = Class.forName("com.bitizen.camera.PreCameraActivity");
				Intent newIntent = new Intent(CONTEXT, requestedClass);
				startActivity(newIntent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			teammate1.setChecked(false);
		} else {
			Toast.makeText(CONTEXT, "Some players still idle.", Toast.LENGTH_LONG).show();
		}
	}
}
