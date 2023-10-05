package com.citex.dragonisland.android.audio;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.core.audio.IMusic;
import com.citex.dragonisland.core.game.Settings;

/**
 * This class and plays MP3 files.
 * @author Lawrence Schmid
 */
public class Music implements IMusic {
	
	/** Path of the mp3 file. */
	private static String mPath;
	
	/** Media player. */
	private static MediaPlayer mPlayer;

	/**
	 * Default constructor.
	 */
	public Music() {}
	
	/**
	 * Plays a MP3 file.
	 */
	public void play(String path) {
		
    	try {
			
			if(Settings.Music && !path.equals(mPath)) {
				
				// Set music path.
				mPath = path;
				
	    		// Initialise the media player.
	    		mPlayer = new MediaPlayer();
	    		mPlayer.reset();
	    		
	    		// Set the data source.
	    		AssetFileDescriptor afd = MainActivity.mAssetManager.openFd(path);
				mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				afd.close();
				
				// Set volume and looping.
				mPlayer.setLooping(true);
				mPlayer.setVolume(1.0f, 1.0f);
				
				// Prepare the media player.
				mPlayer.prepareAsync();
				
				// Add a on prepared listener.
				mPlayer.setOnPreparedListener(new OnPreparedListener() {
			        public void onPrepared(MediaPlayer mp) {       
			        	
			        	// Start the play back.
			        	mPlayer.start();
			        }
			    });
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

    /**
     * Stops the play back and destroys resources.
     */
    public void stop()
    {
    	// Release resources attached to media player.
    	if(mPlayer != null) {
    		mPlayer.release();
    		mPath = null;
    	}
    }
}
