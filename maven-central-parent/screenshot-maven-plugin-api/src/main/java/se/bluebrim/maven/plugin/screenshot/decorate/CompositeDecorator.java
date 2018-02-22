package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * Enables more than one screenshot decorator per Swing component
 * <p> 
 * <img src="doc-files/CompositeDecorator.png" alt="xxx" >
 * 
 * @author Goran Stack
 *
 */
public class CompositeDecorator implements ScreenshotDecorator 
{
	private List<ScreenshotDecorator> decorators;
	
	public CompositeDecorator()
	{
		this(new ScreenshotDecorator[]{});		
	}
	
	public CompositeDecorator(ScreenshotDecorator... decorators)
	{
		this.decorators = new ArrayList<ScreenshotDecorator>();
		for (ScreenshotDecorator decorator : decorators)
			this.decorators.add(decorator);
	}
	
	@Override
	public void paint(Graphics2D g2d, JComponent component, JComponent rootComponent) 
	{
		for (ScreenshotDecorator screenshotDecorator : decorators) 
		{
			screenshotDecorator.paint(g2d, component, rootComponent);
		}
	}

	public void add(ScreenshotDecorator decorator) 
	{
		decorators.add(decorator);		
	}
	
	/**
	 * Calculate the smallest rectangle that contains all decorators
	 */
	@Override
	public Rectangle2D getBounds(JComponent component, JComponent rootComponent) 
	{
		if (decorators.size() < 1)
			return new Rectangle2D.Double();
		
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		
		for (ScreenshotDecorator decorator : decorators) 
		{
			Rectangle2D bounds = decorator.getBounds(component, rootComponent);
			minX = Math.min(minX, bounds.getX());
			minY = Math.min(minY, bounds.getY());
			maxX = Math.max(maxX, bounds.getX() + bounds.getWidth());
			maxY = Math.max(maxY, bounds.getY() + bounds.getHeight());
		}
		
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

}
