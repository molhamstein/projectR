package com.brainSocket.socialrosary.model;

import org.json.JSONObject;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.data.DataStore;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class AppEvent {		
	
	public enum EVENT_TYPE {ZEKER_REQ, ZEKER_RESP } ;
	public enum CONTENT_TYPE {TEXT, UNKNOWN} ;
     
	
	String idGlobal = null;
	String sessionId = null ;
	String taskId = null ;
	boolean hasAgree ;
	long date ;
	String fromUserId ;
	String toUserId ;
	
	// content
	CONTENT_TYPE contentType;
	String contentValue ;
	long contentDate ;
	
	AppContact peer ;
	boolean isEventGeneratedByMe ;


	public AppEvent(JSONObject json)
	{
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("generalEventId")) 
				idGlobal = json.getString("generalEventId");
		}catch (Exception e){} 
		try {
			if(json.has("sessionId")) 
				sessionId = json.getString("sessionId");
		}catch (Exception e){}
		try {
			if(json.has("taskId")) 
				taskId = json.getString("taskId");
		}catch (Exception e){}
		try {
			if(json.has("date")) 
				date = json.getLong("date");
		}catch (Exception e){}
		try {
			if(json.has("hasAgree")) 
				hasAgree = json.getInt("hasAgree")==1?true:false;
		}catch (Exception e){}
		
		JSONObject jsonContent = null ;
		try {
			if(json.has("content")) 
				jsonContent = json.getJSONObject("content");
		} catch (Exception e) {}
		try {
			if(jsonContent != null  && jsonContent.has("value")) 
				contentValue = jsonContent.getString("value");
		} catch (Exception e) {}
		try {
			if(jsonContent != null && jsonContent.has("contentTypeId")) 
				contentType = CONTENT_TYPE.values()[jsonContent.getInt("contentTypeId")];
		} catch (Exception e) {}
		
		/// Peer
		try {
			toUserId = json.getString("toUserId") ;
			fromUserId = json.getString("fromUserId") ;
			if(toUserId.equals(DataStore.meId)){
				isEventGeneratedByMe = false ;
				peer = new AppContact(json.getJSONObject("fromUser")) ;
			}else{
				isEventGeneratedByMe = true ;
				peer = new AppContact(json.getJSONObject("toUser")) ;
			}
		}catch (Exception e){}
	}
	
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("generalEventId", idGlobal);} catch (Exception e) {}
		try {json.put("sessionId", sessionId);} catch (Exception e) {}
		try {json.put("taskId", taskId);} catch (Exception e) {}
		try {json.put("date", date);} catch (Exception e) {}
		try {json.put("toUserId", toUserId);} catch (Exception e) {}
		try {json.put("fromUserId", fromUserId);} catch (Exception e) {}
		 
		JSONObject jsonContent = new JSONObject() ;
		try {jsonContent.put("value", contentValue);} catch (Exception e) {}
		try{jsonContent.put("contentTypeId", contentType.ordinal());} catch (Exception e) {}
		
		try {json.put("content", jsonContent);} catch (Exception e) {}

		try {
			if (isEventGeneratedByMe){
				json.put("toUser", peer.getJsonObject());
			}else{
				json.put("fromUser", peer.getJsonObject());
			}
		} catch (Exception e) {
			// TODO: handle exception
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isHasAgree() {
		return hasAgree;
	}

	public void setHasAgree(boolean hasAgree) {
		this.hasAgree = hasAgree;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public CONTENT_TYPE getContentType() {
		return contentType;
	}

	public void setContentType(CONTENT_TYPE contentType) {
		this.contentType = contentType;
	}

	public String getContentValue() {
		return contentValue;
	}

	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;
	}

	public long getContentDate() {
		return contentDate;
	}

	public void setContentDate(long contentDate) {
		this.contentDate = contentDate;
	}

	public AppContact getPeer() {
		return peer;
	}

	public void setPeer(AppContact peer) {
		this.peer = peer;
	}

	public boolean isEventGeneratedByMe() {
		return isEventGeneratedByMe;
	}

	public void setEventGeneratedByMe(boolean isEventGeneratedByMe) {
		this.isEventGeneratedByMe = isEventGeneratedByMe;
	}
	

        
}
