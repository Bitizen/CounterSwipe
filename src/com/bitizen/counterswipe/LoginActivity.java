package com.bitizen.counterswipe;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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

    private Socket socket;
    private static final int SERVERPORT = 6000;
    //private static final String SERVER_IP = "10.0.2.15";
    private static final String SERVER_IP = "10.0.1.106"; // DICE
    //private static final String SERVER_IP = "10.0.2.2";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initializeElements();
		
		joinBtn.setOnClickListener(this);
		hostBtn.setOnClickListener(this);

		new Thread(new ClientThread()).start();
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
				try {
					String clientUsername = usernameEt.getText().toString();
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())), true);
					out.println(clientUsername);
				} catch (UnknownHostException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				
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


	class ClientThread implements Runnable {
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
