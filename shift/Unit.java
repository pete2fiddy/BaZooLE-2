/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

/**
 *
 * @author phusisian
 */
public class Unit {
    public static double convertToPointX(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        return WorldPanel.worldX + (radius * WorldPanel.straightUnit * Math.cos(WorldPanel.radSpin + offsetTheta));
    }
    
    
    public static double getDistortedHeight(double heightIn)//one of the distortedHeights is redundant...
    {
        return Math.sin(WorldPanel.rotation)*heightIn;
    } 
    public static double getScaledDistortedHeight(double heightIn)
    {
        return WorldPanel.scale * getDistortedHeight(heightIn);
    }
    public static double convertToPointY(double x, double y)
    {
        double radius = Math.sqrt( Math.pow((WorldPanel.worldX + x) - WorldPanel.worldX, 2) + Math.pow((WorldPanel.worldY + y) - WorldPanel.worldY,2));
        double offsetTheta = Math.atan2(y, x);
        return WorldPanel.worldY - (WorldPanel.getShrink * radius * WorldPanel.straightUnit * Math.sin(WorldPanel.radSpin + offsetTheta));
        
    }
}
