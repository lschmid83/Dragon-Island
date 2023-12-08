package com.citex.dragonisland.core.panel;

import android.util.Log;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.android.drawing.Grid;
import com.citex.dragonisland.core.drawing.Color;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Score;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.core.sprite.entity.EntityDescription;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.entity.enemy.JumpingLava;
import com.citex.dragonisland.core.sprite.entity.enemy.PiranhaPlant;
import com.citex.dragonisland.core.sprite.entity.object.CastleFlag;
import com.citex.dragonisland.core.sprite.player.enums.FlagState;
import com.citex.dragonisland.core.sprite.player.enums.PipeState;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.tileset.TileDescription;
import com.citex.dragonisland.core.util.Numbers;

/**
 * Game.java
 * This displays the main game.
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

public class Game extends Timing {

	/** Main thread. */
    private Main mMain;	
        
    /** Show screen transition. */
    private boolean mTransition;
           
    /** List of entities. */
    private ArrayList<IEntity> mEntity;
    
    /** List of points to draw. */
    private ArrayList<Score> mPoints;
    
    /** Game state. */
    private String mState;
    
    /** Number of options in a menu. */
    private int mOptions;
    
    /** Selected option in a menu. */
    private int mSelectedOption;  
    
    /** Time elapsed while saving game progress. */
    private float mTimer;
    
    /** End of level castle position. */
    private Point mCastlePosition;
    
    /** Amount to scroll credits. */
    private int mCreditScroll;
	
    /**
	 * Initialises a Game object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
	 */
	public Game(Main main, Object surface, boolean transition) {

		// Initialise the game.
		mMain = main;
		mMain.setCamera(new Point(0, 0));
    	mTransition = transition;
    	mEntity = new ArrayList<IEntity>();
    	mPoints = new ArrayList<Score>();
    	mCreditScroll = Settings.ScreenHeight / 2;
    	
    	// Find end of level castle position.
    	Level level = main.getLevel();
        for (int i = 0; i < level.getObjectCount(); i++) {
        	
        	// Get tile description.
        	TileDescription tile = level.getTileDescription(i);
            
        	// Castle tile.
        	if (tile.tile == 2 && tile.tileset == 0) {
                mCastlePosition = new Point(tile.x * 16, tile.y * 16);
            }
        }
        
    	// Set the game state.
    	Settings.State = "game";	
    	mState = "game";
			
		// Remove advertisement.
		if(Settings.Mode == GameMode.ANDROID) {
			mMain.getActivity().runOnUiThread(new Runnable() {  
                public void run() {
                	mMain.getActivity().removeAdvertisement(); 
                } 
            });          
		}
		
		// Initialise entities.
		initEntities();
	}  
		
	
	/**
	 * Loads a level and initialises the entities.
	 */
	public void initEntities() {
		
		// Initialise the entity list.
		mEntity = new ArrayList<IEntity>();
			
		// Loop through entity descriptions.
		for(EntityDescription entity : mMain.getLevel().getEntityDescriptions()) {
			
			if(!Settings.RemoveEnemies || (entity.tile > 0 && entity.tile < 4)) {
				addEntity(entity.tile, 'l', entity.angle, entity.x * 16, entity.y * 16);				
			}
		}
	}
    
	/**
	 * Paints the graphics.
	 * @param g Graphics context.
	 * @param dt Delta time.
	 */
    public void paintComponent(Object g, float dt) {

		// Set camera.
		Point cam = mMain.getCamera();
    	cam.x = mMain.getCurrentPlayer().getLeft() - Settings.ScreenWidth / 2;
		cam.y = mMain.getCurrentPlayer().getDown() - Settings.ScreenHeight / 3;   	
    	    	
    	// Draw background.
    	cam = mMain.getBackground().draw(g, dt, cam);
    	
    	// Draw piranha plant, lava ball and castle flag before tileset.
    	for(int i = 0; i < mEntity.size(); i++) {

   			// Skip entities which should not be drawn before tileset.
   			if((mEntity.get(i).getClass() != CastleFlag.class && 
		 	    mEntity.get(i).getClass() != PiranhaPlant.class) ||
		 	    mEntity.get(i).getSpinOffScreen())
   				continue;
    		    		
    		// Game is paused.
    		if(Settings.Paused) {
    			
	   			// Draw entity.
	   			mEntity.get(i).draw(g, dt, cam);
	   			continue;
    		}
    		
   			// Check entity is on the screen.
   		 	if(mEntity.get(i).isInScreenArea(cam, 128)) {
   				
	   			// Animate entity.
	   			mEntity.get(i).animate(dt);
   		 		
	   			// Move entity.
	   			mEntity.get(i).move(dt, cam);

	   			// Draw entity.
	   			mEntity.get(i).draw(g, dt, cam);
	   			
	   			// Detect player collisions.
	   			mEntity.get(i).detectPlayerCollision(mMain.getCurrentPlayer());   
	   			
   		 	}
   		}

    	// Draw level map tiles.
    	mMain.getTileset().drawMap(g, dt, mMain.getLevel().getMap(), cam);

   		// Loop through entities.
   		for(int i = 0; i < mEntity.size(); i++) {
   			
   			// Skip entities which have already been drawn.
   			if(mEntity.get(i).getClass() == CastleFlag.class || 
		 	  (mEntity.get(i).getClass() == PiranhaPlant.class && !mEntity.get(i).getSpinOffScreen()))
   				continue;
   			
    		// Game is paused.
    		if(Settings.Paused) {
    			
	   			// Draw entity.
	   			mEntity.get(i).draw(g, dt, cam);
	   			continue;
    		}
   			   			
   			// Check entity is on the screen.
   		 	if(mEntity.get(i).isInScreenArea(cam, 128)) {
   				
	   			// Animate entity.
	   			mEntity.get(i).animate(dt);
   		 		
	   			// Move entity.
	   			mEntity.get(i).move(dt, cam);

	   			// Draw entity.
	   			mEntity.get(i).draw(g, dt, cam);
	   			
	   			// Detect map collisions.
	   			mEntity.get(i).detectMapCollision(mMain.getLevel().getMap());
	   			
	   			// Detect player collisions.
	   			mEntity.get(i).detectPlayerCollision(mMain.getCurrentPlayer());
	   			
	   			// Detect entity collisions.
	   			mEntity.get(i).detectEntityCollision(mEntity, cam);

   		 	} else {

   		 		// Entity is not in screen area.
   		 		if(mEntity.get(i).getAnimationState().equals("kick") ||
   		 		   mEntity.get(i).getClass() == JumpingLava.class) {
   		 			
   		   			// Move entity.
   		   			mEntity.get(i).move(dt, cam);
    		   			
   		   			// Detect map collisions.
   		   			mEntity.get(i).detectMapCollision(mMain.getLevel().getMap());
   		   			
   		   			// Detect entity collisions.
   		   			mEntity.get(i).detectEntityCollision(mEntity, cam);
   		 		}
   		 		
   		 	}

   			// Remove entity.
   			if(mEntity.get(i).isDead())
   				mEntity.remove(i);
	
   		}

 		// Draw lava map.
    	mMain.getTileset().drawLavaMap(g, dt, mMain.getLevel().getMap(), cam);
   		
   		// Draw player.
 		mMain.getPlayers().get(0).draw(g, dt, cam);
 		
 		// Detect map collisions.
 		mMain.getPlayers().get(0).detectMapCollision(g, mMain.getLevel().getMap());

 		// Detect pipe collisions.
 		mMain.getPlayers().get(0).detectPipeCollision(g, mMain.getLevel().getMap(), cam);		

 		// Draw warp pipe over player.
        if (mMain.getCurrentPlayer().getPipeState() != PipeState.NONE) 
        	mMain.getTileset().drawPipeMap(g, dt, mMain.getLevel().getMap(), cam);
 		
        // Draw castle over player.
        if(mMain.getCurrentPlayer().getFlagState() == FlagState.WALK_TO_CASTLE)
        	mMain.getTileset().drawFrame(g, (int)mCastlePosition.x - (int)cam.x, (int)mCastlePosition.y - (int)cam.y, 2, 0, 0, 0);

        // Draw map object explosions.
        mMain.getTileset().drawExplosions(g, mMain.getLevel().getMap(), cam);

        // Draw points.
        drawPoints(g, dt, cam);
   		
        if(mState != "credits") {
        
	   		if(Settings.ScreenWidth > 400) {
		        
	   			// Draw lives.
	   			drawLives(g, mMain.getPlayers().get(0).getLives(), 50, 15);
		    	
		   		// Draw coins.
		   		drawCoins(g, mMain.getPlayers().get(0).getCoins(), 140, 15);
		   		
		   		// Draw score.
				drawScore(g, mMain.getPlayers().get(0).getScore(), 235, 15);
				
				// Draw time.
				drawTime(g, mMain.getPlayers().get(0).getTime(), 392, 15);
				
	   		} else {
	   			
	   			// Draw lives.
	   			drawLives(g, mMain.getPlayers().get(0).getLives(), 25, 15);
		    	
		   		// Draw coins.
		   		drawCoins(g, mMain.getPlayers().get(0).getCoins(), 112, 15);
		   		
		   		// Draw score.
				drawScore(g, mMain.getPlayers().get(0).getScore(), 189, 15);
				
				// Draw time.
				drawTime(g, mMain.getPlayers().get(0).getTime(), 332, 15);	
	   			
	   		}
        } else {
        	
        	// Draw credits.
        	drawCredits(g, dt);
        }
   		
    	// Draw controls.
		if(Settings.ShowControls) {
			mMain.getControls().drawTouchScreen(g, mMain.getImages());
		}
		
		// Draw debug output.
		if(Settings.DebugMode) {
			mMain.getDebug().draw(g, 5, 5);	
		}
		
		// Draw paused screen.
		if(Settings.Paused) {
			
			if(mState == "pause")
				drawPaused(g);				
			else if(mState == "options")
				drawOptions(g);
			
		}
	
    }

    /**
     * Adds a block explosion.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void addExplosion(float x, float y) {
        mMain.getTileset().setBlockExplode(x, y, 'l');
        mMain.getTileset().setBlockExplode(x + 16, y, 'r');
        mMain.getTileset().setBlockExplode(x, y + 16, 'l');
        mMain.getTileset().setBlockExplode(x + 16, y + 16, 'r');
    }
    
    /**
     * Adds a new entity to the collection.
     * @param index Entity index.
     * @param direction Start direction.
     * @param x the X coordinate.
     * @param y the Y coordinate
     * @throws CloneNotSupportedException 
     */
    public void addEntity(int index, char direction, int angle, float x, float y) {
        
    	if(Settings.RemoveEnemies && index > 8)
    		return;
    	    	
    	IEntity entity = null;
		try {
			
			// Make a clone of the entity resource.
			entity = (IEntity)mMain.getEntity(index).clone((int)x, (int)y, angle);

			// Set direction.
			entity.setDirection(direction);
			
			// Set angle.
			entity.setAngle(angle);
			
			// Add entity.
			mEntity.add(entity);
			
		} catch (CloneNotSupportedException e) {
			
		} catch(Exception e) {
			
		}

    }  
    
    /**
     * Gets a entity.
     * @param index Entity index.
     */
    public IEntity getEntity(int index) {
    	return mEntity.get(index);    	
    }    
        
    /**
     * Adds points to be drawn on the screen.
     * @param score Score object.
     */
    public void addPoints(Score score) {
    	mPoints.add(score);	
    }
    
    /**
     * Draws the amount of remaining lives.
     * @param g Graphics context.
     * @param amount Amount of lives.
     * @param x X coordinate.
     * @param y Y coordinate.
     */   
    public void drawLives(Object g, int amount, int x, int y) {
    	
    	// Convert number into digits.
    	int digit[] = Numbers.convertNumberToDigit(amount, 2);

    	// Draw Lives.    	
    	mMain.getPlayers().get(0).getIcon().draw(g, x, y);   
    	mMain.getGuiSprite(2).drawFrame(g, 0, x + 10, y - 1);
    	mMain.getGuiSprite(0).drawFrame(g, digit[0], x + 38, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[1], x + 46, y + 2);  

    }   
	
    /**
     * Draws the amount of coins. 
     * @param g Graphics context.
     * @param amount Amount of coins.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void drawCoins(Object g, int amount, int x, int y) {  
    	
    	// Convert number into digits.
    	int digit[] = Numbers.convertNumberToDigit(amount, 2);
        
    	// Draw coins.
    	mMain.getGuiSprite(2).drawFrame(g, 1, x, y - 1);
    	mMain.getGuiSprite(0).drawFrame(g, digit[0], x + 28, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[1], x + 36, y + 2);         
    	
    }  
    
    /**
     * Draws the end credits.
     * @param g Graphics context.
     */
    private void drawCredits(Object g, float dt) {

    	// Set the amount to scroll by.
        if (mTimer > 45) {
            mCreditScroll--; 
            mTimer = 0;
        }
        
        // Scroll credits.
        for (int i = 0; i < mMain.getCredits().size(); i++) {
            
        	int x = Settings.ScreenWidth / 2 - mMain.getGameFont(0).getStringWidth(mMain.getCredits().get(i)) / 2; 
            int y = mCreditScroll + i * 20;
            
            if(y > -20 && y < 272)
            	mMain.getGameFont(0).drawString(g, 0, mMain.getCredits().get(i), x, y);
            
            if (i == mMain.getCredits().size() - 1 && y < -20) {

    			// Return to title screen.
            	mMain.setLevelPath("0.0.0.lvl");
    			Settings.State = "load level";
            }
        }
        
    	mTimer+= dt;
    	
    }
    
    /**
     * Draws the options screen.
     * @param g Graphics context.
     */
    private void drawOptions(Object g) {
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);

        // Set background position.
        float x = Settings.ScreenWidth / 2  - 111;
        float y = Settings.ScreenHeight / 2 - 60;
                
    	// Draw background.
    	mMain.getImage(8).draw(g, x, y);
    
    	// Set menu position.
		x = Settings.ScreenWidth / 2 - font.getStringWidth("Music Off") / 2;
        y += Settings.ScreenHeight / 30 + 31;
    	   	
    	// Loop through menu items.
    	mOptions = 2;   	
        for (int i = 0; i < mOptions; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Get the settings.
            String music = Settings.Music == true ? "On" : "Off";
            String sound = Settings.Sound == true ? "On" : "Off";

                        
            // Draw menu items.
            if (i == 0)
                font.drawString(g, sel, "Music " + music, x, y);
            else if (i == 1)
            	font.drawString(g, sel, "Sound " + sound, x, y + 20);

        }
           
    }   
    
    /**
     * Draws the paused screen.
     * @param g Graphics context.
     */
    private void drawPaused(Object g) {
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);

        // Set background position.
        float x = Settings.ScreenWidth / 2  - 111;
        float y = Settings.ScreenHeight / 2 - 60;
                
    	// Draw background.
    	mMain.getImage(8).draw(g, x, y);
    
    	// Set menu position.
		x = Settings.ScreenWidth / 2 - font.getStringWidth("Continue") / 2;
        y += Settings.ScreenHeight / 30 + 11;
    	   	
    	// Loop through menu items.
    	mOptions = 3;   	
        for (int i = 0; i < 4; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Draw menu items.
            if (i == 0)
                font.drawString(g, sel, "Continue", x, y + 10);
            else if (i == 1)
                font.drawString(g, sel, "Options", x, y + 30);
            else if (i == 2)
                font.drawString(g, sel, "Quit", x, y + 50);
        }
           
    }

    /**
     * Draws points.
     * @param g Graphics context.
     * @param dt Delta time.
     * @param cam Point object.
     */
    public void drawPoints(Object g, float dt, Point cam) {
    	
   		// Loop through points.
        for (int i = 0; i < mPoints.size(); i++) {    

        	 // Increase the points timer.
             mPoints.get(i).timer++;
        	
        	 // Draw points.
             mMain.getGameFont(1).drawString(g, 0, String.valueOf(mPoints.get(i).value), mPoints.get(i).x - cam.x, mPoints.get(i).y - cam.y);

             // Move points up.
             if (mPoints.get(i).timer / dt < 1.25f) {
                 mPoints.get(i).y -= dt / 120;
             } else {
            	 
            	 // Remove points.
                 mPoints.remove(i);
             }
  
        }	
    }

    /**
     * Draws the score.
     * @param g Graphics context.
     * @param amount Amount of points.
     * @param x X coordinate
     * @param y Y coordinate
     */     
    public void drawScore(Object g, int amount, int x, int y) {   
    	
    	// Convert number into digits.
    	int digit[] = Numbers.convertNumberToDigit(amount, 8);
    	
    	// Draw score.
    	mMain.getGuiSprite(2).drawFrame(g, 2, x, y - 1);
    	mMain.getGuiSprite(0).drawFrame(g, digit[0], x + 50, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[1], x + 58, y + 2);   
    	mMain.getGuiSprite(0).drawFrame(g, digit[2], x + 66, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[3], x + 74, y + 2); 	
    	mMain.getGuiSprite(0).drawFrame(g, digit[4], x + 82, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[5], x + 90, y + 2);   
    	mMain.getGuiSprite(0).drawFrame(g, digit[6], x + 98, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[7], x + 106, y + 2); 		
    }	
    
    
    /**
     * Draws the time remaining.
     * @param g Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
    */
    public void drawTime(Object g, int time, int x, int y) {
      	
    	// Convert number into digits.
    	int digit[] = Numbers.convertNumberToDigit(time, 3);
    	
    	mMain.getGuiSprite(2).drawFrame(g, 3, x, y - 1);	  	
    	mMain.getGuiSprite(0).drawFrame(g, digit[0], x + 17, y + 2);
    	mMain.getGuiSprite(0).drawFrame(g, digit[1], x + 25, y + 2);   
    	mMain.getGuiSprite(0).drawFrame(g, digit[2], x + 33, y + 2);
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

		if(mMain.getScreenTransition().getTimer() > 0) {
        
	        // Set the background colour.
			Color color = mMain.getBackground().getColor();
	        gl.glClearColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
	        
	        // Draw license screen.
	        paintComponent(gl, dt); 
		}
     
    	// Draw screen transition.
    	if(mTransition) {
    		
    		// Stop drawing the transition if it is finished.
    		if(mMain.getScreenTransition().isFinished()) {
    			mTransition = false;    	
    		}
  
    		// Calculate the percentage of the screen height.
    		float y = (mMain.getScreenTransition().getTimer() + 32f) / Settings.ScreenHeight;
    		
        	// Fill the screen below the transition graphic.
            gl.glScissor(0, -(int)(y * GLSurfaceViewRenderer.Height) , GLSurfaceViewRenderer.Width, GLSurfaceViewRenderer.Height);
            gl.glEnable(GL10.GL_SCISSOR_TEST);

            gl.glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1.0f);
			
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // (or whatever buffer you want to clear)
            gl.glDisable(GL10.GL_SCISSOR_TEST);  		

            // Draw transition.
            mMain.getScreenTransition().draw(gl, dt);
    	}	       

        // End drawing.
        Grid.endDrawing(gl);    	
                                
        // Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	

    }

    /**
     * Called when a touch screen event was not handled by any of the views under it.
     */
    public void onTouchEvent(float x, float y, boolean pressed) {

   	   	// Update the players controls.
       	if(!Settings.Paused)
       		mMain.getPlayers().get(0).setControl(x, y, pressed);
	
    	// Set the current time.
    	mMain.getControls().setCurrentTime(System.currentTimeMillis());
    	
    	// Calculate the delta time.
    	float dt = mMain.getControls().getDeltaTime();
    	
    	// If enough time has elapsed.
    	if(dt > 180) {
 	
	    	// Update the controls.
		   	mMain.setControl(x, y, pressed);
	    	  	
		   	// Notify the panel that the controls have changed.
	    	if(Settings.Paused || mMain.getControls().undo)
	    		controlInput();
	    	
	    	if(mState == "pause")
	    		controlInputPause(x, y, 1);
	    	else if(mState == "options")
	    		controlInputOptions(x, y, 1);
		   	
	        // Set the last update time.
	        mMain.getControls().setLastUpdateTime(getCurrentTime()); 
	        
	        // Clear the controls.
    		mMain.getControls().reset();
    	}
    	else
    		mMain.getControls().reset();      	
       	
       	
    }
    
    /**
     * Called when the activity has detected the user's press of the back key.
     */
    public void onBackPressed() {

    	// Set the current time.
    	mMain.getControls().setCurrentTime(System.currentTimeMillis());
    	
    	// Calculate the delta time.
    	float dt = mMain.getControls().getDeltaTime();
    	
    	// If enough time has elapsed.
    	if(dt > 180) {
 	
	    	// Update the controls.
		   	mMain.getControls().undo = true;
	    	  	
		   	// Notify the panel that the controls have changed.
    		controlInput();
		   	
	        // Set the last update time.
	        mMain.getControls().setLastUpdateTime(getCurrentTime()); 
	        
	        // Clear the controls.
    		mMain.getControls().reset();
    	}
    	else
    		mMain.getControls().reset();     	

    }

    /**
     * Occurs when the controller input has changed.
     */
    public void controlInput() {
    	
    	// Up.   	
    	if(mMain.getControls().up) {
            
    		// Next menu item.
    		if (mSelectedOption > 0)
    			mSelectedOption--;    

    	}
    	
    	// Down.
    	if(mMain.getControls().down) {
            
    		// Previous menu item.
   			if (mSelectedOption < mOptions - 1) 
   				mSelectedOption++;   
    	} 
    	
    	// Undo.
    	if(mMain.getControls().undo || (mState == "game" && !Settings.Paused)) {
    		
    		if(mState == "game") {
    			
    			Settings.Paused = true;
    			mState = "pause";
    			return;
    		}
    		else if(mState == "pause") {
    			Settings.Paused = false;
    			mState = "game";
    		}
    		else if(mState == "options") {
    			mState = "pause";
    		}
     		
    	}  	

    	// Button 1.
    	if(mMain.getControls().jump) {
    		
    		if(mState == "pause") {
    		
				if (mSelectedOption == 0) {
					 
					// Return to editor.
	                mState = "game";
	                Settings.Paused = false;
	                 
	            } else if (mSelectedOption == 1) {
	
	            	// Settings.
	                mState = "options";
	                mSelectedOption = 0;
	                 
	
	            } else if (mSelectedOption == 2) {
	
	    			// Return to title screen.
	            	mMain.setLevelPath("0.0.0.lvl");
	    			Settings.State = "load level";
	    			
	            }
				
    		} else if(mState == "options") {
    			
    			if(mSelectedOption == 0) {
    				
    				// Enable music.
    				Settings.Music = !Settings.Music;   
    				    				
    				if(!Settings.Music) 
    					mMain.getMusicPlayer().stop();
    				else
    					mMain.getMusicPlayer().play("snd/" + mMain.getLevel().getHeader().music + ".mp3");
		
    			}
    			else if(mSelectedOption == 1)  {
    				
    				// Enable sound.
    				Settings.Sound = !Settings.Sound;   
    				
    			}
    		}
    	}
    }
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputPause(float x, float y, int button) {

		double x1, x2, y1, y2;
		float w = (float)GLSurfaceViewRenderer.Width;
		float h =  (float)GLSurfaceViewRenderer.Height;

		x = (float)(x / w * Settings.ScreenWidth);
		y = (float)(y / h * Settings.ScreenHeight);

		Log.i("Controls", "x="+ x + " y=" + y);
		
		// Continue.
		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
			x1 = 192;
			x2 = 288;
			y1 = 100;
			y2 = 120;
		} else {
			x1 = 154;
			x2 = 246;
			y1 = 83;
			y2 = 102;
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {

			// Return to editor.
            mState = "game";
            Settings.Paused = false;
            
		} 
		
		// Options.
		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
			y1 = 120;
			y2 = 140;
		} else {
			y1 = 102;
			y2 = 122;
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {

        	// Settings.
            mState = "options";
            mSelectedOption = 0;
			
		} 		
		
		// Quit.
		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
			y1 = 140;
			y2 = 160;
		} else {
			y1 = 122;
			y2 = 142;
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			
			// Return to title screen.
			mMain.setLevelPath("0.0.0.lvl");
			Settings.State = "load level";
			
		}
		
		if(button == 2) {
			Settings.Paused = false;
			mState = "game";
			mSelectedOption = 0;
		}
		
		
    }
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputOptions(float x, float y, int button) {

		double x1, x2, y1, y2;
		float w = (float)GLSurfaceViewRenderer.Width;
		float h =  (float)GLSurfaceViewRenderer.Height;

		x = (float)(x / w * Settings.ScreenWidth);
		y = (float)(y / h * Settings.ScreenHeight);

		Log.i("Controls", "x="+ x + " y=" + y);

		// Sound.
		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
			x1 = 188;
			x2 = 290;
			y1 = 109;
			y2 = 130;
		} else {
			x1 = 150;
			x2 = 249;
			y1 = 91;
			y2 = 112;
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			
			Settings.Music = !Settings.Music;
			
			if(!Settings.Music) 
				mMain.getMusicPlayer().stop();
			else
				mMain.getMusicPlayer().play("snd/" + mMain.getLevel().getHeader().music + ".mp3");
   
			mSelectedOption = 0;
		} 

		// Music.
		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
			y1 = 130;
			y2 = 152;
		} else {
			y1 = 112;
			y2 = 135;
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			Settings.Sound = !Settings.Sound;
			mSelectedOption = 1;
		} 
	
    }

    /**
     * Sets the game state.
     * @param state State value.
     */
    public void setState(String state) {
    	mState = state;
    }
    
    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl) {
    	
    	// Destroy entities.
    	for(IEntity entity : mEntity) {
    		if(entity != null)
    			entity.destroy(gl);    		
    	}
    }
    
}
