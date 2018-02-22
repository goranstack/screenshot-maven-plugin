/**
 * 
 */
package se.bluebrim.maven.plugin.screenshot.sample;

import java.awt.Dimension;
import java.awt.Paint;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.CheckerboardPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.MattePainter;

@SuppressWarnings("serial")
/**
 * A small panel filled with the specified {@link Paint} as in this
 * example:
 * <p>
 * <img src="doc-files/PaintSamplePanel.png" alt="xxx" >
 * 
 * @author Goran Stack
 */
public class PaintSamplePanel extends JXPanel
{
	private Dimension size;
	
	public PaintSamplePanel(Paint paint) 
	{
		this(paint, new Dimension(80, 80));
	}

	public PaintSamplePanel(Paint paint, Dimension size) 
	{
		this.size = size;
		CompoundPainter<PaintSamplePanel> painter = new CompoundPainter<PaintSamplePanel>(new CheckerboardPainter(), new MattePainter(paint, true));
		setBackgroundPainter(painter);
		setBorder(new DropShadowBorder());
		setPaintBorderInsets(false);
	}
	
	@Override
	public Dimension getPreferredSize() 
	{
		return size;
	}
	
	@Override
	public Dimension getMinimumSize() 
	{
		return size;
	}
}