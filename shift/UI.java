/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author phusisian
 */
public class UI
{
    private Menu menu;
    private boolean drawMenu = true;
    public static int level;
    public static boolean drawLevelEndAnimation = false;
    private Font mediumFont;
    private WorldPanel worldPanel;
    private boolean drawInstructions = false;
    
    private GameSaver gameSaver;// = new GameSaver(false);
    //private LevelLoader levelLoader = new LevelLoader();
    
    public UI(int levelIn, WorldPanel wp)
    {
        level = levelIn;
        worldPanel = wp;
        menu = new Menu(wp, this);
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        mediumFont = new Font("Futura", Font.PLAIN, 24);
    }
    
    public UI(WorldPanel wp)
    {
        level = 1;
        worldPanel = wp;
        menu = new Menu(wp, this);
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext", "true");
        mediumFont = new Font("Futura", Font.PLAIN, 36);
    }
    
    public static void addLevel()
    {
        level++;
        
    }
    
    
    
    public boolean getDrawMenu()
    {
        return drawMenu;
    }
    
    public void setDrawMenu(boolean b)
    {
        drawMenu = b;
    }
    
    public GameSaver getGameSaver()
    {
        return gameSaver;
    }
    
    public void setGameSaver(GameSaver gs)
    {
        gameSaver = gs;
    }
    
    public void setDrawInstructions(boolean b)
    {
        drawInstructions = b;
    }
    
    public void draw(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Futura", Font.PLAIN, 16));
         g.drawString("Volume", 25, 50);
        g.setFont(mediumFont);
       
        g.drawString("Level: " + Integer.toString(level), WorldPanel.screenWidth - 200, 50);
        if(drawMenu && !drawInstructions)
        {
            menu.draw(g);
        }else if(drawInstructions)
        {
            menu.getInstructions().draw(g);
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }
}
