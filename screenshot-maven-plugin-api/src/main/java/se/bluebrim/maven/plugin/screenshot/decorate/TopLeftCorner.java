package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * 
 * @author Goran Stack
 *
 */
public class TopLeftCorner extends DockLayout {

	
	public TopLeftCorner() {
		super();
	}

	public TopLeftCorner(Point offset) {
		super(offset);
	}

	protected Point2D getDockingPoint(Rectangle bounds) 
	{
		return new Point2D.Double(bounds.getX(),  bounds.getY() + bounds.getHeight());
	}

}
