/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author phusisian
 */
public class Ladder extends Scenery implements IPathLink
{

    double widthOffset;
    private double offsetWidth = .1;
    private Path boundPath;
    private Path connectedPath;
    
    public Ladder(Tile tileIn, Path boundPathIn, double offsetXIn, double offsetYIn, double widthOffsetIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        boundPath = boundPathIn;
        widthOffset = widthOffsetIn;
        MergedPaths.pathLinks.add(this);
        boundPath.setLink(this);
    }
    
    private double getLadderTheta()
    {
        if(getOffsetX() == 0 || getOffsetX() == 1)
        {
            return WorldPanel.radSpin - Math.PI/2.0;
        }else
        {
            return WorldPanel.radSpin;
        }
    }
    
    private int[][] getLadderPolyPoints()
    {
        double theta = getLadderTheta();
        int[] xPoints = {(int)(getX() + Math.cos(theta) * getBoundTile().getRawWidth()*offsetWidth*WorldPanel.straightUnit), (int)(getX() - Math.cos(theta) * getBoundTile().getRawWidth()*offsetWidth*WorldPanel.straightUnit), (int)(getX() - Math.cos(theta) * getBoundTile().getRawWidth()*offsetWidth*WorldPanel.straightUnit), (int)(getX() + Math.cos(theta) * getBoundTile().getRawWidth()*offsetWidth*WorldPanel.straightUnit)};
        int[] yPoints = {(int)(getY() - Math.sin(theta) * (WorldPanel.getShrink * getBoundTile().getRawLength()*offsetWidth*WorldPanel.straightUnit)), (int)(getY() + Math.sin(theta) * (WorldPanel.getShrink * getBoundTile().getRawLength()*offsetWidth*WorldPanel.straightUnit)), (int)(getY() + (WorldPanel.scale*distortedHeight(getBoundTile().getHeight())) + Math.sin(theta) * (WorldPanel.getShrink * getBoundTile().getRawLength()*offsetWidth*WorldPanel.straightUnit)), (int)(getY() + (WorldPanel.scale*distortedHeight(getBoundTile().getHeight())) - Math.sin(theta) * (WorldPanel.getShrink * getBoundTile().getRawLength()*offsetWidth*WorldPanel.straightUnit))};
        int[][] gr = {xPoints,yPoints};
        return gr;
    }

    private Polygon linkDetectionPolygon()
    {
        int[][] points = getLadderPolyPoints();
        
        
        int[] xPoints = {(int)(getX()), (int)(getBoundTile().getCenterX()), (int)(getBoundTile().getCenterX()), (int)(getX())};
        int[] yPoints = {(int)(getY()), (int)(getBoundTile().getCenterY() - getBoundTile().getScaledDistortedHeight()), (int)(getBoundTile().getCenterY()), (int)(getY() + getBoundTile().getScaledDistortedHeight())};
                //int[] xPoints = {points[0][2], points[0][3], (int)getBoundTile().getCenterX()};
        //int[] yPoints = {points[1][2], points[1][3], (int)getBoundTile().getCenterY()};
        
        return new Polygon(xPoints, yPoints, 4);
    }
    
    @Override
    public Point[] getLinkPoints() 
    {
        Point[] points = {new Point((int)(getX()), (int)(getY() + getBoundTile().getScaledDistortedHeight())) , new Point((int)(getX()), (int)(getY() + getBoundTile().getScaledDistortedHeight()))};
        //Point[] points = {new Point((int)convertToUnit(getX(), getY() + getBoundTile().getScaledDistortedHeight())[0], (int)convertToUnit(getX(), getY() + getBoundTile().getScaledDistortedHeight())[1]) , new Point((int)convertToUnit(getX(), getY() + getBoundTile().getScaledDistortedHeight())[0], (int)convertToUnit(getX(), getY() + getBoundTile().getScaledDistortedHeight())[1])  };
        //Point[] points = {new Point((int)convertToUnit(getX(), getY()+getBoundTile().getScaledDistortedHeight())[0], (int)convertToUnit(getX(), getY()+getBoundTile().getScaledDistortedHeight())[1]), new Point((int)convertToUnit(getX(), getY()+getBoundTile().getScaledDistortedHeight())[0], (int)convertToUnit(getX(), getY()+getBoundTile().getScaledDistortedHeight())[1])};
        return points;
    }
    
    @Override
    public int[] getLinkHeights()
    {
        int[] heights = {0, getBoundTile().getHeight()};
        return heights;
    }
    
    @Override
    public void draw(Graphics g) 
    {
        //g.fillOval((int)getX(), (int)getY(), 10, 10);
        /*for(int i = 0; i < getLadderPolyPoints()[0].length; i++)
        {
            g.fillOval(getLadderPolyPoints()[0][i] - 5, getLadderPolyPoints()[1][i]-5, 10, 10);
        }*/
        g.fillPolygon(getLadderPolyPoints()[0], getLadderPolyPoints()[1], 4);
        g.setColor(Color.RED);
        g.fillPolygon(linkDetectionPolygon());
        g.setColor(Color.WHITE);
        for(int i = 0; i < getLinkHeights().length; i++)
        {
            g.fillOval((int)getLinkPoints()[i].getX()-5, (int)(getLinkPoints()[i].getY() - (WorldPanel.scale*distortedHeight(getLinkHeights()[i])))-5, 10, 10);
        }
        //throw new UnsupportedOperatdionException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean pathLinkContainsPoint(Point p) 
    {
        if(linkDetectionPolygon().contains(p) || new Polygon(getLadderPolyPoints()[0], getLadderPolyPoints()[1],4).contains(p))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void setConnectedPath(Path p)
    {
        connectedPath = p;
    }

    @Override
    public Path getBoundPath() 
    {
        return boundPath;
    }

    @Override
    public Path getConnectedPath() 
    {
        return connectedPath;
    }

    
    
}
