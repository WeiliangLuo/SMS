package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.sms.Message;
import com.example.sms.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MessageAdapter extends BaseAdapter {
	private List<Message> mMessages;
	private Context mContext;
	
	public MessageAdapter(Context context, List<Message> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mMessages.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message msg = (Message) getItem(position);
        ViewHolder holder; 
        if(convertView == null){
        	holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_row, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.message.setText(msg.getContent());
        //Date date = new Date(msg.getTimeStamp());
        //SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
        //holder.date.setText(dateFormat.format(date));
        LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
        /*//check if it is a status message then remove background, and change text color.
        if(msg.isStatusMessage())
        {
                holder.message.setBackgroundDrawable(null);
                lp.gravity = Gravity.LEFT;
                holder.message.setTextColor(R.color.textFieldColor);
        }
        else
        {                
      
        }*/
        //Check whether message is mine to show green background and align to right
        if(msg.fromMe()){
        	holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
            lp.gravity = Gravity.RIGHT;
        }
        //If not mine then it is from sender to show orange background and align to left
        else{
            holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
            lp.gravity = Gravity.LEFT;
        }
        holder.message.setLayoutParams(lp);
        return convertView;
	}

	/**
	 * remove data item from messageAdapter
	 * 
	 **/
	public void remove(int position){
		mMessages.remove(position);
	}
	
	/**
	 * add data item to messageAdapter
	 * 
	 **/
	public void add(Message msg){
		mMessages.add(msg);
	}
	
	private static class ViewHolder{
		TextView message;
	}	
}
