package com.citex.dragonisland.core.sprite;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import com.citex.dragonisland.core.util.FileIO;

/**
 * SpriteSheetDefinition.java
 * Stores sprite sheet description including the frame count, width and height.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class SpriteSheetDefinition {
	
	/** Index of the sprite. */
	public int mIndex;
	
	/** Description of the sprite. */
	public String mDescription;
	
	/** Frame width. */
	public int mFrameWidth;
	
	/** Frame height */
	public int mFrameHeight;
	
	/** Frame count */
	public int mFrameCount;
	
	/**
	 * Initialises a SpriteSheetDefinition object from a comma separated string.
	 * @param data Comma separated string containing sprite sheet definition.
	 */
	public SpriteSheetDefinition(String data) throws Exception {
		
        mIndex = Integer.parseInt(data.split(",")[0]);			
        mDescription = data.split(",")[1];
        mFrameWidth = Integer.parseInt(data.split(",")[2]);	
        mFrameHeight = Integer.parseInt(data.split(",")[3]);	 
        mFrameCount = Integer.parseInt(data.split(",")[4]);	        
        
	}
	
	/**
	 * Returns a list of sprite sheet definitions retrieved from a folder path.
	 * @return List of SpriteSheetDefinitions.
	 */
	public static ArrayList<SpriteSheetDefinition> getSpriteDefinitions(String path) throws IOException, Exception {
		
		// Initialise an array list to store the definitions.
		ArrayList<SpriteSheetDefinition> def = new ArrayList<SpriteSheetDefinition>();
		
	  	// Open the .ini file containing the sprite definitions.   
    	BufferedReader input = FileIO.openBufferedReader(path);
    	
    	// Loop through the sprite definitions.
        String line;
    	while ((line = input.readLine()) != null) {
            
    		// Add the definition to the list.
    		def.add(new SpriteSheetDefinition(line));
        }       
 
		return def;
		
	}
}
