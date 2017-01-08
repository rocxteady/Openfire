package org.jivesoftware.openfire.plugin.chatsquare.exceptions;

public class WrongUsernameOrPasswordException extends Exception {

	static final long serialVersionUID = 1L;

	public WrongUsernameOrPasswordException() {
		super();
	}

	public WrongUsernameOrPasswordException(String message) {
		super(message);
	}

	public WrongUsernameOrPasswordException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongUsernameOrPasswordException(Throwable cause) {
		super(cause);
	}
}
