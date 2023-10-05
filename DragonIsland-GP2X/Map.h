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
	Map::Map(Header* levelHeader); 
	Tile* Map::getTile(int x, int y);
	void Map::removeTile(int x, int y);
	void Map::setObjectTile(int tileIndex, TileDescription* tileDescription);
	void Map::setEntityTile(int entityIndex, EntityDescription* entityDescription);
	void Map::setCollisionTile(int x, int y, int collision);
	int Map::getHeight();
	int Map::getWidth();
	int Map::getEndX();
	int Map::getEndY();
	int Map::getEndState();
	int Map::getBonusX();
	int Map::getBonusY();
	int Map::getBonusState();
	static bool Map::isTiledTerrain(int tile, int tileset);
	static bool Map::isBlock(int tile, int tileset);
	static bool Map::isAnimated(int tile, int tileset);
	static bool Map::isInvisible(int tile, int tileset);
	static bool Map::isCoin(int tile, int tileset);
	static bool Map::isEmptyBlock(int tile, int tileset);
	static bool Map::isCoinBlock(int tile, int tileset);
	static bool Map::is10CoinBlock(int tile, int tileset);
	static bool Map::isEnlargeBlock(int tile, int tileset);
	static bool Map::isFireBlock(int tile, int tileset);
	static bool Map::isInvincibleBlock(int tile, int tileset);
	static bool Map::isExtraLifeBlock(int tile, int tileset);
	static bool Map::isVineBlock(int tile, int tileset);
	Map::~Map();

private:
    /** The tiles containing the map data */
    Tile** mTile;
    /** The level header data */
    Header* mHeader;
};

#endif
