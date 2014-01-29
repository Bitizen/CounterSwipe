package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AvailableMatchesActivity extends Activity implements View.OnClickListener{

	private TextView usernameTv;
	private Button nextBtn;
	private RadioButton match1;

	private String result;
	private String message;
    private Socket socket;
	private InputStreamReader isr;
	private BufferedReader reader;
	private PrintWriter writer;
	private ExecutorService es;
	private Runnable updateRunnable;
	private final Handler UIHandler = new Handler();
	private static final int SERVERPORT = 5559;
    private static final String SERVERHOST = "10.0.0.204";  
	
	private final Context CONTEXT = this;
	private final String KEY_USERNAME = "username";
	private final String KEY_MATCH = "match";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_availablematches);
	    initializeElements();
	    
		Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        Intent intent = getIntent();
	        String str = intent.getStringExtra(KEY_USERNAME);
	        usernameTv.setText(str);
	    }
		
	    nextBtn.setOnClickListener(this);
	}

	private void initializeElements() {
		usernameTv = (TextView) findViewById(R.id.tvUsernameInAM);
	    nextBtn = (Button) findViewById(R.id.btnNext);
	    match1 = (RadioButton) findViewById(R.id.rbMatch1);
	    //match2 = (RadioButton) findViewById(R.id.rbMatch2);
	    //match3 = (RadioButton) findViewById(R.id.rbMatch3);

	    updateRunnable = new Runnable() {
	        public void run() {
	            updateUI();
	        }
	    };
	}
	 
    private void updateUI() {
    	if (result.equalsIgnoreCase("1")) {
        	Intent newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
			newIntent.putExtra(KEY_USERNAME, message);
			startActivity(newIntent);
        } else if (result.equalsIgnoreCase("1")) {
        	Toast.makeText(CONTEXT, "Username already exists.", Toast.LENGTH_SHORT).show();
        }
    }
    
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnNext:
				if (match1.isChecked()) {
					Intent newIntent = new Intent(CONTEXT, TeamSelectActivity.class);
					startActivity(newIntent);
				} else {
					Toast.makeText(CONTEXT, "Please select a match.", Toast.LENGTH_LONG).show();
				}
				break;
		}
		
	}
}
