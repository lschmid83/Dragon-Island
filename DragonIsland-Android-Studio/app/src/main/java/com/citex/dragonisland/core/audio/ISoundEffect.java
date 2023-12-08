package com.citex.dragonisland.core.audio;

/**
 * ISoundEffect.java
 * Interface for playing wav sound effect.
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

public interface ISoundEffect {

	/**
	 * Plays a wav sound effect.
	 * @param path File name of the sound effect.
	 */
	public void play(String path);	
    
    /**
     * Destroys the resources.
     */
	public void destroy();
}
