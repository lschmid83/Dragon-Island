package com.citex.dragonisland.core.sprite.entity.enemy;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.thread.Main;

/**
 * This class a jumping lava enemy.
 * @author Lawrence Schmid
 */
public class JumpingLava extends Sprite implements IEntity {
    
    /** Time elapsed since lava starts jump. */
    private float mTimer;
    
    /** Random start time. */
    private float mStartTime;
    
    /** Index of the enemy. */
    private static int mIndex;
	
    /** 
     * Initialises a JumpingLava object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public JumpingLava(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise the parent sprite object.
    	super(g, path);		

    	// Set the animation speed.
    	setAnimationSpeed(120);
    	
    	// Set the animation state.
    	setAnimationState("normal");
    	
    	// Set fire invulnerable.
    	setFireInvulnerable(true);
    	
    	// Set rotating.
    	setRotate(true, 1);
 	
    }	
 
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
   	public void move(float dt, Point cam) {
   		
   		if(mTimer > mStartTime) {
  
			if(getJump()) {
				
				// Jumping.
				if(getUp() > getMinMove().y)
					moveUp(dt, 0.12f);	
				else
					setJump(false);

			} else {
				
				// Falling.
				if(getDown() < getMaxMove().y)
					moveDown(dt, 0.12f);	
				else
					setJump(true);
				
			}
   		}
   		mTimer += dt;

   	}   
 
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
	public void detectMapCollision(Map map) {

		// Set max move.
		setMaxMove().y = map.getHeight() + 16;
 	} 
	
	
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player) {
    	
    	// Skip if player is dead.
    	if(player.getAnimationState().equals("die"))
    		return;
    	
    	// Collision between player and enemy.
        if (getBounds().intersects(player.getBounds())) {

        	// Enemy collision.
			player.enemyCollision();

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
    	
    	// Clone the object.
    	JumpingLava clone = (JumpingLava)super.clone(x, y, angle);
    	
    	// Set minimum movement.
    	clone.setMinMove().y = clone.getPosition().y - 96;
    	
    	// Start jump.
    	clone.setJump(true);
    	
    	// Set a start time.
    	if(mIndex % 2 == 0) 
    		mStartTime = 750;
    	else
    		mStartTime = 0;
    	
    	// Increase the enemy index count.
    	mIndex++;
    	
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
    	// Not implemented.
    }
    
}