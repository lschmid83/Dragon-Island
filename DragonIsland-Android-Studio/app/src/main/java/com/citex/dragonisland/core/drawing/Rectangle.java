package com.citex.dragonisland.core.drawing;

/**
 * Rectangle.java
 * This class represents a rectangle and provides methods to test for intersections.
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

public class Rectangle {

	/** X coordinate. */
	public float x;
	
	/** Y coordinate. */
	public float y;
	
	/** Width. */
	public float width;
	
	/** Height. */
	public float height;

	/**
	 * Initialises Rectangle object.
	 * @param x X coordinate of the upper-left corner.
	 * @param y Y coordinate of the upper-left corner.
	 * @param width Width of the rectangle.
	 * @param height Height of the rectangle.
	 */
	public Rectangle(float x, float y, float width, float height) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	/**
	 * Determines whether or not this Rectangle and the specified Rectangle intersect.
	 * @param rect Rectangle object.
	 * @return True if the specified Rectangle and this Rectangle intersect; otherwise false.
	 */
	public boolean intersects(Rectangle rect) {

		if (x < rect.x + rect.width && x + width > rect.x &&    
			y < rect.y + rect.height && y + height > rect.y)
				return true;
			else
				return false;
	
	}
	
}
