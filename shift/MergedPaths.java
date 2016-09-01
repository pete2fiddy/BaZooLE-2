package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.util.ArrayList;

/*
Creates a merged path for path following. May turn out to be useless, depending on the algorithm I end up using converting movement from path chains into player movement.
*/
public class MergedPaths extends Toolbox implements Runnable
{
    public static ArrayList<Path> pathList = new ArrayList<Path>();//holds all the paths. Each path adds itself to this upon initialization.
    public static ArrayList<IPathLink> pathLinks = new ArrayList<IPathLink>();
    public static Area threadedArea;//is updated every time the thread fires. Is how classes can access this area.
    private Thread thread;//this class's thread
    
    /*
    makes and starts the thread.
    */
    public MergedPaths()
    {
        thread = new Thread(this);
        thread.start();
    }
    
    public Thread getThread()
    {
        return thread;
    }
    
    public void setThread(Thread t)
    {
        thread = t;
    }
    
    /*
    builds the area of paths from all of the paths.
    */
    private Area getArea()
    {
        Area area = new Area();
        for(int i = 0; i < pathList.size(); i++)
        {
            area.add(new Area(pathList.get(i).getPathPolygon()));
        }
        return area;
    }
    
    /*
    draws the mergedpath area if need be.(more an invisible shape not meant to be drawn but useful for debugging
    */
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g.setColor(Color.BLACK);
       //g2.fill(threadedArea);//fills the pathed area if you want.
    }
    
    /*
    updates the threaded area.
    */
    @Override
    public void run() 
    {
        threadedArea = getArea();
    }
    
}
