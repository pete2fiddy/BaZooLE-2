package shift;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javafx.scene.shape.Line;

public abstract class Tile extends Toolbox implements Runnable //make a constructor that takes unit inputs or pixel inputs so objects don't have to be placed on a tile?
{
    private boolean rightClicked= false;
    private boolean moveable = true;
    private double spin;
    private boolean spinnable = false;
    private Polygon threadedTilePolygon;
    private Thread thread;
    private Color firstColor;
    private double thisdx, thisdy;
    private int[][] myThreadedUpperPoints, myThreadedLowerPoints, polyPoints1, polyPoints2;
    private double x, y;
    private double initialClickSpin = 0;
    private int baseHeight, width, length;
    private double height;
    private Color color;
    private boolean thisClicked;
    private boolean inTransit = false;
    private double[] oldPos = new double[2];
    private int transitTicksMoved = 0;
    private ArrayList<Scenery> sceneryList = new ArrayList<Scenery>();
    private ArrayList<Lake> lakes = new ArrayList<Lake>();
    private ArrayList<Tree> trees = new ArrayList<Tree>();
    private ArrayList<Waterfall> waterfalls = new ArrayList<Waterfall>();
    private ArrayList<Path> pathList = new ArrayList<Path>();
    private ArrayList<Scenery> assortedScenery = new ArrayList<Scenery>();
    private int[] transitPos;
    private boolean clickBuffer = false;
    private Polygon hitPolygon;
    public static boolean tileJustUnclicked = false;
    public static boolean resortTiles = false;
    private boolean resortTilesDebounce = false;
    public static boolean tileCurrentlyMoving = false;
    private boolean tileCurrentlyMovingDebounce = false;
    private int walkedOn = 0;
    private boolean heightChangeable = false;
    private double movingX = 0, movingY = 0;
    //private double transitX = 0, transitY = 0;
    
    int l = 1;
    sout    
            
    public Tile(int inX, int inY, int inWidth, int inLength, int inHeight)//not sure why position isi given as a double. Can't see myself using half a unit or something.
    {
        x=inX;y=inY;width=inWidth;length=inLength; baseHeight = inHeight; height = baseHeight;
        thisClicked = false;
        polyPoints1 = new int[2][4];
        polyPoints2 = new int[2][4];
        color = new Color(14, 155, 14);
        firstColor = color;
        myThreadedUpperPoints = getUpperPoints();
        myThreadedLowerPoints = getPoints();
        thread = new Thread(this);
        transitPos = new int[2];
        spin = 0;
        movingX = x; 
        movingY = y;
        threadedTilePolygon = new Polygon(myThreadedLowerPoints[0], myThreadedLowerPoints[1],4);
        if(getClass() != BlockTile.class)
        {
            TileDrawer2.tileList.add(this);
        }
        thread.start();
        addRandomFlowers(5, 12);
    }
    
    public void setSpinnable(boolean b){spinnable = b;}
    
    public void removePath(int index)
    {
        for(int i = 0; i < MergedPaths.pathList.size(); i++)
        {
            if(MergedPaths.pathList.get(i)==pathList.get(index))
            {
                MergedPaths.pathList.remove(i);
            }
        }
        pathList.remove(index);
    }
    
    public void setMoveable(boolean b){moveable = b;}
    public void addScenery(Scenery s){sceneryList.add(s);}
    public void addLake(Lake l){lakes.add(l);}
    public void addTree(Tree t){trees.add(t);}
    public void addWaterfall(Waterfall wf){waterfalls.add(wf);}
    public void addPath(Path p){pathList.add(p);}
    public void addAssortedScenery(Scenery s){assortedScenery.add(s);}
    
    public void drawAssortedScenery(Graphics g)
    {
        for(Scenery s : assortedScenery)
        {
            s.draw(g);
        }
    }
    private void addRandomFlowers(int min, int max)
    {
        double radiusApart = 0.05;
        int numFlowers = min+(int)(max*Math.random());
        for(int i = 0; i < numFlowers; i++)
        {
            double randomX = 0.05*(int)(Math.random()/radiusApart);
            double randomY = 0.05*(int)(Math.random()/radiusApart);
            Flower f = new Flower(this,randomX,randomY,10, 1.0);
        }
    }
    public ArrayList<Scenery> getSceneryList(){return sceneryList;}
    public ArrayList<Lake> getLakes(){return lakes;}
    public ArrayList<Tree> getTrees(){return trees;}
    public ArrayList<Waterfall> getWaterfalls(){return waterfalls;}
    public ArrayList<Path> getPathList(){return pathList;}
    
    public int[][] getLeftSidePoints()
    {
        if((WorldPanel.radSpin > (Math.PI/2.0) && WorldPanel.radSpin < (Math.PI)) || (WorldPanel.radSpin > (3*Math.PI/2.0) && WorldPanel.radSpin < (2*Math.PI)))
        {
            return polyPoints2;
        }else{
            return polyPoints1;
        }
    }
    
    public int[][] getRightSidePoints()
    {
        if((WorldPanel.radSpin > (Math.PI/2.0) && WorldPanel.radSpin < (Math.PI)) || (WorldPanel.radSpin > (3*Math.PI/2.0) && WorldPanel.radSpin < (2*Math.PI)))
        {
            return polyPoints1;
        }else{
            return polyPoints2;
        }
    }
    
    public double getSpin(){return spin;}
    public void setSpin(double spinIn){spin = spinIn;}
    
    public Polygon getLowerPolygon(){return threadedTilePolygon;}
    
    public Polygon getUpperPolygon(){return new Polygon(myThreadedUpperPoints[0], myThreadedUpperPoints[1], 4);}
    
    
    public Polygon getHitPolygon(){return hitPolygon;}    
    public Color getColor(){return color;}
    public void setColor(Color c){color = c;}
    public double getRawX()
    {
        if(!inTransit)
        {
            return x;
        }
        return movingX;
    }
    public double getRawY()
    {   
        if(!inTransit)
        {
            return y;
        }
        return movingY;
    
    }
    public double getMiddleCoordX(){return x + ((double)width/2.0);}
    public double getMiddleCoordY(){return y + ((double)length/2.0);}
    public int getRawWidth(){return width;}
    public int getRawLength(){return length;}
    public int getRawHeight(){return (int)height;}
    public int getPointCoordX(int point){return (int)x + ((1-(int)((double)point/2.0))*width);}
    public Thread getThread(){return thread;}
    public void setThread(Thread t){thread = t;}
    
