package com.citex.dragonisland.core.level;

import java.io.*;
import java.util.ArrayList;

import android.os.Environment;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.core.drawing.Color;
import com.citex.dragonisland.core.game.GameFolder;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.sprite.entity.EntityDescription;
import com.citex.dragonisland.core.tileset.TileDescription;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.java.Main;

/**
 * This class opens and saves level files which contain header, tile and entity information.
 * @author Lawrence Schmid
 */
public class Level {

	/** Levels header information. */
    private Header mHeader;
    
    /** Tile information (blocks, terrain, scenery). */
    private ArrayList<TileDescription> mTiles;
    
    /** Sprite information */
    private ArrayList<EntityDescription> mEntities;
    
    /** Entity definitions. */
    private ArrayList<String> mEntityDefinitions;
    
    /** Tile definitions. */
    private ArrayList<String> mTileDefinitions;
    
    /** Level map. */
    private Map mMap;
    
    /**
     * Default constructor.
     * @throws IOException 
     */
    public Level() throws IOException {
    	
    	// Initialise a new level.
    	newLevel();
    	
    	// Initialise level map.
        mMap = toMap();
    }
    
    /**
     * Initialises a Level object. 
     * @param path Level file path.
     * @throws IOException 
     */
    public Level(GameFolder gameFolder, String path) throws IOException {
        
    	// Load level data.
    	loadLevel(gameFolder, path);
    	
    	// Initialise level map.
        mMap = toMap();
    } 

    /**
     * Gets the level header information.
     * @return Level header object.
     */
    public Header getHeader() {
        return mHeader;
    }

    /**
     * Sets the level header information.
     * @param header Level header object.
     */
    public void setHeader(Header header) {
        mHeader = header;
    }

