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
 * @brief This class draws a scrolling background with three layers
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Background
{
public:

	Background::Background(int fileNumber[], int width, int height, int layerSpeed[], int alignement);
	vector<string> Background::readIni(char* layer, int fileNumber);
	void Background::setLayerSpeed(int layer, int speed);
	void Background::setColor(Color* colour);
	Color* Background::getColor();
    void Background::setWidth(int width);
    void Background::setHeight(int height);
    void Background::setAlign(int alignment);	
	Point* Background::draw(SDL_Surface* g, Point* cam);	
	Background::~Background();

private:
	/** The file number of each layer image */
    int* mFileNumber;
    /** The image data for each background layer */
    Image* mLayerImage;
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
