package shift;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.util.ArrayList;

/*
creates a single merged polygon from all of the blocktiles so that it makes a single shape(no lines in between tiles). 
To Do: convert unnecessary arraylists into arrays.
*/
public class MergedBlockTiles extends Toolbox
{
    private BlockTile[] frontTiles;
    private ArrayList<BlockTile> backTiles;
    public static ArrayList<BlockTile> blockTiles = new ArrayList<BlockTile>();//holds all the blocktiles in the game. Is added to when a blocktile object is made.
    public static Area threadedArea;//Area represented by all the blocktiles. Can be useful for seeing where the player clicked and if they clicked any part of this merged area, tile movement can be denied.
    private Area backArea;
    /*
    Initialization:
    ----------------------------------------------------------------------------
    */
    public MergedBlockTiles()
    {
        updateAreas();
    }
    
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    
    /*
    Since the edges of the map have to be drawn in parts (front and back), this
    is called before all the world tiles are drawn.
    */
    public void drawBackEdges(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        Area lowBackArea = getBackAreaFloor();
        lowBackArea.subtract(backArea);
        
        for(BlockTile bt : backTiles)
        {
            bt.drawWaterReflections(g);
        }
        for(BlockTile bt : backTiles)
        {
            bt.drawShadedSides(g, ColorPalette.shadedGrassColor);
        }
        
        g.setColor(ColorPalette.shadedGrassColor);
        g2.fill(backArea);
        for(BlockTile bt : backTiles)
        {
            bt.drawAssortedScenery(g);
        }
    }
    
    /*
    Since the edges of the map have to be drawn in parts (front and back), this
    is called after all the world tiles are drawn.
    */
    public void drawFrontEdges(Graphics g)
    {
        for(BlockTile bt : frontTiles)
        {
            bt.drawWaterReflections(g);
        }
        for(BlockTile bt : frontTiles)
        {
            bt.draw(g);
        }
        for(BlockTile bt : frontTiles)
        {
            bt.drawAssortedScenery(g);
        }
    }
    
    
    /*
    ----------------------------------------------------------------------------
    Getters:
    */
    
