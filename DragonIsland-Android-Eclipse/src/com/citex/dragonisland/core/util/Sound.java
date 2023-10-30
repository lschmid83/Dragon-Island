package com.citex.dragonisland.core.util;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.citex.dragonisland.core.audio.IMusic;
import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;

/**
 * Helper functions for sound effects and music.
 * @author Lawrence Schmid
 */
public class Sound {

	/**
	 * Gets an instance of the Music player class.
	 * @return Object which implements IMusic.
	 */
	public static IMusic getMusicPlayer() {
		
		if(Settings.Mode == GameMode.ANDROID)
			return new com.citex.dragonisland.android.audio.Music();
		else 
			return new com.citex.dragonisland.java.audio.Music();
	}
	
	/**
	 * Gets an instance of the sound effect player class.
	 * @return Object which implements ISoundEffect.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws UnsupportedAudioFileException 
	 * @throws LineUnavailableException 
	 */
	public static ISoundEffect getSoundPlayer(String path) throws IllegalStateException, IOException, LineUnavailableException, UnsupportedAudioFileException {
		
		if(Settings.Mode == GameMode.ANDROID)
			return new com.citex.dragonisland.android.audio.SoundEffect(path);
		else
			return new com.citex.dragonisland.java.audio.SoundEffect(path);	
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