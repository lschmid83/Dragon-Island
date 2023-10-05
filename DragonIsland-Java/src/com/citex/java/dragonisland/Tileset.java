package com.citex.java.dragonisland;

import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

/**
	This class loads the block, terrain and scenery tilesets and draws the tiles
    from the level map on the graphics surface.
	
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

public class Tileset {
	
	/** The sprite sheet containing block tile image data */
    private Image mBlockTileset;
    /** The buffered block tiles from the sprite sheet */
    private BufferedImage[] mBlockTile;
    /** The sprite sheet containing terrain tile image data */
    private Image mTerrainTileset;
    /** The buffered terrain tiles from the sprite sheet */
    private BufferedImage[] mTerrainTile;
    /** The sprite sheet containing scenery tile image data */
    private Image mSceneryTileset;
    /** The buffered scenery tiles from the sprite sheet */
    private BufferedImage[] mSceneryTile;
    /** The folder number of the block, terrain and scenery tilesets  */
    private int mTilesetIndex[] = new int[3];
    /** The time elapsed between changing animation frame */
    private int mFrameTimer;
    /** The frame index for animated tiles */
    private int mFrame;
    /** The tile information from the level map */
    private Tile mTileInfo; 
    /** The list of string definitions for the tiles */
    private ArrayList<String> mTileDefintions;
    /** Stores information about smashed blocks */
    private ArrayList<BlockExplosion> mExplosion = new ArrayList<BlockExplosion>();

    /**
     * Constructs the Tileset
     * @param tileset1 The folder number of the block tile set
     * @param tileset2 The folder number of the terrain tile set
     * @param tileset3 The folder number of the scenery tile set
     */    
    public Tileset(int tileset1, int tileset2, int tileset3) {

        mTilesetIndex[0] = tileset1;
        mTilesetIndex[1] = tileset2;
        mTilesetIndex[2] = tileset3;
        initDefinitions();

        //tileset 1 (block)
        mBlockTileset = new Image("res/obj/block/" + mTilesetIndex[0] + "/tileset.bmp");
        mBlockTile = new BufferedImage[66];
        for (int i = 0; i < 4; i++) {
            int w = Integer.parseInt(getTileDescription(i, 0)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 0)[4]) * 16;
            int x = Integer.parseInt(getTileDescription(i, 0)[6]);
            int y = Integer.parseInt(getTileDescription(i, 0)[7]);
            mBlockTile[i] = mBlockTileset.getSubImage(x, y, w, h);
        }
        
        //smashed block
        mBlockTile[4] = mBlockTileset.getSubImage(0, 112, 16, 64); 
        
        //coin
        mBlockTile[10] = mBlockTileset.getSubImage(16, 112, 16, 64); 
        
        //brick
        mBlockTile[20] = mBlockTileset.getSubImage(32, 112, 16, 64); 
        for (int i = 21; i < 28; i++)
            mBlockTile[i] = mBlockTile[20];
        
        //? block
        mBlockTile[30] = mBlockTileset.getSubImage(48, 112, 16, 64); 
        for (int i = 31; i < 38; i++)
            mBlockTile[i] = mBlockTile[30];
        
        //invisible block
        mBlockTile[40] = mBlockTileset.getSubImage(32,96, 16, 16); 
        mBlockTile[41] = mBlockTileset.getSubImage(32,96, 16, 16);
        
        //warp pipes
        for (int i = 50; i < 66; i++) {
            int w = Integer.parseInt(getTileDescription(i, 0)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 0)[4]) * 16;
            int x = Integer.parseInt(getTileDescription(i, 0)[6]);
            int y = Integer.parseInt(getTileDescription(i, 0)[7]);
            mBlockTile[i] = mBlockTileset.getSubImage(x, y, w, h);
        }

        //tileset 2 (terrain)
        mTerrainTileset = new Image("res/obj/terrain/" + mTilesetIndex[1] + "/tileset.bmp");
        mTerrainTile = new BufferedImage[14];
        for (int i = 0; i < 14; i++) {
            try //loading tile
            {
            	if(i == 2)
            	{
            		mTerrainTile[i] = mTerrainTileset.getSubImage(0, 16, 16, 64); 	
            	}
            	else if(i == 4)
            	{
            		mTerrainTile[i] = mTerrainTileset.getSubImage(16, 16, 16, 64); 	      		
            	}
            	else if(i == 5)
            	{
            		mTerrainTile[i] = mTerrainTileset.getSubImage(32, 16, 16, 64); 	      		
            	}
            	else if(i == 6)
            	{
            		mTerrainTile[i] = mTerrainTileset.getSubImage(48, 16, 16, 64); 	      		
            	}
            	else if(i == 8)
            	{
            		mTerrainTile[i] = mTerrainTileset.getSubImage(64, 16, 16, 64); 	      		
            	}
            	else
            	{
	                int w = Integer.parseInt(getTileDescription(i, 16)[3]) * 16;
	                int h = Integer.parseInt(getTileDescription(i, 16)[4]) * 16;
	                int x = Integer.parseInt(getTileDescription(i, 16)[6]);
	                int y = Integer.parseInt(getTileDescription(i, 16)[7]);
	                // System.out.println("index="+i + " x=" + x + " y=" + y + " w=" + w + " h=" + h);
	                if(!getTileDescription(i, 16)[2].equals("Null"))
	                	mTerrainTile[i] = mTerrainTileset.getSubImage(x, y, w, h);
            	}

            } catch (Exception e) {
                Image img = new Image("res/obj/unknown.bmp");
                mBlockTile[i] = img.getImage();
                e.printStackTrace();
            }
        }

        //tileset 3 (scenery)        
        mSceneryTileset = new Image("res/obj/scenery/" + mTilesetIndex[2] + "/tileset.bmp");
        mSceneryTile = new BufferedImage[6];
        for (int i = 0; i < 6; i++) {
            try //loading tile
            {
                int w = Integer.parseInt(getTileDescription(i, 32)[3]) * 16;
                int h = Integer.parseInt(getTileDescription(i, 32)[4]) * 16;
                int x = Integer.parseInt(getTileDescription(i, 32)[6]);
                int y = Integer.parseInt(getTileDescription(i, 32)[7]);
                mSceneryTile[i] = mSceneryTileset.getSubImage(x, y, w, h);

            } catch (Exception e) {
                Image img = new Image("res/obj/unknown.bmp");
                mBlockTile[i] = img.getImage();
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a tile from the specified tileset using the animation frame set in drawMap()
     * @param tile The index of the tile
     * @param tileset The index of the tileset 0-block, 16-terrain, 32-scenery
     * @param tiled The tile index for repeating terrain
     * @return The tile image data
     */
    public BufferedImage getFrame(int tile, int tileset, int tiled) {
        //brick, block or coin
        if (Map.isAnimated(tile, tileset)) {
            return mBlockTile[tile].getSubimage(0, mFrame * 16, 16, 16);
        }
        if (tileset == 0) {
            return mBlockTile[tile];
        } else if (tileset == 16)  //terrain
        {
        	if(tile == 2 || tile == 4 || tile == 5 || tile == 6 || tile == 8)
        		return mTerrainTile[tile].getSubimage(0, tiled * 16, 16, 16);
        	else
        		return mTerrainTile[tile];
        } else if (tileset == 32)  //scenery
        {
            return mSceneryTile[tile];
        } else {
            return null;
        }
    }

    /**
     * Returns a tile from the specified tileset
     * @param tileIndex The index of the tile
     * @param tilesetIndex The index of the tileset 0-block, 16-terrain, 32-scenery
     * @param repeatIndex The tile index for repeating terrain
     * @param frameNumber The frame number
     * @return The tile image data
     */
    public BufferedImage getFrameIndex(int tileIndex, int tilesetIndex, int repeatIndex, int frameNumber) {

        //brick, block or coin
        if (Map.isAnimated(tileIndex, tilesetIndex)) {
            return mBlockTile[tileIndex].getSubimage(0, frameNumber * 16, 16, 16);
        }
        if (tilesetIndex == 0) {
            return mBlockTile[tileIndex];
        } else if (tilesetIndex == 16) {
            return mTerrainTile[tileIndex]; //.getSubimage(0, tile3*16, 16, 16);
        } else if (tilesetIndex == 32) {
            return mSceneryTile[tileIndex];
        } else {
            return null;
        }
    }

    /**
     * Adds a piece of a block explosion
     * @param x The x coordinate
     * @param y The y coordinate
     * @param direction The direction of the explosion
     */
    public void setBlockExplode(int x, int y, char direction) {
        BlockExplosion be = new BlockExplosion();
        be.x = x;
        be.y = y;
        be.direction = direction;
        mExplosion.add(be);
    }

    /**
     * Draws a tile from the level map on the graphics surface
     * @param gl The graphics context
     * @param x The x coordinate
     * @param y The y coordinate
     * @param cam The camera coordinates
     * @param tile The tile information
     */
    public void draw(Graphics g, int x, int y, Point cam, Tile tile) {

        if (tile.tile1 != 0 && tile.draw == 1) //tile is an object
        {
            x = (x * 16) - cam.x;
            y = (y * 16 - tile.offset) - cam.y;

            if (tile.tile3 > 0) //tiled terrain
            {
                g.drawImage(getFrame(tile.tile1, tile.tile2, tile.tile3 - 1), x, y, null);
            } else {

                if (tile.state == 1) {
                    if (Map.isInvisible(tile.tile1, tile.tile2)) {
                        tile.collision = 1;
                    }

                    if (tile.offset < 5) {
                        tile.offset++;
                    } else {
                        tile.offset = 0;
                        tile.state = 0;
                        if (tile.count > 0 && tile.count != 99) {
                            tile.count--;
                        }

                        if (tile.count == 0) {
                            tile.state = 2;
                        }
                    }

                    if (Map.isCoinBlock(tile.tile1, tile.tile2) && tile.count > 0) {
                        g.drawImage(getFrame(10, 0, 0), x, y - 16, null);
                    } else if (Map.is10CoinBlock(tile.tile1, tile.tile2) && tile.count > 0) {
                        g.drawImage(mBlockTile[10].getSubimage(0, mFrame * 16, 16,16), x, y - 16, null);
                    }
                } else if (tile.state == 2) {
                    tile.tile1 = 40;
                }

                if (!Map.isInvisible(tile.tile1, tile.tile2)) {
                    g.drawImage(getFrame(tile.tile1, tile.tile2, 0), x, y, null);
                } else {
                    if (tile.state == 2) {
                        g.drawImage(getFrame(tile.tile1, tile.tile2, 0), x, y, null);
                    }
                }
            }
        }
    }

    /**
     * Draws pieces of exploding block on the screen
     * @param gl The GL context
     * @param map The level map
     * @param cam The camera coordinates
     */    
    public void drawExplosions(Graphics g, Map map, Point cam) {
        for (int i = 0; i < mExplosion.size(); i++) {
            mExplosion.get(i).timer++;
            if (mExplosion.get(i).y < map.getHeight()) {
                if (mExplosion.get(i).direction == 'r') {
                    mExplosion.get(i).x++;
                    if (mExplosion.get(i).timer < 15) {
                        mExplosion.get(i).y--;
                    } else {
                        if (mExplosion.get(i).y < map.getHeight()) {
                            mExplosion.get(i).y += 3;
                        } else {
                            mExplosion.remove(i);
                        }
                    }
                } else {
                    mExplosion.get(i).x--;
                    if (mExplosion.get(i).timer < 15) {
                        mExplosion.get(i).y--;
                    } else {
                        if (mExplosion.get(i).y < map.getHeight()) {
                            mExplosion.get(i).y += 3;
                        } else {
                            mExplosion.remove(i);
                        }
                    }
                }
                //draw block explosion
                g.drawImage(mBlockTile[4].getSubimage(0,mFrame*16, 16, 16), mExplosion.get(i).x - cam.x, mExplosion.get(i).y - cam.y, null);
            }
        }
    }

    /**
     * Draws the tiles defined in the level map within the camera area
     * @param gl The graphics context
     * @param map The level map
     * @param cam The camera coordinates
     */   
    public void drawMap(Graphics g, Map map, Point cam) {
    	//set frame for animated tiles
        if (Settings.Animation) {
            mFrameTimer++;
            if (mFrameTimer == 20) {
                if (mFrame < 3) {
                    mFrame++;
                } else {
                    mFrame = 0;
                }
                mFrameTimer = 0;
            }
        }
        //draw objects in screen area
        for (int x = (cam.x - 100) / 16; x < (cam.x + 512) / 16; x++) {
            for (int y = (cam.y - 100) / 16; y < (cam.y + 300) / 16; y++) {
                mTileInfo = map.getTile(x, y);
                draw(g, x, y, cam, mTileInfo);
            }
        }
    }

    /**
     * Draws the tiles defined in the level editor map within the camera area
     * @param gl The graphics context
     * @param map The level map
     * @param cam The camera coordinates
     */      
    public void drawEditorMap(Graphics g, Map map, Point cam) {
        //set animation frame for objects in the scene
        if (Settings.Animation) {
            mFrameTimer++;
            if (mFrameTimer == 10) {
                if (mFrame < 3) {
                    mFrame++;
                } else {
                    mFrame = 0;
                }
                mFrameTimer = 0;
            }
        }

        //draw objects in screen area
        for (int x = (cam.x - 100) / 16; x < (cam.x + 512) / 16; x++) {
            for (int y = (cam.y - 100) / 16; y < (cam.y + 300) / 16; y++) {
                mTileInfo = map.getTile(x, y);
                draw(g, x, y, cam, mTileInfo);
                if (Map.isCoinBlock(mTileInfo.tile1, mTileInfo.tile2)) {
                    g.drawImage(getFrame(10, 0, 0), x * 16 - cam.x, (y - 1) * 16 - cam.y, null);
                } else if (Map.is10CoinBlock(mTileInfo.tile1, mTileInfo.tile2)) {
                    g.drawImage(getFrameIndex(10, 0, 0, 3), x * 16 - cam.x, (y - 1) * 16 - cam.y, null);
                }
            }
        }

    }

    /**
     * Initialise the tileset information
     */
    private void initDefinitions() {
        mTileDefintions = new ArrayList<String>();
        //read object and sprite definitions into array list 
        String path = "";
        path = System.getProperty("user.dir") + "\\res\\obj\\block\\" + mTilesetIndex[0] + "\\tileset.ini";
        String line = "";
        //tileset 1
        try {
            BufferedReader input = new BufferedReader(new FileReader(path));
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefintions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tileset 2
        path = System.getProperty("user.dir") + "\\res\\obj\\terrain\\" + mTilesetIndex[1] + "\\tileset.ini";
        try {
            BufferedReader input = new BufferedReader(new FileReader(path));
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefintions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tileset 3
        path = System.getProperty("user.dir") + "\\res\\obj\\scenery\\" + mTilesetIndex[2] + "\\tileset.ini";
        try {
            BufferedReader input = new BufferedReader(new FileReader(path));
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefintions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the description of a tile
     * @param index The index of the tile
     * @return The tile description
     */   
    public String[] getTileDescription(int index) {
        return mTileDefintions.get(index).split(",");
    }

    /**
     * Returns the size of the tile descriptions array
     * @return The size of tile descriptions array
     */   
    public int getSizeOfTileDescriptions() {
        return mTileDefintions.size();
    }

    /**
     * Returns a string array containing a detailed description of the tile
     * @param tileIndex The tile index
     * @param tilesetIndex The tileset index 0-block, 16-object 32-scenery
     * @return The ile description array [0]-index [1]-tileset [3]-name [4]-width [5]-height [6]-collision flag [7]-tileX [8]-tileY
     */   
    public String[] getTileDescription(int tileIndex, int tilesetIndex) {

        String[] desc = new String[5];

        for (int i = 0; i < mTileDefintions.size(); i++) //array of definitions
        {
            //split str into tile number, description and tile width/height
            desc = mTileDefintions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tileIndex)) && desc[1].equals(Integer.toString(tilesetIndex))) //tile match
            {
                return desc;
            }
        }
        desc[2] = "Null";
        return desc;
    }
}

/**
	This class stores the coordinates and direction of a piece of exploding block.
	
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

class BlockExplosion {
    int x = 0;
    int y = 0;
    int timer = 0;
    char direction = 'l';
};