    /*
    returns an array of BlockTiles that make up the closer two edges of the map.
    */
    private BlockTile[] getFrontTiles()
    {
        BlockTile[] giveReturn = new BlockTile[2];
        int tilesAdded = 0;
        for(int i = 0; i < blockTiles.size(); i++)
        {
            if(tilesAdded < giveReturn.length)
            {
                switch(WorldPanel.getSpinQuadrant())
                {
                    case 1:
                        if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }
                        break;
                    case 2:
                        if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }
                        break;
                    case 3:
                        if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }
                        break;
                    case 4: 
                        if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                        {
                            giveReturn[tilesAdded] = blockTiles.get(i);
                            tilesAdded++;
                        }
                        break;
                }
            }
        }
        return giveReturn;    
    }
    
    /*
    Returns an ArrayList of BlockTiles that make up the farther of the two edges of the map.
    To do: fix so it doesn't use an ArrayList. Would go out of bounds with normal array and not completely sure why. 
    */
    private ArrayList<BlockTile> getBackTiles()
    {
        ArrayList<BlockTile> giveReturn = new ArrayList<BlockTile>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.getSpinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                    }
                case 2:
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        giveReturn.add(blockTiles.get(i));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        giveReturn.add(blockTiles.get(i));
                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                    }
                    break;
            }
        }
        return giveReturn; 
    }
    
    /*
    Returns an area object that merges the two top faces of the back tile polygons (this is overlaid on top of the drawn back tiles afterwards to cover where the two meet)
    */
    private Area getBackArea()
    {
        Area area = new Area();
        
        for(BlockTile bt : backTiles)
        {
            area.add(new Area(bt.getUpperPolygon()));
        }
        area.intersect(WorldPanel.clipArea);
        return area;  
    }
    
    /*
    Returns an area object that merges the two bottom faces of the back tiles (used for clipping the shadows and reflections of those back tiles)
    */
    private Area getBackAreaFloor()
    {
        Area area = new Area();
        for(BlockTile bt : backTiles)
        {
            area.add(new Area(bt.getLowerPolygon()));
        }
        area.intersect(WorldPanel.clipArea);
        return area;
    }
    
    /*
    ----------------------------------------------------------------------------
    Updaters:
    */
    public void updateAreas()
    {
        frontTiles = getFrontTiles();
        backTiles = getBackTiles();
        backArea = getBackArea();
    }
    
    
    /*
    DELETABLE:
    */
    /*
    updates position/scaling/point translations...
    */
    /*@Override
    public void run()
    {
        threadedArea = getArea();
    }*/
    
    
    /*public void drawBackArea(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        Color lerpedGrass = ColorPalette.getLerpColor(ColorPalette.shadeColor, ColorPalette.grassColor, ColorPalette.nightShadeAlpha);
        g.setColor(lerpedGrass);
        
        //g.setColor(Color.BLACK);
        for(int i = 0; i < blockTiles.size(); i++)
        {
            blockTiles.get(i).drawWaterReflections(g);
            //blockTiles.get(i).draw(g);
            blockTiles.get(i).draw(g);
            blockTiles.get(i).drawReverseShadedSides(g, ColorPalette.grassColor);
            //g.fillPolygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4);
            //g.fillPolygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4);
            //area.add(new Area(blockTiles.get(i).getUpperPolygon()));
        }
        //g.setColor(Color.GREEN);
        //g.setColor(Color.BLACK);
        for(Tile t : getBackTiles())
        {
            t.draw(g);
            t.drawReverseShadedSides(g, ColorPalette.grassColor);
            //t.drawWaterReflectionCover(g);
            
        }
        g.setColor(ColorPalette.getLerpColor(ColorPalette.shadeColor, ColorPalette.grassColor, ColorPalette.nightShadeAlpha));
        //g2.fill(getBackArea());
        g.setColor(Color.BLACK);
        for(Tile t : getBackTiles())
        {
            t.drawAssortedScenery(g);
        }
        //g2.draw(getBackArea());
        
    }*/
    
    /*
    draws the block tile area as a single shape, and fires blocktile threads.
    */
    /*
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        Polygon p = new Polygon();
        Area area = new Area(p);
        
        drawBackArea(g);
        threadedArea = area;
    }*/
    /*public void drawFrontArea(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        Color lerpedColor = ColorPalette.getLerpColor(ColorPalette.shadeColor, ColorPalette.grassColor, ColorPalette.nightShadeAlpha);
        g.setColor(lerpedColor);
        //g2.fill(getFrontArea());
        for(Polygon p : getFrontPolygons())
        {
            g.fillPolygon(p);
        }
        for(Tile t : getFrontTiles())
        {
            t.draw(g);
            t.drawShadedSides(g, lerpedColor);
            //t.drawWaterReflectionCover(g);
            //t.drawWaterReflections(g);
        }
        g.setColor(Color.BLACK);
        //g2.draw(getFrontArea());
        Area frontSideArea = new Area();
        for(Polygon p : getFrontPolygons())
        {
           
           frontSideArea.add(new Area(p));
           
        }
        //g2.draw(frontSideArea);
        //g2.draw(getFrontArea());
        //g2.draw(getArea());
        for(Tile t : getFrontTiles())
        {
            
            t.drawAssortedScenery(g);
        }
        
    }*/
    /*
    private ArrayList<Polygon> getFrontPolygons()
    {
        ArrayList<Polygon> giveReturn = new ArrayList<Polygon>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.getSpinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 2:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));

                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
            
            }
        }
        return giveReturn;    
    }*/
    /*
    private ArrayList<Polygon> getBackPolygons()
    {
        ArrayList<Polygon> giveReturn = new ArrayList<Polygon>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.getSpinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                case 2:
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
            
            }
        }
        return giveReturn;  
    }*/
    /*
    returns the Area of the combined blocktiles. Is (currently) used to see if you have clicked a block tile.
    */
    /*
    private Area getArea()
    {
        Area area = new Area();
        
        for(int i = 0; i < blockTiles.size(); i++)
        {
            area.add(new Area(blockTiles.get(i).getUpperPolygon()));
        }
        return area;
    }*/
    
    /*public void setThread(Thread t)
    {
        thread = t;
    }*/
    
    /*public Thread getThread()
    {
        return thread;
    }*/
    /*private Area getFrontArea()
    {
        Area area = new Area();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.getSpinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
                case 2:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));

                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
            
            }
        }
        return area;    
    }*/
}