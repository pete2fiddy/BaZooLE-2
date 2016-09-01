/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Peter
 */


public class PathChain
{
    private ArrayList<Path> chain = new ArrayList<Path>();
    private Path startingPath;
    private int index;
    
    public PathChain(Path startingPathIn, int indexIn, boolean isIsolated)
    {
        
        startingPath = startingPathIn;
        index = indexIn;
        if(isIsolated)
        {
            chain.add(startingPath);
        }else{
            buildChain();
        }
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public Path pathOnPoint(int x, int y)
    {
        for(int i = 0; i < chain.size(); i++)
        {
            if(chain.get(i).pathOnPoint(x, y))
            {
                return chain.get(i);
            }
        }
        return null;
    }
    
    public int chainSize()
    {
        return chain.size();
    }
    
    private void buildChain()
    {
        chain.add(startingPath);//adds the starting path
        if(startingPath.numPathConnections() != 0)
        {
            chain.add(startingPath.getNeighboringPaths().get(0));//adds the path directly after it if there is one to add. Chains list is currently size 2.
            Path currentPath = chain.get(chain.size()-1);//makes the current path the last one in the list.
            try{
                while(currentPath.numPathConnections() > 1)//iterates until it reaches the end.
                {
                    chain.add(currentPath.getNeighboringPathsExcluding(chain.get(chain.size()-2)).get(0));//adds the next path in the chain -- excludes the one already found. Note this will not work for paths that fork out. Can only be one continuous line. 
                    currentPath = chain.get(chain.size()-1);
                }
            }catch(Exception e){}
        }
    }
    
    public ArrayList<Path> getChain()
    {
        return chain;
    }

    public boolean pointOnChain(double x, double y)
    {
        for(int i = 0; i < chain.size(); i++)
        {
            if(chain.get(i).pathOnPoint(x, y))
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean equals(PathChain pc)
    {
        if(index == pc.getIndex())
        {
            return true;
        }
        return false;
    }
    
    public void drawChain(Graphics g)
    {
        for(int i = 0; i < chain.size(); i++)
        {
            g.setColor(Color.RED);
            g.fillOval((int)(chain.get(i).getX() - 5), (int)(chain.get(i).getY() - 5), 10, 10);
        }
    }
    public ArrayList<Point> getDirections(Path start, Path end)
    {
        int startIndex = 0;
        for(int i = 0; i < chain.size(); i++)
        {
            if(chain.get(i).equals(start))
            {
                startIndex = i;
            }
        }
        ArrayList<Point> up = new ArrayList<Point>();
        ArrayList<Point> down = new ArrayList<Point>();
        
        for(int i = startIndex; i < chain.size(); i++)
        {
            
            up.add(new Point((int)chain.get(i).getX(), (int)(chain.get(i).getY()+chain.get(i).getBoundTile().getScaledDistortedHeight())));//+chain.get(i).getBoundTile().getScaledDistortedHeight())));
            
            if(chain.get(i).equals(end))
            {
                return up;
                
            }
            
        }
        
        for(int i = startIndex; i >= 0; i--)
        {
            
            down.add(new Point((int)chain.get(i).getX(), (int)(chain.get(i).getY()+chain.get(i).getBoundTile().getScaledDistortedHeight())));//+chain.get(i).getBoundTile().getScaledDistortedHeight())));
            
            if(chain.get(i).equals(end))
            {
                return down;
            }
            
        }
        return null;
    }
    
    
    
    public ArrayList<Integer> getHeightDirections(Path start, Path end)
    {
        int startIndex = 0;
        for(int i = 0; i < chain.size(); i++)
        {
            if(chain.get(i).equals(start))
            {
                startIndex = i;
            }
        }
        ArrayList<Integer> up = new ArrayList<Integer>();
        ArrayList<Integer> down = new ArrayList<Integer>();
        
        for(int i = startIndex; i < chain.size(); i++)
        {
            up.add(chain.get(i).getBoundTile().getHeight());
            
            if(chain.get(i).equals(end))
            {
                return up;
                //return 
            }
            
        }
        
        for(int i = startIndex; i >= 0; i--)
        {
            
            down.add(chain.get(i).getBoundTile().getHeight());
            
            if(chain.get(i).equals(end))
            {
                return down;
            }
            
        }
        return null;
    }
}