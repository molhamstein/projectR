package com.brainSocket.socialrosary.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;
import com.brainSocket.socialrosary.model.AppConversation.CONVERSATION_TYPE;


/**
 * a session represent an conversation that actually exists on the server and have peers in it 
 * @author 2014
 *
 */
public class AppSession {		
	
	
	String idGlobal = null;
	String title = null ;
	long lastUpdate ;
	int unreadEvents ; 
	CONVERSATION_TYPE type ;
	ArrayList<AppContact> peers ;

	public AppSession(JSONObject json)
	{
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("id")) 
				idGlobal = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("title")) 
				title = json.getString("title");
		}
		catch (Exception e) {}
		try {
			if(json.has("sessionTypeId")) 
				type = CONVERSATION_TYPE.values()[json.getInt("sessionTypeId")];
		} 
		catch (Exception e) {}
		try {
			if(json.has("lastUpdate")) 
				lastUpdate = json.getLong("lastUpdate");
		} 
		catch (Exception e) {}
		try {
			if(json.has("notificationsNumber")) 
				unreadEvents = json.getInt("notificationsNumber");
		} 
		catch (Exception e) {}
		try {
			if(json.has("users")){
				JSONArray jsonArray = json.getJSONArray("users");
				peers = new ArrayList<AppContact>() ;
				for (int i = 0; i < jsonArray.length(); i++) {
					AppContact peer = new AppContact( jsonArray.getJSONObject(i) );
					peers.add(peer) ;
				}
			}
		} catch (Exception e) {}

	}
	
	
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", idGlobal);} catch (Exception e) {}
		try {json.put("title", title);} catch (Exception e) {}
		try {json.put("lastUpdate", lastUpdate);} catch (Exception e) {}
		try {json.put("notificationsNumber", unreadEvents);} catch (Exception e) {}
		if(peers != null){
			JSONArray jsonArray = new JSONArray() ;
			for (AppContact appContact : peers) {
				jsonArray.put(appContact.getJsonObject());
			}
			try {json.put("users", jsonArray);} catch (Exception e) {}
		}
		return json;
	}
	
	/**
	 * Returns a string formatted {@link JSONObject} of the user object
	 * @return
	 */
	public String getJsonString()
	{
		JSONObject json = getJsonObject();
		return json.toString();
	}



	public String getIdGlobal() {
		return idGlobal;
	}



	public void setIdGlobal(String idGlobal) {
		this.idGlobal = idGlobal;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public long getLastUpdate() {
		return lastUpdate;
	}



	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}



	public int getUnreadEvents() {
		return unreadEvents;
	}



	public void setUnreadEvents(int unreadEvents) {
		this.unreadEvents = unreadEvents;
	}



	public CONVERSATION_TYPE getType() {
		return type;
	}



	public void setType(CONVERSATION_TYPE type) {
		this.type = type;
	}



	public ArrayList<AppContact> getPeers() {
		return peers;
	}



	public void setPeers(ArrayList<AppContact> peers) {
		this.peers = peers;
	}
	

        
}
