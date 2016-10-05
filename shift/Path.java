package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Scanner;

/*
Is a path that player can walk across to complete objectives. Is a special type of scenery which is traversable. All paths are traversable by default, however in the future i may want to make this toggleable.
*/
public abstract class Path extends Scenery implements Runnable
{
    Point[] links = new Point[2];//right now assuming that each path will only have two possible links. 
    private boolean isStraight = false;
    private Color color;//the main color of the path. 
    private double zeroX, zeroY;//saves the beginning x offset compared to the tile and the ending y offset compared to the tile.
    private double[] vertex;//saves the vertex offsets of the path at the middle. May just want to convert to vertexX and vertexY and make another constructor which takes the two as an x and y and an array, because this is just annoying...
    private double pathWidth = 0.10;//is the default offset width of a path. Could have issues in that bigger tiles have bigger paths, etc. Will fix when it becomes a problem.
    private Polygon pathPolygon;//holds the actual polygon object that makes the path. Can be use for contains() methods, so seeing if the player is currently on top of this polygon, for example.
    private int[][] threadedPathPolygon;//***Consider changing name to specify that this is actually an array of polygon points*** Holds the threaded points that make up this polygon.
    private IPathLink pathLink;
    private IPathLink connectedLink;
    /*
    params: the tile this path is bound to, the vertex offset x and y as an array, the zeroX offset, the zeroY offset, and the most common color of the path.
    */
    public Path(Tile tileIn, double[] vertexPosIn, double zeroXIn, double zeroYIn, Color colorIn)//could take a rotation and flip pos around that?
    {
        super(tileIn, vertexPosIn[0], vertexPosIn[1]);
        vertex = vertexPosIn;
        color = colorIn;
        zeroX = zeroXIn;
        zeroY = zeroYIn;
        threadedPathPolygon = getPathPolygon(pathWidth).clone();
        pathPolygon = new Polygon(threadedPathPolygon[0], threadedPathPolygon[1], 6);
        updateLinks();
        MergedPaths.pathList.add(this);//adds itself to MergedPath's list of paths.
        tileIn.addPath(this);//adds itself to its bound tile's list of paths.
        //tileIn.getAssortedScenery().remove(this);
        pathWidth = pathWidth/Math.sqrt(tileIn.getRawWidth() * tileIn.getRawLength());//to make path widths uniform across multi-sized tiles. May need to be fixed for odd-shaped, non-square tiles.
    }
    
