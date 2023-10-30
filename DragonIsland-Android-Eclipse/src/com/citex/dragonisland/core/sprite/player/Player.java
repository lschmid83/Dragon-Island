package com.citex.dragonisland.core.sprite.player;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.drawing.Rectangle;
import com.citex.dragonisland.core.game.Control;
import com.citex.dragonisland.core.game.GameData;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.SpriteSheet;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.enums.FlagState;
import com.citex.dragonisland.core.sprite.player.enums.LevelExit;
import com.citex.dragonisland.core.sprite.player.enums.PipeState;
import com.citex.dragonisland.core.sprite.player.enums.PlayerSize;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.tileset.Tile;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Numbers;

import java.awt.Color;
import java.awt.Graphics;
/**
 * This class is a user controllable player.
 * @author Lawrence Schmid
 */
public class Player extends Sprite {

    /** Main thread. */
    private Main mMain;
	
	/** Player sprites. */
	private ArrayList<Sprite> mSprites;
	
	/** Invincible stars overlay. */
	private SpriteSheet mStars;
	
	/** Icon. */
	private IBufferedImage mIcon;
	
	/** Number of lives remaining. */
    private int mLives;
    
    /** Score. */
    private int mScore;
    
    /** Coins. */
    private int mCoins;
    
    /** Player state. */
    private PlayerSize mSize;
    
    /** Invincible. 0-None, 1-Power-up, 2-Enemy hit */
    private int mInvincible;
    
    /** Time elapsed while invincible. */
    private float mInvincibleTimer;
    
    /** Invincible animation frame index. */
    private int mInvincibleFrame;
    
    /** Has the player been hit. */ 
    private boolean mFlicker;
    
    /** Remaining time. */
    private int mTime;
        
    /** Controls. */
    private Control mControls;
    
    /** Jump button released. */
    private boolean mJumpReleased;
    
    /** Player is crouching trapped under block. */
    private boolean mCrouchingUnderBlock;

    /** Climbing. */
    private boolean mClimb;
    
    /** Has the jump sound effect played. */
    private boolean mJumpSfx;
    
    /** Time elapsed between throwing fire balls. */
    private float mFireTimer;

    /** Warp pipe state. */
    private PipeState mPipeState;
    
    /** Exit level state .*/
    private LevelExit mExit; 
    
    /** End of level state */
    private FlagState mFlagState;
    
    /** On bonus level. */
    private boolean mBonusLevel;

    /** Level header. */
    private Header mLevelHeader;
    
    /** Time elapsed between clock ticks. */
    private float mClockTimer;
    
    /** Time elapsed between waiting to exit level. */
    private float mVictoryTimer;
    
    /** Has the player bounced after hitting enemy. */
    private boolean mBounce;
    
    /** Has the player already entered the bonus level. */
    private boolean mUsedBonus;
        
    /** 
     * Initialises a Player object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws Exception 
     * @throws IOException 
     */
    public Player(Main main, Object g, String path) throws IOException, Exception {
    	
    	// Initialise the parent sprite object.
    	super();
    	
    	// Initialise the sprites.
    	mSprites = new ArrayList<Sprite>();
    	
    	// Initialise a list of sprite sheets to load.
   		mSprites.add(new Sprite(g, path + "0"));
  		mSprites.add(new Sprite(g, path + "1"));
  		mSprites.add(new Sprite(g, path + "2"));
    	
  		// Load stars sprite sheet.
  	    mStars = new SpriteSheet(g, path + "3.png", 48, 48, 20);
  		
    	// Load icon.
    	mIcon = FileIO.getImageResource(g, path + "4.png");
    	
    	// Initialise the controls.
    	mControls = new Control();
    	
    	// Initialise the default sprite.
    	setSprite(mSprites.get(0));  
        
    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("stand");
        
    	// Set the direction.
    	setDirection('r');
      	
        // Set main thread reference.
        mMain = main;

    }
    
    /**
     * Initialises the player.
     * @param header LevelHeader object.
     * @param gameData GameData object.
     */
    public void init(Header header, GameData gameData) {

    	// Initialise the level header for resuming level from bonus area.
    	if(!mBonusLevel) {
    		mLevelHeader = header;   
    		mUsedBonus = false;
    	}
    	
    	// Resume level from bonus area.
    	PipeState state;
    	if(mBonusLevel && header.area != 0) {
    		 
    		// Set start position.
    		setPosition(new Point(header.bonusX * 16, header.bonusY * 16));
    		
    		// Reverse pipe state so player exits pipe.
    		state = PipeState.values()[header.bonusState + 1];
            if (state == PipeState.UP_PIPE)
                state = PipeState.DOWN_PIPE;
            else if (state == PipeState.DOWN_PIPE)
                state = PipeState.UP_PIPE;
            else if (state == PipeState.LEFT_PIPE)
                state = PipeState.RIGHT_PIPE;
            else if (state == PipeState.RIGHT_PIPE)
                state = PipeState.LEFT_PIPE;
            
            mBonusLevel = false;
            mUsedBonus = true;
    		
    	} else {
    		
    		// Set start position.
    		setPosition(new Point(header.startX * 16, header.startY * 16));
    		state = PipeState.values()[header.startState + 1];
    	}

    	// Set start position and direction.
    	if(state == PipeState.STAND_LEFT)  {
    		
    		// Stand left.
    		setDirection('l');
            mPipeState = PipeState.NONE;
    		
    	} else if(state == PipeState.STAND_RIGHT) {
    		
    		// Stand right.
    		setDirection('r');
            mPipeState = PipeState.NONE;
            
    	} else if(state == PipeState.UP_PIPE) {
    		
    		// Move up.
    		setMinMove().y = getUp();
            setPosition(new Point(getPosition().x, getY() + getFrameHeight()));
            setDirection('r');
            mPipeState = PipeState.UP_PIPE;
            
    	} else if(state == PipeState.DOWN_PIPE) {
    		
    		// Move down.
    		setMaxMove().y = getDown();
    		setPosition(new Point(getPosition().x, getY() - getFrameHeight()));
            setDirection('r');
            mPipeState = PipeState.DOWN_PIPE;
            
    	} else if(state == PipeState.LEFT_PIPE) {
    		
    		// Walk left.
            setMinMove().x = getLeft();
            setPosition(new Point(getX() + getFrameWidth(), getY() - 2));
            setDirection('l');
            mPipeState = PipeState.LEFT_PIPE;
            
    	} else if(state == PipeState.RIGHT_PIPE) {
    		
    		// Walk left.
            setMaxMove().x = getRight();
            setPosition(new Point(getX() - getFrameWidth(), getY() - 2));
            setDirection('r');
            mPipeState = PipeState.RIGHT_PIPE;
    	}
    	
    	// Player is entering level.
    	mExit = null;
    	alive();
    	mTime = header.timeLimit;
    	mFlicker = false;
    	mInvincible = 0;
    	mFlagState = FlagState.NONE;
    	setClimb(false);
    	mControls.reset();
    	getVelocity().x = 0;
    	setAnimationState("stand");
    	
    	if(gameData == null) {
    		
    		// Initialise a new player.
    		mLives = 3;
    		mCoins = 0;
    		mScore = 0;
    		setSize(PlayerSize.SMALL);
    		
    	} else {
    		
    		// Restore player from save game data.
    		mLives = gameData.lives;
    		mCoins = gameData.coins;
    		mScore = gameData.score;
    		setSize(PlayerSize.values()[gameData.size]);
    		
    	}
    	
    	//setSize(PlayerSize.FIRE);
    	
    }
        
