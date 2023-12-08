package com.citex.dragonisland.core.sprite;

/**
 * Frame.java
 * This class stores frame information for the animation.
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

public class Frame {

    public int start;  //start frame
    public int end;    //end frame
    public int bX;     //bounding box 
    public int bY;
    public int bW;     
    public int bH;
    public String state;  //animation (walk, run, jump)
    public int index;
}