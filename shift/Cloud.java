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
    private DayNight dayNight;
    private SnowFlake[] flakeList;
    public Cloud(DayNight dayNightIn, double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight)
    {
        cloudShape = new RectPrism(inX, inY, inZPos, inWidth, inLength, inHeight);
        x=inX; y=inY; length=inLength; width=inWidth;
        zPos=inZPos; height=inHeight;
        cloudSpeed = .01 + .015* Math.random();
        //s = new SnowFlake(this, x, y);
        dayNight = dayNightIn;
        fillFlakeList();
    }
    
    
    private void fillFlakeList()
    {
        if(dayNight.getSeason().equals("winter"))
        {
            double flakeUnit = .25;
            int flakeWaves = (int)((zPos - 150)/30) + 3;
            flakeList = new SnowFlake[(int)(width/flakeUnit)*(int)(length/flakeUnit)*flakeWaves];
            int flakeCount = 0;
            for(int i = 0; i < (int)(width/flakeUnit); i++)
            {
                for(int j = 0; j < (int)(length/flakeUnit); j++)
                {
                    flakeList[flakeCount] = new SnowFlake(this, cloudShape.getCornerX() + (i*flakeUnit), cloudShape.getCornerY()+(j*flakeUnit));
                    flakeCount++;
                    for(int z = 1; z < flakeWaves; z++)
                    {
                        flakeList[flakeCount] = new SnowFlake(this, cloudShape.getCornerX() + (i*flakeUnit), cloudShape.getCornerY()+(j*flakeUnit), 800*z);
                        flakeCount++;
                    }
                }
            }
        }else{
            flakeList = new SnowFlake[0];
        }
    }
    
    public double getCoordX(){
        return x;
    }
    public double getCoordY(){
        return y;
    }
    public double getAlphaPercent(){return (double)alpha/(double)defaultAlpha;}
    public boolean outsideOfMap()
    {
        return cloudShape.outsideOfMap();
    }
    
    public double getCloudSpeed()
    {
        if(alpha != 0)
        {
            return cloudSpeed;
        }
        return 0;
    }
    public int getZPos(){return zPos;}
    
    public void fill(Graphics g)
    {
        if(flakeList != null)
        {
            for(SnowFlake sf : flakeList)
            {
                sf.paint(g);
            }
        }
        //s.paint(g);
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
        width = 1.5+1.0*Math.random();
        length = 1.5+1.0*Math.random();
        y = ((Math.random()*WorldPanel.worldTilesHeight)-(WorldPanel.worldTilesHeight/2));
        x = -x;
        cloudShape.setCenterCoordX(x);
        cloudShape.setCenterCoordY(y);
        //y-= length/2.0;
        //x-= width/2.0;
        cloudShape.setLength(length);
        cloudShape.setWidth(width);
        zPos = (int)(150 + (50* (int)(3 * Math.random())));
        cloudSpeed = 0.015 + (0.01*((zPos - 150)/50.0));
        //cloudSpeed = .01 + .015* Math.random();
        cloudShape.updateShapePolygons();
        
        cloudShape.setZPos(zPos);
        fillFlakeList();
    }
}   
