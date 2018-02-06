package se.bluebrim.maven.plugin.screenshot.example;

import javax.swing.JComponent;

import org.jdesktop.swingx.JXFrame;

import se.bluebrim.maven.plugin.screenshot.Screenshot;

/**
 * 
 * @author Goran Stack
 *
 */
public class HelloWorldPanelTest {
	
	public static void main(String[] args) {
		new HelloWorldPanelTest().openInWindow();
	}
	
	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(createScreenShot());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}	


	@Screenshot
	public JComponent createScreenShot()
	{
		return new HelloWorldPanel();
	}
}
