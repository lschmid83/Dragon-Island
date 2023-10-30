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

/**
 * This class is the flag displayed above the castle at the end of a level.
 * @author Lawrence Schmid
 */
public class CastleFlag extends Sprite implements IEntity {
	
    /** 
     * Initialises a CastleFlag object. 
     * @param main Main thread.
     * @param g Graphics context.
     * @param path Sprite sheet path.
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public CastleFlag(Main main, Object g, String path) throws NumberFormatException, IOException {
    	
       	// Initialise parent sprite object.
    	super(g, path);		

    	// Set animation speed.
    	setAnimationSpeed(120);
    	
    	// Set animation state.
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
    	
    	// Raise the flag.
        if(getPosition().y > getMinMove().y && getJump())
        	moveUp(dt, 0.018f);   
        
    }
    
    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam) {
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

    	// Player is walking to castle.
    	if(player.getFlagState() == FlagState.WALK_TO_CASTLE) {
    		
    		// Raise the flag.
        	setJump(true);
	
    	}
    }

    /**
     * Makes a copy of the object.
     * @param x Start x coordinate.
     * @param y Start y coordinate.
     * @param angle Start angle.
     * @return Cloned object.
     */
    @Override
    public Object clone(int x, int y, int angle) throws CloneNotSupportedException {
    	
    	// Clone sprite.
    	CastleFlag clone = (CastleFlag)super.clone(x, y, angle);
    	
    	// Set flag to behind castle.
   		clone.setPosition(new Point(x, y + 16));
    	
    	// Set minimum movement.
    	clone.setMinMove().y = clone.getPosition().y - 15;
    	
    	// Return the cloned object.
    	return clone;	
    	
    }
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity) {
    	// Not implemented.    	
    }

}