package com.example.sms.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sms.R;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {	
	private ListView listView;
	private List<Map<String, Object>> list;

	private MenuItem searchItem;
	private SearchView searchView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    listView = (ListView) findViewById(R.id.listview);
	    
		list = getData();
		SimpleAdapter adapter = new SimpleAdapter(this, 
				list,
				R.layout.conversation_list,
				new String[]{"name", "count", "summary", "date"},
				new int[]{R.id.name, R.id.count, R.id.summary, R.id.date});
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
	            Intent intent = new Intent();
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
		Map<String, Object> ob = (Map<String, Object>) listView.getItemAtPosition(position);
        Intent intent = new Intent(MainActivity.this,
                MessageListActivity.class);
        intent.putExtra("cid", Integer.parseInt(ob.get("cid").toString()));
        startActivity(intent);
		
		Log.e("Test", ob.get("cid").toString());
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
	            // delete all
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	List<Map<String, Object>> getData(){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		
		for(int i=0; i<10; i++){
			map = new HashMap<String, Object>();
			map.put("name", "Weiliang Luo");
			map.put("count", i);
			map.put("summary", "This is the "+i+"th message so good it is haha");
			map.put("date", "Nov 6");
			map.put("cid", i);
    		list.add(map);        	
		}
        return list;
	}
}
