package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Implemented by objects that paints various decorations
 * on screenshots i. e. call out figures, emphasize certain component and so on.
 * When a ScreenshotDecorator is installed at a Swing component as a client property
 * it will be found by the screenshot plugin and the paint method will be called
 * with a Graphics2D parameter created from the screenshot image. 
 * 
 * @author Goran Stack
 *
 */
public interface ScreenshotDecorator {
	public static final String CLIENT_PROPERTY_KEY = "screenshotDecorator";

	public Rectangle2D getBounds(JComponent component, JComponent rootComponent);
	public void paint(Graphics2D g2d, JComponent component, JComponent rootComponent);
}
