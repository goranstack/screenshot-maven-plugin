package se.bluebrim.maven.plugin.screenshot;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.jdesktop.swingx.image.GaussianBlurFilter;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import com.keypoint.PngEncoder;

import se.bluebrim.maven.plugin.screenshot.decorate.DecoratorUtils;
import se.bluebrim.maven.plugin.screenshot.decorate.ScreenshotDecorator;
import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil;

/**
 * Abstract super class to objects that scans test classes for methods annotated
 * with Screenshot annotation. Since our two mojo's has different superclass we
 * can't use inheritance to provide the features in this class to our mojo's.
 * <p>
 * <a href=
 * "http://www.mail-archive.com/user@mojo.codehaus.org/msg01547.html">Adding
 * project dependencies to a plugin</a>
 * 
 * @author G Stack
 *
 */
public abstract class ScreenshotScanner {

	protected static final String FORMAT_PNG = "png";
	protected MavenProject project;
	protected AbstractMojo mojo;
	private File testClassesDirectory;
	private File classesDirectory;
	private List<String> testClasspathElements;
	private Class<Screenshot> screenshotAnnotation;
	private float scaleFactor = 1f;
	private List<LocaleSpec> localeSpecs;

	public ScreenshotScanner(AbstractMojo mojo, File testClassesDirectory, File classesDirectory,
			List<String> testClasspathElements, List<LocaleSpec> localeSpecs) {
		super();
		this.mojo = mojo;
		this.testClassesDirectory = testClassesDirectory;
		this.classesDirectory = classesDirectory;
		this.testClasspathElements = testClasspathElements;
		this.localeSpecs = localeSpecs;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	protected abstract void handleFoundMethod(Class<?> candidateClass, Method method);

	protected File createScreenshotFile(JComponent screenShotComponent, File dir, String screenshotName) {
		File file = new File(dir.getPath(), screenshotName + "." + FORMAT_PNG);
		File tempFile = createTempFile(screenshotName, "." + FORMAT_PNG, dir);
		takeScreenShot(screenShotComponent, tempFile);
		overwriteIfChanged(file, tempFile);
		return file;
	}

	protected String createScreenshotName(Class<?> screenshotClass, String sceneName) {
		return createScreenshotName(screenshotClass, sceneName, false);
	}

	protected String createScreenshotName(Class<?> screenshotClass, String sceneName, boolean appendLocale) {
		String locale = appendLocale ? "-" + Locale.getDefault().toString() : "";
		return screenshotClass.getSimpleName() + sceneName + locale;
	}

	private void overwriteIfChanged(File originalFile, File tempFile) {
		try {
			if (!FileUtils.contentEquals(originalFile, tempFile)) {
				FileUtils.copyFile(tempFile, originalFile);
				getLog().info("Saved screenshot to: " + originalFile.getName() + " " + originalFile.getPath());
			} else
				getLog().debug("Screenshot unchanged: " + originalFile.getName() + " " + originalFile.getPath());

		} catch (IOException e) {
			throw new RuntimeException(
					"Unable to save screenshot: " + originalFile.getName() + " " + originalFile.getPath(), e);
		} finally {
			tempFile.delete();
		}
	}

	protected File createTempFile(String prefix, String suffix, File directory) {
		try {
			return File.createTempFile(prefix, suffix, directory);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create temp file for storing screenshot: " + directory.getPath() + "/"
					+ prefix + suffix, e);
		}
	}

	/**
	 * Compare name of classes instead of classes to handle that the classes are
	 * loaded with different ClassLoaders.
	 * 
	 * @return The class that should be associated with the screenshot. There are
	 *         cases where the screenShotComponent is a generic panel class
	 *         containing the specific screenshot class.
	 */
	protected Class<?> getTargetClass(Method method, JComponent screenShotComponent) {
		Class<?> targetClass = (Class<?>) retrieveAnnotationPropertyValue(method, "targetClass");
		return ObjectUtils.Null.class.getName().equals(targetClass.getName()) ? screenShotComponent.getClass()
				: targetClass;
	}

	protected boolean isOneForEachLocale(Method method) {
		return (Boolean) retrieveAnnotationPropertyValue(method, "oneForEachLocale");
	}

	protected String getSceneName(Method method) {
		String scene = (String) retrieveAnnotationPropertyValue(method, "scene");
		return (StringUtils.isEmpty(scene)) ? "" : "-" + scene;
	}

