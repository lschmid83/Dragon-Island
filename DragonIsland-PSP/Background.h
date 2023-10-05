#ifndef __Background_H__
#define __Background_H__

#pragma once

#include "Point.h"
#include "Image.h"
#include "Color.h"
#include "Settings.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;  

/**
 * @class Background
 * @brief This class draws a parallax scrolling background
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Background
{
public:

    Background(int fileNumber[], int width, int height, int layerSpeed[], int alignment);
	void setLayerSpeed(int layer, int speed);
	void setColor(Color* colour);
	Color* getColor();
    void setWidth(int width);
    void setHeight(int height);
    void setAlign(int alignment);	
	Point* draw(Point* cam);	
	~Background();

private:
	/** The file number of each layer image */
    int* mFileNumber;
    /** The image data for each background layer */
    Image* mLayerImage[3];
    /** The x coordinate of each layer */
    int* mLayerX;
    /** The y coordinate of each layer */
    int* mLayerY;
    /** The scrolling speed of each layer */
    int* mLayerSpeed;
    /** The width of the background */
    int mWidth;
    /** The height of the background */
    int mHeight;
    /** The alignment of the background 0 - horizontal 1 - vertical */
    int mAlign;
    /** The background color displayed where the background image does not cover the screen */
    Color* mColor;
    /** The time elapsed between scrolling the background */
    int mScrollTimer;
};

#endif
