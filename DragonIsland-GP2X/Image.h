#ifndef __Image_H__
#define __Image_H__

#pragma once

#include "Color.h"
#include <SDL/SDL_image.h>

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
	Image::Image(char* path);
	Image::Image();
	SDL_Surface* Image::getImage();
	SDL_Surface* Image::getSubimage(int x, int y, int w, int h);
	int Image::getWidth();
	int Image::getHeight();
	void Image::draw(SDL_Surface *g, int x, int y);
	Image::~Image();
	Color* Image::get_pixel32(int x, int y);

private:
	/** The buffered image with alpha channel */
	SDL_Surface* mImage;
};

#endif
