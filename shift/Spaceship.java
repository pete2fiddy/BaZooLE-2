/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author phusisian
 */
public class Spaceship extends Scenery implements Runnable//image scaling still hits FPS pretty hard. In the future make the rocketship a polygon instead. 
{
    private Thread thread;
    private double y;
    private SolidShape shipBody;
    private double fireAnimationCount = 0;
    private BufferedImage[] flameArray;
    private boolean takeoff = false;
    private BufferedImage shipImage;
    private Image scaledShip;
    private SolidShape[] shipShapes;
    private LayeredSolidShape finShape;
    public Spaceship(Tile tileIn, double offsetXIn, double offsetYIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        try{
        shipImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/RocketShip3.png"));
//ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/RocketShip3.png"));
        /*BufferedImage[] tempArray = {ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire1.png")), 
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire2.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire3.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire4.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire5.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire6.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire7.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire8.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire9.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire10.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire11.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire12.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire13.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire14.png")),
            ImageIO.read(new File("/Users/phusisian/Dropbox/Shift/Images/Fire15.png")),
            };*/
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
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        scaledShip = shipImage.getScaledInstance((int)(50 * WorldPanel.scale), (int)(WorldPanel.scale*distortedHeight((int)(50 * 3.60))), Image.SCALE_FAST);
        thread = new Thread(this);
        initShapes();
        //initShapes();
        thread.start();
    }

    public void initShapes()
    {
        shipShapes = new SolidShape[5];
        FlatShape[] shapes = new FlatShape[6];
        shapes[0] = new FlatShape(getCoordX(), getCoordY(), (int)(WorldPanel.baseStraightUnit*0.2) + getBoundTile().getHeight(), 0.3, 0.3, 4);//has height of 0.05
        shapes[1] = new FlatShape(getCoordX(), getCoordY(), shapes[0].getZPos() + (int)(WorldPanel.baseStraightUnit * 0.05), 0.2, 4);//has height of 0.46
        shapes[2] = new FlatShape(getCoordX(), getCoordY(), shapes[1].getZPos() + (int)(WorldPanel.baseStraightUnit*0.46), 0.25, 4);//has height of 0.6
        shapes[3] = new FlatShape(getCoordX(), getCoordY(), shapes[2].getZPos() + (int)(WorldPanel.baseStraightUnit*0.6), 0.2625, 4);//has height of 0.68
        shapes[4] = new FlatShape(getCoordX(), getCoordY(), shapes[3].getZPos() + (int)(WorldPanel.baseStraightUnit*0.68), 0.1875, 4);//has height of 0.95
        shapes[5] = new FlatShape(getCoordX(), getCoordY(), shapes[4].getZPos() + (int)(WorldPanel.baseStraightUnit*0.95), 0, 4);
        
        
        
        shipShapes[0]=new LayeredSolidShape(getCoordX(), getCoordY(), getBoundTile().getHeight() + (int)(WorldPanel.baseStraightUnit * 0.2), .2, .2, (int)(WorldPanel.baseStraightUnit * 2.7), 4, shapes);//spaceship body isn't 0.2x0.2, using that to hack in so that bounding box is smaller so it sorts in correct order
        shipBody = shipShapes[0];
        //shipShapes[0] = new RectPrism(getCoordX(), getCoordY(), (int)(WorldPanel.straightUnit*0.2), 0.3, 0.3, (int)(WorldPanel.straightUnit * 0.05));
        //shipShapes[1] = new TruncatedPyramid(getCoordX(), getCoordY(), shipShapes[0].getZPos() + shipShapes[0].getHeight(), 0.2, (int)(WorldPanel.straightUnit * 0.46), 4, 5.0/4.0);
        //shipShapes[2] = new TruncatedPyramid(getCoordX(), getCoordY(), shipShapes[1].getZPos() + shipShapes[1].getHeight(), 0.25, (int)(WorldPanel.straightUnit * 0.6), 4, 5.25/5.0);
        //shipShapes[3] = new TruncatedPyramid(getCoordX(), getCoordY(), shipShapes[2].getZPos() + shipShapes[2].getHeight(), 0.2625, (int)(WorldPanel.straightUnit * 0.68), 4, 3.75/5.25);
        //shipShapes[4] = new Pyramid(getCoordX(), getCoordY(), shipShapes[3].getZPos() + shipShapes[3].getHeight(), 0.1875, (int)(WorldPanel.straightUnit * 0.95), 4);
        //finShape = new LayeredSolidShape(1, 1, 0, 5, 5, 50, 4);
        FlatShape[] finShapes = new FlatShape[4];
        finShapes[0] = new FlatShape(getCoordX() - 0.21, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.45) + getBoundTile().getHeight(), 0, 0.1, 4);
        finShapes[1] = new FlatShape(getCoordX() - 0.275, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.3) + getBoundTile().getHeight(), 0.16, 0.1, 4);
        finShapes[2] = new FlatShape(getCoordX() - 0.34, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.14) + getBoundTile().getHeight(), 0.08, 0.1, 4);
        finShapes[3] = new FlatShape(getCoordX() - 0.35, getCoordY(), getBoundTile().getHeight(), 0, 0.1, 4);
        shipShapes[1] = new LayeredSolidShape(getCoordX() - 0.275, getCoordY(), + getBoundTile().getHeight(), 0.2, 0.1, (int)(0.45*WorldPanel.baseStraightUnit), 4, finShapes);
        
        FlatShape[] finShapes2 = new FlatShape[4];
        finShapes2[0] = new FlatShape(getCoordX() + 0.21, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.45) + getBoundTile().getHeight(), 0, 0.1, 4);
        finShapes2[1] = new FlatShape(getCoordX() + 0.275, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.3) + getBoundTile().getHeight(), 0.16, 0.1, 4);
        finShapes2[2] = new FlatShape(getCoordX() + 0.34, getCoordY(), (int)(WorldPanel.baseStraightUnit*0.14) + getBoundTile().getHeight(), 0.08, 0.1, 4);
        finShapes2[3] = new FlatShape(getCoordX() + 0.35, getCoordY(), getBoundTile().getHeight(), 0, 0.1, 4);
        shipShapes[2] = new LayeredSolidShape(getCoordX() + 0.275, getCoordY(), getBoundTile().getHeight(), 0.2, 0.1, (int)(0.45*WorldPanel.baseStraightUnit), 4, finShapes2);
        
        FlatShape[] finShapes3 = new FlatShape[4];
        finShapes3[0] = new FlatShape(getCoordX(), getCoordY()+ 0.21, (int)(WorldPanel.baseStraightUnit*0.45) + getBoundTile().getHeight(), 0.1, 0, 4);
        finShapes3[1] = new FlatShape(getCoordX(), getCoordY()+ 0.275, (int)(WorldPanel.baseStraightUnit*0.3) + getBoundTile().getHeight(), 0.1, 0.16, 4);
        finShapes3[2] = new FlatShape(getCoordX(), getCoordY()+ 0.34, (int)(WorldPanel.baseStraightUnit*0.14) + getBoundTile().getHeight(),  0.1, 0.08, 4);
        finShapes3[3] = new FlatShape(getCoordX(), getCoordY()+ 0.35, getBoundTile().getHeight(), 0.1, 0, 4);
        shipShapes[3] = new LayeredSolidShape(getCoordX(), getCoordY()+ 0.275, getBoundTile().getHeight(), 0.1, 0.2, (int)(0.45*WorldPanel.baseStraightUnit), 4, finShapes3);
        
        FlatShape[] finShapes4 = new FlatShape[4];
        finShapes4[0] = new FlatShape(getCoordX(), getCoordY()- 0.21, (int)(WorldPanel.baseStraightUnit*0.45) + getBoundTile().getHeight(), 0.1, 0, 4);
        finShapes4[1] = new FlatShape(getCoordX(), getCoordY()- 0.275, (int)(WorldPanel.baseStraightUnit*0.3) + getBoundTile().getHeight(), 0.1, 0.16, 4);
        finShapes4[2] = new FlatShape(getCoordX(), getCoordY()- 0.34, (int)(WorldPanel.baseStraightUnit*0.14) + getBoundTile().getHeight(),  0.1, 0.08, 4);
        finShapes4[3] = new FlatShape(getCoordX(), getCoordY()- 0.35, getBoundTile().getHeight(), 0.1, 0, 4);
        shipShapes[4] = new LayeredSolidShape(getCoordX(), getCoordY()- 0.275,  getBoundTile().getHeight(),  0.1, 0.2, (int)(0.45*WorldPanel.baseStraightUnit), 4, finShapes4);
        
    }
    public void setThread(Thread t)
    {
        thread = t;
    }
    
    public Thread getThread()
    {
        return thread;
    }
    
    public void setTakeoff(boolean b)
    {
        takeoff = b;
    }
    
    public void sortShapes()
    {
        
        
        for(int i = 0; i < shipShapes.length-1; i++)
        {
            int index = i;
            for(int j = i+1; j < shipShapes.length; j++)
            {
                if(shipShapes[j].getSortDistanceConstant() > shipShapes[index].getSortDistanceConstant())
                {
                    index = j;
                }
            }
            SolidShape temp = shipShapes[i];
            shipShapes[i] = shipShapes[index];
            shipShapes[index] = temp;
        }
        //System.out.println();
    }
    
   
    
    @Override
    public void draw(Graphics g) 
    { 
       sortShapes();
        
        
        //finShape.draw(g);
        //Graphics2D g2 = (Graphics2D)g;
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        //g.drawImage(scaledShip, (int)(getX() - (int)(40 * WorldPanel.scale)/2.0), (int)(getY() - distortedHeight((int)(y)) - (int)(WorldPanel.scale*distortedHeight((int)(40 * 3.60)))), null);
        
    
        try {
            
        } catch (Exception e) {
        }
        if(takeoff)
        {
            LevelLoader.isLoading = true;
            fireAnimationCount += .25;  
            
           
            for(SolidShape s : shipShapes)
            {
                s.setDZ(10);
                //s.setZPos(s.getZPos() + 10);
                //System.out.println("Z Pos: " + s.getZPos());
            }
             y += 10.0*WorldPanel.scale;
            if(fireAnimationCount >= 15)
            {
                fireAnimationCount = 0;
            }
            Image scaledFire = flameArray[(int)fireAnimationCount].getScaledInstance((int)(WorldPanel.scale*34), (int)(WorldPanel.scale*distortedHeight(21)), Image.SCALE_AREA_AVERAGING);
            g.drawImage(scaledFire, (int)(getX()-(int)(WorldPanel.scale*17)), (int)(getY()- distortedHeight((int)(y)) +(int)(WorldPanel.scale*distortedHeight(5))), null);
            if(y > (WorldPanel.screenHeight/distortedHeight(WorldPanel.screenHeight))*WorldPanel.screenHeight)
            {
                shipShapes = new SolidShape[0];
                Player.inSpaceship = false;
                takeoff = false;
                y = 0;
                //LevelLoader.spawnLevel(UI.level);
                LevelLoader ll = new LevelLoader();
                ll.spawnLevel(UI.level);
                for(SolidShape s : shipShapes)
                {
                    s.setDZ(0);
                    
                    //System.out.println("Z Pos: " + s.getZPos());
                }
            }
        }
        for(SolidShape s : shipShapes)
        {
            //System.out.println("Order Constant: " + s.getSortDistanceConstant());
            
            //s.setZPos(s.getZPos() + (int)s.getDZ());
            //s.updateShapePolygons();
            if(s == shipBody)
            {
                s.fillDropShadow(g, getBoundTile().getHeight());
                g.setColor(Color.GRAY);
            }else{
                s.fillDropShadow(g, getBoundTile().getHeight());
                g.setColor(Color.RED);
            }
            s.fill(g);
        }
        
    }
    
    @Override
    public void run()
    {
        //scaledShip = shipImage.getScaledInstance((int)(40 * WorldPanel.scale), (int)(WorldPanel.scale*distortedHeight((int)(40 * 3.60))), Image.SCALE_AREA_AVERAGING);
    }
    
    
}
