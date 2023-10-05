package com.citex.dragonisland.core.game;

/**
 * This class stores the number of levels in a world.
 * @author Lawrence Schmid
 */
public class World {
	
	/** Number of levels in a world. */
	private int mLevels;
	
	/**
	 * Initialises a world object.
	 */
	public World() {
		mLevels = 1;	
	}
	
	/**
	 * Adds a level to the world.
	 */
	public void addLevel() {
		mLevels++;
		
	}
	
	/**
	 * Gets the number of levels in a world.
	 * @return
	 */
	public int getLevels() {
		return mLevels;		
	}
}