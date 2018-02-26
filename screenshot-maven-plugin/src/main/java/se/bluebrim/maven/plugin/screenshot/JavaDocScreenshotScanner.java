package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.maven.plugin.AbstractMojo;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;


/**
 * Scans test classes for method annotated with the Screenshot annotation. Calls found methods and save a png-file with the same name as
 * return JComponent subclass. The png is saved in a doc-files directory alongside the source file for the JComponent subclass.
 * 
 * @author G Stack
 *
 */
public class JavaDocScreenshotScanner extends ScreenshotScanner 
{
	private static final String DOC_FILES = "doc-files";
	private File sourceDirectory;
    private boolean updateSrcFiles;
    private String srcFileEncoding;

	public JavaDocScreenshotScanner(AbstractMojo mojo, File testClassesDirectory, File classesDirectory, List<String> testClasspathElements, File sourceDirectory, boolean updateSrcFiles, String srcFileEncoding, List<LocaleSpec> locales) 
	{
		super(mojo, testClassesDirectory, classesDirectory, testClasspathElements, locales);
		this.sourceDirectory = sourceDirectory;
		this.updateSrcFiles = updateSrcFiles;
		this.srcFileEncoding = srcFileEncoding;
	}


	/**	
	 * Use the Javadoc convention to name the screenshot file. See:
	 * <a href="http://java.sun.com/j2se/javadoc/writingdoccomments/#images">Including images in Javadoc</a>
	 */
	@Override
	protected void handleFoundMethod(Class<?> candidateClass, Method method, Screenshot annotation) {
		Object screenshot = callScreenshotMethod(candidateClass, method);
		if (screenshot instanceof JComponent)
		{
			JComponent screenshotComponent = (JComponent)screenshot;
			Class<?> javadocClass = getTargetClass(annotation, screenshotComponent);
			String screenshotName = createScreenshotName(javadocClass, getSceneName(annotation));
			createJavadocScreenshot(screenshotName, screenshotComponent, javadocClass);
			addMissingImageTagToJavadoc(javadocClass, screenshotName);
		} else
			if (screenshot instanceof Collection<?>)
			{
				int index = 0;
				Collection<ScreenshotDescriptor> screenShots = (Collection<ScreenshotDescriptor>) screenshot;
				for (ScreenshotDescriptor screenshotDescriptor : screenShots) {
					Class<?> javadocClass = screenshotDescriptor.getTargetClass();
					String scene = StringUtils.isEmpty(screenshotDescriptor.getScene()) ? "" + index : screenshotDescriptor.getScene();
					createJavadocScreenshot(javadocClass.getSimpleName() + "-" + scene, screenshotDescriptor.getScreenshot(), javadocClass);
				}
			}				
	}

	private void createJavadocScreenshot(String screenshotName, JComponent screenshotComponent, Class<?> javadocClass) 
	{
		File docFilesDirectory = new File(sourceDirectory, org.springframework.util.ClassUtils.classPackageAsResourcePath(javadocClass) + "/" + DOC_FILES);
		docFilesDirectory.mkdirs();
		createScreenshotFile(screenshotComponent, docFilesDirectory, screenshotName);
	}

