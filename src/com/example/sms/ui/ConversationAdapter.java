package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
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
        	holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
        	
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
		Date date = new Date(c.getTimeStamp());
        holder.date.setText(dateFormat.format(date));

        
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
	 * remove data item from messageAdapter
	 * 
	 **/
	public void remove(int position){
		mConversations.remove(position);
	}
	
	/**
	 * remove all data items from messageAdapter
	 * 
	 **/
	public void clear(){
		mConversations.clear();
	}
	
	/**
	 * add data item to messageAdapter
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
		RelativeLayout relativeLayout;
	}	
}
