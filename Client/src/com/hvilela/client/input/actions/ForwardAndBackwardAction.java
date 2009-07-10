package com.hvilela.client.input.actions;

import com.hvilela.client.jmonkey.GameAgent;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;

/**
 * @author Henrique
 */
public class ForwardAndBackwardAction extends KeyInputAction {
	public static final int FORWARD = 0;

	public static final int BACKWARD = 1;

	private GameAgent agent;
	
	private int direction;

	public ForwardAndBackwardAction(GameAgent node, int direction) {
		this.agent = node;
		this.direction = direction;
	}

	public void performAction(InputActionEvent evt) {
		if (direction == FORWARD) {
			agent.accelerate(evt.getTime());
		} else if (direction == BACKWARD) {
			agent.brake(evt.getTime());
		}
	}
}