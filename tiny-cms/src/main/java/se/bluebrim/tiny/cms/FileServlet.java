package se.bluebrim.tiny.cms;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;


/**
 * Servlet that reads the requested file from the file system and
 * writes the file to the response stream. If the file is an image
 * the image is scaled down to the optional maxWidth parameter.
 * 
 * @author Goran Stack
 *
 */
public class FileServlet extends AbstractCmsServlet {

	private static final long serialVersionUID = 1L;

	private String filePath;

    public void init() throws ServletException 
    {
        this.filePath = getUploadDirPath();      
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String requestedFile = request.getPathInfo();

        if (requestedFile == null) {
        	throw new RuntimeException("Unable to find the requested file: \"" + requestedFile + "\"");
        }

        File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));

        if (!file.exists()) {
        	throw new RuntimeException("Unable to find the requested file: \"" + file.getAbsolutePath() + "\"");
        }

        String fileName = file.getName();
		String contentType = getServletContext().getMimeType(fileName);
        response.setContentType(contentType);
        BufferedImage image = ImageIO.read(file);
        if (image != null)
        {
        	image = createScaledImagedIfNeeded(image, request.getParameter("maxWidth"));       
        	ImageIO.write(image, FilenameUtils.getExtension(fileName), response.getOutputStream());
        } else
        {
        	IOUtils.copy(new FileReader(file),  response.getOutputStream());
        }
     }
    
	/**
	 * Use {@link Image#getScaledInstance(int, int, int)} even though its known
	 * to be slow since performance is not an issue and we want the best possible
	 * quality. Besides when using
	 * org.jdesktop.swingx.graphics.GraphicsUtilities.createThumbnailFast on a
	 * head less Linux server the following exception was thrown:
	 * 
	 * <pre>
	 * java.lang.IllegalArgumentException: Unknown image type 0
	 * 	java.awt.image.BufferedImage.<init>(BufferedImage.java:490)
	 * 	org.jdesktop.swingx.graphics.GraphicsUtilities.createCompatibleImage(GraphicsUtilities.java:189)
	 * 	org.jdesktop.swingx.graphics.GraphicsUtilities.createThumbnailFast(GraphicsUtilities.java:433)
	 * </pre>
	 */
    private BufferedImage createScaledImagedIfNeeded(BufferedImage image, String maxWidthParameter)
    {
		if (maxWidthParameter == null)
			return image;
		else {
			float maxWidth;
			try {
				maxWidth = Integer.parseInt(maxWidthParameter);
			} catch (NumberFormatException e) {
				throw new RuntimeException( "Expected a number in the maxWidth parameter but got: \"" + maxWidthParameter + "\"");
			}
			float scale = 1;
			if (maxWidth > 0 && image.getWidth() > maxWidth)
				scale = maxWidth / image.getWidth();

			int width = (int) (image.getWidth() * scale);
			int height = (int) (image.getHeight() * scale);
			Image scaledImage = (scale < 1) ? image.getScaledInstance(width, height, Image.SCALE_SMOOTH) : image;
			if (scaledImage instanceof BufferedImage)
				return (BufferedImage) scaledImage;
			else {
				BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = copy.createGraphics();
				try {
					g2d.drawImage(scaledImage, null, null);
				} finally {
					g2d.dispose();
				}
				return copy;
			}

		}
	}
     

}
