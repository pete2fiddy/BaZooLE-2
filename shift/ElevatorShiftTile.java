package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class ElevatorShiftTile extends Tile
{
    public static final Color redAlpha = new Color(255, 0, 0, 100);
    public static final Color yellowAlpha = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 100);
    Ladder ld;
    double heightRound;
    public ElevatorShiftTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength,inHeight);
        TileSorter.addTile(this);//should I be adding from the Tile class?
        /*Tree tree = new Tree(this, .25, .25);
        Tree tree2 = new Tree(this, .75, .75);
        double[] start = {0, .5};
        double[] vertex = {.5,.5};
        double[] end = {.5, 1};
        DirtPath dp = new DirtPath(this, vertex, 0, 0);
        Waterfall wf = new Waterfall(this, 1, .5);
        ld = new Ladder(this, dp, .5, 1, .1);*/
        //Lake lake = new Lake(this, .5, .5, .8, .8);
        setHeightChangeable(true);
        double[] vertex = {0.5, 0.5};
        DirtPath dp = new DirtPath(this, vertex, 0, 1);
    }
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        drawWaterReflectionCover(g);
        //heightRound += MouseInput.dHeight;
        if(MouseInput.dHeight < 0 && getHeight() > 5)
        {
            heightRound += MouseInput.dHeight;
        }else if(MouseInput.dHeight > 0)
        {
            heightRound += MouseInput.dHeight;
        }
        if(getClicked())
        {
            if(heightRound >= 5 || heightRound <= -5 && getHeight() >= 5)
            {
                addHeight(heightRound);
                heightRound = 0;
                
            }
            
            //addHeight(MouseInput.dHeight);
            //MouseInput.scrollType = "Height";
        }
        
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        for(Waterfall wf : getWaterfalls())
        {
            if(!wf.drawLast())
            {
                wf.draw(g);
            }
        }
        //g.setColor(getColor());
        g2.setPaint(WorldPanel.grassTexture);
        fillPolygons(g);
        g.fillPolygon(threadedUpperPoints()[0],threadedUpperPoints()[1], 4);
        if(getClicked())
        {
            g.setColor(redAlpha);
            g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
            fillPolygons(g);
            shadeSides(g);
        }else{
            shadeSides(g);
            g.setColor(yellowAlpha);
            g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
            fillPolygons(g);
        }
        /*for(Lake lake : getLakes())
        {
            lake.draw(g);
        }*/
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawSidePolygons(g);//draws the sides of the tile.
        g.setColor(Color.BLACK);
        
        g.drawPolygon(threadedUpperPoints()[0],threadedUpperPoints()[1], 4);
        for(Path path : getPathList())
        {
            path.draw(g);
        }
        /*for(int i = 0; i < getTrees().size(); i++)
        {
            getTrees().get(i).draw(g);
        }*/
        
        drawAssortedScenery(g);
        for(Waterfall wf : getWaterfalls())
        {
            if(wf.drawLast())
            {
                wf.draw(g);
            }
        }
        if(!getClicked())
        {
            drawWaterReflectionsWithColor(g, yellowAlpha);
        }else{
            drawWaterReflectionsWithColor(g, redAlpha);
        }
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        //setHeight(getHeight()+1);
        //ld.draw(g);
        /*if(getHitPolygon() != null)
        {
            g.setColor(Color.BLUE);
            g.fillPolygon(getHitPolygon());
        }
        g.drawString(Integer.toString(getBottomCornerOrderPos()), (int)convertToPoint(getBottomCornerCoordinates()[0], getBottomCornerCoordinates()[1])[0], (int)convertToPoint(getBottomCornerCoordinates()[0], getBottomCornerCoordinates()[1])[1]);*/
    }
    
    private void drawSidePolygons(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.drawPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.drawPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        g.setColor(getColor());
    }
    
    public void fillPolygons(Graphics g)
    {
        g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        
    }
    
    
}
