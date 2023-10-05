#ifndef __Level_H__
#define __Level_H__

#pragma once

#include "Color.h"
#include "Header.h"
#include "TileDescription.h"
#include "EntityDescription.h"
#include <iostream>
#include <sys/param.h>
#include <fstream>
#include <string.h>
#include <sstream>
#include <vector>
#include "Map.h"

using namespace std;  

/**
 * @class Level
 * @brief This class opens and saves level files which contain header,
 * tile and entity information
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Level
{
public:
	Level::Level(char* path);
	Level::Level();
	Header* Level::getHeader();
	void Level::setHeader(Header* header);
	TileDescription Level::getTileDescription(int index);
	void Level::setTileDescription(int index, TileDescription obj);
	void Level::removeTile(int index);
	void Level::addTile(int tileIndex, int tilesetNumber, int x, int y, int w, int h);
	EntityDescription* Level::getEntityDescription(int index);
	void Level::setEntityDescription(int index, EntityDescription entity);
	void Level::removeEntity(int index);
	void Level::addEntity(int index, int x, int y);
	int Level::getObjectCount();
	int Level::getEntityCount();
	void Level::removeCastle();
	Map* Level::toMap();
	bool Level::loadLevel(char* path);
	void Level::newLevel();
	void Level::saveLevel(char* path);
	vector<string> Level::getTileStringDescription(int tileIndex, int tilesetIndex);
	vector<string> Level::getEntityStringDescription(int tile);
	void Level::initDefinitions();
	Level::~Level();

string Level::convertInt(int number);
void Level::split(const string& s, char c, vector<string>& v);

private:
	/** The levels header information */
    Header* mHeader;
    /** The tile information (blocks, terrain, scenery) */
    vector<TileDescription> mTiles;
    /** The sprites information */
    vector<EntityDescription> mEntities;
    /** The entity descriptions */
    vector<string> mEntitiyDefinitions;
    /** The tile descriptions */
    vector<string> mTileDefinitions;	
	Map* map;

};

#endif