    public int getPointCoordY(int point){return (int)y + ((int)((double)point/2.0)*length);}
    
    public int[][] getUpperPoints()//should this be working with the threaded upper points? Code works but...
    {
        int[][]myThreadedUpperPoints={getPoints()[0],getPoints()[1]};
        for(int i = 0; i < 4; i++)
        {
            myThreadedUpperPoints[1][i]-=(int)(WorldPanel.scale * WorldPanel.distortedHeight(WorldPanel.rotation, getHeight()));
        }
        return myThreadedUpperPoints;
    }
    public double getScaledDistortedHeight()
    {
        return WorldPanel.distortedHeight(WorldPanel.rotation, (int)(getHeight() * WorldPanel.scale));
    }
    //are all private since are only used in calculating
    private double getdyOne(){return ((WorldPanel.mapPoints[1][2]-WorldPanel.mapPoints[1][1])+0.0)/((WorldPanel.mapHeight/WorldPanel.unit)+0.0);}
    private double getdxOne(){return ((WorldPanel.mapPoints[0][2]-WorldPanel.mapPoints[0][1])+0.0)/((WorldPanel.mapWidth/WorldPanel.unit)+0.0);}
    private double getdyTwo(){return ((WorldPanel.mapPoints[1][3]-WorldPanel.mapPoints[1][2])+0.0)/((WorldPanel.mapHeight/WorldPanel.unit)+0.0);}
    private double getdxTwo(){return ((WorldPanel.mapPoints[0][3]-WorldPanel.mapPoints[0][2])+0.0)/((WorldPanel.mapWidth/WorldPanel.unit)+0.0);}
    private double getxTwo()
    {
        if(!inTransit)
        {
            return WorldPanel.worldX+(getdxOne()*x) +(getdxTwo()*y);
        }
        return WorldPanel.worldX+(getdxOne()*movingX) +(getdxTwo()*movingY);
    }
    public double getyTwo()
    {
        if(!inTransit)
        {
            return WorldPanel.worldY+(getdyOne()*x) + (getdyTwo()*y);
        }
        return WorldPanel.worldY+(getdyOne()*movingX) + (getdyTwo()*movingY);
    }
    private double getxFour()
    {
        if(!inTransit)
        {
            return WorldPanel.worldX+(getdxOne()*(x+width)) +(getdxTwo()*(y+length));
        }
        return WorldPanel.worldX+(getdxOne()*(movingX+width)) +(getdxTwo()*(movingY+length));
    }
    private double getyFour()
    {
        if(!inTransit)
        {
        return WorldPanel.worldY+(getdyOne()*(x+width)) + (getdyTwo()*(y+length));
        }
        return WorldPanel.worldY+(getdyOne()*(movingX+width)) + (getdyTwo()*(movingY+length));
    }
    private double getX()
    {
        if(!inTransit)
        {
            return WorldPanel.worldX+(getdxOne()*(x+width)) +(getdxTwo()*y);
        }
        return WorldPanel.worldX+(getdxOne()*(movingX+width)) +(getdxTwo()*movingY);
    }
    private double getY()
    {   
        if(!inTransit)
        {
            return WorldPanel.worldY+(getdyOne()*(x+width)) + (getdyTwo()*y);
        }
        return WorldPanel.worldY+(getdyOne()*(movingX+width)) + (getdyTwo()*movingY);
    }
    private double getxThree()
    {
        if(!inTransit)
        {
            return WorldPanel.worldX+(getdxOne()*(x)) +(getdxTwo()*(y+length));
        }
        return WorldPanel.worldX+(getdxOne()*(movingX)) +(getdxTwo()*(movingY+length));
    }
    private double getyThree()
    {
        if(!inTransit)
        {
            return WorldPanel.worldY+(getdyOne()*(x)) + (getdyTwo()*(y+length));
        }
        return WorldPanel.worldY+(getdyOne()*(movingX)) + (getdyTwo()*(movingY+length));
    }
    
    
    
    
    public double[][] getPointsDouble()
    {
        double[][] d = {{getX(),getxTwo(),getxThree(),getxFour()},{getY(),getyTwo(),getyThree(),getyFour()}};
        return d;
    }
    public int[] getPoint(int point)
    {
        int[] giveReturn = {getPoints()[0][point], getPoints()[1][point]};
        return giveReturn;
    }
    
    public int getCenterX()
    {
        return (int)convertToPoint(getRawX() + (getRawWidth()/2.0), getRawY() + (getRawLength()/2.0))[0];
        
    }
    
    public int getCenterY()
    {
        return (int)convertToPoint(getRawX() + (getRawWidth()/2.0), getRawY() + (getRawLength()/2.0))[1];
    }
    
    public int[][] getPoints()
    {
        int[][] i = {{(int)getX(),(int)getxTwo(),(int)getxThree(),(int)getxFour()},{(int)getY(),(int)getyTwo(),(int)getyThree(),(int)getyFour()}};
        return i;
    }
    public int getTopLeftIndex()
    {
        if(WorldPanel.spinQuadrant() == 3)
        {
            return 0;
        }else if(WorldPanel.spinQuadrant() == 4)
        {
            return 1;
        }
        return WorldPanel.spinQuadrant() + 1;
        
    }
    public int getTopRightIndex()
    {
        if(getTopLeftIndex() != 3)
        {
            return getTopLeftIndex() + 1;
        }
        return 0;
    }
    public int getBottomRightIndex()
    {
        if(getTopRightIndex() != 3)
        {
            return getTopRightIndex() + 1;
        }
        return 0;
    }
    public int getBottomLeftIndex()
    {
        if(getBottomRightIndex() != 3)
        {
            return getBottomRightIndex() + 1;
        }
        return 0;
    }
    