    /**
     * Draws the player.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera coordinates.
     */    
    public void draw(Object g, float dt, Point cam) {

    	// Draw the sprite.
        if(!mFlicker)
        	super.draw(g, dt, cam);
        else {	
        	if((int)mInvincibleTimer % 2  == 0)
        		super.draw(g, dt, cam);	
        }
        
        // Move the player.
        if(mPipeState == PipeState.NONE)
        	move(dt, cam);  
        else if(mPipeState != PipeState.NONE)
        	movePipe(g, dt, cam);
  
        // Set the movement controls.
        setMovement(g, dt);
        
        // Player is invincible.
        if(mInvincible > 0) 
        	drawInvincible(g, dt, cam);
        
        // Set the time count down.
        if(Settings.State != "title" && !Settings.Paused)
        	setTime(dt);
        
    	// Player has fallen off the map.
        if(getUp() > mMain.getLevel().getMap().getHeight() || getAnimationState().equals("die"))
        	getVelocity().x = 0;
        
        if(getUp() > mMain.getLevel().getMap().getHeight() + 100 && !isDead()) {

			// Kill player.
			kill();
		} 
    }   
    
    /**
	 * Move the player.
	 * @param dt Delta time between frame updates.
	 * @param cam Camera coordinates.
	 */
	public void move(float dt, Point cam)
	{
		// Skip if game is paused.
		if(Settings.Paused)
			return;
		
		// Get the velocity.
		Point velocity = getVelocity();			
		
		// Apply gravity force.
		float gravity;
		if(mFlagState == FlagState.SLIDE_DOWN_POLE)
			gravity = -1.6f;
		else 
			gravity = -4.8f;

		if(getDown() < getMaxMove().y && !getClimb())
	    	velocity.y += gravity;
		else
			velocity.y = 0;
		
	    // Add a dampening force to simulate friction.
	    velocity.x *= 0.90f;
	    velocity.y *= 0.90f;
	    
	    // Add the forward motion force.
	    float forwardMovement = 0.017f;
	    if(getAnimationState() == "run")
	    	forwardMovement = 0.025f;  	
	    else if(mFlagState == FlagState.JUMP_OFF_POLE)
	    	forwardMovement = 0.16f;  
	    else if(mFlagState == FlagState.WALK_TO_CASTLE)
	    	forwardMovement = 0.0128f;  

	    if ((mControls.left || mControls.right) && (!mControls.down || getJump()) && getAnimationState() != "die")
	    	velocity.x += forwardMovement;
	    
	    // Add a jump force.
	    float jumpForce = 4.8f; 

	    if(getDown() > getMinMove().y && (getJump() && mControls.jump) && !getClimb()) 
    		velocity.y += jumpForce;
    	else
    		setJump(false);
    	
    	// Add a climb force.
	    if(getClimb()) {
		    float climbForce = 0.064f;
		    if(mControls.up)
		    	velocity.y += climbForce;
		    else if(mControls.down)
		    	velocity.y -= climbForce;
	    }
	    // Multiply velocity by delta time.
	    Point stepVelocity = new Point(velocity.x * dt, velocity.y * dt);	
	    
	    // Apply clamping to limit maximum movement speed.
	    stepVelocity.x = Numbers.clamp(stepVelocity.x, 0, 3.4f);
	    stepVelocity.y = Numbers.clamp(stepVelocity.y, gravity, 0.30f * dt); 
	    
	    if(velocity.x < 0.005f)
	    	velocity.x = 0;
	    
	    if(stepVelocity.y < -6.0f)
	    	stepVelocity.y = -6.0f;
		
	    // Set the position.
	    if(getDirection() == 'r' && getRight() < getMaxMove().x) {
	    	setPosition(new Point(getPosition().x + stepVelocity.x, getPosition().y - stepVelocity.y));
	    } else if(getRight() >= getMaxMove().x) {
	    	setPosition(new Point((getPosition().x / 16) * 16, getPosition().y - stepVelocity.y));
	    } 
	    
	    if(getDirection() == 'l' && getLeft() > getMinMove().x) {
	    	setPosition(new Point(getPosition().x - stepVelocity.x, getPosition().y - stepVelocity.y));
	    } else if(getLeft() <= getMinMove().x) {
	    	setPosition(new Point((getPosition().x / 16) * 16, getPosition().y - stepVelocity.y));	
	    }
	    
	    // Move while crouching under block.
        if(!mControls.down && mCrouchingUnderBlock) {
        	if(getDirection() == 'r' && getRight() < getMaxMove().x)
                setX(getX() + dt * 0.05f);
        	else if(getDirection() == 'l' && getLeft() > getMinMove().x) 
                setX(getX() - dt * 0.05f);   
        }
    
	}
	
