package com.example.sms.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sms.R;

public class NewMessageActivity extends Activity implements OnClickListener {
	private final static String TAG = "NewMessageActivity";
	private ImageButton btn_send;
	private ImageButton btn_add;
	private EditText et_to;
	private EditText et_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);
		
		btn_send = (ImageButton) findViewById(R.id.btn_send);
		btn_add = (ImageButton) findViewById(R.id.btn_add);
		et_to = (EditText) findViewById(R.id.text_to);
		et_content = (EditText) findViewById(R.id.text_content);
		
		btn_send.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		
		Intent intent = getIntent();
		et_content.setText(intent.getStringExtra("content"));
		// Show the up button
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
		if(v.equals(btn_add)){
			Log.i(TAG, "Add recipients");
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, 1);
		}
		else if(v.equals(btn_send)){
			Log.i(TAG, "Send message");
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
	                    String recipent = String.format("%s <%s>; ", name, number);
	                    et_to.append(recipent);
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
}
