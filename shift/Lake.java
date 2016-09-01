package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/*
Lake is a scenery extension. Holds lakes in tiles.
*/

public class Lake extends Scenery implements Runnable
{
    private final Color dirtBrown = new Color(87, 59, 12), lakeBlue = new Color(30, 144, 255) ;//instance finals for lake color and dirt color 
    private final int basePondHeight = 5;//pixels depth that the pond is
    private int pondHeight = basePondHeight;//pond height will hold the SCALED version of the base pond height (multiplied
    private final int basePondWidth, basePondLength;//holds the base width and length of the pond -- i.e. the UNSCALED number of pixels for width and height. 
    private int pondWidth, pondLength;//holds the SCALED width and length of the pond.
    private int[][][] threadedVisiblePondPolygons, threadedFrontPondPolygons;//holds threaded polygon arrays so that they aren't recalculated every time they are called. Are calculated on firing a thread. ***Consider renaming to indicate the hold points, and are not polygons themselves.***
    private int[][] threadedTopPondPoints, threadedBottomPondPoints;//holds threaded polygon arrays so that they aren't recalculated every time they are called. ***Consider renaming to indicate the hold points, and are not polygons themselves.***
    
    /*
    params: Tile to which this lake is bound. Offset x from left corner as a percentage of the tile. Offset y from bottom corner as a percentage of the tile. 
    */
    public Lake(Tile tileIn, double offsetXIn, double offsetYIn, double offsetPondWidthIn, double offsetPondLengthIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        basePondWidth = (int)(offsetPondLengthIn*tileIn.getRawWidth()*WorldPanel.baseStraightUnit); basePondLength = (int)(offsetPondLengthIn * tileIn.getRawLength() * WorldPanel.baseStraightUnit);//constructs a base width and height IN PIXELS based on what was passed to the constructor. ***consider multiple constructors where you can give the base width and length in pixels instead of in offsets instead***
        pondWidth = basePondWidth; pondLength = basePondLength;//sets them equal to their base values to start with
        
        //sets threaded variables to a starting value
        threadedVisiblePondPolygons = getVisiblePondPolygons().clone();
        threadedFrontPondPolygons = getFrontPondPolygons().clone();
        threadedTopPondPoints = getTopPondPoints().clone();
        threadedBottomPondPoints = bottomPondPoints().clone();
        tileIn.addAssortedScenery(this);
        tileIn.addLake(this);//adds itself to the bound tile's lake arraylist
    }
    
    /*
    returns an array of the points that the top polygon of the lake is made of
    */
    public int[][] getTopPondPoints()
    {
        int[][] points = new int[2][4];
        for(int i = 0; i < points[0].length; i++)
        {
            double iPi = i*(Math.PI/2.0);
            points[0][i] = (int)(getThreadedX() + ((double)pondWidth/Math.sqrt(2))*Math.cos(WorldPanel.radSpin + iPi + (Math.PI/4.0)));
            points[1][i] = (int)(getThreadedY() - (WorldPanel.getShrink*(((double)pondLength/Math.sqrt(2))*Math.sin(WorldPanel.radSpin + iPi + (Math.PI/4.0)))));
        }
        return points;
    }
    
    /*
    returns an array of the points that the bottom polygon of the lake is made of
    */
    public int[][] bottomPondPoints()
    {
        int[][] points = getTopPondPoints().clone();
        int heightAdd = (int)distortedHeight(pondHeight);
        for(int i = 0; i < 4; i++)
        {
            points[1][i] += heightAdd;
        }
        return points;
    }
    
    /*
    returns the four polygons that makes up the side of the lake. ***Consider changing pond to lake... given the class name, and make it clearer this returns an int[][][] not polygons.
    */
    public int[][][] getPondPolygons()
    {
        int[][] top = getTopPondPoints().clone();
        int[][] bottom = bottomPondPoints().clone();
        int[][][] polyPoints = new int[4][2][4];
        for(int i = 0; i < 4; i++)
        {
            if(i != 3)
            {
                int[] xPoints = {top[0][i], top[0][i+1], bottom[0][i+1], bottom[0][i]};
                int[] yPoints = {top[1][i], top[1][i+1], bottom[1][i+1], bottom[1][i]};
                polyPoints[i][0] = xPoints.clone();
                polyPoints[i][1] = yPoints.clone();
            }else{
                int[] xPoints = {top[0][i], top[0][0], bottom[0][0], bottom[0][i]};
                int[] yPoints = {top[1][i], top[1][0], bottom[1][0], bottom[1][i]};
                polyPoints[i][0] = xPoints.clone();
                polyPoints[i][1] = yPoints.clone();
            }
        }
        return polyPoints;
    }
    
