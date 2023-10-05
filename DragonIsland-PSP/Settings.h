#ifndef __Settings_H__
#define __Settings_H__

#include "SaveFile.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;  

/**
 * @class Settings
 * @brief This class stores global setting variables for the program
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Settings
{
public:
	Settings();
    /** The width of the screen */
	static int ScreenWidth;
	/** The height of the screen */
    static int ScreenHeight;
    /** Enable sprite animations */
    static bool Animation;
    /** Enable level backgrounds */
    static bool Background;
    /** Draw a bounding rectangle around sprites */
    static bool DrawBounds;
    /** Enable unlimited time */
    static bool FreezeTime;
    /** The current game state */
    static string State; 
    /** The maximum jump height of the player */
    static int JumpHeight;
    /** Enable player invincibility */
    static bool Invincible;
    /** Enable unlimited player lives */
    static bool InfiniteLives;
    /** Enable player powerups */
    static bool Powerups;
    /** The folder number of the players sprite sheet */
    static int MainCharacter;
    /** The save file game progress information */
    static SaveFile* File; //save game
    /** Enable the debug menu */
    static bool DebugMenu;
    /** Enable music playback */
    static bool Music;
    /** Enable sound effects */
    static bool Sound;
    /** Is the game paused */
    static bool Paused;
    /** Enable level select */
    static bool LevelSelect;
    /** The file number of currently selected save game */
    static int SaveFileNumber;
    /** The time elapsed between repainting graphics */
    static int GameSpeed;
    /** The music file to test playback of */
    static int MusicTest;
    /** The sound effect to test playback of */
    static int SoundTest; 
    /** Remove all enemies from levels */
    static bool RemoveEnemies;
    /** Allow the main game levels to be edited */    
    static bool EditMainGame;
    /** The game version id */
    static string GameVersion;
    /** Is the level editor running */
    static bool LevelEditor;
    /** Limit the x coordinate of the camera */    
    static int LimitCameraX;
    /** Limit the y coordinate of the camera */
    static int LimitCameraY;
	/** Fullscreen mode */
	static bool Fullscreen;

};

#endif
