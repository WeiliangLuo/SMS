package com.example.sms.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sms.Contact;
import com.example.sms.ContactManager;
import com.example.sms.Message;
import com.example.sms.MessageManager;
import com.example.sms.R;

public class NewMessageActivity extends Activity implements OnClickListener {
	private final static String TAG = "NewMessageActivity";
	private ImageButton btn_send;
	private ImageButton btn_add_rec;
	private EditText et_to;
	private EditText et_content;
	private Message draft;
	private long cid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		// initialize a empty draft
		cid = -1;
		draft = new Message(-1, cid, "", Message.MESSAGE_TYPE_DRAFT, false, 0, null, null);
				
		btn_send = (ImageButton) findViewById(R.id.btn_send);
		btn_add_rec = (ImageButton) findViewById(R.id.btn_add);
		et_to = (EditText) findViewById(R.id.text_to);
		et_content = (EditText) findViewById(R.id.text_content);
		
		btn_send.setOnClickListener(this);
		btn_add_rec.setOnClickListener(this);
		et_content.addTextChangedListener(contentWatcher);
		
		Intent intent = getIntent();
		et_content.setText(intent.getStringExtra("content"));
		if(et_content.length()==0){
			btn_send.setEnabled(false);
		}
		
		// Show the up button
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onDestroy(){
		String number = parseRecipient();
		if(number!="" && draft.getContent().length()!=0){
			Contact receiver = ContactManager.getContactByNumber(this, number);
			cid = MessageManager.getOrCreateConversationId(this, number);
			draft.setConversationId(cid);
			draft.setReceiver(receiver);
			draft.setTimeStamp(System.currentTimeMillis());
			draft.insert(this);
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.empty_menu, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	// finish this activity when up button is clicked.
            	// User will back to where he is from.
                finish();
                return true;
        }
        return false;
    }

	@Override
	public void onClick(View v) {
		if(v.equals(btn_add_rec)){
			Log.i(TAG, "Add recipients");
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, 1);
		}
		else if(v.equals(btn_send)){
			Log.i(TAG, "Send message");

			if(sendMessage()){
				// clean content text
				et_content.setText("");
				// Swtich to MessageListActivity
				Intent intent = new Intent(NewMessageActivity.this,
		                MessageListActivity.class);
		        intent.putExtra("cid", cid);
		        startActivity(intent);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(data!=null){
			Uri uri = data.getData();
			
	        if (uri != null) {
	            Cursor c = null;
	            try {
	                c = getContentResolver().query(uri, new String[]{ 
	                            ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY },
	                        null, null, null);

	                if (c != null && c.moveToFirst()) {
	                    String number = c.getString(0);
	                    String name = c.getString(1);
	                    String recipent = String.format("%s <%s>", name, number);
	                    et_to.setText(recipent);
	                }
	            } finally {
	                if (c != null) {
	                    c.close();
	                }
	            }
	        }
		}
	}

	public void showSelectedNumber(String type, String number) {
	    Toast.makeText(this, type + ": " + number, Toast.LENGTH_LONG).show();      
	}

	private TextWatcher contentWatcher = new TextWatcher(){
		@Override
		public void afterTextChanged(Editable etd) {
			if(etd.length()==0){
				btn_send.setEnabled(false);
			}
			else{
				btn_send.setEnabled(true);
			}
			draft.setContent(etd.toString());
		}
	
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
	
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private String parseRecipient(){
		String to = et_to.getText().toString();
		String number = "";
		if(to.length()!=0){
			if(to.contains("<")){
				// get <content> inside <>
				Pattern pattern = Pattern.compile("<(.*?)>");
				Matcher matcher = pattern.matcher(to);
				if (matcher.find()){
				    to = matcher.group(1);
				    // get numbers
					pattern = Pattern.compile("\\d+");
					matcher = pattern.matcher(to);
					while(matcher.find()){
						number += matcher.group();
					}
				}
			}
			else{
				// input must be all numberic characters.
				if(to.matches("^[0-9]+$")){
					number = to;
				}	
			}
		}
		return number;
	}
	
	private boolean sendMessage(){
		String number = parseRecipient();
		if(number==""){
			Toast.makeText(this, 
					"Invalid recipient:"+et_to.getText().toString(), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// update database
		cid = MessageManager.getOrCreateConversationId(this, number);
		Contact receiver = ContactManager.getContactByNumber(this, number);
		Message sentMsg = new Message(draft);
		sentMsg.setConversationId(cid);
		sentMsg.setReceiver(receiver);
		sentMsg.setType(Message.MESSAGE_TYPE_SENT);
		sentMsg.setTimeStamp(System.currentTimeMillis());
		
		// send message
		MessageManager.sendMessage(sentMsg);
		sentMsg.insert(this);

		Log.i(TAG, "cid:"+cid);
		return true;
	}
}
