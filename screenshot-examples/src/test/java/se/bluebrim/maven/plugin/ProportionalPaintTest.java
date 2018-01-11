package se.bluebrim.maven.plugin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

import com.sun.scenario.scenegraph.ProportionalPaint;

import net.miginfocom.swing.MigLayout;
import se.bluebrim.maven.plugin.screenshot.sample.NamedSamplePanel;

/**
 * 
 * @author Goran Stack
 *
 */
public class ProportionalPaintTest {
	
	public static void main(String[] args) 
	{
		final ProportionalPaintTest instance = new ProportionalPaintTest();
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				instance.openInWindow();
			}
		});
	}


	private JPanel testProportionalPaint()
	{
		final Point2D pt1 = new Point2D.Double(0, 0.35);
		final Point2D pt2 = new Point2D.Double(0.32, 1);				
		
		final JPanel panel = new JPanel(new MigLayout());

		final JXPanel samplePanel2 = new JXPanel();
		samplePanel2.setBackgroundPainter(createMattePainter(pt1, pt2));
		samplePanel2.setPreferredSize(new Dimension(165 * 2, 39 * 2));
		panel.add(new NamedSamplePanel(samplePanel2, "Java"), "wrap");		

		final JXPanel samplePanel1 = new JXPanel();
		samplePanel1.setBackgroundPainter(createMattePainter(pt1, pt2));
		samplePanel1.setPreferredSize(new Dimension(165, 39));		
		panel.add(new NamedSamplePanel(samplePanel1, "Java"), "wrap");

		panel.add(new NamedSamplePanel(new JLabel(new ImageIcon(getClass().getResource("/images/psd-gradient.png"))), "Photoshop"), "wrap");


		PointEditor.ChangedListener pointChanged = new PointEditor.ChangedListener() {
			
			@Override
			public void valueChanged() {
				samplePanel1.setBackgroundPainter(createMattePainter(pt1, pt2));
				samplePanel2.setBackgroundPainter(createMattePainter(pt1, pt2));
				panel.repaint();				
			}
		};
		panel.add(new PointEditor(pt1, "Start point", pointChanged));
		panel.add(new PointEditor(pt2, "End point", pointChanged));
		panel.setBackground(new Color(0xEEF3FA));
		return  panel;
	}

	private MattePainter createMattePainter(Point2D pt1, Point2D pt2) 
	{
		Color color1 = new Color(0xdde5eb);
		Color color2 = new Color(0xfafbfc);
//		Color color1 = Color.BLUE;
//		Color color2 = Color.GREEN;
		MattePainter mattePainter = new MattePainter(new ProportionalPaint( new GradientPaint(new Point2D.Double(pt1.getX(), pt1.getY()), color1, new Point2D.Double(pt2.getX(), pt2.getY()),  color2) ), false);
//		MattePainter mattePainter = new MattePainter( new GradientPaint(new Point2D.Double(pt1.getX(), pt1.getY()), color1, new Point2D.Double(pt2.getX(), pt2.getY()),  color2), true);
		mattePainter.setCacheable(false);
		return mattePainter;
	}
	
	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(testProportionalPaint());
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}
		
	@SuppressWarnings("serial")
	private static class PointEditor extends JPanel
	{
		interface ChangedListener
		{
			void valueChanged();
		}
		public PointEditor(final Point2D pt, String header, final ChangedListener listener) 
		{
			setLayout(new MigLayout());
			add(new JLabel(header), "wrap");
			final JLabel xLabel = new JLabel("x: " + pt.getX());
			final JSlider xSlider = new JSlider(0, 100, (int) Math.round(pt.getX() * 100));
			xSlider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					pt.setLocation(xSlider.getValue()/100.0, pt.getY());
					xLabel.setText("x: " + pt.getX());
					listener.valueChanged();
				}
			});
			add(xLabel);
			add(xSlider, "wrap");
			
			final JLabel yLabel = new JLabel("y: " + pt.getY());
			final JSlider ySlider = new JSlider(0, 100, (int) Math.round(pt.getY() * 100));
			ySlider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					pt.setLocation(pt.getX(), ySlider.getValue()/100.0);
					yLabel.setText("y: " + pt.getY());
					listener.valueChanged();
				}
			});
			add(yLabel);
			add(ySlider, "wrap");						
		}		

	}
}
