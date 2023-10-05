#ifndef __Map_H__
#define __Map_H__

#pragma once

#include "Header.h"
#include "Tile.h"
#include "TileDescription.h"
#include "EntityDescription.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

/**
 * @class Map
 * @brief This class stores level information including the header,
 * map tiles and entity data
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Map
{
public:
	Map(Header* levelHeader);
	void clear();
	Tile* getTile(int x, int y);
	void removeTile(int x, int y);
	void setObjectTile(int tileIndex, TileDescription* tileDescription);
	void setEntityTile(int entityIndex, EntityDescription* entityDescription);
	void setCollisionTile(int x, int y, int collision);
	int getHeight();
	int getWidth();
	int getEndX();
	int getEndY();
	int getEndState();
	int getBonusX();
	int getBonusY();
	int getBonusState();
	static bool isTiledTerrain(int tile, int tileset);
	static bool isBlock(int tile, int tileset);
	static bool isAnimated(int tile, int tileset);
	static bool isInvisible(int tile, int tileset);
	static bool isCoin(int tile, int tileset);
	static bool isEmptyBlock(int tile, int tileset);
	static bool isCoinBlock(int tile, int tileset);
	static bool is10CoinBlock(int tile, int tileset);
	static bool isEnlargeBlock(int tile, int tileset);
	static bool isFireBlock(int tile, int tileset);
	static bool isInvincibleBlock(int tile, int tileset);
	static bool isExtraLifeBlock(int tile, int tileset);
	static bool isVineBlock(int tile, int tileset);
	~Map();

private:
    /** The tiles containing the map data */
    Tile** mTile;
    /** The level header data */
    Header* mHeader;
	int mWidth;
	int mHeight;
	Tile* mEmptyTile;
};

#endif
