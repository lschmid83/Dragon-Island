package com.citex.dragonisland.core.game;

/**
 * World.java
 * This class stores the number of levels in a world.
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

public class World {
	
	/** Number of levels in a world. */
	private int mLevels;
	
	/**
	 * Initialises a world object.
	 */
	public World() {
		mLevels = 1;	
	}
	
	/**
	 * Adds a level to the world.
	 */
	public void addLevel() {
		mLevels++;
		
	}
	
	/**
	 * Gets the number of levels in a world.
	 * @return
	 */
	public int getLevels() {
		return mLevels;		
	}
}