    /**
	 * Move the player inside a warp pipe.
	 * @param g Graphics context.
	 * @param dt Delta time between frame updates.
	 * @param cam Camera coordinates.
	 */
	public void movePipe(Object g, float dt, Point cam) {
	
		// Skip if game is paused.
		if(Settings.Paused)
			return;	
		
		// Wait for screen transition to finish.
		if(!mMain.getScreenTransition().isFinished())
			return;
		
		// Move the player in the pipe direction.
		boolean exitedPipe = false;
		if(mPipeState == PipeState.UP_PIPE) {
			
			// Move up.
            if (getUp() > getMinMove().y)
                moveUp(dt, 0.07f);
            else
            	exitedPipe = true;
			
		} else if(mPipeState == PipeState.DOWN_PIPE) {
			
			// Move down.
            if (getDown() < getMaxMove().y)
                moveDown(dt, 0.07f);
            else
            	exitedPipe = true;
            
		} else if(mPipeState == PipeState.LEFT_PIPE) {
			
			// Move left.
            if (getLeft() > getMinMove().x)
                moveLeft(dt, 0.07f);
            else
            	exitedPipe = true;
	
		} else if(mPipeState == PipeState.RIGHT_PIPE) {
			
			// Move right.
            if (getRight() < getMaxMove().x)
                moveRight(dt, 0.07f);
            else
            	exitedPipe = true;
		}
		
		// Exited warp pipe.
		if(mExit != null && exitedPipe) {

			// Get level header.
			Header header = mMain.getLevel().getHeader();
			
			// Create game progress.
			GameData gameData = mMain.getSaveFile().getSaveGame(mMain.getGameFolderIndex());
			gameData.game = mMain.getGameFolderIndex();
			gameData.character = 0;
			gameData.lives = getLives();
			gameData.score = getScore();
			gameData.coins = getCoins();
			gameData.size = getSize().ordinal();

			// Update game data.
			mMain.getSaveFile().setSaveGame(mMain.getGameFolderIndex(), gameData);
						
			if(mExit == LevelExit.EXIT_LEVEL) {
		
				// Return to level.
				if(mBonusLevel) {
					mMain.setLevelPath(header.world + "." + header.level + ".1.lvl");
					Settings.State = "load level";
				}
				else {
				
    				if(header.endWorld != 0 && header.endLevel != 0 && header.endArea != 0) {

    					if(header.level != header.level) {
    						
    						// Return to level select screen.
    						Settings.State = "start level select"; 
            				
        					if(header.endWorld >= gameData.world) {
        						gameData.world = header.endWorld;
        					    					
    	    					if(header.endLevel > gameData.level)
    	    						gameData.level = header.endLevel;
        					}
        					 
        					// Save game.
        					try {
    							mMain.getSaveFile().saveGameData(mMain.getGameFolder().getName() + ".sav", gameData);
        					} catch (IOException e) { }
    						
        					// Set high score
        					setHighScore();
            				        			    						
    					} else {
    						
    						// Advance to next area.
        					mMain.setLevelPath(header.endWorld + "." + header.endLevel + "." + header.endArea + ".lvl");
        					Settings.State = "load level"; 
        					
    					}
	
    				} else 
    					mMain.getGame().setState("credits");
    				
				}
				
			} else {
				
				// Advance to bonus level.
				mMain.setLevelPath(header.world + "." + header.level + "." + "0.lvl");
				mBonusLevel = true;
				Settings.State = "load level"; 
				
			}
			
			// Set velocity.
			getVelocity().x = 0;
			
	
		} else {
			
			// Entering level.
			if(exitedPipe)
				mPipeState = PipeState.NONE;
			
			// Set velocity.
			getVelocity().x = 0;
		}
	}

	/**
	 * Sets the remaining time.
	 * @param dt Delta time between frame updates.
	 */
	public void setTime(float dt) {
		
		// Time is enabled.
        if(mFlagState == FlagState.NONE) {
		
			if (!Settings.FreezeTime) {
	            
	        	// 1 second has passed.
	            if (mClockTimer > 1200) {
	                
	            	// Time remaining.
	            	if (mTime > 0) {
	            		
	            		// Update remaining time.
	                    mTime--;
	                    
	                } else {
	                	
	                	// Time has run out.
	                	kill();
	                	
	                }
	            	
	            	// Reset clock timer.
	            	mClockTimer = 0;
	            }
	        	
	            // Add delta time to clock timer.
	            mClockTimer += dt;
	            	
	        }
        } else {
        	
        	// Count down time.
            if (mTime - 1 > 0) {
                mTime -= 1;
                addScore(10);
            }
            else {

            	// Player has walked to castle.
            	mTime = 0;
            	if(getRight() >= getMaxMove().x) {
            		
            		// Wait to end level.
        			if(mVictoryTimer < 500)
        				mVictoryTimer += dt;
        			else {

        				// Load next level.
        				mVictoryTimer = 0;
        				Header header = mMain.getLevel().getHeader();
        				
    					// Create game progress.
    					GameData gameData = mMain.getSaveFile().getSaveGame(mMain.getGameFolderIndex());
    					gameData.game = mMain.getGameFolderIndex();
    					
    					if(header.endWorld >= gameData.world) {
    						gameData.world = header.endWorld;
    					    					
	    					if(header.endLevel > gameData.level)
	    						gameData.level = header.endLevel;
    					}
    					gameData.character = 0;
    					gameData.lives = getLives();
    					gameData.score = getScore();
    					gameData.coins = getCoins();
    					gameData.size = getSize().ordinal();
    					 
    					// Save game.
    					try {
							mMain.getSaveFile().saveGameData(mMain.getGameFolder().getName() + ".sav", gameData);
    					} catch (IOException e) { }
        				
    					// Set high score
    					setHighScore();
        				        				
        				if(header.endWorld != 0 && header.endLevel != 0 && header.endArea != 0) {
 
        					// Return to level select screen.
        					Settings.State = "load level";
        					mMain.setLevelPath("0.0.1.lvl");
        				}
        				else
        					mMain.getGame().setState("credits");
        				

	
        			}
            	}
            }
        }

	}
		
