package com.example.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.sms.ui.MainActivity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver{    
     private static final String TAG = "Alarm";

	@Override
     public void onReceive(Context context, Intent intent) 
     {   
         PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
         wl.acquire();
         
         handle(context);
         wl.release();
     }

	 static public void SetAlarm(Context context, long triggerTime){
	     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	     Intent in = new Intent(context, Alarm.class);
	     PendingIntent pi = PendingIntent.getBroadcast(context, 0, in, 0);
	     am.set(AlarmManager.RTC_WAKEUP, triggerTime, pi);
     }

     static public void CancelAlarm(Context context){
         Intent intent = new Intent(context, Alarm.class);
         PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         alarmManager.cancel(sender);
     }
     
     static public long nextTriggerTime(long timestamp, int repeat){
		long curTime = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(curTime);
		int curYear = cal.get(Calendar.YEAR);
		int curMonth = cal.get(Calendar.MONTH);
		int curDay = cal.get(Calendar.DAY_OF_MONTH);
		int curDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		
		cal.setTimeInMillis(timestamp);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = 0;
		
		switch(repeat){
		case Constant.REPEAT_NONE:		// No
			if(timestamp<curTime){
				return -1;
			}
			else{
				return timestamp;
			}
			
		case Constant.REPEAT_DAILY:		// Daily  (hour and minute)
			cal.set(curYear, curMonth, curDay, hour, minute, second);
			long triggerInToday = cal.getTimeInMillis();
			if(triggerInToday<curTime){	//passed
				long triggerInTomorrow = triggerInToday + 1000*60*60*24;
				return triggerInTomorrow;
			}
			else{
				return triggerInToday;
			}
			
		case Constant.REPEAT_WEEKLY:		// Weekly
			cal.set(curYear, curMonth, curDay, hour, minute, second);
			if(dayOfWeek==curDayOfWeek && cal.getTimeInMillis()>=curTime){
				return timestamp;
			}
			int delta = dayOfWeek>curDayOfWeek?(dayOfWeek-curDayOfWeek):(7+dayOfWeek-curDayOfWeek);
			cal.add(Calendar.DAY_OF_WEEK, delta);
			return cal.getTimeInMillis();
			
		case Constant.REPEAT_MONTHLY:		// Monthly (day, hour, minute)
			cal.set(curYear, curMonth, day, hour, minute, second);
			
			if(cal.get(Calendar.DAY_OF_MONTH)==day && cal.getTimeInMillis()>=curTime){
				return cal.getTimeInMillis();
			}
				
			// either the day in this month passed, or the day not exist in this month
			// try next month
			int i=1;
			do{
				if(curMonth+i>12){
					cal.set(curYear+1, (curMonth+i)%12, day, hour, minute, second);
				}
				else{
					cal.set(curYear, curMonth+i, day, hour, minute, second);
				}
				if(cal.get(Calendar.DAY_OF_MONTH)==day){
					return cal.getTimeInMillis();
				}
				i++;
			}
			while(true);
			
		case Constant.REPEAT_YEARLY:		// Yearly (month, day, hour, minute)
			cal.set(curYear, month, day, hour, minute, second);
			if(cal.get(Calendar.MONTH)==month && cal.get(Calendar.DAY_OF_MONTH)==day && cal.getTimeInMillis()>=curTime){
				return cal.getTimeInMillis();
			}
			
			i=1;
			do{
				cal.set(curYear+i, month, day, hour, minute, second);
				if(cal.get(Calendar.MONTH)==month && cal.get(Calendar.DAY_OF_MONTH)==day){
					return cal.getTimeInMillis();
				}
				i++;
			}
			while(true);
		}
		return 0;
     }
     
     private void handle(Context context){
    	 List<Message> messages = MessageManager.getScheduledMessages(context);
    	 
 		long curTime = System.currentTimeMillis();
 		Calendar cal = Calendar.getInstance();
 		cal.setTimeInMillis(curTime);
		int curYear = cal.get(Calendar.YEAR);
		int curMonth = cal.get(Calendar.MONTH);
		int curDay = cal.get(Calendar.DAY_OF_MONTH);
		int curDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int curHour = cal.get(Calendar.HOUR_OF_DAY);
		int curMinute = cal.get(Calendar.MINUTE);

		for(Message msg:messages){
			long timestamp = msg.getTimeStamp();
			int repeat = msg.getRepeat();
			
			cal.setTimeInMillis(timestamp);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minute = cal.get(Calendar.MINUTE);
			int second = 0;
			
			switch(repeat){
			case Constant.REPEAT_NONE:		// No
				if(curYear==year && curMonth==month && curDay==day && curHour==hour && curMinute==minute){
					handleNonRepeat(context, msg);
				}
				break;
				
			case Constant.REPEAT_DAILY:		// Daily  (hour and minute)
				if(curHour==hour && curMinute==minute){
					handleRepeat(context, msg);
				}
				break;
					
			case Constant.REPEAT_WEEKLY:		// Weekly
				if(curDayOfWeek==dayOfWeek && curHour==hour && curMinute==minute){
					handleRepeat(context, msg);
				}
				break;
				
			case Constant.REPEAT_MONTHLY:		// Monthly (day, hour, minute)
				if(curDay==day &&curHour==hour && curMinute==minute){
					handleRepeat(context, msg);
				}
				break;
				
			case Constant.REPEAT_YEARLY:		// Yearly (month, day, hour, minute)
				if(curMonth==month && curDay==day &&curHour==hour && curMinute==minute){
					handleRepeat(context, msg);
				}
				break;
			}
		}
 	}
     
     private void handleNonRepeat(Context context, Message msg){
    	 // send message
    	 MessageManager.sendMessage(msg);
    	 
    	 // notify user
    	 sendNotification(context, "Scheduled SMS", "Message sent to "+msg.getContact().getNameOrNumber());
    	 
    	 // delete scheduled msg and insert sent msg
    	 long cid = MessageManager.getOrCreateConversationId(context, msg.getReceiver().getPhoneNumber());
    	 Message sentMsg = new Message(msg);
    	 sentMsg.setConversationId(cid);
    	 sentMsg.setTimeStamp(System.currentTimeMillis());
    	 sentMsg.setType(Message.MESSAGE_TYPE_SENT);
    	 sentMsg.insert(context);
    	 msg.delete(context);
     }
     
     private void handleRepeat(Context context, Message msg){
    	 final long timestamp = msg.getTimeStamp();
    	 final int repeat = msg.getRepeat();
    	 final Context mContext = context;
    	 
    	 // send message
    	 MessageManager.sendMessage(msg);
    	 
    	 // notify user
    	 sendNotification(context, "Scheduled SMS", "Message sent to "+msg.getContact().getNameOrNumber());
    	 
    	 // insert sent msg
    	 String number = msg.getReceiver().getPhoneNumber();
    	 long cid = MessageManager.getOrCreateConversationId(context, number);
    	 Message sentMsg = new Message(msg);
    	 sentMsg.setConversationId(cid);
    	 sentMsg.setTimeStamp(System.currentTimeMillis());
    	 sentMsg.setType(Message.MESSAGE_TYPE_SENT);
    	 sentMsg.insert(context);
    	 
    	 // renew the alarm 1 min later
    	 Handler handler = new Handler();
    	 handler.postDelayed(new Runnable() { 
    		 public void run() { 
    			 long nextTriggerTime = nextTriggerTime(timestamp, repeat);
    			 Alarm.SetAlarm(mContext, nextTriggerTime);
    		 }	
    	 }, 1000*60); 

     }
     
     private void sendNotification(Context context, String title, String content){
 		NotificationCompat.Builder mBuilder =
 		        new NotificationCompat.Builder(context)
 		        .setSmallIcon(R.drawable.ic_notification)
 		        .setContentTitle(title)
 		        .setContentText(content);
 		
 		// Creates an explicit intent for an Activity in your app
 		Intent resultIntent = new Intent(context, MainActivity.class);
 		PendingIntent resultPendingIntent =
 			    PendingIntent.getActivity(
 			    context,
 			    0,
 			    resultIntent,
 			    PendingIntent.FLAG_UPDATE_CURRENT
 			);
 		mBuilder.setContentIntent(resultPendingIntent);
 		
 		// disappear after clicked
 		mBuilder.setAutoCancel(true);
 		NotificationManager mNotificationManager =
 		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
 		// mId allows you to update the notification later on.
 		int mId = Constant.SCHEDULED_SMS_NOTIFICATION_ID;
 		mNotificationManager.notify(mId, mBuilder.build());
 	}
 }
