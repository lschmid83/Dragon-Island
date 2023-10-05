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

	mSpriteSheet = new Image(path); 
	if(mSpriteSheet->getImage())
	{
		mSheetWidth = mSpriteSheet->getWidth();
	    mSheetHeight = mSpriteSheet->getHeight();
	    mRows = mSheetHeight / mFrameHeight;
	    mColumns = mSheetWidth / mFrameWidth;

        //buffer frames of animation
        mFrames = new SDL_Surface[mFrameCount];
        for (int i = 0; i < frameCount; i++) {
			mFrames[i] = *getFrameFromSheet(i);
        }
		delete mSpriteSheet;
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
 * @param frameNumber - The frame number 
 * @return SDL_Surface containing the image data
 */
SDL_Surface* SpriteSheet::getFrameFromSheet(int frameNumber)
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
 * Returns a buffered frame of animation from the sprite sheet
 * @param frameNumber The frame number from the sheet
 * @return SDL_Surface containing the image data
 */
SDL_Surface* SpriteSheet::getFrame(int frameNumber)
{
	if (&mFrames[frameNumber] == NULL) return NULL;
	return &mFrames[frameNumber];
}

/**
 * Returns a buffered frame of animation from the sprite sheet which can be flipped horizontally
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 * @return SDL_Surface containing the image data
 */
SDL_Surface* SpriteSheet::getFrame(int frameNumber, char direction)
{
	if (&mFrames[frameNumber] == NULL) return NULL;
	//http://lists.libsdl.org/pipermail/sdl-libsdl.org/2010-May/076035.html
	if(direction == 'r')	
		return zoomSurface(&mFrames[frameNumber],  -1,  1,  0);
	else
		return zoomSurface(&mFrames[frameNumber],  1,  1,  0);
}

/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space.
 * @param g - The graphics context
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param frameNumber The frame number
 */
void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int frameNumber)
{
  	if (&mFrames[frameNumber] == NULL) return; 
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = mFrameWidth;  src.h = mFrameHeight;
  	dst.x = dstX;  dst.y = dstY;  dst.w = mFrameWidth;  dst.h = mFrameHeight;
  	SDL_BlitSurface(&mFrames[frameNumber], &src, g, &dst);
}


/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space.
 * @param g - The graphics context
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 */
void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int frameNumber, char direction)
{
  	if (&mFrames[frameNumber] == NULL) return; 
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = mFrameWidth;  src.h = mFrameHeight;
  	dst.x = dstX;  dst.y = dstY;  dst.w = mFrameWidth;  dst.h = mFrameHeight;
	SDL_Surface* img = getFrame(frameNumber, direction);
  	SDL_BlitSurface(img, &src, g, &dst);
	SDL_FreeSurface(img);
	delete img;

}

/**
 * Draw the frame with its top-left corner at (x,y) in the destinations coordinate space 
 * with the height and width specified
 * @param g - The graphics context
 * @param dstX The destination x coordiante
 * @param dstY The destination y coordiante
 * @param dstW The destination x coordiante
 * @param dstH The destination y coordiante
 * @param frameNumber The frame number
 * @param direction The horizontal direction of the frame 'l'-left or 'r'-right
 */
void SpriteSheet::drawFrame(SDL_Surface *g, int dstX, int dstY, int dstW, int dstH, int frameNumber, char direction)
{
  	if (&mFrames[frameNumber] == NULL) return; 
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = mFrameWidth;  src.h = mFrameHeight;
  	dst.x = dstX;  dst.y = dstY;  dst.w = mFrameWidth;  dst.h = mFrameWidth;
  	for(int a = 0; a < dstW / mFrameWidth; a++)
	{
		for(int b = 0; b < dstH / mFrameHeight; b++)
		{
		  	dst.x = dstX + (a * mFrameWidth);  dst.y = dstY + (b * mFrameHeight);  dst.w = mFrameWidth;  dst.h = mFrameWidth;
			SDL_BlitSurface(getFrame(frameNumber, direction), &src, g, &dst);
		}
	}
}

/**
 * Deallocates memory by destroying the SpriteSheet
 */
SpriteSheet::~SpriteSheet()
{
    if(mFrames != NULL)
    {
        for (int i = 0; i < mFrameCount; i++) {
            SDL_FreeSurface(&mFrames[i]);
			//delete &mFrames[i];
        }
    }
	delete &mSpriteSheet;
}
