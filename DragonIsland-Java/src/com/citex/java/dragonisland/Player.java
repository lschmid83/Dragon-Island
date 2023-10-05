package com.citex.java.dragonisland;

import java.awt.Point;
import java.awt.Graphics;
import java.util.ArrayList;

/**
	This class draws a user controllable sprite and detects collisions 
    between level map tiles and entities.

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

public class Player extends Sprite {

	/** The number of lives remaining */
    private int mLives;
    /** The total score count */
    private int mScore;
    /** The total coin count */
    private int mCoins;
    /** The remaining time */
    private int mTime;
    /** The invincible status 0-not invincible 1-invincible */
    private int mInvincible;
    /** The time elapsed since the player became invincible */
    private int mIvincibleTimer;
    /** The drawing frame for the invincible sprite sheet */
    private int mInvincibleFrame;
    /** The elapsed time before releasing a fire ball */
    private int mFireTimer;
    /** The size of the player 0-small 1-large 2-fire power-up */
    private int mSize;
    /** The controller state of the player */
    private Control mControls;
    /** The time elapsed between clock ticks */
    private int mTimer;
    /** The end of level flag state */
    private int mFlagState;
    /** The warp pipe state 2-up 3-down 4-left 5-right */
    private int mPipeState;
    /** The end state of the player 7-exit level 8-exit bonus */
    private int mEndState;
    /** Player has exited level through a pipe */
    private boolean mExitBonus;
    /** The exit level state */
    private boolean mExitLevel;
    /** The player is crouching trapped under block */
    private boolean mCrouchingUnderBlock;
    /** Draw points for killing enemy or collecting power-up */
    private ArrayList<Score> mDrawPoints;
    /** The image displayed over player when invincible */
    private SpriteSheet mStars;
    /** The font used to display points */
    private GameFont mPointFont;

    /**
     * Constructs the player
     * @param index The folder number of the players sprite sheet
     * @param size The size of the player 0-small, 1-large, 2-fire power-up
     * @param invincible The invincible state 0-not invincible, 1-invincible
     * @param lives The amount of lives
     * @param coins The amount of coins
     * @param score The total score
     * @param mTime The time limit
     * @param startState The start state 0-stand left, 1-stand right, 2-up pipe 3-down pipe 4-left pipe 5-right pipe
     * @param x The x coordinate
     * @param y The y coordinate
     */    
    public Player(int index, int size, int invincible,
            int lives, int coins, int score, int time, int startState,
            int x, int y) {

        super(index, 'r', x, y); //construct sprite
        loadSpriteSheet("res/chr/" + getIndex() + "/" + size);
        setAnimationState("stand");
        setX(x);
        setY(y);
        setMinY(getDown());

        mSize = size;
        mInvincible = invincible;
        mLives = lives;
        mScore = score;
        mCoins = coins;
        mTime = time;
        mDrawPoints = new ArrayList<Score>();
        mPointFont = new GameFont("score");

        if (startState == 0) //left
        {
            setDirection('l');
        } else if (startState == 1) //right
        {
            setDirection('r');
        } else if (startState == 2) //move up
        {
            setMinY(getUp());
            setY(getY() + getFrameHeight());
            setDirection('r');
            mPipeState = 2;
        } else if (startState == 3) //move down
        {
            setMaxY(getDown());
            setY(getY() - getFrameHeight());
            setDirection('r');
            mPipeState = 3;
        } else if (startState == 4) //walk left
        {
            setMinX(getLeft());
            setX(getX() + getFrameWidth());
            setY(getY() - 2);
            setDirection('l');
            mPipeState = 4;
        } else if (startState == 5) //walk right
        {
            setMaxX(getRight());
            setX(getX() - getFrameWidth());
            setY(getY() - 2);
            setDirection('r');
            mPipeState = 5;
        }
        mControls = new Control();
        mStars = new SpriteSheet("res/chr/" + getIndex() + "/3.bmp", 48, 48, 20);
    }

    /**
     * Sets the size of the player
     * @param size The new size 0-small, 1-large, 2-fire power-up
     */  
    public void setSize(int size) {
        String str = getAnimationState();
        mSize = size;
        super.loadSpriteSheet("res/chr/" + getIndex() + "/" + mSize);
        setAnimationState(str);
    }

    /**
     * Gets the size of the player
     * @return The size of the player
     */    
    public int getSize() {
        return mSize;
    }

    /**
     * Sets the invincible status of the player
     * @param The invincible status of the player 0-normal 1-invincible
     */    
    public void setInvincible(int invincible) {
        mInvincible = invincible;
    }
    
    /**
     * Gets the invincible status of the player
     * @return The invincible status of the player
     */ 
    public int getInvincible() {
        return mInvincible;
    }

    /** Sets the sprite sheet
     * @param character The folder number of the sprite sheet
     */
    public void setIndex(int character) {
        super.setIndex(character);
        setSize(mSize);
    }
    
    /**
     * Draws the player on the graphics surface
     * @param g The graphics context
     * @param cam The camera coordinates
     */    
    public void draw(Graphics g, Point cam) {

        super.draw(g, cam); //draw the sprite

        //draw collected points
        for (int i = 0; i < mDrawPoints.size(); i++) {
            mDrawPoints.get(i).timer++;
            mPointFont.drawString(g, 0, String.valueOf(mDrawPoints.get(i).value), mDrawPoints.get(i).x - cam.x, mDrawPoints.get(i).y - cam.y);
            if (mDrawPoints.get(i).timer % 2 == 0) {
                if (mDrawPoints.get(i).timer < 30) {
                    mDrawPoints.get(i).y--; //scroll up
                } else {
                    mDrawPoints.remove(i);
                }
            }
        }

        //set the amount of time left
        if (mFlagState == 0 && !Settings.FreezeTime) {
            mTimer++;
            if (mTimer % 70 == 0) {
                if (mTime > 0) {
                    mTime--;
                } else //time up
                {
                    if (Settings.State.equals("game")) {
                        enemyCollision(); //kill player
                    }
                }
            }
        }

        //set the exit flag if the player has reached the bonus/end of level
        boolean exit = false;
        if (mPipeState < 2) //normal direction
        {
            move(); //move the player
        } else if (mPipeState == 2) {
            if (getUp() > getMinY()) {
                moveUp(1); //up pipe
            } else {
                exit = true;
            }
        } else if (mPipeState == 3) {
            if (getDown() < getMaxY()) {
                moveDown(1); //down pipe
            } else {
                exit = true;
            }
        } else if (mPipeState == 4) {
            if (getLeft() > getMinX()) {
                moveLeft(1); //left pipe
            } else {
                exit = true;
            }
        } else if (mPipeState == 5) {
            if (getRight() < getMaxX()) {
                moveRight(1); //right pipe
            } else {
                exit = true;
            }
        }
        if (exit) //exit bonus/level position reached
        {
            if (mExitLevel) {
                mEndState = 7; //GamePanel -> AdvanceLevel()
            } else if (mExitBonus) {
                mEndState = 8; //GamePanel -> AdvanceBonus()
            }
            mPipeState = 0;
        }

        //reached end of level flag pole
        if (mFlagState == 1) {
            setAnimationState("flag");
        }
        if (mFlagState == 2) //jump off flag pole
        {
            mTimer++;
            if (mTimer > 100) {
            	//small
            	if(getSize()==0)
            	{
            		setSpeed(2);
            		setFallSpeed(3);
            	}
            	else //large
            	{
            		setSpeed(3);
	                setFallSpeed(5);
            	}
                setJump(true);
                setMinY(getUp());
                mFlagState = 3;
                mTimer = 0;
            }
        } else if (mFlagState == 3) //stand while remaining time is counted
        {
            if (getDown() == getMaxY()) {
                setSpeed(0);
                setDirection('r');
                setAnimationState("victory");
                mTimer++;
                setJump(false);
                if (mTimer > 150) //walk to castle
                {
                    setSpeed(1);
                    mFlagState = 4;
                    setAnimationState("walk");
                    setMaxX(getX() + 158);
                    mTimer = 0;
                }
            }
        } else if (mFlagState > 3) //count remaining time
        {
            if (getRight() >= getMaxX() && mFlagState < 5) 
            {
                mFlagState = 5;
            }
            if (mTime > 0) {
                mTime--;
                addScore(10);
            } else //finished counting time
            {
                mTimer++;
                if (mTimer > 100) {
                    mEndState = 7; //end of level
                    mFlagState = 0;
                }
            }
        }

        //set player movement based on controls if not dead, on flag or in pipe
        if (!getAnimationState().equals("die") && mFlagState == 0 && mPipeState < 2) {
            setMovement();
            if (mInvincible == 1) //invincibility power-up
            {
                mIvincibleTimer++;
                if (mIvincibleTimer % 5 == 0) {
                    if (mInvincibleFrame < 10) {
                        mInvincibleFrame++;
                    } else {
                        mInvincibleFrame = 0;
                    }
                }
                //align animation over player
                if (getDirection() == 'r') {
                    g.drawImage(mStars.getFrame(mInvincibleFrame), getCenter().x - 31 - cam.x, getUp() - 10 - cam.y, null);
                } else {
                    g.drawImage(mStars.getFrame(mInvincibleFrame), getCenter().x - 19 - cam.x, getUp() - 10 - cam.y, null);
                }
                if (mIvincibleTimer > 1000) {
                    mIvincibleTimer = 0;
                    setInvincible(0);
                }
            } else if (mInvincible == 2) //temporary invincibility after enemy collision
            {
                mIvincibleTimer++;
                setFlicker(true);
                if (mIvincibleTimer > 125) {
                    mIvincibleTimer = 0;
                    setInvincible(0);
                    setFlicker(false);
                }
            }
        }
    }

    /**
     * Makes the player bounce off an enemy when hit
     */    
    public void bounce() {
        setJump(true);
        setMinY(getDown() - 25);
        mControls.mJumpPressed = true;
    }

    /**
     * Move the player and set animation state based on the control buttons which are pressed
     */
    public void setMovement() {

        if (getClimb() && mControls.mUpPressed) {
            setAnimationState("climb");
            advanceFrame();
            setMinY(0);
            moveUp(1);
        }

        if (getClimb() && mControls.mDownPressed) {
            setAnimationState("climb");
            advanceFrame();
            moveDown(1);
        }


        //set amount of time the direction button has been pressed
        if ((mControls.mLeftPressed || mControls.mRightPressed) && (!getAnimationState().equals("crouch") || getJump())) {
            if (getClimb()) {
                advanceFrame();
            }

            if (mControls.mRunPressed) //run
            {
                if (mControls.mDirectionHeld < 80) {
                    mControls.mDirectionHeld++;
                }
            } else //walk
            {
                if (mControls.mDirectionHeld < 20) {
                    mControls.mDirectionHeld++;
                } else {
                    mControls.mDirectionHeld -= 4;
                }
            }
        } else //direction released
        {
            if (mControls.mDirectionHeld > 0) {
                mControls.mDirectionHeld -= 4;
            } else {
                mControls.mDirectionHeld = 0;
            }
        }

        //set speed based on how long the direction button has been held
        if (mControls.mDirectionHeld == 0) //stand
        {
            setSpeed(0);
        } else if (mControls.mDirectionHeld > 0 && mControls.mDirectionHeld < 20) //walk 1
        {
            setSpeed(1);
        } else if (mControls.mDirectionHeld > 20 && mControls.mDirectionHeld < 40) //jog
        {
            setSpeed(2);
        } else if (mControls.mDirectionHeld > 40 && mControls.mDirectionHeld <= 80) //run
        {
            setSpeed(2);
        }

        //set player sprite animation based on speed when on ground level
        if (getDown() == getMaxY() && !getAnimationState().equals("climb")) //on ground level
        {
            if (getSpeed() == 0) {
                setAnimationState("stand");
            } else if (getSpeed() == 1) {
                setAnimationState("walk");
            } else if (getSpeed() == 2) {
                setAnimationState("jog");
            } else if (getSpeed() == 3) {
                setAnimationState("run");
            }
            if (mControls.mDownPressed || mCrouchingUnderBlock) {
                setAnimationState("crouch");
                mCrouchingUnderBlock = true;
            }
            if (!mControls.mJumpPressed) {
                mControls.mJumpReleased = true;
            }
        }

        //fireball
        mFireTimer++;
        if (mControls.mRunPressed && mControls.mRunReleased) {
            if (mFireTimer > 40 && mSize == 2) {
                mControls.mRunReleased = false;
                //add fireball
                if ((!getAnimationState().equals("climb") && !getAnimationState().equals("fall")) && !mCrouchingUnderBlock) {
                    new SoundEffect("res/sfx/fire.wav").start();
                    if (getDirection() == 'r') {
                        Main.mFrame.mGamePanel.addEntity(1, 'r', getRight() - 5, getUp() + getHeight() / 2);
                    } else {
                        Main.mFrame.mGamePanel.addEntity(1, 'l', getLeft() - 5, getUp() + getHeight() / 2);
                    }
                }
                mFireTimer = 0;
            }
        }
        //change direction
        if (mControls.mRightPressed && !mControls.mLeftPressed && getDirection() == 'l') {
            setDirection('r');
        } else if (mControls.mLeftPressed && !mControls.mRightPressed && getDirection() == 'r') {
            setDirection('l');
        }

        //set player state to jump
        if ((mControls.mJumpPressed && mControls.mJumpReleased && getDown() == getMaxY()) || (getClimb() && mControls.mJumpPressed && mControls.mJumpReleased)) {
            mControls.mJumpReleased = false;
            setJump(true);
            setMinY(getDown() - Settings.JumpHeight); //set jump height
            if (!getAnimationState().equals("crouch")) {
                new SoundEffect("res/sfx/jump.wav").start();
                setAnimationState("jump");
            }
        }

        //set player state to fall if reaches jump height
        if (getMinY() - getDown() > 65 || (!mControls.mJumpPressed && getJump())) {

            setJump(false);
            if (!getAnimationState().equals("crouch")) {
                setAnimationState("fall");
            }
        }
    }

    /**
     * Detect collisions between the player and entities
     * @param entities A list of entities
     * @return True if a collision has occurred
     */    
    public boolean detectEntityCollision(ArrayList<Entity> entities) {

        boolean collision = false;

        if (getAnimationState().equals("die")) {
            return collision;
        }

        for (int a = 0; a < entities.size(); a++) {

            Entity e = entities.get(a);
            if (e.getType() == 1) //fireball
            {
                if (e.getAnimationState().equals("dead")) {
                    entities.remove(a);
                    return collision;
                }

                //test for collisions between fireball and enemies
                for (int b = 0; b < entities.size(); b++) {

                    if (a != b && e.getBounds().intersects(entities.get(b).getBounds())) {

                        if (entities.get(b).getFireInvunerable() == 0) //kill enemy with fire
                        {
                            if (!entities.get(b).getRotate() && entities.get(b).getType() >= 20 && entities.get(b).getType() < 50) {
                                //remove fireball
                                entities.get(a).setAnimationState("die");
                                entities.get(a).setJump(false);
                                entities.get(a).setSpeed(0);
                                entities.get(a).setMaxY(entities.get(a).getDown());
                                //add points
                                addScore(100);
                                drawPoints(100, entities.get(b).getLeft() + 1, entities.get(b).getUp() - 5);
                                //spin enemy off screen
                                entities.get(b).setRotate(true);
                                entities.get(b).setJump(true);
                                entities.get(b).setSpeed(1);
                                entities.get(b).setJumpSpeed(5);
                                entities.get(b).setFallSpeed(6);
                                if (getDirection() == 'r') {
                                    entities.get(b).setDirection('r');
                                } else {
                                    entities.get(b).setDirection('l');
                                }
                                entities.get(b).setMinY(entities.get(b).getUp() + 25);
                                entities.get(b).setMaxY(200000);
                            }
                        } else //enemy is invunerable to fire
                        {
                            //stop fireball and remove
                        	if (!entities.get(b).getRotate()) //enemy is not dead
                        	{
                        		entities.get(a).setAnimationState("die");
                        		entities.get(a).setJump(false);
                        		entities.get(a).setSpeed(0);
                        		entities.get(a).setMaxY(entities.get(a).getDown());
	                            return collision;
                        	}
                        }

                    }
                }
            }

            //stop player jumping over flag pole
            if (e.getType() == 9 && mFlagState == 0) {
                if (getDown() <= e.getUp()) {
                    if (getRight() >= e.getLeft()) {
                        setX(e.getLeft() - getWidth() - 8);
                    }
                }
            }


            //castle flag
            if (e.getType() == 7) {
                if (mFlagState == 5) //user has reached the end of the level
                {
                    entities.get(a).setAnimationState("raise");
                    if (e.getFrameIndex() >= e.getFrameCount()) {
                        mFlagState = 6;
                    }
                } else if (mFlagState == 6) {
                    entities.get(a).setAnimationState("flying");
                }
            }


            //growing vine
            if (getBounds().intersects(e.getVineBounds()) && e.getType() == 14) //player intersects growing vine entity
            {
                //climb vine if falling, standing
                if (mControls.mUpPressed && (getAnimationState().equals("jump") || getAnimationState().equals("stand"))) {
                    setAnimationState("climb"); //set climb animation
                    setJump(false);
                    if (getSpeed() > 1) {
                        setSpeed(0); 
                    }
                    setClimb(true); 
                }
            } else {
                //set fall state when not on ground level, jumping, vine, flag pole
                if ((!getClimb() && !getJump()) && getDown() != getMaxY() && !getAnimationState().equals("jump") && mFlagState > 1) {
                    setAnimationState("fall");
                }
                setClimb(false);

                //check other vines for collision 
                for (int c = 0; c < entities.size(); c++) {
                    Entity d = entities.get(c);
                    if (getBounds().intersects(d.getVineBounds()) && d.getType() == 14 && getAnimationState().equals("climb")) {
                        setClimb(true);
                        setJump(false);
                        if (getSpeed() > 1) {
                            setSpeed(0);
                        }
                    }
                }
            }

            //collision between player and entity
            if (getBounds().intersects(e.getBounds())) {

                //remove enemy on collision
                if (e.getAnimationState().equals("die") || e.getAnimationState().equals("dead") || e.getRotate()) {
                    if (e.getAnimationState().equals("dead") && e.getType() != 1) {
                        entities.remove(a);
                    }
                    return collision;
                }

                collision = true;
                if (e.getType() == 9) //flag pole
                {
                    for (int b = 0; b < entities.size(); b++) {
                        if (entities.get(b).getType() == 8) //end flag
                        {

                            if (getY() + getHeight() / 2 >= entities.get(b).getUp()) {
                                entities.get(b).setAnimationState("fall");
                                entities.get(b).setFallSpeed(3);
                            }

                            if (mFlagState == 0) //drop flag
                            {
                                setX(getX() + getWidth() / 4 - 1);
                                mFlagState = 1;
                                setAnimationState("flag");
                                setSpeed(0);
                                setFallSpeed(1);
                                return collision;
                            } else if (mFlagState == 1) //flag reached base
                            {
                                if (getDown() == entities.get(b).getMaxY() && entities.get(b).getAnimationState().equals("fall")) {
                                    mFlagState = 2;
                                    return collision;
                                }
                            }
                        }
                    }
                }

                //hit enemy while invincible
                if (mInvincible == 1 && e.getType() >= 20 && e.getType() <= 50) {
                    if (!e.getRotate()) {
                        new SoundEffect("res/sfx/kill.wav").start();
                        if (getDirection() == 'r') {
                            entities.get(a).setDirection('r');
                        } else {
                            entities.get(a).setDirection('l');
                        }
                        entities.get(a).setMaxX(200000);
                        entities.get(a).setRotate(true); //spin off screen
                        entities.get(a).setJump(true);
                        entities.get(a).setSpeed(1);
                        entities.get(a).setJumpSpeed(2);
                        entities.get(a).setFallSpeed(4);
                        entities.get(a).setMinY(e.getUp() - 8);
                        entities.get(a).setMaxY(200000);
                        collision = true;
                    }
                    return collision;
                }

                if (e.getType() == 10) //mushroom
                {
                    new SoundEffect("res/sfx/powerup.wav").start();
                    addScore(1000);
                    drawPoints(1000, getLeft() + 16, getUp() - 5);
                    if (mSize == 0) {
                        setSize(1);
                    }
                    entities.remove(a);
                } else if (e.getType() == 11) //fire
                {
                    new SoundEffect("res/sfx/powerup.wav").start();
                    addScore(1000);
                    if (getDirection() == 'r') {
                        drawPoints(1000, getLeft() + 16, getUp() - 5);
                    } else {
                        drawPoints(1000, getLeft() - 16, getUp() - 5);
                    }
                    setSize(2);
                    mControls.mRunReleased = false;
                    entities.remove(a);
                } else if (e.getType() == 12) //star
                {
                    new SoundEffect("res/sfx/powerup.wav").start();
                    addScore(1000);
                    if (getDirection() == 'r') {
                        drawPoints(1000, getLeft() + 16, getUp() - 5);
                    } else {
                        drawPoints(1000, getLeft() - 16, getUp() - 5);
                    }
                    setInvincible(1);
                    entities.remove(a);
                } else if (e.getType() == 13) //life
                {
                    addScore(1000);
                    if (getDirection() == 'r') {
                        drawPoints(1000, getLeft() + 16, getUp() - 5);
                    } else {
                        drawPoints(1000, getLeft() - 16, getUp() - 5);
                    }
                    addLife();
                    entities.remove(a);
                } else if (e.getType() == 20) //piranha plant
                {
                    //detect collision between frame range
                    if (e.getAnimationState().equals("normal")) {
                        enemyCollision();
                    }
                }

                //1 hit kill enemy type
                if (e.getType() == 30 || e.getType() == 36 || e.getType() == 37) {
                    enemyCollision();
                }

                //normal 1 hit kill enemy / bullet enemy
                if (e.getType() == 31 || e.getType() == 35) {
                    if (getDown() < e.getUp() + 5) {
                        new SoundEffect("res/sfx/kill.wav").start();
                        bounce();
                        addScore(200);
                        drawPoints(200, e.getLeft() + 7, e.getUp());
                        entities.get(a).setAnimationState("die");
                    } else {
                        enemyCollision();
                    }
                }

                //1 hit get up enemy
                if (e.getType() == 32 && !e.getAnimationState().equals("hit")) {
                    if (getDown() < e.getUp() + 5) {
                        bounce();
                        //addScore(1000);
                        new SoundEffect("res/sfx/hit.wav").start();
                        entities.get(a).setAnimationState("hit");
                    } else {
                        enemyCollision();
                    }
                }

                //flying or normal shell enemy
                if (e.getType() == 33 || e.getType() == 34) {
                    if (e.getAnimationState().equals("normal") || e.getAnimationState().equals("kick")) {
                        //set hit state
                        if (getDown() < e.getUp() + 5) {
                            bounce();
                            //addScore(1000);
                            new SoundEffect("res/sfx/hit.wav").start();
                            entities.get(a).setAnimationState("hit");
                        } else {
                            enemyCollision();
                        }
                    } else if (e.getAnimationState().equals("hit")) {
                        new SoundEffect("res/sfx/hit.wav").start();
                        //bounce off shell
                        if (getDown() < e.getUp() + 5) {
                            bounce();
                        }
                        //kick shell
                        if (getCenter().x > e.getCenter().x) {
                            entities.get(a).setDirection('l');
                            entities.get(a).setX(e.getX() - 5);
                        } else {
                            entities.get(a).setDirection('r');
                            entities.get(a).setX(e.getX() + 5);
                        }
                        entities.get(a).setAnimationState("kick");
                    } else if (e.getAnimationState().equals("fly")) {
                        //stop flying
                        if (getDown() < e.getUp() + 5) {
                            bounce();
                            //addScore(1000);
                            new SoundEffect("res/sfx/hit.wav").start();
                            entities.get(a).setAnimationState("normal");
                            entities.get(a).setY(getY() + 20);
                            entities.get(a).setSpeed(1);
                            entities.get(a).setJump(false);
                        } else {
                            enemyCollision();
                        }
                    }
                }
            }

            //make falling enemy drop
            if (e.getType() == 37) {
                if (getRight() + 5 >= e.getLeft()
                        && getLeft() - 5 <= e.getRight()) {
                    entities.get(a).setAnimationState("fall");
                    entities.get(a).setMaxY(10000);
                }
            }

            if (e.getAnimationState().equals("dead") && e.getType() != 1) //remove dead sprite except fireball
            {
                entities.remove(a);
            }
        }

        return collision;
    }

    /**
     * Sets the player size or removes a life if the player collides with an enemy
     */
    public void enemyCollision() {
        if (!Settings.Invincible) {
            if ((!getAnimationState().equals("die") && mInvincible == 0) || mTime < 1 && !getAnimationState().equals("die")) {
                if (mSize == 0) //small
                {
                    new SoundEffect("res/sfx/die.wav").start();
                    setAnimationState("die");
                    setSpeed(0);
                    setMinY(getDown() - 65);
                    setMaxY(10000);
                    setJump(true);
                } else if (mSize == 1) //large
                {
                    new SoundEffect("res/sfx/powerdown.wav").start();
                    setSize(0);
                    setInvincible(2);
                } else if (mSize == 2) //fire
                {
                    new SoundEffect("res/sfx/powerdown.wav").start();
                    setSize(1);
                    setInvincible(2);
                }
            }
        }
    }

    /**
     * Detects collisions between the player and map tiles
     * @param map The level map
     * @return True if a collision occurred
     */ 
    public boolean detectMapCollision(Map map) {

        boolean collision = false;

        if (getAnimationState().equals("die") || (mPipeState > 1 || mFlagState > 3)) 
        {
            return collision; //skip map collision detection
        }

        if(!getClimb())
        {
	        //bonus / end level warp pipe 
	        int exitX = 0;
	        int exitY = 0;
	        int exitState = 0;
	        //exit pipe condition (start bonus or end level)
	        if ((getCenter().x / 16 == map.getBonusX() || getCenter().x / 16 == map.getBonusX()+1) && getY()/ 16 == map.getBonusY()) //bonus level
	        {
	            exitX = map.getBonusX();
	            exitY = map.getBonusY();
	            exitState = map.getBonusState();
	            mExitBonus = true;
	            collision = true;
	        } else if (getCenter().x / 16 >= map.getEndX() && getY() / 16 == map.getEndY()) //end level
	        {
	            exitX = map.getEndX();
	            exitY = map.getEndY();
	            exitState = map.getEndState();
	            mExitLevel = true;
	            collision = true;
	        }
	        //realign and set pipe state to auto move player through pipe
	        if (mControls.mUpPressed && exitState == 2) //up pipe
	        {
	            mPipeState = 2;
	            setX(exitX * 16);
	            setY(exitY * 16);
	            setMinY((getUp() - 2) - getFrameHeight());
	            return collision;
	        }
	        if (mControls.mDownPressed && exitState == 3) //down pipe
	        {
	            mPipeState = 3;
	            setX(exitX * 16);
	            setY(exitY * 16);
	            setMaxY(getDown() + getFrameHeight());
	            return collision;
	        } else if (mControls.mLeftPressed && exitState == 4) //left pipe
	        {
	            mPipeState = 4;
	            setX(exitX * 16);
	            setY(exitY * 16 - 2);
	            setDirection('l');
	            setMinX(getLeft() - getFrameWidth());
	            return collision;
	        } else if (mControls.mRightPressed && exitState == 5) //right pipe
	        {
	            mPipeState = 5;
	            setX(exitX * 16);
	            setY(exitY * 16 - 2);
	            setDirection('r');
	            setMaxX(getRight() + getFrameWidth());
	            return collision;
	        } else {
	            mExitBonus = false; //reset exit variables
	            mExitLevel = false;
	            collision = false;
	        }
        }

        //coin collision 
        //small
        if (map.getTile(getLeft() / 16, getUp() / 16).tile1 == 10) {
            addCoin();
            map.getTile(getLeft() / 16, getUp() / 16).tile1 = 0;
        }
        if (map.getTile(getRight() / 16, getUp() / 16).tile1 == 10) {
            addCoin();
            map.getTile(getRight() / 16, getUp() / 16).tile1 = 0;
        }
        //large
        if (map.getTile(getLeft() / 16, getCenter().y / 16).tile1 == 10) {
            addCoin();
            map.getTile(getLeft() / 16, getCenter().y / 16).tile1 = 0;
        }
        if (map.getTile(getRight() / 16, getCenter().y / 16).tile1 == 10) {
            addCoin();
            map.getTile(getRight() / 16, getCenter().y / 16).tile1 = 0;
        }

        //down collision
        if ((map.getTile((getRight() - 5) / 16, getDown() / 16).collision == 1
                || map.getTile((getLeft() + 5) / 16, getDown() / 16).collision == 1)) {
            if (getClimb() && !mControls.mUpPressed) {
                setClimb(false);
            }
            if ((getMaxY() != getDown() && !getJump()) && ! getAnimationState().equals("crouch")) //set ground level
            {
                setMaxY(getDown() / 16 * 16);
                mControls.mJumpPressed = false;
                if(mFlagState != 1)
                	setAnimationState("stand");
            }
            collision = true;
        } else //fall
        {
            if (!getJump()) {
                if (!getAnimationState().equals("jog") || getLeft() < 1) {
                    setMaxY(map.getHeight() + 100);
                } else if (getDown() == getMaxY()) //run across blocks
                {
                    if ((map.getTile((getCenter().x + 16) / 16, getDown() / 16).collision == 0
                            || map.getTile((getCenter().x + 16) / 16, getDown() / 16).collision == 2) //no collision
                            && getLeft() > 5) {
                        setMaxY(map.getHeight() + 100);
                    } else //collision
                    {
                        setMaxY(getDown() / 16 * 16);
                    }
                }
            }
        }

        //up collision player >= 1 for invisible block
        Point colUp = null;
        if (map.getTile(getRight() / 16, getUp() / 16).collision >= 1 && getJump()) {
            colUp = new Point(getCenter().x / 16, getUp() / 16);
            if (map.getTile(colUp.x, colUp.y).collision == 0) //hit corner
            {
                setX(getX() - 2); //realign
                return false;
            } else //up collision
            {
                collision = true;
                setJump(false);
            }
        } else if (map.getTile(getLeft() / 16, getUp() / 16).collision >= 1 && getJump()) {
            colUp = new Point(getCenter().x / 16, getUp() / 16);
            if (map.getTile(colUp.x, colUp.y).collision == 0) {
                setX(getX() + 2);
                return false;
            } else ///up collision
            {
                collision = true;
                setJump(false);
            }
        }

        //collision with block 
        if (colUp != null) {
            Tile t = map.getTile(colUp.x, colUp.y);
            if (Map.isBlock(t.tile1, t.tile2)) {
                if (t.state == 0) {
                    collision = true;
                    map.getTile(colUp.x, colUp.y).state = 1;
                    if (Map.isEmptyBlock(t.tile1, t.tile2) || t.count == 0) //empty block
                    {
                        if (getSize() > 0) {
                            new SoundEffect("res/sfx/block.wav").start();
                            Main.mFrame.mGamePanel.addBlockExplosion(colUp.x, colUp.y);
                            map.removeTile(colUp.x, colUp.y);
                        }
                    }
                    //block containing item
                    if (t.count > 0) {
                        if (Map.isCoinBlock(t.tile1, t.tile2) || Map.is10CoinBlock(t.tile1, t.tile2)) {
                            addCoin();
                        } else if (Map.isEnlargeBlock(t.tile1, t.tile2)) {
                            if(Settings.Powerups)
                                Main.mFrame.mGamePanel.addEntity(10, 'r', colUp.x * 16, colUp.y * 16 - 5);
                        } else if (Map.isFireBlock(t.tile1, t.tile2)) {
                            if(Settings.Powerups)
                                Main.mFrame.mGamePanel.addEntity(11, 'r', colUp.x * 16, colUp.y * 16 - 5);
                        } else if (Map.isInvincibleBlock(t.tile1, t.tile2)) {
                            if(Settings.Powerups)
                                Main.mFrame.mGamePanel.addEntity(12, 'r', colUp.x * 16, colUp.y * 16 - 5);
                        } else if (Map.isExtraLifeBlock(t.tile1, t.tile2)) {
                            if(Settings.Powerups)
                                Main.mFrame.mGamePanel.addEntity(13, 'r', colUp.x * 16, colUp.y * 16 - 5);
                        } else if (Map.isVineBlock(t.tile1, t.tile2)) {
                            Main.mFrame.mGamePanel.addEntity(14, 'r', colUp.x * 16, (colUp.y * 16) - 15); //-5??
                        }
                    }
                }
            }
        }

        //crouching under block
        if (getSize() >= 1 && (map.getTile(getLeft() / 16, (getUp() - 5) / 16).collision == 1 || map.getTile(getRight() / 16, (getUp() - 5) / 16).collision == 1) && getAnimationState().equals("crouch")) {
            setSpeed(0);
            if (getDirection() == 'r') {
                setX(getX() + 1);
            } else {
                setX(getX() - 1);
            }
            mCrouchingUnderBlock = true;
        } else {
            mCrouchingUnderBlock = false;
        }

        //right collision 
        if (map.getTile(getRight() / 16, getUp() / 16).collision == 1 || //small
                map.getTile(getRight() / 16, getCenter().y / 16).collision == 1) //large
        {
            setMaxX(getRight());
            collision = true;
        } else {
            setMaxX(map.getWidth());
        }

        //left collision
        if (map.getTile(getLeft() / 16, getUp() / 16).collision == 1
                || map.getTile(getLeft() / 16, getCenter().y / 16).collision == 1) {
            setMinX(getLeft());
            collision = true;
        } else {
            setMinX(0);
        }

        return collision;
    }

    /**
     * Gets the total amount of coins the player has collected
     * @return The coin count
     */
    public int getCoins() {
        return mCoins;
    }

    /**
     * Gets the total amount of remaining lives the player has
     * @return The lives count
     */
    public int getLives() {
        return mLives;
    }

    /**
     * Gets the total score count for the player
     * @return The score count
     */   
    public int getScore() {
        return mScore;
    }

    /**
     * Increases the coin count by one
     */
    public void addCoin() {
        if (mCoins < 99) {
            new SoundEffect("res/sfx/coin.wav").start();
            addScore(100);
            mCoins++;
        } else {
            mLives++;
            mCoins = 0;
        }
    }

    /**
     * Add the amount to the score count
     * @param value The value to add to the score count
     */
    public void addScore(int value) {
        mScore += value;
    }

    /** Draw the points on the graphics surface
     * @param value The numeric value of the points collected
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void drawPoints(int value, int x, int y) {
        Score scr = new Score(x, y, value);
        mDrawPoints.add(scr);
    }

    /**
     * Increases the lives count by one
     */
    public void addLife() {
        mLives++;
    }

    /**
     * Gets the remaining time limit
     * @return The remaining time
     */
    public int getTime() {
        return mTime;
    }

    /**
     * Decreases the lives count by one
     */   
    public void removeLife() {
 
        if (!Settings.InfiniteLives) {
            mLives--;
        }
    }

    /**
     * Returns the players controller state
     * @return The players controller state
     */   
    public Control setControls() {
        if (!mControls.mRunPressed) {
            mControls.mRunReleased = true;
        }
        return mControls;
    }

    /** Gets the pipe state of the player */
    public int getPipeState() {
        return mPipeState;
    }

    /** Gets the end state of the player */
    public int getEndState() {
        return mEndState;
    }

    /**
     * Sets the state of the player
     * @param index The player animation state index 0-stand, 1-walk, 2-run, 3-jump, 4-fall, 5-crouch
     */
    public boolean setAnimationStateIndex(int index) {
        if (index == 4) {
            if (getDown() == getMaxY()) {
                new SoundEffect("res/sfx/jump.wav").start();
            }
        }
        super.setAnimationStateIndex(index);
        return true;
    }
       
    /**
		This class stores information about the points displayed on the screen when
        the player destroys and enemy or collects a power-up.
	
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
    
    private class Score {

        int value;
        int timer;
        int x;
        int y;
        public Score(int nX, int nY, int nValue) {
            x = nX;
            y = nY;
            value = nValue;
        }
    }
}
