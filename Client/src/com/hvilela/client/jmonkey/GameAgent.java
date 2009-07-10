package com.hvilela.client.jmonkey;

import com.hvilela.client.util.TextLabel2D;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Spatial;
import com.jmex.model.animation.JointController;

/**
 * @author Henrique
 */
public class GameAgent extends GameObject {

	private static final long serialVersionUID = 1L;

	private float speed;

	private float acceleration;

	private float braking;

	private float turnSpeed;

	private float maxSpeed;

	private float minSpeed;

	private JointController controller;

	public GameAgent(String id, Spatial model) {
		this(id, model, 5, 0, 10, 2, 2.5f);
	}

	public GameAgent(String id, Spatial model, float maxSpeed, float minSpeed, float acceleration, float braking, float turnSpeed) {
		super(id, model);

		BillboardNode nameModel = new TextLabel2D(id).getBillboard(0.3f);
		nameModel.setLocalTranslation(new Vector3f(0, 2f, 0));
		attachChild(nameModel);
		
		controller = ((JointController) model.getController(0));
		controller.setTimes(0, 26);

		this.speed = 0;
		this.maxSpeed = maxSpeed;
		this.minSpeed = minSpeed;
		this.acceleration = acceleration;
		this.braking = braking;
		this.turnSpeed = turnSpeed;
	}

	public void update(float time) {
		this.localTranslation.addLocal(this.localRotation.getRotationColumn(2, new Vector3f()).multLocal(speed * time));
		controller.setSpeed(speed / maxSpeed * 2);
	}

	public boolean isMoving() {
		return speed > FastMath.FLT_EPSILON || speed < -FastMath.FLT_EPSILON;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}

	public float getBraking() {
		return braking;
	}

	public void setBraking(float braking) {
		this.braking = braking;
	}

	public Spatial getModel() {
		return model;
	}

	public void setModel(Spatial model) {
		this.detachChild(this.model);
		this.model = model;
		this.attachChild(this.model);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getTurnSpeed() {
		return turnSpeed;
	}

	public void setTurnSpeed(float turnSpeed) {
		this.turnSpeed = turnSpeed;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public float getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(float minSpeed) {
		this.minSpeed = minSpeed;
	}

	public void brake(float time) {
		speed -= time * braking;
		if (speed < minSpeed) {
			speed = minSpeed;
		}
	}

	public void accelerate(float time) {
		speed += time * acceleration;
		if (speed > maxSpeed) {
			speed = maxSpeed;
		}
	}

	public void drift(float time) {
		if (speed < -FastMath.FLT_EPSILON) {
			speed += (5 * time);

			if (speed > 0) {
				speed = 0;
			}
		} else if (speed > FastMath.FLT_EPSILON) {
			speed -= (5 * time);

			if (speed < 0) {
				speed = 0;
			}
		}
	}
}
