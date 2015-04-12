package com.brainSocket.socialrosary.model;

import org.json.JSONObject;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class AppZiker  {		

	int i;
	String content;
	
	public  AppZiker(String content,int j){
		content=content;
		i=j;
	}
	public int getCount(){return i;}
	
	public String toString(){
		return content;
	}
        
	
	public String getContent() {
		return content;
	}
}
