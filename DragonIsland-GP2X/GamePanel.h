#ifndef __GamePanel_H__
#define __GamePanel_H__

#pragma once

#include "Player.h"
#include "Entity.h"
#include "Background.h"
#include "UserInterface.h"
#include "Tileset.h"
#include "Level.h"
#include "Header.h"
#include "Map.h"
#include "Point.h"
#include "Game.h"
#include "ScriptCommand.h"

#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;  

/**
 * @class GamePanel
 * @brief This class draws all graphics for the game and handles game logic and
 * collision detection between the player, objects and entities
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class GamePanel
{
public:
	GamePanel::GamePanel();
	void GamePanel::showTitleScreen();
	void GamePanel::newGame();
	void GamePanel::createPlayer();
	bool GamePanel::loadLevel(string gameFolder, int world, int level, int area);
	void GamePanel::closeLevel();
	void GamePanel::advanceLevel();
	void GamePanel::advanceBonus();
	void GamePanel::resetLevel();
	void GamePanel::updateSaveGameFile();
	void GamePanel::saveGame(int saveFileNumber);
	void GamePanel::loadSaveGame(int saveFileNumber);
	static void GamePanel::setMusic(int file);
	void GamePanel::addBlockExplosion(int x, int y);
	void GamePanel::addEntity(int sprite, char direction, int x, int y);
	Header* GamePanel::getHeader();
	int GamePanel::getPlayerLives();
	void GamePanel::recordMovement(string gameFolder, int world, int level, int area);
	void GamePanel::playbackMovement(string gameFolder, int world, int level, int area);
	void GamePanel::split(const string& s, char c, vector<string>& v);
	void GamePanel::loadScript();
	void GamePanel::paintComponent(SDL_Surface* g);
	void GamePanel::keyPressed(SDL_Event e);
	void GamePanel::keyReleased(SDL_Event e);
	void GamePanel::setControl(SDL_Event e, bool pressed);
	void GamePanel::mouseMoved(SDL_Event e);
	void GamePanel::mouseDragged(SDL_Event e);
	void GamePanel::mouseReleased(SDL_Event e);
	void GamePanel::mousePressed(SDL_Event e);
	void GamePanel::mouseWheelMoved(SDL_Event e);
	GamePanel::~GamePanel();

private:
	/** The user controllable player */
    Player* mPlayer;
    /** The NPC entities */
    vector<Entity*> mEntities; //http://www.daniweb.com/software-development/cpp/threads/190581
    /** The level background */
    Background* mBackground;
    /** The user interface */
    UserInterface* mUserInterface;
    /** The level tileset (terrain, blocks, scenery) */
    Tileset* mMapTileset;
    /** The level information */ 
    Level* mLevelInfo;
    /** The level header information */
    Header* mLevelHeader;
    /** The level map */    
    Map* mLevelMap;
    /** The camera position */ 
    Point* mCamera;
    /** The position of the end of level castle */
    Point* mCastlePosition;
	/** The save game information */
    Game* mSaveGameInfo;
    /** The double buffer graphics surface */
    SDL_Surface* back_buffer;
    /** The amount of earthquake to apply */
    int mEarthquakeTimer;  
    /** Shake the screen */
    bool mEarthquake;
    /** Is the player on a bonus level */
    bool mOnBonus;
    /** The file containing the players recording movement script */
    ifstream mRecordScript;
    /** The file containing the players playback movement script */
    vector<ScriptCommand> mPlaybackScript;
    /** Stores the last keypress variables for debug cheat menu */
    string mKeyBuffer;
    /** The script mode 0-normal, 1-recording, 2-playback, 3-scripted playback */
    int mScriptMode; 
    /** The time elapsed while running the script */
    int mScriptTimer;
    /** The pause between displaying speech */
    int mScriptCommandPause;  
};

#endif
