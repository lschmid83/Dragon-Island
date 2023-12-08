package com.citex.dragonisland.core.drawing;

/**
 * Color.java
 * This class stores RGBA color information.
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

public class Color {
	
	/** Red intensity. */
    private int mRed; 
    
    /** Green intensity. */
    private int mGreen; 
    
    /** Blue intensity. */
    private int mBlue; 
    
    /** Alpha intensity. */
    private int mAlpha; 

    /**
     * Initialises a Color object with an alpha channel.
     * @param red Red intensity.
     * @param green Green intensity.
     * @param blue Blue intensity.
     * @param alpha Alpha intensity.
     */
    public Color(int red, int green, int blue, int alpha) {
    	mRed = red;  
        mGreen = green;   
        mBlue = blue;    
        mAlpha = alpha;
    }

    /**
     * Initialise a color object.
     * @param red Red intensity.
     * @param green Green intensity.
     * @param blue Blue intensity.
    */
    public Color(int red, int green, int blue) {
        mRed = red;
        mGreen = green;
        mBlue = blue;
    }

    /**
     * Gets the red intensity.
     * @return Red intensity.
     */
    public int getRed() {
        return mRed;
    }

    /**
     * Gets the green intensity.
     * @return Green intensity.
     */
    public int getGreen() {
        return mGreen;
    }

    /**
     * Gets the blue intensity
     * @return Blue intensity.
     */
    public int getBlue() {
        return mBlue;
    }

    /**
     * Gets the alpha intensity.
     * @return Alpha intensity
    */
    public int getAlpha() {
        return mAlpha;
    }
}
