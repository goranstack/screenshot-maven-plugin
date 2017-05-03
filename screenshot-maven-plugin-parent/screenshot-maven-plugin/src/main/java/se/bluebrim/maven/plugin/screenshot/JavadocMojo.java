package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Mojo that generates screen shots to be included in the Javadoc for the panel class that is the source of the screenshot. 
 * 
 * @goal javadoc
 * @requiresDependencyResolution test
 * 
 * @author G Stack
 * 
 */
public class JavadocMojo extends AbstractMojo
{

	/**
     * Javadoc screenshot scale factor. Used to reduce the size of the screenshots to get
     * a more pleasant experience when viewing Javadoc in your IDE. 
     * 0.6 is a recommended value.<br>
     * Must be > 0.0 and =< 1.0
     *
     * @parameter default-value="1"
     */
    private float javaDocImageScale = 1f;

    /**
     * If true, the Java source files of the screen shot target classes will be checked
     * for missing &lt;img src="doc-files/xxxxxxx.png"&gt; tags and missing tags
     * will be added. The implementation of this feature are experimental and somewhat naive
     * so use this with care. The parsing of source files are done with <a href="http://qdox.codehaus.org">QDoc</a>
     * and parsing of Javadoc comments are done with {@link ParserDelegator}. Adding of missing comments and
     * image tags are done by looping through the lines and trying to find the insertion point by simple string
     * comparision. This is the naive part that should be improved. 
     * 
     * @parameter default-value="false"
     */
    private boolean updateSrcFiles = false;

    /**
     * The encoding of the Java source files to be used when the parameter {@code updateSrcFiles = true}
     * If not specified the default encoding of the platform will be used.
     * 
     * @parameter
     */
    private String srcFileEncoding;
	/**
	 * Directory containing the Java source code
	 * 
	 * @parameter expression="project.build.sourceDirectory"
	 * @required
	 */
	protected File sourceDirectory;

	/**
	 * The directory containing generated test classes of the project.
	 * 
	 * @parameter expression="project.build.testOutputDirectory"
	 */
	protected File testClassesDirectory;
	
	/**
	 * The directory containing generated classes of the project.
	 * 
	 * @parameter expression="project.build.outputDirectory"
	 */
	protected File classesDirectory;
	
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
		getLog().info("Generate screenshot executed. The source directory is: " + sourceDirectory.getPath(), null);
		JavaDocScreenshotScanner screenshotScanner = new JavaDocScreenshotScanner(this, testClassesDirectory, classesDirectory, testClasspathElements, sourceDirectory, updateSrcFiles, srcFileEncoding);
		if (javaDocImageScale > 0 && javaDocImageScale <= 1)
			screenshotScanner.setScaleFactor(javaDocImageScale);
		else
			getLog().error("The \"imageScale\" parameter must be > 0 and <= 1");
		screenshotScanner.annotationScan();
	}
		
}
