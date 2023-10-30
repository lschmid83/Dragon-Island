package com.citex.dragonisland.core.audio;

/**
 * Interface for playing wav sound effect.
 * @author Lawrence Schmid
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