	/**
	 * Check for missing image tag in the Java doc.
	 * Add missing image tag in class comment: The image tag look like this:
	 * <pre>
	 * &lt;img src="doc-files/ColorConstants.png"&gt;
	 * </pre>
	 * <p>
	 * Nice to have but not needed, so just skip it with a info line in the log, if anything fails.
	 * 
	 */
	private void addMissingImageTagToJavadoc(Class<?> javadocClass, String screenshotName)
	{
		final String srcPath = DOC_FILES + "/" + screenshotName + "." + FORMAT_PNG;
		// TODO: Find a solution that works for inner classes as well.
		File javaFile = new File(sourceDirectory, org.springframework.util.ClassUtils.convertClassNameToResourcePath(javadocClass.getName()) + ".java");
		String classComment = null;
		try {
			classComment = getClassComment(javaFile);
		} catch (ParseException e) {
			getLog().info("Unable to parse source file due to: " + e.getMessage());
			return;
		} catch (FileNotFoundException e)
		{
			getLog().info("Unable to parse source file due to: " + e.getMessage());
			return;
		} catch (IOException e)
		{
			getLog().info("Unable to parse source file due to: " + e.getMessage());
			return;			
		}
		String template = "<img src=\"{0}\" alt=\"Screenshot image of class: {1}\">";

		String screenShotImageTag = MessageFormat.format(template, srcPath, javadocClass.getSimpleName());
		if (StringUtils.isEmpty(classComment))
		{
			getLog().info("Missing \"" + screenShotImageTag + "\" in class: " + javadocClass.getName());
			if (updateSrcFiles)
				addClassCommentWithImageTag(screenShotImageTag, javaFile);
		}
		
		if (!hasImageTag(classComment, srcPath))
		{
			getLog().info("Missing \"" + screenShotImageTag + "\" in class: " + javadocClass.getName());
			if (updateSrcFiles)
				addImageTagToClassComment(screenShotImageTag, javaFile);
		}

	}
	
	/**
	 * Assume that the class comment should be added before the first line containing "public" and "class". 
	 * A somewhat naive approach but the feature is therefore turned off by default i the plugin config.
	 */
	
	private void addClassCommentWithImageTag(String screenShotImageTag, File javaFile) 
	{
		List<String> classComment = new ArrayList<String>();
		classComment.add("/**");
		classComment.add(" * " + screenShotImageTag);
		classComment.add(" */");
		
		try {
			boolean notAddedYet = true;
			List<String> lines = FileUtils.readLines(javaFile, srcFileEncoding);
			List<String> out = new ArrayList<String>();
			for (Object object : lines) {
				String line = (String)object;
				out.add(line);
				if (line.contains("public") && line.contains("class") && notAddedYet)
				{
					int index = out.indexOf(line) - 1;
					while (index >= 0 && out.get(index).contains("@"))
						index--;
					out.addAll(index, classComment);
					notAddedYet = false;
				}

			FileUtils.writeLines(javaFile, srcFileEncoding, out);		
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Assume that the class comment is the first "/**" in the file. A somewhat naive
	 * approach but the feature is therefore turned off by default i the plugin config.
	 */
	private void addImageTagToClassComment(String screenShotImageTag, File javaFile) 
	{
		try {
			boolean notAddedYet = true;
			List<String> lines = FileUtils.readLines(javaFile, srcFileEncoding);
			List<String> out = new ArrayList<String>();
			for (Object object : lines) {
				String line = (String)object;
				out.add(line);
				if (line.contains("/**") && notAddedYet)
				{
					out.add(" * " + screenShotImageTag);
					notAddedYet = false;
				}
			FileUtils.writeLines(javaFile, srcFileEncoding, out);		
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasImageTag(String comment, final String srcPath)
	{
		final MutableBoolean result = new MutableBoolean(false);
		
	    HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
			
			@Override
			public void handleSimpleTag(Tag tag, MutableAttributeSet a, int pos)
			{
				if (tag.equals(Tag.IMG))
				{
					Object attribute = a.getAttribute(HTML.Attribute.SRC);
					if (attribute instanceof String)
					{
						String srcAttribute = (String) attribute;
						if (srcPath.equals(srcAttribute))
							result.setValue(true);
					}
				}
			}
		};
	    try {
			new ParserDelegator().parse(new StringReader(comment), callback, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result.booleanValue();
	}

	private String getClassComment(File javaFile) throws ParseException, FileNotFoundException, IOException
	{
		JavaDocBuilder builder = new JavaDocBuilder();
		builder.addSource(javaFile);
	    JavaSource src = builder.getSources()[0];
	    JavaClass javaClass = src.getClasses()[0];
	    String comment = "<html>" + javaClass.getComment() + "</html>";
		return comment;
	}

}

