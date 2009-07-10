package com.hvilela.common;

import java.io.Serializable;

public class Point implements Serializable {
	
	private static final long serialVersionUID = -5619946015889485739L;

	protected float x;
	
	protected float y;
	
	protected float z;

	public Point() {
		this(0, 0, 0);
	}
	
	public Point(float x, float y, float z) {
		this.x = x;
		
		this.y = y;
		
		this.z = z;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "x:"+ x + ",y:" + y + ",z:" + z;
	}
}