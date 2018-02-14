package se.bluebrim.maven.plugin.screenshot.example;

import org.jdesktop.swingx.JXFrame;

/**
 * 
 * @author Goran Stack
 *
 */
public class SplashScreenPanelTest {
	
	public static void main(String[] args) {
		new SplashScreenPanelTest().openInWindow();
	}
	
	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(new SplashScreenPanel());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}	

}
