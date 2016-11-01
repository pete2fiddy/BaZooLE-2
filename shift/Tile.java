package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

public abstract class Tile extends Toolbox implements Runnable
{
    private boolean rightClicked= false;
    //private int index = 0;
    private boolean moveable = true;
    private double spin;
    private boolean spinnable = false;
    private Polygon threadedTilePolygon;
    private Thread thread;
    private double thisdx, thisdy;
    private int[][] myThreadedUpperPoints, myThreadedLowerPoints, polyPoints1, polyPoints2;
    private double x, y;
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
    private Grass[] grassList = new Grass[0];
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
    private ArrayList<Scenery> earlyDrawScenery = new ArrayList<Scenery>();
    private double movingX = 0, movingY = 0;
    private Player player;
    private static double grassSkip;
      
    /*
    Initialization:
    */
    
    /*
    Params: Takes a coordinate x(from bottom left corner), coordinate y(from bottom left corner), a units width, a units length, a PIXELS height (pixels it takes up on screen at 1 scale with fully rotated world to see the full side of it)
    */
    public Tile(int inX, int inY, int inWidth, int inLength, int inHeight)//not sure why position is given as a double. Can't see myself using half a unit or something.
    {
        x=inX;y=inY;width=inWidth;length=inLength; baseHeight = inHeight; height = baseHeight;
        thisClicked = false;
        polyPoints1 = new int[2][4];
        polyPoints2 = new int[2][4];
        color = new Color(14, 155, 14);
        myThreadedLowerPoints = getPoints().clone();
        myThreadedUpperPoints = new int[2][4];
        setUpperPoints();
        thread = new Thread(this);
        transitPos = new int[2];
        spin = 0;
        movingX = x; 
        movingY = y;
        threadedTilePolygon = new Polygon(myThreadedLowerPoints[0], myThreadedLowerPoints[1],4);
        TileDrawer2.tileList.add(this);
        thread.start();
        player = getPlayer();
        addRandomScenery();
        sortAllScenery();
        setGrassSkip();
    }
    
    
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    public void drawWaterReflectionsWithColor(Graphics g, Color c)
    {
        
        /*Graphics2D g2 = (Graphics2D)g;
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
        g.setColor(ColorPalette.grassColor);
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
        //g.drawString(Boolean.toString(tileCurrentlyMoving), (int)getX(), (int)getY());*/
        
        double alphaNum = (double)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //int red = (int)(c.getRed() - (alphaNum*(c.getRed()-WorldPanel.waterColor.getRed())));
        //int green = (int)(c.getGreen() - (alphaNum*(c.getGreen()-WorldPanel.waterColor.getGreen())));
        //int blue = (int)(c.getBlue() - (alphaNum*(c.getBlue()-WorldPanel.waterColor.getBlue())));
        g.setColor(ColorPalette.getLerpColor(c, WorldPanel.waterColor, alphaNum));
        int[][] clone1= getLeftSidePoints();
        int[][] clone2 = getRightSidePoints();
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();int red
        int[][] points2 = new int[2][4];//getRightSidePoints().clone();
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[0][i] = clone1[0][i];
            points1[1][i]+= clone1[1][i] + getScaledDistortedHeight();
            
            
            
            points2[0][i] = clone2[0][i];
            points2[1][i]+= clone2[1][i] + getScaledDistortedHeight();
        }
        
        
        //points1[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points1[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        //points2[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points2[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        Area a = new Area(new Polygon(points1[0], points1[1], points1[0].length));
        a.subtract(WorldPanel.belowMapArea);
        Graphics2D g2 = (Graphics2D) g;
        g2.fill(a);
        //g.fillPolygon(points1[0], points1[1],points1[0].length);
        
