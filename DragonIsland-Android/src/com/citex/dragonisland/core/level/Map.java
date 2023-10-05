package com.citex.dragonisland.core.level;

import com.citex.dragonisland.core.sprite.entity.EntityDescription;
import com.citex.dragonisland.core.sprite.player.enums.PipeState;
import com.citex.dragonisland.core.tileset.Tile;
import com.citex.dragonisland.core.tileset.TileDescription;

/**
 * This class stores the level tile map.
 * @author Lawrence Schmid
 */
public class Map {

    /** Tiles containing the map data. */
    private Tile mTile[][];
    
    /** Level header data. */
    private Header mHeader;
        
    /**
     * Initialises a Map object.
     * @param levelHeader LevelHeader object.
     */
    public Map(Header levelHeader) {

    	// Initialise the level header.
        mHeader = levelHeader;
        
        // Initialise the tile map array.
        int w, h;
        w = (mHeader.width / 16) + 100;
        h = (mHeader.height / 16) + 100;
        mTile = new Tile[w][h];  
        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++)
                mTile[a][b] = new Tile();
        }
    }

    /**
     * Gets the tile at the x,y coordinate.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Tile object.
     */
    public Tile getTile(int x, int y) {
        
    	// Check tile coordinates are within level area.
        if (x >= 0 && y >= 0 && x < mHeader.width && y < mHeader.height) {
	    	x = x / 16;
	    	y = y / 16;
	    	
	    	// Return the map tile.
	    	return mTile[x][y];
        }
        else
        	return new Tile();

    }

    /**
     * Removes a tile.
     * @param x X coordinate.
     * @param y Y coordinate/
     */
    public void removeTile(float x, float y) {
        
    	if (x >= 0 && y >= 0 && x < mHeader.width && y < mHeader.height) {
	    	x = x / 16;
	    	y = y / 16;
	        
	    	// Initialise a new empty tile.
	    	mTile[(int)x][(int)y] = new Tile();
    	}
    }

    /**
     * Sets a object tile.
     * @param tileIndex Index of the tile.
     * @param tileDescription TileDescription object.
     */
    public void setObjectTile(int tileIndex, TileDescription tileDescription) {
       
    	try {
            
    		// Loop through tiled width and height.
            for (int w = 0; w < tileDescription.width; w++) {
                
            	for (int h = 0; h < tileDescription.height; h++) {
                    
            		// Initialise object tile.
            		mTile[tileDescription.x + w][tileDescription.y + h].index = tileIndex;
                    mTile[tileDescription.x + w][tileDescription.y + h].tileIndex = tileDescription.tile;
                    mTile[tileDescription.x + w][tileDescription.y + h].tilesetIndex = tileDescription.tileset;
                    mTile[tileDescription.x + w][tileDescription.y + h].width = tileDescription.width - w;
                    mTile[tileDescription.x + w][tileDescription.y + h].height = tileDescription.height - h;
                    
                    // Initialise a random tile index for tiled terrain.
                    if (isTiledTerrain(tileDescription.tile, tileDescription.tileset))
                    	mTile[tileDescription.x + w][tileDescription.y + h].repeatIndex = (int)(Math.random() * 4)+1;
                    else 
                        mTile[tileDescription.x + w][tileDescription.y + h].repeatIndex = 0;
              
                    // Empty blocks have a count of 99 to indicate an empty block.
                    if (isBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 99;
  
                    // Initialise coin / power-up blocks item count..
                    if (isCoinBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    else if (is10CoinBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 10;
                    else if (isMushroomBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    else if (isFireBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    else if (isInvincibleBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    else if (isExtraLifeBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
                    else if (isVineBlock(tileDescription.tile, tileDescription.tileset))
                        mTile[tileDescription.x + w][tileDescription.y + h].count = 1;
         
                    // Initialise collision state.
                    mTile[tileDescription.x + w][tileDescription.y + h].collision = tileDescription.collision;
                    mTile[tileDescription.x + w][tileDescription.y + h].state = 0;
                    mTile[tileDescription.x + w][tileDescription.y + h].offset = 0;
                    mTile[tileDescription.x + w][tileDescription.y + h].draw = 0;
                }
            }
            
            // Initialise tile drawing state.
            if (tileDescription.width == tileDescription.tileWidth && tileDescription.height == tileDescription.tileHeight)
                mTile[tileDescription.x][tileDescription.y].draw = 1;
            else {
            	
            	
        		// Loop through tiled width and height.
                for (int w = 0; w < tileDescription.width / tileDescription.tileWidth; w++) {
                    for (int h = 0; h < tileDescription.height / tileDescription.tileHeight; h++) {
                       
                    	// Initialise drawing state.
                    	mTile[tileDescription.x + (w * tileDescription.tileWidth)][tileDescription.y + (h * tileDescription.tileHeight)].draw = 1;
                    }
                }
            }
            
    	} catch(Exception e) {}
    }

    /**
     * Sets a entity tile.
     * @param entityIndex Index of the entity.
     * @param entityDescription EntityDescription object.
     */
    public void setEntityTile(int entityIndex, EntityDescription entityDescription) {

    	try {
    	
            // Loop through tiled width and height.
            for (int w = 0; w < entityDescription.tileWidth / 16; w++) {
                
            	for (int h = 0; h < entityDescription.tileHeight / 16; h++) {
                   
            		// Initialise the entity tile.
            		mTile[entityDescription.x + w][entityDescription.y + h].spriteIndex = entityIndex;
                    mTile[entityDescription.x + w][entityDescription.y + h].spriteTile = entityDescription.tile;
                    if (w == 0 && h == 0) 
                        mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw = 1;
                    else {
                        if (mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw != 1)
                            mTile[entityDescription.x + w][entityDescription.y + h].spriteDraw = 0;
                    }
                }
            }  
    	} catch(Exception e) {}
    }

    /**
     * Sets the collision state of a tile.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param collision Collision state (0-no collision 1-collision).
     */
    public void setCollisionTile(int x, int y, int collision) {
        mTile[x][y].collision = 1;
    }

    /**
     * Gets the height of the map.
     * @return Height of the map.
     */
    public int getHeight() {
        return mHeader.height;
    }

    /**
     * Gets the width of the map.
     * @return Width of the map.
     */
    public int getWidth() {
        return mHeader.width;
    }

    /**
     * Gets the end x coordinate.
     * @return Level end x coordinate.
     */
    public int getEndX() {
        return mHeader.endX;
    }
    
    /**
     * Gets the start level x coordinate.
     * @return Start level x coordinate.
     */
    public int getStartX() {
        return mHeader.startX;
    }

    /**
     * Gets the start level y coordinate.
     * @return Start level y coordinate.
     */
    public int getStartY() {
        return mHeader.startY;
    }  
    
    /**
     * Gets the players bonus state.
     * @return Players bonus state.
     */
    public PipeState getStartState() {
        return PipeState.values()[mHeader.startState + 1];
    }
        
    /**
     * Gets the level end y coordinate
     * @return Level end y coordinate.
     */
    public int getEndY() {
        return mHeader.endY;
    }

    /**
     * Gets the players end state.
     * @return Players end state.
     */
    public PipeState getEndState() {
        return PipeState.values()[mHeader.endState + 1];
    }
    
    /**
     * Gets the bonus level x coordinate.
     * @return Bonus level x coordinate.
     */
    public int getBonusX() {
        return mHeader.bonusX;
    }

    /**
     * Gets the bonus level y coordinate.
     * @return Bonus level y coordinate.
     */
    public int getBonusY() {
        return mHeader.bonusY;
    }

    /**
     * Gets the players bonus state.
     * @return Players bonus state.
     */
    public PipeState getBonusState() {
        return PipeState.values()[mHeader.bonusState + 1];
    }

    /**
     * Is this terrain tiled.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile has more than one frame; otherwise false.
     */
    public static boolean isTiledTerrain(int tile, int tileset) {
        
    	if (tileset == 16 && (tile == 2 || tile == 4 || tile == 5 || tile == 6 || tile == 8))
            return true;
        else
            return false;
    	
    }

    /**
     * Is the tile a block which can be hit.
     * @param tile Tile index,
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block which can be hit; otherwise false.
     */
    public static boolean isBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false; 
        
        if ((tile >= 20 && tile < 50)) 
            return true;
        else
            return false;
 
    }

    /**
     * Is the tile animated.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile contains more than one frame of animation; otherwise false.
     */
    public static boolean isAnimated(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
       
        if ((tile >= 20 && tile < 40) || tile == 10 || tile == 46)
            return true;
        else
            return false;
    }

    /**
     * Is the tile an invisible block.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is an invisible block; otherwise false.
     */
    public static boolean isInvisible(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
     	
        if (tile == 40 || tile == 41)
            return true;
        else
            return false;

    }

    /**
     * Is the tile a coin.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a coin; otherwise false.
     */
    public static boolean isCoin(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
       
        if (tile == 10)
            return true;
        else 
            return false;

    }

    /**
     * Is the tile an empty block.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is an empty block; otherwise false.
     */
    public static boolean isEmptyBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
        
        if (tile == 20 || tile == 30)
            return true;
        else
            return false;

    }

    /**
     * Is the tile a block containing a coin.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block containing a coin; otherwise false.
     */
    public static boolean isCoinBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
        
        if (tile == 21 || tile == 31 || tile == 40)
            return true;
        else
            return false;

    }

    /**
     * Is the tile a block containing 10 coins.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block containing 10 coins; otherwise false.
     */
    public static boolean is10CoinBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
        
        if (tile == 22 || tile == 32)
            return true;
        else 
            return false;

    }

    /**
     * Is the tile a block containing a mushroom power up.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block containing a enlarge power up; otherwise false.
     */
    public static boolean isMushroomBlock(int tile, int tileset) {
        
    	if (tileset != 0) 
            return false;
        
        if (tile == 23 || tile == 33)
            return true;
        else
            return false;
       
    }

    /**
     * Is the tile a block containing a fire power up.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block containing a fire power up; otherwise false.
     */
    public static boolean isFireBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;
        
        if (tile == 24 || tile == 34)
            return true;
        else
            return false;
        
    }

    /**
     * Is the tile a block containing a invincible power up.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery)
     * @return True if the tile is a block containing a invincible power up; otherwise false.
     */
    public static boolean isInvincibleBlock(int tile, int tileset) {
        
    	if (tileset != 0) 
            return false;

        if (tile == 25 || tile == 35 || tile == 41)
            return true;
        else
            return false;

    }

    /**
     * Is the tile a block containing a extra life.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery).
     * @return True if the tile is a block containing a extra life; otherwise false.
     */
    public static boolean isExtraLifeBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;

        if (tile == 26 || tile == 36)
            return true;
        else
            return false;

    }

    /**
     * Is the tile a block containing a growing vine.
     * @param tile Tile index.
     * @param tileset Tileset index (0-block, 16-terrain, 32-scenery)
     * @return True if the tile is a block containing a growing vine; othewise false.
     */
    public static boolean isVineBlock(int tile, int tileset) {
        
    	if (tileset != 0)
            return false;

        if (tile == 27 || tile == 37)
            return true;
        else
            return false;

    }
}