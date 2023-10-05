#ifndef __EditorPanel_H__
#define __EditorPanel_H__

#pragma once

#include "SpriteSheet.h"
#include "EntityCollection.h"
#include "Background.h"
#include "Music.h"
#include "Tileset.h"
#include "GameFont.h"
#include "Level.h"
#include "Header.h"
#include "Map.h"
#include "Point.h"
#include "Tile.h"
#include "TileDescription.h"
#include "EntityDescription.h"
#include "Rectangle.h"
#include "Color.h"
#include "Control.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <oslib/oslib.h>

using namespace std;  

/**
 * @class EditorPanel
 * @brief This class is used to edit new and existing levels by adding and
 * removing map tiles and entities
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class EditorPanel
{
public:
	EditorPanel();
	void loadLevel(string path);
	void closeLevel();
	void saveLevel(string path);
	void setHeader(Header* header);
	Header* getHeader();
	void setTileset(int tileset0, int tileset16, int tileset32);
	void setBackground(int bgImg[3]);
	void setBackgroundSpeed(int layer, int speed);
	void setBackgroundAlign(int align);
	void setBackgroundColor(Color* color);
	void setBackgroundWidth(int width);
	void setBackgroundHeight(int height);
	void setMusic(int file);
	void keyPressed(OSL_CONTROLLER e);
	void keyReleased(OSL_CONTROLLER e);
	void mouseMoved(OSL_CONTROLLER e);
	void mouseDragged(OSL_CONTROLLER e);
	void mouseReleased(OSL_CONTROLLER e);
	void mousePressed(OSL_CONTROLLER e);
	void mouseWheelMoved(OSL_CONTROLLER e);
	void changeEditorState();
	string getStateDesc(int state);
	void nextObject();
	void previousObject();
	void nextSprite();
	void previousSprite();
	void paintComponent(); 
	void drawGrid();
	void setControl(OSL_CONTROLLER e, bool pressed);
	~EditorPanel();

private:
	/** The user controllable player */
    SpriteSheet* mPlayer;
    /** The NPC entities */
    EntityCollection* mEntities;
    /** The level background */
    Background* mBackground;
    /** The level tileset (terrain, blocks, scenery) */
    Tileset* mTileset;
    /** The font used to display status information */
    GameFont* mFontConsoleFont;
    /** The level information */
    Level* mLevelInfo;
    /** The level header information */
    Header* mLevelHeader;
	/** The level map */
    Map* mLevelMap;
    /** The time elapsed between frames of animation */
    int mAnimationTimer;
    /** The animation frame index */
    int mAnimationFrame; 
	/** The time elapsed between control repeats */
	int mControlTimer;
	/** The camera position */
    Point* mCamera;
    /** The map tile information */
    Tile* mTile;
    /** The selected tile information */
    TileDescription* mTileInfo;
    /** The selected entity tile information */    
    EntityDescription* mEntityInfo;
    /** The number of times the mouse has been clicked */
    int mClick;
    /** The current state of the level editor 0-nothing selected, 1-terrain, 2-blocks, 3-scenery, 4-entities */
    int mEditorState;
    /** The description string of the current selection */
    string mSelectionDescription;
    /** The tile x coordinate */
    int mMouseX;
    /** The tile y coordinate */
    int mMouseY;
    /** The mouse x coordinate */
    int mX;
    /** The mouse y coordinate */
    int mY;		
    /** The mouse x2 coordinate when dragged */
    int mMouseX2;
    /** The mouse y2 coordinate when dragged */
    int mMouseY2;
    /** The mouse dragged selection rectangle */
    Rectangle* mMouseSelectionRect;
    /** The mouse map tile rectangle */
    Rectangle* mMouseTileRect;
    /** The mouse cursor rectangle */
    Rectangle* mMouseCursorRect;
    /** The mouse button that has been pressed */
    int mMouseButton;
	/** The keyboard control state */
    Control* mControls;
	/** The mouse was dragged */
	bool mMouseDragged;

		Tile* tile;
		TileDescription* tileInfo;
		EntityDescription* entityInfo;
		

};

#endif
