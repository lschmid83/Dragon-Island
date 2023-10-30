package com.citex.dragonisland.core.sprite.entity.object;

import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Score;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.Sprite;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.sprite.player.enums.FlagState;
import com.citex.dragonisland.core.sprite.player.enums.PlayerSize;
import com.citex.dragonisland.core.thread.Main;

/**
 * This class is the flag pole at the end of a level.
 * @author Lawrence Schmid
 */
public class FlagPole extends Sprite implements IEntity {

    /** Main thread. */
    private Main mMain;
	
    /** 
     * Initialises a FlagPole object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public FlagPole(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
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
   	public void move(float dt, Point cam) {
   		// Not implemented.
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
 
    	   // Stop player jumping over flag pole.
           if (player.getRight() >= getLeft() && player.getFlagState() == FlagState.NONE) {
               
        	   // Add padding.
        	   int padding = 4;
        	   if(player.getSize() == PlayerSize.SMALL)
        		   padding = 1;
        	   
        	   // Set max movement.
        	   player.setMaxMove().x = getLeft() + 1 + padding;
        	   player.setMaxMove().y = getDown();

        	   // Set position.
               player.setPosition(new Point(getLeft() - player.getFrameWidth() + player.getFrame().bX + padding, player.getY()));
               
               // Set to fall.
               player.setJump(false);
               player.getVelocity().y = -0.05f;
               player.getVelocity().x = 0;
               
               // Set flag state.
               player.setFlagState(FlagState.SLIDE_DOWN_POLE);
             
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
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	// Not implemented.    	
    }

}
