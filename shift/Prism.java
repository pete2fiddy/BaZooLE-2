/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author phusisian
 */
public class Prism extends SolidShape
{
    private FlatShape bottomShape, topShape;
    private Polygon[] threadedVisibleSidePolygons;
    public Prism(double inX, double inY, int inZPos, double radiusIn, int inHeight, int numSidesIn) {
        super(inX, inY, inZPos, radiusIn*2, radiusIn*2, inHeight);
        bottomShape = new FlatShape(inX, inY, inZPos, radiusIn, numSidesIn);
        topShape = new FlatShape(inX, inY, inZPos + inHeight, radiusIn, numSidesIn);
        threadedVisibleSidePolygons = getVisibleSidePolygons();
    }

    public Polygon[] getVisibleSidePolygons()
    {
        Point[] lowerSidePoints = bottomShape.getVisibleSidePoints();
        Point[] upperSidePoints = topShape.getVisibleSidePoints();
        Polygon[] giveReturn = new Polygon[lowerSidePoints.length-1];
        double[] topPoint =convertToPointWithHeight(getCenterCoordX(), getCenterCoordY(), getHeight());
        for(int i = 0; i < giveReturn.length; i++)
        {
            //int[] xPoints = {(int)sidePoints[i].getX(), (int)topPoint[0], (int)sidePoints[i+1].getX()};
            //int[] yPoints = {(int)sidePoints[i].getY(), (int)topPoint[1], (int)sidePoints[i+1].getY()};
            int[] xPoints = {(int)lowerSidePoints[i].getX(), (int)lowerSidePoints[i+1].getX(), (int)upperSidePoints[i+1].getX(), (int)upperSidePoints[i].getX()};
            int[] yPoints = {(int)lowerSidePoints[i].getY(), (int)lowerSidePoints[i+1].getY(), (int)upperSidePoints[i+1].getY(), (int)upperSidePoints[i].getY()};
            Polygon p = new Polygon(xPoints, yPoints, xPoints.length);
            giveReturn[i]=p;
        }
        return giveReturn;
    }
    
    @Override
    void updateShapePolygons()
    {
        try{
            bottomShape.setCenterCoordX(getCenterCoordX());
            bottomShape.setCenterCoordY(getCenterCoordY());
            bottomShape.setZPos(getZPos());
            topShape.setCenterCoordX(getCenterCoordX());
            topShape.setCenterCoordY(getCenterCoordY());
            topShape.setZPos(getZPos() + getHeight());
            threadedVisibleSidePolygons = getVisibleSidePolygons();
            
            
            
        }catch(Exception e)
        {
            
        }
    }

    @Override
    public void draw(Graphics g) 
    {
        //g.setColor(Color.BLUE);
        Polygon[] sidePolygons = threadedVisibleSidePolygons;//getVisibleSidePolygons();
        for(Polygon p : sidePolygons)
        {
            g.fillPolygon(p);
        }
        int[][] topShapePoints = topShape.getShapePolyPoints();
        g.fillPolygon(topShapePoints[0], topShapePoints[1], topShapePoints[0].length);
        g.setColor(Color.BLACK);
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.drawPolygon(p);
            
        }
        g.drawPolygon(topShapePoints[0], topShapePoints[1], topShapePoints[0].length);
        shadeSidePolygons(g, sidePolygons);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void drawExcludingTop(Graphics g) 
    {
        //g.setColor(Color.BLUE);
        Polygon[] sidePolygons = threadedVisibleSidePolygons;//getVisibleSidePolygons();
        try{
            for(Polygon p : sidePolygons)
            {
                g.fillPolygon(p);
            }
        
        
        
        g.setColor(Color.BLACK);
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.drawPolygon(p);
            
        }
        
        shadeSidePolygons(g, sidePolygons);
        }catch(Exception e)
        {
             System.out.println("TruncatedPyramid drawExcludingTop called with empty list!");//added because after tiles would be removed to be respawned, truncated pyramids still (for some reason) would try to draw themselves.

        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void fill(Graphics g) 
    {
        Polygon[] sidePolygons = threadedVisibleSidePolygons;//getVisibleSidePolygons();
        for(Polygon p : sidePolygons)
        {
            g.fillPolygon(p);
        }
        int[][] topShapePoints = topShape.getShapePolyPoints();
        g.fillPolygon(topShapePoints[0], topShapePoints[1], topShapePoints[0].length);
        
        shadeSidePolygons(g, sidePolygons);
    }

    @Override
    void stroke(Graphics g) 
    {
        Polygon[] sidePolygons = threadedVisibleSidePolygons;//getVisibleSidePolygons();
       
        int[][] topShapePoints = topShape.getShapePolyPoints();
       
        g.setColor(Color.BLACK);
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.drawPolygon(p);
            
        }
        g.drawPolygon(topShapePoints[0], topShapePoints[1], topShapePoints[0].length);
        
    }
    
    
}
