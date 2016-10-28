package shift;

import javax.swing.JFrame;

public class Frame extends JFrame
{
    public static final int screenWidth = 1440, screenHeight = 900;//screenWidth dictates the screen's width(and the world panel's), screenHeight dictates the screen's height(and the world panel's)
    
    public Frame()
    {
        super("BaZoo!E");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(true);
        /*Keep below in mind if I ever want to adapt this to any screen*/
        //setSize(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width, java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height);
        WorldPanel wp = new WorldPanel(this);
        setSize(screenWidth, screenHeight);
        add(wp);
    }
}
