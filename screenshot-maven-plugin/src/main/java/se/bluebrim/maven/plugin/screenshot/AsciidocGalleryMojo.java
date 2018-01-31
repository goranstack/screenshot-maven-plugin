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
@Mojo( name = "asciidoc-gallery", requiresDependencyResolution = ResolutionScope.TEST )
public class AsciidocGalleryMojo extends AbstractMojo
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
     * URL to the source code used to build the source code links under each screenshot image in the report
     *
     */
	@Parameter ( required = true )
	private String sourceCodeURL;
	
	/**
     * Maximum screenshot width
     *
     */
	@Parameter ( defaultValue = "600" )
	private int maxWidth;
	
    /**
     * Directory where the generated AsciiDoc will go.
     *
     */
	@Parameter ( defaultValue = "${project.build.directory}", required = true )
    private String outputDirectory;

	@Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    
	@Parameter( defaultValue = "${rectorProjects}", readonly = true )
    private ArrayList reactorProjects;


    /**
     * A screenshot will be created for each Locale where the file name is appended with the
     * Locale as string.
     * 
     */
 	@Parameter
    private List<LocaleSpec> locales;

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		getLog().info("Screenshot gallery executed. The report directory is: " + outputDirectory, null);
		AsciiDocGalleryScreenshotScanner screenshotScanner = new AsciiDocGalleryScreenshotScanner(this, project, testClassesDirectory, classesDirectory, testClasspathElements, maxWidth, outputDirectory, sourceCodeURL, locales);
		screenshotScanner.setProject(project);
		try {
			screenshotScanner.annotationScan();
		} catch (NoClassDefFoundError e) {
			getLog().error("Unable to find class: " + e.getMessage() + " in the class path of: " + project.getArtifactId());
		}
		screenshotScanner.close();		
	}

	
}
