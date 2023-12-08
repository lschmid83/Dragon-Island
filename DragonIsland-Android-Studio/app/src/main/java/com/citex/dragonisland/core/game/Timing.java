package com.citex.dragonisland.core.game;

/**
 * Timing.java
 * This class calculates delta time between frame updates.
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

public class Timing {

	/** Current time. */
	private long mCurrentTime;
	
	/** Last update time. */
	private long mLastUpdateTime;	
	
	/**
	 * Gets the delta time between frame updates.
	 * @return Delta time.
	 */
    public float getDeltaTime() {
		
    	// Calculate delta time.
    	float dt = getCurrentTime() - getLastUpdateTime();
    	
    	// Fix for java timing.
    	//if(Settings.Mode != GameMode.ANDROID)
    	//	dt += 0.05f * dt;
    	
    	if(Settings.GameSpeed > 0)
    		dt *= Settings.GameSpeed;
    	
    	return dt; 	
    }
    
    /**
     * Gets the current time.
     * @return Current time in milliseconds.
     */
    public long getCurrentTime() {
    	return mCurrentTime;
    }
    
    /** 
     * Sets the current time.
     * @param time Current time in milliseconds.
     */
    public void setCurrentTime(long time) {
    	mCurrentTime = time;
    }

    /**
     * Gets the last frame update time.
     * @return Last frame update time in milliseconds.
     */
    public long getLastUpdateTime() {
    	return mLastUpdateTime;
    }
    
    /**
     * Sets the last frame update time.
     * @param time Last frame update time in milliseconds.
     */
    public void setLastUpdateTime(long time) {
    	mLastUpdateTime = time;
    }  

}
