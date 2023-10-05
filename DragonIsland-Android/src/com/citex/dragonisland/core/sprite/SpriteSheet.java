package com.citex.dragonisland.core.sprite;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSprite;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.java.drawing.Image;

/**
    This class loads an image containing a sprite sheet and buffers 
    the frames of animation.
	@author Lawrence Schmid
*/

public class SpriteSheet {

	/** The sprite sheet containing the animation */
    private IBufferedImage mSpriteSheet; 
    
    /** The buffered frames of animation */
    private IBufferedImage mFrames[]; 
    
    /** The frame dimensions */
    private int mFrameWidth, mFrameHeight; 
    
    /** The sprite sheet dimensions */
    private int mSheetWidth, mSheetHeight; 
    
    /** The number of rows and columns in the sprite sheet */
    private int mRows, mColumns;
    
    /** The number of frames in the sprite sheet */
    private int mFrameCount; 

    /**
     * Constructs the sprite sheet
     * @param gl The GL context
     * @param path The path of the image file
     * @param frameWidth The frame width
     * @param frameHeight The frame height
     * @param frameCount The frame count
     * @throws IOException 
     */   
    public SpriteSheet(Object gl, String path, int frameWidth, int frameHeight, int frameCount) throws IOException {
       
    	mFrameWidth = frameWidth;
        mFrameHeight = frameHeight;
        mFrameCount = frameCount;

        if(Settings.Mode == GameMode.ANDROID)
        	mSpriteSheet = new GLSprite((GL10)gl, path, null);
        else        	
        	mSpriteSheet = new Image(path).getImage();
        
        if (mSpriteSheet != null) {
            mSheetWidth = mSpriteSheet.getWidth();
            mSheetHeight = mSpriteSheet.getHeight();
            mRows = mSheetHeight / (mFrameHeight);
            mColumns = mSheetWidth / (mFrameWidth);
        } else {
            mSheetWidth = 0;
            mSheetHeight = 0;
            mRows = 0;
            mColumns = 0;
            mFrameCount = 1;
        }

        //buffer frames from sheet
        mFrames = new IBufferedImage[mFrameCount];
        for (int i = 0; i < mFrameCount; i++) {
            mFrames[i] = getFrameFromSheet(gl, i); //store each frame in the array
        }
    }

    /**
     * Returns a frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The frame of animation
     */
    public IBufferedImage getFrameFromSheet(Object gl, int frameNumber) {
        try {
            if (mRows > 0 && mColumns > 0) {
                int col = frameNumber / mRows; //column
                int row = frameNumber - (col * mRows); //row
                return mSpriteSheet.getSubImage(gl, col * mFrameWidth,
                        row * mFrameHeight, mFrameWidth, mFrameHeight);
            } else //only one column in sheet
            {
                return mSpriteSheet.getSubImage(gl, 0, frameNumber * 16, mFrameWidth, mFrameHeight);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, float x, float y) {   	
    	mFrames[frameNumber].draw(g, x, y);
    }

    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, float x, float y, float w, float h) {   	
    	mFrames[frameNumber].draw(g, x, y, w, h);
    }    
    
    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, char direction, int x, int y) {   	
    	mFrames[frameNumber].draw(g, direction, x, y);
    }   

    
    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, char direction, float angle, int x, int y, int centerX, int centerY) {   	
    	if(frameNumber <= mFrameCount)
    		mFrames[frameNumber].draw(g, direction, angle, x, y, centerX, centerY);
    }  
    
    
    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, char direction, AffineTransform at) {   	
    	//((Graphics2D)g).drawImage(getFrame(frameNumber, direction), at, null);
    }     
        
    
    /**
     * Returns a buffered frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The buffered frame of animation
     */   
    public void drawFrame(Object g, int frameNumber, char direction, int x, int y, int w, int h) {   	
    	//((Graphics)g).drawImage(getFrame(frameNumber, direction), (int)x, (int)y, (int)w, (int)h, null);
    }    
       
    
    /**
     * Returns a buffered frame of animation from the sprite sheet which can be flipped horizontally
     * @param frameNumber The frame number
     * @param direction The horizontal direction of the frame
     * @return The buffered frame of animation
     */   
    public IBufferedImage getFrame(int frameNumber, char direction) {
        try {
            if (direction == 'r') //flip image horizontal
            {
            	//AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                //tx.translate(-mFrames[frameNumber].getWidth(null), 0);
                //AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                //return op.filter(mFrames[frameNumber], null);
            	return mFrames[frameNumber];
            } else {
                return mFrames[frameNumber];
            }
        } catch (Exception e) //frame out of range
        {
            return null;
        }
    }
    
    /**
     * Destroys the textures and releases hardware buffers
     * @param gl The GL context
     */
    public void destroy(GL10 gl) {
      }
    
    public GLSprite getFrame(GL10 gl, int frameNumber, char direction) {
    	return null;
    }
    
    /**
     * Gets the frame width.
     * @return Frame width.
     */
    public int getFrameWidth() {
    	return mFrameWidth;
    
    }
    
    /**
     * Gets the frame height.
     * @return Frame height.
     */
    public int getFrameHeight() {
    	return mFrameHeight;
   
    }



    
}
