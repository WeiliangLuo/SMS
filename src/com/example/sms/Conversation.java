package com.example.sms;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
	private long id;
	private String summary;
	private Contact contact;
	private List<Message> messages;
	private int msgCount;
	
	/* Constructors */
	public Conversation(long id, String summary, Contact contact) {
		super();
		this.id = id;
		this.summary = summary;
		this.contact = new Contact(contact);
		this.messages = new ArrayList<Message>();
		this.msgCount = 0;
	}
	
	public Conversation(Conversation con){
		this(con.id, con.summary, con.contact);
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

	public void setId(int id) {
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
	 *  Search through messages within the conversation
	 *  for given keyword
	 * 
	 * 	@return List of messages contain the keyword
	 */
	public List<Message> searchMessage(String keyword){
		List<Message> matchMsgs = new ArrayList<Message>();
		for(Message msg:messages ){
			if(msg.searchMessage(keyword)){
				matchMsgs.add(msg);	
			}
		}
		return matchMsgs;
	}
}
