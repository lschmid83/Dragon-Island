#include "Map.h"

/**
 * Constructs the Map
 * @param levelHeader The level header information
 */
Map::Map(Header* levelHeader) {

    mHeader = levelHeader;
    int w, h;
    w = (mHeader->Width / 16) + 500;
    h = (mHeader->Height / 16) + 500;

	mTile = new Tile*[h];
	for (int i = 0; i < h; ++i)
		mTile[i] = new Tile[w];
}

/**
 * Returns the tile at the x,y coordinate
 * @param x The x coordinate
 * @param y The y coordinate
 * @return The tile information
 */
Tile* Map::getTile(int x, int y) {
    try {
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        return &mTile[x][y];
    } catch (int e) {
        return new Tile();
    }
}

/**
 * Removes a tile
 * @param x the x coordinate
 * @param y the y coordinate
 */
void Map::removeTile(int x, int y) {
    mTile[x][y] = *new Tile();
}

/**
 * Sets the tile width, height and item count
 * @param tileIndex The index of the tile
 * @param tileDescription The tile description
 */
void Map::setObjectTile(int tileIndex, TileDescription* tileDescription) {
    try {
        //place objects in map array
        for (int w = 0; w < tileDescription->Width; w++) {
            for (int h = 0; h < tileDescription->Height; h++) {
                //mTile[tileDescription->X + w][tileDescription->Y + h] = *new Tile();
                mTile[tileDescription->X + w][tileDescription->Y + h].index = tileIndex;
                mTile[tileDescription->X + w][tileDescription->Y + h].tile1 = tileDescription->Tile;
                mTile[tileDescription->X + w][tileDescription->Y + h].tile2 = tileDescription->Tileset;
                //http://www.cplusplus.com/reference/clibrary/cstdlib/rand/
				/* initialize random seed: */
  				srand ( time(NULL) );
                if (isTiledTerrain(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].tile3 = rand() % 10 + 4;
                } else {
                    mTile[tileDescription->X + w][tileDescription->Y + h].tile3 = 0;
                }
                if (isBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 99;
                }
                if (isCoinBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                } else if (is10CoinBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 10;
                } else if (isEnlargeBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                } else if (isFireBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                } else if (isInvincibleBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                } else if (isExtraLifeBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                } else if (isVineBlock(tileDescription->Tile, tileDescription->Tileset)) {
                    mTile[tileDescription->X + w][tileDescription->Y + h].count = 1;
                }
                mTile[tileDescription->X + w][tileDescription->Y + h].collision = tileDescription->Collision;
                mTile[tileDescription->X + w][tileDescription->Y + h].state = 0;
                mTile[tileDescription->X + w][tileDescription->Y + h].offset = 0;
                mTile[tileDescription->X + w][tileDescription->Y + h].draw = 0;
            }
        }
        //single object
        if (tileDescription->Width == tileDescription->TileWidth && tileDescription->Height == tileDescription->TileHeight) {
            mTile[tileDescription->X][tileDescription->Y].draw = 1;
        } else //multiple objects
        {
            for (int w = 0; w < tileDescription->Width / tileDescription->TileWidth; w++) {
                for (int h = 0; h < tileDescription->Height / tileDescription->TileHeight; h++) {
                    mTile[tileDescription->X + (w * tileDescription->TileWidth)][tileDescription->Y + (h * tileDescription->TileHeight)].draw = 1;
                }
            }
        }
    } catch (int e) {
    }
}

/**
 * Sets a entity tile
 * @param entityIndex The index of the entity
 * @param entityDescription The entity description
 */
void Map::setEntityTile(int entityIndex, EntityDescription* entityDescription) {
 
	try {
        //place objects in map array
        for (int w = 0; w < entityDescription->TileWidth; w++) {
            for (int h = 0; h < entityDescription->TileHeight; h++) {
                //mTile[entityDescription->X + w][entityDescription->Y + h] = *new Tile();
                mTile[entityDescription->X + w][entityDescription->Y + h].spriteTile = entityDescription->Tile;
				//cout << entityDescription.X + w << "," << entityDescription.Y + h << endl;
                if (w == 0 && h == 0) {
                    mTile[entityDescription->X + w][entityDescription->Y + h].spriteDraw = 1;
                } else {
                    if (mTile[entityDescription->X + w][entityDescription->Y + h].spriteDraw != 1)
                    {
                        mTile[entityDescription->X + w][entityDescription->Y + h].spriteDraw = 0;
                    }
                }
            }
        }
    } catch (int e) {
    }
}

