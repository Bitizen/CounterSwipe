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

public class LoginActivity extends Activity implements View.OnClickListener{

	private EditText usernameEt;
	private Button joinBtn;
	private Button hostBtn;

	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeElements();
		
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);
	}

	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnJoin:
				newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
				newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
				
			case R.id.btnHost:
				newIntent = new Intent(CONTEXT, HostLobbyActivity.class);
				newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
		}
	}

}
