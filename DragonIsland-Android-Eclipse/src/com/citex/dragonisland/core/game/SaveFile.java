package com.citex.dragonisland.core.game;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.core.util.FileIO;

/**
	This class reads and writes save files and retrieves game folder data.
	@author Lawrence Schmid
*/
public class SaveFile {

	/** Save game data. */
    private ArrayList<GameData> mSaveGame;

    /** List of user created games. */
	private ArrayList<GameFolder> mGameFolder = new ArrayList<GameFolder>();
	
	/** Save file path. */
	private String mPath;
	
	/**
	 * Initialise a SaveFile object.
	 * @param path Save game data path.
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
    public SaveFile(String path) throws IOException, URISyntaxException {

    	// Initialise path.
    	mPath = path;
    	
    	// Initialise a list of user created games.
    	initGameList(path + "game/");  
    	
    	// Initialise save games for game folders.
    	mSaveGame = new ArrayList<GameData>();
    	for(GameFolder gameFolder : mGameFolder)
    		initSaveGame(gameFolder.getName() + ".sav");

    }
        
    /** 
     * Initialises a list of game folders from an external storage path.
     * @param path Game folders path.
     * @throws IOException 
     * @throws URISyntaxException 
     */
    private void initGameList(String path) throws URISyntaxException, IOException {
    	  		
	    // Add the main game.
	    mGameFolder = new ArrayList<GameFolder>();

	    // Read main game levels from assets.
	    mGameFolder.add(new GameFolder("lvl", "main"));			    
	   	    
	    // Get a list of folders at the path.
	    ArrayList<String> gameFolders = FileIO.getFolderList(path);
	    
	    // Create game folder objects for each game.
	    for(String gameFolder : gameFolders) { 		
	    	
	    	// Check game folder contains levels.
	    	GameFolder folder = new GameFolder(path, gameFolder);
	    	if(folder.getLevels().size() > 0)
	    		mGameFolder.add(folder);
	    }

    }

	/**
	 * Initialises a list of save game data.
	 * @throws IOException 
	 */
    private void initSaveGame(String path) throws IOException {

        GameData gameData = loadGameData(path);
    	if(gameData != null)
    		mSaveGame.add(gameData);
    	else
    		mSaveGame.add(new GameData());
    }
        
	/**
	 * Loads a save file
	 * @param Path Save game data path.
	 * @return GameData object.
	 * @throws IOException 
	 */   
    public GameData loadGameData(String fileName) {
        
    	RandomAccessFile f = null;

    	try {
        	
            GameData gameData = new GameData();
            
            // Open a random access file.
            if(Settings.Mode == GameMode.ANDROID) {
            	f = new RandomAccessFile(MainActivity.mContext.getFilesDir() + "save/" + fileName, "r");	
            }
            else {
            	f = new RandomAccessFile(mPath + "save/" + fileName, "r");
            }
            
            // Read game data.
            gameData.game = f.readInt();
            gameData.world = f.readInt();
            gameData.level = f.readInt();
            gameData.character = f.readInt();
            gameData.size = f.readInt();
            gameData.lives = f.readInt();
            gameData.coins = f.readInt();
            gameData.score = f.readInt();
            
            return gameData;

        } 
    	catch(IOException e) {
    		return null;
    	}
    	finally {
 	        if (f != null) {
 	            try {
 	                f.close();
 	            } catch (IOException e) {
 	            }
 	        }
 	    }
    } 
           
	/**
	 * Saves a game.
	 * @param path Save game file path.
	 * @param index Save game index.
	 * @param data GameData object.
	 * @throws IOException 
	 */  
    public void saveGameData(String fileName, GameData gameData) throws IOException {
       
    	// Check the save game folder exists.
    	File mydir = null;
    	if(Settings.Mode == GameMode.ANDROID) {
    		mydir = new File(MainActivity.mContext.getFilesDir() + "save/");
    	}
    	else {
    		mydir = new File(mPath + "save/");
    	}	
		if(!mydir.exists())
		    mydir.mkdirs();
    	
    	RandomAccessFile f = null;
    	
    	try {

    		// Open a random access file.
    		if(Settings.Mode == GameMode.ANDROID) {
    			f = new RandomAccessFile(MainActivity.mContext.getFilesDir() + "save/" + fileName, "rw"); // getFilesDir() for API 26
    		}
    		else {
    			
    			f = new RandomAccessFile(mPath + "save/" + fileName, "rw");
    		}
            
            // Write game data.
            f.writeInt(gameData.game);
            f.writeInt(gameData.world);
            f.writeInt(gameData.level);
            f.writeInt(gameData.character);
            f.writeInt(gameData.size);
            f.writeInt(gameData.lives);
            f.writeInt(gameData.coins);
            f.writeInt(gameData.score);

    	}
    	finally { 
 	        if (f != null) {
 	            try {
 	                f.close();
 	            } catch (IOException e) {
 	            }
 	        }
 	    }
    	
    }   

    /**
     * Gets a list of game folders.
     * @param includeMainGame Include the main game folder.
     * @return List of game folders.
     */
	public ArrayList<GameFolder> getGameFolders(boolean includeMainGame) {
	    
		// Initialise a list of game folders.
		ArrayList<GameFolder> gameFolders = new ArrayList<GameFolder>();
		
		// Include main game folder.
		if(includeMainGame) {			
			gameFolders = mGameFolder;	
		}
		else
		{
			// Remove main game folder.
			for(GameFolder folder : mGameFolder) {	
				if(folder.getName() != "main")	
					gameFolders.add(folder);
			}		
		}

		return gameFolders;
	}
	
	/**
	 * Gets game data.
	 * @param index Save game index.
	 * @return GameData object.
	 */   
    public GameData getSaveGame(int index) {
    	return mSaveGame.get(index);
    }  
    
    /**
     * Sets save game data.
     * @param index Save game index.
     * @param gameData GameData object.
     */
    public void setSaveGame(int index, GameData gameData) {
    	mSaveGame.set(index, gameData);    	
    }
	
	
    /**
     * Gets a list of save game data.
     * @return List of save game data..
     */
	public ArrayList<GameData> getSaveGames() {
	    return mSaveGame;
	}
    
	/**
	 * Returns the string description of the save game file
	 * @param fileNumber The file number
	 * @return The string description of the file
	 */   
    public String getSaveGameDescription(int index) {
        
    	if (index > mSaveGame.size() - 1) {
            return "Empty";
        } else {
            return mGameFolder.get(mSaveGame.get(index).game).getName() + " " + mSaveGame.get(index).world + " " + 
            					   mSaveGame.get(index).level;
        }
    }  	
	
	/** 
	 * Returns the number of files at the specified path
	 * @param path The file path
	 * @return The number of files at the specified path
	 */
	public static int getFileCount(String path) {
	    File folder = new File(path);
	    File[] files = folder.listFiles();
	    return files.length;
	}

	/**
	 * Returns the number of folders at the specified path
	 * @param path The file path
	 * @return The number of folders at the specified path
	 */
	public static int getFolderCount(String path) {
	    File folder = new File(path);
	    File[] files = folder.listFiles();
	    int count = 0;
	    for (int i = 0; i < files.length; i++) {
	        if (files[i].isDirectory()) {
	            count++;
	        }
	    }
	    return count;
	}
}

