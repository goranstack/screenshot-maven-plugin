package se.bluebrim.maven.plugin.screenshot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXFrame;

import se.bluebrim.maven.plugin.screenshot.decorate.CalloutDecorator;
import se.bluebrim.maven.plugin.screenshot.decorate.Center;
import se.bluebrim.maven.plugin.screenshot.decorate.CompositeDecorator;
import se.bluebrim.maven.plugin.screenshot.decorate.DecoratorUtils;
import se.bluebrim.maven.plugin.screenshot.decorate.Emphasizer;
import se.bluebrim.maven.plugin.screenshot.decorate.FrameDecorator;
import se.bluebrim.maven.plugin.screenshot.decorate.ScreenshotDecorator;
import se.bluebrim.maven.plugin.screenshot.sample.FontChartPanel;
import se.bluebrim.maven.plugin.screenshot.sample.NamedSamplePanel;
import se.bluebrim.maven.plugin.screenshot.sample.PaintSamplePanel;
import se.bluebrim.maven.plugin.screenshot.sample.PalettePanel;

public class ScreenshotTest {
	
	private JTextField textField;
	private JButton button;
	private JLabel label;
	
	public ScreenshotTest() 
	{
	}


	private void addComponents(JPanel panel) 
	{
		label = new JLabel("Hello world");
		textField = new JTextField();
		textField.setColumns(10);
		button = new JButton("Press me");

		panel.add(label);
		panel.add(textField);
		panel.add(button);
	}
	
	
	@Screenshot (targetClass=FrameDecorator.class)
	public JComponent createFrameDecoratorPanel()
	{
		JPanel panel = new DecoratedPanel();
		addComponents(panel);
		FrameDecorator frameDecorator = new FrameDecorator(Color.RED.darker(), 4, 8);
		frameDecorator.setInsets(new Insets(-5, -5, -5, -5));
		label.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, frameDecorator);
		button.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, new FrameDecorator(new Color(0xAFA313), 4, 8));
		return panel;
	}
	
	@Screenshot (targetClass=Emphasizer.class)
	public JComponent createEmphasizedComponent()
	{
		JPanel panel = new DecoratedPanel();
		addComponents(panel);
		button.putClientProperty(Emphasizer.CLIENT_PROPERTY_KEY, new Emphasizer());
		return panel;
	}

	@Screenshot (targetClass=Emphasizer.class, scene="deco")
	public JComponent createEmphasizedComponentAndCallout()
	{
		JPanel panel = new DecoratedPanel();
		addComponents(panel);
		button.putClientProperty(Emphasizer.CLIENT_PROPERTY_KEY, new Emphasizer());
		textField.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, new CalloutDecorator(1));
		button.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, new CalloutDecorator(2, new Center(new Point(0, 40))));
		return panel;
	}


	
	@Screenshot (targetClass=CalloutDecorator.class)
	public JPanel createCalloutDecoratorPanel()
	{
		JPanel panel = new DecoratedPanel();
		addComponents(panel);
		textField.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, new CalloutDecorator(8));
		return panel;
	}
	
	@Screenshot (targetClass=CalloutDecorator.class, scene="transparent")
	public JPanel createCalloutDecoratorTransparentPanel()
	{
		JPanel panel = createCalloutDecoratorPanel();
		panel.setOpaque(false);
		return panel;
	}
	
	@Screenshot (targetClass=CompositeDecorator.class)
	public JPanel createCompositeDecoratorPanel()
	{
		JPanel panel = new DecoratedPanel();
		addComponents(panel);
		CompositeDecorator composite = new CompositeDecorator();
		composite.add(new FrameDecorator(Color.RED.darker(), 4, 8));
		composite.add(new CalloutDecorator(1));
		button.putClientProperty(ScreenshotDecorator.CLIENT_PROPERTY_KEY, composite);
		return panel;
	}
	
	@Screenshot
	public JPanel testFontChartPanel()
	{
		Locale.setDefault(Locale.ENGLISH);
		return FontChartPanel.createFromStaticFontMethods(FontFactory.class);
	}
	
	public static class FontFactory
	{
		public static Font getSerif14()
		{
			return new Font(Font.SERIF, Font.PLAIN, 14);
		}
		
		public static Font getSerifBold14()
		{
			return new Font(Font.SERIF, Font.BOLD, 14);
		}
		
		public static Font getSerifItalic14()
		{
			return new Font(Font.SERIF, Font.ITALIC, 14);
		}
		
		public static Font getSansSerif14()
		{
			return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
		}
		
		public static Font getSansSerif9()
		{
			return new Font(Font.SANS_SERIF, Font.PLAIN, 9);
		}
		
		public static Font getSansSerif20()
		{
			return new Font(Font.SANS_SERIF, Font.PLAIN, 20);
		}
		
		public static Font getSansSerif28()
		{
			return new Font(Font.SANS_SERIF, Font.PLAIN, 28);
		}
		
		public static Font getSansSerifBold14()
		{
			return new Font(Font.SANS_SERIF, Font.BOLD, 14);
		}
		
		public static Font getSansSerifItalic14()
		{
			return new Font(Font.SANS_SERIF, Font.ITALIC, 14);
		}

	}
	
	@Screenshot
	public JComponent testFontPanel()
	{
		return new FontChartPanel.FontPanel(new Font(Font.SERIF, Font.PLAIN, 12), "ScreenshotTest.testFontPanel");
	}
	
	@Screenshot
	public JComponent testFontSizeLabel()
	{
		return new FontChartPanel.FontSizeLabel(new Font(Font.SERIF, Font.PLAIN, 12));
	}
	
	@Screenshot
	public JComponent testFontKerningPanel()
	{
		return new FontChartPanel.FontKerningPanel(new Font(Font.SERIF, Font.PLAIN, 1));
	}
	
	@Screenshot
	public JComponent testFontMetricPanel()
	{
		return new FontChartPanel.FontMetricPanel(new Font(Font.SERIF, Font.PLAIN, 1));
	}
	
	
	@Screenshot
	public JPanel testPaintSamplePanel()
	{
		return new PaintSamplePanel(Color.ORANGE);
	}
	
	@Screenshot
	public JPanel testNamedSamplePanel()
	{
		return new NamedSamplePanel(new PaintSamplePanel(Color.ORANGE), "ORANGE");
	}
	
	@Screenshot
	public JPanel textPalettePanel()
	{
		PalettePanel panel = new PalettePanel(4);
		panel.addSample(Color.BLACK, "BLACK");
		panel.addSample(Color.BLUE, "BLUE");
		panel.addSample(Color.CYAN, "CYAN");
		panel.addSample(Color.LIGHT_GRAY, "LIGHT GRAY");
		panel.addSample(Color.DARK_GRAY, "DARK GRAY");
		panel.addSample(Color.GRAY, "GRAY");
		panel.addSample(Color.GREEN, "GREEN");
		panel.addSample(Color.MAGENTA, "MAGENTA");
		panel.addSample(Color.ORANGE, "ORANGE");
		panel.addSample(Color.PINK, "PINK");
		panel.addSample(Color.RED, "RED");
		panel.addSample(Color.WHITE, "WHITE");
		panel.addSample(Color.YELLOW, "YELLOW");
		return panel;
	}
	
	public static void main(String[] args) {
		final ScreenshotTest instance = new ScreenshotTest();
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				instance.openInWindow();
				instance.openFontChartWindow();
			}
		});
	}

	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		JPanel panel = new JPanel(new MigLayout(new LC().wrapAfter(1)));
		panel.add(createFrameDecoratorPanel());
		panel.add(createCalloutDecoratorPanel());
		panel.add(createCompositeDecoratorPanel());
		panel.add(createEmphasizedComponent());
		panel.add(createEmphasizedComponentAndCallout());
		panel.add(testNamedSamplePanel());
		panel.add(testPaintSamplePanel());
		window.getContentPane().add(panel);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}
	
	private void openFontChartWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(new JScrollPane(testFontChartPanel()));
		window.setSize(1280, 1024);
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}

	
	@SuppressWarnings("serial")
	private static class DecoratedPanel extends JPanel
	{
		@Override
		public void paint(Graphics g) 
		{
			super.paint(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2d.setClip(null);
			DecoratorUtils.decorateScreenshot(this, g2d);
		}
	}

}
