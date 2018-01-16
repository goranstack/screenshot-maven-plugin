package se.bluebrim.maven.plugin.screenshot;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


/**
 * Remove all uploaded screenshots from the content management system. 
 * 
 * @author G Stack
 * 
 */
@Mojo( name = "clean" )
public class CleanMojo extends AbstractMojo
{
    
    /**
     * The content management URL that cleans the screenshots directory
     * 
     */
	@Parameter ( required = true )
    private String cleanUrl;


	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Clean screenshots executed");
		GetMethod method = new GetMethod(cleanUrl);
		HttpClient client = new HttpClient();
		try {
		int status = client.executeMethod(method);
		getLog().debug("HTTP status: " + status + " "+ HttpStatus.getStatusText(status));
		getLog().info(method.getResponseBodyAsString());
		} catch (HttpException e) {
			getLog().error(e);
		} catch (IOException e) {
			getLog().error(e);
		}
	}
		
}
