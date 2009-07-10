package com.hvilela.client;

import com.hvilela.client.jmonkey.GameAgent;
import com.hvilela.common.Position;

/**
 * @author Henrique
 *
 */
public interface ClientEngine {

	public void setInitialData(String id, Position position);
	
	public void start();
	
	public GameAgent getPlayer();

	public void setAgentMovement(String id, Position position, float speed);
	
	public void finish();

	
}