package com.brainSocket.socialrosary;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class AppBaseActivity extends ActionBarActivity {

	//TODO this comment will delete later;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		RosaryApp.setCurrentAcivity(null);
	}
	@Override
	protected void onResume() {
		super.onResume();
		RosaryApp.setCurrentAcivity(this);
	}
	
}
