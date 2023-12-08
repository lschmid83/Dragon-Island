package com.citex.dragonisland.core.drawing;

import javax.microedition.khronos.opengles.GL10;

/**
 * IBUfferedImage.java
 * Interface for drawing Java Image and Android GLSprite objects.
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

public interface IBufferedImage {

    /**
     * Gets the width of the image.
     * @return Width of the image.
     */
    public int getWidth();

    /**
     * Gets the height of the image.
     * @return Height of the image.
     */
    public int getHeight();

    /**
     * Draws the image at the x,y coordinates.
	 * @param gl Graphics context,
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void draw(Object gl, float x, float y); 
    
    /**
     * Draws the image at the x,y coordinates flipped horizontally.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void draw(Object g, char direction, float x, float y);   
  
    /**
     * Draws the image at the x,y coordinates flipped horizontally and rotated.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void draw(Object g, char direction, float angle, float x, float y, float centerX, float centerY);
    
    /**
     * Draws the image at the x,y coordinates tiled to a set width and height.
	 * @param gl Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    public void draw(Object gl, float x, float y, float w, float h);    
    
    /**
     * Gets a sub image defined by a specified rectangular region. 
	 * @param gl Graphics context.
	 * @param x - X coordinate of the upper-left corner of the specified rectangular region.
     * @param y - Y coordinate of the upper-left corner of the specified rectangular region.
     * @param w - Width of the specified rectangular region.
     * @param h - Height of the specified rectangular region.
     * @return Object which is the sub image of this IBufferedImage. 
     */
    public IBufferedImage getSubImage(Object gl, int x, int y, int w, int h);
    
    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl);   
    
    
    /**
     * Make a copy of image.
     * @return Cloned object.
     */
    public Object clone();
    
}
