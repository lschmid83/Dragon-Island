#ifndef __Tileset_H__
#define __Tileset_H__

#pragma once

#include "Image.h"
#include "Tile.h"
#include "Point.h"
#include "Map.h"
#include "BlockExplosion.h"
#include "Settings.h"
#include <SDL/SDL_image.h>
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

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
	Tileset::Tileset(int tileset1, int tileset2, int tileset3);
	SDL_Surface* Tileset::getSubimage(SDL_Surface* img, int x, int y, int w, int h);
	SDL_Surface* Tileset::getFrame(int tileIndex, int tilesetIndex, int repeatIndex);
	SDL_Surface* Tileset::getFrameIndex(int tileIndex, int tilesetIndex, int repeatIndex, int frameNumber);
	void Tileset::setBlockExplode(int x, int y, char direction);
	void Tileset::drawImage(SDL_Surface *g, SDL_Surface *img, int x, int y);
    void Tileset::draw(SDL_Surface* g, int x, int y, Point* cam, Tile* tile);
	void Tileset::drawExplosions(SDL_Surface* g, Map* map, Point* cam);
	void Tileset::drawMap(SDL_Surface* g, Map* map, Point* cam);
	void Tileset::drawEditorMap(SDL_Surface* g, Map* map, Point* cam);
	void Tileset::initDefinitions();
	vector<string> Tileset::getTileDescription(int index);
	void Tileset::split(const string& s, char c, vector<string>& v); 
	int Tileset::getSizeOfTileDescriptions();
	vector<string> Tileset::getTileDescription(int tileIndex, int tilesetIndex);
	Tileset::~Tileset();

private:
	/** The sprite sheet containing block tile image data */
    Image* mBlockTileset;
    /** The buffered block tiles from the sprite sheet */
    SDL_Surface* mBlockTile;
    /** The buffered frames of smashed block animation */
    SDL_Surface* mSmashedBlock;
    /** The buffered frames of coin animation */
    SDL_Surface* mCoin; 
    /** The buffered frames of brick animation */
    SDL_Surface* mBrick;
    /** The buffered frames of question block animation */
    SDL_Surface*  mQuestionBlock;
    /** The sprite sheet containing terrain tile image data */
    Image* mTerrainTileset;
    /** The buffered terrain tiles from the sprite sheet */
    SDL_Surface* mTerrainTile; 
    /** The sprite sheet containing scenery tile image data */
    Image* mSceneryTileset;
    /** The buffered scenery tiles from the sprite sheet */
    SDL_Surface* mSceneryTile;
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
