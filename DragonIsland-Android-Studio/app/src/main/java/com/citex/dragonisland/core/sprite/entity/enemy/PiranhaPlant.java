package com.citex.dragonisland.core.sprite.entity.enemy;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.drawing.Rectangle;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.entity.object.Fireball;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * PiranhaPlant.java
 * This class is a piranha plant enemy.
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

public class PiranhaPlant extends Sprite implements IEntity {
    
    /** Main thread. */
    private Main mMain;
	
    /** Time elapsed while plant is visible. */
    private float mMoveTimer;
    
    /** Is the player near the plant. */
    private boolean mPlayerNear;
	
    /** 
     * Initialises a PiranhaPlant object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public PiranhaPlant(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(90);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
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
                
        // Animate.
        if(getPosition().y > getMinMove().y && getJump()) {
        	
        	// Move up.
        	moveUp(dt, 0.018f);   
        	
        } else if(getPosition().y < getMinMove().y && getJump()) {
        	
        	// Normal position.
        	if(mMoveTimer < 1800)
        		mMoveTimer+= dt;        		
        	else {
        		mMoveTimer = 0;
        		setJump(false);
        	}
        } else if(getPosition().y < getMaxMove().y && !getJump()) {
        	
        	// Move down.
        	moveDown(dt, 0.018f);  
        	
        } else if(getPosition().y >= getMaxMove().y) {
        	
        	// Down position.
        	if(!mPlayerNear) {
	        	if(mMoveTimer < 1500)
	        		mMoveTimer+= dt;        		
	        	else {
	        		mMoveTimer = 0;
	        		setJump(true);
	        	}
        	}
        }
        
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

    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return; 
    	
    	// Skip if player is dead.
    	if(player.getAnimationState().equals("die"))
    		return;
    	
    	// Check if the player is near the plant.
    	Rectangle rect = new Rectangle(player.getLeft() - 10, player.getUp(), player.getWidth() + 20, player.getHeight() + 5);
    	if(getBounds().intersects(rect))
    		mPlayerNear = true;
    	else
    		mPlayerNear = false;

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
        		
        		// Enemy collision.
        		if(getAngle() == 0) {
        			if(getMaxMove().y - getPosition().y > 12)
        				player.enemyCollision();
        		} else if(getAngle() == 180.0f) {
        			if(getPosition().y - getMinMove().y > 12)
        				player.enemyCollision();
        		}
        	}
        	
        }
    }
 
    /**
     * Makes a field-for-field copy of instances of that class.
     * @param x Start x coordinate.
     * @param y Start y coordinate.
     * @param angle Start angle.
     * @return Cloned object.
     */
    @Override
    public Object clone(int x, int y, int angle) throws CloneNotSupportedException {
    	
    	PiranhaPlant clone = (PiranhaPlant)super.clone(x, y, angle);
    	
    	// Set piranha plant to behind warp pipe.
    	if(angle == 0)
    		clone.setPosition(new Point(x, y + 32));
    	else if(angle == 180)
    		clone.setPosition(new Point(x, y));
    	
    	// Set min and max amount of movement in y axis.
    	clone.setMinMove().y = clone.getPosition().y - 31;
		clone.setMaxMove().y = clone.getPosition().y;
    	clone.setJump(true);

    	return clone;	
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

    	// Skip if enemy is spinning off screen.
    	if(getSpinOffScreen())
    		return; 
    	
    	if(entity.getClass() == Fireball.class) {
     	
			// Set the entity to spin off the screen.
			spinOffScreen(entity.getDirection());
			
			// Add points.
			mMain.getCurrentPlayer().addScore(200);
			
			// Play sound effect.
			mMain.getSoundEffects().play("hit.wav");	
			
    	}
    	
    }

}