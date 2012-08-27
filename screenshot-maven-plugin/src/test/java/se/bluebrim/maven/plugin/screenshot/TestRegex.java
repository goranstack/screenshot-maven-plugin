package se.bluebrim.maven.plugin.screenshot;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;

/**
 * A class for testing the regular expression used for adding screenshot
 * image tag to the Javadoc class comments.
 * 
 * @author Goran Stack
 *
 */
public class TestRegex implements FocusListener, DocumentListener
{
	private JTextArea textArea;
	private JLabel result;
	private JTextArea regexField;

	public static void main(String[] args) {
		final TestRegex instance = new TestRegex();
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
		String oneOreMoreWhiteSpace = "\\s*\\s";
		String literalDelimeter = "\"";
		String regex = "<img" + oneOreMoreWhiteSpace + "src" + 
		oneOreMoreWhiteSpace + "=" + oneOreMoreWhiteSpace + literalDelimeter + "doc-files/*" + literalDelimeter + oneOreMoreWhiteSpace + ">";

		result = new JLabel();
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setRows(5);
		textArea.setEditable(true);
		textArea.addFocusListener( this );
	    textArea.getDocument().addDocumentListener( this );
								
		JPanel panel = new JPanel(new MigLayout(new LC().wrapAfter(2).fillX().fillY()));
		panel.add(new JLabel("Regular expression:"));
		regexField = new JTextArea(regex);
		regexField.getDocument().addDocumentListener( this );
		regexField.addFocusListener( this );
		
		panel.add(regexField, "growx");
		panel.add(new JLabel("Find match in:"), new CC().spanY(5));
		panel.add(textArea, new CC().spanY(5).grow());
		panel.add(new JLabel("Result:"));
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
		try {
			Scanner scanner = new Scanner(textArea.getText());
			String hit = scanner.findInLine(regexField.getText());
			result.setText( "" + StringUtils.isNotBlank(hit));
			System.out.println("Update " + hit);
		} catch (Exception e) {
			result.setText(e.getMessage());
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
