
package com.brainSocket.socialrosary.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppEvent;
import com.brainSocket.socialrosary.model.AppUser;

public class DataCacheProvider {
	
	///KEYs
	public static final String PREF_DATA = "rosaryData";
	public static final String PREF_FIRST_TIME = "isFirstTime" ; 
	public static final String PREF_APP_USER  = "userMe" ;
	public static final String PREF_ENROLLED_FRIENDS = "enroledFriends" ;
	public static final String PREF_SETTINGS_NOTIFICATIONS = "settings_notifications" ;
	public static final String PREF_API_ACCESS_TOKEN = "accessToken" ;
	public static final String PREF_PHOTO_CACHE_CLEARED = "image_cache_clear" ;
	public static final String PREF_MAP_SESSIOINS_EVENTS = "sessionsEvents";
	
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
	
	
	/**
	 * Stores the timestamp of the last photo cache clear
	 */
	public void storePhotoClearedCacheTimestamp(long timestamp)
	{
		try {
			prefDataEditor.putLong(PREF_PHOTO_CACHE_CLEARED, timestamp);
			prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	/**
	 * Returns the stored timestamp of the last photo cache clear
	 */
	public long getStoredPhotoClearedCacheTimestamp()
	{
		long timestamp = 0;
		try {
			timestamp = prefData.getLong(PREF_PHOTO_CACHE_CLEARED, 0);
		}
		catch (Exception e) {}
		return timestamp;
	}
	
	
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
	
	
	public void storeSessionsEvents(HashMap<String, ArrayList<AppEvent>> map) {
		try {
				JSONObject obj = sessionsEventsMapToJSON(map);
				String str = obj.toString() ;
				prefDataEditor.putString(PREF_MAP_SESSIOINS_EVENTS, str);
				prefDataEditor.commit();
		}
		catch (Exception e) {}
	}
	
	public HashMap<String, ArrayList<AppEvent>> getStoredSessionsEvents() {
		HashMap<String, ArrayList<AppEvent>> map = null;
		try {
			String str = prefData.getString(PREF_MAP_SESSIOINS_EVENTS, null);
			if(str != null) {
				JSONObject json = new JSONObject(str);
				map = jsonToSessionsEventsMap(json);
			}
		}
		catch (Exception e) {}
		return map;
	}
	
	
		
	public static HashMap<String, ArrayList<AppEvent>> jsonToSessionsEventsMap(JSONObject object) {
	    HashMap<String, ArrayList<AppEvent>> map = new HashMap<String, ArrayList<AppEvent>>();
	    try {
		    Iterator<String> keysItr = object.keys();
		    while(keysItr.hasNext()) {
		        String key = keysItr.next();
		        ArrayList<AppEvent> events = new ArrayList<AppEvent>();
		        JSONArray jsonArray = object.getJSONArray(key);
		        for (int i = 0; i < jsonArray.length(); i++) {
					AppEvent event = new AppEvent(jsonArray.getJSONObject(i));
					events.add(event);
				}
		        map.put(key, events);
		    }
	    } catch (Exception e) {}
	    return map;
	}

	public static JSONObject sessionsEventsMapToJSON(HashMap<String, ArrayList<AppEvent>> map) {
	    JSONObject jsonObj = new JSONObject() ;
	    try {
		    for(Map.Entry<String, ArrayList<AppEvent>> entry : map.entrySet() ){
		        ArrayList<AppEvent> events = (ArrayList<AppEvent>) map.get(entry.getKey());
		        JSONArray jsonArray = new JSONArray() ; 
		        for (int i = 0; i < events.size(); i++) {
					AppEvent event = events.get(i);
					jsonArray.put(event.getJsonString());
				}
		        map.put(entry.getKey(), events);
		    }
	    } catch (Exception e) {}
	    return jsonObj;
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
			}else
				removeStoredMe();
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

