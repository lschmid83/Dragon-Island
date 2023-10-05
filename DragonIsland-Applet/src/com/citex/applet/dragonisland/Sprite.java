package com.citex.applet.dragonisland;

import java.io.*;
import java.awt.Point;
import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

/**
	This class loads and draws a sprite sheet containing frames of animation 
	specified by a .ini file.
	
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

public class Sprite {

	/** The sprite sheet containing the animation */
    private SpriteSheet mSpriteSheet;
    /** The x coordinate */
    private int mX;
    /** The y coordinate */
    public int mY;
    /** The direction 'l'-left 'r'-right */
    private char mDirection;
    /** The movement speed */
    private int mSpeed;
    /** The minimum x coordinate */
    private int mMinX;
    /** The maximum x coordinate */ 
    private int mMaxX;
    /** The minimum y coordinate */
    private int mMinY;
    /** The maximum y coordinate */
    private int mMaxY;
    /** The index of the sprite */
    private int mIndex;
    /** The type of entity 0-player, 1-collectable, 2-1 hit kill enemy, 3-walking enemy, 3-shell enemy, 4-jumping enemy, 5-falling enemy */
    private int mType; 
    /** Is the sprite jumping */
    private boolean mJumping;
    /** Is the sprite climbing */
    private boolean mClimbing;
    /** The animation frame information */
    private ArrayList<Frame> mFrameInfo;
    /** The width of a frame of animation */
    private int mFrameWidth;
    /** The height of a frame of animation */
    private int mFrameHeight;
    /** The number of frame in the sprite sheet */
    private int mFrameCount;
    /** The current frame information */
    private Frame mFrame;
    /** The time elapsed between advancing frame */
    private int mFrameTimer;
    /** The animation speed of the sprite */      
    private int mAnimationSpeed;
    /** The index of the current frame of animation */
    private int mFrameIndex;
    /** The time elapsed before the enemy 'wakes' from being hit */
    private int mHitTimelimit;
    /** Is the sprite invulnerable to fire */
    private int mFireInvunerable;
    /** The amount of change to y coordinate when jumping */
    private int mJumpSpeed;
    /** The amount of change to y coordinate when falling */
    private int mFallSpeed;
    /** Is the sprite flickering when invincible after being hit */   
    private boolean mFlicker;
    /** Is the sprite rotating */
    public boolean mRotate;
    /** The angle of rotation */
    private int mAngle;

    /**
     * Constructs the Sprite
     * @param index The index of the sprite
     * @param direction The direction 'l'-left, 'r'-right
     * @param x The start x coordinate
     * @param y The start y coordinate
     */   
    public Sprite(int index, char direction, int x, int y) {
        mIndex = index;
        mDirection = direction;
        mX = x;
    }

    /**
     * Loads a sprite sheet and frame information
     * @param path The path of the sprite sheet and .ini file
     */  
    public void loadSpriteSheet(String path) {
        try {
            String line;
        	InputStream is = Main.class.getResourceAsStream(path + ".ini"); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));            
            mFrameInfo = new ArrayList<Frame>();
            mFrame = new Frame();
            try {
                mType = Integer.parseInt(input.readLine());
                Integer.parseInt(input.readLine()); //not used
                mHitTimelimit = Integer.parseInt(input.readLine());
                mFireInvunerable = Integer.parseInt(input.readLine());
                mJumpSpeed = Integer.parseInt(input.readLine());
                mFallSpeed = Integer.parseInt(input.readLine());
                mFrameWidth = Integer.parseInt(input.readLine());
                mFrameHeight = Integer.parseInt(input.readLine());
                mFrameCount = Integer.parseInt(input.readLine());
                mAnimationSpeed = Integer.parseInt(input.readLine());
                while ((line = input.readLine()) != null) {
                    Frame f = new Frame();
                    f.state = line.split(",")[0];
                    f.start = Integer.parseInt(line.split(",")[1]);
                    f.end = Integer.parseInt(line.split(",")[2]);
                    f.bX = Integer.parseInt(line.split(",")[3]);
                    f.bY = Integer.parseInt(line.split(",")[4]);
                    f.bW = Integer.parseInt(line.split(",")[5]);
                    f.bH = Integer.parseInt(line.split(",")[6]);
                    mFrameInfo.add(f);
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
        }
        mSpriteSheet = new SpriteSheet(path + ".bmp", mFrameWidth, mFrameHeight, mFrameCount);
    }

    /** 
     * Returns the file number of the sprite    
     * @return The index of the sprite
     */   
    public int getIndex() {
        return mIndex;
    }

    /**
     * Sets the file number of the sprite
     * @param index The index of the sprite
     */    
    public void setIndex(int index) {
        mIndex = index;
    }

    /**
     * Returns the type of sprite
     * @return The type of sprite
     */   
    public int getType() {
        return mType;
    }

    /**
     * Sets the amount of time the sprite pauses for after being hit
     * @param hitTimelimit The amount of time
     */   
    public void setHitTimelimit(int hitTimelimit) {
        mHitTimelimit = hitTimelimit;
    }

    /**
     * Returns the amount of time the sprite pauses for after being hit
     * @return The amount of time
     */
    public int getHitTimelimit() {
        return mHitTimelimit * 50;
    }

    /**
     * Sets the animation state of the player (stand, walk, run, jump, fall, crouch)
     * @param state The animation state
     */  
    public boolean setAnimationState(String state) {


        for (int i = 0; i < mFrameInfo.size(); i++) {
            if (mFrameInfo.get(i).state.equals(state)) {
                mFrame = mFrameInfo.get(i);
                mFrame.index = i;
                return true;
            }
        }

        mFrame.state = state;
        return false;
    }

    /**
     * Sets the animation index state of the player 
     * 0 = stand, 1 = walk, 2 = run, 3 = jump, 4 = fall 5 = crouch
     * @param index The animation index
     */
    public boolean setAnimationStateIndex(int index) {
        mFrame = mFrameInfo.get(index);
        return true;
    }

    /**
     * Returns the animation state of the sprite 
     * @return The animation state of the sprite
     */
    public String getAnimationState() {
        return mFrame.state;
    }
    
    /**
     * Returns the index of the animation frame   
     * @return The index of the animation frame
     */
    public int getAnimationStateIndex() {
        return mFrame.index;
    }

    /**
     * Draws the sprite at the x,y coordinates with the current animation and rotation
     * @param gl The graphics context
     * @param cam The camera position
     */   
    public void draw(Graphics g, Point cam) {

        if (!getClimb()) {
            advanceFrame();
        }

        if (!mRotate) {
            if (!mFlicker) {
            	if(getType() >= 10 && getType() < 14)
            		g.drawImage(mSpriteSheet.getFrame(mFrameIndex, 'l'), mX - cam.x, mY + 1 - cam.y, null);
            	else
            		g.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), mX - cam.x, mY + 1 - cam.y, null);
            } else {
                if (mFrameTimer % 5 == 0) {
                    g.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), mX - cam.x, mY + 1 - cam.y, null);
                }
            }
        } else //rotate sprite
        {
            AffineTransform at = new AffineTransform();
            at.translate(mX - cam.x, mY - cam.y);
            at.rotate(Math.toRadians(mAngle), mFrameWidth / 2, mFrameHeight / 2);

            if (mAngle < 360) {
                mAngle += mAnimationSpeed;
            } else {
                mAngle = 0;
            }

            Graphics2D g2d = (Graphics2D) g;
            if (getType() >= 20) //flip sprite
            {
                g2d.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), at, null);
            } else //draw power-up
            {
                g2d.drawImage(mSpriteSheet.getFrame(mFrameIndex, 'l'), at, null);
            }
        }

        if (Settings.DebugMode) //draw bounding rectangle in debug mode
        {
            g.setColor(Color.RED);
            if (getType() != 14) 
            {
                g.drawRect(getLeft() - cam.x, getUp() - cam.y, getWidth(), getHeight() - 1);
            }
        }
    }

    /**
     * Advances the frame of animation when the frame timer reaches the animation speed
     */
    public void advanceFrame() {
        if (Settings.Animation) {
            if (mFrameTimer < mAnimationSpeed) {
                mFrameTimer++;
            } else {
                if (mFrameIndex < mFrame.start) {
                    mFrameIndex = mFrame.start;
                } else if (mFrameIndex < mFrame.end) {
                    mFrameIndex++;
                } else if (mFrameIndex >= mFrame.end) {
                    mFrameIndex = mFrame.start;
                }
                mFrameTimer = 0;
            }
        }
    }

    /**
     * Move the sprite in the set direction
     */
    public void move() {

        if (getDirection() == 'r') {
            if (getRight() < mMaxX) {
                mX += mSpeed;
            }
        } else {
            if (getLeft() > mMinX) {
                mX -= mSpeed;
            }
        }
        if (mJumping) {
            if (getDown() > mMinY) {
                mY -= mJumpSpeed;
            } else {
                mJumping = false;
            }
        } else {
            if (!mClimbing) {
                if (getDown() + mFallSpeed < mMaxY) {
                    mY += mFallSpeed;
                } else {
                    mY = mMaxY - (mFrame.bY + mFrame.bH);
                }
            }
        }
    }

    /**
     * Decreases the y coordinate by the speed
     * @param speed The movement speed
     */
    public void moveUp(int speed) {
        mY -= speed;
    }

    /**
     * Increases the y coordinate by the speed
     * @param speed The movement speed
     */   
    public void moveDown(int speed) {
        mY += speed;
    }

    /**
     * Decreases the x coordinate by the speed
     * @param speed The movement speed
     */
    public void moveLeft(int speed) {
        mX -= speed;
    }

    /** 
     * Increases the x coordinate by the speed
     * @param speed The movement speed
     */
    public void moveRight(int speed) {
        mX += speed;
    }

    /**
     * Draws the sprite at the x,y coordinate with the current frame of animation
     * @param gl The graphics context
     * @param cam The camera coordinate
     * @param direction The direction
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    public void drawXY(Graphics g, Point cam, char dir, int x, int y) {
        g.drawImage(mSpriteSheet.getFrame(mFrameIndex, dir), x - cam.x, y - cam.y, null);
    }

    /**
     * Sets the flickering animation state
     * @param flicker Is the sprite flickering
     */
    public void setFlicker(boolean flicker) {
        mFlicker = flicker;
    }

    /**
     * Sets the rotating state
     * @param rotate Is the sprite rotating
     */
    public void setRotate(boolean rotate) {
        mRotate = rotate;
    }

    /**
     * Returns the rotating state of the sprite
     * @return Is the sprite rotating
     */
    public boolean getRotate() {
        return mRotate;
    }

    /**
     * Returns the current frame of animation from the sprite sheet
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public BufferedImage getFrame(char direction) {
        return mSpriteSheet.getFrame(mFrameIndex, direction);
    }

    /**
     * Returns a frame of animation from the sprite sheet
     * @param frameIndex The frame index
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public BufferedImage getFrame(int frameIndex, char direction) {
        return mSpriteSheet.getFrame(frameIndex, direction);
    }

    /**
     * Returns a frame of animation from the sprite sheet repeated to the height
     * @param frameIndex The frame index
     * @param direction The direction of the frame
     * @param height The height of the repeating sprite
     * @return The image data for the frame of animation
     */
    public BufferedImage getFrame(int frameIndex, char direction, int height) {
        try {
            BufferedImage img = mSpriteSheet.getFrame(frameIndex, direction);
            BufferedImage retImg = new BufferedImage(16, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = retImg.getGraphics();
            int h = height / 16;
            for (int i = 0; i < h + 1; i++) {
                g.drawImage(img, 0, i * 16, null);
            }
            return retImg.getSubimage(0, 0, img.getWidth(), height);
        } catch (Exception e) {
            return mSpriteSheet.getFrame(frameIndex, direction);
        }

    }

    /**
     * Sets the minimum y coordinate for the sprite movement
     * @param minY The minimum y coordinate
     */
    public void setMinY(int minY) {
        mMinY = minY;
    }

    /**
     * Returns the minimum y coordinate for the sprite movement
     * @return The y minimum coordinate
     */
    public int getMinY() {
        return mMinY;
    }

    /**
     * Sets the maximum y coordinate for the sprite movement
     * @param maxY The minimum y coordinate
     */
    public void setMaxY(int maxY) {
        mMaxY = maxY;
    }

    /**
     * Returns the maximum y coordinate for the sprite movement
     * @return The x maximum coordinate
     */
    public int getMaxY() {
        return mMaxY;
    }

    /**
     * Sets the minimum x coordinate for the sprite movement
     * @param minX The minimum x coordinate
     */
    public void setMinX(int minX) {
        mMinX = minX;
    }

    /**
     * Returns the minimum x coordinate for the sprite movement
     * @return The minimum x coordinate
     */   
    public int getMinX() {
        return mMinX;
    }

    /** 
     * Sets the maximum x coordinate for the sprite movement
     * @param maxX The maximum x coordinate
     */   
    public void setMaxX(int maxX) {
        mMaxX = maxX;
    }

    /**
     * Returns the maximum x coordinate for the sprite movement
     * @return The maximum x coordinate
     */
    public int getMaxX() {
        return mMaxX;
    }

    /**
     * Returns the centre point of the sprite 
     * @return The centre point of the sprite
     */
    public Point getCenter() {
        return new Point(getLeft() + getWidth() / 2, getUp() + getHeight() / 2);
    }

    /**
     * Sets the x coordinate of the sprite
     * @param x The x coordinate
     */
    public void setX(int x) {
        mX = x;
    }

    /** 
     * Returns the x coordinate of the sprite 
     * @return The x coordinate of the sprite
     */
    public int getX() {
        return mX;
    }

    /**
     * Sets the y coordinate of the sprite
     * @param y The y coordinate of the sprite
     */
    public void setY(int y) {
        mY = y;
    }

    /**
     * Returns the y coordinate of the sprite
     * @return The y coordinate of the sprite
     */
    public int getY() {
        return mY;
    }

    /**
     * Returns the width of the sprite 
     * @return The width of the sprite
     */
    public int getWidth() {
        return mFrame.bW;
    }

    /**
     * Returns the height of the sprite
     * @return The height of the sprite
     */
    public int getHeight() {
        return mFrame.bH;
    }

    /**
     * Returns the sprite collision bounding box
     * @return The boundaries of the sprite
     */ 
    public Rectangle getBounds() {
        return new Rectangle(getLeft(), getUp(), getWidth(), getHeight());
    }

    /**
     * Returns the left coordinate of the sprite
     * @return The left coordinate of the sprite
     */ 
    public int getLeft() {
        return mX + mFrame.bX;
    }

    /**
     * Returns the right coordinate of the sprite
     * @return The right coordinate of the sprite
     */   
    public int getRight() {
        return mX + mFrame.bX + mFrame.bW;
    }
    
    /**
     * Returns the up coordinate of the sprite
     * @return The up coordinate of the sprite
     */  
    public int getUp() {
        return mY + mFrame.bY;
    }

    /**
     * Returns the down coordinate of the sprite
     * @return The down coordinate of the sprite
     */  
    public int getDown() {
        return mY + mFrame.bY + mFrame.bH;
    }

    /**
     * Sets the direction of the sprite 
     * @param direction The direction of the sprite 'l'-left, 'r'-right
     */
    public void setDirection(char direction) {
        mDirection = direction;
    }

    /**
     * Returns the direction of the sprite 
     * @return The direction of the sprite 'l'-left, 'r'-right
     */
    public char getDirection() {
        return mDirection;
    }

    /**
     * Returns the movement speed of the sprite 
     * @return The movement speed
     */
    public int getSpeed() {
        return mSpeed;
    }

    /** 
     * Sets the movement speed of the sprite
     * @param speed The movement speed
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    /**
     * Sets the climbing state of the sprite
     * @param climbing Is the sprite climbing
     */
    public void setClimb(boolean climb) {
        mClimbing = climb;
    }

    /**
     * Returns the climbing state of the sprite
     * @return True if the sprite is climbing
     */
    public boolean getClimb() {
        return mClimbing;
    }

    /** 
     * Sets the jumping state of the sprite
     * @param jumping Is the sprite jumping
     */
    public void setJump(boolean jump) {
        mJumping = jump;
    }

    /**
     * Gets the jumping sprite of the sprite
     * @return True if the sprite is jumping
     */
    public boolean getJump() {
        return mJumping;
    }

    /**
     * Sets the falling speed of the sprite
     * @param fallSpeed The change in y coordinate when the sprite is falling
     */
    public void setFallSpeed(int fallSpeed) {
        mFallSpeed = fallSpeed;
    }

    /**
     * Returns the number of frames in the current animation
     * @return The number of frames
     */
    public int getFrameCount() {
        return mFrame.end;
    }

    /**
     * Returns the index of the current frame of animation
     * @return The index of the current frame of animation
     */   
    public int getFrameIndex() {
        return mFrameIndex;
    }

    /**
     * Sets the current frame index
     * @param index The frame index
     */
    public void setFrameIndex(int new_index) {
        mFrameIndex = new_index;
    }
    
    /**
     * Returns the frame height 
     * @return The frame height
     */  
    public int getFrameHeight() {
        return mFrameHeight;
    }

    /**
     * Returns the frame width
     * @return The frame width
     */
    public int getFrameWidth() {
        return mFrameWidth;
    }

    /**
     * Returns the fire invulnerability state of the sprite
     * @return The fire invulnerability 
     */
    public int getFireInvunerable() {
        return mFireInvunerable;
    }
    
    /**
     * Sets the fire invulnerability state of the sprite
     * @param fireInvunerable The fire invulnerability 
     */
    public void setFireInvunerbility(int fireInvunerable) {
        mFireInvunerable = fireInvunerable;
    }


    /**
     * Sets the jumping speed
     * @param jumpSpeed The amount to decrease the y coordinate when jumping
     */
    public void setJumpSpeed(int jumpSpeed) {
        mJumpSpeed = jumpSpeed;
    }

    /**
		This class stores frame information for the animation.
		
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
    
    private class Frame {

        int start;  //start frame
        int end;    //end frame
        int bX;     //bounding box coordinates
        int bY;
        int bW;     //bounding box dimensions
        int bH;
        String state;  //animation (walk, run, jump)
        int index;
    }
}
