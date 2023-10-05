#ifndef __Tile_H__
#define __Tile_H__

#pragma once

/**
 * This class stores level map tile information for entities and tiles
 *
 * @version 1.0
 * @modified 27/04/11
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author
 */
class Tile {
public:
	Tile();
	void clear();
	~Tile();
	int index;     //list index
	int tile1;     //file
	int tile2;     //folder
	int tile3;     //tile no. for tiled terrain
	int state;     //object state 0-normal, 1-collide, 2-empty
	int count;     //item count
	int collision; //collision type 0-none, 1-normal, 2-on fall, 3-item block
	int offset;    //offset y
	int draw;      //draw tile
	int spriteIndex;
	int spriteTile;
	int spriteDraw;
};

#endif
