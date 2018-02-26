package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Draws a frame around a component as in this example: 
 * <p>
 * <img src="doc-files/FrameDecorator.png" alt="Screenshot image of class: FrameDecorator" >
 * <p>
 * @author Goran Stack
 * 
 */
public class FrameDecorator implements ScreenshotDecorator 
{
	private Paint paint;
	private Stroke stroke;
	private double cornerRadius;
	private Insets insets;
	private Point offset;
	
	public FrameDecorator(Paint paint, Stroke stroke, double cornerRadius) 
	{
		super();
		this.paint = paint;
		this.stroke = stroke;
		this.cornerRadius = cornerRadius;
		insets = new Insets(0, 0, 0, 0);
		offset = new Point();
	}
	
	public FrameDecorator(Paint paint, float frameWidth, double cornerRadius) 
	{
		this(paint, new BasicStroke(frameWidth), cornerRadius);
	}

	public FrameDecorator(Paint paint, float frameWidth) 
	{
		this(paint, new BasicStroke(frameWidth), 0);
	}

	public FrameDecorator(Paint paint) 
	{
		this(paint, 1);
	}
	
	/**
	 * Can be used as "outsets" as well by specifying negative values
	 */
	public void setInsets(Insets insets) 
	{
		this.insets = insets;
	}

	public void setOffset(Point offset) 
	{
		this.offset = offset;
	}

	@Override
	public void paint(Graphics2D g2d, JComponent component, JComponent rootComponent) 
	{
		g2d.translate(offset.x, offset.y);
		g2d.setPaint(paint);
		g2d.setStroke(stroke);
		g2d.draw(calculateShape(getGlobalBounds(component, rootComponent)));
	}

	private Shape calculateShape(Rectangle2D rect) {
		if (cornerRadius > 0)
			return new RoundRectangle2D.Double(rect.getX() + insets.left, rect.getY() + insets.top, rect.getWidth() - insets.left - insets.right, rect.getHeight() - insets.top - insets.bottom, cornerRadius, cornerRadius);
		else
			return new Rectangle2D.Double(rect.getX() + insets.left, rect.getY() + insets.top, rect.getWidth() - insets.left - insets.right, rect.getHeight() - insets.top - insets.bottom);
	}
	
	@Override
	public Rectangle2D getBounds(JComponent component, JComponent rootComponent) 
	{		
		return stroke.createStrokedShape(calculateShape(getGlobalBounds(component, rootComponent))).getBounds2D();
	}
	
	private Rectangle getGlobalBounds(JComponent component, JComponent rootComponent) {
		return SwingUtilities.convertRectangle(component.getParent(), component.getBounds(), rootComponent);
	}


}
