package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

/*
creates a single merged polygon from all of the blocktiles so that it makes a single shape(no lines in between tiles). 
*/
public class MergedBlockTiles extends Toolbox implements Runnable
{
    public static ArrayList<BlockTile> blockTiles = new ArrayList<BlockTile>();//holds all the blocktiles in the game. Is added to when a blocktile object is made.
    public static Area threadedArea;//Area represented by all the blocktiles. Can be useful for seeing where the player clicked and if they clicked any part of this merged area, tile movement can be denied.
    private Thread thread;//thread that fires run()
    
    public MergedBlockTiles()
    {
        thread = new Thread(this);
        thread.start();
    }
    
    public void setThread(Thread t)
    {
        thread = t;
    }
    
    public Thread getThread()
    {
        return thread;
    }
    
    /*
    draws the block tile area as a single shape, and fires blocktile threads.
    */
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        Polygon p = new Polygon();
        Area area = new Area(p);
        
        /*
        ***consider switching to threaded area?***
        */
        /*for(int i = 0; i < blockTiles.size(); i++)
        {
            blockTiles.get(i).getThread().interrupt();
            blockTiles.get(i).setThread(new Thread(blockTiles.get(i)));
            blockTiles.get(i).getThread().start();
            //blockTiles.get(i).draw(g);
            area.add(new Area(blockTiles.get(i).getUpperPolygon()));
        }*/
        drawBackArea(g);
        threadedArea = area;
    }
    
    public void drawFrontArea(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(WorldPanel.grassTexture);
        g2.fill(getFrontArea());
        for(Polygon p : getFrontPolygons())
        {
            g.fillPolygon(p);
        }
        for(Tile t : getFrontTiles())
        {
            t.shadeSides(g);
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
        
    }
    
    public void drawBackArea(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.setPaint(WorldPanel.grassTexture);
        
        //g.setColor(Color.BLACK);
        for(int i = 0; i < blockTiles.size(); i++)
        {
            //blockTiles.get(i).draw(g);
            g.fillPolygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4);
            g.fillPolygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4);
            //area.add(new Area(blockTiles.get(i).getUpperPolygon()));
        }
        //g.setColor(Color.GREEN);
        g.setColor(Color.BLACK);
        /*for(Polygon p:getBackPolygons())
        {
            g.drawPolygon(p);
        }*/
        for(Tile t : getBackTiles())
        {
            
            t.reverseShadeSides(g);
            //t.drawWaterReflectionCover(g);
            t.drawWaterReflections(g);
        }
        g2.setPaint(WorldPanel.grassTexture);
        g2.fill(getBackArea());
        g.setColor(Color.BLACK);
        for(Tile t : getBackTiles())
        {
            t.drawAssortedScenery(g);
        }
        //g2.draw(getBackArea());
        
    }
    
    /*
    returns the Area of the combined blocktiles. Is (currently) used to see if you have clicked a block tile.
    */
    private Area getArea()
    {
        Area area = new Area();
        
        for(int i = 0; i < blockTiles.size(); i++)
        {
            area.add(new Area(blockTiles.get(i).getUpperPolygon()));
        }
        return area;
    }
    
    private Area getFrontArea()
    {
        Area area = new Area();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
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
    }
    
    private ArrayList<Polygon> getFrontPolygons()
    {
        ArrayList<Polygon> giveReturn = new ArrayList<Polygon>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
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
    }
    
    private ArrayList<Tile> getFrontTiles()
    {
        ArrayList<Tile> giveReturn = new ArrayList<Tile>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 2:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));

                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
            
            }
        }
        return giveReturn;    
    }
    
    private ArrayList<Polygon> getBackPolygons()
    {
        ArrayList<Polygon> giveReturn = new ArrayList<Polygon>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
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
    }
    
    private ArrayList<Tile> getBackTiles()
    {
        ArrayList<Tile> giveReturn = new ArrayList<Tile>();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                case 2:
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        giveReturn.add(blockTiles.get(i));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints1()[0], blockTiles.get(i).getPolyPoints1()[1], 4));
                        //giveReturn.add(new Polygon(blockTiles.get(i).getPolyPoints2()[0], blockTiles.get(i).getPolyPoints2()[1], 4));
                    }
                    break;
            
            }
        }
        return giveReturn;  
    }
    
    
    private Area getBackArea()
    {
        Area area = new Area();
        for(int i = 0; i < blockTiles.size(); i++)
        {
            switch(WorldPanel.spinQuadrant())
            {
                case 1:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength() && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));

                    }
                case 2:
                    if(blockTiles.get(i).getRawX() == (WorldPanel.worldTilesWidth/2)-blockTiles.get(i).getRawWidth() && blockTiles.get(i).getRawY() == (-WorldPanel.worldTilesHeight/2))
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawWidth() > blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
                case 3:
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2)
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
                case 4: 
                    if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == -WorldPanel.worldTilesHeight/2 && blockTiles.get(i).getRawLength() > blockTiles.get(i).getRawWidth())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }else if(blockTiles.get(i).getRawX() == -WorldPanel.worldTilesWidth/2 && blockTiles.get(i).getRawY() == (WorldPanel.worldTilesHeight/2)-blockTiles.get(i).getRawLength())
                    {
                        area.add(new Area(blockTiles.get(i).getUpperPolygon()));
                    }
                    break;
            
            }
        }
        return area;  
    }
    
    /*
    updates position/scaling/point translations...
    */
    @Override
    public void run()
    {
        threadedArea = getArea();
    }
}