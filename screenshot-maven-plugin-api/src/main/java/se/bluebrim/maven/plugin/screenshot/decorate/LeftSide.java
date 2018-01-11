package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Position the decorator specified offset from the left side of the component
 * 
 * @author Goran Stack
 *
 */
public class LeftSide extends DockLayout {

	
	public LeftSide() {
		super();
	}

	public LeftSide(Point offset) {
		super(offset);
	}

	protected Point2D getDockingPoint(Rectangle bounds) 
	{
		return new Point2D.Double(bounds.getX(),  bounds.getCenterY());
	}

}
