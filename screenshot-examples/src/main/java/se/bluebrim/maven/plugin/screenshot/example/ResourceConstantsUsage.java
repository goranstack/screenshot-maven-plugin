package se.bluebrim.maven.plugin.screenshot.example;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;

/**
 * A class to demonstrate the visual experience of images included in the Javadoc
 * when activating code completion in your IDE.
 * <p>
 * In Eclipse IDE (works probably in other IDE's as well) try the following:
 * <ul>
 * <li>Hover your mouse pointer over a {@code ColorConstants} class reference and see how the Javadoc pop ups</li>
 * <li>Try to assign {@code yourColor} with a color from {@code ColorConstants}</li>
 * <li>Do the same things with {@code IconConstants} and {@link FontFactory}
 * </ul>
 * 
 * @author Goran Stack
 *
 */
public class ResourceConstantsUsage {
	
	private Color myColor = ColorConstants.TRANSPARENT_BLUE;
	private Color yourColor;
	private Icon myIcon = IconConstants.DIALOG_INFORMATION;
	private Icon yourIcon;
	private Font myFont = FontFactory.sansSerif28();
	private Font yourFont;

}
