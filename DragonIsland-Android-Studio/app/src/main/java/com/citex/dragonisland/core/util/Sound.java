package com.citex.dragonisland.core.util;

import java.io.IOException;
import com.citex.dragonisland.core.audio.IMusic;
import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;

/**
 * Sound.java
 * Helper functions for sound effects and music.
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

public class Sound {

	/**
	 * Gets an instance of the Music player class.
	 * @return Object which implements IMusic.
	 */
	public static IMusic getMusicPlayer() {

		return new com.citex.dragonisland.android.audio.Music();
	}
	
	/**
	 * Gets an instance of the sound effect player class.
	 * @return Object which implements ISoundEffect.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	public static ISoundEffect getSoundPlayer(String path) throws IllegalStateException, IOException {
		return new com.citex.dragonisland.android.audio.SoundEffect(path);
	}
	
	/**
	 * Gets the file name of a sound effect from the index.
	 * @param soundIndex Sound file index.
	 * @return Sound effect file name.
	 */
	public static String getSoundEffect(int soundIndex) {
		
		String fileName = "";
		if(soundIndex == 0)
			fileName = "block.wav";
		else if(soundIndex == 1)
			fileName = "coin.wav";
		else if(soundIndex == 2) 
			fileName = "die.wav";
		else if(soundIndex == 3)
			fileName = "fire.wav";
		else if(soundIndex == 4)
			fileName = "hit.wav";
		else if(soundIndex == 5)
			fileName = "jump.wav";
		else if(soundIndex == 6)
			fileName = "kill.wav";
		else if(soundIndex == 7)
			fileName = "option.wav";
		else if(soundIndex == 8)
			fileName = "powerdown.wav";
		else if(soundIndex == 9)
			fileName = "powerup.wav";
		else if(soundIndex == 10)
			fileName = "powerup.wav";
		else if(soundIndex == 11)
			fileName = "select.wav";
				
		return fileName;
		
	}

}