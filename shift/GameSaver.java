/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author phusisian
 */
public class GameSaver 
{
    private String jarPath;
    public static File saveFile;
    private static BufferedWriter bufferedWriter;
    

    public GameSaver(boolean newGame)
    {
        try{
            jarPath = GameSaver.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            saveFile = new File(new File(jarPath).getParentFile().getPath() + "/saves");
            System.out.println("SAVE PATH: " + saveFile.getPath());
            System.out.println("saved level was: " + getSavedLevel());
            //System.out.println(saveFile);
        }catch(Exception e)
        {
            System.out.println(e);
        }
        try{
            if(!saveFile.exists())
            {
                saveFile.createNewFile();
            }
            if(newGame)
            {
                saveFile.createNewFile();
            }else{
                UI.level = getSavedLevel();
                LevelLoader ll = new LevelLoader();
                ll.spawnLevel(UI.level);
            }
            bufferedWriter = new BufferedWriter(new FileWriter(saveFile.getAbsoluteFile()));
            //bufferedWriter.close();
        }catch(Exception e)
        {
            System.out.println(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                try{
                    addLevel(UI.level);
                    bufferedWriter.close();
                }catch(Exception e)
                {

                }
            }
        });
        
    }
    
    public int getSavedLevel()
    {
        try{
            InputStream is = new FileInputStream(saveFile);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(is));
            String line;
            if((line = bfr.readLine()) != null)
            {
                return Integer.parseInt(line.substring(line.lastIndexOf(" ")+1));
            }
        }catch(Exception e)
        {
            
        }
        return 1;
    }
    
    public static void addLevel(int level)
    {
        try{
            bufferedWriter.write(" "+Integer.toString(level));
            
        }catch(Exception e)
        {
            
        }
    }
}

