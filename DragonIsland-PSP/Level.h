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
	Level();
	Level(char* path);
	Header* getHeader();
	void setHeader(Header* header);
	TileDescription* getTileDescription(int index);
	void setTileDescription(int index, TileDescription* obj);
	TileDescription* getTile(int index);
	void removeTile(int index);
	void addTile(int tileIndex, int tilesetNumber, int x, int y, int w, int h);
	void moveUp();
	void moveDown();
	void moveLeft();
	void moveRight();
	EntityDescription* getEntityDescription(int index);
	void setEntityDescription(int index, EntityDescription* entity);
	void removeEntity(int index);
	void addEntity(int index, int x, int y);
	int getObjectCount();
	int getEntityCount();
	void removeCastle();
	Map* toMap(Map* map);
	bool loadLevel(char* path);
	void newLevel();
	void saveLevel(char* path);
	vector<string> getTileStringDescription(int tileIndex, int tilesetIndex);
	vector<string> getEntityStringDescription(int tile);
	void initDefinitions();
	~Level();
	string convertInt(int number);
	void split(const string& s, char c, vector<string>& v);

private:
	/** The levels header information */
    Header* mHeader;
    /** The tile information (blocks, terrain, scenery) */
    vector<TileDescription*> mTiles;
    /** The sprites information */
    vector<EntityDescription*> mEntities;
    /** The entity descriptions */
    vector<string> mEntitiyDefinitions;
    /** The tile descriptions */
    vector<string> mTileDefinitions;	
};

#endif
