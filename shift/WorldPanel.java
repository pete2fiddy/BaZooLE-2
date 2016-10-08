package shift;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WorldPanel extends JPanel implements ActionListener, ChangeListener, Runnable
{
    private Timer tickTimer;
    private boolean drawWater = true;
    private int tempQuadrant, frameCount;
    private long startTime;
    public static int baseUnit = 75, baseMapWidth = 1050, baseMapHeight = 1050, baseMapRadius = baseMapWidth/2, baseMapThickness = 10;
    public static double baseStraightUnit = (double)baseUnit/Math.sqrt(2);
    public static double fps;
    public static double scale;
    public static int unit = 75, mapRadius = baseMapRadius, mapThickness = baseMapThickness;
    public static double straightUnit = (double)unit/Math.sqrt(2);
    public static final int screenWidth = Frame.screenWidth, screenHeight = Frame.screenHeight, fpsCap = 160;
    public static int mapWidth = 1050, mapHeight = 1050;
    public static int widthHalf = screenWidth/2, heightHalf = screenHeight/2, worldTilesWidth = mapWidth/unit, worldTilesHeight = mapHeight/unit, mapRadiusUnits = mapRadius/unit;//RADIUS GOES FROM CORNER TO CORNER
    public static int[][] mapPoints;
    public static double squareWidth = (double)mapWidth/Math.sqrt(2), squareRadius = (double)baseMapRadius/Math.sqrt(2);//straightUnit is the width and height of a single unit... Accidentally coded so that a unit was measured from corner to corner at 45 degrees
    public static double worldX, worldY;
    public static double rotation, rotationFraction, tempRotation, spin, spinCalc, radSpin;
    public static double getShrink;
    private UI ui;
    private static double backgroundColorRotation = 0;
    public static BufferedImage grassImage, leavesImage;
    public static TexturePaint grassTexture, leavesTexture;;
    private Object loopNotify = new Object();
    private JButton turnLeft, turnRight, resetLevel;
    Audio a = new Audio();
    public static Color backgroundColor = new Color(0, 65 + (int)(Math.abs(100*Math.sin(backgroundColorRotation))), 198);
    private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
    public static DayNight dayNight = new DayNight();
    private JButton randomShapes;
    private Timer secondTimer;
    TileSorter ts;
    TileDrawer td;
    private TileDrawer2 td2 = new TileDrawer2(this);
    Input input = new Input();
    MouseInput mouseInput = new MouseInput(this);
    Player player = new Player(30, 30, 5);
    private LevelLoader levelLoader = new LevelLoader(player, this);
    private WaterRipple[] waterRipples = new WaterRipple[8];
    private Toolbox toolbox = new Toolbox(this, player);
    private int timeCount = 0;
    private Timer frameTimer=new Timer(2, this);
    public WorldPanel()
    {
        //panel settings and nuts and bolts methods
        setBounds(0,0,screenWidth, screenHeight);
        setOpaque(true);
        setDoubleBuffered(true);
        addKeyListener(input);
        addMouseListener(mouseInput);
        addMouseWheelListener(mouseInput);
        
        //variable initialization
        initVariables();
        
        //add buttons
        initButtons();
        
        //"talk-to" instances created.
        ts = new TileSorter();
        td = new TileDrawer();
        tickTimer.start();//started here so that everything is initialized by the time the timer calls them
    }
    
    /*
    initializes variables
    */
    private void initVariables()
    {
        worldX = screenWidth/2; worldY=3*screenHeight/5; rotation = Math.toRadians(75); spin = 0; spinCalc = spin+Math.PI + (Math.PI/4); radSpin = spinCalc - (Math.PI/2); tempQuadrant = spinQuadrant();
        mapPoints = mapTopPoints(spin, mapRadius);
        frameCount = 0;
        scale = 2.0;
        ui = new UI(this);
        try{
            if(dayNight.getSeason().equals("winter"))
            {
                BufferedImage snow = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                Graphics g = snow.getGraphics();
                g.setColor(new Color(251, 251, 251));
                g.fillRect(0,0, 256, 256);
                grassImage = snow;
            }else{
                BufferedImage dirt = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                Graphics g = dirt.getGraphics();
                g.setColor(new Color(86, 65, 46));//(120, 72, 0));
                g.fillRect(0,0, 256, 256);
                grassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
            }
            //grassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
            grassTexture = new TexturePaint(grassImage, new Rectangle(0, 0, 256, 256));
            
            leavesImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Leaves3.png"));
            leavesTexture = new TexturePaint(leavesImage, new Rectangle((int)worldX, (int)worldY, leavesImage.getWidth(), leavesImage.getHeight()));
        }catch(Exception e)
        {
            System.err.println(e);
        }
        tickTimer = new Timer(5, this);
        tickTimer.setActionCommand("tick");
        tickTimer.setRepeats(true);
        secondTimer = new Timer(1000, this);
        secondTimer.setActionCommand("second");
        secondTimer.setRepeats(true);
        secondTimer.start();
        frameTimer.setRepeats(true);
        frameTimer.setActionCommand("frame");
        frameTimer.start();
    }
    
    /*
    initializes buttons
    */
    private void initButtons()
    {
        volumeSlider.addChangeListener(this);
        volumeSlider.setBounds(0, 0, 100, 50);
        volumeSlider.setVisible(false);
        add(volumeSlider);
        setLayout(null);
        randomShapes = new JButton("New Shapes");
        randomShapes.addActionListener(this);
        randomShapes.setActionCommand("randomShapes");
        randomShapes.setBounds(1200, 100, 100, 100);
        
        turnLeft = new JButton("<<");
        turnLeft.addActionListener(this);
        turnLeft.setActionCommand("turnLeft");
        turnLeft.setBounds((screenWidth/2)-150, screenHeight - 150, 100, 50);
        add(turnLeft);
        turnLeft.setVisible(false);
        
        turnRight = new JButton(">>");
        turnRight.addActionListener(this);
        turnRight.setActionCommand("turnRight");
        turnRight.setBounds((screenWidth/2)+50, screenHeight - 150, 100, 50);
        add(turnRight);
        turnRight.setVisible(false);
        
        resetLevel = new JButton("Reset Level");
        resetLevel.addActionListener(this);
        resetLevel.setActionCommand("resetLevel");
        resetLevel.setBounds(10, 120, 100, 50);
        add(resetLevel);
        resetLevel.setVisible(false);
    }
    
    /*
    the MAIN paint method for the project. Everything painted from here, or from instances called from here.
    */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        g.setClip(0,0, screenWidth, screenHeight);
        setBackground(dayNight.getColor());//sets the color of the background based on the trig values of backgroundColorRotaion
        frameCount++;//adds one to the number of frames so that the FPS counter knows how many frames have passed since the last interval
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(Toolbox.worldStroke);
        dayNight.drawStars(g);
        drawMapFloor(g);
        
        td2.draw(g);
        fillBelowMap(g);
        
        player.drawPlayersChain(g);//draws the player's chain on top of everything else being drawn so it can always be easily seen
        player.drawTransparentPlayer(g);//draws a transparent player superimposed over where the player is being drawn so that it can be see-through if covered by something.
        ui.draw(g);//draws UI elements like level, etc.
        
        drawFPS(g, g2);
    }
    
    private void drawFPS(Graphics g, Graphics2D g2)
    {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Futura", Font.PLAIN, 16));
        g.drawString("FPS: " + Integer.toString((int)fps), 30, 100);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    
    /*
    Stretches and distorts the world textures so that they properly fit the rotation, scaling, y distortion of the world. Leaves aren't textured, so it is doing extra work by texturing them. Kept it for sake of wanting to texture leaves at some point
    */
    private void renderTextures()
    {
        if(dayNight.getSeason().equals("summer"))
        {
            try {
                grassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
                grassTexture = new TexturePaint(grassImage, new Rectangle(0, 0, 256, 256));
            } catch (Exception e) {
            }
        }else if(dayNight.getSeason().equals("winter"))
        {
            BufferedImage snow = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics g = snow.getGraphics();
            g.setColor(new Color(251, 251, 251));
            g.fillRect(0,0, 256, 256);
            grassImage = snow;
        }
        try
        {
            grassTexture = new TexturePaint(grassImage, new Rectangle((int)worldX, (int)worldY, (int)(scale*128), (int)(scale*128*getShrink)));
            leavesTexture = new TexturePaint(leavesImage, new Rectangle((int)worldX, (int)worldY, (int)(0.5*scale*leavesImage.getWidth()), (int)(0.5*scale*distortedHeight(rotation, leavesImage.getHeight()))));
        }catch(Exception e)
        {
            System.err.println(e);
        }
    }
    
    /*
    Consider moving to the Toolbox class. returns the current x and y coords of the position of the mouse on screen, rounded down.
    */
    public static int[] getMouseUnitPos()//basically works by "unrotating" the world and applying the same algorithm to the position of the mouse along with it so that it can compare the unrotated mouse pos with the unrotated world pos. Works as intended. 
    {
        double dx = (MouseInput.x-worldX)*shrink(rotation);//calculates unsquashed distance from the center of the world to the mouse ("unsquashing" it in the process so the calculation is what it would be on a flat world)
        double dy = MouseInput.y-worldY;//calculates unsquahsed distance from center of map to mouse.
        
        double radiusHeight = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));//finds the radius of the oval's height (since circle is squashed by the amount the world is turned)
        double radiusWidth = radiusHeight/shrink(rotation);//finds the radius of the oval's width taking into account the squahsed world
        
        double theta = Math.atan2(-dy, dx);
        
        double unturneddx = radiusWidth*Math.cos(theta-radSpin);
        double unturneddy = radiusHeight*Math.sin(theta-radSpin);
        
        int[] giveReturn = {(int)(Math.ceil(unturneddx/straightUnit)),(int)(Math.ceil(unturneddy/(straightUnit*shrink(rotation))))};
        return giveReturn;
    }
    
    /*
    Consider moving to the Toolbox class. returns the current x and y coords of the position of the mouse on screen, unrounded.
    */
    public static double[] getMouseUnitPosDouble()//basically works by "unrotating" the world and applying the same algorithm to the position of the mouse along with it so that it can compare the unrotated mouse pos with the unrotated world pos. Works as intended. 
    {
        double dx = (MouseInput.x-worldX)*shrink(rotation);//calculates unsquashed distance from the center of the world to the mouse ("unsquashing" it in the process so the calculation is what it would be on a flat world)
        double dy = MouseInput.y-worldY;//calculates unsquahsed distance from center of map to mouse.
        
        double radiusHeight = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));//finds the radius of the oval's height (since circle is squashed by the amount the world is turned)
        double radiusWidth = radiusHeight/shrink(rotation);//finds the radius of the oval's width taking into account the squahsed world
        
        double theta = Math.atan2(-dy, dx);
        
        double unturneddx = radiusWidth*Math.cos(theta-radSpin);
        double unturneddy = radiusHeight*Math.sin(theta-radSpin);
        
        double[] giveReturn = {unturneddx/straightUnit,unturneddy/(straightUnit*shrink(rotation))};
        return giveReturn;
    }
    
    /*
    Draws the physical map. Not used in normal gameplay, but when drawWater is false, will draw for sake of debugging
    */
    private void drawMap(Graphics g)
    {
        g.setColor(Color.BLACK);
        
        g.drawPolygon(mapPoints[0], mapPoints[1], 4);
        
        int tempLowerPoints[][] = new int[2][4];
        for(int i = 0; i < 4; i++)
        {
            tempLowerPoints[0][i] = mapPoints[0][i];
            tempLowerPoints[1][i] = (int)(mapPoints[1][i]+distortedHeight(rotation, mapThickness));
        }
        
        for(int i = 0; i < 4; i++)//top left, top right, bottom right, bottom left
        {
            if(i != 3)
            {
                int[] xPoints = {mapPoints[0][i], mapPoints[0][i+1], mapPoints[0][i+1], mapPoints[0][i]};
                int[] yPoints = {mapPoints[1][i], mapPoints[1][i+1], (int)(mapPoints[1][i+1]+distortedHeight(rotation, mapThickness)), (int)(mapPoints[1][i]+distortedHeight(rotation, mapThickness))};
                g.drawPolygon(xPoints, yPoints, 4);
            }else{
                int[] xPoints = {mapPoints[0][i], mapPoints[0][0], mapPoints[0][0], mapPoints[0][i]};
                int[] yPoints = {mapPoints[1][i], mapPoints[1][0], (int)(mapPoints[1][0]+distortedHeight(rotation, mapThickness)), (int)(mapPoints[1][i]+distortedHeight(rotation, mapThickness))};
                g.drawPolygon(xPoints, yPoints, 4);
            }
        }
    }
    
    /*
    outputs useful variables values about the world for sake of bug-fixing or finding cases that occur only in certain world orientations.
    */
    private void drawDebugInfo(Graphics g)//draws important dev info.
    {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Futura", Font.PLAIN, 12));
        for(int i = 0; i<4; i++)
        {
            g.drawString(i+"",mapPoints[0][i], mapPoints[1][i]);
        }
        g.drawString("Quadrant: " + Integer.toString(spinQuadrant()),125, 50);
        if(frameCount > 100)
        {
            //fps = (double)(1/((System.nanoTime()-startTime)/1000000000.0));
            //frameCount = 0;
        }
        g.drawString("FPS: " + Integer.toString((int)fps), 50, 50);
        g.drawString("Spin: "+Double.toString(radSpin), 100, 800);
        g.drawString("Scale: " + Double.toString(scale), 100, 775);
    }
    
    /*
    AREA FOR IMPROVEMENT.
    Fills the area beneath the map with the background color on top of any shadows or other protrusions from tiles, shapes, shadows, reflections, etc. that poke outside of the world. Ideally these would simply not draw beneath the bounds of the map.
    */
    private void fillBelowMap(Graphics g)
    {
        int tempLowerPoints[][] = new int[2][4];
        for(int i = 0; i < 4; i++)
        {
            tempLowerPoints[0][i] = mapPoints[0][i];
            tempLowerPoints[1][i] = mapPoints[1][i];
        }
        int[] xPoints1 = {tempLowerPoints[0][getMapCornerIndexAt("left")], tempLowerPoints[0][getMapCornerIndexAt("middle")], tempLowerPoints[0][getMapCornerIndexAt("middle")],tempLowerPoints[0][getMapCornerIndexAt("left")]};
        int[] yPoints1 = {tempLowerPoints[1][getMapCornerIndexAt("left")], tempLowerPoints[1][getMapCornerIndexAt("middle")], screenHeight, screenHeight};
        
        int[] xPoints2 = {tempLowerPoints[0][getMapCornerIndexAt("middle")], tempLowerPoints[0][getMapCornerIndexAt("right")], tempLowerPoints[0][getMapCornerIndexAt("right")], tempLowerPoints[0][getMapCornerIndexAt("middle")]};
        int[] yPoints2 = {tempLowerPoints[1][getMapCornerIndexAt("middle")], tempLowerPoints[1][getMapCornerIndexAt("right")], screenHeight, screenHeight};
        
        //g.setColor(new Color(0, 65 + (int)(Math.abs(100*Math.sin(backgroundColorRotation))), 198));//g.setColor(new Color(30, 144, 255));
        g.setColor(dayNight.getColor());
        g.fillPolygon(xPoints1, yPoints1, 4);
        g.fillPolygon(xPoints2, yPoints2, 4);
    }
    
    public DayNight getDayNight(){return dayNight;}
    
    /*
    returns the index of the integer points that make up the map's polygon in terms of which is on the left compared to the front of it being drawn, the middle, the right, and the back(back won't be used often, if at all).
    */
    private int getMapCornerIndexAt(String s)
    {
        if(s.equals("left"))
        {
            if(radSpin > 0 && radSpin <= Math.PI/2.0)
            {
                return 0;
            }else if(radSpin > Math.PI/2.0 && radSpin < Math.PI)
            {
                return 3;
            }else if(radSpin > Math.PI && radSpin < 3.0*Math.PI/2.0)
            {
                return 2;
            }else{
                return 1;
            }
        }else if(s.equals("middle"))
        {
            if(radSpin > 0 && radSpin <= Math.PI/2.0)
            {
                return 1;
            }else if(radSpin > Math.PI/2.0 && radSpin < Math.PI)
            {
                return 0;
            }else if(radSpin > Math.PI && radSpin < 3.0*Math.PI/2.0)
            {
                return 3;
            }else{
                return 2;
            }
        }else if(s.equals("right"))
        {
            if(radSpin > 0 && radSpin <= Math.PI/2.0)
            {
                return 2;
            }else if(radSpin > Math.PI/2.0 && radSpin < Math.PI)
            {
                return 1;
            }else if(radSpin > Math.PI && radSpin < 3.0*Math.PI/2.0)
            {
                return 0;
            }else{
                return 3;
            }
        }else
        {
            if(radSpin > 0 && radSpin <= Math.PI/2.0)
            {
                return 3;
            }else if(radSpin > Math.PI/2.0 && radSpin < Math.PI)
            {
                return 2;
            }else if(radSpin > Math.PI && radSpin < 3.0*Math.PI/2.0)
            {
                return 1;
            }else{
                return 0;
            }
        }
    }
    
    /*
    CLEANUP: Make all debug info and drawing the grid under a boolean "devMode" instead of toggling water and debug mode separately.
    MINOR PROBLEM: gridding the map is weird. But works.
    paints the floor of the map--whether it is a grid for debugging or just the water. toggles with the drawWater boolean. 
    */
    private void drawMapFloor(Graphics g)
    {
        if(!drawWater)
        {
            
            g.setColor(Color.BLACK);
            for(int i =0; i < 4; i++)
            {
                g.drawString("x" + Integer.toString(i) + ": "+ Integer.toString(mapPoints[0][i]),50, 75+(i*25));
                g.drawString("y" + Integer.toString(i) + ": "+ Integer.toString(mapPoints[1][i]), 130, 75+(i*25));
            }
            int iterations = mapWidth/unit;
            int[][] points = mapPoints;

            double dxOne = points[0][3]-points[0][0];
            double dyOne = points[1][3] - points[1][0];
            double dxTwo = mapPoints[0][0]-mapPoints[0][1];
            double dyTwo = mapPoints[1][0]-mapPoints[1][1];
            
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    g.setColor(Color.YELLOW);//y axis
                }else{
                    g.setColor(Color.BLACK);
                }

                g.drawLine((int)(points[0][0] + i*(dxOne/iterations)), (int)(points[1][0]+(i*dyOne/iterations)), (int)(points[0][1]+(i*dxOne/iterations)), (int)(points[1][1]+(i*dyOne/iterations)));
            }
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    g.setColor(Color.BLUE);//x axis
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                    g.fillOval((int)(points[0][2] + i*(dxTwo/iterations))-10, (int)(points[1][2]+(i*dyTwo/iterations))-10, 20,  20);
                }else{
                    g.setColor(Color.BLACK);
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                }
            }
            drawMap(g);
        }else
        {
            g.setColor(new Color(30, 144, 255));
            g.fillPolygon(mapPoints[0], mapPoints[1],4);
        }
    }
    
    /* Poorly named and (I think) only used
    within the context of having to draw
    lines on the map */
    private double getdx(int[][] points){return points[0][3]-points[0][0];}
    private double getdy(int[][] points){return points[1][3] - points[1][0];}
    private double getOtherdy(){return mapPoints[1][0]-mapPoints[1][1];}
    private double getOtherdx(){return mapPoints[0][0]-mapPoints[0][1];}
    
    /*gives the current quadrant that the world's spin is in, and is
    used to calculate things like draw order and apply negative/positive 
    x and y modifications to variables.*/
    public static int spinQuadrant()
    {
        if((int)(radSpin/(Math.PI/2.0)) + 1 <= 4)
        {
            return (int)(radSpin/(Math.PI/2.0)) + 1;
        }
        return 4;
    }
    private void drawRotationLine(Graphics g)//draws the line of rotation
    {
        g.setColor(Color.GREEN);
        g.drawLine((int)worldX,(int)worldY,(int)worldX+(int)(mapRadius*Math.sin(spinCalc)),(int)worldY+(int)(shrink(rotation)*(mapRadius*Math.cos(spinCalc))));
    }
    
    /* returns an array of ints where first index determines the variable 
    ([0][i] = x values, [1][i] = y values) and returns the array to draw 
    the upper portion of the map.*/
    public static int[][] mapTopPoints(double spinIn, int radiusIn)
    {
        int[][] points = new int[2][4];
        for(int variable = 0; variable < 2; variable++)
        {
            for(int i = 0; i < 4; i++)
            {
                switch(variable + 1)
                {
                    case 1:
                        points[variable][i]=(int)((radiusIn * Math.sin(spinIn + (i*(Math.PI/2)))));
                        break;
                    case 2:
                        points[variable][i]=(int)((radiusIn * Math.cos(spinIn + (i*(Math.PI/2)))));
                        break;
                }
            }
        }
        return points;
    }
    
    /*returns a constant that length is modified by for a given point since 
    turning the field to make it flatter causes it to distort -- this is achieved
    by multiplying the value by a floating point number < 1*/
    public static double shrink(double rotationIn)
    {
        return Math.cos(rotationIn);
    }
    
    /*takes the rotation of the field and a height integer as parameters and returns the height 
    of that object as viewed through the screen from the front -- meaning how tall it should appear
    to look as if it were manipulated in three dimensional space.*/
    public static double distortedHeight(double rotationIn, int heightIn)//one of the distortedHeights is redundant...
    {
        return Math.sin(rotationIn)*heightIn;
    }  
    
    /*
    draws transparent gridded lines over the world.
    */
    public void drawTransparentGridLines(Graphics g)
    {
        if(drawWater)
        {
            g.setColor(new Color(0, 51, 204, 50));
            /*for(int i =0; i < 4; i++)
            {
                g.drawString("x" + Integer.toString(i) + ": "+ Integer.toString(mapPoints[0][i]),50, 75+(i*25));
                g.drawString("y" + Integer.toString(i) + ": "+ Integer.toString(mapPoints[1][i]), 130, 75+(i*25));
            }*/
            int iterations = mapWidth/unit;
            int[][] points = mapPoints;

            double dxOne = points[0][3]-points[0][0];
            double dyOne = points[1][3] - points[1][0];
            double dxTwo = mapPoints[0][0]-mapPoints[0][1];
            double dyTwo = mapPoints[1][0]-mapPoints[1][1];
            
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                /*if(i == (mapWidth/unit)/2)
                {
                    g.setColor(Color.YELLOW);//y axis
                }else{
                    g.setColor(Color.BLACK);
                }*/

                g.drawLine((int)(points[0][0] + i*(dxOne/iterations)), (int)(points[1][0]+(i*dyOne/iterations)), (int)(points[0][1]+(i*dxOne/iterations)), (int)(points[1][1]+(i*dyOne/iterations)));
            }
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    //g.setColor(Color.BLUE);//x axis
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                    g.fillOval((int)(points[0][2] + i*(dxTwo/iterations))-10, (int)(points[1][2]+(i*dyTwo/iterations))-10, 20,  20);
                }else{
                    //g.setColor(Color.BLACK);
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                }
            }
        }
    }
    
    /*Updated every time timer fires it. Handles primarily positioning and 
    dimensions of the world, and starts threads when necessary for objects such as tiles.*/
    private void tick()
    {
        
        if(scale < 6.0 && MouseInput.dScale > 0)
        {
            scale += MouseInput.dScale;
        }
        if(scale > .5 && MouseInput.dScale < 0)
        {
            scale += MouseInput.dScale;
        }
        unit = (int)(baseUnit * scale);
        straightUnit = (double)unit/Math.sqrt(2);
        mapWidth = (int)(baseMapWidth * scale);
        mapRadius = (int)(baseMapRadius*scale);
        mapHeight = (int)(baseMapHeight * scale);
        mapThickness = (int)(baseMapThickness * scale);
        requestFocus();
        MouseInput.updatePos();//updates the mouse's position.
        getShrink = shrink(rotation);//static getShrink is used so that other classes can get it easily.
        mapPoints = mapTopPoints(spin, mapRadius);//more efficient to have an instance variable that updates position rather than having to calculated it every time it is called. 
        for(int i = 0; i < 4; i++)//places the map in relation to its position, as the method that gets its array only gives its position compared to nothing else. May be a little slower to do them separately, but shouldn't really matter much.
        {
            mapPoints[0][i] = (int)worldX + mapTopPoints(spin, mapRadius)[0][i];
            mapPoints[1][i] = (int)worldY+(int)(mapTopPoints(spin, mapRadius)[1][i] * shrink(rotation));
        }
        if(!MouseInput.clicked)
        {
            worldX+= Input.givedx;worldY+= Input.givedy;
        }else{
            worldX = (MouseInput.tempWorldX - (MouseInput.dragdx));
            worldY = (MouseInput.tempWorldY - (MouseInput.dragdy));
        }
        spin += Input.dSpin;
        spinCalc += Input.dSpin;//spinCalc can spin on indefinitely. Could add another if/else if clause along with rotation and radspin.
        radSpin += Input.dSpin;
        rotation+=Input.dRotation;
        
        /*resets the spins if they go over or under a full revolution*/
        if(rotation > Math.PI/2.3){
            rotation = Math.PI/2.3;
        }else if(rotation<0.5){
            rotation = .5;
        }if(radSpin > (2*Math.PI)){
            radSpin -= 2*Math.PI;
        }else if(radSpin < 0){
            radSpin += 2*Math.PI;
        }
        if(tempQuadrant != spinQuadrant() || MouseInput.clickJustReleased() || MouseInput.clicked || LevelLoader.sortTiles || Tile.resortTiles)//needs to run last
        {
            td2.getThread().interrupt();
            td2.setThread(new Thread(td2));
            td2.getThread().start();
            tempQuadrant = spinQuadrant();
            LevelLoader.sortTiles = false; 
        }
        
        for(int i = 0; i < TileSorter.tileList.size(); i++)
        {
            try{
                ts.getThread().join();//waits for editing to be finished first. Maybe there is a better way to do this? Seems like it isn't as fast as possible this way. 
            }catch(Exception e){}//do i want to throw anything here?
            
            TileSorter.tileList.get(i).getThread().interrupt();
            Thread t = new Thread(TileSorter.tileList.get(i));
            t.start();
            TileSorter.tileList.get(i).setThread(t);//shouldn't be working with spinTile but does?
        
        }
        for(int i = 0; i < TileSorter.tileList.size(); i++)
        {
            for(int j = 0; j < TileSorter.tileList.get(i).getSceneryList().size(); j++)
            {
                TileSorter.tileList.get(i).getSceneryList().get(j).getThread().interrupt();
                TileSorter.tileList.get(i).getSceneryList().get(j).setThread(new Thread(TileSorter.tileList.get(i).getSceneryList().get(j)));
                TileSorter.tileList.get(i).getSceneryList().get(j).getThread().start();
            }
        }

        player.getThread().interrupt();
        player.setThread(new Thread(player));
        player.getThread().start();
        
        for(int i = 0; i < MergedBlockTiles.blockTiles.size(); i++)
        {
            MergedBlockTiles.blockTiles.get(i).getThread().interrupt();
            MergedBlockTiles.blockTiles.get(i).setThread(new Thread(MergedBlockTiles.blockTiles.get(i)));
            MergedBlockTiles.blockTiles.get(i).getThread().start();
        }
    }
    
    /*
    when game is in menu, this is called to set false so that you can't see buttons you don't want in the menu. Set to true when the game is visible.
    */
    public void setGameVisible(boolean b)
    {
        volumeSlider.setVisible(b);
        turnLeft.setVisible(b);
        turnRight.setVisible(b);
        resetLevel.setVisible(b);
    }
    
    /*
    handles buttons being pressed, timers being fired, etc. 
    */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String command = e.getActionCommand();
        if(command.equals("tick"))
        {
            Thread t = new Thread(this);
            t.start();
            tick();
        }else if(command.equals("frame"))
        {
            repaint();
        }else if(command.equals("second"))
        {
            
                fps = frameCount;
                frameCount = 0;
                timeCount = 0;
            
        }else if(command.equals("randomShapes"))
        {
            baseMapWidth = 525;
            baseMapHeight = 525;
            baseUnit = 37;
            baseStraightUnit = (double)baseUnit/Math.sqrt(2);
            baseMapRadius = baseMapWidth/2;
            squareWidth = (double)mapWidth/Math.sqrt(2);
            squareRadius = (double)baseMapRadius/Math.sqrt(2);
            
            unit = (int)(baseUnit * scale);
            straightUnit = (double)unit/Math.sqrt(2);
            mapWidth = (int)(baseMapWidth * scale);
            mapRadius = (int)(baseMapRadius*scale);
            mapHeight = (int)(baseMapHeight * scale);
            mapThickness = (int)(baseMapThickness * scale);
        }else if(command.equals("turnLeft"))
        {
            spin += Math.PI/2.0;
            radSpin += Math.PI/2.0;
            spinCalc += Math.PI/2.0;
        }else if(command.equals("turnRight"))
        {
            spin -= Math.PI/2.0;
            radSpin -= Math.PI/2.0;
            spinCalc -= Math.PI/2.0;
        }else if(command.equals("resetLevel"))
        {
            LevelLoader ll = new LevelLoader(player, this);
            ll.spawnLevel(UI.level);
            MouseInput.scrollType = "Zoom";
        }
    }
    
    /*
    controls only the volume slider, as of now.
    */
    @Override
    public void stateChanged(ChangeEvent e) 
    {
        JSlider numIn = (JSlider)e.getSource();
        
        a.setVolume((float)numIn.getValue());
    }

    @Override
    public void run() 
    {
        renderTextures();
    }
}
