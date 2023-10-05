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
 	Rectangle(int x, int y, int w, int h);
	Rectangle();
	bool intersects(Rectangle* r);
	void setLocation(int x, int y);
	void setBounds(int x, int y,  int width,  int height);
	void setSize(int width, int height);
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
