package shift;

import java.awt.Color;
import java.awt.Graphics;

public class ElevatorShiftTile extends Tile
{
    public static final Color redAlpha = new Color(255, 0, 0, 100);
    public static final Color yellowAlpha = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), 100);
    Ladder ld;
    double heightRound;
    
    public ElevatorShiftTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength,inHeight);
        //TileSorter.addTile(this);//should I be adding from the Tile class?
        setHeightChangeable(true);
        double[] vertex = {0.5, 0.5};
        DirtPath dp = new DirtPath(this, vertex, 0, 1);
    }
    
    @Override
    public void drawReflections(Graphics g)
    {
        //drawWaterReflectionCover(g);
        if(!getClicked())
        {
            drawWaterReflectionsWithColor(g, yellowAlpha);
        }else{
            drawWaterReflectionsWithColor(g, redAlpha);
        }
    }
    
    @Override
    public void draw(Graphics g)
    {
        //Graphics2D g2 = (Graphics2D)g;
        drawHitPolygon(g);
        
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
        }
        
        for(Waterfall wf : getWaterfalls())
        {
            if(!wf.drawLast())
            {
                wf.draw(g);
            }
        }
        
        g.setColor(ColorPalette.grassColor);
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
        
        for(Path path : getPathList())
        {
            path.draw(g);
        }
        
        drawAssortedScenery(g);
        
        for(Waterfall wf : getWaterfalls())
        {
            if(wf.drawLast())
            {
                wf.draw(g);
            }
        }
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
