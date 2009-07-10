package com.hvilela.core;

import java.io.Serializable;
import java.util.UUID;

import com.hvilela.common.Position;
import com.sun.sgs.app.ManagedObject;

/**
 * @author   henrique
 */
public class GameObject extends Position implements ManagedObject, Serializable  {

	private static final long serialVersionUID = 4681063103044899719L;

	protected UUID id;

	public GameObject() {
		this.id = UUID.randomUUID();
	}

	public void setPosition(Position position) {
		this.x = position.getX();
		this.y = position.getY();
		this.z = position.getZ();
		
		this.direction = position.getDirection();
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + " " + super.toString();
	}
}