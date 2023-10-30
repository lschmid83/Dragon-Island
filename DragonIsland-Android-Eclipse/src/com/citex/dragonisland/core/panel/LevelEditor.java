package com.citex.dragonisland.core.panel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.citex.dragonisland.core.drawing.Background;
import com.citex.dragonisland.core.drawing.Color;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.core.level.Header;
import com.citex.dragonisland.core.level.Map;
import com.citex.dragonisland.core.sprite.entity.EntityDescription;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.tileset.Tile;
import com.citex.dragonisland.core.tileset.TileDescription;
import com.citex.dragonisland.core.tileset.Tileset;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.java.GamePanel;

/** 
 * This class displays the level editor.
 * @author Lawrence Schmid
 */
public class LevelEditor extends Timing {

	/** Main thread. */
    private Main mMain;	
    
    /** Level information. */
    private Level mLevel;
    
    /** Editor state. */
    private String mState;
    
    /** Number of options in a menu. */
    private int mOptions;
    
    /** Selected option in a menu. */
    private int mSelectedOption;  
            
    /** Background. */
    private Background mBackground;
    
    /** Tileset. */
    private Tileset mTileset;
    
    /** Editor state. 0-Tile Selection, 1-Terrain, 2-Block, 3-Scenery, 4-Entities */
    private int mEditorState;
    
    /** Description string of the current selection. */
    private String mSelectionDescription;
    
    /** Map tile information. */
    private Tile mTile;
    
    /** Mouse coordinates. */
    private Point mMouse;
    
    /** Mouse coordinates. */
    private Point mMouseDragged;
    
    /** Selected tile. */
    private Rectangle mMouseTile;
        
    /** Mouse cursor rectangle. */
    private Rectangle mMouseCursor;
    
    /** Mouse selection rectangle. */
    private Rectangle mMouseSelection;
    
    /** Number of times the mouse has been clicked. */
    private int mMouseClick;
    
    /** Time elapsed while saving level data. */
    private float mSavingTimer;
        
    /** Show screen transition. */
    private boolean mTransition;
    
    /**
	 * Initialises a Editor object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
     * @throws IOException 
	 */
	public LevelEditor(Main main, Object surface, String levelPath, boolean transition) throws IOException {
		
		// Initialise the level editor.
		mMain = main;
		mMain.setCamera(new Point(0, 0));
    	mTransition = transition;
        mMouse = new Point(0,0);
        mMouseDragged = new Point(0,0);
        mMouseSelection = new Rectangle();
        mTile = new Tile();
    	mMouseTile = new Rectangle(0, 0, 16, 16);
        mMouseTile.setLocation(-100, -100);
        mMouseCursor = new Rectangle(0, 0, 5, 5);	
    	mMain.getControls().reset();
       	
    	// Set the game state.
    	Settings.State = "editor";	

    	// Load the level data.
		loadLevel(levelPath);

	}
		
