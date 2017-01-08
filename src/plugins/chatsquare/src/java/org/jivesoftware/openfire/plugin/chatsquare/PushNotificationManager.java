package org.jivesoftware.openfire.plugin.chatsquare;

import java.util.Collection;

import org.jivesoftware.openfire.OfflineMessageListener;
import org.jivesoftware.openfire.OfflineMessageStrategy;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.Message;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

public class PushNotificationManager implements OfflineMessageListener {
	
	private static PushNotificationManager manager;
	
	public static PushNotificationManager sharedManager() {
		if (manager == null) {
			manager = new PushNotificationManager();
		}
		return manager;
	}
	
	public PushNotificationManager() {
		OfflineMessageStrategy.addListener(this);
	}
	
	private void sendPush(String message, int badge, String token, int pushType) {
		String certFilePath = FileManager.getApplePushCertificateFilePath();
		ApnsService service = APNS.newService().withCert(certFilePath, "201015ulas").withSandboxDestination().build();
		String payload = null;
		try {
			payload = APNS.newPayload().alertBody(message)
			        .badge(badge)
			        .customField("pushType", pushType)
			        .build();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	service.push(token, payload);
	}

	@Override
	public void messageBounced(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageStored(Message message) {
		// TODO Auto-generated method stub
		if (message.getBody() != null && message.getBody().length() > 0) {
			boolean isUserOnline = false;
			Collection<ClientSession> sessions = SessionManager.getInstance().getSessions();
			for (ClientSession session : sessions) {
				try {
					if (session.getUsername().equals(message.getTo().getNode())) {
						isUserOnline = true;
						break;
					}
				} catch (UserNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Database.connect();
			String deviceToken = Query.getDeviceToken(message.getTo().getNode());
			int unreadMessageCount = Query.addUnreadMessage(message.getID(), message.getTo().getNode());
			Database.disconnect();
			if (!isUserOnline) {
				String pushMessage = message.getFrom().getNode() + ": " + message.getBody();
				sendPush(pushMessage, unreadMessageCount, deviceToken, PushType.MESSAGE);
			}
		}
	}
	
}
