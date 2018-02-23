package se.bluebrim.maven.plugin.screenshot.example;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * <img src="doc-files/IconConstants.png" alt="Screenshot image of class: IconConstants" >
 * <p>
 * This is an example of a resource class with visual samples included in the Javadoc.
 * The images for the Javadoc are created by a few lines of  code in a test class.
 * The test class is processed by the screenshot-maven-plugin in a Maven build to produce the images.
 * 
 * @author Goran Stack
 *
 */
public class IconConstants {
	
	/**
	 * <img src="doc-files/IconConstants-dialog_error.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon DIALOG_ERROR = loadIcon("dialog-error.png");
	
	/**
	 * <img src="doc-files/IconConstants-dialog_information.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon DIALOG_INFORMATION = loadIcon("dialog-information.png");
	
	/**
	 * <img src="doc-files/IconConstants-dialog_question.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon DIALOG_QUESTION = loadIcon("dialog-question.png");
	
	/**
	 * <img src="doc-files/IconConstants-dialog_warning.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon DIALOG_WARNING = loadIcon("dialog-warning.png");
	
	/**
	 * <img src="doc-files/IconConstants-emblem_readonly.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon EMBLEM_READONLY = loadIcon("emblem-readonly.png");
 
	/**
	 * <img src="doc-files/IconConstants-emblem_unreadable.png" alt="Screenshot image of class:IconConstants " >
	 */
    public final static Icon EMBLEM_UNREADABLE = loadIcon("emblem-unreadable.png");
 
	/**
	 * <img src="doc-files/IconConstants-emblem_web.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon EMBLEM_WEB = loadIcon("emblem-web.png");
 
	/**
	 * <img src="doc-files/IconConstants-folder_open.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon FOLDER_OPEN = loadIcon("folder-open.png");
 
	/**
	 * <img src="doc-files/IconConstants-folder_remote.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon FOLDER_REMOTE = loadIcon("folder-remote.png");
 
	/**
	 * <img src="doc-files/IconConstants-package_x_generic.png" alt="Screenshot image of class: IconConstants" >
	 */
    public final static Icon PACKAGE_X_GENERIC = loadIcon("package-x-generic.png");
 
    private static Icon loadIcon(String name)
    {
    	return new ImageIcon(IconConstants.class.getResource("/images/" + name));
    }

}
