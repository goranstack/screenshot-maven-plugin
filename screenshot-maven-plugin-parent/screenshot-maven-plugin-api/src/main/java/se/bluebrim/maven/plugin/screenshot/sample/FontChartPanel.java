package se.bluebrim.maven.plugin.screenshot.sample;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.mutable.MutableBoolean;

import se.bluebrim.maven.plugin.screenshot.sample.SampleUtil.StaticMethodVisitor;

/**
 * <p>
 * The {@code FontChartPanel} draw an example of each specified font using a pangram and a character sets. 
 * Various sizes of the same font are grouped together. The fonts are specified as a Map to enable each font
 * to be associated with a string that is drawn as the font source.
 * </p>
 * 
 * <img src="doc-files/FontChartPanel.png">
 * 
 * @author Goran Stack
 *
 */
@SuppressWarnings("serial")
public class FontChartPanel extends JPanel
{
	private static ResourceBundle BUNDLE = ResourceBundle.getBundle(FontChartPanel.class.getPackage().getName() + ".messages");
	private static final Font INFO_FONT = new Font("SansSerif", Font.PLAIN, 12);
	private static final Color INFO_COLOR = Color.BLACK;

	/**
	 * This only works if you include the following statement in your main method:
	 * 
	 * <code>System.setProperty("awt.useSystemAAFontSettings", "off");</code>
	 *
	 */
	public static class RenderingHintsModel
	{
		
		public interface Listener
		{
			public void changed();
		}
		
		private Listener listener;
		private MutableBoolean useTextAntialiasing = new MutableBoolean(true);
		private MutableBoolean useFractionalMetrics = new MutableBoolean(true);
		private JPanel panel;

				
		public RenderingHintsModel(Listener listener) {
			super();
			this.listener = listener;
		}

		private void setRenderingHints(Graphics2D g2d)
		{
	        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, useTextAntialiasing.booleanValue() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  useFractionalMetrics.booleanValue() ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			
		}

		public JPanel getCheckBoxPanelPanel()
		{
			if (panel == null)
			{
				panel = new JPanel(new MigLayout(new LC().wrapAfter(1)));
				panel.add(createCheckbox("text.antialiasing", useTextAntialiasing));
				panel.add(createCheckbox("fractional.metrics", useFractionalMetrics));
			}
			return panel;
		}
		
