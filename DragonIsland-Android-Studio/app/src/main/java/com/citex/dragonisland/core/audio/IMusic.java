package com.citex.dragonisland.core.audio;

/**
 * IMusic.java
 * Interface for playing mp3 music.
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

public interface IMusic {

	/**
	 * Plays a MP3 file.
	 * @param path Path to MP3 file.
	 */
	public void play(String path);
    
    /**
     * Stops the play back and destroys resources.
     */
	public void stop();
	
}
