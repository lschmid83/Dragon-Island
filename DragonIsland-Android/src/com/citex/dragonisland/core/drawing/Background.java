package com.citex.dragonisland.core.drawing;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.util.FileIO;

/**
	This class draws a scrolling background.
	@author Lawrence Schmid
*/
public class Background {
    
	/** Background image index. */
	private int[] mIndex;
	
    /** Background images. */
    private IBufferedImage mImage[];
    
    /** Background layer x position. */
    private float mX[];
    
    /** Background layer y position. */
    private float mY[];
    
    /** Width of the level. */
    private int mWidth;
    
    /** Height of the level. */
    private int mHeight;
    
    /** Scroll speed. */
    private float mScroll[];
    
    /** Alignment. */
    private int mAlign;
    
    /** Background colour. */
    private Color mColor;
     
    /**
     * Initialises a Background object.
     * @param g Graphics context.
     * @param index Array of image indexes. [0]-near, [1]-middle, [2]-far
     * @param scroll Scroll speed of layers.
     * @param align Background alignment. 0 - horizontal, 1 - vertical
     * @param width Level width.
     * @param height Level Height.
     * @throws IOException 
     */
    public Background(Object g, int index[], float scroll[], int align, int width, int height) throws IOException {

    	// Initialise layer positions.
    	mX = new float[3];
    	mY = new float[3];
    	
    	// Initialise level size.
    	mWidth = width;
    	mHeight = height;
    	
    	// Initialise background variables.
        mAlign = align;
        mScroll = scroll;
        mIndex = index;
        
        // Initialise background layer images.
        mImage = new IBufferedImage[3];
		mImage[0] = FileIO.getImageResource(g, "bgr/near/" + mIndex[0] + ".png");
        mImage[1] = FileIO.getImageResource(g, "bgr/middle/" + mIndex[1] + ".png");
        mImage[2] = FileIO.getImageResource(g, "bgr/far/" + mIndex[2] + ".png"); 

    	// Read the background colour.
    	String[] colour = FileIO.readLine("bgr/far/" + mIndex[2] + ".ini").split(",");
    	mColor = new Color(Integer.parseInt(colour[0]), Integer.parseInt(colour[1]), Integer.parseInt(colour[2]));   	

    	// Initialise the background layer positions.
    	setHeight(height);
    }

    /**
     * Sets the speed of a background layer
     * @param layer The background layer 0-near, 1-middle 2-far
     * @param speed The horizontal scroll speed
     */
    public void setLayerSpeed(int layer, float speed) {
        mScroll[layer] = speed;
    }
    
    /**
     * Paints the background.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera coordinates.
     * @return Camera coordinates within the screen area.
     */
    public Point draw(Object g, float dt, Point cam) {

    	// Keep the camera within the level area.
    	if (cam.x >= mWidth - Settings.ScreenWidth)
            cam.x = mWidth - Settings.ScreenWidth;
    	else if (cam.x <= 0)
            cam.x = 0;
       	
        if (cam.y >= mHeight - Settings.ScreenHeight)
            cam.y = mHeight - Settings.ScreenHeight;        
        else if (cam.y <= 0)
            cam.y = 0;

        if(!Settings.Background)
        	return cam;
        
        // Set the position of background.
        int bgX = (int)cam.x - ((int)cam.x / 512 * 512);
        int bgY = (int)cam.y - ((int)cam.y / 512 * 512);

        // Scroll background layers.
    	if(!Settings.Paused && Settings.Animation) {
	        for (int i = 0; i < 3; i++) {
	    		if (mX[i] < 512) {
	    			mX[i] += mScroll[i] * dt / 60; 
	    		} else {
	    			mX[i] = 0;
	    		}
	    	}
    	}

        // Draw background.
        if (mAlign == 0) {
        	
        	// Far.
            if(mIndex[2] != 0) {
            	mImage[2].draw(g, -bgX - mX[2], mY[2] - cam.y);
            	mImage[2].draw(g, -bgX - mX[2] + 512, mY[2] - cam.y);
            	mImage[2].draw(g, -bgX - mX[2] + 1024, mY[2] - cam.y);
            }

            // Middle.
            if(mIndex[1] != 0) {
            	mImage[1].draw(g, -bgX - mX[1], mY[1] - cam.y);
            	mImage[1].draw(g, -bgX - mX[1] + 512, mY[1] - cam.y);
            	mImage[1].draw(g, -bgX - mX[1] + 1024, mY[1] - cam.y);
            }

            // Near.
            if(mIndex[0] != 0) {
            	mImage[0].draw(g, -bgX - mX[0], mY[0] - cam.y);
            	mImage[0].draw(g, -bgX - mX[0] + 512, mY[0] - cam.y);
            	mImage[0].draw(g, -bgX - mX[0] + 1024, mY[0] - cam.y);
            }

        } else {
        	// Far.
            mImage[2].draw(g, 0, -bgY);
            mImage[2].draw(g, 0, -bgY + 512);
            mImage[2].draw(g, 0, -bgY + 1024);
        }
        return cam;
    }   

    /**
     * Sets the background colour.
     * @param colour Background colour.
     */
    public void setColor(Color colour) {
        mColor = colour;
    }
    /**
     * Gets the background colour.
     * @return Background colour.
     */
    public Color getColor() {
        return mColor;
    }
    
    /**
     * Sets the alignment of the background.
     * @param align Background alignment. 0 - horizontal, 1 - vertical
     */
    public void setAlign(int align) {
        mAlign = align;
    }

    /**
     * Sets the width of the background
     * @param width The width of the background
     */
    public void setWidth(int width) {
        mWidth = width;
    }
    
    /**
     * Sets the height of the background.
     * @param height Height of the background.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public void setHeight(int height) throws IOException {
    	    	
        // Read background layer heights.
        int[] imageHeight = new int[3];
        imageHeight[0] = Integer.parseInt(FileIO.readLine("bgr/near/" + mIndex[0] + ".ini").split(",")[3]);
        imageHeight[1] = Integer.parseInt(FileIO.readLine("bgr/middle/" + mIndex[1] + ".ini").split(",")[3]);  		
        imageHeight[2] = Integer.parseInt(FileIO.readLine("bgr/far/" + mIndex[2] + ".ini").split(",")[3]);  		
    	
        // Initialise layer height.
        mY = new float[3];
        mY[0] = height - imageHeight[0];
        mY[1] = height - imageHeight[1];
        mY[2] = height - imageHeight[2];
    }

    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl) {
        mImage[0].destroy(gl);
        mImage[1].destroy(gl);
        mImage[2].destroy(gl);
    }
}
