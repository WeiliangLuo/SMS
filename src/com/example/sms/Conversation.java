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
	
	public Conversation(Conversation con){
		this(con.id, con.summary, con.contact, con.timeStamp);
		for(Message msg:messages){
			this.messages.add(msg);
			this.msgCount++;
		}
	}
	/* End Constructor */
	
	
	/* Getter and Setter */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = new Contact(contact);
	}
	
	public int getMsgCount(){
		return this.msgCount;
	}
	
	public void setMsgCount(int msgCount){
		this.msgCount = msgCount;
	}
	
	public long getTimeStamp(){
		return this.timeStamp;
	}
	
	public void setTimeStamp(long timeStamp){
		this.timeStamp = timeStamp;
	}
	
	public void setHasUnread(boolean hasUnread){
		this.hasUnread = hasUnread;
	}
	
	public boolean hasUnread(){
		return this.hasUnread;
	}

	public void setHasDraft(boolean hasDraft){
		this.hasDraft = hasDraft;
	}
	
	public boolean hasDraft(){
		return this.hasDraft;
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
	 *  delete all messages belong to this conversation in the database
	 * 
	 * */
	public void delete(Context context){
		Uri uri = Uri.parse("content://sms/conversations/"+this.id);
		context.getContentResolver().delete(uri, null, null);
	}
}