	/**
	 * We have to retrieve the property values of the Screenshot annotation by using
	 * reflection to avoid ClassCastException when assigning a Screenshot annotation
	 * to variable typed with a Screenshot that is loaded with a different
	 * ClassLoader. By using reflection there is no need for that assignment.
	 */
	private Object retrieveAnnotationPropertyValue(Method method, String propertyName) {
		Method annotationProperty = null;
		try {
			annotationProperty = screenshotAnnotation.getMethod(propertyName, new Class[] {});
		} catch (SecurityException e) {
			getLog().error("Unable to access Screenshot annotation property \"" + propertyName + "\"", e);
		} catch (NoSuchMethodException e) {
			getLog().error("Annotation property \"" + propertyName + "\" is missing", e);
		}
		Object annotation = method.getAnnotation(screenshotAnnotation);
		Object value = null;
		try {
			value = annotationProperty.invoke(annotation);
		} catch (IllegalArgumentException e) {
			getLog().error("Unable to access annotation property \"" + propertyName + "\"", e);
		} catch (IllegalAccessException e) {
			getLog().error("Unable to access annotation property \"" + propertyName + "\"", e);
		} catch (InvocationTargetException e) {
			getLog().error("Unable to access annotation property \"" + propertyName + "\"", e);
		}
		getLog().debug("Screenshot annotation property " + propertyName + ": " + value);
		return value;
	}

	protected Log getLog() {
		return mojo.getLog();
	}

