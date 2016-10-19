/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author phusisian
 */
public class Mountain 
{
    private Point[] mountainPoints;
    private int height;
    private double x, y;
    private double dSpin;
    private double spin = 0;
    private double spinRadius;
    public Mountain(double xIn, double yIn, int mountainType)
    {
        x = xIn;
        y = yIn;
        dSpin = Math.toRadians(1.5+Math.random()*2.0);
        fillMountainPoints(mountainType);
        spinRadius = 15 + (10*Math.random());
    }
    
    private void fillMountainPoints(int mountainType)
    {
        switch(mountainType)
        {//switched from using points to int x and y vals
            case 1:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,479);
                mountainPoints[1] = new Point(251,0);
                mountainPoints[2] = new Point(513, 479);
                height = 479;
                break;
            case 2:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,545);
                mountainPoints[1] = new Point(245,0);
                mountainPoints[2] = new Point(507,545);
                height = 545;
                break;
            case 3:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,383);
                mountainPoints[1] = new Point(107,115);
                mountainPoints[2] = new Point(169,221);
                mountainPoints[3] = new Point(257,0);
                mountainPoints[4] = new Point(472,383);
                height = 383;
                break;
            case 4:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,440);
                mountainPoints[1] = new Point(220,0);
                mountainPoints[2] = new Point(364,285);
                mountainPoints[3] = new Point(401,220);
                mountainPoints[4] = new Point(532,440);
                height = 440;
                break;
            case 5:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,525);
                mountainPoints[1] = new Point(238,0);
                mountainPoints[2] = new Point(403,349);
                mountainPoints[3] = new Point(473,251);
                mountainPoints[4] = new Point(594,525);
                height = 525;
                break;
            case 6:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,470);
                mountainPoints[1] = new Point(268,0);
                mountainPoints[2] = new Point(511,470);
                height = 470;
                break;
            case 7:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,383);
                mountainPoints[1] = new Point(192,0);
                mountainPoints[2] = new Point(387,383);
                height = 383;
                break;
        }
    }
    
    public void draw(Graphics g)
    {
        y=WorldPanel.worldY;
        int[] xPoints = new int[mountainPoints.length];
        int[] yPoints = new int[mountainPoints.length];
        for(int i = 0; i < mountainPoints.length; i++)
        {
            
            //mountainPoints[i] = new Point((int)(mountainPoints[i].getX()*WorldPanel.scale), (int)(mountainPoints[i].getY()*WorldPanel.scale));
            xPoints[i]=(int)(WorldPanel.worldX+WorldPanel.scale*(x-WorldPanel.worldX)+(mountainPoints[i].getX()*WorldPanel.scale));
            yPoints[i]=(int)(WorldPanel.worldY-(height*WorldPanel.scale)+(mountainPoints[i].getY()*WorldPanel.scale) - (WorldPanel.scale*Math.sin(spin)*spinRadius));
        }
        spin += dSpin;
        if(spin > 2*Math.PI)
        {
            spin -= 2*Math.PI;
        }
        g.setColor(Color.GRAY);
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, xPoints.length);
    }
}
