package com.citex.dragonisland.applet;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JApplet;

import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.java.GamePanel;

/**
 * This class creates the applet content pane.
 * @author Lawrence Schmid
 */
@SuppressWarnings("serial")
public class Main extends JApplet implements ComponentListener
{
	/** Panel where graphics are displayed. */
    public static GamePanel mPanel;

	/** Width of the panel. */
	public static int Width;
	
	/** Height of the panel. */
	public static int Height;   
    
    /**
     * Main entry point of the applet.
     * @param args Command line arguments.
     */
    public void init () {
    	
    	// Initialise the settings.
    	Settings.Mode = GameMode.APPLET;
    	Settings.ShowControls = false;
    	
    	// Create the content pane.
     	Container content_pane = getContentPane();
        
     	// Add the game panel to the content.
    	mPanel = new GamePanel(null);        
    	content_pane.add (mPanel);
    	
        // Add component listener.
        addComponentListener(this); 

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
