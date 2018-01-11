package se.bluebrim.maven.plugin.screenshot;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.sonatype.plexus.build.incremental.BuildContext;

import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil;


/**
 * Mojo that generates an image file of a splash screen panel to be used in applications
 * that uses the JVM splash screen feature for an early splash screen that is preceded
 * by Swing panel based splash screen for rich progress feedback. 
 * 
 * @goal splashscreen
 * @requiresDependencyResolution test
 * 
 * @author G Stack
 * 
 */
public class SplashScreenMojo extends AbstractMojo
{
	  /** @component */
	  private BuildContext buildContext;

    /**
     * The name of the splash screen panel class that will be rendered to the specified image file
     * 
     * @parameter
 	 * @required
    */
	private String splashScreenPanelClassName;
	/**
	 * The image file where the splash screen image is written. The extension will be used as the image type
	 * argument to the {@link ImageIO#write(java.awt.image.RenderedImage, String, File)} call.
	 * 
	 * @parameter
	 * @required
	 */
	private File imageFile;

	/**
	 * The directory containing generated classes of the project.
	 * 
	 * @parameter expression="project.build.outputDirectory"
	 */
	private File classesDirectory;
	
	
	/**
     * The classpath elements of the project being tested.
     *
     * @parameter expression="project.testClasspathElements"
     * @required
     * @readonly
     */
    private ArrayList<String> testClasspathElements;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException
	{
//		getLog().info("Splash screen executed. Creating an image of: \"" + splashScreenPanelClassName + "\" and writing it to: \"" + imageFile.getPath() + "\"", null);
		try {
			imageFile.getParentFile().mkdirs();
			imageFile.createNewFile();
			BufferedImage splashScreenImage = createSplashScreenImage();
			ImageIO.write(splashScreenImage, FilenameUtils.getExtension(imageFile.getName()), imageFile);
			buildContext.refresh(imageFile);
		} catch (Exception e) {
			getLog().error(e);
		} 
	}
	
	
	private BufferedImage createSplashScreenImage() throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
	{
		Class<?> splashScreenPanelClass = Class.forName(splashScreenPanelClassName, true, createClassLoader());
		Constructor<?>	constructor = splashScreenPanelClass.getConstructor(new Class[]{});
		JComponent splashScreen = (JComponent)constructor.newInstance( new Object[]{});	
		return ripSwingComponent(splashScreen);
	}
	
	private BufferedImage ripSwingComponent(final JComponent component)
	{
		component.setLocation(0, 0);
		component.setSize(component.getPreferredSize());
		SampleUtil.propagateDoLayout(component);
		Rectangle2D dest = component.getBounds();
		BufferedImage image = new BufferedImage((int)dest.getWidth(), (int)dest.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g =  image.createGraphics();
		try {
			g.translate(-dest.getX(), -dest.getY());
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
			component.setDoubleBuffered(false);
			component.print(g);
		} catch (Exception e) {
			getLog().error("Unable to render spash screen panel", e);
		} finally
		{
			g.dispose();
		}
		return image;
	}

	private ClassLoader createClassLoader() throws MalformedURLException
	{
		List<URL> urls = collectURLs();
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
	}


	private List<URL> collectURLs() throws MalformedURLException
 {
		List<URL> urls = new ArrayList<URL>();
		urls.add(classesDirectory.toURI().toURL());

		for (String classpathElement : testClasspathElements) {
			File pathelem = new File(classpathElement);
			// we need to use 3 slashes to prevent Windows from interpreting
			// 'file://D:/path' as server 'D'
			// we also have to add a trailing slash after directory paths
			URL url = new URL("file:///" + pathelem.getPath()
					+ (pathelem.isDirectory() ? "/" : ""));
			urls.add(url);
			getLog().debug("Adding " + classpathElement + " to the class path used to load the splash screen panel");

		}
		return urls;
	}
	
	
}
