package com.citex.applet.dragonisland;

import javax.sound.sampled.*;
import java.io.*;

/**
	This class loads and plays the games wav sounds effects.
	
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

public class SoundEffect extends Thread {

	/** The file path of the wav sound effect */
    private String mFilePath;
    /** The number of times to loop the sound effect */
    private int mLoop;

    /**
     * Constructs the SoundEffect
     * @param path The file path of the wav file
     */
    public SoundEffect(String path) {
        mFilePath = path;
        mLoop = 0;
    }

    /**
     * Constructs the SoundEffect
     * @param path The file path of the wav file
     * @param loop The number of times to loop the sound effect
     */
    public SoundEffect(String path, int loop) {
        mFilePath = path;
        mLoop = loop;
    }

    /**
     * Run thread and play sound effect
     */
    public void run() {
        if(Settings.Sound)
        {
	    	try {
	            //Open an audio input stream.	            
	        	InputStream is = Main.class.getResourceAsStream(mFilePath); 
	            AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
	            // Get a sound clip resource.
	            Clip clip = AudioSystem.getClip();
	            // Open audio clip and load samples from the audio input stream.
	            clip.open(audioIn);
	            //clip.start();
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
