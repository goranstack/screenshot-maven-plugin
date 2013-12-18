package se.bluebrim.maven.plugin.screenshot.sample;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import se.bluebrim.maven.plugin.screenshot.ScreenshotDescriptor;
import se.bluebrim.maven.plugin.screenshot.sample.FontChartPanel.FontPanel;

/**
 * 
 * @author Goran Stack
 *
 */
public class SampleUtil {
	
	public interface StaticMethodVisitor
	{
		public void visit(Object returnValue, Method method);
	}
	
	public interface StaticFieldVisitor
	{
		public void visit(Object value, Field field);
	}
	
	
	public static void eachStaticField(Class<?> ofClass, Class<?> fieldType, StaticFieldVisitor visitor)
	{
		Field[] fields = ofClass.getDeclaredFields();
		for (Field field : fields) {
			if (fieldType.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers()))
			{
				Object value;
				try {
					value = field.get(null);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				visitor.visit(value, field);
			}
		}
	}
	
	/**
	 * <p>
	 * Only non argument methods is visited.
	 * </p>
	 * @param ofClass the class whose methods will be visited
	 * @param returnType only methods of this return type will be visited 
	 * @param visitor the visit method in the visitor will be called for each method
	 */
	public static void eachStaticMethod(Class<?> ofClass, Class<?> returnType, StaticMethodVisitor visitor)
	{
		Method[] methods = ofClass.getDeclaredMethods();
		for (Method method : methods) {
			if (returnType.isAssignableFrom(method.getReturnType()) && Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0)
			{
				Object returnValue;
					try {
						returnValue = method.invoke(null, new Object[]{});
					} catch (IllegalArgumentException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					}
				visitor.visit(returnValue, method);
			}
		}
	}

	/**
	 * Just ignore null value since its common when developing resource classes to have template
	 * methods returning null.
	 */
	public static Collection<ScreenshotDescriptor> createStaticFieldScreenshots(final Class<?> ofClass, Class<?> returnType)
	{
		final List<ScreenshotDescriptor> screenshotDescriptors = new ArrayList<ScreenshotDescriptor>();
		eachStaticField(ofClass, returnType, new StaticFieldVisitor() {
			
			@Override
			public void visit(Object value, Field field) {
				if (value != null)
					screenshotDescriptors.add(createScreenshotDescriptor( value, ofClass, field.getName().toLowerCase()) );
				
			}
		});
		return screenshotDescriptors;
	}
	
	/**
	 * Just ignore null value since its common when developing resource classes to have template
	 * methods returning null.
	 */
	public static Collection<ScreenshotDescriptor> createStaticMethodScreenshots(final Class<?> ofClass, Class<?> returnType)
	{
		final List<ScreenshotDescriptor> screenshotDescriptors = new ArrayList<ScreenshotDescriptor>();
		eachStaticMethod(ofClass, returnType, new StaticMethodVisitor() {
			
			@Override
			public void visit(Object value, Method method) {
				if (value != null)
					screenshotDescriptors.add(createScreenshotDescriptor( value, ofClass, method.getName().toLowerCase()) );
				
			}
		});
		return screenshotDescriptors;
	}
	
	private static ScreenshotDescriptor createScreenshotDescriptor(Object motif, Class<?> targetClass, String scene)
	{
		if (motif instanceof Paint)
			return new ScreenshotDescriptor(new PaintSamplePanel( (Paint)motif ), targetClass, scene);
		else if (motif instanceof Icon)
			return new ScreenshotDescriptor(new JLabel( (Icon)motif ), targetClass, scene);
		else if (motif instanceof Image)
			return new ScreenshotDescriptor(new JLabel( new ImageIcon((Image)motif )), targetClass, scene);
		else if (motif instanceof Font)
		{
			JLabel pangramLabel = FontPanel.createPangramSample( (Font)motif );
			pangramLabel.setBackground(Color.WHITE);
			pangramLabel.setOpaque(true);
			return new ScreenshotDescriptor(pangramLabel, targetClass, scene);
		} else
			throw new IllegalArgumentException("The screenshot motif type must be Paint, Icon, Image or Font. Got: " + motif.getClass().getSimpleName());
			
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static field returning
	 * a Paint typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticPaintFieldScreenshots(final Class<?> ofClass)
	{
		return createStaticFieldScreenshots(ofClass, Paint.class);
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static field returning
	 * a Icon typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticIconFieldScreenshots(final Class<?> ofClass)
	{
		return createStaticFieldScreenshots(ofClass, Icon.class);
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static field returning
	 * a Icon typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticFontFieldScreenshots(final Class<?> ofClass)
	{
		return createStaticFieldScreenshots(ofClass, Font.class);
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static field returning
	 * a Image typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticImageFieldScreenshots(final Class<?> ofClass)
	{
		return createStaticFieldScreenshots(ofClass, Image.class);
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static field returning
	 * a Paint typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticPaintMethodScreenshots(final Class<?> ofClass)
	{
		return createStaticMethodScreenshots(ofClass, Paint.class);
	}
	
	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static method returning
	 * an Icon typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticIconMethodScreenshots(final Class<?> ofClass)
	{
		return createStaticMethodScreenshots(ofClass, Icon.class);
	}

	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static method returning
	 * a Font typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticFontMethodScreenshots(final Class<?> ofClass) 
	{
		return createStaticMethodScreenshots(ofClass, Font.class);
	}

	/**
	 * 
	 * @return A Collection of ScreenshotDescriptor's for each static method returning
	 * a Image typed object.
	 */
	public static Collection<ScreenshotDescriptor> createStaticImageMethodScreenshots(final Class<?> ofClass) 
	{
		return createStaticMethodScreenshots(ofClass, Image.class);
	}

	/**
	 * Use this method to perform layout on components without open them in a window. Can be useful for
	 * unit testing. <br>
	 * Found at: <a href="http://forums.sun.com/thread.jspa?messageID=10852895#10852895"> Turning a component into a BufferedImage</a>
	 */
	public static void propagateDoLayout(Component c) {
	    synchronized (c.getTreeLock()) {
	        c.doLayout();
	
	        if (c instanceof Container) {
	            for (Component subComp : ((Container) c).getComponents()) {
	                propagateDoLayout(subComp);
	            }
	        }
	    }
	}
	
	/**
	 * This is an alternative approach to headless layout. <br>
	 * Some components can't be properly be layouted without having a peer. The peer is normally created when the component is showed in i window.
	 * Our usage of Swing components requires layout to be performed without displaying in a window (JFrame).
	 * Found at: <a href="http://stackoverflow.com/questions/12500952/when-creating-a-bufferedimage-from-a-jpanel-w-o-a-jframe-can-i-also-use-a-lay">When creating a BufferedImage from a JPanel (w/o a JFrame), can I also use a layout manager?</a>
	 */
	public static void headlessPack(Component c) {
		c.addNotify(); 
		c.validate();
		c.setSize(c.getPreferredSize());
	}



}