    /**
     * Gets tile information.
     * @param index Index of the tile.
     * @return TileDescription object.
     */
    public TileDescription getTileDescription(int index) {
        try {
            return mTiles.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets tile information.
     * @param index Index of the tile
     * @param obj TileDescription object.
     */
    public void setTileDescription(int index, TileDescription obj) {
        mTiles.remove(index);
        mTiles.add(index, obj);
    }

    /**
     * Removes a tile.
     * @param index Index of the tile.
     */
    public void removeTile(int index) {
        mTiles.remove(index);
        mHeader.objectCount = mTiles.size();
    }

    /**
     * Adds a tile.
     * @param tileIndex Index of the tile.
     * @param tilesetNumber Folder containing the tiles 0-block, 16-terrain, 32-scenery.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param w Width of the tile.
     * @param h Height of the tile.
     */
    public void addTile(int tileIndex, int tilesetNumber, int x, int y, int w, int h) {
        
    	// Get the tile description.
    	String desc[] = getTileStringDescription(tileIndex, tilesetNumber);
                
        if (!desc[2].equals("Null")) {
            
        	// Initialise the tile.
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
            
            // Set minimum dimensions.
            if (w < obj.tileWidth)
                obj.width = obj.tileWidth;

            if (h < obj.tileHeight) 
                obj.height = obj.tileHeight;
            
            
            // Add the tile.
            mTiles.add(obj);
            
            // Update the level header.
            mHeader.objectCount = mTiles.size();
            
        }
    }
    
    /**
     * Gets the level map.
     * @return Map object.
     */
    public Map getMap() {
    	return mMap;    	
    }

    /**
     * Gets a entity description
     * @param index Index of the entity.
     * @return EntityDescription object.
     */
    public EntityDescription getEntityDescription(int index) {
        return mEntities.get(index);
    }
    
    /**
     * Gets a list of all the entity descriptions.
     * @return List of EntityDescription objects.
     */
    public ArrayList<EntityDescription> getEntityDescriptions() {
    	return mEntities;    	
    }

    /**
     * Sets a entity description.
     * @param index Index of the entity.
     * @param entity EntityDescription object.
     */
    public void setEntityDescription(int index, EntityDescription entity) {
        mEntities.remove(index);
        mEntities.add(index, entity);
    }

    /**
     * Removes a entity.
     * @param index Index of the entity.
    */
    public void removeEntity(int index) {
        mEntities.remove(index);
        mHeader.entityCount = mEntities.size();
    }

    /**
     * Adds a entity to the level.
     * @param index Index of the entity.
     * @param x X coordinate
     * @param y Y coordinate
     * @param direction Direction of the entity.
     * @param angle Angle of the entity.
     */
    public void addEntity(int index, int x, int y, char direction, int angle) {
        
    	// Get the entity description.
    	String desc[] = getEntityStringDescription(index);
                
        if (!desc[2].equals("Null")) {
            
        	// Initialise the entity description.
        	EntityDescription spr = new EntityDescription();
            spr.tile = index;
            spr.name = desc[1];
            spr.tileWidth = Integer.parseInt(desc[2]);
            spr.tileHeight = Integer.parseInt(desc[3]);
            spr.x = x;
            spr.y = y;
            spr.direction = direction;
            spr.angle = angle;
            
            // Add the entity.
            mEntities.add(spr);
            
            // Update the level header.
            mHeader.entityCount = mEntities.size();
        }
    }
    
    /**
     * Gets the number of tiles in the level.
     * @return Number of tiles in the level.
     */
    public int getObjectCount() {
        return mTiles.size();
    }

    /**
     * Gets the number of entities in the level.
     * @return Number of entities in the level.
     */
    public int getEntityCount() {
        return mEntities.size();
    }

    /**
     * Removes the end of level castle and associated objects and entities.
     */
    public void removeCastle() {
       
    	// Find castle tiles.
    	for (int i = 0; i < mTiles.size(); i++) {
            TileDescription obj = mTiles.get(i);
            if (obj.name.equals("Castle") || obj.name.equals("Flag Pole Base") || obj.name.equals("Castle Extension")) {
                mTiles.remove(i);
                i--; 
            }
        }
    	
    	// Find castle sprites.
        for (int i = 0; i < mEntities.size(); i++) {
            EntityDescription spr = mEntities.get(i);
            if (spr.name.equals("End Flag") || spr.name.equals("Castle Flag") || spr.name.equals("Flag Pole")) {
                mEntities.remove(i);
                i--;
            }
        }
        
        // Update the level header.
        mHeader.objectCount = mTiles.size();
        mHeader.entityCount = mEntities.size();
    }

    /**
     * Gets the level map.
     * @return Map object.
     */
    public Map toMap() {
        
    	// Initialise the map with the level header.
    	Map map = new Map(mHeader);
        
    	// Add entity tiles.
    	for (int i = 0; i < mEntities.size(); i++)
            map.setEntityTile(i, mEntities.get(i));

    	// Add object tiles.
        for (int i = 0; i < mTiles.size(); i++)
            map.setObjectTile(i, mTiles.get(i));
        
        // Initialise the map.
        mMap = map;
        
        return map;
    }

    /**
     * Loads a legacy level file using BufferedReader.
     * @param gameFolder GameFolder object.
     * @param path Level file path.
     * @throws IOException 
     */
    public void loadLegacyLevel(GameFolder gameFolder, String fileName) throws IOException {

    	// Initialise level header.
        mHeader = new Header();
        
        // Initialise tiles.
        mTiles = new ArrayList<TileDescription>();
        mTileDefinitions = new ArrayList<String>();
        
        // Initialise entities.
        mEntities = new ArrayList<EntityDescription>();
        mEntityDefinitions = new ArrayList<String>();
                
        BufferedReader input = null;
        try {
 
            // Open level file.
        	input = FileIO.loadLegacyLevel(gameFolder, fileName);
  
            // Read level header.
            mHeader.world = Integer.parseInt(input.readLine());
        	mHeader.level = Integer.parseInt(input.readLine());
        	mHeader.area = Integer.parseInt(input.readLine());
        	mHeader.name = "World " + mHeader.world + "-" + mHeader.level + " Area " + mHeader.area;
        	mHeader.timeLimit = Integer.parseInt(input.readLine());
			mHeader.bg[0] = Integer.parseInt(input.readLine());
			mHeader.bg[1] = Integer.parseInt(input.readLine());
			mHeader.bg[2] = Integer.parseInt(input.readLine());
			mHeader.bgColor = new Color(Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()), Integer.parseInt(input.readLine()));
			mHeader.bgSpeed[0] = Float.parseFloat(input.readLine());
			mHeader.bgSpeed[1] = Float.parseFloat(input.readLine());
			mHeader.bgSpeed[2] = Float.parseFloat(input.readLine());
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

			// Other unused bytes.
            for (int i = 0; i < 6; i++) {
            	Integer.parseInt(input.readLine());
            }
                
            // Initialise tile definitions.
            initTileDefinitions();
               
            // Read object tile data.     
            String line;
            for (int i = 0; i < mHeader.objectCount; i++) {

                line = input.readLine(); 
                if(line == null)
                	continue;
                
                TileDescription obj = new TileDescription();  
                obj.tile = Integer.parseInt(line.split(",")[0]);
                obj.tileset = Integer.parseInt(line.split(",")[1]);
                
                // Get tile description.
                String def[] = getTileStringDescription(obj.tile, obj.tileset);
                obj.name = def[2];
                obj.tileWidth = Integer.parseInt(def[3]);
                obj.tileHeight = Integer.parseInt(def[4]);
                obj.collision = Integer.parseInt(def[5]);
                
                obj.x = Integer.parseInt(line.split(",")[2]);
                obj.y = Integer.parseInt(line.split(",")[3]);
                obj.width = Integer.parseInt(line.split(",")[4]);
                obj.height = Integer.parseInt(line.split(",")[5]);
                
                if (!def[2].equals("Null"))
                    mTiles.add(obj);
            }
            mHeader.objectCount = mTiles.size();  
             
            // Read sprite tile data.
            for (int i = 0; i < mHeader.entityCount; i++) {

                line = input.readLine();       
                if(line == null)
                	continue;
                
            	EntityDescription spr = new EntityDescription();   	
            	spr.tile = Integer.parseInt(line.split(",")[0]);
                
            	// Get entity description.
            	String def[] = getEntityStringDescription(spr.tile);
                spr.name = def[1];
                spr.tileWidth = Integer.parseInt(def[2]);
                spr.tileHeight = Integer.parseInt(def[3]);
                spr.collision = 1;//Integer.parseInt(def[4]);
                
                spr.x = Integer.parseInt(line.split(",")[1]);
                spr.y = Integer.parseInt(line.split(",")[2]);
                //spr.direction = line.split(",")[3].toCharArray()[0];
                //spr.angle = Integer.parseInt(line.split(",")[4]);
                
                if (!def[1].equals("Null"))
                	mEntities.add(spr);
	                

             }
             mHeader.entityCount = mEntities.size();
             
             // Close level file.
             input.close();
        
        } finally {
        	
        	// Close level file.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	            }
	        }
        }

    }

    
    /**
     * Loads a level file.
     * @param gameFolder GameFolder object.
     * @param path Level file path.
     * @throws IOException 
     */
    public void loadLevel(GameFolder gameFolder, String fileName) throws IOException {

    	// Initialise level header.
        mHeader = new Header();
       
        // Initialise tiles.
        mTiles = new ArrayList<TileDescription>();
        mTileDefinitions = new ArrayList<String>();
        
        // Initialise entities.
        mEntities = new ArrayList<EntityDescription>();
        mEntityDefinitions = new ArrayList<String>();

        RandomAccessFile f = null;
        try {

        	// Open level file.
        	f = FileIO.loadLevel(gameFolder, fileName);
        
        	// Read level header.
            mHeader.world = f.readInt();
        	mHeader.level = f.readInt();
        	mHeader.area = f.readInt();
        	mHeader.name = "World " + mHeader.world + "-" + mHeader.level + " Area " + mHeader.area;
        	mHeader.timeLimit = f.readInt();
        	mHeader.bg[0] =  f.readInt();
            mHeader.bg[1] =  f.readInt();
            mHeader.bg[2] =  f.readInt();
            mHeader.bgColor = new Color(f.readInt(), f.readInt(), f.readInt());
            mHeader.bgSpeed[0] = f.readFloat();
            mHeader.bgSpeed[1] = f.readFloat();
            mHeader.bgSpeed[2] = f.readFloat();
            mHeader.bgAlign = f.readInt();
            mHeader.music = f.readInt();
            mHeader.tileset0 = f.readInt();
            mHeader.tileset16 = f.readInt();
            mHeader.tileset32 = f.readInt();
            mHeader.width = f.readInt() * 16;
            mHeader.height = f.readInt() * 16;
            mHeader.objectCount = f.readInt();
            mHeader.entityCount = f.readInt();
            mHeader.startState = f.readInt();
            mHeader.startX = f.readInt();
            mHeader.startY = f.readInt();
            mHeader.endState = f.readInt();
            mHeader.endX = f.readInt();
            mHeader.endY = f.readInt();
            mHeader.endWorld = f.readInt();
            mHeader.endLevel = f.readInt();
            mHeader.endArea = f.readInt();
            mHeader.bonusX = f.readInt();
            mHeader.bonusY = f.readInt();
            mHeader.bonusState = f.readInt();
            
            // Other unused bytes.
            for (int i = 0; i < 6; i++)
            	f.readInt();

            // Initialise tile definitions.
            try {
            	initTileDefinitions();
            }
            catch(Exception e) {
            	
            	// Try and load legacy level as level header is not initialised.
            	loadLegacyLevel(gameFolder, fileName);
            	return;
            }
    
            // Read object tile data.
            for (int i = 0; i < mHeader.objectCount; i++) {
            	
            	TileDescription obj = new TileDescription();                 
                obj.tile = f.readInt();
                obj.tileset = f.readInt();
                
                String def[] = getTileStringDescription(obj.tile, obj.tileset);
                obj.name = def[2];
                
                obj.tileWidth = f.readInt();
                obj.tileHeight = f.readInt();
               	
                obj.collision = f.readInt();
                
                obj.x = f.readInt();
                obj.y = f.readInt();
                obj.width = f.readInt();
                obj.height = f.readInt();
                
                if (!def[2].equals("Null"))
                	mTiles.add(obj);
             }
             mHeader.objectCount = mTiles.size();  
             
             // Read sprite tile data.
             for (int i = 0; i < mHeader.entityCount; i++) {
            	 
            	 EntityDescription spr = new EntityDescription();   
                 spr.tile = f.readInt();
                 
                 String def[] =getEntityStringDescription(spr.tile);
                 spr.name = def[1];
                 
                 spr.tileWidth = f.readInt();
                 spr.tileHeight = f.readInt();
                 spr.collision = f.readInt();
                 spr.x = f.readInt();
                 spr.y = f.readInt();
                 spr.direction = f.readChar();
                 spr.angle = f.readInt();
                 
                 if (!def[1].equals("Null"))
                     mEntities.add(spr);
                 
             }
             mHeader.entityCount = mEntities.size();
             
             // Close level file.
             f.close();
             
        } finally {

        	// Close level file.
	        if (f != null) {
	            try {
	                f.close();
	            } catch (IOException e) {
	            }
	        }       	
		}

    }

