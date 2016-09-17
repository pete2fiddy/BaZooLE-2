package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
        g2.setPaint(WorldPanel.grassTexture);
        g.fillPolygon(getBoundTile().threadedUpperPoints()[0], getBoundTile().threadedUpperPoints()[1],4);
        g.setColor(Color.BLACK);
    }
}
