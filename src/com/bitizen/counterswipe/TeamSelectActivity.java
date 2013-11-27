package com.bitizen.counterswipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TeamSelectActivity extends Activity implements View.OnClickListener {
	
	private Button teamABtn, teamBBtn;

	private String team;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_TEAM = "team";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teamselect);
		initializeElements();
		
		teamABtn.setOnClickListener(this);
		teamBBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		teamABtn = (Button) findViewById(R.id.btnTeamA);
		teamBBtn = (Button) findViewById(R.id.btnTeamB);
	}

	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnTeamA:
				team = "A";
				break;
				
			case R.id.btnTeamB:
				team = "B";
				break;
		}
		
		newIntent = new Intent(CONTEXT, LobbyActivity.class);
		startActivity(newIntent);
	}
	
}
