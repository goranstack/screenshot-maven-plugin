package se.bluebrim.maven.plugin.screenshot;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXFrame;

/**
 * A class for testing HTML parsing of class comment to find the img tag
 * 
 * @author Goran Stack
 *
 */
public class TestHtmlParser implements FocusListener, DocumentListener
{
	private static final String HTML_SAMPLE = "<html>\n" +
			"<img src=\"doc-files/ColorConstants.png\">\n" +
			"<p> \nThis is an example of a resource class with visual samples included in the Javadoc. " +
			"The images for the Javadoc are created by a few lines of code in a test class. " +
			"The test class is processed by the screenshot-maven-plugin in a Maven build to produce the images. </p>\n" +
			"</html>";

	private JTextArea textArea;
	private JLabel result;

	public static void main(String[] args) {
		final TestHtmlParser instance = new TestHtmlParser();
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				instance.openInWindow();
			}
		});
	}

	private void openInWindow()
	{
		JXFrame window = new JXFrame(getClass().getSimpleName(), true);
		window.getContentPane().add(createPanel());
		window.setSize(1000, 800);
		window.setLocationRelativeTo(null);
		window.setVisible(true);		
	}

	private Component createPanel() 
	{
		result = new JLabel("Nothing");
		textArea = new JTextArea(HTML_SAMPLE);
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setEditable(true);
		textArea.addFocusListener( this );
	    textArea.getDocument().addDocumentListener( this );
								
		JPanel panel = new JPanel(new MigLayout(new LC().wrapAfter(2).fillX().fillY()));
		panel.add(new JLabel("HTML:"));
		panel.add(textArea, new CC().grow());
		panel.add(new JLabel("Image src path:"));
		panel.add(result);

		return panel;
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		update();		
	}

	private void update() {
		result.setText("nothing");
	    HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
			
			@Override
	    	public void handleText(char[] data, int pos) {
	        }

			@Override
			public void handleSimpleTag(Tag tag, MutableAttributeSet a, int pos) 
			{
				if (tag.equals(Tag.IMG))
				{
					Object attribute = a.getAttribute(HTML.Attribute.SRC);
					if (attribute instanceof String)
					{
						String srcAttribute = (String) attribute;
						result.setText(srcAttribute + " pos: " + pos);
					}
				}
			}
		};
	    try {
			new ParserDelegator().parse(new StringReader(textArea.getText()), callback, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		update();		
		
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update();		
		
	}

}
