package com.citex.dragonisland.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.os.Environment;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.android.drawing.GLSprite;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.GameFolder;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.level.Level;

/**
 * FileIO.java
 * Helper functions for handling file IO.
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

public class FileIO {

	/**
	 * Opens a buffered reader.
	 * @param path File path.
	 * @throws IOException
	 */
	public static BufferedReader openBufferedReader(String path) throws IOException {
		
		InputStream is;
        is = MainActivity.mAssetManager.open(path);

    	return new BufferedReader(new InputStreamReader(is));   		
	}
		
	/**
	 * Reads a line from a file.
	 * @param path File path.
	 * @return String containing line read from the file.
	 * @throws IOException 
	 */
	public static String readLine(String path) throws IOException {
		
		String ln = null;
    	BufferedReader br = null;
    	InputStream is = null;
    	try {

    		// Open input stream.
      		is = MainActivity.mAssetManager.open(path);

        	// Open file.        	
            br = new BufferedReader(new InputStreamReader(is));
            
            // Read line.
            ln = br.readLine();

        } finally {
	        
        	// Close file.
        	if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	            }
	        }
	    }
    	
    	return ln;
	}

	/**
	 * Opens an image resources.
	 * @param g Graphics context.
	 * @param path File path.
	 * @return Object which implements IBufferedImage.
	 * @throws IOException 
	 * @throws Exception
	 */
	public static IBufferedImage getImageResource(Object g, String path) throws IOException {
	   	return new GLSprite((GL10)g, path);
	}
		
	/**
	 * Gets a list of folders at the specified path.
	 * @param path File path.
	 * @return List of folders at the specified path.
	 */
	public static ArrayList<String> getFolderList(String path) {
		
		// Initialise a list of folders.
	    ArrayList<String> folders = new ArrayList<String>();
	    
		// Loop through the files in the path.
	    File f = new File(path);
	    
	    // Folder does not exist.
	    File[] files = f.listFiles();
		if(files != null) {    
		    for (File inFile : files) {
		        
		    	// File is a directory.
		    	if (inFile.isDirectory()) {
		            if(inFile.getName() != "main")
		            	folders.add(inFile.getName());
		        }
		    }	
	
		    // Sort the list.
		    Collections.sort(folders);
	    }
	    
	    return folders;
	}		
	

    /**
     * Gets a list of files at the specified path.
     * @param path File path.
     * @param ext File extension.
     * @return List of files at the specified path
     */
	public static ArrayList<String> getFileList(String path, String ext) {

		// Initialise a list of folders.
	    ArrayList<String> folders = new ArrayList<String>();

	    // Loop through the files in the path.
	    File f = new File(path);
	    File[] files = f.listFiles();
	    for (File inFile : files) {
	        
	    	// File is a directory.
	    	if (!inFile.isDirectory()) {
	            if(inFile.getName().substring(inFile.getName().length() - 3, inFile.getName().length()).equals(ext))
	            	folders.add(inFile.getName());
	        }
	    }	

	    // Sort the list.
	    Collections.sort(folders);
	    
	    return folders;

	}	

    /**
     * Gets a list of files at the specified assets path.
     * @param path File path.
     * @param ext File extension.
     * @return List of files at the specified path.
     */
	public static ArrayList<String> getAssetsFileList(String path, String ext) {

		// Initialise a list of folders.
	    ArrayList<String> files = new ArrayList<String>();

	    // Read files from assets folder.
		try {
			String [] fileList = MainActivity.mAssetManager.list(path);
			for(String file : fileList) {
				File f = new File(path + file);
				if(!f.isDirectory()) {
					if(f.getName().substring(f.getName().length() - 3, f.getName().length()).equals(ext))
						files.add(file);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	    // Sort the list.
	    Collections.sort(files);
	    
	    return files;

	}		
			
	/**
	 * Loads a level file from the JAR or internal storage.
	 * @param gameFolder GameFolder object.
	 * @param fileName Level file name.
	 * @return RandomAccessFile.
	 * @throws IOException 
	 */	
	public static RandomAccessFile loadLevel(GameFolder gameFolder, String fileName) throws IOException {
				
		RandomAccessFile f = null;
		// Main game.
		if(gameFolder == null || gameFolder.getName() == "main") {

			// Load level from JAR assets.
			File cacheFile = FileIO.createAndroidCacheFile(MainActivity.mContext, "lvl/" + fileName);
			f = new RandomAccessFile(cacheFile, "r");

		}
		// User created level.
		else {

			// Initialise level file path.
			String path = Environment.getExternalStorageDirectory() + Settings.InternalStorageFolder +
						  "game/" + gameFolder.getName() + "/" + fileName;

			// Create cache file.
			File cacheFile = FileIO.createAndroidCacheFile(MainActivity.mContext, path);

			// Load level from internal storage.
			f = new RandomAccessFile(cacheFile, "r");
		}

		return f;
		
	}
	
	/**
	 * Loads a legacy level file from the JAR or internal storage.
	 * @param gameFolder GameFolder object.
	 * @param fileName Level file name.
	 * @return RandomAccessFile.
	 * @throws IOException 
	 */	
	public static BufferedReader loadLegacyLevel(GameFolder gameFolder, String fileName) throws IOException {
				
		BufferedReader f = null;

		// Main game.
		if(gameFolder == null || gameFolder.getName() == "main") {

			// Load level from JAR assets.
			File cacheFile = FileIO.createAndroidCacheFile(MainActivity.mContext, "lvl/" + fileName);
			f = new BufferedReader(new FileReader(cacheFile));

		}
		// User created level.
		else {

			// Initialise level file path.
			String path = Environment.getExternalStorageDirectory() + Settings.InternalStorageFolder +
						  "game/" + gameFolder.getName() + "/" + fileName;

			// Create cache file.
			File cacheFile = FileIO.createAndroidCacheFile(MainActivity.mContext, path);

			// Load level from internal storage.
			f = new BufferedReader(new FileReader(cacheFile));
		}

		return f;
		
	}	

	/**
	 * Saves a level file.
	 * @param gameFolder GameFolder object.
	 * @param level Level object.
	 * @param fileName Level file name.
	 * @throws IOException 
	 */
	public static void saveLevel(GameFolder gameFolder, Level level, String fileName) throws IOException {
		
		if(gameFolder.getName() == "main") {
	    	
    		// Save to assets folder.
			level.saveLevel(Settings.InternalStorageFolder + "lvl/" + fileName);
    		
    	} else {
    		
    		// Save level to internal storage.
    		if(Settings.Mode == GameMode.ANDROID) {
    			level.saveLevel(Environment.getExternalStorageDirectory() + Settings.InternalStorageFolder + 
    							"game/" + gameFolder.getName() + "/" + fileName);
    		} else {
    			
    			level.saveLevel(Settings.InternalStorageFolder + "game/" + gameFolder.getName() +  "/" + fileName);	
    		}    		
    		
    	}	

	}

	/**
	 * Creates a cache file from an asset which can be opened using RandomAccessFile.
	 * @param context Global information about an application environment.
	 * @param filename Filename.
	 * @return File object.
	 * @throws IOException 
	 */
	public static File createAndroidCacheFile(Context context, String filename) throws IOException {
		  
		File cacheFolder = new File(context.getCacheDir() + "/lvl/");
		if(!cacheFolder.exists())
			cacheFolder.mkdir();
		
		File cacheFile = new File(context.getCacheDir(), filename);
		cacheFile.createNewFile();

		InputStream inputStream = context.getAssets().open(filename);
		FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int length = -1;
		while ( (length = inputStream.read(buffer)) > 0) {
			fileOutputStream.write(buffer,0,length);
		}

		fileOutputStream.close();
		inputStream.close();

		return cacheFile;
	}

}
