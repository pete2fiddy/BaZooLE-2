package shift;

import java.awt.Graphics;

public abstract class Scenery extends Toolbox implements Runnable
{
    private Tile boundTile;
    private double offsetX, offsetY;
    private Thread thread;
    private double threadedX, threadedY, threadedThetaFromCenter, threadedRadius;
    private double boundingBoxWidth = 0, boundingBoxLength = 0;
    
    public Scenery(Tile tileIn, double offsetXIn, double offsetYIn)//offsetX and offsetY are the scenery's position relative to the base corner of the tile. 
    {
        boundTile = tileIn;
        offsetX = offsetXIn;
        offsetY = offsetYIn;
        threadedX = getX();
        threadedY = getY();
        threadedThetaFromCenter = getThetaFromCenter();
        threadedRadius = getRadius();
        thread = new Thread(this);
        thread.start();
        
    }
    
    public void setBoundingBoxDimensions(double widthIn, double lengthIn)
    {
        boundingBoxWidth = widthIn; boundingBoxLength = lengthIn;
    }
    
    public double getSortDistanceConstant()
    {
        double cornerX, cornerY;
        int slope;
        double constant;
        if(WorldPanel.radSpin > 0 && WorldPanel.radSpin <= (Math.PI/2.0))
        {
            cornerX = getCoordX();
            cornerY = getCoordY();
            slope = -1;
            constant = (cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI/2.0 && WorldPanel.radSpin <= (Math.PI))
        {
            cornerX = getCoordX();
            cornerY = getCoordY()+boundingBoxLength;
            slope = 1;
             constant = -(cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI && WorldPanel.radSpin <= (3*Math.PI/2.0))
        {
            cornerX = getCoordX()+boundingBoxWidth;
            cornerY = getCoordY()+boundingBoxLength;
            slope = -1;
            constant = -(cornerY-(slope*cornerX));
        }else{
            cornerX = getCoordX()+boundingBoxWidth;
            cornerY = getCoordY();
            slope = 1;
            constant = cornerY-(slope*cornerX);
        }
       
        return constant;
        
    }
    
    public double getMiddleSortDistanceConstant()
    {
        double cornerX, cornerY;
        int slope;
        double constant;
        if(WorldPanel.radSpin > 0 && WorldPanel.radSpin <= (Math.PI/2.0))
        {
            cornerX = getCoordX();
            cornerY = getCoordY();
            slope = -1;
            constant = (cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI/2.0 && WorldPanel.radSpin <= (Math.PI))
        {
            cornerX = getCoordX();
            cornerY = getCoordY();
            slope = 1;
             constant = -(cornerY-(slope*cornerX));
        }else if(WorldPanel.radSpin > Math.PI && WorldPanel.radSpin <= (3*Math.PI/2.0))
        {
            cornerX = getCoordX();
            cornerY = getCoordY();
            slope = -1;
            constant = -(cornerY-(slope*cornerX));
        }else{
            cornerX = getCoordX();
            cornerY = getCoordY();
            slope = 1;
            constant = cornerY-(slope*cornerX);
        }
       
        return constant;
        
    
    }
    public double getCoordX(){return boundTile.getRawX() + (offsetX * boundTile.getRawWidth());}
    public double getCoordY(){return boundTile.getRawY() + (offsetY * boundTile.getRawLength());}
    public double convertOffsetXToCoord(double offset){return boundTile.getRawX() + (offset * boundTile.getRawWidth());}
    public double convertOffsetYToCoord(double offset){return boundTile.getRawY() + (offset * boundTile.getRawLength());}
    public Thread getThread(){return thread;}
    public void setThread(Thread t){thread = t;}
    public double getThreadedThetaFromCenter(){return threadedThetaFromCenter;}
    public double getThreadedX(){return threadedX;}
    public double getThreadedY(){return threadedY;}
    public double getThreadedRadius(){return threadedRadius;}
    public double getOffsetY(){return offsetY;}
    public double getOffsetX(){return offsetX;}
    public Tile getBoundTile(){return boundTile;}
    
    public double getThetaFromCenter()
    {
        return Math.atan2((boundTile.getRawY()*WorldPanel.straightUnit)+(boundTile.getRawLength()*WorldPanel.straightUnit*offsetY), (boundTile.getRawX()*WorldPanel.straightUnit) + boundTile.getRawWidth()*WorldPanel.straightUnit*offsetX);
    }
    
    public double getRadius()
    {
        return Math.sqrt(Math.pow(((boundTile.getRawX()*WorldPanel.straightUnit)+boundTile.getRawWidth() * WorldPanel.straightUnit * offsetX), 2) + Math.pow((boundTile.getRawY()*WorldPanel.straightUnit)+(boundTile.getRawLength() * WorldPanel.straightUnit * offsetY),2));
    }
    
    public double getRadius(double offsetXIn, double offsetYIn)
    {
        return Math.sqrt(Math.pow(((boundTile.getRawX()*WorldPanel.straightUnit)+boundTile.getRawWidth() * WorldPanel.straightUnit * offsetXIn), 2) + Math.pow((boundTile.getRawY()*WorldPanel.straightUnit)+(boundTile.getRawLength() * WorldPanel.straightUnit * offsetYIn),2));
    }
    
    public double getThetaFromCenter(double offsetXIn, double offsetYIn)
    {
        return Math.atan2((boundTile.getRawY()*WorldPanel.straightUnit)+(boundTile.getRawLength()*WorldPanel.straightUnit*offsetYIn), (boundTile.getRawX()*WorldPanel.straightUnit) + boundTile.getRawWidth()*WorldPanel.straightUnit*offsetXIn);
    }
    
    public double[] getPosAtPoint(double offsetXIn, double offsetYIn)//gives point at a certain offset of the bound tile
    {
        double[] giveReturn = {(WorldPanel.worldX + getRadius(offsetXIn, offsetYIn)*Math.cos(WorldPanel.radSpin + getThetaFromCenter(offsetXIn, offsetYIn))), ((WorldPanel.worldY - WorldPanel.shrink(WorldPanel.rotation)*getRadius(offsetXIn, offsetYIn)*Math.sin(WorldPanel.radSpin + getThetaFromCenter(offsetXIn, offsetYIn)))-boundTile.getScaledDistortedHeight())};
        return giveReturn;
    }
    
    public double getX()
    {
        return (WorldPanel.worldX + getRadius()*Math.cos(WorldPanel.radSpin + getThetaFromCenter()));
    }
    
    public double getY()
    {
        return ((WorldPanel.worldY - WorldPanel.shrink(WorldPanel.rotation)*getRadius()*Math.sin(WorldPanel.radSpin + getThetaFromCenter()))-boundTile.getScaledDistortedHeight());
    }
    
    public abstract void draw(Graphics g);
    
    @Override
    public void run()
    {
        threadedX = getX();
        threadedY = getY();
        threadedThetaFromCenter = getThetaFromCenter();
        threadedRadius = getRadius();//neccessary?
    }
    
}