        alphaNum = (double)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //g2.setComposite(AlphaComposite.getInstance(type, (float)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
        //red = (int)(c.getRed() - (alphaNum*(c.getRed()-WorldPanel.waterColor.getRed())));
        //green = (int)(c.getGreen() - (alphaNum*(c.getGreen()-WorldPanel.waterColor.getGreen())));
        //blue = (int)(c.getBlue() - (alphaNum*(c.getBlue()-WorldPanel.waterColor.getBlue())));
        g.setColor(ColorPalette.getLerpColor(c,WorldPanel.waterColor,alphaNum));
        a = new Area(new Polygon(points2[0], points2[1], points2[0].length));
        a.subtract(WorldPanel.belowMapArea);
        g2.fill(a);
        //g.fillPolygon(points2[0], points2[1], points2[0].length);
        
    }
    
    public void drawWaterReflections(Graphics g)
    {
        /*Graphics2D g2 = (Graphics2D)g;
        Composite originalComposite = g2.getComposite();
        int type = AlphaComposite.SRC_OVER;
        
        AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0))));
        g2.setComposite(transparencyComposite);
        g.setColor(ColorPalette.grassColor);*/
        
        double alphaNum = (double)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //int red = (int)(ColorPalette.grassColor.getRed() - (alphaNum*(ColorPalette.grassColor.getRed()-WorldPanel.waterColor.getRed())));
        //int green = (int)(ColorPalette.grassColor.getGreen() - (alphaNum*(ColorPalette.grassColor.getGreen()-WorldPanel.waterColor.getGreen())));
        //int blue = (int)(ColorPalette.grassColor.getBlue() - (alphaNum*(ColorPalette.grassColor.getBlue()-WorldPanel.waterColor.getBlue())));
        g.setColor(ColorPalette.getLerpColor(ColorPalette.grassColor, WorldPanel.waterColor, alphaNum));
        int[][] clone1= getLeftSidePoints();
        int[][] clone2 = getRightSidePoints();
        int[][] points1 = new int[2][4];//getLeftSidePoints().clone();
        int[][] points2 = new int[2][4];//getRightSidePoints().clone();
        for(int i = 0; i < points1[0].length; i++)
        {
            points1[0][i] = clone1[0][i];
            points1[1][i]+= clone1[1][i] + getScaledDistortedHeight();
            
            
            
            points2[0][i] = clone2[0][i];
            points2[1][i]+= clone2[1][i] + getScaledDistortedHeight();
        }
        
        
        //points1[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points1[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        //points2[1][2] -= (int)(scaledDistortedHeight((int)height)/2.0);
        //points2[1][3] -= (int)(scaledDistortedHeight((int)height)/2.0);
        
        Area a = new Area(new Polygon(points1[0], points1[1], points1[0].length));
        a.subtract(WorldPanel.belowMapArea);
        Graphics2D g2 = (Graphics2D) g;
        g2.fill(a);
        //g.fillPolygon(points1[0], points1[1],points1[0].length);
        
        alphaNum = (double)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //g2.setComposite(AlphaComposite.getInstance(type, (float)(.50 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)))));
        //red = (int)(ColorPalette.grassColor.getRed() - (alphaNum*(ColorPalette.grassColor.getRed()-WorldPanel.waterColor.getRed())));
        //green = (int)(ColorPalette.grassColor.getGreen() - (alphaNum*(ColorPalette.grassColor.getGreen()-WorldPanel.waterColor.getGreen())));
        //blue = (int)(ColorPalette.grassColor.getBlue() - (alphaNum*(ColorPalette.grassColor.getBlue()-WorldPanel.waterColor.getBlue())));
        g.setColor(ColorPalette.getLerpColor(ColorPalette.grassColor, WorldPanel.waterColor, alphaNum));
        a = new Area(new Polygon(points2[0], points2[1], points2[0].length));
        a.subtract(WorldPanel.belowMapArea);
        g2.fill(a);
        //g.fillPolygon(points2[0], points2[1], points2[0].length);
        //g2.setComposite(originalComposite);
    }
    
    
    
    public void drawShadedSides(Graphics g, Color lowerColor)
    {
        Color darkColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + highShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        Color lightColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + lowShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //g.setColor(darkColor);
        //g.fillPolygon(sidePolygons[0]);
        //Polygon[] sides = sidePolygons.clone();
        //g.setColor(lightColor);
        //g.fillPolygon(sidePolygons[1]);
        //int numSides = sides.length;
        //System.out.println("Dark Color " + darkColor);
        //System.out.println("Light Color " + lightColor);
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        //double dr = (double)(lightColor.getRed()-darkColor.getRed())/(double)(2-1);
        ///double dg = (double)(lightColor.getGreen()-darkColor.getGreen())/(double)(2-1);
        //double db = (double)(lightColor.getBlue()-darkColor.getBlue())/(double)(2-1);
        //g.setColor(new Color((int)(darkColor.getRed()), (int)(darkColor.getGreen()), (int)(darkColor.getBlue())));
        g.setColor(lightColor);
        g.fillPolygon(rightPoints[0], rightPoints[1], rightPoints[0].length);
        g.setColor(darkColor);
        g.fillPolygon(leftPoints[0], leftPoints[1], leftPoints[0].length);
        //g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));

        /*for (int i = 0; i < 2; i++) {
            g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));
            //g.fillPolygon(rightPoints[i]);
        }*/
        /*
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        double leftAlpha = 0.31372549019608-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED,ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha),0.5));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }
        
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        double rightAlpha = 0.19607843137255-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED, ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha),0.5));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
            
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
        }
        
        
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);*/
        
    }
    
    public void drawClippedShadedSides(Graphics g, Area clipArea, Color lowerColor)
    {
        Graphics2D g2 = (Graphics2D)g;
        Color darkColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + highShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        Color lightColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + lowShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //g.setColor(darkColor);
        //g.fillPolygon(sidePolygons[0]);
        //Polygon[] sides = sidePolygons.clone();
        //g.setColor(lightColor);
        //g.fillPolygon(sidePolygons[1]);
        //int numSides = sides.length;
        //System.out.println("Dark Color " + darkColor);
        //System.out.println("Light Color " + lightColor);
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        //double dr = (double)(lightColor.getRed()-darkColor.getRed())/(double)(2-1);
        ///double dg = (double)(lightColor.getGreen()-darkColor.getGreen())/(double)(2-1);
        //double db = (double)(lightColor.getBlue()-darkColor.getBlue())/(double)(2-1);
        //g.setColor(new Color((int)(darkColor.getRed()), (int)(darkColor.getGreen()), (int)(darkColor.getBlue())));
        g.setColor(lightColor);
        Area rightArea = new Area(new Polygon(rightPoints[0], rightPoints[1], rightPoints[0].length));
        rightArea.intersect(clipArea);
        g2.fill(rightArea);
        //g.fillPolygon(rightPoints[0], rightPoints[1], rightPoints[0].length);
        g.setColor(darkColor);
        Area leftArea = new Area(new Polygon(leftPoints[0], leftPoints[1], leftPoints[0].length));
        leftArea.intersect(clipArea);
        g2.fill(leftArea);
        
        //g.fillPolygon(leftPoints[0], leftPoints[1], leftPoints[0].length);
        //g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));

        /*for (int i = 0; i < 2; i++) {
            g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));
            //g.fillPolygon(rightPoints[i]);
        }*/
        /*
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        double leftAlpha = 0.31372549019608-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED,ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha),0.5));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }
        
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        double rightAlpha = 0.19607843137255-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED, ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha),0.5));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
            
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
        }
        
        
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);*/
        
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
    
    public void drawReverseShadedSides(Graphics g, Color lowerColor)
    {
        Color darkColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + highShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        Color lightColor = ColorPalette.getLerpColor(Color.BLACK, lowerColor, ColorPalette.nightShadeAlpha + lowShade - (highShade-lowShade)*((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        //g.setColor(darkColor);
        //g.fillPolygon(sidePolygons[0]);
        //Polygon[] sides = sidePolygons.clone();
        //g.setColor(lightColor);
        //g.fillPolygon(sidePolygons[1]);
        //int numSides = sides.length;
        //System.out.println("Dark Color " + darkColor);
        //System.out.println("Light Color " + lightColor);
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        //double dr = (double)(lightColor.getRed()-darkColor.getRed())/(double)(2-1);
        ///double dg = (double)(lightColor.getGreen()-darkColor.getGreen())/(double)(2-1);
        //double db = (double)(lightColor.getBlue()-darkColor.getBlue())/(double)(2-1);
        //g.setColor(new Color((int)(darkColor.getRed()), (int)(darkColor.getGreen()), (int)(darkColor.getBlue())));
        g.setColor(darkColor);
        g.fillPolygon(rightPoints[0], rightPoints[1], rightPoints[0].length);
        g.setColor(lightColor);
        g.fillPolygon(leftPoints[0], leftPoints[1], leftPoints[0].length);
        //g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));

        /*for (int i = 0; i < 2; i++) {
            g.setColor(new Color((int)(darkColor.getRed() + dr*i), (int)(darkColor.getGreen() + dg*i), (int)(darkColor.getBlue() + db*i)));
            //g.fillPolygon(rightPoints[i]);
        }*/
        /*
        int[][] leftPoints = getLeftSidePoints();
        int[][] rightPoints = getRightSidePoints();
        double leftAlpha = 0.31372549019608-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED,ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha),0.5));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, leftAlpha));
            g.fillPolygon(leftPoints[0],leftPoints[1],4);
        }
        
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        double rightAlpha = 0.19607843137255-(0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        if(thisClicked)
        {
            g.setColor(ColorPalette.getLerpColor(Color.RED, ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha),0.5));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
            
        }else{
            g.setColor(ColorPalette.getLerpColor(Color.BLACK, color, rightAlpha));
            g.fillPolygon(rightPoints[0], rightPoints[1],4);
        }
        
        
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);*/
        
    }
    
    public void reverseShadeSides(Graphics g)
    {
        
        int[][] leftPoints = getLeftSidePoints();
        //int leftAlpha = 20+(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        double leftAlpha = 0.07843137254902 + (0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        g.setColor(ColorPalette.getLerpColor(Color.BLACK, ColorPalette.grassColor, leftAlpha));
        g.fillPolygon(leftPoints[0], leftPoints[1], 4);
        //g.fillPolygon(getPolyPoints2()[0], getPolyPoints2()[1], 4);
        //int rightAlpha = 50+(int)(30 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        double rightAlpha = 0.19607843137255 + (0.11764705882353 * ((WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        g.setColor(ColorPalette.getLerpColor(Color.BLACK, ColorPalette.grassColor, rightAlpha));
        int[][] rightPoints = getRightSidePoints();
        //g.fillPolygon(getPolyPoints1()[0], getPolyPoints1()[1], 4);
        g.fillPolygon(rightPoints[0], rightPoints[1], 4);
        //g.setColor(Color.BLACK);
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
    abstract void drawReflections(Graphics g);
    public boolean isVisible(Graphics g)
    {
        
        
        //Polygon p = new Polygon(myThreadedUpperPoints[0], myThreadedUpperPoints[1], myThreadedUpperPoints[0].length);
        for(int i = 0; i < myThreadedUpperPoints[0].length; i++)
        {
            Point p1 = new Point(myThreadedUpperPoints[0][i], myThreadedUpperPoints[1][i]);
            Point p2 = new Point(myThreadedLowerPoints[0][i], myThreadedLowerPoints[1][i]);
            
            if(WorldPanel.frameBounds.contains(p1) || WorldPanel.frameBounds.contains(p2)) //g.getClip().contains(p1) || g.getClip().contains(p2))
            {
                /*Rectangle[] rects = getBoundingRects();
                int numCovered = 0;
                for(Rectangle r : rects)
                {
                    Point[] points = {new Point((int)r.getX(), (int)r.getY()), new Point((int)(r.getX()+r.getWidth()), (int)r.getY()), new Point((int)(r.getX()+r.getWidth()), (int)(r.getY()+r.getHeight())), new Point((int)(r.getX()), (int)(r.getY()+r.getHeight()))};
                    
                    for(Point p : points)
                    {
                        if(RenderCut.pointCovered(index, (int)p.getX(), (int)p.getY()))
                        {
                            numCovered++;
                        }
                    }
                }
                if(numCovered == 8)
                {
                    return false;
                }
                return true;
                */
                return true;
            }
        }
        /*
        if(Player.boundTile != this)
        {
            return (g.getClip().contains(p.getBounds()) || g.getClip().contains(threadedTilePolygon.getBounds()));
        }*/
        return false;
    }
    
    
    public void drawEarlyScenery(Graphics g)
    {
        for(Scenery s : earlyDrawScenery)
        {
            s.draw(g);
        }
        for(Path p : pathList)
        {
            p.draw(g);
        }
    }
    public void drawHitPolygon(Graphics g)
    {
        if(inTransit)
        {
            g.setColor(new Color(255, 0, 0, 80));
            g.fillPolygon(hitPolygon);
        }
    }
    public abstract void draw(Graphics g);
    
    /*
    PROBLEM: change name to something more specific to what it does -- no longer just draws assorted scenery, but other tile-related graphics that all tiles can easily call
    Draws assorted scenery, player if on the tile, its drop shadow, etc.
    */
    public void drawAssortedScenery(Graphics g)
    {
        boolean playerDrawn = false;
        int sceneryCount = 0;
        
        for(double d = 0; d < grassList.length; d+= grassSkip)
        {
            if(d == (int)d)
            {
                int i = (int)d;
                if(assortedScenery.size() > 0 && assortedScenery.get(sceneryCount).getSortDistanceConstant()>grassList[i].getSortDistanceConstant() && sceneryCount < assortedScenery.size()-1)
                {
                    if(assortedScenery.get(sceneryCount).isVisible(g))
                    {
                        assortedScenery.get(sceneryCount).draw(g);
                    }
                    if(player.getSortDistanceConstant() <= assortedScenery.get(sceneryCount).getMiddleSortDistanceConstant() && player.getSortDistanceConstant() >= assortedScenery.get(sceneryCount+1).getMiddleSortDistanceConstant() && !playerDrawn)
                    {
                        playerDrawn = true;
                        drawPlayer(g, Player.xPoint, Player.yPoint, Player.shadowExpand);
                    }
                    sceneryCount++;
                }
                if(grassList[i].isVisible(g))
                {
                    grassList[i].drawTufts(g);
                }
            }
        }
        for(int i = sceneryCount; i < assortedScenery.size(); i++)
        {
            if(assortedScenery.get(sceneryCount).isVisible(g))
            {
                assortedScenery.get(i).draw(g);
            }
        }
        if(!playerDrawn)
        {
            playerDrawn = true;
            drawPlayer(g, Player.xPoint, Player.yPoint, Player.shadowExpand);
        }
        /*if(assortedScenery.size() > 0)
        {
            for(int i = 0; i < assortedScenery.size(); i++)
            {
                assortedScenery.get(i).draw(g);
                if(i < assortedScenery.size() - 1 && player.getSortDistanceConstant() <= assortedScenery.get(i).getMiddleSortDistanceConstant() && player.getSortDistanceConstant() >= assortedScenery.get(i+1).getMiddleSortDistanceConstant() && !playerDrawn)
                {
                    playerDrawn = true;
                    drawPlayer(g, Player.xPoint, Player.yPoint, Player.shadowExpand);
                }
            }
            if(!playerDrawn)
            {
                playerDrawn = true;
                drawPlayer(g, Player.xPoint, Player.yPoint, Player.shadowExpand);
            }
        }*/
        
    }
    
    public void drawPlayer(Graphics g, int xIn, int yIn, double shadowExpand)
    {
        if((player != null && player.getIntersectingTile() != null && player.getIntersectingTile() == this))
        {
            g.setColor(new Color(0,0,0,50));
            int expandPixels = 2;
            g.fillOval((int)Math.round(xIn - ((6 + (expandPixels*shadowExpand))*WorldPanel.scale)), (int)Math.round(yIn-((6 + (expandPixels*shadowExpand))*WorldPanel.getShrink*WorldPanel.scale)), (int)Math.round((12 + (expandPixels*2*shadowExpand))*WorldPanel.scale), (int)Math.round((12+(expandPixels*2*shadowExpand))*WorldPanel.getShrink*WorldPanel.scale));
            player.draw(g);
        }
    }
    
    /*
    ----------------------------------------------------------------------------
    Getters:
    */
    
    
    public int getPointCoordY(int point){return (int)y + ((int)((double)point/2.0)*length);}
    /*
    PROBLEM: Consider renaming to getCoordX();
    returns the coordinate x of the tile. (bottom left). Returns movingX if not in transit for sake of the paint order algorithm being able to sort it ahead of its actual paint position, as its draw position is dictated by the x coord, but it can be sorted based off of its rawX
    */
    public double getRawX()
    {
        if(!inTransit)
        {
            return x;
        }
        return movingX;
    }
    
    /*
    PROBLEM: Consider renaming to getCoordY();
    returns the coordinate y of the tile. (bottom left). Returns movingY if not in transit for sake of the paint order algorithm being able to sort it ahead of its actual paint position, as its draw position is dictated by the x coord, but it can be sorted based off of its rawX
    */
    public double getRawY()
    {   
        if(!inTransit)
        {
            return y;
        }
        return movingY;
    }
    
    public double getMiddleCoordX(){return x + ((double)width/2.0);}//returns the coordinate of the middle of the tile
    public double getMiddleCoordY(){return y + ((double)length/2.0);}
    public int getRawWidth(){return width;}
    public int getRawLength(){return length;}
    public int getRawHeight(){return (int)height;}
    public int getPointCoordX(int point){return (int)x + ((1-(int)((double)point/2.0))*width);}
    public Thread getThread(){return thread;}
    /*
    returns the points that make up the polygon of the left side of the bounding box of the tile.
    */
    public int[][] getLeftSidePoints()
    {
        if((WorldPanel.radSpin > (Math.PI/2.0) && WorldPanel.radSpin < (Math.PI)) || (WorldPanel.radSpin > (3*Math.PI/2.0) && WorldPanel.radSpin < (2*Math.PI)))
        {
            return polyPoints2;
        }else{
            return polyPoints1;
        }
    }
    
    /*
    returns the points that make up the polygon of the right side of the bounding box of the tile.
    */
    public int[][] getRightSidePoints()
    {
        if((WorldPanel.radSpin > (Math.PI/2.0) && WorldPanel.radSpin < (Math.PI)) || (WorldPanel.radSpin > (3*Math.PI/2.0) && WorldPanel.radSpin < (2*Math.PI)))
        {
            return polyPoints1;
        }else{
            return polyPoints2;
        }
    }
    public Rectangle[] getBoundingRects()
    {
        Polygon p1 = new Polygon(polyPoints1[0], polyPoints1[1], 4);
        Polygon p2 = new Polygon(polyPoints2[0], polyPoints2[1], 4);
        Rectangle[] giveReturn = {p1.getBounds(), p2.getBounds()};
        return giveReturn;
    }
    
    public ArrayList<Scenery> getSceneryList(){return sceneryList;}
    public ArrayList<Lake> getLakes(){return lakes;}
    public ArrayList<Tree> getTrees(){return trees;}
    public ArrayList<Waterfall> getWaterfalls(){return waterfalls;}
    public ArrayList<Path> getPathList(){return pathList;}
    public double getSpin(){return spin;}
    public Polygon getLowerPolygon(){return threadedTilePolygon;}//returns the lower polygon of the bounding box of this tile. Normally used for collision detection.
    public Polygon getUpperPolygon(){return new Polygon(myThreadedUpperPoints[0], myThreadedUpperPoints[1], 4);}//returns the upper polygon of the bounding box of this tile. Normally used for point collision detection.
    public Polygon getHitPolygon(){return hitPolygon;}    
    public Color getColor(){return color;}
    public Grass[] getGrassList(){return grassList;}
    /*
    ----------------------------------------------------------------------------
    Setters:
    */
    
    /*
    sets the player so that the tile can use it. Isn't passed through constructor
    */
    public void setPlayer(Player p){player = p;}
    
    /*
    called by tiles that are able to be spun to signify that this tile can be rotated.
    */
    public void setSpinnable(boolean b){spinnable = b;}
    
    
    
    //public void setIndex(int i){index = i;}
    
    
    /*
    setters
    */
    public void setAssortedScenery(ArrayList<Scenery> s){assortedScenery = s;}
    public void setMoveable(boolean b){moveable = b;}
    public void addScenery(Scenery s){sceneryList.add(s);}
    public void addLake(Lake l){lakes.add(l);}
    public void addTree(Tree t){trees.add(t);}
    public void addWaterfall(Waterfall wf){waterfalls.add(wf);}
    public void addPath(Path p){pathList.add(p);}
    public void addAssortedScenery(Scenery s){assortedScenery.add(s);}
    public void addGrass(Grass g)
    {
        Grass[] copy = new Grass[grassList.length + 1];
        for(int i = 0; i < grassList.length; i++)
        {
            copy[i]=grassList[i];
        }
        copy[copy.length - 1] = g;
        grassList = copy;
    }
    public void removeGrass(int index)
    {
        Grass[] copy = new Grass[grassList.length - 1];
        for(int i = 0; i < grassList.length; i++)
        {
            if(i < index)
            {
                copy[i]=grassList[i];
            }else if(i > index)
            {
                copy[i-1]=grassList[i];
            }
        }
        grassList = copy;
    }
    public void setSpin(double spinIn){spin = spinIn;}
    public void setColor(Color c){color = c;}
    public void addEarlyDrawScenery(Scenery s){earlyDrawScenery.add(s);}
    
    
    
   
    
    
    
    
    public static void setGrassSkip()
    {
        if(!DayNight.season.equals("winter"))//winter is laggier since snow is graphics intensive. Also grass is less noticeable in the snow so it doesn't matter as much to draw fewer of it.
        {
            grassSkip = (int)Math.pow(1.5,(int)(3.0-((WorldPanel.scale-WorldPanel.minScale)/((WorldPanel.maxScale-WorldPanel.minScale)/3.0))));//gives the amount the grass list is incremented for the grass being drawn -- e.g. how many grasses to skip so that you draw fewer when zoomed out, more when zoomed in for optimization.
        }else{
            grassSkip = (int)Math.pow(2.5,(int)(3.0-((WorldPanel.scale-WorldPanel.minScale)/((WorldPanel.maxScale-WorldPanel.minScale)/3.0))));//gives the amount the grass list is incremented for the grass being drawn -- e.g. how many grasses to skip so that you draw fewer when zoomed out, more when zoomed in for optimization.
        }
    }
    
    /*
    adds randomly placed flowers and scenery
    */
    private void addRandomFlowers(int heightMin, int heightMax)
    {
        int min = getArea()*2;
        int max = getArea()*4;
        double radiusApart = 0.05;
        int numFlowers = min+(int)(max*Math.random());
        for(int i = 0; i < numFlowers; i++)
        {
            double randomX = radiusApart + 0.05*(int)((1-2*radiusApart)*Math.random()/radiusApart);
            double randomY = radiusApart + 0.05*(int)((1-2*radiusApart)*Math.random()/radiusApart);
            int randomHeight = heightMin + ((int)(Math.random()*(heightMax-heightMin))/(heightMax-heightMin));
            Flower m = new Flower(this,randomX,randomY,randomHeight, 1.0);
            if(m.getOffsetX() < m.getBoundingBoxWidth()/(double)getRawWidth())
            {
                m.addUnitsToOffset(m.getBoundingBoxWidth()/2.0, 0);
            }else if(m.getOffsetX() > m.getBoundingBoxWidth()/(double)(1-getRawWidth()))
            {
                m.addUnitsToOffset(-m.getBoundingBoxWidth()/2.0, 0);
            }
            if(m.getOffsetY() < m.getBoundingBoxLength()/(double)getRawLength())
            {
                m.addUnitsToOffset(0, m.getBoundingBoxLength()/2.0);
            }else if(m.getOffsetY() > m.getBoundingBoxLength()/(double)(1-getRawLength()))
            {
                m.addUnitsToOffset(0, -m.getBoundingBoxLength());
            }
        }
    }
    
    /*
    Adds randomly spawned scenery to the tile.
    */
    private void addRandomScenery()
    {
        addRandomFlowers(5,10);
        int numShrooms = (int)(Math.round(Math.random()));
        double radiusApart = 0.05;
        int numPerUnit = 9;
        for(int i = 0; i < numShrooms; i++)
        {
            double randomX = radiusApart + radiusApart*(int)((1-2*radiusApart)*Math.random()/radiusApart);
            double randomY = radiusApart + radiusApart*(int)((1-2*radiusApart)*Math.random()/radiusApart);
            //int randomHeight = heightMin + ((int)(Math.random()*(heightMax-heightMin))/(heightMax-heightMin));
            Mushroom m = new Mushroom(this,randomX,randomY, 0.25+(Math.random()*.5));
            if(m.getOffsetX() < m.getBoundingBoxWidth()/(double)getRawWidth())
            {
                m.addUnitsToOffset(m.getBoundingBoxWidth()/2.0, 0);
            }else if(m.getOffsetX() > m.getBoundingBoxWidth()/(double)(1-getRawWidth()))
            {
                m.addUnitsToOffset(-m.getBoundingBoxWidth()/2.0, 0);
            }
            if(m.getOffsetY() < m.getBoundingBoxLength()/(double)getRawLength())
            {
                m.addUnitsToOffset(0, m.getBoundingBoxLength()/2.0);
            }else if(m.getOffsetY() > m.getBoundingBoxLength()/(double)(1-getRawLength()))
            {
                m.addUnitsToOffset(0, -m.getBoundingBoxLength());
            }
        }
        /*for(int i = 0; i < width*numPerUnit; i++)
        {
            for(int j = 0; j < length*numPerUnit; j++)
            {
                if(Math.random() < .45)
                {
                    double grassX = (0.05/(double)width)+(double)i/((double)width*numPerUnit);
                    double grassY = (0.05/(double)length)+(double)j/(double)(length*numPerUnit);
                    Grass g = new Grass(this, grassX, grassY);
                }
            }
            
        }*/
        /*
        double placeX = 0;
        double placeY = 0;
        for(double startX = 0.05; startX < .95; startX+= 0.05)
        {
            placeX = startX;
            placeY = 0.05;
            for(int i = 0; i < startX/0.1; i++)
            {
                Grass g = new Grass(this, placeX, placeY);
                placeX -= 0.1;
                placeY += 0.1;
            }
        }
        for(double startY = 0.05; startY < .95; startY+= 0.05)
        {
            placeY = startY;
            placeX = .95;
            for(int i = 0; i < (1-startY)/0.1; i++)
            {
                Grass g = new Grass(this, placeX, placeY);
                placeX -= 0.1;
                placeY += 0.1;
            }
        }
        */
        
    }
    
    public void removeCoveredGrass()
    {
        for(Path p : pathList)
        {
            for(int i = 0; i < grassList.length; i++)
            {
                if(p.pathOnPoint(grassList[i].getX(),grassList[i].getY()))
                {
                    removeGrass(i);
                    i--;
                }
                /*if(p.pathOnCoord(assortedScenery.get(i).getCoordX(), assortedScenery.get(i).getCoordY()))
                {
                    assortedScenery.remove(assortedScenery.get(i));
                    i--;
                }*/
            }
        }
    }
    
    public void removeCoveredScenery()
    {
        for(Path p : pathList)
        {
            for(int i = 0; i < assortedScenery.size(); i++)
            {
                if(p.pathOnPoint(assortedScenery.get(i).getX(),assortedScenery.get(i).getY()))
                {
                    assortedScenery.remove(i);
                    i--;
                }
                /*if(p.pathOnCoord(assortedScenery.get(i).getCoordX(), assortedScenery.get(i).getCoordY()))
                {
                    assortedScenery.remove(assortedScenery.get(i));
                    i--;
                }*/
            }
        }
    }
    
    
    
    
    public void setThread(Thread t){thread = t;}
    
    
    
    public void setUpperPoints()//should this be working with the threaded upper points? Code works but...
    {
        for(int i = 0; i < myThreadedLowerPoints[0].length; i++)
        {
            myThreadedUpperPoints[0][i]=myThreadedLowerPoints[0][i];
            myThreadedUpperPoints[1][i]=myThreadedLowerPoints[1][i]-(int)(WorldPanel.scale * WorldPanel.distortedHeight(WorldPanel.rotation, getHeight()));;
        }
        //myThreadedUpperPoints = myThreadedLowerPoints.clone();//myThreadedLowerPoints.clone();//{getPoints()[0],getPoints()[1]};
        /*for(int i = 0; i < 4; i++)
        {
            myThreadedUpperPoints[1][i]-=(int)(WorldPanel.scale * WorldPanel.distortedHeight(WorldPanel.rotation, getHeight()));
        }*/
        
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
        if(WorldPanel.getSpinQuadrant() == 3)
        {
            return 0;
        }else if(WorldPanel.getSpinQuadrant() == 4)
        {
            return 1;
        }
        return WorldPanel.getSpinQuadrant() + 1;
        
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
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            Tile currentTile = TileDrawer2.tileList.get(i);
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
    
    /*private boolean noOtherTilesClicked()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(TileDrawer2.tileList.get(i).getClicked())
            {
                return false;
            }
        }
        return true;
    }*/
    
    /*private void unclickAllTiles()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(TileDrawer2.tileList.get(i) != this)
            {
                TileDrawer2.tileList.get(i).setClicked(false);
                TileDrawer2.tileList.get(i).setInTransit(false);
            }
        }
    }*/
    
    
    
    
    public static void unclickEveryTile()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            TileDrawer2.tileList.get(i).setClicked(false);
            TileDrawer2.tileList.get(i).setInTransit(false);
            
        }
    }
    
    private void sortScenery(Scenery[] sceneryIn)
    {
        for(int i = 0; i < sceneryIn.length-1; i++)
        {
            int smallestIndex = i;
            for(int j = i+1; j < sceneryIn.length; j++)
            {
                if(sceneryIn[j].getSortDistanceConstant() > sceneryIn[smallestIndex].getSortDistanceConstant())
                {
                    smallestIndex = j;
                }
            }
            Scenery tempScenery = sceneryIn[i];
            sceneryIn[i]= sceneryIn[smallestIndex];
            sceneryIn[smallestIndex]= tempScenery;
        }
    }
    
    private void sortScenery(ArrayList<Scenery> sceneryIn)
    {
        for(int i = 0; i < sceneryIn.size()-1; i++)
        {
            int smallestIndex = i;
            for(int j = i+1; j < sceneryIn.size(); j++)
            {
                if(sceneryIn.get(j).getSortDistanceConstant() > sceneryIn.get(smallestIndex).getSortDistanceConstant())
                {
                    smallestIndex = j;
                }
            }
            Scenery tempScenery = sceneryIn.get(i);
            sceneryIn.set(i, sceneryIn.get(smallestIndex));
            sceneryIn.set(smallestIndex, tempScenery);
        }
    }
    
    
    
    /*private void sortTrees()
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
    }*/
    public void mouseInteraction()
    {
        if(!thisClicked)
        {
            color = Toolbox.grassColor;
        }
        
        if(!thisClicked && tileClicked())
        {
            
            thisClicked = true;
            color = Color.RED;
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
        }else if(!tileClicked() && MouseInput.clicked && thisClicked && !inTransit)
        {
            if(Player.boundTile != this)
            {
                if(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] >= x && convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] <= x+width)
                {
                    int endPos;
                    if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]) > y)
                    {
                        endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] - length  + 1);

                    }else{
                        endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1]);
                    }
                    if(pathIsClear((int)x, (int)y, (int)x, endPos) )
                    {
                        oldPos[0]=x;
                        oldPos[1]=y;
                        transitPos[0]=(int)x;
                        movingX = x;
                        movingY = y;
                        transitPos[1] = endPos;
                        inTransit = true;
                    }else{
                        thisClicked = false;
                        tileJustUnclicked = true;
                        if(heightChangeable)
                        {
                            MouseInput.scrollType = "Zoom";
                        }
                        color = Toolbox.grassColor;
                    }
                }else if(convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] >= y && convertToUnit(MouseInput.clickX, MouseInput.clickY)[1] <= y+length)
                {
                    int endPos;
                    if(Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0])>x)
                    {
                        endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0] - width + 1);

                    }else{
                        endPos=(int)Math.floor(convertToUnit(MouseInput.clickX, MouseInput.clickY)[0]);
                    }
                    if(pathIsClear((int)x, (int)y, endPos, (int)y) )
                    {
                        oldPos[0]=x;
                        oldPos[1]=y;
                        movingX = x;
                        movingY = y;
                        transitPos[0]=endPos;
                        transitPos[1]=(int)y;

                        inTransit = true;
                    }else{
                        thisClicked = false;
                        tileJustUnclicked = true;
                        if(heightChangeable)
                        {
                            MouseInput.scrollType = "Zoom";
                        }
                        color = Toolbox.grassColor;
                    }
                }
            }else{
                thisClicked = false;
                tileJustUnclicked = true;
                if(heightChangeable)
                        {
                            MouseInput.scrollType = "Zoom";
                        }
                        color = Toolbox.grassColor;
            }
        }
        
    }
    
    
    
    public boolean pathIsClear(int startX, int startY, int endX, int endY)
    {
        
        //System.out.println("Start X: " + startX + " Start Y: " + startY + " End X: " + endX + " End Y: " + endY);
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
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            Tile currentTile = TileDrawer2.tileList.get(i);
            if(xIn >= currentTile.getRawX() && xIn <= currentTile.getRawX() + currentTile.getRawWidth() && yIn >= currentTile.getRawY() && yIn <= currentTile.getRawY() + currentTile.getRawLength() && currentTile != this)//!= this so that a tile can't be blocking its own path
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean atCoord(double xIn, double yIn)
    {
        return (xIn >= getRawX() && xIn <= getRawX() + getRawWidth() && yIn >= getRawY() && yIn <= getRawY() + getRawLength());//!= this so that a tile can't be blocking its own path
           
    }
    
    private Polygon getUpdatedHitPolygon()
    {
        /*int[]xPoints = { (int)convertToPoint(oldPos[0] + 0.25, oldPos[1])[0], (int)(convertToPoint(oldPos[0] + width - 0.25, oldPos[1]))[0], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[0]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[0])};
        int[]yPoints = { (int)convertToPoint(oldPos[0] + 0.25, oldPos[1])[1], (int)(convertToPoint(oldPos[0]+ width - 0.25, oldPos[1]))[1], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[1]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[1])};*/
        //int[]xPoints = { (int)convertToPoint(movingX + 0.25, movingY)[0], (int)(convertToPoint(movingX + width - 0.25, movingY))[0], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[0]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[0])};
        //int[]yPoints = { (int)convertToPoint(movingX + 0.25, movingY)[1], (int)(convertToPoint(movingX + width - 0.25, movingY))[1], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[1]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[1])};
        //return new Polygon(xPoints, yPoints, 4);
        if(oldPos[0] == transitPos[0])//y is moving
        {
            if(transitPos[1] < oldPos[1])
            {
                int[]xPoints = { (int)convertToPoint(movingX + 0.25, movingY)[0], (int)(convertToPoint(movingX + width - 0.25, movingY))[0], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[0]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[0])};
                int[]yPoints = { (int)convertToPoint(movingX + 0.25, movingY)[1], (int)(convertToPoint(movingX + width - 0.25, movingY))[1], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[1]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[1])};
                return new Polygon(xPoints, yPoints, 4);
            }else{
                int[]xPoints = { (int)convertToPoint(movingX + 0.25, movingY + length)[0], (int)(convertToPoint(movingX + width - 0.25, movingY + length))[0], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] - 0.25 + length)[0]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] - 0.25 + length)[0])};
                int[]yPoints = { (int)convertToPoint(movingX + 0.25, movingY + length)[1], (int)(convertToPoint(movingX + width - 0.25, movingY + length))[1], (int)(convertToPoint(transitPos[0] + width - 0.25, transitPos[1] - 0.25 + length)[1]), (int)(convertToPoint(transitPos[0] + 0.25, transitPos[1] - 0.25 + length)[1])};
                return new Polygon(xPoints, yPoints, 4);
            }
        }else//x is moving
        {
            if(transitPos[0] < oldPos[0])
            {
                int[] xPoints = {(int)convertToPoint(movingX, movingY + 0.25)[0], (int)convertToPoint(movingX, movingY + length - 0.25)[0], (int)convertToPoint(transitPos[0] + 0.25, transitPos[1] + length - 0.25)[0], (int)convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[0]};
                int[] yPoints = {(int)convertToPoint(movingX, movingY + 0.25)[1], (int)convertToPoint(movingX, movingY + length - 0.25)[1], (int)convertToPoint(transitPos[0] + 0.25, transitPos[1] + length - 0.25)[1], (int)convertToPoint(transitPos[0] + 0.25, transitPos[1] + 0.25)[1]};
                return new Polygon(xPoints, yPoints, 4);
            }else{
                int[] xPoints = {(int)convertToPoint(movingX + width, movingY + 0.25)[0], (int)convertToPoint(movingX + width, movingY + length - 0.25)[0], (int)convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + length - 0.25)[0], (int)convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[0]};
                int[] yPoints = {(int)convertToPoint(movingX + width, movingY + 0.25)[1], (int)convertToPoint(movingX + width, movingY + length - 0.25)[1], (int)convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + length - 0.25)[1], (int)convertToPoint(transitPos[0] + width - 0.25, transitPos[1] + 0.25)[1]};
                return new Polygon(xPoints, yPoints, 4);
            }
        }
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
        if(WorldPanel.getSpinQuadrant() == 1)
        {
            int [][] tempPoints1 = {{myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][1]},{myThreadedUpperPoints[1][1], myThreadedUpperPoints[1][2], threadedPoints()[1][2],threadedPoints()[1][1]}};
            int [][] tempPoints2 = {{myThreadedUpperPoints[0][0], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][0]},{myThreadedUpperPoints[1][0], myThreadedUpperPoints[1][1], threadedPoints()[1][1],threadedPoints()[1][0]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }else if(WorldPanel.getSpinQuadrant() == 2){
            int[][] tempPoints1 = {{myThreadedUpperPoints[0][1], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][1]},{myThreadedUpperPoints[1][1], myThreadedUpperPoints[1][2], threadedPoints()[1][2],threadedPoints()[1][1]}};
            int[][] tempPoints2 = {{myThreadedUpperPoints[0][2], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][3], myThreadedUpperPoints[0][2]},{myThreadedUpperPoints[1][2], myThreadedUpperPoints[1][3], threadedPoints()[1][3],threadedPoints()[1][2]}};
            
            polyPoints1 = tempPoints1.clone();
            polyPoints2 = tempPoints2.clone();
        }else if(WorldPanel.getSpinQuadrant() == 3){
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
        }else{
            color = Toolbox.grassColor;
        }
        
        if(tileContainsPoint(MouseInput.x, MouseInput.y)&& MouseInput.clicked && !thisClicked && !inTransit && !Player.pathIsClicked && Player.boundTile != this && !Player.inTransit)
        {
            thisClicked = true;
            
            
            rightClicked = false;
            inTransit = true;
            
            if(heightChangeable)
            {
                MouseInput.scrollType = "Height";
            }
            MouseInput.clicked = false;
            MouseInput.rightClicked = false;
            //System.out.println("Was clicked");
        }else if(tileContainsPoint(MouseInput.x, MouseInput.y) && MouseInput.rightClicked && !thisClicked && !inTransit && Player.boundTile != this && !Player.inTransit)
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
            inTransit = true;
            thisClicked = true;
            if(heightChangeable)
            {
                MouseInput.scrollType = "Height";
            }
            MouseInput.clicked = false;
            MouseInput.rightClicked = false;
            //System.out.println("Was clicked");
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
        int quad = WorldPanel.getSpinQuadrant();
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
        int quad = WorldPanel.getSpinQuadrant();
        int[] coords = getBottomCornerCoordinates();
        if(quad == 1 || quad == 3)
        {
            return coords[1] - (-1* coords[0]);
        }else{
            return coords[1] - (coords[0]);
        }
    }
    
    public int getBottomCornerOrderPos()
    {
        int quad = WorldPanel.getSpinQuadrant();
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
    
    public void sortAllScenery()
    {
        sortScenery(earlyDrawScenery);
        sortScenery(assortedScenery);
        sortScenery(grassList);
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
        setUpperPoints();
        //myThreadedUpperPoints = getUpperPoints().clone();
        //threadedGrassImage = new BufferedImage((int)threadedTilePolygon.getBounds().getX(), (int)threadedTilePolygon.getBounds().getY(), BufferedImage.TYPE_INT_ARGB);
        
        /*Graphics2D g2 = grassImage.createGraphics();
        for(Grass g : grassList)
        {
            g.drawTufts(g2);
        }
        g2.dispose();*/
        
        if(numClickedTiles() > 1)
        {
            selectClosestOfClickedTiles();
        }
        if(inTransit)
        {
            hitPolygon = getUpdatedHitPolygon();
        }
        /*sortScenery(earlyDrawScenery);
        sortScenery(assortedScenery);
        sortScenery(grassList);*/
        calculatePolyPoints();
        threadedTilePolygon = new Polygon(myThreadedLowerPoints[0], myThreadedLowerPoints[1],4);
        /*if(moveable)
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
        }*/
        
    }
    
    public void tileMovement()
    {
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
