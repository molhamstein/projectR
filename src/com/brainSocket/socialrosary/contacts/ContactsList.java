package com.brainSocket.socialrosary.contacts;

import java.util.ArrayList;
import java.util.List;

import com.brainSocket.socialrosary.R;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;

public class ContactsList extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String columnsToExtract[] = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME, Contacts.PHOTO_THUMBNAIL_URI };
		ContentResolver contentResolver = getContentResolver();
		// filter contacts with empty names
		String whereClause = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
				+ Contacts.DISPLAY_NAME + " != '' ) AND (" + Contacts.HAS_PHONE_NUMBER + "='1' )) ";		
		Cursor cursor = contentResolver.query(Contacts.CONTENT_URI,
				columnsToExtract, whereClause, null, null);	
		ContactInfoListAdapter contactsAdapter = new ContactInfoListAdapter(this, R.layout.list_item,
				cursor, 0);
		contactsAdapter.setWhatsappContacts(extractAccountContacts("com.whatsapp"));
		//contactsAdapter.setViberContacts(extractAccountContacts("com.viber.voip.account"));
		contactsAdapter.setViberContacts(extractViberContacts());
		setListAdapter(contactsAdapter);
	}
	private List<String> extractAccountContacts(String accountType) {
		String[] columnsToExtract = new String[]{RawContacts.CONTACT_ID,RawContacts.ACCOUNT_NAME};
		ContentResolver mResolver = getContentResolver();		
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

		String[] columnsToExtract = new String[]{ContactsContract.Data.RAW_CONTACT_ID};
		ContentResolver mResolver = getContentResolver();		
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
		List<String> viberContactIds = new ArrayList<String>();
		if(c2.moveToFirst()){
			while(!c2.isAfterLast()) {
				viberContactIds.add(c2.getString(c2.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)));
				c2.moveToNext();
			}
		}
		c2.close();
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