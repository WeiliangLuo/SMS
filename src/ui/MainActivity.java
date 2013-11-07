package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.sms.R;

public class MainActivity extends Activity implements OnItemClickListener {	
	private ListView listView;
	private List<Map<String, Object>> list;

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
		ActionBar actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.conversation_list_menu, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
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
    		list.add(map);        	
		}
        return list;
	}
}
