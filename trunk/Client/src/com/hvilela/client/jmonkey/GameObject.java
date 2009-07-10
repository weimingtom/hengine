package com.hvilela.client.jmonkey;

import com.hvilela.common.Position;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * @author Henrique
 */
public class GameObject extends Node {

	private static final long serialVersionUID = 1L;

	protected Spatial model;

	public GameObject(String id, Spatial model) {
		super(id);

		setModel(model);
	}

	public float getX() {
		return localTranslation.x;
	}
	
	public float getY() {
		return localTranslation.z;
	}

	public float getZ() {
		return localTranslation.y;
	}

	public float getRotation() {
		return localRotation.toAngles(null)[1] * FastMath.RAD_TO_DEG;
	}

	public Spatial getModel() {
		return model;
	}

	public void setModel(Spatial model) {
		this.detachChild(this.model);
		this.model = model;
		this.attachChild(this.model);
	}

	public void setPosition(Position position) {
		setLocalTranslation(position.getX(), position.getZ(), position.getY());
		setLocalRotation(new Quaternion(new float[] {0, position.getDirection() * FastMath.DEG_TO_RAD, 0}));
		localRotation.normalize();
	}
}