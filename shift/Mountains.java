/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Graphics;
import java.awt.geom.Area;

/**
 *
 * @author phusisian
 */
public class Mountains 
{
    private Mountain[] mountainList = new Mountain[7];
    public Mountains()
    {
        fillMountainList();
    }
    
    private void fillMountainList()
    {
        int yVal = 995;
        mountainList[0] = new Mountain(-57,995, 1, 4);
        mountainList[1] = new Mountain(145,995, 2, 3);
        mountainList[6] = new Mountain(298,995, 3,0);
        mountainList[5] = new Mountain(468,995, 4,1);
        mountainList[4] = new Mountain(635,995, 5,2);
        mountainList[2] = new Mountain(845,995, 6, 4);
        mountainList[3] = new Mountain(1091,995, 7, 3);
    }
    
    public void draw(Graphics g)
    {
        Area a = new Area();
        for(Mountain m : mountainList)
        {
            
            m.draw(g, a);
            a.add(new Area(m.getMountainPolygon()));
        }
    }
    
}
