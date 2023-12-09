package com.citex.dragonisland.core.game;

import android.util.Log;

import java.util.ArrayList;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.core.drawing.IBufferedImage;

/**
 * Control.java
 * This class handles keyboard and touch screen control events.
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

		double x1, x2, y1, y2;
		float w = (float)GLSurfaceViewRenderer.Width;
		float h =  (float)GLSurfaceViewRenderer.Height;

		x = (float)(x / w * Settings.ScreenWidth);
		y = (float)(y / h * Settings.ScreenHeight);

		Log.i("Controls", "x="+ x + " y=" + y);

		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {

			// Up.
			x1 = 39;
			x2 = 83;
			y1 = 148;
			y2 = 192;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				up = pressed;
			}
		
			// Down.
			x1 = 39;
			x2 = 83;
			y1 = 217;
			y2 = 256;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				down = pressed;
			}	
	
			// Left.
			x1 = 0;
			x2 = 50;
			y1 = 179;
			y2 = 226;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				left = pressed;
			}
			
			// Right.
			x1 = 73;
			x2 = 114;
			y1 = 181;
			y2 = 226;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				right = pressed;
			}	
			
			// Button 1.
			x1 = 414;
			x2 = 480;
			y1 = 186;
			y2 = 255;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				jump = pressed;
			}	
			
			// Button 2.
			x1 = 358;
			x2 = 411;
			y1 = 186;
			y2 = 255;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
	
				if(Settings.State == "title")
					undo = pressed;
				else
					fire = pressed;
			}
		
		} else {

			// Up.
			x1 = 38;
			x2 = 82;
			y1 = 117;
			y2 = 162;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				up = pressed;
			}
		
			// Down.
			x1 = 38;
			x2 = 82;
			y1 = 186;
			y2 = 225;
			
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				down = pressed;
			}	
	
			// Left.
			x1 = 0;
			x2 = 50;
			y1 = 149;
			y2 = 196;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				left = pressed;
			}
			
			// Right.
			x1 = 74;
			x2 = 110;
			y1 = 149;
			y2 = 196;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				reset();
				right = pressed;
			}	
			
			// Button 1.
			x1 = 334;
			x2 = 400;
			y1 = 166;
			y2 = 226;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
				jump = pressed;
			}	
			
			// Button 2.
			x1 = 300;
			x2 = 343;
			y1 = 166;
			y2 = 226;
			if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
	
				if(Settings.State == "title")
					undo = pressed;
				else
					fire = pressed;
			}

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
