package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/*Is a tile that denies movement of other tiles into this slot -- e.g. what is surrounding the edges of maps.*/
public class BlockTile extends Tile
{
    private boolean isEdgeBlock;
    /*
    params: coordinate position of X, coordinate position of Y, 
    coordinates width, coordinates length, PIXELS height,
    boolean that dictates whether or not the tile should have a black border around it (may be useless with how drawing them is handled now
    */
    public BlockTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength, inHeight);
        setMoveable(false);
        MergedBlockTiles.blockTiles.add(this);
        isEdgeBlock = false;
        getAssortedScenery().clear();
    }
    
    public BlockTile(int inX, int inY, int inWidth, int inLength, int inHeight, boolean isEdgeBlockIn) 
    {
        super(inX, inY, inWidth, inLength, inHeight);
        setMoveable(false);
        MergedBlockTiles.blockTiles.add(this);
        isEdgeBlock = isEdgeBlockIn;
        getAssortedScenery().clear();
    }
    
    @Override
    public void drawReflections(Graphics g)
    {
        drawWaterReflectionCover(g);
        drawWaterReflections(g);
    }
    
    public boolean getIsEdgeBlock(){return isEdgeBlock;}
    
    /*
    overwrites superclass's abstract method to draw.
    */
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setPaint(WorldPanel.grassTexture);
        g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        g.setColor(Color.BLACK);
        
        g.drawPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.drawPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
    }   
}
    

