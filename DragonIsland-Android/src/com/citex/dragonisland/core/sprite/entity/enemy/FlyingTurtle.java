package com.citex.dragonisland.core.sprite.entity.enemy;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.entity.object.Fireball;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * This class a flying turtle enemy.
 * @author Lawrence Schmid
 */
public class FlyingTurtle extends Sprite implements IEntity {

    /** Main thread. */
    private Main mMain;
    
    /** Time elapsed since the was hit. */
    private float mHitTimer;
	
    /** 
     * Initialises a FlyingTurtle object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public FlyingTurtle(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("fly");

    	// Initialise velocity.
    	setVelocity(new Point(0.05f, 0.3f));
    	
    	// Set minimum movement.
    	setMinMove().y = 0;
    	
    	// Set main thread reference.
    	mMain = main;    	
    	
    }	
    
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
   	public void move(float dt, Point cam) {
		
        // Spin off screen.
    	if(getSpinOffScreen()) {
    		moveSpin(dt, cam);
    		return;
    	}	    
        
   		// Gravity.
   		if(!getAnimationState().equals("fly")) {
   			if(getDown() < getMaxMove().y) {
   				moveDown(dt, 0.2f);	
   			}
   		}
    	
        // Move.
        if((getAnimationState().equals("hit") || getAnimationState().equals("kick")) && 
           !getAnimationState().equals("fly")) {

        	// Wait while the enemy dies.
        	if(mHitTimer < 3500) {
        		mHitTimer += dt;
        	} else {
        		
        		if(!getAnimationState().equals("kick")) {
	        		setAnimationState("normal");
	            	setVelocity(new Point(0.05f, 0));
	        		mHitTimer = 0;
        		}
        	}
 	
        }
  
        // Enemy does not move if hit.
        if(getAnimationState().equals("hit"))
        	return;

   		// Move forward.
   		if(getAnimationState().equals("fly")) {

   			if(!getJump()) {
   				if(getDown() < getMaxMove().y)
   					moveDown(dt, 0.05f);	
   				else
   					setJump(true);
   			} else {
   				
   				if(getUp() > getMinMove().y)
   					moveUp(dt, 0.05f);	
   				else
   					setJump(false);
   			}
   			
   			
   		} else {
   			
   	   		if (getDirection() == 'r') {
   	   			if (getRight() < getMaxMove().x)
   	   				moveRight(dt, getVelocity().x);
   	        } else {
   	        	if (getLeft() > getMinMove().x)
   	        		moveLeft(dt, getVelocity().x);
   	        }  			  			
   		}

   	}   
 
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
	public void detectMapCollision(Map map) {

		// Skip if spinning off screen.
		if(getSpinOffScreen())
			return;		
		
		// Left.
        if(!getAnimationState().equals("fly")) {
        	
			if (map.getTile(getLeft(), getUp()).collision == 1 || 
	        	map.getTile(getLeft(), getCenterY()).collision == 1) {
	            
	        	setMinMove().x = getLeft(); 
	        	setDirection('r');
	
	        } else {
	            setMinMove().x = -100;
	        }    
	
	        // Right.
	        if (map.getTile(getRight(), getUp()).collision == 1 ||
	            map.getTile(getRight(), getCenterY()).collision == 1) 
	        {
	            setMaxMove().x = getRight();
	            setDirection('l');
	
	        } else {
	            setMaxMove().x = map.getWidth();
	        }
        } else {
        	
        	// Up.
            if (map.getTile(getRight(), getUp()).collision >= 1 && getJump() ||
            	map.getTile(getLeft(), getUp()).collision >= 1 && getJump()) {

            	setJump(false);
                
            }  	
        
        }
                
        // Down.
        if(getAnimationState().equals("normal") && getDown() == getMaxMove().y) {
        	 
	        if ((map.getTile(getLeft(), getDown()).isGap())) {
	        		setDirection('r');
	        } else if ((map.getTile(getRight(), getDown()).isGap())) {
	        		setDirection('l');
	        }
	        
        } 
    	
    	// Down.
        if ((map.getTile(getRight(), getDown()).collision == 1 || 
             map.getTile(getLeft(), getDown()).collision == 1)) {

        	// Calculate the down position aligned to the grid.
        	int down = getDown() / 16 * 16;          	
        	setMaxMove().y = getDown() / 16 * 16;

           	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));
        	
        }
        else {
        	if(getAnimationState().equals("fly"))
        		setMaxMove().y = map.getHeight();
        	else 
        		setMaxMove().y = map.getHeight() + 100;
        }
       
		
	} 
	
	
    /**
     * Detect collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {

    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return; 
    	
    	// Skip if player is dead.
    	if(player.getAnimationState().equals("die"))
    		return;
    	
    	// Collision between player and enemy.
        if (getBounds().intersects(player.getBounds())) {
          	
        	// Player is invincible.
        	if(player.getInvincible() == 1) {

        		// Set the entity to spin off the screen.
        		spinOffScreen(player.getDirection());
        		
				// Add points.
				mMain.getCurrentPlayer().addScore(200);
        		
        		
        		// Play sound effect.
        		mMain.getSoundEffects().play("hit.wav");
        		
        	} else {
        	
        		if(player.getDown() < getUp() + 10) {
        			        			
        			if (getAnimationState().equals("normal") || getAnimationState().equals("kick") || getAnimationState().equals("fly")) {
        			
        				// Make player bounce.
        				player.bounce();
        				
	        			// Set animation.
	        			setAnimationState("hit");	
	        			
	        			// Play sound effect.
	        			mMain.getSoundEffects().play("hit.wav");
	        			
                    	// Set velocity.
                    	setVelocity(new Point(0, 0));
                    	
                    	// Reset hit timer.
                    	mHitTimer = 0;
        			
        			} else if(getAnimationState().equals("hit")) {
        				
        				// Kick shell.
        				if(mHitTimer > 350)
        					kick(player);
	        			
        			}
	
        		}
        		else {

	        		// Enemy collision.
        			if(getAnimationState().equals("normal"))
        				player.enemyCollision();
        			else if(getAnimationState().equals("kick")) {
        				if(mHitTimer > 500)
        					player.enemyCollision();
        			}
        			else {
        				
        				// Kick shell.
         				if(mHitTimer > 250)
         					kick(player);        				
        			}
        			
        		}
        	}
        }
        
    }
    
    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam) {

		// Skip if spinning off screen.
		if(getSpinOffScreen())
			return;	
    	
    	// Loop through entities.
    	for(IEntity entity : entities) {
    		
			// Skip if enemy is spinning off screen.
			if(entity.getSpinOffScreen() || entity.getClass() == Fireball.class) 
				continue;   	
    		
    		// Check entity is in screen area.
    		if(entity.isInScreenArea(cam, 64)) {
    		   	 
    			// Collision between shell and entity.
    			if(entity != this && getBounds().intersects(entity.getBounds())) {
	
    				if(getAnimationState().equals("kick")) {
    					
    					// Set the entity to hit.
    	        		entity.hit(this);
    	        		
    	        		// Hit another kicked shell.
    					if(entity.getAnimationState().equals("kick")) {
    						
    						// Change direction.
    						turn();
    						
    						// Add points.
    						mMain.getCurrentPlayer().addScore(200);
    						
    						// Spin off screen.
    						spinOffScreen(getDirection());
    					}
    					
    	                
    				} else {
    					
    					// Change direction.
    					if(!entity.getAnimationState().equals("kick"))
    						turn();
    				}
    				
    			}
    		}
    		
    	}          	
    	
    }
    
    /**
     * Initialises the kick motion.
     * @param player Player object.
     */
    public void kick(Player player) {
    	
		// Set animation.
		setAnimationState("kick");	
		
		// Play sound effect.
		mMain.getSoundEffects().play("hit.wav");	
		
        // Kick shell.
        if (player.getCenter().x > getCenter().x) {
            setDirection('l');
            setX(getX() - 5);
        } else {
            setDirection('r');
            setX(getX() + 5);
        }
        
    	// Set velocity.
    	setVelocity(new Point(0.18f, 0.2f));
    	
    	// Set hit timer.
    	mHitTimer = 350;
    	
    }
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	
    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return;
    	
		if (getAnimationState().equals("normal")) {
			
			// Set animation.
			setAnimationState("hit");	
			
			// Play sound effect.
			mMain.getSoundEffects().play("hit.wav");

		} else if(getAnimationState().equals("hit") || getAnimationState().equals("kick")) {
       		
			if(mHitTimer > 350) {

				// Set the entity to spin off the screen.
				spinOffScreen(entity.getDirection());
			
				// Add points.
				mMain.getCurrentPlayer().addScore(200);
				
				// Play sound effect.
				mMain.getSoundEffects().play("hit.wav");
			}

		}
			
    }
    
}