package com.citex.dragonisland.core.sprite.entity.powerup;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Score;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.Numbers;

/**
 * Star.java
 * This class is a star power-up which makes the player invincible.
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

public class Star extends Sprite implements IEntity {

    /** Main thread. */
    private Main mMain;
	
    /** 
     * Initialises a Star object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public Star(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
    	// Set rotating.
    	setRotate(true, 0.5f);

    	// Initialise velocity.
    	setVelocity(new Point(0.08f, 0.08f));
    	
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
  	public void move(float dt, Point cam)
  	{
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
        if ((map.getTile(getRight(), getDown() - 2).collision == 1 || 
             map.getTile(getLeft(), getDown() - 2).collision == 1)) {
        	
        	// Calculate the down position aligned to the grid.
        	int down = getDown() / 16 * 16;          	
        	setMaxMove().y = down;

        	// Set jump height.
	     	setMinMove().y = down - 50;
	     	setMaxMove().y = down;

           	// Set position.
       		setPosition(new Point(getPosition().x, down - (getFrame().bY + getFrame().bH)));
        	
        	// Set jumping to false.
        	setJump(true);
       	
        }
        else {
        	setMaxMove().y =  map.getHeight() + 100;
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

        // Left.
        if (map.getTile(getLeft(), getUp()).collision == 1 || 
        	map.getTile(getLeft(), getCenterY()).collision == 1) {
            setMinMove().x = getLeft();
            setDirection('r');
        } else {
            setMinMove().x = -100;
        }
    	
    }
      	
    /**
     * Draws the sprite at the x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera position.
     */ 
  	@Override
    public void draw(Object g, float dt, Point cam) {
    	this.draw(g, 'l', dt, cam);
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

            // Set player state to invincible.
            player.setInvincible(1);
            
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