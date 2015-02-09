package com.brainSocket.socialrosary.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class AppUser implements Parcelable {		
	
	public enum GENDER {MALE, FEMALE} ;
	
	public static final String DEFAULT_AVATAR=  "https://s3-us-west-2.amazonaws.com/almuajaha/profile/avatar01.png" ;
	
	String id = null;
	String name = null;
	String picture = null;
	String fbId = null;
	String phoneNum = null;
	int isAppUser = 0 ; 
		
	

	public AppUser(JSONObject json)
	{
		if(json == null) {
			return;
		}
		
		try {
			if(json.has("id")) 
				id = json.getString("id");
		} 
		catch (Exception e) {}
		try {
			if(json.has("display_name")) 
				name = json.getString("display_name");
		} 
		catch (Exception e) {}
		try {
			if(json.has("phone_num")) 
				phoneNum = json.getString("phone_num");
		} 
		catch (Exception e) {}
		try {
			if(json.has("is_app_user")) 
				isAppUser = json.getInt("is_app_user");
		} 
		catch (Exception e) {}
		try {
			if(json.has("facebook_id")) {
				fbId = json.getString("facebook_id");
				// check if 0
				if(fbId != null && fbId.equals("0")) {
					fbId = null;
				}
			}
		} 
		catch (Exception e) {}
		try {
			if(json.has("picture_url")) 
				picture = json.getString("picture_url");
			if(picture == null || picture.equals("")) {
				picture = DEFAULT_AVATAR;
			}
		} 
		catch (Exception e) {}

	}
	
	/**
	 * Returns the {@link JSONObject} containing the user 
	 * details, just like the structure received from the API
	 * @return
	 */
	public JSONObject getJsonObject()
	{
		JSONObject json = new JSONObject();
		try {json.put("id", id);} catch (Exception e) {}
		try {json.put("display_name", name);} catch (Exception e) {}
		try {json.put("picture_url", picture);} catch (Exception e) {}
		try {json.put("facebook_id", fbId);} catch (Exception e) {}
		try {json.put("phone_num", phoneNum);} catch (Exception e) {}
		try {json.put("is_app_user", isAppUser);} catch (Exception e) {}
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
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	
	public boolean isFacebookActivated() {
		if(fbId != null && !fbId.equals("")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) 	{		
		parcel.writeString(id);
		parcel.writeString(name);
		parcel.writeString(picture);
		parcel.writeString(fbId);
		parcel.writeString(phoneNum);
		parcel.writeInt(isAppUser);
	}
	
	public AppUser(Parcel parcel) 	{	// order does matter here
		id = parcel.readString();
		name = parcel.readString();
		picture = parcel.readString();
		fbId = parcel.readString();
		phoneNum = parcel.readString();
		isAppUser= parcel.readInt();

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
