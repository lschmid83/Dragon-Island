package com.citex.applet.dragonisland;

import java.awt.*;
import java.awt.image.*;

/**
	This class draws a parallax scrolling background.
	
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

public class Background {

	/** The file number of each layer image */
	private int[] mFileNumber;
	/** The image data for each background layer */
	private BufferedImage[] mLayerImage;
	/** The x coordinate of each layer */
	private int mLayerX[];
	/** The y coordinate of each layer */
	private int mLayerY[];
    /** The scrolling speed of each layer */
    private int mLayerSpeed[];
    /** The width of the background */
    private int mWidth;
    /** The height of the background */
    private int mHeight;
    /** The alignment of the background 0 - horizontal 1 - vertical */
    private int mAlign;
    /** The background color displayed where the background image does not cover the screen */
    private Color mColor;
    /** The time elapsed between scrolling the background */
	private int mScrollTimer;
   
    /**
     * Constructs the Background
     * @param fileNumber The number of the image file for the near and far layer images [0]-near, [1]-middle [2]-far
     * @param width The width of the level background
     * @param height The height of the level background
     * @param layerSpeed The scrolling speed of each background layer
     * @param alignement The alignment of the background 0-horizontal 1-vertical
     */
    public Background(int fileNumber[], int width, int height, int layerSpeed[], int alignement) {
    	mFileNumber = new int[3];
    	mFileNumber = fileNumber;
    	mLayerX = new int[3];
        mWidth = width;
        mHeight = height;
        mAlign = alignement;
        mLayerSpeed = new int[3];
        mLayerSpeed = layerSpeed;
        mLayerImage = new BufferedImage[3];
        
        try 
        {
        	//load background layer images
            mLayerImage[0] = new Image("res/bgr/near/" + mFileNumber[0] + ".bmp").getImage(); //near
            mLayerImage[1] = new Image("res/bgr/middle/" + mFileNumber[1] + ".bmp").getImage(); //middle
            mLayerImage[2] = new Image("res/bgr/far/" + mFileNumber[2] + ".bmp").getImage(); //far
            if (mFileNumber[2] != 0) //far layer is set
            {
                mColor = new Color(mLayerImage[2].getRGB(0, 0)); //set color using top left pixel
            }           
            //set the height using the LayerOffsetY values
            setHeight(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the speed of a background layer
     * @param layer The background layer 0-near, 1-middle 2-far
     * @param speed The horizontal scroll speed
     */
    public void setLayerSpeed(int layer, int speed) {
        mLayerSpeed[layer] = speed;
    }
      
    /**
     * Draws the background on the graphics surface at the camera position
     * @param g The graphics context
     * @param cam The camera coordinates
     * @return The camera coordinates within the screen area
     */
    public Point draw(Graphics g, Point cam) {
        if (cam.x >= mWidth - 480) {
            cam.x = mWidth - 480;
        }
        if (cam.x <= 0) {
            cam.x = 0;
        }
        if (cam.y >= mHeight - 272) {
            cam.y = mHeight - 272;
        }
        if (cam.y <= 0) {
            cam.y = 0;
        }

        g.setColor(mColor);
        g.fillRect(0, 0, 480, 272);

        int bgX = cam.x - (cam.x / 512 * 512);
        int bgY = cam.y - (cam.y / 512 * 512);

        if (Settings.Animation) //move background
        {
            if (mScrollTimer < 7) {
                mScrollTimer++;
            } else {
                for (int i = 0; i < 3; i++) {
                    if (mLayerX[i] < 512) {
                        mLayerX[i] += mLayerSpeed[i]; 
                    } else {
                        mLayerX[i] = 0;
                    }
                }
                mScrollTimer = 0;
            }
        } else {
            mLayerX[0] = 0;
            mLayerX[1] = 0;
            mLayerX[2] = 0;
        }

        if (Settings.Background) {
            if (mAlign == 0) //horizontal
            {
                g.drawImage(mLayerImage[2], -bgX - mLayerX[2], mLayerY[2] - cam.y, null);
                g.drawImage(mLayerImage[2], -bgX - mLayerX[2] + 512, mLayerY[2] - cam.y, null);
                g.drawImage(mLayerImage[2], -bgX - mLayerX[2] + 1024, mLayerY[2] - cam.y, null);

                g.drawImage(mLayerImage[1], -bgX - mLayerX[1], mLayerY[1] - cam.y, null);
                g.drawImage(mLayerImage[1], -bgX - mLayerX[1] + 512, mLayerY[1] - cam.y, null);
                g.drawImage(mLayerImage[1], -bgX - mLayerX[1] + 1024, mLayerY[1] - cam.y, null);

                g.drawImage(mLayerImage[0], -bgX - mLayerX[0], mLayerY[0] - cam.y, null);
                g.drawImage(mLayerImage[0], -bgX - mLayerX[0] + 512, mLayerY[0] - cam.y, null);
                g.drawImage(mLayerImage[0], -bgX - mLayerX[0] + 1024, mLayerY[0] - cam.y, null);

            } else //vertical
            {
                g.drawImage(mLayerImage[2], 0, -bgY, null);
                g.drawImage(mLayerImage[2], 0, -bgY + 512, null);
                g.drawImage(mLayerImage[2], 0, -bgY + 1024, null);
            }
        }
        return cam;
    }

    /**
     * Sets the background color
     * @param colour The RGB background color
     */
    public void setColor(Color colour) {
        mColor = colour;
    }

    /**
     * Returns the background color
     * @return The RGB background color
     */
    public Color getColor() {
        return mColor;
    }

    /**
     * Sets the width of the background
     * @param width The width of the background
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * Sets the height of the background
     * @param height The height of the background
     */
    public void setHeight(int height) {
        mHeight = height;
        mLayerY = new int[3];
        mLayerY[0] = height - mLayerImage[0].getHeight();
        mLayerY[1] = height - mLayerImage[1].getHeight();
        mLayerY[2] = height - mLayerImage[2].getHeight();
    }
   
    /**
     * Sets the alignment of the background
     * @param align the alignment of the background 0-repeat x axis / 1=repeat y axis (far layer)
     */
    public void setAlign(int alignment) {
        mAlign = alignment;
    }
}
