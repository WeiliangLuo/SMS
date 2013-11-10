package com.example.sms;

public class Message {
	public static final int SMS_TYPE_SENT		= 0;
	public static final int SMS_TYPE_RECEIVED 	= 1;	
	public static final int SMS_TYPE_DRAFT 		= 2;
	public static final int SMS_TYPE_PENDING 	= 3;
	public static final int SMS_TYPE_FAILED 	= 4;

	
	public static final boolean SMS_UNREAD 	= true;
	public static final boolean SMS_READ 	= false;

	private int id;
	private String content;
	// Epoch time
	private long timeStamp;
	// SENT:		Message sent by user	
	// RECEIVED:	Message received by user
	// DRAFT:		Draft message (At most 1 per conversation)
	// PENDING:		Message being/to be sent from the user
	// FAILED:		Message failed to be sent
	private int type;
	// Indicating if message has been read (For RECEIVED message only) 
	private boolean unread;
	
	// one of them will be null (the user)
	private Contact sender;
	private Contact receiver;
	
	/* Constructors */
	public Message(int id, String content, int type, boolean unread, long timeStamp, Contact sender,
			Contact receiver) {
		super();
		this.id = id;
		this.content = content;
		this.type = type;
		this.unread = unread;
		this.timeStamp = timeStamp;
		this.sender = (sender==null)?null:new Contact(sender);
		this.receiver = (receiver==null)?null:new Contact(receiver);
	}

	public Message(Message msg){
		this(msg.id, msg.content, msg.type, msg.unread, msg.timeStamp, msg.sender, msg.receiver);
	}
	/* End Constructor */
	
	
	/* Getter and Setter */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}	
	
	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Contact getSender() {
		return sender;
	}

	public void setSender(Contact sender) {
		this.sender = (sender==null)?null:new Contact(sender);
	}

	public Contact getReceiver() {
		return receiver;
	}

	public void setReceiver(Contact receiver) {
		this.receiver = (receiver==null)?null:new Contact(receiver);
	}
	/* End Getter and Setter */

	/**
	 *	Search keyword through the message content
	 *	
	 * 	@return true if found, false if not found
	 **/
	public boolean searchMessage(String keyword){
		if(content.toLowerCase().contains(keyword.toLowerCase()))
			return true;
		return false;
	}
	
	/**
	 * 	Compare this message with another one
	 * 	
	 * 
	 **/
	public boolean equals(Message msg){
		return msg.id == id;
	}
	
	public boolean fromMe(){
		return sender==null;
	}
}
