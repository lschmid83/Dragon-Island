package com.citex.dragonisland.java;

import java.awt.*;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.*;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.thread.Main;

/**
 * This class provides a drawing surface for the graphics and handles mouse and keyboard events.
 * @author Lawrence Schmid
 */
@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, 
												 MouseMotionListener, MouseWheelListener {

    /** Title screen thread. */    
    private Thread mThread;	
	
	/** Back buffer. */
    private BufferedImage mBackBuffer;
    
    /** Graphics context. */
    private Graphics2D mGraphics;
    
    /** Main thread. */
    private Main mMain;
    
    /** Is the thread running. */
    private boolean mRunning;
 
    /**
	 * Initialise a GamePanel object.
	 * @param frame JFrame object.
	 */
    public GamePanel(JFrame frame) {

    	// Set the screen resolution.
    	setResolution(Settings.ScreenWidth, Settings.ScreenHeight);

        // Initialise the game thread.
        mMain = new Main(null, this, frame);
       
        // Add event listeners.
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);      
        
        // Set focusable.
        setFocusable(true);
        requestFocus();
        
        // Start the thread.
        start();
    }

    /**
     * Recreates the graphics surface to a set height and width.
     * @param width Width of graphics surface.
     * @param height Height of graphics surface.
     */
    public void setResolution(int width, int height) {
    	
    	// Create the back buffer.
        mBackBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Get the graphics context.
        mGraphics = (Graphics2D) mBackBuffer.getGraphics(); 	
   	
    }
        
    /**
     * Gets the buffer graphics context.
     */
    public Graphics getBufferedGraphics() {
    	return mGraphics;
    }   
    
    /**
     * Starts the thread.
     */
    public void start() {
    	mThread = new Thread(this);
        mThread.start();
    }       
    
    /**
     * Runs the thread.
     */
    public void run() {

		// Repaint the component.
    	repaint();
    	
		// Sleep.
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {}  
        
        mRunning = true;
    	while(mRunning) {
    		
    		// Repaint the component.
        	repaint();

    		// Sleep.
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}  
    		
    	}
    }
    
    /**
     * Paints the component.
     */
    public void paintComponent(Graphics g) {
    	
    	// Paint panel.
    	super.paintComponent(g);
        
    	if(!mRunning) {    	
    		
    		g.setColor(new Color(37, 37, 37));
    		g.fillRect(0, 0, getWidth(), getHeight());
    	
        	// Draw back buffer.
        	g.drawImage(mBackBuffer, 0, 0, getWidth() + 2, getHeight() + 1, this);

        	return;

    	}

    	// Draw panel.
    	if(Settings.State == "license" && mMain.getLicense() != null)
        	mMain.getLicense().drawFrame(mGraphics);
        else if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().drawFrame(mGraphics);
        else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null)
        	mMain.getLevelSelectScreen().drawFrame(mGraphics);         
        else if(Settings.State == "level loading" && mMain.getLevelLoadingScreen() != null)
        	mMain.getLevelLoadingScreen().drawFrame(mGraphics);    
        else if(Settings.State == "game" && mMain.getGame() != null)
        	mMain.getGame().drawFrame(mGraphics);  
        else if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().drawFrame(mGraphics);  

    	// Draw back buffer.
    	g.drawImage(mBackBuffer, 0, 0, getWidth() + 2, getHeight() + 1, this);

    }
    
    /**
     * Invoked when a key has been pressed.
     * @param e event which indicates that a keystroke occurred in a component
     */
    public void keyPressed(KeyEvent e) {
    
        if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().onKeyDown(e.getKeyCode(), true);	
        else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null)
        	mMain.getLevelSelectScreen().onKeyDown(e.getKeyCode(), true);
        else if(Settings.State == "game" && mMain.getGame() != null)
        	mMain.getGame().onKeyDown(e.getKeyCode(), true);	
        else if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().onKeyDown(e.getKeyCode(), true);	
        
    }
    
    /**
     * Invoked when a key has been released
     * @param e event which indicates that a keystroke occurred in a component
    */
    public void keyReleased(KeyEvent e) {
    
        if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().onKeyDown(e.getKeyCode(), false);	   	
        else if(Settings.State == "game" && mMain.getGame() != null)
        	mMain.getGame().onKeyDown(e.getKeyCode(), false);
        else if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().onKeyDown(e.getKeyCode(), false);	  
    }
    
    /**
     * Invoked when a key has been typed
     * @param e event which indicates that a keystroke occurred in a component
     */
    public void keyTyped(KeyEvent e) {
    	
    }
    
    /**
     * Invoked when the mouse enters a component
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component
     * @param e An event which indicates that a mouse action occurred in a component
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseMoved(MouseEvent e) {

        if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().mouseMoved(e);	     	
    	
    }

    /**
     * Invoked when the mouse exits a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseDragged(MouseEvent e) {

        if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().mouseDragged(e);
    	
    }

    /**
     * Invoked when a mouse button has been released on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseReleased(MouseEvent e) {

        if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().mouseReleased(e);
    }

    /**
     * Invoked when a mouse button has been pressed on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mousePressed(MouseEvent e) {
    	
        if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().mousePressed(e);
        else if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().mousePressed(e);
        else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null)
        	mMain.getLevelSelectScreen().mousePressed(e);
        else if(Settings.State == "game" && mMain.getLevelSelectScreen() != null)
        	mMain.getGame().mousePressed(e);

    }

    /**
     * Invoked when the mouse wheel is rotated<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that the mouse wheel was rotated in a component
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

        if(Settings.State == "editor" && mMain.getEditor() != null)
        	mMain.getEditor().mouseWheelMoved(e);	
    	
    }  
   
}
