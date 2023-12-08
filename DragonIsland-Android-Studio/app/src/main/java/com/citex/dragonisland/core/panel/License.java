package com.citex.dragonisland.core.panel;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSurfaceView;
import com.citex.dragonisland.android.drawing.Grid;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.ResourceType;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.core.sprite.SpriteSheet;
import com.citex.dragonisland.core.sprite.SpriteSheetDefinition;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.thread.Resource;
import com.citex.dragonisland.core.util.FileIO;

/**
 * License.java
 * This class displays the license screen.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class License extends Timing {

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
	
	/**
	 * Initialises a License object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
	 */
	public License(Main main, Object surface) {
		
		// Initialise the license screen.
		mMain = main; 
    	mSurface = surface;
		Settings.State = "license";
		
	}
    
    /**
     * Initialises the resources used by the title screen.
     * @param g Graphics context.
     */
    public void initResources(Object g) {

	    try {
		    	
	    	// Load license graphic.
 			IBufferedImage img = FileIO.getImageResource(g, "gui/logo.png");
 			mMain.setImage(img);
	    	
	    	// Load main game font.
			GameFont gameFont = new GameFont(g, "fnt/main");
			mMain.setGameFont(gameFont);

	    	// Initialise the title screen level data.
	    	Level levelData = new Level(null, "0.0.0.lvl");
	    	Header levelHeader = levelData.getHeader();
	    	mMain.setLevel(levelData);

	    	// Initialise the resource loader.	       	
	    	mResources = Resource.getResourceList();

	    	// Initialise a list of images resources.	
	    	mResources.add(new Resource<String>(g, mMain, "gui/transition.png", ResourceType.IMAGE));  
	    	mResources.add(new Resource<String>(g, mMain, "gui/title.png", ResourceType.IMAGE));
	    	mResources.add(new Resource<String>(g, mMain, "gui/dpad.png", ResourceType.IMAGE)); 
	    	mResources.add(new Resource<String>(g, mMain, "gui/button1.png", ResourceType.IMAGE));
	    	mResources.add(new Resource<String>(g, mMain, "gui/button2.png", ResourceType.IMAGE));
	    	mResources.add(new Resource<String>(g, mMain, "gui/undo.png", ResourceType.IMAGE));  
	    	mResources.add(new Resource<String>(g, mMain, "gui/level.png", ResourceType.IMAGE));     	
	    	mResources.add(new Resource<String>(g, mMain, "gui/pause.png", ResourceType.IMAGE));     	
	    	mResources.add(new Resource<String>(g, mMain, "gui/frame.png", ResourceType.IMAGE));     
	    	mResources.add(new Resource<String>(g, mMain, "gui/padlock.png", ResourceType.IMAGE));  
	    	mResources.add(new Resource<String>(g, mMain, "gui/arrow.png", ResourceType.IMAGE));  
	    		    	
	    	// Initialise a list of fonts resources.
	    	mResources.add(new Resource<String>(g, mMain, "fnt/score", ResourceType.FONT));
	    	mResources.add(new Resource<String>(g, mMain, "fnt/speech", ResourceType.FONT));
	    	mResources.add(new Resource<String>(g, mMain, "fnt/console", ResourceType.FONT));
	    	
	    	// Initialise a list of entity resources.
	    	ArrayList<SpriteSheetDefinition> entities = SpriteSheetDefinition.getSpriteDefinitions("spr/sprite.ini");
	    	for(SpriteSheetDefinition entity : entities) {
		    	mResources.add(new Resource<SpriteSheetDefinition>(g, mMain, entity, ResourceType.ENTITY));	
	    	}
	
	    	// Initialise a list of player resources.
		   	mResources.add(new Resource<String>(g, mMain, "chr/1/", ResourceType.PLAYER));	
 	
	    	// Initialise a list of GUI sprite sheets resources.
	    	ArrayList<SpriteSheetDefinition> gui = SpriteSheetDefinition.getSpriteDefinitions("gui/sprite.ini");
	    	for(SpriteSheetDefinition ui : gui) {
		    	mResources.add(new Resource<SpriteSheetDefinition>(g, mMain, ui, ResourceType.GUI));	
	    	}      	
	    	
	    	// Initialise a tileset resource.
	    	mResources.add(new Resource<Header>(g, mMain, levelHeader, ResourceType.TILESET));

	    	// Initialise a background resource.
	    	mResources.add(new Resource<Header>(g, mMain, levelHeader, ResourceType.BACKGROUND));
	    	
	    	// Initialise a screen transition resource.
	    	mResources.add(new Resource<String>(g, mMain, "gui/transition.png", ResourceType.TRANSITION));
	    	
	    	// Initialise sound resources.
	    	mResources.add(new Resource<String>(g, mMain, "sfx/", ResourceType.SOUND));
	    	
	    	// Initialise credits.
	    	mResources.add(new Resource<String>(g, mMain, "gui/credits.txt", ResourceType.CREDITS));
	    	
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
    	}    	
    	
		// If resource has finished loading.
		if(mResources.get(0).isFinished() && getProgress() < 100) {

			// Increase count of resources processed.
			mCount++;
			
			// Sleep.
			try {
				Thread.sleep(10);
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
	
    	// Draw the company logo.
    	if(mMain.getImage(0) != null) {
        	int x = Settings.ScreenWidth / 2 - 290 / 2;
        	int y = Settings.ScreenHeight / 2 - 64 / 2;    		
        	mMain.getImage(0).draw(g, x, y);
    	}

    	// Draw the loading progress.
    	if(mMain.getGameFont(0) != null && mProgress < 94)
    		mMain.getGameFont(0).drawString(g, 0, "Loading " + getProgress(), Settings.ScreenWidth - 120, Settings.ScreenHeight - 32);
    	else
    		mMain.getGameFont(0).drawString(g, 0, "Loading 100", Settings.ScreenWidth - 120, Settings.ScreenHeight - 32);
   	    	
    	// Indicate the resources have finished loading.
    	if(getProgress() == 100) {

			// Sleep.
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {}
    		
    		Settings.State = "title screen";
    		
	 		// Clear resource loading event.
	        if(Settings.Mode == GameMode.ANDROID)
	        	((GLSurfaceView)mSurface).clearEvent();  
    	}

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
		
        // Set the background colour.       
        gl.glClearColor(37 / 255.0f, 37 / 255.0f, 37 / 255.0f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
        
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
