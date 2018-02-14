package se.bluebrim.maven.plugin.screenshot.example;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Demonstrates the use of a JVM splash screen created by the Screenshot Maven Plugin.
 * Run mvn install to get a target/classes/splash.png image of the {@link SplashScreenPanel}
 * Specify the VM argument:
 * -splash:target/classes/splash.png
 *  
 * @author Goran Stack
 *
 */
public class HelloWorldApp {
	
	public static void main(String[] args) throws InterruptedException {
		SwingUtilities.invokeLater(() -> new HelloWorldApp().openInWindow());
	}
	
	private void openInWindow()
	{
		JFrame window = new JFrame(getClass().getSimpleName());
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.getContentPane().add(createWindowContent());
		window.pack();
		window.setLocationRelativeTo(null);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		window.setVisible(true);		
	}	


	public JComponent createWindowContent()
	{
		return new HelloWorldPanel();
	}
}
