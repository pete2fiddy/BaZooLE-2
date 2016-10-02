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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

/**
 *
 * @author phusisian
 */
public class Grass extends Scenery implements ActionListener
{
    //public Timer refreshTimer = new Timer(200, this);
    //Method names based around Standard quadratic form of y = a(x-h)^2+k
    private static final Color darkGrass = new Color(23,68,0);//(37, 89, 11);
    private static int height;
    public static Point[][] grassPoints = new Point[5][5];
    private static boolean goingForward = true;
    private static double offset = 0;
    private static double radius = 5;
    public Grass(Tile tileIn, double offsetXIn, double offsetYIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        height = 4;//3 + (int)(3*Math.random());
        
        //refreshTimer.setRepeats(true);
        radius = 5;//+(3*Math.random());
        //refreshTimer.start();
        setGrassPoints();
        tileIn.addGrass(this);
    }
    
    private static double getA(double hIn)
    {
        return -10*radius/Math.pow(hIn, 2);
    }
    
    private static double getK(double hIn)
    {
        return Math.sqrt(Math.pow(radius, 2) - Math.pow(hIn, 2));
    }
    
    private static double getXIntercept(double hIn)
    {
        return hIn - (Math.abs(hIn)/hIn)*Math.sqrt(-getK(hIn)/getA(hIn));
    }
    
    
    private static double getYValue(double hIn, double xIn)
    {
        return getA(hIn)*Math.pow(xIn-hIn, 2)+getK(hIn);
    }
    
    private void graphLeaf(Graphics g, double hIn)
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
    }
    
    public void drawTufts(Graphics g)
    {
       
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke((int)(.75*WorldPanel.scale)));
        double x = getX();
        double y = getY();
        Point endPoint = new Point((int)x, (int)y);
        Point startPoint = new Point((int)x, (int)(y-radius*WorldPanel.scale));
       // g.setColor(Color.RED);
       // g.fillOval((int)(startPoint.getX()-3), (int)(startPoint.getY() - 3), 6, 6);
       // g.fillOval((int)(endPoint.getX()-3), (int)(endPoint.getY() - 3), 6, 6);
        //GradientPaint gp = new GradientPaint(startPoint,Toolbox.defaultGrassColor,endPoint,darkGrass);
        
        g.setColor(Toolbox.grassColor);
        //g2.setPaint(gp);
        
        for(int i = 0; i < grassPoints.length; i++)
        {
            for(int j = 0; j < grassPoints[0].length-1; j++)
            {
                double colorMultiplier = (double)(j)/(double)(grassPoints[0].length);
                //System.out.println("Color multiplier: " + colorMultiplier);
                //new Color(14, 155, 14);default grass
                //new Color(23,68,0) dark grass
                
                int red = (int)(darkGrass.getRed() - (colorMultiplier*(darkGrass.getRed()-Toolbox.grassColor.getRed())));
                int green = (int)(darkGrass.getGreen() + (colorMultiplier*(Toolbox.grassColor.getGreen()-darkGrass.getGreen())));
                int blue = (int)(darkGrass.getBlue() + (colorMultiplier*(Toolbox.grassColor.getBlue()-darkGrass.getBlue())));
                Color c = new Color(red, green, blue);
                
                //Color c = new Color(darkGrass.getRed() - (int)(colorMultiplier*(darkGrass.getRed() - Toolbox.defaultGrassColor.getRed())),darkGrass.getGreen() - (int)(colorMultiplier*(darkGrass.getGreen() - Toolbox.defaultGrassColor.getGreen())), darkGrass.getBlue() - (int)(colorMultiplier*(darkGrass.getBlue() - Toolbox.defaultGrassColor.getBlue())));
                //g.drawLine(10, 10, 20, 20);
                g.setColor(c);
                g.drawLine((int)(x + grassPoints[i][j].getX()), (int)(y - grassPoints[i][j].getY()),(int)(x + grassPoints[i][j+1].getX()), (int)(y - grassPoints[i][j+1].getY()));
            }
        }
        g2.setStroke(new BasicStroke(1));
    }
    
    
    public static void setGrassPoints()
    {
        if(goingForward)
        {
            offset += 0.03;
            if(offset/radius > .2)
            {
                goingForward = false;
            }
        }else{
            offset -= 0.03;
            if(offset/radius < -.2)
            {
                goingForward = true;
            }
        }
        
        double incrementH = 24.0/radius/4.0;
        //int grassCount = 0;
        double dOffset = offset - (2*incrementH);
        for(int grassCount = 0; grassCount < grassPoints.length; grassCount++)//double h = offset - (2*incrementH); h < offset + (2*incrementH); h += incrementH)
        {
            Point[] points = new Point[grassPoints[0].length];
            double range = dOffset-getXIntercept(dOffset);
            double increment = range/(double)points.length;
            //System.out.println("y value: " + getYValue());
            for(int i = 0; i < points.length; i++)
            {
                points[i] = new Point((int)(WorldPanel.scale*(getXIntercept(dOffset)+(increment*i))), (int)(WorldPanel.scale*Math.sin(WorldPanel.rotation)*(getYValue(dOffset, (getXIntercept(dOffset)+(increment*i))))));
            }
            dOffset += incrementH;
            grassPoints[grassCount]=points;
            //grassCount++;
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
