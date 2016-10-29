/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author Peter
 */
public class Toolbox 
{   
    
    public static BasicStroke worldStroke = new BasicStroke((float)(1), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
   
    public static final Color defaultSnowColor = new Color(251, 251, 251);
    public static Color grassColor = defaultSnowColor;//new Color(14, 155, 14);
    public static final Color defaultGrassColor = new Color(14, 155, 14);
    public static BufferedImage defaultGrassImage, defaultSnowImage;
    public static WorldPanel worldPanel;
    public static Player player;
    public static double lowShade = 0.1;
    public static double highShade = 0.2;
    //public static double maxNightShade = 0.25;
    //public static double nightShadeAdd = 0;
    
    public Toolbox(WorldPanel wpIn, Player playerIn)
    {
        worldPanel = wpIn;
        player = playerIn;
        try{
            BufferedImage snow = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics g = snow.getGraphics();
            g.setColor(new Color(251, 251, 251));
            g.fillRect(0,0, 256, 256);
            defaultGrassImage = ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
            defaultSnowImage = snow;//ImageIO.read(WorldPanel.class.getClassLoader().getResourceAsStream("Images/Grass5.png"));
            if(wpIn.getDayNight().getSeason().equals("winter"))
            {
                Grass.lowGrassShade = Grass.defaultLowGrassSnowShade;//defaultSnowColor;
                grassColor = defaultSnowColor;
            }else{
                Grass.lowGrassShade = Grass.defaultLowGrassShade;
                grassColor = defaultGrassColor;
            }
            //grassTexture = new TexturePaint(grassImage, new Rectangle(0, 0, 256, 256));
        }catch(Exception e)
        {
            System.err.println(e);
        }
    }
    public Toolbox()
    {
        
    }
    
     public static void setToolboxPlayer(Player p){player = p;}
     public static void setWorldPanel(WorldPanel wpIn){worldPanel = wpIn;}
    
    
    
    public Player getPlayer()
    {
        return player;
    }
    
    public double distortedHeight(double heightIn)//one of the distortedHeights is redundant...
    {
        return Math.sin(WorldPanel.rotation)*heightIn;
    }  
    public double scaledDistortedHeight(double heightIn)
    {
        return WorldPanel.scale * distortedHeight(heightIn);
    }
    public double[] convertToUnit(double x, double y)//basically works by "unrotating" the world and applying the same algorithm to the position of the mouse along with it so that it can compare the unrotated mouse pos with the unrotated world pos. Works as intended. 
    {
        double dx = (x-WorldPanel.worldX)*WorldPanel.getShrink;//calculates unsquashed distance from the center of the world to the mouse ("unsquashing" it in the process so the calculation is what it would be on a flat world)
        double dy = y-WorldPanel.worldY;//calculates unsquahsed distance from center of map to mouse.
        
        double radiusHeight = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));//finds the radius of the oval's height (since circle is squashed by the amount the world is turned)
        double radiusWidth = radiusHeight/WorldPanel.getShrink;//finds the radius of the oval's width taking into account the squahsed world
        
        double theta = Math.atan2(-dy, dx);
        
        double unturneddx = radiusWidth*Math.cos(theta-WorldPanel.radSpin);
        double unturneddy = radiusHeight*Math.sin((theta-WorldPanel.radSpin));
        
        double[] giveReturn = {unturneddx/WorldPanel.straightUnit,unturneddy/(WorldPanel.straightUnit*WorldPanel.getShrink)};
        return giveReturn;
    }
    public static double[] convertToPoint(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        double[] giveReturn = {WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta)), WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta))};
        return giveReturn;
    }
    /*
    public double getX()
    {
        return WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + getOffsetTheta()));
    }
    public double getY()
    {
        return WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + getOffsetTheta()));//subtracting since y axis is flipped
    }
    public double getRadius()
    {
        return Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
    }
    public double getOffsetTheta()
    {
        return Math.atan2(y, x);
    }*/
}
