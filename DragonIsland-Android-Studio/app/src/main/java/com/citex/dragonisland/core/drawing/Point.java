package com.citex.dragonisland.core.drawing;

/**
 * Point.java
 * This class represents a point.
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

public class Point {
	
	/** X coordinate. */
	public float x;
	
	/** Y coordinate. */
	public float y;
	
	/**
	 * Initialises a Point object.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
