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
    private double x;
    private double dSpin;
    private double spin = 0;
    private double spinRadius;
    private int sortDistance;
    private int[] xPoints, yPoints;
    private Polygon mountainPolygon;
    private Point topPoint;
    /*
    Initialization:
    */
    public Mountain(double xIn, double yIn, int mountainType, int sortDistanceIn, double minScale)
    {
        x = xIn;
        relX = (x-WorldPanel.worldX);
        dSpin = Math.toRadians(0.75+Math.random());
        fillMountainPoints(mountainType, minScale);
        sortDistance = sortDistanceIn;
        spinRadius = 7 + (8*Math.random());
        setMountainPolygon();
    }
    
    
    
    /*
    Populates the list of mountain points (baseMountainPoints, they are scaled and moved around based on position and scale of the world)
    MountainType is the type of mountain it is, of which there are seven. Each mountain type has it's own mountain polygon.
    */
    private void fillMountainPoints(int mountainType, double minScale)
    {
        switch(mountainType)
        {
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
    
    /*
    ----------------------------------------------------------------------------
    Painting:
    */
    
    
    
    
    
    
    public void draw(Graphics g, Area a, Area drawnArea, Area undrawnArea, int mountainCount, Mountain[] mountains, Area screenArea)
    {
        
        Graphics2D g2 = (Graphics2D)g;
        
        Area underMapArea = new Area(new Rectangle(0,(int)WorldPanel.worldY-5,WorldPanel.screenWidth, (int)(20*WorldPanel.scale)));
        double lowerAlpha = 0.07843137254902;
        double upperAlpha = 0.47058823529412;
        int numShades = 10;
        Color shadeColor = new Color(65, 0, 120);
        int grayInc = 5;
        Color backgroundColor = new Color(Color.GRAY.getRed() - grayInc * sortDistance, Color.GRAY.getGreen() - grayInc * sortDistance, Color.GRAY.getBlue() - grayInc * sortDistance);
        Color backdropColor;
        
        /*if(mountainCount > 0 && mountains[mountainCount-1] != null)
        {
            backdropColor = getMountainBehindColor(mountainCount, mountains);
        }else{
            backdropColor = backgroundColor;
        }*/
        Mountain[] lowerMountains = getLowerMountains();
        g.setColor(backgroundColor);
        Area thisMountainArea = new Area(getScaledMountainPolygon(1));
        for(Mountain m : lowerMountains)
        {
            backdropColor = getColorAtSortDistance(m.getSortDistance());
            for(int i = numShades+1; i > 1; i--)
            {
                Area resizedArea = new Area(getScaledMountainPolygon(1+0.1*(double)i/(double)numShades));
                resizedArea.intersect(drawnArea);
                double alphaNum = upperAlpha - i*((upperAlpha-lowerAlpha)/(double)numShades);
                g.setColor(ColorPalette.getLerpColor(shadeColor, backdropColor, alphaNum));//getAlphaColor(alphaNum,shadeColor, backdropColor));
                resizedArea.subtract(underMapArea);
                resizedArea.intersect(screenArea);
                resizedArea.intersect(new Area(m.getScaledMountainPolygon(1)));
                resizedArea.subtract(thisMountainArea);
                g2.fill(resizedArea);
            }
        }
        //int grayInc = 5;
        g.setColor(backgroundColor);
        //Area thisMountainArea = new Area(getScaledMountainPolygon(1));
        
        thisMountainArea.subtract(underMapArea);
        thisMountainArea.subtract(undrawnArea);
        thisMountainArea.intersect(screenArea);
        //thisMountainArea.subtract(drawnArea);
        g2.fill(thisMountainArea);
        //g.fillPolygon(getScaledPolygon(1));
        
        g2.setStroke(new BasicStroke(1));
    }
    
    /*
    ----------------------------------------------------------------------------
    Getters:
    */
    
    private Mountain[] getLowerMountains()
    {
        Mountain[] mountains = new Mountain[0];
        for(Mountain m : Mountains.mountainList)
        {
            if(m.getSortDistance() > sortDistance)
            {
                mountains = addMountain(mountains, m);
            }
        }
        System.out.println(mountains.length);
        return mountains;
    }
    
    private Mountain[] addMountain(Mountain[] mountains, Mountain mountain)
    {
        Mountain[] mountainReturn = new Mountain[mountains.length+1];
        for(int i = 0; i < mountains.length; i++)
        {
            mountainReturn[i] = mountains[i];
        }
        mountainReturn[mountains.length] = mountain;
        return mountainReturn;
    }
    
    private Polygon getScaledMountainPolygon(double scale)
    {
        int[] xPoints2 = new int[mountainPoints.length];
        int[] yPoints2 = new int[mountainPoints.length];
        double normalWidth = (topPoint.getX()*WorldPanel.scale);
        for(int i = 0; i < mountainPoints.length; i++)
        {
            double amountShift = ((normalWidth*scale)-normalWidth);
            xPoints2[i] = (int)(WorldPanel.worldX + WorldPanel.scale*relX + (mountainPoints[i].getX()*WorldPanel.scale*scale)-amountShift);
            yPoints2[i]=(int)(WorldPanel.worldY-(height*WorldPanel.scale*scale)+(mountainPoints[i].getY()*WorldPanel.scale*scale) - (WorldPanel.scale*Math.sin(spin)*spinRadius));
        }
        return new Polygon(xPoints2, yPoints2, xPoints2.length);
    }
    
    public int getSortDistance()
    {
        return sortDistance;
    }
    
    public Polygon getMountainPolygon()
    {
        return mountainPolygon;
    }
    
    public Color getColorAtSortDistance(int sortDistance)
    {
        int grayInc = 5;
        return new Color(Color.GRAY.getRed() - grayInc * sortDistance, Color.GRAY.getGreen() - grayInc * sortDistance, Color.GRAY.getBlue() - grayInc * sortDistance);
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
                returnColor = getColorAtSortDistance(mountains[i].getSortDistance());
            }
        }
        if(colorFound)
        {
            return returnColor;
        }
        return getColorAtSortDistance(sortDistance);
    }
    
    public double getX()
    {
        return x;
    }
    
    /*
    ----------------------------------------------------------------------------
    Setters:
    */
    
    private void setMountainPolygon()
    {
        xPoints = new int[mountainPoints.length];
        yPoints = new int[mountainPoints.length];
        for(int i = 0; i < mountainPoints.length; i++)
        {
            xPoints[i] = (int)(WorldPanel.worldX + WorldPanel.scale*relX + (mountainPoints[i].getX()*WorldPanel.scale));
            yPoints[i]=(int)(WorldPanel.worldY-(height*WorldPanel.scale)+(mountainPoints[i].getY()*WorldPanel.scale) - (WorldPanel.scale*Math.sin(spin)*spinRadius));
        }
        mountainPolygon = new Polygon(xPoints, yPoints, xPoints.length);
        
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
    
    /*DELETABLE*/
    
    /*private Color getAlphaColor(double alphaNum, Color shadeColor, Color backgroundColor)
    {
        int red = (int)(shadeColor.getRed() + ((1-alphaNum)*(backgroundColor.getRed()-shadeColor.getRed())));
        int green = (int)(shadeColor.getGreen() + ((1-alphaNum)*(backgroundColor.getGreen()-shadeColor.getGreen())));
        int blue = (int)(shadeColor.getBlue() + ((1-alphaNum)*(backgroundColor.getBlue()-shadeColor.getBlue())));
        return new Color(red, green, blue);
    }*/
}
