package com.citex.dragonisland.core.game;

/**
 * Score.java
 * This class stores information about points displayed on the screen.
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

public class Score {

	/** Number of points collected. */
    public int value;
    
    /** Time between updates. */
    public int timer;
    
    /** X coordinate. */
    public int x;
    
    /** Y coordinate. */
    public int y;
    
    /**
     * Initialise a Score object.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param value Number of points collected.
     */
    public Score(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
        
}