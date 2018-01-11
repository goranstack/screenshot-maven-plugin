package se.bluebrim.maven.plugin.screenshot.example;

import javax.swing.JComponent;

import se.bluebrim.maven.plugin.screenshot.Screenshot;

/**
 * 
 * @author Goran Stack
 *
 */
public class HelloWorldPanelTest {

	@Screenshot
	public JComponent createScreenShot()
	{
		return new HelloWorldPanel();
	}
}
