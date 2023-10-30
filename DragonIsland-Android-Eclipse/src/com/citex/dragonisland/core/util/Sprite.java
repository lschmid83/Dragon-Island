package com.citex.dragonisland.core.util;

import java.io.IOException;

import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.entity.enemy.Beetle;
import com.citex.dragonisland.core.sprite.entity.enemy.FallingBlock;
import com.citex.dragonisland.core.sprite.entity.enemy.JumpingLava;
import com.citex.dragonisland.core.sprite.entity.enemy.Turtle;
import com.citex.dragonisland.core.sprite.entity.enemy.FlyingTurtle;
import com.citex.dragonisland.core.sprite.entity.enemy.PiranhaPlant;
import com.citex.dragonisland.core.sprite.entity.enemy.SpinyBeetle;
import com.citex.dragonisland.core.sprite.entity.enemy.Gremlin;
import com.citex.dragonisland.core.sprite.entity.enemy.Robot;
import com.citex.dragonisland.core.sprite.entity.object.CastleFlag;
import com.citex.dragonisland.core.sprite.entity.object.Fireball;
import com.citex.dragonisland.core.sprite.entity.object.Flag;
import com.citex.dragonisland.core.sprite.entity.object.FlagPole;
import com.citex.dragonisland.core.sprite.entity.object.Vine;
import com.citex.dragonisland.core.sprite.entity.powerup.ExtraLife;
import com.citex.dragonisland.core.sprite.entity.powerup.Flower;
import com.citex.dragonisland.core.sprite.entity.powerup.Mushroom;
import com.citex.dragonisland.core.sprite.entity.powerup.Star;
import com.citex.dragonisland.core.thread.Main;

/**
 * Helper functions for sprites.
 * @author Lawrence Schmid
 */
public class Sprite {
	
	/**
	 * Gets an entity object from the index.
	 * @param g Graphics context.
	 * @param path Path to the sprite sheet.
	 * @param index Index of the entity.
	 * @return Object which implements IEntity.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static IEntity getEntity(Main main, Object g, String path, int index) throws NumberFormatException, IOException {
		
		// Initialise the entity.
		IEntity entity = null;
		path = path + index;
		switch(index) {
			case 0: entity = new Fireball(main, g, path); break;
			case 1: entity = new CastleFlag(main, g, path); break;
			case 2: entity = new Flag(main, g, path); break;
			case 3: entity = new FlagPole(main, g, path); break;
			case 4: entity = new Mushroom(main, g, path); break;
			case 5: entity = new Flower(main, g, path); break;
			case 6: entity = new Star(main, g, path); break;
			case 7: entity = new ExtraLife(main, g, path); break;
			case 8: entity = new Vine(main, g, path); break;
			case 9: entity = new PiranhaPlant(main, g, path); break;
			case 10: entity = new SpinyBeetle(main, g, path); break;
			case 11: entity = new Gremlin(main, g, path); break;
			case 12: entity = new Robot(main, g, path); break;
			case 13: entity = new Beetle(main, g, path); break;
			case 14: entity = new Turtle(main, g, path); break;
			case 15: entity = new FlyingTurtle(main, g, path); break;
			case 16: entity = new FallingBlock(main, g, path); break;
			case 17: entity = new JumpingLava(main, g, path); break;
		}
				
		return entity;
	}
	
}
