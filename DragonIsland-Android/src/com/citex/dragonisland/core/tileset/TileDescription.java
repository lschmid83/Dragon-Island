package com.citex.dragonisland.core.tileset;

/**
 * This class stores the description of a tile read from the .ini file.
 * @author Lawrence Schmid
 */
public class TileDescription {

	/** Name of the tile. */
	public String name;
	
	/** X coordinate in the sprite sheet. */
	public int x;
	
	/** Y coordinate in the sprite sheet. */
	public int y;
	
	/** Width of the tile in the sprite sheet */
	public int width;
	
	/** Height of the tile. */
	public int height;
	
	/** Tile index. */
	public int tile;
	
	/** Tileset index. */
	public int tileset;
	
	/** Tile width. */
	public int tileWidth;
	
	/** Tile height. */
	public int tileHeight;
	
	/** Collision type. */
	public int collision;
};
