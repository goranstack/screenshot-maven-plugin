package se.bluebrim.maven.plugin.screenshot.decorate;

/**
 * Marker class used to emphasize components in screenshots. If one or more Emphasizer objects are found,
 * the screenshot is painted with a blur effect and then all emphasized component
 * are painted a second time without any blur effect.
 * <p> 
 * <img src="doc-files/Emphasizer.png" alt="Screenshot image of class: Emphasizer" > 
 * <p>
 * You can still decorate components as usual
 * <p>
 * <img src="doc-files/Emphasizer-deco.png" alt="Screenshot image of class: Emphasizer" >
 * <p>
 * @author Goran Stack
 *
 */
public class Emphasizer {
	
	public static final String CLIENT_PROPERTY_KEY = "emphasizer";

}
