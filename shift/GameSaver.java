package shift;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        }catch(Exception e)
        {
            System.err.println(e);
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
        }catch(Exception e)
        {
            System.err.println(e);
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
            System.err.println(e);
        }
        return 1;
    }
    
    public static void addLevel(int level)
    {
        try{
            bufferedWriter.write(" "+Integer.toString(level));
        }catch(Exception e)
        {
            System.err.println(e);
        }
    }
}