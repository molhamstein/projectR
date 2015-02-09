package com.brainSocket.socialrosary.data;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;

import com.brainSocket.socialrosary.model.AppUser;
import com.brainSocket.socialrosary.model.AppUser.GENDER;

/**
 * This class will be responsible for requesting new data from the data providers
 * like  and invoking the callback when ready plus handling multithreading when required 
 * @author MolhamStein
 *
 */
public class DataStore {
	
	
	public enum GENERIC_ERROR_TYPE {NO_ERROR, UNDEFINED_ERROR, NO_CONNECTION, NOT_LOGGED_IN}
	private static DataStore instance = null ;
	
	private static ServerAccess serverHandler = null;
	private static DataCacheProvider cacheProvider = null;
	private static Handler handler = null;
	private ArrayList<DataStoreUpdatListener> updateListeners ;
	
	private static ArrayList<AppUser> arrayEnrolledFriends = null;
	private AppUser me = null;
	
	private DataStore() { 
		try {
			serverHandler = ServerAccess.getInstance();
			cacheProvider = DataCacheProvider.getInstance();
			handler = new Handler() ;
			updateListeners = new ArrayList<DataStore.DataStoreUpdatListener>() ;
			getLocalData();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataStore getInstance() {
		if(instance == null) {
			instance = new DataStore();
		}
		return instance;
	}
	
	/**
	 * user to invoke the DataRequestCallback on the main thread 
	 */	
	private void invokeCallback(final DataRequestCallback callback ,final boolean success, final HashMap<String, Object> data){
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(callback == null )
					return ;
				callback.onDataReady(data,success);
			}
		});
	}
	
	private void getLocalData(){
		me = cacheProvider.getStoredMe() ;
		arrayEnrolledFriends = cacheProvider.getStoredEnrolledFriends() ;
	}
	
	public void clearLocalData()
	{
		try {
			cacheProvider.clearCache();
		}
		catch (Exception e) {}
	}
	//--------------------
	// DataStore Update
	//-------------------------------------------
	public void triggerDataUpdate(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				//// TODO: request async update to the Server to sync all data like:  me, MyProgress , enroledFriends List .....
				// when update is done invoke broadcastDataStoreUpdate() on the main thread
			}
		}).start();
	}
	private void broadcastDataStoreUpdate(){
		for (DataStoreUpdatListener listener : updateListeners) {
			listener.onDataStoreUpdate();
		}
	}
	private void removeUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners != null && updateListeners.contains(listener))
			updateListeners.remove(listener);
	}
	private void addUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners == null)
			updateListeners = new ArrayList<DataStore.DataStoreUpdatListener>() ;
		if(!updateListeners.contains(listener))
			updateListeners.add(listener);
	}
	
	/**
	 * Async Request to the API to check if the app should force update or not according 
	 * to the current version , not implemented yet
	 * @return
	 */
	public void checkAppVersion(final DataRequestCallback callback) {
		new Thread( new Runnable() { // any request to the server should be in a separate thread
			@Override
			public void run() {
				boolean success = true ;
				HashMap<String, Object> results = new HashMap<String, Object>();//serverHandler.checkAppVersion();  // not implemented yet
				String error = (String) results.get("error") ;
				if(error != null && !error.isEmpty()){
					success = false ;
					// we can handle the error here if needed
				}else{
					/// no error, so we can extract the data that we need to update here then invoke the callback 
					//userLoggedIn = (Boolean) results.get("forceUpdate");
				}
				invokeCallback(callback, success, results); // invoking the callback
			}
		}).start();
	}
	
	//
	// USER //
	
	public boolean isUserLoggedIn() {
		return (me!=null) ? true : false;
	}
	
	public AppUser getMe() {
		if(me == null) {
			me = cacheProvider.getStoredMe() ;
		}
		return me;
	}
	public void setMe(AppUser me){
		cacheProvider.storeMe(me);
		this.me = me ;
	}
	
	/**
	 * attempting login using phone number
	 * @param phoneNumfinal
	 * @param callback
	 */
	public void attemptLogin(final String phoneNumfinal, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				HashMap<String, Object> results = new HashMap<String, Object>();//serverHandler.login();
				String error = (String) results.get("error") ;
				if(error != null && !error.isEmpty()){
					success = false ;
					// we can handle the error here if needed
				}else{
					/// no error, so we can extract the data that we need to update here then invoke the callback 
					//userLoggedIn = (Boolean) results.get("forceUpdate");
				}
				invokeCallback(callback, success, results); // invoking the callback
			}
		}).start();	
	}
	
	/** 
	 * @param phoneNumfinal
	 * @param username
	 * @param gender
	 * @param FbID : pass null if signing-up without facebook
	 * @param FbAccessToken : pass null if signing-up without facebook
	 * @param callback
	 */
	public void attemptSingnUp(final String phoneNumfinal,String username, GENDER gender,String FbID, String FbAccessToken, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				HashMap<String, Object> results = new HashMap<String, Object>();//serverHandler.login();
				String error = (String) results.get("error") ;
				if(error != null && !error.isEmpty()){
					success = false ;
					// we can handle the error here if needed
				}else{
					/// no error, so we can extract the data that we need to update here then invoke the callback 
					//userLoggedIn = (Boolean) results.get("forceUpdate");
				}
				invokeCallback(callback, success, results); // invoking the callback
			}
		}).start();	
	}
	
	
	public interface DataRequestCallback {
		public void onDataReady(HashMap<String, Object> data, boolean success);
	}
	
	public interface DataStoreUpdatListener{
		public void onDataStoreUpdate();
	}
}
