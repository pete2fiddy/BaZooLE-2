/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author phusisian
 */
public class Sun 
{
    private int changeRed = 3, changeGreen = -79, changeBlue = 10;
    private static final Color baseColor = new Color(242, 237,111);
    private static final Color[] sunColors = {new Color(242, 237,111), new Color(244,224,77), new Color(241, 145, 167)};
    private static double relX, relY;
    private static final int unscaledBaseMaxHeight = 500;
    private static int baseMaxHeight = 500; //500 above worldX and worldY
    private static int unscaledSunDim = 225;
    private static int baseSunDim;// = 225;
    private static double unscaledUpperSunDim = 1000 - (((relY-(baseMaxHeight/8))/(double)baseMaxHeight)*1000) + 75;
    private static double baseUpperSunDim;// = baseSunDim + 1000 - (((relY-(baseMaxHeight/8))/(double)baseMaxHeight)*1000) + 75;
    private static double minScale;
    private static double sunMargin;
    public Sun()
    {
        relX = 0;
        relY = baseMaxHeight;
        
    }
    
    public static void setHeightWithScale(double minScaleIn)
    {
        minScale = minScaleIn;
        baseSunDim = (int)(unscaledSunDim/minScaleIn);
        baseUpperSunDim = baseSunDim + (unscaledUpperSunDim/minScaleIn);
        relY = (int)(baseMaxHeight/minScaleIn);
        baseMaxHeight = (int)((double)unscaledBaseMaxHeight/minScaleIn);
        sunMargin = 75.0/minScale;
    }
    
    public void controlSun( double amount)
    {
        relY += amount;
        
    }
    
    public void setHeight(double amount)
    {
        relY = amount;
    }
    
    public int getBaseMaxHeight(){return baseMaxHeight;}
    
    public void draw(Graphics g)
    {
        
        Area screenArea = new Area(new Rectangle(0,0, WorldPanel.screenWidth, WorldPanel.screenHeight));
        
        Graphics2D g2 = (Graphics2D)g;
        
        Composite originalComposite = g2.getComposite();
        //g.setColor(Color.YELLOW);
        //g.fillOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*baseSunDim/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*baseSunDim/2.0)), (int)(WorldPanel.scale*baseSunDim), (int)(WorldPanel.scale*baseSunDim));
        //System.out.println(baseSunDim);
        //System.out.println(baseUpperSunDim);
        double smallR = baseSunDim;
        double percentRisen = (baseMaxHeight-relY)/(baseMaxHeight);
        //System.out.println("Percent risen: " + percentRisen);
        double bigR = baseUpperSunDim - (baseUpperSunDim-baseSunDim)*(1-percentRisen) + sunMargin;// - (baseUpperSunDim-baseSunDim)*((relY-baseMaxHeight)/(baseUpperSunDim-baseSunDim));//baseSunDim + 1000 - (((relY-(baseMaxHeight/8))/(double)baseMaxHeight)*1000) + 75;
        //System.out.println(bigR);
        int type = AlphaComposite.SRC_OVER;
        int increments = 25;
        double redInc = (double)changeRed/(double)increments;
        double greenInc = (double)changeGreen/(double)increments;
        double blueInc = (double)changeBlue/(double)increments;
        double expandAmount = (bigR-smallR)/increments;
        double constant = (double)((bigR-smallR))/(double)Math.pow(increments, 2);
        double initialAlpha = 0;
        //int radiusCount = 0;
        if(relY < baseMaxHeight/3)
        {
            initialAlpha = -(1-((relY)/(double)(baseMaxHeight/3)));
        }
        Area sunArea = new Area();
        for(int i = increments; i > 0; i--)
        {
            
            if(initialAlpha + ((double)(increments-i)/(double)increments) > 0)
            {
                //AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)(initialAlpha+((double)(increments-i)/(double)increments)));

                //g2.setComposite(transparencyComposite);
                //double radius = smallR + 0.1*Math.pow(radiusCount, 2);
                double radius = constant*Math.pow(i,2) + smallR;
                //Color c = new Color(baseColor.getRed()+(int)(redInc*i), baseColor.getGreen()+(int)(greenInc*i), baseColor.getBlue()+(int)(blueInc*i), (int)(255*((double)(increments-i)/(double)increments)));
                Color c = new Color(baseColor.getRed()+(int)(redInc*i), baseColor.getGreen()+(int)(greenInc*i), baseColor.getBlue()+(int)(blueInc*i));

                //g.setColor(c);
                drawSunWithRadius(g, c, sunArea,radius, (double)1.0-(initialAlpha+((double)(increments-i)/(double)increments)), screenArea);
                
            }
            //radiusCount++;
        }
        
        g2.setComposite(originalComposite);
        
        /*int expandAmount = 20;
        for(int i = sunColors.length - 1; i > 0; i--)
        {
            g.setColor(sunColors[i]);
            g.fillOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*(baseSunDim + (i*expandAmount))/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*(baseSunDim + (i*expandAmount))/2.0)), (int)(WorldPanel.scale*(baseSunDim + (i*expandAmount))), (int)(WorldPanel.scale*(baseSunDim + (i*expandAmount))));
        }*/
        
    }
    
    private void drawSunWithRadius(Graphics g, Color c, Area sunArea, double r, double transparency, Area screenArea)
    {
        int red = (int)(c.getRed() - (transparency*(c.getRed()-DayNight.color.getRed())));
        int green = (int)(c.getGreen() - (transparency*(c.getGreen()-DayNight.color.getGreen())));
        int blue = (int)(c.getBlue() - (transparency*(c.getBlue()-DayNight.color.getBlue())));
        g.setColor(new Color(red,green,blue));
        Ellipse2D.Double sunEllipse = new Ellipse2D.Double((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.scale*r), (int)(WorldPanel.scale*r));
        Area a = new Area(sunEllipse);
        a.intersect(sunArea);
        a.subtract(WorldPanel.belowMapArea);
        Graphics2D g2 = (Graphics2D)g;
        a.intersect(sunArea);
        g2.fill(a);
        sunArea.add(new Area(sunEllipse));
        //g.fillOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.scale*r), (int)(WorldPanel.scale*r));
        
    }
}
