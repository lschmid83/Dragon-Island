package com.citex.dragonisland.core.sprite.entity.object;

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
 * Vine.java
 * This class is a vine which the player can use to climb.
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

public class Vine extends Sprite implements IEntity {
	
    /** 
     * Initialises a Vine object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Vine(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
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
     * Draws the power-up.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera coordinates.
     */    
    public void draw(Object g, float dt, Point cam) {

        // Draw growing vine. 
        if (getStartPosition().y - getDown() + 16 > 5) {
        	draw(g, dt, 0, getX() - cam.x, getDown() - cam.y, 16, getStartPosition().y - getDown() + 16);
        }
        
    	// Draw the sprite.
        super.draw(g, dt, 'r', cam);

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

		// Add a jump force.
	    float jumpForce = 0.002f * dt;   
	    if (getUp() > getMinMove().y) {
	    	velocity.y = jumpForce;
	    } 
	    else
	    	velocity.y = 0;

	    // Multiply velocity by delta time.
	    Point stepVelocity  = new Point(velocity.x * dt, velocity.y * dt);	

	    // Set the position.
    	setPosition(new Point((getPosition().x / 16) * 16, getPosition().y - stepVelocity.y));
    	
  	} 	
  	
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
    public void detectMapCollision(Map map) {

    	// Down.
        if ((map.getTile(getRight(), getDown()).collision == 1 || 
             map.getTile(getLeft(), getDown()).collision == 1)) {
        	
        	// Set min / max coordinates.
	     	setMinMove().y = -16;
        }
    	
    }
        
    /**
     * Gets the collision bounding box.
     * @return Rectangle object.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX() + 6, getDown() + 4, 1, getStartPosition().y - getDown() + 25);
    }
    
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {
    	
    	// Collision between entity and player.
        if (getBounds().intersects(player.getBounds())) {

        	// Set player to climb.
        	if (player.getControls().up) { 
        		player.setClimb(true);
        		player.setPosition(new Point(getCenterX() - player.getFrameWidth() / 2, player.getY()));
        		
            }  	

        } 
        else {
        	player.setClimb(false);
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