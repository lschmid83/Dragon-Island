package com.citex.dragonisland.core.sprite.entity.object;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.sprite.player.enums.FlagState;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.Numbers;

/**
 * Flag.java
 * This class is the flag at the end of a level.
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

public class Flag extends Sprite implements IEntity {
    
    /** Has the flag fallen */
    private boolean mFall;
	
    /** 
     * Initialises a Flag object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Flag(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");

    	// Set fire invulnerable.
    	setFireInvulnerable(true);
    	
    }	
        
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
  	public void move(float dt, Point cam)
  	{
		// Get the velocity.
		Point velocity = getVelocity();			
		
		// Initialise gravity force.
		float gravity = -0.010f * dt;
		if(getDown() < getMaxMove().y)
	    	velocity.y += gravity;
	    else
	    	velocity.y = 0;

		// Add a dampening force to simulate friction.
	    velocity.y *= 0.90f;

	    // Apply clamping to limit maximum movement speed.
	    velocity.y = Numbers.clamp(velocity.y, -0.010f * dt, 0.10f);

	    // Multiply velocity by delta time.
	    Point stepVelocity  = new Point(velocity.x * dt, velocity.y * dt);	

	    // Set the position.
    	setPosition(new Point(getPosition().x, getPosition().y - stepVelocity.y));

  	}

	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
	public void detectMapCollision(Map map) {
		// Not implemented.
	} 
		
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {

    	// Player is on flag pole.
    	if(player.getFlagState() == FlagState.SLIDE_DOWN_POLE) {
    		
    		// Set flag to fall.
    		if(!mFall) {
    			setMaxMove().y = getDown() + 122;
    			mFall = true;
    		}
    		
    		// Flag has reached ground.
    		if(getDown() >= getMaxMove().y && player.getDown() >= player.getMaxMove().y && 
    		   player.getFlagState() == FlagState.SLIDE_DOWN_POLE) {
    			
    			// Player should jump off flag pole.
    			player.setFlagState(FlagState.JUMP_OFF_POLE);
    			
    			// Set jump height.
    			player.setMinMove().y = player.getDown() - 30;
    			
    			// Set max movement.
    			player.setMaxMove().x = getLeft() + 90;
    			
    			// Set player to jump right.
    			player.getControls().right = true;
    			player.getControls().jump = true;
    			player.setJump(true);
    		
    		}
    		
    	}
    	
    }
    
    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam) {
		// Not implemented.	
    }
    
    /**
     * Makes a field-for-field copy of instances of that class.
     * @return Cloned object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
		setMaxMove().y = getDown();
    	return super.clone();	
    }
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	// Not implemented.    	
    }
    
}
