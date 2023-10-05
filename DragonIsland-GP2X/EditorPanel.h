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
	EditorPanel::EditorPanel();
	void EditorPanel::loadLevel(string path);
	void EditorPanel::saveLevel(string path);
	void EditorPanel::setHeader(Header* header);
	Header* EditorPanel::getHeader();
	void EditorPanel::setTileset(int tileset0, int tileset16, int tileset32);
	void EditorPanel::setBackground(int bgImg[3]);
	void EditorPanel::setBackgroundSpeed(int layer, int speed);
	void EditorPanel::setBackgroundAlign(int align);
	void EditorPanel::setBackgroundColor(Color* color);
	void EditorPanel::setBackgroundWidth(int width);
	void EditorPanel::setBackgroundHeight(int height);
	void EditorPanel::setMusic(int file);
	void EditorPanel::keyPressed(SDL_Event e);
	void EditorPanel::keyReleased(SDL_Event e);
	void EditorPanel::mouseMoved(SDL_Event e);
	void EditorPanel::mouseDragged(SDL_Event e);
	void EditorPanel::mouseReleased(SDL_Event e);
	void EditorPanel::mousePressed(SDL_Event e);
	void EditorPanel::mouseWheelMoved(SDL_Event e);
	void EditorPanel::changeEditorState();
	string EditorPanel::getStateDesc(int state);
	void EditorPanel::nextObject();
	void EditorPanel::previousObject();
	void EditorPanel::nextSprite();
	void EditorPanel::previousSprite();
	void EditorPanel::paintComponent(SDL_Surface* g); 
	void EditorPanel::drawGrid(SDL_Surface* g);
	void EditorPanel::setControl(SDL_Event e, bool pressed);
	EditorPanel::~EditorPanel();

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
    TileDescription mTileInfo;
    /** The selected entity tile information */    
    EntityDescription* mEntityInfo;
    /** The number of times the mouse has been clicked */
    int mClick;
    /** The current state of the level editor 0-nothing selected, 1-terrain, 2-blocks, 3-scenery, 4-entities */
    int mEditorState;
    /** The description string of the current selection */
    string mSelectionDescription;
    /** The mouse x coordinate */
    int mMouseX;
    /** The mouse y coordinate */
    int mMouseY;
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
};

#endif
