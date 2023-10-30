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
import com.citex.dragonisland.applet.Main;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.GameFolder;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.java.drawing.Image;

/**
 * Helper functions for handling file IO between Java and Android versions.
 * @author Lawrence Schmid
 */
public class FileIO {

	/**
	 * Opens a buffered reader.
	 * @param path File path.
	 * @throws IOException
	 */
	public static BufferedReader openBufferedReader(String path) throws IOException {
		
		InputStream is;
    	if(Settings.Mode == GameMode.ANDROID)
            is = MainActivity.mAssetManager.open(path);	
        else
        	is = Main.class.getClassLoader().getResourceAsStream(path);
    
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
        	if(Settings.Mode == GameMode.ANDROID)
        		is = MainActivity.mAssetManager.open(path);
        	else
         		is = Main.class.getClassLoader().getResourceAsStream(path); 	  	
  
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
    	
		if(Settings.Mode == GameMode.ANDROID)
	    	return new GLSprite((GL10)g, path);	
		else
			return new Image(path).getImage();

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
	    if(Settings.Mode == GameMode.ANDROID) {
	    	
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
	    	
	    } else {

	    	CodeSource src = Main.class.getProtectionDomain().getCodeSource();
			if (src != null) {
			  URL jar = src.getLocation();
			  ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(jar.openStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while(true) {
			    
				ZipEntry e = null;
				try {
					e = zip.getNextEntry();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    if (e == null) {
			    	
			    	// Run with IDE

			    	// Get the URL of the JAR file.
			    	URL dirURL = Main.class.getClassLoader().getResource(path);
				      
					// File path.
					if (dirURL != null && dirURL.getProtocol().equals("file")) {
						try {
							String [] fileList = new File(dirURL.toURI()).list();
							for(String file : fileList) {
								File f = new File(path + file);
								if(!f.isDirectory()) {	
									if(f.getName().substring(f.getName().length() - 3, f.getName().length()).equals(ext))
										files.add(file);
								}
							}   
						} catch (URISyntaxException ex) {
							ex.printStackTrace();
						}
				    }				    	

			      break;
 
			      
			    }
			    String name = e.getName();
			    if (name.startsWith(path)) {

			    	// Get filename from path.
			    	Path p = Paths.get(name);
			    	String fn = p.getFileName().toString();
		    	
					if(name.substring(name.length() - 3, name.length()).equals(ext))
						files.add(fn);
			    }
			  }
			} 

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

		if(Settings.Mode == GameMode.ANDROID) { 

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

		} else {
         	 
        	// Main game.
			if(gameFolder == null || gameFolder.getName() == "main") {
         		
				if(!Settings.EditMainGame || Settings.Mode == GameMode.APPLET) {
         		
					// Create cache file.
					String path = "lvl/" + fileName;
					File cacheFile = FileIO.createJavaCacheFile(path, "assets/cache/");
					
					// Load level from internal storage.
					f = new RandomAccessFile(cacheFile, "r");
             		
                } else {
                 	
                 	// Load level from internal storage.
             		File file = new File(Settings.InternalStorageFolder + "lvl/" + fileName);
             		f = new RandomAccessFile(file, "rw");	
                 	
                }
 
			}
         	// User created level.
         	else {
         		
         		// Load level from internal storage.
         		File file = new File(Settings.InternalStorageFolder + "game/" + gameFolder.getName() + "/" + fileName);
         		f = new RandomAccessFile(file, "rw");
         		
         	}
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

		if(Settings.Mode == GameMode.ANDROID) { 

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

		} else {
         	 
        	// Main game.
			if(gameFolder == null || gameFolder.getName() == "main") {
         		
				if(!Settings.EditMainGame || Settings.Mode == GameMode.APPLET) {
         		
             		// Load level from JAR.
					File file = new File(Main.class.getClassLoader().getResource("lvl/" + fileName).getFile());
					f = new BufferedReader(new FileReader(file));
             		
                } else {
                 	
                 	// Load level from internal storage.
             		File file = new File(Settings.InternalStorageFolder + "lvl/" + fileName);
    				f = new BufferedReader(new FileReader(file));	
                 	
                }
 
			}
         	// User created level.
         	else {
         		
         		// Load level from internal storage.
         		File file = new File(Settings.InternalStorageFolder + "game/" + gameFolder.getName() + "/" + fileName);
				f = new BufferedReader(new FileReader(file));
         		
         	}
		}

		return f;
		
	}	

	/**
	 * Saves a level file.
	 * @param gameFolder GameFolder object.
	 * @param Level Level object.
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
	 * @param path File path.
	 * @param cache Cache folder path.
	 * @return File object.
	 * @throws IOException 
	 */
	public static File createJavaCacheFile(String path, String cache) throws IOException {
		  
		File cacheFolder = new File(cache);
		if(!cacheFolder.exists())
			cacheFolder.mkdir();
		
		for(File file: cacheFolder.listFiles()) file.delete();
		
		Path p = Paths.get(path);
		String file = p.getFileName().toString();
				
		File cacheFile = new File(cache + file);
		cacheFile.createNewFile();

		InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(path);
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
