package com.hvilela.client;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

/**
 * @author Henrique
 *
 */
public class ProgressWindow extends JFrame {

	private static final long serialVersionUID = 916180568139170279L;

	public ProgressWindow() {
		super("Wait please...");

		JPanel root = new JPanel();
		root.setBorder(new EmptyBorder(10, 10, 10, 10));
		root.setLayout(new BorderLayout(10, 10));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		root.add(progressBar);
		
		setContentPane(root);
		
		setPreferredSize(new Dimension(200, 80));
		
		pack();
	}
}
