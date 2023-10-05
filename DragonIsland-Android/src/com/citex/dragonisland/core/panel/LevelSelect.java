package com.citex.dragonisland.core.panel;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.android.drawing.Grid;
import com.citex.dragonisland.core.drawing.Color;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.GameData;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.Drawing;
import com.citex.dragonisland.core.util.Numbers;
import com.citex.dragonisland.java.GamePanel;

/**
 * This class displays the level select screen.
 * @author Lawrence Schmid
 */
public class LevelSelect extends Timing {

	/** Main thread. */
    private Main mMain;	

    /** Show screen transition. */
    private boolean mTransition;
    
    /** Current world selection. */
    private int mSelectedWorld;
    
    /** Number of levels unlocked. */
    private int mUnlockedLevels;
    
	/**
	 * Initialises a LevelSelect object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
	 */
	public LevelSelect(Main main, Object surface, boolean transition) {
		
		// Initialise the title screen.
		mMain = main;
    	mMain.getControls().reset();
    	mTransition = transition;
		Settings.State = "level select";	
		Settings.Paused = false;
		mSelectedWorld = 1;
		
    	// Get the save game data.
		try {
			mMain.setSaveFile(new SaveFile(Settings.InternalStorageFolder));
		}
		catch(Exception e) {};
	
	}
	
	/**
	 * Paints the graphics.
	 * @param g Graphics context.
	 * @param dt Delta time.
	 */
    public void paintComponent(Object g, float dt) {

    	// Draw background.
    	Point cam = mMain.getCamera();
    	cam.x = mMain.getCurrentPlayer().getLeft() - Settings.ScreenWidth / 2;
		cam.y = mMain.getCurrentPlayer().getDown() - Settings.ScreenHeight / 3;   	
    	cam = mMain.getBackground().draw(g, dt, cam);
	
    	// Get game font.
    	GameFont font = mMain.getGameFont(0);   
    	
		// Set title position.
		float x = Settings.ScreenWidth / 2 - font.getStringWidth("World " + mSelectedWorld + " - Level Select") / 2;
        float y = 21;
    	
        // Draw title.
        font.drawString(g, 0, "World " + mSelectedWorld + " - Level Select", (int)x, (int)y);

        // Get the total number of worlds. 
        int totalWorlds = mMain.getGameFolder().getWorlds().size();
        
        // Get the total number levels in the world.
        int totalLevels = mMain.getGameFolder().getWorlds().get(mSelectedWorld - 1).getLevels();
        
        // Get levels unlocked.
        GameData gameData = mMain.getSaveFile().getSaveGame(mMain.getGameFolderIndex());
        
        // Set number of unlocked levels.
        if(gameData.world > mSelectedWorld)
        	mUnlockedLevels = totalLevels;
        else
        	mUnlockedLevels = gameData.level;	
        
        // Level select.
        if(Settings.LevelSelect)
        	mUnlockedLevels = 8;
        	
        // Draw level frames.
        if(Settings.ScreenWidth > 400)
        	x = Settings.ScreenWidth / 2 - (75 * 4 + 25 * 3) / 2; 
        else 
        	x = Settings.ScreenWidth / 2 - (75 * 4 + 15 * 3) / 2;   
        
        // Add padding.
        int padding;
        if(Settings.ScreenWidth > 400)
        	padding = 100;
        else
        	padding = 90;
        
        y = 50;	
        int pad = 0;
        for(int i = 1; i <= totalLevels; i++) {
        	
        	// New row.
        	if(i == 5 && i > 0) {
        	
                x = Settings.ScreenWidth / 2 - (75 * 4 + 25 * 3) / 2; 
                if(Settings.ScreenWidth <= 400)
                    x = Settings.ScreenWidth / 2 - (75 * 4 + 15 * 3) / 2;   	
        		
        		y = 150;
        		pad = 0;
        	}	

        	mMain.getImage(9).draw(g, x + padding * pad, y);
        	pad++;
        }
      	
        // Draw level number.
        y = 83; 	       
        for(int i = 1; i <= totalLevels; i++) {
     	
        	// New row.
        	if(i % 5 == 0) {

        		if(Settings.ScreenWidth <= 400)
                    x = Settings.ScreenWidth / 2 - (75 * 4 + 15 * 3) / 2;
                else 
            		x = Settings.ScreenWidth / 2 - (75 * 4 + 25 * 3) / 2; 
                	
        		y = 183;
        	}
 
            // Draw level number.
            if(i < mUnlockedLevels + 1)
            	font.drawString(g, 0, Integer.toString(i), x + 34, y); 
        	
            // Move to next level number.
        	x += padding;

        }


        // Draw padlock.
        y = 80;
        x = Settings.ScreenWidth / 2 - (75 * 4 + 25 * 3) / 2; 
        if(Settings.ScreenWidth <= 400)
            x = Settings.ScreenWidth / 2 - (75 * 4 + 15 * 3) / 2;       
        
        for(int i = 1; i <= totalLevels; i++) {
        	
        	// New row.
        	if(i % 5 == 0) {

        		if(Settings.ScreenWidth <= 400)
                    x = Settings.ScreenWidth / 2 - (75 * 4 + 15 * 3) / 2;
                else 
            		x = Settings.ScreenWidth / 2 - (75 * 4 + 25 * 3) / 2; 
        		
        		y = 180;
        	}
    	
            // Draw padlock.
        	if(i >= mUnlockedLevels + 1)
        		mMain.getImage(10).draw(g, x + 30, y);
        	
            // Move to next level number.
        	x += padding;
	
        }
        
        // Draw world selection arrows.
        if(mSelectedWorld < totalWorlds) {
        	
        	// Forward arrow.
            if(Settings.ScreenWidth <= 400)
            	mMain.getImage(11).draw(g, 'r', Settings.ScreenWidth - 23, Settings.ScreenHeight / 2 + 10);
            else
            	mMain.getImage(11).draw(g, 'r', Settings.ScreenWidth - 34, Settings.ScreenHeight / 2 - 6);
        }
        
    	// Back arrow.
    	if(mSelectedWorld > 1) {
        	if(Settings.ScreenWidth <= 400)
        		mMain.getImage(11).draw(g, 6, Settings.ScreenHeight / 2 + 10);
        	else
        		mMain.getImage(11).draw(g, 17, Settings.ScreenHeight / 2 - 6);
    	}

        // Draw back button.
		if(Settings.ScreenWidth > 400) {
			x = Settings.ScreenWidth / 2 - font.getStringWidth("Back") / 2;
			font.drawString(g, 0, "Back", x, Settings.ScreenHeight - 27);	
		}
       
    }
    
