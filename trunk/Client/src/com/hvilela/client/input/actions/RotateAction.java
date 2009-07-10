package com.hvilela.client.input.actions;

import com.hvilela.client.jmonkey.GameAgent;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

/**
 * @author Henrique
 */
public class RotateAction extends KeyInputAction {
	
	public static final int RIGHT = 0;

	public static final int LEFT = 1;
	
	private GameAgent agent;
	
	private int direction;
	
	private int modifier = 1;

	public RotateAction(GameAgent vehicle, int direction) {
		this.agent = vehicle;
		this.direction = direction;
	}

	public void performAction(InputActionEvent evt) {
		if (direction == LEFT) {
			modifier = 1;
		} else if (direction == RIGHT) {
			modifier = -1;
		}

		Matrix3f incr = new Matrix3f();

		if (agent.getSpeed() < 0) {
			incr.fromAngleNormalAxis(-modifier * agent.getTurnSpeed() * evt.getTime(), Vector3f.UNIT_Y);
		} else {
			incr.fromAngleNormalAxis(modifier * agent.getTurnSpeed() * evt.getTime(), Vector3f.UNIT_Y);
		}

		agent.getLocalRotation().fromRotationMatrix(incr.mult(agent.getLocalRotation().toRotationMatrix(new Matrix3f()), new Matrix3f()));
		agent.getLocalRotation().normalize();
	}
}