    /**
     * Initialises a new level.
     * @throws IOException 
     */
    public void newLevel() throws IOException {
    	
    	// Initialise header.
        mHeader = new Header();
        
        // Initialise tiles.
        mTileDefinitions = new ArrayList<String>();
        mTiles = new ArrayList<TileDescription>();
        
        // Initialise entities.
        mEntities = new ArrayList<EntityDescription>();
        mEntityDefinitions = new ArrayList<String>();
    	
        // Initialise header.
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
        
        // Initialise tile definitions.
        initTileDefinitions();
    }

    /**
     * Saves the level data.
     * @param path Level file path.
     * @throws IOException 
     */
    public void saveLevel(String path) throws IOException {
   	
    	RandomAccessFile f = null;
        try {
            
        	// Create level file.
        	f = new RandomAccessFile(path, "rw");
            
            // Write level header.
            f.writeInt(mHeader.world);
            f.writeInt(mHeader.level);
            f.writeInt(mHeader.area);
            f.writeInt(mHeader.timeLimit);
            f.writeInt(mHeader.bg[0]);
            f.writeInt(mHeader.bg[1]);
            f.writeInt(mHeader.bg[2]);
            f.writeInt(mHeader.bgColor.getRed());
            f.writeInt(mHeader.bgColor.getGreen());
            f.writeInt(mHeader.bgColor.getBlue());
            f.writeFloat(mHeader.bgSpeed[0]);
            f.writeFloat(mHeader.bgSpeed[1]);
            f.writeFloat(mHeader.bgSpeed[2]);
            f.writeInt(mHeader.bgAlign);
            f.writeInt(mHeader.music);
            f.writeInt(mHeader.tileset0);
            f.writeInt(mHeader.tileset16);
            f.writeInt(mHeader.tileset32);
            f.writeInt(mHeader.width / 16);
            f.writeInt(mHeader.height / 16);
            f.writeInt(mHeader.objectCount);
            f.writeInt(mHeader.entityCount);
            f.writeInt(mHeader.startState);
            f.writeInt(mHeader.startX);
            f.writeInt(mHeader.startY);
            f.writeInt(mHeader.endState);
            f.writeInt(mHeader.endX);
            f.writeInt(mHeader.endY);
            f.writeInt(mHeader.endWorld);
            f.writeInt(mHeader.endLevel);
            f.writeInt(mHeader.endArea);
            f.writeInt(mHeader.bonusX);
            f.writeInt(mHeader.bonusY);
            f.writeInt(mHeader.bonusState);
        	
            // Other unused bytes.
            for (int i = 0; i < 6; i++)
            	f.writeInt(0);
                        
            // Write object tile data.
            TileDescription obj;
            for (int i = 0; i < mTiles.size(); i++) {
                obj = mTiles.get(i);
                f.writeInt(obj.tile);
                f.writeInt(obj.tileset);
                f.writeInt(obj.tileWidth);
                f.writeInt(obj.tileHeight);
                f.writeInt(obj.collision);
                f.writeInt(obj.x);
                f.writeInt(obj.y);
                f.writeInt(obj.width);
                f.writeInt(obj.height);
            }

            // Write sprite tile data.
            EntityDescription spr;
            for (int i = 0; i < mEntities.size(); i++) {
                spr = mEntities.get(i);
                f.writeInt(spr.tile);
                f.writeInt(spr.tileWidth);
                f.writeInt(spr.tileHeight);
                f.writeInt(spr.collision);
                f.writeInt(spr.x);
                f.writeInt(spr.y);
                f.writeChar(spr.direction);
                f.writeInt(spr.angle);
            }
            
            // Close file.
            f.close();
            
        } finally {
        	
        	// Close level file.
	        if (f != null) {
	            try {
	                f.close();
	            } catch (IOException e) {
	            }
	        }
        }
    	
    }
        
