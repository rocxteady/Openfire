package org.jivesoftware.openfire.plugin.chatsquare.models;

import org.jivesoftware.openfire.plugin.chatsquare.Status;

public class JsonResponse {

	private int status;
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		switch (status) {
		case Status.OK:
			this.message = Status.OkDescription;
			break;
		case Status.UserNotFound:
			this.message = Status.UserNotFoundDescription;
			break;
		case Status.UserAlreadyExist:
			this.message = Status.UserAlreadyExistsDescription;
			break;
		case Status.NotAuthroized:
			this.message = Status.BadTokenDescription;
			break;
		case Status.WrongLogin:
			this.message = Status.WrongLoginDescription;
			break;
		case Status.Unknown:
			this.message = Status.UnknownDescription;
			break;

		default:
			this.message = Status.UnknownDescription;
			break;
		}
	}

	public String getMessage() {
		return message;
	}

	public JsonResponse() {
		status = Status.Unknown;
		message = Status.UnknownDescription;
	}

	@Override
	public String toString() {
		return "JsonResponse [status=" + status + ", message=" + message + "]";
	}

}
