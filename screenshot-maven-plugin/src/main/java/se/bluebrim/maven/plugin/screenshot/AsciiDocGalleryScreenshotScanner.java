package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JComponent;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Scans test classes for method annotated with the Screenshot annotation. Calls found methods and save a png-file with the same name as
 * return JComponent subclass. The png is saved in a doc-files directory alongside the source file for the JComponent subclass.
 * 
 * @author G Stack
 *
 */
public class AsciiDocGalleryScreenshotScanner extends ScreenshotScanner 
{
	private File outputDirectory;
	private File imagesOutputDirectory;
	private MavenProject project;

	public AsciiDocGalleryScreenshotScanner(AbstractMojo mojo, MavenProject project, File testClassesDirectory, File classesDirectory, List<String> testClasspathElements, int maxWidth, String outputDirectory, String sourceCodeURL, List<LocaleSpec> locales) 
	{
		super(mojo, testClassesDirectory, classesDirectory, testClasspathElements, locales);
		this.project = project;
		this.outputDirectory = new File(outputDirectory);
		this.imagesOutputDirectory = new File(outputDirectory, project.getArtifactId());
		this.outputDirectory.mkdirs();
		this.imagesOutputDirectory.mkdirs();
	}

	protected void handleFoundMethod(Class candidateClass, Method method) 
	{
		Object screenshot = callScreenshotMethod(candidateClass, method);
		if (screenshot instanceof JComponent)
		{
			JComponent screenshotComponent = (JComponent)screenshot;
			Class screenshotClass = getTargetClass(method, screenshotComponent);
			File file = createScreenshotFile(screenshotComponent, screenshotClass, imagesOutputDirectory, method);
			appendAsciiDoc(screenshotClass, file);
		}
	}
	
	private void appendAsciiDoc(Class screenshotClass, File screenshotFile) {
		
		String template = ".{0}\n"
				+ "image::{1}[]\n";
		
		String imageFile = project.getArtifactId() + "/" + screenshotFile.getName();
		String data = MessageFormat.format(template, screenshotClass.getName(), imageFile);
		try {
			File outputFile = new File(outputDirectory, "gallery.adoc");
			getLog().info("Writing AsciDoc to: " + outputFile.getPath());
			FileUtils.writeStringToFile(outputFile, data, Charset.defaultCharset(), true);
		} catch (IOException e) {
			getLog().info("Error when writing AsciiDoc to file", e);
		}
	}
	
	protected Log getLog() 
	{
		return mojo.getLog();
	}
		
	public void close()
	{
	}

}
