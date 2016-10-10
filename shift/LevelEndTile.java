package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class LevelEndTile extends Tile
{
    private Spaceship spaceship;
    private boolean pathSpawned = false;
    public LevelEndTile(int inX, int inY, int inWidth, int inLength, int inHeight) 
    {
        super(inX, inY, inWidth, inLength, inHeight);
        //TileSorter.addTile(this);
        setSpinnable(false);
        setMoveable(false);
        double[] vertexPos = {.5, .5};
        LevelEndPath lep = new LevelEndPath(this, vertexPos, 0, 0);//sort of janky way to make a path the same size as the tile so the tile can be treated like a walkable path.
        
        //ArrayList<Scenery> s = new ArrayList<Scenery>();
        getAssortedScenery().clear();
        spaceship = new Spaceship(this, .5, .5);
        setColor(Color.GRAY);
        double[] vertex = {0.5,0.5};
    }

    @Override 
    public void drawReflections(Graphics g)
    {      
        drawWaterReflectionCover(g);
        drawWaterReflectionsWithColor(g, Color.MAGENTA);
    }
    
    @Override
    public void draw(Graphics g) 
    {
       
       
       
       
       
       
       
       
        
        
        if(Input.dRotation != 0 || MouseInput.dScale != 0)
        {
            spaceship.getThread().interrupt();
            spaceship.setThread(new Thread(spaceship));
            spaceship.getThread().start();
        }
        
        for(Waterfall wf : getWaterfalls())
        {
            if(!wf.drawLast())
            {
                wf.draw(g);
            }
        }
        drawEarlyScenery(g);
        g.setColor(getColor());
        fillPolygons(g);
        
        for(Lake lake : getLakes())
        {
            lake.draw(g);
        }
        
        drawSidePolygons(g);//draws the sides of the tile.
        shadeSides(g);
        
        g.setColor(Color.BLACK);
        
        for(Path path : getPathList())
        {
            //path.draw(g);
        }
        for(int i = 0; i < getTrees().size(); i++)
        {
            getTrees().get(i).draw(g);
        }
        for(Waterfall wf : getWaterfalls())
        {
            if(wf.drawLast())
            {
                wf.draw(g);
            }
        }
        
        
        drawAssortedScenery(g);//see if I need to draw all the scenery individually when calling this.
        //spaceship.draw(g);
        //drawWaterReflectionsWithColor(g, Color.MAGENTA);
        //spaceship.initShapes();
    }
    
    public Spaceship getSpaceship()
    {
        return spaceship;
    }
    
    private void drawSidePolygons(Graphics g)
    {
        //g.setColor(Color.BLACK);
        //g.drawPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        //g.drawPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        //g.setColor(getColor());
    }
    
    private void fillPolygons(Graphics g)
    {
        g.setColor(Color.MAGENTA);
        g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
    }
    
}
