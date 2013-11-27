package com.bitizen.counterswipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class LobbyActivity extends Activity implements View.OnClickListener {

	private RadioButton teammate1;
	private Button readyBtn;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		initializeElements();
		
		teammate1.setOnClickListener(this);
	}

	private void initializeElements() {
		teammate1 = (RadioButton) findViewById(R.id.rbTeammate1);
		readyBtn = (Button) findViewById(R.id.btnReady);
		
		teammate1.setClickable(false);
	}
	

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnReady:
				toggleReady();
				break;
		}
		
		checkForFlag();
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
		}
	}
}