    /**
     * Saves the level data in plain text.
     * @param path Level file path.
     * @throws IOException 
     */
    public void saveLegacyLevel(String path) throws IOException {
    	
    	PrintWriter pw = null;
        try {
        	
        	// Create level file.
        	pw = new PrintWriter(new FileWriter(path));
        	
        	// Write level header.
        	pw.println(mHeader.world);
        	pw.println(mHeader.level);
        	pw.println(mHeader.area);
        	pw.println(mHeader.timeLimit);
        	pw.println(mHeader.bg[0]);
        	pw.println(mHeader.bg[1]);
        	pw.println(mHeader.bg[2]);
        	pw.println(mHeader.bgColor.getRed());
        	pw.println(mHeader.bgColor.getGreen());
        	pw.println(mHeader.bgColor.getBlue());
        	pw.println(mHeader.bgSpeed[0]);
        	pw.println(mHeader.bgSpeed[1]);
        	pw.println(mHeader.bgSpeed[2]);
        	pw.println(mHeader.bgAlign);
        	pw.println(mHeader.music);
        	pw.println(mHeader.tileset0);
        	pw.println(mHeader.tileset16);
        	pw.println(mHeader.tileset32);
        	pw.println(mHeader.width / 16);
        	pw.println(mHeader.height / 16);
        	pw.println(mHeader.objectCount);
        	pw.println(mHeader.entityCount);
        	pw.println(mHeader.startState);
        	pw.println(mHeader.startX);
        	pw.println(mHeader.startY);
        	pw.println(mHeader.endState);
        	pw.println(mHeader.endX);
        	pw.println(mHeader.endY);
        	pw.println(mHeader.endWorld);
        	pw.println(mHeader.endLevel);
        	pw.println(mHeader.endArea);
        	pw.println(mHeader.bonusX);
        	pw.println(mHeader.bonusY);
        	pw.println(mHeader.bonusState);
                   	
        	// Other unused bytes.
            for (int i = 0; i < 6; i++)
            	pw.println(0);
                        
            // Write object data.
            TileDescription obj;
            for (int i = 0; i < mTiles.size(); i++) {
                obj = mTiles.get(i);
                pw.println(obj.tile + "," + obj.tileset + "," + obj.x + "," + obj.y + "," + obj.width + "," + obj.height);
            }
            // Write sprite data.
            EntityDescription spr;
            for (int i = 0; i < mEntities.size(); i++) {
                spr = mEntities.get(i);
                pw.println(spr.tile + "," + spr.x + "," + spr.y + "," + spr.direction + "," + spr.angle);
            }
            
            // Close the file.
            pw.close();
            
        } finally {
        	
        	// Close level file.
	        if (pw != null) {
	            pw.close();
	        }
	        
        }
    	
    }
    
