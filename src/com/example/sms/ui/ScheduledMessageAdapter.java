package com.example.sms.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sms.Constant;
import com.example.sms.Message;
import com.example.sms.R;

public class ScheduledMessageAdapter extends BaseAdapter {	
	private List<Message> mMessages;
	private Context mContext;
	
	public ScheduledMessageAdapter(Context context, List<Message> messages) {
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
        	convertView = LayoutInflater.from(mContext).inflate(R.layout.scheduled_sms_row, parent, false);
        	holder.name = (TextView) convertView.findViewById(R.id.name);
        	holder.summary = (TextView) convertView.findViewById(R.id.summary);
        	holder.time = (TextView) convertView.findViewById(R.id.time);
        	holder.repeat = (TextView) convertView.findViewById(R.id.repeat);
        	convertView.setTag(holder);	
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        
        if(msg.getContent().length() > 30){
        	holder.summary.setText(msg.getContent().substring(0, 30));
        }
        else{
        	holder.summary.setText(msg.getContent());
        }
        holder.name.setText(msg.getReceiver().getNameOrNumber());
        setTimeAndRepeat(msg.getRepeat(), msg.getTimeStamp(), holder);
        
        holder.summary.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        holder.time.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
        holder.repeat.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_bright));
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
		TextView name;
		TextView summary;
		TextView time;
		TextView repeat;
	}
	
	public void setTimeAndRepeat(int repeat, long timeStamp, ViewHolder holder){
		String time="";
		String repeatStr="";
		SimpleDateFormat sdf = null;
		switch(repeat){
		case Constant.REPEAT_NONE:		// No
			sdf = new SimpleDateFormat("MMM-dd-yyyy HH:mm");
			time = sdf.format(new Date(timeStamp));
			repeatStr = "Once";
			break;
			
		case Constant.REPEAT_DAILY:		// Daily
			sdf = new SimpleDateFormat("HH:mm");
			time = sdf.format(new Date(timeStamp));
			repeatStr = "Daily";
			break;
			
		case Constant.REPEAT_WEEKLY:		// Weekly
			sdf = new SimpleDateFormat("E HH:mm");
			time = sdf.format(new Date(timeStamp));
			repeatStr = "Weekly";
			break;
			
		case Constant.REPEAT_MONTHLY:		// Monthly
			sdf = new SimpleDateFormat("dd");
			time = sdf.format(new Date(timeStamp))+" th";
			
			sdf = new SimpleDateFormat(" HH:mm");
			time += sdf.format(new Date(timeStamp));
			repeatStr = "Monthly";
			break;
			
		case Constant.REPEAT_YEARLY:		// Yearly
			sdf = new SimpleDateFormat("MMM dd HH:mm");
			time = sdf.format(new Date(timeStamp));
			repeatStr = "Yearly";
			break;
		}
		
		holder.repeat.setText(repeatStr);
		holder.time.setText(time);
	}
}
