package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sms.Conversation;
import com.example.sms.R;

public class ConversationAdapter extends BaseAdapter {
	private final static String TAG = "ConversationAdapter";
	
	private List<Conversation> mConversations;
	private Context mContext;
	
	public ConversationAdapter(Context context, List<Conversation> conversations) {
        super();
        this.mContext = context;
        this.mConversations = conversations;
	}
	
	@Override
	public int getCount() {
		return mConversations.size();
	}

	@Override
	public Object getItem(int position) {
		return mConversations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mConversations.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Conversation c = (Conversation) getItem(position);
        ViewHolder holder;
        if(convertView == null){
        	holder = new ViewHolder();
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_row, parent, false);
        	holder.name = (TextView) convertView.findViewById(R.id.name);
        	holder.count = (TextView) convertView.findViewById(R.id.count);
        	holder.draft = (TextView) convertView.findViewById(R.id.draft);
        	holder.summary = (TextView) convertView.findViewById(R.id.summary);
        	holder.date = (TextView) convertView.findViewById(R.id.date);
        	//holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
        	
        	holder.draft.setTextColor(Color.RED);
        	holder.count.setTextColor(Color.GRAY);
        	holder.date.setTextColor(Color.GRAY);
        	convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }        
		
        holder.name.setText(c.getContact().getNameOrNumber());
        holder.count.setText(String.valueOf(c.getMsgCount()));
        holder.summary.setText(c.getSummary());

        // Set up time display format
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(c.getTimeStamp());
        Calendar curDate = Calendar.getInstance();
        curDate.setTimeInMillis(System.currentTimeMillis());   
		SimpleDateFormat dateFormat = null;
		if(date.get(Calendar.YEAR)==curDate.get(Calendar.YEAR)){
			if(date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR) ){
				dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
			}
			else{
				dateFormat = new SimpleDateFormat("MMM d", Locale.US);
			}
		}
		else{
			dateFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
		}
        holder.date.setText(dateFormat.format(c.getTimeStamp()));

        
        if(c.hasUnread()){
        	//holder.relativeLayout.setBackground();
        	Log.i("ConversationAdapter", c.getContact().getNameOrNumber());
        	holder.name.setTypeface(null, Typeface.BOLD);
        }
        else{
        	holder.name.setTypeface(null, Typeface.NORMAL);
        }
        
        if(c.hasDraft()){
        	holder.draft.setText("(Draft)");
        }
        else{
        	holder.draft.setText("");
        }
        return convertView;
	}

	/**
	 * remove data item from Adapter
	 * 
	 **/
	public void remove(int position){
		mConversations.remove(position);
	}
	
	/**
	 * remove all data items from Adapter
	 * 
	 **/
	public void clear(){
		mConversations.clear();
	}
	
	/**
	 * add data item to Adapter
	 * 
	 **/
	public void add(Conversation c){
		mConversations.add(c);
	}
	
	private static class ViewHolder{
		TextView name;
		TextView count;
		TextView draft;
		TextView summary;
		TextView date;
		//RelativeLayout relativeLayout;
	}	
}
