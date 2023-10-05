package com.citex.dragonisland.core.drawing;

/**
 * This class represents a rectangle and provides methods to test for intersections.
 * @author Lawrence Schmid
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
