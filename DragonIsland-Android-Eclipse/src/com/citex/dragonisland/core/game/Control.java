package com.citex.dragonisland.core.game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.core.drawing.IBufferedImage;

/**
 * This class handles keyboard and touch screen control events.
 * @author Lawrence Schmid.
 */
public class Control extends Timing {
	       
    /** Up. */
    public boolean up;

    /** Down/ */
    public boolean down;
  
	/** Left. */
    public boolean left;
    
    /** Right. */
    public boolean right;   
     
    /** Button 1 (jump). */
    public boolean jump;  
    
    /** Button 2 (fire). */
    public boolean fire;    
    
    /** Undo. */
    public boolean undo;
    
    /** Direction held elapsed time. */
    public int directionHeld;

    /**
     * Sets controls from a touch screen event.
     * @param x X coordinate.
     * @param y Y coordinate
     * @param pressed True if the screen is pressed; otherwise false.
     */
    public void setControl(float x, float y, boolean pressed) {
   	
     	// Initialise coordinates.
		float x1, x2, y1, y2;    	
    	
		// Recalculate coordinates with 0,0 starting at top left.
		x = x / GLSurfaceViewRenderer.Width * Settings.ScreenWidth;
		y = y / GLSurfaceViewRenderer.Height * Settings.ScreenHeight;

		if(Settings.ScreenWidth > 400) {
		
			// Up.
			x1 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (145f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (195f / Settings.ScreenHeight) * Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				up = pressed;
			}
		
			// Down.
			x1 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (221f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				down = pressed;
			}	
	
			// Left.
			x1 = 0;
			x2 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (145f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				left = pressed;
			}
			
			// Right.
			x1 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (240f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (145f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				right = pressed;
			}	
			
			// Button 1.
			x1 = (416f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = Settings.ScreenWidth;
			y1 = (145f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				jump = pressed;
			}	
			
			// Button 2.
			x1 = (350f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (416f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (145f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
	
				if(Settings.State == "title")
					undo = pressed;
				else
					fire = pressed;
			}
		
		} else {
			
			// Up.
			x1 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (105f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (163f / Settings.ScreenHeight) * Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				up = pressed;
			}
		
			// Down.
			x1 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (187f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				down = pressed;
			}	
	
			// Left.
			x1 = 0;
			x2 = (50f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (105f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				left = pressed;
			}
			
			// Right.
			x1 = (76f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (200f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (105f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				right = pressed;
			}	
			
			// Button 1.
			x1 = (336f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = Settings.ScreenWidth;
			y1 = (105f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				jump = pressed;
			}	
			
			// Button 2.
			x1 = (260f / Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (336f / Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (105f / Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = Settings.ScreenHeight;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
	
				if(Settings.State == "title")
					undo = pressed;
				else
					fire = pressed;
			}

		}

    }
    
    /**
     * Sets controls from a key code.
     * @param event KeyCode associated with the key in this event.
     * @parma pressed True if the key is pressed; otherwise false.
     */
    public void setControl(int keyCode, boolean pressed) {
    	
    	// Set controls from key code.   	
    	if(keyCode == KeyEvent.VK_UP) {
    		up = pressed;    		
    	} else if (keyCode == KeyEvent.VK_DOWN) {
    		down = pressed;
    	} else if(keyCode == KeyEvent.VK_LEFT) {
    		left = pressed;    		
    	} else if(keyCode == KeyEvent.VK_RIGHT) {
    		right = pressed;
    	} else if(keyCode == KeyEvent.VK_ENTER && (Settings.State == "title" || Settings.State == "level select" || Settings.State == "editor" || (Settings.Paused && Settings.State == "game"))) {
    		jump = pressed;
    	} else if(keyCode == KeyEvent.VK_ESCAPE) {
    		undo = pressed;
    	} else if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S || keyCode ==KeyEvent.VK_SPACE) {
    		jump = pressed; 	   	
    	} else if(keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL) {
    		fire = pressed;
    	}

    }
       
    /**
     * Resets controls.
     */
    public void reset() {
    	
		up  = false;
		left = false;
		down = false;
		right = false; 
		
		if(Settings.State != "game" || Settings.Paused) {
			fire = false;
			jump = false;
			undo = false;
		}
	}

    /**
     * Sets the direction button held elapsed time.
     */
    public void setDirectionHeld(float dt) {
    	
        // Set amount of time the direction button has been pressed.
        if (left || right) {
            
    		// Run.
            if (directionHeld < 200) {
           		directionHeld+= dt / 10;
            }	
        } else {
        	
        	// Released direction button.
            if (directionHeld > 0) {
                directionHeld -= dt / 10;
            } else {
                directionHeld = 0;
            }
        }
    }
    
    /** 
     * Gets a list of accepted key codes A-Z, 0-9
     * @param keyCode Integer representing key code.
     */
    public static boolean isLetterOrDigit(int keyCode) {
    	
    	if ((keyCode >= 48 && keyCode <= 57) ||	keyCode >= 65 && keyCode <= 90)
    	    return true;
    	else
    		return false; 
    }
        
    /**
     * Draws touch screen controls.
     * @param g Graphics context.
     * @param images ArrayList of objects which implement IBufferedImage.
     */
    public void drawTouchScreen(Object g, ArrayList<IBufferedImage> images) {
    	
		// D-pad.
		images.get(3).draw(g, 25, Settings.ScreenHeight - 100f);

		// Button 1.
		images.get(4).draw(g, Settings.ScreenWidth - 60f, Settings.ScreenHeight - 65f);

		// Button 2.
		images.get(5).draw(g, Settings.ScreenWidth - 112f, Settings.ScreenHeight - 65f);
	
    }

}
