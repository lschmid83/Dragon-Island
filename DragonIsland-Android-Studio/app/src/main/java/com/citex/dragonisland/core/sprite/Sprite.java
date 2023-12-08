package com.citex.dragonisland.core.sprite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSprite;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.drawing.Rectangle;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.sprite.player.enums.FlagState;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Numbers;

/**
 * Sprite.java
 * This class loads and draws a sprite sheet containing frames of animation.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class Sprite implements Cloneable {

	/** Sprite sheet. */
    private SpriteSheet mSpriteSheet;
    
    /** Position. */
    private Point mPosition;
    
    /** Start position. */
    private Point mStartPosition;
    
    /** Velocity. */
    private Point mVelocity;

    /** Animation information. */
    private ArrayList<Frame> mFrameInfo;

    /** Current animation. */
    private Frame mFrame;

    /** Current frame of animation */
    private float mFrameIndex;
    
    /** Time elapsed before increasing frame. */
    private float mFrameTimer;

    /** Direction. 'l'-left 'r'-right */
    private char mDirection;
    
    /** Minimum movement. */
    private Point mMinMovement;
    
    /** Maximum movement. */
    private Point mMaxMovement;

    /** Jumping. */
    private boolean mJumping;
    
    /** Is the sprite rotating. */
    public boolean mRotate;  
    
    /** Angle of rotation. */
    private float mAngle;
        
    /** Is the sprite dying. */
    private boolean mSpinOffScreen;
    
    /** Is the sprite dead. */
    private boolean mDead;
    
    /** Animation speed. */
    private float mAnimationSpeed;
    
    /** Is the sprite invulnerable to fire. */
    private boolean mFireInvulnerable;
    
    /** Rotation speed of the sprite. */
    private float mRotationSpeed;
    
    /** Time elapsed since turning. */
    private float mTurnTimer;

    /**
     * Default constructor.
     */
    public Sprite() {
    	
    	// Initialise sprite.
    	initSprite();
    }
        
    /**
     * Initialise a Sprite object.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Sprite(Object g, String path) throws NumberFormatException, IOException {
    	
    	// Initialise sprite.
    	initSprite();
    	
    	// Load the sprite sheet.
    	loadSprite(g, path);
    	
    	// Get the default animation.
    	mFrame = mFrameInfo.get(0);
    	
    	// Set start frame.
        mFrameIndex = mFrame.start;
    	
    	// Set fire invulnerable.
    	mFireInvulnerable = false;
    	
    }
    
    /**
     * Initialise the sprite.
     */
    public void initSprite() {
    	
    	// Initialise the velocity.
    	mVelocity = new Point(0.0001f, 0);
    	
    	// Initialise the start position.
    	mPosition = new Point(0, 0);
    	mStartPosition = null;
    	
    	// Initialise the min / max movement.
    	mMinMovement = new Point(0,0);
    	mMaxMovement = new Point(0,0);    	
    	
    }
    
    /**
     * Loads a sprite sheet and frame information.
     * @param gl Graphics context.
     * @param path Path of the sprite sheet.
     * @throws IOException 
     * @throws NumberFormatException 
     */  
    private void loadSprite(Object gl, String path) throws NumberFormatException, IOException {
        
    	BufferedReader br = null;
		int frameWidth = 0, frameHeight = 0, frameCount = 0;
    	
    	try {
    		
    	  	// Open the .ini file containing the sprite definition.
        	br = FileIO.openBufferedReader(Settings.ResourcePath + path + ".ini");
            
        	// Initialise the frame information.
            mFrameInfo = new ArrayList<Frame>();
            mFrame = new Frame();
        	
            // Initialise the frame size and count.
        	frameWidth = Integer.parseInt(br.readLine());
        	frameHeight = Integer.parseInt(br.readLine());
        	frameCount = Integer.parseInt(br.readLine());    
            
            // Read animation frame information.
        	String line;
            while ((line = br.readLine()) != null) {           	
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
            
        	// Initialise the sprite sheet.
          	mSpriteSheet = new SpriteSheet(gl, path + ".png", frameWidth, frameHeight, frameCount);
          	
    	}
        finally {
        	
        	// Close buffered reader.
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	            }
	        }
	    } 	
    	

      
    }   
    
    
    /**
   	 * Move the player.
   	 * @param cam Camera coordinates.
   	 * @param dt Delta time between frame updates.
   	 */
   	public void moveSpin(float dt, Point cam)
   	{
  		Point velocity = new Point(0.18f, 0.18f);
  		
  		// Move forward.
  		if (getDirection() == 'r') {
            if (getRight() < getMaxMove().x)
                moveRight(dt, velocity.x);
        } else {
            if (getLeft() > getMinMove().x)
                moveLeft(dt, velocity.x);
        }
        
  		// Jump.
  		if (getJump()) {
            if (getUp() > getMinMove().y)
                moveUp(dt, velocity.y);
            else
            	setJump(false);
        } else {
            if (getDown() < getMaxMove().y)
                moveDown(dt, velocity.y);
            else 
            	kill();
        }
   	}
        
    /**
     * Sets the sprite and frame information.
     * @param sprite Sprite object.
     */
    public void setSprite(Sprite sprite) {
    	
    	mSpriteSheet = sprite.getSpriteSheet();
    	mFrameInfo = sprite.getFrameInfo();
    	mFrame = mFrameInfo.get(0);
    	
    }
    
    /** Gets the sprite sheet. */
    public SpriteSheet getSpriteSheet() {
    	return mSpriteSheet;
    }
    
    /**
     * Draws the sprite at the x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera position.
     */   
    public void draw(Object g, float dt, Point cam) {
    	
    	if(mTurnTimer < 300) {
    		mTurnTimer += dt;    		
    	}
    	
    	draw(g, dt, mDirection, cam);
    }
    
    /**
     * Draws the sprite at the x,y coordinates.
     * @param g Graphics context.
     * @param dir Direction.
     * @param dt Delta time between frame updates.
     * @param cam Camera position.
     */   
    public void draw(Object g, char dir, float dt, Point cam) {
    	
    	if(mTurnTimer < 300) {
    		mTurnTimer += dt;    		
    	}
    	
    	draw(g, dt, dir, cam);
    }
    
    /**
     * Draws the sprite at the x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param direction Direction of sprite.
     * @param cam Camera position.
     */   
    public void draw(Object g, float dt, char direction, Point cam) {

        if (!mRotate && mAngle == 0)
        	mSpriteSheet.drawFrame(g, (int)mFrameIndex, direction, 0f, (int)(mPosition.x - cam.x), (int)(mPosition.y - cam.y), 0, 0);
        else {
        	
        	// Calculate angle.
        	if(mRotate)
        		mAngle = calculateAngle(dt, mAngle, mRotationSpeed);
        	
        	// Draw rotated sprite.
        	mSpriteSheet.drawFrame(g, (int)mFrameIndex, direction, mAngle, (int)(mPosition.x - cam.x), (int)(mPosition.y - cam.y), mFrame.bX + mFrame.bW / 2, mFrame.bY + mFrame.bH / 2);

        }
    }
    
    /**
     * Draws the sprite tiled specified to the width and height.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param frame Frame index.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param w Width.
     * @param h Height.
     */
    public void draw(Object g, float dt, int frame, float x, float y, float w, float h) {
    	mSpriteSheet.drawFrame(g, frame, x, y, w, h);
    }
    
    
    /**
     * Draws the sprite at the x,y coordinates with the current frame of animation.
     * @param g The graphics context
     * @param dt Delta time
     * @param x The x coordinate 
     * @param y The y coordinate
     */
    public void draw(Object g, float dt, int x, int y, char dir) {
    	
		// Draw sprite.
    	mSpriteSheet.getFrame((int)mFrameIndex, dir).draw(g, x, y);
    } 
    
    /** 
     * Draws the entity at x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param dir Direction of the entity.
     */
    public void draw(Object g, float dt, int x, int y, char dir, int angle) {
    	
		// Draw sprite.
    	mSpriteSheet.drawFrame(g, (int)mFrameIndex, dir, angle, x, y, mFrame.bX + mFrame.bW / 2, mFrame.bY + mFrame.bH / 2);	
    	
    }
    
    
    /**
     * Calculates angle of rotation based on delta time.
     * @param dt Delta time between frame updates.
     * @param angle Angle of rotation.
     * @return Angle of rotation plus delta time.
     */
    public float calculateAngle(float dt, float angle, float rotationSpeed) {
    	
    	if(Settings.Paused)
    		return angle;
    	
        if (angle < 360) {
            angle += dt * rotationSpeed; 
        } else {
            angle = 0;
        }
    	
        return angle;
        
    }
    
    
    public float getAngle() {
    	return mAngle;
    }
    
    /**
     * Gets the frame height.
     * @return Frame height.
     */  
    public int getFrameHeight() {
        return mSpriteSheet.getFrameHeight();
    }

    /**
     * Gets the frame width.
     * @return Frame width.
     */
    public int getFrameWidth() {
        return mSpriteSheet.getFrameWidth();
    }
    
    /**
     * Sets the direction.
     * @param direction Direction of the sprite.
     */
    public void setDirection(char direction) {
        mDirection = direction;
    }
    
    /**
     * Sets the angle.
     * @param angle Angle of the entity.
     */
    public void setAngle(int angle) {
    	mAngle = angle;
    }

    /**
     * Gets the direction of the sprite 
     * @return Direction of the sprite.
     */
    public char getDirection() {
        return mDirection;
    }

    
    public Frame getFrame() {
    	return mFrame;
    	
    }
    
    
    /** Gets frame information. */
    public ArrayList<Frame>getFrameInfo() {
    	
    	return mFrameInfo;
    }

    /**
     * Sets the animation state of the sprite.
     * @param state Animation state.
     */  
    public boolean setAnimationState(String state) {

    	if(mFrame.state != state) {
    	
	        for (int i = 0; i < mFrameInfo.size(); i++) {
	            if (mFrameInfo.get(i).state.equals(state)) {
	                mFrame = mFrameInfo.get(i);
	                mFrame.index = i;
	                return true;
	            }
	        }
    	}


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
     * Advances the frame of animation when the frame timer reaches the animation speed
     */
    public void animate(float dt) {

    	if(!Settings.Paused && Settings.Animation) {
    	
    		// Limit frame updates to animation speed.
    		if(mFrameTimer + dt < mAnimationSpeed) {
    			mFrameTimer += dt;
    		} else {
    			
    			// Increase frame index.
    	        if (mFrameIndex < mFrame.start)
    	            mFrameIndex = mFrame.start;
	            else if (mFrameIndex < mFrame.end)
		            mFrameIndex++;
		        else if (mFrameIndex >= mFrame.end)
		            mFrameIndex = mFrame.start; 
    			    			
    			mFrameTimer = 0;
    		}
    	}

    }

    /**
     * Decreases the y coordinate by the speed
     */
    public void moveUp(float dt, float velocity) {
        mPosition.y -= velocity * dt;
    }

    /**
     * Increases the y coordinate by the speed
     */
    public void moveDown(float dt, float velocity) {
        mPosition.y += velocity * dt;
    }

    /**
     * Decreases the x coordinate by the speed
     */
    public void moveLeft(float dt, float velocity) {
        mPosition.x -= velocity * dt;
    }

    /** 
     * Increases the x coordinate by the speed
     */
    public void moveRight(float dt, float velocity) {
        mPosition.x += velocity * dt;
    }

    /**
     * Changes the direction.
     */
    public void turn() {

    	if(mTurnTimer >= 300 && !getAnimationState().equals("fly")) {
	    	if(mDirection == 'l')
	    		mDirection = 'r';
	    	else
	    		mDirection = 'l';
	    	
	    	mTurnTimer = 0;
    	}
    }
    
    /**
     * Returns the current frame of animation from the sprite sheet
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public IBufferedImage getFrame(char direction) {
        return mSpriteSheet.getFrame((int)mFrameIndex, direction);
    }

    /**
     * Returns a frame of animation from the sprite sheet
     * @param frameIndex The frame index
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public IBufferedImage getFrame(int frameIndex, char direction) {
        return mSpriteSheet.getFrame(frameIndex, direction);
    }

    /**
     * Returns a frame of animation from the sprite sheet repeated to the height
     * @param frameIndex The frame index
     * @param direction The direction of the frame
     * @param height The height of the repeating sprite
     * @return The image data for the frame of animation
     */
    public IBufferedImage getFrame(int frameIndex, char direction, int height) {
        try {
            IBufferedImage img = mSpriteSheet.getFrame(frameIndex, direction);
            /*
            IBufferedImage retImg = new BufferedImage(16, height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = retImg.getGraphics();
            int h = height / 16;
            for (int i = 0; i < h + 1; i++) {
                g.drawImage(img, 0, i * 16, null);
            }
            return retImg.getSubimage(0, 0, img.getWidth(), height);*/
            return img;
        } catch (Exception e) {
            return mSpriteSheet.getFrame(frameIndex, direction);
        }

    }
    
    /**
     * Returns the current frame of animation from the sprite sheet
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public GLSprite getFrame(GL10 gl, char direction) {
        return mSpriteSheet.getFrame(gl, (int)mFrameIndex, direction);
    }

    /**
     * Returns a frame of animation from the sprite sheet
     * @param frameIndex The frame index
     * @param direction The direction of the frame
     * @return The image data for the frame of animation
     */
    public GLSprite getFrame(GL10 gl, int frameIndex, char direction) {
        return mSpriteSheet.getFrame(gl, frameIndex, direction);
    }
  


    /**
     * Returns the centre point of the sprite 
     * @return The centre point of the sprite
     */
    public Point getCenter() {
        return new Point(getLeft() + getWidth() / 2, getUp() + getHeight() / 2);
    }
    
    
    public int getCenterX() {
    	
    	return (int)getLeft() + getWidth() / 2;
    }
    
    public int getCenterY() {
    	
    	return (int)getUp() + getHeight() / 2;
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
     * Gets the collision bounding box.
     * @return Rectangle object.
     */ 
    public Rectangle getBounds() {
    	return new Rectangle(getLeft(), getUp(), getWidth(), getHeight());
    }

    /**
     * Returns the left coordinate of the sprite
     * @return The left coordinate of the sprite
     */ 
    public int getLeft() {
        return (int)mPosition.x + mFrame.bX;
    }

    /**
     * Returns the right coordinate of the sprite
     * @return The right coordinate of the sprite
     */   
    public int getRight() {
        return (int)mPosition.x + mFrame.bX + mFrame.bW;
    }
    
    /**
     * Returns the up coordinate of the sprite
     * @return The up coordinate of the sprite
     */  
    public int getUp() {
        return (int)mPosition.y + mFrame.bY;
    }

    /**
     * Returns the down coordinate of the sprite
     * @return The down coordinate of the sprite
     */  
    public int getDown() {
        return (int)mPosition.y + mFrame.bY + mFrame.bH;
    }
    
    /**
     * Returns the animation state of the sprite
     * @return The animation state of the sprite
     */
    public String getState() {
        return mFrame.state;
    }  
  
    /**
     * Sets the animation index state of the player
     * 0 = stand, 1 = walk, 2 = run, 3 = jump, 4 = fall 5 = crouch
     * @param index The animation index
     */
    public boolean setState(int index) {
        mFrame = mFrameInfo.get(index);
        return true;
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
    public float getFrameIndex() {
        return (int)mFrameIndex;
    }

    /**
     * Sets the current frame index
     */
    public void setFrameIndex(int new_index) {
        mFrameIndex = new_index;
    }
    
    /**
     * Destroys the textures and releases hardware buffers
     * @param gl
     */
    public void destroy(GL10 gl)
    {
        if(mSpriteSheet != null)
            mSpriteSheet.destroy(gl);

    }
    
    public Point getVelocity() {
    	return mVelocity;
    	
    }
 
    
    public void setVelocity(Point velocity) {
    	mVelocity = velocity;
   
    }

    public Point getStartPosition() {
    	
    	if(mStartPosition != null)
    		return mStartPosition;
    	else
    		return mPosition;
    }
        
    public void setPosition(Point position) {
    	mPosition = position;
    	
    	if(mStartPosition == null)
    		mStartPosition = mPosition;
    }
    
    
    public Point getPosition() {
    	return mPosition;
   
    }

    /**
     * Determines whether the sprite is within the screen area.
     * @param cam Camera coordinates.
     * @param padding Amount of padding to apply to camera coordinates.
     * @return True if the sprite is within the screen area; otherwise false.
     */
    public boolean isInScreenArea(Point cam, int padding) {
    	
    	if(getPosition().x > cam.x - padding &&
    	   getPosition().x < cam.x + Settings.ScreenWidth + padding &&
    	   getPosition().y > cam.y - padding &&
    	   getPosition().y < cam.y + Settings.ScreenHeight + padding) {
    		return true;
    	}
    	else
    		return false;
    	
    }
    
    
    /**
     * Determines whether the sprite is invulnerable to fire.
     * @return True if the sprite is invulnerable to fire; otherwise false.
     */
    public boolean getFireInvulnerable() {
    	
    	return mFireInvulnerable;
    }
    
    public void setFireInvulnerable(boolean invulnerable) {
    	mFireInvulnerable = invulnerable; 	
    }
    
    
    public Point setMaxMove() {
    	return mMaxMovement;    	
    }
    
    public Point getMaxMove() {
    	return mMaxMovement;    	
    }
    
    
    public Point setMinMove() {
    	return mMinMovement;
    }
    
    public Point getMinMove() {
    	return mMinMovement;
    }
    
    
    
    public void setJump(boolean jumping) {
    	mJumping = jumping;
    }
    
    
    public boolean getJump() {
    	return mJumping;
    }

    /**
     * Sets the rotating state
     * @param rotate Is the sprite rotating
     */
    public void setRotate(boolean rotate, float speed) {
        mRotate = rotate;
        mRotationSpeed = speed;
    }

    /**
     * Returns the rotating state of the sprite
     * @return Is the sprite rotating
     */
    public boolean getRotate() {
        return mRotate;
    }   
    
    
    /**
     * Sets the x coordinate of the sprite
     * @param x The x coordinate
     */
    public void setX(float x) {
        mPosition.x = x;
    }  
    
    public void setY(float y) {
        mPosition.y = y;
    }  
    
    
    /** 
     * Returns the x coordinate of the sprite 
     * @return The x coordinate of the sprite
     */
    public float getX() {
        return mPosition.x;
    }  
    
    public float getY() {
        return mPosition.y;
    }   

    /**
     * Make a field-for-field copy of instances of that class.
     * @return Cloned object.
     */
    public Object clone(int x, int y, int angle) throws CloneNotSupportedException {
    	
    	Sprite clone = (Sprite)super.clone();
    	
    	// If the class contains members of any class type then 
    	// only the object references to those members are copied
    	// so create new objects.
    	clone.setPosition(new Point(x, y));
    	
    	mAngle = angle;
    	
    	clone.mMinMovement = new Point(0, 0);
    	clone.mMaxMovement = new Point(0, 0);
    	clone.mFrame = new Frame();
    	clone.setAnimationState(getAnimationState());
    	
    	return clone;	
    }
    
    
    public void alive() {
    	mDead = false;
    }
    
    public void kill() {
    	mDead = true;
    }
    

    /**
     * Makes the sprite spin off the screen.
     * @param direction Spin direction.
     */
    public void spinOffScreen(char direction) {
    	
    	if(!mSpinOffScreen) {
	    	
    		setJump(true);
	    	setRotate(true, 1);
	    	setMaxMove().x = 200000;
	        setMaxMove().y = 200000;
	        setMinMove().y = getUp() - 20;
	    	
	        setDirection(direction);
	        
	        mSpinOffScreen = true;
    	}
    }
    
    /**
     * Is the sprite spinning off the screen.
     * @return True if the sprite is spinning off screen; otherwise false.
     */
    public boolean getSpinOffScreen() {
    	return mSpinOffScreen;
   
    }
    
    public boolean isDead() {
    	return mDead;
   
    }
    
    public void setAnimationSpeed(float speed) {
    	mAnimationSpeed = speed;
    }
    
}
