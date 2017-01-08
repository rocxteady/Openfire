package org.jivesoftware.openfire.plugin.chatsquare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jivesoftware.util.Blowfish;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.StringUtils;

public class Database {

	static Blowfish cipher;
	private static Connection connection = null;
	
	public static Connection getConnection() {
		return connection;
	}

	public static boolean connect() {
		String url = "jdbc:mysql://localhost:3306/chatsquare";
		String user = "rocxteady";
		String password = "201015ulas";

		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean disconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public static Blowfish getCipher() {
		if (cipher != null) {
			return cipher;
		}
		// Get the password key, stored as a database property. Obviously,
		// protecting your database is critical for making the
		// encryption fully secure.
		String keyString;
		try {
			keyString = JiveGlobals.getProperty("passwordKey");
			if (keyString == null) {
				keyString = StringUtils.randomString(15);
				JiveGlobals.setProperty("passwordKey", keyString);
				// Check to make sure that setting the property worked. It won't
				// work,
				// for example, when in setup mode.
				if (!keyString.equals(JiveGlobals.getProperty("passwordKey"))) {
					return null;
				}
			}
			cipher = new Blowfish(keyString);
		} catch (Exception e) {
			// Log.error(e.getMessage(), e);
		}
		return cipher;
	}

}
