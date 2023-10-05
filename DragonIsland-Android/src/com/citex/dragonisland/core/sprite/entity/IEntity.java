package com.citex.dragonisland.core.sprite.entity;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.drawing.Rectangle;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.player.Player;

/**
 * Interface for the different types of entity.
 * @author Lawrence Schmid
 */
public interface IEntity {

    /**
     * Draws the entity.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param cam Camera coordinates.
     */    
    public void draw(Object g, float dt, Point cam);
    
    /** 
     * Draws the entity at x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param dir Direction of the entity.
     */
    public void draw(Object g, float dt, int x, int y, char dir);
    
    /** 
     * Draws the entity at x,y coordinates.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param dir Direction of the entity.
     */
    public void draw(Object g, float dt, int x, int y, char dir, int angle);
  
	/**
     * Detects collisions between the entity and map tiles.
     * @param map Level map.
     */ 
    public void detectMapCollision(Map map);   
     
    /**
     * Detects collisions between the entity and a player.
     * @param player Player object.
     */    
    public void detectPlayerCollision(Player player);
    
    /**
     * Detects collisions between the entity and other entities.
     * @param entities List of entities.
     */    
    public void detectEntityCollision(ArrayList<IEntity> entities, Point cam);    
        
    /**
   	 * Moves the entity.
   	 * @param dt Delta time between frame updates.
   	 * @param cam Camera coordinates.
   	 */
   	public void move(float dt, Point cam);
    
    /**
     * Animates the entity.
     * @param dt Delta time between frame updates.
     */
    public void animate(float dt);
    
    /**
     * Gets the collision bounding box.
     * @return Rectangle object.
     */ 
    public Rectangle getBounds();  
    
    /**
     * Gets the position of entity.
     * @return Point object.
     */
    public Point getPosition();
    
    /**
     * Should the enemy be removed.
     * @return True if dead; otherwise false.
     */
    public boolean isDead();
    
    /**
     * Determines whether the sprite is within the screen area.
     * @param cam Camera coordinates.
     * @param paddding. Amount of padding to apply to camera coordinates.
     * @return True if the sprite is within the screen area; otherwise false.
     */
    public boolean isInScreenArea(Point cam, int padding);
      
    /**
     * Determines whether the sprite is invulnerable to fire.
     * @return True if the sprite is invulnerable to fire; otherwise false.
     */
    public boolean getFireInvulnerable();   
    
    /**
     * Gets the animation state of the entity. 
     * @return Animation state of the entity.
     */
    public String getAnimationState();
    
    /**
     * Sets the direction.
     * @param direction Direction of the entity.
     */
    public void setDirection(char direction);   
    
    /**
     * Changes the direction.
     */
    public void turn();
    
    /**
     * Sets the angle.
     * @param angle Angle of the entity.
     */
    public void setAngle(int angle);   
    
    /**
     * Sets the position of the entity.
     * @param position Point object.
     */
    public void setPosition(Point position);
    
    /**
     * Gets the direction of the entity. 
     * @return Direction of the entity.
     */
    public char getDirection();
    
    /**
     * Sets the entities hit state.
     * @param entity Object which implements IEntity.
     */
    public void hit(IEntity entity);
    
    /**
     * Makes the sprite spin off the screen.
     * @param dir Spin direction.
     */
    public void spinOffScreen(char dir);
    
    /**
     * Is the sprite spinning off the screen.
     * @return True if the sprite is spinning off screen; otherwise false.
     */
    public boolean getSpinOffScreen();
        
    /**
     * Makes a copy of entity.
     * @param x Start x coordinate.
     * @param y Start y coordinate.
     * @return Cloned object.
     */
    public Object clone(int x, int y, int angle) throws CloneNotSupportedException;
    
    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl);
	
}
