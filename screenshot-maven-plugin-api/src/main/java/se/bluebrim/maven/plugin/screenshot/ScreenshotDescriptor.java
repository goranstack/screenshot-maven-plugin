package se.bluebrim.maven.plugin.screenshot;

import javax.swing.JComponent;


/**
 * Used as element type for a {@code Collection} of screenshots returned 
 * by a {@code @Screenshot} annotated method in a test class.
 * <p>
 * Sometimes it's more convenient to return a {@code Collection} of screen 
 * shots from a {@code @Screenshot} annotated method
 * instead of writing a separate method for each screenshot. 
 * In that case the parameters specified for the {@code @Screenshot} annotation
 * are ignored and instead you can specify the corresponding parameters in 
 * the {@code ScreenshotDescriptor} for each screenshot in the collection.
 * <p>
 * Since the possibility to return a Collection of {@code ScreenshotDescriptor} was
 * added to generate screenshots for resource classes containing Paint, Fonts etc
 * Collections of ScreenshotDescriptor are only processed by the {@code screenshot:javadoc} goal.
 * <p>
 * @author Goran Stack
 *
 */
public class ScreenshotDescriptor 
{
	private JComponent screenshot;
	private Class<?> targetClass;
	private String scene;
	private boolean oneForEachLocale;
	
	public ScreenshotDescriptor(JComponent screenshot, Class<?> targetClass, String scene, boolean oneForEachLocale) 
	{
		super();
		this.screenshot = screenshot;
		this.targetClass = targetClass;
		this.scene = scene;
		this.oneForEachLocale = oneForEachLocale;
	}
	
	public ScreenshotDescriptor(JComponent screenshot, Class<?> targetClass, String scene)
	{
		this(screenshot, targetClass, scene, false);
	}

	public JComponent getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(JComponent screenshot) {
		this.screenshot = screenshot;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public boolean isOneForEachLocale() {
		return oneForEachLocale;
	}

	public void setOneForEachLocale(boolean oneForEachLocale) {
		this.oneForEachLocale = oneForEachLocale;
	}
	
	

}
