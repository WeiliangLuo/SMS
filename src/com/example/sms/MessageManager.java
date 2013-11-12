package com.example.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class MessageManager {
	private static final String TAG = "MessageManager";
	
	/*Column names unique in conversation*/
	public static final String CONVERSATION_ID = "thread_id";
	public static final String MESSAGE_COUNT = "msg_count";
	public static final String SNIPPET = "snippet";
	/*Column names in sms*/
	public static final String ID = "_id";
	public static final String TIMESTAMP = "date";
	public static final String ADDRESS = "address";
	public static final String CONTENT = "body";
	public static final String READ = "read";
	public static final String TYPE = "type";
	
	
	/**
	 * Function to be called when new message is received
	 * 
	 **/
	public static void receiveMessage(Context context, String address, String body, long timeStamp){
		Contact sender = ContactManager.getContactByNumber(context, address);
		Message msg = new Message(-1,
								-1,
								body, 
								Message.MESSAGE_TYPE_INBOX, 
								Message.SMS_UNREAD, 
								timeStamp, 
								sender,
								null);
		// TODO save 
		msg.insert(context);
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
	 * List messages (sent/received) belong to the given conversation 
	 * 
	 * @param condId Conversation Id
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
				new String[] {"1" /* MESSAGE_TYPE_INBOX */, "2" /* MESSAGE_TYPE_SENT */}, 
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
	 * @param condId Conversation Id
	 * @return draft belong to conversation
	 **/
	public static Message getDraftInCoversation(Context context, long cid){
		Uri uriMsg = Uri.parse("content://sms/conversations/"+cid);
		Cursor curMsg = context.getContentResolver().query(
				uriMsg,
				new String[] {MessageManager.ID, MessageManager.ADDRESS, MessageManager.CONTENT, MessageManager.TIMESTAMP},
				MessageManager.TYPE+"=?",
				new String[] {"3" /* MESSAGE_TYPE_DRAFT */},
				null);
		if(curMsg!=null){
			// There is only one (or no) draft per conversation
			if(curMsg.moveToFirst()){
				long id = curMsg.getLong(0);
				String address = curMsg.getString(1);
				String body = curMsg.getString(2);
				long timeStamp = curMsg.getLong(3);
				Contact rec = ContactManager.getContactByNumber(context, address);
				Message draft = new Message(id, cid, body, Message.MESSAGE_TYPE_DRAFT, Message.SMS_READ, timeStamp, null, rec);
				return draft;
			}
			curMsg.close();
		}
		return null;
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
							new String[] {"3" /* MESSAGE_TYPE_DRAFT */},
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
	public static List<Message> searchMessages(String query){
		List<Message> messages = new ArrayList<Message>();
		Contact rec = new Contact(1, null, "2106302912");
		for(int i=0; i<6; i++){
			if(i%2==0){
				Message msg = new Message(i,
						-1,
						"Message from me, this is a very nice day. We are going to picnic tommorrow." +
						"I am so happy. How are u doing?", 
						Message.MESSAGE_TYPE_SENT, 
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
						Message.MESSAGE_TYPE_INBOX, 
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
		
		// Allocate new conversation_id
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
		}
		return cid;
	}
}
