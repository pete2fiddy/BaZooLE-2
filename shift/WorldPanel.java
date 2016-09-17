package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class WorldPanel extends JPanel implements ActionListener, Runnable, ChangeListener
{
    private Timer tickTimer;
    private boolean drawWater = true;
    private int tempQuadrant, frameCount;
    private long startTime;
    public static double getFPS;
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
    private double colorRotation = 0;
    public static BufferedImage grassImage, leavesImage;
    public static TexturePaint grassTexture, leavesTexture;;
    private Object loopNotify = new Object();
    private Thread thread;
    private JButton turnLeft, turnRight, resetLevel;
    RectPrism prism;
    Audio a = new Audio();
    private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
    
    private JButton randomShapes;
    
    TileSorter ts;
    TileDrawer td;
    private TileDrawer2 td2 = new TileDrawer2();
    Input input = new Input();
    MouseInput mouseInput = new MouseInput(this);
    Player player = new Player(0, 0, 5);
    private LevelLoader levelLoader = new LevelLoader(player, this);
    private WaterRipple[] waterRipples = new WaterRipple[8];
    private Toolbox toolbox = new Toolbox(this, player);
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
        
        try{
            grassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
    
            grassTexture = new TexturePaint(grassImage, new Rectangle(0, 0, 256, 256));
            
            leavesImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Leaves3.png"));
            
            leavesTexture = new TexturePaint(leavesImage, new Rectangle((int)worldX, (int)worldY, leavesImage.getWidth(), leavesImage.getHeight()));
        }catch(Exception e)
        {
            System.out.println(e);
        }
        
        prism = new RectPrism(0, 0, 50, 1.0, 2.0, 100);
        tick();
        thread = new Thread(this);
        
        thread.start();
        tickTimer = new Timer(5, this);
        tickTimer.setActionCommand("tick");
        tickTimer.setRepeats(true);
        tickTimer.start();
    }
    
    private void initVariables()
    {
        worldX = screenWidth/2; worldY=3*screenHeight/5; rotation = Math.toRadians(75); spin = 0; spinCalc = spin+Math.PI + (Math.PI/4); radSpin = spinCalc - (Math.PI/2); tempQuadrant = spinQuadrant();
        mapPoints = mapTopPoints(spin, mapRadius);
        frameCount = 0;
        scale = 2.0;
        ui = new UI(this);
        //fillWaterRipples();
        
    }
    private void initButtons()
    {
        volumeSlider.addChangeListener(this);
        volumeSlider.setBounds(0, 0, 100, 50);
        volumeSlider.setVisible(false);
        add(volumeSlider);
        //setLayout(new FlowLayout());
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
        //add(randomShapes);
    }
    
   
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
    Point2D p2din, p2dout;

    p2din = new Point2D.Double(0.0, 0.0);
    p2dout = at.transform(p2din, null);
    double ytrans = p2dout.getY();

    p2din = new Point2D.Double(0, bi.getHeight());
    p2dout = at.transform(p2din, null);
    double xtrans = p2dout.getX();

    AffineTransform tat = new AffineTransform();
    tat.translate(-xtrans, -ytrans);
    return tat;
  }
    
    public void addTile(Tile t){ts.addTile(t);}//necessary?
    
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        frameCount++;//band-aid way to make the FPS count not change too quickly to read -- only changes the FPS once frameCount reaches a certain number and is then reset. Could be fixed.
        startTime = System.nanoTime();
        
        //thread.interrupt();
        //thread = new Thread(this);
        //thread.start();
        //tick();
        
        
        colorRotation += Math.PI/5000.0;
        setBackground(new Color(0, 65 + (int)(Math.abs(100*Math.sin(colorRotation))), 198));
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(Toolbox.worldStroke);
        
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        
        //drawMap(g);
        //ArrayList<Tile> tempTile = ts.holdList;//create a temp list of held tiles to be drawn. Maybe is clunky for no reason, but was originally made to skirt the list being modified while it was drawn.
        
        stripeMap(g, spin); 
        drawWater(g);
        
        td2.draw(g);
        
        fillBelowMap(g);
        if((double)(1/((System.nanoTime()-startTime)/1000000000.0)) > fpsCap)//limit FPS. sometimes gets a negative timeout thrown. FIX
        {
            try {
                //System.out.println("HI");
                Thread.sleep((long)((1000.0/(double)fpsCap) - ((System.nanoTime()-startTime)/1000000)));
                
            } catch (Exception ex) {
                Logger.getLogger(WorldPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //player.draw(g);
        player.drawPlayersChain(g);
        player.drawTransparentPlayer(g);
        ui.draw(g);
        renderTextures();
        
        
        getFPS = (double)(1.0/((System.nanoTime() - startTime)/1000000000.0));
       
        //drawDebugInfo(g);//needs to go after sleeping since FPS is calculated here and it needs to happen last.
        if(frameCount > 20)
        {
            fps = (double)(1/((System.nanoTime()-startTime)/1000000000.0));
            frameCount = 0;
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Futura", Font.PLAIN, 16));
        g.drawString("FPS: " + Integer.toString((int)fps), 30, 100);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        repaint();
    }
    
    private void renderTextures()
    {
        try{
            
            double rotationRequired = radSpin;
            double locationX = grassImage.getWidth()/2;
            double locationY = grassImage.getHeight()/2;
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

            grassTexture = new TexturePaint(grassImage, new Rectangle((int)worldX, (int)worldY, (int)(scale*128), (int)(scale*128*getShrink)));
            
            leavesTexture = new TexturePaint(leavesImage, new Rectangle((int)worldX, (int)worldY, (int)(0.5*scale*leavesImage.getWidth()), (int)(0.5*scale*distortedHeight(rotation, leavesImage.getHeight()))));
            
            
            // Drawing the rotated image at the required drawing locations
            //g.drawImage(newGrass, drawLocationX, drawLocationY, null);
            
            
            
            
            
            
            
            /*
            //TexturePaint tempTexture = new TexturePaint(grassImage, new Rectangle((int)worldX, (int)worldY, (int)(straightUnit), (int)(straightUnit*getShrink)));
            TexturePaint tempTexture = new TexturePaint(grassImage, new Rectangle((int)worldX, (int)worldY, (int)(straightUnit), (int)(straightUnit)));
            BufferedImage tempImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);



            Graphics2D tg2 = tempImage.createGraphics();
            tg2.setPaint(tempTexture);
            tg2.fillRect(144,144,512,512);



            double rotationRequired = -(spin + (Math.PI/4.0));
            double locationX = 400;
            double locationY = 400;
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

            
            
            tempImage = op.filter(tempImage, null);
            Graphics2D tg3 = tempImage.createGraphics();
            tg3.drawRect(0, 0, tempImage.getWidth() - 1, tempImage.getHeight() - 1);
            g.drawImage(tempImage.getScaledInstance(tempImage.getWidth(), (int)(tempImage.getHeight()*getShrink), Image.SCALE_AREA_AVERAGING), (int)worldX -tempImage.getWidth()/2, (int)(worldY - ((tempImage.getHeight()/2)*getShrink)), null);
            g.fillOval((int)(locationX + worldX -tempImage.getWidth()/2), (int)(locationY + worldY - ((tempImage.getHeight()/2)*getShrink)), 10, 10);
            //g.drawImage(tempImage.getScaledInstance(725, (int)(725*getShrink), Image.SCALE_AREA_AVERAGING), (int)(worldX - (362)), (int)(worldY - (getShrink*(362))), null);

            //grassTexture = new TexturePaint(tempImage, new Rectangle(0,0, screenWidth, (int)(screenWidth*getShrink)));



            //grassTexture = new TexturePaint(op.filter(tempImage, null), new Rectangle((int)worldX, (int)worldY, (int)screenWidth, (int)(screenHeight)));

            //grassTexture = new TexturePaint(op.filter(grassImage,null), new Rectangle((int)(worldX-((unit - straightUnit)*Math.cos(radSpin%(Math.PI/2.0)))), (int)(worldY+((unit - straightUnit)*Math.sin(radSpin%(Math.PI/2.0)))), (int)((Math.cos(radSpin%(Math.PI/2.0))*straightUnit) + (Math.sin(radSpin%(Math.PI/2.0))*straightUnit)), (int)(((Math.cos(radSpin%(Math.PI/2.0))*straightUnit) + (Math.sin(radSpin%(Math.PI/2.0))*straightUnit)) * getShrink)));
            //grassTexture = new TexturePaint(op.filter(grassImage,null), new Rectangle((int)(worldX-((unit - straightUnit)*Math.cos(radSpin%(Math.PI/2.0)))), (int)(worldY+((unit - straightUnit)*Math.sin(radSpin%(Math.PI/2.0)))), (int)straightUnit, (int)(straightUnit*getShrink)));
            //g.drawString("textureWidth: " + Integer.toString((int)((Math.cos(radSpin%(Math.PI/2.0))*straightUnit) + (Math.sin(radSpin%(Math.PI/2.0))*straightUnit))), 200, 200);
            */
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }
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
        //drawRotationLine(g);
        
        
    }
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
            fps = (double)(1/((System.nanoTime()-startTime)/1000000000.0));
            frameCount = 0;
        }
        g.drawString("FPS: " + Integer.toString((int)fps), 50, 50);
        //g.drawString("X: " + Double.toString(getMouseUnitPosDouble()[0]), MouseInput.x, MouseInput.y - 15);
        //g.drawString("Y: " + Double.toString(getMouseUnitPosDouble()[1]), MouseInput.x, MouseInput.y);
        g.drawString("Spin: "+Double.toString(radSpin), 100, 800);
        g.drawString("Scale: " + Double.toString(scale), 100, 775);
    }
    private void drawWater(Graphics g)
    {
        if(drawWater)
        {
            //Graphics2D g2 = (Graphics2D)g;
            g.setColor(new Color(30, 144, 255));
            //g2.setPaint(grassTexture);
            g.fillPolygon(mapPoints[0], mapPoints[1],4);
        }
    }
    
    private void fillBelowMap(Graphics g)
    {
        int tempLowerPoints[][] = new int[2][4];
        for(int i = 0; i < 4; i++)
        {
            tempLowerPoints[0][i] = mapPoints[0][i];
            tempLowerPoints[1][i] = mapPoints[1][i];//(int)(mapPoints[1][i]+distortedHeight(rotation, mapThickness));
        }
        int[] xPoints1 = {tempLowerPoints[0][getMapLeftCornerIndex()], tempLowerPoints[0][getMapMiddleCornerIndex()], tempLowerPoints[0][getMapMiddleCornerIndex()],tempLowerPoints[0][getMapLeftCornerIndex()]};
        int[] yPoints1 = {tempLowerPoints[1][getMapLeftCornerIndex()], tempLowerPoints[1][getMapMiddleCornerIndex()], screenHeight, screenHeight};
        
        int[] xPoints2 = {tempLowerPoints[0][getMapMiddleCornerIndex()], tempLowerPoints[0][getMapRightCornerIndex()], tempLowerPoints[0][getMapRightCornerIndex()], tempLowerPoints[0][getMapMiddleCornerIndex()]};
        int[] yPoints2 = {tempLowerPoints[1][getMapMiddleCornerIndex()], tempLowerPoints[1][getMapRightCornerIndex()], screenHeight, screenHeight};
        
        g.setColor(new Color(0, 65 + (int)(Math.abs(100*Math.sin(colorRotation))), 198));//g.setColor(new Color(30, 144, 255));
        
        g.fillPolygon(xPoints1, yPoints1, 4);
        g.fillPolygon(xPoints2, yPoints2, 4);
    }
    
    private int getMapLeftCornerIndex()
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
    }
    
    private int getMapMiddleCornerIndex()
    {
        if(getMapLeftCornerIndex() != 3)
        {
            return getMapLeftCornerIndex() + 1;
        }else{
            return 0;
        }
    }
    
    private int getMapRightCornerIndex()
    {
        if(getMapMiddleCornerIndex() != 3)
        {
            return getMapMiddleCornerIndex() + 1;
        }else{
            return 0;
        }
    }
    
    private void stripeMap(Graphics g, double spinIn)//this is incredibly clunky with methods that don't always do what they should
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

            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    g.setColor(Color.YELLOW);//y axis
                }else{
                    g.setColor(Color.BLACK);
                }

                g.drawLine((int)(points[0][0] + i*(getdx(points)/iterations)), (int)(points[1][0]+(i*getdy(points)/iterations)), (int)(points[0][1]+(i*getdx(points)/iterations)), (int)(points[1][1]+(i*getdy(points)/iterations)));
            }
            for(int i = 0; i < (mapWidth/unit); i++)
            {
                if(i == (mapWidth/unit)/2)
                {
                    g.setColor(Color.BLUE);//x axis
                    g.drawLine((int)(points[0][1] + i*(getOtherdx()/iterations)), (int)(points[1][1]+(i*getOtherdy()/iterations)),(int)(points[0][2] + i*(getOtherdx()/iterations)), (int)(points[1][2]+(i*getOtherdy()/iterations)));
                    g.fillOval((int)(points[0][2] + i*(getOtherdx()/iterations))-10, (int)(points[1][2]+(i*getOtherdy()/iterations))-10, 20,  20);
                }else{
                    g.setColor(Color.BLACK);
                    g.drawLine((int)(points[0][1] + i*(getOtherdx()/iterations)), (int)(points[1][1]+(i*getOtherdy()/iterations)),(int)(points[0][2] + i*(getOtherdx()/iterations)), (int)(points[1][2]+(i*getOtherdy()/iterations)));
                }
            }
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
    
    /*Updated every time paintComponent() is called. Handles primarily positioning and 
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
        //scale = 10.0;
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
            //Thread t = new Thread(ts);
            //ts.setThread(t);
            //t.start();
            td2.getThread().interrupt();
            td2.setThread(new Thread(td2));
            td2.getThread().start();
            //td2.run();
            
            tempQuadrant = spinQuadrant();
            
            //ts.getThread().interrupt();
            //ts.setThread(new Thread(ts));
            //ts.getThread().start();
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
        //if(Input.givedx != 0 || Input.givedy != 0 || Input.dSpin != 0 || Input.dRotation != 0)//used to fire threads that only need to update when the map moves.
        //{
            for(int i = 0; i < TileSorter.tileList.size(); i++)
            {
                for(int j = 0; j < TileSorter.tileList.get(i).getSceneryList().size(); j++)
                {
                    TileSorter.tileList.get(i).getSceneryList().get(j).getThread().interrupt();
                    TileSorter.tileList.get(i).getSceneryList().get(j).setThread(new Thread(TileSorter.tileList.get(i).getSceneryList().get(j)));
                    TileSorter.tileList.get(i).getSceneryList().get(j).getThread().start();
                }
            }
        //}
        player.getThread().interrupt();
        player.setThread(new Thread(player));
        player.getThread().start();
        
        /*synchronized(loopNotify)
        {
            loopNotify.notify();
        }*/
        //System.out.println(TileDrawer2.tileList.size());
    }
    
    public void setGameVisible(boolean b)
    {
        volumeSlider.setVisible(b);
        turnLeft.setVisible(b);
        turnRight.setVisible(b);
        resetLevel.setVisible(b);
    }
    
    
    /*What happens when the random shapes button is clicked.*/
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String command = e.getActionCommand();
        if(command.equals("tick"))
        {
            tick();
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
            //td.populateBoardFixedProportions(2,2,10);
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

    public static int getSortSlope()
    {
        int quad = spinQuadrant();
        if(quad == 1 || quad == 3)
        {
            return -1;
        }else{
            return 1;
        }
    }
    
    public static int getBottomCornerConstant()
    {
        int quad = spinQuadrant();
        if(quad == 1 || quad == 4)
        {
            return -worldTilesHeight/2;
        }else{
            return worldTilesHeight/2;
        }
    }
    
    @Override
    public void run() 
    {
        
        //tick();
            
        
    }
    
    

    @Override
    public void stateChanged(ChangeEvent e) 
    {
        JSlider numIn = (JSlider)e.getSource();
        
        a.setVolume((float)numIn.getValue());
    }

    
}
