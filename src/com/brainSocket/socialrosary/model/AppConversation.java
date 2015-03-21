package com.brainSocket.socialrosary.model;

import java.sql.Date;
import java.util.ArrayList;

import org.json.JSONObject;

import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;

import android.app.backup.RestoreObserver;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class AppConversation {		
	
	public enum CONVERSATION_TYPE {SINGLE, GROUP} ;
	

	ArrayList<AppContact> contacts ;
	AppSession session ;
	boolean hasSession = false ;
	CONVERSATION_TYPE conversationType ;
	

	// non Server properties

	public AppConversation (AppSession session){
		if(session == null )
			return ;
		
		hasSession = true ;
		conversationType = session.getType() ;
		contacts = session.getPeers() ;
	}
	
	public AppConversation (AppContact contact ){
		if(contact == null )
			return ;
		
		hasSession = false ;
		conversationType = CONVERSATION_TYPE.SINGLE ;
		contacts = new ArrayList<AppContact>();
		contacts.add(contact);
	}
	
	
	public String getName (){
		if(hasSession){
			return session.getTitle() ;
		}else{
			return contacts.get(0).getName() ;
		}
	}
	
	public String getUnreadEventsTitle(){
		if(hasSession){
			return String.valueOf(session.getUnreadEvents() );
		}else{
			return "No previous conversations" ;
		}
	}
	public SOCIAL_MEDIA_ACCOUNT_TYPE getNetwork(){
		if(hasSession){
			return SOCIAL_MEDIA_ACCOUNT_TYPE.SAB3EEN ;
		}else{
			return contacts.get(0).getNetwork() ;
		}
	}
	
	
/*	public AppConversation(JSONObject json)
	{
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("id")) 
				globalId = json.getString("id");
		}catch (Exception e){} 
		try {
			if(json.has("userName")) 
				name = json.getString("userName");
		}
		catch (Exception e) {}
		try {
			if(json.has("mobileNumber")) 
				phoneNum = json.getString("mobileNumber");
		} 
		catch (Exception e) {}
		try {
			if(json.has("photo")) 
				pictureURI =  json.getString("photo");
			if(pictureURI == null ) {
				pictureURI = DEFAULT_AVATAR ;
			}
		} 
		catch (Exception e) {}

	}*/
	
/*	public AppConversation (Cursor cursor){
		if( cursor == null || cursor.isAfterLast() )
			return ;
		
		try {
			pictureURI = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
		} catch (Exception e) { }		 
		try {
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ;
		} catch (Exception e) { }
		try {
			localId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)) ;
		} catch (Exception e) { }
		try {
			phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)) ;
		} catch (Exception e) { }

	}*/
	
	

/*	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", globalId);} catch (Exception e) {}
		try {json.put("userName", name);} catch (Exception e) {}
		try {json.put("photo", pictureURI);} catch (Exception e) {}
		try {json.put("mobileNumber", phoneNum);} catch (Exception e) {}
		return json;
	}*/
	

/*	public String getJsonString()
	{
		JSONObject json = getJsonObject();
		return json.toString();
	}*/

        
}
