#include "SpriteSheet.h"

/**
 * Constructs the SpriteSheet
 * @param path The path of the image file
 * @param frameWidth The frame width
 * @param frameHeight The frame height
 * @param frameCount The frame count
 */
SpriteSheet::SpriteSheet(char* path, int frameWidth, int frameHeight, int frameCount)
{
	this->mFrameWidth = frameWidth;
    this->mFrameHeight = frameHeight;
    this->mFrameCount = frameCount;
	mDirection = 'l';

	mSpriteSheet = new Image(path, OSL_PF_4444); 
	if(mSpriteSheet->getImage())
	{
		mSheetWidth = mSpriteSheet->getWidth();
	    mSheetHeight = mSpriteSheet->getHeight();
	    mRows = mSheetHeight / mFrameHeight;
	    mColumns = mSheetWidth / mFrameWidth;
	}
	else //unable to load sprite sheet
	{
		mSheetWidth = 0;
	    mSheetHeight = 0;
	    mRows = 0;
	    mColumns = 0;
	}
}

/**
 * Returns a frame of animation from the sprite sheet
 * @param frameNumber The frame number from the sheet
 * @return SDL_Surface containing the image data
 */
OSL_IMAGE* SpriteSheet::getFrame(int frameNumber)
{
	int col, row;
	if (mRows > 0 && mColumns > 0)
	{
		//calculate the row and column of the frame
		col = frameNumber / mRows;
        row = frameNumber - (col * mRows);
        return mSpriteSheet->getSubimage(col * mFrameWidth,
                                  row * mFrameHeight,
                                  mFrameWidth,
                                  mFrameHeight);
    }
	else //there is only one frame in the sheet
    {
    	return mSpriteSheet->getSubimage(0, 0, mFrameWidth, mFrameHeight);
    }
}

/**
 * Returns a frame of animation from the sprite sheet flipped horizontally
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 * @return SDL_Surface containing the image data
 */
OSL_IMAGE* SpriteSheet::getFrame(int frameNumber, char direction)
{
	OSL_IMAGE* img = getFrame(frameNumber);
	oslSwizzleImage(img);
	if(direction == 'r')
		oslMirrorImageH(img);
	return img;
}

/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param frameNumber The frame number
 */
void SpriteSheet::drawFrame(int dstX, int dstY, int frameNumber)
{
	OSL_IMAGE* img = getFrame(frameNumber);
	oslSwizzleImage(img);
	oslDrawImageXY(img, dstX, dstY);
	oslDeleteImage(img);
}


/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space flipped horizontally
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 */
void SpriteSheet::drawFrame(int dstX, int dstY, int frameNumber, char direction)
{
	OSL_IMAGE* img = getFrame(frameNumber);
	oslSwizzleImage(img);
	if(direction == 'r')
		oslMirrorImageH(img);
	oslDrawImageXY(img, dstX, dstY);
	oslDeleteImage(img);
}

/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space flipped horizontally
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 * @param angle The rotation angle
 */
void SpriteSheet::drawFrame(int dstX, int dstY, int frameNumber, char direction, int angle)
{
	OSL_IMAGE* img = getFrame(frameNumber);
	img->angle = angle;
	img->centerX = mFrameWidth / 2;		
	img->centerY = mFrameHeight / 2;	
	oslDrawImageXY(img, dstX, dstY);
	oslDeleteImage(img);
}

/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space 
 * with the height and width specified and flipped horizontally
 * @param g - The graphics context
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param dstW The destination x coordiante
 * @param dstH The destination y coordiante
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 */
void SpriteSheet::drawFrame(int dstX, int dstY, int dstW, int dstH, int frameNumber, char direction)
{
  	int dx = dstX;  int dy = dstY;
  	for(int a = 0; a < dstW / mFrameWidth; a++)
	{
		for(int b = 0; b < dstH / mFrameHeight; b++)
		{
		  	dx = dstX + (a * mFrameWidth);  dy = dstY + (b * mFrameHeight);
			oslDrawImageXY(getFrame(frameNumber, direction), dx, dy);
		}
	}
}

/**
 * Deallocates memory by destroying the SpriteSheet
 */
SpriteSheet::~SpriteSheet()
{
	if(mSpriteSheet)
		delete mSpriteSheet;
}
