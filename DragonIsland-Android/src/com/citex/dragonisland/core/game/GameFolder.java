package com.citex.dragonisland.core.game;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.citex.dragonisland.core.util.FileIO;

/**
 * This class represents a game folder.
 * @author Lawrence Schmid
 */
public class GameFolder {

	/** Game folder name */
	private String mName;
	
	/** List of levels in game folder. */
	public ArrayList<String> mLevel;
	
	/** List of worlds containing level data. */
	public ArrayList<World> mWorlds;
	
	/**
	 * Initialise a GameFolder object.
	 * @param path Game folder path.
	 * @param name Folder name.
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public GameFolder(String path, String name) throws URISyntaxException, IOException {
		
		// Initialise game folder.
		mName = name;
		mLevel = new ArrayList<String>();
		mWorlds = new ArrayList<World>();
		
		// Get a list of the levels in the game folder.
		if(path != null) {
			
			if(name == "main") {
				
				if(Settings.Mode == GameMode.ANDROID) {
					
					// Get file list from assets.
					mLevel.addAll(FileIO.getAssetsFileList(path, "lvl"));
					
				}
				else {
					
					// Add command line parameter to run configuration to set this.
					if(Settings.EditMainGame && Settings.Mode != GameMode.APPLET) {
						
						// Get file list from assets folder.
						mLevel.addAll(FileIO.getFileList("assets/" + path, "lvl"));
						
					} else {
						
						// Get file list from JAR.
						mLevel.addAll(FileIO.getAssetsFileList("lvl/", "lvl"));
					}
				}
			
			}
			else
				mLevel.addAll(FileIO.getFileList(path + name + "/", "lvl"));
			
		}
		
		// Loop through level names and initialise world data.
		for(String level : mLevel) {
			
			try {
				// Get world and area number.
				int w = Integer.parseInt(level.split("\\.")[0]);
				int a = Integer.parseInt(level.split("\\.")[2]);
				
				// Not title screen or a bonus level.
				if(w > 0 && a == 1) {
					
					if(mWorlds.size() >= w) {
						
						// Add a level to the world.
						mWorlds.get(w - 1).addLevel();
						
					} else {
	
						// New world.
						mWorlds.add(new World());
					}
					
				}
			}
			catch(Exception e) {
				
				boolean test = false;
			}
		}
		
	}
	
	/**
	 * Gets the game folder name.
	 * @return Game folder name.
	 */
	public String getName()  {	
		return mName;
	}
	
	/**
	 * Gets a list of the level files in the game folder.
	 * @return List of level files.
	 */
	public ArrayList<String> getLevels() {
		return mLevel;
	}
	
	/**
	 * Gets world data containing level count.
	 * @return List of World objects.
	 */
	public ArrayList<World> getWorlds() {
		return mWorlds;
	}

}
