/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

/**
 *
 * @author phusisian
 */
public abstract class SolidShape 
{
    private double xCoord, yCoord, width, length, spin;
    private double centerCoordX, centerCoordY;
    private int height, zPos;
    public SolidShape(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        centerCoordX = inX;
        centerCoordY = inY;
        xCoord = inX - (double)(inWidth/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        yCoord = inY - (double)(inLength/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        zPos = inZPos;
        width = inWidth;
        length = inLength;
        height = inHeight;
        spin = 0;
    }
    
    public SolidShape(double inX, double inY, int inZPos, double inWidth, double inLength, int inHeight, double spinIn)//consider adding a keyword saying from where the shape is spawned. E.G. points passed to it are from the top right, instead of middle, etc.
    {
        centerCoordX = inX;
        centerCoordY = inY;
        xCoord = inX - (double)(inWidth/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        yCoord = inY - (double)(inLength/2.0);//so that they are spawned from the center of the shape, but have coordinate at bottom left
        zPos = inZPos;
        width = inWidth;
        length = inLength;
        height = inHeight;
        spin = spinIn;
    }
    
    public double getCenterCoordX(){return centerCoordX;}
    public double getCenterCoordY(){return centerCoordY;}
    public int getZPos(){return zPos;}
    public int getHeight(){return height;}
    public double getWidth(){return width;}
    public double getLength(){return length;}
    
    public double getSortDistanceConstant()
    {
        double cornerX, cornerY;
        int slope;
        double constant;
        if(WorldPanel.radSpin > 0 && WorldPanel.radSpin <= (Math.PI/2.0))
        {
            cornerX = xCoord;
            cornerY = yCoord;
            slope = -1;
             constant = (cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI/2.0 && WorldPanel.radSpin <= (Math.PI))
        {
            cornerX = xCoord;
            cornerY = yCoord+length;
            slope = 1;
             constant = -(cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI && WorldPanel.radSpin <= (3*Math.PI/2.0))
        {
            cornerX = xCoord+width;
            cornerY = yCoord+length;
            slope = -1;
            constant = -(cornerY-(slope*cornerX));
        }else{
            cornerX = xCoord+width;
            cornerY = yCoord;
            slope = 1;
            constant = cornerY-(slope*cornerX);
        }
       
        return constant;
        
    }
    
    public void setCenterCoordX(double newX)
    {
        centerCoordX = newX;
        xCoord = newX - (width/2.0);
    }
    public void setCenterCoordY(double newY)
    {
        centerCoordY = newY;
        yCoord = newY - (length/2.0);
    }
    public void setZPos(int newZPos)
    {
        zPos = newZPos;
    }
    public int[][] getLowerBoundingShapePolyPoints()//bottom left, bottom right, top right, top left.
    {
        //spin += Math.PI/128.0;
        //if(spin > Math.PI*2.0)
        //{
        //    spin -= Math.PI*2.0;
        //}
        
        
        double lengthTheta = Math.atan2(-Math.cos(spin), Math.sin(spin));
        double widthTheta = Math.atan2(Math.cos(spin), -Math.sin(spin));
        
        int[] xPoints = {(int)convertToPointX(xCoord, yCoord), (int)convertToPointX(xCoord+width, yCoord), (int)convertToPointX(xCoord+width, yCoord+length), (int)convertToPointX(xCoord, yCoord+length)};
        int[] yPoints = {(int)convertToPointY(xCoord, yCoord), (int)convertToPointY(xCoord+width, yCoord), (int)convertToPointY(xCoord+width, yCoord+length), (int)convertToPointY(xCoord, yCoord+length)};
        //int[] xPoints = {(int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI)+spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI*3.0/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI*3.0/2.0)+spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos(spin), centerCoordY + (length/2.0)*Math.sin(spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI/2.0)+spin))};
        //int[] yPoints = {(int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI)+spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI*3.0/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI*3.0/2.0)+spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos(spin), centerCoordY + (length/2.0)*Math.sin(spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI/2.0)+spin))};
        
        //int[] xPoints = {(int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI)+spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI*3.5/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI*3.0/2.0)+spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos(spin), centerCoordY + (length/2.0)*Math.sin(spin)), (int)convertToPointX(centerCoordX + (width/2.0)*Math.cos((Math.PI/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI/2.0)+spin))};
        //int[] yPoints = {(int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI)+spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI*3.5/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI*3.0/2.0)+spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos(spin), centerCoordY + (length/2.0)*Math.sin(spin)), (int)convertToPointY(centerCoordX + (width/2.0)*Math.cos((Math.PI/2.0)+spin), centerCoordY + (length/2.0)*Math.sin((Math.PI/2.0)+spin))};
        
        
        
        for(int i = 0; i < yPoints.length; i++)
        {
            yPoints[i] -= (int)getScaledDistortedHeight(zPos);
        }
        int[][] giveReturn = new int[2][4];
        giveReturn[0] = xPoints;
        giveReturn[1] = yPoints;
        return giveReturn;
    }
    
    
    
    public int[][] getUpperBoundingShapePolyPoints()//bottom left, bottom right, top right, top left.
    {
        int[][] giveReturn = new int[2][4];
        int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        for(int i = 0; i < lowerPoints[0].length; i++)
        {
            giveReturn[0][i] = lowerPoints[0][i];
            giveReturn[1][i] = lowerPoints[1][i] - (int)getScaledDistortedHeight(height);
        }
        return giveReturn;
    }
    
    public int[][] getLeftBoundingShapePolyPoints()//bottom left, bottom right, top right, top left. Find some way to efficientize so that if/else chanins aren't needed.
    {
        int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        
        if((WorldPanel.radSpin)%(Math.PI*2.0) >= 0 && (WorldPanel.radSpin)%(Math.PI*2.0) < (Math.PI/2.0))
        {
            int[] xPoints = {lowerPoints[0][3], lowerPoints[0][0], lowerPoints[0][0], lowerPoints[0][3]};
            int[] yPoints = {lowerPoints[1][3], lowerPoints[1][0], lowerPoints[1][0]- (int)getScaledDistortedHeight(height), lowerPoints[1][3]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else if((WorldPanel.radSpin)%(Math.PI*2.0) >= (Math.PI/2.0) && (WorldPanel.radSpin)%(Math.PI*2.0) < (Math.PI))
        {
            int[] xPoints = {lowerPoints[0][2], lowerPoints[0][3], lowerPoints[0][3], lowerPoints[0][2]};
            int[] yPoints = {lowerPoints[1][2], lowerPoints[1][3], lowerPoints[1][3]- (int)getScaledDistortedHeight(height), lowerPoints[1][2]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else if((WorldPanel.radSpin)%(Math.PI*2.0) >= (Math.PI) && (WorldPanel.radSpin)%(Math.PI*2.0) < (3.0*Math.PI/2.0))
        {
            int[] xPoints = {lowerPoints[0][1], lowerPoints[0][2], lowerPoints[0][2], lowerPoints[0][1]};
            int[] yPoints = {lowerPoints[1][1], lowerPoints[1][2], lowerPoints[1][2]- (int)getScaledDistortedHeight(height), lowerPoints[1][1]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else{
            int[] xPoints = {lowerPoints[0][0], lowerPoints[0][1], lowerPoints[0][1], lowerPoints[0][0]};
            int[] yPoints = {lowerPoints[1][0], lowerPoints[1][1], lowerPoints[1][1]- (int)getScaledDistortedHeight(height), lowerPoints[1][0]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }
    }
    
    
    public int[][] getRightBoundingShapePolyPoints()//bottom left, bottom right, top right, top left. Find some way to efficientize so that if/else chanins aren't needed.
    {
        int[][] lowerPoints = getLowerBoundingShapePolyPoints();
        
        if((WorldPanel.radSpin)%(Math.PI*2.0) >= 0 && (WorldPanel.radSpin)%(Math.PI*2.0) < (Math.PI/2.0))
        {
            int[] xPoints = {lowerPoints[0][0], lowerPoints[0][1], lowerPoints[0][1], lowerPoints[0][0]};
            int[] yPoints = {lowerPoints[1][0], lowerPoints[1][1], lowerPoints[1][1]- (int)getScaledDistortedHeight(height), lowerPoints[1][0]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else if((WorldPanel.radSpin)%(Math.PI*2.0) >= (Math.PI/2.0) && (WorldPanel.radSpin)%(Math.PI*2.0) < (Math.PI))
        {
            int[] xPoints = {lowerPoints[0][3], lowerPoints[0][0], lowerPoints[0][0], lowerPoints[0][3]};
            int[] yPoints = {lowerPoints[1][3], lowerPoints[1][0], lowerPoints[1][0]- (int)getScaledDistortedHeight(height), lowerPoints[1][3]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else if((WorldPanel.radSpin)%(Math.PI*2.0) >= (Math.PI) && (WorldPanel.radSpin)%(Math.PI*2.0) < (3.0*Math.PI/2.0))
        {
            int[] xPoints = {lowerPoints[0][2], lowerPoints[0][3], lowerPoints[0][3], lowerPoints[0][2]};
            int[] yPoints = {lowerPoints[1][2], lowerPoints[1][3], lowerPoints[1][3]- (int)getScaledDistortedHeight(height), lowerPoints[1][2]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }else{
            int[] xPoints = {lowerPoints[0][1], lowerPoints[0][2], lowerPoints[0][2], lowerPoints[0][1]};
            int[] yPoints = {lowerPoints[1][1], lowerPoints[1][2], lowerPoints[1][2]- (int)getScaledDistortedHeight(height), lowerPoints[1][1]-(int)getScaledDistortedHeight(height)};
            int[][] giveReturn = new int[2][4];
            giveReturn[0] = xPoints;
            giveReturn[1] = yPoints;
            return giveReturn;
        }
    }
    
    
    
    public void shadeBoundingBoxSides(Graphics g)
    {
        int leftAlpha = 80-(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        g.setColor(new Color(0,0,0,leftAlpha));
        int[][] leftPoints = getLeftBoundingShapePolyPoints();
        int[][] rightPoints = getRightBoundingShapePolyPoints();
        g.fillPolygon(leftPoints[0], leftPoints[1], leftPoints[0].length);
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        int rightAlpha = 50-(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        g.setColor(new Color(0,0,0,rightAlpha));
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(rightPoints[0], rightPoints[1], rightPoints[0].length);
        g.setColor(Color.BLACK);
    
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
    
    public double convertToPointYWithHeight(double x, double y, int heightIn)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        return WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta)) - getScaledDistortedHeight(heightIn);
        
    }
    
    public double[] convertToPoint(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        double[] giveReturn = {WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta)), WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta))};
        return giveReturn;
    }
    
    public double[] convertToPointWithHeight(double x, double y, int heightIn)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        double[] giveReturn = {WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta)), WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta))-getScaledDistortedHeight(heightIn)};
        return giveReturn;
    }
    
    public void shadeSidePolygons(Graphics g, Polygon[] sidePolygons)
    {
        int numSides = sidePolygons.length;
        //int leftAlpha = 80-(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        int shadeAlpha = 80;
        for(Polygon p : sidePolygons)
        {
            shadeAlpha -= (int)(30.0/(double)numSides);
            g.setColor(new Color(0,0,0, shadeAlpha - (int)((30.0/(double)numSides) * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
            g.fillPolygon(p);
        }
        
    }
    
    abstract void updateShapePolygons();
    //abstract void setShapeSpecificPolygons();
    //abstract void paintShading(Graphics g);
    abstract void draw(Graphics g);
    abstract void drawExcludingTop(Graphics g);
}
