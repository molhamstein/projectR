package com.brainSocket.socialrosary.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;

import com.brainSocket.socialrosary.RosaryApp;
import com.brainSocket.socialrosary.model.AppContact;
import com.brainSocket.socialrosary.model.AppContact.SOCIAL_MEDIA_ACCOUNT_TYPE;

public class ContactsMgr {
	
	
	private static ContactsMgr instance ;
	public static ContactsMgr getInstance() {
		if(instance == null) {
			instance = new ContactsMgr();
		}
		return instance;
	}
	
	private ContactsMgr() {
		
	}
	
	public ArrayList<AppContact> getLocalContacts (Context context){
		ArrayList<AppContact> contacts = new ArrayList<AppContact>() ;
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
		
		while (phones.moveToNext()){
			AppContact contact = new AppContact(phones);
			contacts.add(contact);
		}
		List<String> viberIds = extractViberContacts() ;
		List<String> whatsAppIds = getWhatAppContacts() ;
		for (AppContact con : contacts) {
			String id = con.getLocalId() ;
			if(viberIds.contains(id)){
				con.setNetwork(SOCIAL_MEDIA_ACCOUNT_TYPE.VIBER);
			}
			if(whatsAppIds.contains(con.getLocalId())){
				con.setNetwork(SOCIAL_MEDIA_ACCOUNT_TYPE.WHATSAP);
			}	
		}
		
		return contacts ;
		
		
		/*String columnsToExtract[] = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.PHOTO_THUMBNAIL_URI };
		ContentResolver contentResolver = context.getContentResolver();
		// filter contacts with empty names
		String whereClause = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND (" + Contacts.DISPLAY_NAME + " != '' ) AND (" + Contacts.HAS_PHONE_NUMBER + "='1' )) ";
		Cursor cursor = contentResolver.query(Contacts.CONTENT_URI,	columnsToExtract, whereClause, null, null);*/
		
		
	}
	
	public List<String> getWhatAppContacts () {
		return extractAccountContacts("com.whatsapp") ;
	}
	
	
	private List<String> extractAccountContacts(String accountType) {
		String[] columnsToExtract = new String[]{RawContacts.CONTACT_ID,RawContacts.ACCOUNT_NAME};
		ContentResolver mResolver = RosaryApp.getAppContext().getContentResolver();		
		Cursor c = mResolver.query(
		        RawContacts.CONTENT_URI,
		        columnsToExtract,
		        RawContacts.ACCOUNT_TYPE + "=?",
		        new String[]{accountType},
		        null);
		
		List<String> accountContacts = new ArrayList<String>();
		int contactIdColumn = c.getColumnIndex(RawContacts.CONTACT_ID);		
		while (c.moveToNext())
		{		    
		    accountContacts.add(c.getString(contactIdColumn));
		}
		//Log.i(accountType,accountContacts.toString());
		return accountContacts;
	}
	
	private List<String> extractViberContacts(){
		List<String> viberContactIds = new ArrayList<String>();
		try{
			String[] columnsToExtract = new String[]{ContactsContract.Data.RAW_CONTACT_ID};
			ContentResolver mResolver = RosaryApp.getAppContext().getContentResolver();		
			Cursor c = mResolver.query(
			        ContactsContract.Data.CONTENT_URI,
			        columnsToExtract,
			        ContactsContract.Data.MIMETYPE + "=?",
			        new String[]{"vnd.android.cursor.item/vnd.com.viber.voip.viber_out_call_viber"},
			        null);
			List<String> viberRawContactIds = new ArrayList<String>();
			if(c.moveToFirst()) {
				while(!c.isAfterLast()) {
					viberRawContactIds.add(c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID)));
					c.moveToNext();
				}
			}
			c.close();
			columnsToExtract = new String[]{ContactsContract.RawContacts.CONTACT_ID};
			String selection = ContactsContract.Data._ID + " IN (" + makePlaceholders(viberRawContactIds.size()) + ")";
			String[] selectionArgs = viberRawContactIds.toArray(new String[viberRawContactIds.size()]);	
			Cursor c2 = mResolver.query(
					ContactsContract.RawContacts.CONTENT_URI,
					columnsToExtract,
					selection,
					selectionArgs,
					null);
			if(c2.moveToFirst()){
				while(!c2.isAfterLast()) {
					viberContactIds.add(c2.getString(c2.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)));
					c2.moveToNext();
				}
			}
			c2.close();
		}catch(Exception e){}
		return viberContactIds;
	}

	private String makePlaceholders(int len) {
	    StringBuilder sb = new StringBuilder(len * 2 - 1);
	    sb.append("?");
	    for (int i = 1; i < len; i++)
	        sb.append(",?");
	    return sb.toString();
	}

}
