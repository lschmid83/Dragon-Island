package com.citex.dragonisland.java;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import com.citex.dragonisland.core.game.Settings;

/**
  * This class creates the main application window.
  * @author Lawrence Schmid
  */
@SuppressWarnings("serial")
public class GameFrame extends JFrame implements ComponentListener {
    
	/** Panel where graphics are displayed. */
    public GamePanel mPanel;

	/** Width of the frame. */
	public static int Width;
	
	/** Height of the frame. */
	public static int Height;
    
    /**
     * Initialise a GameFrame object.
     */
    public GameFrame() {
        
    	// Set the window title.
    	setTitle("Dragon Island");
    	
    	// Set the size of the window.
        setSize(Settings.ScreenWidth + 14, Settings.ScreenHeight + 38);
        
        // YouTube video size.
    	//setSize(1296, 760);
    	
        // Screenshot size.
        //setSize((Settings.ScreenWidth * 2) + 14, (Settings.ScreenHeight * 2) + 38);
    	
        // Set the close operation.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialise the game panel.
        mPanel = new GamePanel(this);  
        add(mPanel, BorderLayout.CENTER);
        
        // Centre the window.
        centerScreen();
        
        // Add component listener.
        addComponentListener(this); 
        
        // Set resizable.
        setResizable(true);
        
        // Set visible.
        setVisible(true);

      }

    /**
     * Centres the main game window.
     */
    public void centerScreen() {
        
    	// Get the screen size.
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
    	
    	// Centre the window.
        int x = ((toolkit.getScreenSize().width - getWidth()) / 2);
        int y = ((toolkit.getScreenSize().height - getHeight()) / 2);
        setLocation(x, y);
    }
    
    /**
     * Invoked when the component's size changes.
     * @param event Component event.
     */
    public void componentResized(ComponentEvent event)
    {
    	Width = this.getWidth();
    	Height = this.getHeight();
    }; 
    
    /**
     * Invoked when the component has been made invisible.
     * @param event Component event.
     */
    public void componentHidden(ComponentEvent event){
    	// Not implemented.
    };

    /**
     * Invoked when the component has been made visible.
     * @param event Component event.
     */
    public void componentShown(ComponentEvent event) {
    	// Not implemented.
    };

    /**
     * Invoked when the component's position changes.
     * @param event Component event.
     */
    public void componentMoved(ComponentEvent event) {
    	// Not implemented.
    };

}
