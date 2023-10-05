package com.citex.applet.dragonisland;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

/**
	This class draws all graphics for the game and handles game logic and 
	collision detection between the player, objects and entities.
	
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

public class GamePanel extends JPanel implements KeyListener, MouseListener,
        MouseMotionListener, MouseWheelListener, Runnable {

	/** The user controllable player */
    private Player mPlayer;
    /** The NPC entities */
    private ArrayList<Entity> mEntities;
    /** The level background */
    private Background mBackground;
    /** The user interface */
    private UserInterface mUserInterface;
    /** The music */
    private static Music mMusic;
    /** The level tileset (terrain, blocks, scenery) */
    private Tileset mMapTileset;
    /** The level information */ 
    private Level mLevelInfo;
    /** The level header information */
    private Header mLevelHeader;
    /** The level map */    
    private Map mLevelMap;
    /** The camera position */ 
    private Point mCamera;
    /** The position of the end of level castle */
    private Point mCastlePosition;
	/** The save game information */
    private Game mSaveGameInfo;
    /** The main game thread */    
    private Thread thread;
    /** The double buffer graphics surface */
    private BufferedImage back_buffer;
    /** The double buffer graphics context */
    private Graphics2D back_buffer_graphics;
    /** The amount of earthquake to apply */
    private int mEarthquakeTimer;  
    /** Shake the screen */
    private boolean mEarthquake;
    /** Is the player on a bonus level */
    private boolean mOnBonus;
    /** The file containing the players recording movement script */
    private BufferedReader mRecordScript;
    /** The file containing the players playback movement script */
    private ArrayList<ScriptCommand> mPlaybackScript;
    /** Stores the last keypress variables for debug cheat menu */
    private String mKeyBuffer;
    /** The script mode 0-normal, 1-recording, 2-playback, 3-scripted playback */
    private int mScriptMode = 0; 
    /** The time elapsed while running the script */
    private int mScriptTimer = 0;
    /** The pause between displaying speech */
    private int mScriptCommandPause = 250;  
    /** Distinguishes the class when it is serialized and deserialized */
    public final static long serialVersionUID = 3000000;

	/**
	 * Constructs the GamePanel
	 */
    public GamePanel() {

        back_buffer = new BufferedImage(480, 272, BufferedImage.TYPE_INT_RGB);
        back_buffer_graphics = (Graphics2D) back_buffer.getGraphics();

        mUserInterface = new UserInterface();
        mKeyBuffer = "";

        //add event listeners
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setFocusable(true);
        requestFocus();

        showTitleScreen();
    }
    
	/**
	 * Shows the title screen
	 */
    public void showTitleScreen() {
 
    	mOnBonus = false;
        Settings.State = "title";
        mEarthquake = false;
        mScriptMode = 0;
        loadLevel("Main Game", 0, 0, 0);   	
    	setMusic(mLevelHeader.music);
        mPlayer = new Player(Settings.MainCharacter, 0, 0, 3, 0, 0, 0, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
    }   
    
    /**
     * Starts a new game and loads the first level
     */
    public void newGame() {

        try //intro level
        {          
        	InputStream is = Main.class.getResourceAsStream("res/lvl/" + "Main Game" + "/" + 0 + "." + 0 + "." + 1 + ".ls"); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            input.close();
            Settings.State = "game";
            //mScreenTransition = 2;
            loadLevel("Main Game", 0, 0, 1);
        } catch (Exception e) {
            Settings.State = "level open";
            Settings.Paused = true; 
            // System.err.println("Error: Cannot find the new game intro file.");
            loadLevel("Main Game", 1, 1, 1);
            	
        }

        mPlayer = new Player(Settings.MainCharacter, 0, 0, 3, 0, 0, mLevelInfo.getHeader().timeLimit, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
        loadScript();
        updateSaveGameFile();
    }
    
    /**
     * Creates the player object with the selected character
     */
    public void createPlayer() {
        mPlayer = new Player(Settings.MainCharacter, 0, 0, 3, 0, 0, 0, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
    }   
    
    /**
   	 * Loads the level and creates the player, tileset, entities and background
   	 * @param gameFolder The folder containing the levels
     * @param world The world number of the level
     * @param level The level number of the level
     * @param area The area number of the level
     * @return True if the level exists
     */
    public boolean loadLevel(String gameFolder, int world, int level, int area) {
       	Music.closeMusic();
        mLevelInfo = new Level();
        if (mLevelInfo.loadLevel(("res/lvl/" + gameFolder + "/" + world + "." + level + "." + area + ".lvl"))) {
            mLevelHeader = mLevelInfo.getHeader();
            mLevelMap = mLevelInfo.toMap();
            mCamera = new Point(0, 0);
            mMapTileset = new Tileset(mLevelHeader.tileset0, mLevelHeader.tileset16, mLevelHeader.tileset32);
            mEntities = new ArrayList<Entity>();
            mBackground = new Background(mLevelHeader.bg, mLevelHeader.width, mLevelHeader.height, mLevelHeader.bgSpeed, mLevelHeader.bgAlign);
            mBackground.setColor(mLevelHeader.bgColor);
            //setMusic(mLevelHeader.Music);
            for (int i = 0; i < mLevelInfo.getEntityCount(); i++) {
                EntityDescription s = mLevelInfo.getEntityDescription(i);
                if (!Settings.RemoveEnemies) {
                    mEntities.add(new Entity(s.tile, 'l', s.x * 16, s.y * 16));
                } else //remove enemies
                {
                    if (s.tile < 20) {
                        mEntities.add(new Entity(s.tile, 'l', s.x * 16, s.y * 16));
                    }
                }
            }
            //find castle side extension
            mCastlePosition = new Point(-100, -100); //set offscreen
            for (int i = 0; i < mLevelInfo.getObjectCount(); i++) {
                if (mLevelInfo.getTileDescription(i).tile == 2 && mLevelInfo.getTileDescription(i).tileset == 0) {
                    mCastlePosition = new Point(mLevelInfo.getTileDescription(i).x * 16, mLevelInfo.getTileDescription(i).y * 16);
                }
            }
            return true;
        } else //level not found
        {
            return false;
        }
    }
    
	/**
	 * Advances to the next level specified by the header information
	 */
    public void advanceLevel() {

        Settings.State = "game";
        Settings.LimitCameraX = 240;
        Settings.LimitCameraY = 136;

        if (mLevelHeader.timeLimit == 0) //no timelimit set
        {
            mLevelHeader.timeLimit = mPlayer.getTime();
        }
        if (loadLevel("Main Game", mLevelHeader.endWorld, mLevelHeader.endLevel, mLevelHeader.endArea)) {
            if (mOnBonus) //restart player at bonus position
            {
                int state = mLevelInfo.getHeader().bonusState; //set start state to opposite of bonus state
                if (state == 2) {
                    state = 3;
                } else if (state == 3) {
                    state = 2;
                } else if (state == 4) {
                    state = 5;
                } else if (state == 5) {
                    state = 4;
                }
                mPlayer = new Player(Settings.MainCharacter, mPlayer.getSize(), mPlayer.getInvincible(), mPlayer.getLives(), mPlayer.getCoins(), mPlayer.getScore(), mLevelInfo.getHeader().timeLimit, state, mLevelHeader.bonusX * 16, mLevelHeader.bonusY * 16);
                mLevelInfo.getHeader().bonusState = 99; //stop player entering pipe again
                loadScript();
                mOnBonus = false;
            } else //new level
            {      
            	Settings.State = "level open";
                mPlayer = new Player(Settings.MainCharacter, mPlayer.getSize(), 0, mPlayer.getLives(), mPlayer.getCoins(), mPlayer.getScore(), mLevelInfo.getHeader().timeLimit, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
                loadScript();
                updateSaveGameFile();
            }
        } else {
            System.out.println("Level does not exist");
            showTitleScreen();
        }
    }
    
    /**
     * Advances to the bonus level
     */
    public void advanceBonus() {
        if (loadLevel("Main Game", mLevelHeader.world, mLevelHeader.level, 0)) {
            if (mLevelHeader.timeLimit == 0) //no timelimit set
            {
                mLevelHeader.timeLimit = mPlayer.getTime(); //continue time from previous level
            }
            mPlayer = new Player(Settings.MainCharacter, mPlayer.getSize(), mPlayer.getInvincible(), mPlayer.getLives(), mPlayer.getCoins(), mPlayer.getScore(), mLevelInfo.getHeader().timeLimit, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
            setMusic(mLevelHeader.music);
            loadScript();
        } else {
            System.out.println("Bonus does not exist");
            showTitleScreen();
        }
    }   
    
    /**
     * Resets the level
     */
    public void resetLevel() {
        Settings.State = "game";
        
        if(loadLevel("Main Game", mLevelHeader.world, mLevelHeader.level, mLevelHeader.area))
        {
        	mPlayer = new Player(Settings.MainCharacter, 0, 0, mPlayer.getLives(), mPlayer.getCoins(), mPlayer.getScore(), mLevelInfo.getHeader().timeLimit, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);  	
        	setMusic(mLevelHeader.music);
        	loadScript();
        	updateSaveGameFile();
        }
        else
        	showTitleScreen();
    }    
        
	/**
	 * Updates the save game information
	 */
    public void updateSaveGameFile() {
        mSaveGameInfo = new Game();
        mSaveGameInfo.game = SaveFile.mGameNumber;
        mSaveGameInfo.world = mLevelHeader.world;
        mSaveGameInfo.level = mLevelHeader.level;
        mSaveGameInfo.character = Settings.MainCharacter;
        mSaveGameInfo.size = mPlayer.getSize();
        mSaveGameInfo.lives = mPlayer.getLives();
        mSaveGameInfo.coins = mPlayer.getCoins();
        mSaveGameInfo.score = mPlayer.getScore();
    }
    
	/**
	 * Saves the game progress
	 * @param fileNumber The save game file number
	 */
    public void saveGame(int fileNumber) {
        Settings.File.setGame(fileNumber, mSaveGameInfo);
        Settings.File.saveFile(fileNumber);
    }
   
	/**
	 * Loads a save game
	 * @param fileNumber The file number containing the save game information
	 */
    public void loadSaveGame(int fileNumber) {
        Game g = Settings.File.getGame(fileNumber);
        Settings.State = "level open";
        
        if(loadLevel(SaveFile.mGameName.get(g.game), g.world, g.level, 1))
        {
	        mPlayer = new Player(g.character, g.size, 0, g.lives, g.coins, g.score, mLevelInfo.getHeader().timeLimit, mLevelHeader.startState, mLevelHeader.startX * 16, mLevelHeader.startY * 16);
	        loadScript();
	        updateSaveGameFile();
        }
        else
        	showTitleScreen();
    }
   
    /**
     * Sets the level music
     * @param file The midi file number
     */
    public void setMusic(int file) {
        if (file != 0 && Settings.Music) {
            String path = "res/snd/" + file + ".mp3"; //".mp3";
            if (mMusic != null) {
                Music.closeMusic();
            }
            mMusic = new Music(path);
        } else {
            if (mMusic != null) {
                Music.closeMusic();
            }
        }
    }   
    
    /**
     * Adds a block explosion
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void addBlockExplosion(int x, int y) {
        x = x * 16;
        y = y * 16;

        mMapTileset.setBlockExplode(x, y, 'l');
        mMapTileset.setBlockExplode(x + 16, y, 'r');
        mMapTileset.setBlockExplode(x, y + 16, 'l');
        mMapTileset.setBlockExplode(x + 16, y + 16, 'r');
    }   
    
    /**
     * Adds a new entity to the collection
     * @param sprite the entities sprite sheet
     * @param direction the start direction 'l' = left 'r' = right
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addEntity(int sprite, char direction, int x, int y) {
        mEntities.add(new Entity(sprite, direction, x, y));
    }

    /**
     * Returns the level header information
     * @return The level header
     */
    public Header getHeader() {
        return mLevelHeader;
    }
    
    /**
     * Returns the players lives count
     * @return The players lives count
     */
    public int getPlayerLives() {
        return mPlayer.getLives();
    }

    /**
     * Records player movements in a level script
     * @param gameFolder The folder containing the levels
     * @param world The world number 
     * @param level The level number
     * @param area The area number
     */
    public void recordMovement(String gameFolder, int world, int level, int area) {
        String fileName = "res\\rec\\" + gameFolder + "." + world + "." + level + "." + area + ".rec";
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            mScriptTimer = 0;
            mScriptMode = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Plays back player movements from a level script
     * @param gameFolder the folder containing the levels
     * @param world the world number of the level
     * @param level the level number of the level
     * @param area the area number of the level
     */
    public void playbackMovement(String gameFolder, int world, int level, int area) {
        String fileName = "res/rec/" + gameFolder + "." + world + "." + level + "." + area + ".rec";
    	InputStream is = Main.class.getResourceAsStream(fileName); 
    	try {    	
            mRecordScript = new BufferedReader(new InputStreamReader(is));   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a script containing commands which run in a time sequence
     */
    public void loadScript() {
        try {
        	InputStream is = Main.class.getResourceAsStream("res/lvl/" + "Main Game" + "/" + mLevelInfo.getHeader().world + "." + mLevelInfo.getHeader().level + "." + mLevelInfo.getHeader().area + ".ls"); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));  
            mPlaybackScript = new ArrayList<ScriptCommand>();
            String line;
            try {
                while ((line = input.readLine()) != null) {
                    ScriptCommand sc = new ScriptCommand();
                    sc.time = Integer.parseInt(line.split(",")[0]);
                    sc.command = line.split(",")[1];
                    sc.text = line.split(",")[2];
                    sc.x = Integer.parseInt(line.split(",")[3]);
                    sc.y = Integer.parseInt(line.split(",")[4]);
                    sc.width = Integer.parseInt(line.split(",")[5]);
                    sc.height = Integer.parseInt(line.split(",")[6]);
                    sc.direction = line.split(",")[7].toCharArray()[0];
                    mPlaybackScript.add(sc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Settings.LimitCameraX = 75;
            Settings.LimitCameraY = 136;

            mScriptTimer = 0;
            mScriptMode = 3;

            playbackMovement("Main Game", mLevelInfo.getHeader().world, mLevelInfo.getHeader().level, mLevelInfo.getHeader().area);

            input.close();

        } catch (Exception e) {
            Settings.LimitCameraX = 240;
            Settings.LimitCameraY = 136;
            mScriptMode = 0;
        }
    }

    /**
     * Starts the main game thread
     */
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Repaint the game panel in a thread
     */
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(Settings.GameSpeed);
            } catch (InterruptedException e) {
            }
        }
    }

	/**
	 * Draws the player, background, tileset and entities, handles collision detection
	 * and control the state of the game
	 * @param gl The graphics context
	 */
    public void paintComponent(Graphics g) {
    	
    	if (!Settings.Paused) {
                
    		//set camera and draw background
            mCamera.x = mPlayer.getLeft() - Settings.LimitCameraX;
            mCamera.y = mPlayer.getDown() - Settings.LimitCameraY;
            mCamera = mBackground.draw(back_buffer_graphics, mCamera);

            //draw objects 
            mMapTileset.drawMap(back_buffer_graphics, mLevelMap, mCamera);

            //play / record player movement
            if ((mRecordScript != null && mScriptMode > 0) && !mUserInterface.showTransition) {

            	if (mScriptMode > 1 && mScriptMode < 4) //play
                {
            		try {
            			//set player movement
                        mScriptTimer++; //advance capture / playback  timer
                        String ln = mRecordScript.readLine();
                        mPlayer.setX(Integer.parseInt(ln.split(",")[0]));
                        mPlayer.setY(Integer.parseInt(ln.split(",")[1]));
                        mPlayer.setDirection(ln.split(",")[2].charAt(0));
                        mPlayer.setAnimationStateIndex(Integer.parseInt(ln.split(",")[3]));
                    } catch (Exception e) //end of .rec file
                    {
                    	e.printStackTrace();
                        try {
                        	mRecordScript.close();
                        } catch (Exception er) { }

                        if (mScriptMode == 3) {
                        	advanceLevel(); //end of script
                        }
                        mScriptMode = 0; //set capture mode to 0 normal
                    }               
                }
                if (mScriptMode == 3) {
                	//read command script file
                    for (int i = 0; i < mPlaybackScript.size(); i++) {
                    	if ((mScriptTimer == mPlaybackScript.get(i).time) || mPlaybackScript.get(i).timer > 0) //if script command == capture timer
                        {
                    		ScriptCommand sc = mPlaybackScript.get(i);

                            if (sc.command.equals("Earthquake")) {
                            	mEarthquake = !mEarthquake; //start/stop quake
                            } else if (sc.command.equals(("Sound"))) {
                            	new SoundEffect("res/sfx/tank.wav", sc.x).start(); //sc.x == number of loops
                            } else if (sc.command.equals("Speech")) {
                            	mUserInterface.drawSpeech(back_buffer_graphics, sc.x, sc.y, sc.width, sc.height, sc.text, sc.direction);
                            	if (mPlaybackScript.get(i).timer < mScriptCommandPause) {
                            		if (mPlaybackScript.get(i).timer == 0) {
                            			new SoundEffect("res/sfx/speech.wav").start();
                                     }
                                     mPlaybackScript.get(i).timer++;
                                } else {
                                	mPlaybackScript.get(i).timer = 0;
                                }
							} else if (sc.command.equals("Enemy")) {
								addEntity(Integer.parseInt(sc.text),
										sc.direction, sc.x, sc.y);
							} else if (sc.command.equals("Block")) {
								mLevelInfo.addTile(1, 0, sc.x, sc.y, sc.width,
										sc.height);
								mLevelMap = mLevelInfo.toMap();
							} else if (sc.command.equals("Collision")) {
								if (mLevelMap.getTile(sc.x, sc.y).collision == 0) {
									mLevelMap.setCollisionTile(sc.x, sc.y, 1);
								} else {
									mLevelMap.removeTile(sc.x, sc.y);
								}
							} else if (sc.command.equals("Explosion")) {
								new SoundEffect("res/sfx/block.wav").start();
								mLevelMap.removeTile(sc.x, sc.y);
								addBlockExplosion(sc.x, sc.y);
							} else if (sc.command.equals("Advance Level")) {
								advanceLevel();
							}
						}
					}
				}
			}

            //loop through enemies array list
            for (int i = 0; i < mEntities.size(); i++) {
            	if (mEntities.get(i).getX() > mCamera.x - 250
            			&& mEntities.get(i).getX() < mCamera.x + 580) //enemy is on screen
                {
            		mEntities.get(i).detectMapCollision(mLevelMap); //detect map collisions
                    mEntities.get(i).draw(back_buffer_graphics, mCamera); //paint / move enemy
                }
            }

            //draw map object explosions
            mMapTileset.drawExplosions(back_buffer_graphics, mLevelMap, mCamera);

            //detect collisions between player and entities
            mPlayer.detectEntityCollision(mEntities);

            //detect collisions between player and map objects
            mPlayer.detectMapCollision(mLevelMap);

            //draw player
            mPlayer.draw(back_buffer_graphics, mCamera);

            if (mPlayer.getPipeState() > 1) //redraw map objects
            {
            	mMapTileset.drawMap(back_buffer_graphics, mLevelMap, mCamera); //draw pipe over player
            }

            //draw castle extension over player
            back_buffer_graphics.drawImage(mMapTileset.getFrame(2, 0, 0), mCastlePosition.x - mCamera.x, mCastlePosition.y - mCamera.y, null);

            //advance level if player has reached end state
            if (mPlayer.getEndState() == 7) {
            	advanceLevel();
            } else if (mPlayer.getEndState() == 8) //goto bonus level if player has reached bonus level
            {
            	advanceBonus();
                mOnBonus = true;
            }

			// player has died falling off map
			if (mPlayer.getUp() > mLevelMap.getHeight() + mPlayer.getHeight()) {
				if (!mPlayer.getAnimationState().equals("die")) {
					new SoundEffect("res/sfx/die.wav").start();
				}
				mPlayer.removeLife(); // lose life
				if (mPlayer.getLives() > 0) {
					resetLevel(); // reset level
				} else {
					showTitleScreen(); // lost all lives reset to title screen
				}
			}

			if (mPlayer.getLives() < 0) {
				showTitleScreen();
			}
		}

        //draw status bar
        if (Settings.State.equals("game")) {
            if (mScriptMode < 3 && !Settings.DebugMode) 
            {
                mUserInterface.drawStatus(back_buffer_graphics, 50, 15, mPlayer.getLives(), mPlayer.getCoins(), mPlayer.getScore(), mPlayer.getTime());
            }
        } else {
            mUserInterface.draw(back_buffer_graphics); 
        }

        //debug mode 
        if (Settings.DebugMode && Settings.State.equals("game")) {
            back_buffer_graphics.setColor(Color.BLACK);
            back_buffer_graphics.drawString("Level: " + mLevelInfo.getHeader().world + "-" + mLevelInfo.getHeader().level + "-" + mLevelInfo.getHeader().area, 5, 15);
            back_buffer_graphics.drawString("Tileset: " + mLevelInfo.getHeader().tileset0 + "," + mLevelInfo.getHeader().tileset16 + "," + mLevelInfo.getHeader().tileset32, 5, 30);
            back_buffer_graphics.drawString("Background: " + mLevelInfo.getHeader().bg[0] + "-" + mLevelInfo.getHeader().bg[1] + "-" + mLevelInfo.getHeader().bg[2], 5, 45);
            back_buffer_graphics.drawString("Music: " + mLevelInfo.getHeader().music, 5, 60);
            back_buffer_graphics.drawString("Map Width: " + mLevelInfo.getHeader().width + " px", 5, 75);
            back_buffer_graphics.drawString("Map Height: " + mLevelInfo.getHeader().height + " px", 5, 90);
            back_buffer_graphics.drawString("Character: " + Settings.MainCharacter, 150, 15);
            back_buffer_graphics.drawString("Coordinates: x=" + mPlayer.getX() + " y=" + mPlayer.getY(), 150, 30);
            back_buffer_graphics.drawString("State: " + mPlayer.getAnimationState(), 150, 45);
            back_buffer_graphics.drawString("Script Mode: " + mScriptMode, 150, 60);
            back_buffer_graphics.drawString("Script Timer: " + mScriptTimer, 150, 75);
            back_buffer_graphics.drawString("Memory: " + Long.toString(Runtime.getRuntime().totalMemory()), 150, 90);
        }

        if (mUserInterface.showTransition) {
            mUserInterface.drawScreenTransition(back_buffer_graphics);
        }

        if (mEarthquake) {
            if (mEarthquakeTimer < 2) {
                mEarthquakeTimer++;
            } else {
                mEarthquakeTimer = 0;
            }
            g.drawImage(back_buffer, -mEarthquakeTimer, -mEarthquakeTimer, getWidth() + 2, getHeight() + 1, this); //move x,y
        } else {
            g.drawImage(back_buffer, 0, 0, getWidth() + 2, getHeight() + 1, this); //draw back buffer
        }
    }


    /**
     * Invoked when a key has been pressed<BR>
     * Sets the player controls, pause the game or pass event the level editor panel
     * @param e event which indicates that a keystroke occurred in a component
    */
    public void keyPressed(KeyEvent e) {
        if (Settings.State.equals("title")) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                mKeyBuffer += 'u';
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                mKeyBuffer += 'd';
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                mKeyBuffer += 'l';
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                mKeyBuffer += 'r';
            } else if (e.getKeyCode() == KeyEvent.VK_B) {
                mKeyBuffer += 'b';
            } else if (e.getKeyCode() == KeyEvent.VK_A) {
                mKeyBuffer += 'a';
            }
            if (mKeyBuffer.equals("uuddlrlrba")) //enable debug mode
            {
                Settings.DebugMenu = !Settings.DebugMenu;
                mKeyBuffer = "";
            }
        } else {
            mKeyBuffer = "";
        }
        if(e.getKeyCode() == KeyEvent.VK_EQUALS)
        {
        	if(Settings.GameSpeed > 0)
        		Settings.GameSpeed--;          
        }
        if(e.getKeyCode() == KeyEvent.VK_MINUS)
        {
        	if(Settings.GameSpeed < 30)
        		Settings.GameSpeed++;       	
        }          
        if (e.getKeyCode() == KeyEvent.VK_END && Settings.LevelSelect && Settings.State.equals("game")) {
            advanceLevel();
        }
        if (Settings.State.equals("game") && mScriptMode < 2) {
            setControl(e, true);
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !mUserInterface.showTransition) {
            if (Settings.State.equals("game") || Settings.State.equals("level editor")) {
                new SoundEffect("res/sfx/select.wav").start();
                if (mScriptMode == 0) {
                    Settings.Paused = true;
                    Settings.State = "pause";
                } else 
                {
                    advanceLevel();
                    mEarthquake = false;
                }
            } else {
                mUserInterface.keyPressed(e);
            }
        } else {
            if (!Settings.State.equals("game") && !Settings.State.equals("level editor")) {
                mUserInterface.keyPressed(e);
            }
        }
    }

    /**
     * Invoked when a key has been released
     * @param e event which indicates that a keystroke occurred in a component
    */
    public void keyReleased(KeyEvent e) {
        setControl(e, false);
    }

    /**
     * Invoked when a key has been typed
     * @param e event which indicates that a keystroke occurred in a component
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
	 * Sets the controller flags for the player
     * @param e event which indicates that a keystroke occurred in a component
     * @param pressed True if a key is pressed
     */
    public void setControl(KeyEvent e, boolean pressed) {
        int keyCode = e.getKeyCode();
        if (mPlayer != null) {
            if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
                mPlayer.setControls().mJumpPressed = pressed;
            } else if (keyCode == KeyEvent.VK_UP) {
                mPlayer.setControls().mUpPressed = pressed;
            } else if (keyCode == KeyEvent.VK_LEFT) {
                mPlayer.setControls().mLeftPressed = pressed;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                mPlayer.setControls().mDownPressed = pressed;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                mPlayer.setControls().mRightPressed = pressed;
            } else if (keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_A) {
                mPlayer.setControls().mRunPressed = pressed;
            }
        }
    }

    /**
     * Invoked when the mouse enters a component
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component
     * @param e An event which indicates that a mouse action occurred in a component
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been released on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that a mouse action occurred in a component
     */
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Invoked when the mouse wheel is rotated<BR>
     * Pass event to the level editor panel
     * @param e event which indicates that the mouse wheel was rotated in a component
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    /**
	    This class stores level script commands.
		
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
    private class ScriptCommand 
    {
        int time;
        String command;
        String text;
        int x;
        int y;
        int width;
        int height;
        char direction;
        int timer;
    }
}
