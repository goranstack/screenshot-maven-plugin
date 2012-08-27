package se.bluebrim.maven.plugin.screenshot.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * @author Goran Stack
 *
 */
public class Bottom extends DockLayout {

	
	public Bottom() {
		super();
	}

	public Bottom(Point offset) {
		super(offset);
	}

	protected Point2D getDockingPoint(Rectangle bounds) 
	{
		return new Point2D.Double(bounds.getCenterX(),  bounds.getY() + bounds.getHeight());
	}

}