    /*
    handles movement/position/scaling of the lake. Is run through its thread.
    */
    public void tick()
    {
        pondWidth = (int)(basePondWidth*WorldPanel.scale);
        pondLength = (int)(basePondLength*WorldPanel.scale);
        pondHeight = (int)(basePondHeight*WorldPanel.scale);
    }
    
    /*
    returns only the VISIBLE pond polygons -- meaning the edges of the pond that can be seem from the current vantage point so sides that aren't meant to be seen aren't drawn.
    */
    public int[][][] getVisiblePondPolygons()
    {
        int[][][] points = new int[2][2][4];
        int[][][] pondPolygons = getPondPolygons();
        if(WorldPanel.spinQuadrant() == 1)
        {
            points[0]=pondPolygons[3];
            points[1]=pondPolygons[0];
        }else if(WorldPanel.spinQuadrant() == 2)
        {
            points[0]=pondPolygons[2];
            points[1]=pondPolygons[3];
        }else if(WorldPanel.spinQuadrant() == 3)
        {
            points[0]=pondPolygons[1];
            points[1]=pondPolygons[2];
        }else{
            points[0]=pondPolygons[0];
            points[1]=pondPolygons[1];
        }
        return points;
    }
    
    /*
    returns the NON-VISIBLE pond polygons. Must be drawn in the same color as the tile and the top polygon of the tile be redrawn on top so that the color doesn't clip through the tile.
    */
    public int[][][] getFrontPondPolygons()
    {
        int[][][] points = new int[2][2][4];
        int[][][] pondPolygons = getPondPolygons().clone();
        if(WorldPanel.spinQuadrant() == 1)
        {
            points[0]=pondPolygons[1];
            points[1]=pondPolygons[2];
        }else if(WorldPanel.spinQuadrant() == 2)
        {
            points[0]=pondPolygons[0];
            points[1]=pondPolygons[1];
        }else if(WorldPanel.spinQuadrant() == 3)
        {
            points[0]=pondPolygons[0];
            points[1]=pondPolygons[3];
        }else{
            points[0]=pondPolygons[2];
            points[1]=pondPolygons[3];
        }
        return points;
    }
    
    /*
    overrides Scenery's abstract draw method. Draws the lake.
    */
    public void draw(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;
        g.setColor(lakeBlue);
        g.fillPolygon(threadedBottomPondPoints[0], threadedBottomPondPoints[1], 4);
        g.setColor(dirtBrown);
        for(int i = 0; i < 2; i++)
        {
            g.fillPolygon(threadedVisiblePondPolygons[i][0], threadedVisiblePondPolygons[i][1], 4);
        }
        g.setColor(Color.BLACK);
        g.drawPolygon(threadedVisiblePondPolygons[1][0], threadedVisiblePondPolygons[1][1], 4);
        g2.setPaint(WorldPanel.grassTexture);
        //g.setColor(getBoundTile().getColor());
        for(int i = 0; i < 2; i++)
        {
            g.fillPolygon(threadedFrontPondPolygons[i][0],threadedFrontPondPolygons[i][1],4);
        }
        g.setColor(Color.BLACK);
        g.drawPolygon(threadedTopPondPoints[0], threadedTopPondPoints[1],4);
    }
    
    /*
    Lake's thread. Fires the superclass's thread so its calculations can run as well and rescales the tile, repositions, etc.
    */
    @Override
    public void run()
    {
        super.run();
        tick();
        threadedVisiblePondPolygons = getVisiblePondPolygons().clone();
        threadedFrontPondPolygons = getFrontPondPolygons().clone();
        threadedTopPondPoints = getTopPondPoints().clone();
        threadedBottomPondPoints = bottomPondPoints().clone();
    }
}
