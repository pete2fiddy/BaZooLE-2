package shift;

import java.awt.Color;
import java.awt.Graphics;

public class ShiftTile extends Tile
{
    public static final Color redAlpha = new Color(255, 0, 0, 100);
    private RectPrism tilePrism;
    public ShiftTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength,inHeight);
        tilePrism = new RectPrism(inX - (inWidth/2), inY - (inLength/2), 0, inWidth, inLength, inHeight);
        //Snowman s = new Snowman(this,0.5,0.5);
        //TileSorter.addTile(this);//should I be adding from the Tile class?
    }
    
    @Override
    public void drawReflections(Graphics g)
    {
        //drawWaterReflectionCover(g);
        if(!getClicked())
        {
            drawWaterReflections(g);
        }else{
            drawWaterReflectionsWithColor(g, redAlpha);
        }
    }
    @Override
    public void draw(Graphics g)
    {
        
        if(isVisible(g))
        {
            //Graphics2D g2 = (Graphics2D)g;
            drawHitPolygon(g);
            for(Waterfall wf : getWaterfalls())
            {
                if(!wf.drawLast())
                {
                    wf.draw(g);
                }
            }
            g.setColor(ColorPalette.grassColor);
            fillPolygons(g);
            //g.setColor(ColorPalette.grassColor);
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, ColorPalette.grassColor, ColorPalette.nightShadeAlpha));
            g.fillPolygon(threadedUpperPoints()[0],threadedUpperPoints()[1], 4);
            if(getClicked())
            {
                g.setColor(redAlpha);
                g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
                fillPolygons(g);
                g.setColor(ColorPalette.grassColor);
            }

            drawEarlyScenery(g);

            for(Path path : getPathList())
            {
                path.draw(g);
            }
            for(Waterfall wf : getWaterfalls())
            {
                if(wf.drawLast())
                {
                    wf.draw(g);
                }
            }
            drawAssortedScenery(g);
        }
        
        //tilePrism.fill(g);
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
        drawShadedSides(g, ColorPalette.grassColor);
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        //shadeSides(g);
    }
    
    @Override
    public void run()
    {
        super.run();
        //tilePrism.updateShapePolygons();
    }
    
}
