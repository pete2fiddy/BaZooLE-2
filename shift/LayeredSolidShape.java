package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

public class LayeredSolidShape extends SolidShape
{
    private int numSides;
    private FlatShape[] flatShapes;
    private ArrayList<Polygon> threadedVisibleSidePolygons = new ArrayList<Polygon>();
    public LayeredSolidShape(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight, int numSidesIn, FlatShape[] flatShapesIn) 
    {
        super(inX, inY, inZPos, inWidth, inLength, inHeight);
        numSides = numSidesIn;
        flatShapes = flatShapesIn;
    }

    @Override
    void updateShapePolygons() 
    {
        threadedVisibleSidePolygons.clear();
        for(int i = 0; i < flatShapes.length ; i++)
        {
            if(i < flatShapes.length - 1)
            {
                Polygon[] visibleSides = getVisibleSidePolygonsAtIndex(i);
                for(int j = 0; j < visibleSides.length; j++)
                {
                    threadedVisibleSidePolygons.add(visibleSides[j]);
                }
            }
            setZPos(getZPos()+(int)getDZ());
            flatShapes[i].setZPos((int)(flatShapes[i].getZPos() + getDZ()));
        }
    }

    public void updateFlatShapes(double coordX, double coordY)
    {
         for(FlatShape fs : flatShapes)
         {
             fs.setCenterCoordX(coordX);
             fs.setCenterCoordY(coordY);
         }
    }
    
    private Polygon[] getVisibleSidePolygonsAtIndex(int flatShapeIndex)
    {
        Point[] lowerSidePoints = flatShapes[flatShapeIndex].getVisibleSidePoints();
        Point[] upperSidePoints = flatShapes[flatShapeIndex+1].getVisibleSidePoints();
        Polygon[] giveReturn = new Polygon[lowerSidePoints.length-1];
        double[] topPoint =convertToPointWithHeight(getCenterCoordX(), getCenterCoordY(), getHeight());
        for(int i = 0; i < giveReturn.length; i++)
        {
            int[] xPoints = {(int)lowerSidePoints[i].getX(), (int)lowerSidePoints[i+1].getX(), (int)upperSidePoints[i+1].getX(), (int)upperSidePoints[i].getX()};
            int[] yPoints = {(int)lowerSidePoints[i].getY(), (int)lowerSidePoints[i+1].getY(), (int)upperSidePoints[i+1].getY(), (int)upperSidePoints[i].getY()};
            Polygon p = new Polygon(xPoints, yPoints, xPoints.length);
            giveReturn[i]=p;
        }
        return giveReturn;
    }
    
    @Override
    void draw(Graphics g) 
    {
        updateShapePolygons();
        Color drawColor = g.getColor();
        int numPolygons = 0;
        
        for(int i = 0; i < threadedVisibleSidePolygons.size(); i++)
        {
            g.setColor(drawColor);
            g.fillPolygon(threadedVisibleSidePolygons.get(i));
            g.setColor(Color.BLACK);
            g.drawPolygon(threadedVisibleSidePolygons.get(i));
            if(((i)%(int)(Math.ceil((double)numSides/2.0))) == 1)
            {
                Polygon[] sides = new Polygon[(int)(Math.ceil((double)numSides/2.0))];
                for(int j = 0; j < sides.length; j++)
                {
                    sides[j] = threadedVisibleSidePolygons.get(i-1+ j);
                }
            }
        }
    }

    @Override
    void drawExcludingTop(Graphics g) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void fill(Graphics g) 
    {
        updateShapePolygons();
        Color drawColor = g.getColor();
        int numPolygons = 0;
        
        for(int i = 0; i < threadedVisibleSidePolygons.size(); i++)
        {
            g.setColor(drawColor);
            g.fillPolygon(threadedVisibleSidePolygons.get(i));
            
            if(((i)%(int)(Math.ceil((double)numSides/2.0))) == 1)
            {
                Polygon[] sides = new Polygon[(int)(Math.ceil((double)numSides/2.0))];
                for(int j = 0; j < sides.length; j++)
                {
                    sides[j] = threadedVisibleSidePolygons.get(i-1+ j);
                }
            }
        }
        for(int i = 0; i < flatShapes.length - 1; i++)
        {
            Polygon[] visibleSides = getVisibleSidePolygonsAtIndex(i);
            shadeSidePolygonsWithZPos(g, visibleSides, flatShapes[i].getZPos());
        }
    }

    @Override
    public void fillDropShadow(Graphics g, int lowerHeight)
    {
        int biggestWidthIndex = 0;
        int biggestLengthIndex = 0;
        for(int i = 0; i < flatShapes.length; i++)
        {
            if(flatShapes[i].getWidth() > flatShapes[biggestWidthIndex].getWidth())
            {
                biggestWidthIndex = i;
            }
            if(flatShapes[i].getLength() > flatShapes[biggestLengthIndex].getLength())
            {
                biggestLengthIndex = i;
            }
        }
        g.setColor(new Color(0,0,0,20));
        flatShapes[biggestWidthIndex].fillDropShadow(g, lowerHeight);
        flatShapes[biggestLengthIndex].fillDropShadow(g, lowerHeight);
    }
    
    @Override
    void stroke(Graphics g) 
    {
        updateShapePolygons();
        Color drawColor = g.getColor();
        int numPolygons = 0;
        
        for(int i = 0; i < threadedVisibleSidePolygons.size(); i++)
        {
            g.setColor(Color.BLACK);
            g.drawPolygon(threadedVisibleSidePolygons.get(i));   
        }   
    }

    @Override
    void fillExcludingTop(Graphics g) 
    {
        fill(g);
        System.out.println("fillExcludingTop defers to filling normally");
    }
}
