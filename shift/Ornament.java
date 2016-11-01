/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author phusisian
 */
public class Ornament 
{
    private Color color = new Color(204,0,0);
    private double[] ornamentDim = {2.0/(double)WorldPanel.baseStraightUnit, 2.0/(double)WorldPanel.baseStraightUnit};
    private int zPos;
    private double rotation;
    private SolidShape boundShape;
    public static final Color[] colors = {new Color(204,0,0), new Color(0,153,0)};
    
    public Ornament(SolidShape boundShapeIn, int zPosIn, double rotationIn, int colorNum)
    {
        boundShape = boundShapeIn;
        rotation = rotationIn;
        //coordX = coordXIn;
        //coordY = coordYIn;
        zPos = zPosIn;
        color = colors[colorNum];
    }
    
    public void setColor(Color c){color = c;}
    
    public void draw(Graphics g)
    {
        System.out.println(zPos);
        TruncatedPyramid pyr = (TruncatedPyramid)boundShape;
        FlatShape s1 = pyr.getBaseShape();
        double baseR = s1.getRadius();
        s1.setRadius(s1.getRadius()-ornamentDim[0]);
        FlatShape s2 = pyr.getTopShape();
        s2.setRadius(s2.getRadius()- ornamentDim[0]); 
        
        double[] coord = pyr.getVisibleCoordAtHeightAndSpin(zPos, rotation, s1, s2);
        //double[] coord = pyr.getVisibleCoordAtHeightAndSpin(zPos, rotation, s1, s2);
        if(coord != null)
        {
            g.setColor(color);
            double alphaConstant = 1;
            for(double i = 1; i >= 0.5; i-=.25)
            {
                g.setColor(ColorPalette.getLerpColor(color, Color.WHITE, alphaConstant));//For some reason, get lerp color returns white no matter if color is listed as the top or bottom color with the same alpha
                g.fillOval((int)(Unit.convertToPointX(coord[0], coord[1])-(WorldPanel.straightUnit*ornamentDim[0]*i/2.0)), (int)(Unit.convertToPointY(coord[0], coord[1])-(WorldPanel.straightUnit*i*ornamentDim[1]/2.0)) - (int)Unit.getScaledDistortedHeight(pyr.getZPos()+zPos), (int)(WorldPanel.straightUnit*i*ornamentDim[0]), (int)(WorldPanel.straightUnit*i*ornamentDim[0]));
                alphaConstant -= 0.15;
            }
            
        }
        s1.setRadius(baseR);
        s2.setRadius(baseR);
    }
}
