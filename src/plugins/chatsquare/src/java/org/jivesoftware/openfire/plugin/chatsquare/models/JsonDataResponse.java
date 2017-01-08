package org.jivesoftware.openfire.plugin.chatsquare.models;

public class JsonDataResponse<T> extends JsonResponse {
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