    /**
     * Draw the current frame.
     * @param gl OpenGL context
     */
    public void drawFrame(Graphics g) {   
    	
    	// Set the current time.
    	setCurrentTime(System.currentTimeMillis());

    	// Calculate the delta time.
    	float dt = getDeltaTime();
		    		
		// Set the background colour.
		if(mMain.getScreenTransition().getTimer() > 0) {
    		
			Color color = mMain.getBackground().getColor();
        	g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
			g.fillRect(0, 0, Settings.ScreenWidth, Settings.ScreenHeight);

			// Paint the component.
    		paintComponent(g, dt);
		}
			    	
    	// Draw screen transition.
    	if(mTransition) {
    		
    		// Stop drawing the transition if it is finished.
    		if(mMain.getScreenTransition().isFinished()) {
    			mTransition = false;    	
    		}
    		
        	// Fill the screen below the transition graphic.
        	g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, (int)mMain.getScreenTransition().getTimer() + 32, 480, 272);    		
    		
            // Draw transition.
    		mMain.getScreenTransition().draw(g, dt);
    	}	
	
        // Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	    
    	
    }
        
    /**
     * Draw the current frame.
     * @param gl OpenGL context
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
     * Called when a key was pressed down and not handled by any of the views inside of the activity.
     * @param event Description of the key event.
     */
    public void onKeyDown(int keyCode, boolean pressed) {
    	    	
    	// Update the controls.
	   	mMain.getControls().setControl(keyCode, pressed);	

	   	// Notify the panel that the controls have changed.
    	controlInput(); 
    	
    }
    
    /**
     * Occurs when the controller input has changed.
     */
    public void controlInput() {
    	
    	// Jump.
    	if(mMain.getControls().jump) {
    		
			mMain.setLevelPath(mSelectedWorld + "." + mUnlockedLevels + ".1.lvl");
            Settings.State = "load level";
   			mMain.getSoundEffects().play("option.wav");
    	}
    	
    	// Undo.
    	if(mMain.getControls().undo) {
			
    		// Return to title screen.
        	mMain.setLevelPath("0.0.0.lvl");
			Settings.State = "load level";	
			
    	}
    	
    }
        
    /**
     * Called when a touch screen event was not handled by any of the views under it.
     * @param event The touch screen event being processed.
     */
    public void onTouchEvent(float x, float y, boolean pressed) {
    	
    	// Set the current time.
    	mMain.getControls().setCurrentTime(System.currentTimeMillis());
    	
    	// Calculate the delta time.
    	float dt = mMain.getControls().getDeltaTime();
    	
    	// If enough time has elapsed.
    	if(dt > 180) {
 	
	    	// Update the controls.
		   	mMain.setControl(x, y, pressed);
	    	  	
		 	// Notify the thread that there was a touch event.
    		controlInput(x, y);
		   	
	        // Set the last update time.
	        mMain.getControls().setLastUpdateTime(getCurrentTime()); 
	        
	        // Clear the controls.
    		mMain.getControls().reset();
    	}
    	else
    		mMain.getControls().reset();      	
	
    } 

    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mousePressed(MouseEvent e) {
 
	 	// Notify the thread that there was a mouse pressed event.
    	controlInput(e.getX(), e.getY());
    	
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
	    	
			// Return to title screen.
        	mMain.setLevelPath("0.0.0.lvl");
			Settings.State = "load level";
		   	
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
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void controlInput(float x, float y) {
    	
    	// Initialise coordinates.
		float x1, x2, y1, y2;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;
		
		// Level 1.
		if(Settings.ScreenWidth > 400) {
			x1 = (54 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (128 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (29 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (103 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		y1 = (51 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		y2 = (125 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			mMain.setLevelPath(mSelectedWorld + ".1.1.lvl");
            Settings.State = "load level";
		} 

		// Level 2.
		if(Settings.ScreenWidth > 400) {
			x1 = (154 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (228 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (119 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (193 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 1) {			
            	mMain.setLevelPath(mSelectedWorld + ".2.1.lvl");
            	Settings.State = "load level";
            }
		} 

		// Level 3.
		if(Settings.ScreenWidth > 400) {
			x1 = (254 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (328 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (209 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (283 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 2) {			
            	mMain.setLevelPath(mSelectedWorld + ".3.1.lvl");
            	Settings.State = "load level";
            }
		} 

		// Level 4.
		if(Settings.ScreenWidth > 400) {
			x1 = (354 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (428 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (299 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (373 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 3) {			
            	mMain.setLevelPath(mSelectedWorld + ".4.1.lvl");
            	Settings.State = "load level";
            }
		} 		

		// Level 5.
		if(Settings.ScreenWidth > 400) {
			x1 = (54 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (128 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (29 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (103 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		y1 = (151 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		y2 = (225 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 4) {			
            	mMain.setLevelPath(mSelectedWorld + ".5.1.lvl");
            	Settings.State = "load level";
            }
		} 
		
		// Level 6.
		if(Settings.ScreenWidth > 400) {
			x1 = (154 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (228 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (119 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (193 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 5) {			
            	mMain.setLevelPath(mSelectedWorld + ".6.1.lvl");
            	Settings.State = "load level";
            }
		} 	
				
		// Level 7.
		if(Settings.ScreenWidth > 400) {
			x1 = (254 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (328 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (209 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (283 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 6) {			
            	mMain.setLevelPath(mSelectedWorld + ".7.1.lvl");
            	Settings.State = "load level";
            }
		} 
		
		// Level 8.
		if(Settings.ScreenWidth > 400) {
			x1 = (354 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (428 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = (299 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (373 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
            if(mUnlockedLevels > 7) {			
            	mMain.setLevelPath(mSelectedWorld + ".8.1.lvl");
            	Settings.State = "load level";
            }
		}
		
		if(Settings.State == "load level")
			mMain.getSoundEffects().play("option.wav");

		// Next world.
		if(Settings.ScreenWidth > 400) {
			x1 = (429 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (Settings.ScreenWidth / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (126 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (150 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			x1 = (374 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (Settings.ScreenWidth / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (126 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (151 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			
			if(mMain.getSaveFile().getSaveGame(0).world > mSelectedWorld) {
				mSelectedWorld++;
				System.out.println("World" + mSelectedWorld);
			}
		} 

		// Previous world.
		if(Settings.ScreenWidth > 400) {
			x1 = 0;
			x2 = (53 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		} else {
			x1 = 0;
			x2 = (28 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
		}
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			
			if(mSelectedWorld > 1) {
				mSelectedWorld--;
				System.out.println("World" + mSelectedWorld);
			}
		}
		
		// Back button.
		if(Settings.ScreenWidth > 400) {
			
			x1 = (205 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (280 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (235 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				
    			// Return to title screen.
            	mMain.setLevelPath("0.0.0.lvl");
    			Settings.State = "load level";
    			
			}
			
		}
	
    }
    
}