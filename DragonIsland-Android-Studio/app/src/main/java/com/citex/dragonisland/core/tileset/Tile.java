package com.citex.dragonisland.core.tileset;

/**
 * Tile.java
 * This class is a level map tile description.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class Tile {

	/** Tile description index. */
	public int index; 
	
	/** Tile index. */
	public int tileIndex; 
	
	/** Tileset index. */
	public int tilesetIndex;   
	
	/** Tile number for tiled terain. */
	public int repeatIndex;  
	
	/** Tile state (0-normal, 1-collide, 2-empty). */
	public int state; 
	
	/** Item count */
	public int count; 
	
	/** Collision type. (0-none, 1-normal, 2-invisible) */
	public int collision; 
	
	/** Tile offset when hit. */
	public int offset; 
	
	/** Should the tile be drawn. */
	public int draw; 
	   
	/** Sprite tile description index. */
	public int spriteIndex;
	
	/** Sprite tile index. */
	public int spriteTile;
	
	/** Should the sprite tile be drawn. */
	public int spriteDraw;
	
	/** Tiled width. */
	public int width;
	
	/** Tiled height. */
	public int height;
	
	/** Direction of a sprite tile. */
	public char direction;
	
	/** Angle of a sprite tile. */
	public int angle;
	
	/**
	 * Gets whether tile is a gap.
	 * @return True if it is a gap; otherwise false.
	 */
	public boolean isGap() {
		
		if(collision == 0 || collision == 2 || collision == 3) 
			return true;
		else
			return false;
	
	}

}