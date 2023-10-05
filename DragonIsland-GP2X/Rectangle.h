#ifndef __Rectangle_H__
#define __Rectangle_H__

/**
 * @class Rectangle
 * @brief This class stores a x,y,w,h coordinates
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Rectangle
{
public:
 	Rectangle::Rectangle(int x, int y, int w, int h);
	Rectangle::Rectangle();
	bool Rectangle::intersects(Rectangle* r);
	void Rectangle::setLocation(int x, int y);
	void Rectangle::setBounds(int x, int y,  int width,  int height);
	void Rectangle::setSize(int width, int height);
	/** The x coordinate */
 	int x;
	/** The y coordinate */
 	int y;
	/** The width */
 	int w;
	/** The height */
 	int h;
};

#endif
