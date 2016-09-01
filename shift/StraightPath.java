/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author phusisian
 */
public class StraightPath extends Path
{

    public StraightPath(Tile tileIn, double startXIn, double startYIn, double endXIn, double endYIn, Color colorIn) 
    {
        super(tileIn, startXIn, startYIn, endXIn, endYIn, colorIn);
    }

    @Override
    public void draw(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
