package com.brainSocket.socialrosary.data;

import java.util.Calendar;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.brainSocket.socialrosary.ConversationActivity;
import com.brainSocket.socialrosary.MainActivity;
import com.brainSocket.socialrosary.R;
import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.SplashScreen;
import com.brainSocket.socialrosary.model.AppEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMHandler 
{
	private static final String GCM_SENDER_ID = "608654228486";
	private static final String GCM_REGISTRATION_ID = "registration_id" ;
	public static final String PREF_GCM = "gcm";
//	public static final String EXTRA_MESSAGE = "message";
    /*private static final String PROPERTY_APP_VERSION = "appVersion";*/
    /*private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;*/
    private static GoogleCloudMessaging gcm;
//    AtomicInteger msgId = new AtomicInteger();

    public static final int NOTIFICATION_ID = 1213124287;
    private static NotificationManager mNotificationManager;
//    Notification.Builder builder;
    public static enum APP_STATE {MAIN, MAIN_BG, CONVERSATION_DETAILS_BG, NOT_LOGGED_IN, CLOSED};
    public static APP_STATE appState = APP_STATE.CLOSED;
    public static OnPushNotificationListener notificationListener;
    
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServices(Context context) 
	{
	    try {
	    	/*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
			Configuration.displayToast("Result Code = " + resultCode, Toast.LENGTH_SHORT);*/
			/*if (resultCode != ConnectionResult.SUCCESS) {
			 * 
			    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//			        GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			    }
			    else {
			       
			    }
			    return false;
			}*/
		} 
	    catch (Exception e) {}
	    return true;
	}
	
	/**
	 * Requests the Registration Id from GCM. If it has been already fetched, it will return the 
	 * stored id in Shared Preferences. If not, a task will run requesting the registration id 
	 * from GCM
	 * @param context
	 * @param listener
	 */
	public static void requestGCMRegistrationId(final Context context, AppGcmListener listener)
	{
		try {
			if(checkPlayServices(context)) {
			    gcm = GoogleCloudMessaging.getInstance(context);
			    String regId = getRegistrationId(context);
			    if(regId.isEmpty()) {
			        registerInBackground(context, listener);
			    }
			    else {
			    	if(listener != null) {
			    		listener.onRegistratinId(regId);
			    	}
			    }
			}
			else {
				if(listener != null) {
					listener.onPlayServicesError();
				}
			}
		} 
		catch (Exception e) {}
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private static String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(GCM_REGISTRATION_ID, "");
	    if (registrationId.isEmpty()) {
	        return "";
	    }
	    /*// Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        return "";
	    }*/
	    return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
	    return context.getSharedPreferences(PREF_GCM, Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 *//*
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } 
	    catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}*/
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 *
	 */
	private static void registerInBackground(final Context context, final AppGcmListener listener) {
	    new AsyncTask<Void, Void, Void>() {
	    	int attemotsMade = 0 ;
	    	int MAX_ATTEMPTS_ALLOWED = 5 ;
	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	                if(gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                String regId = gcm.register(GCM_SENDER_ID);
	                if(listener != null) {
	                	listener.onRegistratinId(regId);
	                }
	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regId);
	            } 
	            catch (Exception ex) {
	            	/*Configuration.displayToast("REGISTRATION ERROR " + ex != null? ex.toString(): "", Toast.LENGTH_SHORT);*/
	            	ex.printStackTrace();
	            	if(attemotsMade < MAX_ATTEMPTS_ALLOWED){
	            		try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	            		doInBackground(params);
	            	}
	            }
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void msg) {
	            
	        }
	    }.execute(null, null, null);
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private static void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    /*int appVersion = getAppVersion(context);*/
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(GCM_REGISTRATION_ID, regId);
	    /*editor.putInt(PROPERTY_APP_VERSION, appVersion);*/
	    editor.commit();
	}
	
	/**
	 *  Interface for gcm callbacks
	 * @author Nabil
	 *
	 */
	
	public interface AppGcmListener
	{
		void onRegistratinId(String regId);
		
		void onPlayServicesError();
	}
	
	//
	// RECEIVE
	//
	
	/**
	 * This function is called when a new push notification message
	 * is received. The message extras will be sent along from 
	 * the GCM intent service. 
	 * @param context
	 * @param extras
	 */
	public static void handleReceivedPushMessage(Context context, Bundle extras)
	{
		try {
			// setup message
			String msg = null;
			AppEvent event = null ;
			if(extras.containsKey("contentType")) {
				String contentType= extras.getString("contentType");
				if(contentType.equals("event")){
					msg = extras.getString("content");
					JSONObject jsonObj = new JSONObject(msg);
					event = new AppEvent(jsonObj);
				}
				
			}
			else {
				msg = context.getString(R.string.app_name);
			}
			
			/*// create notification object
			AppPushNotification pushNotification = new AppPushNotification(offerId, msg);*/
			
			// check if user not logged in, dismiss notification
			if(RosaryApp.getAppContext() == null) {
				//RosaryApp.setAppContext(context);
			}
			if( DataStore.getInstance().getMe() == null ) {
				appState = APP_STATE.NOT_LOGGED_IN;
				return;
			}
		
			// user is logged in	
			// create notification intent
			Intent intent = new Intent();
			// setup flag
			intent.putExtra("isPush", true);
			
			switch (appState) {
			
			case MAIN_BG:
				intent = new Intent(context, MainActivity.class);
				intent.setClass(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				displayPushNotification(context, msg, intent);
				break;
			case CONVERSATION_DETAILS_BG:
				intent = new Intent(context, ConversationActivity.class);
				intent.setClass(context, ConversationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				displayPushNotification(context, msg, intent);
				break;
			case NOT_LOGGED_IN:
				return;
			case CLOSED:
				
				
				
				intent.setClass(context, SplashScreen.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				displayPushNotification(context, msg, intent);
				break;
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Handles displaying the push notification in the notification status bar
	 * @param context
	 * @param title
	 * The push notification info received
	 */
	private static void displayPushNotification(Context context, String title, Intent intent)
	{
		try {	
			// check if notifications are enabled or disabled
			if(!DataCacheProvider.getInstance().getNotificationsSettings()) {
				return;
			}
			
			// setup notification
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// setup intent
			int uniqueId = (int) Calendar.getInstance().getTimeInMillis();
			PendingIntent contentIntent = PendingIntent.getActivity(context, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_friends)
			.setContentTitle(context.getString(R.string.app_name))
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(title))
			.setAutoCancel(true)
			.setTicker(title)
			.setDefaults(Notification.DEFAULT_ALL)
			.setContentText(title);

			
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} 
		catch (Exception e) {}
	}
	
	/**
	 * Removes the push notification from the status bar if it's shown
	 * @param context
	 */
	public static void dismissPushNotification(Context context)
	{
		try {
			if(mNotificationManager == null) {
				mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			}
			
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
		catch (Exception e) {}
	}
	
	/**
	 * Interface responsible for actions between the GCMHandler and any attached controller
	 * @author Nabil
	 *
	 */
	public interface OnPushNotificationListener
	{
//		void onPushReceived(AppPushNotification pushNotification);
	}
	
	/**
	 * Registers a new Push event listener and a new App_state. This will
	 * replace the old value or the default ones
	 * @param currentUI
	 * @param listener
	 */
	public static void registerUIForNotification(APP_STATE currentUI, OnPushNotificationListener listener)
	{
		appState = currentUI;
		notificationListener = listener;
//		.i("PUSH", "App_State: " + appState.toString());
	}
}
