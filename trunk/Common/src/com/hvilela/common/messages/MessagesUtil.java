package com.hvilela.common.messages;

import java.nio.ByteBuffer;

import com.hvilela.common.Message;

public class MessagesUtil {
	
	public Message parseMessage(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		byte type = buffer.get();

		if (type == Message.Type.POSITION.value) {
			MovementMessage positionMessage = new MovementMessage();
			positionMessage.fromBytes(buffer);
			
			return positionMessage;
		}

		return null;
	}
}