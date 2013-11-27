package com.bitizen.counterswipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class HostLobbyActivity extends Activity implements View.OnClickListener {

	private TextView usernameTv;
	private RadioButton teammate1;
	private Button beginBtn;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hostlobby);
		initializeElements();
		
		beginBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		usernameTv = (TextView) findViewById(R.id.tvUsernameInLobby);
		teammate1 = (RadioButton) findViewById(R.id.rbHTeammate1);
		beginBtn = (Button) findViewById(R.id.btnBegin);
	
		teammate1.setClickable(false);
	}
	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnBegin:
				toggleReady();
				break;
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
