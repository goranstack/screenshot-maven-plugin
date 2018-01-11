/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package worldtimeclock;

/**
 *
 * @author hansolo
 */
public class SunMoon extends javax.swing.JComponent
{
    private int timeOfDay = 0;
    public enum TYPE {SUNRISE, SUN, SUNSET, MOON};
    private final java.awt.image.BufferedImage SUNRISE_IMAGE = createSunMoonImage(TYPE.SUNRISE);
    private final java.awt.image.BufferedImage SUN_IMAGE = createSunMoonImage(TYPE.SUN);
    private final java.awt.image.BufferedImage SUNSET_IMAGE = createSunMoonImage(TYPE.SUNSET);
    private final java.awt.image.BufferedImage MOON_IMAGE = createSunMoonImage(TYPE.MOON);


    public SunMoon()
    {
        super();
        setPreferredSize(new java.awt.Dimension(24, 24));
        setSize(new java.awt.Dimension(24, 24));
    }

    @Override
    protected void paintComponent(java.awt.Graphics g)
    {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);

        switch(timeOfDay)
        {
            case -2:
                g2.drawImage(MOON_IMAGE, 0, 0 , null);
                break;

            case -1:
                g2.drawImage(SUNRISE_IMAGE, 0, 0 , null);
                break;

            case 0:
                g2.drawImage(SUN_IMAGE, 0, 0 , null);
                break;

            case 1:
                g2.drawImage(SUNSET_IMAGE, 0, 0 , null);
                break;

            default:
                g2.drawImage(SUN_IMAGE, 0, 0 , null);
                break;
        }
    }

    private java.awt.image.BufferedImage createSunMoonImage(TYPE type)
    {

        java.awt.GraphicsConfiguration gfxConf = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final java.awt.image.BufferedImage IMAGE = gfxConf.createCompatibleImage(24, 24, java.awt.Transparency.TRANSLUCENT);
        java.awt.Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        
        final java.awt.geom.Point2D START_SUN = new java.awt.geom.Point2D.Double(6, 4);
        final java.awt.geom.Point2D STOP_SUN = new java.awt.geom.Point2D.Double(16, 17);
        final float[] FRACTIONS_SUN =
        {
            0.0f,
            0.2f,
            0.6f,
            1.0f
        };
        final java.awt.Color[] COLORS_SUN =
        {
            new java.awt.Color(0xFDF589),
            new java.awt.Color(0xFEE438),
            new java.awt.Color(0xF6B31B),
            new java.awt.Color(0xEE891A)
        };
        final java.awt.LinearGradientPaint GRADIENT_SUN = new java.awt.LinearGradientPaint(START_SUN, STOP_SUN, FRACTIONS_SUN, COLORS_SUN);

        final java.awt.geom.Point2D CENTER_SUN = new java.awt.geom.Point2D.Double(11.5, 11.5);
        final float[] FRACTIONS_SUN_INNERSHADOW =
        {
            0.0f,
            0.6f,
            0.8f,
            1.0f
        };
        final java.awt.Color[] COLORS_INNERSHADOW =
        {
            new java.awt.Color(0.5f, 0.5f, 0.5f, 0.0f),
            new java.awt.Color(0.5f, 0.5f, 0.5f, 0.0f),
            new java.awt.Color(0.0f, 0.0f, 0.0f, 0.1f),
            new java.awt.Color(0.0f, 0.0f, 0.0f, 0.5f),
        };
        final java.awt.RadialGradientPaint GRADIENT_INNERSHADOW = new java.awt.RadialGradientPaint(CENTER_SUN, 10f, FRACTIONS_SUN_INNERSHADOW, COLORS_INNERSHADOW);

        final java.awt.geom.Point2D START_SUN_LIGHT = new java.awt.geom.Point2D.Double(0,4);
        final java.awt.geom.Point2D STOP_SUN_LIGHT = new java.awt.geom.Point2D.Double(0,12);
        final float[] FRACTIONS_SUN_LIGHT =
        {
            0.0f,
            1.0f
        };
        final java.awt.Color[] COLORS_SUN_LIGHT =
        {
            new java.awt.Color(1.0f, 1.0f, 1.0f, 0.4f),
            new java.awt.Color(1.0f, 1.0f, 1.0f, 0.05f),
        };
        final java.awt.LinearGradientPaint GRADIENT_SUN_LIGHT = new java.awt.LinearGradientPaint(START_SUN_LIGHT, STOP_SUN_LIGHT, FRACTIONS_SUN_LIGHT, COLORS_SUN_LIGHT);

        final java.awt.Color[] COLORS_SUNSET =
        {
            new java.awt.Color(0xFCCC89),
            new java.awt.Color(0xFCAB2E),
            new java.awt.Color(0xF25D0E),
            new java.awt.Color(0xE74811)
        };
        final java.awt.LinearGradientPaint GRADIENT_SUNSET = new java.awt.LinearGradientPaint(START_SUN, STOP_SUN, FRACTIONS_SUN, COLORS_SUNSET);

        final java.awt.geom.Point2D START_MOON = new java.awt.geom.Point2D.Double(0, 2);
        final java.awt.geom.Point2D STOP_MOON = new java.awt.geom.Point2D.Double(0, 21);
        final float[] FRACTIONS_MOON =
        {
            0.0f,
            1.0f
        };
        final java.awt.Color[] COLORS_MOON =
        {
            new java.awt.Color(0xFFFFFF),
            new java.awt.Color(0xAAAAAA)
        };
        final java.awt.LinearGradientPaint GRADIENT_MOON = new java.awt.LinearGradientPaint(START_MOON, STOP_MOON, FRACTIONS_MOON, COLORS_MOON);
        final java.awt.geom.Area MOON = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));
        MOON.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(1, 1, 15, 20)));


        switch(type)
        {
            case SUNRISE: // SUNRISE
                g2.translate(0, 12);
                // Draw shadow
                g2.setColor(new java.awt.Color(0x000000));
                g2.translate(1, 1);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }
                g2.translate(-1, -1);

                // Draw sun
                g2.setColor(new java.awt.Color(0xF0BE26));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }

                g2.setPaint(GRADIENT_SUN);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.setPaint(GRADIENT_INNERSHADOW);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.rotate(Math.toRadians(-10), 10, 8);
                g2.setPaint(GRADIENT_SUN_LIGHT);
                g2.fill(new java.awt.geom.Ellipse2D.Double(11, 8, 4, 4));
                g2.rotate(Math.toRadians(10), 10, 8);
                g2.translate(0, -12);
                break;

            case SUN: // SUN
                // Draw shadow
                g2.setColor(new java.awt.Color(0x000000));
                g2.translate(1, 1);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }
                g2.translate(-1, -1);

                // Draw sun
                g2.setColor(new java.awt.Color(0xF0BE26));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }

                g2.setPaint(GRADIENT_SUN);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.setPaint(GRADIENT_INNERSHADOW);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.rotate(Math.toRadians(-10), 10, 8);
                g2.setPaint(GRADIENT_SUN_LIGHT);
                g2.fill(new java.awt.geom.Ellipse2D.Double(11, 8, 4, 4));
                g2.rotate(Math.toRadians(10), 10, 8);                
                break;

            case SUNSET:
                // Draw shadow
                g2.translate(0, 12);
                g2.setColor(new java.awt.Color(0x000000));
                g2.translate(1, 1);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));                
                g2.translate(-1, -1);

                // Draw sun
                g2.setPaint(GRADIENT_SUNSET);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.setPaint(GRADIENT_INNERSHADOW);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.rotate(Math.toRadians(-10), 10, 8);
                g2.setPaint(GRADIENT_SUN_LIGHT);
                g2.fill(new java.awt.geom.Ellipse2D.Double(11, 8, 4, 4));
                g2.rotate(Math.toRadians(10), 10, 8);
                g2.translate(0, -12);
                break;

            case MOON: // MOON
                // Draw shadow
                g2.rotate(Math.toRadians(10), 12 ,12);
                g2.setColor(new java.awt.Color(0x000000));
                g2.translate(1, 1);
                g2.fill(MOON);
                g2.translate(-1, -1);

                // Draw moon
                g2.setPaint(GRADIENT_MOON);
                g2.fill(MOON);

                g2.setPaint(GRADIENT_INNERSHADOW);
                g2.fill(MOON);
                g2.rotate(Math.toRadians(-10), 12, 12);
                break;

            default: // SUN
                // Draw shadow
                g2.setColor(new java.awt.Color(0x000000));
                g2.translate(1, 1);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }
                g2.translate(-1, -1);

                // Draw sun
                g2.setColor(new java.awt.Color(0xF0BE26));
                for (int alpha = 0 ; alpha < 360 ; alpha += 15)
                {
                    g2.rotate(Math.toRadians(alpha), 12, 12);
                    g2.draw(new java.awt.geom.Line2D.Double(12, 1, 12, 20));
                    g2.rotate(Math.toRadians(-alpha), 12, 12);
                }

                g2.setPaint(GRADIENT_SUN);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.setPaint(GRADIENT_INNERSHADOW);
                g2.fill(new java.awt.geom.Ellipse2D.Double(2, 2, 20, 20));

                g2.rotate(Math.toRadians(-10), 10, 8);
                g2.setPaint(GRADIENT_SUN_LIGHT);
                g2.fill(new java.awt.geom.Ellipse2D.Double(11, 8, 4, 4));
                g2.rotate(Math.toRadians(10), 10, 8);                
                break;
        }

        g2.dispose();

        return IMAGE;
    }

    public int getTimeOfDay()
    {
        return this.timeOfDay;
    }

    public void setTimeOfDay(int timeOfDay)
    {
        this.timeOfDay = timeOfDay;
        repaint();
    }

    @Override
    public java.awt.Dimension getSize()
    {
        return new java.awt.Dimension(24, 24);
    }

    @Override
    public java.awt.Dimension getSize(java.awt.Dimension dim)
    {
        return new java.awt.Dimension(24, 24);
    }

}