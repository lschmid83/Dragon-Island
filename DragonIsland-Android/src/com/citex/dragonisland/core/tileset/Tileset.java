package com.citex.dragonisland.core.tileset;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.FileIO;

/**
 * This class loads loads and draw level map tiles.
 * @author Lawrence Schmid
 */
public class Tileset {

	/** Block tile sprite sheet. */
    private IBufferedImage mBlockTileset;
        
    /** Block tile images. */
    private IBufferedImage[] mBlockTile;
    
    /** Smashed block tile images. */
    private IBufferedImage[] mSmashedBlock;
    
    /** Coin tile images. */
    private IBufferedImage[] mCoin;
    
    /** Brick tile images. */
    private IBufferedImage[] mBrick;
    
    /** Question block tile images. */
    private IBufferedImage[] mQuestionBlock;
    
    /** Lava tile images. */
    private IBufferedImage[] mLava;
    
    /** Terrain tile sprite sheet. */
    private IBufferedImage mTerrainTileset;
    
    /** Terrain tile images. */
    private IBufferedImage[] mTerrainTile;
	
    /** Scenery tile sprite sheet. */
    private IBufferedImage mSceneryTileset;
    
    /** Scenery tile images. */
    private IBufferedImage[] mSceneryTile;
    
    /** Tileset indexes. */
    private int[] mTilesetIndex;
    
    /** Time elapsed between advancing animation frame.*/
    private float mFrameTimer;
    
    /** Animation frame index. */
    private int mFrame;

    /** List of tile definitions. */
    private ArrayList<String> mTileDefinitions;
    
    /** List of block explosions. */
    private ArrayList<Explosion> mExplosion;
 
    /**
     * Initialise a Tileset object.
     * @param g Graphics context.
     * @param tileset0 Block tileset index.
     * @param tileset16 Terrain tileset index.
     * @param tileset32 Scenery tileset index.
     * @throws IOException 
     */
    public Tileset(Object g, int tileset0, int tileset16, int tileset32) throws IOException {
        
    	// Initialise the tileset indexes.
    	mTilesetIndex = new int[3];
        mTilesetIndex[0] = tileset0;
        mTilesetIndex[1] = tileset16;
        mTilesetIndex[2] = tileset32;
       
        // Initialise a list of block explosions.
        mExplosion = new ArrayList<Explosion>();
        
        // Initialise tile definitions.
        initDefinitions();

        // Load the block tileset sprite sheet.
        mBlockTileset = FileIO.getImageResource(g, "obj/block/" + mTilesetIndex[0] + "/tileset.png");

        // Get the tile images from the sprite sheet.
        mBlockTile = new IBufferedImage[83]; 
        for (int i = 0; i < 4; i++) {
        	
        	// Block tile.
            int x = Integer.parseInt(getTileDescription(i, 0)[6]);
            int y = Integer.parseInt(getTileDescription(i, 0)[7]);
            int w = Integer.parseInt(getTileDescription(i, 0)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 0)[4]) * 16;  
            mBlockTile[i] = mBlockTileset.getSubImage(g, x, y, w, h);
        }

        // Smashed block.
        mSmashedBlock = new IBufferedImage[4];
        mSmashedBlock[0] = mBlockTileset.getSubImage(g, 0, 112, 16, 16);
        mSmashedBlock[1] = mBlockTileset.getSubImage(g, 0, 128, 16, 16);
        mSmashedBlock[2] = mBlockTileset.getSubImage(g, 0, 144, 16, 16);
        mSmashedBlock[3] = mBlockTileset.getSubImage(g, 0, 160, 16, 16);

        // Coin.
        mCoin = new IBufferedImage[4];
        mCoin[0] = mBlockTileset.getSubImage(g, 16, 112, 16, 16);
        mCoin[1] = mBlockTileset.getSubImage(g, 16, 128, 16, 16);
        mCoin[2] = mBlockTileset.getSubImage(g, 16, 144, 16, 16);
        mCoin[3] = mBlockTileset.getSubImage(g, 16, 160, 16, 16);    

