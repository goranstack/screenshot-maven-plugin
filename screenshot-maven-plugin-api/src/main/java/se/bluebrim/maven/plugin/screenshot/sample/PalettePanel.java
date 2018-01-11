package se.bluebrim.maven.plugin.screenshot.sample;

import java.awt.Paint;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil.StaticFieldVisitor;
import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil.StaticMethodVisitor;

/**
 * Arrange added samples in rows with the specified number of columns.
 * The name of each sample is drawn below the sample.
 * 
 * @author Goran Stack
 *
 */
@SuppressWarnings("serial")
public class PalettePanel extends JPanel
{
	public static PalettePanel createFromStaticPaintFields(Class<?> ofClass, int noOfColumns)
	{
		return createFromStaticFields(ofClass, noOfColumns, Paint.class);
	}

	public static PalettePanel createFromStaticIconFields(Class<?> ofClass, int noOfColumns)
	{
		return createFromStaticFields(ofClass, noOfColumns, Icon.class);
	}
	
	public static PalettePanel createFromStaticPaintMethods(Class<?> ofClass, int noOfColumns)
	{
		return createFromStaticMethods(ofClass, noOfColumns, Paint.class);
	}

	public static PalettePanel createFromStaticIconMethods(Class<?> ofClass, int noOfColumns)
	{
		return createFromStaticMethods(ofClass, noOfColumns, Icon.class);
	}
	
	private static PalettePanel createFromStaticFields(Class<?> ofClass, int noOfColumns, Class<?> fieldType)
	{
		final PalettePanel panel = new PalettePanel(noOfColumns);
		
		SampleUtil.eachStaticField(ofClass, fieldType, new StaticFieldVisitor() {
			
			@Override
			public void visit(Object value, Field field) 
			{
				addSample(panel, value, field.getName());				
			}
		});
	
		return panel;
	}
	
	private static PalettePanel createFromStaticMethods(Class<?> ofClass, int noOfColumns, Class<?> returnType)
	{
		final PalettePanel panel = new PalettePanel(noOfColumns);
		SampleUtil.eachStaticMethod(ofClass, returnType, new StaticMethodVisitor() {
			
			@Override
			public void visit(Object returnValue, Method method) 
			{
				addSample(panel, returnValue, method.getName());
			}
		});
		return panel;
	}

	/**
	 * Creates and add a sample panel to specified panel.
	 */
	private static void addSample(final PalettePanel panel, Object value, String name) 
	{
		if (value instanceof Paint)
			panel.addSample( (Paint) value, name);
		else
			if (value instanceof Icon)
				panel.addSample(new JLabel((Icon)value), name);
	}


	public PalettePanel(int noOfColumns) 
	{
		setLayout(new MigLayout(new LC().wrapAfter(noOfColumns)));
		setOpaque(false);
	}
	
	public void addSample(JComponent sample, String name)
	{
		JPanel samplePanel = new NamedSamplePanel(sample, name);
		add(samplePanel, "center, growy");			
	}
	
	public void addSample(Paint paint, String name)
	{
		addSample(new PaintSamplePanel(paint), name);
	}

}