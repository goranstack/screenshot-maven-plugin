package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;


/**
 * Mojo that generates screen shots and uploads them to a CMS. 
 * 
 * 
 * @author G Stack
 * 
 */
@Mojo( name = "upload", requiresDependencyResolution = ResolutionScope.TEST )
public class UploadMojo extends AbstractMojo
{

	/**
	 * The directory containing generated test classes of the project.
	 * 
	 */
	@Parameter( defaultValue = "${project.build.testOutputDirectory}", readonly = true )
	protected File testClassesDirectory;
	
	/**
	 * The directory containing generated classes of the project.
	 * 
	 */
	@Parameter( defaultValue = "${project.build.outputDirectory}", readonly = true )
	protected File classesDirectory;
	
    /**
     * The classpath elements of the project being tested.
     *
     */
	@Parameter( defaultValue = "${project.testClasspathElements}", readonly = true )
    private ArrayList<String> testClasspathElements;
    
 	@Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;

 	/**
     * The content management base URL that is appended with the screenshot file name and 
     * used in the POST request.
     * 
     */
 	@Parameter ( required = true )
    private String uploadBaseUrl;


    /**
     * A screenshot will be created for each Locale where the file name is appended with the
     * Locale as string.
     * 
     */
 	@Parameter
    private List<LocaleSpec> locales;

        
	public void execute() throws MojoExecutionException, MojoFailureException
	{

		getLog().info("Upload screenshots executed");
		UploadScreenshotScanner screenshotScanner = new UploadScreenshotScanner(this, testClassesDirectory, classesDirectory, testClasspathElements, uploadBaseUrl, locales);
		screenshotScanner.setProject(project);

		screenshotScanner.annotationScan();
	}
		
}
