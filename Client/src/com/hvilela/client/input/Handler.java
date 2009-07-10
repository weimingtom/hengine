package com.hvilela.client.input;


import com.hvilela.client.input.actions.DriftAction;
import com.hvilela.client.input.actions.ForwardAndBackwardAction;
import com.hvilela.client.input.actions.RotateAction;
import com.hvilela.client.jmonkey.GameAgent;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;

/**
 * @author Henrique
 */
public class Handler extends InputHandler {

	private GameAgent agent;

	private DriftAction drift;

	public Handler(GameAgent agent, String api) {
		this.agent = agent;
		setKeyBindings(api);
		setActions();
	}

	private void setActions() {
		ForwardAndBackwardAction forward = new ForwardAndBackwardAction(agent, ForwardAndBackwardAction.FORWARD);
		addAction(forward, "forward", true);

		ForwardAndBackwardAction backward = new ForwardAndBackwardAction(agent, ForwardAndBackwardAction.BACKWARD);
		addAction(backward, "backward", true);
		
		RotateAction rotateLeft = new RotateAction(agent, RotateAction.LEFT);
		addAction(rotateLeft, "turnLeft", true);

		RotateAction rotateRight = new RotateAction(agent, RotateAction.RIGHT);
		addAction(rotateRight, "turnRight", true);

		drift = new DriftAction(agent);
	}

	private void setKeyBindings(String api) {
		KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

		keyboard.add("forward", KeyInput.KEY_UP);
		keyboard.add("backward", KeyInput.KEY_DOWN);
		keyboard.add("turnRight", KeyInput.KEY_RIGHT);
		keyboard.add("turnLeft", KeyInput.KEY_LEFT);
		
		keyboard.add("forward", KeyInput.KEY_W);
		keyboard.add("backward", KeyInput.KEY_S);
		keyboard.add("turnRight", KeyInput.KEY_D);
		keyboard.add("turnLeft", KeyInput.KEY_A);
	}

	public void update(float time) {
		if (!isEnabled())
			return;

		super.update(time);

		drift.performAction(event);
		agent.update(time);
	}
}
