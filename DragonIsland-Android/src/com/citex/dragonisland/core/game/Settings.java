package com.citex.dragonisland.core.game;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.citex.dragonisland.android.MainActivity;

/**
 * This class stores game settings.
 * @author Lawrence Schmid
 */
public class Settings {
	
	/** Width of the screen. */
	public static int ScreenWidth = 480;
	
	/** Height of the screen. */
	public static int ScreenHeight = 270;
	
	/** Enable music. */
	public static boolean Music = true;
	
	/** Enable sound effects. */
	public static boolean Sound = true;
	
	/** Enable the debug menu. */
	public static boolean DebugMenu = false;
	
	/** Enable level select. */
	public static boolean LevelSelect = false;
	
	/** Enables level editor. */
	public static boolean LevelEditor = false;
	
	/** Enable player invincibility. */
	public static boolean Invincible = false;
	
	/** Enable unlimited time. */
	public static boolean FreezeTime = false;
	
	/** Enable unlimited lives. */
	public static boolean InfiniteLives = false;
	
	/** Enable player power-ups */
	public static boolean Powerups = true;
	
	/** Speed multiplier. */
	public static float GameSpeed = 0;
	
	/** Music test index. */
	public static int MusicTest = 0;
	
	/** Sound test index. */
	public static int SoundTest = 0; 
	
	/** Enable animation. */
	public static boolean Animation = true;
	
	/** Draw backgrounds. */
	public static boolean Background = true;
	
	/** Remove enemies. */
	public static boolean RemoveEnemies = false;
	
	/** Draw collision bounds and debug output. */
	public static boolean DebugMode = false;
	
	/** Maximum jump height. */
	public static int JumpHeight = 75;
		    
	/** Edit main game levels. */    
	public static boolean EditMainGame = false;
	
	/** High scores. */
	public static int HighScore[] = new int[3]; 
	
	/** Game state. */
	public static String State = "title"; 
	
	/** Paused state. */
	public static boolean Paused = false;
	
	/** Show level select screen. */
	public static boolean ShowLevelSelect = false;
	
	/** Game version. */
	public static String GameVersion = "2.0";
	
	/** Resource path. */
	public static String ResourcePath = "";
	
	/** Internal storage path. (Android) */
	public static String InternalStorageFolder = "assets/";
	
	/** Game mode. */
	public static GameMode Mode;
	
	/** Show touch screen controls. */
	public static Boolean ShowControls = true;
	
	/** 
	 * Loads a settings file. 
	 * @param path Settings file path.
	 * @throws IOException 
	 */
	public static void loadSettings(String path) throws IOException {
				
		RandomAccessFile f = null;
    	try {
   
            // Open a random access file.
    		if(Settings.Mode == GameMode.ANDROID) {
    			f = new RandomAccessFile(MainActivity.mContext.getFilesDir() + path, "r");
    		}
    		else
    		{
    			f = new RandomAccessFile(path, "r");
    		}

            // Read game settings.
            ScreenWidth = f.readInt();
            ScreenHeight = f.readInt();
            Music = f.readBoolean();
            Sound = f.readBoolean();
            HighScore[0] = f.readInt();
            HighScore[1] = f.readInt();
            HighScore[2] = f.readInt();

        } 
    	finally {
    		
    		// Close file.
 	        if (f != null) {
 	            try {
 	                f.close();
 	            } catch (IOException e) {
 	            }
 	        }
 	    }
	}
	
	/**
	 * Saves a settings file.
	 * @param path Settings file path.
	 * @throws IOException 
	 */
	public static void saveSettings(String path) throws IOException {
		
    	// Check the save game folder exists.
		File mydir = null;
		if(Settings.Mode == GameMode.ANDROID) {
			mydir = new File(MainActivity.mContext.getFilesDir() + Settings.InternalStorageFolder);
		}
		else {
			mydir = new File(Settings.InternalStorageFolder);
		}
		if(!mydir.exists())
		    mydir.mkdirs();
		
    	RandomAccessFile f = null;
    	try {
    		if(Settings.Mode == GameMode.ANDROID) {
    			f = new RandomAccessFile(MainActivity.mContext.getFilesDir() + path, "rw");
    		}
    		else 
    		{
    			f = new RandomAccessFile(path, "rw");
    		}          
            // Write game settings.
            f.writeInt(ScreenWidth);
            f.writeInt(ScreenHeight);
            f.writeBoolean(Music);
            f.writeBoolean(Sound);
            f.writeInt(HighScore[0]);
            f.writeInt(HighScore[1]);
            f.writeInt(HighScore[2]);

    	}
    	finally { 
    		
    		// Close file.
 	        if (f != null) {
 	            try {
 	                f.close();
 	            } catch (IOException e) {
 	            }
 	        }
 	    }		

	}
	    
}