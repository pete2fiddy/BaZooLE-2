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
    public TileDrawer2()
    {
        ll= new LevelLoader();   
        ll.spawnLevel(UI.level);
        thread = new Thread(this);
        thread.start();
    }
    
    public Thread getThread()
    {
        return thread;
    }
    
    public void setThread(Thread t)
    {
        thread = t;
    }
    
    public void draw(Graphics g)
    {
        //tileList = TileSorter2.sortByDistance(tileList);
        mbt.getThread().interrupt();
        mbt.setThread(new Thread(mbt));
        mbt.getThread().start();
        mbt.draw(g);
        mp.getThread().interrupt();
        mp.setThread(new Thread(mp));
        mp.getThread().start();
        Graphics2D g2 = (Graphics2D)g;
        
        /*Composite originalComposite = g2.getComposite();
        int type = AlphaComposite.SRC_OVER;
        AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, 0.75f);
        g2.setComposite(transparencyComposite);
        g2.setPaint(WorldPanel.grassTexture);
        g2.fill(getTotalLeftReflectionArea());
        
        AlphaComposite transparencyComposite2 = AlphaComposite.getInstance(type, 0.50f);
        g2.setComposite(transparencyComposite2);
        g2.fill(getTotalRightReflectionArea());
        
        g2.setComposite(originalComposite);*/
        //drawReflectionOutlines(g);
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
        mp.draw(g);
        mbt.drawFrontArea(g);
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
