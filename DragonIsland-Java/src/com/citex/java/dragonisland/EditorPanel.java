package com.citex.java.dragonisland;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Rectangle;

/**
    This class is used to create new and edit existing levels by adding 
    and removing map tiles and entities.
   
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

public class EditorPanel extends JPanel {

	/** The user controllable player */
    private SpriteSheet mPlayer;
    /** The NPC entities */
    private EntityCollection mEntities;
    /** The level background */
    private Background mBackground;
	/** The music */
    private Music mMusic;
    /** The level tileset (terrain, blocks, scenery) */
    private Tileset mTileset;
    /** The font used to display status information */
    private GameFont mFontConsoleFont;
    /** The level information */
    private Level mLevelInfo;
    /** The level header information */
    private Header mLevelHeader;
	/** The level map */
    private Map mLevelMap;
    /** The time elapsed between frames of animation */
    private int mAnimationTimer;
    /** The animation frame index */
    private int mAnimationFrame; 
	/** The camera position */
    private Point mCamera;
    /** The map tile information */
    private Tile mTile;
    /** The selected tile information */
    private TileDescription mTileInfo;
    /** The selected entity tile information */    
    private EntityDescription mEntityInfo;
    /** The number of times the mouse has been clicked */
    private int mClick;
    /** The current state of the level editor 0-nothing selected, 1-terrain, 2-blocks, 3-scenery, 4-entities */
    private int mEditorState;
    /** The description string of the current selection */
    private String mSelectionDescription;
    /** The mouse x coordinate */
    private int mMouseX;
    /** The mouse y coordinate */
    private int mMouseY;
    /** The mouse x2 coordinate when dragged */
    private int mMouseX2;
    /** The mouse y2 coordinate when dragged */
    private int mMouseY2;
    /** The mouse dragged selection rectangle */
    private Rectangle mMouseSelectionRect;
    /** The mouse map tile rectangle */
    private Rectangle mMouseTileRect;
    /** The mouse cursor rectangle */
    private Rectangle mMouseCursorRect;
    /** The mouse button that has been pressed */
    private int mMouseButton;
    /** Distinguishes the class when it is serialized and deserialized */
    public final static long serialVersionUID = 1000000;

    /**
     * Constructs the EditorPanel
     */
    public EditorPanel() {
        mPlayer = new SpriteSheet("res/chr/" + Settings.MainCharacter + "/0.bmp", 32, 32, 29);
        mSelectionDescription = "Select Object";
    }

    /**
     * Loads a level file for editing
     * @param path The level file path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
     */
    public void loadLevel(String path) {
        mLevelInfo = new Level(path);
        mClick = 0;
        mEditorState = 0;
        mMouseSelectionRect = new Rectangle();
        mTile = new Tile();
        mLevelHeader = mLevelInfo.getHeader();
        mLevelMap = mLevelInfo.toMap();
        mMouseTileRect = new Rectangle(0, 0, 16, 16);
        mMouseTileRect.setLocation(mLevelHeader.width, mLevelHeader.height);
        mCamera = new Point(0, (mLevelHeader.startY * 16) - 224);
        mEntities = new EntityCollection();
        mTileset = new Tileset(mLevelHeader.tileset0, mLevelHeader.tileset16, mLevelHeader.tileset32);
        setMusic(mLevelHeader.music);
        setBackground(mLevelHeader.bg);
        mMouseCursorRect = new Rectangle(0, 0, 5, 5);
        mMouseTileRect.setLocation(-100, -100);
        mMouseCursorRect.setLocation(-100, -100);
        mFontConsoleFont = new GameFont("speech");
        repaint();
    }

    /**
     * Saves a level which has been created using the level editor
     * @param path The file output path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
     */
    public void saveLevel(String path) {
        mLevelInfo.saveLevel(path + mLevelHeader.world + "." + mLevelHeader.level + "." + mLevelHeader.area + ".lvl");
    }
    
    /**
     *  Stop the currently playing music
     */
    public void stopMusic()
    {
    	Music.stopMusic();	
    }

    /**
     * Sets the level header information 
     * @param header The level header 
     */
    public void setHeader(Header header) {
        mLevelInfo.setHeader(header);
        mLevelHeader = mLevelInfo.getHeader();
        mBackground.setWidth(mLevelHeader.width);
        mBackground.setHeight(mLevelHeader.height);
    }

    /**
     * Returns the level header information
     * @return The level header
     */
    public Header getHeader() {
        return mLevelInfo.getHeader();
    }

    /**
     * Sets the tileset resource folder, update the level header and recreate
     * objects with the new tileset
     * @param tileset0 The block tileset (res/block)
     * @param tileset16 The terrain tileset (res/terrain)
     * @param tileset32 The scenery tileset (res/scenery)
     */
    public void setTileset(int tileset0, int tileset16, int tileset32) {
        mLevelHeader = mLevelInfo.getHeader();
        mLevelHeader.tileset0 = tileset0;
        mLevelHeader.tileset16 = tileset16;
        mLevelHeader.tileset32 = tileset32;
        mTileset = new Tileset(mLevelHeader.tileset0, mLevelHeader.tileset16, mLevelHeader.tileset32);
        mLevelInfo.setHeader(mLevelHeader);
    }

    /**
     * Sets the background and update the level header
     * @param new_bgImg[] The background image resource (res/bgr) 0-near, 1-middle, 2-far
     */
    public void setBackground(int fileNumber[]) {
        mBackground = new Background(fileNumber, mLevelHeader.width, mLevelHeader.height, mLevelHeader.bgSpeed, mLevelInfo.getHeader().bgAlign);
        if (fileNumber[2] == 0) //far layer not set use header background color
        {
            mBackground.setColor(mLevelHeader.bgColor);
        } else
        {
            mLevelHeader.bgColor = mBackground.getColor();
        }
        mBackground.setLayerSpeed(0, mLevelInfo.getHeader().bgSpeed[0]);
        mBackground.setLayerSpeed(1, mLevelInfo.getHeader().bgSpeed[1]);
        mBackground.setLayerSpeed(2, mLevelInfo.getHeader().bgSpeed[2]);
    }

    /**
     * Sets the speed of a background layer
     * @param layer[] The background layer 0-near, 1-middle 2-far
     * @param speed The horizontal scroll speed 0 - No scroll >0 - Set speed
     */
    public void setBackgroundSpeed(int layer, int speed) {
        mBackground.setLayerSpeed(layer, speed);
    }

    /**
     * Sets the alignment of the background
     * @param align The alignment of the background 0-Repeat x axis / 1-Repeat y axis (far layer)
     */
    public void setBackgroundAlign(int align) {
        mBackground.setAlign(align);
    }

    /**
     * Sets the background color
     * @param colour The new background color
     */
    public void setBackgroundColor(Color color) {
        mBackground.setColor(color);
    }

    /**
     * Sets the width of the background
     * @param width The width of the background
     */
    public void setBackgroundWidth(int width) {
        mBackground.setWidth(width);
    }

    /**
     * Sets the height of the background
     * @param height The height of the background
     */
    public void setBackgroundHeight(int height) {
        mBackground.setHeight(height);
    }

    /**
     * Sets the music
     * @param file The music resource file (res/snd)
     */
    public void setMusic(int file) {
        if (file != 0 && Settings.Music) {
            String path = System.getProperty("user.dir") + "\\res\\snd\\" + mLevelHeader.music + ".mp3";
            mMusic = new Music(path);
        } else {
            if (mMusic != null) {
                Music.stopMusic();
            }
        }
    }

    /**
     * Invoked when a key has been typed
     * @param e Event which indicates that a keystroke occurred in a component
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Invoked when a key has been released
     * @param e Event which indicates that a keystroke occurred in a component
    */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when a key has been pressed<BR>
     * Moves the camera around the map area, scroll through object/sprite selection
     * @param e Event which indicates that a keystroke occurred in a component
    */
    public void keyPressed(KeyEvent e) {
        int kc = e.getKeyCode();
        if (kc == KeyEvent.VK_LEFT) {
            mCamera.x -= 16;
        } else if (kc == KeyEvent.VK_RIGHT) {
            mCamera.x += 16;
        } else if (kc == KeyEvent.VK_UP) {
            mCamera.y -= 16;
        } else if (kc == KeyEvent.VK_DOWN) {
            mCamera.y += 16;
        } else if (kc == KeyEvent.VK_F1) {
            changeEditorState();
        } else if (kc == KeyEvent.VK_PAGE_UP) {
            if (mEditorState > 0 && mEditorState < 4) {
                nextObject();
            } else if (mEditorState == 4) {
                nextSprite();
            }
        } else if (kc == KeyEvent.VK_PAGE_DOWN) {
            if (mEditorState > 0 && mEditorState < 4) {
                previousObject();
            } else if (mEditorState == 4) {
                previousSprite();
            }
        } 
    }

    /**
     * Invoked when the mouse enters a component
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component<BR>
     * Sets the mouse and cursor rectangles x,y coordinates
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseMoved(MouseEvent e) {
        mMouseX = e.getX() + mCamera.x;
        mMouseY = e.getY() + mCamera.y;
        mMouseX = mMouseX / 16;
        mMouseY = mMouseY / 16;
        mMouseTileRect.setLocation(e.getX() / 16 * 16, e.getY() / 16 * 16);
        mMouseCursorRect.setLocation(e.getX(), e.getY());
    }

    /**
     * Invoked when the mouse exits a component<BR>
     * Sets the mouse and cursor rectangles x,y coordinates outside of the screen area
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseExited(MouseEvent e) {
        mMouseTileRect.setLocation(mLevelHeader.width, mLevelHeader.height);
        mMouseCursorRect.setLocation(mLevelHeader.width * 16, mLevelHeader.height * 16);
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged<BR>
     * Sets the size of the selection rectangle
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseDragged(MouseEvent e) {
        mMouseCursorRect.setLocation(e.getX(), e.getY());
        if (mEditorState > 0 && mEditorState < 4 && mMouseButton == 1) {
            mMouseX2 = e.getX() + mCamera.x;
            mMouseY2 = e.getY() + mCamera.y;
            mMouseX2 = mMouseX2 / 16 + 1;
            mMouseY2 = mMouseY2 / 16 + 1;
            if (mMouseX2 <= mMouseX) {
                mMouseX2 = mMouseX + 1;
            }
            if (mMouseY2 <= mMouseY) {
                mMouseY2 = mMouseY + 1;
            }
            mMouseSelectionRect.setBounds(mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component<BR>
     * Adds an object to the map and sets the size of selection rectangle
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mouseReleased(MouseEvent e) {
        if (mEditorState > 0 && mEditorState < 4 && mMouseButton == 1) //add object
        {
            if (e.getButton() == 1) {
                mLevelInfo.addTile(mTile.tile1, mTile.tile2, mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
                //set box width/height if mouse dragged and x2,y2 are set
                if (mMouseX2 - mMouseX > 0 && mMouseY2 - mMouseY > 0) {
                    mMouseSelectionRect.setBounds(mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
                } else //set box width/height to tile size
                {
                    mMouseSelectionRect.setBounds(mMouseX, mMouseY, Integer.parseInt(mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[3]), Integer.parseInt(mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[4]));
                }
                mLevelMap = mLevelInfo.toMap();
                mMouseX2 = 0;
                mMouseY2 = 0;
            }
        }
        mMouseButton = 0;
    }

    /**
     * Invoked when a mouse button has been pressed on a component<BR>
     * Button1 - Adds / Selects an object or sprite and set position<BR>
     * Button2 - Changes the editor state 0-Select 1-Object 2-Sprite<BR>
     * Button3 - Removes an object or sprite</p>
     * @param e Event which indicates that a mouse action occurred in a component
     */
    public void mousePressed(MouseEvent e) {
        mMouseX = e.getX() + mCamera.x;
        mMouseY = e.getY() + mCamera.y;
        mMouseX = mMouseX / 16;
        mMouseY = mMouseY / 16;
        mMouseButton = e.getButton();
        if (mEditorState == 0 && e.getButton() == 1) {
            if (mClick == 0) //object is not selected
            {
                mTile = mLevelMap.getTile(mMouseX, mMouseY); //get tile information about map coordinate

                if (mTile.spriteTile == 0) //selected object
                {
                    mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
                    if (!mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2].equals("Null")) {
                        mTileInfo = mLevelInfo.getTileDescription(mTile.index);
                        if ((mTile.tile1 >= 1 && mTile.tile1 <= 3) && mTile.tile2 == 0) //castle / extension / flag pole base
                        {
                            mSelectionDescription = "Flag Pole";
                            mMouseSelectionRect.setBounds(mMouseX, mMouseY, 15, 10);
                            //level.removeCastle();
                            mLevelMap = mLevelInfo.toMap();
                            mMouseTileRect.setSize(15 * 16, 10 * 16);
                            // box.setBounds(header.Width * 16, header.Height * 16, 0, 0);
                        } else {
                            mMouseTileRect.setSize(mTileInfo.width * 16, mTileInfo.height * 16);
                            mMouseSelectionRect.setBounds(mTileInfo.x, mTileInfo.y, mTileInfo.width, mTileInfo.height);
                        }
                        mClick = 1;
                    } else {
                        mMouseTileRect.setSize(16, 16);
                        mClick = 0;
                        mMouseSelectionRect.setBounds(mLevelHeader.width * 16, mLevelHeader.height * 16, 0, 0);
                        mSelectionDescription = "Select Object";
                    }
                } else {
                    mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.spriteTile)[1];

                    if (!mLevelInfo.getEntityStringDescription(mTile.spriteTile)[1].equals("Null")) {
                        mEntityInfo = mLevelInfo.getEntityDescription(mTile.spriteIndex);

                        if (mTile.spriteTile >= 7 && mTile.spriteTile <= 9) //castle flag / flag / flag pole
                        {
                            mSelectionDescription = "Flag Pole";
                            //level.removeCastle();
                            mLevelMap = mLevelInfo.toMap();
                            mMouseTileRect.setSize(15 * 16, 10 * 16);
                            mMouseSelectionRect.setBounds(mMouseX, mMouseY, 15, 10);
                            //box.setBounds(header.Width * 16, header.Height * 16, 0, 0);
                        } else {
                            mMouseTileRect.setSize(mEntityInfo.tileWidth * 16, mEntityInfo.tileHeight * 16);
                            mMouseSelectionRect.setBounds(mEntityInfo.x, mEntityInfo.y, mEntityInfo.tileWidth, mEntityInfo.tileHeight);
                        }
                        mClick = 1;
                    } else {
                        mMouseTileRect.setSize(16, 16);
                        mClick = 0;
                        mMouseSelectionRect.setBounds(mLevelHeader.width * 16, mLevelHeader.height * 16, 0, 0);
                        mSelectionDescription = "Select Object";
                    }
                }

                //change start state on mouse press
                if ((mMouseX >= mLevelHeader.startX && mMouseX <= mLevelHeader.startX + 1)
                        && (mMouseY >= mLevelHeader.startY && mMouseY <= mLevelHeader.startY + 1)) {
                    if (mLevelHeader.startState < 5) {
                        mLevelHeader.startState++;
                    } else {
                        mLevelHeader.startState = 0;
                    }
                }

                //change end state on mouse press
                if ((mMouseX >= mLevelHeader.endX && mMouseX <= mLevelHeader.endX + 1)
                        && (mMouseY >= mLevelHeader.endY && mMouseY <= mLevelHeader.endY + 1)) {
                    if (mLevelHeader.endState < 5) {
                        mLevelHeader.endState++;
                    } else {
                        mLevelHeader.endState = 0;
                    }
                }

                //change bonus state on mouse press
                if ((mMouseX >= mLevelHeader.bonusX && mMouseX <= mLevelHeader.bonusX + 1)
                        && (mMouseY >= mLevelHeader.bonusY && mMouseY <= mLevelHeader.bonusY + 1)) {
                    if (mLevelHeader.bonusState < 5) {
                        mLevelHeader.bonusState++;
                    } else {
                        mLevelHeader.bonusState = 0;
                    }
                }

            } else //object has been selected and moved position
            {
                if (mSelectionDescription.equals("Flag Pole")) //add additional end of level objects
                {
                    mLevelInfo.removeCastle();
                    mLevelInfo.addEntity(8, mMouseX - 1, mMouseY); //flag
                    mLevelInfo.addEntity(9, mMouseX, mMouseY); //flag pole
                    mLevelInfo.addTile(3, 0, mMouseX, mMouseY + 9, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //flag pole base
                    mLevelInfo.addEntity(7, mMouseX + 10, mMouseY + 1); //castle flag
                    mLevelInfo.addTile(1, 0, mMouseX + 8, mMouseY + 4, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle
                    mLevelInfo.addTile(2, 0, mMouseX + 11, mMouseY + 7, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle extension
                    mMouseSelectionRect.setBounds(mMouseX, mMouseY, 15, 10);
                    mSelectionDescription = "";
                } else {
                    if (mTile.spriteTile == 0) //object
                    {
                        mTileInfo.x = mMouseX;
                        mTileInfo.y = mMouseY;
                        mMouseSelectionRect.setBounds(mTileInfo.x, mTileInfo.y, mTileInfo.width, mTileInfo.height);

                    } else //sprite
                    {
                        mEntityInfo.x = mMouseX;
                        mEntityInfo.y = mMouseY;
                        mLevelInfo.setEntityDescription(mTile.spriteIndex, mEntityInfo);
                        mMouseSelectionRect.setBounds(mEntityInfo.x, mEntityInfo.y, mEntityInfo.tileHeight, mEntityInfo.tileHeight);
                    }
                }
                mMouseTileRect.setSize(16, 16);

                mClick = 0;
                mLevelMap = mLevelInfo.toMap();
            }
        } else if (mEditorState == 4 && e.getButton() == 1) //add sprite
        {
            if (mSelectionDescription.equals("Flag Pole")) //add additional end of level objects
            {
                mLevelInfo.removeCastle();
                mLevelInfo.addEntity(8, mMouseX - 1, mMouseY); //flag
                mLevelInfo.addEntity(9, mMouseX, mMouseY); //flag pole
                mLevelInfo.addTile(3, 0, mMouseX, mMouseY + 9, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //flag pole base
                mLevelInfo.addEntity(7, mMouseX + 10, mMouseY + 1); //castle flag
                mLevelInfo.addTile(1, 0, mMouseX + 8, mMouseY + 4, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle
                mLevelInfo.addTile(2, 0, mMouseX + 11, mMouseY + 7, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle extension
                mMouseSelectionRect.setBounds(mMouseX, mMouseY, 15, 10);
            } else {
                mLevelInfo.addEntity(mTile.tile1, mMouseX, mMouseY);
                mMouseSelectionRect.setBounds(mMouseX, mMouseY, Integer.parseInt(mLevelInfo.getEntityStringDescription(mTile.tile1)[2]), Integer.parseInt(mLevelInfo.getEntityStringDescription(mTile.tile1)[3]));
            }
            mLevelMap = mLevelInfo.toMap();
        }

        if (e.getButton() == 2) //change editor state
        {
            changeEditorState();
        }

        if (e.getButton() == 3) //remove object or sprite
        {
            Tile del = mLevelMap.getTile(mMouseX, mMouseY);
            if (del.spriteTile == 0) //object
            {
                if (!mLevelInfo.getTileStringDescription(del.tile1, del.tile2)[2].equals("Null")) {
                    if ((del.tile1 >= 1 && del.tile1 <= 3) && del.tile2 == 0) //castle / extension / flag pole base
                    {
                        mLevelInfo.removeCastle();
                    } else {
                        mLevelInfo.removeTile(del.index);
                    }
                }
            } else //sprite
            {
                if (!mLevelInfo.getEntityStringDescription(del.spriteTile)[1].equals("Null")) {
                    if (del.spriteTile >= 7 && del.spriteTile <= 9) //castle flag / flag / flag pole
                    {
                        mLevelInfo.removeCastle();
                    } else {
                        mLevelInfo.removeEntity(del.spriteIndex);
                    }
                }
            }
            mMouseTileRect.setSize(16, 16);
            mClick = 0;
            mLevelMap = mLevelInfo.toMap();
            mMouseSelectionRect.setBounds(mLevelHeader.width, mLevelHeader.height, 0, 0);
        }
    }

    /**
     * Invoked when the mouse wheel is rotated<BR>
     * Scrolls through object or sprite selection
     * @param e Event which indicates that the mouse wheel was rotated in a component
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        int wheelRotation = e.getWheelRotation();
        if (mEditorState > 0 && mEditorState < 4) {
            if (wheelRotation < 0) //scroll up
            {
                nextObject();
            } else //scroll down
            {
                previousObject();
            }
        } else if (mEditorState == 4) {
            if (wheelRotation < 0) //scroll up
            {
                nextSprite();
            } else //scroll down
            {
                previousSprite();
            }
        }
    }

    /**
     * Changes the editor state 0-Tile Selection 1-Terrain, 2-Block, 3-Scenery 4-Entities
     */
    public void changeEditorState() {
        mTile.tile1 = 1;
        if (mEditorState < 4) {
            mEditorState++;
        } else {
            mEditorState = 0;
        }
        switch (mEditorState) {
            case 1: mTile.tile2 = 16; break;
            case 2: mTile.tile2 = 0; mTile.tile1 = 20; break;
            case 3: mTile.tile2 = 32; break;
        }
        if (mEditorState == 4) {
            mTile.tile1 = 30;
            mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.tile1)[1];
        } else {
            if (mEditorState != 0) {
                mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
            } else {
                mSelectionDescription = "Select Object";
            }
        }
    }

    /** Gets the string description of the players animation state 
     * @param state The index of the players animation
     * @return The string description of the players animation state
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
     * Moves to the next object in the tileset
     */
    public void nextObject() {
        mTile.tile1++;
        mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
        while (mSelectionDescription.equals("Null")) {
            if (mTile.tile1 < 100) {
                mTile.tile1++;
            } else {
                if (mTile.tile2 == 0) {
                    mTile.tile1 = 10;
                } else {
                    mTile.tile1 = 0;
                }
            }
            mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
        }
    }

    /**
     * Return to the previous object in the tileset
     */
    public void previousObject() {
        mTile.tile1--;
        mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
        int minTile = 0;
        if (mTile.tile2 == 0) {
            minTile = 10;
        }
        while (mSelectionDescription.equals("Null")) {
            if (mTile.tile1 > minTile) {
                mTile.tile1--;
            } else {
                mTile.tile1 = 100;
            }
            mSelectionDescription = mLevelInfo.getTileStringDescription(mTile.tile1, mTile.tile2)[2];
        }
    }

    /**
     * Moves forward to the next sprite
     */
    public void nextSprite() {
        mTile.tile1++;
        mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.tile1)[1];
        while (mSelectionDescription.equals("Null")) {
            if (mTile.tile1 < mEntities.getSize()) {
                mTile.tile1++;
            } else {
                mTile.tile1 = 9;
            }
            mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.tile1)[1];
        }
    }

    /**
     * Return to the previous sprite
     */
    public void previousSprite() {
        mTile.tile1--;
        if (mLevelInfo.getEntityStringDescription(mTile.tile1)[1].equals("End Flag")) {
            mTile.tile1 = mEntities.getSize();
        }
        mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.tile1)[1];
        while (mSelectionDescription.equals("Null")) {
            if (mTile.tile1 > 0) {
                mTile.tile1--;
            } else {
                mTile.tile1 = mEntities.getSize();
            }
            mSelectionDescription = mLevelInfo.getEntityStringDescription(mTile.tile1)[1];
        }
    }

    /**
     * Draws the level editor graphics object/entity collection and background
     * @param g The graphics context
     */
    protected void paintComponent(Graphics2D g) {

        //g.setColor(mBackground.getColor());
        //g.fillRect(0, 0, 480, 272);
        mBackground.draw(g, mCamera);
        mTileset.drawEditorMap(g, mLevelMap, mCamera);
        mEntities.draw(g, mLevelMap, mCamera);
        drawGrid(g);
        if (mEditorState == 0) {
            if (Settings.State.equals("level editor")) {
                g.setColor(new Color(255, 1, 1, 120));
                g.draw(mMouseTileRect);
            }
        } else if (mEditorState > 0 && mEditorState < 4) {
            if (Map.isTiledTerrain(mTile.tile1, mTile.tile2)) {
                g.drawImage(mTileset.getFrame(mTile.tile1, mTile.tile2, mTile.tile3), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY(), this);
            } else {
                if (Map.isCoinBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mTileset.getFrame(10, 0, 0), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.is10CoinBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mTileset.getFrameIndex(10, 0, 0, 3), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.isEnlargeBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mEntities.getEntity(10, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.isFireBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mEntities.getEntity(11, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.isInvincibleBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mEntities.getEntity(12, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.isExtraLifeBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mEntities.getEntity(13, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                } else if (Map.isVineBlock(mTile.tile1, mTile.tile2)) {
                    g.drawImage(mEntities.getEntity(14, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() - 16, null);
                }
                g.drawImage(mTileset.getFrame(mTile.tile1, mTile.tile2, mTile.tile3), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY(), this);
            }
        } else if (mEditorState == 4) {
            g.drawImage(mEntities.getEntity(mTile.tile1, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY(), this);
        }

        //drag catle and flag pole
        if (mSelectionDescription.equals("Flag Pole") && mEditorState == 4)
        {
            //flag
            g.drawImage(mEntities.getEntity(8, 'l'), (int) mMouseTileRect.getX() - (1 * 16), (int) mMouseTileRect.getY(), this);
            //flag pole
            g.drawImage(mEntities.getEntity(9, 'l'), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY(), this);
            //flag pole base
            g.drawImage(mTileset.getFrame(3, 0, 0), (int) mMouseTileRect.getX(), (int) mMouseTileRect.getY() + (9 * 16), this);
            //castle flag
            g.drawImage(mEntities.getEntity(7, 'l'), (int) mMouseTileRect.getX() + (10 * 16), (int) mMouseTileRect.getY() + (1 * 16), this);
            //castle
            g.drawImage(mTileset.getFrame(1, 0, 0), (int) mMouseTileRect.getX() + (8 * 16), (int) mMouseTileRect.getY() + (4 * 16), this);
        }

        if (Settings.Animation) {
            if (mAnimationTimer < 10) {
                mAnimationTimer++;
            } else {
                if (mAnimationFrame < 5) {
                    mAnimationFrame++;
                } else {
                    mAnimationFrame = 0;
                }
                mAnimationTimer = 0;
            }
        }

        if (Settings.State.equals("level editor")) {
            g.setColor(new Color(255, 1, 1, 120));
            g.drawRect((int) mMouseSelectionRect.getX() * 16 - mCamera.x, (int) mMouseSelectionRect.getY() * 16 - mCamera.y,
                    (int) mMouseSelectionRect.getWidth() * 16, (int) mMouseSelectionRect.getHeight() * 16);
            g.fillRect(mMouseCursorRect.x, mMouseCursorRect.y, 5, 5);
        }
        g.setColor(new Color(165, 50, 33));

        //start posiiton
        g.drawImage(mPlayer.getFrame(mAnimationFrame, 'r'), mLevelHeader.startX * 16 - mCamera.x + 2, mLevelHeader.startY * 16 - mCamera.y, this);
        mFontConsoleFont.drawString(g, "Start", mLevelHeader.startX * 16 - mCamera.x + 5, mLevelHeader.startY * 16 - mCamera.y - 17);
        int startPosX = (mLevelHeader.startX * 16 - mCamera.x) - mFontConsoleFont.getStringWidth(getStateDesc(mLevelHeader.startState)) / 2;
        mFontConsoleFont.drawString(g, getStateDesc(mLevelHeader.startState), startPosX + 17, mLevelHeader.startY * 16 - mCamera.y - 5);

        //end position
        if (mLevelHeader.endX != 0 && mLevelHeader.endY != 0) {
            g.drawImage(mPlayer.getFrame(mAnimationFrame, 'r'), mLevelHeader.endX * 16 - mCamera.x + 2, mLevelHeader.endY * 16 - mCamera.y, this);
            mFontConsoleFont.drawString(g, "End", mLevelHeader.endX * 16 - mCamera.x + 7, mLevelHeader.endY * 16 - mCamera.y - 17);
            int endPosX = (mLevelHeader.endX * 16 - mCamera.x) - mFontConsoleFont.getStringWidth(getStateDesc(mLevelHeader.endState)) / 2;
            mFontConsoleFont.drawString(g, getStateDesc(mLevelHeader.endState), endPosX + 14, mLevelHeader.endY * 16 - mCamera.y - 5);
        }

        //bonus position
        if (mLevelHeader.bonusX != 0 && mLevelHeader.bonusY != 0) {
            g.drawImage(mPlayer.getFrame(mAnimationFrame, 'r'), mLevelHeader.bonusX * 16 - mCamera.x + 2, mLevelHeader.bonusY * 16 - mCamera.y, this);
            mFontConsoleFont.drawString(g, "Bonus", mLevelHeader.bonusX * 16 - mCamera.x + 2, mLevelHeader.bonusY * 16 - mCamera.y - 17);
            int bonusPosX = (mLevelHeader.bonusX * 16 - mCamera.x) - mFontConsoleFont.getStringWidth(getStateDesc(mLevelHeader.bonusState)) / 2;
            mFontConsoleFont.drawString(g, getStateDesc(mLevelHeader.bonusState), bonusPosX + 15, mLevelHeader.bonusY * 16 - mCamera.y - 5);
        }

        if (Settings.State.equals("level editor")) {
            if (mSelectionDescription == null || mSelectionDescription.equals("Select Object")) {
                mFontConsoleFont.drawString(g, "x-" + (mMouseX + 1) + " y-" + (mMouseY + 1), 5, 6);
                mFontConsoleFont.drawString(g, "Left Button   - Select Object", 61, 6);
                mFontConsoleFont.drawString(g, "Right Button  - Remove Object", 61, 17);
                mFontConsoleFont.drawString(g, "Middle Button", 61, 28);
                mFontConsoleFont.drawString(g, "- Select Tileset", 135, 28);
            } else {
                mFontConsoleFont.drawString(g, "x-" + (mMouseX + 1) + " y-" + (mMouseY + 1) + " " + mSelectionDescription, 5, 6);
            }
        }
    }

    /**
     * Draws a 16x16 line grid overlay
     * @param g The graphics context
     */
    public void drawGrid(Graphics g) {
        g.setColor(new Color(255, 255, 255, 50));
        for (int i = mCamera.x / 16; i < mCamera.x + 480 / 16; i++) //horizontal
        {
            g.drawLine((16 * i) - mCamera.x, 0, (16 * i) - mCamera.x, mLevelHeader.height);
        }
        for (int i = mCamera.y / 16; i < mCamera.y + 272 / 16; i++) //vertical
        {
            g.drawLine(0, (16 * i) - mCamera.y, mLevelHeader.width, (16 * i) - mCamera.y);
        }
    }
}
