package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class DayNight implements ActionListener
{
    private final double starSpeed = .05;
    private static final int daySeconds = 10;
    private static final int transitSeconds = 5;
    private String timeDescriber = "day";
    private static final Color nightColor = new Color(42, 57, 86);
    private static final Color dayColor = new Color(38, 94, 172);
    public static Color color = dayColor;
    private static final int timerIncrement = 10;
    private double secondsTicked = 0;
    private Timer dayTimer = new Timer(timerIncrement, this);
    Point[] starPoints = new Point[75];
    private int daysPassed = 0;
    private int daysSinceSeasonChange=0;
    public static String season = "winter";
    private int starMoveCount = 0;
    private int starMove = 0;
    private Sun sun = new Sun();
    public DayNight()
    {
        dayTimer.setActionCommand("tick");
        dayTimer.setRepeats(true);
        dayTimer.start();
        fillStarPoints();
    }
    
    public static void spawnSceneryOnTileType(Class<?> tileType, double rarity, String sceneryType)
    {
        double radiusApart = 0.05;
        
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            if(TileDrawer2.tileList.get(i).getClass() == tileType)
            {
                if(Math.random() < rarity)
                {
                    double offsetX = radiusApart + 0.05*(int)((1-2*radiusApart)*Math.random()/radiusApart);
                    double offsetY = radiusApart + 0.05*(int)((1-2*radiusApart)*Math.random()/radiusApart);
                    if(sceneryType.equals("Pumpkin"))
                    {
                        Pumpkin m = new Pumpkin(TileDrawer2.tileList.get(i), offsetX, offsetY);
                        if(m.getOffsetX() < m.getBoundingBoxWidth()/(double)m.getBoundTile().getRawWidth())
                        {
                            m.addUnitsToOffset(m.getBoundingBoxWidth()/2.0, 0);
                        }else if(m.getOffsetX() > m.getBoundingBoxWidth()/(double)(1-m.getBoundTile().getRawWidth()))
                        {
                            m.addUnitsToOffset(-m.getBoundingBoxWidth()/2.0, 0);
                        }
                        if(m.getOffsetY() < m.getBoundingBoxLength()/(double)m.getBoundTile().getRawLength())
                        {
                            m.addUnitsToOffset(0, m.getBoundingBoxLength()/2.0);
                        }else if(m.getOffsetY() > m.getBoundingBoxLength()/(double)(1-m.getBoundTile().getRawLength()))
                        {
                            m.addUnitsToOffset(0, -m.getBoundingBoxLength());
                        }
                    }else if(sceneryType.equals("Tree"))
                    {
                        Tree m = new Tree(TileDrawer2.tileList.get(i), offsetX, offsetY);
                        if(m.getOffsetX() < m.getBoundingBoxWidth()/(double)m.getBoundTile().getRawWidth())
                        {
                            m.addUnitsToOffset(m.getBoundingBoxWidth(), 0);
                        }else if(m.getOffsetX() > m.getBoundingBoxWidth()/(double)(1-m.getBoundTile().getRawWidth()))
                        {
                            m.addUnitsToOffset(-m.getBoundingBoxWidth(), 0);
                        }
                        if(m.getOffsetY() < m.getBoundingBoxLength()/(double)m.getBoundTile().getRawLength())
                        {
                            m.addUnitsToOffset(0, m.getBoundingBoxLength());
                        }else if(m.getOffsetY() > m.getBoundingBoxLength()/(double)(1-m.getBoundTile().getRawLength()))
                        {
                            m.addUnitsToOffset(0, -m.getBoundingBoxLength());
                        }
                    }else if(sceneryType.equals("Snowman"))
                    {
                        Snowman m = new Snowman(TileDrawer2.tileList.get(i), offsetX, offsetY);
                        if(m.getOffsetX() < m.getBoundingBoxWidth()/(double)m.getBoundTile().getRawWidth())
                        {
                            m.addUnitsToOffset(m.getBoundingBoxWidth()/2.0, 0);
                        }else if(m.getOffsetX() > m.getBoundingBoxWidth()/(double)(1-m.getBoundTile().getRawWidth()))
                        {
                            m.addUnitsToOffset(-m.getBoundingBoxWidth()/2.0, 0);
                        }
                        if(m.getOffsetY() < m.getBoundingBoxLength()/(double)m.getBoundTile().getRawLength())
                        {
                            m.addUnitsToOffset(0, m.getBoundingBoxLength()/2.0);
                        }else if(m.getOffsetY() > m.getBoundingBoxLength()/(double)(1-m.getBoundTile().getRawLength()))
                        {
                            m.addUnitsToOffset(0, -m.getBoundingBoxLength());
                        }
                    }else{
                        System.err.println("Scenery not supported by method 'spawnSceneryForAllTiles'");
                    }
                }
            }
        }
        
        
    }
    
    public static void addSeasonalScenery(String season)
    {
        if(season.equals("winter"))
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
        }else if(season.equals("summer"))
        {
            for(int i = 0; i < TileDrawer2.tileList.size(); i++)
            {
                if(Math.random()>0.75)
                {
                    double offsetX = Math.random();
                    double offsetY = Math.random();
                    Pumpkin p = new Pumpkin(TileDrawer2.tileList.get(i), offsetX, offsetY);
                }
            }
        }
    }
    
    public static void removeSeasonalScenery(String season)
    {
        if(!season.equals("winter"))
        {
            removeSceneryType(Snowman.class);
        }else if(!season.equals("summer"))
        {
            removeSceneryType(Pumpkin.class);
        }
    }
    
    public static void removeSceneryType(Class<?> c)
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            for(int j = 0; j < TileDrawer2.tileList.get(i).getAssortedScenery().size(); j++)
            {
                if(TileDrawer2.tileList.get(i).getAssortedScenery().get(j).getClass() == c)
                {
                    TileDrawer2.tileList.get(i).getAssortedScenery().remove(j);
                    j--;
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
    
    /*
    Draws the background and in-game time-related objects
    */
    public Color getColor(){return color;}
    
    public void draw(Graphics g)
    {
        if(!timeDescriber.equals("night"))
        {
            sun.draw(g);
        }
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
            //g.setColor(new Color(255,255,255,alpha));
            g.setColor(Color.WHITE);
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
            g.setColor(Color.WHITE);
            //g.setColor(new Color(255,255,255,(int)(255*((double)(transitSeconds-secondsTicked)/(double)transitSeconds))));
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
            //addSnowmen();
            season = "winter";
            addSeasonalScenery(season);
            removeSeasonalScenery(season);
            ColorPalette.grassColor = ColorPalette.defaultSnowColor;
            ColorPalette.updateShadedGrassColor();
            //Toolbox.grassColor = Toolbox.defaultSnowColor;
            Grass.lowGrassShade = Grass.defaultLowGrassSnowShade;
            shortenGrass(3);
        }else if(season.equals("winter"))
        {
            //removeSnowmen();
            season = "summer";
            addSeasonalScenery(season);
            removeSeasonalScenery(season);
            ColorPalette.grassColor = ColorPalette.defaultGrassColor;
            ColorPalette.updateShadedGrassColor();
            //Toolbox.grassColor = Toolbox.defaultGrassColor;
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
            ColorPalette.nightShadeAlpha = ColorPalette.nightShadeAlpha + ColorPalette.maxNightShadeAlpha*((double)timerIncrement/1000.0)/(double)transitSeconds;
            ColorPalette.updateShadedGrassColor();
            //ColorPalette.nightShadeAlpha = ColorPalette.nightShadeAlpha+Toolbox.maxNightShade*((double)timerIncrement/1000.0)/(double)transitSeconds;
            WorldPanel.waterColor = ColorPalette.getLerpColor(ColorPalette.shadeColor, WorldPanel.baseWaterColor, ColorPalette.nightShadeAlpha);
            sun.controlSun(-(((double)timerIncrement/1000.0)/(double)transitSeconds) * sun.getBaseMaxHeight());
            color = new Color((int)(dayColor.getRed() + (secondsTicked/(double)transitSeconds)*(nightColor.getRed() - dayColor.getRed())),
            (int)(dayColor.getGreen() + (secondsTicked/(double)transitSeconds)*(nightColor.getGreen() - dayColor.getGreen())), 
            (int)(dayColor.getBlue() + (secondsTicked/(double)transitSeconds)*(nightColor.getBlue() - dayColor.getBlue())));
        }else if(timeDescriber.equals("morning"))
        {
            ColorPalette.updateShadedGrassColor();
            ColorPalette.nightShadeAlpha = ColorPalette.nightShadeAlpha - ColorPalette.maxNightShadeAlpha*((double)timerIncrement/1000.0)/(double)transitSeconds;
            //ColorPalette.nightShadeAlpha = ColorPalette.nightShadeAlpha - Toolbox.maxNightShade*(((double)timerIncrement/1000.0))/(double)transitSeconds;
            WorldPanel.waterColor = ColorPalette.getLerpColor(ColorPalette.shadeColor, WorldPanel.baseWaterColor, ColorPalette.nightShadeAlpha);
            sun.controlSun((((double)timerIncrement/1000.0)/(double)transitSeconds) * sun.getBaseMaxHeight());
            color = new Color((int)(nightColor.getRed() - (secondsTicked/transitSeconds)*(nightColor.getRed() - dayColor.getRed())),
            (int)(nightColor.getGreen() - (secondsTicked/transitSeconds)*(nightColor.getGreen() - dayColor.getGreen())), 
            (int)(nightColor.getBlue() - (secondsTicked/transitSeconds)*(nightColor.getBlue() - dayColor.getBlue())));
        }
        
        if(secondsTicked > daySeconds && timeDescriber.equals("day"))
        {
            sun.setHeight(sun.getBaseMaxHeight());
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
        //System.out.println(ColorPalette.nightShadeAlpha);
    }
    
}
