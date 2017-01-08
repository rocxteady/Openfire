package org.jivesoftware.openfire.plugin.chatsquare.models;

public class UnreadMessageCountRequest {
	
	private int count;

	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "UnreadMessageCountRequest [count=" + count + "]";
	}

}
