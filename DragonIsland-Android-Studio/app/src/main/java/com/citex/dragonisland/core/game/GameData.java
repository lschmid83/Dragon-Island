package com.citex.dragonisland.core.game;

/**
 * GameData.java
 * This class stores the game progress data.
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

public class GameData {

	/** Game folder index. */
	public int game;
	
	/** World. */
	public int world;
	
	/** Level. */
	public int level;
	
	/** Character index. */
	public int character;
	
	/** Player size. */
	public int size;
	
	/** Number of lives. */
	public int lives;
	
	/** Coin count. */
	public int coins;
	
	/** Score. */
	public int score;
	
	/**
	 * Initialises a GameData object.
	 */
	public GameData() {
		world = 1;
		level = 1;
		character = 0;
		size = 0;
		lives = 3;
		coins = 0;
		score = 0;
	}
	
}

