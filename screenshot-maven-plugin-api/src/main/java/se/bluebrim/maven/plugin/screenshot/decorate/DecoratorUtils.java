package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComponent;

import org.apache.commons.lang.mutable.MutableBoolean;


/**
 * Helper methods for applying screenshot decorator's to Swing UI's
 * 
 * @author Goran Stack
 *
 */
public class DecoratorUtils {

	public interface ScreenshotDecoratorVisitor
	{
		public void visit(ScreenshotDecorator decorator, JComponent component);
	}

	public interface EmphasizedComponentVisitor
	{
		public void visit(JComponent emphasizedComponent);
	}

	public static void eachDecorator(JComponent rootComponent, final DecoratorUtils.ScreenshotDecoratorVisitor visitor)
	{
		eachComponent(rootComponent, new ComponentVisitor() {			
			@Override
			public void visit(JComponent component) {
				Object clientProperty = component.getClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY);
				if (isScreenshotDecorator(clientProperty))
					visitor.visit(new ScreenshotDecoratorWrapper(clientProperty), component);	
			}
		});
	}
		
	private interface ComponentVisitor
	{
		public void visit(JComponent component);
	}
	
	public static boolean hasEmphasizers(JComponent rootComponent)
	{
		final MutableBoolean result = new MutableBoolean(false);
		eachEmphasizedComponent(rootComponent, new EmphasizedComponentVisitor(){

			@Override
			public void visit(JComponent emphasizedComponent) {
				result.setValue(true);				
			}});		
		return result.booleanValue();
	}
	
	public static void eachEmphasizedComponent(JComponent rootComponent, final DecoratorUtils.EmphasizedComponentVisitor visitor)
	{
		eachComponent(rootComponent, new DecoratorUtils.ComponentVisitor(){

			@Override
			public void visit(JComponent component) {
				
				Object clientProperty = component.getClientProperty(Emphasizer.CLIENT_PROPERTY_KEY);
				if (isEmphasizer(clientProperty))
					visitor.visit(component);				
			}});		
	}
		
	/**
	 * Recursive method traversing the component hierarchy
	 */
	private static void eachComponent(JComponent rootComponent, DecoratorUtils.ComponentVisitor visitor)
	{
		for (Component child : rootComponent.getComponents())
			if (child instanceof JComponent) {
				eachComponent((JComponent) child, visitor);
			}
		visitor.visit(rootComponent);
	}
	
	public static void decorate(String name, JComponent inPanel, ScreenshotDecorator withDecorator)
	{
		JComponent component = DecoratorUtils.findNamedComponent(inPanel, name);
		if (component == null)
			throw new RuntimeException("Unable to find component named: \"" + name + "\"");
		component.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, withDecorator);		
	}
	
	/**
	 * Use this method to display decorated screenshot panels in 
	 * a JFrame. Can save you some time when developing new decorators classes.
	 */
	public static void decorateScreenshot(final JComponent rootComponent, final Graphics2D g2d)
	{		
		eachDecorator(rootComponent, new DecoratorUtils.ScreenshotDecoratorVisitor() {
			
			@Override
			public void visit(ScreenshotDecorator decorator, JComponent component) {
				decorator.paint(g2d, component, rootComponent);				
			}
		});
	}

	public static void emphasize(String name, JComponent inPanel)
	{
		JComponent component = DecoratorUtils.findNamedComponent(inPanel, name);
		if (component == null)
			throw new RuntimeException("Unable to find component named: \"" + name + "\"");
		component.putClientProperty(Emphasizer.CLIENT_PROPERTY_KEY, new Emphasizer());		
	}

	
	/**
	 * Replacement for <code>instanceof</code> that won't work when the same class is loaded with different class loaders 
	 */
	private static boolean isScreenshotDecorator(Object candidate)
	{
		if (candidate == null)
			return false;
		try {
			ScreenshotDecoratorWrapper.createPaintMethod(candidate);
			ScreenshotDecoratorWrapper.createGetBoundsMethod(candidate);
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Replacement for <code>instanceof</code> that won't work when the same class is loaded with different class loaders 
	 */
	private static boolean isEmphasizer(Object candidate)
	{
		if (candidate == null)
			return false;
		return (candidate.getClass().getName().equals(Emphasizer.class.getName()));
	}

	
	/**
	 * 
	 * @return The last component in the component hierarchy with the specified name.
	 */
	private static JComponent findNamedComponent(JComponent root, final String name)
	{
		if (name.equals(root.getName()))
			return root;
		FindNamedComponentVisitor visitor = new FindNamedComponentVisitor(name);
		eachComponent(root, visitor);		
		return visitor.found;
	}
	
	private static class FindNamedComponentVisitor implements ComponentVisitor
	{
		String name;
		JComponent found;
		
		
		public FindNamedComponentVisitor(String name) {
			super();
			this.name = name;
		}


		@Override
		public void visit(JComponent component) {
			if (name.equals(component.getName()))
				found = component;			
		}
		
	}
	
	/**
	 * The screenshot plugin generates screenshots using its owns class loaders for several reasons.
	 * While processing screenshot decorations we must handle cases where the same class is loaded
	 * with different classloader. So instead of class casting a ScreenshotDecorator whose class
	 * is loaded with a separate class loader it is wrapped and methods are invoked using reflection.
	 */
	private static class ScreenshotDecoratorWrapper implements ScreenshotDecorator
	{
		private Object screenshotDecorator;
		private Method paintMethod;
		private Method getBoundsMethod;
		

		private static Method createGetBoundsMethod(Object candidate)
				throws NoSuchMethodException {
			return candidate.getClass().getMethod("getBounds", JComponent.class, JComponent.class);
		}


		private static Method createPaintMethod(Object candidate)
				throws NoSuchMethodException {
			return candidate.getClass().getMethod("paint", Graphics2D.class, JComponent.class, JComponent.class);
		}
		
		
		public ScreenshotDecoratorWrapper(Object screenshotDecorator) 
		{
			super();
			this.screenshotDecorator = screenshotDecorator;
			try {
				paintMethod = createPaintMethod(screenshotDecorator);
				getBoundsMethod = createGetBoundsMethod(screenshotDecorator);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}



		@Override
		public Rectangle2D getBounds(JComponent component, JComponent rootComponent) 
		{
			try {
				return (Rectangle2D) getBoundsMethod.invoke(screenshotDecorator, component, rootComponent);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void paint(Graphics2D g2d, JComponent component, JComponent rootComponent) 
		{
			try {
				paintMethod.invoke(screenshotDecorator, g2d, component, rootComponent);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}		
		}
	}

}
