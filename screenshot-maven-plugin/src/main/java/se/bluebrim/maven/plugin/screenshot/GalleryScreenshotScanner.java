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
public class GalleryScreenshotScanner extends ScreenshotScanner 
{
	private File imagesOutputDirectory;
	private MavenProject project;
	private File asciiDoc;

	public GalleryScreenshotScanner(AbstractMojo mojo, MavenProject project, File testClassesDirectory, File classesDirectory, List<String> testClasspathElements, int maxWidth, String outputDirectory, String sourceCodeURL, List<LocaleSpec> locales) 
	{
		super(mojo, testClassesDirectory, classesDirectory, testClasspathElements, locales);
		this.project = project;
		this.imagesOutputDirectory = new File(outputDirectory, project.getArtifactId());
		asciiDoc = new File(outputDirectory, "gallery.adoc");
	}
	
	@Override
	protected void processModule() {
		imagesOutputDirectory.mkdirs();
		appendHeaderAsciiDoc();
		appendModuleAsciiDoc();
	}

	private void appendHeaderAsciiDoc() {
		if (asciiDoc.exists()) return;
		String template = "= Screenshot Gallery\n"
				+ ":toc: left\n" + 
				":icons: font\n" + 
				":sectnums:\n";
		String data = MessageFormat.format(template, project.getArtifactId());
		appendAsciiDoc(data);		
	}

	protected void handleFoundMethod(Class candidateClass, Method method) 
	{
		Object screenshot = callScreenshotMethod(candidateClass, method);
		if (screenshot instanceof JComponent)
		{
			JComponent screenshotComponent = (JComponent)screenshot;
			Class screenshotClass = getTargetClass(method, screenshotComponent);
			File file = createScreenshotFile(screenshotComponent, screenshotClass, imagesOutputDirectory, method);
			appendScreenshotAsciiDoc(screenshotClass, file);
		}
	}
	
	private void appendModuleAsciiDoc() {
		String template = "\n== {0}\n"
				+ "{1}\n\n";
		String data = MessageFormat.format(template, project.getArtifactId(), project.getDescription());
		appendAsciiDoc(data);
	}

	private void appendAsciiDoc(String data) {
		try {
			getLog().info("Writing AsciDoc to: " + asciiDoc.getPath());
			FileUtils.writeStringToFile(asciiDoc, data, Charset.defaultCharset(), true);
		} catch (IOException e) {
			getLog().info("Error when writing AsciiDoc to file", e);
		}
	}
	
	private void appendScreenshotAsciiDoc(Class screenshotClass, File screenshotFile) {
		
		String template = ".{0}\n"
				+ "image::{1}[]\n";
		
		String imageFile = project.getArtifactId() + "/" + screenshotFile.getName();
		String data = MessageFormat.format(template, screenshotClass.getName(), imageFile);
		appendAsciiDoc(data);
	}
	
	protected File createScreenshotFile(JComponent screenShotComponent, File dir, String screenshotName) {
		File file = new File(dir.getPath(), screenshotName + "." + FORMAT_PNG);
		takeScreenShot(screenShotComponent, file);
		return file;
	}

	
	protected Log getLog() 
	{
		return mojo.getLog();
	}
		
	public void close()
	{
	}

}
