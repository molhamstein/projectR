
package com.brainSocket.socialrosary.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppUser;

public class DataCacheProvider {
	
	///KEYs
	public static final String PREF_DATA = "rosaryData";
	public static final String PREF_FIRST_TIME = "isFirstTime" ; 
	public static final String PREF_APP_USER  = "userMe" ;
	public static final String PREF_ENROLLED_FRIENDS = "enroledFriends" ;
	public static final String PREF_SETTINGS_NOTIFICATIONS = "settings_notifications" ;
	public static final String PREF_API_ACCESS_TOKEN = "accessToken" ;
	
	
	private static DataCacheProvider cacheProvider = null;
	// shared preferences
	SharedPreferences prefData;
	SharedPreferences.Editor prefDataEditor;

	
	public static DataCacheProvider getInstance()
	{
		if(cacheProvider == null) {
			cacheProvider = new DataCacheProvider();
		}
		return cacheProvider;
	}
	
	private DataCacheProvider() {
		try {
			// initialize
			prefData = RosaryApp.getAppContext().getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
			prefDataEditor = prefData.edit();
		} 
		catch (Exception e) {}
	}

	
	public void clearCache() {
		try {
			prefDataEditor.clear();
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	//
	
	public boolean isFirstTime() {
		boolean res = false;
		try {
			res = prefData.getBoolean(PREF_FIRST_TIME, true);
			if(res) {
				prefDataEditor.putBoolean(PREF_FIRST_TIME, false);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
		return res;
	}
	
	
	// USER //
	
	/**
	 * Stores the {@link AppUser} object in Shared Preferences
	 * @param appUser
	 */
	public void storeMe(AppUser me) {
		try {
			if(me != null) {
				String str = me.getJsonString();
				prefDataEditor.putString(PREF_APP_USER, str);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the {@link AppUser} object stored in Shared Preferences,
	 * null otherwise
	 */
	public AppUser getStoredMe() {
		AppUser appUser = null;
		try {
			String str = prefData.getString(PREF_APP_USER, null);
			if(str != null) {
				JSONObject json = new JSONObject(str);
				appUser = new AppUser(json);
			}
		}
		catch (Exception e) {}
		return appUser;
	}
	
	/**
	 * Removes the stored {@link AppUser} from Shared Preferences
	 */
	public void removeStoredMe()
	{
		try {
			prefDataEditor.remove(PREF_APP_USER);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	// USER //
	//

	/**
	 * Stores the Array of enrolled Friends 
	 */
	public void storeEnrolledFriends(ArrayList<AppUser> arrayFriends) {
		try {
			if(arrayFriends != null) {
				JSONArray array = new JSONArray();
				for(AppUser user: arrayFriends) {
					JSONObject json = user.getJsonObject();
					array.put(json);
				}
				String str = array.toString();
				prefDataEditor.putString(PREF_ENROLLED_FRIENDS, str);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the arrayFriends stored in Shared Preferences
	 */
	public ArrayList<AppUser> getStoredEnrolledFriends() {
		ArrayList<AppUser> arrayFriends = null;
		try {
			String str = prefData.getString(PREF_ENROLLED_FRIENDS, null);
			if(str != null) {
				JSONArray array = new JSONArray(str);
				arrayFriends = new ArrayList<AppUser>();
				for(int i = 0; i < array.length(); i++) {
					try {
						JSONObject object = array.getJSONObject(i);
						AppUser user = new AppUser(object);
						arrayFriends.add(user);
					} 
					catch (Exception e) {}
				}
			}
		}
		catch (Exception e) {}
		return arrayFriends;
	}
	

	
	/**
	 * Stores the accessToken received on login from the API 
	 * in shared preferences
	 * @param accessToken
	 */
	public void storeAccessToken(String accessToken)
	{
		try {
			if(accessToken != null) {
				prefDataEditor.putString(PREF_API_ACCESS_TOKEN, accessToken);
				prefDataEditor.commit();
			}
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the cached API accessToken from Shared Preferences,
	 *  null if it wasn't stored before
	 */
	public String getStoredAccessToken()
	{
		String apiAccessToken = null;
		try {
			apiAccessToken = prefData.getString(PREF_API_ACCESS_TOKEN, null);
		}
		catch(Exception e) {}
		return apiAccessToken;
	}
	
	/**
	 * Removes the API accessToken stored in Shared Preferences
	 */
	public void removeAccessToken()
	{
		try {
			prefDataEditor.remove(PREF_API_ACCESS_TOKEN);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}	
	
	
	
	/// settings 
	
	public boolean getNotificationsSettings() {
		boolean settings = true;
		try {
			settings = prefData.getBoolean(PREF_SETTINGS_NOTIFICATIONS, true);
		}
		catch (Exception e) {}
		return settings;
	}
	
}

