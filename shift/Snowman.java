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
public class Snowman extends Scenery
{

    private RectPrism[] shapes;
    public Snowman(Tile tileIn, double offsetXIn, double offsetYIn) {
        super(tileIn, offsetXIn, offsetYIn);
        shapes = new RectPrism[7];
        double rotation = (int)(Math.random()*4)*(Math.PI/2.0);
        double squareDim = 0.2;
        int heightAdd = 0;
        for(int i = 0; i < 3; i++)
        {
            shapes[i] = new RectPrism(getCoordX(), getCoordY(), getBoundTile().getHeight()+heightAdd, squareDim, squareDim, (int)(WorldPanel.straightUnit*squareDim));
            shapes[i].setColor(Color.WHITE);
            double armLength = 0.10;
            double armWidth = 0.02;
            heightAdd += (WorldPanel.straightUnit*squareDim);
            if(i==1)
            {
                if(rotation == 0 || rotation == Math.PI)
                {
                    shapes[5] = new RectPrism(getCoordX(),
                                                getCoordY()+(squareDim/2.0)+(armLength/2.0),
                                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit/3.0)),
                                                armWidth,
                                                armLength,
                                                1);
                    //shapes[5].rotateAroundCoord(getCoordX(), getCoordY(), rotation);
                    shapes[5].setColor(new Color(86, 53, 17));
                    shapes[5].setOffsetX(0);
                    shapes[5].setOffsetY((squareDim/2.0)+(armLength/2.0));

                    shapes[6] = new RectPrism(getCoordX(),
                                                getCoordY()-(1.25*squareDim)+(armLength/2.0),
                                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit/3.0)),
                                                armWidth,
                                                armLength,
                                                1);
                    shapes[6].setColor(new Color(86, 53, 17));
                    shapes[6].setOffsetX(0);
                    shapes[6].setOffsetY(-1.25*squareDim + (armLength/2.0));
                    //shapes[6].rotateAroundCoord(getCoordX(), getCoordY(), rotation);
                }else{
                    shapes[5] = new RectPrism(getCoordX()+(squareDim/2.0)+(armLength/2.0),
                                                getCoordY(),
                                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit/3.0)),
                                                armLength,
                                                armWidth,
                                                1);
                    shapes[5].setColor(new Color(86, 53, 17));
                    shapes[5].setOffsetX((squareDim/2.0)+(armLength/2.0));
                    shapes[5].setOffsetY(0);
                    //shapes[5].rotateAroundCoord(getCoordX(), getCoordY(), rotation);

                    shapes[6] = new RectPrism(getCoordX()-(1.25*squareDim)+(armLength/2.0),
                                                getCoordY(),
                                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit/3.0)),
                                                armLength,
                                                armWidth,
                                                1);
                    shapes[6].setColor(new Color(86, 53, 17));
                    shapes[6].setOffsetX(-1.25*squareDim + (armLength/2.0));
                    shapes[6].setOffsetY(0);
                    //shapes[6].rotateAroundCoord(getCoordX(), getCoordY(), rotation);
                }
            }
            
            squareDim -= 0.05;
            double eyeballDim = 0.02;
            
            int eyeballHeight =(int)(eyeballDim*WorldPanel.straightUnit);
            
            
            
            if(i == 2)
            {
                shapes[3] = new RectPrism(getCoordX()+((squareDim)+(eyeballDim/2.0)),
                                getCoordY()+(squareDim/2.0),
                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit)-((double)eyeballHeight/2.0)/2.0),
                                eyeballDim, 
                                eyeballDim,
                                eyeballHeight);
                shapes[3].setOffsetX(((squareDim)+(eyeballDim/2.0)));
                shapes[3].setOffsetY((squareDim/2.0));
                shapes[3].rotateAroundCoord(getCoordX(), getCoordY(), rotation);
                shapes[3].setColor(Color.BLACK);
                
                
                shapes[4] = new RectPrism(getCoordX()+((squareDim)+(eyeballDim/2.0)),
                                getCoordY()-(squareDim/2.0),
                                (int)(getBoundTile().getHeight()+heightAdd-(squareDim*WorldPanel.straightUnit)-((double)eyeballHeight/2.0)/2.0),
                                eyeballDim, 
                                eyeballDim,
                                eyeballHeight);
                shapes[4].setOffsetX(((squareDim)+(eyeballDim/2.0)));
                shapes[4].setOffsetY(-(squareDim/2.0));
                shapes[4].rotateAroundCoord(getCoordX(), getCoordY(), rotation);
                shapes[4].setColor(Color.BLACK);
                
            }
        }
        tileIn.addAssortedScenery(this);
    }
    
    public void sortShapes()
    {
        
        
        for(int i = 0; i < shapes.length-1; i++)
        {
            int index = i;
            for(int j = i+1; j < shapes.length; j++)
            {
                if(shapes[j].getMiddleSortDistanceConstant() > shapes[index].getMiddleSortDistanceConstant())
                {
                    index = j;
                }else if(shapes[j].getCenterCoordX() == shapes[index].getCenterCoordX() && shapes[j].getCenterCoordY() == shapes[index].getCenterCoordY() && shapes[j].getZPos() < shapes[i].getZPos())
                {
                    index = j;
                }
            }
            RectPrism temp = shapes[i];
            shapes[i] = shapes[index];
            shapes[index] = temp;
        }
        
        //System.out.println();
    }
    

    public void updateShapes()
    {
        for(RectPrism r : shapes)
        {
            r.setCenterCoordX(getCoordX() + r.getOffsetX());
            r.setCenterCoordY(getCoordY() + r.getOffsetY());
        }
    }
    
    @Override
    public void draw(Graphics g) 
    {
        if(isVisible(g))
        {
            updateShapes();
            sortShapes();
            for(RectPrism r : shapes)
            {
                r.updateShapePolygons();
                g.setColor(r.getColor());
                r.fill(g);
                r.paintShading(g);
            }
            /*g.setColor(Color.GRAY);
            shapes[3].fill(g);
            shapes[3].paintShading(g);
            //shapes[3].rotateAroundCoord(getCoordX(), getCoordY(), Math.PI/32.0);
            g.setColor(Color.GRAY);
            shapes[4].fill(g);
            shapes[4].paintShading(g);
            //shapes[4].rotateAroundCoord(getCoordX(), getCoordY(), Math.PI/32.0);
            g.setColor(Color.BLACK);
            shapes[5].fill(g);
            shapes[5].paintShading(g);
            g.setColor(Color.BLACK);
            shapes[6].fill(g);
            shapes[6].paintShading(g);*/
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
