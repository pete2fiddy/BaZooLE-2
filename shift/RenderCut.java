/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Rectangle;

/**
 *
 * @author Peter
 */
public class RenderCut 
{
    public static boolean pointCovered(int index, int xPos, int yPos)
    {
        for(int i = index + 1; i < TileDrawer2.tileList.size(); i++)
        {
            Rectangle[] rects = TileDrawer2.tileList.get(i).getBoundingRects();
            for(Rectangle r : rects)
            {
                if(r.contains(xPos, yPos))
                {
                    return true;
                }
            }
            /*if(tileList.get(i).getBoundingRect().contains(xPos, yPos))
            {
                return true;
            }*/
        }
        return false;
    }
}
