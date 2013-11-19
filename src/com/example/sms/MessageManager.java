package com.example.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;

public class MessageManager {
	private static final String TAG = "MessageManager";
	
	/*Column names unique in conversation*/
	/** Column name in sms database */
	public static final String CONVERSATION_ID = "thread_id";
	/** Column name in sms database */
	public static final String MESSAGE_COUNT = "msg_count";
	/** Column name in sms database */
	public static final String SNIPPET = "snippet";
	/*Column names in sms*/
	/** Column name in sms database */
	public static final String ID = "_id";
	/** Column name in sms database */
	public static final String TIMESTAMP = "date";
	/** Column name in sms database */
	public static final String ADDRESS = "address";
	/** Column name in sms database */
	public static final String CONTENT = "body";
	/** Column name in sms database */
	public static final String READ = "read";
	/** Column name in sms database */
	public static final String TYPE = "type";
	
	
	/**
	 * Function to be called when new message is received
	 * 
	 **/
	public static void receiveMessage(Context context, String number, String body, long timeStamp){
		Contact sender = ContactManager.getContactByNumber(context, number);
		long cid = getOrCreateConversationId(context, number);
		Message msg = new Message(-1,
								cid,
								body, 
								Message.MESSAGE_TYPE_INBOX, 
								Message.SMS_UNREAD,
								timeStamp, 
								sender,
								null);
		// save the message into  
		msg.insert(context);
	}
	
