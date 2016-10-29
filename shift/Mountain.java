package shift;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;

public class Mountain 
{
    private static final int maxSortDist = 4;
    private double relX;
    private Point[] mountainPoints;
    private int height;
    private double x, y;
    private double dSpin;
    private double spin = 0;
    private double spinRadius;
    private int sortDistance;
    private int[] xPoints, yPoints;
    private Polygon mountainPolygon;
    private Point topPoint;
    public Mountain(double xIn, double yIn, int mountainType, int sortDistanceIn, double minScale)
    {
        x = xIn;
        relX = (x-WorldPanel.worldX);
        y = yIn;
        dSpin = Math.toRadians(0.75+Math.random());
        fillMountainPoints(mountainType, minScale);
        sortDistance = sortDistanceIn;
        spinRadius = 7 + (8*Math.random());
        setMountainPolygon();
    }
    
    public int getSortDistance()
    {
        return sortDistance;
    }
    
    private void fillMountainPoints(int mountainType, double minScale)
    {
        switch(mountainType)
        {//switched from using points to int x and y vals
            case 1:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,(int)(479.0/minScale));
                mountainPoints[1] = new Point((int)(251.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(513.0/minScale), (int)(479.0/minScale));
                height = (int)(479.0/minScale);
                break;
            case 2:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,(int)(545.0/minScale));
                mountainPoints[1] = new Point((int)(245.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(507.0/minScale),(int)(545/minScale));
                height = (int)(545.0/minScale);
                break;
            case 3:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,(int)(383.0/minScale));
                mountainPoints[1] = new Point((int)(107.0/minScale),(int)(115.0/minScale));
                mountainPoints[2] = new Point((int)(169.0/minScale),(int)(221.0/minScale));
                mountainPoints[3] = new Point((int)(257.0/minScale),0);
                topPoint = mountainPoints[3];
                mountainPoints[4] = new Point((int)(472.0/minScale),(int)(383.0/minScale));
                height = (int)(383.0/minScale);
                break;
            case 4:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,(int)(440.0/minScale));
                mountainPoints[1] = new Point((int)(220.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(364.0/minScale),(int)(285.0/minScale));
                mountainPoints[3] = new Point((int)(401.0/minScale),(int)(220.0/minScale));
                mountainPoints[4] = new Point((int)(532.0/minScale),(int)(440.0/minScale));
                height = (int)(440.0/minScale);
                break;
            case 5:
                mountainPoints = new Point[5];
                mountainPoints[0] = new Point(0,(int)(525.0/minScale));
                mountainPoints[1] = new Point((int)(238.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(403.0/minScale),(int)(349.0/minScale));
                mountainPoints[3] = new Point((int)(473.0/minScale),(int)(251.0/minScale));
                mountainPoints[4] = new Point((int)(594.0/minScale),(int)(525.0/minScale));
                height = (int)(525.0/minScale);
                break;
            case 6:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,(int)(470.0/minScale));
                mountainPoints[1] = new Point((int)(268.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(511.0/minScale),(int)(470.0/minScale));
                height = (int)(470.0/minScale);
                break;
            case 7:
                mountainPoints = new Point[3];
                mountainPoints[0] = new Point(0,(int)(383.0/minScale));
                mountainPoints[1] = new Point((int)(192.0/minScale),0);
                topPoint = mountainPoints[1];
                mountainPoints[2] = new Point((int)(387.0/minScale),(int)(383.0/minScale));
                height = (int)(383.0/minScale);
                break;
        }
    }
    
    private void setMountainPolygon()
    {
        y=WorldPanel.worldY-spinRadius;
        xPoints = new int[mountainPoints.length];
        yPoints = new int[mountainPoints.length];
        int centerX = (int)(mountainPoints[mountainPoints.length-1].getX()/2);
        for(int i = 0; i < mountainPoints.length; i++)
        {
            xPoints[i] = (int)(WorldPanel.worldX + WorldPanel.scale*relX + (mountainPoints[i].getX()*WorldPanel.scale));
            //xPoints[i] = (int)(WorldPanel.worldX + WorldPanel.scale*(x-WorldPanel.worldX) + ((mountainPoints[i].getX()*WorldPanel.scale)));
            //xPoints[i]=(int)(WorldPanel.worldX+WorldPanel.scale*(x-WorldPanel.worldX)+(mountainPoints[i].getX()*WorldPanel.scale));
            yPoints[i]=(int)(WorldPanel.worldY-(height*WorldPanel.scale)+(mountainPoints[i].getY()*WorldPanel.scale) - (WorldPanel.scale*Math.sin(spin)*spinRadius));
        }
        mountainPolygon = new Polygon(xPoints, yPoints, xPoints.length);
        
    }
    
