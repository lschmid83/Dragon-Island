package com.citex.dragonisland.core.sprite.entity.object;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.entity.enemy.Robot;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * Fireball.java
 * This class is a fire ball which can kill other entities.
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

public class Fireball extends Sprite implements IEntity {

	/** Minimum amount of movement in y axis. */
	private float mMinMove;
	
    /** 
     * Initialises a FireBall object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Fireball(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
    	// Set rotating.
    	setRotate(true, 0.05f);

    	// Initialise velocity.
    	setVelocity(new Point(0.18f, 0.18f));

    	// Set fire invulnerable.
    	setFireInvulnerable(true); 
    	
    	mMinMove = 0;
    	
    }
    
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
  	public void move(float dt, Point cam) {

        // Kill the fireball if it has left the screen area.
        if(!isInScreenArea(cam, 64))
        	kill();
        
        // Move the player.
        if(getAnimationState().equals("die")) {

        	// Set the animation speed.
        	setAnimationSpeed(20);
        	
        	// Kill the fireball when the animation completes.
        	if(getFrameIndex() == getFrame().end)
        		kill();
        	
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
        
  		// Jump.
  		if (getJump()) {
            if (getUp() > getMinMove().y)
                moveUp(dt, getVelocity().y);
            else
            	setJump(false);
        } else {
            if (getDown() < getMaxMove().y)
                moveDown(dt, getVelocity().y);
        }
	
  	} 	
  	
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
    public void detectMapCollision(Map map) {

    	// Down.
        if (map.getTile(getRight(), getDown()).collision == 1 || 
            map.getTile(getLeft(), getDown()).collision == 1) {
        	
        	// Calculate the down position aligned to the grid.
        	int down = getDown() / 16 * 16;        	

        	// Set jump height.
	     	setMinMove().y = down - 27;
	     	setMaxMove().y = down;

	     	if(getMinMove().y > mMinMove) {
	     		
	     		// Set minimum move in y axis.
	     		mMinMove = getMinMove().y;
	     	} else {
	     		
	     		// Stop fire ball moving any higher.
	     		if(getMinMove().y < mMinMove)
	     			setAnimationState("die");
	     	}
	     	
        	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));

        	// Set jumping.
        	setJump(true);
       	
        }
        else {
        	setMaxMove().y = map.getHeight() + 100;
        }

        // Left.
        if (map.getTile(getLeft(), getUp()).collision == 1 || 
        	map.getTile(getLeft(), getCenterY()).collision == 1) {
            
        	setMinMove().x = getLeft(); 
        	setAnimationState("die");

        } else {
            setMinMove().x = -100;
        }    

        // Right.
        if (map.getTile(getRight(), getUp()).collision == 1 ||
            map.getTile(getRight(), getCenterY()).collision == 1) 
        {
            setMaxMove().x = getRight();
            setAnimationState("die");

        } else {
            setMaxMove().x = map.getWidth();
        }

    }

    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {
    	// Not implemented.
    }

    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam) {
    	
    	// Loop through entities.
    	for(IEntity entity : entities) {
    		
    		// Check entity is in screen area.
    		if(entity.isInScreenArea(cam, 64)) {
    		   	 
    			// Collision between fireball and entity.
    			if(entity != this && getBounds().intersects(entity.getBounds())) {
    				
	        		// Kill the fireball animation.
    				if(entity.getClass() == Robot.class) {

    					if(!entity.getAnimationState().equals("hit"))
    						setAnimationState("die");
    				
    				} else {
    					
    					if(!entity.getAnimationState().equals("die"))
    						setAnimationState("die");
    				}
    				
    				if(!entity.getFireInvulnerable() && !entity.getSpinOffScreen()) {

    					// Set the entity to spin off the screen.
    	        		entity.hit(this);	
    	                
    				} 
    				
    			}
    		}
    		
    	}

    } 
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	// Not implemented.    	
    }

}