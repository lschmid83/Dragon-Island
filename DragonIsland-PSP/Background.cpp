#include "Background.h"

/**
 * Constructs the Background
 * @param fileNumber The number of the image file for the near and far layer images [0]-near, [1]-middle [2]-far
 * @param width The width of the level background
 * @param height The height of the level background
 * @param layerSpeed The scrolling speed of each background layer
 * @param alignement The alignment of the background 0-horizontal 1-vertical
 */
Background::Background(int fileNumber[], int width, int height, int layerSpeed[], int alignment)
{
	mFileNumber = fileNumber;
	mLayerX = new int[3];
	mWidth = width;
	mHeight = height;
	mAlign = alignment;
	mLayerSpeed = layerSpeed;
	
	//load background layer images
	char path[100];
	sprintf (path, "res/bgr/near/%d.png", mFileNumber[0]);	
	mLayerImage[0] = new Image(path, OSL_PF_5551);   //near
	sprintf (path, "res/bgr/middle/%d.png", mFileNumber[1]);	
	mLayerImage[1] = new Image(path, OSL_PF_5551);  //middle
	sprintf (path, "res/bgr/far/%d.png", mFileNumber[2]);
	mLayerImage[2] = new Image(path, OSL_PF_5551);  //far

	//set color from far layer top left pixel
	mColor = mLayerImage[2]->get_pixel32(0, 0);
	
	//set the height using the LayerOffsetY values
	setHeight(height);
}

/**
 * Sets the speed of a background layer
 * @param layer The background layer 0-near, 1-middle 2-far
 * @param speed The horizontal scroll speed
 */
void Background::setLayerSpeed(int layer, int speed) {
    mLayerSpeed[layer] = speed;
}

/**
 * Sets the background color
 * @param colour The RGB background color
 */
void Background::setColor(Color* colour) {
    mColor = colour;
}

/**
 * Returns the background color
 * @return The RGB background color
 */
Color* Background::getColor() {
    return mColor;
}

/**
 * Sets the width of the background
 * @param width The width of the background
 */
void Background::setWidth(int width) {
    mWidth = width;
}

/**
 * Sets the height of the background
 * @param height The height of the background
 */
void Background::setHeight(int height) {
	mHeight = height;
    mLayerY = new int[3];
    mLayerY[0] = height - mLayerImage[0]->getHeight();
    mLayerY[1] = height - mLayerImage[1]->getHeight();
    mLayerY[2] = height - mLayerImage[2]->getHeight();
}

/**
 * Sets the alignment of the background
 * @param alignment the alignment of the background 0-repeat x axis / 1=repeat y axis (far layer)
 */
void Background::setAlign(int alignment) {
    mAlign = alignment;
}

/**
 * Draws the background on the graphics surface at the camera position
 * @param g The drawing surface
 * @param cam The camera coordinates
 * @return The camera coordinates within the screen area
 */
Point* Background::draw(Point* cam) {

    oslDrawFillRect(0, 0, 480, 272, RGB(mColor->getRed(), mColor->getGreen(), mColor->getBlue()));
        
    if (cam->x >= mWidth - 480) {
        cam->x = mWidth - 480;
    }
    if (cam->x <= 0) {
        cam->x = 0;
    }
    if (cam->y >= mHeight - 272) {
        cam->y = mHeight - 272;
    }
    if (cam->y <= 0) {
        cam->y = 0;
    }

    int bgX = cam->x - (cam->x / 512 * 512);
    int bgY = cam->y - (cam->y / 512 * 512);

    if (Settings::Animation) //move background
    {
        if (mScrollTimer < 7) {
            mScrollTimer++;
        } else {
            for (int i = 0; i < 3; i++) {
                if (mLayerX[i] < 512) {
                    mLayerX[i] += mLayerSpeed[i];
                } else {
                    mLayerX[i] = 0;
                }
            }
            mScrollTimer = 0;
        }
    } else {
    	mLayerX[0] = 0;
        mLayerX[1] = 0;
        mLayerX[2] = 0;
    }


    if (Settings::Background) {
        if (mAlign == 0) //horizontal
        {
            mLayerImage[2]->draw(-bgX - mLayerX[2], mLayerY[2] - cam->y);
            mLayerImage[2]->draw(-bgX - mLayerX[2] + 512, mLayerY[2] - cam->y);
            mLayerImage[2]->draw(-bgX - mLayerX[2] + 1024, mLayerY[2] - cam->y);

            mLayerImage[1]->draw(-bgX - mLayerX[1], mLayerY[1] - cam->y);
            mLayerImage[1]->draw(-bgX - mLayerX[1] + 512, mLayerY[1] - cam->y);
            mLayerImage[1]->draw(-bgX - mLayerX[1] + 1024, mLayerY[1] - cam->y);

            mLayerImage[0]->draw(-bgX - mLayerX[0], mLayerY[0] - cam->y);
            mLayerImage[0]->draw(-bgX - mLayerX[0] + 512, mLayerY[0] - cam->y);
            mLayerImage[0]->draw(-bgX - mLayerX[0] + 1024, mLayerY[0] - cam->y);
        } 
		else //vertical
        {
            mLayerImage[2]->draw(0, -bgY);
            mLayerImage[2]->draw(0, -bgY + 512);
            mLayerImage[2]->draw(0, -bgY + 1024);
        }
    }
    return cam;
}

/**
 * Deallocates memory by destroying the Background
 */
Background::~Background()
{
	delete [] mLayerX;
	delete [] mLayerY;
	for(int i = 0; i < 3; i++)
	{
		if(mLayerImage[i])
			delete mLayerImage[i];
	}
	delete mColor;
}
