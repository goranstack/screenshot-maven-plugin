package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.ParserDelegator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;


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
     * If true, the Java source files of the screenshot target classes will be checked
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
	 * All projects in the Maven reactor. Used to locate source files of
	 * {@code @Screenshot} target classes that belong to a different Maven module
	 * than the test class containing the screenshot method.
	 */
	@Parameter( defaultValue = "${reactorProjects}", readonly = true, required = true )
	private List<MavenProject> reactorProjects;

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
     * Specify the locale to be used when generating the screenshots. In a previous version it was possible to specify
     * several locales and get side-by-side screenshots for each locale. This feature is disabled for now but you can still
     * use this parameter to specify one single locale in cases where the default locale is not desirable.
     */
 	@Parameter  (required = false)
    private List<LocaleSpec> locales;
 	

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Generate screenshot executed. The source directory is: " + sourceDirectory.getPath(), null);
		List<File> allSourceDirectories = collectSourceDirectories();
		getLog().info("Searching " + allSourceDirectories.size() + " source directories for target class source files.");
		JavaDocScreenshotScanner screenshotScanner = new JavaDocScreenshotScanner(this, testClassesDirectory, classesDirectory, testClasspathElements, sourceDirectory, allSourceDirectories, updateSrcFiles, srcFileEncoding, locales);
		if (javaDocImageScale > 0 && javaDocImageScale <= 1)
			screenshotScanner.setScaleFactor(javaDocImageScale);
		else
			getLog().error("The \"imageScale\" parameter must be > 0 and <= 1");
		screenshotScanner.execute();
	}

	/**
	 * Collects source directories to search when resolving the source file for a
	 * {@code @Screenshot} targetClass.
	 * <p>
	 * Two complementary strategies are used so the plugin works correctly both
	 * when invoked from the multi-module root (where {@code reactorProjects}
	 * contains all modules) and when invoked directly on a single module (where
	 * {@code reactorProjects} only contains that one module):
	 * <ol>
	 *   <li>Add every source directory reported by the Maven reactor.</li>
	 *   <li>Scan sibling directories on the file system: {@code sourceDirectory}
	 *       is typically {@code <module>/src/main/java}; four levels up is the
	 *       parent directory that contains all sibling modules, so every
	 *       {@code <sibling>/src/main/java} that exists is added as well.</li>
	 * </ol>
	 */
	private List<File> collectSourceDirectories() {
		List<File> dirs = new ArrayList<File>();

		// Strategy 1: reactor projects (populated when invoked from the root)
		if (reactorProjects != null) {
			for (MavenProject p : reactorProjects) {
				File dir = new File(p.getBuild().getSourceDirectory());
				if (dir.isDirectory() && !dirs.contains(dir))
					dirs.add(dir);
			}
		}

		// Strategy 2: filesystem scan of sibling modules.
		// sourceDirectory = <module>/src/main/java  →  4 levels up = <modules-parent>
		File modulesParentDir = sourceDirectory.getParentFile();
		for (int i = 0; i < 3 && modulesParentDir != null; i++)
			modulesParentDir = modulesParentDir.getParentFile();

		if (modulesParentDir != null && modulesParentDir.isDirectory()) {
			File[] siblings = modulesParentDir.listFiles();
			if (siblings != null) {
				for (File sibling : siblings) {
					if (!sibling.isDirectory())
						continue;
					File srcDir = new File(sibling, "src/main/java");
					if (srcDir.isDirectory() && !dirs.contains(srcDir))
						dirs.add(srcDir);
				}
			}
		}

		// Always include the current module's source directory
		if (!dirs.contains(sourceDirectory))
			dirs.add(0, sourceDirectory);
		return dirs;
	}
		
}
