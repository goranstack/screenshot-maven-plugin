/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package worldtimeclock;



/**
 *
 * @author hansolo
 */
public class AnalogClockDayNight extends javax.swing.JComponent implements java.awt.event.ActionListener
{
    private final double ANGLE_STEP = 6;
    private final double NIGHT_DAY_ANGLE_STEP = 0.25;
    private final javax.swing.Timer CLOCK_TIMER = new javax.swing.Timer(100, this);
    private double minutePointerAngle = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE) * ANGLE_STEP;
    private double hourPointerAngle = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR) * ANGLE_STEP * 5 + 0.5 * java.util.Calendar.getInstance().get( java.util.Calendar.MINUTE);
    private double secondPointerAngle = java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) * ANGLE_STEP;
    private double nightDayAngle = (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) * 60 + java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE)) * NIGHT_DAY_ANGLE_STEP;
    private java.awt.geom.Rectangle2D hourPointer;
    private java.awt.geom.Rectangle2D minutePointer;
    private java.awt.geom.GeneralPath secondPointer;
    private java.awt.geom.Ellipse2D centerKnob;
    private java.awt.geom.Rectangle2D smallTick;
    private java.awt.geom.Rectangle2D bigTick;
    private final java.awt.Color SECOND_COLOR = new java.awt.Color(0xF00000);
    private final java.awt.Color SHADOW_COLOR = new java.awt.Color(0.0f, 0.0f, 0.0f, 0.65f);
    private java.awt.image.BufferedImage backgroundImage = null;
    private java.awt.image.BufferedImage nightDayImage = null;    
    private final java.awt.Stroke STAR_STROKE = new java.awt.BasicStroke(0.9f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND);
    public enum TYPE
    {
        LIGHT,
        DARK
    };
    private TYPE type = TYPE.DARK;
    private static final String TIMEOFDAY_PROPERTY = "timeOfDay";
    private int timeOfDay = 0;
    private static final String DAYOFFSET_PROPERTY = "dayOffset";
    private int dayOffset = 0;
    private java.awt.Color currentForegroundColor;
    private java.awt.Color[] currentBackgroundColor;
    private java.awt.geom.Point2D center;
    private int timeZoneOffsetHour = 0;
    private int timeZoneOffsetMinute = 0;    
    private int hour;
    private int minute;
    boolean am = (java.util.Calendar.getInstance().get(java.util.Calendar.AM_PM) == java.util.Calendar.AM);
    // Flags
    private boolean secondPointerVisible = true;
    private boolean autoType = true;

    public AnalogClockDayNight()
    {
        super();
        setSize(66, 66);

        init();

        CLOCK_TIMER.start();        
    }

    private void init()
    {
        // Rotation center
        this.center = new java.awt.geom.Point2D.Float(getWidth() / 2.0f, getWidth() / 2.0f);

        // Hour pointer
        final double HOUR_POINTER_WIDTH = getWidth() * 0.0545454545;
        final double HOUR_POINTER_HEIGHT = getWidth() * 0.3090909091;
        this.hourPointer = new java.awt.geom.Rectangle2D.Double(center.getX() - (HOUR_POINTER_WIDTH / 2), (getWidth() * 0.1909090909), HOUR_POINTER_WIDTH, HOUR_POINTER_HEIGHT);

        // Minute pointer
        final double MINUTE_POINTER_WIDTH = getWidth() * 0.0454545455;
        final double MINUTE_POINTER_HEIGHT = getWidth() * 0.4363636364;
        this.minutePointer = new java.awt.geom.Rectangle2D.Double(center.getX() - (MINUTE_POINTER_WIDTH / 2), (getWidth() * 0.0636363636), MINUTE_POINTER_WIDTH, MINUTE_POINTER_HEIGHT);

        // Second pointer
        final java.awt.geom.GeneralPath SECOND_AREA = new java.awt.geom.GeneralPath();
        SECOND_AREA.moveTo(getWidth() * 0.4863636364, center.getY());
        SECOND_AREA.lineTo(getWidth() * 0.5136363636, center.getY());
        SECOND_AREA.lineTo(getWidth() * 0.5045454545, getWidth() * 0.0363636364);
        SECOND_AREA.lineTo(getWidth() * 0.4954545455, getWidth() * 0.0363636364);
        SECOND_AREA.closePath();
        java.awt.geom.Area second = new java.awt.geom.Area(SECOND_AREA);
        second.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(getWidth() * 0.4545454545, getWidth() * 0.1454545455, getWidth() * 0.0909090909, getWidth() * 0.0909090909)));
        second.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(getWidth() * 0.4636363636, getWidth() * 0.1545454545, getWidth() * 0.0727272727, getWidth() * 0.0727272727)));
        this.secondPointer = new java.awt.geom.GeneralPath(second);
        
        // Center knob
        final double CENTER_KNOB_DIAMETER = getWidth() * 0.090909;
        this.centerKnob = new java.awt.geom.Ellipse2D.Double(center.getX() - CENTER_KNOB_DIAMETER / 2, center.getY() - CENTER_KNOB_DIAMETER / 2, CENTER_KNOB_DIAMETER, CENTER_KNOB_DIAMETER);

        // Minute tick mark
        final double SMALL_TICK_WIDTH = getWidth() * 0.0181818;
        final double SMALL_TICK_HEIGHT = getWidth() * 0.0363636;
        this.smallTick = new java.awt.geom.Rectangle2D.Double(center.getX() - (SMALL_TICK_WIDTH / 2), SMALL_TICK_HEIGHT, SMALL_TICK_WIDTH, SMALL_TICK_HEIGHT);

        // Hour tick mark
        final double BIG_TICK_WIDTH = getWidth() * 0.0363636;
        final double BIG_TICK_HEIGHT = getWidth() * 0.10909090;
        this.bigTick = new java.awt.geom.Rectangle2D.Double(center.getX() - (BIG_TICK_WIDTH / 2), SMALL_TICK_HEIGHT, BIG_TICK_WIDTH, BIG_TICK_HEIGHT);

        switch (type)
        {
            case LIGHT:
                this.currentForegroundColor = java.awt.Color.BLACK;
                this.currentBackgroundColor = new java.awt.Color[]
                {
                    new java.awt.Color(0xF7F7F7),
                    new java.awt.Color(0xF0F0F0)
                };
                break;

            case DARK:
                this.currentForegroundColor = java.awt.Color.WHITE;
                this.currentBackgroundColor = new java.awt.Color[]
                {
                    new java.awt.Color(0x3E3B32),
                    new java.awt.Color(0x232520)
                };
                break;

            default:
                this.currentForegroundColor = java.awt.Color.WHITE;
                this.currentBackgroundColor = new java.awt.Color[]
                {
                    new java.awt.Color(0x3E3B32),
                    new java.awt.Color(0x232520)
                };
                break;
        }
        this.backgroundImage = null;
        repaint(0, 0, getWidth(), getWidth());
    }

    @Override
    protected void paintComponent(java.awt.Graphics g)
    {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        java.awt.geom.AffineTransform oldTransform = g2.getTransform();        

        // Draw daynight image in the background
        g2.rotate(Math.toRadians(nightDayAngle), center.getX(), center.getY());
        if (nightDayImage == null)
        {
            nightDayImage = createNightDayImage();
        }
        g2.drawImage(nightDayImage, (int) (getWidth() * 0.21), (int) (getWidth() * 0.21), null);
        g2.rotate(Math.toRadians(-nightDayAngle), center.getX(), center.getY());

        // Draw background image
        if (backgroundImage == null)
        {
            backgroundImage = createBackgroundImage();
        }        
        g2.drawImage(backgroundImage, 0, 0, null);        
        
        // Draw hour pointer
//        g2.setTransform(oldTransform);
//        g2.setColor(SHADOW_COLOR);
//        g2.rotate(Math.toRadians(hourPointerAngle + (1 * Math.sin(Math.toRadians(hourPointerAngle)))), center.getX(), center.getY());
//        g2.fill(hourPointer);
//
        g2.setTransform(oldTransform);
        g2.setColor(currentForegroundColor);
        g2.rotate(Math.toRadians(hourPointerAngle), center.getX(), center.getY());
        g2.fill(hourPointer);

        // Draw minute pointer
        // Draw pointer shadow if background is dark and pointer is white
        if (getType().equals(TYPE.DARK))
        {
            g2.setTransform(oldTransform);
            g2.setColor(SHADOW_COLOR);
            g2.rotate(Math.toRadians(minutePointerAngle + (1 * Math.sin(Math.toRadians(minutePointerAngle)))), center.getX(), center.getY());
            g2.fill(minutePointer);
        }
        g2.setTransform(oldTransform);
        g2.setColor(currentForegroundColor);
        g2.rotate(Math.toRadians(minutePointerAngle), center.getX(), center.getY());
        g2.fill(minutePointer);

        if (secondPointerVisible)
        {
            // Draw second pointer
            g2.setTransform(oldTransform);
            g2.setColor(SHADOW_COLOR);
            g2.rotate(Math.toRadians(secondPointerAngle + (2 * Math.sin(Math.toRadians(secondPointerAngle)))), center.getX(), center.getY());
            g2.fill(secondPointer);

            g2.setTransform(oldTransform);
            g2.setColor(SECOND_COLOR);
            g2.rotate(Math.toRadians(secondPointerAngle), center.getX(), center.getY());
            g2.fill(secondPointer);
        }        
        g2.setTransform(oldTransform);

        g2.setColor(currentForegroundColor);
        g2.fill(centerKnob);
    }

    public TYPE getType()
    {
        return this.type;
    }

    public void setType(TYPE type)
    {
        this.type = type;
        init();
    }

    public boolean isSecondPointerVisible()
    {
        return this.secondPointerVisible;
    }

    public void setSecondPointerVisible(boolean secondPointerVisible)
    {
        this.secondPointerVisible = secondPointerVisible;

        /*
         * Adjust the timer delay due to the visibility
         * of the second pointer.
         */
        if (secondPointerVisible)
        {
            CLOCK_TIMER.stop();
            CLOCK_TIMER.setDelay(100);
            CLOCK_TIMER.start();
        }
        else
        {
            CLOCK_TIMER.stop();
            CLOCK_TIMER.setDelay(1000);
            CLOCK_TIMER.start();
        }
        init();
    }

    public boolean isAutoType()
    {
        return this.autoType;
    }

    public void setAutoType(boolean autoType)
    {
        this.autoType = autoType;
    }

    public int getTimeZoneOffsetHour()
    {
        return this.timeZoneOffsetHour;
    }

    public void setTimeZoneOffsetHour(int timeZoneOffsetHour)
    {
        this.timeZoneOffsetHour = timeZoneOffsetHour;
    }

    public int getTimeZoneOffsetMinute()
    {
        return this.timeZoneOffsetMinute;
    }

    public void setTimeZoneOffsetMinute(int timeZoneOffsetMinute)
    {
        this.timeZoneOffsetMinute = timeZoneOffsetMinute;        
    }

    private java.awt.image.BufferedImage createBackgroundImage()
    {
        java.awt.GraphicsConfiguration gfxConf = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final java.awt.image.BufferedImage IMAGE = gfxConf.createCompatibleImage(getWidth(), getWidth(), java.awt.Transparency.TRANSLUCENT);
        java.awt.Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);

        java.awt.geom.AffineTransform oldTransform = g2.getTransform();

        // Define shape for the window
        final java.awt.geom.Arc2D OUTER_PIE = new java.awt.geom.Arc2D.Double(IMAGE.getWidth() * 0.21021, IMAGE.getHeight() * 0.21021, IMAGE.getWidth() * 0.58558, IMAGE.getHeight() * 0.58558, 25, 130, java.awt.geom.Arc2D.PIE);
        final java.awt.geom.Arc2D INNER_PIE = new java.awt.geom.Arc2D.Double((IMAGE.getWidth() * 0.38438), (IMAGE.getHeight() * 0.38438), (IMAGE.getWidth() * 0.23723), (IMAGE.getHeight() * 0.23723), 0, 180, java.awt.geom.Arc2D.PIE);
        final java.awt.geom.Area WINDOW = new java.awt.geom.Area(OUTER_PIE);
        WINDOW.subtract(new java.awt.geom.Area(INNER_PIE));
        final java.awt.geom.Area CLOCK_BACKGROUND_WITH_WINDOW = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Float(1, 1, getWidth() - 2, getWidth() - 2));
        CLOCK_BACKGROUND_WITH_WINDOW.subtract(WINDOW);

        final java.awt.geom.Area BACKGROUND_GRADIENT_WITH_WINDOW = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getWidth()));
        BACKGROUND_GRADIENT_WITH_WINDOW.subtract(WINDOW);

        // Draw clock background
        final java.awt.geom.Point2D LIGHT_START = new java.awt.geom.Point2D.Float(0, 0);
        final java.awt.geom.Point2D LIGHT_STOP = new java.awt.geom.Point2D.Float(0, getWidth());
        final float[] LIGHT_FRACTIONS =
        {
            0.0f,
            1.0f
        };
        final java.awt.Color[] LIGHT_COLORS =
        {
            new java.awt.Color(0x000000),
            new java.awt.Color(0x645E54)
        };
        final java.awt.LinearGradientPaint LIGHT_GRADIENT = new java.awt.LinearGradientPaint(LIGHT_START, LIGHT_STOP, LIGHT_FRACTIONS, LIGHT_COLORS);
        g2.setPaint(LIGHT_GRADIENT);        
        g2.fill(BACKGROUND_GRADIENT_WITH_WINDOW);

        final java.awt.geom.Point2D BACKGROUND_START = new java.awt.geom.Point2D.Float(0, 1);
        final java.awt.geom.Point2D BACKGROUND_STOP = new java.awt.geom.Point2D.Float(0, getWidth() - 2);
        final float[] BACKGROUND_FRACTIONS =
        {
            0.0f,
            1.0f
        };
        final java.awt.Color[] BACKGROUND_COLORS = currentBackgroundColor;
        final java.awt.LinearGradientPaint BACKGROUND_GRADIENT = new java.awt.LinearGradientPaint(BACKGROUND_START, BACKGROUND_STOP, BACKGROUND_FRACTIONS, BACKGROUND_COLORS);
        g2.setPaint(BACKGROUND_GRADIENT);
        g2.fill(CLOCK_BACKGROUND_WITH_WINDOW);        

        // Draw window frame
        final java.awt.geom.Point2D WINDOW_START = new java.awt.geom.Point2D.Double(0, IMAGE.getHeight() * 0.21021);
        final java.awt.geom.Point2D WINDOW_STOP = new java.awt.geom.Point2D.Double(0, IMAGE.getHeight() * 0.38438);
        final float[] WINDOW_FRACTIONS =
        {
            0.0f,
            0.8f,
            1.0f
        };
        final java.awt.Color[] WINDOW_COLORS;
        if (type == TYPE.DARK)
        {
            WINDOW_COLORS = new java.awt.Color[]
            {
                new java.awt.Color(0x000000),
                new java.awt.Color(0x333333),
                new java.awt.Color(0xCCCCCC)
            };
        }
        else
        {
            WINDOW_COLORS = new java.awt.Color[]
            {
                new java.awt.Color(0x333333),
                new java.awt.Color(0x666666),
                new java.awt.Color(0xAAAAAA)
            };
        }
        final java.awt.LinearGradientPaint WINDOW_GRADIENT = new java.awt.LinearGradientPaint(WINDOW_START, WINDOW_STOP, WINDOW_FRACTIONS, WINDOW_COLORS);
        g2.setPaint(WINDOW_GRADIENT);
        g2.draw(WINDOW);

        // Draw minutes tickmarks
        g2.setColor(currentForegroundColor);
        for (int tickAngle = 0 ; tickAngle < 360 ; tickAngle += 6)
        {
            g2.setTransform(oldTransform);
            g2.rotate(Math.toRadians(tickAngle), center.getX(), center.getY());
            g2.fill(smallTick);
        }

        // Draw hours tickmarks
        for (int tickAngle = 0 ; tickAngle < 360 ; tickAngle += 30)
        {
            g2.setTransform(oldTransform);
            g2.rotate(Math.toRadians(tickAngle), center.getX(), center.getY());
            g2.fill(bigTick);
        }

        g2.setTransform(oldTransform);

        g2.dispose();

        return IMAGE;
    }

    private java.awt.image.BufferedImage createNightDayImage()
    {
        java.awt.GraphicsConfiguration gfxConf = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        final java.awt.image.BufferedImage IMAGE = gfxConf.createCompatibleImage((int)(getWidth() * 0.585), (int)(getWidth() * 0.585), java.awt.Transparency.TRANSLUCENT);
        java.awt.Graphics2D g2 = IMAGE.createGraphics();

        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION, java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_COLOR_RENDERING, java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        final java.awt.geom.Point2D CENTER = new java.awt.geom.Point2D.Double(IMAGE.getWidth() / 2, IMAGE.getHeight() / 2);

        // Rotate image
        //g2.rotate(Math.toRadians(nightDayAngle) ,CENTER.getX(), CENTER.getY());
        
        // Draw conical gradient for day-night-background
        final float[] FRACTIONS =
        {
            0.0f,
            0.10f,
            0.14f,
            0.18f,
            0.32f,
            0.68f,
            0.82f,
            0.86f,
            0.90f,
            1.0f
        };

        final java.awt.Color[] COLORS =
        {
            new java.awt.Color(0x000000),
            new java.awt.Color(0x000000),
            new java.awt.Color(0x332200),
            new java.awt.Color(0x664411),
            new java.awt.Color(0x85A4C3),
            new java.awt.Color(0x85A4C3),
            new java.awt.Color(0x004466),
            new java.awt.Color(0x002233),
            new java.awt.Color(0x000000),
            new java.awt.Color(0x000000)
        };

        final ConicalGradientPaint CONICAL_GRADIENT_PAINT = new ConicalGradientPaint(CENTER, nightDayAngle, FRACTIONS, COLORS);
        g2.setPaint(CONICAL_GRADIENT_PAINT);
        g2.fill(new java.awt.geom.Ellipse2D.Double(0, 0, IMAGE.getWidth(), IMAGE.getHeight()));        
                
        // Draw sun

        // Draw sun beams
        g2.setColor(new java.awt.Color(0xF0BE26));
        for (int alpha = 0 ; alpha < 360 ; alpha += 15)
        {
            g2.rotate(Math.toRadians(alpha), IMAGE.getWidth() * 0.49, IMAGE.getHeight() * 0.86206);
            g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.49, IMAGE.getHeight() * 0.86206, IMAGE.getWidth() * 0.51, IMAGE.getHeight() * 0.86206 - IMAGE.getWidth() * 0.12));
            g2.rotate(Math.toRadians(-alpha), IMAGE.getWidth() * 0.49, IMAGE.getWidth() * 0.86206);
        }

        // Draw sun body
        final java.awt.geom.Point2D START_SUN = new java.awt.geom.Point2D.Double(0, IMAGE.getHeight() * 0.75862);
        final java.awt.geom.Point2D STOP_SUN = new java.awt.geom.Point2D.Double(0, IMAGE.getHeight() * 0.96551);

        final float[] FRACTIONS_SUN =
        {
            0.0f,
            0.4f,
            0.8f,
            1.0f
        };
        final java.awt.Color[] COLORS_SUN =
        {
            new java.awt.Color(0xEE891A),
            new java.awt.Color(0xF6B31B),
            new java.awt.Color(0xFEE438),
            new java.awt.Color(0xFDF589)
        };
        final java.awt.LinearGradientPaint GRADIENT_SUN = new java.awt.LinearGradientPaint(START_SUN, STOP_SUN, FRACTIONS_SUN, COLORS_SUN);

        final java.awt.geom.Point2D CENTER_SUN = new java.awt.geom.Point2D.Double(IMAGE.getWidth() * 0.5, IMAGE.getHeight() * 0.86206);
        final float[] FRACTIONS_INNERSHADOW =
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
        final java.awt.RadialGradientPaint GRADIENT_SUN_INNERSHADOW = new java.awt.RadialGradientPaint(CENTER_SUN, IMAGE.getWidth() * 0.103445f, FRACTIONS_INNERSHADOW, COLORS_INNERSHADOW);
        
        final java.awt.geom.Ellipse2D SUN = new java.awt.geom.Ellipse2D.Double(IMAGE.getWidth() * 0.39316, IMAGE.getHeight() * 0.75862, IMAGE.getWidth() * 0.20512, IMAGE.getHeight() * 0.20512);
        g2.setPaint(GRADIENT_SUN);
        g2.fill(SUN);
        g2.setPaint(GRADIENT_SUN_INNERSHADOW);
        g2.fill(SUN);


        // Draw clouds
        final java.awt.geom.Area CLOUD = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.71 * IMAGE.getWidth(), 0.78 * IMAGE.getHeight(), 0.1 * IMAGE.getWidth(), 0.06 * IMAGE.getWidth()));
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.73 * IMAGE.getWidth(), 0.75 * IMAGE.getHeight(), 0.09 * IMAGE.getWidth(), 0.05 * IMAGE.getWidth())));
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.76 * IMAGE.getWidth(), 0.73 * IMAGE.getHeight(), 0.04 * IMAGE.getWidth(), 0.03 * IMAGE.getWidth())));
        
        final java.awt.geom.GeneralPath CLOUD1 = new java.awt.geom.GeneralPath(CLOUD);

        CLOUD.reset();
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.28 * IMAGE.getWidth(), 0.78 * IMAGE.getHeight(), 0.07 * IMAGE.getWidth(), 0.06 * IMAGE.getWidth())));
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.23 * IMAGE.getWidth(), 0.72 * IMAGE.getHeight(), 0.08 * IMAGE.getWidth(), 0.12 * IMAGE.getWidth())));
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.16 * IMAGE.getWidth(), 0.71 * IMAGE.getHeight(), 0.16 * IMAGE.getWidth(), 0.07 * IMAGE.getWidth())));
        CLOUD.add(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(0.21 * IMAGE.getWidth(), 0.68 * IMAGE.getHeight(), 0.05 * IMAGE.getWidth(), 0.07 * IMAGE.getWidth())));
        
        final java.awt.geom.GeneralPath CLOUD2 = new java.awt.geom.GeneralPath(CLOUD);

        CLOUD.reset();

        g2.setColor(new java.awt.Color(1.0f, 1.0f, 1.0f, 0.9f));
        g2.fill(CLOUD1);
        g2.fill(CLOUD2);

        // Draw moon
        final java.awt.geom.Point2D START_MOON = new java.awt.geom.Point2D.Double(0, IMAGE.getHeight() * 0.01709);
        final java.awt.geom.Point2D STOP_MOON = new java.awt.geom.Point2D.Double(0,IMAGE.getHeight() * 0.24137);
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
        final java.awt.geom.Point2D MOON_CENTER = new java.awt.geom.Point2D.Double(IMAGE.getWidth() * 0.5, IMAGE.getHeight() * 0.13793);

        final java.awt.LinearGradientPaint GRADIENT_MOON = new java.awt.LinearGradientPaint(START_MOON, STOP_MOON, FRACTIONS_MOON, COLORS_MOON);
        final java.awt.geom.Area MOON = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(IMAGE.getWidth() * 0.376, IMAGE.getHeight() * 0.01709, IMAGE.getWidth() * 0.22413, IMAGE.getHeight() * 0.22413));
        MOON.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(IMAGE.getWidth() * 0.32482, 0, IMAGE.getWidth() * 0.22137, IMAGE.getHeight() * 0.21689)));
        final java.awt.RadialGradientPaint GRADIENT_MOON_INNERSHADOW = new java.awt.RadialGradientPaint(MOON_CENTER, IMAGE.getWidth() * 0.11206f, FRACTIONS_INNERSHADOW, COLORS_INNERSHADOW);

        g2.setPaint(GRADIENT_MOON);
        g2.fill(MOON);
        g2.setPaint(GRADIENT_MOON_INNERSHADOW);
        g2.fill(MOON);

        // Draw stars
        g2.setStroke(STAR_STROKE);
        g2.setColor(new java.awt.Color(0xFFFFFF));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.18965, IMAGE.getHeight() * 0.31034, IMAGE.getWidth() * 0.18965, IMAGE.getHeight() * 0.31034));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.15517, IMAGE.getHeight() * 0.22413, IMAGE.getWidth() * 0.15517, IMAGE.getHeight() * 0.22413));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.25862, IMAGE.getHeight() * 0.20689, IMAGE.getWidth() * 0.25862, IMAGE.getHeight() * 0.20689));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.36206, IMAGE.getHeight() * 0.15517, IMAGE.getWidth() * 0.36206, IMAGE.getHeight() * 0.15517));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.32758, IMAGE.getHeight() * 0.10344, IMAGE.getWidth() * 0.32758, IMAGE.getHeight() * 0.10344));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.41379, IMAGE.getHeight() * 0.06896, IMAGE.getWidth() * 0.41379, IMAGE.getHeight() * 0.06896));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.68965, IMAGE.getHeight() * 0.10344, IMAGE.getWidth() * 0.68965, IMAGE.getHeight() * 0.10344));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.65517, IMAGE.getHeight() * 0.15517, IMAGE.getWidth() * 0.65517, IMAGE.getHeight() * 0.15517));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.74137, IMAGE.getHeight() * 0.15517, IMAGE.getWidth() * 0.74137, IMAGE.getHeight() * 0.15517));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.75862, IMAGE.getHeight() * 0.20689, IMAGE.getWidth() * 0.75862, IMAGE.getHeight() * 0.20689));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.82758, IMAGE.getHeight() * 0.20689, IMAGE.getWidth() * 0.82758, IMAGE.getHeight() * 0.20689));
        g2.draw(new java.awt.geom.Line2D.Double(IMAGE.getWidth() * 0.79310, IMAGE.getHeight() * 0.24137, IMAGE.getWidth() * 0.79310, IMAGE.getHeight() * 0.24137));


        g2.dispose();

        return IMAGE;
    }
    
    @Override
    public void setPreferredSize(java.awt.Dimension dimension)
    {
        if (dimension.width >= dimension.height)
        {
            super.setSize(new java.awt.Dimension(dimension.width, dimension.width));
        }
        else
        {
            super.setSize(new java.awt.Dimension(dimension.height, dimension.height));
        }
        if (nightDayImage != null)
        {
            nightDayImage.flush();
        }
        nightDayImage = null;
        init();
    }

    @Override
    public void setSize(java.awt.Dimension dimension)
    {
        if (dimension.width >= dimension.height)
        {
            super.setSize(new java.awt.Dimension(dimension.width, dimension.width));
        }
        else
        {
            super.setSize(new java.awt.Dimension(dimension.height, dimension.height));
        }
        if (nightDayImage != null)
        {
            nightDayImage.flush();
        }
        nightDayImage = null;
        init();
    }

    @Override
    public void setSize(int width, int height)
    {
        if (width >= height)
        {
            super.setPreferredSize(new java.awt.Dimension(width, width));
            super.setSize(width, width);
        }
        else
        {
            super.setPreferredSize(new java.awt.Dimension(height, height));
            super.setSize(height, height);
        }
        if (nightDayImage != null)
        {
            nightDayImage.flush();
        }
        nightDayImage = null;
        init();
    }


    @Override
    public void actionPerformed(java.awt.event.ActionEvent event)
    {
        if (event.getSource().equals(CLOCK_TIMER))
        {   
            // Seconds
            secondPointerAngle = java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) * ANGLE_STEP + java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND) * ANGLE_STEP / 1000;
            
            // Hours
            hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR) - this.timeZoneOffsetHour;
            if (hour > 12)
            {
                hour -= 12;                
            }
            if (hour < 0)
            {
                hour += 12;                
            }            

            // Minutes
            minute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE) + this.timeZoneOffsetMinute;
            if (minute > 60)
            {
                minute -= 60;
                hour++;                
            }
            if (minute < 0)
            {
                minute += 60;
                hour--;
            }

            // Calculate angles from current hour and minute values            
            hourPointerAngle = hour * ANGLE_STEP * 5 + (0.5) * minute;            
            minutePointerAngle = minute * ANGLE_STEP;

            if (java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) == 0 && java.util.Calendar.getInstance().get(java.util.Calendar.MILLISECOND) < 150)
            {                                
                nightDayAngle = ((java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - timeZoneOffsetHour) * 60 + java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE) + timeZoneOffsetMinute) * NIGHT_DAY_ANGLE_STEP;
                nightDayImage.flush();
                nightDayImage = null;                
            }
            
            // AutoType
            if (autoType)
            {                                
                if (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - timeZoneOffsetHour < 4)
                {
                    // Night
                    int oldTimeOfDay = this.timeOfDay;
                    setType(type.DARK);
                    this.timeOfDay = -2;
                    firePropertyChange(TIMEOFDAY_PROPERTY, oldTimeOfDay, timeOfDay);                    
                }
                else if (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - timeZoneOffsetHour < 6)
                {
                    // Sunrise
                    int oldTimeOfDay = this.timeOfDay;
                    setType(type.DARK);
                    this.timeOfDay = -1;
                    firePropertyChange(TIMEOFDAY_PROPERTY, oldTimeOfDay, timeOfDay);                    
                }
                else if(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - this.timeZoneOffsetHour >= 20)
                {
                    // Night
                    int oldTimeOfDay = this.timeOfDay;
                    setType(type.DARK);
                    this.timeOfDay = -2;
                    firePropertyChange(TIMEOFDAY_PROPERTY, oldTimeOfDay, timeOfDay);
                }
                else if(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - this.timeZoneOffsetHour >= 18)
                {
                    // Sunset
                    int oldTimeOfDay = this.timeOfDay;
                    setType(type.DARK);
                    this.timeOfDay = 1;
                    firePropertyChange(TIMEOFDAY_PROPERTY, oldTimeOfDay, timeOfDay);                    
                }                
                else
                {
                    // Day
                    int oldTimeOfDay = this.timeOfDay;
                    setType(type.LIGHT);
                    this.timeOfDay = 0;
                    firePropertyChange(TIMEOFDAY_PROPERTY, oldTimeOfDay, timeOfDay);                    
                }

                if (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - this.timeZoneOffsetHour >= 24)
                {
                    int oldDayOffset = this.dayOffset;
                    this.dayOffset = 1;

                    firePropertyChange(DAYOFFSET_PROPERTY, oldDayOffset, dayOffset);
                }
                else if (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) - this.timeZoneOffsetHour < 0)
                {
                    int oldDayOffset = this.dayOffset;
                    this.dayOffset = -1;
                    firePropertyChange(DAYOFFSET_PROPERTY, oldDayOffset, dayOffset);
                }
                else
                {
                    int oldDayOffset = this.dayOffset;
                    this.dayOffset = 0;
                    firePropertyChange(DAYOFFSET_PROPERTY, oldDayOffset, dayOffset);
                }
            }
            repaint(0, 0, getWidth(), getWidth());
        }
    }

    @Override
    public String toString()
    {
        return "DayNight Analog Clock";
    }
}
