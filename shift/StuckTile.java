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
public class StuckTile extends ShiftTile
{

    public StuckTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength, inHeight);
        setMoveable(false);
    }
    
    public void draw(Graphics g)
    {
        super.draw(g);
    }
    
}