	/**
	 * Sets the movement and state of the player sprite.
	 * @param dt Delta time between frame updates.
	 */
	public void setMovement(Object g, float dt) {
		
		// Skip if player is dead.
		if(getAnimationState().equals("die")) {
			animate(dt);
			return;
		}
		
		// Skip while player is on flag pole.
		if(mFlagState == FlagState.SLIDE_DOWN_POLE) {
			setAnimationState("flag");
			animate(dt);
			return;
		}
		
		// Skip while player is in pipe.
		if(mPipeState != PipeState.NONE) {
			setAnimationState("stand");
			animate(dt);
			return;
		}		
		
		// Wait while player celebrates finishing the level.
		if(mFlagState == FlagState.VICTORY) {
						
			if(mVictoryTimer < 1200) {
				mVictoryTimer += dt;
			}
			else {
				
				// Set player to walk to castle.
				mVictoryTimer = 0;
				mFlagState = FlagState.WALK_TO_CASTLE;
				mControls.right = true;
				setMaxMove().x = getX() + 145;
			}
			
			animate(dt);
			return;
		}
					
	    // Set player direction.
	    if(mControls.right && !mControls.left && getDirection() == 'l') {
	    	setDirection('r');
	    	mControls.directionHeld = 0;
	    } else if(mControls.left && !mControls.right && getDirection() == 'r') {
	    	setDirection('l');
	    	mControls.directionHeld = 0;
	    }

	    // Set animation state.
        if(!getClimb()) {
		            	
        	// Standing.
        	if (getVelocity().x == 0.0f) {
	            setAnimationState("stand");
	            mControls.directionHeld = 0;
	        } 
	        else if (getVelocity().x > 0.0f) {
	        	
	        	// Walking.
	            setAnimationState("walk");
	        }

        	
        } else {
        	
        	// Climbing.
        	if(mControls.up || mControls.down) {
        		
        		// Not moving.
        		getVelocity().x = 0;

        		// Jump sound effect.
        		mJumpSfx = false;
        		
        		// Climbing animation.
        		setAnimationState("climb");
        		
            	// Set jump height.
           		setMinMove().y = getDown() - Settings.JumpHeight;
            	
            	// Advance frame.
        		animate(dt);	

        	}
        	
            // Jump.
            if (mControls.jump && mJumpReleased || (getClimb() && (mControls.left || mControls.right))) {
            	setJump(true);
    	    	mJumpReleased = false;
            }
        	
        }
        
        // Fireball.
        mFireTimer++;
        if (mControls.fire) {
            if (mFireTimer > 40 && mSize == PlayerSize.FIRE) {
               
                // Add fireball.
                if (!getClimb()) {
                   
                	// Play sound effect.
                	mMain.getSoundEffects().play("fire.wav");
                    
                	if (getDirection() == 'r') {
                        mMain.getGame().addEntity(0, 'r', 0, getCenter().x + 5, getUp());
                    } else {
                    	mMain.getGame().addEntity(0, 'l', 0, getCenter().x - 25, getUp());
                    }
                }
                mFireTimer = 0;
            }
        }   
        
                
        // If direction is held set animation state to run.
        mControls.setDirectionHeld(dt);
        if(mControls.directionHeld > 45 && mFlagState != FlagState.WALK_TO_CASTLE)
        	setAnimationState("run");     
        
    	// Crouch.
    	if((mControls.down || mCrouchingUnderBlock) && !getClimb() && mExit == null ) 
    		setAnimationState("crouch");

    	
        // Set jumping animation.
        if(getVelocity().y != 0 && !mControls.down && !getClimb()) {
        	
       		if(!mJumpSfx && !mBounce && getVelocity().y > 0) {
       			mMain.getSoundEffects().play("jump.wav");
       			mJumpSfx = true;
       		}   	
        	
        	setAnimationState("jump");
        }
        
	    // Advance the frame of animation.
        if(!getClimb())
        	animate(dt);
		
	}
	
	/**
	 * Draws the invincible player for a set amount of time.
	 * @param g Graphics context.
	 * @param dt Delta time between frame updates.
	 * @param cam Camera coordinates.
	 */
	public void drawInvincible(Object g, float dt, Point cam) {
		
		// Player is not dead or in a warp pipe or end of level.
        if (!getState().equals("die") && mFlagState == FlagState.NONE && mPipeState == PipeState.NONE) {

        	// Invincibility power-up.
            if (mInvincible == 1) 
            {
            	// Wait for frame updates.
                if ((int)mInvincibleTimer % 2 == 0) {
                    if (mInvincibleFrame < 10) {
                        mInvincibleFrame++;
                    } else {
                        mInvincibleFrame = 0; 
                    }
                }
                mInvincibleTimer+=dt;
                
                // Display stars animation over player.
                if (getDirection() == 'r')
                    mStars.drawFrame(g, mInvincibleFrame, getCenter().x - 28 - cam.x, getUp() - 16 - cam.y);
                else
                    mStars.drawFrame(g, mInvincibleFrame, getCenter().x - 19 - cam.x, getUp() - 16 - cam.y);
                               
                // Invicibility has run out.
                if (mInvincibleTimer > 9000) {
                    mInvincibleTimer = 0;
                    setInvincible(0);
                }
                
            } else if (mInvincible == 2) {
            	
            	// Temporary invincibility after enemy collision.
                mFlicker = true;
                if (mInvincibleTimer > 800) {
                    mInvincibleTimer = 0;
                    setInvincible(0);
                    mFlicker = false;;
                }
                mInvincibleTimer+=dt;
            }	
            
        }
	}
	
