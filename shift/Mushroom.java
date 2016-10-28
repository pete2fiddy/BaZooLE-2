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
public class Mushroom extends Scenery
{
    private static final double defaultRadius = 0.1;
    private double radius;
    private static final int defaultStemHeight = 6;
    private static final int defaultShroomHeight = 7;
    private double stemHeight, shroomHeight;
    private SolidShape[] shapes = new SolidShape[2];
    private double scale = 1;
    public Mushroom(Tile tileIn, double offsetXIn, double offsetYIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        shapes[0] = new Prism(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), tileIn.getHeight(), defaultRadius/3.0, defaultStemHeight, 8);
        FlatShape[] flatShapes = new FlatShape[5];
        int numShape = 0;
        for(double d = 0; d < Math.PI/2.0; d+= (Math.PI/2.0)/((double)flatShapes.length-1))
        {
            if(numShape < flatShapes.length-1)
            {
                double width = defaultRadius*2*Math.cos(d);
                int zPos = (int)(tileIn.getHeight() + defaultStemHeight+(numShape*(defaultShroomHeight/((double)flatShapes.length-1))));
                flatShapes[numShape]=new FlatShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), zPos, width/2.0, 8);
            }
            numShape++;
        }
        flatShapes[4] = new FlatShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), flatShapes[3].getZPos() + 1, 0, 8);
        shapes[1]=new LayeredSolidShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), tileIn.getHeight() + defaultStemHeight, defaultRadius*2, defaultRadius*2, defaultShroomHeight, 8, flatShapes);
        stemHeight = defaultStemHeight;
        shroomHeight = defaultShroomHeight;
        radius = defaultRadius;
        tileIn.addAssortedScenery(this);
        setBoundingBoxDimensions(shapes[1].getWidth(), shapes[1].getLength());
    }
    
    public Mushroom(Tile tileIn, double offsetXIn, double offsetYIn, double scaleIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        stemHeight = (defaultStemHeight*scaleIn);
        shroomHeight = (defaultShroomHeight*scaleIn);
        radius = defaultRadius*scaleIn;
        scale = scaleIn;
        shapes[0] = new Prism(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), tileIn.getHeight(), radius/3.0, (int)stemHeight, 8);
        FlatShape[] flatShapes = new FlatShape[5];
        int numShape = 0;
        /*for(double d = 0; d < Math.PI/2.0; d+= (Math.PI/2.0)/((double)flatShapes.length))
        {
            if(numShape < flatShapes.length)
            {
                double width = radius*2*Math.cos(d);
                int zPos = (int)(tileIn.getHeight() + stemHeight + (double)(defaultShroomHeight*scaleIn)*Math.sin(d));//(int)(tileIn.getHeight() + stemHeight+(numShape*(shroomHeight/((double)(flatShapes.length)))));
                flatShapes[numShape]=new FlatShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), zPos, width/2.0, 8);
                System.out.println("Time called");
            }
            numShape++;
        }
        System.out.println();*/
        double rotation = 0;
        for(int i = 0; i < flatShapes.length; i++)
        {
            
            double width = radius  * Math.cos(rotation);
            double zPos = tileIn.getHeight() + stemHeight + (int)(((shroomHeight)*Math.sin(rotation)));
            flatShapes[i] = new FlatShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), (int)zPos, width, 8);
            rotation += Math.PI/(2.0*(flatShapes.length-1));
        }
        //flatShapes[4] = new FlatShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn),(int)(tileIn.getHeight() + stemHeight), 0, 8);//(int)(flatShapes[3].getZPos() + (scaleIn)), 0, 8);
        shapes[1]=new LayeredSolidShape(convertOffsetXToCoord(offsetXIn), convertOffsetYToCoord(offsetYIn), tileIn.getHeight() + (int)stemHeight, radius*2, radius*2, (int)Math.round(shroomHeight), 8, flatShapes);
        
        tileIn.addAssortedScenery(this);
    }
    

    @Override
    public void draw(Graphics g) 
    {
        /*stemPrism.setCenterCoordX(getCoordX());
        stemPrism.setCenterCoordY(getCoordY());
        petalPrism.setCenterCoordX(getCoordX());
        petalPrism.setCenterCoordY(getCoordY());*/
        
        //if(getBoundTile().getInTransit())
        //{
            shapes[0].setCenterCoordX(getCoordX());
            shapes[1].setCenterCoordX(getCoordX());
            shapes[0].setCenterCoordY(getCoordY());
            shapes[1].setCenterCoordY(getCoordY());

            shapes[0].updateShapePolygons();
            shapes[1].updateShapePolygons();
            LayeredSolidShape s = (LayeredSolidShape)shapes[1];
            s.updateFlatShapes(getCoordX(), getCoordY());
        //}
        
        shapes[1].fillDropShadow(g, getBoundTile().getHeight());
        //shapes[0].updateShapePolygons();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        g.setColor(Color.WHITE);
        shapes[0].fill(g);
        shapes[1].fillDropShadowOntoSolid(g, shapes[0].getVisibleShapeSidePolygons(), (int)((double)stemHeight/2.0));
        g.setColor(Color.RED);
        shapes[1].fill(g);
        
        
    }
}