		private JCheckBox createCheckbox(String nameKey, final MutableBoolean value)
		{
			final JCheckBox checkBox = new AntialiasedCheckBox();
			checkBox.setSelected(value.booleanValue());
			checkBox.setAction(new AbstractAction(BUNDLE.getString(nameKey)) {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					value.setValue(checkBox.isSelected());
					listener.changed();
				}
			});
			return checkBox;
		}
		
	}
	/**
	 * <p>
	 * A panel that draw font info and sample text of the font. 
	 * </p>
	 * <img src="doc-files/FontPanel.png">
	 */
	public static class FontPanel extends JPanel implements Comparable<FontChartPanel.FontPanel>
	{
		private static final float FONT_SAMPLE_SIZE = 16f;		// The size of the character set and digits sample
		private Font font;
		
		public FontPanel(Font font, String fontSource)
		{
			this(font, fontSource, null);
		}

		
		public FontPanel(Font font, String fontSource, RenderingHintsModel renderingHintsModel) 
		{
			this.font = font;
			setBackground(Color.WHITE);
			setLayout(new MigLayout(new LC().wrapAfter(1).insets("10 0 10 10")));
			addInfoLine(BUNDLE.getString("font.label") + ": " + getFontName());
			if (fontSource != null)
				addInfoLine(BUNDLE.getString("font.source") + ": " + fontSource);
			addSampleLine(font, "font.characterset", renderingHintsModel);
			addSampleLine(font, "font.digits", renderingHintsModel);
		}
	
		/**
		 * This kind of font is always derived to an appropriate size
		 * and is to small to display as a sample
		 */
		public static Font magnifyOnePointSize(Font font) 
		{
			return font.getSize() == 1 ? font.deriveFont(12f) : font;
		}
	
		private void addInfoLine(String info)
		{
			JLabel infoLine = new AntialiasedLabel(info);
			infoLine.setFont(INFO_FONT);
			infoLine.setForeground(INFO_COLOR);
			add(infoLine, "growx");			
		}
			
		public String getFontName() {
			return font.getFontName();
		}
				
	
		public Font getFont() {
			return font;
		}
	
		private void addSampleLine(Font font, String key, RenderingHintsModel renderingHints) {
			JLabel fontSample = new BestRenderQualityLabel(BUNDLE.getString(key), font.deriveFont(FONT_SAMPLE_SIZE), true, renderingHints);
			add(fontSample, "growx");
		}
		
		public static JLabel createPangramSample(Font font)
		{
			return new BestRenderQualityLabel(BUNDLE.getString("font.pangram"), FontPanel.magnifyOnePointSize(font));
		}
		

		public static JLabel createPangramSample(Font font, RenderingHintsModel renderingHints)
		{
			return new BestRenderQualityLabel(BUNDLE.getString("font.pangram"), FontPanel.magnifyOnePointSize(font), true, renderingHints);
		}

		@Override
		public int compareTo(FontChartPanel.FontPanel panel) {
			
			CompareToBuilder compareToBuilder = new CompareToBuilder();
			compareToBuilder.append(getFontName(), panel.getFontName());
			compareToBuilder.append(font.getSize2D(), panel.font.getSize2D());
			return compareToBuilder.toComparison();
		}
	}

	/**
	 * <p>
	 * A panel that draw the font size as a formatted number. 
	 * </p>
	 * <img src="doc-files/FontSizeLabel.png">
	 */
	public static class FontSizeLabel extends AntialiasedLabel
	{
		private static final NumberFormat FONT_SIZE_FORMAT = NumberFormat.getNumberInstance();
		{
			FONT_SIZE_FORMAT.setMinimumFractionDigits(0);		// Avoid to display font size as 22.0 but enable display of font size 22.5
		}

		public FontSizeLabel(Font font) 
		{
			super(FONT_SIZE_FORMAT.format(font.getSize2D()));
			setOpaque(false);
			setFont(INFO_FONT);
			setForeground(INFO_COLOR);
		}		

	}

	public static class DividerPanel extends JPanel
	{
		public DividerPanel() 
		{
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setClip(null);
			g.setColor(INFO_COLOR);
			g.drawLine(0, getHeight(), getWidth(), getHeight());
		}
		
		@Override
		public Dimension getPreferredSize() 
		{
			return new Dimension(1, 1);
		}
	}
	
	/**
	 * <p>
	 * A panel that illustrates the font metrics by drawing ascender, base and descender line
	 * on top of text sample with a rather large font size.
	 * </p>
	 * <img src="doc-files/FontMetricPanel.png">
	 */
	public static class FontMetricPanel extends BestRenderQualityLabel
	{
		
		public FontMetricPanel(Font font)
		{
			this(font, null);
		}

		public FontMetricPanel(Font font, RenderingHintsModel renderingHints) 
		{
			super(" Kpfx   ", font.deriveFont(48f), true, renderingHints);
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D g2d = (Graphics2D)g;
			TextLayout textLayout = new TextLayout(getText(), getFont(), g2d.getFontRenderContext());
			g2d.setStroke(new BasicStroke(1f));
			g2d.setColor(Color.RED.darker());
			Rectangle2D bounds = textLayout.getBounds();

			float ascent = textLayout.getAscent();
			Line2D.Double ascenderLine = new Line2D.Double(bounds.getX(), bounds.getY() + ascent, bounds.getX() + bounds.getWidth(), bounds.getY() + ascent);
			Line2D.Double baseLine = new Line2D.Double(bounds.getX(), ascent, bounds.getX() + bounds.getWidth(), ascent);
			float descent = textLayout.getDescent() + ascent;
			Line2D.Double descenderLine = new Line2D.Double(bounds.getX(), descent, bounds.getX() + bounds.getWidth(), descent);
			
			g2d.draw(ascenderLine);			
			g2d.draw(baseLine);			
			g2d.draw(descenderLine);		
		}
		
	}
	
	/**
	 * <p>
	 * Draw a text sample twice one with kerning on and one without kerning.
	 * </p>
	 * <img src="doc-files/FontKerningPanel.png">
	 *
	 */
	public static class FontKerningPanel extends JPanel
	{		
		public FontKerningPanel(Font font) {
			this(font, null);
		}

		public FontKerningPanel(Font font, RenderingHintsModel renderingHints) {
			super();
			setLayout(new MigLayout(new LC().flowY().gridGap("0", "0")));
			setOpaque(false);
			setBackground(null);
			String text = "AV To ";
			font = font.deriveFont(32f);
			add(new BestRenderQualityLabel(text, font, false, renderingHints));
			add(new BestRenderQualityLabel(text, font, true, renderingHints));
		}		
	}

	public static class BestRenderQualityLabel extends JLabel
	{
		private static HashMap<Font, FontMetrics> FONT_METRICS = new HashMap<Font, FontMetrics>();
		private RenderingHintsModel renderingHints;

		public BestRenderQualityLabel(String string, Font font)
		{
			this(string, font, true);
		}
		
		@SuppressWarnings("unchecked")
		public BestRenderQualityLabel(String string, Font font, boolean kerning) 
		{
			super(string);
			Map attributes = font.getAttributes();
			if (kerning)
			{
				attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
				font = font.deriveFont(attributes);
			}
			setFont(font);
		}
		
		
		public BestRenderQualityLabel(String string, Font font, boolean kerning, RenderingHintsModel renderingHints)
		{
			this(string, font, kerning);
			this.renderingHints = renderingHints;
		}

	
		@Override
		public void paint(Graphics g) 
		{
			Graphics2D g2d = (Graphics2D)g;
			setRenderingHints(g2d);
	        super.paint(g);
		}

		private void setRenderingHints(Graphics2D g2d) {
			if (renderingHints != null)
				renderingHints.setRenderingHints(g2d);
			else
			{
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			}
		}
		
		@Override
		/**
		 * @see http://stackoverflow.com/questions/2753514/java-friendlier-way-to-get-an-instance-of-fontmetrics
		 */
		public FontMetrics getFontMetrics(Font font) 
		{
		    if (FONT_METRICS.containsKey(font))
		    {
		        return FONT_METRICS.get(font);
		    }
		    FontMetrics fm = createFontMetrics(font);
		    FONT_METRICS.put(font, fm);
		    return fm;
		}
		

		private FontMetrics createFontMetrics(Font font)
		{
		    BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
		    Graphics g = bi.getGraphics();
		    setRenderingHints((Graphics2D) g);
		    FontMetrics fm = g.getFontMetrics(font);
		    g.dispose();
		    bi = null;
		    return fm;
		}

		
	}

	public static FontChartPanel createFromStaticFontMethods(final Class<?> inClass)
	{
		final Map<Font, String> fontsWithSource = new HashMap<Font, String>();
		SampleUtil.eachStaticMethod(inClass, Font.class, new StaticMethodVisitor() {
			
			@Override
			public void visit(Object returnValue, Method method) 
			{
				if (returnValue != null)
					fontsWithSource.put((Font)returnValue, inClass.getSimpleName() + "." + method.getName());
			}
		});
		return new FontChartPanel(fontsWithSource);
	}
	
	

	public FontChartPanel(Map<Font, String> fontsWithSource)
	{
		this(fontsWithSource, null);
	}

	/**
	 * 
	 * @param fontsWithSource is a map containing the fonts to display in the font chart. Each font has an associated source.
	 * The source can for example specify the class and the static method that provides the font
	 */
	public FontChartPanel(Map<Font, String> fontsWithSource, RenderingHintsModel renderingHintsModel)
	{
		setLayout(new MigLayout(new LC().wrapAfter(2).alignX("left")));
		setBackground(Color.WHITE);
		final List<FontChartPanel.FontPanel> samples = new ArrayList<FontChartPanel.FontPanel>();
		
		Set<Entry<Font, String>> entrySet = fontsWithSource.entrySet();
		for (Entry<Font, String> entry : entrySet) {
			samples.add(new FontPanel( entry.getKey(), entry.getValue(), renderingHintsModel));
		}
		
		Collections.sort(samples);
		String fontName = null;
		for (FontChartPanel.FontPanel fontPanel : samples) 
		{
			if (!fontPanel.getFontName().equals(fontName))
			{
				if (fontName != null)
					add(new DividerPanel(), "span 2, growx");
				add(fontPanel, "span 2");
				add(new FontMetricPanel(fontPanel.font, renderingHintsModel), "span 2");				
				add(new FontKerningPanel(fontPanel.font, renderingHintsModel), "span 2");
							
				fontName = fontPanel.getFontName();
			}
			Font font = FontPanel.magnifyOnePointSize(fontPanel.font);
			add(new FontSizeLabel(font));
			add(FontPanel.createPangramSample(font, renderingHintsModel));

		}			
	}
	
	/**
	 * To enable turning on and off the antialiasing we have to turn off the global setting in Java with the call:
	 * <code>System.setProperty("awt.useSystemAAFontSettings", "off");</code>
	 * This will make the labels ugly unless we turn on the antialiasing in the paint method.
	 *
	 */
	private static class AntialiasedLabel extends JLabel
	{
		public AntialiasedLabel(String text) {
			super(text);
		}

		@Override
		public void paint(Graphics g) {			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				super.paint(g2d);
			} finally
			{
				g2d.dispose();
			}
		}
		
	}
	
	private static class AntialiasedCheckBox extends JCheckBox
	{
		@Override
		public void paint(Graphics g) {			
			Graphics2D g2d = (Graphics2D) g.create();
			try {
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				super.paint(g2d);
			} finally
			{
				g2d.dispose();
			}
		}
		
	}
	
}