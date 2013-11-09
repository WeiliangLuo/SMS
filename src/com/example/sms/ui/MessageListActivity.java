package com.example.sms.ui;

import com.example.sms.Contact;
import com.example.sms.R;
import com.example.sms.R.layout;
import com.example.sms.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MessageListActivity extends Activity {
	private Contact rec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);
		rec = new Contact(1, "Will", "2106302912");
				
				
		// Show the up button
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(rec.getName());
		actionBar.setSubtitle(rec.getPhoneNumber());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_list_menu, menu);
		return true;
	}
	
	/**
	 *	Handle click on action bar 
	 * 
	 **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_call:
	        	String uri = "tel:" + rec.getPhoneNumber();
	        	Intent intent = new Intent(Intent.ACTION_DIAL);
	        	intent.setData(Uri.parse(uri));
	        	startActivity(intent);
	            return true;
	        case R.id.action_add_contact:
	        	// Ask stock contact manager to insert the new contact
	        	Intent in = new Intent(Intents.Insert.ACTION);
	        	in.setType(ContactsContract.RawContacts.CONTENT_TYPE);
	        	in.putExtra(Intents.Insert.PHONE, rec.getPhoneNumber());
	        	startActivity(in);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
