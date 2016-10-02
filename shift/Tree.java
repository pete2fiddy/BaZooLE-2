package shift;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Tree extends Scenery implements Runnable
{
    public static final int baseWidth = WorldPanel.baseUnit/7, baseLength = WorldPanel.baseUnit/7, baseTrunkHeight = 15, baseLeavesHeight = 65, baseLeavesWidth = (int)(baseWidth * 2.1), baseLeavesLength =(int)(baseLength*2.1);
    private double trunkWidth = baseWidth, trunkLength = baseLength, trunkHeight = baseTrunkHeight, leavesHeight = baseLeavesHeight, leavesWidth = baseLeavesWidth, leavesLength = baseLeavesLength;
    private double treeScale;
    private int[][] threadedUpperTrunkPoints, threadedBasePoints;
    int[][][] threadedTrunkPolygons, threadedLeavesPolygons, threadedVisibleTrunkPolygons, threadedVisibleLeavesPolygons;
    private SolidShape[] treeShapes;
    
    public Tree(Tile tileIn, double offsetXIn, double offsetYIn)
    {
        super(tileIn, offsetXIn, offsetYIn);
        tileIn.addTree(this);
        threadedUpperTrunkPoints = upperTrunkPoints();
        threadedBasePoints = basePoints();
        
        threadedTrunkPolygons = getTrunkPolygons();
        threadedLeavesPolygons = getLeavesPolygons();
        threadedVisibleTrunkPolygons = getVisibleTrunkPolygons();
        threadedVisibleLeavesPolygons = getVisibleLeavesPolygons();
        treeScale = 1.0;
        tileIn.addAssortedScenery(this);
        
        initShapes();
    }
    
    public Tree(Tile tileIn, double offsetXIn, double offsetYIn, double scaleIn)
    {
        super(tileIn, offsetXIn, offsetYIn);
        tileIn.addTree(this);
        threadedUpperTrunkPoints = upperTrunkPoints();
        threadedBasePoints = basePoints();
        
        threadedTrunkPolygons = getTrunkPolygons();
        threadedLeavesPolygons = getLeavesPolygons();
        threadedVisibleTrunkPolygons = getVisibleTrunkPolygons();
        threadedVisibleLeavesPolygons = getVisibleLeavesPolygons();
        treeScale = scaleIn;
        trunkWidth *= scaleIn; trunkLength *= scaleIn; trunkHeight *= scaleIn; leavesHeight *= scaleIn; leavesWidth *= scaleIn; leavesLength  *= scaleIn;
        initShapes();
        tileIn.addAssortedScenery(this);
    }
    
    private void initShapes()
    {
        treeShapes = new SolidShape[6];
        RectPrism trunkRect = new RectPrism(getCoordX(), getCoordY(), 0, 1.0/7.0, 1.0/7.0,15, Math.PI/4.0);
        //trunkRect.draw(g);
        double scaleNumber = 1.0;
        treeShapes[0]=trunkRect;
        int numShapes = 1;
        for(int heightCount = 10; heightCount < 50; heightCount+= 10)
        {
            //double topSideRadius = (scaleNumber*(1.0/4.0)) - 0.173;
            //double topShrinkAmount = topSideRadius/(scaleNumber*(1.0/4.0));
            //top side is .175 units smaller than bottom side.
            TruncatedPyramid tp = new TruncatedPyramid(getCoordX(), getCoordY(), heightCount, scaleNumber*(1.0/4.0), 10, 4);
            tp.setTopShape((scaleNumber*(1.0/4.0))-0.1);
            scaleNumber -= 0.15;
            treeShapes[numShapes] = tp;
            numShapes++;
            //tp.draw(g);
        }
        setBoundingBoxDimensions(treeShapes[1].getWidth(), treeShapes[1].getLength());
        treeShapes[5] = new Pyramid(getCoordX(), getCoordY(), 50, 0.25*(1.0/4.0),10, 4);
    }

    public int[][] basePoints()
    {
        int[][] points = new int[2][4];
        for(int i = 0; i < points[0].length; i++)
        {
            points[0][i] = (int)(getX() + ((double)trunkWidth/Math.sqrt(2))*Math.cos(WorldPanel.radSpin + (i*(Math.PI/2.0) + Math.PI/4.0)));
            points[1][i] = (int)(getY() - (WorldPanel.getShrink*(((double)trunkLength/Math.sqrt(2))*Math.sin(WorldPanel.radSpin + (i*(Math.PI/2.0) + Math.PI/4.0)))));
        }
        return points;
    }
    
    public int[][] upperTrunkPoints()
    {
        int[][] points = basePoints();
        for(int i = 0; i < points[0].length; i++)
        {
            points[1][i] -= WorldPanel.distortedHeight(WorldPanel.rotation, (int)trunkHeight);
        }
        return points;
    }
    
    public int[][][] getTrunkPolygons()
    {
        int[][][] trunkPolygons = new int[4][2][4];
        for(int i = 0; i < 4; i++)
        {
            if(i < 3)
            {
                int[] xPoints = {upperTrunkPoints()[0][i], upperTrunkPoints()[0][i+1], basePoints()[0][i+1], basePoints()[0][i]};
                int[] yPoints = {upperTrunkPoints()[1][i], upperTrunkPoints()[1][i+1], basePoints()[1][i+1], basePoints()[1][i]};
                trunkPolygons[i][0] = xPoints;
                trunkPolygons[i][1] = yPoints;
            }else{
                int[] xPoints = {upperTrunkPoints()[0][i], upperTrunkPoints()[0][0], basePoints()[0][0], basePoints()[0][i]};
                int[] yPoints = {upperTrunkPoints()[1][i], upperTrunkPoints()[1][0], basePoints()[1][0], basePoints()[1][i]};
                trunkPolygons[i][0] = xPoints;
                trunkPolygons[i][1] = yPoints;
            }
        }
        return trunkPolygons;
    }
    
    public int[][][] getLeavesPolygons()
    {
        int[][] squarePoints = new int[2][4];
        for(int i = 0; i < squarePoints[0].length; i++)
        {
            squarePoints[0][i] = (int)(getX() + ((double)leavesWidth/Math.sqrt(2))*Math.cos(WorldPanel.radSpin + (i*(Math.PI/2.0) + Math.PI/4.0)));
            squarePoints[1][i] = (int)(getY() - WorldPanel.distortedHeight(WorldPanel.rotation, (int)trunkHeight) - (WorldPanel.getShrink*(((double)leavesLength/Math.sqrt(2))*Math.sin(WorldPanel.radSpin + (i*(Math.PI/2.0) + Math.PI/4.0)))));
        }
        
        int[][][] points = new int[4][2][4];
        int[] topPoint = {(int)getX(), (int)(getY() - WorldPanel.distortedHeight(WorldPanel.rotation, (int)(trunkHeight + leavesHeight)))};
        for(int i = 0; i < 4; i++)
        {
            if(i < 3 )
            {
                int[] xPoints = {squarePoints[0][i], topPoint[0], squarePoints[0][i+1]};
                int[] yPoints = {squarePoints[1][i], topPoint[1], squarePoints[1][i+1]};
                points[i][0]=xPoints;
                points[i][1]=yPoints;
            }else{
                int[] xPoints = {squarePoints[0][i], topPoint[0], squarePoints[0][0]};
                int[] yPoints = {squarePoints[1][i], topPoint[1], squarePoints[1][0]};
                points[i][0]=xPoints;
                points[i][1]=yPoints;
                //System.out.println("hit");
            }
            
        }
        return points;
    }
    
    public int[][][] getVisibleTrunkPolygons()
    {
        int[][][] polyPoints = new int[2][2][4];
        int[][][] trunkPoly = threadedTrunkPolygons;
        if(WorldPanel.spinQuadrant() == 1)//1 and 2
        {
            polyPoints[0][0] = trunkPoly[1][0];//polyX one
            polyPoints[0][1] = trunkPoly[1][1];
            polyPoints[1][0] = trunkPoly[2][0];
            polyPoints[1][1] = trunkPoly[2][1];
        }else if(WorldPanel.spinQuadrant() == 2)//0 and 1
        {
            polyPoints[0][0] = trunkPoly[0][0];//polyX one
            polyPoints[0][1] = trunkPoly[0][1];
            polyPoints[1][0] = trunkPoly[1][0];
            polyPoints[1][1] = trunkPoly[1][1];
        }else if(WorldPanel.spinQuadrant() == 3)//3 and 0
        {
            polyPoints[0][0] = trunkPoly[3][0];//polyX one
            polyPoints[0][1] = trunkPoly[3][1];
            polyPoints[1][0] = trunkPoly[0][0];
            polyPoints[1][1] = trunkPoly[0][1];
        }else{//2 and 3
            polyPoints[0][0] = trunkPoly[2][0];//polyX one
            polyPoints[0][1] = trunkPoly[2][1];
            polyPoints[1][0] = trunkPoly[3][0];
            polyPoints[1][1] = trunkPoly[3][1];
        }
        return polyPoints;
    }
    
    public int[][][] getVisibleLeavesPolygons()
    {
        int[][][] polyPoints = new int[2][2][4];
        int[][][] leavesPoly = threadedLeavesPolygons;
        if(WorldPanel.spinQuadrant() == 1)//1 and 2
        {
            polyPoints[0][0] = leavesPoly[1][0];
            polyPoints[0][1] = leavesPoly[1][1];
            polyPoints[1][0] = leavesPoly[2][0];
            polyPoints[1][1] = leavesPoly[2][1];
        }else if(WorldPanel.spinQuadrant() == 2)//0 and 1
        {
            polyPoints[0][0] = leavesPoly[0][0];
            polyPoints[0][1] = leavesPoly[0][1];
            polyPoints[1][0] = leavesPoly[1][0];
            polyPoints[1][1] = leavesPoly[1][1]; 
        }else if(WorldPanel.spinQuadrant() == 3)//3 and 0
        {
            polyPoints[0][0] = leavesPoly[3][0];
            polyPoints[0][1] = leavesPoly[3][1];
            polyPoints[1][0] = leavesPoly[0][0];
            polyPoints[1][1] = leavesPoly[0][1];
        }else{//2 and 3
            polyPoints[0][0] = leavesPoly[2][0];
            polyPoints[0][1] = leavesPoly[2][1];
            polyPoints[1][0] = leavesPoly[3][0];
            polyPoints[1][1] = leavesPoly[3][1];
        }
        return polyPoints;
    }
    
    @Override
    public void draw(Graphics g)//fix so that polygon arrays ONLY have the polygons that should be drawn for the current quadrant. Also, trying to only draw the "visible" parts of the leaves doesn't work because of its shape -- can result in a side missing.
    {
        
        run();
        Graphics2D g2 = (Graphics2D)g;
        /*
        float dash1[] = {3.0f};
        
        g2.setStroke(new BasicStroke((float)(1), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        
        for(int i = 0; i < 2; i++)//fill and stroke trunk
        {
            g.setColor(new Color(86, 53, 17));
            g.fillPolygon(threadedVisibleTrunkPolygons[i][0],threadedVisibleTrunkPolygons[i][1],4);
            g.setColor(Color.BLACK);
            g.drawPolygon(threadedVisibleTrunkPolygons[i][0],threadedVisibleTrunkPolygons[i][1],4);
        }
        
        //g.setColor(getBoundTile().getColor());
        g2.setPaint(WorldPanel.leavesTexture);
        for(int i = 0; i < 4; i++)//fill leaves
        {
            g.fillPolygon(threadedLeavesPolygons[i][0], threadedLeavesPolygons[i][1],3);
        }
        
        g.setColor(Color.BLACK);
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(int i = 0; i < 2; i++)
        {
            g.drawPolygon(threadedVisibleLeavesPolygons[i][0], threadedVisibleLeavesPolygons[i][1],3);
        }
        
        g2.setStroke(Toolbox.worldStroke);
        */
        
        treeShapes[1].fillDropShadow(g, getBoundTile().getHeight());
        g.setColor(new Color(86, 53, 17));
        treeShapes[0].fill(g);
        RectPrism ss = (RectPrism)treeShapes[0];
        ss.paintShading(g);
        for(int i = 1; i < treeShapes.length; i++)
        {
            g.setColor(Toolbox.grassColor);
            //g.setColor(new Color(251, 251, 251));
            //g2.setPaint(WorldPanel.leavesTexture);
            
            if(i < treeShapes.length -1)
            {
                treeShapes[i].fill(g);//drawExcludingTop(g);
                //treeShapes[i].shadeBoundingBoxSides(g);
                if(i != 1)
                {
                    treeShapes[i].fillDropShadowOntoSolid(g, treeShapes[i-1].getVisibleShapeSidePolygons(), treeShapes[i].getHeight()/4);
                }
                //g.drawString(Double.toString(treeShapes[i].getWidth()), (int)treeShapes[i].convertToPointX(treeShapes[i].getCenterCoordX(), treeShapes[i].getCenterCoordY()), (int)treeShapes[i].convertToPointY(treeShapes[i].getCenterCoordX(), treeShapes[i].getCenterCoordY()));
            }else{
                //treeShapes[i].draw(g);
                treeShapes[i].fill(g);
                treeShapes[i].fillDropShadowOntoSolid(g, treeShapes[i-1].getVisibleShapeSidePolygons(), treeShapes[i].getHeight()/4);
            }
            
        }
        
        //.setColor(Color.WHITE);
        //g.drawString(Double.toString((int)(100.0*getSortDistanceConstant())/100.0), (int)getX(), (int)getY());
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
    public void tick()
    {
        trunkWidth = baseWidth * WorldPanel.scale * treeScale; 
        trunkLength = baseLength * WorldPanel.scale * treeScale;
        trunkHeight = baseTrunkHeight * WorldPanel.scale * treeScale; 
        leavesHeight = baseLeavesHeight * WorldPanel.scale * treeScale; 
        leavesWidth = baseLeavesWidth * WorldPanel.scale * treeScale; 
        leavesLength = baseLeavesLength * WorldPanel.scale * treeScale;
    }
    @Override
    public void run()
    {
        //super.run();
        tick();
        /*threadedUpperTrunkPoints = upperTrunkPoints();
        threadedBasePoints = basePoints();
        threadedTrunkPolygons = getTrunkPolygons().clone();
        threadedLeavesPolygons = getLeavesPolygons().clone();
        threadedVisibleTrunkPolygons = getVisibleTrunkPolygons().clone();
        threadedVisibleLeavesPolygons = getVisibleLeavesPolygons().clone();*/
        int heightCount = 0;
        
        
        for(SolidShape s : treeShapes)
        {
            //System.out.println("called");
            s.setCenterCoordX(getCoordX());
            s.setCenterCoordY(getCoordY());
            s.setZPos(getBoundTile().getHeight() + heightCount);
            s.updateShapePolygons();
            heightCount += 10;
        }
        
        
    }
}
