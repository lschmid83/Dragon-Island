#ifndef __Tileset_H__
#define __Tileset_H__

#pragma once

#include "Image.h"
#include "Tile.h"
#include "Point.h"
#include "Map.h"
#include "BlockExplosion.h"
#include "Settings.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

#include <oslib/oslib.h>

using namespace std;  

/**
 * This class loads the block, terrain and scenery tilesets and draws the tiles
 * from the level map on the graphics surface
 *
 * @version 1.0
 * @modified 21/04/11
 * @author Lawrence Schmid<BR><BR>
 *
 * Not for duplication or distribution without the permission of the author
 */
class Tileset
{
public:
	Tileset(int tileset1, int tileset2, int tileset3);
	OSL_IMAGE* getSubimage(OSL_IMAGE* img, int x, int y, int w, int h);
	OSL_IMAGE* getFrame(int tileIndex, int tilesetIndex, int repeatIndex);
	OSL_IMAGE* getFrameIndex(int tileIndex, int tilesetIndex, int repeatIndex, int frameNumber);
	void setBlockExplode(int x, int y, char direction);
	void drawImage(OSL_IMAGE* img, int x, int y);
    void draw(int x, int y, Point* cam, Tile* tile);
	void drawExplosions(Map* map, Point* cam);
	void drawMap(Map* map, Point* cam);
	void drawVineBlocks(Map* map, Point* cam);
	void drawEditorMap(Map* map, Point* cam);
	void initDefinitions();
	vector<string> getTileDescription(int index);
	void split(const string& s, char c, vector<string>& v); 
	int getSizeOfTileDescriptions();
	vector<string> getTileDescription(int tileIndex, int tilesetIndex);
	~Tileset();

private:
	/** The sprite sheet containing block tile image data */
    Image* mBlockTileset;
    /** The buffered block tiles from the sprite sheet */
    OSL_IMAGE* mBlockTile[66];
    /** The buffered frames of smashed block animation */
    OSL_IMAGE* mSmashedBlock[4];
    /** The buffered frames of coin animation */
    OSL_IMAGE* mCoin[4]; 
    /** The buffered frames of brick animation */
    OSL_IMAGE* mBrick[4];
    /** The buffered frames of question block animation */
    OSL_IMAGE*  mQuestionBlock[4];
    /** The sprite sheet containing terrain tile image data */
    Image* mTerrainTileset;
    /** The buffered terrain tiles from the sprite sheet */
    OSL_IMAGE* mTerrainTile[14]; 
    /** The sprite sheet containing scenery tile image data */
    Image* mSceneryTileset;
    /** The buffered scenery tiles from the sprite sheet */
    OSL_IMAGE* mSceneryTile[6];
    /** The folder number of the block, terrain and scenery tilesets  */
    int mTilesetIndex[3];
    /** The time elapsed between changing animation frame */
    int mFrameTimer;
    /** The frame index for animated tiles */
    int mFrame;
    /** The tile information from the level map */
    Tile* mTileInfo;
    /** The list of string definitions for the tiles */
    vector<string> mTileDefintions;
    /** Stores information about smashed blocks */
    vector<BlockExplosion> mExplosion;
};

#endif
