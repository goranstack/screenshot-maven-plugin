/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldtimeclock;


public class ConicalGradientPaint implements java.awt.Paint
{
    private final java.awt.geom.Point2D CENTER;
    private final double OFFSET;
    private final double[] FRACTION_ANGLE;
    private final double[] RED_STEP_LOOKUP;
    private final double[] GREEN_STEP_LOOKUP;
    private final double[] BLUE_STEP_LOOKUP;
    private final double[] ALPHA_STEP_LOOKUP;
    private final java.awt.Color[] COLORS;
    final double COLOR_FACTOR = 1.0 / 255.0;

    public ConicalGradientPaint(java.awt.geom.Point2D center, float[] fractions, java.awt.Color[] colors) throws IllegalArgumentException
    {
        this(center, 0.0, fractions, colors);
    }

    public ConicalGradientPaint(java.awt.geom.Point2D center, double offset, float[] fractions, java.awt.Color[] colors) throws IllegalArgumentException
    {        
        super();
        
        if (fractions.length != colors.length)
        {
            throw new IllegalArgumentException("Fractions and colors must equal in size");
        }

        // Adjust fractions and colors array in the case where startvalue != 0.0f and/or endvalue != 1.0f
        java.util.List<Float> fractionList = new java.util.ArrayList<Float>(fractions.length);
        for (float fraction : fractions)
        {
            fractionList.add(fraction);
        }
        java.util.List<java.awt.Color> colorList = new java.util.ArrayList<java.awt.Color>(colors.length);
        for (java.awt.Color color : colors)
        {
            colorList.add(color);
        }
        
        if (fractionList.get(0) != 0.0f)
        {
            fractionList.add(0, 0.0f);
            java.awt.Color tmpColor = colorList.get(0);
            colorList.add(0, tmpColor);
        }

        if (fractionList.get(fractionList.size() - 1) != 1.0f)
        {
            fractionList.add(1.0f);            
            colorList.add(colors[0]);
        }

        Float[] tmpFractions = fractionList.toArray(new Float[]{});
        java.awt.Color[] tmpColors = colorList.toArray(new java.awt.Color[]{});

        this.CENTER = center;
        this.OFFSET = offset;
        this.COLORS = tmpColors;

        // Prepare lookup table for the angles of each fraction
        this.FRACTION_ANGLE = new double[tmpFractions.length];
        for (int i = 0 ; i < tmpFractions.length ; i++)
        {                       
            FRACTION_ANGLE[i] = tmpFractions[i] * 360;
        }

        // Prepare lookup tables for the color stepsize of each color
        RED_STEP_LOOKUP = new double[COLORS.length];
        GREEN_STEP_LOOKUP = new double[COLORS.length];
        BLUE_STEP_LOOKUP = new double[COLORS.length];
        ALPHA_STEP_LOOKUP = new double[COLORS.length];

        for (int i = 0 ; i < (COLORS.length - 1) ; i++)
        {            
            RED_STEP_LOOKUP[i] = ((COLORS[i + 1].getRed() - COLORS[i].getRed()) * COLOR_FACTOR) / (FRACTION_ANGLE[i + 1] - FRACTION_ANGLE[i]);
            GREEN_STEP_LOOKUP[i] = ((COLORS[i + 1].getGreen() - COLORS[i].getGreen()) * COLOR_FACTOR) / (FRACTION_ANGLE[i + 1] - FRACTION_ANGLE[i]);
            BLUE_STEP_LOOKUP[i] = ((COLORS[i + 1].getBlue() - COLORS[i].getBlue()) * COLOR_FACTOR) / (FRACTION_ANGLE[i + 1] - FRACTION_ANGLE[i]);
            ALPHA_STEP_LOOKUP[i] = ((COLORS[i + 1].getAlpha() - COLORS[i].getAlpha()) * COLOR_FACTOR) / (FRACTION_ANGLE[i + 1] - FRACTION_ANGLE[i]);
        }
    }
    
