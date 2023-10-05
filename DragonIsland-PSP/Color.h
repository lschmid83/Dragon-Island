#ifndef __Color_H__
#define __Color_H__

/**
 * @class Color
 * @brief This class stores RGB color information
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Color
{
public:
 	Color(int red, int green, int blue);
	Color();
	int getRed();
 	int getGreen();
 	int getBlue();

private:
	/** The red intensity */
 	int mRed;
	/** The green intensity */
 	int mGreen;
	/** The blue intensity */
 	int mBlue;
};

#endif
