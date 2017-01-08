package org.jivesoftware.openfire.plugin.chatsquare.parse;

import java.lang.reflect.Type;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jivesoftware.openfire.plugin.chatsquare.Constants;
import org.jivesoftware.openfire.plugin.chatsquare.models.ParseResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ParseClient {

	HttpClient httpClient;
	PostMethod postMethod;
	Gson gson;
	
	public ParseClient() {
		httpClient = new HttpClient();
		gson = new Gson();
	}

	private void addHeaders() {
		postMethod.addRequestHeader("X-Parse-Application-Id", "me.chatsquare");
		postMethod.addRequestHeader("X-Parse-REST-API-Key", "o9ybU872O5UZ5070t32A2953r7DFKgcg");
	}

	public ParseResponse<String> getDeviceToken(String username) {
		postMethod = new PostMethod(Constants.PARSE_URL + "/functions" + Constants.PARSE_FUNCTION_GET_DEVICE_TOKEN
				+ "?username=" + username);
		addHeaders();
		String result = request();
		Type dataType = new TypeToken<ParseResponse<String>>() {
		}.getType();
		ParseResponse<String> parseResponse = gson.fromJson(result, dataType);
		return parseResponse;
	}

	private String request() {
		String resultString = null;
		try {
			postMethod.getResponseBodyAsString();
			int returnCode = httpClient.executeMethod(postMethod);

			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err.println("The Post method is not implemented by this URI");
				// still consume the response body
			} else {
				final byte[] responseBody = postMethod.getResponseBody();
				resultString = new String(responseBody);
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			postMethod.releaseConnection();
		}
		return resultString;
	}

}
