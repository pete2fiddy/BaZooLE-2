package shift;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import javax.swing.JButton;

public class Instructions implements ActionListener
{
    private Image[] instructionPages;
    private JButton next, back, exit;
    private Menu menu;
    private WorldPanel worldPanel;
    private UI ui;
    private int currentPage = 0;
    private boolean showInstructions = false;
    
    public Instructions(WorldPanel wp, UI uiIn, Menu menuIn)
    {
        worldPanel = wp;
        ui = uiIn;
        try{
            Image[] tempImages = {ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Instructions1.png")).getScaledInstance(WorldPanel.screenWidth-80, ((WorldPanel.screenWidth-80)/16)*9, Image.SCALE_AREA_AVERAGING),ImageIO.read(getClass().getClassLoader().getResourceAsStream("Images/Instructions2.png")).getScaledInstance(WorldPanel.screenWidth-80, ((WorldPanel.screenWidth-80)/16)*9, Image.SCALE_AREA_AVERAGING) };
            instructionPages = tempImages;
        }catch(Exception e)
        {
            
        }
        menu = menuIn;
        initButtons();
    }
    
    public void setShowInstructions(boolean b)
    {
        next.setVisible(b);
        back.setVisible(b);
        exit.setVisible(b);
        showInstructions = b;
    }
    
    private void initButtons()
    {
        next = new JButton("Next");
        next.addActionListener(this);
        next.setActionCommand("next");
        next.setBounds((WorldPanel.screenWidth/2), WorldPanel.screenHeight - 150, 300, 70);
        next.setVisible(false);
        worldPanel.add(next);
        
        back = new JButton("Back");
        back.addActionListener(this);
        back.setActionCommand("back");
        back.setBounds((WorldPanel.screenWidth/2)-300, WorldPanel.screenHeight - 150, 300, 70);
        back.setVisible(false);
        worldPanel.add(back);
        
        exit = new JButton("Exit");
        exit.addActionListener(this);
        exit.setActionCommand("exit");
        exit.setBounds(50, 50, 50, 50);
        exit.setVisible(false);
        worldPanel.add(exit);
    }
    
    public void draw(Graphics g)
    {
        g.setColor(new Color(42, 56, 143));
        g.fillRect(0, 0, WorldPanel.screenWidth, WorldPanel.screenHeight);
        g.drawImage(instructionPages[currentPage], 40, (40/16)*9, null);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("next"))
        {
            if(currentPage < 1)
            {
                currentPage++;
            }
        }else if(e.getActionCommand().equals("back"))
        {
            if(currentPage > 0)
            {
                currentPage--;
            }
        }else if(e.getActionCommand().equals("exit"))
        {
            setShowInstructions(false);
            ui.setDrawInstructions(false);
            ui.setDrawMenu(true);
            menu.setMenuButtonsVisible(true);
        }
    }
}
