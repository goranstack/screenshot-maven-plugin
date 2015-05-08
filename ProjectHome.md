# Introduction #
The screenshot-maven-plugin is used to generate screen shots from Java Swing applications for two purposes. One is to include the screen shots in the JavaDoc of your classes. The other is to create screen shot galleries that are uploaded in a Content Management System to be accessed when producing documents including screen shots such as user manuals and technical documentation.

The usual way to create screen shots is to start the application, navigate to the desired screen, use the screen capture function in the OS to get a screen shot of the active window in the clip board and finally saving the screen shot by pasting the content from the clip board to an image editing program. Sometimes you have to crop the image before saving it if the captured window contains several sub panels and you only want a screen shot of one of them.

![http://screenshot-maven-plugin.googlecode.com/svn/site/images/atg/PaymentPanel-deco.png](http://screenshot-maven-plugin.googlecode.com/svn/site/images/atg/PaymentPanel-deco.png)

When screen shots are included in user manuals its common to decorate the screen shots with call outs that are referred from the text. This creates even more work to create the screen shot since you have to manual add the call out graphics in the image editor before saving it.

If your application supports multiple languages and you have to produce user manuals for those languages the steps above have to be repeated for each supported language.

The screenshot-maven-plugin automates the task of creating those kind of screen shots with a little bit of coding from your side.

## Screen shots for JavaDoc ##

The support for images in JavaDoc is excellent in Eclipse IDE and probably in other IDE's as well. You can see images in some classes in the JDK for example in [LinearGradientPaint](http://download.oracle.com/javase/6/docs/api/java/awt/LinearGradientPaint.html) and [JLayeredPane](http://download.oracle.com/javase/6/docs/api/javax/swing/JLayeredPane.html). It's very easy to include images in the JavaDoc by adding an image tag such as `<img src = "doc-files/LinearGradientPaint.png">` and putting the corresponding image file in a `doc-files` folder in the same location as the source file of the class. See: [How to Write Doc Comments for the Javadoc Tool - Including Images](http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#images)
When developing a Swing based application it's nice to include an image in the JavaDoc for all classes and/or methods that have a visual presentation. Your co-developers will be very happy when they discover that a visual representation pop ups when hovering over a class in Eclipse.

![http://screenshot-maven-plugin.googlecode.com/svn/site/images/buttondemo-mouse-over.jpg](http://screenshot-maven-plugin.googlecode.com/svn/site/images/buttondemo-mouse-over.jpg)

This project include examples how to use the screenshot-maven-plugin to generate screen shots of panels in the [SwingSet3](https://swingset3.dev.java.net) demo application. The screen shot above was created by the following steps:

  1. Add a method in a test class that returns an instance of the `ButtonDemo` class and annotate the method with the `@Screenshot` annotation
```
	@Screenshot
	public JComponent createButtonDemoScreenShot()
	{
		return new ButtonDemo();
	}
```
  1. Add an image tag to the JavaDoc of the `ButtonDemo` class
```
   /**
    * <img src="doc-files/ButtonDemo.png">
    *
    * @author aim
    */
```
  1. Run the javadoc goal of screenshot-maven-plugin.


## Screen shots for CMS ##

The screenshot-maven-plugin is capable of generating screen shots that is uploaded to a CMS as part of the continuous integration build in a build server for example Hudson. A tiny CMS implemented as a Java servlet is included in this project. It can be used together with the screenshot-maven-plugin to produce a screen shot gallery web page.

![http://screenshot-maven-plugin.googlecode.com/svn/site/images/atg/gallery.png](http://screenshot-maven-plugin.googlecode.com/svn/site/images/atg/gallery.png)

Save a bookmark in your web browser to the page and you can easy check all generated screen shots produced by the last build. In cases where the developers uses Windows on their workstations and the build server runs Linux you get a valuable check that the UI's look good on Linux as well.

The screenshot-maven-plugin can be configured to generate a gallery with a column for
each supported locale. You get the screen shots for the supported languages side by side and can quickly detect layout problems caused by the multi language support.

Below each screen shot a xml snippet is displayed that can be included in a DocBook source file. Although DocBook is a great concept it can be a little bit difficult to master. The support for DocBook in the screenshot-maven-plugin is experimental and there is room for improvements. The goal of supporting DocBook is to create a work flow where both pdf and html documents including generated screen shots are produced from a single source and the whole process is performed by a Maven build.

## How to use the screenshot-maven-plugin in your project ##

See [Configure your pom files](useIt.md)

---

The project logotype: http://wefunction.com/2008/07/function-free-icon-set