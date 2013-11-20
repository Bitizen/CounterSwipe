package com.bitizen.counterswipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AvailableMatchView extends Activity implements View.OnClickListener{

	TextView tvDisplayUN;
	Button nextBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_availablematch_view);
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) 
	    {
	    	tvDisplayUN = (TextView) findViewById(R.id.tvDisplayUName);
	        Intent intent = getIntent();
	        String str = intent.getStringExtra("location");
	        tvDisplayUN.setText(str);
	    }
	    nextBtn = (Button) findViewById(R.id.btnNext);
	    nextBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){
		case R.id.btnNext:
			try{
				Class ourClass;
				ourClass = Class.forName("com.bitizen.counterswipe.TeamSelectView");
				Intent ourIntent = new Intent(AvailableMatchView.this, ourClass);
				startActivity(ourIntent);
			}catch(ClassNotFoundException e){
				e.printStackTrace(); // logs within the debugger
			}finally{
				
			}
			break;
		}
	}
}