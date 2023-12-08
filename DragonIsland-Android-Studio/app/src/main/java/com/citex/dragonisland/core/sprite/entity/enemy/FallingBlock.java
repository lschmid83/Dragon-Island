package com.citex.dragonisland.core.sprite.entity.enemy;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.drawing.Rectangle;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * FallingBlock.java
 * This class is a falling block enemy.
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

public class FallingBlock extends Sprite implements IEntity {
	
    /** 
     * Initialises a FallingBlock object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public FallingBlock(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");

    	// Initialise velocity.
    	setVelocity(new Point(0.05f, 0.2f));
    	
    	// Set fire invulnerable.
    	setFireInvulnerable(true);

    }	
    
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
   	public void move(float dt, Point cam) {
		
   		// Move forward.
   		if(getAnimationState().equals("fall")) {

   			if(!getJump()) {
   				if(getDown() < getMaxMove().y)
   					moveDown(dt, 0.3f);	
   				else
   					setJump(true);
   			} else {
   				
   				if(getUp() > getMinMove().y)
   					moveUp(dt, 0.1f);	
   				else
   					setJump(false);
   			}
   			
   		}
   	}   
 
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
	public void detectMapCollision(Map map) {

    	// Up.
        if ((map.getTile((int)getCenter().x, getUp()).collision >= 1 && getJump()) || getUp() < 1) {

        	// Stop block falling.
        	setAnimationState("normal");
        	setJump(false);
            
        }  	
                	
    	// Down.
        if ((map.getTile(getRight(), getDown()).collision == 1 || 
             map.getTile(getLeft(), getDown()).collision == 1)) {

        	// Calculate the down position.
        	int down = getDown() / 16 * 16;          	
        	setMaxMove().y = getDown() / 16 * 16;

           	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));
        	
        }
        else {
        	setMaxMove().y = map.getHeight();
        }
    
	} 
	
	
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {

    	// Skip if player is dead.
    	if(player.getAnimationState().equals("die"))
    		return;
    	
       	// Check if the player is near the falling block.
    	Rectangle rect = new Rectangle(player.getLeft() - 10, getUp(), player.getWidth() + 20, player.getDown() - getUp());
    	if(getBounds().intersects(rect))
    		setAnimationState("fall");
    	
    	// Collision between player and enemy.
        if (getBounds().intersects(player.getBounds())) {

        	// Enemy collision.
			player.enemyCollision();

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
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	// Not implemented.    	
    }
    
}