    /**
     * Detect collisions between the player and entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities) {
    	// Not implemented.
    }
	
	/**
     * Detects collisions between the player and map tiles.
     * @param map Level map.
     */ 
    public void detectMapCollision(Object g, Map map) {
    	
    	// Skip collision detection if player is in a pipe.
    	if(mPipeState != PipeState.NONE || mFlagState == FlagState.SLIDE_DOWN_POLE || mFlagState == FlagState.WALK_TO_CASTLE)
    		return;
    	
		// Skip if player is dead.
		if(getAnimationState().equals("die"))
			return;
    	    	
        // Up.
    	Point col = null;
        if (map.getTile(getRight(), getUp()).collision >= 1 && getJump()) {

            // Hit corner of block to right.
        	if (map.getTile(getCenterX(), getUp()).collision == 0) {
                setX(getX() - 2);
            } else {
   
            	// Store the coordinates of the collision.
                col = Numbers.normalisePoint(getCenter().x, getUp());

                // Fall.
                setJump(false);
    
            }
        } else if (map.getTile(getLeft(), getUp()).collision >= 1 && getJump()) {
        	
            // Hit corner of block to left.
            if (map.getTile(getCenterX(), getUp()).collision == 0) {
                setX(getX() + 2); 
            } else {
            	
            	// Store the coordinates of the collision.
                col = Numbers.normalisePoint(getCenter().x, getUp());

            	// Fall.
                setJump(false);
            }
        } 
        
        // Collision with block.
        if (col != null) {
            
        	// Get the tile information.
        	Tile t = map.getTile((int)col.x, (int)col.y);
            
            // Tile is a block.
            if (Map.isBlock(t.tileIndex, t.tilesetIndex)) {
                
            	// Block has not been hit.
            	if (t.state == 0) {
  
            		// Set the state to hit.
                    t.state = 1;
                    
                    // Empty block.
                    if (Map.isEmptyBlock(t.tileIndex, t.tilesetIndex) || t.count == 0) {
                    	
                    	// If player is large then smash block.
                        if (getSize() != PlayerSize.SMALL) {
                            
                        	mMain.getSoundEffects().play("block.wav");
                        	mMain.getGame().addExplosion(col.x, col.y);
                            map.removeTile(col.x, col.y);
                        }
                    }
                    
                    // Block containing item.
                    if (t.count > 0) {
                         
                    	if (Map.isCoinBlock(t.tileIndex, t.tilesetIndex) || Map.is10CoinBlock(t.tileIndex, t.tilesetIndex)) {
                           	
                    		// Coin block.
                    		addCoin();
                        } else if (Map.isMushroomBlock(t.tileIndex, t.tilesetIndex)) {
                            
                    		// Mushroom.
                    		if(Settings.Powerups)
                                mMain.getGame().addEntity(4, 'r', 0, col.x, col.y - 16);
                        }
                        else if (Map.isFireBlock(t.tileIndex, t.tilesetIndex)) {
                            
                        	// Flower.
                        	if(Settings.Powerups)
                                mMain.getGame().addEntity(5, 'r', 0, col.x, col.y - 16);
                        }
                        else if (Map.isInvincibleBlock(t.tileIndex, t.tilesetIndex)) {
                            
                        	// Star.
                        	if(Settings.Powerups)
                        		mMain.getGame().addEntity(6, 'r', 0, col.x, col.y - 16);
                        }
                        else if (Map.isExtraLifeBlock(t.tileIndex, t.tilesetIndex)) {
                            
                        	// Extra life.
                        	if(Settings.Powerups)
                        		mMain.getGame().addEntity(7, 'r', 0, col.x, col.y - 16);
                        } 
                        else if (Map.isVineBlock(t.tileIndex, t.tilesetIndex)) {
                        	
                        	// Vine.
                        	mMain.getGame().addEntity(8, 'r', 0, col.x, col.y - 16);
                        }
                    }
                }
            }
        }      

       	// Lava.
    	if ((map.getTile(getRight() - 5, getDown()).collision == 3 || 
             map.getTile(getLeft() + 5, getDown()).collision == 3)) {
    		
    		// One hit kill.
    		die();
    		return;
    	}
                
    	// Down.
        if ((map.getTile(getRight() - 5, getDown()).collision == 1 || 
             map.getTile(getLeft() + 5, getDown()).collision == 1)) {
	
        	// Set jump sound to false.
        	mJumpSfx = false;
        	
        	// Calculate the down position aligned to the grid.
        	int down = getDown() / 16 * 16;          	
        	setMaxMove().y = getDown() / 16 * 16;

           	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));
       		
            // Jump.
            if (mControls.jump && mJumpReleased) {
    	    	
            	// Set min / max coordinates.
           		setMinMove().y = getDown() - Settings.JumpHeight;
            	
            	setJump(true);
    	    	mJumpReleased = false;
    	    	
            } 
            
        	// Set jump button released.
            if (!mControls.jump)
            	mJumpReleased = true;
            else
            	mJumpReleased = false;
            
