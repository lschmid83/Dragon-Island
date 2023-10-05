package com.citex.dragonisland.core.panel;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.swing.SwingUtilities;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.android.drawing.Grid;
import com.citex.dragonisland.core.drawing.Color;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Control;
import com.citex.dragonisland.core.game.GameFolder;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Timing;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.Dialog;
import com.citex.dragonisland.core.util.Drawing;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Numbers;
import com.citex.dragonisland.core.util.Sound;
import com.citex.dragonisland.java.GamePanel;

/** 
 * This class displays the title screen.
 * @author Lawrence Schmid
 */
public class TitleScreen extends Timing {

	/** Main thread. */
    private Main mMain;	
 
    /** Number of options in a menu. */
    private int mOptions;
    
    /** Selected option in a menu. */
    private int mSelectedOption;  
    
    /** Title screen state. */
    private String mState;
    
    /** Level editor game folder name. */
    private String mFolderName;
    
    /** Level editor folder name character. */
    private int mCharacter;
    
    /** Show screen transition. */
    private boolean mTransition;
    
    /** Has the screen resolution changed. */
    private boolean mResolutionChanged;
    
	/**
	 * Initialises a TitleScreen object.
	 * @param main Main thread.
	 * @param surface Graphics surface.
	 */
	public TitleScreen(Main main, Object surface, boolean transition) {

		// Initialise the title screen.
		mMain = main;
		mMain.setCamera(new Point(0, 0));
    	mTransition = transition;
    	mState = "main";
    	mCharacter = 10;
    	mMain.getControls().reset();
    	mMain.getCurrentPlayer().reset();
		Settings.State = "title";	
		Settings.Paused = false;
		
    	// Get the save game data.
		try {
			mMain.setSaveFile(new SaveFile(Settings.InternalStorageFolder));
		}
		catch(Exception e) {};
				
		// Initialise player object.
		main.getPlayers().get(0).setPosition(new Point(main.getLevel().getHeader().startX * 16, 
													   main.getLevel().getHeader().startY * 16));
	}
    
