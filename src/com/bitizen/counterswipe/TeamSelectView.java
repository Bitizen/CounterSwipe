package com.bitizen.counterswipe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class TeamSelectView extends Activity {
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teamselect_view);
		initializeElements();
		teamABtn.setOnClickListener(this);
		teamBBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		// TODO Auto-generated method stub
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);
	}
	}
	
}