    public int getHeight(){return (int)height;}
    public void addHeight(double d){height += d;}
    public void setHeight(double i){height = i;}
    public void addTimeWalkedOn(){walkedOn ++;}
    public int getTimesWalkedOn(){return walkedOn;}
    public abstract void draw(Graphics g);
    public int[][] threadedUpperPoints(){return myThreadedUpperPoints;}
    public int[][] threadedPoints(){return myThreadedLowerPoints;}
    public int[][] getPolyPoints1(){return polyPoints1;}
    public int[][] getPolyPoints2(){return polyPoints2;}
    public int[] lowestHighestPoint()
    {
        int highest = myThreadedUpperPoints[1][0];
        int lowest = myThreadedUpperPoints[1][0];
        for(int yPoint : myThreadedUpperPoints[1])
        {
            if(yPoint > highest)
            {
                highest = yPoint;
            }
            if(yPoint < lowest)
            {
                lowest = yPoint;
            }
        }
        int[] giveReturn = {lowest,highest};
        return giveReturn;
    }
    public int[] leftestRightestPoint()
    {
        int leftest = myThreadedUpperPoints[0][0];
        int rightest = myThreadedUpperPoints[0][0];
        for(int xPoint : myThreadedUpperPoints[0])
        {
            if(xPoint > rightest)
            {
                rightest = xPoint;
            }
            if(xPoint < leftest)
            {
                leftest = xPoint;
            }
        }
        int[] giveReturn = {leftest, rightest};
        return giveReturn;
    }
    
    
    
    public Tile[] getAdjacentTiles()//gives an array of all the tiles adjacent to this one. If none, return null;
    {
        ArrayList<Tile> nearTiles = new ArrayList<Tile>();
        for(int i = 0; i < TileDrawer.tileList.size(); i++)
        {
            Tile currentTile = TileDrawer.tileList.get(i);
            if(currentTile.getRawX() + currentTile.getRawWidth() == x || currentTile.getRawX() == x + width || currentTile.getRawY() == y + length || currentTile.getRawY() + currentTile.getRawLength() == y)
            {
                nearTiles.add(currentTile);
            }
        }
        if(nearTiles.size() == 0)
        {
            return null;
        }
        Tile[] giveReturn = new Tile[nearTiles.size()];
        for(int i = 0; i < giveReturn.length; i++)
        {
            giveReturn[i] = nearTiles.get(i);
        }
        return giveReturn;
    }
    
    
    public boolean tileContainsPoint(int x, int y)
    {
        int[][] p1 = getPolyPoints1();
        int[][] p2 = getPolyPoints2();
        return (new Polygon(myThreadedUpperPoints[0], myThreadedUpperPoints[1], 4).contains(x, y) || new Polygon(p1[0], p1[1], 4).contains(x, y) || new Polygon(p2[0], p2[1], 4).contains(x, y));
    }
    
    public boolean tileClicked()
    {
        if(tileContainsPoint(MouseInput.x, MouseInput.y)  && !Player.isClicked && !Player.inTransit && MouseInput.clicked && !clickBuffer && !Player.pathIsClicked)
        {
            clickBuffer = true;
            return true;
        }
        clickBuffer = false;
        return false;
    }
    public ArrayList<Scenery> getAssortedScenery(){return assortedScenery;}
    public boolean getInTransit(){return inTransit;}
    public boolean getClicked(){return thisClicked;}
    public void setInTransit(boolean b){inTransit = b;}
    public void setClicked(boolean b){thisClicked = b;}
    public void setHeightChangeable(boolean b){heightChangeable = b;}
    private boolean noOtherTilesClicked()
    {
        for(int i = 0; i < TileDrawer.tileList.size(); i++)
        {
            if(TileDrawer.tileList.get(i).getClicked())
            {
                return false;
            }
        }
        return true;
    }
    private void unclickAllTiles()
    {
        for(int i = 0; i < TileDrawer.tileList.size(); i++)
        {
            if(TileDrawer.tileList.get(i) != this)
            {
                TileDrawer.tileList.get(i).setClicked(false);
                TileDrawer.tileList.get(i).setInTransit(false);
            }
        }
    }
    
    public static void unclickEveryTile()
    {
        for(int i = 0; i < TileDrawer.tileList.size(); i++)
        {
            TileDrawer.tileList.get(i).setClicked(false);
            TileDrawer.tileList.get(i).setInTransit(false);
            
        }
    }
    
