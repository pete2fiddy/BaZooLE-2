package shift;

import java.awt.Graphics;
import java.awt.geom.Area;


/*
Holds and draws all the mountains in the background.
Issue: Mountains is a mostly static class, and if I wanted to have multiple sets of mountains
using the mountains class as a container, I couldn't. I can't forsee myself needing more than
one list of mountains, so I haven't taken efforts to fix this. 
*/
public class Mountains 
{
    /*
    Initialization:
    */
    public static Mountain[] mountainList = new Mountain[7];
    public Mountains()
    {
        fillMountainList();
    }
    
    public static void fillMountainList()
    {
        int centerScreen = (int)((double)WorldPanel.screenWidth/(2.0));
        mountainList[0] = new Mountain(centerScreen+(int)((double)((-57)-centerScreen)/WorldPanel.minScale), 1, 4, WorldPanel.minScale);
        mountainList[1] = new Mountain(centerScreen+(int)((double)(145-centerScreen)/WorldPanel.minScale), 2, 3,WorldPanel.minScale);
        mountainList[6] = new Mountain(centerScreen+(int)((double)(298-centerScreen)/WorldPanel.minScale), 3,0,WorldPanel.minScale);
        mountainList[5] = new Mountain(centerScreen+(int)((double)(468-centerScreen)/WorldPanel.minScale), 4,1,WorldPanel.minScale);
        mountainList[4] = new Mountain(centerScreen+(int)((double)(635-centerScreen)/WorldPanel.minScale), 5,2,WorldPanel.minScale);
        mountainList[2] = new Mountain(centerScreen+(int)((double)(845-centerScreen)/WorldPanel.minScale), 6, 4,WorldPanel.minScale);
        mountainList[3] = new Mountain(centerScreen+(int)((double)(1091-centerScreen)/WorldPanel.minScale), 7, 3,WorldPanel.minScale);
    }
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    
    /*
    Uses so man areas so drop shadows of mountains don't have to draw over themselves -- can be clipped by the other mountains that will be on top of it.
    */
    public void draw(Graphics g)
    {
        Area undrawnArea = new Area();
        Area a = new Area();
        Area drawnArea = new Area();
        for(Mountain m : mountainList)
        {
            a.add(new Area(m.getMountainPolygon()));
        }
        undrawnArea = (Area)a.clone();
        int mountainCount = 0;
        for(Mountain m : mountainList)
        {
            if(m!= null)
            {
                undrawnArea.subtract(new Area(m.getMountainPolygon()));
                m.draw(g, a, drawnArea, undrawnArea, mountainCount, mountainList, WorldPanel.clipArea);
                drawnArea.add(new Area(m.getMountainPolygon()));
                undrawnArea.subtract(new Area(m.getMountainPolygon()));
                mountainCount++;
            }
        }
    }
    /*
    ----------------------------------------------------------------------------
    Updates mountain position when called by a timer
    */
    public void moveMountains()
    {
        for(Mountain m : mountainList)
        {
            m.moveMountain();
        }
    }
}
