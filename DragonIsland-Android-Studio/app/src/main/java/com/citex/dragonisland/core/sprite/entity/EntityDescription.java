package com.citex.dragonisland.core.sprite.entity;

/**
 * EntityDescription.java
 * This class stores entity information.
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