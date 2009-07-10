package com.hvilela.server;

import java.io.Serializable;

import com.hvilela.common.messages.MovementMessage;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedObject;

/**
 * @author  Henrique
 */
class UserSession implements ManagedObject, ClientSessionListener, Serializable {

	private static final long serialVersionUID = 1L;

	private ClientSession session;

	public UserSession(ClientSession session) {
		this.session = session;
	}

	public void setSession(ClientSession session) {
		this.session = session;
	}

	public void receivedMessage(byte[] message) {
		ChannelManager channelManager = AppContext.getChannelManager();
		Channel chat = channelManager.getChannel("chat");

		chat.send(message);
	}

	public void disconnected(boolean graceful) {
		session.disconnect();
	}

	public void send(MovementMessage message) {
		session.send(message.toBytes().array());		
	}
}