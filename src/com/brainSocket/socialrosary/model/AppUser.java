package com.brainSocket.socialrosary.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class AppUser /*implements Parcelable */{		
	
	public enum GENDER {MALE, FEMALE} ;
	
	public static final String DEFAULT_AVATAR=  "https://s3-us-west-2.amazonaws.com/almuajaha/profile/avatar01.png" ;
	

	String phoneNum = null;
	String id = null;
	String name = null;
	String countryId ;
	String gcmId ;
	String picture ;
	String accessToken ;
	GENDER gender ;
	Long date ;
	long score ; 
	 
		

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
			if(json.has("mobileNumber")) 
				phoneNum = json.getString("mobileNumber");
		} 
		catch (Exception e) {}
		try {
			if(json.has("userName")) 
				name = json.getString("userName");
		} 
		catch (Exception e) {}
		try {
			if(json.has("countryId")) 
				countryId = json.getString("countryId");
		} 
		catch (Exception e) {}
		try {
			if(json.has("isMale")) 
				gender = (json.getInt("isMale") == 1)?GENDER.MALE:GENDER.FEMALE;
		}catch (Exception e) {gender = GENDER.MALE;}
		try {
			if(json.has("accessToken"))
				accessToken = json.getString("accessToken");
		}catch (Exception e) {}
		try {
			if(json.has("score"))
				score = json.getLong("score");
		}catch (Exception e) {}
		try {
			if(json.has("gcmId"))
				gcmId = json.getString("gcmId");
		}catch (Exception e) {}
		try {
			if(json.has("date"))
				date = json.getLong("date");
		}catch (Exception e) {}
		try {
			if(json.has("photo")) 
				picture = json.getString("photo");
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
		try {json.put("userName", name);} catch (Exception e) {}
		try {json.put("photo", picture);} catch (Exception e) {}
		try {json.put("date", date);} catch (Exception e) {}
		try {json.put("mobileNumber", phoneNum);} catch (Exception e) {}
		try {json.put("score", score);} catch (Exception e) {}
		try {json.put("accessToken", accessToken);} catch (Exception e) {}
		try {json.put("isMale", (gender==GENDER.MALE)?1:0);} catch (Exception e) {}
		try {json.put("gcmId", gcmId);} catch (Exception e) {}
		try {json.put("countryId", countryId);} catch (Exception e) {}
		
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

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
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

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getGcmId() {
		return gcmId;
	}

	public void setGcmId(String gcmId) {
		this.gcmId = gcmId;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public GENDER getGender() {
		return gender;
	}

	public void setGender(GENDER gender) {
		this.gender = gender;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public static String getDefaultAvatar() {
		return DEFAULT_AVATAR;
	}
	
	
	
	
	

/*	@Override
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

	}*/
	

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
