package com.hvilela.client.net;

import com.hvilela.common.messages.ChatMessage;
import com.hvilela.common.messages.MovementMessage;

public interface ConnectionListener {

	public void loggedIn();

	public void loggedOut();

	public void receivedMovementMessage(MovementMessage message);
	
	public void receivedChatMessage(ChatMessage message);

	public void ready(MovementMessage message);
}