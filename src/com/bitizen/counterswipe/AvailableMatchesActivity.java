package com.bitizen.counterswipe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AvailableMatchesActivity extends Activity implements View.OnClickListener{

	private TextView usernameTv;
	private Button nextBtn;
	private RadioButton match1;
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	private final String KEY_NULL = "0";
	private final String KEY_MATCH_ONE = "1";
	private final String KEY_MATCH_TWO = "2";
	private final String KEY_MATCH_THREE = "3";

    private Socket socket;
    private static final int SERVERPORT = 6000;
    private static final String SERVER_IP = "10.0.2.33";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_availablematches);
	    initializeElements();
	    
		/*
		Bundle extras = getIntent().getExtras();
	    if (extras != null) 
	    {
	    	usernameTv = (TextView) findViewById(R.id.tvDisplayUName);
	        Intent intent = getIntent();
	        String str = intent.getStringExtra("location");
	        usernameTv.setText(str);
	    }
	    */
		
	    nextBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		usernameTv = (TextView) findViewById(R.id.tvUsernameInAM);
	    nextBtn = (Button) findViewById(R.id.btnNext);
	    match1 = (RadioButton) findViewById(R.id.rbMatch1);
	    //match2 = (RadioButton) findViewById(R.id.rbMatch2);
	    //match3 = (RadioButton) findViewById(R.id.rbMatch3);
	}
	
	@Override
	public void onClick(View view) {
		String chosenMatch = KEY_NULL;
		
		switch (view.getId()) {
			case R.id.btnNext:
				if (match1.isChecked()) {
					chosenMatch = KEY_MATCH_ONE;
					Intent newIntent = new Intent(CONTEXT, TeamSelectActivity.class);
					startActivity(newIntent);
				} else {
					Toast.makeText(CONTEXT, "Please select a match.", Toast.LENGTH_LONG).show();
				}
				break;
		}
		
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			out.println(chosenMatch);
		} catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
}