    /*private Polygon resizePolygon(int xPoints[], int yPoints[], double x, double y, double scale)
    {
        //Polygon poly = (Polygon)p.clone();
        int xPoints2[] = new int[xPoints.length];
        int yPoints2[] = new int[yPoints.length];
        
        for(int i = 0; i < xPoints.length; i++)
        {
            xPoints2[i]= (int)(xPoints[i]-(x/WorldPanel.scale));
            yPoints2[i] = (int)(yPoints[i]-(y/WorldPanel.scale));
        }
        int centerX = (int)((double)(xPoints[xPoints.length-1])/2.0);
        int height = (int)(new Polygon(xPoints, yPoints, xPoints.length).getBoundingBox().getHeight()*WorldPanel.scale);
        System.out.println(centerX);
        for(int i = 0; i < xPoints.length; i++)
        {
            int dx = xPoints[i]-centerX;
            
            xPoints2[i] = (int)( centerX + scale*(dx));
            yPoints2[i] = (int)(y-height + scale*yPoints[i]);
        }
        return new Polygon(xPoints2, yPoints2, xPoints2.length);
    }*/
    
    private Polygon getScaledPolygon(double scale)
    {
        //y=WorldPanel.worldY;
        int[] xPoints2 = new int[mountainPoints.length];
        int[] yPoints2 = new int[mountainPoints.length];
        double normalWidth = (topPoint.getX()*WorldPanel.scale);
        int centerX = (int)(mountainPoints[mountainPoints.length-1].getX()/2);
        for(int i = 0; i < mountainPoints.length; i++)
        {
            double amountShift = ((normalWidth*scale)-normalWidth);
            xPoints2[i] = (int)(WorldPanel.worldX + WorldPanel.scale*relX + (mountainPoints[i].getX()*WorldPanel.scale*scale)-amountShift);
            //xPoints[i] = (int)(WorldPanel.worldX + WorldPanel.scale*(x-WorldPanel.worldX) + ((mountainPoints[i].getX()*WorldPanel.scale)));
            //xPoints[i]=(int)(WorldPanel.worldX+WorldPanel.scale*(x-WorldPanel.worldX)+(mountainPoints[i].getX()*WorldPanel.scale));
            yPoints2[i]=(int)(WorldPanel.worldY-(height*WorldPanel.scale*scale)+(mountainPoints[i].getY()*WorldPanel.scale*scale) - (WorldPanel.scale*Math.sin(spin)*spinRadius));
        }
        return new Polygon(xPoints2, yPoints2, xPoints2.length);
        
    }
    
    public double getX()
    {
        return x;
    }
    
