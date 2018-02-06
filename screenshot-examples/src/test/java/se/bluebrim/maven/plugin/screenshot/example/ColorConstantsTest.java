package se.bluebrim.maven.plugin.screenshot.example;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXFrame;

import se.bluebrim.maven.plugin.screenshot.Screenshot;
import se.bluebrim.maven.plugin.screenshot.ScreenshotDescriptor;
import se.bluebrim.maven.plugin.screenshot.sample.PalettePanel;
import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil;

/**
 * Demonstrates how to create screenshots for a constant class containing visualizable resources.
 * In this case we are working with Colors but anything that can be painted by a Swing component will do.
 * {@code ColorConstantsTest} creates one gallery screenshot for the Javadoc of the {@code ColorConstant} class
 * and one screenshot to be included in the Javadoc for each static field declaration. The {@code @Screenshot}
 * annotaded methods are processed by the screenshot:javadoc goal and stores the images files in a "doc-files" directory
 * following Javadoc conventions.
 * 
 * @author Goran Stack
 *
 */
public class ColorConstantsTest {

	public static void main(String[] args) {
		final ColorConstantsTest instance = new ColorConstantsTest();
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
		window.getContentPane().add(createPaletteScreenshot());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}	


	@Screenshot(oneForEachLocale = false, targetClass = ColorConstants.class)
	public JComponent createPaletteScreenshot()
	{
		return PalettePanel.createFromStaticPaintFields(ColorConstants.class, 7);
	}

	
	/**
	 * Creates a screenshot for each field in the ColorConstants class
	 */
	@Screenshot
	public Collection<ScreenshotDescriptor> createStaticFieldScreenshots()
	{
		return SampleUtil.createStaticPaintFieldScreenshots(ColorConstants.class);
	}

}
