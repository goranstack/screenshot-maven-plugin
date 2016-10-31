package se.bluebrim.tiny.cms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.oreilly.servlet.MultipartRequest;


/**
 * http://forums.devshed.com/java-help-9/how-to-write-a-servlet-to-read-file-send-by-412124.html
 * 
 * @author Goran Stack
 *
 */
public class UploadServlet extends AbstractCmsServlet {

	private static final long serialVersionUID = 1L;

	private String uploadDir;
	
	@Override
	public void init() throws ServletException {
		uploadDir = getUploadDirPath();
		createDirIfMissing(new File(uploadDir));
	}
	
	private void createDirIfMissing(File directory)
	{
		try {
			directory.mkdirs();
		} catch (SecurityException e) {
			throw new RuntimeException("Not privileged to create directory: \"" + directory + "\"");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException 
	{	
		String toDir = request.getParameter("toDir");
		String saveDirectoryPath = uploadDir;
		if (StringUtils.isNotBlank(toDir))
		{			
			File saveDirectory = new File(uploadDir, toDir);
			createDirIfMissing(saveDirectory);
			saveDirectoryPath = saveDirectory.getPath();
		}
		MultipartRequest multipart = new MultipartRequest(request, saveDirectoryPath, 10000000);
	
		Enumeration fileNames = multipart.getFileNames();
		while (fileNames.hasMoreElements()) {
			
			String fileName = (String) fileNames.nextElement();
			response.setContentType("text/html");
			PrintWriter output = response.getWriter();
			output.println("Received an uploaded file: " + fileName + ". Saving it to directory: " + saveDirectoryPath + "<br>");			
		}
	
	}

}