/**
 * Sets the collision state of a tile
 * @param x The x coordinate
 * @param y The y coordinate
 * @param collision The collision state 0-no collision 1-collision
 */
void Map::setCollisionTile(int x, int y, int collision) {
    mTile[x][y].collision = 1;
}

/**
 * Returns the height of the map
 * @return The height of the map
 */
int Map::getHeight() {
    return mHeader->Height;
}

/**
 * Returns the width of the map
 * @return The width of the map
 */
int Map::getWidth() {
    return mHeader->Width;
}

/**
 * Returns the levels end x coordinate
 * @return The level end x coordinate
 */
int Map::getEndX() {
    return mHeader->EndX;
}

/**
 * Returns the level end y coordinate
 * @return The level end y coordinate
 */
int Map::getEndY() {
    return mHeader->EndY;
}

/**
 * Returns the players end state
 * @return The players end state, 0=stand left, 1=stand right, 2=up pipe, 3=down pipe, 4=left pipe, 5=right pipe
 */
int Map::getEndState() {
    return mHeader->EndState;
}

/**
 * Returns the levels bonus x coordinate
 * @return The levels bonus x coordinate
 */
int Map::getBonusX() {
    return mHeader->BonusX;
}

/**
 * Returns the levels bonus y coordinate
 * @return the levels bonus y coordinate
 */
int Map::getBonusY() {
    return mHeader->BonusY;
}

/**
 * Returns the players bonus state
 * @return The players bonus state, 0=stand left, 1=stand right, 2=up pipe, 3=down pipe, 4=left pipe, 5=right pipe
 */
int Map::getBonusState() {
    return mHeader->BonusState;
}

/**
 * Is this terrain tiled i.e. contains more than one frame
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the terrain contains more than one tile
 */
bool Map::isTiledTerrain(int tile, int tileset) {
    if (tileset == 16 && (tile == 2 || tile == 4 || tile == 5 || tile == 6 || tile == 8)) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block which can be hit
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block which can be hit
 */
bool Map::isBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false; 
    }
    if ((tile >= 20 && tile < 50)) 
    {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile animated
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile contains more than one frame of animation
 */
bool Map::isAnimated(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if ((tile >= 20 && tile < 40) || tile == 10) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile an invisible block
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is an invisible block
 */
bool Map::isInvisible(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 40 || tile == 41) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a coin
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a coin
 */
bool Map::isCoin(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 10) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile an empty block
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is an empty block
 */
bool Map::isEmptyBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 20 || tile == 30) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a coin
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a coin
 */
bool Map::isCoinBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 21 || tile == 31 || tile == 40) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing 10 coins
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing 10 coins
 */
bool Map::is10CoinBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 22 || tile == 32) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a enlarge power up
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a enlarge power up
 */
bool Map::isEnlargeBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 23 || tile == 33) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a fire power up
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a fire power up
 */
bool Map::isFireBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 24 || tile == 34) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a invincible power up
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a invincible power up
 */
bool Map::isInvincibleBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }

    if (tile == 25 || tile == 35 || tile == 41) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a extra life
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a extra life
 */
bool Map::isExtraLifeBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 26 || tile == 36) {
        return true;
    } else {
        return false;
    }
}

/**
 * Is the tile a block containing a growing vine
 * @param tile The tile index
 * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
 * @return True if the tile is a block containing a growing vine
 */
bool Map::isVineBlock(int tile, int tileset) {
    if (tileset != 0) {
        return false;
    }
    if (tile == 27 || tile == 37) {
        return true;
    } else {
        return false;
    }
}

Map::~Map()
{
/*
    int h = (mHeader->Height / 16) + 500;
	for (int i = 0; i < h; ++i)
	{
		//delete [] &mTile[i];
	}
	delete [] mTile;
*/
}






