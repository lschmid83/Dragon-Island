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
 * This class a robot enemy.
 * @author Lawrence Schmid
 */
public class Robot extends Sprite implements IEntity {

    /** Main thread. */
    private Main mMain;
    
    /** Time elapsed since the was hit. */
    private float mHitTimer;
	
    /** 
     * Initialises a Robot object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Robot(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
    	// Initialise velocity.
    	setVelocity(new Point(0.06f, 0.25f));
    	
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
		if(getDown() < getMaxMove().y)
	    	moveDown(dt, getVelocity().y);	
   		
        // Move.
        if(getAnimationState().equals("hit")) {
        	
        	// Wait while the enemy dies.
        	if(mHitTimer < 1000) {
        		mHitTimer += dt;
        	} else {
        		setAnimationState("normal");
        		mHitTimer = 0;
        	}
        	return;
        }  		
   		   		
   		// Move forward.
   		if (getDirection() == 'r') {
   			if (getRight() < getMaxMove().x)
   				moveRight(dt, getVelocity().x);
        } else {
        	if (getLeft() > getMinMove().x)
        		moveLeft(dt, getVelocity().x);
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
                
        // Gap.
        if(getDown() == getMaxMove().y) {
	        if ((map.getTile(getLeft(), getDown()).isGap())) {
	        	setDirection('r');
	        }
	        else if ((map.getTile(getRight(), getDown()).isGap())) {
	        	setDirection('l');
	        }   
        }
        
    	// Down.
        if ((map.getTile(getRight(), getDown()).collision == 1 || 
             map.getTile(getLeft(), getDown()).collision == 1)) {
        	
        	// Calculate the down position aligned to the grid.
        	int down = getDown() / 16 * 16;          	
        	setMaxMove().y = down;

           	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));
       	
        }
        else {
        	setMaxMove().y = map.getHeight() + 100;
        }

	} 
	
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {

    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return; 
    	
    	// Skip if player is dead.
    	if(player.getAnimationState().equals("die"))
    		return;
    	
    	// Skip if the enemy is hit.
    	if(getAnimationState().equals("hit"))
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
        			
        			// Set animation.
        			setAnimationState("hit");	
        			
        			// Play sound effect.
        			mMain.getSoundEffects().play("hit.wav");
        			
        		}
        		else {

	        		// Enemy collision.
	       			player.enemyCollision();
        			
        		}
        	}
        }      	
    }
    
    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam) {
    	
    	// Skip if enemy is dead.
    	if(getAnimationState().equals("hit"))
    		return;
    	
		// Skip if spinning off screen.
		if(getSpinOffScreen())
			return;	
    	
    	// Loop through entities.
    	for(IEntity entity : entities) {
    		
			// Skip if enemy is spinning off screen.
			if(entity.getSpinOffScreen() || entity.getAnimationState().equals("kick") || entity.getClass() == Fireball.class) 
				continue; 
			
    		// Check entity is in screen area.
    		if(entity.isInScreenArea(cam, 64)) {
    		   	 
    			// Collision between entities.
    			if(entity != this && getBounds().intersects(entity.getBounds())) {

    				// Change direction.
    				turn();
    			}
    		}
    		
    	}  
        
    }
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	
    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return; 
    	
    	// Skip if the enemy is hit.
    	if(getAnimationState().equals("hit"))
    		return;
    	
    	// Set animation.
		setAnimationState("hit");	
		
		// Play sound effect.
		mMain.getSoundEffects().play("hit.wav");	
    	
    }

}