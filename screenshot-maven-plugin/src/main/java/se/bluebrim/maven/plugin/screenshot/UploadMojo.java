package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;


/**
 * Mojo that generates screen shots and uploads them to a CMS. 
 * 
 * @goal upload
 * @requiresDependencyResolution test
 * 
 * @author G Stack
 * 
 */
public class UploadMojo extends AbstractMojo
{

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
    
    /**
     * @parameter default-value="project"
     * @required
     * @readonly
     */
    private MavenProject project;

 	/**
     * The content management base URL that is appended with the screenshot file name and 
     * used in the POST request.
     * 
     * @parameter
     * @required
     */
    private String uploadBaseUrl;


    /**
     * A screenshot will be created for each Locale where the file name is appended with the
     * Locale as string.
     * 
     * @parameter
     */
    private List<LocaleSpec> locales;

        
	public void execute() throws MojoExecutionException, MojoFailureException
	{

		getLog().info("Upload screenshots executed");
		UploadScreenshotScanner screenshotScanner = new UploadScreenshotScanner(this, testClassesDirectory, classesDirectory, testClasspathElements, uploadBaseUrl, locales);
		screenshotScanner.setProject(project);

		screenshotScanner.annotationScan();
	}
		
}
