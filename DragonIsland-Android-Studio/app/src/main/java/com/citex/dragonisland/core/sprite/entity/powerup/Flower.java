package com.citex.dragonisland.core.sprite.entity.powerup;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Score;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.sprite.player.enums.PlayerSize;
import com.citex.dragonisland.core.thread.Main;

/**
 * Flower.java
 * This class is a fire flower power-up which gives the player the ability to throw fire balls.
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

public class Flower extends Sprite implements IEntity {

    /** Main thread. */
    private Main mMain;
	
    /** 
     * Initialises a Flower object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Flower(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
    	// Set fire invulnerable.
    	setFireInvulnerable(true);
    	
    	// Set main thread reference.
    	mMain = main;    	
    	
    }	
	
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
  	public void move(float dt, Point cam) {
  		//Not implemented
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
    	
    	 // Collision between entity and player.
        if (getBounds().intersects(player.getBounds())) {

        	// Play sound effect.
        	mMain.getSoundEffects().play("powerup.wav");
            
        	// Add points to the score.
        	player.addScore(1000);
        	
        	// Draw the points.
            mMain.getGame().addPoints(new Score((int)getLeft() + 16, (int)getUp() - 5, 1000));

            // Set player size to large.
            player.setSize(PlayerSize.FIRE);
            
            // Remove entity.
            kill();            
            
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