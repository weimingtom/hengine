package com.hvilela.common.messages;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.hvilela.common.Message;
import com.hvilela.common.Position;

public class MovementMessage extends Message {

	private Position position;
	
	private float speed;

	public MovementMessage() {
		this(null, null, 0);
	}

	public MovementMessage(UUID sender, Position position, float speed) {
		super(4 * Float.SIZE, Message.Type.POSITION, sender);
		
		this.position = position;
		this.speed = speed;
	}

	public Position getPosition() {
		return position;
	}

	public float getSpeed() {
		return speed;
	}
	
	@Override
	public ByteBuffer toBytes() {
		ByteBuffer byteBuffer = super.toBytes();

		byteBuffer.putFloat(position.getX());
		byteBuffer.putFloat(position.getY());
		byteBuffer.putFloat(position.getZ());
		byteBuffer.putFloat(position.getDirection());

		return byteBuffer;
	}

	public MovementMessage fromBytes(ByteBuffer byteBuffer) {
		MovementMessage message = new MovementMessage();

		message.read(byteBuffer);

		return message;
	}

	public void read(ByteBuffer byteBuffer) {
		super.read(byteBuffer);

		position = new Position();
		
		position.setX(byteBuffer.getFloat());
		position.setY(byteBuffer.getFloat());
		position.setZ(byteBuffer.getFloat());
		position.setDirection(byteBuffer.getFloat());
	}

	@Override
	public String toString() {
		return super.toString() + " " + position.toString();
	}
}