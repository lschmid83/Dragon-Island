#ifndef __TileDescription_H__
#define __TileDescription_H__

#pragma once

#include <fstream>
#include <string.h>

using namespace std; 

/**
 * This class stores tile information
 *
 * @version 1.0
 * @modified 15/10/09
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author
 */
class TileDescription {
public:
	string Name;
	int X;
	int Y;
	int Width;
	int Height;
	int Tile;
	int Tileset;
	int TileWidth;
	int TileHeight;
	int Collision;
};

#endif
