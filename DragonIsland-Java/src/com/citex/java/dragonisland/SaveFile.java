package com.citex.java.dragonisland;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
	This class reads and writes .sav file containing game progress data.
	
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

public class SaveFile {

	/** The game data */
    private Game mGame[];
    /** The file path of the save data */
    private String mPath;
	/** The list of level names in the game folder */
	public static ArrayList<String> mLevelName = new ArrayList<String>();
	/** The list of names of game folders containing the levels */
	public static ArrayList<String> mGameName = new ArrayList<String>();
	/** The index of the game folder containing the levels */
	public static int mGameNumber = 0;

	/**
	 * Constructs the SaveFile
	 */
    public SaveFile(String path) {
        mPath = path;
        loadFiles();
    }

	/**
	 * Loads all of the save game files
	 */
    public void loadFiles() {
    	mGame = new Game[5];
        for (int i = 0; i < 4; i++) {
            mGame[i] = loadFile(i);
        }
    }

	/**
	 * Deletes a save file from the memory card
	 * @param fileNumber The file number to delete
	 */
    public void deleteFile(int fileNumber) {
        File f = new File(mPath + fileNumber + ".sav");
        f.delete();
    }

	/**
	 * Loads a save file
	 * @param fileNumber The file number
	 * @return The game progress data
	 */   
    public Game loadFile(int fileNumber) {
        try {
            Game g = new Game();
            RandomAccessFile f = new RandomAccessFile(mPath + fileNumber + ".sav", "r");
            g.game = f.readInt();
            g.world = f.readInt();
            g.level = f.readInt();
            g.character = f.readInt();
            g.size = f.readInt();
            g.lives = f.readInt();
            g.coins = f.readInt();
            g.score = f.readInt();
            f.close();
            return g;

        } catch (Exception e) {
            return null;
        }
    }

	/**
	 * Saves the game progress
	 * @param fileNumber The file number
	 */  
    public void saveFile(int fileNumber) {
        try {
            RandomAccessFile f = new RandomAccessFile(mPath + fileNumber + ".sav", "rw");
            f.writeInt(mGame[fileNumber].game);
            f.writeInt(mGame[fileNumber].world);
            f.writeInt(mGame[fileNumber].level);
            f.writeInt(mGame[fileNumber].character);
            f.writeInt(mGame[fileNumber].size);
            f.writeInt(mGame[fileNumber].lives);
            f.writeInt(mGame[fileNumber].coins);
            f.writeInt(mGame[fileNumber].score);
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * Creates a new save game file
	 * @return The save game file
	 */  
    public Game newGame() {
        Game g = new Game();
        g.game = 1;
        g.world = 1;
        g.level = 1;
        g.character = 1;
        g.size = 0;
        g.lives = 3;
        g.coins = 0;
        g.score = 0;
        return g;
    }

	/**
	 * Gets the game progress data for a save file
	 * @param fileNumber
	 * @return The game progress information
	 */   
    public Game getGame(int fileNumber) {
        return mGame[fileNumber];
    }

	/**
	 * Sets the game progress file data for a save file
	 * @param fileNumber The file number
	 * @param game The game progress data
	 */   
    public void setGame(int fileNumber, Game game) {
        mGame[fileNumber] = game;
    }

	/**
	 * Returns the string description of the save game file
	 * @param fileNumber The file number
	 * @return The string description of the file
	 */   
    public String getDescription(int fileNumber) {
        if (mGame[fileNumber] == null) {
            return "Empty";
        } else {
            return SaveFile.mGameName.get(mGame[fileNumber].game) + " " + mGame[fileNumber].world
                    + " " + mGame[fileNumber].level;
        }
    }

    /**
     * Returns a list of the files at the specified path
     * @param path The file path
     * @return The list of files at the specified path
     */
	public static ArrayList<String> getFileList(String path) {
	    ArrayList<String> list = new ArrayList<String>();
	    File folder = new File(path);
	    File[] files = folder.listFiles();
	    if (files != null) {
	        for (int i = 0; i < files.length; i++) {
	            if (!files[i].isDirectory()) {
	                String str = files[i].getName();
	                if (!str.substring(str.length() - 3, str.length()).equals(".ls")) {
	                    str = str.substring(0, str.length() - 4);
	                    list.add(str);
	                }
	            }
	        }
	        Collections.sort(list);
	        return list;
	    } else {
	        return null;
	    }
	}
	
	/**
	 * Returns a list of folders at the specified path
	 * @param path The file path
	 * @return The number of folders at the specified path
	 */
	public static ArrayList<String> getFolderList(String path) {
		
	    ArrayList<String> list = new ArrayList<String>();
	    File folder = new File(path);
	    File[] files = folder.listFiles();
	    for (int i = 0; i < files.length; i++) {
	        if (files[i].isDirectory()) {
	            list.add(files[i].getName());
	        }
	    }
	    Collections.sort(list);
	    return list;
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

	/**
	 * Sets the list of levels in the selected game folder
	 */
	public static void refreshLevelList() {
	    if (!mGameName.get(0).equals("Empty")) {
	        mLevelName = SaveFile.getFileList("res/lvl/" + mGameName.get(mGameNumber) + "/");
	    }
	}

	/**
	 * Sets the list of game folders in the level folder 
	 */
	public static void refreshGameList() {
	    mGameName = new ArrayList<String>();
	    mGameName = SaveFile.getFolderList("res/lvl/");
	    if (mGameName.size() == 0) {
	        mGameName.add("Empty");
	    }
	}
}

/**
	This class stores the game progress data.
	
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

class Game {
    int game;
    int world;
    int level;
    int character;
    int size;
    int lives;
    int coins;
    int score;
}
