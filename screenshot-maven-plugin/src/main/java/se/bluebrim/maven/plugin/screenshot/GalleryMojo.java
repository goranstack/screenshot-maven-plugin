package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;



/**
 * Generates a screenshots gallery report. 
 * 
 * @author G Stack
 *
 */
@Mojo( name = "gallery", defaultPhase = LifecyclePhase.SITE, requiresDependencyResolution = ResolutionScope.TEST )
public class GalleryMojo extends AbstractMavenReport
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
     * Directory where reports will go.
     *
     */
	@Parameter( defaultValue = "${project.reporting.outputDirectory}", readonly = true, required = true )
    private String outputDirectory;

	@Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
    
	@Parameter( defaultValue = "${rectorProjects}", readonly = true )
    private ArrayList reactorProjects;

	@Component
    private Renderer siteRenderer;

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		getLog().info("Screenshot gallery executed. The report directory is: " + outputDirectory, null);
		GalleryScreenshotScanner screenshotScanner = new GalleryScreenshotScanner(this, project, testClassesDirectory, classesDirectory, testClasspathElements, maxWidth, outputDirectory, sourceCodeURL);
		screenshotScanner.setProject(project);
		try {
			screenshotScanner.annotationScan();
		} catch (NoClassDefFoundError e) {
			getLog().error("Unable to find class: " + e.getMessage() + " in the class path of: " + project.getArtifactId());
		}
		screenshotScanner.close();		
	}

	protected MavenProject getProject()
	{
	    return project;
	}

	protected String getOutputDirectory()
	{
	    return outputDirectory;
	}

	protected Renderer getSiteRenderer()
	{
	    return siteRenderer;
	}

	public String getDescription( Locale locale )
	{
	    return getBundle( locale ).getString( "report.gallery.description" );
	}

	public String getName( Locale locale )
	{
	    return getBundle( locale ).getString( "report.gallery.name" );
	}

	public String getOutputName()
	{
	    return "screenshot-gallery";
	}

	private ResourceBundle getBundle( Locale locale )
	{
	    return ResourceBundle.getBundle( "screenshot-gallery", locale, this.getClass().getClassLoader() );
	}

	
}
