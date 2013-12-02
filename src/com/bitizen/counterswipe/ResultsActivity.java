package com.bitizen.counterswipe;

import java.util.Random;

import android.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResultsActivity extends Activity implements View.OnClickListener{

	private TextView resultsTv;
	private Button replayBtn;
	private Button leaveBtn;

	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);
		initializeElements();
		
		replayBtn.setOnClickListener(this);
		leaveBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		resultsTv = (TextView) findViewById(R.id.tvResults);
		replayBtn = (Button) findViewById(R.id.btnReplay);
		leaveBtn = (Button) findViewById(R.id.btnLeave);
	}

	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnReplay:
				newIntent = new Intent(CONTEXT, LobbyActivity.class);
				//newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
				
			case R.id.btnLeave:
				newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
				//newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
		}
	}

}
