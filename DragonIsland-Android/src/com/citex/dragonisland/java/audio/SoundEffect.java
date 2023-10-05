package com.citex.dragonisland.java.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.citex.dragonisland.applet.Main;
import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.game.Settings;

/**
	This class loads and plays the games wav sounds effects.
	@author Lawrence Schmid.
*/

public class SoundEffect extends Thread implements ISoundEffect {

	/** Path to the wav audio files. */
	private String mPath;
    
    /** Number of times to loop the sound effect. */
    private int mLoop;

    /**
     * Initialise a collection of sound effects.
     * @param path Path to the wav sound effect files.
     * @throws LineUnavailableException 
     * @throws IOException 
     * @throws UnsupportedAudioFileException 
     */
    public SoundEffect(String path) {
    	mPath = path;
    }      
	
	/**
	 * Plays a sound effect 
	 * @param soundName The name of the wav sound file
	 * @throws LineUnavailableException 
	 */
	public void play(String soundName) {
	
        if(Settings.Sound)
        {
	    	try {
	            
	            // Open an input stream.
	            InputStream is = Main.class.getClassLoader().getResourceAsStream(mPath + soundName);
	            
	            // Add buffer for mark/reset support.
	            InputStream bufferedIn = new BufferedInputStream(is);
	            
	            // Open audio input stream.
	            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
		            
	            // Get a sound clip resource.
	            Clip clip = AudioSystem.getClip();
	            
	            // Open audio clip and load samples from the audio input stream.
	            clip.open(audioIn);
	            
	            // Loop the sound clip a number of times.
	            clip.loop(mLoop);
	
	        } catch (UnsupportedAudioFileException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (LineUnavailableException e) {
	            e.printStackTrace();
	        }
        }	

	}

}
