package com.citex.dragonisland.core.game;

/**
 * This class stores the game progress data.
 * @author Lawrence Schmid
 */
public class GameData {

	/** Game folder index. */
	public int game;
	
	/** World. */
	public int world;
	
	/** Level. */
	public int level;
	
	/** Character index. */
	public int character;
	
	/** Player size. */
	public int size;
	
	/** Number of lives. */
	public int lives;
	
	/** Coin count. */
	public int coins;
	
	/** Score. */
	public int score;
	
	/**
	 * Initialises a GameData object.
	 */
	public GameData() {
		world = 1;
		level = 1;
		character = 0;
		size = 0;
		lives = 3;
		coins = 0;
		score = 0;
	}
	
}

