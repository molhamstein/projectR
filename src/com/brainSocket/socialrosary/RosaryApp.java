package com.brainSocket.socialrosary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.brainSocket.socialrosary.data.PromptManager;

public class RosaryApp extends Application {

	private static Context AppContext ;
	private static Activity currentAcivity ;
	public static final String VERSIOIN_ID = "0.1";
	
	
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
	
	
	/// DATE & TIME 
	
	public static String getFormatedDateForDisplay(long timestamp)
	{
		String res = null;
		try {
			Date date = new Date(timestamp);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			res = sdf.format(date);
		}
		catch (Exception e) {}
		return res;
	}
    public static long getTimestampNow()
    {
    	long res = 0;
    	try {
    		res = Calendar.getInstance().getTimeInMillis();
    	}
    	catch (Exception e) {}
    	return res;
    }
    
    
    private static final long oneDayMillies = 24 *60 * 60 * 1000 ;
    private static final long oneHourMillies = 60 * 60 * 1000 ;
    private static final long oneMinuteMillies =  60 * 1000 ;
    
    public static String getDaysLapsedString (Date date) {
    	String result  = "";
    	long now =  Calendar.getInstance().getTimeInMillis() ;
    	long timeLapsed  = now - date.getTime() ;
    	int days = (int) (timeLapsed /  oneDayMillies) ;
    	int hours = (int) (timeLapsed /  oneHourMillies) ;//(int) (timeLapsed - (long)(days * oneDayMillies))  ;
    	if(days > 0 ){
    		if(days > 1)
    			result = days + " days ago " ;
    		else 
    			result = days + " day ago " ;
    	}else if(hours > 0){
    		if(hours > 1)
    			result = hours + " hours ago " ;
    		else 
    			result = hours + " hour ago " ;
    	}else{
    		result = "few minutes ago" ;
    	}
    	
    	return result ;
    }
	
	public static boolean isPhoneNumberEqual (String num1, String num2){
		if(num1 == null || num2 == null)
			return false ;
		return num1.equals(num2);
	}
    
	
	
}
