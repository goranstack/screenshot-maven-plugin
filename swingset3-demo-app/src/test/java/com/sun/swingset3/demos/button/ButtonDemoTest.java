package com.sun.swingset3.demos.button;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import se.bluebrim.maven.plugin.screenshot.Screenshot;

import com.sun.swingset3.demos.table.TableDemo;
import com.sun.swingset3.demos.togglebutton.ToggleButtonDemo;
import com.sun.swingset3.demos.tree.TreeDemo;

/**
 * Demonstrates how to create various screenshots of components in the SwingSet3 application
 * 
 * @author Goran Stack
 *
 */
public class ButtonDemoTest 
{
	@Screenshot
	public JComponent createButtonDemoScreenShot()
	{
		return new ButtonDemo();
	}
	
	@Screenshot (scene = "laf-nimbus")
	public JComponent createNimbusButtonDemoScreenshot() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		LookAndFeel laf = UIManager.getLookAndFeel();
		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		ButtonDemo buttonDemo = new ButtonDemo();
		UIManager.setLookAndFeel(laf);
		return buttonDemo;
	}
	
	@Screenshot
	public JComponent createToogleButtonDemoScreenshot()
	{
		return new ToggleButtonDemo();
	}
	
	@Screenshot
	public JComponent createTableDemoScreenshot()
	{
		return new TableDemo();
	}
	
	@Screenshot
	public JComponent createTreeDemoScreenshot()
	{
		return new TreeDemo();
	}


}
