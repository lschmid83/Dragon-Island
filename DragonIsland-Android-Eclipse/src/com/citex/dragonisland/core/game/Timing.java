package com.citex.dragonisland.core.game;

/**
 * This class calculates delta time between frame updates.
 * @author Lawrence Schmid
 */
public class Timing {

	/** Current time. */
	private long mCurrentTime;
	
	/** Last update time. */
	private long mLastUpdateTime;	
	
	/**
	 * Gets the delta time between frame updates.
	 * @return Delta time.
	 */
    public float getDeltaTime() {
		
    	// Calculate delta time.
    	float dt = getCurrentTime() - getLastUpdateTime();
    	
    	// Fix for java timing.
    	//if(Settings.Mode != GameMode.ANDROID)
    	//	dt += 0.05f * dt;
    	
    	if(Settings.GameSpeed > 0)
    		dt *= Settings.GameSpeed;
    	
    	return dt; 	
    }
    
    /**
     * Gets the current time.
     * @return Current time in milliseconds.
     */
    public long getCurrentTime() {
    	return mCurrentTime;
    }
    
    /** 
     * Sets the current time.
     * @param time Current time in milliseconds.
     */
    public void setCurrentTime(long time) {
    	mCurrentTime = time;
    }

    /**
     * Gets the last frame update time.
     * @return Last frame update time in milliseconds.
     */
    public long getLastUpdateTime() {
    	return mLastUpdateTime;
    }
    
    /**
     * Sets the last frame update time.
     * @param time Last frame update time in milliseconds.
     */
    public void setLastUpdateTime(long time) {
    	mLastUpdateTime = time;
    }  

}
