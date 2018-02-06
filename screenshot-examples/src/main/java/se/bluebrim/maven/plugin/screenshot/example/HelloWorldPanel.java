package se.bluebrim.maven.plugin.screenshot.example;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The classic "Hello World" used to demonstrate the Screenshot Maven Plugin.
 * The camera image was found at: http://wefunction.com/2008/07/function-free-icon-set
 * 
 * @author Goran Stack
 *
 */
@SuppressWarnings("serial")
public class HelloWorldPanel extends JPanel {

	public HelloWorldPanel() 
	{
		JLabel label = new JLabel("Hello World from Screenshot Maven Plugin");
		ImageIcon icon;
		try {
			icon = new ImageIcon(new URL("http://screenshot-maven-plugin.googlecode.com/svn/site/images/camera.png"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		label.setIcon(icon);
		label.setFont(label.getFont().deriveFont(32f));
		label.setForeground(Color.DARK_GRAY);
		add(label);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED.darker(), 4), BorderFactory.createEmptyBorder(60, 20, 60, 20)));		
	}
}
