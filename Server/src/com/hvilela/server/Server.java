package com.hvilela.server;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hvilela.common.Position;
import com.hvilela.common.messages.MovementMessage;
import com.hvilela.core.GameAgent;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.TaskManager;

/**
 * @author Henrique
 *
 */
public class Server implements Serializable, AppListener, ChannelListener {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public void initialize(Properties props) {
		getChannel("position");
		
		TaskManager taskManager = AppContext.getTaskManager();

		for (int i = 0; i < 5; i++) {
			GameAgent agent = new GameAgent();
			agent.setPosition(new Position((float) (Math.random() * 200), (float) (Math.random() * 200), 0, (float) (Math.random() * 360)));
			agent.setSpeed(10);

			taskManager.schedulePeriodicTask(agent, 0, 200);
		}
	}
	
	public ClientSessionListener loggedIn(ClientSession session) {
		DataManager dataManager = AppContext.getDataManager();
		
		logger.log(Level.INFO, "User {0} has logged in", session.getName());

		String playerBinding = "Player." + session.getName();
		String agentBinding = "Agent." + session.getName();
		
		UserSession userSession;
		GameAgent agent;

		try {
			userSession = dataManager.getBinding(playerBinding, UserSession.class);
			userSession.setSession(session);

			agent = dataManager.getBinding(agentBinding, GameAgent.class);
			
			logger.log(Level.INFO, "LOADED " + playerBinding + " " + agentBinding);
		} catch (NameNotBoundException exception) {
			logger.log(Level.INFO, exception.getMessage());
			
			userSession = new UserSession(session);
			dataManager.setBinding(playerBinding, userSession);

			agent = new GameAgent();
			agent.setPosition(new Position(100, 100, 0, 0));
			dataManager.setBinding(agentBinding, agent);
			
			logger.log(Level.INFO, "CREATED " + playerBinding + " " + agentBinding);
		}

		getChannel("chat").join(session, null);
		getChannel("position").join(session, null);

		MovementMessage message = new MovementMessage(agent.getId(), agent, agent.getSpeed());
		userSession.send(message);

		logger.log(Level.INFO, "done");
		
		return userSession;
	}

	private Channel getChannel(String name) {
		ChannelManager channelManager = AppContext.getChannelManager();
		
		try {
			channelManager.createChannel(name, this, Delivery.RELIABLE);
		} catch (Exception exception) {
		}

		return channelManager.getChannel(name);
	}

	@Override
	public void receivedMessage(Channel channel, ClientSession session, byte[] messageData) {
		DataManager dataManager = AppContext.getDataManager();

		String agentBinding = "Agent." + session.getName();
		GameAgent agent = dataManager.getBinding(agentBinding, GameAgent.class);

		MovementMessage message = new MovementMessage();
		message.read(ByteBuffer.wrap(messageData));

		agent.setPosition(message.getPosition());
		agent.setSpeed(message.getSpeed());
	}
}