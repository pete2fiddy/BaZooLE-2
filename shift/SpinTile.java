package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class SpinTile extends Tile implements Runnable
{
    public static final Color redAlpha = new Color(255, 0, 0, 100);
    private Prism cylinder;
    private final int tileVertices = 32;
    private int[][] threadedCylinderPoints, threadedTopCylinderPoints;
    private int[][][] threadedSideArrays;
    
    private double rotation;
    private int diameter;
    private Thread thread;
    
    public SpinTile(int inX, int inY, int diameterIn, int inHeight) 
    {
        super(inX, inY, diameterIn, diameterIn, inHeight);
        setMoveable(false);
        setSpinnable(true);
        diameter = diameterIn;
        rotation = WorldPanel.radSpin;
        TileSorter.addTile(this);
        threadedCylinderPoints = cylinderPoints(tileVertices);
        threadedTopCylinderPoints = cylinderPoints(tileVertices);
        threadedSideArrays = calcSideArrays(tileVertices);
        thread = new Thread(this);
        double[] vertex = {.5, .5};
        Path p = new DirtPath(this, vertex, 0, 1);
        thread.start();
        cylinder = new Prism(getMiddleCoordX(), getMiddleCoordY(), 0, ((double)diameterIn/2.0)/Math.sqrt(2), inHeight, 32);
        
    }
    public SpinTile(int inX, int inY, int diameterIn, int inHeight, int angleIn) 
    {
        super(inX, inY, diameterIn, diameterIn, inHeight);
        setMoveable(false);
        setSpinnable(true);
        diameter = diameterIn;
        rotation = WorldPanel.radSpin;
        TileSorter.addTile(this);
        threadedCylinderPoints = cylinderPoints(tileVertices);
        threadedTopCylinderPoints = cylinderPoints(tileVertices);
        threadedSideArrays = calcSideArrays(tileVertices);
        thread = new Thread(this);
        double[] vertex = {.5, .5};
        Path p = new DirtPath(this, vertex, 0, 1);
        thread.start();
        setSpin(((double)angleIn/180.0)*Math.PI);
        cylinder = new Prism(getMiddleCoordX(), getMiddleCoordY(), 0, ((double)diameterIn/2.0)/Math.sqrt(2), inHeight, 32);
    }
    
    @Override
    public void drawReflections(Graphics g)
    {
        
        cylinder.shadeWaterReflections(g, cylinder.getVisibleSidePolygons());
    }
    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(Color.BLACK);
        //g.drawPolygon(threadedCylinderPoints[0],threadedCylinderPoints[1],32);
        //g.setColor(getColor());
        g2.setPaint(WorldPanel.grassTexture);
        g.fillPolygon(threadedTopCylinderPoints[0], threadedTopCylinderPoints[1], 32);
        drawSides(g);
        
        if(getClicked())
        {
            g.setColor(redAlpha);
            g.fillPolygon(threadedTopCylinderPoints[0], threadedTopCylinderPoints[1], 32);
            drawSides(g);
        }
        g.setColor(Color.BLACK);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //g.drawPolygon(threadedTopCylinderPoints[0], threadedTopCylinderPoints[1], tileVertices);
        for(Path p:getPathList())
        {
            p.draw(g);
        }
        
        
        drawPlayer(g, Player.xPoint, Player.yPoint, Player.shadowExpand);
        cylinder.shadeSidePolygons(g, cylinder.getVisibleSidePolygons());
        //cylinder.draw(g);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        //g.setColor(Color.BLUE);
        //drawSpinLine(g);
    }
    private void drawSpinLine(Graphics g)
    {
        g.fillOval((int)convertToPoint(getRawX() + (getRawWidth()/2.0), getRawY() + (getRawLength()/2.0))[0] - 5, (int)convertToPoint(getRawX() + (getRawWidth()/2.0), getRawY() + (getRawLength()/2.0))[1] - 5, 10, 10);
        int x = getCenterX();
        int y = getCenterY();
        int x2 = (int)(x + ((getRawWidth()/2.0) * Math.cos(getSpin()+WorldPanel.radSpin))*WorldPanel.straightUnit);
        int y2 = (int)(y - ((getRawLength()/2.0) * Math.sin(getSpin()+WorldPanel.radSpin) * WorldPanel.getShrink * WorldPanel.straightUnit));
        
        g.drawLine(x, y, x2, y2);
    }
    private void drawCorners(Graphics g)
    {
        double[] center = {(threadedPoints()[0][0] + threadedPoints()[0][2])/2.0, (threadedPoints()[1][0] + threadedPoints()[1][2])/2.0};
        g.fillOval((int)center[0]-5,(int)center[1]-5,10, 10);
        g.setColor(Color.RED);
        g.fillOval(threadedPoints()[0][getTopLeftIndex()]-5,threadedPoints()[1][getTopLeftIndex()]-5, 10, 10);
        
        g.setColor(Color.BLUE);
        g.fillOval(threadedPoints()[0][getTopRightIndex()]-5, threadedPoints()[1][getTopRightIndex()]-5, 10, 10);
        
        g.setColor(Color.MAGENTA);
        g.fillOval(threadedPoints()[0][getBottomRightIndex()]-5, threadedPoints()[1][getBottomRightIndex()]-5, 10, 10);
        
        g.setColor(Color.GREEN);
        g.fillOval(threadedPoints()[0][getBottomLeftIndex()]-5, threadedPoints()[1][getBottomLeftIndex()]-5, 10 ,10);
    }
    private void drawSides(Graphics g)
    {
        for(int i = 0; i < threadedSideArrays.length; i++)
        {
            g.fillPolygon(threadedSideArrays[i][0], threadedSideArrays[i][1], 4);
        }
    }
    private int[][][] calcSideArrays(int vertices)
    {
        int[][][] giveReturn = new int[vertices][2][4];
        double[] center = {(threadedPoints()[0][0] + threadedPoints()[0][2])/2.0, (threadedPoints()[1][0] + threadedPoints()[1][2])/2.0};
        double radius = Math.abs((Math.sqrt(Math.pow(threadedPoints()[0][0] - center[0],2) + Math.pow((threadedPoints()[1][0]-center[1])/WorldPanel.getShrink,2)))/Math.sqrt(2));
        
        for(int i = 0; i < vertices; i++)
        {
            if(i != vertices - 1)
            {
                int[] xPoints = {threadedTopCylinderPoints[0][i], threadedTopCylinderPoints[0][i+1], threadedCylinderPoints[0][i+1], threadedCylinderPoints[0][i]};
                int[] yPoints = {threadedTopCylinderPoints[1][i], threadedTopCylinderPoints[1][i+1], threadedCylinderPoints[1][i+1], threadedCylinderPoints[1][i]};
                giveReturn[i][0]=xPoints;
                giveReturn[i][1]=yPoints;
            }else{
                int[] xPoints = {threadedTopCylinderPoints[0][i], threadedTopCylinderPoints[0][0], threadedCylinderPoints[0][0], threadedCylinderPoints[0][i]};
                int[] yPoints = {threadedTopCylinderPoints[1][i], threadedTopCylinderPoints[1][0], threadedCylinderPoints[1][0], threadedCylinderPoints[1][i]};
                giveReturn[i][0]=xPoints;
                giveReturn[i][1]=yPoints;
            }
        }
        return giveReturn;
    }
    public int[][] edgePoints()
    {
        int[][] points = threadedPoints();
        int[][] giveReturn = new int[2][4];
        for(int i = 0; i < 4; i++)
        {
            if(i != 3)
            {
                giveReturn[0][i] = (points[0][i]+points[0][i+1])/2;
                giveReturn[1][i] = (points[1][i] + points[1][i+1])/2;
            }
            else{
                giveReturn[0][i]=(points[0][i]+points[0][0])/2;
                giveReturn[1][i] = (points[1][i] + points[1][0])/2;
            }
        }
        return giveReturn;
    }
    public int[][] cylinderPoints(int vertices)
    {
        double[] center = {(threadedPoints()[0][0] + threadedPoints()[0][2])/2.0, (threadedPoints()[1][0] + threadedPoints()[1][2])/2.0};//average of points directly across from each other to find center.
        double radius = Math.abs((Math.sqrt(Math.pow(threadedPoints()[0][0] - center[0],2) + Math.pow((threadedPoints()[1][0]-center[1])/WorldPanel.getShrink,2)))/Math.sqrt(2));
        
        double[] lastPoint = {edgePoints()[0][0],edgePoints()[1][0]};
        int[][] holdPoints = new int[2][vertices];
        holdPoints[0][0] = (int)lastPoint[0];
        holdPoints[1][0] = (int)lastPoint[1];
        int timesRun = 0;
        for(double spin = 0; spin < Math.PI*2.0; spin += 2*Math.PI/(double)vertices)
        {
            double dy = radius*Math.sin(spin - WorldPanel.radSpin)*WorldPanel.getShrink;
            double dx = radius*Math.cos(spin - WorldPanel.radSpin);
            lastPoint[0]= center[0] + dx;
            lastPoint[1]= center[1] + dy;
            holdPoints[0][timesRun]=(int)lastPoint[0];
            holdPoints[1][timesRun]=(int)lastPoint[1];
            timesRun++;
        }
        return holdPoints;
    }
    public int[][] topCylinderPoints(int vertices)
    {
        int[][] giveReturn = cylinderPoints(vertices);
        for(int i = 0; i < giveReturn[0].length; i++)
        {
            giveReturn[1][i]-=(int)(WorldPanel.scale * WorldPanel.distortedHeight(WorldPanel.rotation, getHeight()));
        }
        return giveReturn;
    }
    @Override
    public void run()
    {
        super.run();
        threadedTopCylinderPoints = topCylinderPoints(tileVertices);
        threadedCylinderPoints = cylinderPoints(tileVertices);
        threadedSideArrays = calcSideArrays(tileVertices);
    }
}
