package com.hvilela.core;

import java.io.Serializable;

import com.hvilela.common.messages.MovementMessage;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.Task;

/**
 * @author Henrique
 */
public class GameAgent extends GameObject implements Serializable, ManagedObject, Task {
	
	private static final long serialVersionUID = -347821968803045076L;

	protected float speed;

	public GameAgent() {
		super();

		speed = 0;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public final void run() throws Exception {
		action();

		ChannelManager channelManager = AppContext.getChannelManager();
		Channel channel = channelManager.getChannel("position");

		MovementMessage message = new MovementMessage(id, this, speed);
		channel.send(message.toBytes().array());
	}

	public void action() {
		direction += 2.5;

		x += speed / 5 * Math.cos((direction - 45) / 180 * Math.PI);
		y -= speed / 5 * Math.sin((direction - 45) / 180 * Math.PI);

		direction += 2.5;
	}

	@Override
	public String toString() {
		return super.toString() + ",s:" + speed;
	}
}