            // Enable jump after bounce.
            if(mBounce && !getJump()) {
            	mJumpReleased = true;
            	mControls.jump = false;
            	mBounce = false;
            }
            
            // Set velocity.
            getVelocity().y = 0;
            
            // Stop climbing.
            if(getClimb()) {
            	setClimb(false);
            }
            
            // Player has landed from flag pole.
            if(mFlagState == FlagState.JUMP_OFF_POLE) {
            	mControls.right = false;
            	mControls.jump = false;
            	getVelocity().x = 0;
            	setAnimationState("victory");
            	mFlagState = FlagState.VICTORY;
            }
	
        }
        else {

        	// Run across gaps.
        	if(mControls.directionHeld > 45 && getDown() == getMaxMove().y) {

        		if (map.getTile((int)getCenter().x + 16, getDown()).collision == 1)
        			setMaxMove().y = (getDown() / 16 * 16);
                else
                	setMaxMove().y = map.getHeight() + 200;	
        		
        	} else
        		setMaxMove().y = map.getHeight() + 200;
        }
        
        // Crouching under block.
        if (getSize().ordinal() >= 1 && 
            (map.getTile(getLeft(), getUp() - 5).collision == 1 || 
             map.getTile(getRight(), getUp() - 5).collision == 1) && 
            (getAnimationState().equals("crouch") || mCrouchingUnderBlock)) {
            mCrouchingUnderBlock = true;
        } else {
            mCrouchingUnderBlock = false;
        }
        
        // Right.
        if (map.getTile(getRight(), getUp()).collision == 1 ||
            map.getTile(getRight(), getCenterY()).collision == 1) 
        {
            setMaxMove().x = getRight();
        } else {
        	
        	if(mFlagState == FlagState.NONE)
        		setMaxMove().x = map.getWidth();
        }

        // Left.
        if (map.getTile(getLeft(), getUp()).collision == 1 || 
        	map.getTile(getLeft(), getCenterY()).collision == 1) {
            setMinMove().x = getLeft();
        } else {
            setMinMove().x = 0;
        }
        
        // Coin.
        col = null;
        if (map.getTile(getLeft(), getUp()).tileIndex == 10)
            col =  Numbers.normalisePoint(getLeft(), getUp());
        else if (map.getTile(getLeft(), getDown()).tileIndex == 10)
            col =  Numbers.normalisePoint(getLeft(), getDown());
        else if (map.getTile(getLeft(), (int)getCenter().y).tileIndex == 10)
            col =  Numbers.normalisePoint(getLeft(), (int)getCenter().y);
        else if (map.getTile(getRight(), getUp()).tileIndex == 10)
            col =  Numbers.normalisePoint(getRight(), getUp());
        else if (map.getTile(getRight(), getDown()).tileIndex == 10)
            col =  Numbers.normalisePoint(getRight(), getDown());
        else if (map.getTile(getRight(), (int)getCenter().y).tileIndex == 10)
            col =  Numbers.normalisePoint(getRight(), (int)getCenter().y);
          
        if(col != null) {
           	
        	// Get the tile information.
        	Tile t = map.getTile((int)col.x, (int)col.y);
            
        	if(Map.isCoin(t.tileIndex, t.tilesetIndex)) {		
        		addCoin();
        		t.tileIndex = 0;	
        	}        	
        
        }

    }
        
	/**
     * Detects collisions between the player and map tiles.
     * @param map Level map.
     */ 
    public void detectPipeCollision(Object g, Map map, Point cam) {
    	
    	// Skip if player is already in a pipe.
    	if(mPipeState != PipeState.NONE)
    		return;

    	// Initialise level exit state.
        Point exit = new Point(0, 0);
        LevelExit exitType = LevelExit.NONE;
        PipeState exitState = PipeState.NONE;
         
        // Initialise bonus pipe collision rectangle.
        Rectangle bonus = new Rectangle(0, 0, 0, 0);
        if(map.getBonusState() == PipeState.RIGHT_PIPE)
        	bonus = new Rectangle(map.getBonusX() * 16 + 15, map.getBonusY() * 16 + 10, 11, 11);
        else if(map.getBonusState() == PipeState.LEFT_PIPE)
        	bonus = new Rectangle(map.getBonusX() * 16 + 5, map.getBonusY() * 16 + 10, 11, 11);
        else if(map.getBonusState() == PipeState.DOWN_PIPE)
        	bonus = new Rectangle(map.getBonusX() * 16 + 10, map.getBonusY() * 16 + 15, 11, 11);
        else if(map.getBonusState() == PipeState.UP_PIPE)
        	bonus = new Rectangle(map.getBonusX() * 16 + 10, map.getBonusY() * 16 + 5, 11, 11);
        
        // Initialise end pipe collision rectangle.	 		
        Rectangle end = new Rectangle(0, 0, 0, 0);
        if(map.getEndState() == PipeState.RIGHT_PIPE)
        	end = new Rectangle(map.getEndX() * 16 + 15, map.getEndY() * 16 + 10, 11, 11);
        else if(map.getEndState() == PipeState.LEFT_PIPE)
        	end = new Rectangle(map.getEndX() * 16 + 5, map.getEndY() * 16 + 10, 11, 11);
        else if(map.getEndState() == PipeState.DOWN_PIPE)
        	end = new Rectangle(map.getEndX() * 16 + 10, map.getEndY() * 16 + 15, 11, 11);
        else if(map.getEndState() == PipeState.UP_PIPE)
        	end = new Rectangle(map.getEndX() * 16 + 10, map.getEndY() * 16 + 5, 11, 11);
                
        // Draw bonus bounds.
        if(Settings.DebugMode && Settings.Mode == GameMode.JAVA) {
        	((Graphics)g).setColor(Color.RED);
        	((Graphics)g).drawRect((int)bonus.x - (int)cam.x, (int)bonus.y - (int)cam.y, (int)bonus.width, (int)bonus.height);
        	((Graphics)g).drawRect((int)end.x - (int)cam.x, (int)end.y - (int)cam.y, (int)end.width, (int)end.height);
        }
        
        // Detect warp pipe collision.
        if (getBounds().intersects(bonus)) {

        	// Player can only enter bonus level once per level.
        	if(!mUsedBonus) {
	        	exit.x = map.getBonusX() * 16;
	            exit.y = map.getBonusY() * 16;
	            exitState = map.getBonusState();
	            exitType = LevelExit.EXIT_BONUS;
        	}

        } else if (getBounds().intersects(end)) {
        	
        	// End level.
            exit.x = map.getEndX() * 16;
            exit.y = map.getEndY() * 16;
            exitState = map.getEndState();
            exitType = LevelExit.EXIT_LEVEL;

        }  
        
        // Initialise exit pipe state.
        if (mControls.up && exitState == PipeState.UP_PIPE) {
            
        	// Up pipe.
        	mPipeState = PipeState.UP_PIPE;
            setPosition(new Point(exit.x, exit.y - 2));
            setMinMove().y = getUp() - getFrameHeight() - 10;
            setAnimationState("stand");
            setJump(false);
            mExit = exitType;
            
        } else if(mControls.down && exitState == PipeState.DOWN_PIPE) {

        	// Down pipe.
            mPipeState = PipeState.DOWN_PIPE;
            setPosition(new Point(exit.x, exit.y));
            setMaxMove().y = getDown() + getFrameHeight();  
            setAnimationState("stand");
            mExit = exitType;
            
        } else if(mControls.left && exitState == PipeState.LEFT_PIPE) {
        	
        	// Left pipe.
            mPipeState = PipeState.LEFT_PIPE;
            setPosition(new Point(exit.x, exit.y));
            setDirection('l');
            setMinMove().x = getLeft() - getFrameWidth();
            setAnimationState("stand");
            mExit = exitType;
        	
        } else if(mControls.right && exitState == PipeState.RIGHT_PIPE) {
        	
        	// Right pipe.
            mPipeState = PipeState.RIGHT_PIPE;
            setPosition(new Point(exit.x, exit.y));
            setDirection('r');
            setMaxMove().x = getRight() + getFrameWidth();
            setAnimationState("stand");
            mExit = exitType;

        }
      
    }
    
    /**
     * Sets the player size or removes a life if the player collides with an enemy.
     */
    public void enemyCollision() {
        
    	if (!Settings.Invincible) {
            
    		if ((!getAnimationState().equals("die") && mInvincible == 0) || 
    			(mTime < 1 && !getAnimationState().equals("die"))) {
    
            	if (mSize == PlayerSize.SMALL) {
            		
            		// Small player 
            		die();
                    
                } else if (mSize == PlayerSize.LARGE) {
                	
                	// Large player.
                	mMain.getSoundEffects().play("powerdown.wav");
                    setSize(PlayerSize.SMALL);
                    setInvincible(2);
                    
                } else if (mSize == PlayerSize.FIRE) {
                	
                	// Fire player.
                	mMain.getSoundEffects().play("powerdown.wav");
                    setSize(PlayerSize.LARGE);
                    setInvincible(2);
                }
            }
        }
    } 
    
    /**
     * Makes the player bounce when they hit a enemy.
     */
    public void bounce() {
        
    	setJump(true);
      
        if(mControls.jump)
        	setMinMove().y = getDown() - 50;
        else 
        	setMinMove().y = getDown() - 20;
        
        mBounce = true;
        mControls.jump = true;  
    }
    
    /** 
     * Player has died.
     */
    public void die() {
    	
       	// Small player.
        setAnimationState("die");
        mMain.getSoundEffects().play("die.wav");

        setMinMove().y = getDown() - 45;
        setMaxMove().y = mMain.getLevel().getHeader().height + 200;
        getVelocity().x = 0;
        getVelocity().y = 0;
        mControls.reset();
        mControls.jump = true;
        setJump(true);
    }
    
    /**
     * Increases the coin count.
     */
    public void addCoin() {
        
    	if (mCoins < 99) {
            
    		mMain.getSoundEffects().play("coin.wav");
            addScore(100);
            mCoins++;
        } else {
            mLives++;
            mCoins = 0;
        }
    }
    
    /**
     * Increases the lives count.
     * @param lives Number of lives to add.
     */
    public void addLives(int lives) {
        mLives += lives;
    }
    
    /**
     * Add the amount to the score count
     * @param value The value to add to the score count
     */
    public void addScore(int value) {
        mScore += value;
    }
        
    /**
     * Gets the controls.
     * @return Control object.
     */
    public Control getControls() {
    	return mControls;    	
    }
    
    /**
     * Sets controls from a touch screen event.
     * @param x X coordinate.
     * @param y Y coordinate
     * @param pressed True if the screen is pressed; otherwise false.
     */
    public void setControl(float x, float y, boolean pressed) {

    	// Ignore controls if player has completed level.
    	if(mFlagState == FlagState.NONE)
    		mControls.setControl(x, y, pressed);
    }
    
    /**
     * Sets controls from a key code.
     * @param event KeyCode associated with the key in this event.
     * @parma pressed True if the key is pressed; otherwise false.
     */
    public void setControl(int keyCode, boolean pressed) {
    	
    	// Ignore controls if player has completed level.
    	if(mFlagState == FlagState.NONE && !getAnimationState().equals("die"))
    		mControls.setControl(keyCode, pressed);
    	
    }
    
    /**
     * Gets the players coins.
     * @return Number of coins.
     */
    public int getCoins() {
        return mCoins;
    }

    /**
     * Gets the players lives.
     * @return Number of lives.
     */
    public int getLives() {
        return mLives;
    }

    /**
     * Sets the number of lives.
     * @param lives Number of lives.
     */
    public void setLives(int lives) {
    	mLives = lives;    	
    }
    
    /**
     * Gets the pipe state of the player.
     * @return Pipe state.
     */
    public PipeState getPipeState() {
        return mPipeState;
    }
    
    /**
     * Gets score for the player.
     * @return Number of points.
     */   
    public int getScore() {
        return mScore;
    }
    
    /**
     * Gets the size of the player.
     * @return Player state enum.
     */    
    public PlayerSize getSize() {
        return mSize;
    }  
   
    /**
     * Gets the remaining time.
     * @return Remaining time.
     */
    public int getTime() {
        return mTime;
    }
  
    /**
     * Gets icon.
     * @return Player icon.
     */
    public IBufferedImage getIcon() {
    	return mIcon;    	
    }
    
    /**
     * Sets the invincible state of the player.
     * @param Invincible state.
     */    
    public void setInvincible(int invincible) {
    	mInvincibleTimer = 0;
        mInvincible = invincible;
    }  
    
    /**
     * Gets the invincible state of the player.
     */    
    public int getInvincible() {
        return mInvincible;
    }  
    
    /** Is the player climbing. */
    public boolean getClimb() {
    	return mClimb;    	
    }
    
    /**
     * Sets the climbing state of the player.
     * @param climb Climbing state.
     */
    public void setClimb(boolean climb) {
    	mClimb = climb;
    }
    
    /**
     * Resets the player.
     */
    public void reset() {
    	
    	mControls.reset();
    	mBonusLevel = false;
    	//mLives = 1;
    	mScore = 0;
    	mCoins = 0;
    	mTime = 300;
    	setDirection('r');
    }
    
	/**
	 * Sets a new high score.
	 * @param amount Score.
	 */
	public void setHighScore()
	{
		int score = getScore();
		if(score >= Settings.HighScore[0]) {
			Settings.HighScore[2] = Settings.HighScore[1];
			Settings.HighScore[1] = Settings.HighScore[0];
			Settings.HighScore[0] = score;
			return;
		}
		else if(score >= Settings.HighScore[1])	{
			Settings.HighScore[2] = Settings.HighScore[1];
			Settings.HighScore[1] = score;
			return;
		}
		else if(score >= Settings.HighScore[2]) {	
			Settings.HighScore[2] = score;
		}	
	}
    
    /**
     * Kills the player.
     */
    public void kill() {
    	
    	// Kill the sprite.
    	super.kill();
    	  
		// Create game progress.
		Header header = mMain.getLevel().getHeader();
				
		GameData gameData = new GameData();
		GameData saveFile = mMain.getSaveFile().getSaveGame(mMain.getGameFolderIndex());

		gameData.game = mMain.getGameFolderIndex();
		gameData.world = saveFile.world;
		gameData.level = saveFile.level;
		gameData.character = 0;
		gameData.score = getScore();
		gameData.coins = getCoins();
		gameData.size = PlayerSize.SMALL.ordinal();

    	// Player has remaining lives.
		if(getLives() > 1) {

			// Remove life.
			if(!Settings.InfiniteLives)
				mLives--;
			
			// Player is on bonus level.
			if(mBonusLevel) {
				
				// Reset to main level.
				mMain.setLevelPath(mLevelHeader.world + "." + mLevelHeader.level + "." + mLevelHeader.area + ".lvl");
				mBonusLevel = false;
				
			} else {
				
				// Reset level.
				mMain.setLevelPath(header.world + "." + header.level + ".1.lvl");
			}
			
			gameData.lives = getLives();

			// Set level opening state.
			Settings.State = "load level";	
			
		} else {
			
			// Return to title screen.
			mMain.setLevelPath("0.0.0.lvl");
			Settings.State = "load level";
			
			// Player has died reset score and coins.
			gameData.score = 0;
			gameData.coins = 0;
			gameData.lives = 3;

		}

		// Save game.
		try {
			mMain.getSaveFile().setSaveGame(mMain.getGameFolderIndex(), gameData);
			mMain.getSaveFile().saveGameData(mMain.getGameFolder().getName() + ".sav", gameData);
		} catch (IOException e) { }
		
		// Set high score.
		setHighScore();   	
		
    	
    }
               
    /**
     * Sets the players size.
     * @param size PlayerSize enum value.
     */
    public void setSize(PlayerSize size) {
        
    	mSize = size;  
        
    	if(size == PlayerSize.SMALL)
        	setSprite(mSprites.get(0));  
    	else if(size == PlayerSize.LARGE)
        	setSprite(mSprites.get(1));  
        else if(size == PlayerSize.FIRE)
        	setSprite(mSprites.get(2));        	
        
    } 
    
    /**
     * Gets the end of level flag state.
     * @return FlagState enum.
     */
    public FlagState getFlagState() {
    	return mFlagState;    	
    }
    
    /**
     * Sets the end of level flag state.
     * @param flagState FlagState enum.
     */
    public void setFlagState(FlagState flagState) {
    	mFlagState = flagState;
    }
    
    /**
     * Gets whether the player has entered a bonus level.
     * @return True if the player has entered bonus; otherwise false.
     */
    public boolean isOnBonus() {
    	return mBonusLevel;   
    }
    
    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    @Override
    public void destroy(GL10 gl) {
    	
    	// Destroy sprites.
    	for(Sprite sprite : mSprites) {	
    		if(sprite != null)
    			sprite.destroy(gl);    		
    	}
    	
    	// Destroy stars.
    	if(mStars != null)
    		mStars.destroy(gl);

    	// Destroy icon.
    	if(mIcon != null)
    		mIcon.destroy(gl);
    	
    	// Destroy parent sprite.
    	super.destroy(gl);
    }
 
}
