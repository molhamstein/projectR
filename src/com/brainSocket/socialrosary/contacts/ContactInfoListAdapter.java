package com.brainSocket.socialrosary.contacts;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.brainSocket.socialrosary.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactInfoListAdapter extends ResourceCursorAdapter {

	private final String TAG = "ContactInfoListAdapter";
	private final Context mApplicationContext;
	private final int mBitmapSize;
	private final BitmapDrawable mNoPictureBitmap;
	private List<String> mWhatsappContactsIds;
	private List<String> mViberContactsIds;

	public ContactInfoListAdapter(Context context, int layout, Cursor c,
			int flags) {

		super(context, layout, c, flags);		
		mApplicationContext = context;

		// default thumbnail photo
		mNoPictureBitmap = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.contact);
		mBitmapSize = (int) context.getResources().getDimension(
				R.dimen.textview_height);
		mNoPictureBitmap.setBounds(0, 0, mBitmapSize, mBitmapSize);

	}
	public void setWhatsappContacts(List<String> whatsappContacts){
		mWhatsappContactsIds = whatsappContacts;
	}
	public void setViberContacts(List<String> viberContacts){
			mViberContactsIds = viberContacts;
	}
	// Create and return a new contact data view
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return inflater.inflate(R.layout.list_item, parent, false);

	}

	// Update and return a contact data view
	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		TextView textView = (TextView) view.findViewById(R.id.name);
		ImageView whatsappIcon = (ImageView) view.findViewById(R.id.whatsappIcon);
		ImageView viberIcon = (ImageView) view.findViewById(R.id.viberIcon);
		textView.setText(cursor.getString(cursor
				.getColumnIndex(Contacts.DISPLAY_NAME)));
		final String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID));
		if(mWhatsappContactsIds.contains(contactId)){			
			whatsappIcon.setVisibility(View.VISIBLE);
			whatsappIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {					
					//Utils.sendViaWhatsapp(mApplicationContext, ":)");
					Log.i(TAG, contactId);
					//Utils.sendViaWhatsapp(mApplicationContext, contactId,";(");
					Utils.sendViaWhatsapp(mApplicationContext, ";)");
					}
			});
		}
		else {
			whatsappIcon.setVisibility(View.INVISIBLE);
		}
		if(mViberContactsIds.contains(contactId)){			
			viberIcon.setVisibility(View.VISIBLE);	
			viberIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Utils.showData(mApplicationContext,contactId);
					
				}
			});
			}
		else {
			viberIcon.setVisibility(View.INVISIBLE);
		}

		// Default photo
		BitmapDrawable photoBitmap = mNoPictureBitmap;

		// Get actual thumbnail photo if it exists
		String photoContentUri = cursor.getString(cursor
				.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI));

		if (null != photoContentUri) {

			InputStream input = null;

			try {

				// Read thumbnail data from input stream
				input = context.getContentResolver().openInputStream(
						Uri.parse(photoContentUri));

				if (input != null) {

					photoBitmap = new BitmapDrawable(
							mApplicationContext.getResources(), input);
					photoBitmap.setBounds(0, 0, mBitmapSize, mBitmapSize);

				}
			} catch (FileNotFoundException e) {

				Log.i(TAG, "FileNotFoundException");

			}
		}

		// Set thumbnail image
		textView.setCompoundDrawables(photoBitmap, null, null, null);

	}
}