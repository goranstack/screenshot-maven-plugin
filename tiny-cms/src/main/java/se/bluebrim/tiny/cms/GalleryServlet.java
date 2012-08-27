package se.bluebrim.tiny.cms;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.text.StrTokenizer;

/**
 * The image Docbook template is based on: http://www.docbook.org/tdg/en/html/imageobject.html
 * 
 * @author Goran Stack
 *
 */
public class GalleryServlet extends AbstractCmsServlet {

	private static final long serialVersionUID = 1L;
	private static final String SVG_TEMPLATE = "<object type='image/svg+xml' data='@url@' width='600'></object>";
	private static final String IMAGE_TEMPLATE = "<img src='@url@'/>";

	
	private String docBookTemplate = 
			"<mediaobject>\n" +
			"   <imageobject>\n" +
			"      <imagedata fileref=\"@fileName@\"\n" +
			"       format=\"@format@\" scalefit=\"1\" width=\"100%\" contentdepth=\"100%\"/>\n" +
			"   </imageobject>\n" +
			"</mediaobject> ";
	
	
	
	private String imagesBaseUrl;
	private String[] sideBySideMasks;

	@Override
	public void init() throws ServletException {
		prepareDocBookTemplate();
		imagesBaseUrl = getInitParameter("images-base-url");
		if (!imagesBaseUrl.endsWith("/"))
			imagesBaseUrl = imagesBaseUrl + "/";
		sideBySideMasks = StrTokenizer.getCSVInstance((getInitParameter("side-by-side-masks"))).getTokenArray();
	}

	private void prepareDocBookTemplate() {
		docBookTemplate = docBookTemplate.replace("<", "&lt;");
		docBookTemplate = docBookTemplate.replace(">", "&gt;");
		docBookTemplate = docBookTemplate.replace("\n", "<br />");		
	}


	@Override
	/**
	 * The image path can be a directory or a single image file.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {

		String imageDirPath = getUploadDirPath();
		String pathInfo = req.getPathInfo();
		File imagePath = pathInfo != null ? new File(imageDirPath, pathInfo) : new File(imageDirPath);
		if (imagePath.exists())
		{
			response.setContentType("text/html");
			PrintWriter output = response.getWriter();
			if (imagePath.isDirectory())
				createGalleryPage(req, pathInfo, imagePath, output);
			else
				createSingleImagePage(pathInfo, output);
		} else
			throw new RuntimeException("Image directory/file: \"" + imageDirPath + "\" does not exist");

	}

	private void createSingleImagePage(String pathInfo, PrintWriter output) 
	{
		output.println("<html>");
		inlineImage(pathInfo, output);		
		output.println("</html>");
	}

	private void createGalleryPage(HttpServletRequest req, String pathInfo,
			File imagePath, PrintWriter output) {
		File[] subDirectories = imagePath.listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		
		
		File[] files = imagePath.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				String lowerCaseName = name.toLowerCase();
				return lowerCaseName.endsWith("png") || lowerCaseName.endsWith("svg");
			}
		});
		output.println("<html>");
		output.println("<head>");
		output.println("<title>T2 Screenshot Gallery</title>");
		output.println("<link rel='stylesheet' href='"  + req.getContextPath() +  "/gallery.css'>");
		output.println("</head>");
		output.println("<body>");
		
		output.println("<table>");
		
		if (subDirectories.length > 0)
			output.println("<tr><td colspan='99'><p class='subdirectoryTitle'>Subdirectories</p></td></tr>");

		for (File subDirectory : subDirectories) {
			output.println("<tr>");
			output.println("<td colspan='99'>");				
			String subDirectoryName = subDirectory.getName();
			output.println("<a href='" + getUrl3(req) + "/" + subDirectoryName + "'>" + subDirectoryName + "</a>");
			output.println("</tr>");
			output.println("</td>");
		}

		
		Collection<List<File>> rows = createGalleryRows(files);
		
		for (List<File> columns : rows) {
			Collections.sort(columns);
			output.println("<tr>");
			for (File column : columns) {
				output.println("<td>");
				printImageBlock(pathInfo, output, column);
				output.println("</td>");
			}
			output.println("</tr>");
		}			
		output.println("</table>");

		output.println("</body>");
		output.println("</html>");
	}

	private void printImageBlock(String pathInfo, PrintWriter output, File file) {
		String subdirPath = "";
		if (pathInfo != null)
		{
			subdirPath = pathInfo;
			subdirPath = subdirPath.replaceFirst("/", ""); // Remove the slash at the beginning
			if (!subdirPath.isEmpty()) 
				subdirPath = subdirPath + "/";
		}
		String relativeFilePath = FilenameUtils.normalizeNoEndSeparator(subdirPath + file.getName());
		relativeFilePath = FilenameUtils.separatorsToUnix(relativeFilePath);
		output.println(createScreenshotBlock(relativeFilePath));
	}
	
	private static String getUrl3(HttpServletRequest req) 
	{
	    String scheme = req.getScheme();             
	    String serverName = req.getServerName();    
	    int serverPort = req.getServerPort();       
	    String contextPath = req.getContextPath();  
	    String servletPath = req.getServletPath();  
	    String pathInfo = req.getPathInfo();         
	    String queryString = req.getQueryString();        

	    // Reconstruct original requesting URL
	    String url = scheme+"://"+serverName+":"+serverPort+contextPath+servletPath;
	    if (pathInfo != null) {
	        url += pathInfo;
	    }
	    if (queryString != null) {
	        url += "?"+queryString;
	    }
	    return url;
	}
	
	private String createScreenshotBlock(String filename)
	{
		StringWriter writer = new StringWriter();
		PrintWriter output = new PrintWriter(writer);
		inlineImage(filename, output);		
		output.println("<p class='filename'>" + filename + "</p>");
		output.println("<pre class='docbook'>" + docBookTemplate.replace("@fileName@", filename).replace("@format@", FilenameUtils.getExtension(filename)) + "</pre>");
		return writer.getBuffer().toString();
	}

	private void inlineImage(String filename, PrintWriter output) {
		if (filename.toLowerCase().endsWith(".svg"))
			output.println(SVG_TEMPLATE.replace("@url@", imagesBaseUrl + filename) + "<br />");
		else			
			output.println(IMAGE_TEMPLATE.replace("@url@", imagesBaseUrl + filename) + "<br />");
	}
	
	/**
	 * Collect images files that should be presented side by side on the same row.
	 */
	private Collection<List<File>> createGalleryRows(File[] files)
	{
		Map<String,  List<File>> rows = new HashMap<String,  List<File>>();
		for (File file : files) {
			String key = mask(file.getName());
			if (!rows.containsKey(key))
			{
				List<File> fileList = new ArrayList<File>();
				fileList.add(file);
				rows.put(key, fileList);
			} else
			{
				rows.get(key).add(file);
			}
		}
		return rows.values();
	}
	
	private String mask(String fileName)
	{
		String result = fileName;
		for (String mask : sideBySideMasks)
		{
			result = result.replace(mask, "");
		}
		return result;
	}	

}
