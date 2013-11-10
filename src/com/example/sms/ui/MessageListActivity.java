package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Intents;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.sms.Contact;
import com.example.sms.Message;
import com.example.sms.R;

public class MessageListActivity extends ListActivity implements OnItemClickListener, OnClickListener{
	private Contact rec;
	private ImageButton btn_send; 
	private EditText et_content;
	private List<Message> messages;
	private MessageAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);
		btn_send = (ImageButton) findViewById(R.id.btn_send);
		et_content = (EditText) findViewById(R.id.text_content);
		btn_send.setOnClickListener(this);
		
		// initialize conversation data
		Intent intent = getIntent();
		int cid = intent.getIntExtra("cid", 0);
		rec = new Contact(1, null, "2106302912");
		messages = new ArrayList<Message>();
		
		// Show the up button
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Set activity title
		if(rec.getName()!=null){
			actionBar.setTitle(rec.getName());
			actionBar.setSubtitle(rec.getPhoneNumber());
		}
		else{
			actionBar.setTitle(rec.getPhoneNumber());
		}
			    
	    // associate data with listView
		for(int i=0; i<6; i++){
			if(i%2==0){
				Message msg = new Message(i, 
						"Message from me, this is a very nice day. We are going to picnic tommorrow." +
						"I am so happy. How are u doing?", 
						Message.SMS_TYPE_SENT, 
						Message.SMS_READ, 
						12345678, 
						null,
						rec);
				messages.add(msg);
			}
			else{
				Message msg = new Message(i, 
						"Replied message from Bob", 
						Message.SMS_TYPE_RECEIVED, 
						Message.SMS_READ, 
						123456789, 
						rec,
						null);
				messages.add(msg);
			}
		}
		adapter = new MessageAdapter(this, messages);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().requestFocus();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_list_menu, menu);
		menu.getItem(1).setEnabled(rec.getName()==null);
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
	
	/**
	 *	Prepare data for listView 
	 * 
	 **/
	private List<Map<String, Object>> getData(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		for(int i=0; i<10; i++){
			map = new HashMap<String, Object>();
			map.put("content", "Hello, this is a fake message");
			map.put("date", "Nov 9");
			map.put("full_timestamp", "1:33PM Nov 6");
			map.put("mid", i);
    		list.add(map);        	
		}
        return list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		show_message_detail(pos);
	}
	
	/// Floating contextual Menu ///
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	            ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.message_context_menu, menu);
		menu.setHeaderTitle("Message options");
	}

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = 
    			(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	int pos = info.position;
    	
        switch (item.getItemId()) {
            case R.id.action_forward:
                forward_message(pos);
                return true;
            case R.id.action_delete:
                delete_message(pos);
                return true;
            case R.id.action_detail:
            	show_message_detail(pos);
            	return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    ///END: Floating contextual Menu///
    
    private void forward_message(int position){
    	Message msg = (Message) getListView().getItemAtPosition(position);
        Intent intent = new Intent();
        intent.setClass(MessageListActivity.this, NewMessageActivity.class);        
        intent.putExtra("content", msg.getContent());
        startActivity(intent);
    }
    
    private void delete_message(int position){
    	//Message msg = (Message) getListView().getItemAtPosition(position);
    	adapter.remove(position);
    	adapter.notifyDataSetChanged();
    }
    
    private void show_message_detail(int position){
		String detail = "";
		
		Message msg = (Message) getListView().getItemAtPosition(position);
		Date date = new Date(msg.getTimeStamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:m a");
        
		if(msg.fromMe()){
			detail += "To: "+rec.getPhoneNumber()+"\n";
			detail += "Sent: "+dateFormat.format(date);
		}
		else{
			detail += "From: "+rec.getPhoneNumber()+"\n";
			detail += "Received: "+dateFormat.format(date);
		}
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Message detials");
		alert.setMessage(detail);
		alert.show();
    }

	@Override
	public void onClick(View v) {
		if(v.equals(btn_send)){
			//TODO send message out
		}
	}
}
