#ifndef __SpritSheet_H__
#define __SpriteSheet_H__

#pragma once

#include "Image.h"
#include <oslib/oslib.h>

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
	SpriteSheet(char* path, int frameWidth, int frameHeight, int frameCount);
	OSL_IMAGE* getFrame(int frameNumber);
	OSL_IMAGE* getFrame(int frameNumber, char direction);
	void drawFrame(int dstX, int dstY, int frameNumber);
	void drawFrame(int dstX, int dstY, int frameNumber, char direction);
    void drawFrame(int dstX, int dstY, int frameNumber, char direction, int angle);
	void drawFrame(int dstX, int dstY, int dstW, int dstH, int frameNumber, char direction);
	~SpriteSheet();

private:
	/** The sprite sheet containing the animation */
	Image* mSpriteSheet; 
    /** The frame dimensions */
	int mFrameWidth, mFrameHeight; 
    /** The sprite sheet dimensions */
    int mSheetWidth, mSheetHeight; 
	/** The number of rows and columns in the sprite sheet */    
	int mRows, mColumns; 
	/** The number of frames in the sprite sheet */    
	int mFrameCount; 
	/** The horizontal direction of the frame */
	bool mDirection;
};

#endif
