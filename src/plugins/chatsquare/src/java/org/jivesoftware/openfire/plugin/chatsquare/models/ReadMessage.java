package org.jivesoftware.openfire.plugin.chatsquare.models;

public class ReadMessage {
	
	String id;
	
	String username;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "ReadMessagesRequest [id=" + id + ", username=" + username + "]";
	}
	
}
