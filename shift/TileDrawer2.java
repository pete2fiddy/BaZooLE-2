/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.util.ArrayList;

/**
 *
 * @author phusisian
 */
public class TileDrawer2 implements Runnable
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
    public TileDrawer2(WorldPanel wp)
    {
        ll= new LevelLoader();   
        //ll.spawnLevel(UI.level);
        thread = new Thread(this);
        thread.start();
        fillWaterRipples();
        worldPanel= wp;
        populateCloudList();
    }
    
    public static void populateCloudList()
    {
        for(int i = 0; i < 7; i++)
        {
            double randX = WorldPanel.worldTilesWidth*Math.random()-(WorldPanel.worldTilesWidth/2);
            double randY = (WorldPanel.worldTilesHeight*Math.random())-(WorldPanel.worldTilesHeight/2);
            System.out.println("randY: " +randY);
            double randWidth = 1+1.5*Math.random();
            double randLength = 1+1.5*Math.random();
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
        //mp.getThread().interrupt();
        //mp.setThread(new Thread(mp));
        //mp.getThread().start();
        Graphics2D g2 = (Graphics2D)g;
        
        
        for(int i = 0; i < tileList.size(); i++)//removing causes negligible change in FPS
        {
            if(tileList.get(i).getClass() != BlockTile.class)
            {
                tileList.get(i).drawReflections(g);
            }else{
                BlockTile bt = (BlockTile)tileList.get(i);
                if(!bt.getIsEdgeBlock())
                {
                    tileList.get(i).drawReflections(g);
                }
            }
        }
        worldPanel.drawTransparentGridLines(g);//removing has negligible change in fps
        for(WaterRipple wr : waterRipples)
        {
            wr.draw(g);
        }
        
        /*mbt.getThread().interrupt();//doesn't do anything?
        mbt.setThread(new Thread(mbt));
        mbt.getThread().start();*/
        
        mbt.draw(g);
        for(int i = 0; i < tileList.size(); i++)
        {
            if(tileList.get(i).getClass() != BlockTile.class)
            {
                tileList.get(i).draw(g);
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
}
