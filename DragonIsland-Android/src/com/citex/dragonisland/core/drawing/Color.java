package com.citex.dragonisland.core.drawing;

/**
 * This class stores RGBA color information.
 * @author Lawrence Schmid
 */
public class Color {
	
	/** Red intensity. */
    private int mRed; 
    
    /** Green intensity. */
    private int mGreen; 
    
    /** Blue intensity. */
    private int mBlue; 
    
    /** Alpha intensity. */
    private int mAlpha; 

    /**
     * Initialises a Color object with an alpha channel.
     * @param red Red intensity.
     * @param green Green intensity.
     * @param blue Blue intensity.
     * @param alpha Alpha intensity.
     */
    public Color(int red, int green, int blue, int alpha) {
    	mRed = red;  
        mGreen = green;   
        mBlue = blue;    
        mAlpha = alpha;
    }

    /**
     * Initialise a color object.
     * @param red Red intensity.
     * @param green Green intensity.
     * @param blue Blue intensity.
    */
    public Color(int red, int green, int blue) {
        mRed = red;
        mGreen = green;
        mBlue = blue;
    }

    /**
     * Gets the red intensity.
     * @return Red intensity.
     */
    public int getRed() {
        return mRed;
    }

    /**
     * Gets the green intensity.
     * @return Green intensity.
     */
    public int getGreen() {
        return mGreen;
    }

    /**
     * Gets the blue intensity
     * @return Blue intensity.
     */
    public int getBlue() {
        return mBlue;
    }

    /**
     * Gets the alpha intensity.
     * @return Alpha intensity
    */
    public int getAlpha() {
        return mAlpha;
    }
}
