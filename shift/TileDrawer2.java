/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.util.ArrayList;
import javax.swing.Timer;

/**
 *
 * @author phusisian
 */
public class TileDrawer2 implements Runnable, ActionListener
{
    public static ArrayList<Tile> tileList = new ArrayList<Tile>();
    public static ArrayList<WaterDroplet> waterDroplets = new ArrayList<WaterDroplet>();
    private LevelLoader ll;
    private MergedBlockTiles mbt = new MergedBlockTiles();
    private MergedPaths mp = new MergedPaths();
    private Thread thread;
    private WaterRipple[] waterRipples=new WaterRipple[8];
    private WorldPanel worldPanel;
    private static ArrayList<Cloud> cloudList = new ArrayList<Cloud>();
    private Timer movementTimer;
    private Mountains mountains = new Mountains();
    
    public TileDrawer2(WorldPanel wp)
    {
        ll= new LevelLoader();   
        ll.spawnLevel(UI.level);
        thread = new Thread(this);
        thread.start();
        fillWaterRipples();
        worldPanel= wp;
        //populateCloudList();
        movementTimer = new Timer(16, this);
        movementTimer.setActionCommand("move");
        movementTimer.setRepeats(true);
        movementTimer.start();
    }
    
    public static void populateCloudList()
    {
        cloudList.clear();
        System.out.println("world width: " + WorldPanel.worldTilesWidth);
        System.out.println("world height: " + WorldPanel.worldTilesHeight);
        int numClouds = (int)(((WorldPanel.worldTilesWidth-1)*(WorldPanel.worldTilesHeight-1))/6);
        double sizeBound = ((WorldPanel.worldTilesWidth-1)*(WorldPanel.worldTilesHeight-1))/100.0;
        for(int i = 0; i < numClouds; i++)
        {
            double randX = WorldPanel.worldTilesWidth*Math.random()-(WorldPanel.worldTilesWidth/2);
            double randY = (WorldPanel.worldTilesHeight*Math.random())-(WorldPanel.worldTilesHeight/2);
            //System.out.println("randY: " +randY);
            double randWidth = sizeBound+(sizeBound*1.5)*Math.random();
            double randLength = sizeBound+(sizeBound*1.5)*Math.random();
            int randHeight = (int)(5+10*Math.random());
            cloudList.add(new Cloud(WorldPanel.dayNight, randX, randY, 200, randWidth, randLength, randHeight));
        }
    }
    
    public static void clearCloudList()
    {
        cloudList.clear();
    }
    
    public Thread getThread()
    {
        return thread;
    }
    
    public void setThread(Thread t)
    {
        thread = t;
    }
    
    private void fillWaterRipples()
    {
        for(int i = 0; i < waterRipples.length; i++)
        {
            double scale = Math.random()*2;
            double rippleX = (int)((WorldPanel.worldTilesWidth-scale)*Math.random())-((WorldPanel.worldTilesWidth-scale)/2);
            double rippleY=(int)((WorldPanel.worldTilesHeight-scale)*Math.random())-((WorldPanel.worldTilesHeight-scale)/2);
            
            waterRipples[i]=new WaterRipple(rippleX, rippleY, Math.random()*2);
        }
    }
    public void draw(Graphics g)
    {
        Grass.setGrassPoints();
        Graphics2D g2 = (Graphics2D)g;
        
        
        mountains.draw(g);
        worldPanel.drawMapFloor(g);
        for(WaterRipple wr : waterRipples)
        {
            wr.draw(g);
        }
        //worldPanel.drawTransparentGridLines(g);//removing has negligible change in fps
        mbt.draw(g);
        for(int i = 0; i < tileList.size(); i++)//removing causes negligible change in FPS
        {
            
            if(tileList.get(i).getClass() != BlockTile.class)
            {
                if(tileList.get(i).isVisible(g))
                {
                    tileList.get(i).drawReflections(g);
                }
            }else{
                BlockTile bt = (BlockTile)tileList.get(i);
                if(!bt.getIsEdgeBlock() && bt.isVisible(g))
                {
                    tileList.get(i).drawReflections(g);
                }
            }
        }
        
        
        
        /*mbt.getThread().interrupt();//doesn't do anything?
        mbt.setThread(new Thread(mbt));
        mbt.getThread().start();*/
        
        //mbt.draw(g);
        
        for(int i = 0; i < tileList.size(); i++)
        {
            if(tileList.get(i).getClass() != BlockTile.class)
            {
                tileList.get(i).draw(g);
                //g.setColor(new Color(255, 0, 0, 70));
                //g.fillRect((int)tileList.get(i).getBoundingRect().getX(), (int)tileList.get(i).getBoundingRect().getY(), (int)tileList.get(i).getBoundingRect().getWidth(), (int)tileList.get(i).getBoundingRect().getHeight());
                //g.setColor(Color.BLACK);
                //g.drawString("Index: " + Integer.toString(tileList.get(i).getIndex()), (int)tileList.get(i).threadedUpperPoints()[0][0], (int)tileList.get(i).threadedUpperPoints()[1][0]);
            }else{
                BlockTile bt = (BlockTile)tileList.get(i);
                if(!bt.getIsEdgeBlock())
                {
                    tileList.get(i).draw(g);
                }
            }
        }
        
        for(WaterDroplet wd : waterDroplets)
        {
            wd.draw(g);
        }
        //mp.draw(g);
        //worldPanel.drawTransparentGridLines(g);
        mbt.drawFrontArea(g);
        
        for(Cloud c : cloudList)
        {
            c.fill(g);
        }
    }

