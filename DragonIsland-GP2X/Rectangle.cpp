#include "Rectangle.h"

/**
 * Constructs the Rectangle
 * @param x The x coordinate
 * @param y The y coordinate
 * @param w The width
 * @param h The height
 */
Rectangle::Rectangle(int x, int y, int w, int h)
{
	this->x = x;
	this->y = y;
	this->w = w;
	this->h = h;
}

/**
 * Default constructor
 */
Rectangle::Rectangle()
{

}

/**
 * Determines whether or not this Rectangle and the specified Rectangle intersect. 
 * Two rectangles intersect if their intersection is nonempty.
 * @param r The specified Rectangle 
 * @return True if the specified Rectangle and this Rectangle intersect;
 */
bool Rectangle::intersects(Rectangle* r)
{
	//http://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
	if (x < r->x + r->w && x + w > r->x &&    
		y < r->y + r->h && y + h > r->y)
		return true;
	else
		return false;
}

/**
 * Moves this Rectangle to the specified location.
 * @param x The X coordinate of the new location
 * @param y The Y coordinate of the new location
 */
void Rectangle::setLocation(int x, int y)
{
	this->x = x;
	this->y = y;
}


/**
 * Sets the bounding Rectangle of this Rectangle to the specified x, y, width, and height. 
 * @param x The new X coordinate for the upper-left corner of this Rectangle
 * @param y The new Y coordinate for the upper-left corner of this Rectangle
 * @param width The new width for this Rectangle
 * @param height The new height for this Rectangle
 */
void Rectangle::setBounds(int x, int y,  int width,  int height)
{
	this->x = x;
	this->y = y;
	this->w = width;
	this->h = height;
}


/*
 * Sets the size of this Rectangle to the specified width and height. 
 * @param width The new width for this Rectangle
 * @param height The new height for this Rectangle
*/
void Rectangle::setSize(int width, int height)
{
	this->w = width;
	this->h = height;
}

