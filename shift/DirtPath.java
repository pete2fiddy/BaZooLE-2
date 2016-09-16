package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;


/*
Is a dirt path. As of now is a standard path with no notable differences to a standard path. 
*/

public class DirtPath extends Path
{
    private final Color darkBrown = new Color(125, 62, 17);
    private final Color darkerBrown = new Color(115, 59, 18);
    private final Color evenDarkerBrown = new Color(102, 52, 16);
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
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.setColor(getColor());
        g.setColor(darkBrown);
        g.fillPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],getThreadedPathPolygon()[0].length);
        g.setColor(evenDarkerBrown);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.drawPolygon(getThreadedPathPolygon()[0], getThreadedPathPolygon()[1],getThreadedPathPolygon()[0].length);
        //drawPathDot(g);
        g.setColor(Color.BLUE);
        /*for(Point p : getLinks())
        {
            g.fillOval((int)p.getX() - 5, (int)p.getY() - 5, 10, 10);
        }*/
        //drawLinks(g);
    }
}
