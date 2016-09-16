/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author phusisian
 */
public class LevelLoader 
{
    public static boolean sortTiles = false;
    public static final double straightUnitConstant = WorldPanel.baseStraightUnit;
    public static final int unitConstant = WorldPanel.baseUnit;
    public static Path playerStartPath;
    public static boolean movePlayerToStart = false;
    private Player player;
    public static boolean isLoading = false;
    //private GameSaver gameSaver = new GameSaver(false);
    WorldPanel worldPanel;
    public LevelLoader(Player p, WorldPanel wp)
    {
        player = p;
        worldPanel = wp;
    }
    
    public LevelLoader()
    {
        
    }
    
    private static void clearBoard()
    {
        
        MergedBlockTiles.blockTiles.clear();
        MergedPaths.pathList.clear();
        MergedPaths.pathLinks.clear();
        
        TileDrawer.tileList.clear();
        TileDrawer.waterDroplets.clear();
        TileDrawer2.tileList.clear();
        try{
            TileSorter.holdList.clear();
            TileSorter.tileList.clear();
        }catch(Exception e)
        {
            
        }
    }
    
    public void spawnLevel(int level)
    {
        isLoading = true;
        GameSaver.addLevel(level);
        clearBoard();
        BufferedReader lvlIn;
        Tile startTile = null;
        playerStartPath = null;
        try
        {
            //lvlIn = new BufferedReader(new FileReader("/Users/phusisian/Dropbox/Shift/src/Levels/lvl"+Integer.toString(level)+".lvl"));
            String path = new File("src/Levels/lvl"+Integer.toString(level)+".lvl").getAbsolutePath();
            //System.out.println(path);
            //URL url = getClass().getResource("lvl"+Integer.toString(level)+".lvl");
            
            //InputStream in = LevelLoader.class.getResourceAsStream("../Levels/lvl"+Integer.toString(level)+".lvl");
            //lvlIn = new BufferedReader(new InputStreamReader(in));
            
            File tryFile = new File(LevelLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            InputStream tryStream = getClass().getResourceAsStream("/Levels/lvl"+Integer.toString(level)+".lvl");
            //newPath += "/src/Levels/lvl"+Integer.toString(level)+".lvl";
            lvlIn = new BufferedReader(new InputStreamReader(tryStream));
            if(lvlIn != null)
            {
                int currentLine = 0;
                String line;
                
                while((line = lvlIn.readLine()) != null)
                {
                    if(currentLine == 0)
                    {
                        int worldWidth = Integer.parseInt(line.substring(line.indexOf(": ")+2, line.indexOf(","))) + 2;
                        int worldHeight = Integer.parseInt( line.substring(line.indexOf(",") + 1))+ 2;
                        
                        //int worldWidth = Integer.parseInt(line.substring(line.indexOf(": ") + 2, line.indexOf(":")+3))+2;
                        //int worldHeight = Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(",")+2))+2;
                        WorldPanel.baseMapWidth = unitConstant*worldWidth; //(Integer.parseInt(line.substring(line.indexOf(": ") + 2, line.indexOf(": ")+3))+2);
                        WorldPanel.baseMapHeight = unitConstant*worldHeight; //(Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(",")+2))+2);
                        WorldPanel.baseStraightUnit = (double)unitConstant/Math.sqrt(2);
                        WorldPanel.baseMapRadius = WorldPanel.baseMapWidth/2;
                        WorldPanel.squareWidth = (double)WorldPanel.mapWidth/Math.sqrt(2);
                        WorldPanel.squareRadius = (double)WorldPanel.baseMapRadius/Math.sqrt(2);
                        WorldPanel.worldTilesWidth = worldWidth;
                        WorldPanel.worldTilesHeight = worldHeight;
                        
                        BlockTile bt1 = new BlockTile(-worldWidth/2, -worldHeight/2, 1, worldHeight, 5, true);
                        BlockTile bt2 = new BlockTile((worldWidth/2) - 1, -worldHeight/2, 1, worldHeight, 5, true);
                        BlockTile bt3 = new BlockTile(-worldWidth/2, (worldHeight/2)-1, worldWidth, 1, 5, true);
                        BlockTile bt4 = new BlockTile(-worldWidth/2, (-worldHeight/2), worldWidth, 1, 5, true);
                    }else{
                        if(line.contains("Shift:"))
                        {   
                            ShiftTile st = spawnShiftTile(line);
                            addSceneryToTile(st, line);
                            if(line.contains("Start"))
                            {
                                //System.out.println("Player start tile set to shift!");
                                startTile = st;
                            }
                        }else if(line.contains("Spin:"))
                        {
                            SpinTile st = spawnSpinTile(line);
                            if(line.contains("Start"))
                            {
                                //System.out.println("Player start tile set to spin!");
                                startTile = st;
                            }
                        }
                        else if(line.contains("ShiftBreak:"))
                        {
                            BreakShiftTile bst = spawnBreakShiftTile(line);
                            addSceneryToTile(bst, line);
                            if(line.contains("Start"))
                            {
                                startTile = bst;
                            }
                        }
                        else if(line.contains("ShiftElevator:"))
                        {
                            ElevatorShiftTile est = spawnElevatorShiftTile(line);
                            addSceneryToTile(est, line);
                            if(line.contains("Start"))
                            {
                                startTile = est;
                            }
                        }
                        else if(line.contains("Stuck:"))
                        {
                            StuckTile st = spawnStuckTile(line);
                            addSceneryToTile(st, line);
                            //spawnStuckTile(line);
                        }
                        else if(line.contains("End:"))
                        {
                            LevelEndTile let = spawnLevelEndTile(line);
                        }
                    }
                    
                    if(startTile != null)
                    {
                        //player.setFreezePlayer(false);
                        playerStartPath = startTile.getPathList().get(0);
                        //player.setX(playerStartPath.getCoordX());
                        //player.setY(playerStartPath.getCoordY());
                        movePlayerToStart = true;
                        
                    }
                    currentLine++;
                    //System.out.println(line);
                }
                
            }
            sortTiles = true;
            player.setFreezePlayer(false);
            
        }catch(Exception e)
        {
            System.out.println(e);
        }
        isLoading = false;
        //setTileInfo();
        
    }
    
    private static LevelEndTile spawnLevelEndTile(String line)
    {
        int[] values = getLevelEndTileValues(line.substring(line.indexOf("End: ") + "End: ".length(), line.indexOf(" |")));
        LevelEndTile let = new LevelEndTile(values[0], values[1], values[2], values[3], values[4]);
        return let;
    }
    
    private static ElevatorShiftTile spawnElevatorShiftTile(String line)
    {
        int[] values = getElevatorShiftTileValues(line.substring(line.indexOf("ShiftElevator: ") + "ShiftElevator: ".length(), line.indexOf(" |")));
        ElevatorShiftTile est = new ElevatorShiftTile(values[0], values[1], values[2], values[3], values[4]);
        return est;
    }
    
    private static int[] getLevelEndTileValues(String s)
    {
        int[] values = new int[5];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static ShiftTile spawnShiftTile(String line)
    {
        String tileLine = line.substring(line.indexOf("Shift: ") + "Shift: ".length(), line.indexOf(" | ", line.indexOf("Shift: ") + "Shift: ".length()));
        int[] values = new int[5];
        int lastCommaIndex = 0;
        ShiftTile thisTile;
        for(int i = 0; i < values.length; i++)
        {
            if(i == 0)
            {
                values[i] = Integer.parseInt(tileLine.substring(0, tileLine.indexOf(",")));
                lastCommaIndex = tileLine.indexOf(",");
            }else if (i > 0 && i < values.length - 1){
                values[i] = Integer.parseInt( tileLine.substring(lastCommaIndex + 1, tileLine.indexOf ( "," , lastCommaIndex + 1 ) ) );
                lastCommaIndex=tileLine.indexOf ( "," , lastCommaIndex + 1 );
            }else{
                values[i] = Integer.parseInt(tileLine.substring(lastCommaIndex + 1));
            }
        }
        
        thisTile = new ShiftTile(values[0], values[1], values[2], values[3], values[4]);//new ShiftTile(Integer.parseInt(tileLine.substring(0, 1)), Integer.parseInt(tileLine.substring(2, 3)), Integer.parseInt(tileLine.substring(4, 5)), Integer.parseInt(tileLine.substring(6, 7)), Integer.parseInt(tileLine.substring(8, 9)));

        
        int lastPathIndex = line.indexOf(" |")+1;//line.indexOf("Path: ") + "Path: ".length();
        //System.out.println(lastPathIndex);
        for(int i = 0; i < line.length() - line.replace("Path: ", "     ").length(); i++)
        {
            //String tileLine = line.substring(line.indexOf("Shift: ") + "Shift: ".length(), line.indexOf(" | ", line.indexOf("Shift: ") + "Shift: ".length()));
            
            double[] pathValues = getPathValues(line.substring(line.indexOf("Path: ", lastPathIndex+1) + "Path: ".length(), line.indexOf(" |", lastPathIndex+1)));
            String curLine = line.substring(lastPathIndex, line.indexOf(" |", lastPathIndex + 1));
            //System.out.println(curLine);
            lastPathIndex = line.indexOf(" |", lastPathIndex)+1;//line.indexOf(" |", lastPathIndex);//line.indexOf(" |", line.indexOf("Path: ", lastPathIndex));//line.indexOf("Path: ", lastPathIndex)+ "Path: ".length();
            //System.out.println(lastPathIndex);
            if(!curLine.contains("Straight"))
            {
                double vertex[] = {pathValues[0], pathValues[1]};
                DirtPath dp = new DirtPath(thisTile, vertex, (int)pathValues[2], (int)pathValues[3]);
            }else{
                DirtPath dp = new DirtPath(thisTile, pathValues[0], pathValues[1], pathValues[2], pathValues[3]);
            }
            
            //here
        }
        return thisTile;
    }
    
    private void setTileInfo()
    {
        for(int i = 0; i < TileDrawer2.tileList.size(); i++)
        {
            TileDrawer2.tileList.get(i).setPlayer(player);
        }
    }
    
    private static SpinTile spawnSpinTile(String line)
    {
        SpinTile thisTile;
        int[] spinValues = getSpinTileValues(line.substring(line.indexOf("Spin: ") + "Spin: ".length(), line.indexOf(" |")));
        thisTile = new SpinTile(spinValues[0], spinValues[1], spinValues[2], spinValues[3], spinValues[4]);
        //thisTile.setSpin(Double.parseDouble(line.substring(line.indexOf("Angle: ") + "Angle: ".length())));
        return thisTile;
    }
    
    private static BreakShiftTile spawnBreakShiftTile(String line)
    {
        BreakShiftTile thisTile;
        int[] values = getSpinTileValues(line.substring(line.indexOf("ShiftBreak: ") + "ShiftBreak: ".length(), line.indexOf(" |")));
        thisTile = new BreakShiftTile(values[0],values[1], values[2], values[3], values[4]);
        //thisTile.setSpin(Double.parseDouble(line.substring(line.indexOf("Angle: ") + "Angle: ".length())));
        return thisTile;
    }
    
    
    private static StuckTile spawnStuckTile(String line)
    {
        StuckTile thisTile;
        int[] stuckValues = getStuckTileValues(line.substring(line.indexOf("Stuck: ") + "Stuck: ".length(), line.indexOf(" |")));
        thisTile = new StuckTile(stuckValues[0],stuckValues[1],stuckValues[2],stuckValues[3],stuckValues[4]);
        return thisTile;
    }
    
    
    
    private static double[] getPathValues(String s)
    {
        double[] values = new double[4];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i == 0)
            {
                values[i] = Double.parseDouble(s.substring(0, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if (i > 0 && i < values.length - 1){
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1, s.indexOf ( "," , lastCommaIndex + 1 ) ) );
                lastCommaIndex=s.indexOf ( "," , lastCommaIndex + 1 );
            }else{
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    
    private static int[] getSpinTileValues(String s)
    {
        int[] values = new int[5];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static int[] getBreakShiftTileValues(String s)
    {
        int[] values = new int[5];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static int[] getElevatorShiftTileValues(String s)
    {
        int[] values = new int[5];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static int[] getStuckTileValues(String s)
    {
        int[] values = new int[5];
        int lastCommaIndex = 0;
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Integer.parseInt(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static void addSceneryToTile(Tile tile, String line)
    {
        int lastBreakIndex = 0;
        String currentPart = "";
        try{
            line = line.substring(line.indexOf("Scenery //") + "Scenery //".length());
            //System.out.println("Line is:" + line);
            for(int i = 0; i < line.length() - line.replace(" |", " ").length(); i++)
            {
                currentPart = line.substring(lastBreakIndex, line.indexOf(" |", lastBreakIndex+1));
                //System.out.println("CurrentPart is:" + currentPart);
                if(currentPart.contains("Tree: "))
                {
                    tile.addTree(new Tree(tile, getTreeValues(currentPart)[0], getTreeValues(currentPart)[1], getTreeValues(currentPart)[2]));
                }else if(currentPart.contains("Lake: "))
                {
                    tile.addLake(new Lake(tile, getLakeValues(currentPart)[0], getLakeValues(currentPart)[1], getLakeValues(currentPart)[2], getLakeValues(currentPart)[3]));
                }else if(currentPart.contains("Waterfall: "))
                {
                    tile.addWaterfall(new Waterfall(tile, getWaterfallValues(currentPart)[0], getWaterfallValues(currentPart)[1], getWaterfallValues(currentPart)[2]));
                }
                
                lastBreakIndex = line.indexOf(" |", lastBreakIndex+1);
            }
        }catch(Exception e)
        {
            //System.out.println("Scenery // not found");
        }
        
    }
    
    private static double[] getLakeValues(String s)
    {
        double[] values = new double[4];
        int lastCommaIndex = s.indexOf("Lake: ") + "Lake: ".length();
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static double[] getWaterfallValues(String s)
    {
        double[] values = new double[3];
        int lastCommaIndex = s.indexOf("Waterfall: ") + "Waterfall: ".length();
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
    private static double[] getTreeValues(String s)
    {
        double[] values = new double[3];
        int lastCommaIndex = s.indexOf("Tree: ") + "Tree: ".length();
        for(int i = 0; i < values.length; i++)
        {
            if(i==0)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex, s.indexOf(",")));
                lastCommaIndex = s.indexOf(",");
            }else if(i > 0 && i < values.length - 1)
            {
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1, s.indexOf(",", lastCommaIndex + 1)));
                lastCommaIndex = s.indexOf(",", lastCommaIndex + 1);
            }else{
                values[i] = Double.parseDouble(s.substring(lastCommaIndex + 1));
            }
        }
        return values;
    }
    
}
