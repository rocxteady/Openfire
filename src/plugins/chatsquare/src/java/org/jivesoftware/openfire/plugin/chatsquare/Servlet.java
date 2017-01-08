/**
 * Openfire online users plugin
 * Copyright (C) 2011 Amiado Group AG
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jivesoftware.openfire.plugin.chatsquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.plugin.chatsquare.models.JsonDataResponse;
import org.jivesoftware.openfire.plugin.chatsquare.models.JsonResponse;
import org.jivesoftware.openfire.plugin.chatsquare.models.ReadMessageRequest;
import org.jivesoftware.openfire.plugin.chatsquare.models.UnreadMessageCountRequest;
import org.jivesoftware.openfire.roster.Roster;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.roster.RosterItem.SubType;
import org.jivesoftware.openfire.roster.RosterManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.JID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet for serving the online users count and the list of online users
 * 
 * @author Michael Weibel <michael.weibel@amiadogroup.com>
 */
public class Servlet extends HttpServlet {

	//private UserManager userManager;

	/**
	 * 
	 */
	private static final long serialVersionUID = -329699890203170575L;

	/**
	 * Initialize servlet & add exclude to authcheck for
	 * http://yourserver:9090/plugins/onlineusers. If
	 * plugin.onlineUsers.list.disableAuth is set, also add an exclude to
	 * authcheck for http://yourserver:9090/plugins/onlineusers/list.
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		//XMPPServer server = XMPPServer.getInstance();
		//userManager = server.getUserManager();
		AuthCheckFilter.addExclude("chatsquare/*");
	}

	/**
	 * Get request on the online users plugin. Serve the number of users or the
	 * list of users.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if (req.getHeader("Authorization") == null || !req.getHeader("Authorization").equals(Constants.SECRET_KEY)) {
			resp.setStatus(Status.NotAuthroized);
			JsonResponse response = new JsonResponse();
			PrintWriter out = null;
			try {
				out = resp.getWriter();
				response.setStatus(Status.NotAuthroized);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Gson gson = new Gson();
			Type dataType = new TypeToken<JsonResponse>() {
			}.getType();
			String json = gson.toJson(response, dataType);
			out.println(json);
			out.flush();
			return;
		}
		if (req.getPathInfo().endsWith("/getOnlineUsers")) {
			Set<JID> users = getOnlineUsers();
			displayUserList(resp, users);
		} else if (req.getPathInfo().endsWith("/isUserOnline")) {
			isUserOnline(resp, req.getParameter("username"));
		} else if (req.getPathInfo().endsWith("/getUsersSubscriptionForUser")) {
			isUserSubscribedToUser(resp, req.getParameter("username"), req.getParameter("jid"),
					req.getParameter("otherUserJid"));
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		PrintWriter out = resp.getWriter();
		resp.setContentType("application/json");
		String userToken = req.getHeader("UserToken");
		if (req.getHeader("Authorization") == null || !req.getHeader("Authorization").equals(Constants.SECRET_KEY)) {
			resp.setStatus(Status.NotAuthroized);
			JsonResponse response = new JsonResponse();
			response.setStatus(Status.NotAuthroized);
			Type dataType = new TypeToken<JsonResponse>() {
			}.getType();
			String json = gson.toJson(response, dataType);
			out.println(json);
			out.flush();
			return;
		} else if (req.getPathInfo().indexOf("/updateUnreadMessageCount") != -1) {
			JsonDataResponse<Boolean> response = new JsonDataResponse<>();
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = req.getReader();
				while ((line = reader.readLine()) != null)
					jb.append(line);
				response.setStatus(Status.OK);
			} catch (Exception e) { /* report an error */
				e.printStackTrace();
			}
			String json = jb.toString();
			UnreadMessageCountRequest request = gson.fromJson(json, UnreadMessageCountRequest.class);
			Database.connect();
			boolean updated = Query.updateMessageCount(request.getCount(), userToken);
			response.setData(updated);
			response.setStatus((updated) ? Status.OK : Status.Unknown);
			Type dataType = new TypeToken<JsonDataResponse<Boolean>>() {
			}.getType();
			out.println(gson.toJson(response, dataType));
			Database.disconnect();
		} else if (req.getPathInfo().endsWith("/readMessages")) {
			StringBuffer jb = new StringBuffer();
			String line = null;
			try {
				BufferedReader reader = req.getReader();
				while ((line = reader.readLine()) != null)
					jb.append(line);
			} catch (Exception e) { /* report an error */
				e.printStackTrace();
			}
			String json = jb.toString();
			readMessages(resp, json, userToken);
		}
		out.flush();
	}

	/**
	 * Displays the users lists as a JSON array
	 * 
	 * @param response
	 * @param users
	 * @throws IOException
	 */
	private void displayUserList(HttpServletResponse resp, Set<JID> users) throws IOException {
		JsonDataResponse<Set<JID>> response = new JsonDataResponse<>();
		response.setStatus(Status.OK);
		response.setData(users);
		Type type = new TypeToken<JsonDataResponse<Set<JID>>>() {
		}.getType();
		Gson gson = new Gson();
		String usersJson = gson.toJson(response, type);
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		out.println(usersJson);
		out.flush();
	}

	private void isUserOnline(HttpServletResponse response, String username) {
		int sessionCount = SessionManager.getInstance().getActiveSessionCount(username);
		Gson gson = new Gson();
		JsonDataResponse<Boolean> jsonResponse = new JsonDataResponse<Boolean>();
		response.setContentType("application/json");

		jsonResponse.setData(true);
		if (sessionCount == 0) {
			jsonResponse.setData(false);
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
			jsonResponse.setStatus(Status.OK);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Type dataType = new TypeToken<JsonDataResponse<Boolean>>() {
		}.getType();
		out.println(gson.toJson(jsonResponse, dataType));
		out.flush();
	}

	/**
	 * Get online users list
	 * 
	 * @return online users list as a set
	 */
	private Set<JID> getOnlineUsers() {
		Collection<ClientSession> sessions = SessionManager.getInstance().getSessions();
		Database.connect();
		Set<JID> users = new HashSet<JID>(sessions.size());
		for (ClientSession session : sessions) {
			JID jid = session.getPresence().getFrom();
			users.add(jid);
		}
		Database.disconnect();
		return users;
	}

	private void isUserSubscribedToUser(HttpServletResponse response, String username, String jid,
			String otherUserJid) {
		Gson gson = new Gson();
		JsonDataResponse<Boolean> jsonResponse = new JsonDataResponse<Boolean>();
		jsonResponse.setData(false);
		response.setContentType("application/json");
		PrintWriter out = null;

		RosterManager manager = new RosterManager();
		try {
			out = response.getWriter();

			Roster roster = manager.getRoster(username);
			RosterItem rosterItem = roster.getRosterItem(new JID(otherUserJid));
			SubType subType = rosterItem.getSubStatus();
			if (subType == SubType.TO || subType == SubType.BOTH) {
				jsonResponse.setData(true);
			}
			jsonResponse.setStatus(Status.OK);
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			jsonResponse.setStatus(Status.OK);
			e.printStackTrace();
		} catch (IOException e) {
			jsonResponse.setStatus(Status.Unknown);
			e.printStackTrace();
		}
		Type dataType = new TypeToken<JsonDataResponse<Boolean>>() {
		}.getType();
		out.println(gson.toJson(jsonResponse, dataType));
		out.flush();
	}

	private void readMessages(HttpServletResponse response, String json, String userToken) {
		Gson gson = new Gson();
		ReadMessageRequest readMessageRequest = gson.fromJson(json, ReadMessageRequest.class);
		JsonDataResponse<Boolean> jsonResponse = new JsonDataResponse<Boolean>();
		jsonResponse.setData(false);
		response.setContentType("application/json");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Database.connect();
			boolean result = Query.readMessages(readMessageRequest.getMessages(), userToken);
			Database.disconnect();
			if (result) {
				jsonResponse.setData(true);
				jsonResponse.setStatus(Status.OK);
			} else {
				jsonResponse.setStatus(Status.Unknown);
			}
		} catch (IOException e) {
			jsonResponse.setStatus(Status.Unknown);
			e.printStackTrace();
		}
		Type dataType = new TypeToken<JsonDataResponse<Boolean>>() {
		}.getType();
		out.println(gson.toJson(jsonResponse, dataType));
		out.flush();
	}

	/**
	 * Destroy - remove the exclude filters
	 */
	@Override
	public void destroy() {
		super.destroy();

		// Release the excluded URL
		AuthCheckFilter.removeExclude("database/*");
		//userManager = null;
	}
}
