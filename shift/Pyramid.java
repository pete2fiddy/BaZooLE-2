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
public class Pyramid extends SolidShape
{
    private FlatShape polyBase;
    private int numSides;
    private Polygon[] threadedVisibleSidePolygons;
    private int[][] threadedVisibleBasePolyPoints;
    
    public Pyramid(double inX, double inY, int inZPos, double radiusIn, int inHeight, int numSidesIn) 
    {
        super(inX, inY, inZPos, radiusIn*2, radiusIn*2, inHeight);
        numSides = numSidesIn;
        polyBase = new FlatShape(inX, inY, inZPos, radiusIn, numSidesIn);
        updateShapePolygons();
    }

    void paintShading(Graphics g) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public FlatShape getBaseShape(){return polyBase;}
    public int getNumSides(){return numSides;}

    private Polygon[] getVisibleSidePolygons()
    {
        Point[] sidePoints = polyBase.getVisibleSidePoints();
        Polygon[] giveReturn = new Polygon[sidePoints.length-1];
        double[] topPoint =convertToPointWithHeight(getCenterCoordX(), getCenterCoordY(), getHeight() + getZPos());
        for(int i = 0; i < giveReturn.length; i++)
        {
            int[] xPoints = {(int)sidePoints[i].getX(), (int)topPoint[0], (int)sidePoints[i+1].getX()};
            int[] yPoints = {(int)sidePoints[i].getY(), (int)topPoint[1], (int)sidePoints[i+1].getY()};
            Polygon p = new Polygon(xPoints, yPoints, 3);
            giveReturn[i]=p;
        }
        return giveReturn;
    }
    
    @Override
    public void updateShapePolygons()
    {
        polyBase.setCenterCoordX(getCenterCoordX());
        polyBase.setCenterCoordY(getCenterCoordY());

        threadedVisibleBasePolyPoints = polyBase.getShapePolyPoints().clone();
        threadedVisibleSidePolygons = getVisibleSidePolygons().clone();
        
        polyBase.setZPos(getZPos());
                
        
    }
    
    @Override
    void draw(Graphics g) 
    {
        //g.setColor(Color.BLUE);
        //Polygon[] sidePolygons = getVisibleSidePolygons();
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.fillPolygon(p);
            
        }
        
        g.setColor(Color.BLACK);
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.drawPolygon(p);
            
        }
        shadeSidePolygons(g, threadedVisibleSidePolygons);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void drawExcludingTop(Graphics g)
    {
        
    }

    @Override
    void fill(Graphics g) {
        //g.setColor(Color.BLUE);
        //Polygon[] sidePolygons = getVisibleSidePolygons();
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.fillPolygon(p);
            
        }
        
        
        shadeSidePolygons(g, threadedVisibleSidePolygons);
    }

    @Override
    void stroke(Graphics g) 
    {
        g.setColor(Color.BLACK);
        for(Polygon p : threadedVisibleSidePolygons)
        {
            g.drawPolygon(p);
            
        }
    }

    
}