    /**
	 * Paints the graphics.
	 * @param g Graphics context.
	 * @param dt Delta time.
	 */
    public void paintComponent(Object g, float dt) {

    	// Draw background.
    	Point cam = mMain.getCamera();
    	cam.x = mMain.getCurrentPlayer().getLeft() - Settings.ScreenWidth / 2;
		cam.y = mMain.getCurrentPlayer().getDown() - Settings.ScreenHeight / 3;   	

    	cam = mMain.getBackground().draw(g, dt, cam);
	
    	// Draw level map tiles.
    	mMain.getTileset().drawMap(g, dt, mMain.getLevel().getMap(), cam);
    	  
    	// Draw player.
    	if(!Settings.ShowControls) {
    		mMain.getPlayers().get(0).draw(g, dt, cam);
    	}
    	
   		// Detect map collisions.
   		mMain.getPlayers().get(0).detectMapCollision(g, mMain.getLevel().getMap());
    	    	
    	// Draw title graphic.
    	float x = Settings.ScreenWidth / 2 - 166 / 2;
    	float y = Settings.ScreenHeight * 0.12f;    	
    	if(mState != "options -> debug" && mState != "options -> controls")
    		mMain.getImage(2).draw(g, x, y);
    	           	
    	// Main menu.
    	GameFont font = mMain.getGameFont(0);   	
    	if(mState == "main") {

    		// Set menu position.
    		if(Settings.Mode == GameMode.APPLET)
    			x = Settings.ScreenWidth / 2 - font.getStringWidth("New Game") / 2;
    		else if(Settings.Mode == GameMode.ANDROID)
    			x = Settings.ScreenWidth / 2 - font.getStringWidth("High Scores") / 2;
    		else
    			x = Settings.ScreenWidth / 2 - font.getStringWidth("Level Editor") / 2;
            
            y += Settings.ScreenHeight / 30 + 87;
    		
    		// Loop through menu items.
   			if(Settings.Mode == GameMode.APPLET)
   				mOptions = 2;
   			else if(Settings.Mode == GameMode.ANDROID || !Settings.LevelEditor)
   				mOptions = 3;
   			else
   				mOptions = 4;
    		
    		for (int i = 0; i < mOptions; i++) {

    			// Set selected menu item.
    			int sel = 0;
	            if (i == mSelectedOption)
	                sel = 1;
	            
	            // Draw menu.
	            if (i == 0) {
	                if(Settings.Mode != GameMode.APPLET)
	                	font.drawString(g, sel, "Start Game", x, y);
	                else
	                	font.drawString(g, sel, "New Game", x, y);
	            } else if (i == 1) {
	                if(Settings.Mode == GameMode.APPLET)
	                	font.drawString(g, sel, "Options", x + 5, y + 20);
	                else
	                	font.drawString(g, sel, "High Scores", x, y + 20);
	            } else if (i == 2) {
	                if(Settings.Mode == GameMode.ANDROID || !Settings.LevelEditor)
	                	font.drawString(g, sel, "Settings", x, y + 40);
	                else if(Settings.Mode == GameMode.JAVA)
	                	font.drawString(g, sel, "Level Editor", x, y + 40);
	            } else if (i == 3) {
	                if(Settings.Mode == GameMode.JAVA && Settings.LevelEditor)
	                	font.drawString(g, sel, "Settings", x, y + 60);
	            }
	        }
    	}
    	else if(mState == "new" || mState == "editor -> new level" || mState == "editor -> load level") {

    		// Include Main Game in level folders.
    		boolean includeMainGame = false;
    		if(mState == "new")
    			includeMainGame = true;
    		else 
    			includeMainGame = Settings.EditMainGame;
    		
    		// Get a list of games created in the external storage.
    		ArrayList<GameFolder> game = mMain.getSaveFile().getGameFolders(includeMainGame);
    		
    		// Add extra option for new game folder.
    		if(mState == "editor -> new level")
    			mOptions = game.size() + 1;
    		else
    			mOptions = game.size();

    		// Draw title.
    		x = Settings.ScreenWidth / 2 - font.getStringWidth("Select Game") / 2;
            y += Settings.ScreenHeight / 30 + 87;
    		font.drawString(g, 1, "Select Game", x, y);
    		            
            // Draw menu.
            if(mSelectedOption < game.size()) {

            	String gameFolder = game.get(mSelectedOption).getName();
            	if(gameFolder == "main")
            		gameFolder = "main game";
            	
            	x = Settings.ScreenWidth / 2 - font.getStringWidth(gameFolder) / 2;
            	font.drawString(g, 0, gameFolder, x, y + 43);
            }
            else {
                x = Settings.ScreenWidth / 2 - (font.getStringWidth("New Folder") / 2);
                font.drawString(g, 0, "New Folder", x, y + 43);    	
            }
     		
    	}
    	else if(mState == "load") {
    		            
    		// Find the longest save game title.
    		mOptions = 4;
    		int a = 0;
            for (int i = 0; i < mOptions; i++) {
                int b = font.getStringWidth("  " + mMain.getSaveFile().getSaveGameDescription(i));
                if (b > a) {
                    a = b;
                }
            }

            // Set menu position.
            x = Settings.ScreenWidth / 2 - a / 2;
            y += Settings.ScreenHeight / 30 + 87; 		
    		
            // Loop through menu items.
            for (int i = 0; i < mOptions; i++) {

            	// Set selected menu item.
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                
                // Draw menu.
                if (i == 0) {
                    font.drawString(g, sel, "1", x, y);
                    font.drawString(g, sel, mMain.getSaveFile().getSaveGameDescription(0), x + 17, y);
                } else if (i == 1) {
                    font.drawString(g, sel, "2", x, y + 20);
                    font.drawString(g, sel, mMain.getSaveFile().getSaveGameDescription(1), x + 17, y + 20);
                } else if (i == 2) {
                    font.drawString(g, sel, "3", x, y + 40);
                    font.drawString(g, sel, mMain.getSaveFile().getSaveGameDescription(2), x + 17, y + 40);
                } else if (i == 3) {
                    font.drawString(g, sel, "4", x, y + 60);
                    font.drawString(g, sel, mMain.getSaveFile().getSaveGameDescription(3), x + 17, y + 60);
                }
            }

    	}
    	else if(mState == "scores") {

    		// Draw title.
    		x = Settings.ScreenWidth / 2 - font.getStringWidth("High scores") / 2;
            y += Settings.ScreenHeight / 30 + 87;
    		font.drawString(g, 1, "High scores", x, y);
  
    		// Draw high scores.
    		x = x - 4;
        	font.drawString(g, 0, "1", x, y + 20);
        	font.drawString(g, 0, "-", x + 15, y + 20);
        	drawHighScore(g, x + 30, y + 20, Settings.HighScore[0]);
        	
        	font.drawString(g, 0, "2", x, y + 40);
        	font.drawString(g, 0, "-", x + 15, y + 40);
        	drawHighScore(g, x + 30, y + 40, Settings.HighScore[1]);
        	
        	font.drawString(g, 0, "3", x, y + 60);
        	font.drawString(g, 0, "-", x + 15, y + 60);
        	drawHighScore(g, x + 30, y + 60, Settings.HighScore[2]);

    	}
    	else if(mState == "editor") {
    		
            // Set menu position.
            x = Settings.ScreenWidth / 2 - font.getStringWidth("Load Level") / 2;
            y += Settings.ScreenHeight / 30 + 87; 	
    		
    		// Loop through menu items.
            mOptions = 2;
            for (int i = 0; i < mOptions; i++) {
                
            	// Set selected menu item.
            	int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                
                // Draw menu.
                if (i == 0) {
                    font.drawString(g, sel, "New Level", x, y);

                } else if (i == 1) {
                    font.drawString(g, sel, "Load Level", x, y + 20);
                } 
            }
    		
    	}
    	else if(mState == "editor -> create folder") {

    		// Set menu position.
            x = Settings.ScreenWidth / 2 - font.getStringWidth("Enter folder name") / 2;
            y += Settings.ScreenHeight / 30 + 87; 
            
            font.drawString(g, 1, "Enter folder name", x, y);
            font.drawString(g, 0, mFolderName, x, y + 40);
            
    		// Draw selected character.
            if(Settings.Mode == GameMode.ANDROID) {
            	int folderNameWidth = font.getStringWidth(mFolderName);
            	font.drawCharacter(g, 0, mCharacter, x + folderNameWidth, y + 40);
            }
    	}
    	else if(mState == "editor -> level list") {
    		
            mOptions = mMain.getGameFolder().getLevels().size();
            y += Settings.ScreenHeight / 30 + 87; 
    		x = Settings.ScreenWidth / 2 - font.getStringWidth("Select Level") / 2;
            font.drawString(g, 1, "Select Level", x, 130);
            x = Settings.ScreenWidth / 2 - font.getStringWidth(mMain.getGameFolder().getLevels().get(mSelectedOption)) / 2;
            font.drawString(g, 0, mMain.getGameFolder().getLevels().get(mSelectedOption), x, y+ 40);		
	
    	}
    	else if(mState == "options") {
    		
            // Set menu position.
            x = Settings.ScreenWidth / 2 - font.getStringWidth("Music Off") / 2;
            y += Settings.ScreenHeight / 30 + 87; 			
    		    		
    		// Set number of options.
    		mOptions = 2;
    		
    		if (Settings.DebugMenu && Settings.Mode == GameMode.JAVA)
                mOptions++;
    		
    		if(Settings.Mode == GameMode.ANDROID && GLSurfaceViewRenderer.Width > 480 && GLSurfaceViewRenderer.Height > 272)
    			mOptions++;
    		
    		// Loop through menu items.
            for (int i = 0; i < mOptions; i++) {
                
            	// Set selected menu item.
            	int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }

                // Draw menu.
                if (i == 0) {
                    if (Settings.Music) {
                        font.drawString(g, sel, "Music On", x, y);
                    } else {
                        font.drawString(g, sel, "Music Off", x, y);
                    }

                } else if (i == 1) {
                    if (Settings.Sound) {
                        font.drawString(g, sel, "Sound On", x, y + 20);
                    } else {
                        font.drawString(g, sel, "Sound Off", x, y + 20);
                    }
                    
                } else if (i == 2) {
                    if(Settings.Mode == GameMode.ANDROID && 
                       GLSurfaceViewRenderer.Width > 480 && GLSurfaceViewRenderer.Height > 272)
                    	font.drawString(g, sel, "Display", x, y + 40);
                    else {
                    	font.drawString(g, sel, "Debug", x, y + 40);
                    }
                } 
            } 
    	}
        else if(mState == "options -> debug") {
       		
       		mOptions = 15;
            
       		// Loop through menu items.
       		for (int i = 0; i < 26; i++) {
                
       			// Set selected menu item.
       			int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
               
                font.drawString(g, 1, "Debug Menu", 192, 10);
                int colX = 20;
                int optX = colX + 140;
                                
                if (i == 0) {
                    font.drawString(g, sel, "Level Select", colX, 50);
                    font.drawString(g, sel, Boolean.toString(Settings.LevelSelect), optX, 50);
                }
                if (i == 1) {
                    font.drawString(g, sel, "Invincible", colX, 70);
                    font.drawString(g, sel, Boolean.toString(Settings.Invincible), optX, 70);
                }
                if (i == 2) {
                    font.drawString(g, sel, "Freeze Time", colX, 90);
                    font.drawString(g, sel, Boolean.toString(Settings.FreezeTime), optX, 90);
                }
                if (i == 3) {
                    font.drawString(g, sel, "Infinite Lives", colX, 110);
                    font.drawString(g, sel, Boolean.toString(Settings.InfiniteLives), optX, 110);
                }
                if (i == 4) {
                    font.drawString(g, sel, "Powerups", colX, 130);
                    font.drawString(g, sel, Boolean.toString(Settings.Powerups), optX, 130);
                }
                if (i == 5) {
                	font.drawString(g, sel, "Jump Height", colX, 150);
                    font.drawString(g, sel, Integer.toString(Settings.JumpHeight), optX, 150);
                }
                if (i == 6) {
                    font.drawString(g, sel, "Music Test", colX, 170);
                    font.drawString(g, sel, Integer.toString(Settings.MusicTest), optX, 170);
                }
                if (i == 7) {
                    font.drawString(g, sel, "Sound Test", colX, 190);
                    font.drawString(g, sel, Integer.toString(Settings.SoundTest), optX, 190);
                }

                colX = 231;
                optX = colX + 152;
                if (i == 8) {
                    font.drawString(g, sel, "Animation", colX, 50);
                    font.drawString(g, sel, Boolean.toString(Settings.Animation), optX, 50);
                } else if (i == 9) {
                    font.drawString(g, sel, "Background", colX, 70);
                    font.drawString(g, sel, Boolean.toString(Settings.Background), optX, 70);
                } else if (i == 10) {
                    font.drawString(g, sel, "Remove Enemies", colX, 90);
                    font.drawString(g, sel, Boolean.toString(Settings.RemoveEnemies), optX, 90);
                } else if (i == 11) {
                    font.drawString(g, sel, "Debug Mode", colX, 110);
                    font.drawString(g, sel, Boolean.toString(Settings.DebugMode), optX, 110);
                } else if (i == 12) {
                    font.drawString(g, sel, "Game Speed", colX, 130);
                    font.drawString(g, sel, String.format("%.2f", Settings.GameSpeed), optX, 130);
                } else if (i == 13) {
                    font.drawString(g, sel, "Edit Main Game", colX, 150);
                    font.drawString(g, sel, Boolean.toString(Settings.EditMainGame), optX, 150);
                } else if (i == 14) {
                    font.drawString(g, sel, "Ok", 232, 220);
                }
                font.drawString(g, sel, "Game Version", colX, 170);
                font.drawString(g, sel, Settings.GameVersion, optX, 170);
                font.drawString(g, sel, "Total Memory", colX, 190);
                font.drawString(g, sel, Long.toString(Runtime.getRuntime().totalMemory()), optX, 190);
       		
       		}
       	} else if(mState == "options -> display") {
	    	
            // Set menu position.
    		x = Settings.ScreenWidth / 2 - font.getStringWidth("Screen resolution") / 2;
            y += Settings.ScreenHeight / 30 + 87;
            font.drawString(g, 0, "Screen resolution", x, y);
    		
    		// Set number of options.
    		mOptions = 1;
    
    		// Loop through menu items.
			int w, h;
    		if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
				w = 480;
				h = 272;
			} else {
				w = 400;
				h = 240;
			}

