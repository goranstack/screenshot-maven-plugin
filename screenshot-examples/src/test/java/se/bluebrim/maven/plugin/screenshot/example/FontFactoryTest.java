package se.bluebrim.maven.plugin.screenshot.example;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;

import se.bluebrim.maven.plugin.screenshot.Screenshot;
import se.bluebrim.maven.plugin.screenshot.ScreenshotDescriptor;
import se.bluebrim.maven.plugin.screenshot.sample.FontChartPanel;
import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil;

/**
 * Demonstrates how to create screenshots for a factory class containing visualizable resources.
 * In this case we are working with Font's but anything that can be painted by a Swing component will do.
 * {@code FontFactoryTest} creates one font chart screenshot for the Javadoc of the {@code FontFactory} class
 * and one screenshot to be included in the Javadoc for each static method declaration. The {@code @Screenshot}
 * annotaded methods are processed by the screenshot:javadoc goal and stores the images files in a "doc-files" directory
 * following Javadoc conventions.
 * 
 * @author Goran Stack
 *
 */
public class FontFactoryTest {

	
	public static void main(String[] args) {
		final FontFactoryTest instance = new FontFactoryTest();
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				instance.openInWindow();			
			}
		});
	}

	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(createFontChartScreenshot());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}	

	@Screenshot(targetClass = FontFactory.class)
	public JComponent createFontChartScreenshot()
	{
		return FontChartPanel.createFromStaticFontMethods(FontFactory.class);
	}

	
	/**
	 * Creates a screen shot for each method in the PaintFactory 
	 */
	@Screenshot
	public Collection<ScreenshotDescriptor> createStaticMethodScreenshots()
	{
		return SampleUtil.createStaticFontMethodScreenshots(FontFactory.class);
	}

}
