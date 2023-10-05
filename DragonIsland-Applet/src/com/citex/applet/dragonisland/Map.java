package com.citex.applet.dragonisland;

import java.util.Random;

/**
	This class stores level information including the header, map tiles 
	and entity positions.

	@version 1.0
	@modified 20/04/2012
	@author Lawrence Schmid<BR><BR>
	
	This file is part of Dragon Island.<BR><BR>
	
	Dragon Island is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.<BR><BR>
	
	Dragon Island is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.<BR><BR>
	
	You should have received a copy of the GNU General Public License
	along with Dragon Island. If not, see http://www.gnu.org/licenses/.<BR><BR>
	
	Copyright 2012 Lawrence Schmid
*/

public class Map {

    /** The tiles containing the map data */
    private Tile mTile[][];
    /** The level header data */
    private Header mHeader;

    /**
     * Constructs the Map
     * @param levelHeader The level header information
     */
    public Map(Header levelHeader) {

        mHeader = levelHeader;
        int w, h;
        w = (mHeader.width / 16) + 100;
        h = (mHeader.height / 16) + 100;
        mTile = new Tile[w][h];

        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++) {
                mTile[a][b] = new Tile();
            }
        }
    }

    /**
     * Returns the tile at the x,y coordinate
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The tile information
     */
    public Tile getTile(int x, int y) {
        try {
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            return mTile[x][y];
        } catch (Exception e) {
            return new Tile();
        }
    }

    /**
     * Removes a tile
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void removeTile(int x, int y) {
        mTile[x][y] = new Tile();
    }

    /**
     * Sets the tile width, height and item count
     * @param tileIndex The index of the tile
     * @param tileDescription The tile description
     */
    public void setObjectTile(int tileIndex, TileDescription tileDescription) {
        try {
            //place objects in map array
            for (int w = 0; w < tileDescription.width; w++) {
                for (int h = 0; h < tileDescription.height; h++) {
                    mTile[tileDescription.x + w][tileDescription.y + h].index = tileIndex;
                    mTile[tileDescription.x + w][tileDescription.y + h].tile1 = tileDescription.tile;
                    mTile[tileDescription.x + w][tileDescription.y + h].tile2 = tileDescription.tileset;
                    Random rnd = new Random(25);
                    if (isTiledTerrain(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].tile3 = rnd.nextInt(4) + 1;
                    } else {
                        mTile[tileDescription.x + w][tileDescription.y + h].tile3 = 0;
                    }
                    if (isBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 99;
                    }
                    if (isCoinBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    } else if (is10CoinBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 10;
                    } else if (isEnlargeBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    } else if (isFireBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    } else if (isInvincibleBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    } else if (isExtraLifeBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    } else if (isVineBlock(tileDescription.tile, tileDescription.tileset)) {
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    }
                    mTile[tileDescription.x + w][tileDescription.y + h].collision = tileDescription.collision;
                    mTile[tileDescription.x + w][tileDescription.y + h].state = 0;
                    mTile[tileDescription.x + w][tileDescription.y + h].offset = 0;
                    mTile[tileDescription.x + w][tileDescription.y + h].draw = 0;
                }
            }
            //single object
            if (tileDescription.width == tileDescription.tileWidth && tileDescription.height == tileDescription.tileHeight) {
                mTile[tileDescription.x][tileDescription.y].draw = 1;
            } else //multiple objects
            {
                for (int w = 0; w < tileDescription.width / tileDescription.tileWidth; w++) {
                    for (int h = 0; h < tileDescription.height / tileDescription.tileHeight; h++) {
                        mTile[tileDescription.x + (w * tileDescription.tileWidth)][tileDescription.y + (h * tileDescription.tileHeight)].draw = 1;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Sets a entity tile
     * @param entityIndex The index of the entity
     * @param entityDescription The entity description
     */
    public void setEntityTile(int entityIndex, EntityDescription entityDescription) {
        try {
            //place objects in map array
            for (int w = 0; w < entityDescription.tileWidth; w++) {
                for (int h = 0; h < entityDescription.tileHeight; h++) {
                    mTile[entityDescription.x + w][entityDescription.y + h].spriteIndex = entityIndex;
                    mTile[entityDescription.x + w][entityDescription.y + h].spriteTile = entityDescription.tile;
                    if (w == 0 && h == 0) {
                        mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw = 1;
                    } else {
                        if (mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw != 1)
                        {
                            mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Sets the collision state of a tile
     * @param x The x coordinate
     * @param y The y coordinate
     * @param collision The collision state 0-no collision 1-collision
     */
    public void setCollisionTile(int x, int y, int collision) {
        mTile[x][y].collision = 1;
    }

    /**
     * Returns the height of the map
     * @return The height of the map
     */
    public int getHeight() {
        return mHeader.height;
    }

    /**
     * Returns the width of the map
     * @return The width of the map
     */
    public int getWidth() {
        return mHeader.width;
    }

    /**
     * Returns the levels end x coordinate
     * @return The level end x coordinate
     */
    public int getEndX() {
        return mHeader.endX;
    }

    /**
     * Returns the level end y coordinate
     * @return The level end y coordinate
     */
    public int getEndY() {
        return mHeader.endY;
    }

    /**
     * Returns the players end state
     * @return The players end state, 0=stand left, 1=stand right, 2=up pipe, 3=down pipe, 4=left pipe, 5=right pipe
     */
    public int getEndState() {
        return mHeader.endState;
    }

    /**
     * Returns the levels bonus x coordinate
     * @return The levels bonus x coordinate
     */
    public int getBonusX() {
        return mHeader.bonusX;
    }

    /**
     * Returns the levels bonus y coordinate
     * @return the levels bonus y coordinate
     */
    public int getBonusY() {
        return mHeader.bonusY;
    }

    /**
     * Returns the players bonus state
     * @return The players bonus state, 0=stand left, 1=stand right, 2=up pipe, 3=down pipe, 4=left pipe, 5=right pipe
     */
    public int getBonusState() {
        return mHeader.bonusState;
    }

    /**
     * Is this terrain tiled i.e. contains more than one frame
     * @param tile The tile index
     * @param tileset The tileset index 0-block, 16-terrain, 32-scenery
     * @return True if the terrain contains more than one tile
     */
    public static boolean isTiledTerrain(int tile, int tileset) {
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
    public static boolean isBlock(int tile, int tileset) {
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
    public static boolean isAnimated(int tile, int tileset) {
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
    public static boolean isInvisible(int tile, int tileset) {
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
    public static boolean isCoin(int tile, int tileset) {
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
    public static boolean isEmptyBlock(int tile, int tileset) {
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
    public static boolean isCoinBlock(int tile, int tileset) {
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
    public static boolean is10CoinBlock(int tile, int tileset) {
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
    public static boolean isEnlargeBlock(int tile, int tileset) {
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
    public static boolean isFireBlock(int tile, int tileset) {
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
    public static boolean isInvincibleBlock(int tile, int tileset) {
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
    public static boolean isExtraLifeBlock(int tile, int tileset) {
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
    public static boolean isVineBlock(int tile, int tileset) {
        if (tileset != 0) {
            return false;
        }
        if (tile == 27 || tile == 37) {
            return true;
        } else {
            return false;
        }
    }
}

/**
	Map tile description.
	
	@version 1.0
	@modified 20/04/2012
	@author Lawrence Schmid<BR><BR>
	
	This file is part of Dragon Island.<BR><BR>
	
	Dragon Island is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.<BR><BR>
	
	Dragon Island is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.<BR><BR>
	
	You should have received a copy of the GNU General Public License
	along with Dragon Island. If not, see http://www.gnu.org/licenses/.<BR><BR>
	
	Copyright 2012 Lawrence Schmid
*/

class Tile {

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
}
