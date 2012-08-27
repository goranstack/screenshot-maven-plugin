package se.bluebrim.tiny.cms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/**
 * Found at: http://forums.devshed.com/java-help-9/how-to-write-a-servlet-to-read-file-send-by-412124.html
 * 
 * 
 * @author Goran Stack
 *
 */
public class UploadTest {

   public static void main(String[] args) throws IOException {
      String url = "http://localhost:8080/tiny-cms/upload";

      PostMethod post = new PostMethod(url);

      File file = new File(UploadTest.class.getResource("/example.txt").getFile());

      // Checks that is possible to save the file under i different name
      Part[] parts = {new FilePart(file.getName(), "one-" + file.getName(), file)};

      post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));

      HttpClient client = new HttpClient();

      int status = client.executeMethod(post);

      System.out.println(status);

      try {
         FileWriter file1 = new FileWriter("output.html");
         PrintWriter output1 = new PrintWriter(file1);

         output1.println(post.getResponseBodyAsString());

         output1.close();
      } catch (IOException ie) {
         System.out.println(ie);
      }

      System.out.println(HttpStatus.getStatusText(status));

      System.out.println(post.getResponseBodyAsString());
   }

}
