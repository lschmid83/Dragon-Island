package com.citex.dragonisland.core.util;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.applet.Main;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.java.GameFrame;

/** 
 * Helper functions for numerics.
 * @author Lawrence Schmid
 */
public class Numbers {

	/**
	 * Converts a number into separate digits.
	 * @param number Integer number.
	 * @param digits Number of digits in number.
	 * @return Integer array of digits.
	 */
	public static int[] convertNumberToDigit(int number, int digits) {
		
        int tmp = number;
        int digit[] = new int[digits];
        
        // Divide number by mod 10.
        for (int i = digits - 1; i >= 0; i--) {
            digit[i] = tmp % 10; 
            tmp = tmp / 10;
        }
        return digit;
	}
	
	/**
	 * Clamps a number to a min and max range.
	 * @param value Number to clamp.
	 * @param min Min value.
	 * @param max Max value.
	 * @return Clamped value.
	 */
	public static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
	
	/**
	 * Gets a normalised point to the map grid size.
	 * @param x X coordinate.
	 * @param y Y Coordinate.
	 * @return Normalised point.
	 */
	public static Point normalisePoint(float x, float y) {	
        return new Point(((int)(x /16) * 16), ((int)(y /16) * 16));
	}
		
	/** 
	 * Gets mouse / touchscreen coordinates normalised to screen size.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @return Normalised coordinates to screen size.
	 */
	public static Point getNormalisedCoordinate(float x, float y) {
		
		// Recalculate coordinates with 0,0 starting at top left.
		if(Settings.Mode == GameMode.ANDROID) {
			
			x = x / GLSurfaceViewRenderer.Width * Settings.ScreenWidth;
			y = y / GLSurfaceViewRenderer.Height * Settings.ScreenHeight;
			
		} else if(Settings.Mode == GameMode.JAVA) {
			
	    	x = x / ((float)GameFrame.Width - 14) * (Settings.ScreenWidth);
	    	y = y / ((float)GameFrame.Height - 38) * (Settings.ScreenHeight);	
	    	
		} else if(Settings.Mode == GameMode.APPLET) {
			
	    	x = x / ((float)Main.Width) * (Settings.ScreenWidth);
	    	y = y / ((float)Main.Height) * (Settings.ScreenHeight);	
		}
		
		// Return the normalised coordinates.
		return new Point(x, y);
		
	}
	
}