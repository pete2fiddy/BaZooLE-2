package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


/*
Is a dirt path. As of now is a standard path with no notable differences to a standard path. 
*/

public class DirtPath extends Path
{
    /*
    params: the tile to which the path is placed on, array of doubles specifying where the vertex of the path is AS A DOUBLE PERCENTAGE RELATION FROM LEFT TO RIGHT. e.g. 0.0 is far left, .5 is middle, etc. x, y order.
    zeroXIn is the x  double (using same formatting as vertex) that the path starts at, zeryYIn is the y double (same formatting) that the path ends at. 
    */
    public DirtPath(Tile tileIn,double[] vertexPosIn, double zeroXIn, double zeroYIn) 
    {
        super(tileIn, vertexPosIn, zeroXIn, zeroYIn, new Color(139, 69, 19));//supers the color of dirt paths to path class
    }
    
    public DirtPath(Tile tileIn, double startXIn, double startYIn, double endXIn, double endYIn)
    {
        super(tileIn, startXIn, startYIn, endXIn, endYIn, new Color(139, 69, 19));
    }
    /*
    override's path's abstract draw method. draws the path. 
    */
    @Override
    public void draw(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D)g;
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(getColor());
        
        g.fillPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],getThreadedPathPolygon()[0].length);
        g.setColor(Color.BLACK);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],getThreadedPathPolygon()[0].length);
        /*g.fillOval(getThreadedPathPolygon()[0][0]-5, getThreadedPathPolygon()[1][0]-5, 10, 10);
        g.fillOval(getThreadedPathPolygon()[0][5]-5, getThreadedPathPolygon()[1][5]-5, 10, 10);
        g.fillOval(getThreadedPathPolygon()[0][2]-5, getThreadedPathPolygon()[1][2]-5, 10, 10);
        g.fillOval(getThreadedPathPolygon()[0][3]-5, getThreadedPathPolygon()[1][3]-5, 10, 10);
        g.setColor(Color.WHITE);*/
        /*for(int i = 0; i < getLinks().length; i++)
        {
            //g.setColor(Color.BLACK);
            g.fillOval((int)getLinks()[i].getX() - 5, (int)getLinks()[i].getY() - 5, 10, 10);
        }*/
    }
}
