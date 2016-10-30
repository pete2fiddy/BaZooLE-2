package shift;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WorldPanel extends JPanel implements ActionListener, ChangeListener
{
    public static double minScale = 0.5, maxScale = 6.0;
    private boolean drawWater = true;
    private int tempQuadrant, frameCount;
    public static int baseUnit = 75, baseMapWidth = 1050, baseMapHeight = 1050, baseMapRadius = baseMapWidth/2, baseMapThickness = 10;
    public static double baseStraightUnit = (double)baseUnit/Math.sqrt(2);
    public static double fps;
    public static double scale;
    public static int unit = 75, mapRadius = baseMapRadius, mapThickness = baseMapThickness;
    public static double straightUnit = (double)unit/Math.sqrt(2);
    public static final int fpsCap = 160;
    public static int screenWidth = Frame.screenWidth, screenHeight = Frame.screenHeight;
    public static int mapWidth = 1050, mapHeight = 1050;
    public static int widthHalf = screenWidth/2, heightHalf = screenHeight/2, worldTilesWidth = mapWidth/unit, worldTilesHeight = mapHeight/unit, mapRadiusUnits = mapRadius/unit;//RADIUS GOES FROM CORNER TO CORNER
    public static int[][] mapPoints;
    public static double squareWidth = (double)mapWidth/Math.sqrt(2), squareRadius = (double)baseMapRadius/Math.sqrt(2);//straightUnit is the width and height of a single unit... Accidentally coded so that a unit was measured from corner to corner at 45 degrees
    public static double worldX, worldY;
    public static double rotation, rotationFraction, tempRotation, spin, spinCalc, radSpin;
    public static double getShrink;
    private UI ui;
    private static double backgroundColorRotation = 0;
    private JButton turnLeft, turnRight, resetLevel;
    Audio a = new Audio();
    public static Color backgroundColor = new Color(0, 65 + (int)(Math.abs(100*Math.sin(backgroundColorRotation))), 198);
    private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
    public static DayNight dayNight = new DayNight();
    public static Color baseWaterColor = new Color(30, 144, 255);
    public static Color waterColor = baseWaterColor;
    private Timer secondTimer;
    private TileDrawer2 td2;
    Input input = new Input();
    MouseInput mouseInput = new MouseInput(this);
    Player player = new Player(30, 30, 5);
    public static final int maxFPS = 60;
    private Timer frameTimer=new Timer((int)(1000.0/(double)maxFPS), this);
    public static Area clipArea;
    public static Area belowMapArea;
    private JFrame frame;
    private Rectangle frameBounds;
    
    /*Initialization
    ----------------------------------------------------------------------------
    */
    public WorldPanel(JFrame frameIn)
    {
        frame = frameIn;
        
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
        td2 = new TileDrawer2(this);
        tick();
        setFocusable(true);
        ColorPalette.updateShadedGrassColor();
    }
    
    /*
    initializes variables
    */
    private void initVariables()
    {
        worldX = screenWidth/2; worldY=3*screenHeight/5; rotation = Math.toRadians(75); spin = 0; spinCalc = spin+Math.PI + (Math.PI/4); radSpin = spinCalc - (Math.PI/2); tempQuadrant = getSpinQuadrant();
        mapPoints = mapTopPoints(spin, mapRadius);
        frameCount = 0;
        scale = 2.0;
        ui = new UI(this);
        if(dayNight.getSeason().equals("winter"))
        {
            ColorPalette.grassColor = ColorPalette.defaultSnowColor;
        }else{
            ColorPalette.grassColor = ColorPalette.defaultGrassColor;
        }
        
        secondTimer = new Timer(1000, this);
        secondTimer.setActionCommand("second");
        secondTimer.setRepeats(true);
        secondTimer.start();
        frameTimer.setRepeats(true);
        frameTimer.setActionCommand("frame");
        frameTimer.start();
        
        Toolbox.setWorldPanel(this);
        Toolbox.setToolboxPlayer(player);
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
    ----------------------------------------------------------------------------
    
    Painting
    */
    
    private void addSpin()
    {
        spin += TileDrawer2.changeInSpin;
        spinCalc += TileDrawer2.changeInSpin;
        radSpin += TileDrawer2.changeInSpin;
        TileDrawer2.changeInSpin = 0;
    }
    
    /*
    the MAIN paint method for the project. Everything painted from here, or from instances called from here.
    */
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g); 
        requestFocus();
        addSpin();
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(Toolbox.worldStroke);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        //g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        setBelowMapArea();//set here rather than during the game update timer because there won't be a lag behind the bottom map clipping and map movement if it's set first here.
        
        g.setClip(0,0, screenWidth, screenHeight);
        setBackground(dayNight.getColor());//sets the color of the background based on the trig values of backgroundColorRotaion
        frameCount++;//adds one to the number of frames so that the FPS counter knows how many frames have passed since the last interval
        
        dayNight.draw(g);
        td2.draw(g);
        drawFPS(g, g2);
        player.drawPlayersChain(g);//draws the player's chain on top of everything else being drawn so it can always be easily seen
        player.drawTransparentPlayer(g);//draws a transparent player superimposed over where the player is being drawn so that it can be see-through if covered by something.
        ui.draw(g);//draws UI elements like level, etc.*/
        drawDebugInfo(g);
        drawRotationLine(g);
        System.out.println("dRotation " + Input.dRotation);
        tick();
    }
    
    /*
    Draws the Frames Per Second of the game onto the screen.
    */
    private void drawFPS(Graphics g, Graphics2D g2)
    {
        g.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Futura", Font.PLAIN, 16));
        g.drawString("FPS: " + Integer.toString((int)fps), 30, 100);
        g.drawString("Scale: " + Double.toString(scale), 110, 775);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    
    /*
    Draws the map (either wire mesh or water). Not used in normal gameplay, but when drawWater is false, will draw for sake of debugging
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
    private void drawDebugInfo(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Futura", Font.PLAIN, 12));
        for(int i = 0; i<4; i++)
        {
            g.drawString(i+"",mapPoints[0][i], mapPoints[1][i]);
        }
        g.drawString("Quadrant: " + Integer.toString(getSpinQuadrant()),125, 50);
        g.drawString("FPS: " + Integer.toString((int)fps), 50, 50);
        g.drawString("Spin: "+ Double.toString(radSpin), 100, 800);
        g.drawString("Scale: " + Double.toString(scale), 100, 775);
    }
    
    /*
    CLEANUP: Make all debug info and drawing the grid under a boolean "devMode" instead of toggling water and debug mode separately.
    MINOR PROBLEM: gridding the map is weird. But works.
    paints the floor of the map--whether it is a grid for debugging or just the water. toggles with the drawWater boolean. 
    */
    public void drawMapFloor(Graphics g)
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
    
    private void drawRotationLine(Graphics g)//draws a line to show map rotation and orientation
    {
        g.setColor(Color.GREEN);
        g.drawLine((int)worldX,(int)worldY,(int)worldX+(int)(mapRadius*Math.sin(spinCalc)),(int)worldY+(int)(shrink(rotation)*(mapRadius*Math.cos(spinCalc))));
    }
    
    /*
    draws transparent gridded lines over the world.
    */
    public void drawTransparentGridLines(Graphics g)
    {
        if(drawWater)
        {
            g.setColor(new Color(0, 51, 204, 50));//consider replacing with color interpolation instead of using alpha.
            int iterations = mapWidth/unit;
            int[][] points = mapPoints;

            double dxOne = points[0][3]-points[0][0];
            double dyOne = points[1][3] - points[1][0];
            double dxTwo = mapPoints[0][0]-mapPoints[0][1];
            double dyTwo = mapPoints[1][0]-mapPoints[1][1];
            
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                g.drawLine((int)(points[0][0] + i*(dxOne/iterations)), (int)(points[1][0]+(i*dyOne/iterations)), (int)(points[0][1]+(i*dxOne/iterations)), (int)(points[1][1]+(i*dyOne/iterations)));
            }
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                    g.fillOval((int)(points[0][2] + i*(dxTwo/iterations))-10, (int)(points[1][2]+(i*dyTwo/iterations))-10, 20,  20);
                }else{
                    g.drawLine((int)(points[0][1] + i*(dxTwo/iterations)), (int)(points[1][1]+(i*dyTwo/iterations)),(int)(points[0][2] + i*(dxTwo/iterations)), (int)(points[1][2]+(i*dyTwo/iterations)));
                }
            }
        }
    }
    
    /*
    ----------------------------------------------------------------------------
    Setters:
    */
    
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
    
    private void setBelowMapArea()
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
        
        belowMapArea = new Area(new Polygon(xPoints1, yPoints1, 4));
        belowMapArea.add(new Area(new Polygon(xPoints2, yPoints2, 4)));
        
        //g.setColor(new Color(0, 65 + (int)(Math.abs(100*Math.sin(backgroundColorRotation))), 198));//g.setColor(new Color(30, 144, 255));
        //g.setColor(dayNight.getColor());
        //g.fillPolygon(xPoints1, yPoints1, 4);
        //g.fillPolygon(xPoints2, yPoints2, 4);
    }
    
    /*
    ----------------------------------------------------------------------------
    Getters:
    */
    public DayNight getDayNight(){return dayNight;}
    public Player getPlayer(){return player;}
    
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
    
    /*gives the current quadrant that the world's spin is in, and is
    used to calculate things like draw order and apply negative/positive 
    x and y modifications to variables.*/
    public static int getSpinQuadrant()
    {
        if((int)(radSpin/(Math.PI/2.0)) + 1 <= 4)
        {
            return (int)(radSpin/(Math.PI/2.0)) + 1;
        }
        return 4;
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
    ----------------------------------------------------------------------------
    Loopers/refreshers (all are looped and update values):
    */
    /*Updated every time timer fires it. Handles primarily positioning and 
    dimensions of the world, and starts threads when necessary for objects such as tiles.*/
    public void tick()
    {
        frameBounds = frame.getBounds();
        if(screenWidth != (int)frameBounds.getWidth() || screenHeight != (int)frameBounds.getHeight())
        {
            screenWidth = (int)frameBounds.getWidth();
            screenHeight = (int)frameBounds.getHeight();
        }
        clipArea = new Area(frameBounds);
        
        if(scale < maxScale && MouseInput.dScale > 0)
        {
            scale += MouseInput.dScale;
            Tile.setGrassSkip();//determines how many grass needs to be drawn based on the scale
        }
        if(scale > minScale && MouseInput.dScale < 0)
        {
            scale += MouseInput.dScale;
            Tile.setGrassSkip();//determines how many grass needs to be drawn based on the scale
        }
        unit = (int)(baseUnit * scale);
        straightUnit = (double)unit/Math.sqrt(2);
        mapWidth = (int)(baseMapWidth * scale);
        mapRadius = (int)(baseMapRadius*scale);
        mapHeight = (int)(baseMapHeight * scale);
        mapThickness = (int)(baseMapThickness * scale);
        
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
        //spin += Input.dSpin;
        //spinCalc += Input.dSpin;//spinCalc can spin on indefinitely. Could add another if/else if clause along with rotation and radspin.
        //radSpin += Input.dSpin;
        rotation+=Input.dRotation;
        if(rotation > 1.3659){
            rotation = 1.3659;
        }else if(rotation<0.5){
            rotation = .5;
        }
        /*resets the spins if they go over or under a full revolution*/
        if(radSpin > (2*Math.PI)){
            radSpin -= 2*Math.PI;
        }else if(radSpin < 0){
            radSpin += 2*Math.PI;
        }
        
        if(tempQuadrant != getSpinQuadrant())
        {
            for(int i = 0; i < TileDrawer2.tileList.size(); i++)
            {
                TileDrawer2.tileList.get(i).sortAllScenery();
            }
            td2.getThread().interrupt();
            td2.setThread(new Thread(td2));
            td2.getThread().start();
            
            tempQuadrant = getSpinQuadrant();
            LevelLoader.sortTiles = false; 
        }else if(MouseInput.clickJustReleased() || MouseInput.clicked || LevelLoader.sortTiles || Tile.resortTiles)//needs to run last
        {
            td2.getThread().interrupt();
            td2.setThread(new Thread(td2));
            td2.getThread().start();
            
            tempQuadrant = getSpinQuadrant();
            LevelLoader.sortTiles = false; 
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
    handles buttons being pressed, timers being fired, etc. 
    */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String command = e.getActionCommand();
        if(command.equals("frame"))
        {
            repaint();
        }else if(command.equals("second"))
        {
            
                fps = frameCount;
                frameCount = 0;
            
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

    
    /*DELETABLE:*/
    
    
    
    /*@Override
    public void run() 
    {
        renderTextures();
    }*/
    
    
    /*
    Stretches and distorts the world textures so that they properly fit the rotation, scaling, y distortion of the world. Leaves aren't textured, so it is doing extra work by texturing them. Kept it for sake of wanting to texture leaves at some point
    */
    /*private void renderTextures()
    {
        if(dayNight.getSeason().equals("summer"))
        {
            try {
                //grassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
                //grassTexture = new TexturePaint(grassImage, new Rectangle(0, 0, 256, 256));
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
            //grassTexture = new TexturePaint(grassImage, new Rectangle((int)worldX, (int)worldY, (int)(scale*128), (int)(scale*128*getShrink)));
            //leavesTexture = new TexturePaint(leavesImage, new Rectangle((int)worldX, (int)worldY, (int)(0.5*scale*leavesImage.getWidth()), (int)(0.5*scale*distortedHeight(rotation, leavesImage.getHeight()))));
        }catch(Exception e)
        {
            System.err.println(e);
        }
    }*/
    /*private double getdx(int[][] points){return points[0][3]-points[0][0];}
    private double getdy(int[][] points){return points[1][3] - points[1][0];}
    private double getOtherdy(){return mapPoints[1][0]-mapPoints[1][1];}
    private double getOtherdx(){return mapPoints[0][0]-mapPoints[0][1];}*/
    /*
    AREA FOR IMPROVEMENT.
    Fills the area beneath the map with the background color on top of any shadows or other protrusions from tiles, shapes, shadows, reflections, etc. that poke outside of the world. Ideally these would simply not draw beneath the bounds of the map.
    */
    /*private void fillBelowMap(Graphics g)
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
    }*/
}
