package com.citex.applet.dragonisland;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;

/**
	This class loads an mp3 file and plays it in a loop.
	
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

public class Music {
	
	/** The path of the mp3 file */
	private static String mPath;
	/** Controls the playback of audio files */
	private static JLayerMp3Player mPlayer;

	/**
	 * Constructs the Music
	 * @param path The path of the mp3 file
	 */
	public Music(String path) {
		mPath = path;
		try {
			if(Settings.Music)
				play(path);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Stops the playback
	 */
	public static void stopMusic() {
		if(mPlayer != null)
			mPlayer.pause();
	}

	/**
	 * Closes the JLayerMp3Player
	 */
	public static void closeMusic() {
		if(mPlayer != null)
			mPlayer.close();
	}
		
	/**
	 * Starts the playback
	 */
	public static void startMusic() {
		if(mPlayer != null)
			mPlayer.start();
	}

	/**
	 * Play the mp3 file
	 * @param filename The path of the mp3 file
	 * @throws JavaLayerException
	 * @throws IOException
	 */
	public void play(String filename) throws JavaLayerException, IOException {
		InfoListener lst = new InfoListener();
		playMp3(filename, lst);
	}

	/**
	 * Shows details of the the mp3 file in use
	 */
	public void showUsage() {
		System.out.println("Usage: jla <filename>");
		System.out.println("");
		System.out.println(" e.g. : java javazoom.jl.player.advanced.jlap localfile.mp3");
	}

	/**
	 * Play the mp3 file
	 * @param mp3
	 * @param listener
	 * @return
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public static JLayerMp3Player playMp3(String path, PlaybackListener listener)
			throws IOException, JavaLayerException {
		return playMp3(path, 0, Integer.MAX_VALUE, listener);
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
	public static JLayerMp3Player playMp3(String path, int start, int end,
			PlaybackListener listener) throws IOException, JavaLayerException {
    	InputStream is = Main.class.getResourceAsStream(path); 
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
