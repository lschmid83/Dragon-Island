package com.citex.dragonisland.java;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;

/**
 * This class creates the main application window.
 * @author Lawrence Schmid
 */
public class Main {

    /** Main window. */
    public static GameFrame mFrame;

    /**
     * Main entry point of the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
    	
    	// Initialise the settings.
    	Settings.Mode = GameMode.JAVA;
    	Settings.ShowControls = false;
        
    	// Create assets folder.
		File cacheFolder = new File("assets/");
		if(!cacheFolder.exists())
			cacheFolder.mkdir();
		
    	// Create the game window.
    	mFrame = new GameFrame();
    	
    	// Add a shutdown hook.
    	Runtime.getRuntime().addShutdownHook(new Thread() {

    	    @Override
    	    public void run() {
    	        
    	    	// Save settings.
    	    	try {
					Settings.saveSettings(Settings.InternalStorageFolder + "settings.dat");
				} catch (IOException e) {
					e.printStackTrace();
				}
    	    }

    	});

    }
}
