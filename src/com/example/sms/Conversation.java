package com.example.sms;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

public class Conversation {
	private static final String TAG = "Conversation";

	private long id;
	private String summary;
	private Contact contact;
	private List<Message> messages;
	private int msgCount;
	private long timeStamp;
	private boolean hasUnread;
	private boolean hasDraft;
	
	/* Constructors */
	/**
	 *  Construct Conversation using (id, summary, contact, timeStamp)
	 *  
	 *  Contact is the other party involved in this conversation
	 *  TimeStamp is timeStamp the most recent sent/received/composed message,
	 *  which is miliseconds from epoch time
	 **/
	public Conversation(long id, String summary, Contact contact, long timeStamp) {
		super();
		this.id = id;
		this.summary = summary;
		this.contact = new Contact(contact);
		this.messages = new ArrayList<Message>();
		this.msgCount = 0;
		this.timeStamp = timeStamp;
		this.hasUnread = false;
		this.hasDraft = false;
	}
	
	/**
	 *  Construct Conversation using another conversation object
	 *  
	 **/
	public Conversation(Conversation con){
		this(con.id, con.summary, con.contact, con.timeStamp);
		for(Message msg:messages){
			this.messages.add(msg);
			this.msgCount++;
		}
	}
	/* End Constructor */
	
	
	/* Getter and Setter */
	/**
	 *  Get id of Conversation
	 **/
	public long getId() {
		return id;
	}

	/**
	 *  Set id of Conversation
	 **/
	public void setId(long id) {
		this.id = id;
	}

	/**
	 *  Get summary of Conversation
	 **/
	public String getSummary() {
		return summary;
	}

	/**
	 *  Set summary of Conversation
	 **/
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 *  Get the other party of Conversation
	 **/
	public Contact getContact() {
		return contact;
	}

	/**
	 *  Set the other party of Conversation
	 **/
	public void setContact(Contact contact) {
		this.contact = new Contact(contact);
	}
	
	/**
	 *  Get the message count of Conversation
	 **/
	public int getMsgCount(){
		return this.msgCount;
	}
	
	/**
	 *  Set the message count of Conversation
	 **/
	public void setMsgCount(int msgCount){
		this.msgCount = msgCount;
	}
	
	/**
	 *  Get the TimeStamp of Conversation
	 **/
	public long getTimeStamp(){
		return this.timeStamp;
	}
	
	/**
	 *  Set the TimeStamp of Conversation
	 **/
	public void setTimeStamp(long timeStamp){
		this.timeStamp = timeStamp;
	}
	
	/**
	 *  Get the unread flag of Conversation
	 **/
	public boolean hasUnread(){
		return this.hasUnread;
	}
	
	/**
	 *  Set the unread flag of Conversation
	 **/
	public void setHasUnread(boolean hasUnread){
		this.hasUnread = hasUnread;
	}
	
	/**
	 *  Get the hasDraft flag of Conversation
	 **/
	public boolean hasDraft(){
		return this.hasDraft;
	}
	
	/**
	 *  Set the hasDraft flag of Conversation
	 **/
	public void setHasDraft(boolean hasDraft){
		this.hasDraft = hasDraft;
	}
	/* End Getter and Setter */

	
	/**
	 *	Add message into conversation
	 *
	 **/
	public void addMessage(Message msg){
		messages.add(msg);
		this.msgCount++;
	}
	
	/**
	 *  Delete message from conversation
	 *  
	 */
	public void deleteMessage(Message msg){
		if(messages.contains(msg)){
			messages.remove(msg);
			this.msgCount--;
		}
	}

	/**
	 *  Delete all messages belong to this conversation in the database
	 * 
	 *  There is no conversation entry stored in the database,
	 *  the concept of conversation is just a logic concept based on a group
	 *  of messages sharing the same thread_id
	 * */
	public void delete(Context context){
		Uri uri = Uri.parse("content://sms/conversations/"+this.id);
		context.getContentResolver().delete(uri, null, null);
	}
}
