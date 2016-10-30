package shift;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import static javax.swing.SwingUtilities.convertPointFromScreen;
import javax.swing.Timer;

/*
Holds mouse-related info and makes it accessible to other classes.
*/

public class MouseInput extends MouseAdapter implements ActionListener
{
    public static int x, y, clickX, clickY, releaseX, releaseY;//x position of mouse, y position of mouse, x position of mouse click, y position of mouse click, x position of mouse released, y position of mouse released.
    public static double dScale;//rate at which the world needs to scale from scrolling.
    public static boolean clicked, rightClicked, clickReleaseBuffer;//if the mouse is currently clicked and held down, if the mouse is currently right clicked and held down, buffers mouseReleased and mousePressed events, although MAY not be needed. I can't remember.
    private static JPanel panelInstance;//holds the panel within which mouse position is made in relation to. Otherwise if you move the window, etc, use weird resolutions, x and y pos of mouse will be wrong. Is static since no other mouseInputs will be created and the same panel will always be used anyway, and needs to be used in a static method.
    private Timer wheelMovementTimer;//fires a timer to figure out when you've stopped scrolling.
    public static boolean quickClicked = false;
    public static int dragdx = 0, dragdy = 0;
    public static double tempWorldX, tempWorldY;
    public static String scrollType = "Zoom";
    public static double dHeight = 0;
    /*
    params: takes the WorldPanel, or the panel in which mouse clicks are made in relation to.
    */
    public MouseInput(JPanel p) 
    {
        dScale = 0;
        panelInstance = p;
        clickReleaseBuffer = false;
    }
    
