/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;

/**
 * Alahu Akhbar
 * @author phusisian
 */
public class ColorPalette 
{
    public static final Color defaultSnowColor = new Color(251, 251, 251);
    public static final Color defaultGrassColor = new Color(80,124,41);
    public static Color grassColor = defaultGrassColor;//(89,139,44);
    public static double nightShadeAlpha = 0;
    public static double maxNightShadeAlpha = 0.25;
    public static Color shadeColor = Color.BLACK;
    public static Color shadedGrassColor =getLerpColor(shadeColor, grassColor, 0);
    
    public static Color getLerpColor(Color topColor, Color bottomColor, double alpha)
    {
        //double alphaNum = (double)(.65 - (.15*(WorldPanel.radSpin%(Math.PI/2.0))/(Math.PI/2.0)));
        
        int red, green, blue;
        
        if(topColor.getRed() > bottomColor.getRed())
        {
            red = (int)(topColor.getRed() + ((alpha)*(bottomColor.getRed()-topColor.getRed())));
        }else{
            red = (int)(topColor.getRed() + ((1-alpha)*(bottomColor.getRed()-topColor.getRed())));
        }
        if(topColor.getGreen() > bottomColor.getGreen())
        {
            green = (int)(topColor.getGreen() + ((alpha)*(bottomColor.getGreen()-topColor.getGreen())));
        }else{
            green = (int)(topColor.getGreen() + ((1-alpha)*(bottomColor.getGreen()-topColor.getGreen())));
        }
        if(topColor.getBlue() > bottomColor.getBlue())
        {
            blue = (int)(topColor.getBlue() + ((alpha)*(bottomColor.getBlue()-topColor.getBlue())));
        }else{
            blue = (int)(topColor.getBlue() + ((1-alpha)*(bottomColor.getBlue()-topColor.getBlue())));
        }
        return new Color(red,green,blue);
    }
    
    public static void updateShadedGrassColor()
    {
        shadedGrassColor = ColorPalette.getLerpColor(shadeColor, grassColor, nightShadeAlpha);
    }
    
    public static void setNightShadeAlpha(double numIn)
    {
        nightShadeAlpha = numIn;
    }
}
