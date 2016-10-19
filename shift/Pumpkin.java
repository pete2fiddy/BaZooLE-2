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
public class Pumpkin extends Scenery
{
    private static final Color pumpkinColor = new Color(255,102,51);
    private static final Color stemColor = new Color(153,102,51);
    private RectPrism pumpkinShape;
    private RectPrism stemShape;
    private static final double defaultPumpkinDim = 0.2;
    private static final int defaultPumpkinHeight = 10;
    private static final double defaultStemDim = 0.03;
    private int defaultStemHeight = 3;
    private double scale;
    public Pumpkin(Tile tileIn, double offsetXIn, double offsetYIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        scale = 0.5+(Math.random()/1.5);
        pumpkinShape = new RectPrism(getCoordX(), getCoordY(),tileIn.getHeight(), (defaultPumpkinDim*scale), (defaultPumpkinDim*scale), (int)(defaultPumpkinHeight*scale));
        stemShape = new RectPrism(getCoordX(), getCoordY(), tileIn.getHeight() + (int)(defaultPumpkinHeight*scale), defaultStemDim*scale, defaultStemDim*scale, (int)(defaultStemHeight*scale));
        tileIn.addAssortedScenery(this);
    }

    @Override
    public void draw(Graphics g)
    {
        if(isVisible(g))
        {
            pumpkinShape.setCenterCoordX(getCoordX() + pumpkinShape.getOffsetX());
            pumpkinShape.setCenterCoordY(getCoordY() + pumpkinShape.getOffsetY());
            stemShape.setCenterCoordX(getCoordX() + stemShape.getOffsetX());
            stemShape.setCenterCoordY(getCoordY() + stemShape.getOffsetY());
            pumpkinShape.updateShapePolygons();
            stemShape.updateShapePolygons();
            g.setColor(pumpkinColor);
            pumpkinShape.fill(g);
            pumpkinShape.paintShading(g);
            g.setColor(stemColor);
            stemShape.fill(g);
            stemShape.paintShading(g);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
