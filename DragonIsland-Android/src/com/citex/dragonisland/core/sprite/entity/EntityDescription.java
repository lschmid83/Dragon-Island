package com.citex.dragonisland.core.sprite.entity;

/**
 * This class stores entity information.
 * @author Lawrence Schmid
 */
public class EntityDescription {

	/** Name of entity. */
	public String name;
	
	/** X coordinate. */
	public int x;
	
	/** Y coordinate. */
	public int y;
	
	/** Tile width. */
	public int tileWidth;
	
	/** Tile height. */
	public int tileHeight;
	
	/** Tile index. */
	public int tile;
	
	/** Is collision detection enabled. */
	public int collision;
	
	/** Direction. */
	public char direction;
	
	/** Angle. */
	public int angle;
}