package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.app.AlertDialog.Builder;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Intents;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sms.Conversation;
import com.example.sms.MessageManager;
import com.example.sms.R;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {	
	private static final String TAG = "MainActivity";
	private ListView listView;
	private List<Conversation> conversationList;
	private ConversationAdapter adapter;
	
	private MenuItem searchItem;
	private SearchView searchView;
	
	private MultiChoiceModeListener multiChoiceModeListener = new MultiChoiceModeListener() {
	    
		@Override
	    public void onItemCheckedStateChanged(ActionMode mode, int position,
	                                          long id, boolean checked) {
	        // Here you can do something when items are selected/de-selected,
	        // such as update the title in the CAB
	    	mode.setTitle(listView.getCheckedItemCount()+" conversation selected");
	    }

	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        // Respond to clicks on the actions in the CAB
	        switch (item.getItemId()) {
            case R.id.action_delete:
            	// Get checkedPositions before show alertDialog
            	// becuase alertDialog will deselect items selected in listView
    			List<Integer> checkedPositions = new ArrayList<Integer>();    			
    			SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
    			if(checkedItems!=null){
    				int count = adapter.getCount();
    				for(int pos=0; pos<count; pos++){
    					if(checkedItems.get(pos)){
    						checkedPositions.add(pos);
    					}
    				}
    			}
            	delete_conversations(checkedPositions);
            	
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
	        }
	    }

	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate the menu for the CAB
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.conversation_context_menu, menu);
	        mode.setTitle(listView.getCheckedItemCount()+" conversation selected");
	        return true;
	    }

	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        // Here you can make any necessary updates to the activity when
	        // the CAB is removed. By default, selected items are deselected/unchecked.
	    }

	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        // Here you can perform updates to the CAB due to
	        // an invalidate() request
	        return false;
	    }
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    listView = (ListView) findViewById(R.id.listview);
	    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	    listView.setMultiChoiceModeListener(multiChoiceModeListener);
	    
	    // associate data with listView
	    conversationList = MessageManager.getConversations(this);
	    // sort conversationList by timeStamp
	    Collections.sort(conversationList, new Comparator<Conversation>(){
			public int compare(Conversation o1, Conversation o2){
				Long l1 = Long.valueOf(o1.getTimeStamp());
				Long l2 = Long.valueOf(o2.getTimeStamp());
				return l2.compareTo(l1);
			}
		});
	    adapter = new ConversationAdapter(this, conversationList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_list_menu, menu);
		searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setIconified(true);
		
		SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
	        @Override
	        public boolean onQueryTextSubmit(String query) {
	            Intent intent = new Intent(Intent.ACTION_SEARCH);
	            intent.setClass(MainActivity.this, SearchActivity.class);
	            intent.putExtra(SearchManager.QUERY, query);
	            startActivity(intent);
	            searchItem.collapseActionView();
	            return true;
	        }

	        @Override
	        public boolean onQueryTextChange(String newText) {
	            return false;
	        }
	    };
	    
		searchView.setOnQueryTextListener(queryTextListener);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setIconifiedByDefault(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchManager != null) {
            SearchableInfo info = searchManager.getSearchableInfo(this.getComponentName());
            searchView.setSearchableInfo(info);
        }
        
		return true;
	}

    @Override
    public boolean onSearchRequested() {
        if (searchItem != null) {
            searchItem.expandActionView();
        }
        return true;
    } 

	/**
	 *	Handle click action on listView Item 
	 * 
	 **/
	@Override
	public void onItemClick(AdapterView<?> lv, View v, int position, long id) {
		ListView listView = (ListView) lv;
		Conversation c = (Conversation) listView.getItemAtPosition(position);
        Intent intent = new Intent(MainActivity.this,
                MessageListActivity.class);
        intent.putExtra("cid", c.getId());
        startActivity(intent);
		
		Log.e("Test", String.valueOf(c.getId()));
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
                Intent intent = new Intent(MainActivity.this,
                        NewMessageActivity.class);
                startActivity(intent);
	            return true;
	        case R.id.action_delete_all:
	        	delete_conversations(null);
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
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;

		for(Conversation c:conversationList){
			map = new HashMap<String, Object>();
			map.put("name", c.getContact().getNameOrNumber());
			map.put("count", c.getMsgCount());
			map.put("summary", c.getSummary());
			map.put("cid", c.getId());
			Date date = new Date(c.getTimeStamp());
			map.put("date", dateFormat.format(date));
    		list.add(map);
		}
        return list;
	}
	
	/**
	 *	Delete conversations  
	 * 
	 *  @param positions : positions of item to be deleted
	 *  				   delete all conversations when null
	 **/
	private void delete_conversations(List<Integer> positions){
		final List<Integer> checkedPositions = positions;
		
		// prompt alert dialog, ask user's confirmation
    	AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
    	alert.setTitle("Alert");
    	alert.setMessage("Conversations will be permanently deleted! Are you sure to continue?");
    	alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			// TODO update database in another thread.  
    			
    			// delete selected items
    			if(checkedPositions!=null){
	    			// sort the positions in descending order
	    			// so that it is safe to delete data item by locations
	    			Collections.sort(checkedPositions, new Comparator<Integer>(){
	    				public int compare(Integer o1, Integer o2){
	    					return o2.compareTo(o1);
	    				}
	    			});
	    			for(Integer pos:checkedPositions){
	    				Log.i(TAG, "Remove data from list"+pos.toString());
	    				adapter.remove(pos.intValue());
	    			}
    			}
    			// delete all items
    			else{
    				adapter.clear();
    			}
    			adapter.notifyDataSetChanged();
    			Log.i(TAG, "conversations deleted");
    		}
    	});
    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
        		  Log.i(TAG, "conversations not deleted");
    		}
    	});
    	alert.show();
	}
}
