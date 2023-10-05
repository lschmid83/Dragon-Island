#include "Tile.h"

/**
 * Default constructor
 */
Tile::Tile()
{
	index = 0;
	tile1 = 0;
	tile2 = 0;
	tile3 = 0;
	state = 0;
	count = 0;
	collision = 0;
	offset = 0;
	draw = 0;
	spriteIndex = 0;
	spriteTile = 0;
	spriteDraw = 0;

}

/**
 * Deallocates memory by destroying the Tile
 */
Tile::~Tile()
{
	delete &index;     
	delete &tile1;     
	delete &tile2;    
	delete &tile3;    
	delete &state;     
	delete &count;     
	delete &collision; 
	delete &offset;   
	delete &draw;      
	delete &spriteIndex;
	delete &spriteTile;
	delete &spriteDraw;
}
