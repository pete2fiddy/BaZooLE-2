/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shift;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author phusisian
 */
public class Menu extends JPanel implements ActionListener
{
    private BufferedImage bazooleImage;
    private JButton newGame, loadGame, instructionsButton;
    private WorldPanel worldPanel;
    private Image resizedBazoole;
    private Font buttonFont = new Font("Futura", Font.PLAIN, 36);
    UI ui;
    private Instructions instructions;
    
    
    public Menu(WorldPanel wp, UI uiIn)
    {
        try{
            bazooleImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Bazoole2.png"));
            resizedBazoole = bazooleImage.getScaledInstance((int)(bazooleImage.getWidth() * ((double)WorldPanel.screenWidth)/1920.0), (int)(bazooleImage.getHeight() * ((double)WorldPanel.screenWidth)/1920.0), Image.SCALE_AREA_AVERAGING);
        }catch(Exception e)
        {
            System.out.println(e);
        }
        worldPanel = wp;
        ui = uiIn;
        instructions = new Instructions(wp, uiIn,this);
        initButtons();
    }
    private void initButtons()
    {
        
        newGame = new JButton("New Game");
        newGame.setFont(buttonFont);
        newGame.addActionListener(this);
        newGame.setActionCommand("newGame");
        newGame.setBounds((WorldPanel.screenWidth/2)-300, 350, 600, 70);
        worldPanel.add(newGame);
        
        loadGame = new JButton("Load Game");
        loadGame.setFont(buttonFont);
        loadGame.addActionListener(this);
        loadGame.setActionCommand("loadGame");
        loadGame.setBounds((WorldPanel.screenWidth/2)-300, 430, 600, 70);
        worldPanel.add(loadGame);
        
        instructionsButton = new JButton("Instructions");
        instructionsButton.setFont(buttonFont);
        instructionsButton.addActionListener(this);
        instructionsButton.setActionCommand("instructions");
        instructionsButton.setBounds((WorldPanel.screenWidth/2)-300, 510, 600, 70);
        worldPanel.add(instructionsButton);
    }
    public void draw(Graphics g)
    {
        g.setColor(new Color(46, 48, 146));
        g.fillRect(0,0,WorldPanel.screenWidth, WorldPanel.screenHeight);
        
        g.drawImage(resizedBazoole, (WorldPanel.screenWidth/2) - (int)((bazooleImage.getWidth() * ((double)WorldPanel.screenWidth)/1920.0)/2), (int)(45*(WorldPanel.screenWidth/1920.0)), null);
    }
    
    public Instructions getInstructions()
    {
        return instructions;
    }

    public void setMenuButtonsVisible(boolean b)
    {
        
            newGame.setVisible(b);
            loadGame.setVisible(b);
            instructionsButton.setVisible(b);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getActionCommand().equals("newGame"))
        {
            ui.setDrawMenu(false);
            newGame.setVisible(false);
            loadGame.setVisible(false);
            instructionsButton.setVisible(false);
            ui.setGameSaver(new GameSaver(true));
            worldPanel.setGameVisible(true);
        }else if(e.getActionCommand().equals("loadGame"))
        {
            ui.setDrawMenu(false);
            newGame.setVisible(false);
            loadGame.setVisible(false);
            instructionsButton.setVisible(false);
            ui.setGameSaver(new GameSaver(false));
            worldPanel.setGameVisible(true);
        }else if(e.getActionCommand().equals("instructions"))
        {
            instructions.setShowInstructions(true);
            ui.setDrawInstructions(true);
            newGame.setVisible(false);
            loadGame.setVisible(false);
            instructionsButton.setVisible(false);
            
        }
    }
}
