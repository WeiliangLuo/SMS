package com.example.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MessageManager {
	private static final String TAG = "MessageManager";
	/**
	 * Function to be called when new message is received
	 * 
	 **/
	public static void receiveMessage(Context context, String address, String body, long timeStamp){
		Contact sender = ContactManager.getContactByNumber(context, address);
		Message msg = new Message(-1,
								-1,
								body, 
								Message.SMS_TYPE_RECEIVED, 
								Message.SMS_UNREAD, 
								timeStamp, 
								sender,
								null);
		// TODO save 
		// int mid = saveReceivedMessage(msg);
		// int cid = ?
		//msg.setId(mid);
	}
	
	/**
	 * Send message out
	 * 	
	 **/
	public static void sendMessage(Message msg){
		
	}
	
	/**
	 * Send a list of messages out 
	 * 
	 **/
	public static void sendMessages(List<Message> msgs){
		for(Message msg:msgs){
			sendMessage(msg);
		}
	}
	
	/**
	 * List all messages in database 
	 * //TODO who will use this?
	 **/
	public static List<Message> listMessages(){
		return null;
	}
	
	/**
	 * Get message by id
	 * //TODO who will use this?
	 * */
	public static Message getMessageById(int id){
		return null;
	}
	
	/**
	 * List messages belong to the given conversation 
	 * 
	 * @param condId Conversation Id
	 * @return List of messages belong to conversation
	 **/
	public static List<Message> getMessagesInCoversation(int cid){
		List<Message> messages = new ArrayList<Message>();
		Contact rec = new Contact(1, null, "2106302912");
		for(int i=0; i<6; i++){
			if(i%2==0){
				Message msg = new Message(i,
						-1,
						"Message from me, this is a very nice day. We are going to picnic tommorrow." +
						"I am so happy. How are u doing?", 
						Message.SMS_TYPE_SENT, 
						Message.SMS_READ, 
						12345678, 
						null,
						rec);
				messages.add(msg);
			}
			else{
				Message msg = new Message(i,
						-1,
						"Replied message from Bob", 
						Message.SMS_TYPE_RECEIVED, 
						Message.SMS_READ, 
						123456789, 
						rec,
						null);
				messages.add(msg);
			}
		}
		return messages;
	}
	
	/**
	 * List all conversations in database
	 * 
	 * */
	public static List<Conversation> listConversations(Context context){
		Uri uri = Uri.parse("content://sms/conversations");
		Cursor cur = context.getContentResolver().query(
				uri, 
				null, 
				null, 
				null, 
				null);
		if(cur!=null){
			if(cur.moveToFirst()){
				do{
					Log.i(TAG, cur.getColumnNames().toString());
				}
				while(cur.moveToNext());
			}
			cur.close();
		}
		return null;
	}
	
	/**
	 * Get conversation by id
	 * 
	 * */
	public static Conversation getConversation(int id){
		return null;
	}
	
	/**
	 * Search through message database
	 * 
	 * */
	public static List<Message> searchMessages(String query){
		List<Message> messages = new ArrayList<Message>();
		Contact rec = new Contact(1, null, "2106302912");
		for(int i=0; i<6; i++){
			if(i%2==0){
				Message msg = new Message(i,
						-1,
						"Message from me, this is a very nice day. We are going to picnic tommorrow." +
						"I am so happy. How are u doing?", 
						Message.SMS_TYPE_SENT, 
						Message.SMS_READ, 
						12345678, 
						null,
						rec);
				messages.add(msg);
			}
			else{
				Message msg = new Message(i,
						-1,
						"Replied message from Bob", 
						Message.SMS_TYPE_RECEIVED, 
						Message.SMS_READ, 
						123456789, 
						rec,
						null);
				messages.add(msg);
			}
		}
		return messages;	
	}
}
