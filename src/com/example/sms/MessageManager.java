package com.example.sms;

import java.util.List;

public class MessageManager {
	/**
	 * Function to be called when new message is received
	 * 
	 **/
	public void receiveMessage(){
		
	}
	
	/**
	 * Send message out
	 * 	
	 **/
	public void sendMessage(Message msg){
		
	}
	
	/**
	 * Send a list of messages out 
	 * 
	 **/
	public void sendMessages(List<Message> msgs){
		for(Message msg:msgs){
			sendMessage(msg);
		}
	}
	
	/**
	 * List all messages in database 
	 * //TODO who will use this?
	 **/
	public List<Message> listMessages(){
		return null;
	}
	
	/**
	 * Get message by id
	 * //TODO who will use this?
	 * */
	public Message getMessageById(int id){
		return null;
	}
	
	/**
	 * List messages belong to the given conversation 
	 * 
	 * @param condId Conversation Id
	 * @return List of messages belong to conversation
	 **/
	public List<Message> getMessagesInCoversation(int conId){
		return null;
	}
	
	/**
	 * List all conversations in database
	 * 
	 * */
	public List<Conversation> listConversations(){
		return null;
	}
	
	/**
	 * Get conversation by id
	 * 
	 * */
	public Conversation getConversation(int id){
		return null;
	}
}
