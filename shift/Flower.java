package shift;

import java.awt.Color;
import java.awt.Graphics;

public class Flower extends Scenery implements Runnable
{
    private RectPrism stemPrism;
    private RectPrism[] leafPrisms;
    private RectPrism petalPrism;
    private double flowerScale;
    private Color petalColor;
    private double stemSize, leafWidth;
    public static Color[] possibleColors = {new Color(176,23,31), new Color(238, 18, 137), new Color(218, 112, 214), new Color(128, 0, 128)};
    
    public Flower(Tile tileIn, double offsetXIn, double offsetYIn, int stemHeight, double flowerScaleIn) 
    {
        super(tileIn, offsetXIn, offsetYIn);
        stemSize = 0.02*flowerScaleIn;
        stemPrism = new RectPrism(getCoordX(), getCoordY(),getBoundTile().getHeight(), stemSize, stemSize, stemHeight,Math.PI/4.0);
        petalPrism = new RectPrism(getCoordX(), getCoordY(), getBoundTile().getHeight() + stemHeight, 3*stemSize, 3*stemSize, (int)(4*flowerScaleIn),Math.PI/4.0);
        int leafNumber = 0;
        leafWidth = stemSize*2.0;
        flowerScale = flowerScaleIn;
        leafPrisms = new RectPrism[(int)((stemHeight-5)/10) + 1];
        for(int i = 5; i < leafPrisms.length * 10; i+= 10)
        {
            if(leafNumber %2 == 0)
            {
                leafPrisms[leafNumber] = new RectPrism(getCoordX()-(stemSize/2.0)-(leafWidth/2.0), getCoordY(), getBoundTile().getHeight() + i, leafWidth, 0.02*flowerScaleIn, 3);
            }else{
                leafPrisms[leafNumber] = new RectPrism(getCoordX()+(stemSize/2.0)+(leafWidth/2.0), getCoordY(), getBoundTile().getHeight() + i, leafWidth, 0.02*flowerScaleIn, 3);
            }
            leafNumber++;
        }
        setRandomPetalColor();
        setBoundingBoxDimensions(stemPrism.getWidth(), stemPrism.getLength());
        tileIn.addAssortedScenery(this);
    }

    private void setRandomPetalColor()
    {
        int colorNumber = (int)(Math.random()*possibleColors.length);
        petalColor = possibleColors[colorNumber];
    }
    
    @Override
    public void draw(Graphics g) 
    {
        stemPrism.setCenterCoordX(getCoordX());
        stemPrism.setCenterCoordY(getCoordY());
        petalPrism.setCenterCoordX(getCoordX());
        petalPrism.setCenterCoordY(getCoordY());
        stemPrism.updateShapePolygons();
        petalPrism.updateShapePolygons();
        petalPrism.fillDropShadow(g, getBoundTile().getHeight());
        for(RectPrism rp : leafPrisms)
        {
            g.setColor(Toolbox.grassColor);
            if(WorldPanel.radSpin > 0 && WorldPanel.radSpin <= Math.PI)
            {
                if(rp.getCenterCoordX() > getCoordX())
                {
                    rp.updateShapePolygons();
                    rp.setCenterCoordX(getCoordX()+(stemSize/2.0)+(leafWidth/2.0));
                    rp.setCenterCoordY(getCoordY());
                    rp.fill(g);
                }
            }else{
                if(rp.getCenterCoordX() < getCoordX())
                {
                    rp.setCenterCoordX(getCoordX()-(stemSize/2.0)-(leafWidth/2.0));
                    rp.setCenterCoordY(getCoordY());
                    rp.updateShapePolygons();
                    rp.fill(g);
                }
            }
        }
        g.setColor(Toolbox.grassColor);
        stemPrism.fill(g);
        for(RectPrism rp : leafPrisms)//done this way to sort leaves by draw order.
        {
            g.setColor(Toolbox.grassColor);
            if(WorldPanel.radSpin > 0 && WorldPanel.radSpin <= Math.PI)
            {
                if(rp.getCenterCoordX() < getCoordX())
                {
                    rp.setCenterCoordX(getCoordX()-(stemSize/2.0)-(leafWidth/2.0));
                    rp.setCenterCoordY(getCoordY());
                    rp.updateShapePolygons();
                    rp.fill(g);
                }
            }else{
                if(rp.getCenterCoordX() > getCoordX())
                {
                    rp.setCenterCoordX(getCoordX()+(stemSize/2.0)+(leafWidth/2.0));
                    rp.setCenterCoordY(getCoordY());
                    rp.updateShapePolygons();
                    rp.fill(g);
                }
            }
        }
        g.setColor(petalColor);
        petalPrism.fill(g);
    }
    
    @Override public void run()
    {
        try{
            int heightCount = 5;
            int heightAdd = 10;
            stemPrism.setCenterCoordX(getCoordX());
            stemPrism.setCenterCoordY(getCoordY());
            stemPrism.setZPos(getBoundTile().getHeight());
            stemPrism.updateShapePolygons();

            petalPrism.setCenterCoordX(getCoordX());
            petalPrism.setCenterCoordY(getCoordY());
            petalPrism.setZPos(getBoundTile().getHeight() + stemPrism.getHeight());
            petalPrism.updateShapePolygons();
            for(int i = 0; i < leafPrisms.length; i++)
            {
                if(i%2 == 0)
                {
                    leafPrisms[i].setCenterCoordX(getCoordX()-(stemSize/2.0));
                    leafPrisms[i].setCenterCoordY(getCoordY());
                }else{
                    leafPrisms[i].setCenterCoordX(getCoordX()+(stemSize/2.0));
                    leafPrisms[i].setCenterCoordY(getCoordY());
                }
                leafPrisms[i].setZPos(getBoundTile().getHeight() + heightCount);
                leafPrisms[i].updateShapePolygons();
                heightCount += heightAdd;
            }
        }catch(Exception e)
        {
            
        }
    }
}