	private List<URL> collectURLs() {
		List<URL> urls = new ArrayList<URL>();
		try {
			urls.add(testClassesDirectory.toURI().toURL());				
			urls.add(classesDirectory.toURI().toURL());				

			for (String classpathElement : testClasspathElements) {
				File pathelem = new File(classpathElement);
				// we need to use 3 slashes to prevent Windows from interpreting
				// 'file://D:/path' as server 'D'
				// we also have to add a trailing slash after directory paths
				URL url = new URL("file:///" + pathelem.getPath() + (pathelem.isDirectory() ? "/" : ""));
				urls.add(url);

			}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return urls;
	}

	private URL getURLtoScan() {
		try {
			return testClassesDirectory.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final void execute() {
		annotationScan();
	}
	
	protected void processModule() {
		
	}

	/**
	 * Make sure that the test class and the annotation class are loaded with the
	 * same class loader otherwise the Method.isAnnotationPresent won't work. <br>
	 * When screenshots are created for more than one Locale the classes are
	 * unloaded after each Locale to allow static declared ResourceBundles variables
	 * and other Locale dependent variables. The unloading of classes is done by
	 * removing all references to the class loader and the force a garbage collect.
	 * The same technique is used by Tomcat and OSGi.
	 */
	private void annotationScan() {
		ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			List<Locale> locales = getLocales();
			for (Locale locale : locales) {
				Locale.setDefault(locale);
				ClassLoader classLoader = createClassLoader();
				screenshotAnnotation = loadAnnotationClass(Screenshot.class.getName(), classLoader);
				Reflections reflections = null;
				reflections = new Reflections(new ConfigurationBuilder().setUrls(getURLtoScan())
						.addClassLoader(classLoader).setScanners(new MethodAnnotationsScanner()));

				Set<Method> annotadedMethods = reflections.getMethodsAnnotatedWith(Screenshot.class);

				getLog().info("Found: " + annotadedMethods.size() + " screenshot annotaded methods");
				
				if (annotadedMethods.size() > 0) {
					processModule();
				}

				getLog().info("Processing annotaded metthods with Locale=" + locale);
				Thread.currentThread().setContextClassLoader(classLoader);
				processAnnotadedMethods(annotadedMethods);
				classLoader = null;
				Thread.currentThread().setContextClassLoader(null);
				screenshotAnnotation = null;
				System.gc(); // Asynchronous garbage collector might already run.
				System.gc(); // To make sure it does a full gc, call it twice
			}
		} catch (Exception e) {
			getLog().error(e);
		} finally {
			Thread.currentThread().setContextClassLoader(oldContextClassLoader);
		}

	}

	private List<Locale> getLocales() {
		List<Locale> locales = new ArrayList<Locale>();
		if (localeSpecs != null)
			for (LocaleSpec localeSpec : localeSpecs)
				locales.add(localeSpec.getLocale());
		if (locales.isEmpty())
			locales.add(Locale.getDefault());
		return locales;
	}

	/**
	 * Process classes with one ore more screenshot annotated method
	 */
	private void processAnnotadedMethods(Set<Method> annotadedMethods) {
		for (Method method : annotadedMethods) {
			Class<?> declaringClass = method.getDeclaringClass();
			if (Modifier.isAbstract(declaringClass.getModifiers()))
				continue;
			getLog().debug("The method: \"" + method.getName() + "\" is annotated with Screenshot");
			Screenshot annotation = method.getAnnotation(Screenshot.class);
			handleFoundMethod(declaringClass, method);
		}
	}

	protected Object callScreenshotMethod(Class<?> targetClass, Method screenshotMethod) {
		try {
			Object instance = targetClass.newInstance();
			return screenshotMethod.invoke(instance);
		} catch (InstantiationException e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e);
			return null;
		} catch (IllegalAccessException e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e);
			return null;
		} catch (SecurityException e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e);
			return null;
		} catch (IllegalArgumentException e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e);
			return null;
		} catch (InvocationTargetException e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e.getTargetException());
			return null;
		} catch (Exception e) {
			handleExceptionInCalledMethod(targetClass, screenshotMethod, e);
			return null;
		}
	}

	private void handleExceptionInCalledMethod(Class<?> targetClass, Method screenshotMethod, Throwable e) {
		getLog().info(
				"Unable to create screenshot by calling: " + targetClass.getName() + "." + screenshotMethod.getName(),
				e);
	}

	protected void takeScreenShot(JComponent component, File file) {
		writeScreenshot(ripSwingComponent(component), file);
	}

	/**
	 * If on ore more child components in the specified component should be
	 * emphasized the screenshot is drawn with a blurred filter and then the
	 * emphasized components are drawn a second time without any filtering.
	 */
	protected BufferedImage ripSwingComponent(final JComponent component) {
		component.setLocation(0, 0);
		component.setSize(component.getPreferredSize());
		SampleUtil.propagateDoLayout(component);
		Rectangle2D rect = calculateDecoratorBounds(component);
		Rectangle2D dest = new Rectangle2D.Float();
		Rectangle2D.union(rect, component.getBounds(), dest);
		BufferedImage image = new BufferedImage((int) dest.getWidth(), (int) dest.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = createGraphics(image, dest);
		component.setDoubleBuffered(false);
		if (DecoratorUtils.hasEmphasizers(component)) {
			BufferedImage blurBuffer = new BufferedImage(component.getWidth(), component.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D gBlurred = createGraphics(blurBuffer, dest);
			component.print(gBlurred);
			gBlurred.dispose();
			g.drawImage(blurBuffer, new GaussianBlurFilter(5), 0, 0);
			DecoratorUtils.eachEmphasizedComponent(component, new DecoratorUtils.EmphasizedComponentVisitor() {
				@Override
				public void visit(JComponent emphasizedComponent) {
					Point pt = SwingUtilities.convertPoint(emphasizedComponent.getParent(),
							emphasizedComponent.getLocation(), component);
					g.translate(pt.x, pt.y);
					emphasizedComponent.print(g);
					g.translate(-pt.x, -pt.y);
				}
			});
		} else {
			component.print(g);
		}
		DecoratorUtils.decorateScreenshot(component, g);
		g.dispose();
		return (scaleFactor < 1) ? createScaledImage(image, scaleFactor) : image;
	}

	/**
	 * Use {@link Image#getScaledInstance(int, int, int)} even though its known to
	 * be slow since performance is not an issue and we want the best possible
	 * quality. Besides when using
	 * org.jdesktop.swingx.graphics.GraphicsUtilities.createThumbnailFast on a head
	 * less Linux server the following exception was thrown:
	 * 
	 * <pre>
	 * java.lang.IllegalArgumentException: Unknown image type 0
	 * 	java.awt.image.BufferedImage.<init>(BufferedImage.java:490)
	 * 	org.jdesktop.swingx.graphics.GraphicsUtilities.createCompatibleImage(GraphicsUtilities.java:189)
	 * 	org.jdesktop.swingx.graphics.GraphicsUtilities.createThumbnailFast(GraphicsUtilities.java:433)
	 * </pre>
	 */
	private BufferedImage createScaledImage(BufferedImage image, float scale) {
		int width = (int) (image.getWidth() * scale);
		int height = (int) (image.getHeight() * scale);
		Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		if (scaledImage instanceof BufferedImage)
			return (BufferedImage) scaledImage;
		else {
			BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = copy.createGraphics();
			try {
				g2d.drawImage(scaledImage, null, null);
			} finally {
				g2d.dispose();
			}
			return copy;
		}
	}

	private Graphics2D createGraphics(BufferedImage image, Rectangle2D dest) {
		final Graphics2D g = image.createGraphics();
		g.translate(-dest.getX(), -dest.getY());

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g;
	}

	/**
	 * Calculate the smallest box that includes all decorators. The rectangle is
	 * relative the rootComponent so a decorator above or to the left of the
	 * rootComponent will result in negative x or y value
	 */
	private Rectangle2D calculateDecoratorBounds(final JComponent rootComponent) {
		final Area area = new Area();
		DecoratorUtils.eachDecorator(rootComponent, new DecoratorUtils.ScreenshotDecoratorVisitor() {

			@Override
			public void visit(ScreenshotDecorator decorator, JComponent component) {
				area.add(new Area(decorator.getBounds(component, rootComponent)));
			}
		});
		return area.getBounds2D();
	}

	/**
	 * At:
	 * <p>
	 * http://www.mail-archive.com/docbook-apps@lists.oasis-open.org/msg08803.html
	 * </p>
	 * you can read the following: <br>
	 * <p>
	 * "I think the bottom line here is that specifying pixel dimensions (if you
	 * want to display an image "as-is") isn't too useful with FOP since there's no
	 * way to way to tell FOP to ignore the dpi info. So far just setting the dpi to
	 * 96 in the image files themselves seems to be the only way to get them to
	 * display properly. "
	 * </p>
	 * <p>
	 * But when using 96 the images was to large in the final pdf. Tried different
	 * values and 110 is the best so far. TODO: Find out how to derive the value
	 * from the environment instead.
	 * </p>
	 */
	private void writeScreenshot(BufferedImage screenshot, File file) {
		writePngFile(screenshot, file, 110);
	}

	/**
	 * Found at:
	 * http://www.rhinocerus.net/forum/lang-java-programmer/582238-how-do-you-specify-dpi-png-image-file.html
	 */
	// private void writePngFile(RenderedImage image, File file, int dotsPerInch)
	// {
	// String dotsPerMeter = String.valueOf((int) (dotsPerInch / 0.0254));
	//
	// // retrieve list of ImageWriters for png images (most likely only one
	// // but who knows)
	// Iterator<ImageWriter> imageWriters =
	// ImageIO.getImageWritersByFormatName(FORMAT_PNG);
	//
	// // loop through available ImageWriters until one succeeds
	// while (imageWriters.hasNext()) {
	// ImageWriter iw = imageWriters.next();
	//
	// // get default metadata for png files
	// ImageWriteParam iwp = iw.getDefaultWriteParam();
	// IIOMetadata metadata = iw.getDefaultImageMetadata( new
	// ImageTypeSpecifier(image), iwp);
	//
	// // get png specific metatdata tree
	// String pngFormatName = metadata.getNativeMetadataFormatName();
	// IIOMetadataNode pngNode = (IIOMetadataNode)
	// metadata.getAsTree(pngFormatName);
	//
	// // find pHYs node, or create it if it doesn't exist
	// IIOMetadataNode physNode = null;
	// NodeList childNodes = pngNode.getElementsByTagName("pHYs");
	// if (childNodes.getLength() == 0) {
	// physNode = new IIOMetadataNode("pHYs");
	// pngNode.appendChild(physNode);
	// } else if (childNodes.getLength() == 1) {
	// physNode = (IIOMetadataNode) childNodes.item(0);
	// } else {
	// throw new IllegalStateException("Don't know what to do with multiple pHYs
	// nodes");
	// }
	//
	// physNode.setAttribute("pixelsPerUnitXAxis", dotsPerMeter);
	// physNode.setAttribute("pixelsPerUnitYAxis", dotsPerMeter);
	// physNode.setAttribute("unitSpecifier", "meter");
	//
	// try {
	// metadata.setFromTree(pngFormatName, pngNode);
	// IIOImage iioImage = new IIOImage(image, null, metadata);
	// ImageOutputStream ios = ImageIO.createImageOutputStream(file);
	// iw.setOutput(ios);
	// iw.write(iioImage);
	// ios.flush();
	// ios.close();
	// } catch (Exception e) {
	// throw new RuntimeException("Unable to write screenshot to: " +
	// file.getPath());
	// }
	// break;
	// }
	//
	// }

	/**
	 * Decided to use the PngEncoder from
	 * <a href="http://www.jfree.org/jcommon/">JFree JCommons project</a> instead of
	 * the method above. Keep the above method until we know for sure that
	 * everything is working as expected.
	 */
	private void writePngFile(BufferedImage image, File file, int dpi) {
		PngEncoder pngEncoder = new PngEncoder(image, true);
		pngEncoder.setDpi(dpi, dpi);
		byte[] pngbytes;

		try {
			FileOutputStream outfile = new FileOutputStream(file);
			pngbytes = pngEncoder.pngEncode();
			outfile.write(pngbytes);
			outfile.flush();
			outfile.close();
		} catch (IOException e) {
			throw new RuntimeException("Unable to write screenshot to: " + file.getPath());
		}

	}

	private ClassLoader createClassLoader() {
		List<URL> urls = collectURLs();
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
	}

	@SuppressWarnings("unchecked")
	private Class<Screenshot> loadAnnotationClass(String className, ClassLoader classLoader) {
		try {
			return (Class<Screenshot>) classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			getLog().info("No screenshot-maven-plugin dependency found in: " + project.getArtifactId());
			return null;
		}
	}

}