    public Path(Tile tileIn, double startXIn, double startYIn, double endXIn, double endYIn, Color colorIn)//could take a rotation and flip pos around that?
    {
        super(tileIn, Math.abs((startXIn + endXIn)/2.0), Math.abs((startYIn + endYIn)/2.0));
        double d[] = {Math.abs((startXIn + endXIn)/2.0), Math.abs((startYIn + endYIn)/2.0)};
        vertex = d;
        color = colorIn;
        if(startXIn == endXIn)
        {
            zeroY = 1;
            zeroX = 0;
        }else{
            zeroX = 1;
            zeroY = 0;
        }
        //zeroX = 0;//set to zero because it needs to be set to something -- come here if problems occur.
        //zeroY = 0;//set to zero because it needs to be set to something -- come here if problems occur.
        threadedPathPolygon = getPathPolygon(pathWidth).clone();
        pathPolygon = new Polygon(threadedPathPolygon[0], threadedPathPolygon[1], 6);
        isStraight = true;
        updateLinks();
        MergedPaths.pathList.add(this);//adds itself to MergedPath's list of paths.
        tileIn.addPath(this);//adds itself to its bound tile's list of paths.
        pathWidth = pathWidth/Math.sqrt(tileIn.getRawWidth() * tileIn.getRawLength());//to make path widths uniform across multi-sized tiles. May need to be fixed for odd-shaped, non-square tiles.
    }
    
    
    
    
    /*
    updates the links's list so that the hangover points for path connections are accurate with world movement.
    */
    public void updateLinks()//make sure it ALWAYS makes proper links for its path -- haven't tried every path combination yet. (make sure where links are being placed makes sense)
    {
        if(!isStraight)
        {
            //doesn't work for 1, 0  or  1, 1
            double hangoverAmount = 10*WorldPanel.scale;
            /*if(zeroX != zeroY)
            {
                int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0)))));
            }else {
                int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin() + (Math.PI/2.0))), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI/2.0)))));
            }*/

            if(zeroX == 0 && zeroY == 0)
            {
                int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI/2.0)))));
                links[1] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin() )), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
            }else if(zeroX == 0 && zeroY == 1){
                int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0)))));
            }else if(zeroX == 1 && zeroY == 0)
            {
                /*int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin() + (Math.PI/2.0))), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI/2.0)))));*/


                int[][] tempPolygon = getPathPolygon(0);

                links[0] = new Point((int)(tempPolygon[0][0] + hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] - hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin() + (Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI/2.0)))));

            }else{
                int[][] tempPolygon = getPathPolygon(0);

                /*links[0] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0)))));*/


                //links[0] = new Point((int)(tempPolygon[0][3] + hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0))), (int)(tempPolygon[1][3] - hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0)))));
                //links[1] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));

                links[0] = new Point((int)(tempPolygon[0][3] + hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(Math.PI/2.0))), (int)(tempPolygon[1][3] - hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(Math.PI/2.0)))));
                links[1] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+ (Math.PI))), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin() + (Math.PI)))));
            }
        }else{
            double hangoverAmount = 10*WorldPanel.scale;
            int[][] tempPolygon = getPathPolygon(0);
            if(zeroY == 1)
            {
                //links[0] = new Point( (int)(tempPolygon[0][3] +) );
                //int[][] tempPolygon = getPathPolygon(0);
                links[0] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(Math.PI/2.0))), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(Math.PI/2.0)))));//new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(3*Math.PI/2.0)))));
            }else{
                links[0] = new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));//new Point((int)(tempPolygon[0][0] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin())), (int)(tempPolygon[1][0] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()))));
                links[1] = new Point((int)(tempPolygon[0][3] - hangoverAmount * Math.cos(WorldPanel.radSpin + getBoundTile().getSpin()+(2*Math.PI/2.0))), (int)(tempPolygon[1][3] + hangoverAmount * (WorldPanel.getShrink * Math.sin(WorldPanel.radSpin + getBoundTile().getSpin()+(2*Math.PI/2.0)))));
            }
        }
        
    }
    
    public boolean pathOnCoord(double xIn, double yIn)
    {
        double[] points = convertToPoint(xIn, yIn);
        return (pathPolygon.contains(points[0], points[1]));
    }
    
    public double getZeroX()
    {
        return zeroX;
    }
    
    public double getZeroY()
    {
        return zeroY;
    }
    
    public boolean contains(int x, int y)
    {
        return (pathPolygon.contains(x, y));
    }
    
    /*
    params: takes a path and sees if this path is connected to that one.
    returns: whether or not this path is connected to the path passed to it.
    */
    public boolean isConnected(Path comparePath)//boolean that returns if another path is directly connected to this one.
    {
        for(Point p : links)
        {
            if(comparePath.getPathPolygon().contains((int)p.getX(), (int)p.getY()) && comparePath.getBoundTile().getHeight() == getBoundTile().getHeight())
            {
                return true;
            }else if(comparePath.getPathLink() != null)
            {
                if(comparePath.getPathLink().pathLinkContainsPoint(p))
                {
                    connectedLink = comparePath.getPathLink();
                    return true;
                }
            }
        }
        if(pathLink != null)
        {
            for(Point p : comparePath.getLinks())
            {
                if(pathLink.pathLinkContainsPoint(p))
                {
                    pathLink.setConnectedPath(comparePath);
                    return true;
                }
            }
        }
        return false;
    }
   
    public IPathLink getConnectedLink()
    {
        return connectedLink;
    }
    
    public void setLink(IPathLink ipl)
    {
        pathLink = ipl;
    }
    
    public IPathLink getPathLink()
    {
        return pathLink;
    }
    
    
    /*
    returns the number of paths this one is connected to. If it is 0, it is on its own, if it is 1, it is the start or finish of a path chain, if it is 2, it is somewhere in the middle of a path chain.
    */
    public int numPathConnections()
    {
        return getNeighboringPaths().size();
    }
    
    /*
    draws a green dot on the getX() and getY() of the path. Useful for debugging.
    */
    public void drawPathDot(Graphics g)
    {
        g.setColor(Color.GREEN);
        g.fillOval((int)getX()-5, (int)getY()-5, 10, 10);
    }
    
    /*
    ***Consider changing this to getConnectedPaths()***
    returns all paths that are directly connected to this one.
    */
    public ArrayList<Path> getNeighboringPaths()
    {
        ArrayList<Path> giveReturn = new ArrayList<Path>();
        for(int i = 0; i < MergedPaths.pathList.size(); i++)
        {
            if(isConnected(MergedPaths.pathList.get(i)))
            {
                giveReturn.add(MergedPaths.pathList.get(i));
            }
        }
        return giveReturn;
    }
    
    /*
    ***Consider changing this to getConnectedPathsExcluding***
    params: a path to exclude during this process.
    returns: a list of all paths that are connected to this one EXCLUDING the path passed to it.
    */
    public ArrayList<Path> getNeighboringPathsExcluding(Path exclusion)//returns an arrayList of all directly connected paths to this one OTHER than the one passed to it. Being an arraylist may not be relevant as I don't know if I will ever have 3 way/4 way paths
    {
        ArrayList<Path> giveReturn = new ArrayList<Path>();
        for(int i = 0; i < MergedPaths.pathList.size(); i++)
        {
            if(isConnected(MergedPaths.pathList.get(i)) && MergedPaths.pathList.get(i) != exclusion)
            {
                giveReturn.add(MergedPaths.pathList.get(i));
            }
        }
        return giveReturn;
    }
    
    public double[] getVertex()
    {
        return vertex;
    }
    
    /*
    returns the coordinate value of the vertex.
    */
    public double[] getVertexCoord()
    {
        double[] giveReturn = {getBoundTile().getRawX() + (getBoundTile().getRawWidth() * vertex[0]), getBoundTile().getRawY() + (getBoundTile().getRawLength() * vertex[1])};
        return giveReturn;
    }
    
    public int[][] getThreadedPathPolygon()
    {
        return threadedPathPolygon;
    }
    
    public double getPathWidth()
    {
        return pathWidth;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public void setPathPolygon(Polygon p){pathPolygon = p;}
    public void setPathWidth(double d){pathWidth = d;}
    
    /*
    returns the offset position of the start point of the path. ***May not be needed***
    */
    public double[] getStart(double pathWidth)
    {
        double[] giveReturn = {0, getOffsetY()+pathWidth};
        return giveReturn;
    }
    
    /*
    returns the offset position of the end point of the path. ***May not be needed***
    */
    public double[] getEnd(double pathWidth)
    {
        double[] giveReturn = {getOffsetX()+pathWidth, 1};
        return giveReturn;
    }
    
    /*
    ***May not be needed. Consider renaming to getPathOffsets(), as that is what it is, basically.***
    */
    public int[][] getPathPoints()
    {
        int[][] pathPoints = { {(int)getPosAtPoint(zeroX, getOffsetY())[0], (int)getPosAtPoint(getOffsetX(), getOffsetY())[0], (int)getPosAtPoint(getOffsetX(), zeroY)[0]} , {(int)getPosAtPoint(zeroX, getOffsetY())[1], (int)getPosAtPoint(getOffsetX(), getOffsetY())[1], (int)getPosAtPoint(getOffsetX(), zeroY)[1]} };
        return pathPoints;   
    }
    
    /*
    returns the points of the path in terms of x and y coordinates on the world panel.
    */
    public double[][] getPathCoords()
    {
        int[][] points = getPathPoints();
        double[][] giveReturn = new double[2][3];
        for(int i = 0; i < 3; i++)
        {
            giveReturn[0][i]=convertToUnit(points[0][i], points[1][i])[0];
            giveReturn[1][i]=convertToUnit(points[0][i], points[1][i])[1];
        }
        return giveReturn;
    }
    
    public int[][] getPathPolygon(double pathWidth)
    {
        if(!isStraight)
        {
            if(zeroX==zeroY)
            {

                int[][] pathPoints = {{(int)getPosAtPoint(zeroX, getOffsetY()+pathWidth)[0], (int)getPosAtPoint(getOffsetX()+pathWidth, getOffsetY()+pathWidth)[0], (int)getPosAtPoint(getOffsetX()+pathWidth, zeroY)[0], (int)getPosAtPoint(getOffsetX()-pathWidth, zeroY)[0], (int)getPosAtPoint(getOffsetX()-pathWidth, getOffsetY()-pathWidth)[0], (int)getPosAtPoint(zeroX, getOffsetY()-pathWidth)[0]},{(int)getPosAtPoint(zeroX, getOffsetY()+pathWidth)[1], (int)getPosAtPoint(getOffsetX()+pathWidth, getOffsetY()+pathWidth)[1], (int)getPosAtPoint(getOffsetX()+pathWidth, zeroY)[1], (int)getPosAtPoint(getOffsetX()-pathWidth, zeroY)[1], (int)getPosAtPoint(getOffsetX()-pathWidth, getOffsetY()-pathWidth)[1], (int)getPosAtPoint(zeroX, getOffsetY()-pathWidth)[1]}};


                for(int i = 0; i < pathPoints[0].length; i++)
                {
                    double vertexCoordX = convertToUnit(getX(), getY())[0];
                    double vertexCoordY = convertToUnit(getX(), getY())[1];
                    double currentX = convertToUnit(pathPoints[0][i], pathPoints[1][i])[0];
                    double currentY = convertToUnit(pathPoints[0][i], pathPoints[1][i])[1];
                    double dy = vertexCoordY-currentY ;
                    double dx = currentX - vertexCoordX;

                    double theta = Math.atan2(dy, dx);
                    double radius = Math.sqrt( Math.pow(dy , 2)  + Math.pow(dx, 2)  );

                    pathPoints[0][i]=(int)(getX()+(radius*WorldPanel.straightUnit*Math.cos(theta + getBoundTile().getSpin()-WorldPanel.radSpin)));//Math.PI/2.0 added since it was off by a constant radian -- not sure why
                    pathPoints[1][i]=(int)(getY() +(WorldPanel.getShrink*WorldPanel.straightUnit*radius*Math.sin(theta+getBoundTile().getSpin()-WorldPanel.radSpin)));//Math.PI/2.0 added since it was off by a constant radian -- not sure why
                }
                return pathPoints;
            }else{
                int[][] pathPoints = {{(int)getPosAtPoint(zeroX, getOffsetY()+pathWidth)[0], (int)getPosAtPoint(getOffsetX()-pathWidth, getOffsetY()+pathWidth)[0], (int)getPosAtPoint(getOffsetX()-pathWidth, zeroY)[0], (int)getPosAtPoint(getOffsetX()+pathWidth, zeroY)[0], (int)getPosAtPoint(getOffsetX()+pathWidth, getOffsetY()-pathWidth)[0], (int)getPosAtPoint(zeroX, getOffsetY()-pathWidth)[0]}   ,   {(int)getPosAtPoint(zeroX, getOffsetY()+pathWidth)[1], (int)getPosAtPoint(getOffsetX()-pathWidth, getOffsetY()+pathWidth)[1], (int)getPosAtPoint(getOffsetX()-pathWidth, zeroY)[1], (int)getPosAtPoint(getOffsetX()+pathWidth, zeroY)[1], (int)getPosAtPoint(getOffsetX()+pathWidth, getOffsetY()-pathWidth)[1], (int)getPosAtPoint(zeroX, getOffsetY()-pathWidth)[1]}};            
                for(int i = 0; i < pathPoints[0].length; i++)
                {
                    double vertexCoordX = convertToUnit(getX(), getY())[0];
                    double vertexCoordY = convertToUnit(getX(), getY())[1];
                    double currentX = convertToUnit(pathPoints[0][i], pathPoints[1][i])[0];
                    double currentY = convertToUnit(pathPoints[0][i], pathPoints[1][i])[1];
                    double dy = currentY - vertexCoordY;
                    double dx = currentX - vertexCoordX;

                    double theta = Math.atan2(dy, dx);
                    double radius = Math.sqrt( Math.pow(dy , 2)  + Math.pow(dx, 2)  );

                    pathPoints[0][i]=(int)(getX()+(radius*WorldPanel.straightUnit*Math.cos(theta + getBoundTile().getSpin()+WorldPanel.radSpin)));//Math.PI/2.0 added since it was off by a constant radian -- not sure why
                    pathPoints[1][i]=(int)(getY()-(WorldPanel.getShrink*WorldPanel.straightUnit*radius*Math.sin(theta + getBoundTile().getSpin()+ WorldPanel.radSpin)));//Math.PI/2.0 added since it was off by a constant radian -- not sure why
                }
                return pathPoints;
            }
        }else{
            if(zeroX == 1)
            {
                int[] xPoints = { (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]+pathWidth))[0] , (int)convertToPoint(convertOffsetXToCoord(vertex[0]), convertOffsetYToCoord(vertex[1]+pathWidth))[0] , (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]+pathWidth))[0], (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]-pathWidth))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]), convertOffsetYToCoord(vertex[1]-pathWidth))[0], (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]-pathWidth))[0] };
                int[] yPoints = { (int)(convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]+pathWidth))[1] - scaledDistortedHeight(getBoundTile().getRawHeight())) , (int)(convertToPoint(convertOffsetXToCoord(vertex[0]), convertOffsetYToCoord(vertex[1]+pathWidth))[1]- scaledDistortedHeight(getBoundTile().getRawHeight())) , (int)(convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]+pathWidth))[1]- scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]-pathWidth))[1]- scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]), convertOffsetYToCoord(vertex[1]-pathWidth))[1]- scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]-pathWidth))[1] - scaledDistortedHeight(getBoundTile().getRawHeight()))};
                int[][] pathPoints = new int[2][6];
                pathPoints[0]=xPoints;
                pathPoints[1]=yPoints;
                return pathPoints;
            }else{
                //int[] xPoints = { (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]+pathWidth))[0] , (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]+pathWidth))[0], (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]-pathWidth))[0], (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]-pathWidth))[0] };
                int[] xPoints = {(int)convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(0))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(vertex[1]))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(1))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(1))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(vertex[1]))[0], (int)convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(0))[0]};
                int[] yPoints = {(int)(convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(0))[1]-scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(vertex[1]))[1]-scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]+pathWidth), convertOffsetYToCoord(1))[1]-scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(1))[1]-scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(vertex[1]))[1]-scaledDistortedHeight(getBoundTile().getRawHeight())), (int)(convertToPoint(convertOffsetXToCoord(vertex[0]-pathWidth), convertOffsetYToCoord(0))[1]-scaledDistortedHeight(getBoundTile().getRawHeight()))};
                //int[] yPoints = { (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]+pathWidth))[1] , (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]+pathWidth))[1], (int)convertToPoint(convertOffsetXToCoord(1), convertOffsetYToCoord(vertex[1]-pathWidth))[1], (int)convertToPoint(convertOffsetXToCoord(0), convertOffsetYToCoord(vertex[1]-pathWidth))[1] };
                int[][] pathPoints = new int[2][6];
                pathPoints[0]=xPoints;
                pathPoints[1]=yPoints;
                return pathPoints;
            }
            
        }
    }
    
    public abstract void draw(Graphics g);
    
    public Point[] getLinks()
    {
        return links;
    }
    
    /*
    ***Consider doing path contains detection from here so i don't have to grab the polygon every time.***
    */
    public boolean pathOnPoint(double x, double y)
    {
        if(getPathPolygon().contains(x, y))//uses getPathPolygon so i can override a tile's path polygon if it is oddly shaped (level end path for example)
        {
            //System.out.println("was called");
            return true;
        }
        return false;
    }
    
    public Polygon getPathPolygon()
    {
        return pathPolygon;
    }
    
    public boolean eqauls(Path p)
    {
        return p.getX() == getX() && p.getY() == getY();
    }
    
    
    
    /*
    updates positioning
    */
    @Override
    public void run()
    {
        threadedPathPolygon = getPathPolygon(pathWidth).clone();
        
        pathPolygon = new Polygon(threadedPathPolygon[0], threadedPathPolygon[1], threadedPathPolygon[0].length);
        updateLinks();
    }
}
