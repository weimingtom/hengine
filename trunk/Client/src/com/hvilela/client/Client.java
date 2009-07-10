package com.hvilela.client;

import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.hvilela.client.jmonkey.GameAgent;
import com.hvilela.client.jmonkey.JMonkeyEngine;
import com.hvilela.client.net.Connection;
import com.hvilela.client.net.ConnectionListener;
import com.hvilela.common.Position;
import com.hvilela.common.messages.ChatMessage;
import com.hvilela.common.messages.MovementMessage;

/**
 * @author Henrique
 */
public class Client implements ConnectionListener, Runnable {

	private Connection connection;

	private ClientEngine world;

	private static ProgressWindow progress;

	public static void main(String[] args) {
		new Client();

		progress = new ProgressWindow();
		progress.setVisible(true);
	}

	public Client() {
		ResourceBundle config = ResourceBundle.getBundle("config");

		world = new JMonkeyEngine();
		connection = new Connection(config.getString("host"), config.getString("port"));
		connection.addConnectionListener(this);

		login();
	}

	private void login() {
		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("username"));
		JTextField username = new JTextField("guest");
		panel.add(username);

		panel.add(new JLabel("password"));
		JTextField password = new JPasswordField("guest");
		panel.add(password);

		if (JOptionPane.showConfirmDialog(null, panel) == JOptionPane.OK_OPTION) {
			connection.login(username.getText(), password.getText());
		}
	}

	@Override
	public void loggedIn() {
		System.out.println("Logged in");
	}

	public void ready(MovementMessage message) {
		System.out.println("Ready " + message);

		world.setInitialData(message.getSender().toString(), message.getPosition());

		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();

		progress.setVisible(false);

		createGame();
	}

	public void run() {
		try {
			while (true) {
				try {
					GameAgent player = world.getPlayer();

					if (player != null) {
						MovementMessage message = new MovementMessage(connection.getId(), new Position(player.getX(), player.getY(), player.getZ(), player.getRotation()), 0);
						connection.sendChannelMessage("position", message);

						Thread.sleep(200);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void createGame() {
		new Thread() {
			@Override
			public void run() {
				world.start();
				super.run();
			}
		}.start();
	}

	@Override
	public void receivedMovementMessage(MovementMessage message) {
		world.setAgentMovement(message.getSender().toString(), message.getPosition(), message.getSpeed());
	}

	@Override
	public void receivedChatMessage(ChatMessage chatMessage) {
		System.out.println("Channel: " + chatMessage.getMessage());
	}

	@Override
	public void loggedOut() {
		System.out.println("Logged out");

		world.finish();
		login();
	}
}