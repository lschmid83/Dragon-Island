package com.citex.applet.dragonisland;

/**
	This class stores keyboard control variables.
	
	@version 1.0
	@modified 20/04/2012
	@author Lawrence Schmid<BR><BR>
	
	This file is part of Dragon Island.<BR><BR>
	
	Dragon Island is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.<BR><BR>
	
	Dragon Island is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.<BR><BR>
	
	You should have received a copy of the GNU General Public License
	along with Dragon Island. If not, see http://www.gnu.org/licenses/.<BR><BR>
	
	Copyright 2012 Lawrence Schmid
*/

public class Control {
	
	/** The left key is pressed (left arrow) */
    public boolean mLeftPressed;
    /** The right key is pressed (right arrow) */
    public boolean mRightPressed;
    /** The up key is pressed (up arrow)*/
    public boolean mUpPressed;
    /** The down key is pressed (down arrow) */
    public boolean mDownPressed;
    /** The run key is pressed (q / a / ctrl) */
    public boolean mRunPressed;
    /** The run key is released */
    public boolean mRunReleased = true; 
    /** The jump key is pressed (w / s / space) */
    public boolean mJumpPressed;
    /** The jump key is released */
    public boolean mJumpReleased = true;
    /** The amount of time the direction key is held */
    public int mDirectionHeld; 
}
