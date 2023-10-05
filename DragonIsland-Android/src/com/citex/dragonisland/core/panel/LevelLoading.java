package com.citex.dragonisland.core.panel;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.os.Environment;

import com.citex.dragonisland.android.drawing.GLSurfaceView;
import com.citex.dragonisland.android.drawing.Grid;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.ResourceType;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.thread.Resource;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Numbers;
import com.citex.dragonisland.java.GamePanel;

/**
 * This class displays the level loading screen.
 * @author Lawrence Schmid
 */
public class LevelLoading extends Timing {

	/** Main thread. */
    private Main mMain;	
    
    /** Resources thread. */
    private ArrayList<Resource<?>> mResources;

    /** Graphics surface. */
    private Object mSurface;
    
    /** Current resources index. */
    private int mCount;
    
    /** Total resources. */
    private int mTotal;
    
	/** Percent progress. */
	private float mProgress;  
	
	/** Have the resources been initialised. */
	private boolean mInitResources;
	
	/** Level path. */
	private String mLevel;
	
	/** Display level loading. */
	private boolean mDisplay;
					
	/**
	 * Initialises a LevelLoading object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
	 * @param level Level path.
	 * @param display Display level loading.
	 */
	public LevelLoading(Main main, Object surface, String level, boolean display) {
		
		// Initialise the level loading screen.
		mMain = main; 
    	mSurface = surface;
    	mLevel = level;
    	mDisplay = display;
		Settings.State = "level loading";
		
	}	
	
	/**
     * Initialises the resources used by the game.
     * @param g Graphics context.
     */
    public void initResources(Object g) {

	    try {
		    	    	
	    	// Initialise the level data.
	    	Level levelData = new Level(mMain.getGameFolder(), mLevel);
	    	mMain.setLevel(levelData);

	    	// Initialise the resource loader.	       	
	    	mResources = Resource.getResourceList();

	    	// Initialise a tileset resource.
	    	Header levelHeader = levelData.getHeader();
	    	if(Settings.Mode == GameMode.ANDROID)
	    		mMain.getTileset().destroy((GL10)g);
	    	mResources.add(new Resource<Header>(g, mMain, levelHeader, ResourceType.TILESET));

	    	// Initialise a background resource.
	    	if(Settings.Mode == GameMode.ANDROID)
	    		mMain.getBackground().destroy((GL10)g);
	    	mResources.add(new Resource<Header>(g, mMain, levelHeader, ResourceType.BACKGROUND));
	    	
	    	// Start the resource thread.
	    	if(Settings.Mode == GameMode.ANDROID) {
		    	
	    		// Queue a runnable to be run on the GL rendering thread.
	    		((GLSurfaceView)mSurface).queueEvent(mResources.get(0));
	    	}
	    	else {
	    		
	    		// Start the resource thread.
	    		mResources.get(0).start();
	    	}
	 		
	 		mTotal = mResources.size();
	 		mInitResources = true;

	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }		
    }	
	
    /**
	 * Paints the license screen.
	 * @param g Graphics context.
	 * @param dt Delta time between frame updates.
	 */
    public void paintComponent(Object g, float dt) {
	
    	// Initialise resources.
    	if(!mInitResources) {
    		initResources(g);
    		
    		// Add advertisement.
    		if(Settings.Mode == GameMode.ANDROID) {
    			mMain.getActivity().runOnUiThread(new Runnable() {  
                    public void run() {
                    	mMain.getActivity().addAdvertisement(); 
                    } 
                });          
    		}
 
    	}
    	    	
		// If resource has finished loading.
		if(mResources != null && mResources.get(0).isFinished() && getProgress() < 100) {

			// Increase count of resources processed.
			mCount++;
			
			// Sleep.
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}        			
			
			// Remove resource and start the next thread.
			if(mResources.size() > 1) {
								
				mResources.remove(0);
				
				if(Settings.Mode == GameMode.ANDROID) {
					((GLSurfaceView)mSurface).clearEvent();
					((GLSurfaceView)mSurface).queueEvent(mResources.get(0));	
				}
				else  {
					mResources.get(0).start();
				}
			}	
		}

