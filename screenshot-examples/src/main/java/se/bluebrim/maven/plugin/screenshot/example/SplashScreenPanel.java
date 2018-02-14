package se.bluebrim.maven.plugin.screenshot.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class SplashScreenPanel extends JPanel {
	
	public SplashScreenPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(new Color(33, 104, 183));
		JLabel message = new JLabel("Starting...");
		message.setFont(message.getFont().deriveFont(36f).deriveFont(Font.ITALIC));
		message.setForeground(Color.WHITE);
		message.setHorizontalAlignment(SwingConstants.CENTER);
		add(message, BorderLayout.CENTER);
		JLabel version = new JLabel("Version: " + BuildProperties.PROJECT_VERSION);
		version.setForeground(Color.WHITE);
		version.setHorizontalAlignment(SwingConstants.RIGHT);
		add(version, BorderLayout.SOUTH);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 400);
	}

}
