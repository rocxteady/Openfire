package org.jivesoftware.openfire.plugin.chatsquare.exceptions;

public class BadTokenException extends Exception {

	static final long serialVersionUID = 1L;

	public BadTokenException() {
		super();
	}

	public BadTokenException(String message) {
		super(message);
	}

	public BadTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadTokenException(Throwable cause) {
		super(cause);
	}
}
