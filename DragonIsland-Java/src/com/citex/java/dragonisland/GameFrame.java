package com.citex.java.dragonisland;

import java.awt.*;
import javax.swing.*;

/**
    This class creates the main application window containing the game and level editor panels.
 
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

public class GameFrame extends JFrame {

	/** The game panel containing the main game loop */
    public GamePanel mGamePanel;
    /** The level editor panel containing the level editor code */
    public EditorPanel mEditorPanel;
    /** Distinguishes the class when it is serialized and deserialized */
    public final static long serialVersionUID = 2000000;

    /**
     * Constructs the GameFrame
     */
    public GameFrame() {
        
    	setTitle("Dragon Island");
        setSize(494, 309);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mGamePanel = new GamePanel();        
        mGamePanel.start(); 
        add(mGamePanel, BorderLayout.CENTER);
        
        centerScreen();
        setResizable(true);
        setVisible(true);

      }

    /**
     * Centres the main game frame
     */
    public void centerScreen() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = ((toolkit.getScreenSize().width - getWidth()) / 2);
        int y = ((toolkit.getScreenSize().height - getHeight()) / 2);
        setLocation(x, y);

    }

    /**
     * Starts the level editor
     */
    public void startEditor() {
        Main.mFrame.mGamePanel.stopMusic();
        mEditorPanel = new EditorPanel();
        Settings.LevelEditor = true;
    }

    /**
     * Stops the level editor
     */
    public void stopEditor() {
        mEditorPanel = null;
        Settings.LevelEditor = false;
    }
}
