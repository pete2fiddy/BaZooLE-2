package shift;

import java.util.ArrayList;

public class TileSorter implements Runnable
{
    private Thread thread;
    public static ArrayList<Tile> holdList, tileList;
    
    private int tempQuadrant = WorldPanel.spinQuadrant();
    public TileSorter()
    {
        holdList = new ArrayList<Tile>();
        tileList = new ArrayList<Tile>();
        //TileDrawer.tileList = holdList;
        thread = new Thread(this);
        thread.start();
    }
    public static void addTile(Tile t)
    {
        tileList.add(t);
    }
    public static void resetTileList(){tileList = new ArrayList<Tile>();}
    public void setThread(Thread t){thread = t;}
    public Thread getThread(){return thread;}
    public int getBackPoint()
    {
        if(WorldPanel.spinQuadrant() != 1)
        {
            return WorldPanel.spinQuadrant() - 2;
        }
        return 3;
    }
    public int getOrderPoint()//finds the point that determines the order that each tile is drawn so there is no strange overlap... Not sure if I should use back point, or front point.
    {
        if(WorldPanel.spinQuadrant() != 1)
        {
            return WorldPanel.spinQuadrant() - 2;
        }
        return 3;
    }
    private int getFrontPoint()
    {
        if(WorldPanel.spinQuadrant() == 4)
        {
            return 0;
        }
        return WorldPanel.spinQuadrant();
    }
   
    private int[] bottomMapCoord()
    {
        int radiusUnits = WorldPanel.mapRadiusUnits;
        if(tempQuadrant == 1)
        {
            int[] giveReturn = {-radiusUnits,-radiusUnits};
            return giveReturn;
        }else if(tempQuadrant == 2)
        {
            int[] giveReturn = {-radiusUnits,radiusUnits};
            return giveReturn;
        }else if(tempQuadrant == 3){
            int[] giveReturn = {radiusUnits,radiusUnits};
            return giveReturn;
        }else{
            int[] giveReturn = {radiusUnits,-radiusUnits};
            return giveReturn;
        }
    }
    private int getMapTopPoint()//gets the point of the map that is highest displayed
    {
        int quadrant = WorldPanel.spinQuadrant();
        if(quadrant == 4)
        {
            return 0;
        }else if(quadrant == 3)
        {
            return 1;
        }else if(quadrant == 2)
        {
            return 2;
        }
        return 3;
    }
    
    private int getComparePoint(){return WorldPanel.spinQuadrant() - 1;}
    
    private int getLeftPoint()
    {
        int quadrant = WorldPanel.spinQuadrant();
        if(quadrant == 1)
        {
            return 2;
        }else if(quadrant==2)
        {
            return 3;
        }else if(quadrant==3)
        {
            return 0;
        }
        return 1;
    }
    public void sortByDistance()
    {
        //if(tempQuadrant!=WorldPanel.spinQuadrant())//(tempQuadrant != WorldPanel.spinQuadrant())
        //{
            ArrayList<Tile>temp = new ArrayList<Tile>();
            tempQuadrant = WorldPanel.spinQuadrant();
            //int highest = 0;
            int i = 0;
            while(tileList.size()>0)
            {
                int highest = 0;
                int j = 0;
                while(j < tileList.size())
                {
                    if(addAmount(tileList.get(j)) > addAmount(tileList.get(highest)))
                    {
                        highest = j;
                    }
                    j++;
                }
                temp.add(tileList.get(highest));
                tileList.remove(highest);
            }
            sortAgain();
            tileList = temp;
        //}
    }
    private void sortAgain()
    {
        int backPoint = getBackPoint();
        int frontPoint = getFrontPoint();
        int rightPoint = getComparePoint();
        int leftPoint = getLeftPoint();
        boolean go = true;
        while(go)
        {
            int numOff=0;
            for(int i = 0; i < tileList.size()-1; i++)
            {
                if(addAmount(tileList.get(i+1), rightPoint) > addAmount(tileList.get(i), leftPoint) || addAmount(tileList.get(i+1), leftPoint) > addAmount(tileList.get(i), rightPoint))//if(addAmount(tileList.get(i+1),leftPoint) > addAmount(tileList.get(i),rightPoint)||addAmount(tileList.get(i+1),rightPoint) > addAmount(tileList.get(i),leftPoint)||addAmount(tileList.get(i+1),rightPoint) > addAmount(tileList.get(i))||addAmount(tileList.get(i+1),leftPoint) > addAmount(tileList.get(i)))
                {
                    Tile tempTile = tileList.get(i);
                    tileList.set(i, tileList.get(i+1));
                    tileList.set(i+1, tempTile);
                    numOff++;
                }
            }
            if(numOff == 0)
            {
                go=false;
            }
            
        }
    }
    private double getDist(Tile t)
    {
        int x1 = bottomMapCoord()[0];
        int y1 = bottomMapCoord()[1];
        int x2 = t.getPointCoordX(getFrontPoint());
        int y2 = t.getPointCoordY(getFrontPoint());
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }
    
    private int addAmount(Tile t)//ranks how close the tile is to the map's bottom point
    {
        int amount = bottomMapCoord()[0]+bottomMapCoord()[1];
        int x = t.getPointCoordX(getFrontPoint());
        int y = t.getPointCoordY(getFrontPoint());
        int diffX = Math.abs(x-bottomMapCoord()[0]);
        int diffY = Math.abs(y-bottomMapCoord()[1]);
        
        return (diffX+diffY);
    }
    private int addAmount(Tile t, int point)
    {
        int amount = bottomMapCoord()[0]+bottomMapCoord()[1];
        int x = t.getPointCoordX(point);
        int y = t.getPointCoordY(point);
        int diffX = Math.abs(x-bottomMapCoord()[0]);
        int diffY = Math.abs(y-bottomMapCoord()[1]);
        
        return (diffX+diffY);
    }
    public void run() 
    {
        
        sortByDistance();
        holdList = tileList;
        TileDrawer.tileList = TileSorter2.sortByDistance(TileDrawer.tileList);
        
        
        
    }
}
