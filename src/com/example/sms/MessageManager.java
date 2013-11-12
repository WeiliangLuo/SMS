package com.example.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MessageManager {
	private static final String TAG = "MessageManager";
	
	public static final String CONVERSATION_ID = "thread_id";
	public static final String MESSAGE_COUNT = "msg_count";
	public static final String SNIPPET = "snippet";
	public static final String TIMESTAMP = "date";
	public static final String ADDRESS = "address";
	
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
	public static List<Conversation> getConversations(Context context){
		List<Conversation> res = new ArrayList<Conversation>();
		Uri uriCon = Uri.parse("content://sms/conversations");
		Cursor curCon = context.getContentResolver().query(
				uriCon, 
				null,
				null, 
				null, 
				null);
		if(curCon!=null){
			if(curCon.moveToFirst()){
				do{
					// +conversation_id
					// +message_count
					// +summary
					long cid = curCon.getLong(curCon.getColumnIndex(MessageManager.CONVERSATION_ID));
					int msgCount = curCon.getInt(curCon.getColumnIndex(MessageManager.MESSAGE_COUNT));
					String summary = curCon.getString(curCon.getColumnIndex(MessageManager.SNIPPET));
					// +address
					// +timeStamp
					String address = "";
					long timeStamp = 0;
					Uri uriMsg = Uri.parse("content://sms/conversations/"+cid);
					Cursor curMsg = context.getContentResolver().query(
							uriMsg, 
							new String[] {MessageManager.ADDRESS, MessageManager.TIMESTAMP},
							null, 
							null, 
							MessageManager.TIMESTAMP+" DESC");
					if(curMsg!=null){
						if(curMsg.moveToFirst()){
							address = curMsg.getString(0);
							timeStamp = curMsg.getLong(1);
						}
						curMsg.close();
					}
					// +hasUnread
					boolean hasUnread = false;
					curMsg = context.getContentResolver().query(
							uriMsg, 
							new String[] { MessageManager.ADDRESS },
							"read=?", 
							new String[] {"0"}, 
							null);
					if(curMsg!=null){
						if(curMsg.moveToFirst()){
							hasUnread = true;
						}
						curMsg.close();
					}
					// create conversation instance
					Contact contact = ContactManager.getContactByNumber(context, address);
					Conversation c = new Conversation(cid, summary, contact, timeStamp);
					c.setMsgCount(msgCount);
					c.setHasUnread(hasUnread);
					res.add(c);
				}
				while(curCon.moveToNext());
			}
			curCon.close();
		}
		return res;
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
