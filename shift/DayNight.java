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
    private final double starSpeed = .05;
    private static final int daySeconds = 20;
    private static final int transitSeconds = 5;
    private String timeDescriber = "day";
    private static final Color nightColor = new Color(42, 57, 86);
    private static final Color dayColor = new Color(38, 94, 172);
    private Color color = dayColor;
    private static final int timerIncrement = 10;
    private double secondsTicked = 0;
    private Timer dayTimer = new Timer(timerIncrement, this);
    Point[] starPoints = new Point[75];
    private int daysPassed = 0;
    private int daysSinceSeasonChange=0;
    private String season = "summer";
    private int starMoveCount = 0;
    private int starMove = 0;
    public DayNight()
    {
        dayTimer.setActionCommand("tick");
        dayTimer.setRepeats(true);
        dayTimer.start();
        fillStarPoints();
    }
    
    public void addSnowmen()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(Math.random()>0.75)
            {
                double offsetX = Math.random();
                double offsetY = Math.random();
                Snowman s = new Snowman(TileDrawer2.tileList.get(i), offsetX, offsetY);
            }
        }
    }
    
    public void removeSnowmen()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            for(int j = 0; j < TileDrawer2.tileList.get(i).getAssortedScenery().size(); j++)
            {
                if(TileDrawer2.tileList.get(i).getAssortedScenery().get(j).getClass() == Snowman.class)
                {
                    TileDrawer2.tileList.get(i).getAssortedScenery().remove(j);
                }
            }
        }
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
                
                //p.setLocation((double)(p.getX() + starMove), (double)p.getY());
                
                if(p.getX() > WorldPanel.screenWidth)
                {
                    p.setLocation(0, p.getY());
                }
                //g.setColor(Color.YELLOW);
                //g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 8, 8);
                g.setColor(Color.WHITE);
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
            if(starMove > 1)
            {
                starMove = 0;
            }
        }else if(timeDescriber.equals("evening"))
        {
            int alpha = (int)(255*(secondsTicked/(double)transitSeconds));
            g.setColor(new Color(255,255,255,alpha));
            for(Point p : starPoints)
            {
                //p.setLocation((double)(p.getX() + starMove), (double)p.getY());
                
                if(p.getX() > WorldPanel.screenWidth)
                {
                    p.setLocation(0, p.getY());
                }
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
            if(starMove > 1)
            {
                starMove = 0;
            }
        }else if(timeDescriber.equals("morning"))
        {
            g.setColor(new Color(255,255,255,(int)(255*((double)(transitSeconds-secondsTicked)/(double)transitSeconds))));
            for(Point p : starPoints)
            {
                //p.setLocation((double)(p.getX() + starMove), (double)p.getY());
                
                if(p.getX() > WorldPanel.screenWidth)
                {
                    p.setLocation(0, p.getY());
                }
                g.fillOval((int)p.getX() - 3, (int)p.getY() - 3, 6, 6);
            }
            if(starMove > 1)
            {
                starMove = 0;
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
    
    public String getSeason(){return season;}
    
    public static void shortenGrass(int amount)
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            for(Grass g : TileDrawer2.tileList.get(i).getGrassList())
            {
                if(g.getHeight()-amount >= Grass.minRadius)
                {
                    g.setHeight(g.getHeight()-amount);
                }else{
                    g.setHeight(Grass.minRadius);
                }
            }
            
        }
    }
    
    public static void restoreGrassHeight()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            for(Grass g : TileDrawer2.tileList.get(i).getGrassList())
            {
                g.setHeight(g.getInitialHeight());
            }
            //TileDrawer2.tileList.get(i).setHeight(TileDrawer2.tileList.get(i).getInitialHeight());
        }
    }
    
    public void toggleSeason()
    {
        if(season.equals("summer"))
        {
            addSnowmen();
            season = "winter";
            Toolbox.grassColor = Toolbox.defaultSnowColor;
            WorldPanel.grassImage = Toolbox.defaultSnowImage;
            Grass.lowGrassShade = Grass.defaultLowGrassSnowShade;
            shortenGrass(3);
        }else if(season.equals("winter"))
        {
            removeSnowmen();
            season = "summer";
            Toolbox.grassColor = Toolbox.defaultGrassColor;
            WorldPanel.grassImage = Toolbox.defaultGrassImage;
            Grass.lowGrassShade = Grass.defaultLowGrassShade;
            restoreGrassHeight();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        starMoveCount++;
        if(starMoveCount > 3 && (timeDescriber.equals("night") || timeDescriber.equals("morning") || timeDescriber.equals("evening")))
        {
            for(Point p : starPoints)
            {
                p.setLocation((double)(p.getX() + 1), (double)p.getY());
            }
            starMoveCount = 0;
        }
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
            daysPassed++;
            daysSinceSeasonChange++;
            if(Math.random() > 1.0/(double)daysSinceSeasonChange)
            {
                toggleSeason();
                daysSinceSeasonChange = 0;
            }
        }
        
    }
    
}
