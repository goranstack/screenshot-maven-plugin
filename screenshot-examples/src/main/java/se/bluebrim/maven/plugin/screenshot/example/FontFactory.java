package se.bluebrim.maven.plugin.screenshot.example;

import java.awt.Font;

/**
 * <img src="doc-files/FontFactory.png" alt="Screenshot image of class: FontFactory" >
 * <p>
 * This is an example of a resource class with visual samples included in the Javadoc.
 * The images for the Javadoc are created by a few lines of  code in a test class.
 * The test class is processed by the screenshot-maven-plugin in a Maven build to produce the images.
 * 
 * @author Goran Stack
 *
 */
public class FontFactory 
{
	/**
	 * <img src="doc-files/FontFactory-serif14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font serif14()
	{
		return new Font(Font.SERIF, Font.PLAIN, 14);
	}
	
	/**
	 * <img src="doc-files/FontFactory-serifBold14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font serifBold14()
	{
		return new Font(Font.SERIF, Font.BOLD, 14);
	}
	
	/**
	 * <img src="doc-files/FontFactory-serifItalic14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font serifItalic14()
	{
		return new Font(Font.SERIF, Font.ITALIC, 14);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerif14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerif14()
	{
		return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerif9.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerif9()
	{
		return new Font(Font.SANS_SERIF, Font.PLAIN, 9);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerif20.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerif20()
	{
		return new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerif28.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerif28()
	{
		return new Font(Font.SANS_SERIF, Font.PLAIN, 28);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerifBold14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerifBold14()
	{
		return new Font(Font.SANS_SERIF, Font.BOLD, 14);
	}
	
	/**
	 * <img src="doc-files/FontFactory-sansSerifItalic14.png" alt="Screenshot image of class: FontFactory" >
	 */
	public static Font sansSerifItalic14()
	{
		return new Font(Font.SANS_SERIF, Font.ITALIC, 14);
	}

}
