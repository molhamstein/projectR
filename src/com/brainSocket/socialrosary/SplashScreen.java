package com.brainSocket.socialrosary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.brainSocket.socialrosary.data.DataStore;
import com.brainSocket.socialrosary.model.AppUser;


public class SplashScreen extends Activity {

	private final static int TUT_ACTIVITY_REQ_CODE = 3452 ;
	private final static int LOGIN_ACTIVITY_REQ_CODE = 3454 ;
	
	Handler handler;
	Runnable proceedRunnable = new Runnable() {

		@Override
		public void run() {
			
			AppUser currentUser = DataStore.getInstance().getMe();
			if (( currentUser == null )) {
				showTut();
			}else{
				goToMain();
				finish() ;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		handler = new Handler() ;
		handler.postDelayed(proceedRunnable, 1000);
	}

	
	private void showLogin(){
		
		Intent i = new Intent(this, LoginActivity.class);
		startActivityForResult(i, LOGIN_ACTIVITY_REQ_CODE);
	}
	private void goToMain(){
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
	
	private void showTut(){
		Intent i = new Intent(this, TutActivity.class);
		startActivityForResult(i, TUT_ACTIVITY_REQ_CODE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == TUT_ACTIVITY_REQ_CODE){
			showLogin();
		}else if(requestCode == LOGIN_ACTIVITY_REQ_CODE && resultCode == Activity.RESULT_OK){
			goToMain() ;
			finish();
		}else{
			finish() ;
		}
	}
	
	

}
