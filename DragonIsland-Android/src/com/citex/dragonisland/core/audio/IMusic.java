package com.citex.dragonisland.core.audio;

/**
 * Interface for playing mp3 music.
 * @author Lawrence Schmid
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
