package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class FlatShape
{
    private double xCoord, yCoord, width, length, spin;
    private double radius = 0;
    private double centerCoordX, centerCoordY;
    private int zPos, numSides;
    
    public FlatShape(double inX, double inY, int inZPos, double radiusIn, int sideNumberIn)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        radius = radiusIn;//*(Math.sqrt(2))*2.0;
        centerCoordX = inX;
        centerCoordY = inY;////MAKE SURE X AND Y COORD ARE SET TO THE BOTTOM LEFT CORNER
        xCoord = inX +(double)(radius);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        yCoord = inY +(double)(radius);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        zPos = inZPos;
        width = radiusIn*(Math.sqrt(2))*2.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        length = radiusIn*(Math.sqrt(2))*2.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        spin = Math.PI/4.0;//made so that the edges of the shape are pallel to world lines. by default spawns in so that corners are parralel
        numSides = sideNumberIn;
    }
    
    public FlatShape(double inX, double inY, int inZPos, double widthIn, double lengthIn, int sideNumberIn)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        radius = widthIn/2.0;//*(Math.sqrt(2))*2.0;
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
    public double getRadius(){return radius;}
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
    
    public void setRadius(double newRadius)
    {
        radius = newRadius;
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
    /*
    public double[][] getShapeCoords()//bottom left, bottom right, top right, top left.
    {
        double[] xPoints = new double[numSides];
        double[] yPoints = new double[numSides];
        int currentSide = 0;
        for(double spinAmount = spin+(Math.PI); spinAmount < (Math.PI*3.0) + spin; spinAmount += ((Math.PI*2.0)/(double)numSides))
        {
            if(currentSide < numSides)
            {
                xPoints[currentSide] = centerCoordX + ((width/2.0)*Math.cos(spinAmount));
                yPoints[currentSide] = centerCoordY+ ((length/2.0)*Math.sin(spinAmount));
            }
            currentSide++;
        }
        double[][] giveReturn = new double[2][numSides];
        giveReturn[0]=xPoints;
        giveReturn[1]=yPoints;
        
        return giveReturn;
    }*/
    
    public double[][] getShapeCoords()//bottom left, bottom right, top right, top left.
    {
        double[] xPoints = new double[numSides];
        double[] yPoints = new double[numSides];
        int currentSide = 0;
        for(double spinAmount = spin+(Math.PI); spinAmount < (Math.PI*3.0) + spin; spinAmount += ((Math.PI*2.0)/(double)numSides))
        {
            if(currentSide < numSides)
            {
                xPoints[currentSide] = centerCoordX + ((width/2.0)*Math.cos(spinAmount));
                yPoints[currentSide] = centerCoordY+((length/2.0)*Math.sin(spinAmount));
            }
            currentSide++;
        }
        double[][] giveReturn = new double[2][numSides];
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
    
    public double[] getCoordAtRotation( double rotation)
    {
        int startIndex = (int)(((((Math.PI*2.0)+rotation-(Math.PI/4.0))))/((Math.PI*2.0/(double)numSides)))%(numSides);
        int endIndex = startIndex+1;
        if(startIndex == numSides-1)
        {
            endIndex = 0;
        }
        
        double[][] points = getShapeCoords();
        //System.out.println(getShapeCoords()[1][0]);
        double x1 = points[0][startIndex];
        double x2 = points[0][endIndex];
        double y1 = points[1][startIndex];
        double y2 = points[1][endIndex];
        double edgeSlope = (y2-y1)/(x2-x1);
        double xIntersect = 0, yIntersect= 0;
        if(edgeSlope < 1)
        {
           xIntersect = (edgeSlope*x1 - y1 + centerCoordY)/(edgeSlope - Math.tan(rotation));
           yIntersect = Math.tan(rotation)*(xIntersect ) + centerCoordY;
           xIntersect += centerCoordX;
        }else{
            xIntersect = x1;
            yIntersect = Math.tan(rotation)*(xIntersect - centerCoordX)+ centerCoordY;
        }
        //xIntersect += centerCoordX;
        //System.out.println(edgeSlope);
        //double xIntersect = (edgeSlope*x1 - y1 + centerCoordY)/(edgeSlope - Math.tan(rotation));
        //double xIntersect = ((-edgeSlope * x1) + y1 + Math.tan(rotation)*centerCoordX + centerCoordY)/(Math.tan(rotation)-edgeSlope);
        //double yIntersect = Math.tan(rotation)*(xIntersect)+ centerCoordY;
        //double edgeSlope = (points[1][endIndex]-points[1][startIndex])/(points[0][endIndex]-points[0][startIndex]);
        //double xIntersect = (-edgeSlope*points[0][startIndex] + points[1][startIndex])/(Math.sin(rotation)+edgeSlope);
        //double yIntersect = Math.sin(rotation)*xIntersect;
        double[] giveReturn = {xIntersect, yIntersect};
        /*g.setColor(Color.GREEN);
        g.fillOval(getShapePolyPoints()[0][startIndex]-5, getShapePolyPoints()[1][startIndex]-5, 10, 10);
        g.setColor(Color.BLUE);
        g.fillOval(getShapePolyPoints()[0][endIndex]-5, getShapePolyPoints()[1][endIndex]-5, 10, 10);
        g.setColor(Color.RED);
        
        
        g.drawLine((int)(convertToPointX(x1, y1)),(int)(convertToPointY(x1, y1)),(int)(convertToPointX(x2, y2)),(int)(convertToPointY(x2, y2)));
        g.drawLine((int)convertToPointX(xIntersect, yIntersect), (int)convertToPointY(xIntersect, yIntersect), (int)convertToPointX(centerCoordX, centerCoordY), (int)(convertToPointY(centerCoordX, centerCoordY)));*/
        return giveReturn;
        
    }
    
    public double[] getVisibleCoordAtRotation( double rotation)
    {
        //int startIndex = (int)(((((Math.PI*2.0)+rotation-(Math.PI/4.0))))/((Math.PI*2.0/(double)numSides)))%(numSides);
        int startIndex = (int)(((((Math.PI*2.0)+rotation+(Math.PI/4.0))))/((Math.PI*2.0/(double)numSides)))%(numSides);
        int endIndex = startIndex+1;
        if(startIndex == numSides-1)
        {
            endIndex = 0;
        }
        //System.out.println("vis1: "+getVisibleSideIndexes()[0]);
        //System.out.println("vis2: "+getVisibleSideIndexes()[1]);
        //System.out.println(startIndex);
        int[] bounds = getVisibleSideIndexes();
        
        if(bounds[0] < bounds[1])
        {
            if(startIndex > bounds[0] && startIndex <= bounds[1])
            {
                return getCoordAtRotation(rotation);
            }
        }else{
            if(startIndex > bounds[0] || startIndex <= bounds[1])
            {
                return getCoordAtRotation(rotation);
            }
        }
        
        
        return null;
        
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
    
    public int[] getVisibleSideIndexes()
    {
        int[] giveReturn = {getPointSideStartNumber(), (getPointSideStartNumber()+(int)Math.ceil(numSides/2.0))%numSides};
        //Point[] sidePoints = new Point[(int)Math.ceil(numSides/2)+1];
        return giveReturn;
    
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
