package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Position the decorator specified offset from the right side of the component
 * 
 * @author Goran Stack
 *
 */
public class RightSide extends DockLayout {

	
	public RightSide() {
		super();
	}

	public RightSide(Point offset) {
		super(offset);
	}

	protected Point2D getDockingPoint(Rectangle bounds) 
	{
		return new Point2D.Double(bounds.getX() + bounds.getWidth(),  bounds.getCenterY());
	}

}
