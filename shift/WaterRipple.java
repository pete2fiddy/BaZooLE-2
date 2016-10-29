/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author phusisian
 */
public class WaterRipple extends Toolbox
{
    private static final double maxDScale = 0.023, minDScale = 0.01;
    private int ticksToRespawn = 0;
    private int ticksCounted = 0;
    private static final int numCircles = 3;
    private final int baseRadius = 5;
    private double scale = 1, x, y;
    private static final Color color = new Color(0,191,255);
    private double initialScale;
    private double dScale = 1;
    public WaterRipple(double xIn, double yIn, double scaleIn)
    {
        x=xIn; y=yIn; scale = scaleIn; initialScale = scaleIn;
        dScale = maxDScale - ((maxDScale-minDScale)*Math.random());
    }
    public void draw(Graphics g)
    {
        if(scale < initialScale *1.5)
        {
            
            Graphics2D g2 = (Graphics2D) g;
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(1.0f));

            double pos[] = convertToPoint(x, y);
            for(int i = 1; i < numCircles+1; i++)
            {
                g.setColor(ColorPalette.getLerpColor(color, WorldPanel.waterColor, (double)(((54+(((int)(200*((initialScale*1.5)-scale)))/(double)i))))/255.0));
                //g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(54+(((int)(200*((initialScale*1.5)-scale)))/(double)i))));
                g.drawOval((int)(pos[0]-(baseRadius*scale*i*WorldPanel.scale)), (int)(pos[1]-(baseRadius*scale*i*WorldPanel.scale*WorldPanel.getShrink)), (int)(baseRadius*2*i*scale*WorldPanel.scale), (int)(baseRadius*2*i*scale*WorldPanel.scale*WorldPanel.getShrink));
                //g.drawOval((int)(pos[0]-(baseRadius*scale*i)), (int)(pos[1]-(baseRadius*scale.WorldPanel.getShrink*i)), (int)(baseRadius*2*scale*i), (int)(baseRadius*2*scale*WorldPanel.scale*WorldPanel.getShrink));
            }
            scale += dScale;

            g2.setStroke(new BasicStroke());
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
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
        double unitsWidth = WorldPanel.straightUnit/((scale+1)*numCircles*baseRadius*WorldPanel.scale);
        double unitsLength = WorldPanel.straightUnit/((scale+1)*numCircles*baseRadius*WorldPanel.scale);
        //System.out.println("Units Width: " + unitsWidth);
        double xMin = -(WorldPanel.worldTilesWidth/2.0)+(unitsWidth/2.0);
        double xMax = (WorldPanel.worldTilesWidth/2.0)-(unitsWidth/2.0);
        double yMin = -(WorldPanel.worldTilesHeight/2.0)+(unitsLength/2.0);
        double yMax = (WorldPanel.worldTilesHeight/2.0)-(unitsLength/2.0);
        x = xMin + (Math.random()*(xMax-xMin));
        y = yMin + (Math.random()*(yMax-yMin));
        //x=-(WorldPanel.worldTilesWidth/2.0)+(Math.random() * ((((WorldPanel.worldTilesWidth/2.0)-unitsWidth)+(WorldPanel.worldTilesWidth/2.0))));
        //y=-(WorldPanel.worldTilesHeight/2.0)+(Math.random() * ((((WorldPanel.worldTilesHeight/2.0) - unitsLength))))
        //x = (int)((WorldPanel.worldTilesWidth-((baseRadius * numCircles * (scale + 1))/WorldPanel.straightUnit))*Math.random())-((WorldPanel.worldTilesWidth-((baseRadius * numCircles * (scale + 1))/WorldPanel.straightUnit))/2);
        //y = (int)((WorldPanel.worldTilesHeight-((baseRadius * numCircles * (scale + 1))/WorldPanel.straightUnit))*Math.random())-((WorldPanel.worldTilesHeight-((baseRadius * numCircles * (scale + 1))/WorldPanel.straightUnit))/2);
        //y=(int)(WorldPanel.worldTilesHeight*Math.random())-(WorldPanel.worldTilesHeight/2);
        initialScale = scale;
        dScale = maxDScale - ((maxDScale-minDScale)*Math.random());
        ticksToRespawn = 0;
    }
}
