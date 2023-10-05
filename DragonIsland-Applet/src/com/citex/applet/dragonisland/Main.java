package com.citex.applet.dragonisland;

import java.awt.*;
import javax.swing.*;
import java.net.URL;

/**
	This class create the main application window.
	
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

public class Main extends JApplet
{
	/** The applet code base */
    public static URL cb;
	/** The game panel containing the main game loop */
    public static GamePanel mGamePanel;	
    /** Distinguishes the class when it is serialized and deserialized */
    public final static long serialVersionUID = 1000000;
	
    /**
     * Applet initialization
     */
    public void init () {
      	cb = getCodeBase();
    	Container content_pane = getContentPane ();
        mGamePanel = new GamePanel();        
    	content_pane.add (mGamePanel);
    	mGamePanel.start(); 
    }        
} 
