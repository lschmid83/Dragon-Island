package nsmb_demo1;

import java.awt.*;
import javax.swing.*;

/**
 * Creates a frame containing the canvas which the GamePanel which the level
 * graphics are drawn on.
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class GameFrame extends JFrame {
    
    public static GamePanel canvas = null;
    
    /**
     * Construct GameFrame 
     */     
    public GameFrame() {
        setTitle("New Super Mario Bros. Demo 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //construct GamePanel
        canvas = new GamePanel();
        canvas.start(); //start game

        //add GamePanel to frame
        add(canvas, BorderLayout.CENTER);
        setSize(502,310);
        centerScreen();  
        setResizable(true);
        setVisible(true);             
    }   

    /**
    * Center frame for the screen resolution
    */   
    public void centerScreen()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = ((toolkit.getScreenSize().width - getWidth()) / 2);
        int y = ((toolkit.getScreenSize().height - getHeight()) / 2);
        setLocation(x, y);     
    }          
}