	/**
	 * Send message out
	 * 	
	 **/
	public static void sendMessage(Message msg){
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(msg.getReceiver().getPhoneNumber(), null, msg.getContent(), null, null);
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
	 * List messages (sent/received) belong to the given conversation 
	 * 
	 * @param cid Conversation Id
	 * @return List of messages belong to conversation
	 **/
	
	public static List<Message> getMessagesInCoversation(Context context, long cid){
		List<Message> messages = new ArrayList<Message>();
		Uri uriMsg = Uri.parse("content://sms/conversations/"+cid);
		String[] cols = new String[] {MessageManager.ID, 
							MessageManager.ADDRESS, 
							MessageManager.CONTENT,
							MessageManager.TIMESTAMP,
							MessageManager.TYPE,
							MessageManager.READ};
		
		Cursor curMsg = context.getContentResolver().query(
				uriMsg, 
				cols,
				MessageManager.TYPE+"=? or "+ MessageManager.TYPE+"=?", 
				new String[] {String.valueOf(Message.MESSAGE_TYPE_INBOX), String.valueOf(Message.MESSAGE_TYPE_SENT)}, 
				MessageManager.TIMESTAMP+" ASC");
		
		if(curMsg!=null){
			if(curMsg.moveToFirst()){
				do{
					long id = curMsg.getLong(0);
					String address = curMsg.getString(1);
					String body = curMsg.getString(2);
					long timeStamp = curMsg.getLong(3);
					int type = curMsg.getInt(4);
					boolean unread = (curMsg.getInt(5)==0);
					Contact receiver = null;
					Contact sender = null;
					if(type==Message.MESSAGE_TYPE_SENT){
						receiver = ContactManager.getContactByNumber(context, address);
						sender = null;
					}
					else{
						receiver = null;
						sender = ContactManager.getContactByNumber(context, address);
					}
					Message msg = new Message(id, cid, body, 
							type, unread, timeStamp, sender, receiver);
					messages.add(msg);
				}
				while(curMsg.moveToNext());
			}
			curMsg.close();
		}
		return messages;
	}
	
	/**
	 * List draft belong to the given conversation 
	 * 
	 * @param cid Conversation Id
	 * @return draft belong to conversation
	 **/
	public static Message getDraftInCoversation(Context context, long cid){
		Message draft = null;
		Uri uriMsg = Uri.parse("content://sms/conversations/"+cid);
		Cursor curMsg = context.getContentResolver().query(
				uriMsg,
				new String[] {MessageManager.ID, MessageManager.ADDRESS, MessageManager.CONTENT, MessageManager.TIMESTAMP},
				MessageManager.TYPE+"=?",
				new String[] {String.valueOf(Message.MESSAGE_TYPE_DRAFT)},
				null);
		if(curMsg!=null){
			// There is only one (or no) draft per conversation
			if(curMsg.moveToFirst()){
				long id = curMsg.getLong(0);
				String address = curMsg.getString(1);
				String body = curMsg.getString(2);
				long timeStamp = curMsg.getLong(3);
				Contact rec = ContactManager.getContactByNumber(context, address);
				draft = new Message(id, cid, body, Message.MESSAGE_TYPE_DRAFT, Message.SMS_READ, timeStamp, null, rec);
			}
			curMsg.close();
		}
		return draft;
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
					// +hasDraft
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
							// use the time of latest message
							timeStamp = curMsg.getLong(1);
							do{
								// address of draft may be null
								address = curMsg.getString(0);
							}
							while(curMsg.moveToNext()&&address==null);
						}
						curMsg.close();
					}
					// +hasUnread
					boolean hasUnread = false;
					curMsg = context.getContentResolver().query(
							uriMsg, 
							new String[] { MessageManager.ADDRESS },
							MessageManager.READ+"=?", 
							new String[] {"0"}, 
							null);
					if(curMsg!=null){
						if(curMsg.moveToFirst()){
							hasUnread = true;
						}
						curMsg.close();
					}
					// +hasDraft
					boolean hasDraft = false;
					curMsg = context.getContentResolver().query(
							uriMsg, 
							new String[] { MessageManager.ADDRESS },
							MessageManager.TYPE+"=?", 
							new String[] {String.valueOf(Message.MESSAGE_TYPE_DRAFT)},
							null);
					if(curMsg!=null){
						if(curMsg.moveToFirst()){
							hasDraft = true;
						}
						curMsg.close();
					}
					// create conversation instance
					Contact contact = ContactManager.getContactByNumber(context, address);
					Conversation c = new Conversation(cid, summary, contact, timeStamp);
					c.setMsgCount(msgCount);
					c.setHasUnread(hasUnread);
					c.setHasDraft(hasDraft);
					res.add(c);
				}
				while(curCon.moveToNext());
			}
			curCon.close();
		}
		return res;
	}
	
	/**
	 * Search through message database
	 * 
	 * */
	public static List<Message> searchMessages(Context context, String query){
		List<Message> messages = new ArrayList<Message>();
		Uri uriMsg = Uri.parse("content://sms");
		
		String[] cols = new String[] {MessageManager.ID, 
				MessageManager.ADDRESS, 
				MessageManager.CONTENT,
				MessageManager.TIMESTAMP,
				MessageManager.TYPE,
				MessageManager.CONVERSATION_ID};
		
		// use LIKE query to search through all messages
		Cursor curMsg = context.getContentResolver().query(
				uriMsg, 
				cols,
				MessageManager.CONTENT+" LIKE '%"+query+"%'", 
				null, 
				MessageManager.TIMESTAMP+" DESC");
		if(curMsg!=null){
			if(curMsg.moveToFirst()){
				do{
					long id = curMsg.getLong(0);
					String address = curMsg.getString(1);
					String content = curMsg.getString(2);
					long timeStamp = curMsg.getLong(3);
					int type = curMsg.getInt(4);
					long cid = curMsg.getLong(5);
					Contact contact = ContactManager.getContactByNumber(context, address);
					// for searchActivity it really doesn't matter if its an outgoing or incoming message
					// the cid will lead user to messageListActivity that the message belongs to.
					Message msg = new Message(id, cid, content, type, Message.SMS_READ, timeStamp, contact, contact);
					messages.add(msg);
				}
				while(curMsg.moveToNext());
			}
			curMsg.close();
		}
		return messages;
	}

	/**
	 * Find conversation by phone number
	 * 
	 * @return conversation id, new id will be assigned if not exist
	 **/
	public static long getOrCreateConversationId(Context context, String number){
		long cid = -1;
		Uri uri = Uri.parse("content://sms");
		Cursor cur = context.getContentResolver().query(
				uri, 
				new String[] {MessageManager.CONVERSATION_ID},
				MessageManager.ADDRESS+"=?", 
				new String[] {number}, 
				null);
		if(cur!=null){
			if(cur.moveToFirst()){
				cid = cur.getLong(0);
			}
			cur.close();
		}
		
		// Allocate new conversation_id if no match
		if(cid==-1){
			cur = context.getContentResolver().query(
					uri, 
					new String[] {MessageManager.CONVERSATION_ID},
					null, 
					null, 
					MessageManager.CONVERSATION_ID+" DESC ");
			if(cur!=null){
				if(cur.moveToFirst()){
					// new conversation_id = Max(conversation_id)+1
					cid = cur.getLong(0)+1;
				}
				cur.close();
			}
			// no entry in the database, start the id from 10 in case smaller numbers are reserved
			if(cid==-1){
				cid=10;
			}
		}

		return cid;
	}
}
