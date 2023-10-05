package com.citex.applet.dragonisland;

import java.io.*;
import java.util.ArrayList;
import java.awt.Color;

/**
	This class opens and saves level files which contain header, tile and entity information.
	
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

public class Level {

	/** The levels header information */
    private Header mHeader;
    /** The tile information (blocks, terrain, scenery) */
    private ArrayList<TileDescription> mTiles;
    /** The sprites information */
    private ArrayList<EntityDescription> mEntities;
    /** The entity descriptions */
    private ArrayList<String> mEntitiyDefinitions;
    /** The tile descriptions */
    private ArrayList<String> mTileDefinitions;

    /**
     * Constructs the Level 
     * @param path the file input path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
     */
    public Level(String path) {
        loadLevel(path);
    } 
    
    /**
     * Default constructor
     */
    public Level() {
    }

    /**
     * Returns the level header information
     * @return The level header
     */
    public Header getHeader() {
        return mHeader;
    }

    /**
     * Sets the level header information
     * @param header The level header
     */
    public void setHeader(Header header) {
        mHeader = header;
    }

    /**
     * Returns tile information
     * @param index The index of the tile
     * @return The level tile information
     */
    public TileDescription getTileDescription(int index) {
        try {
            return mTiles.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets tile information
     * @param index The index of the tile
     * @param obj The level tile information
     */
    public void setTileDescription(int index, TileDescription obj) {
        mTiles.remove(index);
        mTiles.add(index, obj);
    }

    /**
     * Removes a tile from the level
     * @param index The index of the tile
     */
    public void removeTile(int index) {
        mTiles.remove(index);
        mHeader.objectCount = mTiles.size();
    }

    /**
     * Adds a tile to the level
     * @param tileIndex The index for the tile
     * @param tilesetNumber The folder containing the tiles 0-block, 16-terrain, 32-scenery
     * @param x The x coordinate
     * @param y The y coordinate
     * @param w The width of the tile
     * @param h The height of the tile
     */
    public void addTile(int tileIndex, int tilesetNumber, int x, int y, int w, int h) {
        String desc[] = getTileStringDescription(tileIndex, tilesetNumber);
        if (!desc[2].equals("Null")) {
            TileDescription obj = new TileDescription();
            obj.x = x;
            obj.y = y;
            obj.tile = tileIndex;
            obj.tileset = tilesetNumber;
            obj.name = desc[2];
            obj.tileWidth = Integer.parseInt(desc[3]);
            obj.tileHeight = Integer.parseInt(desc[4]);
            obj.collision = Integer.parseInt(desc[5]);
            obj.width = w;
            obj.height = h;
            if (w < obj.tileWidth) {
                obj.width = obj.tileWidth;
            }
            if (h < obj.tileHeight) {
                obj.height = obj.tileHeight;
            }
            mTiles.add(obj);
            mHeader.objectCount = mTiles.size();
        }
    }

    /**
     * Returns a entity description
     * @param index The index of the entity
     * @return The entity description
     */
    public EntityDescription getEntityDescription(int index) {
        return mEntities.get(index);
    }

    /**
     * Sets a entity description
     * @param index The index of the entity
     * @param entity The entity description
     */
    public void setEntityDescription(int index, EntityDescription entity) {
        mEntities.remove(index);
        mEntities.add(index, entity);
    }

    /**
     * Removes a entity
     * @param index The index of the entity
    */
    public void removeEntity(int index) {
        mEntities.remove(index);
        mHeader.entityCount = mEntities.size();
    }

    /**
     * Adds a entity to the level
     * @param index The image resource index for the sprite sheet
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void addEntity(int index, int x, int y) {
        String desc[] = getEntityStringDescription(index);
        if (!desc[2].equals("Null")) {
            EntityDescription spr = new EntityDescription();
            spr.tile = index;
            spr.name = desc[1];
            spr.tileWidth = Integer.parseInt(desc[2]);
            spr.tileHeight = Integer.parseInt(desc[3]);
            spr.x = x;
            spr.y = y;
            mEntities.add(spr);
            mHeader.entityCount = mEntities.size();
        }
    }

    /**
     * Returns the number of tiles in the level
     * @return The number of tiles in the level
     */
    public int getObjectCount() {
        return mTiles.size();
    }

    /**
     * Returns the number of entities in the level
     * @return The number of entities in the level
     */
    public int getEntityCount() {
        return mEntities.size();
    }

    /**
     * Removes the end of level castle and associated objects and entities
     */
    public void removeCastle() {
        for (int i = 0; i < mTiles.size(); i++) {
            TileDescription obj = mTiles.get(i);
            if (obj.name.equals("Castle") || obj.name.equals("Flag Pole Base") || obj.name.equals("Castle Extension")) {
                mTiles.remove(i);
                i--; 
            }
        }
        for (int i = 0; i < mEntities.size(); i++) {
            EntityDescription spr = mEntities.get(i);
            if (spr.name.equals("End Flag") || spr.name.equals("Castle Flag") || spr.name.equals("Flag Pole")) {
                mEntities.remove(i);
                i--;
            }
        }
        mHeader.objectCount = mTiles.size();
        mHeader.entityCount = mEntities.size();
    }

    /**
     * Returns the level map
     * @return The level map containing object and entities
     */
    public Map toMap() {
        Map map = new Map(mHeader);
        for (int i = 0; i < mEntities.size(); i++) {
            map.setEntityTile(i, mEntities.get(i));
        }
        for (int i = 0; i < mTiles.size(); i++) {
            map.setObjectTile(i, mTiles.get(i));
        }
        return map;
    }

    /**
     * Loads the level header, tile and entity information
     * @param world The world number
     * @param level The level number
     * @param area The area number
     * @return True if the level exists
     */
    public boolean loadLevel(String path) {

        boolean result = false;
        mHeader = new Header();
        mTiles = new ArrayList<TileDescription>();
        mEntities = new ArrayList<EntityDescription>();
        mEntitiyDefinitions = new ArrayList<String>();
        mTileDefinitions = new ArrayList<String>();
        
        try {
            String line;
        	InputStream is = Main.class.getResourceAsStream(path); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            mHeader.world = Integer.parseInt(input.readLine());
        	mHeader.level = Integer.parseInt(input.readLine());
        	mHeader.area = Integer.parseInt(input.readLine());
        	mHeader.name = "World " + mHeader.world + "-" + mHeader.level
             + " Area " + mHeader.area;
        	mHeader.timeLimit = Integer.parseInt(input.readLine());
        	
        	 mHeader.bg[0] = Integer.parseInt(input.readLine());
             mHeader.bg[1] = Integer.parseInt(input.readLine());
             mHeader.bg[2] = Integer.parseInt(input.readLine());
             mHeader.bgColor = new Color(Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()));
             mHeader.bgSpeed[0] = Integer.parseInt(input.readLine());
             mHeader.bgSpeed[1] = Integer.parseInt(input.readLine());
             mHeader.bgSpeed[2] = Integer.parseInt(input.readLine());
             mHeader.bgAlign = Integer.parseInt(input.readLine());
             mHeader.music =Integer.parseInt(input.readLine());
             mHeader.tileset0 = Integer.parseInt(input.readLine());
             mHeader.tileset16 = Integer.parseInt(input.readLine());
             mHeader.tileset32 = Integer.parseInt(input.readLine());
             mHeader.width = Integer.parseInt(input.readLine()) * 16;
             mHeader.height = Integer.parseInt(input.readLine()) * 16;
             mHeader.objectCount = Integer.parseInt(input.readLine());
             mHeader.entityCount = Integer.parseInt(input.readLine());
             mHeader.startState = Integer.parseInt(input.readLine());
             mHeader.startX = Integer.parseInt(input.readLine());
             mHeader.startY = Integer.parseInt(input.readLine());
             mHeader.endState = Integer.parseInt(input.readLine());
             mHeader.endX = Integer.parseInt(input.readLine());
             mHeader.endY = Integer.parseInt(input.readLine());
             mHeader.endWorld = Integer.parseInt(input.readLine());
             mHeader.endLevel = Integer.parseInt(input.readLine());
             mHeader.endArea = Integer.parseInt(input.readLine());
             mHeader.bonusX = Integer.parseInt(input.readLine());
             mHeader.bonusY = Integer.parseInt(input.readLine());
             mHeader.bonusState = Integer.parseInt(input.readLine());
             //other unused bytes
             for (int i = 0; i < 6; i++) {
            	 Integer.parseInt(input.readLine());
             }
             initDefinitions();
             String def[] = null;
             //read object data
             TileDescription obj;           
        	
             for (int i = 0; i < mHeader.objectCount; i++) {
                 obj = new TileDescription();                 
                 line = input.readLine();              
                 obj.tile = Integer.parseInt(line.split(",")[0]);
                 obj.tileset = Integer.parseInt(line.split(",")[1]);
                 def = getTileStringDescription(obj.tile, obj.tileset);
                 obj.name = def[2];
                 obj.tileWidth = Integer.parseInt(def[3]);
                 obj.tileHeight = Integer.parseInt(def[4]);
                 obj.collision = Integer.parseInt(def[5]);
                 obj.x = Integer.parseInt(line.split(",")[2]);
                 obj.y = Integer.parseInt(line.split(",")[3]);
                 obj.width = Integer.parseInt(line.split(",")[4]);
                 obj.height = Integer.parseInt(line.split(",")[5]);
                 if (!def[2].equals("Null")) {
                     mTiles.add(obj);
                 }
             }
             mHeader.objectCount = mTiles.size();  
             
             //read sprite data
             EntityDescription spr;
             for (int i = 0; i < mHeader.entityCount; i++) {
                 spr = new EntityDescription();
                 line = input.readLine();       
                 spr.tile = Integer.parseInt(line.split(",")[0]);
                 def = getEntityStringDescription(spr.tile);
                 spr.name = def[1];
                 spr.tileWidth = Integer.parseInt(def[2]);
                 spr.tileHeight = Integer.parseInt(def[3]);
                 spr.collision = 1;//Integer.parseInt(def[4]);
                 spr.x = Integer.parseInt(line.split(",")[1]);
                 spr.y = Integer.parseInt(line.split(",")[2]);
                 if (!def[1].equals("Null")) {
                     mEntities.add(spr);
                 }
             }
             mHeader.entityCount = mEntities.size();
             input.close();
             result = true;
         } catch (FileNotFoundException e) {
             newLevel();
             return result;
         } catch (IOException e) {
             newLevel();
             return result;
         }                
       
         toMap();
         return result;
    }

    /**
     * Initialises a new level
     */
    public void newLevel() {
        mHeader.world = 1;
        mHeader.level = 1;
        mHeader.area = 1;
        mHeader.endWorld = mHeader.world;
        mHeader.endLevel = mHeader.level + 1;
        mHeader.endArea = 1;
        mHeader.bg[0] = 1;
        mHeader.bg[1] = 1;
        mHeader.bg[2] = 1;
        mHeader.timeLimit = 300;
        mHeader.tileset0 = 1;
        mHeader.tileset16 = 1;
        mHeader.tileset32 = 1;
        mHeader.music = 1;
        mHeader.width = 480;
        mHeader.height = 272;
        mHeader.startX = 2;
        mHeader.startY = 14;
        mHeader.startState = 1;
        mHeader.objectCount = mTiles.size();
        mHeader.entityCount = mEntities.size();
        initDefinitions();
    }

     /**
     * Returns the description string of a tile
     * @param tileIndex The index of tile
     * @param tilesetindex The tileset index 0-block, 16-terrain, 32-scenery
     */
    public String[] getTileStringDescription(int tileIndex, int tilesetIndex) {
        String[] desc = new String[5];
        for (int i = 0; i < mTileDefinitions.size(); i++) //array of tile definitions
        {
            //split str into tile number, description and tile width/height
            desc = mTileDefinitions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tileIndex)) && desc[1].equals(Integer.toString(tilesetIndex))) //tile match
            {
                return desc;
            }
        }
        desc[2] = "Null";
        return desc;
    }

    /**
     * Returns the description string of a entity
     * @param tile The index of the entity
    */
    public String[] getEntityStringDescription(int tile) {
        String desc[] = new String[5];
        for (int i = 0; i < mEntitiyDefinitions.size(); i++) {
            desc = mEntitiyDefinitions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tile))) {
                return desc;
            }
        }
        desc[1] = "Null";
        return desc;

    }

    /**
     * Initialises the object definitions
     */
    private void initDefinitions() {
        //read from file object and sprite definitions into array list <String>
        String path;        
        String line;    
        //tileset 1       
        path = "res/obj/block/" + mHeader.tileset0 + "/tileset.ini";       
        try {
        	InputStream is = Main.class.getResourceAsStream(path); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));    	
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefinitions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //tileset 2
        path = "res/obj/terrain/"  + mHeader.tileset16 + "/tileset.ini";
        try {
        	InputStream is = Main.class.getResourceAsStream(path); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefinitions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //tileset 3
        path = "res/obj/scenery/" + mHeader.tileset32 + "/tileset.ini";
        try {
        	InputStream is = Main.class.getResourceAsStream(path); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            try {
                while ((line = input.readLine()) != null) {
                    mTileDefinitions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sprite
        path = "res/spr/sprite.ini";
        mEntitiyDefinitions.clear();
        try {
        	InputStream is = Main.class.getResourceAsStream(path); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            try {
                while ((line = input.readLine()) != null) {
                    mEntitiyDefinitions.add(line);
                }
            } finally {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
	This class stores level header information such as the background, music
		and tilesets associated with the level.
	
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

class Header {

    String name;
    int world;
    int level;
    int area;
    int timeLimit;
    int bg[] = new int[3];
    Color bgColor;
    int bgSpeed[] = new int[3];
    int bgAlign;
    int music;
    int tileset0;
    int tileset16;
    int tileset32;
    int width;
    int height;
    int objectCount;
    int entityCount;
    int startState;
    int startX;
    int startY;
    int endState;
    int endX;
    int endY;
    int endWorld;
    int endLevel;
    int endArea;
    int bonusX;
    int bonusY;
    int bonusState;
}

/**
	This class stores tile information.
	
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

class TileDescription {

    String name;
    int x;
    int y;
    int width;
    int height;
    int tile;
    int tileset;
    int tileWidth;
    int tileHeight;
    int collision;
};

/**
	This class stores entity information.
	
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

class EntityDescription {

    String name;
    int x;
    int y;
    int tileWidth;
    int tileHeight;
    int tile;
    int type;
    int collision;
}