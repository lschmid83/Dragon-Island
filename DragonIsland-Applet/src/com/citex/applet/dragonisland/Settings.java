package com.citex.applet.dragonisland;

/**
	This class stores global setting variables for the program.
	
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

public class Settings {

    /** Enable music playback */
    public static boolean Music = true;
    /** Enable sound effects */
    public static boolean Sound = true;
    /** Enable the debug menu */
    public static boolean DebugMenu = false;
    /** Enable level select */
    public static boolean LevelSelect = false;
    /** Enable player invincibility */
    public static boolean Invincible = false;
    /** Enable unlimited time */
    public static boolean FreezeTime = false;
    /** Enable unlimited player lives */
    public static boolean InfiniteLives = false;
    /** Enable player power-ups */
    public static boolean Powerups = true;
    /** Enable sprite animations */
    public static boolean Animation = true;
    /** Enable level backgrounds */
    public static boolean Background = true;
    /** Remove all enemies from levels */
    public static boolean RemoveEnemies = false;
    /** The folder number of the players sprite sheet */
    public static int MainCharacter = 1;
    /** The maximum jump height of the player */
    public static int JumpHeight = 65;
    /** Limit the x coordinate of the camera */    
    public static int LimitCameraX = 240;
    /** Limit the y coordinate of the camera */
    public static int LimitCameraY = 136;
    /** The current game state */
    public static String State = "title"; 
    /** Is the level editor running */
    public static boolean LevelEditor = false; 
    /** Is the game paused */
    public static boolean Paused = false;
    /** The save file game progress information */
    public static SaveFile File = new SaveFile("res/sav/");
    /** The file number of currently selected save game */
    public static int SaveFileNumber = 0;
    /** The time elapsed between repainting graphics */
    public static int GameSpeed = 9;
    /** Draw a bounding rectangle around sprites */
    public static boolean DebugMode = false;
    /** Allow the main game levels to be edited */    
    public static boolean EditMainGame = true;
    /** The game version id */
    public static String GameVersion = "1-0a";
    /** The music file to test playback of */
    public static int MusicTest = 0;
    /** The sound effect to test playback of */
    public static int SoundTest = 0; 
}
