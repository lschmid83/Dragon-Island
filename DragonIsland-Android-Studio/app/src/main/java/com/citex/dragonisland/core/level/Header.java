package com.citex.dragonisland.core.level;

import com.citex.dragonisland.core.drawing.Color;

/**
 * Header.java
 * This class stores level header information.
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

public class Header {

	// Level name.
	public String name;
	
	// World.
	public int world;
	
	// Level.
	public int level;
	
	// Area.
	public int area;
	
	// Time limit.
	public int timeLimit;
	
	// Background layer indexes.
	public int bg[] = new int[3];
	
	// Background color.
	public Color bgColor;
	
	// Background layer speed.
	public float bgSpeed[] = new float[3];
	
	// Background alignment.
	public int bgAlign;
	
	// Music file index.
	public int music;
	
	// Tileset (blocks).
	public int tileset0;
	
	// Tileset (terrain).
	public int tileset16;
	
	// Tileset (scenery).
	public int tileset32;
	
	// Level width.
	public int width;
	
	// Level height.
	public int height;
	
	// Object tile count.
	public int objectCount;
	
	// Entity tile count.
	public int entityCount;
	
	// Player start state.
	public int startState;
	
	// Player start X coordinate.
	public int startX;
	
	// Player start Y coordinate.
	public int startY;
	
	// Player end state.
	public int endState;
	
	// Player end X coordinate.
	public int endX;
	
	// Player end Y coordinate.
	public int endY;
	
	// End world number.
	public int endWorld;
	
	// End level number.
	public int endLevel;
	
	// End level area.
	public int endArea;
	
	// Player bonus X coordinate.
	public int bonusX;
	
	// Player bonus Y coordinate.
	public int bonusY;
	
	// Player bonus state.
	public int bonusState;
}