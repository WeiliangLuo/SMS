package com.example.sms.ui;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.sms.Message;
import com.example.sms.MessageManager;
import com.example.sms.R;

public class SearchActivity extends ListActivity implements OnItemClickListener {
	private final static String TAG = "SearchActivity";
	
	private List<Message> messages;
	private MessageAdapter adapter;
	private int count;
	private String query;

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			query = intent.getStringExtra(SearchManager.QUERY);
			messages = MessageManager.searchMessages(this, query);
			count = messages.size();
			  
			// display search result
			adapter = new MessageAdapter(this, messages, MessageAdapter.TYPE_BASIC);
			setListAdapter(adapter);
			getListView().setOnItemClickListener(this);
		}
		
		// show up button 
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(count + " matches for \"" + query+"\"");
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
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		Message msg = (Message) adapter.getItem(pos);
		long cid = msg.getConversationId();
		Intent intent = new Intent(SearchActivity.this,
                MessageListActivity.class);
        intent.putExtra("cid", cid);
        startActivity(intent);
	}

}
