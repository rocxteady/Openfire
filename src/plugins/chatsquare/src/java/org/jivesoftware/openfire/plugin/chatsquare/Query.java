package org.jivesoftware.openfire.plugin.chatsquare;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.jivesoftware.openfire.plugin.chatsquare.models.ParseResponse;
import org.jivesoftware.openfire.plugin.chatsquare.models.ReadMessage;
import org.jivesoftware.openfire.plugin.chatsquare.parse.ParseClient;

public class Query {

	public static int addUnreadMessage(String messageId, String username) {
		int unreadMessageCount = 0;
		try {
			String query = "INSERT INTO ofUnreadMessage (id, username) VALUES ";
			query = query + "(\"" + messageId + "\", \"" + username + "\")";
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
			if (statement != null) {
				statement.close();
			}
			query = "SELECT COUNT(id) FROM ofUnreadMessage";
			PreparedStatement pst = Database.getConnection().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				unreadMessageCount = rs.getInt(1);
			} else {
				if (pst != null) {
					pst.close();
				}
				return 0;
			}
			if (pst != null) {
				pst.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return unreadMessageCount;
	}

	public static boolean readMessages(ArrayList<ReadMessage> messages, String userToken) {
		if (messages.size() == 0) {
			return true;
		}
		try {
			String query = "DELETE from ofUnreadMessage WHERE ";
			for (int i = 0; i < messages.size(); i++) {
				String messageId = messages.get(i).getId();
				if (i > 0) {
					query = query + "OR ";
				}
				query = query + "id = \"" + messageId + "\"";
			}
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updateMessageCount(int count, String userToken) {
		try {
			String query = "UPDATE ofUser SET";
			query = query + " unreadMessageCount=\"" + count + "\"";
			query = query + " WHERE token=\"" + userToken + "\"";
			Statement statement = Database.getConnection().createStatement();
			statement.executeUpdate(query);
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getDeviceToken(String username) {
		String deviceToken = null;

		ParseResponse<String> parseResponse = new ParseClient().getDeviceToken(username);
		if (parseResponse != null) {
			deviceToken = parseResponse.getResult();
		}
		return deviceToken;
	}
}
