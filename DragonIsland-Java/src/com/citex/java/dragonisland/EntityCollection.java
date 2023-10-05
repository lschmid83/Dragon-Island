package com.citex.java.dragonisland;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
    This class draws a collection of entities defined in the level map,
    with no collision detection.
 
    @version 1.0
    @modified 20/04/2012
    @author Lawrence Schmid<BR><BR>
    
    This file is part of Dragon Island.<BR><BR>
 
    Dragon Island is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.<BR><BR>

    Dragon Island is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.<BR><BR>

    You should have received a copy of the GNU General Public License
    along with Dragon Island. If not, see http://www.gnu.org/licenses/.<BR><BR>
    
    Copyright 2012 Lawrence Schmid
*/

public class EntityCollection {

	/** The list of entity objects */
    private ArrayList<Sprite> mEntities;
    /** The map tile */
    private Tile mTile;

    /**
     * Constructs the EntityCollection
     */
    public EntityCollection() {
        mEntities = new ArrayList<Sprite>();
        for (int i = 0; i < 50; i++) {
            Sprite s = new Sprite(i, 'l', 0, 0);
            s.loadSpriteSheet("res/spr/" + i); 
            if (s.getFrame('l') == null) {
                s = null;
            } else {
                if (s.getType() == 34) {
                    s.setAnimationState("fly");
                } else {
                    s.setAnimationState("normal");
                }
            }
            mEntities.add(s);
        }
    }

    /**
     * Gets the number of elements in the entity collection
     * @return The size of the entity collection
     */
    public int getSize() {
        return mEntities.size();
    }

    /**
     * Gets a buffered image containing the first frame from the entity sprite
     * @param index The index of the entity in the sprite list
     * @param direction The direction 'l' = left 'r' = right
     * @return A BufferedImage containing the first frame from the entity sprite sheet
     */
    public BufferedImage getEntity(int index, char direction) {
        return mEntities.get(index).getFrame(direction);
    }

    /**
     * Draws entities which have been defined in the map on the graphics surface using the camera coordinates
     * @param g The graphics context
     * @param map The level map
     * @param cam The camera coordinates
     */
    public void draw(Graphics g, Map map, Point cam) {

        for (int i = 1; i < mEntities.size(); i++) {
            if (mEntities.get(i) != null) {
                mEntities.get(i).advanceFrame();
            }
        }

        //draw objects in the screen area
        for (int x = cam.x / 16 - 10; x < (cam.x + 512) / 16; x++) {
            for (int y = cam.y / 16; y < (cam.y + 300) / 16; y++) {
                mTile = map.getTile(x, y);
                if (Map.isEnlargeBlock(mTile.tile1, mTile.tile2)) {
                    mEntities.get(10).drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
                } else if (Map.isFireBlock(mTile.tile1, mTile.tile2)) {
                    mEntities.get(11).drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
                } else if (Map.isInvincibleBlock(mTile.tile1, mTile.tile2)) {
                    mEntities.get(12).drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
                } else if (Map.isExtraLifeBlock(mTile.tile1, mTile.tile2)) {
                    mEntities.get(13).drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
                } else if (Map.isVineBlock(mTile.tile1, mTile.tile2)) {
                    mEntities.get(14).drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
                } else {
                    if (mTile.spriteTile != 0 && mTile.spriteDraw == 1) //sprite
                    {
                        mEntities.get(mTile.spriteTile).drawXY(g, cam, 'l', x * 16, y * 16);
                    }
                }
            }
        }
    }
}
