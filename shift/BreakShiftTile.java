package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class BreakShiftTile extends Tile
{
    public static TexturePaint crackTexture;
    public static BufferedImage[] crackImages;
    public static final Color redAlpha = new Color(255, 0, 0, 100);
    public static final Color purpleAlpha = new Color(128,0,128,100);
    Ladder ld;
    private int numBreak = 2;
    public BreakShiftTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
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
        double[] vertex = {0.5, 0.5};
        DirtPath dp = new DirtPath(this,vertex,1,1);
        try{
            BufferedImage[] temp = {ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_0.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_1.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_2.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_3.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_4.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_5.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_6.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_7.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_8.png")),
            ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/destroy_stage_9.png"))};
            crackImages = temp;
            crackTexture = new TexturePaint(crackImages[1], new Rectangle((int)WorldPanel.worldX, (int)WorldPanel.worldY, (int)(WorldPanel.scale * 64), (int)(WorldPanel.scale * 64 * WorldPanel.getShrink)));
        }catch(Exception e)
        {
            
        }
        
    }
    @Override
    public void drawReflections(Graphics g)
    {
        drawWaterReflectionCover(g);
        if(!getClicked())
        {
            drawWaterReflectionsWithColor(g, purpleAlpha);
        }else{
            drawWaterReflectionsWithColor(g, redAlpha);
        }
    }
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        drawHitPolygon(g);
        //drawWaterReflectionCover(g);
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
        }else
        {
            shadeSides(g);
            g.setColor(purpleAlpha);
            g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
            fillPolygons(g);
        }
        if(getTimesWalkedOn() > 0)
        {
            crackTexture = new TexturePaint(crackImages[(int)((((double)getTimesWalkedOn()/(double)numBreak))*(crackImages.length - 1))], new Rectangle((int)WorldPanel.worldX, (int)WorldPanel.worldY, (int)(WorldPanel.scale * 64), (int)(WorldPanel.scale * 64 * WorldPanel.getShrink)));
            g2.setPaint(crackTexture);
            g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
        }
        if(getTimesWalkedOn() >= numBreak && Player.boundTile != this)
        {
            removeSelfFromList();
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
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        //setHeight(getHeight()+1);
        //ld.draw(g);
        /*if(getHitPolygon() != null)
        {
            g.setColor(Color.BLUE);
            g.fillPolygon(getHitPolygon());
        }*/
        //g.drawString(Integer.toString(getBottomCornerOrderPos()), (int)convertToPoint(getBottomCornerCoordinates()[0], getBottomCornerCoordinates()[1])[0], (int)convertToPoint(getBottomCornerCoordinates()[0], getBottomCornerCoordinates()[1])[1]);
        
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
    private void removeSelfFromList()
    {
        TileDrawer.tileList.remove(this);
        
        for(int j = 0; j < getPathList().size(); j++)
        {
            MergedPaths.pathList.remove(getPathList().get(j));
        }
        
    }
    
}

/*
public class BreakShiftTile extends ShiftTile
{
    public static final Color purpleAlpha = new Color(128,0,128,150);
    
    private int numBreak = 2;
    public BreakShiftTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength, inHeight);
        double[] vertex = {0.5,0.5};
        DirtPath dp = new DirtPath(this,vertex,1,1);
    }
    public void draw(Graphics g)
    {
        super.draw(g);
        if(!getClicked())
        {
            g.setColor(purpleAlpha);
            g.fillPolygon(threadedUpperPoints()[0], threadedUpperPoints()[1], 4);
            fillPolygons(g);
        }
        if(getTimesWalkedOn() >= numBreak && Player.boundTile != this)
        {
            removeSelfFromList();
        }
    }
    
    private void removeSelfFromList()
    {
        TileDrawer.tileList.remove(this);
        
        for(int j = 0; j < getPathList().size(); j++)
        {
            MergedPaths.pathList.remove(getPathList().get(j));
        }
        
    }
    
}*/
