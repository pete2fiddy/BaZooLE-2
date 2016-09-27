/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author phusisian
 */
public class DayNight implements ActionListener
{
    private static final int daySeconds = 5;
    private static final int transitSeconds = 10;
    private String timeDescriber = "day";
    private static final Color nightColor = new Color(42, 57, 86);
    private static final Color dayColor = new Color(38, 94, 172);
    private Color color = dayColor;
    private static final int timerIncrement = 10;
    private double secondsTicked = 0;
    private Timer dayTimer = new Timer(timerIncrement, this);
    Point[] starPoints = new Point[75];
    public DayNight()
    {
        dayTimer.setActionCommand("tick");
        dayTimer.setRepeats(true);
        dayTimer.start();
        fillStarPoints();
    }
    
    private void fillStarPoints()
    {
        for(int i = 0; i < starPoints.length; i++)
        {
            starPoints[i] = new Point((int)(WorldPanel.screenWidth*Math.random()), (int)(WorldPanel.screenHeight * Math.random()));
        }
    }
    
    public Color getColor(){return color;}
    
    public void drawStars(Graphics g)
    {
        if(timeDescriber.equals("night"))
        {
            //g.setColor(Color.WHITE);
            for(Point p : starPoints)
            {
                //g.setColor(Color.YELLOW);
                //g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 8, 8);
                g.setColor(Color.WHITE);
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
        }else if(timeDescriber.equals("evening"))
        {
            int alpha = (int)(255*(secondsTicked/(double)transitSeconds));
            g.setColor(new Color(255,255,255,alpha));
            for(Point p : starPoints)
            {
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
        }else if(timeDescriber.equals("morning"))
        {
            g.setColor(new Color(255,255,255,(int)(255*((double)(transitSeconds-secondsTicked)/(double)transitSeconds))));
            for(Point p : starPoints)
            {
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
        }
    }
    
    public void nightShade(Graphics g)
    {
        /*if(timeDescriber.equals("evening") || timeDescriber.equals("night") || timeDescriber.equals("morning"))
        {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
            g.fillRect(0, 0, WorldPanel.screenWidth, WorldPanel.screenHeight);
        }*/
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        System.out.println(timeDescriber);
        secondsTicked += (double)timerIncrement/1000.0;
        if(timeDescriber.equals("evening"))
        {
            color = new Color((int)(dayColor.getRed() + (secondsTicked/(double)transitSeconds)*(nightColor.getRed() - dayColor.getRed())),
            (int)(dayColor.getGreen() + (secondsTicked/(double)transitSeconds)*(nightColor.getGreen() - dayColor.getGreen())), 
            (int)(dayColor.getBlue() + (secondsTicked/(double)transitSeconds)*(nightColor.getBlue() - dayColor.getBlue())));
        }else if(timeDescriber.equals("morning"))
        {
            color = new Color((int)(nightColor.getRed() - (secondsTicked/transitSeconds)*(nightColor.getRed() - dayColor.getRed())),
            (int)(nightColor.getGreen() - (secondsTicked/transitSeconds)*(nightColor.getGreen() - dayColor.getGreen())), 
            (int)(nightColor.getBlue() - (secondsTicked/transitSeconds)*(nightColor.getBlue() - dayColor.getBlue())));
        }
        
        if(secondsTicked > daySeconds && timeDescriber.equals("day"))
        {
            timeDescriber = "evening";
            secondsTicked = 0;
        }else if(secondsTicked > transitSeconds && timeDescriber.equals("evening"))
        {
            timeDescriber = "night";
            secondsTicked = 0;
        }else if(secondsTicked > daySeconds && timeDescriber.equals("night"))
        {
            timeDescriber = "morning";
            secondsTicked = 0;
        }else if(secondsTicked > transitSeconds && timeDescriber.equals("morning"))
        {
            timeDescriber = "day";
            secondsTicked = 0;
        }
        
    }
    
}
