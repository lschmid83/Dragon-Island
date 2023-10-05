package com.citex.dragonisland.java.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;

import com.citex.dragonisland.applet.Main;
import com.citex.dragonisland.core.audio.IMusic;
import com.citex.dragonisland.core.game.Settings;

/**
	This class loads an mp3 file and plays it in a loop.
	@author Lawrence Schmid
*/
public class Music implements IMusic {
	
	/** Path of the mp3 file. */
	private static String mPath;
	
	/** MP3 player library. */
	private static JLayerMp3Player mPlayer;

	/**
	 * Stops the JLayerMp3Player.
	 */
	public void stop() {
		if(mPlayer != null) {
			mPlayer.close();
			mPath = null;
		}
	}

	/**
	 * Play the mp3 file
	 * @param path Path of the MP3 file.
	 */
	public void play(String path) {

		try {
			if(Settings.Music && !path.equals(mPath)) {
				mPath = path;
				InfoListener lst = new InfoListener();
				playMp3(Settings.ResourcePath + path, lst);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		

	}

	/**
	 * Play the mp3 file
	 * @param mp3
	 * @param listener
	 * @return
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static JLayerMp3Player playMp3(String mp3, PlaybackListener listener)
			throws IOException, JavaLayerException {
		return playMp3(mp3, 0, Integer.MAX_VALUE, listener);
	}

	/**
	 * Play the mp3 file specifying start and end time
	 * @param mp3
	 * @param start
	 * @param end
	 * @param listener
	 * @return
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static JLayerMp3Player playMp3(String mp3, int start, int end,
			PlaybackListener listener) throws IOException, JavaLayerException {
    	InputStream is = Main.class.getClassLoader().getResourceAsStream(mp3); 
     		
		return playMp3(new BufferedInputStream(is),
				start, end, listener);
	}

	/**
	 * Play the mp3 file in a thread 
	 * @param is
	 * @param start
	 * @param end
	 * @param listener
	 * @return
	 * @throws JavaLayerException
	 */
	public static JLayerMp3Player playMp3(final InputStream is, final int start,
			final int end, PlaybackListener listener) throws JavaLayerException {
		
		mPlayer = new JLayerMp3Player(is);
		mPlayer.setPlayBackListener(listener);
		// run in new thread
		new Thread() {
			public void run() {
				try {
					mPlayer.play(start, end);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		}.start();
		return mPlayer;

	}

	/**
	 * JLayerMp3Player event listener 
	 */
	public class InfoListener extends PlaybackListener {
		
		public void playbackStarted(PlaybackEvent evt) {
			// System.out.println("Play started from frame " + evt.getFrame());
		}
		
		public void playbackFinished(PlaybackEvent evt) {
			InfoListener lst = new InfoListener();
			try {
				playMp3(mPath, lst);
			} catch (Exception e) {
			}
			// System.out.println("Play completed at frame " + evt.getFrame());
		}
	}
}