    private Area getTotalLeftReflectionArea()
    {
        Area a = new Area();
        for(int i = 0; i < tileList.size(); i++)
        {
            a.add(tileList.get(i).getLeftReflectionArea());
        }
        return a;
    }
    
    private Area getTotalRightReflectionArea()
    {
        Area a = new Area();
        for(int i = 0; i < tileList.size(); i++)
        {
            a.add(tileList.get(i).getRightReflectionArea());
        }
        return a;
    }
    
    private void drawReflectionOutlines(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g.setColor(new Color(0, 0, 0, 80));
        for(int i = 0; i < tileList.size(); i++)
        {
            g2.draw(tileList.get(i).getLeftReflectionArea());
        }
    }
    
    @Override
    public void run()
    {
        
        tileList = TileSorter2.sortByDistance(tileList);
        TileDrawer.tileList = tileList;//to keep from breaking code that relies on TileDrawer.tileList. FIX LATER.
        if(Tile.tileJustUnclicked)
        {
            for(int i = 0; i < TileDrawer2.tileList.size(); i++)
            {
                TileDrawer2.tileList.get(i).setClicked(false);
            }
            Tile.tileJustUnclicked = false;
        }
    }
    
    public static boolean pointCovered(int index, int xPos, int yPos)
    {
        for(int i = index + 1; i < tileList.size(); i++)
        {
            Rectangle[] rects = tileList.get(i).getBoundingRects();
            for(Rectangle r : rects)
            {
                if(r.contains(xPos, yPos))
                {
                    return true;
                }
            }
            /*if(tileList.get(i).getBoundingRect().contains(xPos, yPos))
            {
                return true;
            }*/
        }
        return false;
    }
    
    public static int getGreatestTileHeight()
    {
        int highest = 0;
        for(int i = 0; i < tileList.size(); i++)
        {
            if(tileList.get(i).getHeight() > highest)
            {
                highest = tileList.get(i).getHeight();
            }
        }
        return highest;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        /*for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            
        }*/
        String action = e.getActionCommand();
        if(action.equals("move"))
        {
            for(Cloud c : cloudList)
            {
                c.updatePosition();
                c.updateSnowFlakes();
            }
            //System.out.println("ticking");
            worldPanel.tick();
            mountains.moveMountains();
            for (int i = 0; i < tileList.size(); i++) {
                tileList.get(i).tileMovement();
                TileDrawer2.tileList.get(i).getThread().interrupt();
                Thread t = new Thread(TileDrawer2.tileList.get(i));
                TileDrawer2.tileList.get(i).setThread(t);
                t.start();

            }
            worldPanel.getPlayer().tick();
        }
    }
}
