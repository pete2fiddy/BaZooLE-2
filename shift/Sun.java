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

/**
 *
 * @author phusisian
 */
public class Sun 
{
    private int changeRed = 3, changeGreen = -79, changeBlue = 10;
    private static final Color baseColor = new Color(242, 237,111);
    private static final Color[] sunColors = {new Color(242, 237,111), new Color(244,224,77), new Color(241, 145, 167)};
    private double relX, relY;
    private static final int baseMaxHeight = 700; //500 above worldX and worldY
    private int baseSunDim = 225;
    public Sun()
    {
        relX = 0;
        relY = baseMaxHeight;
        
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
        Graphics2D g2 = (Graphics2D)g;
        
        Composite originalComposite = g2.getComposite();
        //g.setColor(Color.YELLOW);
        //g.fillOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*baseSunDim/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*baseSunDim/2.0)), (int)(WorldPanel.scale*baseSunDim), (int)(WorldPanel.scale*baseSunDim));
        double smallR = baseSunDim;
        double bigR = baseSunDim + 1000 - (((relY-(baseMaxHeight/8))/(double)baseMaxHeight)*1000) + 75;
        int type = AlphaComposite.SRC_OVER;
        int increments = 25;
        double redInc = (double)changeRed/(double)increments;
        double greenInc = (double)changeGreen/(double)increments;
        double blueInc = (double)changeBlue/(double)increments;
        double expandAmount = (bigR-smallR)/increments;
        double constant = (double)(bigR-smallR)/(double)Math.pow(increments, 2);
        double initialAlpha = 0;
        //int radiusCount = 0;
        if(relY < baseMaxHeight/3)
        {
            initialAlpha = -(1-((relY)/(double)(baseMaxHeight/3)));
        }
        for(int i = increments; i > 0; i--)
        {
            
            if(initialAlpha + ((double)(increments-i)/(double)increments) > 0)
            {
                AlphaComposite transparencyComposite = AlphaComposite.getInstance(type, (float)(initialAlpha+((double)(increments-i)/(double)increments)));

                g2.setComposite(transparencyComposite);
                //double radius = smallR + 0.1*Math.pow(radiusCount, 2);
                double radius = constant*Math.pow(i,2) + smallR;
                //Color c = new Color(baseColor.getRed()+(int)(redInc*i), baseColor.getGreen()+(int)(greenInc*i), baseColor.getBlue()+(int)(blueInc*i), (int)(255*((double)(increments-i)/(double)increments)));
                Color c = new Color(baseColor.getRed()+(int)(redInc*i), baseColor.getGreen()+(int)(greenInc*i), baseColor.getBlue()+(int)(blueInc*i));

                g.setColor(c);
                drawSunWithRadius(g,radius);
                
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
    
    private void drawSunWithRadius(Graphics g, double r)
    {
        g.fillOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.scale*r), (int)(WorldPanel.scale*r));
        //g.setColor(Color.BLACK);
        //g.drawOval((int)(WorldPanel.worldX + (WorldPanel.scale*relX) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.worldY - (WorldPanel.scale*relY) - (WorldPanel.scale*r/2.0)), (int)(WorldPanel.scale*r), (int)(WorldPanel.scale*r));
    }
}
