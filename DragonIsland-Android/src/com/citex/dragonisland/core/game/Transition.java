package com.citex.dragonisland.core.game;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.util.FileIO;

/**
 * This class draws a screen transition.
 * @author Lawrence Schmid
 */
public class Transition {
	
	/** Transition image. */ 
	private IBufferedImage mTransition;
	
	/** Transition timer. */
	private float mTimer;
	
    /** Indicates if the transition is running. */
    private boolean mRunning;

    /**
	 * Initialises a Transition object.
	 * @param g Graphics context.
	 * @param path Path of the image.
     * @throws IOException 
	 */
	public Transition(Object g, String path) throws IOException {
		mTransition = FileIO.getImageResource(g, path);
		mTimer = 0;
	}
	
	/** 
	 * Paints the transition.
	 * @param g Graphics context.
	 * @param dt Delta time.
	 */
	public void draw(Object g, float dt) {

		if(mRunning) {
		
			if(dt < Settings.ScreenHeight) {

				mTransition.draw(g, 0, mTimer);
		        
				if (mTimer + dt < Settings.ScreenHeight)
		            mTimer += dt * 0.55f;  
				else
					mRunning = false;
		        
			}
		}
        
		
	}
	
    /**
     * Indicates if the thread has finished executing.
     * @return True if finished; otherwise false.
     */
    public boolean isFinished() {
    	return !mRunning;
    }	
	
    /**
     * Starts the transition.
     */
	public void start() {
		mTimer = 0;
		mRunning = true;
	}
		
	/**
	 * Stops the transition.
	 */
	public void stop() {
		mTimer = 0;
		mRunning = false;
	}
	
	/** 
	 * Returns the timer.
	 * @return Integer.
	 */
	public float getTimer() {
		return mTimer;
	}
	
    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl) {
    	
    	// Destroy transition.
    	if(mTransition != null)
    		mTransition.destroy(gl);
    
    }
}
