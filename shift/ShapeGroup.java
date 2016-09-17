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
public class ShapeGroup 
{
    private SolidShape[] shapeList;
    public ShapeGroup(SolidShape[] shapesIn)
    {
        shapeList = shapesIn;
    }
    
    public void rotateGroup(double dSpin)
    {
        
    }
    
    public void setGroupRotation(double newSpin)
    {
        for(SolidShape ss : shapeList)
        {
            //ss.setSpin(newSpin);
        }
    }
}