    	// Update progress.
		mProgress = (float)mCount / (float)mTotal * 100f;
	
		// Display level loading.
    	if(mDisplay) {
		
	    	// Draw the level loading graphic.
	    	float x = Settings.ScreenWidth / 2 - 158 / 2;
	    	float y = Settings.ScreenHeight * 0.445f;    		
	    	mMain.getImage(7).draw(g, x, y);
	 
	    	// Draw Lives.    	
	    	mMain.getGuiSprite(2).drawFrame(g, 0, Settings.ScreenWidth * 0.446f, Settings.ScreenHeight * 0.606f);
	    	mMain.getPlayers().get(0).getIcon().draw(g, Settings.ScreenWidth * 0.415f, Settings.ScreenHeight * 0.61f);
	
	    	// Convert number of lives into digits.
	    	int lives = mMain.getSaveFile().getSaveGame(mMain.getGameFolderIndex()).lives;
	    	int digit[] = Numbers.convertNumberToDigit(lives, 2);  	
	        mMain.getGuiSprite(0).drawFrame(g, digit[0], Settings.ScreenWidth * 0.518f, Settings.ScreenHeight * 0.616f);
	        mMain.getGuiSprite(0).drawFrame(g, digit[1], Settings.ScreenWidth * 0.538f, Settings.ScreenHeight * 0.616f);
	
	        // Draw level number.
	        mMain.getGameFont(0).drawString(g, 0, "World " + mMain.getLevel().getHeader().world + "-" + mMain.getLevel().getHeader().level, Settings.ScreenWidth * 0.408f, y + 1);
    	}
    	    	
    	// Indicate the resources have finished loading.
    	if(getProgress() == 100) {
    		 
	  		if(mDisplay) {
	  			
	  			// Sleep.
    			try { 
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {}	
	  		}    		
    		
    		// Check number of lives remaining.
	  		if(mLevel == "0.0.0.lvl") {
	  			
	    		// Return to title screen.
	  			Settings.State = "title screen";
	  			
	  		} 
	  		else if(mLevel == "0.0.1.lvl") {
	  		
	  			// Level select.
	  			Settings.State = "start level select";	
	  			
	  		} else {
	  			
	  			// Restart level.
	  			Settings.State = "start game";	  			
	  		}

	 		// Clear resource loading event.
	        if(Settings.Mode == GameMode.ANDROID)
	        	((GLSurfaceView)mSurface).clearEvent(); 

    	}

    }
  
    /**
     * Draw the current frame.
     * @param g Graphics context.
     */
    public void drawFrame(Graphics g) {
    	
    	// Set the current time.
    	setCurrentTime(System.currentTimeMillis());

    	// Calculate the delta time.
    	float dt = getDeltaTime();
		
    	if(mDisplay) {
    		
    		// Set the background colour.
    		g.setColor(new java.awt.Color(235, 235, 235));
			g.fillRect(0, 0, Settings.ScreenWidth, Settings.ScreenHeight);	
    	}
    	
		// Paint the component.
		paintComponent(g, dt);

		// Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	    	
    	
    }
        
    /**
     * Draw the current frame.
     * @param gl Graphics context.
     */
    public void drawFrame(GL10 gl) {
    
    	// Set the current time.
    	setCurrentTime(System.currentTimeMillis());

    	// Calculate the delta time.
    	float dt = getDeltaTime();

    	// Begin drawing.
        Grid.beginDrawing(gl, true, false);
		
    	if(mDisplay) {
        
    		// Set the background colour.       
    		gl.glClearColor(235.0f / 255.0f, 235.0f / 255.0f, 235.0f / 255.0f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
    	}
    	
        // Draw license screen.
        paintComponent(gl, dt);

        // End drawing.
        Grid.endDrawing(gl);    	
        
        // Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	

    }
    
    /**
     * Gets the percentage progress.
     * @return Percent progress.
     */
    public int getProgress() {
    	return (int)mProgress;
    }
    
    /**
     * Gets the total number of resources. 
     * @return Total resources.
     */
    public int getTotal() {
    	return mResources.size();
    }  
    
}
