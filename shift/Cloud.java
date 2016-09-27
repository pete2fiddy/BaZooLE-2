/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author phusisian
 */
public class Cloud 
{
    private final int defaultAlpha = 120;
    private int alpha = defaultAlpha;
    private double cloudSpeed = .03;
    private RectPrism cloudShape;
    private double x, y, length, width;
    private int zPos, height;
    private int waitTime = 10;
    private long endTime = 0;
    public Cloud(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight)
    {
        cloudShape = new RectPrism(inX, inY, inZPos, inWidth, inLength, inHeight);
        x=inX; y=inY; length=inLength; width=inWidth;
        zPos=inZPos; height=inHeight;
        cloudSpeed = .01 + .015* Math.random();
    }
    
    public void fill(Graphics g)
    {
        cloudShape.updateShapePolygons();
        x+=cloudSpeed;
        //y+=cloudSpeed;
        if(cloudShape.outsideOfMap() && Math.abs(x) == x && alpha > 0)
        {
            alpha -= 5;
        }else if(alpha < defaultAlpha)
        {
            alpha += 5;
        }
        cloudShape.setCenterCoordX(x);
        cloudShape.setCenterCoordY(y);
        g.setColor(new Color(255,255,255,alpha));
        
        
        
        
        cloudShape.fill(g);
        
        if(!cloudShape.isVisible() && Math.abs(x) == x || alpha <= 0)
        {
            if(endTime == 0)
            {
                endTime = System.currentTimeMillis();
                waitTime = (int)(100 + 500*Math.random());
            }
            
            if(System.currentTimeMillis() > endTime + waitTime)
            {
                reshapeCloud();
            }
            
        }
        Graphics2D g2 = (Graphics2D)g;
        Composite originalComposite = g2.getComposite();
        int type = AlphaComposite.SRC_OVER;
        
        AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)((double)alpha/255.0));
        g2.setComposite(transparencyComposite);
        cloudShape.paintShading(g);
        g2.setComposite(originalComposite);
    }
    
    public void reshapeCloud()
    {
        //x = -x;
        endTime = 0;
            //y = -y;
            //alpha = defaultAlpha;
        width = 1.0+1.0*Math.random();
        length = 1.0+1.0*Math.random();
        y = ((Math.random()*WorldPanel.worldTilesHeight)-(WorldPanel.worldTilesHeight/2));
        x = -x;
        //y-= length/2.0;
        //x-= width/2.0;
        cloudShape.setLength(length);
        cloudShape.setWidth(width);
        zPos = (int)(150 + (50* (int)(3 * Math.random())));
        cloudSpeed = 0.015 + (0.01*((zPos - 150)/50.0));
        //cloudSpeed = .01 + .015* Math.random();
        cloudShape.updateShapePolygons();
        
        cloudShape.setZPos(zPos);
    }
}   
