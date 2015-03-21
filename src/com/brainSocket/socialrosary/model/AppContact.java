package com.brainSocket.socialrosary.model;

import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class AppContact implements Parcelable {		
	
	public enum GENDER {MALE, FEMALE} ;
	public enum SOCIAL_MEDIA_ACCOUNT_TYPE {PHONE_BOOK, SAB3EEN, VIBER, WHATSAP} ;
	
	public static final String DEFAULT_AVATAR=  "https://s3-us-west-2.amazonaws.com/almuajaha/profile/avatar01.png" ;
	
	
	String globalId = null;
	String localId = null ;
	String phoneNum = null;
	String name = null;
	String pictureURI = null;


	// non Server properties
	SOCIAL_MEDIA_ACCOUNT_TYPE network ;
		


	public AppContact(JSONObject json)
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

	}
	
	public AppContact (Cursor cursor){
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

	}
	
	
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject() {
		JSONObject json = new JSONObject();
		try {json.put("id", globalId);} catch (Exception e) {}
		try {json.put("userName", name);} catch (Exception e) {}
		try {json.put("photo", pictureURI);} catch (Exception e) {}
		try {json.put("mobileNumber", phoneNum);} catch (Exception e) {}
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
	


	public String getGlobalId() {
		return globalId;
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureURI() {
		return pictureURI;
	}

	public void setPictureURI(String pictureURI) {
		this.pictureURI = pictureURI;
	}

	public SOCIAL_MEDIA_ACCOUNT_TYPE getNetwork() {
		return network;
	}

	public void setNetwork(SOCIAL_MEDIA_ACCOUNT_TYPE network) {
		this.network = network;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) 	{		
		parcel.writeString(globalId);
		parcel.writeString(name);
		parcel.writeString(pictureURI);
		parcel.writeString(phoneNum);
	}
	
	public AppContact(Parcel parcel) 	{	// order does matter here
		globalId = parcel.readString();
		name = parcel.readString();
		pictureURI = parcel.readString();
		phoneNum = parcel.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	

	/*public static final Parcelable.Creator<AppUser> CREATOR = new Parcelable.Creator<AppUser>()
    {
		public AppUser createFromParcel(Parcel in)
		{
			return new AppUser(in);
		}

		public AppUser[] newArray(int size)
		{
			return new AppUser[size];
		}
    };*/
        
}
