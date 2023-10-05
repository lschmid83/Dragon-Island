#ifndef __Image_H__
#define __Image_H__

#pragma once

#include "Color.h"
#include <oslib/oslib.h>

/**
 * @class Image
 * @brief This class that wraps up some common functionality for using images
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Image
{
public:
	Image(char* filename, short pixelFormat);
	Image();
	OSL_IMAGE* getImage();
	OSL_IMAGE* getSubimage(int x, int y, int w, int h);
	int getWidth();
	int getHeight();
	void draw(int x, int y);
	~Image();
	Color* get_pixel32(int x, int y);

private:
	/** The buffered image with alpha channel */
	OSL_IMAGE* mImage;
};

#endif
