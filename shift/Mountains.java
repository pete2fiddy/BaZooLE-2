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
        Area screenArea = new Area(new Rectangle(0,0,WorldPanel.screenWidth, WorldPanel.screenHeight));
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
                m.draw(g, a, drawnArea, undrawnArea, mountainCount, mountainList, screenArea);
                drawnArea.add(new Area(m.getMountainPolygon()));
                undrawnArea.subtract(new Area(m.getMountainPolygon()));
                //a.add(new Area(m.getMountainPolygon()));
                //g.setColor(Color.RED);
                //g.fillOval((int)(WorldPanel.worldX+(WorldPanel.scale*(m.getX()-WorldPanel.worldX)))-5, (int)WorldPanel.worldY-200-5, 10,10);
                mountainCount++;
            }
        }
        //Graphics2D g2 = (Graphics2D)g;
        //g2.fill(a);
    }
    
    public void moveMountains()
    {
        for(Mountain m : mountainList)
        {
            m.moveMountain();
        }
    }
    
}
