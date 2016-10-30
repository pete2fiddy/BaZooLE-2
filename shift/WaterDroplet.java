/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author phusisian
 */
public class WaterDroplet 
{
    private double baseXSpeed;
    private double xSpeed;
    private double baseX, baseY;
    private double x, y, constant;
    private double relativeX, relativeY = 0;
    private int baseDiameter;
    private int direction;
    private int widthTravels, baseWidthTravels;
    private int diameter;
    private Color color;
    Waterfall boundWaterfall;
    public WaterDroplet(Waterfall boundWaterfallIn, double xIn, double yIn, int diameterIn, int widthTravelsIn, int directionIn, Color c)//double constantIn, Color c)
    {
        boundWaterfall = boundWaterfallIn;
        x = xIn; y = yIn; baseDiameter = diameterIn; widthTravels = widthTravelsIn;color = c;//constant = constantIn; color = c;
        baseX = x; baseY = y;
        direction = directionIn;
        diameter = baseDiameter;
        relativeX = direction * -(widthTravels/2);
        constant = .01;
        xSpeed = (.75*Math.random()) + .25;
        baseXSpeed = xSpeed;
        baseWidthTravels = widthTravels;
    }
    public void draw(Graphics g)
    {
        tick();
        g.setColor(color);
        g.fillOval((int)(x + relativeX - (diameter/2)), (int)(y + relativeY -(diameter/2)), diameter, diameter);
        g.setColor(Color.BLACK);
        g.drawOval((int)(x +relativeX - (diameter/2)), (int)(y + relativeY -(diameter/2)), diameter, diameter);
    }
    public void tick()
    {
        xSpeed = baseXSpeed * WorldPanel.scale;
        
        x = boundWaterfall.getX() + baseX;
        y = boundWaterfall.getBaseY() + baseY;
        relativeX+= direction * xSpeed*WorldPanel.scale;
        relativeY = constant * Math.pow(relativeX,2);
        if(direction > 0)
        {
            if(relativeX > WorldPanel.scale*(direction*widthTravels/2))
            {
                randomizeVariables();
                relativeX = direction*-(widthTravels/2);
                relativeY = 0;
            }
        }else if(direction < 0)
        {
            if(relativeX < WorldPanel.scale*(direction*widthTravels/2))
            {
                randomizeVariables();
                relativeX = direction*-(widthTravels/2);
                relativeY = 0;
            }
        }
        diameter = (int)(baseDiameter*WorldPanel.scale);
    }
    public void randomizeVariables()
    {
        Random r = new Random();
        baseXSpeed = ((.75*Math.random()) + .25)/WorldPanel.scale;
        int randomDir = r.nextInt(2);
        if(randomDir == 0)
        {
            direction = -1;
        }else{
            direction = 1;
        }
        baseX = r.nextInt(boundWaterfall.getBasedx()+1)-(boundWaterfall.getBasedx()/2.0);
        baseY = r.nextInt(boundWaterfall.getBasedy()+1)-(boundWaterfall.getBasedy()/2.0);
        diameter = r.nextInt(5) + 5;
        widthTravels = r.nextInt(30) + 30;
        
    }
}
