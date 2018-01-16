package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;

import javax.swing.text.html.parser.ParserDelegator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * Generates screenshots to be included in the Javadoc for the panel class that is the source of the screenshot. 
 * 
 * 
 * @author G Stack
 * 
 */
@Mojo( name = "javadoc", requiresDependencyResolution = ResolutionScope.TEST )
public class JavadocMojo extends AbstractMojo
{

	/**
     * Javadoc screenshot scale factor. Used to reduce the size of the screenshots to get
     * a more pleasant experience when viewing Javadoc in your IDE. 
     * 0.6 is a recommended value.<br>
     * Must be > 0.0 and =< 1.0
     *
     */
	@Parameter ( defaultValue = "1" )
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
     */
	@Parameter ( defaultValue = "false" )
    private boolean updateSrcFiles = false;

    /**
     * The encoding of the Java source files to be used when the parameter {@code updateSrcFiles = true}
     * If not specified the default encoding of the platform will be used.
     * 
     */
	@Parameter
    private String srcFileEncoding;
    
	/**
	 * Directory containing the Java source code
	 * 
	 */
	@Parameter( defaultValue = "${project.build.sourceDirectory}", readonly = true )
	protected File sourceDirectory;

	/**
	 * The directory containing compiled test classes of the project.
	 * 
	 */
	@Parameter( defaultValue = "${project.build.testOutputDirectory}", readonly = true )
	protected File testClassesDirectory;
	
	/**
	 * The directory containing compiled classes of the project.
	 * 
	 */
	@Parameter( defaultValue = "${project.build.outputDirectory}", readonly = true )
	protected File classesDirectory;
	
	/**
     * The classpath elements of the project being processed.
     *
     */
	@Parameter( defaultValue = "${project.testClasspathElements}", readonly = true, required = true )
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