    /**
     * Gets the string description of a tile.
     * @param tileIndex Index of tile.
     * @param tilesetindex Tileset index (0-block, 16-terrain, 32-scenery).
     */
    public String[] getTileStringDescription(int tileIndex, int tilesetIndex) {

    	// Loop through tile definitions.
       	String[] desc = new String[5];
    	for (int i = 0; i < mTileDefinitions.size(); i++) {
            
        	// Find tile.
            desc = mTileDefinitions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tileIndex)) && 
            	desc[1].equals(Integer.toString(tilesetIndex))) {
                return desc;
            }
        }
        
        desc[2] = "Null";
        return desc;
    }

    /**
     * Gets the string description of a entity.
     * @param tile Index of the entity.
    */
    public String[] getEntityStringDescription(int tile) {
       
    	// Loop through entity definitions.
    	String desc[] = new String[5];
    	for (int i = 0; i < mEntityDefinitions.size(); i++) {

            // Find entity.
    		desc = mEntityDefinitions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tile)))
                return desc;

        }
    	
        desc[1] = "Null";
        return desc;

    }

    /**
     * Initialises tile definitions.
     * @throws IOException 
     */
    private void initTileDefinitions() throws IOException {

        // Tileset 0 (block).    
        BufferedReader input = null;
        String line = null;    
        mTileDefinitions.clear();
        try {
        	
           	// Open buffered reader.
        	input = FileIO.openBufferedReader("obj/block/" + mHeader.tileset0 + "/tileset.ini");
            
        	// Read tile definitions.
        	while ((line = input.readLine()) != null)
                mTileDefinitions.add(line);
        	
        } finally {
        	
        	// Close buffered reader.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	            }
	        }
        }

        // Tileset 16 (terrain).
        try {
        	
           	// Open buffered reader.
        	input = FileIO.openBufferedReader(Settings.ResourcePath + "obj/terrain/" + mHeader.tileset16 + "/tileset.ini");
            
        	// Read tile definitions.
        	while ((line = input.readLine()) != null)
                mTileDefinitions.add(line);
        	
        } finally {
        	
        	// Close buffered reader.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	            }
	        }
        }

        // Tileset 3 (scenery)        
        try {
        	
           	// Open buffered reader.
        	input = FileIO.openBufferedReader(Settings.ResourcePath + "obj/scenery/" + mHeader.tileset32 + "/tileset.ini");
            
        	// Read tile definitions.
        	while ((line = input.readLine()) != null)
                mTileDefinitions.add(line);
        	
        } finally {
        	
        	// Close buffered reader.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	            }
	        }
        }        

        // Sprites.
        mEntityDefinitions.clear(); 
        try {
        	
           	// Open buffered reader.
        	input = FileIO.openBufferedReader(Settings.ResourcePath + "spr/sprite.ini");
            
        	// Read tile definitions.
        	while ((line = input.readLine()) != null)
                mEntityDefinitions.add(line);
        	
        } finally {
        	
        	// Close buffered reader.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	            }
	        }
        }       

    }
    
}