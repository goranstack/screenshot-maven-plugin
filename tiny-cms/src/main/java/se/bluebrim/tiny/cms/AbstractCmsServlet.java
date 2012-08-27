package se.bluebrim.tiny.cms;

import javax.servlet.http.HttpServlet;

/**
 * 
 * @author Goran Stack
 *
 */
public abstract class AbstractCmsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String CMS_DIRECTORY = "tiny-cms-content";

	protected String getUploadDirPath()
	{
		return System.getProperty("user.home") + "/" + CMS_DIRECTORY;
	}
}
