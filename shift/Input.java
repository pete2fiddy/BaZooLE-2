package shift;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Input extends KeyAdapter
{
    public static double givedx, givedy, dRotation, dSpin;
    public Input()
    {
        givedx = 0; givedy = 0; dRotation = 0;
    }
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if(keyCode == e.VK_LEFT)
        {
            givedx = -3;
        }
        if(keyCode == e.VK_RIGHT)
        {
            givedx = 3;
        }
        if(keyCode == e.VK_UP)
        {
            givedy = -3;
        }
        if(keyCode == e.VK_DOWN)
        {
            givedy = 3;
        }
        if(keyCode == e.VK_W)
        {
            dRotation = -(Math.PI/400.0);
        }
        if(keyCode == e.VK_S)
        {
            dRotation = (Math.PI/400.0);
        }
        if(keyCode == e.VK_A)
        {
            dSpin = (Math.PI/200.0);//used to be /200.0
        }
        if(keyCode == e.VK_D)
        {
            dSpin = (-Math.PI/200.0);
        }
    }
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if(keyCode == e.VK_LEFT)
        {
            givedx = 0;
        }
        if(keyCode == e.VK_RIGHT)
        {
            givedx = 0;
        }
        if(keyCode == e.VK_UP)
        {
            givedy = 0;
        }
        if(keyCode == e.VK_DOWN)
        {
            givedy = 0;
        }
        if(keyCode == e.VK_W)
        {
            dRotation = 0;
        }
        if(keyCode == e.VK_S)
        {
            dRotation = 0;
        }
        if(keyCode == e.VK_A)
        {
            dSpin = 0;
        }
        if(keyCode == e.VK_D)
        {
            dSpin = 0;
        }
    }
}