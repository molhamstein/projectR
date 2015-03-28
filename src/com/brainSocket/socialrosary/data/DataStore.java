package com.brainSocket.socialrosary.data;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Configuration;
import android.os.Handler;
import android.widget.Toast;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.data.GCMHandler.AppGcmListener;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppConversation;
import com.brainSocket.socialrosary.model.AppConversation.CONVERSATION_TYPE;
import com.brainSocket.socialrosary.model.AppEvent;
import com.brainSocket.socialrosary.model.AppSession;
import com.brainSocket.socialrosary.model.AppUser;
import com.brainSocket.socialrosary.model.AppUser.GENDER;
import com.google.android.gms.internal.nu;

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
	
	private String apiAccessToken = null;
	//private ArrayList<AppContact> arrayEnrolledFriends = null;
	private ArrayList<AppContact> contacts ;
	private ArrayList<AppConversation> converastions ;
	
	private HashMap<String, ArrayList<AppEvent>> mapSessionEvents ;
/*	private ArrayList<String> whatsAppFriendsLocalIds = null ;
	private ArrayList<String> viberFriendsLocalIds = null ;
*/	private AppUser me = null;
	public static String meId = "";
	
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
	private void invokeCallback(final DataRequestCallback callback ,final boolean success, final ServerResult data){
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
		apiAccessToken = cacheProvider.getStoredAccessToken();
		meId = me.getId() ;
		mapSessionEvents = cacheProvider.getStoredSessionsEvents() ;
		//arrayEnrolledFriends = cacheProvider.getStoredEnrolledFriends() ;
	}
	
	public void clearLocalData()
	{
		try {
			cacheProvider.clearCache();
			apiAccessToken = null;
		}
		catch (Exception e) {}
	}
	
	public String getApiAccessToken() {
		return apiAccessToken;
	}
	public void setApiAccessToken(String apiAccessToken) {
		this.apiAccessToken = apiAccessToken;
		cacheProvider.storeAccessToken(apiAccessToken);
	}
	//--------------------
	// DataStore Update
	//-------------------------------------------
	public void triggerDataUpdate(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				converastions = updateConversations() ;
				
				broadcastDataStoreUpdate();
			}
		}).start();
	}
	private void broadcastDataStoreUpdate(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (DataStoreUpdatListener listener : updateListeners) {
					listener.onDataStoreUpdate();
				}
			}
		});

	}
	public void removeUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners != null && updateListeners.contains(listener))
			updateListeners.remove(listener);
	}
	public void addUpdateBroadcastListener(DataStoreUpdatListener listener){
		if(updateListeners == null)
			updateListeners = new ArrayList<DataStore.DataStoreUpdatListener>() ;
		if(!updateListeners.contains(listener))
			updateListeners.add(listener);
	}
	
	public ArrayList<AppContact> getContacts() {
		return contacts;
	}
	public void setContacts(ArrayList<AppContact> contacts) {
		this.contacts = contacts;
	}
	
	public void triggerContactsUpdate () {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				contacts = ContactsMgr.getInstance().getLocalContacts(RosaryApp.getAppContext());
				
				broadcastDataStoreUpdate();
			}
		}).start(); 

	}
	
	
	/**
	 * Async Request to the API to check if the app should force update or not according 
	 * to the current version , not implemented yet
	 * @return
	 */
	public void checkAppVersion(final DataRequestCallback callback) {
/*		new Thread( new Runnable() { // any request to the server should be in a separate thread
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
		}).start();*/
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
				ServerResult result = serverHandler.login(phoneNumfinal);
				if(result.connectionFailed()){
					success = false ;
				}else{
					if(result.isValid()){
						AppUser me= (AppUser) result.getPairs().get("appUser") ;
						apiAccessToken = me.getAccessToken();
						meId = me.getId();
						setApiAccessToken(apiAccessToken);
						setMe(me);
					}
				}
				invokeCallback(callback, success, result); // invoking the callback
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
	public void attemptSignUp(final String phoneNum, final String username,final GENDER gender,final  String osVersion,final String versionId, final DataRequestCallback callback) {
		
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.registerUser(username, phoneNum, gender==GENDER.MALE, osVersion, versionId);
				HashMap<String, Object> pairs = result.getPairs() ;
				if(result.connectionFailed()){
					success = false ;
				}else{
					AppUser me= (AppUser) result.getPairs().get("appUser") ;
					apiAccessToken = me.getAccessToken();
					setApiAccessToken(apiAccessToken);
				}
				invokeCallback(callback, success, result); // invoking the callback
			}
		}).start();	
	}
	
	public void connectWithFB(final String fbAccessToken,  final DataRequestCallback callback) {
		
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.importFacebookFriends(apiAccessToken, fbAccessToken);
				HashMap<String, Object> pairs = result.getPairs() ;
				if(result.connectionFailed()){
					success = false ;
				}else{
					//TODO : add contacts if not already added
				}
				if(callback != null)
					invokeCallback(callback, success, result); 
			}
		}).start();	
	}
	
	public void getTodayHadeeth(final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				boolean success = true ;
				ServerResult result = serverHandler.getTodayHadith(apiAccessToken);
				if(result.connectionFailed()){
					success = false ;
				}else{
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public void requestSessionEvents(final String SessionId, final DataRequestCallback callback) {
		new Thread( new Runnable() { 
			@Override
			public void run() {
				
				boolean success = true ;
				ServerResult result = serverHandler.getSessionEvents(apiAccessToken, SessionId);
				if(result.connectionFailed()){
					success = false ;
				}else{
					if(mapSessionEvents == null)
						mapSessionEvents = new HashMap<String, ArrayList<AppEvent>>() ;
					ArrayList<AppEvent> events = (ArrayList<AppEvent>) result.getValue("events") ;
					mapSessionEvents.put(SessionId, events) ;
					cacheProvider.storeSessionsEvents(mapSessionEvents);
				}
				invokeCallback(callback, success, result);
			}
		}).start();	
	}
	
	public ArrayList<AppEvent> getSessionEvents(String sessionId) {
		if(mapSessionEvents == null)
			mapSessionEvents = new HashMap<String, ArrayList<AppEvent>>() ;
		if(sessionId!= null && mapSessionEvents.containsKey(sessionId))
			return mapSessionEvents.get(sessionId);
		return null;
	}
	
	// GCM
	
	public void requestGCMRegsitrationId() {
		
		GCMHandler.requestGCMRegistrationId(RosaryApp.getAppContext(), new AppGcmListener() {			
			@Override
			public void onRegistratinId(final String regId) {
				new Thread(new Runnable() {			
					@Override
					public void run() {
						/*Configuration.displayToast("REGISTRATION ID = " + regId, Toast.LENGTH_SHORT);*/
						sendGCMRegistratinId(regId);
					}
				}).start();
			}
			
			@Override
			public void onPlayServicesError() {
				//Configuration.displayToast("onPlayServicesError", Toast.LENGTH_SHORT);
			}
		});
	}
	
		
	/**
	 * Register the device with the API to receive Push notifications
	 * @param regId
	 * The registration Id received from the GCM provider
	 */
	public void sendGCMRegistratinId(String regId) {
		try {
			if( regId != null && !regId.equals("")) {
				serverHandler.setGcmId(apiAccessToken, regId);//sendGCMRegistratinId(apiAccessToken, regId/*, appUser.getId()*/);
			}
		}
		catch (Exception e) {}
	}
	
	
	
	private ArrayList<AppConversation> updateConversations(){
		ArrayList<AppConversation> conversations = null ;
		ArrayList<AppSession> sessions = null;
		try{
			conversations = null ;
			sessions = null; 
			ArrayList<AppContact> contacts = ContactsMgr.getInstance().getLocalContacts(RosaryApp.getAppContext());
			
			ServerResult result = serverHandler.getSessionsByDate(apiAccessToken, 0);
			HashMap<String, Object> pairs = result.getPairs() ;
			if(result.connectionFailed()){
				sessions = new ArrayList<AppSession>() ;
			}else{
				sessions = (ArrayList<AppSession>) result.getPairs().get("sessions") ;
			}
			
			conversations = new ArrayList<AppConversation>() ;
			ArrayList<String> usersWithSessionsId = new ArrayList<String>() ;
			for (AppSession appSession : sessions) {
				AppConversation conversation = new AppConversation(appSession) ;
				conversations.add(conversation) ;
				if(appSession.getType() == CONVERSATION_TYPE.SINGLE){
					usersWithSessionsId.add(appSession.getPeers().get(0).getPhoneNum());
				}
			}
			
			// adding contacts with no session to the Conversations  
			for (AppContact contatc : contacts) {
				if(!usersWithSessionsId.contains(contatc.getPhoneNum())){
					AppConversation con = new AppConversation(contatc) ;
					conversations.add(con);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return conversations ;
	}
	
	// getteres
	public ArrayList<AppConversation> getConverastions() {
		return converastions;
	}
	
	
	public interface DataRequestCallback {
		public void onDataReady(ServerResult data, boolean success);
	}
	
	public interface DataStoreUpdatListener{
		public void onDataStoreUpdate();
	}
}
