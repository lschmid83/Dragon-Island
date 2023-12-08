package com.citex.dragonisland.core.tileset;

/**
 * TileDesription.java
 * This class stores the description of a tile read from the .ini file.
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
