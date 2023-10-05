#ifndef __EntityDescription_H__
#define __EntityDescription_H__

#pragma once

#include <fstream>
#include <string.h>

using namespace std;

/**
 * This class stores entity information
 *
 * @version 1.0
 * @modified 15/10/09
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author
 */
class EntityDescription {
public:
	string Name;
	int X;
	int Y;
	int TileWidth;
	int TileHeight;
	int Tile;
	int Type;
	int Collision;
};

#endif
