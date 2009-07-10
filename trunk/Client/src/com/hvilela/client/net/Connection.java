package com.hvilela.client.net;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import com.hvilela.common.Message;
import com.hvilela.common.messages.ChatMessage;
import com.hvilela.common.messages.MovementMessage;
import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.SessionId;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;

/**
 * @author Henrique
 */
public class Connection implements SimpleClientListener, ClientChannelListener {

	private static final long serialVersionUID = 1L;

	private SimpleClient simpleClient;

	protected final Map<String, ClientChannel> channelsByName = new HashMap<String, ClientChannel>();

	private Set<ConnectionListener> listeners;

	private String host;

	private String port;

	private String username = "";

	private String password = "";

	private UUID id;

	public Connection(String host, String port) {
		this.host = host;

		this.port = port;

		simpleClient = new SimpleClient(this);

		listeners = new CopyOnWriteArraySet<ConnectionListener>();
	}

	public UUID getId() {
		return id;
	}
	
	public void addConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	public void removeConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	public void login(String username, String password) {
		this.username = username;
		this.password = password;

		try {
			Properties connectProps = new Properties();
			connectProps.put("host", host);
			connectProps.put("port", port);

			simpleClient.login(connectProps);
		} catch (Exception e) {
			e.printStackTrace();
			disconnected(false, e.getMessage());
		}
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}

	public void loggedIn() {
		System.out.println("logeed in");
		
		fireLoggedIn();
	}

	public void loginFailed(String reason) {
		System.out.println("login failed");
		
		fireLoggedOut();
	}

	public void disconnected(boolean graceful, String reason) {
		System.out.println(graceful ? "disconnected" : ("kicked " + reason));
		
		fireLoggedOut();
	}

	public ClientChannelListener joinedChannel(ClientChannel channel) {
		System.out.println("Joined to channel " + channel.getName());

		channelsByName.put(channel.getName(), channel);

		return this;
	}

	@Override
	public void leftChannel(ClientChannel channel) {
		System.out.println("Left channel " + channel.getName());
	}

	public void sendMessage(Message message) throws IOException {
		simpleClient.send(message.toBytes().array());
	}

	public void sendChannelMessage(String channelName, Message message) throws IOException {
		ClientChannel channel = channelsByName.get(channelName);
		channel.send(message.toBytes().array());
	}

	@Override
	public void receivedMessage(byte[] messageData) {
		MovementMessage message = new MovementMessage();
		message.read(ByteBuffer.wrap(messageData));

		id = message.getSender();
		
		fireReady(message);
	}

	public void receivedMessage(ClientChannel channel, SessionId sessionId, byte[] messageData) {
		if (channel.getName().equals("position")) {
			MovementMessage message = new MovementMessage();
			message.read(ByteBuffer.wrap(messageData));
			fireReceivedMovementMessage(message);
		} else {
			fireReceivedChatMessage(channel == null ? "" : channel.getName(), new String(messageData));
		}
	}

	public void reconnected() {
		fireLoggedIn();
	}

	public void reconnecting() {
		System.out.println("reconnecting");
	}

	// fire
	
	private void fireLoggedIn() {
		for (ConnectionListener listener : listeners) {
			listener.loggedIn();
		}
	}

	private void fireReady(MovementMessage message) {
		for (ConnectionListener listener : listeners) {
			listener.ready(message);
		}
	}

	private void fireReceivedMovementMessage(MovementMessage message) {
		for (ConnectionListener listener : listeners) {
			listener.receivedMovementMessage(message);
		}
	}

	private void fireReceivedChatMessage(String channel, String message) {
		for (ConnectionListener listener : listeners) {
			listener.receivedChatMessage(new ChatMessage(null, message));
		}
	}

	private void fireLoggedOut() {
		for (ConnectionListener listener : listeners) {
			listener.loggedOut();
		}
	}
}