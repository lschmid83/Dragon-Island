package com.citex.java.dragonisland;

import java.awt.image.*;
import java.awt.geom.AffineTransform;

/**
    This class loads an image containing a sprite sheet and buffers 
    the frames of animation.
    
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

public class SpriteSheet {

	/** The sprite sheet containing the animation */
    private BufferedImage mSpriteSheet; 
    /** The buffered frames of animation */
    private BufferedImage mFrames[]; 
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
     */   
    public SpriteSheet(String path, int frameWidth, int frameHeight, int frameCount) {
        mFrameWidth = frameWidth;
        mFrameHeight = frameHeight;
        mFrameCount = frameCount;

        mSpriteSheet = new Image(path).getImage();
        if (mSpriteSheet != null) {
            mSheetWidth = mSpriteSheet.getWidth(null);
            mSheetHeight = mSpriteSheet.getHeight(null);
            mRows = mSheetHeight / (mFrameHeight + 1);
            mColumns = mSheetWidth / (mFrameWidth + 1);
        } else {
            mSheetWidth = 0;
            mSheetHeight = 0;
            mRows = 0;
            mColumns = 0;
            mFrameCount = 1;
        }

        //buffer frames from sheet
        mFrames = new BufferedImage[mFrameCount];
        for (int i = 0; i < mFrameCount; i++) {
            mFrames[i] = getFrameFromSheet(i); //store each frame in the array
        }
    }

    /**
     * Returns a frame of animation from the sprite sheet
     * @param frameNumber The frame number
     * @return The frame of animation
     */
    public BufferedImage getFrameFromSheet(int frameNumber) {
        try {
            if (mRows > 0 && mColumns > 0) {
                int col = frameNumber / mRows; //column
                int row = frameNumber - (col * mRows); //row
                return mSpriteSheet.getSubimage(col * mFrameWidth + col + 1,
                        row * mFrameHeight + row + 1, mFrameWidth, mFrameHeight);
            } else //only one column in sheet
            {
                return mSpriteSheet.getSubimage(0, frameNumber * 16, mFrameWidth, mFrameHeight);
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
    public BufferedImage getFrame(int frameNumber) {
        return mFrames[frameNumber];
    }

    /**
     * Returns a buffered frame of animation from the sprite sheet which can be flipped horizontally
     * @param frameNumber The frame number
     * @param direction The horizontal direction of the frame
     * @return The buffered frame of animation
     */   
    public BufferedImage getFrame(int frameNumber, char direction) {
        try {
            if (direction == 'r') //flip image horizontal
            {
            	AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-mFrames[frameNumber].getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                return op.filter(mFrames[frameNumber], null);
            } else {
                return mFrames[frameNumber];
            }
        } catch (Exception e) //frame out of range
        {
            return null;
        }
    }
}
