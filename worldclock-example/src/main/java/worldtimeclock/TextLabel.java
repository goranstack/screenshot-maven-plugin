/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package worldtimeclock;

/**
 *
 * @author hansolo
 */
public class TextLabel extends javax.swing.JLabel
{
    final java.awt.Color TEXT_COLOR = new java.awt.Color(0xF0F0F0);

    public TextLabel()
    {
        super("");
    }
    

    @Override
    protected void paintComponent(java.awt.Graphics g)
    {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);

        final java.awt.FontMetrics METRICS = g2.getFontMetrics(g2.getFont());
        
        // Draw text shadow
        g2.setColor(java.awt.Color.BLACK);
        g2.drawString(getText(), getInsets().left + 1, (getHeight() - METRICS.getDescent()) + 1);

        // Draw text
        g2.setColor(TEXT_COLOR);
        g2.drawString(getText(), getInsets().left, (getHeight() - METRICS.getDescent()));

        g2.dispose();
    }
}
