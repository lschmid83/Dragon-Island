#ifndef _Header_H__
#define __Header_H__

#pragma once

#include "Color.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;

/**
 * This class stores level header information such as the background, music
 * and tilesets associated with the level
 *
 * @version 1.0
 * @modified 15/10/09
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author
 */
class Header {
public:
	Header();
	~Header();
	string Name;
	int World;
	int Level;
	int Area;
	int TimeLimit;
	int Bg[3];
	Color* BgColor;
	int BgSpeed[3];
	int BgAlign;
	int Music;
	int Tileset0;
	int Tileset16;
	int Tileset32;
	int Width;
	int Height;
	int ObjectCount;
	int EntityCount;
	int StartState;
	int StartX;
	int StartY;
	int EndState;
	int EndX;
	int EndY;
	int EndWorld;
	int EndLevel;
	int EndArea;
	int BonusX;
	int BonusY;
	int BonusState;
};

#endif
