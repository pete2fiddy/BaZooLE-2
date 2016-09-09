package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Player extends Toolbox implements Runnable
{
    private boolean followMovingPlayer = false;
    public static final int ticksToMovePerUnit = 50;
    private int counterSinceTurned = 5000;//arbitrarily high number
    public static Tile boundTile;
    public static Thread thread;
    private final int unscaledPlayerRadius = 5;
    private int playerRadius = unscaledPlayerRadius;
    private double x, y, height, unscaledHeight;
    private double spin, radius, threadedOffsetTheta;
    public static boolean inTransit = false;
    public static boolean isClicked = false;
    private int[][] squarePoints = new int[2][4];
    //private PathChains pathChains = new PathChains(this);
    private PathChains pathChains = new PathChains();
    private PathChain playersChain;
    public static boolean pathIsClicked = false;
    private ArrayList<Double> directionsX = new ArrayList<Double>();
    private ArrayList<Double> directionsY = new ArrayList<Double>();
    private ArrayList<Integer> directionsHeight = new ArrayList<Integer>();
    private double fireAnimationCount = 0;
    private BufferedImage[] flameArray;
    public static boolean inSpaceship = false;
    
    private int ticksMoved = 0;
    private Path boundPath;
    private int hoverAmount = 0;
    private double hoverCount = 0;
    private double tempWorldX, tempWorldY;
    private boolean addLevelDebounce = false;
    private boolean freezePlayer = false;
    public static int xPoint = 0, yPoint = 0;
    public static double shadowExpand = 0;
    public Player(double xIn, double yIn, double heightIn)//x and y are relative to WorldPanel's x and y
    {
        x=xIn; y=yIn; height = heightIn;
        unscaledHeight = height;
        thread = new Thread(this);
        radius = getRadius();
        thread.start();
        spin = Math.PI/2.0;
        threadedOffsetTheta = getOffsetTheta();
        //boundTile = getBoundTile();
        playersChain = pathChains.chainOnPoint(getX(), getY());
        if(playersChain != null)
        {
            boundPath = playersChain.pathOnPoint((int)getX(), (int)getY());
            boundTile = boundPath.getBoundTile();
        }
        try{
            BufferedImage[] tempArray = {ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire1.png")), 
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire2.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire3.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire4.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire5.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire6.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire7.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire8.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire9.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire10.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire11.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire12.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire13.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire14.png")),
            ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Fire15.png")),
            };
            flameArray = tempArray;
        }catch(Exception e){}
        
        //boundPath = playersChain.pathOnPoint((int)getX(), (int)getY());
        //PathChains.thread = new Thread(pathChains);
        //PathChains.thread.start();
    }
    public void draw(Graphics g)
    {   
       
        //drawTurnBounds(g);   
        if(isClicked)
        {
            g.setColor(Color.RED);
        }else{
            g.setColor(Color.GREEN);
        }
        followPath();
        g.setColor(Color.BLACK);
        //g.fillOval((int)(getX())-playerRadius, (int)(getY() - getDistortedHeight())-playerRadius, playerRadius * 2, playerRadius * 2);
        drawPlayer(g);
        if(playersChain != null && MouseInput.clicked && playersChain.getChain().size() > 1)
        {
            
            //System.out.println(playersChain.getIndex());
            //System.out.println("hit");
            //if(pathChains.chainOnPoint(MouseInput.x, MouseInput.y) != null)
            {
                //System.out.println(pathChains.chainOnPoint(MouseInput.x, MouseInput.y).getIndex());
                //System.out.println();
                if(playersChain.pointOnChain(MouseInput.x, MouseInput.y))
                {
                    directionsX = convertPointsToXCoords(playersChain.getDirections(playersChain.pathOnPoint((int)getX(), (int)getY()), playersChain.pathOnPoint(MouseInput.x, MouseInput.y)));
                    directionsY = convertPointsToYCoords(playersChain.getDirections(playersChain.pathOnPoint((int)getX(), (int)getY()), playersChain.pathOnPoint(MouseInput.x, MouseInput.y)));
                    directionsHeight = playersChain.getHeightDirections(playersChain.pathOnPoint((int)getX(), (int)getY()), playersChain.pathOnPoint(MouseInput.x, MouseInput.y));
                    x = directionsX.get(0);
                    y = directionsY.get(0);
                    //NOT IT
                    inTransit = true;
                    if(followMovingPlayer)
                    {
                        tempWorldX = WorldPanel.worldX;
                        tempWorldY = WorldPanel.worldY;
                        WorldPanel.worldX = (int)(WorldPanel.worldX + ((WorldPanel.screenWidth/2.0) - getX()));//tempWorldX +(int)(tempWorldX-getX() );
                        WorldPanel.worldY = (int)(WorldPanel.worldY + ((WorldPanel.screenHeight/2.0)-getY()));
                        //System.out.println("clicked navigable path");
                    }
                    
                }
            }
        }
        if(playersChain != null)
        {
            playersChain.drawChain(g);
        }
        if(inTransit)
        {
            followDirections();
        }else if(!inTransit && playersChain != null){
            
            if(boundPath != null && boundPath.getBoundTile().getInTransit())
            {
                //NOT IT 
                x = boundPath.getBoundTile().getRawX() + (boundPath.getBoundTile().getRawWidth() * boundPath.getVertex()[0]);
                y = boundPath.getBoundTile().getRawY() + (boundPath.getBoundTile().getRawLength() * boundPath.getVertex()[1]);
            }else{
                if(playersChain.pathOnPoint((int)getX(), (int)getY()) != null)
                {
                    boundPath = playersChain.pathOnPoint((int)getX(), (int)getY());
                }
                if(boundPath != null)
                {
                    boundTile = boundPath.getBoundTile();
                }
            }
        }
        
        if(getIntersectingTile() != null && getIntersectingTile().getClicked())
        {
            freezePlayer = false;
        }
        if(!inTransit && !freezePlayer && !boundPath.getBoundTile().getClicked())
        {
            travelToClosestPath();
            
           
        }else if(boundPath.getBoundTile().getInTransit())
        {
            travelToClosestPath();
        }
        
        if(getBoundPath() == null && !inTransit && !freezePlayer)
        {
            travelToClosestPath();
        }
        if(!playerOnPathPoint() && !inTransit && !freezePlayer)
        {
            travelToClosestPath();
        }
        System.out.println(freezePlayer);
        
    }
    
    
    public boolean playerOnPathPoint()
    {
        Path p = getBoundPath();
        if(p != null)
        {
            if(getX() >= p.getX()-1 && getX() <= p.getX() + 1 && getY() >= p.getY()-1 && getY() <= p.getY() + 1)
            {
                return true;
            }
        }
        return false;
    }
    
    public void setFreezePlayer(boolean b){freezePlayer = b;}
    
    private void travelToClosestPath()//used if player becomes off-center, making path chains difficult to make and determining what path the player is on is difficult. Sets the player to the nearest path point to him. Can't see any way that this could stick him on the wrong path.
    {
        Path p = getBoundPath();
        
        if(p != null)
        {
            try{
                Tile t = p.getBoundTile();

                int smallestDistIndex = 0;
                double smallestDist = 10000;
                for(int i = 0; i < t.getPathList().size(); i++)
                {

                    double x1 = t.getPathList().get(i).getVertexCoord()[0];//t.getPathList().get(i).getBoundTile().getRawX() + ((t.getPathList().get(i).getBoundTile().getRawWidth()) * t.getPathList().get(i).getVertexCoord()[0]);
                    double y1 = t.getPathList().get(i).getVertexCoord()[1];//t.getPathList().get(i).getBoundTile().getRawY() + ((t.getPathList().get(i).getBoundTile().getRawLength()) * t.getPathList().get(i).getVertexCoord()[1]);
                    double dist = Math.sqrt(Math.pow(y-y1, 2) + Math.pow(x-x1, 2));
                    if(i == 0)
                    {
                        smallestDist = dist;
                    }
                    if(dist < smallestDist)
                    {
                        smallestDist = dist;
                        smallestDistIndex = i;
                    }
                }
                //IS IT
                if(smallestDist < 10000)
                {
                    x = t.getPathList().get(smallestDistIndex).getVertexCoord()[0];
                    y = t.getPathList().get(smallestDistIndex).getVertexCoord()[1];
                }
                //System.out.println(t.getPathList().get(smallestDistIndex).getBoundTile().getHeight());
                unscaledHeight = t.getPathList().get(smallestDistIndex).getBoundTile().getHeight();
                //System.out.println(height);
            }catch(Exception e)
            {

                //System.out.println("travelToClosestPath() failed!");
            }
        }else{
            
            
            if(getBoundPath() != null)
            {
                Tile t = getBoundTile();
                int smallestDistIndex = 0;
                double smallestDist = 10000;
                for(int i = 0; i < t.getPathList().size(); i++)
                {

                    double x1 = t.getPathList().get(i).getVertexCoord()[0];//t.getPathList().get(i).getBoundTile().getRawX() + ((t.getPathList().get(i).getBoundTile().getRawWidth()) * t.getPathList().get(i).getVertexCoord()[0]);
                    double y1 = t.getPathList().get(i).getVertexCoord()[1];//t.getPathList().get(i).getBoundTile().getRawY() + ((t.getPathList().get(i).getBoundTile().getRawLength()) * t.getPathList().get(i).getVertexCoord()[1]);
                    double dist = Math.sqrt(Math.pow(y-y1, 2) + Math.pow(x-x1, 2));
                    if(i == 0)
                    {
                        smallestDist = dist;
                    }
                    if(dist < smallestDist)
                    {
                        smallestDist = dist;
                        smallestDistIndex = i;
                    }
                }
                //IS IT
                if(smallestDist < 10000)
                {
                    x = t.getPathList().get(smallestDistIndex).getVertexCoord()[0];
                    y = t.getPathList().get(smallestDistIndex).getVertexCoord()[1];
                    

                }
                unscaledHeight = t.getPathList().get(smallestDistIndex).getBoundTile().getHeight();
                //System.out.println(t.getPathList().get(smallestDistIndex).getBoundTile().getHeight());
            }else{
                Tile t = getIntersectingTile();
                if(t != null)
                {
                    int smallestDistIndex = 0;
                    double smallestDist = 10000;
                    for(int i = 0; i < t.getPathList().size(); i++)
                    {

                        double x1 = t.getPathList().get(i).getVertexCoord()[0];//t.getPathList().get(i).getBoundTile().getRawX() + ((t.getPathList().get(i).getBoundTile().getRawWidth()) * t.getPathList().get(i).getVertexCoord()[0]);
                        double y1 = t.getPathList().get(i).getVertexCoord()[1];//t.getPathList().get(i).getBoundTile().getRawY() + ((t.getPathList().get(i).getBoundTile().getRawLength()) * t.getPathList().get(i).getVertexCoord()[1]);
                        double dist = Math.sqrt(Math.pow(y-y1, 2) + Math.pow(x-x1, 2));
                        if(i == 0)
                        {
                            smallestDist = dist;
                        }
                        if(dist < smallestDist)
                        {
                            smallestDist = dist;
                            smallestDistIndex = i;
                        }
                    }
                    //IS IT
                    if(smallestDist < 10000)
                    {
                        x = t.getPathList().get(smallestDistIndex).getVertexCoord()[0];
                        y = t.getPathList().get(smallestDistIndex).getVertexCoord()[1];
                    }
                    //System.out.println(t.getPathList().get(smallestDistIndex).getBoundTile().getHeight());
                    unscaledHeight = t.getPathList().get(smallestDistIndex).getBoundTile().getHeight();
                }
            }
        }
        
    }
    
    public Tile getIntersectingTile()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(TileDrawer2.tileList.get(i).tileAtCoord((int)getX(), (int)getY()))
            {
                return TileDrawer2.tileList.get(i);
            }
        }
        return null;
    }
    
    
    private void drawPlayer(Graphics g)
    {
        
        if(!inSpaceship)
        {
            
             shadowExpand = 1.0+(-scaledDistortedHeight(hoverAmount + 15)/30);
            //g.fillOval((int)(getX() - (10*WorldPanel.scale) - (shadowExpand/2)), (int)(getY()-((10)*WorldPanel.getShrink*WorldPanel.scale)-(shadowExpand/2)), (int)((20+(shadowExpand/2))*WorldPanel.scale), (int)((20+(shadowExpand/2))*WorldPanel.getShrink*WorldPanel.scale));
            xPoint = (int)getX();
            yPoint = (int)getY();
            
            WorldPanel.scale/=1.5;
            
            
            
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            hoverCount += Math.PI/40.0;
            fireAnimationCount += .25;
            if(fireAnimationCount >= flameArray.length)
            {
                fireAnimationCount = 0;
            }
            hoverAmount = (int)(15*Math.sin(hoverCount))-15;
            


            double shrinkMultiplier = .33;
            Image tempImage = flameArray[(int)fireAnimationCount].getScaledInstance((int)(WorldPanel.scale*26), (int)(WorldPanel.scale*distortedHeight(16)), Image.SCALE_AREA_AVERAGING);
            //g.drawImage(tempImage, (int)getX()-(int)(13*WorldPanel.scale), (int)(getY()+(WorldPanel.scale * (hoverAmount*shrinkMultiplier - distortedHeight(5)))), null);//flameArray[(int)fireAnimationCount], 10, 10, null);
            //Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(1));


            
            g.setColor(Color.GRAY);

            g.fillRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*21)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(17-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*42), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(8)));
            g.fillRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*28)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(79-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*56), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(62)));
            g.fillRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*22)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(112-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*44), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(30)));
            g.fillRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*34)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(75-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*6), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(16)));
            g.fillRect((int)(getX() + (shrinkMultiplier*WorldPanel.scale*28)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(75-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*6), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(16)));

            int[]xPoints1 = {(int)(getX() -(int)(shrinkMultiplier*WorldPanel.scale*43)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*32)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*36)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*47))};
            int[]yPoints1 = {(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(79-hoverAmount))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(78-hoverAmount))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(22-hoverAmount))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(23-hoverAmount)))};


            int[]xPoints2 = {(int)(getX() +(int)(shrinkMultiplier*WorldPanel.scale*43)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*32)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*36)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*47))};



            g.setColor(Color.WHITE);

            g.fillOval((int)(getX()-(shrinkMultiplier*WorldPanel.scale*13)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale * distortedHeight(107-hoverAmount))), (int)(9*shrinkMultiplier*WorldPanel.scale), (int)(9*shrinkMultiplier*WorldPanel.scale));
            g.fillOval((int)(getX()+(shrinkMultiplier*WorldPanel.scale*6)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale * distortedHeight(107-hoverAmount))), (int)(9*shrinkMultiplier*WorldPanel.scale), (int)(9*shrinkMultiplier*WorldPanel.scale));

            g.setColor(Color.BLACK);
            g.fillOval((int)(getX()-(shrinkMultiplier*WorldPanel.scale*11)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale * distortedHeight(106-hoverAmount))), (int)(6*shrinkMultiplier*WorldPanel.scale), (int)(6*shrinkMultiplier*WorldPanel.scale));
            g.fillOval((int)(getX()+(shrinkMultiplier*WorldPanel.scale*6)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale * distortedHeight(106-hoverAmount))), (int)(6*shrinkMultiplier*WorldPanel.scale), (int)(6*shrinkMultiplier*WorldPanel.scale));


            g.setColor(Color.BLACK);

            g.drawRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*21)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(17-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*42), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(8)));
            g.drawRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*28)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(79-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*56), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(62)));
            g.drawRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*22)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(112-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*44), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(30)));
            g.drawRect((int)(getX() - (shrinkMultiplier*WorldPanel.scale*34)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(75-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*6), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(16)));
            g.drawRect((int)(getX() + (shrinkMultiplier*WorldPanel.scale*28)), (int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(75-hoverAmount))), (int)(shrinkMultiplier*WorldPanel.scale*6), (int)(shrinkMultiplier*WorldPanel.scale*distortedHeight(16)));

            //int[]xPoints1 = {(int)(getX() -(int)(shrinkMultiplier*WorldPanel.scale*43)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*32)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*36)), (int)(getX() - (int)(shrinkMultiplier*WorldPanel.scale*47))};
            //int[]yPoints1 = {(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(79))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(78))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(22))),(int)(getY() - (shrinkMultiplier*WorldPanel.scale*distortedHeight(23)))};
            g.setColor(Color.GRAY);
            g.fillPolygon(xPoints1, yPoints1, 4);
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints1, yPoints1, 4);

            //int[]xPoints2 = {(int)(getX() +(int)(shrinkMultiplier*WorldPanel.scale*43)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*32)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*36)), (int)(getX() + (int)(shrinkMultiplier*WorldPanel.scale*47))};
            g.setColor(Color.GRAY);
            g.fillPolygon(xPoints2, yPoints1, 4);
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints2, yPoints1, 4);

            

            g2.setStroke(Toolbox.worldStroke);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            /*if(boundPath != null && boundTile != null)
            {
                g.drawString(boundPath.toString(), 100, 100);
                g.drawString(boundTile.toString(), 100, 120);
            }*/
               WorldPanel.scale*=1.5;
        }
        
    }
    
    private ArrayList<Double> convertPointsToXCoords(ArrayList<Point> points)
    {
        ArrayList<Double> giveReturn = new ArrayList<Double>();
        for(Point p : points)
        {
            giveReturn.add(convertToUnit(p.getX(), p.getY())[0]);
        }
        return giveReturn;
    }
    
    private ArrayList<Double> convertPointsToYCoords(ArrayList<Point> points)
    {
        ArrayList<Double> giveReturn = new ArrayList<Double>();
        for(Point p : points)
        {
            giveReturn.add(convertToUnit(p.getX(), p.getY())[1]);
        }
        return giveReturn;
    }
    
    private void drawTurnBounds(Graphics g)
    {
        g.setColor(Color.RED);
        g.fillPolygon(squarePoints[0], squarePoints[1], 4);
        
        g.setColor(Color.BLUE);//front
        g.fillOval(squarePoints[0][0]-5, squarePoints[1][0]-5, 10, 10);
        
        g.setColor(Color.BLACK);//left
        g.fillOval(squarePoints[0][1]-5, squarePoints[1][1]-5, 10, 10);
        
        g.setColor(Color.GREEN);//behind
        g.fillOval(squarePoints[0][2]-5, squarePoints[1][2]-5, 10, 10);
        
        g.setColor(Color.YELLOW);//right
        g.fillOval(squarePoints[0][3]-5, squarePoints[1][3]-5, 10, 10);
        
        drawSpinLine(g);
    }
    public int getDistortedHeight(){return (int)distortedHeight((int)height);}
    public Thread getThread(){return thread;}
    public void setThread(Thread t){thread = t;}
    public double getX(){return WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + threadedOffsetTheta));}
    public double getY(){return WorldPanel.worldY - (WorldPanel.scale * distortedHeight((int)unscaledHeight)) - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + threadedOffsetTheta));}//subtracting since y axis is flipped
    public double getRadius(){return Math.sqrt(Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));}
    public double getOffsetTheta(){return Math.atan2(y, x);}
    
    
    public void followDirections()
    {
        ticksMoved++;
        int numDirectionsFollowed = 0;
        if(directionsX.size() > 1)
        {
            
            //WorldPanel.worldY = tempWorldY + (int)(getY() - tempWorldY);
            double dx = directionsX.get(1)-directionsX.get(0);
            double dy = directionsY.get(1)-directionsY.get(0);
            double dHeight = directionsHeight.get(1)-directionsHeight.get(0);
            double numTicks = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2))*50;
            if(ticksMoved < numTicks)
            {
                //WorldPanel.worldX = (int)(tempWorldX + (dx*WorldPanel.straightUnit * WorldPanel.scale/numTicks));
                //WorldPanel.worldY = (int)(tempWorldY + (dy*WorldPanel.straightUnit * WorldPanel.scale/numTicks));
                if(followMovingPlayer)
                {
                    WorldPanel.worldX -= (dx*WorldPanel.straightUnit*Math.cos(WorldPanel.radSpin)/numTicks);
                    WorldPanel.worldY += (dx*WorldPanel.straightUnit*Math.sin(WorldPanel.radSpin)/numTicks);

                    WorldPanel.worldY -= (dy*WorldPanel.straightUnit*Math.sin(WorldPanel.radSpin)/numTicks);
                    WorldPanel.worldX -= (dy*WorldPanel.straightUnit*Math.cos(WorldPanel.radSpin)/numTicks);

                }
                //NOT IT
                x += dx/numTicks;
                y += dy/numTicks;
                unscaledHeight += dHeight/numTicks;
                
            }else{
                numDirectionsFollowed++;
                ticksMoved = 0;
                directionsX.remove(0);
                directionsY.remove(0);
                directionsHeight.remove(0);
                //NOT IT
                x = directionsX.get(0);
                y = directionsY.get(0);
                unscaledHeight = directionsHeight.get(0);
                Path p = getBoundPath();//playersChain.pathOnPoint((int)getX(), (int)getY());
                if(p != null)
                {
                    boundTile = p.getBoundTile();
                    boundPath = p;
                    p.getBoundTile().addTimeWalkedOn();
                }
            }
        }else{
            ticksMoved = 0;
            
            //unscaledHeight = (int)(directionsHeight.get(directionsHeight.size()-1)/WorldPanel.scale);
          
            directionsX.clear();
            directionsY.clear();
            //unscaledHeight = directionsHeight.get(directionsHeight.size()-1);
            directionsHeight.clear();
            //directionsHeight.clear();
            //boundTile = getBoundTile();
            //unscaledHeight = boundTile.getHeight();
            inTransit = false;
            //playersChain = pathChains.chainOnPoint(getX(), getY());
            if(playersChain != null)
            {
                boundPath = playersChain.pathOnPoint((int)getX(), (int)getY());
                boundTile = boundPath.getBoundTile();
                //boundTile.addTimeWalkedOn();
            }
            
            
            if(boundPath.getClass() == LevelEndPath.class && addLevelDebounce == false)
            {
                freezePlayer = true;
                addLevelDebounce = true;
                System.out.println("level ended!");
                UI.addLevel();
                LevelEndTile t = (LevelEndTile)boundPath.getBoundTile();
                t.getSpaceship().setTakeoff(true);
                inSpaceship = true;
                
                
            }
            
            
            
        }
    }
    
    
    public Tile getBoundTile()
    {
        getBoundPath().getBoundTile().setPlayersTile(true);
        return getBoundPath().getBoundTile();
    }
    
    public Path getBoundPath()
    {
       
         
        if(boundPath != null && !boundPath.getBoundTile().getClicked())
        {
            int tileListSize = TileDrawer.tileList.size();
            for(int i = 0; i < tileListSize; i++)
            {
                int pathListSize = TileDrawer.tileList.get(i).getPathList().size();
                for(int j = 0; j < pathListSize; j++)
                {
                    if(TileDrawer.tileList.get(i).getPathList().get(j).getPathPolygon().contains((int)getX(), (int)getY()) && TileDrawer.tileList.get(i).getHeight() >= height - 5 && TileDrawer.tileList.get(i).getHeight() <= height + 5)
                    {
                        return TileDrawer.tileList.get(i).getPathList().get(j);
                    }
                }
            }
        }else if(boundPath != null && boundPath.getBoundTile().getClicked())
        {
            return boundPath;
        }
        return null;
    }
    
    public void drawSpinLine(Graphics g)
    {
        g.setColor(Color.BLUE);
        g.drawLine((int)getX(), (int)(getY()-getDistortedHeight()), (int)(getX()+(Math.cos(spin+WorldPanel.radSpin)*50)), (int)(getY() - getDistortedHeight() - (Math.sin(spin+WorldPanel.radSpin)*50)));
    }
    
    public void followPath()//old, working version
    {
        
    }
    
    
    
    private boolean checkAhead()
    {
        double dx = Math.cos(spin)*.005;
        double dy = Math.sin(spin)*.005;
        double[] checkPoint = convertToPoint(x+dx, y+dy);
        if(MergedPaths.threadedArea.contains(checkPoint[0], checkPoint[1]))
        {
            return true;
        }
        return false;
    }
    
    private boolean checkLeft()
    {
        if(MergedPaths.threadedArea.contains(squarePoints[0][1], squarePoints[1][1]))
        {
            return true;
        }
        return false;
    }
    
    private boolean checkRight()
    {
        if(MergedPaths.threadedArea.contains(squarePoints[0][3], squarePoints[1][3]))
        {
            return true;
        }
        return false;
    }
    
    public void tick()
    {
        
        threadedOffsetTheta = getOffsetTheta();
        int i = 0;
        for(double rad = WorldPanel.radSpin + spin; rad < WorldPanel.radSpin + (Math.PI*2.0) + spin; rad+=Math.PI/2.0)
        {
            if(i < 4)
            {
                squarePoints[0][i]=(int)(getX()+ Math.cos(rad)*playerRadius*5.0);
                squarePoints[1][i]=(int)(getY() - distortedHeight((int)height) - Math.sin(rad)*playerRadius*5.0*WorldPanel.getShrink);
            }
            i++;
        }
        //boundTile = getBoundTile();
        if(boundTile!=null)
        {
            //unscaledHeight = boundTile.getHeight();
            if(boundTile.getInTransit())
            {
                //x += boundTile.getdx();
                //y += boundTile.getdy();
            }
        }
        playerRadius = (int)(unscaledPlayerRadius*WorldPanel.scale);
        height = unscaledHeight * WorldPanel.scale;
        radius = getRadius();
        
        if(LevelLoader.movePlayerToStart)
        {
            //System.out.println(LevelLoader.playerStartPath.getBoundTile());
            boundTile = LevelLoader.playerStartPath.getBoundTile();
            boundPath = LevelLoader.playerStartPath;
           // System.out.println(LevelLoader.startTile);
            x=LevelLoader.playerStartPath.getCoordX();
            y=LevelLoader.playerStartPath.getCoordY();
            //freezePlayer = false;
            
            height = LevelLoader.playerStartPath.getBoundTile().getRawHeight();
            unscaledHeight = LevelLoader.playerStartPath.getBoundTile().getRawHeight();
            LevelLoader.movePlayerToStart = false;
            addLevelDebounce = false;
        }
        //setClicked();
    }
    
    public void setX(double xIn)
    {
        x=xIn;
    }
    
    public void setY(double yIn)
    {
        y = yIn;
    }
    
    public void setHeight(int heightIn)
    {
        height = heightIn;
    }
    
    @Override
    public void run() 
    {
        tick();
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
        System.out.println("OffsetTheta: " + getOffsetTheta());
        System.out.println("Radius: " + getRadius());
        System.out.println(getX());
        playersChain = pathChains.chainOnPoint(getX(), getY());
        if(playersChain != null)
        {
            pathIsClicked = playersChain.pointOnChain(MouseInput.x, MouseInput.y);
            //boundPath = playersChain.pathOnPoint((int)getX(), (int)getY());
        }
    }
}