    /*
    Consider moving to the Toolbox class. returns the current x and y coords of the position of the mouse on screen, rounded down.
    */
    public static int[] getMouseUnitPos()//basically works by "unrotating" the world and applying the same algorithm to the position of the mouse along with it so that it can compare the unrotated mouse pos with the unrotated world pos. Works as intended. 
    {
        double dx = (MouseInput.x-WorldPanel.worldX)*WorldPanel.shrink(WorldPanel.rotation);//calculates unsquashed distance from the center of the world to the mouse ("unsquashing" it in the process so the calculation is what it would be on a flat world)
        double dy = MouseInput.y-WorldPanel.worldY;//calculates unsquahsed distance from center of map to mouse.
        
        double radiusHeight = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));//finds the radius of the oval's height (since circle is squashed by the amount the world is turned)
        double radiusWidth = radiusHeight/WorldPanel.shrink(WorldPanel.rotation);//finds the radius of the oval's width taking into account the squahsed world
        
        double theta = 0.5;//Math.atan2(-dy, dx);
        
        double unturneddx = radiusWidth*Math.cos(theta-WorldPanel.radSpin);
        double unturneddy = radiusHeight*Math.sin(theta-WorldPanel.radSpin);
        
        int[] giveReturn = {(int)(Math.ceil(unturneddx/WorldPanel.straightUnit)),(int)(Math.ceil(unturneddy/(WorldPanel.straightUnit*WorldPanel.shrink(WorldPanel.rotation))))};
        return giveReturn;
    }
    
    /*
    Consider moving to the Toolbox class. returns the current x and y coords of the position of the mouse on screen, unrounded.
    */
    public static double[] getMouseUnitPosDouble()//basically works by "unrotating" the world and applying the same algorithm to the position of the mouse along with it so that it can compare the unrotated mouse pos with the unrotated world pos. Works as intended. 
    {
        double dx = (MouseInput.x-WorldPanel.worldX)*WorldPanel.shrink(WorldPanel.rotation);//calculates unsquashed distance from the center of the world to the mouse ("unsquashing" it in the process so the calculation is what it would be on a flat world)
        double dy = MouseInput.y-WorldPanel.worldY;//calculates unsquahsed distance from center of map to mouse.
        
        double radiusHeight = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));//finds the radius of the oval's height (since circle is squashed by the amount the world is turned)
        double radiusWidth = radiusHeight/WorldPanel.shrink(WorldPanel.rotation);//finds the radius of the oval's width taking into account the squahsed world
        
        double theta = 0.5;//Math.atan2(-dy, dx);
        
        double unturneddx = radiusWidth*Math.cos(theta-WorldPanel.radSpin);
        double unturneddy = radiusHeight*Math.sin(theta-WorldPanel.radSpin);
        
        double[] giveReturn = {unturneddx/WorldPanel.straightUnit,unturneddy/(WorldPanel.straightUnit*WorldPanel.shrink(WorldPanel.rotation))};
        return giveReturn;
    }
    
    /*
    handles mouse wheel movement. Sets dScale.
    */
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int notches = e.getWheelRotation();
        if (notches > 0) {
            if(scrollType.equals("Zoom"))
            {
                dScale = .03*WorldPanel.scale;//smoother scroll??? find a way to make it not speed up scaling as it shrinks -- perhaps a way to set dScale compared to the amount it is shrunk already?
            }else if(scrollType.equals("Height"))
            {
                dHeight = 1.25;
            }
        } else if (notches < 0) {
            if(scrollType.equals("Zoom"))
            {
                dScale = -.03*WorldPanel.scale;
            }
            else if(scrollType.equals("Height"))
            {
                dHeight = -1.25;
            }
        }
        if (wheelMovementTimer != null && wheelMovementTimer.isRunning()) {
            wheelMovementTimer.stop();
        }
        wheelMovementTimer = new Timer(100, this);
        wheelMovementTimer.setRepeats(false);
        wheelMovementTimer.start();
    }

    /*
    fires after a suitable time has passed since the last mouse wheel movement meaning that the scrolling has stopped.
    */
    public void actionPerformed(ActionEvent e)
    {
        dScale = 0;
        dHeight = 0;
    }

    /*
    What happens when the mouse is pressed (not just clicked, but held down). Sets right clicked and left clicked.
    */
    public void mousePressed(MouseEvent e)
    {
        if(SwingUtilities.isLeftMouseButton(e))
        {
            clicked = true;
            clickReleaseBuffer = true;
            clickX = e.getX();
            clickY = e.getY();
            tempWorldX = WorldPanel.worldX;
            tempWorldY = WorldPanel.worldY;
        }else if(SwingUtilities.isRightMouseButton(e))
        {
            rightClicked = true;
            clickReleaseBuffer = true;
        }
    }
    
    /*
    what happens when mouse is released. May only fire when clicked then released immediately? Not sure why I have both released methods.
    */
    public void mouseReleased(MouseEvent e) 
    {
        clicked = false;
        releaseX = e.getX();
        releaseY = e.getY();
    }
    
    /*
    returns true if the mouse has JUST been released. Like how keys have keyPressed and keyReleased, this would be clickReleased. Not sure if even neccessary?
    */
    public static boolean clickJustReleased()
    {
        if(clickReleaseBuffer && !clicked)
        {
            clickReleaseBuffer = false;
            return true;
        }
        return false;
    }
    
    public void mouseClicked(MouseEvent e) 
    {
        //quickClicked = true;
    }

    public void mouseEntered(MouseEvent e) 
    {

    }

    
    
    /*
    Is fired from another class's draw. May want to thread this to constantly be updating later. Isn't particularly taxing so I haven't.
    */
    public static void updatePos() 
    {
        PointerInfo info = MouseInfo.getPointerInfo();
        Point b = info.getLocation();
        
        
        convertPointFromScreen(b, panelInstance);
        x = (int) b.getX();
        y = (int) b.getY();
        if(clickX - x > 3 || clickY - y > 3)
        {
            dragdx = clickX - x;
            dragdy = clickY - y;
        }else{
            dragdx = 0;
            dragdy = 0;
        }
    }
}
