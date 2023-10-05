#ifndef __EntityCollection_H__
#define __EntityCollection_H__

#pragma once

#include "Sprite.h"
#include "Tile.h"
#include "Map.h"
#include "Point.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <oslib/oslib.h>

using namespace std;  

/**
 * @class EntityCollection
 * @brief This class creates a collection of entities for the level editor
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class EntityCollection
{
public:
	EntityCollection();
	int getSize();
	OSL_IMAGE* getEntity(int index, char direction);
	void draw(Map* map, Point* cam);
	void loadSprite(int i);
	~EntityCollection();

private:
	/** The list of entity objects */
    vector<Sprite*> mEntities;
    /** The tile information from the level map */
    Tile* mTileInfo;
};

#endif
