## Step by step how to getting started ##

  1. Install Eclipse IDE for Java Developers from: http://www.eclipse.org/downloads/
  1. Install the Subclipse plug-in from: http://subclipse.tigris.org/
  1. Install the M2Eclipse plug-in from: http://m2eclipse.sonatype.org/
  1. Open the SVN Repository Exploring Perspective in Eclipse
  1. Press the "Add SVN Repository" button and add the following URL in the dialog: http://screenshot-maven-plugin.googlecode.com/svn/trunk
  1. Select the screenshot-maven-plugin-parent folder and use the right button menu to perform: Checkout as Maven Project...

Your Eclipse workspace should now be populated with the modules of the screenshot-maven-plugin project. The dependency jars should be automatically downloaded by Maven.

See screen shots in action by open the class `ResourceConstantsUsage` and hover the mouse over the `ColorConstants` class.

![http://screenshot-maven-plugin.googlecode.com/svn/site/images/colorconstants-hover.jpg](http://screenshot-maven-plugin.googlecode.com/svn/site/images/colorconstants-hover.jpg)