        // Brick.
        mBrick = new IBufferedImage[4];
        mBrick[0] = mBlockTileset.getSubImage(g, 32, 112, 16, 16);
        mBrick[1] = mBlockTileset.getSubImage(g, 32, 128, 16, 16);
        mBrick[2] = mBlockTileset.getSubImage(g, 32, 144, 16, 16);
        mBrick[3] = mBlockTileset.getSubImage(g, 32, 160, 16, 16);        
  
        // ? block.
        mQuestionBlock = new IBufferedImage[4];
        mQuestionBlock[0] = mBlockTileset.getSubImage(g, 48, 112, 16, 16);
        mQuestionBlock[1] = mBlockTileset.getSubImage(g, 48, 128, 16, 16);
        mQuestionBlock[2] = mBlockTileset.getSubImage(g, 48, 144, 16, 16);
        mQuestionBlock[3] = mBlockTileset.getSubImage(g, 48, 160, 16, 16);         

        // Lava.
        mLava = new IBufferedImage[4];
        mLava[0] = mBlockTileset.getSubImage(g, 64, 112, 16, 16);
        mLava[1] = mBlockTileset.getSubImage(g, 64, 128, 16, 16);
        mLava[2] = mBlockTileset.getSubImage(g, 64, 144, 16, 16);
        mLava[3] = mBlockTileset.getSubImage(g, 64, 160, 16, 16);            
        
        // Inivisible block.
        mBlockTile[40] = mBlockTileset.getSubImage(g, 32, 96, 16, 16);  
        mBlockTile[41] = mBlockTileset.getSubImage(g ,32, 96, 16, 16);
        mBlockTile[45] = mBlockTileset.getSubImage(g ,48, 96, 16, 16);
        
