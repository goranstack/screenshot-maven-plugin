package se.bluebrim.maven.plugin.screenshot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.maven.plugin.AbstractMojo;

/**
 * Used to upload the screenshots to a CMS
 * 
 * @author Goran Stack
 *
 */
public class UploadScreenshotScanner extends ScreenshotScanner {

	private String baseUrl;
	private List<LocaleSpec> localeSpecs;
	
	public UploadScreenshotScanner(AbstractMojo mojo,
			File testClassesDirectory, File classesDirectory,
			List<String> testClasspathElements,
			String baseUrl,
			List<LocaleSpec> locales) {
		super(mojo, testClassesDirectory, classesDirectory, testClasspathElements);
		this.baseUrl = baseUrl;
		this.localeSpecs = locales;
	}
	
	@Override
	protected List<Locale> getLocales() 
	{
		List<Locale> locales = new ArrayList<Locale>();
		if (localeSpecs != null)
			for (LocaleSpec localeSpec : localeSpecs) 
				locales.add(localeSpec.getLocale());
		if (locales.isEmpty())
			locales.add(Locale.getDefault());
		return locales;
	}

	@Override
	protected void handleFoundMethod(Class candidateClass, Method method) 
	{
		uploadScreenshot(candidateClass, method, isOneForEachLocale(method));
	}

	private void uploadScreenshot(Class candidateClass, Method method, boolean oneForEachLocale) 
	{
		Object screenshot = callScreenshotMethod(candidateClass, method);
		if (screenshot instanceof JComponent)
		{
			JComponent screenshotComponent = (JComponent)screenshot;
			Class screenshotClass = getTargetClass(method, screenshotComponent);
			String screenshotName = createScreenshotName(screenshotClass, method, oneForEachLocale);
			File tempFile = createTempFile(screenshotName, "." + FORMAT_PNG, null);
			takeScreenShot(screenshotComponent, tempFile);
			try {
				uploadFile(tempFile, screenshotName + "." + FORMAT_PNG);
			} catch (HttpException e) {
				getLog().error(e);
			} catch (IOException e) {
				getLog().error(e);
			}
		}
	}
	
	/**
	 * Found at: http://forums.devshed.com/java-help-9/how-to-write-a-servlet-to-read-file-send-by-412124.html
	 */
	private void uploadFile(File file, String fileName) throws HttpException, IOException
	{
	      PostMethod post = new PostMethod(baseUrl);
	      Part[] parts = {new FilePart(fileName, fileName, file)};
	      post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
	      HttpClient client = new HttpClient();
	      int status = client.executeMethod(post);
	      getLog().debug("HTTP status: " + status + " " + HttpStatus.getStatusText(status));
	      getLog().info(post.getResponseBodyAsString());
	   }

}
