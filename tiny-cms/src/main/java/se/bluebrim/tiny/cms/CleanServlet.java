package se.bluebrim.tiny.cms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;


/**
 * Remove all files from the upload directory. Invoked from the clean goal of the Maven screenshot plugin
 * 
 * @author Goran Stack
 *
 */
public class CleanServlet extends AbstractCmsServlet {

	private static final long serialVersionUID = 1L;

	private File uploadDir;
	
	@Override
	public void init() throws ServletException {
		uploadDir = new File(getUploadDirPath());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		
		int numberOfFiles = uploadDir.list().length;
		FileUtils.cleanDirectory(uploadDir);
		response.setContentType("text/html");
		PrintWriter output = response.getWriter();
		output.println("Removed " + numberOfFiles + " files in: \"" + uploadDir + "\"");		
	}
}