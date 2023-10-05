package com.citex.dragonisland.android.audio;

import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.game.Settings;

/**
 * This class plays wav sounds effects.
 * @author Lawrence Schmid
 */
public class SoundEffect implements ISoundEffect {

	/** Sound pool. */
	private static SoundPool mSoundPool;
	
	/** Sound pool map. */
	private static HashMap<Integer, Integer> mSoundPoolMap;
	
	/**
	 * Initialise a collection of sound effects.
	 * @param path Path to the wav sound effect files.
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@SuppressLint("UseSparseArrays") 
	public SoundEffect(String path) throws IllegalStateException, IOException {

		// Initialise the sound pool.
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		
		// Sound pool map.
		mSoundPoolMap = new HashMap<Integer, Integer>(12);  
		
		// Load sound effects from assets.
		AssetFileDescriptor afd = MainActivity.mAssetManager.openFd(path + "block.wav");
		mSoundPoolMap.put(1, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "coin.wav");
		mSoundPoolMap.put(2, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "die.wav");
		mSoundPoolMap.put(3, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "fire.wav");
		mSoundPoolMap.put(4, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "hit.wav");
		mSoundPoolMap.put(5, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "jump.wav");
		mSoundPoolMap.put(6, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "kill.wav");
		mSoundPoolMap.put(7, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "option.wav");
		mSoundPoolMap.put(8, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "powerdown.wav");
		mSoundPoolMap.put(9, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "powerup.wav");
		mSoundPoolMap.put(10, mSoundPool.load(afd, 1));
		
		afd = MainActivity.mAssetManager.openFd(path + "select.wav");
		mSoundPoolMap.put(11, mSoundPool.load(afd, 1));

	}

	/**
	 * Plays a wav sound effect 
	 * @param soundName File name of the sound effect.
	 */
	public void play(String soundName) {
		
		// Initialise sound properties.
		float leftVolume = 0.5f;
		float rightVolume = 0.5f;
		int priority = 1;
		int loop = 0;
		float rate = 1.0f;
		
		if (Settings.Sound) {

			if (soundName.equals("block.wav"))
				mSoundPool.play(mSoundPoolMap.get(1), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("coin.wav"))
				mSoundPool.play(mSoundPoolMap.get(2), leftVolume - 0.05f, rightVolume - 0.05f, priority, loop, rate);
			else if (soundName.equals("die.wav"))
				mSoundPool.play(mSoundPoolMap.get(3), leftVolume - 0.05f, rightVolume - 0.05f, priority, loop, rate);
			else if (soundName.equals("fire.wav"))
				mSoundPool.play(mSoundPoolMap.get(4), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("hit.wav"))
				mSoundPool.play(mSoundPoolMap.get(5), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("jump.wav"))
				mSoundPool.play(mSoundPoolMap.get(6), leftVolume - 0.1f, rightVolume - 0.1f, priority, loop, rate);
			else if (soundName.equals("kill.wav"))
				mSoundPool.play(mSoundPoolMap.get(7), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("option.wav"))
				mSoundPool.play(mSoundPoolMap.get(8), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("powerdown.wav"))
				mSoundPool.play(mSoundPoolMap.get(9), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("powerup.wav"))
				mSoundPool.play(mSoundPoolMap.get(10), leftVolume, rightVolume, priority, loop, rate);
			else if (soundName.equals("select.wav"))
				mSoundPool.play(mSoundPoolMap.get(11), leftVolume, rightVolume, priority, loop, rate);
			
		}
	}

    /**
     * Destroys the resources.
     */
	public void destroy() {
		
		// Destroy sound effects.
		mSoundPool.release();
		
	}
}
