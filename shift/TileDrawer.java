package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Random;

public class TileDrawer
{
    public static ArrayList<Tile> tileList = new ArrayList<Tile>();//needed?
    public static ArrayList<WaterDroplet> waterDroplets = new ArrayList<WaterDroplet>();
    MergedBlockTiles mbt = new MergedBlockTiles();
    MergedPaths mp = new MergedPaths();
    
    public TileDrawer()
    {
        //populateBoardFixedProportions(2, 2, 5);
        /*ShiftTile st = new ShiftTile(2,2,2,2,5);
        ShiftTile nt = new ShiftTile(0, -2,2,2,5);
        ShiftTile ot = new ShiftTile(0, 0, 2, 2, 5);
        ShiftTile lt = new ShiftTile(-2, -2, 2, 2, 5);
        ShiftTile pt = new ShiftTile(2, 0, 2, 2, 100);
        //pt.getPathList().remove(0);
        
        Waterfall wf = new Waterfall(pt, 0, 0.5);
        LevelEndTile let = new LevelEndTile(-2, 0, 2, 2, 5);
        //lt.removePath(0);
        
        //ot.removePath(0);
        double[] vertex = {.5,.5};
        Path path2 = new DirtPath(pt, vertex, 1, 1);
        ot.getPathList().add(new DirtPath(ot, vertex, 0, 1));
        lt.getPathList().add(new DirtPath(lt, vertex, 0, 1));
        BlockTile bt = new BlockTile((int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(WorldPanel.squareWidth/WorldPanel.straightUnit), 2, 5, true);
        
        BlockTile bt2 = new BlockTile((int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0) - 2, (int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), 2, (int)(WorldPanel.squareWidth/WorldPanel.straightUnit), 5, true);
        
        BlockTile bt3 = new BlockTile((int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0)-2, (int)(WorldPanel.squareWidth/WorldPanel.straightUnit), 2, 5, true);
        
        BlockTile bt4 = new BlockTile((int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), 2, (int)(WorldPanel.squareWidth/WorldPanel.straightUnit), 5, true);
        
        SpinTile spinT = new SpinTile(0,2, 2, 5);
        
        //BlockTile btNo1 = new BlockTile((int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), 2, 2, 5, false);
        //BlockTile btNo2 = new BlockTile((int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), (int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0)-2, 2, 2, 5, false);
        
        //BlockTile btNo3 = new BlockTile((int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0)-2, (int)(-WorldPanel.squareWidth/WorldPanel.straightUnit/2.0), 2, 2, 5, false);
        //BlockTile btNo4 = new BlockTile((int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0)-2, (int)(WorldPanel.squareWidth/WorldPanel.straightUnit/2.0)-2, 2, 2, 5, false);
        
        */
       
        LevelLoader ll = new LevelLoader();
        ll.spawnLevel(UI.level);
    }
    /*still don't know how to make it reset on click*/
    public void populateBoardFixedProportions(int width, int length, int height)
    {
        boolean[][] tilesFilled = new boolean[WorldPanel.worldTilesWidth+width][WorldPanel.worldTilesHeight+length];
        
        for(int i = 0; i < tilesFilled.length; i++)//needs to be filled with falses?
        {
            for(int j = 0; j < tilesFilled[0].length; j++)
            {
                tilesFilled[i][j]=false;
            }
        }
        
        Random r = new Random();
        int tilesSpawned = 0;
        
        while(tilesSpawned < 15)
        {
            int x = r.nextInt(WorldPanel.worldTilesWidth);
            int y = r.nextInt(WorldPanel.worldTilesHeight);
            while(checkTilesFilled(tilesFilled, x, y, width, length))//method isn't totally random, but I don't really care
            {
                if(x+width < tilesFilled.length && x >= 0)
                {
                    x++;
                }
                if(y+length < tilesFilled[0].length && y >= 0)
                {
                    y++;
                }
            }
            for(int i = x; i < x+width; i++)
            {
                for(int j = y; j < y+length; j++)
                {
                    tilesFilled[i][j]=true;
                }
            }
            switch(r.nextInt(2)+1)
            {
                case 1:
                    ShiftTile shiftTile = new ShiftTile(x-WorldPanel.mapRadiusUnits, y-WorldPanel.mapRadiusUnits, width, length, height);
                    break;
                case 2:
                    //ShiftTile otherShiftTile = new ShiftTile(x-WorldPanel.mapRadiusUnits, y-WorldPanel.mapRadiusUnits, width, length, height);
                    SpinTile spinTile = new SpinTile(x-WorldPanel.mapRadiusUnits, y-WorldPanel.mapRadiusUnits, width, height);
                    break;
            }
            tilesSpawned++;
        }
    }
    /*takes an array of booleans, and a position in it along with a width and a length and returns
    whether or not that shape with those dimensions and position will be overlapping another*/
    public boolean checkTilesFilled(boolean[][] tilesFilled, int x, int y, int width, int length)
    {
        for(int i = x; i < x+width; i++)
        {
            for(int j = y; j < y+length; j++)
            {
                if(tilesFilled[i][j])
                {
                    return true;
                }
            }
        }
        return false;
    }
    public static void setList(ArrayList<Tile> listIn)
    {
        tileList = listIn;
    }
    public void draw(Graphics g)//to do: Buffer draw order calculations
    {
        for(int i = 0; i < tileList.size(); i++)
        {
            tileList.get(i).draw(g);
        }
    }
    
    public void draw(Graphics g, ArrayList<Tile> listIn)//to do: Buffer draw order calculations
    {
        //tileList = TileSorter2.sortByDistance(tileList);
        mbt.getThread().interrupt();
        mbt.setThread(new Thread(mbt));
        mbt.getThread().start();
        mbt.draw(g);
        mp.getThread().interrupt();
        mp.setThread(new Thread(mp));
        mp.getThread().start();
        
        //g2.draw(area2);
        for(int i = 0; i < listIn.size(); i++)
        {
            //listIn.get(i).setPositionInArray(i);
            listIn.get(i).draw(g);
        }
        for(WaterDroplet wd : waterDroplets)
        {
            wd.draw(g);
        }
        mp.draw(g);
        mbt.drawFrontArea(g);
        /*ArrayList<Tile> list = TileSorter2.sortByDistance(tileList);
        for(Tile t : list)
        {
            System.out.println(t.getBottomCornerOrderPos());
        }*/
    }
}
