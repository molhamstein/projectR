package com.brainSocket.socialrosary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.brainSocket.socialrosary.data.PromptManager;

public class RosaryApp extends Application {

	private static Context AppContext ;
	private static Activity currentAcivity ;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		AppContext = this ;
	}
	
	public static Context getAppContext() {
		return AppContext;
	}
	public static void setCurrentAcivity(Activity currentAcivity) {
		RosaryApp.currentAcivity = currentAcivity;
		PromptManager.getInstanse().setContext(currentAcivity);
	}
	public static Activity getCurrentAcivity() {
		return currentAcivity;
	}
	
	
	//-----------------------------------------------------------------------------------------------
	// utils ..... those methode could be move to a seperate Configs Class
	//-----------------------------------------------------------------------------------------------
	public enum phoeNumCheckResult {OK, SHORT, WRONG, EMPTY};
	public static phoeNumCheckResult validatePhoneNum(String num) {
		if(num == null || "".equals(num.trim()))
			return phoeNumCheckResult.EMPTY ;
		
		phoeNumCheckResult result = phoeNumCheckResult.OK;
		if (num.length() < 10) {
			result = phoeNumCheckResult.SHORT;
		}
		return result;
	}
}
