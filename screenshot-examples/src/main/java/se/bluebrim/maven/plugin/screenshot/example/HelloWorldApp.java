package se.bluebrim.maven.plugin.screenshot.example;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Demonstrates the use of a JVM splash screen created by the Screenshot Maven
 * Plugin. Run mvn install to get a target/classes/splash.png image of the
 * {@link SplashScreenPanel} Specify the VM argument:
 * -splash:target/classes/splash.png
 * 
 * @author Goran Stack
 *
 */
public class HelloWorldApp {

	public static void main(String[] args) throws InterruptedException {
		new HelloWorldApp().openInWindow();
	}

	private void openInWindow() {
		JFrame window = new JFrame(getClass().getSimpleName());
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.getContentPane().add(createWindowContent());
		window.pack();
		window.setLocationRelativeTo(null);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> window.setVisible(true));
			}
		}, 3000);
	}

	public JComponent createWindowContent() {
		return new HelloWorldPanel();
	}
}
