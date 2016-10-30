package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;

public class RectPrism extends SolidShape
{
    private int[][] threadedUpperPoints;
    private Polygon[] threadedVisibleSidePolygons;
    public RectPrism(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight) 
    {
        super(inX, inY, inZPos, inWidth, inLength, inHeight);
        updateShapePolygons();
    }
    
    public RectPrism(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight, double spinIn) 
    {
        super(inX, inY, inZPos, inWidth, inLength, inHeight, spinIn);
        updateShapePolygons();
    }

    @Override
    void draw(Graphics g) 
    {
        Color c = g.getColor();
        //g.setColor(Color.BLUE);
        int[][] upperPoints = threadedUpperPoints;//getUpperBoundingShapePolyPoints();
        //int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        g.fillPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.fillPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        //g.setColor(Color.BLUE);
        //int[][] leftPoints = getLeftBoundingShapePolyPoints();
        //int[][] rightPoints = getRightBoundingShapePolyPoints();
        g.fillPolygon(threadedVisibleSidePolygons[0]);
        g.fillPolygon(threadedVisibleSidePolygons[1]);
        
        g.setColor(Color.BLACK);
        
        g.drawPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        
        
        g.drawPolygon(threadedVisibleSidePolygons[0]);
        g.drawPolygon(threadedVisibleSidePolygons[1]);
        
        
        
        
        Polygon[] poly = threadedVisibleSidePolygons;
        shadeSidePolygons(g, poly, c);
    }   
    
    public void drawExcludingTop(Graphics g)
    {
        Color c = g.getColor();
        //g.setColor(Color.BLUE);
        int[][] upperPoints = threadedUpperPoints;//getUpperBoundingShapePolyPoints();
        //int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        //g.fillPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.fillPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        //g.setColor(Color.BLUE);
        //int[][] leftPoints = getLeftBoundingShapePolyPoints();
        //int[][] rightPoints = getRightBoundingShapePolyPoints();
        g.fillPolygon(threadedVisibleSidePolygons[0]);
        g.fillPolygon(threadedVisibleSidePolygons[1]);
        
        g.setColor(Color.BLACK);
        
        //g.drawPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.drawPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        
        g.drawPolygon(threadedVisibleSidePolygons[0]);
        g.drawPolygon(threadedVisibleSidePolygons[1]);
        
        //paintShading(g);
        Polygon[] poly = threadedVisibleSidePolygons;
        shadeSidePolygons(g, poly, c);
        
        //Pyramid py = new Pyramid(3, 3, 0, 1, 100, 8);
        //py.draw(g);
        //FlatShape stretchShape = new FlatShape(-3, -2, 0, 1, 2, 4);
        //g.fillPolygon(stretchShape.getShapePolyPoints()[0], stretchShape.getShapePolyPoints()[1], stretchShape.getNumSides());
        //TruncatedPyramid tp = new TruncatedPyramid(1,-2, 0, 0.4, 50, 4, 0.5);
        //tp.draw(g);
    }
    
    
    
    @Override
    public void fillExcludingTop(Graphics g)
    {
        Color c = g.getColor();
        //g.setColor(Color.BLUE);
        int[][] upperPoints = threadedUpperPoints;//getUpperBoundingShapePolyPoints();
        //int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        //g.fillPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.fillPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        //g.setColor(Color.BLUE);
        //int[][] leftPoints = getLeftBoundingShapePolyPoints();
        //int[][] rightPoints = getRightBoundingShapePolyPoints();
        g.fillPolygon(threadedVisibleSidePolygons[0]);
        g.fillPolygon(threadedVisibleSidePolygons[1]);
        
        g.setColor(Color.BLACK);
        
        //g.drawPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.drawPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        
        g.fillPolygon(threadedVisibleSidePolygons[0]);
        g.fillPolygon(threadedVisibleSidePolygons[1]);
        
        //paintShading(g);
        Polygon[] poly = threadedVisibleSidePolygons;
        shadeSidePolygons(g, poly, c);
        
        //Pyramid py = new Pyramid(3, 3, 0, 1, 100, 8);
        //py.draw(g);
        //FlatShape stretchShape = new FlatShape(-3, -2, 0, 1, 2, 4);
        //g.fillPolygon(stretchShape.getShapePolyPoints()[0], stretchShape.getShapePolyPoints()[1], stretchShape.getNumSides());
        //TruncatedPyramid tp = new TruncatedPyramid(1,-2, 0, 0.4, 50, 4, 0.5);
        //tp.draw(g);
    }
    
    public void paintShading(Graphics g) 
    {
        Color c = g.getColor();
        Polygon[] poly = threadedVisibleSidePolygons;
        shadeSidePolygons(g, poly,c);
        //shadeBoundingBoxSides(g);
    }

    @Override
    public void updateShapePolygons() 
    {
        threadedUpperPoints = getUpperBoundingShapePolyPoints();
        int[][] leftP = getLeftBoundingShapePolyPoints();
        int[][] rightP = getRightBoundingShapePolyPoints();
        Polygon[] tempP ={new Polygon(leftP[0], leftP[1], leftP[0].length), new Polygon(rightP[0], rightP[1], rightP[0].length)};
        threadedVisibleSidePolygons = tempP;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    void fill(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;
        //g.setColor(Color.BLUE);
        //int[][] upperPoints = threadedUpperPoints;//getUpperBoundingShapePolyPoints();
        //int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        Area a = new Area(new Polygon(threadedUpperPoints[0], threadedUpperPoints[1],threadedUpperPoints[0].length));
        a.intersect(WorldPanel.clipArea);
        g2.fill(a);
        //g.fillPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        //g.fillPolygon(lowerPoints[0], lowerPoints[1], lowerPoints[0].length);
        //g.setColor(Color.BLUE);
        //int[][] leftPoints = getLeftBoundingShapePolyPoints();
        //int[][] rightPoints = getRightBoundingShapePolyPoints();
        //g.fillPolygon(threadedVisibleSidePolygons[0]);
        //g.fillPolygon(threadedVisibleSidePolygons[1]);
        
        
        
        //dotSortCorner(g);
    }

    @Override
    void stroke(Graphics g) 
    {
        //g.setColor(Color.BLUE);
        int[][] upperPoints = threadedUpperPoints;//getUpperBoundingShapePolyPoints();
        //int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        
        g.setColor(Color.BLACK);
        
        g.drawPolygon(upperPoints[0], upperPoints[1], upperPoints[0].length);
        
        
        g.drawPolygon(threadedVisibleSidePolygons[0]);
        g.drawPolygon(threadedVisibleSidePolygons[1]);
        
    }
    
}
