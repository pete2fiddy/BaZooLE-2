/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Graphics;

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
        mountainList[0] = new Mountain(-57,995, 1);
        mountainList[1] = new Mountain(145,995, 2);
        mountainList[2] = new Mountain(298,995, 3);
        mountainList[3] = new Mountain(468,995, 4);
        mountainList[4] = new Mountain(635,995, 5);
        mountainList[5] = new Mountain(845,995, 6);
        mountainList[6] = new Mountain(1091,995, 7);
    }
    
    public void draw(Graphics g)
    {
        for(Mountain m : mountainList)
        {
            m.draw(g);
        }
    }
    
}