	/**
	 * Paints the graphics.
	 * @param g Graphics context.
	 * @param dt Delta time between frame updates.
	 */
    public void paintComponent(Graphics2D g, float dt) {

		// Get camera coordinates.
		Point cam = mMain.getCamera();
  	
    	// Draw background.
    	cam = mBackground.draw(g, dt, cam);

    	// Draw level map tiles.
    	mTileset.drawEditorMap(g, dt, mMain, mLevel.getMap(), cam);

    	// Loop through entities.  	
    	for(EntityDescription entity : mLevel.getEntityDescriptions()) {
    		
    		// Draw entity.
  			mMain.getEntity(entity.tile).draw(g, dt, entity.x * 16 - (int)cam.x, entity.y * 16 - (int)cam.y, entity.direction, entity.angle);
    	}
    	
    	// Animate entities.
    	for(IEntity entity : mMain.getEntities()) {
    		if(entity != null)
    			entity.animate(dt);		
    	}
    	
    	// Draw grid.
   		drawGrid(g);
   		
    	// Editor mode.
    	if (mState == "editor") {
       
    		// Set red colour.
    		g.setColor(new java.awt.Color(255, 1, 1, 120));    		
    		
       		// Select tile.
            if (mEditorState == 0) {

            	// Draw mouse over tile.
                g.draw(mMouseTile);
                
            } else if (mEditorState > 0 && mEditorState < 4) {
                
            	if (Map.isTiledTerrain(mTile.tileIndex, mTile.tilesetIndex)) {
                    
            		// Tiled terrain.
            		mTileset.drawAnimatedFrame(g, (int)mMouseTile.getX(), (int)mMouseTile.getY(), mTile.tileIndex, mTile.tilesetIndex, mTile.repeatIndex);
                
            	} else {
                    
            		if (Map.isCoinBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                       
            			// Draw coin.
            			mTileset.drawAnimatedFrame(g, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 10, 0, 0);
            			
                    } else if (Map.is10CoinBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                       
                    	// Draw 10 coins.
                    	mTileset.drawFrame(g, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 10, 0, 0, 3);
                    
                    } else if (Map.isMushroomBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                        
                    	// Draw mushroom.
                    	mMain.getEntity(4).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 'l');
                    
                    } else if (Map.isFireBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                        
                    	// Draw fire flower.
                    	mMain.getEntity(5).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 'l');
                    
                    } else if (Map.isInvincibleBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                        
                    	// Draw star.
                    	mMain.getEntity(6).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 'l');
                   
                    } else if (Map.isExtraLifeBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                        
                    	// Draw extra life.
                    	mMain.getEntity(7).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 'l');

                    } else if (Map.isVineBlock(mTile.tileIndex, mTile.tilesetIndex)) {
                        
                    	// Draw vine.
                    	mMain.getEntity(7).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY() - 16, 'l');
                    }
            		
            		// Draw blocks, terrain and scenery.
                    mSelectionDescription = mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[2];
            		if(mSelectionDescription != "Null")
            			mTileset.drawAnimatedFrame(g, (int)mMouseTile.getX(), (int)mMouseTile.getY(), mTile.tileIndex, mTile.tilesetIndex, mTile.repeatIndex);
            	}
            }
            else if (mEditorState == 4) {
        		
            	// Draw entity selection.
        		if(mTile.tileIndex == 9)
        			mMain.getEntity(mTile.tileIndex).draw(g, dt, (int)mMouseTile.getX(), (int) mMouseTile.getY(), mTile.direction, mTile.angle);
        		else
        			mMain.getEntity(mTile.tileIndex).draw(g, dt, (int)mMouseTile.getX(), (int) mMouseTile.getY(), mTile.direction, 0);
        			
                // Draw castle and flag pole.
                if (mSelectionDescription.equals("Flag Pole") && mEditorState == 4)
                {
                    // Flag.
                    mMain.getEntity(2).draw(g, dt, (int)mMouseTile.getX() - (1 * 16), (int) mMouseTile.getY(), 'l');

                    // Flag pole.
                    mMain.getEntity(3).draw(g, dt, (int)mMouseTile.getX(), (int)mMouseTile.getY(), 'l');
                                        
                    // Flag pole base.
                    mTileset.drawAnimatedFrame(g, (int)mMouseTile.getX(), (int)mMouseTile.getY() + (9 * 16), 3, 0, 0);
                    
                    // Castle flag.
                    mMain.getEntity(1).draw(g, dt, (int) mMouseTile.getX() + (10 * 16), (int) mMouseTile.getY() + (1 * 16), 'l');
                                        
                    // Castle.
                    mTileset.drawAnimatedFrame(g, (int)mMouseTile.getX() + (8 * 16), (int)mMouseTile.getY() + (4 * 16), 1, 0, 0);

                }            
            }

            // Draw mouse selection rectangle.
            g.drawRect((int)mMouseSelection.getX() * 16 - (int)cam.x, (int)mMouseSelection.getY() * 16 - (int)cam.y,
                       (int)mMouseSelection.getWidth() * 16, (int)mMouseSelection.getHeight() * 16);

            // Draw mouse cursor.
            g.fillRect(mMouseCursor.x, mMouseCursor.y, 5, 5);
                        
            // Draw selected tile description and mouse coordinates.
            GameFont font = mMain.getGameFont(2);
            if (mSelectionDescription == null || mSelectionDescription.equals("Select Object")) {
                
            	// Instructions.
            	font.drawString(g, "x-" + ((int)mMouse.x + 1) + " y-" + ((int)mMouse.y + 1), 5, 6);
                font.drawString(g, "Left Button   - Select Object", 61, 6);
                font.drawString(g, "Right Button  - Remove Object", 61, 17);
                font.drawString(g, "Middle Button", 61, 28);
                font.drawString(g, "- Select Tileset", 135, 28);
            } else {
                font.drawString(g, "x-" + ((int)mMouse.x + 1) + " y-" + ((int)mMouse.y + 1) + " " + mSelectionDescription, 5, 6);
            }

                   
        }
    	else if (mState == "settings") {	
    		drawSettings(g);
    	} else if(mState == "background -> rgb") {
    		drawBackgroundRGB(g);		
    	} else if(mState == "background -> speed") {
    		drawBackgroundSpeed(g);		
    	} else if(mState == "pause") {
    		drawPaused(g);
    	} else if(mState == "saving") {
    		drawSaving(g, dt);    		
    	}
    	
    	// Get the font and level header.
        GameFont font = mMain.getGameFont(2);
        Header header = mLevel.getHeader();  
        
        // Set the start position.
        int startX = header.startX * 16 - (int)cam.x;
        int startY = header.startY * 16 - (int)cam.y;        
        
        // Draw position.
        mMain.getPlayers().get(0).draw(g, dt, startX + 2, startY, 'r');
        font.drawString(g, "Start", startX + 5, startY - 17);           
        font.drawString(g, getStateDesc(header.startState), startX - font.getStringWidth(getStateDesc(header.startState)) / 2 + 17, startY - 5);
  
        // End position.
        if (header.endX != 0 && header.endY != 0) {

        	// Set the end position.
        	int endX = header.endX * 16 - (int)cam.x;
        	int endY = header.endY * 16 - (int)cam.y;
            
        	// Draw position.
        	mMain.getPlayers().get(0).draw(g, dt, endX + 2, endY, 'r');
            font.drawString(g, "End", endX + 7, endY - 17);  
            font.drawString(g, getStateDesc(header.endState), endX - font.getStringWidth(getStateDesc(header.endState)) / 2 + 14, endY - 5);
        }

        // Bonus position.
        if (header.bonusX != 0 && header.bonusY != 0) {
           
        	// Set the bonus position.
        	int bonusX = header.bonusX * 16 - (int)cam.x;
        	int bonusY = header.bonusY * 16 - (int)cam.y;	
            
        	// Draw position.
        	mMain.getPlayers().get(0).draw(g, dt, bonusX + 2, bonusY, 'r');
            font.drawString(g, "Bonus", bonusX + 2, bonusY - 17);  
            font.drawString(g, getStateDesc(header.bonusState), bonusX - font.getStringWidth(getStateDesc(header.bonusState)) / 2 + 15, bonusY - 5);
        } 
    	
    	// Draw debug output.
		if(Settings.DebugMode) {
			mMain.getDebug().draw(g, 5, 5);	
		}

    }

    
    /**
     * Draw the current frame.
     * @param g Graphics context.
     */
    public void drawFrame(Graphics g) {
    	
    	// Set the current time.
    	setCurrentTime(System.currentTimeMillis());

    	// Calculate the delta time.
    	float dt = getDeltaTime();
		    		
		// Set the background colour.
		if(mMain.getScreenTransition().getTimer() > 0) {
    		
			Color color = mBackground.getColor();
        	g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
			g.fillRect(0, 0, Settings.ScreenWidth, Settings.ScreenHeight);

			// Paint the component.
    		paintComponent((Graphics2D)g, dt);
		}
			    		
    	// Draw screen transition.
    	if(mTransition) {
    		
    		// Stop drawing the transition if it is finished.
    		if(mMain.getScreenTransition().isFinished()) {
    			mTransition = false;    	
    		}
    		
        	// Fill the screen below the transition graphic.
        	g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, (int)mMain.getScreenTransition().getTimer() + 32, 480, 272);    		
    		
            // Draw transition.
    		mMain.getScreenTransition().draw(g, dt);
    	}	
		
        // Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	    
 	
    }  
    
    
    /**
     * Called when a key was pressed down and not handled by any of the views inside of the activity.
     * @param event Description of the key event.
     */
    public void onKeyDown(int keyCode, boolean pressed) {
    	
    	// Only handle key pressed events.
       	if(!pressed)
       		return;
       	
    	// Get camera.
    	Point cam = mMain.getCamera();
    	int angle = 0;
    	char dir = 'l';
    	
     	if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
    		
    		if(mState == "settings") {

    			// Get the level header.
    			Header header = mLevel.getHeader();
    			
    			// Set min / max values for selected option.
                int value = 0;
                float bgSpeedValue = 0;
                int minOption;
                int maxOption;
                switch (mSelectedOption) {
                    case 0: value = header.world; minOption = 0; maxOption = 99; break;
                    case 1: value = header.level; minOption = 0; maxOption = 99; break;
                    case 2: value = header.area; minOption = 0; maxOption = 99; break;
                    case 3: value = header.bg[0]; minOption = 0; maxOption = FileIO.getAssetsFileList("bgr/near","png").size() -1; break;
                    case 4: value = header.bg[1]; minOption = 0; maxOption = FileIO.getAssetsFileList("bgr/middle", "png").size() -1; break;
                    case 5: value = header.bg[2]; minOption = 0; maxOption = FileIO.getAssetsFileList("bgr/far", "png").size() -1; break;
                    case 7: value = header.bgAlign; minOption = 0; maxOption = 1; break;
                    case 8: value = header.tileset0; minOption = 1; maxOption = FileIO.getFolderList(Settings.InternalStorageFolder + "obj/block/").size(); break;
                    case 9: value = header.tileset16; minOption = 1; maxOption = FileIO.getFolderList(Settings.InternalStorageFolder + "obj/terrain/").size(); break;
                    case 10: value = header.tileset32; minOption = 1; maxOption = FileIO.getFolderList(Settings.InternalStorageFolder + "obj/scenery/").size(); break;
                    case 11: value = header.music; minOption = 0; maxOption = FileIO.getAssetsFileList("snd/","mp3").size(); break;
                    case 12: value = header.width / 16; minOption = 30; maxOption = 999; break;
                    case 13: value = header.height / 16; minOption = 17; maxOption = 999; break;
                    case 14: value = header.startX; minOption = 0; maxOption = header.width; break;
                    case 15: value = header.startY; minOption = 0; maxOption = header.height; break;
                    case 16: value = header.endWorld; minOption = 0; maxOption = 99; break;
                    case 17: value = header.endLevel; minOption = 0; maxOption = 99; break;
                    case 18: value = header.endArea; minOption = 0; maxOption = 99; break;
                    case 19: value = header.timeLimit; minOption = 0; maxOption = 999; break;
                    case 20: value = header.endX; minOption = 0; maxOption = header.width; break;
                    case 21: value = header.endY; minOption = 0; maxOption = header.height; break;
                    case 22: value = header.bonusX; minOption = 0; maxOption = header.width; break;
                    case 23: value = header.bonusY; minOption = 0; maxOption = header.height; break;
                    default: value = 0; minOption = 0; maxOption = 0; break;
                }
                
                // Set value.
                if (keyCode == KeyEvent.VK_LEFT && value > minOption) {
                    value--;
                } else if (keyCode == KeyEvent.VK_RIGHT && value < maxOption) {
                    value++;
                }

                switch (mSelectedOption) {
                    case 0: header.world = value; break;
                    case 1: header.level = value; break;
                    case 2: header.area = value; break;
                    case 3: header.bg[0] = value; setBackground(header); break;
                    case 4: header.bg[1] = value; setBackground(header); break;
                    case 5: header.bg[2] = value; setBackground(header); break;
                    case 6: break; 
                    case 7: header.bgAlign = value; mBackground.setAlign(value); break;
                    case 8: header.tileset0 = value; setTileset(header); break;
                    case 9: header.tileset16 = value; setTileset(header); break;
                    case 10: header.tileset32 = value; setTileset(header); break;
                    case 11: header.music = value; mMain.getMusicPlayer().stop(); mMain.getMusicPlayer().play("snd/" + header.music + ".mp3"); break;
                    case 12: header.width = value * 16; mBackground.setWidth(value * 16); break;
                    case 13: header.height = value * 16; try { mBackground.setHeight(value * 16); } catch(Exception e) {}; break;
                    case 14: header.startX = value; break;
                    case 15: header.startY = value; break;
                    case 16: header.endWorld = value; break;
                    case 17: header.endLevel = value; break;
                    case 18: header.endArea = value; break;
                    case 19: header.timeLimit = value; break;
                    case 20: header.endX = value; break;
                    case 21: header.endY = value; break;
                    case 22: header.bonusX = value; break;
                    case 23: header.bonusY = value; break;
                    default: value = 0; break;
                }

            } else if(mState == "background -> rgb") {

    			// Get the level header.
    			Header header = mLevel.getHeader();
            	
       			// Set min / max values for selected option.
                int value;
            	int minOption;
                int maxOption;
                switch (mSelectedOption) {
                    case 0: value = header.bgColor.getRed(); minOption = 0; maxOption = 255; break;
                    case 1: value = header.bgColor.getGreen(); minOption = 0; maxOption = 255; break;
                    case 2: value = header.bgColor.getBlue(); minOption = 0; maxOption = 255; break;
                    default: value = 0; minOption = 0; maxOption = 0; break;
                }
                
                // Set value.
                if (keyCode == KeyEvent.VK_LEFT && value > minOption) {
                    value--;
                } else if (keyCode == KeyEvent.VK_RIGHT && value < maxOption) {
                    value++;
                }

                switch (mSelectedOption) {
                    case 0: header.bgColor = new Color(value, header.bgColor.getGreen(), header.bgColor.getBlue()); break;
                    case 1: header.bgColor = new Color(header.bgColor.getRed(), value, header.bgColor.getBlue()); break;
                    case 2: header.bgColor = new Color(header.bgColor.getRed(), header.bgColor.getGreen(), value); break;
                    default: value = 0; break;
                }
                
                mBackground.setColor(header.bgColor);
                
            } else if(mState == "background -> speed") {
            	
    			// Get the level header.
    			Header header = mLevel.getHeader();
            	
       			// Set min / max values for selected option.            	
                float bgSpeedValue = 0;
                int minOption;
                int maxOption;
                switch (mSelectedOption)
                {
                    case 0: bgSpeedValue = header.bgSpeed[0];  minOption = 0; maxOption = 5; break;
                    case 1: bgSpeedValue = header.bgSpeed[1];  minOption = 0; maxOption = 5; break;
                    case 2: bgSpeedValue = header.bgSpeed[2]; minOption = 0; maxOption = 5; break;
                    default: bgSpeedValue = 0; minOption = 0; maxOption = 0;break;
                }
                
                // Set value.
                if (keyCode == KeyEvent.VK_LEFT && bgSpeedValue > minOption) {
                	bgSpeedValue -= 0.2f;
                } else if (keyCode == KeyEvent.VK_RIGHT && bgSpeedValue < maxOption) {
                	bgSpeedValue +=0.2f;
                }

                switch (mSelectedOption) {
                    case 0: header.bgSpeed[0] = bgSpeedValue; mBackground.setLayerSpeed(0, bgSpeedValue); break;
                    case 1: header.bgSpeed[1] = bgSpeedValue; mBackground.setLayerSpeed(1, bgSpeedValue); break;
                    case 2: header.bgSpeed[2] = bgSpeedValue; mBackground.setLayerSpeed(2, bgSpeedValue); break;
                    default: bgSpeedValue = 0; break;
                }

            }  		
	
    	}
    	    	
        if (keyCode == KeyEvent.VK_LEFT) {
            
        	if(mState == "editor") {
	        	// Move camera left.
	        	if(cam.x - 16 >= 0)
	        		cam.x -= 16;
        	}
            
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
            
        	if(mState == "editor") {
	        	// Move camera right.
	        	if(cam.x + 16 <= mLevel.getHeader().width - Settings.ScreenWidth)
	        		cam.x += 16;
        	}

        } else if (keyCode == KeyEvent.VK_UP) {
           
        	if(mState == "editor") {
	        	
        		// Move camera up.
	        	if(cam.y - 16 >= 0)
	        		cam.y -= 16;
        	
        	} else {
        	
	    		// Previous menu item.
	    		if (mSelectedOption > 0)
	    			mSelectedOption--;   
        	}
            
        } else if (keyCode == KeyEvent.VK_DOWN) {
        	
        	if(mState == "editor") {
        	
	        	// Move camera down.
	        	if(cam.y + 16 <= mLevel.getHeader().height - Settings.ScreenHeight)
	        		cam.y += 16;
        	
        	} else {
        		
        		// Next menu item.
      			if (mSelectedOption < mOptions - 1) 
      				mSelectedOption++;   
      			
        	}
            
        } else if(keyCode == KeyEvent.VK_ENTER) {
        
        	if (mState == "editor") {
        		
        		// Pause the editor.
                mSelectedOption = 0;
                mState = "pause";
                Settings.Paused = true;
                
        	} else if (mState == "settings") {
    			    			
                if (mSelectedOption == 5) {
                	
                	// Background RGB color.
                    if (mLevel.getHeader().bg[2] == 0) {
                        mState = "background -> rgb";
                        mSelectedOption = 0;
                    }
                    
                } else if (mSelectedOption == 6) {
                	
                	// Background speed.
                    mState = "background -> speed";
                    mSelectedOption = 0;
                    
                } else if (mSelectedOption == 24) {
                	
                	// Return to level editor.
                    mState = "editor";
                    mSelectedOption = 0;
                }		
    			
    		} else if(mState == "background -> rgb") {
    			
    			// Return to settings.
                mState = "settings";
                mSelectedOption = 5;
                
    		} else if(mState == "background -> speed") {
    			
    			// Return to settings.
                mState = "settings";
                mSelectedOption = 6;	
                
    		} else if(mState == "pause") {
    			
    			 if (mSelectedOption == 0) {
    				 
    				 // Return to editor.
                     mState = "editor";
                     Settings.Paused = false;
                     
                 } else if (mSelectedOption == 1) {

                	 // Save level.
                     mState = "saving";
                     mSelectedOption = 0;
                     try {
                    	 saveLevel();
                     } catch(Exception e) {
                    	 e.printStackTrace();
                     }

                 } else if (mSelectedOption == 2) {

                	 // Settings.
                     mState = "settings";
                     mSelectedOption = 0;
                     
                 } else if (mSelectedOption == 3) {
                     
                	 // Exit.
                     Settings.State = "title screen";
                     Settings.Paused = false;
                     mSelectedOption = 0;
                 }
    			
    		}
        	
        } else if (keyCode == KeyEvent.VK_F1) {
        	
        	// Change editor state.
            changeEditorState();
            
        } else if (keyCode == KeyEvent.VK_PAGE_UP) {
            
        	// Next object.
        	if (mEditorState > 0 && mEditorState < 4) {
        		
        		// Tile.
                nextObject();
            } else if (mEditorState == 4) {
            	
            	// Entity.
                nextSprite();
            }
        } else if (keyCode == KeyEvent.VK_PAGE_DOWN) {
            
        	// Previous object.
        	if (mEditorState > 0 && mEditorState < 4) {
                
        		// Tile.
        		previousObject();
            } else if (mEditorState == 4) {
            	
            	// Entity.
                previousSprite();
            }
        } else if(keyCode == KeyEvent.VK_ESCAPE) {
        	
       		if(mState == "settings") {
       			mState = "editor"; 			
       		}
        } else if(keyCode == KeyEvent.VK_1)  {
        	angle = -90;
        	dir = 'l';
        } else if(keyCode == KeyEvent.VK_2)  {
        	angle = 90; 
        	dir = 'r';
        } else if(keyCode == KeyEvent.VK_3)  {
        	angle = 0;     	
        } else if(keyCode == KeyEvent.VK_4)  {
        	angle = 180;    	
        }
        
    	if(mTile.tileIndex == 9) {
    		mTile.direction ='l';
    		mTile.angle = angle;
    	} else {
    		mTile.direction = dir;
    		mTile.angle = 0;
    	}
    }

    /**
     * Loads a level file for editing.
     * @param fileName Level file name.
     * @throws IOException 
     */
    public void loadLevel(String fileName) throws IOException {
        
    	// Level file not specified.
    	if(fileName == null) {
    	
    		// Create a new level.
    		mLevel = new Level();
    		mState = "settings";
    		
    	} else {
    		
        	// Initialise the existing level data.
        	mLevel = new Level(mMain.getGameFolder(), fileName);	
        	mState = "editor";
    		
    	}
    	
    	// Initialise music.
	  	mMain.getMusicPlayer().stop();
  		mMain.getMusicPlayer().play("snd/" + mLevel.getHeader().music + ".mp3");
    	
    	// Initialise camera.
    	Point cam = mMain.getCamera();
    	cam.y = mLevel.getHeader().startY * 16 - 224;
    	
    	// Initialise background.
    	setBackground(mLevel.getHeader());
    	
    	// Initialise tileset.
    	setTileset(mLevel.getHeader());
    	
      
    }
        
    /**
     * Saves a level.
     * @throws IOException 
     */
    public void saveLevel() throws IOException {
       
    	// Get the level header.
    	Header header = mLevel.getHeader();
    	
    	// Save the level.
    	FileIO.saveLevel(mMain.getGameFolder(), mLevel, header.world + "." + header.level + "." + header.area + ".lvl");
    	
    } 
        
    /**
     * Sets the background.
     * @param g Graphics context.
     * @param levelHeader LevelHeader object.
     */
    public void setBackground(Header levelHeader) {
            	    	
    	try {
			
        	// Initialise background.
    		mBackground = new Background(null, levelHeader.bg, levelHeader.bgSpeed, levelHeader.bgAlign, levelHeader.width, levelHeader.height);
		
	    	// Far layer is not initialised.
	        if (levelHeader.bg[2] == 0) {
	        	
	        	// Set background color.
	            mBackground.setColor(levelHeader.bgColor);
	            
	        } else {
	        	
	        	// Use default background color.
	            levelHeader.bgColor = mBackground.getColor();
	        }	

    	} catch (IOException e) {
		}
    }
    
    
    /**
     * Sets the tileset.
     * @param g Graphics context.
     * @param levelHeader LevelHeader object.
     */
    public void setTileset(Header levelHeader) {
        
    	
        try {
			mTileset = new Tileset(null, levelHeader.tileset0, levelHeader.tileset16, levelHeader.tileset32);
		} catch (IOException e) {

		}
    }  

    /**
     * Changes the editor state. 0-Tile Selection, 1-Terrain, 2-Block, 3-Scenery, 4-Entities
     */
    public void changeEditorState() {
        
    	// Set the index of the tile.
    	mTile.tileIndex = 1;
        
    	// Change the editor state.
    	if (mEditorState < 4) {
            mEditorState++;
        } else {
            mEditorState = 0;
        }
    	
    	// Set the tileset index.
        switch (mEditorState) {
            case 1: mTile.tilesetIndex = 16; break;
            case 2: mTile.tilesetIndex = 0; mTile.tileIndex = 20; break;
            case 3: mTile.tilesetIndex = 32; break;
        }
        
        // Set the tile description.
        if(mEditorState == 0) {
        	
        	// Tile selection.
        	mSelectionDescription = "Select Object";
        	
        } else if (mEditorState == 4) {
        	
        	// Entity.
            mTile.tileIndex = 4;
            mSelectionDescription = mLevel.getEntityStringDescription(mTile.tileIndex)[1];
            
        } else {
        	
        	// Terrain, block or scenery.
            mSelectionDescription = mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[2];
        }
    }   
         
    
    /**
     * Moves forward to the next object in the tileset.
     */
    public void nextObject() {
           	
        // Loop while tile description is null.
    	mSelectionDescription = "Null";
        while (mSelectionDescription.equals("Null")) {
            
        	// Move to the next tile.
        	if (mTile.tileIndex < 100)
                mTile.tileIndex++;
            else {
                
            	// Reset the tile index.
            	if (mTile.tilesetIndex == 0)
                    mTile.tileIndex = 10;
                else
                    mTile.tileIndex = 0;           
            }
        	
        	// Set the tile description.
            mSelectionDescription = mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[2];
        }
    }

    /**
     * Return to the previous object in the tileset.
     */
    public void previousObject() {
        
        // Set the min tile index for the tileset.
        int minTile = 0;
        if (mTile.tileIndex == 0) {
            minTile = 10;
        }
        
        // Loop while description is null.
        mSelectionDescription = "Null";
        while (mSelectionDescription.equals("Null")) {
            
        	// Move to the previous tile.
        	if (mTile.tileIndex > minTile) {
                mTile.tileIndex--;
            } else {
                mTile.tileIndex = 100;
            }
        	
        	// Set the tileset description.
            mSelectionDescription = mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[2];
        }
    }

    /**
     * Moves forward to the next sprite.
     */
    public void nextSprite() {
        
    	// Loop while description is null.
    	mSelectionDescription = "Null";
        while (mSelectionDescription.equals("Null")) {
            
        	// Move to the next sprite.
        	if (mTile.tileIndex < mMain.getEntities().size()) {
        		mTile.tileIndex++;
        		if(mTile.tileIndex == 9)
        			mTile.direction = 2;
        		mTile.angle = 0;
            } else {
                mTile.tileIndex = 3;
            }
            mSelectionDescription = mLevel.getEntityStringDescription(mTile.tileIndex)[1];
        }
    }

    /**
     * Return to the previous sprite.
     */
    public void previousSprite() {
        
    	// Loop while description is null.
    	mSelectionDescription = "Null";
        while (mSelectionDescription.equals("Null")) {
            
        	// Move to the previous sprite.
        	if (mTile.tileIndex > 3) {
                mTile.tileIndex--;
        		if(mTile.tileIndex == 9)
        			mTile.direction = 2;
        			mTile.angle = 0;
            } else {
                mTile.tileIndex = mMain.getEntities().size();
            }
        	
        	// Set the sprite description.
            mSelectionDescription = mLevel.getEntityStringDescription(mTile.tileIndex)[1];
        }
    }   

    /**
     * Draws a 16x16 grid.
     * @param g The graphics context
     */
    public void drawGrid(Graphics g) {
        
		// Set camera.
		Point cam = mMain.getCamera();	
    	
    	// Set color.
    	g.setColor(new java.awt.Color(255, 255, 255, 50));
        
    	// Draw grid.
    	for (int x = (int)cam.x / 16; x < cam.x + 480 / 16; x++) {
    		
    		// Horizontal.
    		g.drawLine((16 * x) - (int)cam.x, 0, (16 * x) - (int)cam.x, mLevel.getHeader().height);
        }

    	for (int y = (int)cam.y / 16; y < cam.y + 272 / 16; y++) {
			
			// Vertical.
			g.drawLine(0, (16 * y) - (int)cam.y, mLevel.getHeader().width, (16 * y) - (int)cam.y);

		}  	
 	
    }   
    
    /**
     * Draws the settings screen.
     * @param g Graphics context.
     */
    public void drawSettings(Object g) {

    	// Get the level header.
        Header header = mLevel.getHeader();
        
        // Get the font.
        GameFont font = mMain.getGameFont(0);

        // Loop through menu items.       
        mOptions = 25;
        for (int i = 0; i < mOptions; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Draw title.
            font.drawString(g, 1, "Level Settings", 170, 10);
            
            // Draw menu items.
            if (i == 0) {
                
            	// World.
            	font.drawString(g, sel, "World", 20, 50);
                font.drawString(g, sel, Integer.toString(header.world), 118, 50);
                
            } else if (i == 1) {
            	
            	// Level.
                font.drawString(g, sel, "Level", 20, 70);
                font.drawString(g, sel, Integer.toString(header.level), 118, 70);
                
            } else if (i == 2) {
            	
            	// Area.
                font.drawString(g, sel, "Area", 20, 90);
                font.drawString(g, sel, Integer.toString(header.area), 118, 90);
                
            } else if (i == 3) {
            	
            	// Background (near).
                font.drawString(g, sel, "Bg Near", 20, 110);
                font.drawString(g, sel, Integer.toString(header.bg[0]), 118, 110);
                
            } else if (i == 4) {
            	
            	// Background (middle).
                font.drawString(g, sel, "Bg Middle", 20, 130);
                font.drawString(g, sel, Integer.toString(header.bg[1]), 118, 130);
                
            } else if (i == 5) {
            	
            	// Background (far).
                font.drawString(g, sel, "Bg Far", 20, 150);
                
                // Set RGB background color.
                if (header.bg[2] == 0)
                    font.drawString(g, sel, "RGB", 118, 150);
                else
                    font.drawString(g, sel, Integer.toString(header.bg[2]), 118, 150);
                
            } else if (i == 6) {
                
            	// Background speed.
            	font.drawString(g, sel, "Bg Speed", 20, 170);
                //font.drawString(g, sel, Float.toString(header.bgSpeed[0]) + "?" + Float.toString(header.bgSpeed[1]) + "?" + Float.toString(header.bgSpeed[2]), 118, 170);
            
            } else if (i == 7) {
            	
            	// Background alignment.
                font.drawString(g, sel, "Bg Align", 20, 190);
                font.drawString(g, sel, Integer.toString(header.bgAlign), 118, 190);
                
            } else if (i == 8) {
            	
            	// Blocks.
                font.drawString(g, sel, "Blocks", 170, 50);
                font.drawString(g, sel, Integer.toString(header.tileset0), 268, 50);
                
            } else if (i == 9) {
            	
            	// Terrain.
                font.drawString(g, sel, "Terrain", 170, 70);
                font.drawString(g, sel, Integer.toString(header.tileset16), 268, 70);
                
            } else if (i == 10) {
            	
            	// Scenery.
                font.drawString(g, sel, "Scenery", 170, 90);
                font.drawString(g, sel, Integer.toString(header.tileset32), 268, 90);
            } else if (i == 11) {
            	
            	// Music.
                font.drawString(g, sel, "Music", 170, 110);
                font.drawString(g, sel, Integer.toString(header.music), 268, 110);
                
            } else if (i == 12) {
            	
            	// Level width.
                font.drawString(g, sel, "Width", 170, 130);
                font.drawString(g, sel, Integer.toString(header.width / 16), 268, 130);
                
            } else if (i == 13) {
            	
            	// Level height.
                font.drawString(g, sel, "Height", 170, 150);
                font.drawString(g, sel, Integer.toString(header.height / 16), 268, 150);
                
            } else if (i == 14) {
            	
            	// Start X.
                font.drawString(g, sel, "Start X", 170, 170);
                font.drawString(g, sel, Integer.toString(header.startX), 268, 170);
                
            } else if (i == 15) {
            	
            	// Start Y.
                font.drawString(g, sel, "Start Y", 170, 190);
                font.drawString(g, sel, Integer.toString(header.startY), 268, 190);
                
            } else if (i == 16) {
            	
            	// End world.
                font.drawString(g, sel, "End World", 315, 50);
                font.drawString(g, sel, Integer.toString(header.endWorld), 433, 50);
                
            } else if (i == 17) {
            	
            	// End level.
                font.drawString(g, sel, "End Level", 315, 70);
                font.drawString(g, sel, Integer.toString(header.endLevel), 433, 70);
                
            } else if (i == 18) {
            	
            	// End area.
                font.drawString(g, sel, "End Area", 315, 90);
                font.drawString(g, sel, Integer.toString(header.endArea), 433, 90);
                
            } else if (i == 19) {
            	
            	// Time limit.
                font.drawString(g, sel, "Time", 315, 110);
                font.drawString(g, sel, Integer.toString(header.timeLimit), 433, 110);
                
            } else if (i == 20) {
            	
            	// End X.
                font.drawString(g, sel, "End X", 315, 130);
                font.drawString(g, sel, Integer.toString(header.endX), 433, 130);
                
            } else if (i == 21) {
            	
            	// End Y.
                font.drawString(g, sel, "End Y", 315, 150);
                font.drawString(g, sel, Integer.toString(header.endY), 433, 150);
                
            } else if (i == 22) {
            	
            	// Bonus X.
                font.drawString(g, sel, "Bonus X", 315, 170);
                font.drawString(g, sel, Integer.toString(header.bonusX), 433, 170);
                
            } else if (i == 23) {
            	
            	// Bonus Y.
                font.drawString(g, sel, "Bonus Y", 315, 190);
                font.drawString(g, sel, Integer.toString(header.bonusY), 433, 190);
                
            } else if (i == 24) {
            	
            	// OK button.
                font.drawString(g, sel, "Ok", 433, 240);
            }
            
            // Game Folder.
            font.drawString(g, 0, "Game Folder", 20, 220);
            font.drawString(g, 0, mMain.getGameFolder().getName(), 20, 240);
        }

    }
    
    
    /**
     * Draws the background RGB settings screen.
     * @param g Graphics context.
     */
    public void drawBackgroundRGB(Object g) {
    	
    	// Get the level header.
    	Header header = mLevel.getHeader();
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);
        
        // Loop through menu items.
    	mOptions = 4;
        for (int i = 0; i < mOptions; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Draw title.
            font.drawString(g, 1, "Background Color", 163, 75);
                        
            if (i == 0) {
                
            	// Red.
            	font.drawString(g, sel, "Red", 176, 115);
                font.drawString(g, sel, Integer.toString(header.bgColor.getRed()), 274, 115);
                
            } else if (i == 1) {
                
            	// Green.
            	font.drawString(g, sel, "Green", 176, 135);
                font.drawString(g, sel, Integer.toString(header.bgColor.getGreen()), 274, 135);
                
            } else if (i == 2) {
            	
            	// Blue.
                font.drawString(g, sel, "Blue", 176, 155);
                font.drawString(g, sel, Integer.toString(header.bgColor.getBlue()), 274, 155);
                
            } else if (i == 3) {
            	
            	// OK button.
                font.drawString(g, sel, "Ok", 235, 190);
            }
            
        }
    
    }
    
    /**
     * Draws the background layer speed settings screen.
     * @param g Graphics context.
     */
    public void drawBackgroundSpeed(Object g) {
    	
    	// Get the level header.
    	Header header = mLevel.getHeader();
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);
        
        // Loop through menu items.
        mOptions = 4;
        for (int i = 0; i < 26; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Draw title.
            font.drawString(g, 1, "Background Speed", 157, 75);
            
            if (i == 0) {
            	
            	// Near.
                font.drawString(g, sel, "Near", 176, 115);
                font.drawString(g, sel, Float.toString(header.bgSpeed[0]), 274, 115);
            } else if (i == 1) {
            	
            	// Middle.
                font.drawString(g, sel, "Middle", 176, 135);
                font.drawString(g, sel, Float.toString(header.bgSpeed[1]), 274, 135);
                
            } else if (i == 2) {
                
            	// Far.
            	font.drawString(g, sel, "Far", 176, 155);
                font.drawString(g, sel, Float.toString(header.bgSpeed[2]), 274, 155);
            } else if (i == 3) {
                
            	// OK button.
            	font.drawString(g, sel, "Ok", 235, 190);
            }
        }
        
    }
    
    /**
     * Draws the paused screen.
     * @param g Graphics context.
     */
    private void drawPaused(Object g) {
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);

        // Set background position.
        float x = Settings.ScreenWidth / 2  - 111;
        float y = Settings.ScreenHeight / 2 - 60;
                
    	// Draw background.
    	mMain.getImage(8).draw(g, x, y);
    
    	// Set menu position.
		x = Settings.ScreenWidth / 2 - font.getStringWidth("Continue") / 2;
        y += Settings.ScreenHeight / 30 + 11;
    	   	
    	// Loop through menu items.
    	mOptions = 4;   	
        for (int i = 0; i < 4; i++) {
            
        	// Set selected menu item.
        	int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            
            // Draw menu items.
            if (i == 0)
                font.drawString(g, sel, "Continue", x, y);
            else if (i == 1)
                font.drawString(g, sel, "Save", x, y + 20);
            else if (i == 2)
                font.drawString(g, sel, "Options", x, y + 40);
            else if (i == 3)
                font.drawString(g, sel, "Quit", x, y + 60);
        }
           
    }
    
    /**
     * Draws the saving screen.
     * @param g Graphics context.
     * @param dt Delta time between frame updates.
     */
    private void drawSaving(Object g, float dt) {
    	
    	// Get the game font.
        GameFont font = mMain.getGameFont(0);

        // Set background position.
        float x = Settings.ScreenWidth / 2  - 111;
        float y = Settings.ScreenHeight / 2 - 60;
                
    	// Draw background.
    	mMain.getImage(8).draw(g, x, y);
    
    	// Set menu position.
		x = Settings.ScreenWidth / 2 - font.getStringWidth("Saving") / 2;
        y = Settings.ScreenHeight / 2 - 4;

        // Draw saving.
    	font.drawString(g, 1, "Saving", x, y);

    	// Pause while game is saved.
        if (mSavingTimer > 800) {
            
        	//Settings.File.loadFiles(); 
            mState = "pause";
            mSavingTimer = 0;
        }
        mSavingTimer += dt;
    	
    }
        
    /** 
     * Gets the string description of the players pipe state.
     * @param state Player pipe state.
     * @return String description of the players state.
     */
    private String getStateDesc(int state) {
        switch (state) {
            case 0:
                return "Stand Left";
            case 1:
                return "Stand Right";
            case 2:
                return "  Up Pipe ";
            case 3:
                return " Down Pipe ";
            case 4:
                return "Left Pipe";
            case 5:
                return " Right Pipe";
            default:
                return "          ";
        }
    }
    
    /**
     * Invoked when the mouse cursor has been moved onto a component.<br>
     * Sets the mouse, cursor and tile coordinates.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mouseMoved(MouseEvent e) {
        
    	// Get camera coordinates.
    	Point cam = mMain.getCamera();
    	
    	// Normalise the coordinates.
    	float x = e.getX() / ((float)mMain.getGameFrame().getWidth() - 14) * (Settings.ScreenWidth);
    	float y = e.getY() / ((float)mMain.getGameFrame().getHeight() - 38) * (Settings.ScreenHeight);

    	// Set the position of the mouse inside the window.
    	mMouse.x = x + cam.x;
        mMouse.y = y + cam.y;
        mMouse.x = mMouse.x / 16;
        mMouse.y = mMouse.y / 16;
        
        // Set the position of the mouse over tile.
        mMouseTile.setLocation((int)x / 16 * 16, (int)y / 16 * 16);
       
        // Set the position of the mouse cursor.
        mMouseCursor.setLocation((int)x, (int)y);
   
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.<br>
     * Button1 - Adds / Selects an object or sprite and set position.<br>
     * Button2 - Changes the editor state 0-Select 1-Object 2-Sprite.<br>
     * Button3 - Removes an object or sprite.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mousePressed(MouseEvent e) {
 
		 // Initialise mouse coordinates.
    	 int mouseX = (int)mMouse.x;
    	 int mouseY = (int)mMouse.y;
    	 int mouseDraggedX = (int)mMouseDragged.x;
    	 int mouseDraggedY = (int)mMouseDragged.y;
	 
    	 // Select an object.
         if (mEditorState == 0 && e.getButton() == 1) {
                
        	 // Object is not selected.
        	 if (mMouseClick == 0) {
        		 
        		 // Get tile information.
                 mTile = mLevel.getMap().getTile(mouseX * 16, mouseY * 16);

                 // Object is a tile block, terrain or scenery.
                 if (mTile.spriteTile == 0) {
                	 
                	 // Get description.
                     mSelectionDescription = mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[2];
                         
                     // Is a valid tile.
                     if (mSelectionDescription != "Null") {
                         
                    	 // Get the tile description.
                    	 TileDescription mTileInfo = mLevel.getTileDescription(mTile.index);
                         
                    	 // Tile group of castle, extension and flag pole base
                    	 if ((mTile.tileIndex >= 1 && mTile.tileIndex <= 3) && mTile.tilesetIndex == 0) {
                    		 
                    		 // Set description.
                             mSelectionDescription = "Flag Pole";
                                   
                             // Set mouse selection.
                             mMouseSelection.setBounds((int)mMouse.x, (int)mMouse.y, 15, 10);
                             mMouseTile.setSize(15 * 16, 10 * 16);

                         } else {
                            
                        	 // Set mouse selection.
                        	 mMouseTile.setSize(mTileInfo.width * 16, mTileInfo.height * 16);
                             mMouseSelection.setBounds(mTileInfo.x, mTileInfo.y, mTileInfo.width, mTileInfo.height);
                         }

                         mMouseClick = 1;
                         
                     } else {
                    	 
                    	 // Reset selection.
                         mMouseTile.setSize(16, 16);
                         mMouseSelection.setBounds(mLevel.getHeader().width * 16, mLevel.getHeader().height * 16, 0, 0);
                         mSelectionDescription = "Select Object";
                         mMouseClick = 0;
                     }
                     
                 } else {
                	 
                	 // Object is an entity.                	 
                     mSelectionDescription = mLevel.getEntityStringDescription(mTile.spriteTile)[1];

                     // Is a valid tile.
                     if (mSelectionDescription != "Null") {
                         
                    	 // Get the entity description.
                    	 EntityDescription mEntityInfo = mLevel.getEntityDescription(mTile.spriteIndex);

                    	 // Sprite group of castle flag and flag pole.                    	 
                         if (mTile.spriteTile >= 1 && mTile.spriteTile <= 4) {
                        	 
                        	 // Set description.
                             mSelectionDescription = "Flag Pole";

                             // Set mouse selection.
                             mMouseTile.setSize(15 * 16, 10 * 16);
                             mMouseSelection.setBounds((int)mMouse.x, (int)mMouse.y, 15, 10);
   
                         } else {
                        	 
                        	 // Set mouse selection.
                             mMouseTile.setSize(mEntityInfo.tileWidth, mEntityInfo.tileHeight);
                             mMouseSelection.setBounds(mEntityInfo.x, mEntityInfo.y, mEntityInfo.tileWidth / 16, mEntityInfo.tileHeight / 16);
                         }
                         
                         // Object has been selected.
                         mMouseClick = 1;
    
                     } else {
                    	 
                    	 // Reset selection.
                         mMouseTile.setSize(16, 16);
                         mMouseSelection.setBounds(mLevel.getHeader().width * 16, mLevel.getHeader().height * 16, 0, 0);
                         mSelectionDescription = "Select Object";
                         mMouseClick = 0;
                     }
                 }

                 // Start state.
                 Header header = mLevel.getHeader();
                 if ((mMouse.x >= header.startX && mMouse.x <= header.startX + 2) && 
                     (mMouse.y >= header.startY && mMouse.y <= header.startY + 2)) {
                     
                	 // Change the start state.
                	 if (header.startState < 5)
                         header.startState++;
                     else
                         header.startState = 0;
                 }

                 // End state.
                 if ((mMouse.x >= header.endX && mMouse.x <= header.endX + 2) && 
                     (mMouse.y >= header.endY && mMouse.y <= header.endY + 2)) {
                     
                	 // Change end state.
                	 if (header.endState < 5)
                         header.endState++;
                     else
                         header.endState = 0;
                 }

                 // Bonus state.
                 if ((mMouse.x >= header.bonusX && mMouse.x <= header.bonusX + 2) && 
                     (mMouse.y >= header.bonusY && mMouse.y <= header.bonusY + 2)) {
                     
                	 // Change bonus state.
                	 if (header.bonusState < 5)
                         header.bonusState++;
                     else
                         header.bonusState = 0;
                 }

             } else {
            	 
            	 // Move object.
                 if (mSelectionDescription.equals("Flag Pole")) {
                	 
                	 // Remove castle tiles and entities.
                     mLevel.removeCastle();
                     
                     // Flag.
                     mLevel.addEntity(2, mouseX - 1, mouseY, 'l', 0);
                     
                     // Flag pole.
                     mLevel.addEntity(3, mouseX, mouseY, 'l', 0); 
                     
                     // Flag pole base.
                     mLevel.addTile(3, 0, mouseX, mouseY + 9, mouseDraggedX - mouseX, mouseDraggedY - mouseY);
                     
                     // Castle flag.
                     mLevel.addEntity(1, mouseX + 10, mouseY + 1, 'l', 0);
                    
                     // Castle.
                     mLevel.addTile(1, 0, mouseX + 8, mouseY + 4, mouseDraggedX - mouseX, mouseDraggedY - mouseY);                     
                     
                     // Castle extension.
                     mLevel.addTile(2, 0, mouseX + 11, mouseY + 7, mouseDraggedX - mouseX, mouseDraggedY - mouseY);
                     
                     // Set mouse selection.
                     mMouseSelection.setBounds(mouseX, mouseY, 15, 10);
                     mSelectionDescription = "";
                   
                 } else {
                     
                	 if (mTile.spriteTile == 0) {
                		 
                    	 // Move tile.
                		 TileDescription tileInfo = mLevel.getTileDescription(mTile.index);
                		 tileInfo.x = mouseX;
                         tileInfo.y = mouseY;
                         mMouseSelection.setBounds(tileInfo.x, tileInfo.y, tileInfo.width, tileInfo.height);

                     } else {
                    	 
                    	 // Move sprite.
                    	 EntityDescription entityInfo = mLevel.getEntityDescription(mTile.spriteIndex);
                    	 entityInfo.x = mouseX;
                         entityInfo.y = mouseY;
                         mLevel.setEntityDescription(mTile.spriteIndex, entityInfo);
                         mMouseSelection.setBounds(entityInfo.x, entityInfo.y, entityInfo.tileHeight / 16, entityInfo.tileHeight / 16);
                     }
                 }
                 
                 // Reset selection.
                 mMouseTile.setSize(16, 16);
                 mMouseClick = 0;
                 
                 // Update level map.
                 mLevel.toMap();
                 
             }
         } else if (mEditorState == 4 && e.getButton() == 1) {
        	 
        	 // Add flag pole and castle entity.      	 
             if (mSelectionDescription.equals("Flag Pole")) 
             {             
            	 // Remove castle tiles and entities.
                 mLevel.removeCastle();
                 
                 // Flag.
                 mLevel.addEntity(2, mouseX - 1, mouseY, 'l', 0);
                 
                 // Flag pole.
                 mLevel.addEntity(3, mouseX, mouseY, 'l', 0); 
                 
                 // Flag pole base.
                 mLevel.addTile(3, 0, mouseX, mouseY + 9, mouseDraggedX - mouseX, mouseDraggedY - mouseY);
                 
                 // Castle flag.
                 mLevel.addEntity(1, mouseX + 10, mouseY + 1, 'l', 0);
                
                 // Castle.
                 mLevel.addTile(1, 0, mouseX + 8, mouseY + 4, mouseDraggedX - mouseX, mouseDraggedY - mouseY);                     
                 
                 // Castle extension.
                 mLevel.addTile(2, 0, mouseX + 11, mouseY + 7, mouseDraggedX - mouseX, mouseDraggedY - mouseY);
                 
                 // Set mouse selection.
                 mMouseSelection.setBounds(mouseX, mouseY, 15, 10);           

             } else {
                 
            	 // Add entity.
            	 mLevel.addEntity(mTile.tileIndex, mouseX, mouseY, mTile.direction, mTile.angle);
                 mMouseSelection.setBounds(mouseX, mouseY, Integer.parseInt(mLevel.getEntityStringDescription(mTile.tileIndex)[2]) / 16, Integer.parseInt(mLevel.getEntityStringDescription(mTile.tileIndex)[3]) / 16);
             }
             
             // Update level map.
             mLevel.toMap();
         }

         // Change editor state.
         if (e.getButton() == 2) {
             changeEditorState();
         }

         // Remove tile or entity.
         if (e.getButton() == 3) {
        	  
        	 // Get the tile to remove.
             Tile del = mLevel.getMap().getTile(mouseX * 16, mouseY * 16);
             
             // Tile.
             if (del.spriteTile == 0) {
                 
            	 if (!mLevel.getTileStringDescription(del.tileIndex, del.tilesetIndex)[2].equals("Null")) {
                     
            		 // Castle / extension / flag pole base/
            		 if (del.tileIndex >= 1 && del.tileIndex <= 3 && del.tilesetIndex == 0) 
                          mLevel.removeCastle();
                     else 
                         mLevel.removeTile(del.index);
                 }
            	 
             } else {
            	 
            	 // Sprite.
                 if (!mLevel.getEntityStringDescription(del.spriteTile)[1].equals("Null")) {
                     
                	 // Castle flag / flag / flag pole.
                	 if (del.spriteTile >= 1 && del.spriteTile <= 3) 
                         mLevel.removeCastle();
                     else 
                         mLevel.removeEntity(del.spriteIndex);
                    
                 }
             }
             
             // Reset selection.
             mMouseTile.setSize(16, 16);
             mMouseClick = 0;
             mLevel.toMap();
             mMouseSelection.setBounds(mLevel.getHeader().width, mLevel.getHeader().height, 0, 0);
         }    	

    }
    
    /**
     * Invoked when a mouse button has been released on a component.<br>
     * Adds an object to the map and sets the size of selection rectangle.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mouseReleased(MouseEvent e) {
        
    	// Add tile object.
    	if (mEditorState > 0 && mEditorState < 4 && e.getButton() == 1) 
        {
    		// Add the tile.
            mLevel.addTile(mTile.tileIndex, mTile.tilesetIndex, (int)mMouse.x, (int)mMouse.y, (int)mMouseDragged.x - (int)mMouse.x, (int)mMouseDragged.y - (int)mMouse.y);
                        
            // Mouse dragged.
            if (mMouseDragged.x - mMouse.x > 0 && mMouseDragged.y - mMouse.y > 0) {
                
            	// Set selection rectangle.
            	mMouseSelection.setBounds((int)mMouse.x, (int)mMouse.y, (int)mMouseDragged.x - (int)mMouse.x, (int)mMouseDragged.y - (int)mMouse.y);
            
            } else {
            	
            	// Set selection to tile size.
                mMouseSelection.setBounds((int)mMouse.x, (int)mMouse.y, Integer.parseInt(mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[3]), 
                											            Integer.parseInt(mLevel.getTileStringDescription(mTile.tileIndex, mTile.tilesetIndex)[4]));
            }
           
            // Update level map.
            mLevel.toMap();
            
            // Reset mouse dragged coordinates.
            mMouseDragged = new Point(0,0);
        }
    } 
        
    /**
     * Invoked when the mouse wheel is rotated<BR>
     * Scrolls through object or sprite selection.
     * @param e Event which indicates that the mouse wheel was rotated in a component.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        
    	// Get mouse wheel rotation.
    	int wheelRotation = e.getWheelRotation();
        
    	// Tile selection.
    	if (mEditorState > 0 && mEditorState < 4) {
            
        	// Scroll through tiles.
        	if (wheelRotation < 0)
                nextObject();
            else 
                previousObject();

        } else if (mEditorState == 4) {
        	
        	// Scroll through entities.
            if (wheelRotation < 0)
                nextSprite();
            else
                previousSprite();
        }
    }
        
    /**
     * Invoked when a mouse button is pressed on a component and then dragged<BR>
     * Sets the size of the selection rectangle.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mouseDragged(MouseEvent e) {
        
    	// Set mouse cursor location.
    	mMouseCursor.setLocation(e.getX(), e.getY());

        // Add terrain, block or scenery.
        if (mEditorState > 0 && mEditorState < 4 && SwingUtilities.isLeftMouseButton(e)) {
            
        	// Get camera coordinates.
        	Point cam = mMain.getCamera();
        	
        	mMouseDragged.x = e.getX() + cam.x;
        	mMouseDragged.y = e.getY() + cam.y;
        	mMouseDragged.x = mMouseDragged.x  / 16 + 1;
        	mMouseDragged.y = mMouseDragged.y / 16 + 1;
            
        	if (mMouseDragged.x <= mMouse.x)
                mMouseDragged.x = mMouse.x + 1;
            
        	if (mMouseDragged.y <= mMouse.y)
                mMouseDragged.y = mMouse.y + 1;
        	
        	// Set mouse selection.
            mMouseSelection.setBounds((int)mMouse.x, (int)mMouse.y, (int)mMouseDragged.x - (int)mMouse.x, (int)mMouseDragged.y - (int)mMouse.y);

        }
    }
     
}
