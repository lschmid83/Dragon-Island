#ifndef __SpritSheet_H__
#define __SpriteSheet_H__

#pragma once

#include "Image.h"
#include <SDL/SDL_rotozoom.h> //add -lSDL_gfx to compiler options (http://www.gp32x.com/board/index.php?/topic/31888-problem-with-sdl-gfx/)

/**
 * @class SpriteSheet
 * @brief This class loads an image containing a sprite sheet and buffers the frames of animation
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class SpriteSheet
{
public:
	SpriteSheet::SpriteSheet(char* path, int frameWidth, int frameHeight, int frameCount);
	SpriteSheet::~SpriteSheet();
	SDL_Surface* SpriteSheet::getFrameFromSheet(int frameNumber);
	SDL_Surface* SpriteSheet::getFrame(int frameNumber);
	SDL_Surface* SpriteSheet::getFrame(int frameNumber, char direction);
	void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int frameNumber);
	void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int frameNumber, char direction);
	void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int dstW, int dstH, int frameNumber, char direction);

private:
	/** The sprite sheet containing the animation */
	Image* mSpriteSheet; 
	/** The buffered frames of animation */
    SDL_Surface* mFrames; 
    /** The frame dimensions */
	int mFrameWidth, mFrameHeight; 
    /** The sprite sheet dimensions */
    int mSheetWidth, mSheetHeight; 
	/** The number of rows and columns in the sprite sheet */    
	int mRows, mColumns; 
	/** The number of frames in the sprite sheet */    
	int mFrameCount; 

};

#endif
