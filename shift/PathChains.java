package shift;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PathChains implements Runnable
{
    private ArrayList<PathChain> chains = new ArrayList<PathChain>();
    private ArrayList<Path> paths = MergedPaths.pathList;
    private Thread thread;
    
    
    public PathChains()
    {
        rebuildChains();
        thread = new Thread(this);
        thread.start();
    }
    
    public String toString()
    {
        String giveReturn = "";
        for(int i = 0; i < chains.size(); i++)
        {
            giveReturn += Integer.toString(chains.get(i).chainSize()) + "\n";
        }
        giveReturn += "\n";
        return giveReturn;
    }
    
    public ArrayList<PathChain> getChains(){return chains;}
    
    public void rebuildChains()
    {
        try{
            chains.clear();
            int chainsMade = 0;
            for(int i = 0; i < paths.size(); i++)
            {

                if(paths.get(i).numPathConnections() == 0)
                {
                    chains.add(new PathChain(paths.get(i), chainsMade, true));
                    chainsMade ++;
                }else if(paths.get(i).numPathConnections() >= 1)
                {
                    boolean dupeFound = false;
                    for(PathChain pc : chains)
                    {
                        if(pc.getChain().get(pc.chainSize()-1)==paths.get(i))
                        {
                            dupeFound = true;
                        }
                    }
                    if(!dupeFound)
                    {
                        chains.add(new PathChain(paths.get(i), chainsMade, false));
                        chainsMade ++;
                    }
                }
            }
        }catch(Exception e){}
    }
    
    private int getNumPathsInChains()
    {
        int count = 0;
        for(int i = 0; i < chains.size(); i++)
        {
            count += chains.get(i).getChain().size();
        }
        return count;
    }
    
    public PathChain chainOnPoint(double x, double y)
    {
        try{
        for(int i = 0; i < chains.size(); i++)
        {
            if(chains.get(i).pointOnChain(x, y))
            {
                return chains.get(i);
            }
        }
        }catch(Exception e)
        {
            
        }
        return null;
    }
    
    
    @Override
    public void run()
    {
        while(true)
        {
            if(!Player.inTransit)
            {
                rebuildChains();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {

            }
        }
    }
}



