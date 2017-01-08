package org.jivesoftware.openfire.plugin.chatsquare;

public class Status {

	public static final int OK = 200;
	public static final int UserNotFound = 101;
	public static final int WrongLogin = 102;
	public static final int UserAlreadyExist = 103;
	public static final int NotAuthroized = 401;
	public static final int Unknown = -998;
	
	public static final String OkDescription = "OK";
	public static final String UserNotFoundDescription = "User not found.";
	public static final String WrongLoginDescription = "Username or password is incorrect.";
	public static final String UserAlreadyExistsDescription = "User already registered.";
	public static final String BadTokenDescription = "Not authorized.";
	public static final String UnknownDescription = "Unknown error.";
	
}
