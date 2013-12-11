package com.example.sms;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

import com.example.sms.ui.MainActivity;


public class MessageReceiver extends BroadcastReceiver {
	private static final String TAG = "MessageReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() == Constant.SMS_RECEIVED) {
            abortBroadcast();
            // Parse new message
			Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {
                    String body = messages[0].getMessageBody();
                    String address = extractNumber( messages[0].getOriginatingAddress() );
                    long timeStamp = System.currentTimeMillis(); // use the timeStamp that message was received
                    //messages[0].getTimestampMillis(); --> time the message was sent?
                    
                    // handle new message (save new message to database)
                    MessageManager.receiveMessage(context, address, body, timeStamp);
                    // set status bar notification
                    sendNotification(context, address, body);
                }
            }
        }
	}
	
	private void sendNotification(Context context, String address, String body){
		String name = ContactManager.getContactByNumber(context, address).getNameOrNumber();
        // notify user if this app is NOT running foreground
        // TODO delete notification if this app is launched by the user.
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_notification)
		        .setContentTitle(name)
		        .setContentText(body);
		
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
		int mId = Constant.NEW_SMS_NOTIFICATION_ID;
		mNotificationManager.notify(mId, mBuilder.build());
	}
	
	private String extractNumber(String address){
		String number = "";
	    // get numbers
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(address);
		while(matcher.find()){
			number += matcher.group();
		}

		return number;
	}
}
