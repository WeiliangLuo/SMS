package com.example.sms.ui;

import java.util.List;

import com.example.sms.Constant;
import com.example.sms.Conversation;
import com.example.sms.Message;
import com.example.sms.MessageManager;
import com.example.sms.R;
import com.example.sms.R.layout;
import com.example.sms.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ScheduleListActivity extends ListActivity {
	private static final String TAG = "ScheduleListActivity";
	private ListView listView;
	private List<Message> scheduledMessages;
	private ScheduledMessageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_list);
		
		OnItemClickListener listener = new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> lv, View v, int position, long id) {
				Message msg = (Message) adapter.getItem(position);
				long mid = msg.getId();
				String phoneNumber = msg.getContact().getPhoneNumber();
				String content = msg.getContent();
				int repeat = msg.getRepeat();
				long timestamp = msg.getTimeStamp();
				
				Bundle b = new Bundle();
				b.putLong("id", mid);
				b.putString("address", phoneNumber);
				b.putString("body", content);
				b.putInt("repeat", repeat);
				b.putLong("timestamp", timestamp);
				Intent in = new Intent(ScheduleListActivity.this, ScheduleSMSActivity.class);
				in.putExtras(b);
				startActivity(in);
			}
		};
		this.setTitle("ScheduledSMS");
		initData();
		getListView().setOnItemClickListener(listener);
		getListView().requestFocus();
		//registerForContextMenu(getListView());
	}

	@Override
	protected void onResume(){
		initData();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule_list_menu, menu);
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
	        case R.id.action_create_message:
	        	Intent intent = new Intent(ScheduleListActivity.this,
                        ScheduleSMSActivity.class);
                startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void initData(){
		scheduledMessages = MessageManager.getScheduledMessages(this);
	    // associate data with listView
		adapter = new ScheduledMessageAdapter(this, scheduledMessages);
		setListAdapter(adapter);
		//getListView().setSelectionFromTop(adapter.getCount(), 0);
	}
}
