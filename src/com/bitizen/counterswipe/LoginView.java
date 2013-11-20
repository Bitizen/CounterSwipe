package com.bitizen.counterswipe;

import java.util.Random;

import android.R.layout;
import android.os.Bundle;
import android.app.Activity;
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

public class LoginView extends Activity implements View.OnClickListener{

	EditText usernameEt;
	Button joinBtn;
	Button hostBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_view);
		initializeElements();
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		// TODO Auto-generated method stub
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_view, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.btnJoin:
			try{
				Class ourClass = Class.forName("com.bitizen.counterswipe.AvailableMatchView");
				Intent ourIntent = new Intent(LoginView.this, ourClass);
				ourIntent.putExtra("location", usernameEt.getText().toString());
				startActivity(ourIntent);
			}catch(ClassNotFoundException e){
				e.printStackTrace(); // logs within the debugger
			}finally{
				
			}
			break;
		case R.id.btnHost:
			break;
		}
	}

}
