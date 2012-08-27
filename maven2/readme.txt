The files in this folder are intended for the owner of the open source project to perform releases on a
Windows machine.  

When running the prepare goal inside Eclipse I get the following error:

"Maven executable not found at: C:\workspaces\screenshot-maven-plugin\screenshot-maven-plugin-parent\EMBEDDED\bin\mvn.bat"

This forces me to run Maven outside Eclipse from the command line. These files helps me to avoid typing 
the Maven release commands every time a release should be performed.

Valuable tips of how to release was found at: http://code.google.com/p/maven-install4j/wiki/ReleaseProcedure


If you get:

 svn: MKACTIVITY .................: authorization failed: Could not authenticate to server: rejected Basic challenge
 
 
 Try to add your svn password to the release prepare command:
 
 mvn release:prepare -Dusername=[username] -Dpassword=[password]
 
 