		// Warp pipes.
        for (int i = 50; i < 82; i++) {
            int w = Integer.parseInt(getTileDescription(i, 0)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 0)[4]) * 16;
            int x = Integer.parseInt(getTileDescription(i, 0)[6]);
            int y = Integer.parseInt(getTileDescription(i, 0)[7]);
            mBlockTile[i] = mBlockTileset.getSubImage(g, x, y, w, h);
        }

        // Load the terrain tileset sprite sheet.
        mTerrainTileset = FileIO.getImageResource(g, "obj/terrain/" + mTilesetIndex[1] + "/tileset.png");
    
        // Get the tile images from the sprite sheet.
        mTerrainTile = new IBufferedImage[14];
        for (int i = 1; i < 14; i++) {
        	
        	// Terrain tiles.
            int w = Integer.parseInt(getTileDescription(i, 16)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 16)[4]) * 16;
            int x = Integer.parseInt(getTileDescription(i, 16)[6]);
            int y = Integer.parseInt(getTileDescription(i, 16)[7]);
            mTerrainTile[i] = mTerrainTileset.getSubImage(g, x, y, w, h);
        }

        // Load the scenery tileset sprite sheet.
        mSceneryTileset = FileIO.getImageResource(g, "obj/scenery/" + mTilesetIndex[2] + "/tileset.png");

        // Get the tile images from the sprite sheet.
        mSceneryTile = new IBufferedImage[6];
        for (int i = 1; i < 6; i++) {
            
        	// Scenery tiles.
        	int w = Integer.parseInt(getTileDescription(i, 32)[3]) * 16;
            int h = Integer.parseInt(getTileDescription(i, 32)[4]) * 16;
            int x = Integer.parseInt(getTileDescription(i, 32)[6]);
            int y = Integer.parseInt(getTileDescription(i, 32)[7]);
            mSceneryTile[i] = mSceneryTileset.getSubImage(g, x, y, w, h); 
            
        }
    }

    /**
     * Gets a tile specified tileset.
     * @param tileIndex Index of the tile.
     * @param tilesetIndex Index of the tileset (0-block, 16-terrain, 32-scenery).
     * @param repeatIndex Tile index for tiled terrain.
     * @return Tile image data.
     */
    public IBufferedImage getTile(int tileIndex, int tilesetIndex, int repeatIndex) {

        // Brick, block or coin.
        if (Map.isAnimated(tileIndex, tilesetIndex)) {
            
        	if (tileIndex == 10)
                return mCoin[(int)mFrame];
            else if (tileIndex >= 20 && tileIndex <= 27) 
            	return mBrick[(int)mFrame];
            else if (tileIndex >= 30 && tileIndex <= 37)
            	return mQuestionBlock[(int)mFrame];
            else if (tileIndex == 46)
                return mLava[(int)mFrame];           
        }
        
        if (tilesetIndex == 0)
            return mBlockTile[tileIndex];
        else if (tilesetIndex == 16)
            return mTerrainTile[tileIndex];//.getFrame(tile3);
        else if (tilesetIndex == 32)
            return mSceneryTile[tileIndex];
        else
            return null;
    }
    
    /**
     * Adds a piece of a block explosion.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param direction Direction of the explosion.
     */
    public void setBlockExplode(float x, float y, char direction) {
        
    	// Initialise a explosion.
    	Explosion explosion = new Explosion();
        explosion.x = x;
        explosion.y = y;
        explosion.direction = direction;
        mExplosion.add(explosion);
        
    }

    /**
     * Draws a tile.
     * @param gl Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param cam Camera coordinates.
     * @param tile Tile object.
     */
	public void draw(Object gl, int x, int y, Point cam, Tile tile) {

		// Tile should be drawn.
		if (tile.tileIndex != 0 && tile.draw == 1) {
			
			// Calculate coordinates.
			x = (x * 16) - (int)cam.x;
			y = (y * 16 - tile.offset) - (int)cam.y;

			// Tile has been hit.
			if (tile.state == 1) {
				
				// Make invisible tile visible.
				if (Map.isInvisible(tile.tileIndex, tile.tilesetIndex))
					tile.collision = 1;
			
				// Set tile offset.
				if (tile.offset < 5)
					tile.offset++;
				else {
					
					// Reset tile offset.
					tile.offset = 0;
					tile.state = 0;
					
					// Decrease item count.
					if (tile.count > 0 && tile.count != 99)
						tile.count--;
				
					// Set tile to hit.
					if (tile.count == 0) {
						tile.state = 2;
					}
				}

				// Draw coins above block.
				if (Map.isCoinBlock(tile.tileIndex, tile.tilesetIndex)&& tile.count > 0)
					getTile(10, 0, 0).draw(gl, x, y - 16);
				 else if (Map.is10CoinBlock(tile.tileIndex, tile.tilesetIndex) && tile.count > 0)
					mCoin[(int)mFrame].draw(gl, x, y - 16);

			} else if (tile.state == 2) {
				
				// Set tile index to hit.
				tile.tileIndex = 40;
			}

			// Draw tile.
			if (!Map.isInvisible(tile.tileIndex, tile.tilesetIndex))
				getTile(tile.tileIndex, tile.tilesetIndex, 0).draw(gl, x, y);
			else {
				
				// Draw invisible tile if hit.
				if (tile.state == 2)
					getTile(tile.tileIndex, tile.tilesetIndex, 0).draw(gl, x, y);
			}
		}

	}

    /**
     * Draws explosions.
     * @param g Graphics context.
     * @param map Map object.
     * @param cam Camera coordinates.
     */
    public void drawExplosions(Object g, Map map, Point cam) {
        
    	// Loop though explosions.
    	for (int i = 0; i < mExplosion.size(); i++) {
        	
    		// Increase timer.
    		mExplosion.get(i).timer++;
    		
    		// Left jump.
            if (mExplosion.get(i).y < map.getHeight()) {
                
            	if (mExplosion.get(i).direction == 'r') {
                                		
            		mExplosion.get(i).x+=2;
                    if (mExplosion.get(i).timer < 7)
                        mExplosion.get(i).y-=2;
                    else {
                    	
                    	// Fall.
                        if (mExplosion.get(i).y < map.getHeight())
                            mExplosion.get(i).y += 5;
                        else
                            mExplosion.remove(i);
           
                    }
                } else {
                	
                	// Right jump.
                    mExplosion.get(i).x-=2;
                    if (mExplosion.get(i).timer < 7)
                        mExplosion.get(i).y-=2;
                    else {
                        
                    	// Fall.
                    	if (mExplosion.get(i).y < map.getHeight())
                            mExplosion.get(i).y += 5;
                        else 
                            mExplosion.remove(i);
                       
                    }
                }
            	
            	// Draw explosion.
                mSmashedBlock[(int)mFrame].draw(g, mExplosion.get(i).x - cam.x, mExplosion.get(i).y - cam.y);
            }
        }
    }

    /**
     * Draws the level map.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param map Map object.
     * @param cam Camera coordinates.
     */
    public void drawMap(Object g, float dt, Map map, Point cam) {
        
    	// Set animation frame.
    	if(!Settings.Paused && Settings.Animation) {
    		
    		if(mFrameTimer + dt < 350)
    			mFrameTimer += dt;
    		else {
    			
    			// Advance frame.
    			if(mFrame + 1 < 4)
    				mFrame++;
    			else
    				mFrame = 0;
    			
    			// Reset frame timer.
    			mFrameTimer = 0;
    			
    		}
    	}

        // Draw tiles in screen area.
        for (int x = (int)(cam.x - 100) / 16; x < (cam.x + Settings.ScreenWidth) / 16; x++) {
            for (int y = (int)(cam.y - 100) / 16; y < (cam.y + Settings.ScreenHeight) / 16; y++) {
                Tile tile = map.getTile(x * 16, y * 16);
               	if(tile.tileIndex != 46)
               		draw(g, x, y, cam, tile);
            }
        }
    }
        
    /**
     * Draws the lava from the level map.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param map Map object.
     * @param cam Camera coordinates.
     */
    public void drawLavaMap(Object g, float dt, Map map, Point cam) {

        // Draw tiles in screen area.
        for (int x = (int)(cam.x - 100) / 16; x < (cam.x + Settings.ScreenWidth) / 16; x++) {
            for (int y = (int)(cam.y - 100) / 16; y < (cam.y + Settings.ScreenHeight) / 16; y++) {
                Tile tile = map.getTile(x * 16, y * 16);
               	if(tile.tileIndex == 46)
               		draw(g, x, y, cam, tile);
            }
        }
    }

    /**
     * Draws the warp pipes from the level map.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param map Map object.
     * @param cam Camera coordinates.
     */
    public void drawPipeMap(Object g, float dt, Map map, Point cam) {
        // Draw tiles in screen area.
        for (int x = (int)(cam.x - 100) / 16; x < (cam.x + Settings.ScreenWidth) / 16; x++) {
            for (int y = (int)(cam.y - 100) / 16; y < (cam.y + Settings.ScreenHeight) / 16; y++) {
                Tile tile = map.getTile(x * 16, y * 16);
               	if(tile.tilesetIndex == 0 && tile.tileIndex >= 50 && tile.tileIndex <= 81)
               		draw(g, x, y, cam, tile);
            }
        }
    }

    /**
     * Draws the level map with items displayed above blocks.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     * @param main Main game thread.
     * @param map Map object.
     * @param cam Camera coordinates.
     */
    public void drawEditorMap(Object g, float dt, Main main, Map map, Point cam) {
        
    	// Set animation frame.
    	if(!Settings.Paused)
    	{
    		if(mFrameTimer + dt < 350)
    			mFrameTimer += dt;
    		else {
    			
    			// Advance frame.
    			if(mFrame + 1 < 4)
    				mFrame += 1;
    			else
    				mFrame = 0;
    			
    			// Reset frame timer.
    			mFrameTimer = 0;
    		}
    	}

        // Draw tiles in screen area.
    	for (int x = (int)(cam.x - 100) / 16; x < (cam.x + Settings.ScreenWidth) / 16; x++) {
            
    		for (int y = (int)(cam.y - 100) / 16; y < (cam.y + Settings.ScreenHeight) / 16; y++) {
                
    			// Get the map tile.
    			Tile tile = map.getTile(x * 16, y * 16);
                
    			// Set the tile coordinates.
    			int tileX = x * 16 - (int)cam.x;
    			int tileY = y * 16 - 16 - (int)cam.y;
    			
    			// Draw items above tiles.
    			if (Map.isCoinBlock(tile.tileIndex, tile.tilesetIndex)) {
                      
    				// Coin.
    				drawAnimatedFrame(g, tileX, tileY, 10, 0, 0);

                } else if (Map.is10CoinBlock(tile.tileIndex, tile.tilesetIndex)) {
                      
                	// 10 coins.
    				drawFrame(g, tileX, tileY, 10, 0, 0, 3);
                }		
                else if (Map.isMushroomBlock(tile.tileIndex, tile.tilesetIndex)) {
					
					// Mushroom.
					main.getEntity(4).draw(g, dt, tileX, tileY, 'l');
					
				} else if (Map.isFireBlock(tile.tileIndex, tile.tilesetIndex)) {
				     
					// Fire flower.
					main.getEntity(5).draw(g, dt, tileX, tileY, 'l');

				} else if (Map.isInvincibleBlock(tile.tileIndex, tile.tilesetIndex)) {
					
					// Star.
					main.getEntity(6).draw(g, dt, tileX, tileY, 'l');
					
				} else if (Map.isExtraLifeBlock(tile.tileIndex, tile.tilesetIndex)) {
				     
					// Extra life.
					main.getEntity(7).draw(g, dt, tileX, tileY, 'l');

				} else if (Map.isVineBlock(tile.tileIndex, tile.tilesetIndex)) {

					// Vine.
					main.getEntity(8).draw(g, dt, tileX, tileY, 'l');
				} 
                               
                // Draw the tile.
                draw(g, x, y, cam, tile);
                
            }
        }
    }  
    
    /**
     * Draws a tile from the tileset.
     * @param g Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param tile Tile index.
     * @param tileset Tileset index.
     * @param tiled Tiled terrain index.
     * @param frame Frame index.
     */
    public void drawFrame(Object g, int x, int y, int tile, int tileset, int tiled, int frame) {

        if (Map.isAnimated(tile, tileset)) {
        	
        	// Draw animated brick, block or coin tile.
            if (tile == 10) {
            	
            	// Draw coin.
                mCoin[frame].draw(g, x, y);
                
            } else if (tile >= 20 && tile <= 27) {
            	
            	// Draw brick.
            	mBrick[mFrame].draw(g, x, y);
            	
            } else if (tile >= 30 && tile <= 37)  {

            	// Draw ? block.
                mQuestionBlock[mFrame].draw(g, x, y);
            } else if (tile == 42)  {

            	// Draw lava.
                mLava[mFrame].draw(g, x, y);
            } 
        	
        }
        else if(tileset == 0) {
        	
        	// Draw tile.
        	mBlockTile[tile].draw(g, x, y);
        	
        } else if(tileset == 16) {
        	
        	// Draw terrain.
        	if(tile == 2 || tile == 4 || tile == 5 || tile == 6 || tile == 8)
        		
        		// Terrain with repeated tiles.
        		mTerrainTile[tile].draw(g, x, y);
        	else
        		mTerrainTile[tile].draw(g, x, y);      	

        } else if(tileset == 32) {
        	
        	// Scenery.
        	mSceneryTile[tile].draw(g, x, y);
        }

    }    
    
    /**
     * Draws an animated tile from the tileset.
     * @param g Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param tile Tile index.
     * @param tileset Tileset index.
     * @param tiled Tiled terrain index.
     */
    public void drawAnimatedFrame(Object g, int x, int y, int tile, int tileset, int tiled) {
    	drawFrame(g, x, y, tile, tileset, tiled, mFrame);
    }
    
    /**
     * Initialise the tile definitions.
     * @throws IOException 
     */
    private void initDefinitions() throws IOException {
        
    	mTileDefinitions = new ArrayList<String>();
        BufferedReader br = null;
        String ln = null;
        
        // Tileset 0 (block).
        try {
            
        	// Open buffered reader.
        	br = FileIO.openBufferedReader("obj/block/" + mTilesetIndex[0] + "/tileset.ini");
            
        	// Read tile definitions.
        	while ((ln = br.readLine()) != null)
                mTileDefinitions.add(ln);

        } finally {
        	
        	// Close buffered reader.
            if (br != null)
            	br.close();
        }
        
        // Tileset 16 (terrain).
        try {
	    	// Open buffered reader.
	    	br = FileIO.openBufferedReader("obj/terrain/" + mTilesetIndex[1] + "/tileset.ini");
	        
	    	// Read tile definitions.
	    	while ((ln = br.readLine()) != null)
	            mTileDefinitions.add(ln);
	    	
        } finally {
        	
        	// Close buffered reader.
            if (br != null)
            	br.close();
        }
	
        // Tileset 32 (scenery).
        try {
	    	// Open buffered reader.
	    	br = FileIO.openBufferedReader("obj/scenery/" + mTilesetIndex[2] + "/tileset.ini");
	        
	    	// Read tile definitions.
	    	while ((ln = br.readLine()) != null)
	            mTileDefinitions.add(ln);
	    	
        } finally {
        	
        	// Close buffered reader.
            if (br != null)
            	br.close();
        }      

    }

    /**
     * Gets a tile description,.
     * @param tileIndex Tile index.
     * @param tilesetIndex Tileset index (0-block, 16-object 32-scenery).
     * @return String array of tile description. 
     * [0]-index [1]-tileset [3]-name [4]-width [5]-height [6]-collision flag [7]-tileX [8]-tileY
     */
    public String[] getTileDescription(int tileIndex, int tilesetIndex) {

        String[] desc = new String[5];
        
        // Loop through tile definitions.
        for (int i = 0; i < mTileDefinitions.size(); i++) {

        	// Find tile description.
            desc = mTileDefinitions.get(i).split(",");
            if (desc[0].equals(Integer.toString(tileIndex)) && desc[1].equals(Integer.toString(tilesetIndex)))
                return desc;
        }
        
        desc[2] = "Null";
        return desc;
    }

    /**
     * Destroys the resources.
     * @param gl Graphics context.
     */
    public void destroy(GL10 gl)
    {
        // Destroy block tileset.
        if(mBlockTileset != null)
        	mBlockTileset.destroy(gl);
        
        // Destroy block tiles.
        for (int i = 0; i < 83; i++) {
        	if(mBlockTile[i] != null)
        		mBlockTile[i].destroy(gl);
        }
        
        // Destroy smashed block tiles.
        for (int i = 0; i < 4; i++) {
        	if(mSmashedBlock[i] != null)
        		mSmashedBlock[i].destroy(gl);
        }
        
        // Destroy coin tiles.        
        for (int i = 0; i < 4; i++) {
        	if(mCoin[i] != null)
        		mCoin[i].destroy(gl);
        }
                
        // Destroy brick tiles.
        for (int i = 0; i < 4; i++) {
        	if(mBrick[i] != null)
        		mBrick[i].destroy(gl);
        }
        
        // Destroy question block tiles.
        for (int i = 0; i < 4; i++) {
        	if(mQuestionBlock[i] != null)
        		mQuestionBlock[i].destroy(gl);
        }
        
        // Destroy lava tiles.
        for (int i = 0; i < 4; i++) {
        	if(mLava[i] != null)
        		mLava[i].destroy(gl);
        }

        // Destroy terrain tileset.
    	if(mTerrainTileset != null)
    		mTerrainTileset.destroy(gl);
    	
    	// Destroy terrain tiles.
        for (int i = 1; i < 14; i++) {
        	if(mTerrainTile[i] != null)
        		mTerrainTile[i].destroy(gl);
        }
                
        // Destroy scenery tileset.
        if(mSceneryTileset != null)
        	mSceneryTileset.destroy(gl);
        
        // Destroy scenery tiles.
        for (int i = 1; i < 6; i++) {
        	if(mSceneryTile[i] != null)
        		mSceneryTile[i].destroy(gl);
        }
        
    }

}