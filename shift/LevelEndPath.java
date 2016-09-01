/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 *
 * @author phusisian
 */
public class LevelEndPath extends Path
{

    public LevelEndPath(Tile tileIn, double[] vertexPosIn, double zeroXIn, double zeroYIn) 
    {
        super(tileIn, vertexPosIn, zeroXIn, zeroYIn, tileIn.getColor());
        
    }

    @Override
    public void draw(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;
        //g.setColor(getColor());
        g2.setPaint(WorldPanel.grassTexture);
        //g.fillPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],6);
        g.fillPolygon(getBoundTile().threadedUpperPoints()[0], getBoundTile().threadedUpperPoints()[1],4);
        g.setColor(Color.BLACK);
        //g.drawPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],6);
        g.drawPolygon(getBoundTile().threadedUpperPoints()[0], getBoundTile().threadedUpperPoints()[1],4);
        /*for(int i = 0; i < getLinks().length; i++)
        {
            g.setColor(Color.BLACK);
            g.fillOval((int)getLinks()[i].getX() - 5, (int)getLinks()[i].getY() - 5, 10, 10);
        }*/
    }
    
}
