package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class FlatShape
{
    private double xCoord, yCoord, width, length, spin;
    private double centerCoordX, centerCoordY;
    private int zPos, numSides;
    
    public FlatShape(double inX, double inY, int inZPos, double radius, int sideNumberIn)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        centerCoordX = inX;
        centerCoordY = inY;
        xCoord = inX - (double)(radius/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        yCoord = inY - (double)(radius/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        zPos = inZPos;
        width = radius*(Math.sqrt(2))*2.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        length = radius*(Math.sqrt(2))*2.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        spin = Math.PI/4.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        numSides = sideNumberIn;
    }
    
    public FlatShape(double inX, double inY, int inZPos, double widthIn, double lengthIn, int sideNumberIn)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        centerCoordX = inX;
        centerCoordY = inY;
        xCoord = inX - (double)(widthIn/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        yCoord = inY - (double)(lengthIn/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        zPos = inZPos;
        width = widthIn*(Math.sqrt(2));//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        length = lengthIn*(Math.sqrt(2));//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        spin = Math.PI/4.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        numSides = sideNumberIn;
    }
    
    public double getCenterCoordX(){return centerCoordX;}
    public double getCenterCoordY(){return centerCoordY;}
    public double getWidth(){return width;}
    public double getLength(){return length;}
    public double getSpin(){return spin;}
    public int getZPos(){return zPos;}
    public int getNumSides(){return numSides;}
    
    public void setCenterCoordX(double newX)
    {
        centerCoordX = newX;
        xCoord = newX - (double)(width/2.0);
    }
    
    public void setCenterCoordY(double newY)
    {
        centerCoordY = newY;
        yCoord = newY - (double)(length/2.0);
    }
    
    public void setZPos(int newZPos)
    {
        zPos = newZPos;
    }
    
    public int[][] getShapePolyPoints()//bottom left, bottom right, top right, top left.
    {
        int[] xPoints = new int[numSides];
        int[] yPoints = new int[numSides];
        int currentSide = 0;
        for(double spinAmount = spin+(Math.PI); spinAmount < (Math.PI*3.0) + spin; spinAmount += ((Math.PI*2.0)/(double)numSides))
        {
            if(currentSide < numSides)
            {
                xPoints[currentSide] = (int)convertToPointX(centerCoordX + ((width/2.0)*Math.cos(spinAmount)), centerCoordY+((length/2.0)*Math.sin(spinAmount)));
                yPoints[currentSide] = (int)(convertToPointY(centerCoordX + ((width/2.0)*Math.cos(spinAmount)), centerCoordY+((length/2.0)*Math.sin(spinAmount)))-getScaledDistortedHeight(zPos));
            }
            currentSide++;
        }
        int[][] giveReturn = new int[2][numSides];
        giveReturn[0]=xPoints;
        giveReturn[1]=yPoints;
        
        return giveReturn;
    }
    
    public double getDistortedHeight(double heightIn)//one of the distortedHeights is redundant...
    {
        return Math.sin(WorldPanel.rotation)*heightIn;
    }  
    
    public double getScaledDistortedHeight(double heightIn)
    {
        return WorldPanel.scale * getDistortedHeight(heightIn);
    }
    
    public double convertToPointX(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        return WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta));
    }
    
    public double convertToPointY(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        return WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta));
    }
    
    public double[] convertToPoint(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        double[] giveReturn = {WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta)), WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta))};
        return giveReturn;
    }
    
    /*Fix for shading*/
    public void shadeSidePolygons(Graphics g, Polygon[] sidePolygons)
    {
        int numSides = sidePolygons.length;
        int shadeAlpha = 80;
        for(Polygon p : sidePolygons)
        {
            shadeAlpha -= (int)(30.0/(double)numSides);
            g.setColor(new Color(0,0,0, shadeAlpha - (int)((30.0/(double)numSides) * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
            g.fillPolygon(p);
        }
    }
    
    public Point[] getVisibleSidePoints()
    {
        Point[] sidePoints = new Point[(int)Math.ceil(numSides/2)+1];
        int pointStartNumber = getPointSideStartNumber();
        int[][] points = getShapePolyPoints();
        for(int i = 0; i < sidePoints.length; i++)
        {
            sidePoints[i]=new Point(points[0][pointStartNumber], points[1][pointStartNumber]);
            pointStartNumber += 1;
            if(pointStartNumber >= numSides)
            {
                pointStartNumber = 0;
            }
        }
        return sidePoints;
    }
    
    public void fillDropShadow(Graphics g, int lowerHeight)
    {
        int[][] points = getShapePolyPoints().clone();
        for(int i = 0; i < points[1].length; i++)
        {
            points[1][i] += getScaledDistortedHeight(zPos-lowerHeight);
        }
        
        g.setColor(new Color(0, 0, 0, 70));
        g.fillPolygon(points[0], points[1], points[0].length);
    }
    
    public int getPointSideStartNumber()
    {
        int polyStartNumber = ((int)((WorldPanel.radSpin+spin+(Math.PI*2.0/(double)(numSides*2.0)))/((Math.PI*2.0)/(double)numSides))%numSides);
        polyStartNumber = (numSides)-polyStartNumber;
        if(polyStartNumber >= numSides)
        {
            polyStartNumber = 0;
        }
        return polyStartNumber;
    }
}
