package com.bitizen.counterswipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.layout;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener{

	private EditText usernameEt;
	private Button joinBtn;
	private Button hostBtn;
	
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
	private final String KEY_LOGIN_GOOD = "good";
	private final String KEY_LOGIN_BAD = "bad";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeElements();
		
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);
		
		result = new String();
		message = new String();
		
		es = Executors.newFixedThreadPool(10);
		es.execute(new ClientReaderThread());
	}

	private void initializeElements() {
		joinBtn = (Button) findViewById(R.id.btnJoin);
		hostBtn = (Button) findViewById(R.id.btnHost);
		usernameEt = (EditText) findViewById(R.id.etUsername);

	    updateRunnable = new Runnable() {
	        public void run() {
	            updateUI();
	        }
	    };
	}

	private void setupNetworking() {
		try {
			socket = new Socket(SERVERHOST, SERVERPORT);
			isr = new InputStreamReader(socket.getInputStream());
			reader = new BufferedReader(isr);
			writer = new PrintWriter(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
     
    private void updateUI() {
    	if (result.equalsIgnoreCase(KEY_LOGIN_GOOD)) {
        	Intent newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
			newIntent.putExtra(KEY_USERNAME, message);
			startActivity(newIntent);
        } else if (result.equalsIgnoreCase(KEY_LOGIN_BAD)) {
        	Toast.makeText(CONTEXT, "Username already exists.", Toast.LENGTH_SHORT).show();
        }
    }
        
	@Override
	public void onClick(View view) {
		Intent newIntent;
		switch (view.getId()) {
			case R.id.btnJoin:
				message = usernameEt.getText().toString();
				es.execute(new ClientWriterThread());

				/*
				newIntent = new Intent(CONTEXT, AvailableMatchesActivity.class);
				newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				*/
				break;
				
			case R.id.btnHost:
				newIntent = new Intent(CONTEXT, HostLobbyActivity.class);
				newIntent.putExtra(KEY_USERNAME, usernameEt.getText().toString());
				startActivity(newIntent);
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		es.shutdown();
	}
	
	private class ClientReaderThread implements Runnable {
		@Override
		public void run() {
			setupNetworking();
			
			result = new String();
			try {
				while((result=reader.readLine())!=null) {
					System.out.println("server: " + result);
					UIHandler.post(updateRunnable);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}		

	private class ClientWriterThread implements Runnable {
		@Override
		public void run() {
			try{
				writer.println(message);
				writer.flush();
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
}


