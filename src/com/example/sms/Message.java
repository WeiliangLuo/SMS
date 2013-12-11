package com.example.sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Message {
	private static final String TAG = "Message";
	
	/**  All messages, not used this project */
    public static final int MESSAGE_TYPE_ALL    = 0;
	/**  Message received by user */
    public static final int MESSAGE_TYPE_INBOX  = 1;
	/** Message sent by user */
    public static final int MESSAGE_TYPE_SENT   = 2;    
	/** Draft message (At most 1 per conversation) */
    public static final int MESSAGE_TYPE_DRAFT  = 3;    
    /** Including draft, failed, queued, sent messages, not used this project */
    public static final int MESSAGE_TYPE_OUTBOX = 4;
	/** Message failed to be sent, not used this project */
    public static final int MESSAGE_TYPE_FAILED = 5;
	/** Message being/to be sent from the user, not used this project */
    public static final int MESSAGE_TYPE_QUEUED = 6;
	/** Message being/to be sent from the user, not used this project */
    public static final int MESSAGE_TYPE_SCHEDULED = 7;

    /** Candidate value for message unread flag */
	public static final boolean SMS_UNREAD 	= true;
	/** Candidate value for message unread flag */
	public static final boolean SMS_READ 	= false;
		
	private long id;
	private long conversation_id;
	private String content;
	// Epoch time
	private long timeStamp;
	// MESSAGE_TYPE_SENT:		Message sent by user	
	// MESSAGE_TYPE_INBOX:		Message received by user
	// MESSAGE_TYPE_DRAFT:		Draft message (At most 1 per conversation)
	// MESSAGE_TYPE_QUEUED:		Message being/to be sent from the user
	// MESSAGE_TYPE_FAILED:		Message failed to be sent
	private int type;
	// Indicating if message has been read (For MESSAGE_TYPE_INBOX message only) 
	private boolean unread;
	
	// one of them will be null (the user)
	private Contact sender;
	private Contact receiver;
	
	// exclusively for scheduled sms
	private int repeat;
	
	/* Constructors */
	/**
	 *  Construct Message using (message id, conversation id, content, type, unread, timeStamp, sender, receiver)
	 *  
	 *  Either sender or receiver will be null, for outgoing or incoming message
	 **/
	public Message(long id, long cid, String content, int type, boolean unread, long timeStamp, Contact sender,
			Contact receiver) {
		super();
		this.id = id;
		this.conversation_id = cid;
		this.content = content;
		this.type = type;
		this.unread = unread;
		this.timeStamp = timeStamp;
		this.sender = (sender==null)?null:new Contact(sender);
		this.receiver = (receiver==null)?null:new Contact(receiver);
	}
	
	/**
	 *  Construct Message from another message object
	 *  
	 **/
	public Message(Message msg){
		this(msg.id, msg.conversation_id, msg.content, msg.type, msg.unread, msg.timeStamp, msg.sender, msg.receiver);
	}
	/* End Constructor */
	
	
	/* Getter and Setter */
	/**
	 *  Get id of Message
	 **/
	public long getId() {
		return id;
	}
	
	/**
	 *  Set id of Message
	 **/
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 *  Get conversation id of Message
	 **/
	public long getConversationId() {
		return conversation_id;
	}

	/**
	 *  Set conversation id of Message
	 **/
	public void setConversationId(long cid) {
		this.conversation_id = cid;
	}

	/**
	 *  Get content of Message
	 **/
	public String getContent() {
		return content;
	}

	/**
	 *  Set conversation id of Message
	 **/
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 *  Get type of Message
	 **/
	public int getType() {
		return type;
	}

	/**
	 *  Set type of Message
	 **/
	public void setType(int type) {
		this.type = type;
	}	
	
	/**
	 *  Get unread flag of Message
	 **/
	public boolean isUnread() {
		return unread;
	}

	/**
	 *  Set unread flag of Message
	 **/
	public void setUnread(boolean unread) {
		this.unread = unread;
	}
	
	/**
	 *  Get timeStamp of Message
	 **/
	public long getTimeStamp() {
		return timeStamp;
	}

	/**
	 *  Set timeStamp of Message
	 **/
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 *  Get sender of Message
	 *  
	 *  Notice: sender is null, when this is an outgoing message
	 **/
	public Contact getSender() {
		return sender;
	}

	/**
	 *  Set sender of Message
	 */
	public void setSender(Contact sender) {
		this.sender = (sender==null)?null:new Contact(sender);
	}

	/**
	 *  Get receiver of Message
	 *  
	 *  Notice: receiver is null, when this is an incoming message
	 **/
	public Contact getReceiver() {
		return receiver;
	}

	/**
	 *  Set receiver of Message
	 */
	public void setReceiver(Contact receiver) {
		this.receiver = (receiver==null)?null:new Contact(receiver);
	}
	
	/**
	 *	Set repeat of a scheduled sms 
	 **/
	public void setRepeat(int repeat){
		this.repeat = repeat;
	}
	
	/**
	 *	Get repeat of a scheduled sms 
	 **/
	public int getRepeat(){
		return this.repeat;
	}
	/* End Getter and Setter */
	
	/**
	 * 	Compare this message with another one
	 * 	using the message id
	 **/
	public boolean equals(Message msg){
		return msg.id == id;
	}
	
	/**
	 *  Return true if this message is sent from user
	 *  
	 **/
	public boolean fromMe(){
		return sender==null;
	}
	
	/**
	 *  Return the contact of the other party of Message
	 * 
	 * */
	public Contact getContact(){
		return (receiver==null)?sender:receiver;
	}
	
	/**
	 *  Update current message in the database
	 *  This is usually used by draft/received msg
	 * 
	 *  Update content, timeStamp, read(unread) status
	 * */
	public void update(Context context){
		Uri uriMsg = Uri.parse("content://sms/"+this.id);
		ContentValues values = new ContentValues();
		values.put(MessageManager.CONTENT, this.content);
		values.put(MessageManager.TIMESTAMP, this.timeStamp);
		values.put(MessageManager.READ, this.unread?0:1);
		
		if(this.type == Message.MESSAGE_TYPE_SCHEDULED){
			values.put(MessageManager.STATUS, this.repeat);
		}
		
		context.getContentResolver().update(uriMsg, values, null, null);
	}
	
	/**
	 *  Delete current message in the database
	 * 
	 * */
	public void delete(Context context){
		Uri uriMsg = Uri.parse("content://sms/"+this.id);
		context.getContentResolver().delete(uriMsg, null, null);
	}
	
	/**
	 *  Insert current message in the database
	 *  
	 * */
	public void insert(Context context){
		Uri uriMsg = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();
		if(this.type==Message.MESSAGE_TYPE_DRAFT
				||this.type==Message.MESSAGE_TYPE_FAILED
				||this.type==Message.MESSAGE_TYPE_QUEUED
				||this.type==Message.MESSAGE_TYPE_SENT
				||this.type==Message.MESSAGE_TYPE_SCHEDULED){
			values.put(MessageManager.ADDRESS, this.receiver.getPhoneNumber());
		}
		else{ //this.type==Message.MESSAGE_TYPE_INBOX
			values.put(MessageManager.ADDRESS, this.sender.getPhoneNumber());
		}

		values.put(MessageManager.CONTENT, this.content);
		values.put(MessageManager.TIMESTAMP, this.timeStamp);
		values.put(MessageManager.READ, this.unread?0:1);
		if(this.type==Message.MESSAGE_TYPE_SCHEDULED){
			values.put(MessageManager.STATUS, this.repeat);
		}
		values.put(MessageManager.TYPE, this.type);
		values.put(MessageManager.CONVERSATION_ID, this.conversation_id);
		uriMsg = context.getContentResolver().insert(uriMsg, values);
		
		// update the id
		Cursor cur = context.getContentResolver().query(
				uriMsg, new String[] {MessageManager.ID}, null, null, null);
		if(cur!=null){
			if(cur.moveToFirst()){
				id = cur.getLong(0);
			}
			cur.close();
		}
	}
	
	/**
	 *  Update draft to sent message in the database
	 *  
	 * */
	public void updateSentDraft(Context context){
		Uri uriMsg = Uri.parse("content://sms/"+this.id);
		
		ContentValues values = new ContentValues();
		values.put(MessageManager.TYPE, Message.MESSAGE_TYPE_SENT);
		values.put(MessageManager.CONTENT, this.content);
		values.put(MessageManager.TIMESTAMP, this.timeStamp);
		
		context.getContentResolver().update(uriMsg, values, null, null);
	}
}
