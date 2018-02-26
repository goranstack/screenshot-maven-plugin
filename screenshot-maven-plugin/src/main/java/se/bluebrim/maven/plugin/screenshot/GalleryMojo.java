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
 * Generates a screenshots gallery report. 
 * 
 * @author G Stack
 *
 */
@Mojo( name = "gallery", requiresDependencyResolution = ResolutionScope.TEST )
public class GalleryMojo extends AbstractMojo
{

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
	
	
    /**
     * Directory where the generated AsciiDoc will go.
     *
     */
	@Parameter ( defaultValue = "${project.build.directory}", required = true )
    private String outputDirectory;

	@Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    
    /**
     * Specify the locale to be used when generating the screenshots. In a previous version it was possible to specify
     * several locales and get side-by-side screenshots for each locale. This feature is disabled for now but you can still
     * use this parameter to specify one single locale in cases where the default locale is not desirable.
     */
 	@Parameter (required = false)
    private List<LocaleSpec> locales;

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		getLog().info("Screenshot gallery executed. The output directory is: " + outputDirectory, null);
		GalleryScreenshotScanner screenshotScanner = new GalleryScreenshotScanner(this, project, testClassesDirectory, classesDirectory, testClasspathElements, outputDirectory, locales);
		screenshotScanner.setProject(project);
		try {
			screenshotScanner.execute();
		} catch (NoClassDefFoundError e) {
			getLog().error("Unable to find class: " + e.getMessage() + " in the class path of: " + project.getArtifactId());
		}
		screenshotScanner.close();		
	}

	
}
