package com.citex.dragonisland.core.game;

/**
 * This class stores information about points displayed on the screen.
 * @author Lawrence Schmid
 */
public class Score {

	/** Number of points collected. */
    public int value;
    
    /** Time between updates. */
    public int timer;
    
    /** X coordinate. */
    public int x;
    
    /** Y coordinate. */
    public int y;
    
    /**
     * Initialise a Score object.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param value Number of points collected.
     */
    public Score(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
        
}