    		x = Settings.ScreenWidth / 2 - font.getStringWidth(w + "x" + h) / 2;
            for (int i = 0; i < mOptions; i++) {
                
            	// Set selected menu item.
            	int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                } 
                
                if (i == 0) {
                    font.drawString(g, sel, w + "x" + h, x, y + 40);
                    
                } 
            }

	    }
    	
    	// Draw controls.
		if(Settings.ShowControls) {
			mMain.getControls().drawTouchScreen(g, mMain.getImages());
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

			Color color = mMain.getBackground().getColor();
        	g.setColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()));
			g.fillRect(0, 0, Settings.ScreenWidth, Settings.ScreenHeight);

			// Paint the component.
    		paintComponent(g, dt);
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
     * Draw the current frame.
     * @param gl Graphics context.
     */
    public void drawFrame(GL10 gl) {
    
    	// Set the current time.
    	setCurrentTime(System.currentTimeMillis());

    	// Calculate the delta time.
    	float dt = getDeltaTime();

		// Resolution has changed.
        if(mResolutionChanged) {
        	Drawing.setResolution(gl, Settings.ScreenWidth, Settings.ScreenHeight);
        	mResolutionChanged = false;
        }  	
    	
    	// Begin drawing.
        Grid.beginDrawing(gl, true, false);

		if(mMain.getScreenTransition().getTimer() > 0) {
        
	        // Set the background colour.
			Color color = mMain.getBackground().getColor();
	        gl.glClearColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
	        
	        // Draw license screen.
	        paintComponent(gl, dt); 
		}
     
    	// Draw screen transition.
    	if(mTransition) {
    		
    		// Stop drawing the transition if it is finished.
    		if(mMain.getScreenTransition().isFinished()) {
    			mTransition = false;    	
    		}
  
    		// Calculate the percentage of the screen height.
    		float y = (mMain.getScreenTransition().getTimer() + 32f) / Settings.ScreenHeight;
    		
        	// Fill the screen below the transition graphic.
            gl.glScissor(0, -(int)(y * GLSurfaceViewRenderer.Height) , GLSurfaceViewRenderer.Width, GLSurfaceViewRenderer.Height);
            gl.glEnable(GL10.GL_SCISSOR_TEST);

            gl.glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1.0f);
			
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // (or whatever buffer you want to clear)
            gl.glDisable(GL10.GL_SCISSOR_TEST);  		

            // Draw transition.
            mMain.getScreenTransition().draw(gl, dt);
    	}	       

        // End drawing.
        Grid.endDrawing(gl);    	
        
        // Set the last update time.
        setLastUpdateTime(getCurrentTime()); 	

    }
    
    /**
     * Draws a high score.
     * @param g Graphics context.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param score Score amount.
     */
    public void drawHighScore(Object g, float x, float y, int score)
    {
    	int tmp = score;
	    int digit[] = new int[8];
	    for (int i = 7; i >= 0; i--) {
	        digit[i] = tmp % 10;
	        tmp = tmp / 10;
	    }
		String str = "";
		for(int i = 0; i < 8; i++)
			str += digit[i];
	
		mMain.getGameFont(0).drawString(g, 0, str, x, y);	
	}   
    
    /**
     * Called when a key was pressed down and not handled by any of the views inside of the activity.
     * @param event Description of the key event.
     */
    public void onKeyDown(int keyCode, boolean pressed) {
    	    	
    	// Update the controls.
	   	mMain.getControls().setControl(keyCode, pressed);	

	   	// Notify the panel that the controls have changed.
    	controlInput(); 	
    	
    	// Get level editor folder name key strokes.
        if(pressed) {
	    	if (mState.equals("editor -> create folder")) {
	            
            	// Remove character from folder name.
	    		if (keyCode == KeyEvent.VK_BACK_SPACE) {
	                
	            	if (mFolderName.length() > 0) {
	                    mFolderName = mFolderName.substring(0, mFolderName.length() - 1);
	            	}	
	            }
	    		else {
	    			
	    			// Update folder name.
	            	if(mFolderName.length() < 9) {
	            	
		            	if(keyCode == KeyEvent.VK_SPACE) {
		            		mFolderName += " ";
		            	}
		            	else if(Control.isLetterOrDigit(keyCode)) {
		                    mFolderName += KeyEvent.getKeyText(keyCode);
		                }
	            	}
	            }
	        } 
        }
	
    }
    
    /**
     * Called when a touch screen event was not handled by any of the views under it.
     * @param event The touch screen event being processed.
     */
    public void onTouchEvent(float x, float y, boolean pressed) {
    	
    	// Set the current time.
    	mMain.getControls().setCurrentTime(System.currentTimeMillis());
    	
    	// Calculate the delta time.
    	float dt = mMain.getControls().getDeltaTime();
    	
    	// If enough time has elapsed.
    	if(dt > 180) {
 	
	    	// Update the controls.
		   	mMain.setControl(x, y, pressed);
	    	  	
		   	// Notify the panel that the controls have changed.
	    	controlInput();
		   	
	    	if(mState == "main")
	    		controlInputMain(x, y, 1);
	    	else if(mState == "new" || mState == "editor -> new level" || mState == "editor -> load level")
	    		controlInputSelectGame(x, y, 1);
	    	else if(mState == "editor")
	    		controlInputEditor(x, y, 1);
	    	else if(mState == "options")
	    		controlInputOptions(x, y, 1);
	    	
	        // Set the last update time.
	        mMain.getControls().setLastUpdateTime(getCurrentTime()); 
	        
	        // Clear the controls.
    		mMain.getControls().reset();
    	}
    	else
    		mMain.getControls().reset();
	
    }
    
    /**
     * Called when the activity has detected the user's press of the back key.
     */
    public void onBackPressed() {

    	// Set the current time.
    	mMain.getControls().setCurrentTime(System.currentTimeMillis());
    	
    	// Calculate the delta time.
    	float dt = mMain.getControls().getDeltaTime();
    	
    	// If enough time has elapsed.
    	if(dt > 180) {
 	
	    	// Update the controls.
		   	mMain.getControls().undo = true;
	    	  	
		   	// Notify the panel that the controls have changed.
    		controlInput();
		   	
	        // Set the last update time.
	        mMain.getControls().setLastUpdateTime(getCurrentTime()); 
	        
	        // Clear the controls.
    		mMain.getControls().reset();
    	}
    	else
    		mMain.getControls().reset();     	
    	
    } 
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e Event which indicates that a mouse action occurred in a component.
     */
    public void mousePressed(MouseEvent e) {
 
    	// Detect right mouse button.
    	int button = 1;
    	if(SwingUtilities.isRightMouseButton(e))
    		button = 2;
    	
	 	// Notify the thread that there was a mouse pressed event.
    	if(mState == "main")
    		controlInputMain(e.getX(), e.getY(), button);
    	else if(mState == "new" || mState == "editor -> new level" || mState == "editor -> load level")
    		controlInputSelectGame(e.getX(), e.getY(), button);
    	else if(mState == "editor")
    		controlInputEditor(e.getX(), e.getY(), button);
    	else if(mState == "options")
    		controlInputOptions(e.getX(), e.getY(), button);
    	else if(mState == "scores")
    		controlHighScores(e.getX(), e.getY(), button);
    	 
    }

    /**
     * Occurs when the controller input has changed.
     */
    public void controlInput() {
    	
    	// Up.
    	if(mMain.getControls().up) {
            
    		// Next menu item.
    		if(mState != "editor -> create folder") {
    		
	    		if (mSelectedOption > 0)
	                mSelectedOption--;    
    		}
    		// Select new folder name characters using up / down keys.
    		else {
    			
    			// Change character.
    			if(mCharacter > 0)
    				mCharacter--;
    			else 
    				mCharacter = 42;  			
    		}
		
    	}
    	
    	// Down.
    	if(mMain.getControls().down) {
            
    		// Previous menu item.
    		if(mState != "editor -> create folder") {
    			
    			if (mSelectedOption < mOptions - 1) 
                    mSelectedOption++;   
    		}	
    		// Select new folder name characters using up / down keys.
    		else  {
	
    			// Change character.
    			if(mCharacter < 42)
    				mCharacter++;
    			else 
    				mCharacter = 10;		
    		}
   		
    	} 
    	
    	// Button 1.
    	if(mMain.getControls().jump) {
    		
    		// Main menu.
    		if(mState == "main") {    		
	            if (mSelectedOption == 0) {           	
	                
	            	// Start a new game.
	            	newGame();
 
	            } else if (mSelectedOption == 1) {
	            	
	            	// Load game / Options (Applet)
	            	if(Settings.Mode == GameMode.APPLET) {
	            		mState = "options";
	            		mSelectedOption = 0;
	            	} else
	            		mState = "scores";
	                mSelectedOption = 0;
	            } else if (mSelectedOption == 2) {
	                if(Settings.Mode == GameMode.ANDROID || !Settings.LevelEditor)
	                	mState = "options";
	                else
	                	mState = "editor";
	                mSelectedOption = 0;
	            } else if (mSelectedOption == 3) {
	                mState = "options";
	                mSelectedOption = 0;
	            }
    		}
    		// New game.
    		else if(mState == "new") {
    			selectGame();
    		}
    		// Editor menu.
    		else if(mState == "editor") {
    			
    			// Set game folder.
                if (mSelectedOption == 0) {
                    mState = "editor -> new level";
                } else if (mSelectedOption == 1) {
                    mState = "editor -> load level";
                }		
    		}
    		// Editor -> New level.
    		else if(mState == "editor -> new level") {
    			selectGame();          	
    		} 
    		// Editor -> Create folder.
    		else if (mState == "editor -> create folder") {
    			
    			// Update folder name.
    			if(mFolderName.length() < 9) {
    				mFolderName += mMain.getGameFont(0).getCharacter(mCharacter);
    			}
    		}
    		// Editor -> Load level.
    		else if (mState == "editor -> load level") {
    			selectGame();
    		} else if(mState == "editor -> level list") {
    		
    			String levelName = mMain.getGameFolder().getLevels().get(mSelectedOption);
                mMain.setLevelPath(levelName);
            	Settings.State = "start editor";
                mSelectedOption = 0;  			
    			
    		} 
    		// High scores.
    		else if(mState == "scores") {
				mState = "main";
				mSelectedOption = 0;	
    		}
    		// Options.
    		else if(mState == "options") {
    			
    			// Enable music.
    			if(mSelectedOption == 0) {
    				Settings.Music = !Settings.Music;
    				
    				if(!Settings.Music) 
    					mMain.getMusicPlayer().stop();
    				else
    					mMain.getMusicPlayer().play("snd/0.mp3");
    			}
    			// Enable sound.
    			else if(mSelectedOption == 1) {
    				Settings.Sound = !Settings.Sound;
    			}
    			// Display / Debug
    			else if(mSelectedOption == 2) {
    				
    				if(Settings.Mode == GameMode.ANDROID) {
    					mState = "options -> display";
    					mSelectedOption = 0;
    				}
    				else {
    					if(Settings.DebugMenu) {
    						mState = "options -> debug";
    						mSelectedOption = 0; 
    					}
    				}
    			}	
    		}
    		// Debug menu.
    		else if(mState == "options -> debug") {
    			
    			 // Initialise the max number of options for each menu item.
    			 int value = 0;
    			 float speedValue = 0;
                 int maxOption;
                 switch (mSelectedOption) {
                 	 case 5: value = Settings.JumpHeight; maxOption = 100; break;
                     case 6: value = Settings.MusicTest; maxOption = 8; break;
                     case 7: value = Settings.SoundTest; maxOption = 10; break;
                     case 12: speedValue = Settings.GameSpeed; maxOption = 2; break;
                     default: value = 0; maxOption = 0; break;
                 }
                 
                 // Update the menu option.
                 if(value < maxOption)
                	 value++;
                 else 
                	 value = 1;
                 
                 if(mSelectedOption == 5) {
                	 
                     if(speedValue < maxOption)
                    	 speedValue +=0.1f;
                     else 
                    	 speedValue = 1;
                 }
                 
                 switch (mSelectedOption)
                 {
                     case 0: Settings.LevelSelect = !Settings.LevelSelect; break;
                     case 1: Settings.Invincible = !Settings.Invincible; break;
                     case 2: Settings.FreezeTime = !Settings.FreezeTime; break;
                     case 3: Settings.InfiniteLives = !Settings.InfiniteLives; break;
                     case 4: Settings.Powerups = !Settings.Powerups; break;
                     case 5: Settings.JumpHeight = value; break;
                     case 6: Settings.MusicTest = value; break;
                     case 7: Settings.SoundTest = value; break;
                     case 8: Settings.Animation = !Settings.Animation; break;
                     case 9: Settings.Background = !Settings.Background; break;
                     case 10: Settings.RemoveEnemies = !Settings.RemoveEnemies; break;
                     case 11: Settings.DebugMode = !Settings.DebugMode; break;
                     case 12: Settings.GameSpeed = speedValue; break;
                     case 13: Settings.EditMainGame = !Settings.EditMainGame; break;   
                     case 14: mState = "options"; mSelectedOption = 2; break;
                     default: break;
                 }

                 // Music test.
                 if(mSelectedOption == 6) {
                	 mMain.getMusicPlayer().stop();
                	 mMain.getMusicPlayer().play("snd/" + Settings.MusicTest + ".mp3");                	 
                 }
                 
                 // Sound test.
                 if(mSelectedOption == 7) {
                	 mMain.getSoundEffects().play(Sound.getSoundEffect(Settings.SoundTest));                	 
                 }
                 
		
    		} else if(mState == "options -> display") {
    			
    			// Change screen resolution.
    			if(Settings.ScreenWidth == 480 && Settings.ScreenHeight == 270) {
    				Settings.ScreenWidth = 400;
    				Settings.ScreenHeight = 237;
    			} else {
    				Settings.ScreenWidth = 480;
    				Settings.ScreenHeight = 270;
    			}
    			mResolutionChanged = true;
    		}
    		 
    		// Reset selected option.
    		if(mState != "options" && mState != "options -> debug")
    			mSelectedOption = 0;   	
    		
    		// Play sound effect.
    		if(mSelectedOption != 7)
    			mMain.getSoundEffects().play("option.wav");
    		    		
    	}
    	
    	// Button 2.
    	if(mMain.getControls().fire) {
    		
    		if (mState == "editor -> create folder") {
                
    			// Remove character from folder name.
    			if(mFolderName != null && mFolderName.length() > 0)
    				mFolderName = mFolderName.substring(0, mFolderName.length() - 1);	
    		}		
    	}

    	// Undo.
    	if(mMain.getControls().undo) {
    		
    		if(mState == "main") {
        		
    			// Exit game.
         		if(Settings.Mode == GameMode.ANDROID)
         			Dialog.showExitDialog(mMain.getActivity());
     		}
    		if(mState == "options -> display") {
        		
    			// Reset to options.
    			mSelectedOption = 0;
    			mState = "options";
    			
    		} else {
    			
        		// Reset to main menu.
    			mSelectedOption = 0;
    			mState = "main";
    		}
    	}
    	
    }
    
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputMain(float x, float y, int button) {
    	
    	// Initialise coordinates.
		float x1, x2, y1, y2;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;

		// New game.
		if(Settings.ScreenWidth > 400) {
			x1 = (178 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (302 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (122 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			x1 = (138 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			x2 = (262 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			y1 = (117 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			newGame();
		} 
		
		// Load game / Options.
		if(Settings.ScreenWidth > 400) {
			y1 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;  
			y2 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
	    	if(Settings.Mode == GameMode.APPLET) {
	    		mState = "options";
	    		mSelectedOption = 0;
	    	} else
	    		mState = "scores";	
		} 	

		// Editor / Scores.
		if(Settings.ScreenWidth > 400) {
			y1 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (178 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			if(Settings.Mode == GameMode.ANDROID || !Settings.LevelEditor)
            	mState = "options";
            else
            	mState = "editor";	    	
            mSelectedOption = 0;
		} 
		
		// Options.
		if(Settings.ScreenWidth > 400) {
			y1 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (205 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (178 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (200 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			if(Settings.Mode == GameMode.JAVA && Settings.LevelEditor) {
				mState = "options";
				mSelectedOption = 0;
			}
		} 
		
    }
        
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputSelectGame(float x, float y, int button) {
    	
    	// Initialise coordinates.
		float x1, x2, y1, y2;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;
		
		// Select game.
		if(Settings.ScreenWidth > 400) {
			x1 = (175 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (312 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (165 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (190 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			x1 = (135 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			x2 = (267 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			y1 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			selectGame();
		} 
		
		if(button == 2) {
			mState = "main";
			mSelectedOption = 0;
		}

    }
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputLoadGame(float x, float y, int button) {
    	
    	// Initialise coordinates.
		float x1 = 0, x2 = 0, y1 = 0, y2 = 0;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;
		
		// Load game 1.
		if(Settings.ScreenWidth > 400) {
			x1 = (168 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (320 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (122 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			x1 = (130 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			x2 = (280 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			y1 = (117 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			// loadGame();
		} 	
		
		// Load game 2.
		if(Settings.ScreenWidth > 400) {
			y1 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;  
			y2 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			// loadGame();
		} 		
	
		// Load game 3.
		if(Settings.ScreenWidth > 400) {
			y1 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (178 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			// loadGame();
		} 		
		
		// Load game 4.
		if(Settings.ScreenWidth > 400) {
			y1 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (205 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (178 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (200 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}

		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			// loadGame();
		} 		
    	
    } 
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputEditor(float x, float y, int button) {
    	
    	// Initialise coordinates.
		float x1 = 0, x2 = 0, y1 = 0, y2 = 0;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;
		
		// New game.
		if(Settings.ScreenWidth > 400) {
			x1 = (183 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (300 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (122 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			x1 = (142 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			x2 = (257 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			y1 = (117 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			mState = "editor -> new level";
			mSelectedOption = 0;
		} 
		
		// Load level.
		if(Settings.ScreenWidth > 400) {
			y1 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (167 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			y1 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (160 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			mState = "editor -> load level";
			mSelectedOption = 0;
		} 
		
		if(button == 2) {
			mState = "main";
			mSelectedOption = 0;
		}
		
		
    }
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlInputOptions(float x, float y, int button) {
    	
    	// Initialise coordinates.
		float x1 = 0, x2 = 0, y1 = 0, y2 = 0;    	
		
		// Get the normalised coordinates.
		Point coordinates = Numbers.getNormalisedCoordinate(x, y);
		x = coordinates.x;
		y = coordinates.y;
		
		// Music.
		if(Settings.ScreenWidth > 400) {
			x1 = (188 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			x2 = (293 / (float)Settings.ScreenWidth) * Settings.ScreenWidth;
			y1 = (122 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			
		} else {
			x1 = (147 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			x2 = (252 / (float)Settings.ScreenWidth) * Settings.ScreenWidth; 
			y1 = (117 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			Settings.Music = !Settings.Music;
			if(!Settings.Music) 
				mMain.getMusicPlayer().stop();
			else
				mMain.getMusicPlayer().play("snd/0.mp3");
			mSelectedOption = 0;
		} 
		
		// Sound.
		if(Settings.ScreenWidth > 400) {
			y1 = (143 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (138 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;  
			y2 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {
			Settings.Sound = !Settings.Sound;	
			mSelectedOption = 1;
		} 
		
		// Display / Debug.
		if(Settings.ScreenWidth > 400) {
			y1 = (163 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
			y2 = (183 / (float)Settings.ScreenHeight) * Settings.ScreenHeight;
		} else {
			y1 = (158 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
			y2 = (178 / (float)Settings.ScreenHeight) * Settings.ScreenHeight; 
		}
		
		if (x >= x1 && x <= x2 && y > y1 && y <= y2) {

			if(Settings.Mode == GameMode.ANDROID) {
				mState = "options -> display";
				mSelectedOption = 0;
			}
			else {
				if(Settings.DebugMenu) {
					mState = "options -> debug";
					mSelectedOption = 0; 
				}
			}			
		}
		
		if(button == 2) {
			mState = "main";
			mSelectedOption = 0;
		}

    }
    
    /**
     * Occurs when the touch screen input has changed.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param button Mouse button.
     */
    public void controlHighScores(float x, float y, int button) {
		mState = "main";
		mSelectedOption = 0;	
    }
         
    /**
     * Starts a new game.
     */
    public void newGame() {
    	
    	// Check if there are any games created with the level editor.
    	if(mMain.getSaveFile().getGameFolders(false).size() > 0 && Settings.Mode == GameMode.JAVA)
    		mState = "new";
    	else {
    		
			// Start a new game.
            mMain.setGameFolder(0);
            Settings.State = "start level select";
            
    	}
    }
    
    /**
     * Select game screen.
     */
    public void selectGame() {

    	if(mState == "new") {
			
			// Start a new game.
            mMain.setGameFolder(mSelectedOption);
            //mMain.setLevelPath("1.1.1.lvl");
            //Settings.State = "load level";	
            Settings.State = "start level select";	

		} else if(mState == "editor -> new level") {
			
			// Create a new game folder.
            if (mSelectedOption == mMain.getSaveFile().getGameFolders(Settings.EditMainGame).size()) {
                mState = "editor -> create folder";
                mFolderName = "";
                mCharacter = 10; // A
            } 
            // Create a new level in existing game folder.
            else 
            {
            	mMain.setGameFolder(mSelectedOption);
                mMain.setLevelPath(null);
            	Settings.State = "start editor";
                mSelectedOption = 0;
            }            	
		} else if (mState == "editor -> load level") {
		    
			// Create a new game folder.
            if (mSelectedOption == mMain.getSaveFile().getGameFolders(Settings.EditMainGame).size()) {
                mState = "editor -> create folder";
                mFolderName = "";
                mCharacter = 10; // A
            } else {		
			
            	// Get list of levels in game folder.
            	mState = "editor -> level list";
            	mMain.setGameFolder(mSelectedOption);
            }

		}
    	
    }

}