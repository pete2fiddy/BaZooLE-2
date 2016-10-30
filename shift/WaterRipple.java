package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class WaterRipple extends Toolbox
{
    private static final double DSCALE_MAX = 0.023, DSCALE_MIN = 0.01;
    private int ticksToRespawn = 0;
    private int ticksCounted = 0;
    private static final int NUM_CIRCLES = 3;
    private final int baseRadius = 5;
    private double scale = 1, x, y;
    private static final Color DEFAULT_RIPPLE_COLOR = new Color(0,191,255);
    private double initialScale;
    private double dScale = 1;
    
    /*
    Initialization:
    */
    public WaterRipple(double xIn, double yIn, double scaleIn)
    {
        x=xIn; y=yIn; scale = scaleIn; initialScale = scaleIn;
        dScale = DSCALE_MAX - ((DSCALE_MAX-DSCALE_MIN)*Math.random());
    }
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    public void draw(Graphics g)
    {
        if(scale < initialScale *1.5)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double pos[] = convertToPoint(x, y);
            for(int i = 1; i < NUM_CIRCLES+1; i++)
            {
                g.setColor(ColorPalette.getLerpColor(DEFAULT_RIPPLE_COLOR, WorldPanel.waterColor, (double)(((54+(((int)(200*((initialScale*1.5)-scale)))/(double)i))))/255.0));
                g.drawOval((int)(pos[0]-(baseRadius*scale*i*WorldPanel.scale)), (int)(pos[1]-(baseRadius*scale*i*WorldPanel.scale*WorldPanel.getShrink)), (int)(baseRadius*2*i*scale*WorldPanel.scale), (int)(baseRadius*2*i*scale*WorldPanel.scale*WorldPanel.getShrink));
            }
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }
    
    /*
    ----------------------------------------------------------------------------
    Animation updating/replacing when animation is finished: 
    */
    
    public void update()
    {
        if(scale < initialScale *1.5)
        {
            scale += dScale;
        }else if(ticksToRespawn == 0)
        {
            ticksToRespawn = 40 + (int)(100*Math.random());
        }else{
            ticksCounted++;
            if(ticksCounted > ticksToRespawn)
            {
                respawn();
            }
        }
    }
    
    private void respawn()
    {
        ticksToRespawn = 0;
        ticksCounted = 0;
        scale = 0.5+Math.random();
        double unitsWidth = WorldPanel.straightUnit/((scale+1)*NUM_CIRCLES*baseRadius*WorldPanel.scale);
        double unitsLength = WorldPanel.straightUnit/((scale+1)*NUM_CIRCLES*baseRadius*WorldPanel.scale);
        double xMin = -(WorldPanel.worldTilesWidth/2.0)+(unitsWidth/2.0);
        double xMax = (WorldPanel.worldTilesWidth/2.0)-(unitsWidth/2.0);
        double yMin = -(WorldPanel.worldTilesHeight/2.0)+(unitsLength/2.0);
        double yMax = (WorldPanel.worldTilesHeight/2.0)-(unitsLength/2.0);
        x = xMin + (Math.random()*(xMax-xMin));
        y = yMin + (Math.random()*(yMax-yMin));
        initialScale = scale;
        dScale = DSCALE_MAX - ((DSCALE_MAX-DSCALE_MIN)*Math.random());
        ticksToRespawn = 0;
    }
    /*
    ----------------------------------------------------------------------------
    */
}