    private void sortAssortedScenery()
    {
        for(int i = 0; i < assortedScenery.size(); i++)
        {
            int smallestIndex = i;
            for(int j = i; j < assortedScenery.size(); j++)
            {
                if(assortedScenery.get(j).getSortDistanceConstant() > assortedScenery.get(smallestIndex).getSortDistanceConstant())
                {
                    smallestIndex = j;
                }
            }
            Scenery tempScenery = assortedScenery.get(i);
            assortedScenery.set(i, assortedScenery.get(smallestIndex));
            assortedScenery.set(smallestIndex, tempScenery);
        }
    }
    private void sortTrees()
    {
        for(int i = 0; i < trees.size(); i++)
        {
            int smallestIndex = i;
            for(int j = i; j < trees.size(); j++)
            {
                if(trees.get(j).getY() < trees.get(smallestIndex).getY())
                {
                    smallestIndex = j;
                }
            }
            Tree tempTree = trees.get(i);
            trees.set(i, trees.get(smallestIndex));
            trees.set(smallestIndex, tempTree);
        }
    }
    public void mouseInteraction()
    {
        if(!thisClicked)
        {
            color = Toolbox.grassColor;
        }
        
        if(!thisClicked && tileClicked())
        {
            if(!tileCurrentlyMoving)
            {
                //unclickAllTiles();
            }
            
            thisClicked = true;
            color = Color.RED;
            System.out.println("Was clicked");
            //MouseInput.clicked = false;
            //MouseInput.rightClicked = false;
            if(heightChangeable)
            {
                MouseInput.scrollType = "Height";
            }
        }else if(thisClicked && tileClicked())
        {
            tileJustUnclicked = true;
            thisClicked = false;
            if(heightChangeable)
            {
                MouseInput.scrollType = "Zoom";
            }
            color = Toolbox.grassColor;
            //MouseInput.clicked = false;
            //MouseInput.rightClicked = false;
        }else if(!tileClicked() && MouseInput.clicked && thisClicked && !inTransit)
        {
            if(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] >= x && convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] <= x+width)//(WorldPanel.getMouseUnitPos()[0] >= x && WorldPanel.getMouseUnitPos()[0] <= x+width)//if x is a straight line
            {
                //System.out.println("hi");
                int endPos;
                if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]) > y)
                {
                    //endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] - ((double)length/2.0));
                    endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] - length  + 1);//WorldPanel.getMouseUnitPos()[1]-length;

                }else{
                    endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]);//WorldPanel.getMouseUnitPos()[1]-length;
                }
                if(pathIsClear((int)x, (int)y, (int)x, endPos) )//&& !MergedBlockTiles.threadedArea.contains(MouseInput.x, MouseInput.y))
                {
                    oldPos[0]=x;
                    oldPos[1]=y;
                    transitPos[0]=(int)x;
                    movingX = x;
                    movingY = y;
                    transitPos[1] = endPos;
                    /*if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]) > y)
                    {
                        transitPos[1]=endPos;//(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] - ((double)length/2.0));
                    }else{
                        transitPos[1]=endPos;//(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]);//WorldPanel.getMouseUnitPos()[1]-length;
                    }*/
                    inTransit = true;
                    
                    //MouseInput.clicked = false;
                    //MouseInput.rightClicked = false;
                }else{
                    thisClicked = false;
                    tileJustUnclicked = true;
                    if(heightChangeable)
                    {
                        MouseInput.scrollType = "Zoom";
                    }
                    //MouseInput.clicked = false;
                    //MouseInput.rightClicked = false;
                    color = Toolbox.grassColor;
                }
            }else if(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] >= y && convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] <= y+length)//(WorldPanel.getMouseUnitPos()[1] >= y && WorldPanel.getMouseUnitPos()[1] <= y+length)
            {
                int endPos;
                if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0])>x)
                {
                    //endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] - ((double)width/2.0));
                    endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] - width + 1);//WorldPanel.getMouseUnitPos()[1]-length;

                }else{
                    endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0]);//WorldPanel.getMouseUnitPos()[1]-length;
                }
                if(pathIsClear((int)x, (int)y, endPos, (int)y) )//&& !MergedBlockTiles.threadedArea.contains(MouseInput.x, MouseInput.y))
                {
                    oldPos[0]=x;
                    oldPos[1]=y;
                    movingX = x;
                    movingY = y;
                    transitPos[0]=endPos;
                    /*if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0])>x)
                    {
                        transitPos[0]=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] - ((double)width/2.0));
                        
                    }else{
                        transitPos[0]=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0]);//WorldPanel.getMouseUnitPos()[1]-length;
                    }*/
                    transitPos[1]=(int)y;
                    
                    inTransit = true;
                    
                    //MouseInput.clicked = false;
                    //MouseInput.rightClicked = false;
                }else{
                    thisClicked = false;
                    tileJustUnclicked = true;
                    if(heightChangeable)
                    {
                        MouseInput.scrollType = "Zoom";
                    }
                    //MouseInput.clicked = false;
                    //MouseInput.rightClicked = false;
                    color = Toolbox.grassColor;
                }
            }
        }
        
    }
    
    public boolean pathIsClear(int startX, int startY, int endX, int endY)
    {
        
        System.out.println("Start X: " + startX + " Start Y: " + startY + " End X: " + endX + " End Y: " + endY);
        if(startX == endX)//y is moving
        {
            if(endY < startY)
            {
                int[]xPoints = { (int)convertToPoint(startX + 0.25, startY)[0], (int)(convertToPoint(startX + width - 0.25, startY))[0], (int)(convertToPoint(endX + width - 0.25, endY + 0.25)[0]), (int)(convertToPoint(endX + 0.25, endY + 0.25)[0])};
                int[]yPoints = { (int)convertToPoint(startX + 0.25, startY)[1], (int)(convertToPoint(startX + width - 0.25, startY))[1], (int)(convertToPoint(endX + width - 0.25, endY + 0.25)[1]), (int)(convertToPoint(endX + 0.25, endY + 0.25)[1])};
                hitPolygon = new Polygon(xPoints, yPoints, 4);
                for(int i = 0; i < TileDrawer2.tileList.size(); i++)
                {
                    if(TileDrawer2.tileList.get(i)!=this)
                    {
                        Area area1 = new Area(TileDrawer2.tileList.get(i).getLowerPolygon());
                        //Area area2 = new Area(hitPolygon);
                        area1.intersect(new Area(hitPolygon));
                        if(!area1.isEmpty())
                        {
                            return false;
                        }
                        /*for(int j = 0; j < 4; j++)
                        {
                            if(TileDrawer2.tileList.get(i).getLowerPolygon().contains(xPoints[j],yPoints[j]))
                            {
                                System.out.println("false");
                                return false;
                            }
                            if(MergedBlockTiles.threadedArea.contains(xPoints[j], yPoints[j]))
                            {
                                System.out.println("false");
                                return false;
                            }
                        }*/
                    }
                }
            }else{
                //int[]xPoints = { (int)convertToPoint(startX + 0.25, startY)[0], (int)(convertToPoint(startX + width - 0.25, startY))[0], (int)(convertToPoint(endX + width - 0.25, endY + 0.25)[0]), (int)(convertToPoint(endX + 0.25, endY + 0.25)[0])};
                //int[]yPoints = { (int)convertToPoint(startX + 0.25, startY)[1], (int)(convertToPoint(startX + width - 0.25, startY))[1], (int)(convertToPoint(endX + width - 0.25, endY + 0.25)[1]), (int)(convertToPoint(endX + 0.25, endY + 0.25)[1])};
                int[]xPoints = { (int)convertToPoint(startX + 0.25, startY + length)[0], (int)(convertToPoint(startX + width - 0.25, startY + length))[0], (int)(convertToPoint(endX + width - 0.25, endY - 0.25 + length)[0]), (int)(convertToPoint(endX + 0.25, endY - 0.25 + length)[0])};
                int[]yPoints = { (int)convertToPoint(startX + 0.25, startY + length)[1], (int)(convertToPoint(startX + width - 0.25, startY + length))[1], (int)(convertToPoint(endX + width - 0.25, endY - 0.25 + length)[1]), (int)(convertToPoint(endX + 0.25, endY - 0.25 + length)[1])};
                hitPolygon = new Polygon(xPoints, yPoints, 4);
                for(int i = 0; i < TileDrawer2.tileList.size(); i++)
                {
                    if(TileDrawer2.tileList.get(i)!=this)
                    {
                        Area area1 = new Area(TileDrawer2.tileList.get(i).getLowerPolygon());
                        //Area area2 = new Area(hitPolygon);
                        area1.intersect(new Area(hitPolygon));
                        if(!area1.isEmpty())
                        {
                            return false;
                        }
                        //return area1.isEmpty();
                        /*for(int j = 0; j < 4; j++)
                        {
                            if(TileDrawer2.tileList.get(i).getLowerPolygon().contains(xPoints[j],yPoints[j]))
                            {
                                System.out.println("false");
                                return false;
                            }
                            if(MergedBlockTiles.threadedArea.contains(xPoints[j], yPoints[j]))
                            {
                                System.out.println("false");
                                return false;
                            }
                        }*/
                    }
                }
            }
            
        }else//x is moving
        {
            /*int[]xPoints = { (int)convertToPoint(startX + 0.5, startY)[0], (int)(convertToPoint(startX + width - 0.5, startY))[0], (int)(convertToPoint(endX + width - 0.5, endY)[0]), (int)(convertToPoint(endX + 0.5, endY)[0])};
            int[]yPoints = { (int)convertToPoint(startX + 0.5, startY)[1], (int)(convertToPoint(startX + width - 0.5, startY))[1], (int)(convertToPoint(endX + width - 0.5, endY)[1]), (int)(convertToPoint(endX + 0.5, endY)[1])};
            //Polygon hitPolygon = new Polygon(xPoints, yPoints, 4);
            for(int i = 0; i < TileDrawer.tileList.size(); i++)
            {
                if(TileDrawer.tileList.get(i)!=this)
                {
                    for(int j = 0; j < 4; j++)
                    {
                        if(TileDrawer.tileList.get(i).getLowerPolygon().contains(xPoints[j],yPoints[j]))
                        {
                            return false;
                        }
                    }
                }
            }*/
            if(endX < startX)
            {
                int[] xPoints = {(int)convertToPoint(startX, startY + 0.25)[0], (int)convertToPoint(startX, startY + length - 0.25)[0], (int)convertToPoint(endX + 0.25, endY + length - 0.25)[0], (int)convertToPoint(endX + 0.25, endY + 0.25)[0]};
                int[] yPoints = {(int)convertToPoint(startX, startY + 0.25)[1], (int)convertToPoint(startX, startY + length - 0.25)[1], (int)convertToPoint(endX + 0.25, endY + length - 0.25)[1], (int)convertToPoint(endX + 0.25, endY + 0.25)[1]};
                hitPolygon = new Polygon(xPoints, yPoints, 4);
                for(int i = 0; i < TileDrawer2.tileList.size(); i++)
                {
                    if(TileDrawer2.tileList.get(i)!=this)
                    {
                        Area area1 = new Area(TileDrawer2.tileList.get(i).getLowerPolygon());
                        //Area area2 = new Area(hitPolygon);
                        area1.intersect(new Area(hitPolygon));
                        if(!area1.isEmpty())
                        {
                            return false;
                        }
                        /*for(int j = 0; j < 4; j++)
                        {
                            if(TileDrawer2.tileList.get(i).getLowerPolygon().contains(xPoints[j],yPoints[j]))
                            {
                                return false;
                            }
                            if(MergedBlockTiles.threadedArea.contains(xPoints[j], yPoints[j]))
                            {
                                return false;
                            }
                        }*/
                    }
                }
            }else{
                
                int[] xPoints = {(int)convertToPoint(startX + width, startY + 0.25)[0], (int)convertToPoint(startX + width, startY + length - 0.25)[0], (int)convertToPoint(endX + width - 0.25, endY + length - 0.25)[0], (int)convertToPoint(endX + width - 0.25, endY + 0.25)[0]};
                int[] yPoints = {(int)convertToPoint(startX + width, startY + 0.25)[1], (int)convertToPoint(startX + width, startY + length - 0.25)[1], (int)convertToPoint(endX + width - 0.25, endY + length - 0.25)[1], (int)convertToPoint(endX + width - 0.25, endY + 0.25)[1]};
                hitPolygon = new Polygon(xPoints, yPoints, 4);
                for(int i = 0; i < TileDrawer2.tileList.size(); i++)
                {
                    if(TileDrawer2.tileList.get(i)!=this)
                    {
                        Area area1 = new Area(TileDrawer2.tileList.get(i).getLowerPolygon());
                        //Area area2 = new Area(hitPolygon);
                        area1.intersect(new Area(hitPolygon));
                        if(!area1.isEmpty())
                        {
                            return false;
                        }
                        /*for(int j = 0; j < 4; j++)
                        {
                            if(TileDrawer2.tileList.get(i).getLowerPolygon().contains(xPoints[j],yPoints[j]))
                            {
                                return false;
                            }
                            if(MergedBlockTiles.threadedArea.contains(xPoints[j], yPoints[j]))
                            {
                                return false;
                            }
                        }*/
                    }
                }
            }
        }
        
        return true;
    }
    
    
    public int getArea()
    {
        return width*length;
    }
    
    
    
    
    
    
    public boolean tileAtCoord(double xIn, double yIn)
    {
        for(int i = 0; i < TileDrawer.tileList.size(); i++)
        {
            Tile currentTile = TileDrawer.tileList.get(i);
            if(xIn >= currentTile.getRawX() && xIn <= currentTile.getRawX() + currentTile.getRawWidth() && yIn >= currentTile.getRawY() && yIn <= currentTile.getRawY() + currentTile.getRawLength() && currentTile != this)//!= this so that a tile can't be blocking its own path
            {
                return true;
            }
        }
        return false;
    }
    
    private void transitMovement()
    {
        if(inTransit)
        {
            //x=transitPos[0];
            //y=transitPos[1];
            double oldX = x;
            double oldY = y;
            tileCurrentlyMoving = true;
            tileCurrentlyMovingDebounce = true;
            double ticksToMove = (10*Math.sqrt(Math.pow(transitPos[0]-oldPos[0],2) + Math.pow(transitPos[1]-oldPos[1],2)));
            double dx = transitPos[0]-oldPos[0];
            double dy = transitPos[1]-oldPos[1];
            //System.out.println("oldPos[0]: " + oldPos[0] + " oldPos[1]: "+ oldPos[1]    );
            thisdx = dx/(double)ticksToMove;
            thisdy = dy/(double)ticksToMove;
            if(transitTicksMoved <= ticksToMove)
            {
                movingX += dx/ticksToMove;
                movingY += dy/ticksToMove;
                if(dx != 0)
                {
                    x = (int)Math.round(movingX + (dx%(Math.abs(dx))));//transitPos[0];
                }
                if(dy != 0)
                {
                    y = (int)Math.round(movingY + (dy%Math.abs(dy)));//transitPos[1];
                }
                if(dx > 0 && x > transitPos[0])
                {
                    x=transitPos[0];
                }else if(dx < 0 && x < transitPos[0])
                {
                    x=transitPos[0];
                }
                
                if(dy > 0 && y > transitPos[1])
                {
                    y=transitPos[1];
                }else if(dy < 0 && y  < transitPos[1])
                {
                    y=transitPos[1];
                }
                //x += dx/ticksToMove;
                //y += dy/ticksToMove;
                transitTicksMoved++;
                if(oldX != x || oldY != y)
                {
                    resortTiles = true;
                }
            }else{
                MouseInput.clicked = false;
                color = Toolbox.grassColor;
                x=transitPos[0];
                y=transitPos[1];
                thisClicked = false;
                tileJustUnclicked = true;
                if(heightChangeable)
                {
                    MouseInput.scrollType = "Zoom";
                }
                inTransit = false;
                resortTilesDebounce = true;
                //resortTiles = true;
                hitPolygon = null;
                transitTicksMoved = 0;
                //PathChains.thread = new Thread(new PathChain)
                oldPos[0]=x;
                oldPos[1]=y;
            }
        }else{
            if(resortTilesDebounce)
            {
                resortTiles = true;
                resortTilesDebounce = false;
            }
            
            if(tileCurrentlyMovingDebounce)
            {
                tileCurrentlyMoving = false;
                tileCurrentlyMovingDebounce = false;
            }
            thisdx = 0; thisdy =0;
            hitPolygon = null;
        }
    }
    
    public void drawWaterReflectionsWithColor(Graphics g, Color c)
    {
        
        Graphics2D g2 = (Graphics2D)g;
        Composite originalComposite = g2.getComposite();
        int type = AlphaComposite.SRC_OVER;
        
        AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0))));
        
        
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
        int[][] points2 = new int[2][4];getRightSidePoints().clone();
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[0][i] = getLeftSidePoints()[0][i];
            points1[1][i]+= getLeftSidePoints()[1][i] + getScaledDistortedHeight();
            
            
            
            points2[0][i] = getRightSidePoints()[0][i];
            points2[1][i]+= getRightSidePoints()[1][i] + getScaledDistortedHeight();
        }
        g.setColor(c);
        g.fillPolygon(points1[0], points1[1], points1[0].length);
        g.fillPolygon(points2[0], points2[1], points2[0].length);
        g2.setPaint(WorldPanel.grassTexture);
        g2.setComposite(transparencyComposite);
        //points1[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points1[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        //points2[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points2[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        g.fillPolygon(points1[0], points1[1],points1[0].length);
        g2.setComposite(AlphaComposite.getInstance(type, (float)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
        g.fillPolygon(points2[0], points2[1], points2[0].length);
        g2.setComposite(originalComposite);
        
        g.setColor(Color.BLACK);
        g.drawString(Boolean.toString(tileCurrentlyMoving), (int)getX(), (int)getY());
        
    }
    
    public void drawWaterReflections(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        Composite originalComposite = g2.getComposite();
        int type = AlphaComposite.SRC_OVER;
        
        AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0))));
        g2.setComposite(transparencyComposite);
        g2.setPaint(WorldPanel.grassTexture);
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
        int[][] points2 = new int[2][4];getRightSidePoints().clone();
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[0][i] = getLeftSidePoints()[0][i];
            points1[1][i]+= getLeftSidePoints()[1][i] + getScaledDistortedHeight();
            
            
            
            points2[0][i] = getRightSidePoints()[0][i];
            points2[1][i]+= getRightSidePoints()[1][i] + getScaledDistortedHeight();
        }
        
        
        //points1[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points1[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        //points2[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points2[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        g.fillPolygon(points1[0], points1[1],points1[0].length);
        g2.setComposite(AlphaComposite.getInstance(type, (float)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
        g.fillPolygon(points2[0], points2[1], points2[0].length);
        g2.setComposite(originalComposite);
    }
    
    public void shadeSides(Graphics g)
    {
        int leftAlpha = 80-(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        g.setColor(new Color(0,0,0,leftAlpha));
        g.fillPolygon(getLeftSidePoints()[0], getLeftSidePoints()[1], 4);
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        int rightAlpha = 50-(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        g.setColor(new Color(0,0,0,rightAlpha));
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(getRightSidePoints()[0], getRightSidePoints()[1], 4);
        g.setColor(Color.BLACK);
    }
    
    public void drawWaterReflectionCover(Graphics g)
    {
        if(!tileCurrentlyMoving)
        {
            /*Graphics2D g2 = (Graphics2D)g;
            Composite originalComposite = g2.getComposite();
            int type = AlphaComposite.SRC_OVER;
            AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, 0.65f);
            g2.setComposite(transparencyComposite);
            g2.setPaint(WorldPanel.grassTexture);*/
            int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
            int[][] points2 = new int[2][4];//getRightSidePoints().clone();
            for(int i = 0; i < points1[0].length; i++)
            {
                points1[0][i] = getLeftSidePoints()[0][i];
                points1[1][i]+= getLeftSidePoints()[1][i] + getScaledDistortedHeight();



                points2[0][i] = getRightSidePoints()[0][i];
                points2[1][i]+= getRightSidePoints()[1][i] + getScaledDistortedHeight();
            }


            points1[1][2] = WorldPanel.screenHeight;
            points1[1][3] = WorldPanel.screenHeight;

            points2[1][2] = WorldPanel.screenHeight;
            points2[1][3] = WorldPanel.screenHeight;
            g.setColor(new Color(30, 144, 255));
            g.fillPolygon(points1[0], points1[1],points1[0].length);
            //g2.setComposite(AlphaComposite.getInstance(type, 0.57f));
            g.fillPolygon(points2[0], points2[1], points2[0].length);
            //g2.setComposite(originalComposite);
        }
    }
    
    public Area getLeftReflectionArea()
    {
        //int[][] points1 = new int[2][4];//.clone() not working for some reason??
        //int[][] points2 = new int[2][4];//.clone() not working for some reason??
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
        for(int i = 0; i < getPolyPoints1()[0].length; i++)
        {
            points1[0][i] = getLeftSidePoints()[0][i];
            points1[1][i] = getLeftSidePoints()[1][i];
            //points2[0][i] = getPolyPoints2()[0][i];
            //points2[1][i] = getPolyPoints2()[1][i];
        }
        //int[][] points1 = getPolyPoints1().clone();
        //int[][] points2 = getPolyPoints2().clone();
        
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[1][i]+=(int)(scaledDistortedHeight((int)height));
            
        }
        Area a = new Area(new Polygon(points1[0], points1[1], points1[0].length));
        //a.add(new Area(new Polygon(points2[0], points2[1], points2[0].length)));
        return a;
    }
    
    public Area getRightReflectionArea()
    {
        //int[][] points1 = new int[2][4];//.clone() not working for some reason??
        //int[][] points2 = new int[2][4];//.clone() not working for some reason??
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
        for(int i = 0; i < getPolyPoints1()[0].length; i++)
        {
            points1[0][i] = getRightSidePoints()[0][i];
            points1[1][i] = getRightSidePoints()[1][i];
            //points2[0][i] = getPolyPoints2()[0][i];
            //points2[1][i] = getPolyPoints2()[1][i];
        }
        //int[][] points1 = getPolyPoints1().clone();
        //int[][] points2 = getPolyPoints2().clone();
        
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[1][i]+=(int)(scaledDistortedHeight((int)height));
            
        }
        Area a = new Area(new Polygon(points1[0], points1[1], points1[0].length));
        //a.add(new Area(new Polygon(points2[0], points2[1], points2[0].length)));
        return a;
    }
    
    public double getdx(){return thisdx;}
    public double getdy(){return thisdy;}
    
    private void calculatePolyPoints()
    {
        if(WorldPanel.spinQuadrant() == 1)
        {
            int [][] tempPoints1 = {{myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][1]},{myThreadedUpperPoints[1][1], myThreadedUpperPoints[1][2], threadedPoints()[1][2],threadedPoints()[1][1]}};
            int [][] tempPoints2 = {{myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][0]},{myThreadedUpperPoints[1][0], myThreadedUpperPoints[1][1], threadedPoints()[1][1],threadedPoints()[1][0]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }else if(WorldPanel.spinQuadrant() == 2){
            int[][] tempPoints1 = {{myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][1]},{myThreadedUpperPoints[1][1], myThreadedUpperPoints[1][2], threadedPoints()[1][2],threadedPoints()[1][1]}};
            int[][] tempPoints2 = {{myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][2]},{myThreadedUpperPoints[1][2], myThreadedUpperPoints[1][3], threadedPoints()[1][3],threadedPoints()[1][2]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }else if(WorldPanel.spinQuadrant() == 3){
            int[][] tempPoints1 = {{myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][3]},{myThreadedUpperPoints[1][3], myThreadedUpperPoints[1][0], threadedPoints()[1][0],threadedPoints()[1][3]}};
            int[][] tempPoints2 = {{myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][2]},{myThreadedUpperPoints[1][2], myThreadedUpperPoints[1][3], threadedPoints()[1][3],threadedPoints()[1][2]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }else{
            int[][] tempPoints1= {{myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][3]},{myThreadedUpperPoints[1][3], myThreadedUpperPoints[1][0], threadedPoints()[1][0],threadedPoints()[1][3]}};
            int[][] tempPoints2 = {{myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][0]},{myThreadedUpperPoints[1][0], myThreadedUpperPoints[1][1], threadedPoints()[1][1],threadedPoints()[1][0]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }
    }
    
   
    private void spinMouseInteraction()
    {
        if(thisClicked)
        {
            color = Color.RED;
            double x1 = (WorldPanel.getMouseUnitPosDouble()[0])-(x + (width/2.0));
            double y1 = (WorldPanel.getMouseUnitPosDouble()[1])-(y + (length/2.0));
            //spin = (Math.atan2(y1, x1))-initialClickSpin;// + ;//(Math.atan2(y1, x1))-initialClickSpin;
        }else{
            color = Toolbox.grassColor;
        }
        if(tileContainsPoint(MouseInput.x, MouseInput.y)&& MouseInput.clicked && !thisClicked && !inTransit && !Player.pathIsClicked && Player.boundTile != this)
        {
            thisClicked = true;
            
            
            rightClicked = false;
            initialClickSpin = spin;//Math.atan2(convertToUnit(x + (width/2), y + (length/2))[1] - convertToUnit(MouseInput.x, MouseInput.y)[1], convertToUnit(x + (width/2), y + (length/2))[0] - convertToUnit(MouseInput.x, MouseInput.y)[0])-spin;
            inTransit = true;
            
            if(heightChangeable)
            {
                MouseInput.scrollType = "Height";
            }
            MouseInput.clicked = false;
            MouseInput.rightClicked = false;
            System.out.println("Was clicked");
        }else if(tileContainsPoint(MouseInput.x, MouseInput.y) && MouseInput.rightClicked && !thisClicked && !inTransit && Player.boundTile != this)
        {
            
            if(heightChangeable)
            {
                MouseInput.scrollType = "Zoom";
            }
            /*if(heightChangeable)
            {
                MouseInput.scrollType = "Zoom";
            }*/
            thisClicked = false;
            tileJustUnclicked = true;
            if(heightChangeable)
            {
                MouseInput.scrollType = "Zoom";
            }
            rightClicked = true;
            initialClickSpin = spin;//Math.atan2(convertToUnit(x + (width/2), y + (length/2))[1] - convertToUnit(MouseInput.x, MouseInput.y)[1], convertToUnit(x + (width/2), y + (length/2))[0] - convertToUnit(MouseInput.x, MouseInput.y)[0])-spin;
            inTransit = true;
            thisClicked = true;
            if(heightChangeable)
            {
                MouseInput.scrollType = "Height";
            }
            MouseInput.clicked = false;
            MouseInput.rightClicked = false;
            System.out.println("Was clicked");
        }
        /*else if(thisClicked && MouseInput.clicked)
        {
            thisClicked = false;
            spin = (Math.PI/2.0)*Math.round(spin/(Math.PI/2.0));
            MouseInput.clicked = false;
        }*/
        
    }
    
    public int[] getBottomCornerCoordinates()
    {
        int quad = WorldPanel.spinQuadrant();
        if(quad == 1)
        {
            int[] giveReturn = {(int)x, (int)y};
            return giveReturn;
        }else if(quad == 2)
        {
            int[] giveReturn = {(int)x, (int)y+length};
            return giveReturn;
        }else if(quad == 3)
        {
            int[] giveReturn = {(int)x+width, (int)y+length};
            return giveReturn;
        }else{
            int[] giveReturn = {(int)x+width, (int)y};
            return giveReturn;
        }
    }
    
    
    public int getBottomCornerConstant()//y = mx + b ... b = y-mx
    {
        int[] coords = getBottomCornerCoordinates();
        return coords[1] - (WorldPanel.getSortSlope() * coords[0]);
    }
    
    public int getBottomCornerOrderPos()
    {
        int quad = WorldPanel.spinQuadrant();
        if(quad == 1 || quad == 4)
        {
            return -getBottomCornerConstant();
        }else{
            return getBottomCornerConstant();
        }
    }
    
    public int getSideCornerOrderPos()
    {
        if(width == length)
        {
            return getBottomCornerOrderPos()-width;
        }else{
            if(width > length)
            {
                return getBottomCornerOrderPos()-width;
            }else{
                return getBottomCornerOrderPos()-length;
            }
        }
    }
    
    private void spinAnimation()
    {
        
        if(!inTransit || !thisClicked)
        {
            spin = Math.round(spin/(Math.PI/2.0)) * (Math.PI/2.0);
        }
        
        int ticksToMovePer90 = 20;
        
        double addAmount = (Math.PI/2.0)/ticksToMovePer90;
        if(inTransit && transitTicksMoved < ticksToMovePer90)
        {
            tileCurrentlyMoving = true;
            tileCurrentlyMovingDebounce = true;
            transitTicksMoved++;
            if(rightClicked)
            {
                spin += addAmount;
            }else if(thisClicked){
                spin -= addAmount;
            }
        }else{
            if(tileCurrentlyMovingDebounce)
            {
                tileCurrentlyMoving = false;
                tileCurrentlyMovingDebounce = false;
                thisClicked = false;
                tileJustUnclicked = true;
                inTransit = false;
                transitTicksMoved = 0;
                //System.out.println(spin);
            }
            
            
            if(heightChangeable)
            {
                MouseInput.scrollType = "Zoom";
            }   
        }
        
        
    }
    
    private void selectClosestOfClickedTiles()//used if multiple tiles are clicked with one click due to how they are lined up. 
    {
        boolean closestClickedFound = false;
        for(int i = TileDrawer2.tileList.size() - 1; i > 0; i--)
        {
            if(!closestClickedFound && TileDrawer2.tileList.get(i).getClicked())
            {
                closestClickedFound = true;
            }else if(closestClickedFound && TileDrawer2.tileList.get(i).getClicked())
            {
                TileDrawer2.tileList.get(i).setClicked(false);
            }
        }
    }
    
    private int numClickedTiles()
    {
        int giveReturn = 0;
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(TileDrawer2.tileList.get(i).getClicked())
            {
                giveReturn ++;
            }
        }
        return giveReturn;
    }
    
    
    
    @Override
    public void run()//see if I can relocate handling clicking to here
    {
        //height = baseHeight;//(int)(WorldPanel.scale*baseHeight);
        myThreadedLowerPoints = getPoints().clone();
        myThreadedUpperPoints = getUpperPoints().clone();
        if(numClickedTiles() > 1)
        {
            selectClosestOfClickedTiles();
        }
        sortAssortedScenery();
        //sortTrees();
        calculatePolyPoints();
        threadedTilePolygon = new Polygon(myThreadedLowerPoints[0], myThreadedLowerPoints[1],4);
        if(moveable)
        {
            if(!tileCurrentlyMoving)
            {
                mouseInteraction();
            }
            transitMovement();
        }else if(spinnable)
        {
            if(!tileCurrentlyMoving)
            {
                spinMouseInteraction();
            }
            spinAnimation();
        }
        
    }
}
