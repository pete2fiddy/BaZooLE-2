package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

public class LevelEndPath extends Path implements Runnable
{
    public LevelEndPath(Tile tileIn, double[] vertexPosIn, double zeroXIn, double zeroYIn) 
    {
        super(tileIn, vertexPosIn, zeroXIn, zeroYIn, tileIn.getColor());
    }
    
    public LevelEndPath(Tile tileIn, double startXIn, double startYIn, double endXIn, double endYIn)
    {
        super(tileIn,startXIn,startYIn,endXIn,endYIn, tileIn.getColor());
    }

    @Override
    public void draw(Graphics g) 
    {
        updateLinks();
        //Graphics2D g2 = (Graphics2D)g;
        g.setColor(WorldPanel.grassColor);
        g.fillPolygon(getBoundTile().threadedUpperPoints()[0], getBoundTile().threadedUpperPoints()[1],4);
        
        /*g2.setPaint(Color.YELLOW);
        g.fillPolygon(getPathPolygon());
        g.setColor(Color.BLACK);
        g.drawString(Integer.toString(numPathConnections()),(int)getX(), (int)getY());*/
    }
    
    
    @Override
    public Polygon getPathPolygon()
    {
        return new Polygon(getBoundTile().threadedUpperPoints()[0], getBoundTile().threadedUpperPoints()[1],4);
    }
    
}
