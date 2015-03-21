package com.brainSocket.socialrosary.contacts;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

public class Utils {

	public static void sendViaWhatsapp(Context context, String message){

		    PackageManager pm = context.getPackageManager();
		    try {
		    	Uri uri = Uri.parse("smsto:963944577857@s.whatsapp.net" );
		    	Intent smsSIntent = new Intent(Intent.ACTION_SENDTO, uri);
		    	smsSIntent.putExtra("body",message);	
		    	smsSIntent.putExtra("chat", true);
		        PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);//just to get exception if not installed
		        smsSIntent.setPackage("com.whatsapp");		       
		        context.startActivity(smsSIntent);
		   } catch (NameNotFoundException e) {
		        Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
		                .show();
		   }  
	}
	public static void sendViaWhatsapp(Context context, String contactId, String message){
		String phoneNumber = getPhoneNumber(context, contactId);
		Log.i("phone",phoneNumber);
		Cursor c = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
		        new String[] { ContactsContract.Data._ID }
		        ,ContactsContract.Data.DATA1 + "=?"
		        ,new String[]{phoneNumber + "@s.whatsapp.net"}
				, null);
		c.moveToFirst();
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("content://com.android.contacts/data/" + c.getString(0)));
		context.startActivity(i);
	}
	private static String getPhoneNumber(Context context, String contactId) {
		Cursor pCursor = context.getContentResolver().query(
	 		    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	 		    new String[] {ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER}, 
	 		    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
	 		    new String[]{contactId}
	 		    , null);
	 	       pCursor.moveToFirst();
	 	       return pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)).replace("+", "");
	}
	public static void showData(Context context, String contactId) {
		Cursor c = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
		        new String[] { ContactsContract.RawContacts._ID,ContactsContract.RawContacts.ACCOUNT_NAME,ContactsContract.RawContacts.ACCOUNT_TYPE }
		        ,ContactsContract.RawContacts.CONTACT_ID + "=?"
		        ,new String[]{contactId}
				, null);
		c.moveToFirst();
		int idIndex = c.getColumnIndex(ContactsContract.RawContacts._ID);
		int accountNameIndex = c.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME);
		int accountTypeINdex = c.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE);
		while (!c.isAfterLast()) {
			Log.i("contactInfo",c.getString(accountTypeINdex) + ":" + c.getString(accountNameIndex));
			
			Cursor c2 = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
			        new String[] { ContactsContract.Data.MIMETYPE,ContactsContract.Data.DATA1,ContactsContract.Data.DATA2 }
			        ,ContactsContract.Data.RAW_CONTACT_ID + "=?"
			        ,new String[]{c.getString(idIndex)}
					, null);
			c2.moveToFirst();
			int mimeIndex = c2.getColumnIndex(ContactsContract.Data.MIMETYPE);
			int data1INdex = c2.getColumnIndex(ContactsContract.Data.DATA1);
			int dat21INdex = c2.getColumnIndex(ContactsContract.Data.DATA2);
			while (!c2.isAfterLast()) {
				Log.i("contactInfo/data",c2.getString(mimeIndex) + ":" + c2.getString(data1INdex) + ":" + c2.getShort(dat21INdex));
				c2.moveToNext();
			}
			c.moveToNext();
		}
	}
}
