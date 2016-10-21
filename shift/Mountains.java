/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Area;

/**
 *
 * @author phusisian
 */
public class Mountains 
{
    private static Mountain[] mountainList = new Mountain[7];
    public Mountains()
    {
        fillMountainList();
    }
    
    public static void fillMountainList()
    {
        //WorldPanel.minScale = (double)WorldPanel.screenWidth/(double)(WorldPanel.unit*WorldPanel.worldTilesWidth);
        //WorldPanel.scale = (double)WorldPanel.screenWidth/(double)(WorldPanel.straightUnit*Math.sqrt(2)*WorldPanel.worldTilesWidth);
        int yVal = 995;
        int centerScreen = (int)((double)WorldPanel.screenWidth/(2.0));
        System.out.println(WorldPanel.minScale);
        mountainList[0] = new Mountain(centerScreen+(int)((double)((-57)-centerScreen)/WorldPanel.minScale),995, 1, 4, WorldPanel.minScale);
        mountainList[1] = new Mountain(centerScreen+(int)((double)(145-centerScreen)/WorldPanel.minScale),995, 2, 3,WorldPanel.minScale);
        mountainList[6] = new Mountain(centerScreen+(int)((double)(298-centerScreen)/WorldPanel.minScale),995, 3,0,WorldPanel.minScale);
        mountainList[5] = new Mountain(centerScreen+(int)((double)(468-centerScreen)/WorldPanel.minScale),995, 4,1,WorldPanel.minScale);
        mountainList[4] = new Mountain(centerScreen+(int)((double)(635-centerScreen)/WorldPanel.minScale),995, 5,2,WorldPanel.minScale);
        mountainList[2] = new Mountain(centerScreen+(int)((double)(845-centerScreen)/WorldPanel.minScale),995, 6, 4,WorldPanel.minScale);
        mountainList[3] = new Mountain(centerScreen+(int)((double)(1091-centerScreen)/WorldPanel.minScale),995, 7, 3,WorldPanel.minScale);
    }
    
    public void draw(Graphics g)
    {
        Area a = new Area();
        for(Mountain m : mountainList)
        {
            if(m!= null)
            {
                
                m.draw(g, a);
                a.add(new Area(m.getMountainPolygon()));
                //g.setColor(Color.RED);
                //g.fillOval((int)(WorldPanel.worldX+(WorldPanel.scale*(m.getX()-WorldPanel.worldX)))-5, (int)WorldPanel.worldY-200-5, 10,10);
            }
        }
    }
    
}
