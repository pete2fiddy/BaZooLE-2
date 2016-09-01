package shift;

import java.util.ArrayList;

public class TileSorter2 
{
    public static ArrayList<Tile> sortByDistance(ArrayList<Tile> tilesIn)
    {
        ArrayList<Tile> giveReturn = new ArrayList<Tile>();
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        for(int i = 0; i < tilesIn.size(); i++)
        {
            tiles.add(tilesIn.get(i));
        }
        for(int orderPos = -(WorldPanel.worldTilesHeight) + 1; orderPos < (WorldPanel.worldTilesHeight) + 1; orderPos++)
        {
            ArrayList<Tile> posList = new ArrayList<Tile>();
            int tileCount = 0;
            while(tileCount < tiles.size())
            {
                if(tiles.get(tileCount).getSideCornerOrderPos() == orderPos)//switched to side corner order to fix order bugs. May work?
                {
                    posList.add(tiles.get(tileCount));
                    tiles.remove(tileCount);
                }else{
                    tileCount++;
                }
            }
            /*if(posList.size() > 1)
            {
                for(int indexNum = 0; indexNum < posList.size(); indexNum++)
                {
                    int biggestAreaIndex = indexNum;
                    for(int i = indexNum; i < posList.size(); i++)
                    {
                        if(posList.get(i).getArea() > posList.get(biggestAreaIndex).getArea())
                        {
                            biggestAreaIndex = i;
                        }
                    }
                    Tile temp = posList.get(indexNum);
                    posList.set(indexNum, posList.get(biggestAreaIndex));
                    posList.set(biggestAreaIndex, temp);
                }
                
            }*/
            //System.out.print(orderPos + ": ");
            for(Tile t : posList)
            {
                //System.out.print(t.getArea() + " ");
                giveReturn.add(t);
            }
            //System.out.println();
        }
        Tile.resortTiles = false;
        return giveReturn;
    }
}
