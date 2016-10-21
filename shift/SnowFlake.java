/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author phusisian
 */
public class SnowFlake extends Toolbox
{
    private double x, y;
    private double dx, dy;
    private double height;
    private double fallSpeed = .75 + .75*(Math.random());
    private Cloud boundCloud;
    private int waitTime;
    private long currentTime;
    public SnowFlake(Cloud boundCloudIn, double xIn, double yIn)
    {
        boundCloud = boundCloudIn;
        x=xIn; y=yIn; height=boundCloud.getZPos();
        dx = x - boundCloud.getCoordX();
        dy = y - boundCloud.getCoordY();
        currentTime = System.currentTimeMillis();
        waitTime = (int)(Math.random()*200);
    }
    public SnowFlake(Cloud boundCloudIn, double xIn, double yIn, int baseDelay)
    {
        boundCloud = boundCloudIn;
        x=xIn; y=yIn; height=boundCloud.getZPos();
        dx = x - boundCloud.getCoordX();
        dy = y - boundCloud.getCoordY();
        currentTime = System.currentTimeMillis();
        waitTime = baseDelay + (int)(Math.random() * 400);
    }
    
    private boolean isVisible(Graphics g, double x, double y)
    {
        return (g.getClip().contains(x,y));
    }
    
    public void paint(Graphics g)
    {
        if(!boundCloud.outsideOfMap())
        {
            //x+=boundCloud.getCloudSpeed();
            x = boundCloud.getCoordX() + dx;
            y = boundCloud.getCoordY() + dy;
            double[] points = convertToPoint(x,y);
            if(isVisible(g, points[0], points[1]))
            {
                if(currentTime + waitTime < System.currentTimeMillis())
                {
                    height -= fallSpeed;
                    if(boundCloud.getCloudSpeed() != 0)
                    {
                        g.setColor(Color.WHITE);
                        g.fillOval((int)(points[0]-(WorldPanel.scale*1)), (int)(points[1]-(WorldPanel.scale*1) - scaledDistortedHeight((int)height)), (int)(WorldPanel.scale*2), (int)(WorldPanel.scale*2));
                    }
                }
                if(height < 0)
                {
                    height = boundCloud.getZPos();
                    fallSpeed = .75 + .75*(Math.random());
                    currentTime = System.currentTimeMillis();
                    waitTime = (int)(50 + Math.random()*200);
                }
            }
            /*if(x > (WorldPanel.worldTilesWidth/2) || x < -(WorldPanel.worldTilesWidth/2))
            {
                x = -x;
            }*/
        }else{
            //x+=boundCloud.getCloudSpeed();
            x = boundCloud.getCoordX() + dx;
            y = boundCloud.getCoordY() + dy;
            double[] points = convertToPoint(x,y);
            if(isVisible(g, points[0], points[1]))
            {
                if(currentTime + waitTime < System.currentTimeMillis())
                {
                    height -= fallSpeed;
                    if(boundCloud.getCloudSpeed() != 0)
                    {
                        g.setColor(new Color(255, 255, 255, (int)(255*boundCloud.getAlphaPercent())));
                        g.fillOval((int)(points[0]-(WorldPanel.scale*1)), (int)(points[1]-(WorldPanel.scale*1) - scaledDistortedHeight((int)height)), (int)(WorldPanel.scale*2), (int)(WorldPanel.scale*2));
                    }
                }
                if(height < 0)
                {
                    height = boundCloud.getZPos();
                    fallSpeed = .75 + .75*(Math.random());
                    currentTime = System.currentTimeMillis();
                    waitTime = (int)(50 + Math.random()*200);
                }
            }
        }
    }
}