    public void draw(Graphics g, Area a, Area drawnArea, Area undrawnArea, int mountainCount, Mountain[] mountains, Area screenArea)
    {
        
        Graphics2D g2 = (Graphics2D)g;
        
        //Composite originalComposite = g2.getComposite();
        
        
        
        
        
        /*int upperAlpha = 120;
        int lowerAlpha = 20;
        int numShades = 10;
        
        
        //Area shaderArea = new Area();
        for(int i = 1; i < numShades+1; i++)
        {
            Area aCopy = (Area)a.clone();
            aCopy.intersect(new Area(getScaledPolygon(1+.1*((double)i/(double)numShades))));
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(((((upperAlpha) - (lowerAlpha)))/numShades)/255.0));
            g2.setComposite(ac);
            
            
            g.setColor(new Color(65, 0, 120));
            g2.fill(aCopy);
        }
        g2.setComposite(originalComposite);*/
        
        Area shadeArea = new Area(getScaledPolygon(1));
        
        Area underMapArea = new Area(new Rectangle(0,(int)WorldPanel.worldY-5,WorldPanel.screenWidth, (int)(20*WorldPanel.scale)));
        double lowerAlpha = 0.07843137254902;
        double upperAlpha = 0.47058823529412;
        int numShades = 10;
        Color shadeColor = new Color(65, 0, 120);
        int grayInc = 5;
        Color backgroundColor = new Color(Color.GRAY.getRed() - grayInc * sortDistance, Color.GRAY.getGreen() - grayInc * sortDistance, Color.GRAY.getBlue() - grayInc * sortDistance);
        /*for(int i = numShades+1; i > 1; i--)
        {
            Polygon resizedPolygon = getScaledPolygon(1+.1*((double)i/(double)numShades));
            Area resizedArea = new Area(resizedPolygon);
            resizedArea.subtract(a);
            g.setColor(Color.BLUE);
            g2.fill(resizedArea);
        }*/
        Color backdropColor;
        if(mountainCount > 0 && mountains[mountainCount-1] != null)
        {
            backdropColor = getMountainBehindColor(mountainCount, mountains);
        }else{
            backdropColor = backgroundColor;
        }
        for(int i = numShades+1; i > 1; i--)
        {
            Area resizedArea = new Area(getScaledPolygon(1+0.1*(double)i/(double)numShades));
            resizedArea.intersect(drawnArea);
            //Area drawnCopy = (Area)drawnArea.clone();
            //resizedArea.intersect(drawnCopy);
            double alphaNum = upperAlpha - i*((upperAlpha-lowerAlpha)/(double)numShades);
            g.setColor(getAlphaColor(alphaNum,shadeColor, backdropColor));
            resizedArea.subtract(underMapArea);
            resizedArea.intersect(screenArea);
            g2.fill(resizedArea);
        }
        
        //int grayInc = 5;
        g.setColor(backgroundColor);
        Area thisMountainArea = new Area(getScaledPolygon(1));
        
        thisMountainArea.subtract(underMapArea);
        thisMountainArea.subtract(undrawnArea);
        thisMountainArea.intersect(screenArea);
        //thisMountainArea.subtract(drawnArea);
        g2.fill(thisMountainArea);
        //g.fillPolygon(getScaledPolygon(1));
        
        g2.setStroke(new BasicStroke(1));
       
        
        //g.setColor(Color.BLUE);
        //g2.fill(aCopy);
        //g2.fill(a);
        /*g.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g.drawPolygon(xPoints, yPoints, xPoints.length);*/
    }
    
    public Color getMountainBehindColor(int mountainCount, Mountain[] mountains)
    {
        Color returnColor = new Color(0,0,0);
        boolean colorFound = false;
        Area thisArea = new Area(getMountainPolygon());
        for(int i = mountainCount; i > 0; i--)
        {
            if(thisArea.contains(mountains[i].getMountainPolygon().getBounds()))
            {
                colorFound = true;
                returnColor = getColor(mountains[i].getSortDistance());
            }
        }
        if(colorFound)
        {
            return returnColor;
        }
        return getColor(sortDistance);
    }
    
    public Color getColor(int sortDistance)
    {
        int grayInc = 5;
        return new Color(Color.GRAY.getRed() - grayInc * sortDistance, Color.GRAY.getGreen() - grayInc * sortDistance, Color.GRAY.getBlue() - grayInc * sortDistance);
    }
    
    private Color getAlphaColor(double alphaNum, Color shadeColor, Color backgroundColor)
    {
        
        //int red = (int)(shadeColor.getRed() + (alphaNum*(backgroundColor.getRed()-shadeColor.getRed())));
        //int green = (int)(shadeColor.getGreen() + (alphaNum*(backgroundColor.getGreen()-shadeColor.getGreen())));
        //int blue = (int)(shadeColor.getBlue() + (alphaNum*(backgroundColor.getBlue()-shadeColor.getBlue())));
        int red = (int)(shadeColor.getRed() + ((1-alphaNum)*(backgroundColor.getRed()-shadeColor.getRed())));
        int green = (int)(shadeColor.getGreen() + ((1-alphaNum)*(backgroundColor.getGreen()-shadeColor.getGreen())));
        int blue = (int)(shadeColor.getBlue() + ((1-alphaNum)*(backgroundColor.getBlue()-shadeColor.getBlue())));
        return new Color(red, green, blue);
    }
    
    public void moveMountain()
    {
        spin += dSpin;
        if(spin > 2*Math.PI)
        {
            spin -= 2*Math.PI;
        }
        setMountainPolygon();
    }
    
    public Polygon getMountainPolygon()
    {
        return mountainPolygon;
    }
}
