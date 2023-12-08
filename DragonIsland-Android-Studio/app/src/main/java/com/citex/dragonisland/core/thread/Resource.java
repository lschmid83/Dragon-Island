package com.citex.dragonisland.core.thread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.drawing.Background;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.ResourceType;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Transition;
import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.sprite.SpriteSheet;
import com.citex.dragonisland.core.sprite.SpriteSheetDefinition;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.tileset.Tileset;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Sound;
import com.citex.dragonisland.core.util.Sprite;

/**
 * Resource.java
 * This class loads game resources in a thread.
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

public class Resource<T> extends Thread {
		
    /** Main thread. */
    private Main mMain;
    
    /** Resources thread. */    
    private Thread mThread;		
    
    /** Graphics context */
    private Object mGraphics;
    
    /** Resource. */
    private T mResource;  
    
    /** Resource type */
    private ResourceType mResourceType;

    /** Indicates if the thread is running. */
    private boolean mRunning;    	
	
    /** Default constructor */
    public Resource() {}
        
    /**
     * Initialises a Resource object.
     * @param g Graphics context.
     * @param main Main thread.
     * @param resource Resource to load.
     * @param resourceType Type of resource.
     */
	public Resource(Object g, Main main, T resource, ResourceType resourceType) {
    	
		mGraphics = g;
    	mMain = main;
    	mResource = resource;
    	mResourceType = resourceType;
    	mRunning = false;
 	
	}

	/**
     * Starts the thread.
     */
	public void start() {
		if (mThread == null) {
			mThread = new Thread(this);
			mThread.start();
		}
	}
	
    /**
     * Runs the thread.
     */	
	public void run() {
		
		mRunning = true;
		synchronized (this) {

	    	// Load images.
	    	if(mResourceType == ResourceType.IMAGE) {	
	    		
				try {
					IBufferedImage img = FileIO.getImageResource(mGraphics, (String)mResource);
					mMain.setImage(img);
				} catch (IOException e) {
				}
	 			
	    	}	
	    	
	    	// Load game fonts.
	    	else if(mResourceType == ResourceType.FONT) {			
				
	    		try {
	    			GameFont gameFont = new GameFont(mGraphics, (String)mResource);
	    			mMain.setGameFont(gameFont);
	    		} catch(IOException e) {
	    			
	    		}
	    		
	    	}   	
	    	
	    	// Load entity sprite sheets.
	    	else if(mResourceType == ResourceType.ENTITY) {
	 			
	    		SpriteSheetDefinition sprite = (SpriteSheetDefinition)mResource;	
	    		
	    		try {
					mMain.setEntitySprite(Sprite.getEntity(mMain, mGraphics, "spr/", sprite.mIndex));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}   
	    	
	    	// Load player sprite sheets.
	    	else if(mResourceType == ResourceType.PLAYER) {
	 			
	    		Player player = null;
				try {
					player = new Player(mMain, mGraphics, (String)mResource);
				} catch (Exception e) {
				}
	    		mMain.setPlayerSprite(player);
	    	}   
	    	
	    	// Load GUI sprite sheets.
	    	else if(mResourceType == ResourceType.GUI) {
	 			
	    		try {
	    			SpriteSheetDefinition sprite = (SpriteSheetDefinition)mResource;
	    			SpriteSheet spriteSheet = new SpriteSheet(mGraphics, "gui/" + sprite.mIndex + ".png", sprite.mFrameWidth, sprite.mFrameHeight, sprite.mFrameCount);
	    			mMain.setGuiSprite(spriteSheet);
	    		} catch(IOException e) {
	    			
	    			
	    		}
	    		
	    	}  
	    	
	    	// Load tilesets.
	    	else if(mResourceType == ResourceType.TILESET) {
	 			
	    		try {
	    			Header levelHeader = (Header)mResource;
					Tileset tileset  = new Tileset(mGraphics, levelHeader.tileset0, levelHeader.tileset16, levelHeader.tileset32);
					mMain.setTileset(tileset);
	    		} catch(IOException e) {
	    			
	    		}
	    	}     	
	    	
	    	// Load backgrounds.
	    	else if(mResourceType == ResourceType.BACKGROUND) {
	 			
	    		try {
	    			Header levelHeader = (Header)mResource;
	    			int height = levelHeader.height;
	    			if(height < Settings.ScreenHeight)
	    				height = Settings.ScreenHeight;
					Background background = new Background(mGraphics, levelHeader.bg, levelHeader.bgSpeed, levelHeader.bgAlign, levelHeader.width, height);
					mMain.setBackground(background);
	    		}
	    		catch(Exception e) {}
	    	}  
    	
	    	// Load screen transitions.
	    	else if(mResourceType == ResourceType.TRANSITION) {
	 			
	    		try {
	    			Transition transition = new Transition(mGraphics, (String)mResource);
	    			mMain.setScreenTransition(transition);
	    		} catch(Exception e) {
	    			
	    		}
	    		
	    	}   
	    	
	    	// Load sound effects.
	    	else if(mResourceType == ResourceType.SOUND) {
	    		
	    		try {
	    			ISoundEffect soundEffects = Sound.getSoundPlayer((String)mResource);
	    			mMain.setSoundEffects(soundEffects);
	    		}
	    		catch(Exception e) {}
	    		
	    	}

	    	// Load credits text.
	    	else if(mResourceType == ResourceType.CREDITS) {
	    		
	    		BufferedReader input = null;
	            try 
	            {
	            	input = FileIO.openBufferedReader((String)mResource);
	                String line = null;
	                ArrayList<String> credits = new ArrayList<String>();
	                while ((line = input.readLine()) != null) {
	                    credits.add(line);
	                }
	                mMain.setCredits(credits);
	            } catch (IOException ex) {

	            }  
	            finally {
	            	
                    try {
                        input.close();
                    } catch (IOException e) {
                    }
	            }
	    		
	    	}
	    	
	    	
		}
		mRunning = false;
	}
		
    /**
     * Indicates if the thread has finished executing.
     * @return True if finished; otherwise false.
     */
    public boolean isFinished() {
    	return !mRunning;
    }  
    
    /** 
     * Gets a new resource list.
     */
    public static ArrayList<Resource<?>> getResourceList() {

    	ArrayList<Resource<?>> list = new ArrayList<Resource<?>>();	 
    	list.add(new Resource<String>());   	
    	return list;
    }

}