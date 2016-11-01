package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;


public class TileDrawer2 implements Runnable, ActionListener
{
    public static boolean drawGrass = false;
    public static ArrayList<Tile> tileList = new ArrayList<Tile>();
    public static WaterDroplet[] waterDroplets = new WaterDroplet[0];
    private MergedBlockTiles mbt;
    private Thread thread;
    private WaterRipple[] waterRipples=new WaterRipple[8];
    private WorldPanel worldPanel;
    private static Cloud[] clouds = new Cloud[0];
    private Timer movementTimer;
    private Mountains mountains = new Mountains();
    public static double changeInSpin = 0;
    public static final int REFRESH_PER_SEC =250;
    
    /*
    Initialization:
    */
    public TileDrawer2(WorldPanel wp)
    {
        
        
        new LevelLoader().spawnLevel(UI.level);
        thread = new Thread(this);
        thread.start();
        fillWaterRipples();
        worldPanel= wp;
        movementTimer = new Timer((int)(1000.0/(double)REFRESH_PER_SEC), this);
        movementTimer.setActionCommand("move");
        movementTimer.setRepeats(true);
        movementTimer.start();
        mbt = new MergedBlockTiles();
    }
    
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    public void draw(Graphics g)
    {
        if(drawGrass)
        {
            Grass.setGrassPoints();//currently has no grass because of FPS impact, and looks (to me) cleaner without it. Will likely add functionality to enable it through graphics settings
        }
        
        mountains.draw(g);
        worldPanel.drawMapFloor(g);
        for(WaterRipple wr : waterRipples)
        {
            wr.draw(g);
        }
        worldPanel.drawTransparentGridLines(g);
        mbt.updateAreas();
        mbt.drawBackEdges(g);
        for(int i = 0; i < tileList.size(); i++)
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
        mbt.drawFrontEdges(g);
        
        for(Cloud c : clouds)
        {
            c.fill(g);
        }
    }
    
    /*
    ----------------------------------------------------------------------------
    Getters:
    */
    public Thread getThread(){return thread;}
    
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
    
    /*
    ----------------------------------------------------------------------------
    Setters/Adders/Clearers:
    */
    public void setThread(Thread t){thread = t;}
    
    /*
    fills the water ripple array
    */
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
    
    /*
    fills the cloud array
    */
    public static void populateClouds()
    {
        clearClouds();
        int numClouds = (int)(((WorldPanel.worldTilesWidth-1)*(WorldPanel.worldTilesHeight-1))/6);
        double sizeBound = ((WorldPanel.worldTilesWidth-1)*(WorldPanel.worldTilesHeight-1))/100.0;
        for(int i = 0; i < numClouds; i++)
        {
            double randX = WorldPanel.worldTilesWidth*Math.random()-(WorldPanel.worldTilesWidth/2);
            double randY = (WorldPanel.worldTilesHeight*Math.random())-(WorldPanel.worldTilesHeight/2);
            double randWidth = sizeBound+(sizeBound*1.5)*Math.random();
            double randLength = sizeBound+(sizeBound*1.5)*Math.random();
            int randHeight = (int)(5+10*Math.random());
            addCloud(new Cloud(WorldPanel.dayNight, randX, randY, 200, randWidth, randLength, randHeight));
        }
    }
    
    public static void addCloud(Cloud c)
    {
        Cloud[] cloudAdded = new Cloud[clouds.length+1];
        for(int i = 0; i < clouds.length; i++)
        {
            cloudAdded[i] = clouds[i];
        }
        cloudAdded[clouds.length] = c;
        clouds = cloudAdded.clone();
    }
    
    public static void addWaterDroplet(WaterDroplet wDroplet)
    {
        WaterDroplet[] dropletAdded = new WaterDroplet[waterDroplets.length+1];
        for(int i = 0; i < waterDroplets.length; i++)
        {
            dropletAdded[i] = waterDroplets[i];
        }
        dropletAdded[waterDroplets.length] = wDroplet;
        waterDroplets = dropletAdded;
    }
    
    public static void clearClouds(){clouds = new Cloud[0];}
    
    public static void clearWaterDroplets(){waterDroplets = new WaterDroplet[0];}
    
    /*
    ----------------------------------------------------------------------------
    TileDrawer2's threaded functionality:
    (Sorts the tiles whenever it is called so that they draw from back to front)
    */
    @Override
    public void run()
    {
        tileList = TileSorter2.sortByDistance(tileList);
        if(Tile.tileJustUnclicked)
        {
            for(int i = 0; i < TileDrawer2.tileList.size(); i++)
            {
                TileDrawer2.tileList.get(i).setClicked(false);
            }
            Tile.tileJustUnclicked = false;
        }
    }
    
    /*
    ----------------------------------------------------------------------------
    Timer for movement in the game: 
    Updates objects within the game's position based on a timer so their movement speed is independent from the FPS
    */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String action = e.getActionCommand();
        if(action.equals("move"))
        {
            
            changeInSpin += Input.dSpin;//*((double)60/(double)REFRESH_PER_SEC);
            worldPanel.tick();
            for(Cloud c : clouds)
            {
                c.updatePosition();
                c.updateSnowFlakes();
            }
            
            mountains.moveMountains();
            
            for(WaterRipple wr : waterRipples)
            {
                wr.update();
            }
            worldPanel.getPlayer().tick();
            for (int i = 0; i < tileList.size(); i++) 
            {
                tileList.get(i).tileMovement();
                TileDrawer2.tileList.get(i).getThread().interrupt();
                Thread t = new Thread(TileDrawer2.tileList.get(i));
                TileDrawer2.tileList.get(i).setThread(t);
                t.start();

            }
        }
    }
    
    /*DELETABLE:*/
    
    /*
    private Area getTotalRightReflectionArea()
    {
        Area a = new Area();
        for(int i = 0; i < tileList.size(); i++)
        {
            a.add(tileList.get(i).getRightReflectionArea());
        }
        return a;
    }
    */
    
    /*private Area getTotalLeftReflectionArea()
    {
        Area a = new Area();
        for(int i = 0; i < tileList.size(); i++)
        {
            a.add(tileList.get(i).getLeftReflectionArea());
        }
        return a;
    }*/
    
    /*private void drawReflectionOutlines(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g.setColor(new Color(0, 0, 0, 80));
        for(int i = 0; i < tileList.size(); i++)
        {
            g2.draw(tileList.get(i).getLeftReflectionArea());
        }
    }*/
}
