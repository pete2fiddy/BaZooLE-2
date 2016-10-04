/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

/**
 *
 * @author phusisian
 */

//Plotted using this function: https://www.desmos.com/calculator/ztyfsgdcoj
public class Grass extends Scenery implements ActionListener
{
    //public Timer refreshTimer = new Timer(200, this);
    //Method names based around Standard quadratic form of y = a(x-h)^2+k
    private static final Color darkGrass = new Color(23,68,0);//(37, 89, 11);
    private static int height;
    public static Point[][][] grassPoints = new Point[3][5][4];
    private static boolean goingForward = true;
    private static double[] offsets = new double[grassPoints.length];
    private int thisRadius;
    private static final int minRadius = 3;
    private static final int maxRadiusAdd = 3;
    private static BasicStroke grassStroke;
    public Grass(Tile tileIn, double offsetXIn, double offsetYIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        height = 4;//3 + (int)(3*Math.random());
        
        //refreshTimer.setRepeats(true);
        for(int i = 0; i < offsets.length; i++)
        {
            offsets[i]=0;
        }
        thisRadius = minRadius+(int)(Math.random()*maxRadiusAdd);
        //refreshTimer.start();
        setGrassPoints();
        tileIn.addGrass(this);
    }
    
    private static double getA(double hIn, double radius)
    {
        return -Math.pow(radius,2)/Math.pow(hIn, 2);
    }
    
    private static double getK(double hIn, double radius)
    {
        return Math.sqrt(Math.pow(radius, 2) - Math.pow(hIn, 2));
    }
    
    private static double getXIntercept(double hIn, double radius)
    {
        return hIn - (Math.abs(hIn)/hIn)*Math.sqrt(-getK(hIn,radius)/getA(hIn, radius));
    }
    
    
    private static double getYValue(double hIn, double xIn, double radius)
    {
        return getA(hIn, radius)*Math.pow(xIn-hIn, 2)+getK(hIn,radius);
    }
    
    /*private void graphLeaf(Graphics g, double hIn)
    {
        g.setColor(darkGrass);
        Point[] points = new Point[3];
        double range = hIn-getXIntercept(hIn);
        double increment = range/(double)points.length;
        System.out.println("a: " + getA(hIn));
        System.out.println("k: " + getK(hIn));
        System.out.println("intercept: " + getXIntercept(hIn));
        //System.out.println("y value: " + getYValue());
        for(int i = 0; i < points.length; i++)
        {
            points[i] = new Point((int)(WorldPanel.scale*(getXIntercept(hIn)+(increment*i))), (int)(WorldPanel.scale*getYValue(hIn, (getXIntercept(hIn)+(increment*i)))));
        }
        
        for(int i = 0; i < points.length-1 ; i++)
        {
            g.drawLine((int)(getX() + points[i].getX()), (int)(getY() - points[i].getY()), (int)(getX() + points[i+1].getX()), (int)(getY() - points[i+1].getY()));
            //g.fillOval((int)(getX() + points[i].getX() - 2), (int)(getY()-points[i].getY()-2), 4, 4);
            
            //g.drawLine((int)points[i].getX(), (int)points[i].getY(), (int)points[i+1].getX(), (int)points[i+1].getY());
        }
    }*/
    
    public void drawTufts(Graphics g)
    {
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(grassStroke);
        double x = getX();
        double y = getY();
        Rectangle grassRect = new Rectangle((int)x,(int)y,10,10);
        if(g.getClip().contains(grassRect))
        {
        int iLength = grassPoints[0].length;
        int jLength = grassPoints[0][0].length;
        int diffR = darkGrass.getRed()-Toolbox.grassColor.getRed();
        int diffG = Toolbox.grassColor.getGreen()-darkGrass.getGreen();
        int diffB = Toolbox.grassColor.getBlue()-darkGrass.getBlue();
        for(int i = 0; i < iLength; i++)
        {
            for(int j = 0; j < jLength-1; j++)
            {
                double colorMultiplier = (double)(j)/(double)(iLength);
                
                int red = (int)(darkGrass.getRed() - (colorMultiplier*diffR));
                int green = (int)(darkGrass.getGreen() + (colorMultiplier*diffG));
                int blue = (int)(darkGrass.getBlue() + (colorMultiplier*diffB));
                Color c = new Color(red, green, blue);
                g.setColor(c);
                g.drawLine((int)(x + grassPoints[thisRadius-minRadius][i][j].getX()), (int)(y - grassPoints[thisRadius-minRadius][i][j].getY()),(int)(x + grassPoints[thisRadius-minRadius][i][j+1].getX()), (int)(y - grassPoints[thisRadius-minRadius][i][j+1].getY()));
            }
        }
        g2.setStroke(new BasicStroke(1));
        }
    }
    
    
    public static void setGrassPoints()
    {
        
        grassStroke = new BasicStroke((int)(WorldPanel.scale*0.75));
        for(int stage = 0; stage < grassPoints.length; stage++)
        {
            
            if(goingForward)
            {
                offsets[stage] += 0.03;
                if(offsets[stage]/(stage+minRadius) > .2)
                {
                    goingForward = false;
                }
            }else{
                offsets[stage] -= 0.03;
                if(offsets[stage]/(stage+minRadius) < -.2)
                {
                    goingForward = true;
                }
            }
            double incrementH = 24.0/(double)(stage+minRadius)/4.0;
            //int grassCount = 0;
            double dOffset = offsets[stage] - (2*incrementH);
            for(int grassCount = 0; grassCount < grassPoints[0].length; grassCount++)//double h = offset - (2*incrementH); h < offset + (2*incrementH); h += incrementH)
            {
                Point[] points = new Point[grassPoints[0][0].length];
                double range = dOffset-getXIntercept(dOffset, stage + minRadius);
                double increment = range/(double)points.length;
                //System.out.println("y value: " + getYValue());
                for(int i = 0; i < points.length; i++)
                {
                    points[i] = new Point((int)(WorldPanel.scale*(getXIntercept(dOffset, stage + minRadius)+(increment*i))), (int)(WorldPanel.scale*Math.sin(WorldPanel.rotation)*(getYValue(dOffset, (getXIntercept(dOffset, stage + minRadius)+(increment*i)), stage + minRadius))));
                }
                dOffset += incrementH;
                grassPoints[stage][grassCount]=points;
                //grassCount++;
            }
        }
    }
    
    @Override
    public void draw(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke((int)(0.5*WorldPanel.scale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
        //double increment = 24.0/radius/4.0;
        /*for(int i = 0; i < 4; i++)
        {
            graphLeaf(g, offset - (increment*2)+(increment*i));
        }*/
        //drawTufts(g);
        //graphLeaf(g, offset);
        //graphLeaf(g, offset + 5);
        //graphLeaf(g, offset + 10);
        //g.setColor(Color.BLACK);
        drawTufts(g);
        g2.setStroke(new BasicStroke(1));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        setGrassPoints();
    }
    
}