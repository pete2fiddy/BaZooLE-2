package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Waterfall extends Scenery implements Runnable
{
    private final Color lightBlue = new Color(30, 144, 255), darkBlue = new Color(24, 116, 205);
    private int baseWidth = (int)(WorldPanel.baseStraightUnit*.9), baseHeight = getBoundTile().getHeight(), baseStripeHeight = 25;
    private int width = baseWidth, height = baseHeight, stripeHeight = baseStripeHeight;
    private final double baseAnimationSpeed = .45;
    private double waterAnimationY;
    WaterDroplet wd;
    
    private int[][] threadedWaterfallPoints;
    
    public Waterfall(Tile tileIn, double offsetXIn, double offsetYIn, double unitsWidthIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        waterAnimationY = 0;
        tileIn.addWaterfall(this);
        threadedWaterfallPoints = waterfallPoints().clone();
        baseWidth = (int)(WorldPanel.baseStraightUnit * unitsWidthIn);
        width = baseWidth;
        tileIn.getAssortedScenery().remove(this);
        //spawnWaterDrops();
    }
    
    private void spawnWaterDrops()
    {
        Random r = new Random();
        for(int i = 0; i < 5; i++)
        {
            int positive = r.nextInt(2);
            int direction = 0;
            if(positive == 0)
            {
                direction = -1;
            }else{
                direction = 1;
            }
            WaterDroplet wDroplet = new WaterDroplet(this, (r.nextInt(getBasedx()+1)-(getBasedx()/2.0)), r.nextInt(getBasedy()+1)-(getBasedy()/2.0), r.nextInt(5) + 5, r.nextInt(30) + 30, direction, lightBlue);
            TileDrawer2.addWaterDroplet(wDroplet);
        }
    }
    
    public int getBaseY(){return(int)(getY() + WorldPanel.distortedHeight(WorldPanel.rotation, height));}//returns the y position of the point in the middle of the waterfall at its base.
    private double getBaseSlope()//isn't used now, but if I want to spawn droplets more realistically, could be. Finds the slope of the line that makes up the base of the waterfall.
    {
        if(!drawLast())
        {
            return (waterfallPoints()[1][2]-waterfallPoints()[1][3])/(waterfallPoints()[0][2]-waterfallPoints()[0][3]);
        }else{
            return (waterfallPoints()[1][3]-waterfallPoints()[1][2])/(waterfallPoints()[0][3]-waterfallPoints()[0][2]);
        }
    }
    
    public int getBasedx(){return Math.abs(waterfallPoints()[0][2]-waterfallPoints()[0][3]);}//finds the change in x from start of base to end of base
    public int getBasedy(){return Math.abs(waterfallPoints()[1][2]-waterfallPoints()[1][3]);}//finds the change in y from start of base to end of base
    
    public double waterfallTheta()//finds the angle the waterfall is pointing at so that its polygon can be drawn correctly.
    {
        if(getOffsetX() == 0 || getOffsetX() == 1)
        {
            return WorldPanel.radSpin - Math.PI/2.0;
        }else
        {
            return WorldPanel.radSpin;
        }
    }
    
    public int[] quadrantsItFaces()//gives the two quadrants this waterfall faces. If it is facing those quadrants, it is drawn on top of the tile, otherwise not.
    {
        if(getOffsetX() == 1)
        {
            int[] giveReturn = {3, 4};
            return giveReturn;
        }else if(getOffsetX() == 0)
        {
            int[] giveReturn = {1, 2};
            return giveReturn;
        }else if(getOffsetY() == 1)
        {
            int[] giveReturn = {2, 3};
            return giveReturn;
        }else{
            int[] giveReturn = {1, 4};
            return giveReturn;
        }
    }
    
    public boolean drawLast()//tells whether or not the waterfall should be drawn in front of or behind its tile depending on the spin.
    {
        if(WorldPanel.getSpinQuadrant() == quadrantsItFaces()[0] || WorldPanel.getSpinQuadrant() == quadrantsItFaces()[1])
        {
            return true;
        }
        return false;
    }
    
    public int[][] waterfallPoints()
    {
        int[][] points = {{(int)(getX() - ((width/2.0)*Math.cos(waterfallTheta()))), (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() - ((width/2.0)*Math.cos(waterfallTheta())))},{(int)(getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, height), (int)(getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, height)}};
        return points;
    }
    
    public int[][] animatedPoints(int heightIn)//height from top of waterfall
    {
        int[][] points = new int[2][4];
        if(heightIn > 0)
        {
            int[] xPoints = { (int)(getX() - ((width/2.0)*Math.cos(waterfallTheta()))), (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() - ((width/2.0)*Math.cos(waterfallTheta())))};
            int[] yPoints = {(int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn) + getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn) + getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn))+(int)(getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, stripeHeight), (int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn))+(int)(getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, stripeHeight)};
            points[0] = xPoints;
            points[1] = yPoints;
            return points;
        }else{
            
            int[] xPoints = { (int)(getX() - ((width/2.0)*Math.cos(waterfallTheta()))), (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() + ((width/2.0)*Math.cos(waterfallTheta()))) , (int)(getX() - ((width/2.0)*Math.cos(waterfallTheta())))};
            int[] yPoints = {(int)(getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))), (int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn))+(int)(getY() - (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, stripeHeight), (int)(WorldPanel.distortedHeight(WorldPanel.rotation, heightIn))+(int)(getY() + (WorldPanel.getShrink*(width/2.0)*Math.sin(waterfallTheta()))) + (int)WorldPanel.distortedHeight(WorldPanel.rotation, stripeHeight)};
            points[0] = xPoints;
            points[1] = yPoints;
            return points;
        }
    }
    @Override
    public void draw(Graphics g) 
    {
        run();
        g.setColor(lightBlue);
        g.fillPolygon(waterfallPoints()[0], waterfallPoints()[1], 4);
        g.fillPolygon(threadedWaterfallPoints[0], threadedWaterfallPoints[1],4);
        g.setColor(darkBlue);
        for(int i = 0; i < height - stripeHeight; i += 2*stripeHeight)
        {
            g.fillPolygon(animatedPoints(i)[0], animatedPoints((int)(i + waterAnimationY))[1], 4);
        }
        
    }
    public void tick()
    {
        width = (int)(baseWidth * WorldPanel.scale);
        height = (int)(baseHeight * WorldPanel.scale);
        stripeHeight = (int)(baseStripeHeight * WorldPanel.scale);
        waterAnimationY += WorldPanel.scale*baseAnimationSpeed;
        if(waterAnimationY > stripeHeight)
        {
            waterAnimationY = -stripeHeight;
        }
    }
    
    @Override
    public void run()
    {
        //super.run();
        tick();
        threadedWaterfallPoints = waterfallPoints().clone();
    }
    
}
