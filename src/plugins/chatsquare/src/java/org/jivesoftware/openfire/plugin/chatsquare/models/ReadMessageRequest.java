package org.jivesoftware.openfire.plugin.chatsquare.models;

import java.util.ArrayList;

public class ReadMessageRequest {

	private ArrayList<ReadMessage> messages;
	
	public ArrayList<ReadMessage> getMessages() {
		return messages;
	}
	
	public void setMessages(ArrayList<ReadMessage> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "ReadMessageRequest [messages=" + messages + "]";
	}
		
}
