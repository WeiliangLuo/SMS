package com.example.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactManager {
	private final static String TAG = "ContactManager";
	
	/**
	 * Look for contact that match given number
	 * by query Contact Provider in android
	 * 
	 * Return Contact with name as null, 
	 * if no match is found
	 * 
	 **/
	public static Contact getContactByNumber(Context context, String number){
		Contact res = new Contact(-1, null, number);
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Cursor cur = context.getContentResolver().query(uri, 
				new String[] {PhoneLookup._ID, PhoneLookup.DISPLAY_NAME}, 
				null, 
				null, 
				null);
		if(cur!=null){
			if(cur.moveToFirst()){
	            String name = cur.getString(cur.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	            long id = cur.getLong(cur.getColumnIndex(ContactsContract.Data._ID));
	            res = new Contact(id, name, number);
			}
			cur.close();
		}
		return res;
	}
}