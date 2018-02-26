package se.bluebrim.maven.plugin.screenshot.sample;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXPanel;

/**
 * A small panel with a sample of something that can be drawn by a {@link JComponent}
 * with a text label below the sample.
 * <p>
 * <img src="doc-files/NamedSamplePanel.png" alt="Screenshot image of class: NamedSamplePanel" >
 * 
 * @author Goran Stack
 *
 */
@SuppressWarnings("serial")
public class NamedSamplePanel extends JXPanel {

	public NamedSamplePanel(JComponent samplePanel, String name) {
		setLayout(new MigLayout(new LC().flowY()));
		setOpaque(false);
		add(samplePanel, new CC().alignX("center").pushY());
		JLabel label = new JLabel(name);
		label.setForeground(Color.GRAY);
		label.setOpaque(false);
		add(label, "center");
	}
}