    @Override
    public java.awt.PaintContext createContext(java.awt.image.ColorModel colorModel, java.awt.Rectangle deviceBounds, java.awt.geom.Rectangle2D userBounds, java.awt.geom.AffineTransform transform, java.awt.RenderingHints hints)
    {
        java.awt.geom.Point2D transformedCenter = transform.transform(CENTER, null);        
        return new ConicalGradientPaintContext(transformedCenter);
    }

    @Override
    public int getTransparency()
    {        
        return java.awt.Transparency.TRANSLUCENT;
    }

    private class ConicalGradientPaintContext implements java.awt.PaintContext
    {
        final private java.awt.geom.Point2D CENTER;

        public ConicalGradientPaintContext(java.awt.geom.Point2D center)
        {
            super();
            this.CENTER = new java.awt.geom.Point2D.Double(center.getX(), center.getY());
        }

        @Override
        public void dispose()
        {
        }

        @Override
        public java.awt.image.ColorModel getColorModel()
        {
            return java.awt.image.ColorModel.getRGBdefault();
        }

        @Override
        public java.awt.image.Raster getRaster(int x, int y, int tileWidth, int tileHeight)
        {
            final double ROTATION_CENTER_X = -x + CENTER.getX();
            final double ROTATION_CENTER_Y = -y + CENTER.getY();
            
            final int MAX = FRACTION_ANGLE.length;

            // Create raster for given colormodel
            java.awt.image.WritableRaster raster = getColorModel().createCompatibleWritableRaster(tileWidth, tileHeight);

            // Create data array with place for red, green, blue and alpha values
            int[] data = new int[(tileWidth * tileHeight * 4)];

            double dx;
            double dy;
            double distance;
            double angle;
            double currentRed = 0;
            double currentGreen = 0;
            double currentBlue = 0 ;
            double currentAlpha = 0;                        

            for (int py = 0; py < tileHeight; py++)
            {
                for (int px = 0; px < tileWidth; px++)
                {

                    // Calculate the distance between the current position and the rotation angle
                    dx = px - ROTATION_CENTER_X;
                    dy = py - ROTATION_CENTER_Y;                    
                    distance = Math.sqrt(dx * dx + dy * dy);

                    // Avoid division by zero
                    if (distance == 0)
                    {
                        distance = 1;
                    }

                    // 0° degree on top
                    angle = Math.abs(Math.toDegrees(Math.acos(dx / distance)));

                    if (dx >= 0 && dy <= 0)
                    {
                        angle = 90.0 - angle;
                    }
                    else if (dx >= 0 && dy >= 0)
                    {
                        angle += 90.0;
                    }
                    else if (dx <= 0 && dy >= 0)
                    {
                        angle += 90.0;
                    }
                    else if (dx <= 0 && dy <= 0)
                    {
                        angle = 450.0 - angle;
                    }                    
                    
                    // Check for each angle in fractionAngles array
                    for (int i = 0 ; i < (MAX - 1) ; i++)
                    {
                        if ((angle >= FRACTION_ANGLE[i]) )
                        {
                            currentRed = COLORS[i].getRed() * COLOR_FACTOR + (angle - FRACTION_ANGLE[i]) * RED_STEP_LOOKUP[i];
                            currentGreen = COLORS[i].getGreen() * COLOR_FACTOR + (angle - FRACTION_ANGLE[i]) * GREEN_STEP_LOOKUP[i];
                            currentBlue = COLORS[i].getBlue() * COLOR_FACTOR + (angle - FRACTION_ANGLE[i]) * BLUE_STEP_LOOKUP[i];
                            currentAlpha = COLORS[i].getAlpha() * COLOR_FACTOR + (angle - FRACTION_ANGLE[i]) * ALPHA_STEP_LOOKUP[i];
                           
                            continue;
                        }
                    }

                    // Fill data array with calculated color values
                    int base = (py * tileWidth + px) * 4;
                    data[base + 0] = (int) (currentRed * 255);
                    data[base + 1] = (int) (currentGreen * 255);
                    data[base + 2] = (int) (currentBlue * 255);
                    data[base + 3] = (int) (currentAlpha * 255);
                }
            }

            // Fill the raster with the data
            raster.setPixels(0, 0, tileWidth, tileHeight, data);

            return raster;
        }
    }


}
