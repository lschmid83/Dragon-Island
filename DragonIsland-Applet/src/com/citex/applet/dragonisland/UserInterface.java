package com.citex.applet.dragonisland;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
	This class draw screen overlays such as the title screen graphics, level opening and
	game status information.
	
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

public class UserInterface {

	/** The title screen graphic */
    private BufferedImage mTitleLogo;
    /** The small character icon displayed on the level opening screen */
    private BufferedImage mCharacterIcon;
    /** The pause options background */
    private BufferedImage mPauseBackground;
    /** The level opening background image */
    private BufferedImage mLevelOpening;
    /** The screen transition image */
    private BufferedImage[] mScreenTransition;
    /** The sprite sheet containing the numbers used in the game */
    private SpriteSheet mNumbers;
    /** The icons displayed in the status for score, live, time */
    private SpriteSheet mStatusIcons;
    /** The speech bubble sprite sheet used in the script sequences */
    private SpriteSheet mSpeechBubble;
    /** The font used in the level script speech sequences */
    private GameFont mSpeechFont;
    /** The main game font used in the title and pause screens */
    private GameFont mGameFont;
    /** The number of options in a menu */
    private int mNumberOfOptions;
    /** The currently selected option in a menu */
    private int mSelectedOption;
    /** The time elapsed between while the game is saving and credits scrolling */
    public static int mTimer;
    /** The level header information */   
    private Header mLevelHeader;
    /** The currently selected save file */
    private int mSelectedSaveFile;
    /** The name string for a new game folder */
    private String mNewGameName;
    /** Is the screen transitions being displayed */
    public boolean showTransition;
    /** The screen transition mode 0-horizontal, 1-vertical */
    private int mScreenTransitionMode;
    /** The time elapsed while showing a screen transition */
    private int mTransitionTimer;
    /** The text displayed in the credits screen */ 
    private ArrayList<String> mCreditsText;
    /** The amount the credits have scrolled in the y direction */
    private int mCreditScrollY;
    /** The pause while showing the level opening screen */
    private int mLevelOpenPause;   
  
    /**
     * Constructs the UserInterface
     */   
    public UserInterface() {
        mGameFont = new GameFont("main");
        mSpeechFont = new GameFont("speech");
        mNumbers = new SpriteSheet("res/gui/numbers.bmp", 16, 16, 10);
        mStatusIcons = new SpriteSheet("res/gui/status.bmp", 50, 17, 4);
        mSpeechBubble = new SpriteSheet("res/gui/speech.bmp", 16, 16, 7);
        mTitleLogo = new Image("res/gui/title.bmp").getImage();
        mPauseBackground = new Image("res/gui/pause.bmp").getImage();
        mLevelOpening = new Image("res/gui/level.bmp").getImage();
        mScreenTransition = new BufferedImage[2];
        mScreenTransition[0] = new Image("res/gui/transition1.bmp").getImage();
        mScreenTransition[1] = new Image("res/gui/transition2.bmp").getImage();
        setCharacter();
  
        mCreditsText = new ArrayList<String>();
        mCreditsText.add("Credits");
        mCreditsText.add("");
        mCreditsText.add("Programmer - Lawrence Schmid");  
    
        try 
        {
        	InputStream is = Main.class.getResourceAsStream("res/gui/credits.txt"); 
            BufferedReader input = new BufferedReader(new InputStreamReader(is));   
            String ln = null;
            while ((ln = input.readLine()) != null) {
                mCreditsText.add(ln);   
            }
        } catch (IOException ex) {}     
                
        mLevelOpenPause = 180;  
        mScreenTransitionMode = 1;
    }

    /**
     * Loads the main character icon based on the MainCharacter setting
     */
    public void setCharacter()
    {
        Image img = new Image("res/chr/" + Settings.MainCharacter + "/icon.bmp");
        mCharacterIcon = img.getImage();
    }

    /**
     * Draws the amount of remaining lives 
     * @param g The graphics context
     * @param amount The amount of lives
     * @param x The x coordinate
     * @param y The y coordinate
     */   
    public void drawLives(Graphics g, int amount, int x, int y) {
        //convert number into digits - http://stackoverflow.com/questions/5196186/split-int-value-into-seperate-digits
        int tmp = amount;
        int digit[] = new int[2];
        for (int i = 1; i >= 0; i--) {
            digit[i] = tmp % 10; 
            tmp = tmp / 10;
        }

        g.drawImage(mCharacterIcon, x - 10, y + 1, null);
        g.drawImage(mStatusIcons.getFrame(0), x, y, null);
        g.drawImage(mNumbers.getFrame(digit[0]), x + 28, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[1]), x + 36, y + 3, null);
    }

    /**
     * Draws the amount of coins 
     * @param g The graphics context
     * @param amount The amount of coins
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void drawCoins(Graphics g, int amount, int x, int y) { 
    	int tmp = amount;
        int digit[] = new int[2];
        for (int i = 1; i >= 0; i--) {
            digit[i] = tmp % 10; 
            tmp = tmp / 10;
        }

        g.drawImage(mStatusIcons.getFrame(1), x, y, null);
        g.drawImage(mNumbers.getFrame(digit[0]), x + 28, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[1]), x + 36, y + 3, null);
    }

    /**
     * Draws the score 
     * @param g The graphics context
     * @param amount The amount of points
     * @param x The x coordinate
     * @param y The y coordinate
     */     
    public void drawScore(Graphics g, int amount, int x, int y) {      
    	int tmp = amount;
        int digit[] = new int[8];
        for (int i = 7; i >= 0; i--) {
            digit[i] = tmp % 10; 
            tmp = tmp / 10;
        }
        g.drawImage(mStatusIcons.getFrame(2), x, y, null);
        g.drawImage(mNumbers.getFrame(digit[0]), x + 50, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[1]), x + 58, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[2]), x + 66, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[3]), x + 74, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[4]), x + 82, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[5]), x + 90, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[6]), x + 98, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[7]), x + 106, y + 3, null);
    }

    /**
     * Draws the amount of time remaining
     * @param g The graphics context
     * @param amount The remaining time
     * @param x The x coordinate
     * @param y The y coordinate
    */
    public void drawTime(Graphics g, int time, int x, int y) {    
    	int tmp = time;
        int digit[] = new int[3];
        for (int i = 2; i >= 0; i--) {
            digit[i] = tmp % 10;   
            tmp = tmp / 10;
        }
        g.drawImage(mStatusIcons.getFrameFromSheet(3), x, y, null);
        g.drawImage(mNumbers.getFrame(digit[0]), x + 14, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[1]), x + 22, y + 3, null);
        g.drawImage(mNumbers.getFrame(digit[2]), x + 30, y + 3, null);
    }

    /**
     * Draws the status bar at the top of the game screen with player information
     * @param g The graphics context
     * @param x The x coordinate
     * @param y The y coordinate
     * @param lives The amount of lives
     * @param coins The amount of coins
     * @param score The total score
     * @param time The amount of time
     */   
    public void drawStatus(Graphics g, int x, int y, int lives, int coins, int score, int time) {
        drawLives(g, lives, x, y);
        drawCoins(g, coins, x + 90, y);
        drawScore(g, score, x + 185, y);
        drawTime(g, time, x + 345, y);
    }

    /**
     * Draw scripted speech text inside a speech bubble    
     * @param g The graphics context
     * @param x The x coordinate
     * @param y The y coordinate
     * @param w The width of the speech bubble
     * @param h The height of the speech bubble
     * @param text The text displayed in the speech bubble
     * @param direction The direction of the speech bubble arrow
     */
    public void drawSpeech(Graphics g, int x, int y, int w, int h, String text, char direction) {
        if (w < 48) {
            w = 48;
        }
        if (h < 32) {
            h = 32;
        }
        g.drawImage(mSpeechBubble.getFrame(0), x, y, null); //top left
        g.drawImage(mSpeechBubble.getFrame(1), x, y + h - 16, null); //bottom left

        g.drawImage(mSpeechBubble.getFrame(4), x, y + 16, 16, h - 32, null); //left
        g.drawImage(mSpeechBubble.getFrame(4, 'r'), x + w - 16, y + 16, 16, h - 32, null); //right

        g.drawImage(mSpeechBubble.getFrame(2), x + 16, y, w - 32, 16, null); //top
        g.drawImage(mSpeechBubble.getFrame(3), x + 16, y + h - 16, w - 32, 16, null); //bottom

        g.drawImage(mSpeechBubble.getFrame(0, 'r'), x + w - 16, y, null); //top right
        g.drawImage(mSpeechBubble.getFrame(1, 'r'), x + w - 16, y + h - 16, null); //bottom right

        g.drawImage(mSpeechBubble.getFrame(5), x + 16, y + 16, w - 32, h - 32, null); //top left

        if (direction == 'l') {
            g.drawImage(mSpeechBubble.getFrame(6, 'r'), x + 16, y + h - 8, null);
        } else {
            g.drawImage(mSpeechBubble.getFrame(6, 'l'), x + w - 32, y + h - 8, null);
        }

        try 
        {
            String str[] = new String[3];
            str[0] = text.split("<br>")[0];
            str[1] = text.split("<br>")[1];
            str[0].trim();
            str[1].trim();

            mSpeechFont.drawString(g, str[0], x + 11, y + 12);
            mSpeechFont.drawString(g, str[1], x + 11, y + 26);
        } catch (Exception e)
        {
            mSpeechFont.drawString(g, text, x + 11, y + 12);
        }
    }

    /**
     * Draws a screen transition overlay
     * @param g The graphics context
     */
    public void drawScreenTransition(Graphics g) {
        if (mTransitionTimer > 540) {
            mTransitionTimer = 0;
            showTransition = false;
        } else {
            mTransitionTimer += 5;
            g.setColor(Color.BLACK);
            if (mScreenTransitionMode == 0) {
                g.drawImage(mScreenTransition[0], mTransitionTimer, 0, null);
                g.fillRect(mTransitionTimer + 32, 0, 480, 272);
            } else {
                g.drawImage(mScreenTransition[1], 0, mTransitionTimer, null);
                g.fillRect(0, mTransitionTimer + 32, 480, 272);
            }
        }
    }

    /**
     * Draws the title, load game, options, erase game, pause, options, save game, credits
     * level opening, level editor settings etc..
     * @param g The graphics context
     */   
    public void draw(Graphics g) {

        if (Settings.State.equals("title")) //title screen
        {
            mNumberOfOptions = 2;
            for (int i = 0; i < 2; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                if (i == 0) {
                    mGameFont.drawString(g, sel, "New Game", 201, 140);
                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "Options", 206, 160);
                }

            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);

        } else if (Settings.State.equals("new") || Settings.State.equals("level editor load game"))
        {
            mNumberOfOptions = SaveFile.mGameName.size() - 1;
            mGameFont.drawString(g, 1, "Select Game", 186, 130);
            int x = 239 - (mGameFont.getStringWidth("Main Game") / 2);
            mGameFont.drawString(g, 0, "Main Game", x, 170);
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("level editor load level")) 
        {
            mNumberOfOptions = SaveFile.mLevelName.size() - 1;
            mGameFont.drawString(g, 1, "Select Level", 185, 130);
            int x = 244 - (mGameFont.getStringWidth(SaveFile.mLevelName.get(mSelectedOption)) / 2);
            mGameFont.drawString(g, 0, SaveFile.mLevelName.get(mSelectedOption), x, 170);
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("level editor set game")) {
            mNumberOfOptions = SaveFile.mGameName.size();
            mGameFont.drawString(g, 1, "Select Game", 186, 30);
            if (SaveFile.mGameNumber < SaveFile.mGameName.size()) {
                int x = 240 - (mGameFont.getStringWidth(SaveFile.mGameName.get(SaveFile.mGameNumber)) / 2);
                mGameFont.drawString(g, 0, SaveFile.mGameName.get(SaveFile.mGameNumber), x, 170);
            } else {
                int x = 239 - (mGameFont.getStringWidth("New Folder") / 2);
                mGameFont.drawString(g, 0, "New Folder", x, 170);
            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("level editor create folder")) {
            mNumberOfOptions = SaveFile.mGameName.size();
            int x = 238 - (mGameFont.getStringWidth("Enter folder name") / 2);
            mGameFont.drawString(g, 1, "Enter folder name", x, 130);
            mGameFont.drawString(g, 0, mNewGameName, 157, 170);
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("load")) //load game
        {
            mNumberOfOptions = 4;
            int a = 0;
            for (int i = 0; i < 4; i++) {
                int b = mGameFont.getStringWidth("  " + Settings.File.getDescription(i));
                if (b > a) {
                    a = b;
                }
            }
            for (int i = 0; i < 4; i++) {

                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                int x = 250 - a / 2;

                if (i == 0) {
                    mGameFont.drawString(g, sel, "1", x, 130);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(0), x + 17, 130);
                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "2", x, 150);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(1), x + 17, 150);
                } else if (i == 2) {
                    mGameFont.drawString(g, sel, "3", x, 170);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(2), x + 17, 170);
                } else if (i == 3) {
                    mGameFont.drawString(g, sel, "4", x, 190);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(3), x + 17, 190);
                }
            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("level editor menu")) {
            mNumberOfOptions = 2;
            for (int i = 0; i < 4; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                if (i == 0) {
                    mGameFont.drawString(g, sel, "New Level", 195, 130);

                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "Load Level", 195, 150);

                }
            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("options")) //options
        {
            if (Settings.DebugMenu) {
                mNumberOfOptions = 4;
            } else {
                mNumberOfOptions = 3;
            }
            for (int i = 0; i < 4; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }

                if (i == 0) {
                    if (Settings.Music) {
                        mGameFont.drawString(g, sel, "Music On", 205, 130);
                    } else {
                        mGameFont.drawString(g, sel, "Music Off", 205, 130);
                    }

                } else if (i == 1) {
                    if (Settings.Sound) {
                        mGameFont.drawString(g, sel, "Sound On", 205, 150);
                    } else {
                        mGameFont.drawString(g, sel, "Sound Off", 205, 150);
                    }
                } else if (i == 2) {
                    mGameFont.drawString(g, sel, "Credits", 205, 170);
                } else if (i == 3) {
                    if (Settings.DebugMenu) {
                        mGameFont.drawString(g, sel, "Debug", 205, 190);
                    }
                }
            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        } else if (Settings.State.equals("erase")) //erase save
        {
            mNumberOfOptions = 2;
            for (int i = 0; i < 4; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                mGameFont.drawString(g, 1, "Erase Save Game", 165, 130);
                if (i == 0) {
                    mGameFont.drawString(g, sel, "Yes", 230, 150);
                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "No", 230, 170);

                }
            }
            g.drawImage(mTitleLogo, (480 / 2) - (mTitleLogo.getWidth() / 2), 20, null);
        }  else if (Settings.State.equals("pause")) {
            g.drawImage(mPauseBackground, 240 - (mPauseBackground.getWidth() / 2), 136 - (mPauseBackground.getHeight() / 2), null);

            mNumberOfOptions = 3;
            for (int i = 0; i < 3; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                if (i == 0) {
                    mGameFont.drawString(g, sel, "Continue", 205, 112);
                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "Options", 205, 132);
                } else if (i == 2) {
                    mGameFont.drawString(g, sel, "Quit", 205, 152);
                }
            }
        } else if (Settings.State.equals("save")) {
            g.drawImage(mPauseBackground, 240 - (mPauseBackground.getWidth() / 2), 136 - (mPauseBackground.getHeight() / 2), null);
            mNumberOfOptions = 4;
            for (int i = 0; i < 4; i++) {

                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                if (i == 0) {
                    mGameFont.drawString(g, sel, "1", 145, 102);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(0), 162, 102);
                } else if (i == 1) {
                    mGameFont.drawString(g, sel, "2", 145, 122);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(1), 162, 122);
                } else if (i == 2) {
                    mGameFont.drawString(g, sel, "3", 145, 142);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(2), 162, 142);
                } else if (i == 3) {
                    mGameFont.drawString(g, sel, "4", 145, 162);
                    mGameFont.drawString(g, sel, Settings.File.getDescription(3), 162, 162);
                }
            }
        } else if (Settings.State.equals("saving")) {
            g.drawImage(mPauseBackground, 240 - (mPauseBackground.getWidth() / 2), 136 - (mPauseBackground.getHeight() / 2), null);
            mGameFont.drawString(g, 1, "Saving", 210, 130);

            mTimer++;
            if (mTimer > 100) {
                Settings.File.loadFiles(); 
                Settings.State = "pause";
                mTimer = 0;
            }
        } else if (Settings.State.equals("level open")) {
            mLevelHeader = Main.mGamePanel.getHeader();
            g.setColor(new Color(235, 235, 235));
            g.fillRect(0, 0, 480, 272);
            g.drawImage(mLevelOpening, 240 - mLevelOpening.getWidth() / 2, 136 - mLevelOpening.getHeight() / 2 - 10, null);
            g.drawImage(mStatusIcons.getFrame(0), 214, 164, null);
            g.drawImage(mCharacterIcon, 199, 165, null);

            int tmp = Main.mGamePanel.getPlayerLives();
            int digit[] = new int[2];

            for (int i = 1; i >= 0; i--) {
                digit[i] = tmp % 10;  
                tmp = tmp / 10;
            }
            g.drawImage(mNumbers.getFrame(digit[0]), 248, 167, null);
            g.drawImage(mNumbers.getFrame(digit[1]), 258, 167, null);

            mGameFont.drawString(g, 0, "World " + mLevelHeader.world + "-" + mLevelHeader.level, 196, 121);

            mTimer++;
            if (mTimer > mLevelOpenPause) {
                Main.mGamePanel.setMusic(mLevelHeader.music);
            	Settings.State = "game";
                Settings.Paused = false;
                showTransition = true;
                mTimer = 0;
            }
        } else if (Settings.State.equals("pause options")) {
            g.drawImage(mPauseBackground, 240 - (mPauseBackground.getWidth() / 2), 136 - (mPauseBackground.getHeight() / 2), null);
            mNumberOfOptions = 2;
            for (int i = 0; i < 4; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }

                if (i == 0) {
                    if (Settings.Music) {
                        mGameFont.drawString(g, sel, "Music On", 200, 120);
                    } else {
                        mGameFont.drawString(g, sel, "Music Off", 200, 120);
                    }

                } else if (i == 1) {
                    if (Settings.Sound) {
                        mGameFont.drawString(g, sel, "Sound On", 200, 140);
                    } else {
                        mGameFont.drawString(g, sel, "Sound Off", 200, 140);
                    }
                }
            }
        } else if (Settings.State.equals("credits")) {
            mTimer++;
            if (mTimer % 6 == 0) {
                mCreditScrollY-=2; 
            }
            for (int i = 0; i < mCreditsText.size(); i++) {
                int x = 240 - (mGameFont.getStringWidth(mCreditsText.get(i)) / 2); 
                int y = mCreditScrollY + i * 20;
                if(y > -20 && y < 272)
                	mGameFont.drawString(g, 0, mCreditsText.get(i), x, y);
                if (i == mCreditsText.size() - 1 && y < -20)
                {
                    mSelectedOption = 0;
                    Settings.State = "options";
                }
            }
        } else if (Settings.State.equals("debug menu")) {
            mNumberOfOptions = 15;
            for (int i = 0; i < 26; i++) {
                int sel = 0;
                if (i == mSelectedOption) {
                    sel = 1;
                }
                mGameFont.drawString(g, 1, "Debug Menu", 192, 10);
                int colX = 20;
                int optX = colX + 140;
                if (i == 0) {
                    mGameFont.drawString(g, sel, "Level Select", colX, 50);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.LevelSelect), optX, 50);
                }
                if (i == 1) {
                    mGameFont.drawString(g, sel, "Invincible", colX, 70);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.Invincible), optX, 70);
                }
                if (i == 2) {
                    mGameFont.drawString(g, sel, "Freeze Time", colX, 90);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.FreezeTime), optX, 90);
                }
                if (i == 3) {
                    mGameFont.drawString(g, sel, "Infinite Lives", colX, 110);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.InfiniteLives), optX, 110);
                }
                if (i == 4) {
                    mGameFont.drawString(g, sel, "Powerups", colX, 130);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.Powerups), optX, 130);
                }
                if (i == 5) {
                    mGameFont.drawString(g, sel, "Game Speed", colX, 150);
                    mGameFont.drawString(g, sel, Integer.toString(Settings.GameSpeed), optX, 150);
                }
                if (i == 6) {
                    mGameFont.drawString(g, sel, "Music Test", colX, 170);
                    mGameFont.drawString(g, sel, Integer.toString(Settings.MusicTest), optX, 170);
                }
                if (i == 7) {
                    mGameFont.drawString(g, sel, "Sound Test", colX, 190);
                    mGameFont.drawString(g, sel, Integer.toString(Settings.SoundTest), optX, 190);
                }

                colX = 231;
                optX = colX + 152;
                if (i == 8) {
                    mGameFont.drawString(g, sel, "Animation", colX, 50);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.Animation), optX, 50);
                } else if (i == 9) {
                    mGameFont.drawString(g, sel, "Background", colX, 70);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.Background), optX, 70);
                } else if (i == 10) {
                    mGameFont.drawString(g, sel, "Remove Enemies", colX, 90);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.RemoveEnemies), optX, 90);
                } else if (i == 11) {
                    mGameFont.drawString(g, sel, "Debug Mode", colX, 110);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.DebugMode), optX, 110);
                } else if (i == 12) {
                    mGameFont.drawString(g, sel, "Main Character", colX, 130);
                    mGameFont.drawString(g, sel, Integer.toString(Settings.MainCharacter), optX, 130);
                } else if (i == 13) {
                    mGameFont.drawString(g, sel, "Edit Main Game", colX, 150);
                    mGameFont.drawString(g, sel, Boolean.toString(Settings.EditMainGame), optX, 150);
                } else if (i == 14) {
                    mGameFont.drawString(g, sel, "Ok", 232, 220);
                }
                mGameFont.drawString(g, sel, "Game Version", colX, 170);
                mGameFont.drawString(g, sel, Settings.GameVersion, optX, 170);
                mGameFont.drawString(g, sel, "Total Memory", colX, 190);
                mGameFont.drawString(g, sel, Long.toString(Runtime.getRuntime().totalMemory()), optX, 190);
            }
        }
    }

	/**
	 * Invoked when a key has been pressed
	 * @param e Event which indicates that a keystroke occurred
	 */
    public void keyPressed(KeyEvent e) {

        int kc = e.getKeyCode();

        if (Settings.State.equals("level editor create folder")) {
            if (e.getKeyCode() == KeyEvent .VK_BACK_SPACE) {
                if (mNewGameName.length() > 0) {
                    mNewGameName = mNewGameName.substring(0, mNewGameName.length() - 1);
                }
            } else {
                if (mNewGameName.length() < 16) {
                    mNewGameName += e.getKeyChar();
                }
            }
        }
        if (kc == KeyEvent.VK_UP) {
            if (mSelectedOption > 0) {
                mSelectedOption--;
                new SoundEffect("res/sfx/option.wav",1).start();
            }
        }
        if (kc == KeyEvent.VK_DOWN) {
            if (mSelectedOption < mNumberOfOptions - 1) {
                mSelectedOption++;
                new SoundEffect("res/sfx/option.wav").start();
            }
        }
        if (kc == KeyEvent.VK_ENTER) {
            new SoundEffect("res/sfx/select.wav").start();

            if (Settings.State.equals("title")) //title
            {
                if (mSelectedOption == 0) {
                    Main.mGamePanel.newGame();
                    mSelectedOption = 0;

                } else if (mSelectedOption == 1) {
                    Settings.State = "options";
                }
                mSelectedOption = 0;
            } else if (Settings.State.equals("level editor menu")) {
                //remove Main Game from level editor list
                if (!Settings.EditMainGame) {
                    for (int i = 0; i < SaveFile.mGameName.size(); i++) {
                        if (SaveFile.mGameName.get(i).equals("Main Game")) {
                            SaveFile.mGameName.remove(i);
                        }
                    }
                }

                if (mSelectedOption == 0) {
                    Settings.State = "level editor set game";
                    mSelectedOption = 0;
                } else if (mSelectedOption == 1) {
                    Settings.State = "level editor load game";
                    mSelectedOption = 0;
                }
            } else if (Settings.State.equals("new")) {
                 Main.mGamePanel.newGame();
                 mSelectedOption = 0;
            } else if (Settings.State.equals("load")) {
                if (Settings.File.getGame(mSelectedOption) != null) 
                {
                    Main.mGamePanel.loadSaveGame(mSelectedOption);
                    mSelectedOption = 0;
                }
            } else if (Settings.State.equals("level editor settings")) {
                if (mSelectedOption == 5) {
                    if (mLevelHeader.bg[2] == 0) 
                    {
                        Settings.State = "level editor rgb";
                        mSelectedOption = 0;
                    }
                } else if (mSelectedOption == 6) {
                    Settings.State = "level editor bg speed";
                    mSelectedOption = 0;
                } else if (mSelectedOption == 24) {
                    Settings.State = "level editor";
                    mSelectedOption = 0;
                }
            } else if (Settings.State.equals("level editor rgb")) {
                if (mSelectedOption == 3) {
                    Settings.State = "level editor settings";
                    mSelectedOption = 5;
                }
            } else if (Settings.State.equals("level editor bg speed")) {
                if (mSelectedOption == 3) {
                    Settings.State = "level editor settings";
                    mSelectedOption = 6;
                }
            } else if (Settings.State.equals("level editor")) {
                mSelectedOption = 0;
                Settings.State = "pause";
            } else if (Settings.State.equals("pause")) {
                if (mSelectedOption == 0) {
                    if (Settings.LevelEditor) {
                        Settings.State = "level editor";
                    } else {
                        Settings.State = "game";
                    }
                    Settings.Paused = false;
                }  else if (mSelectedOption == 1) {
                    if (Settings.LevelEditor) {
                        Settings.State = "level editor settings";
                    } else {
                        Settings.State = "pause options";
                    }
                    mSelectedOption = 0;
                } else if (mSelectedOption == 2) {
                    Settings.State = "title";
                    Main.mGamePanel.showTitleScreen();
                    Settings.Paused = false;
                    mSelectedOption = 0;
                }
            } else if (Settings.State.equals("save")) {
                Main.mGamePanel.saveGame(mSelectedOption);
                Settings.State = "saving";
                mSelectedOption = 0;
            } else if (Settings.State.equals("pause options")) {
                Settings.State = "pause";
                mSelectedOption = 0;
            } else if (Settings.State.equals("erase")) {
                if (mSelectedOption == 0) {
                    Settings.File.deleteFile(mSelectedSaveFile);
                    Settings.File.loadFiles();
                    Settings.State = "title";
                } else {
                    Settings.State = "title";
                    mSelectedOption = 0;
                }
            } else if (Settings.State.equals("options")) {
                if (mSelectedOption == 2) 
                {
                    mTimer = 0;
                    mCreditScrollY = 100;
                    Settings.State = "credits";
                } else if (mSelectedOption == 3)
                {
                    mSelectedOption = 0;
                    Settings.State = "debug menu";
                }
            }else if (Settings.State.equals("credits")) {
                mSelectedOption = 0;
                Settings.State = "options";
            } else if(Settings.State.equals("debug menu"))
            {
                if(mSelectedOption == 14)
                {
                    mSelectedOption = 0;
                    Settings.State = "options";
                }

            }
        }

        if (kc == KeyEvent.VK_DELETE) {
            if (Settings.State.equals("load")) {
                Settings.State = "erase";
                mSelectedSaveFile = mSelectedOption;
                mSelectedOption = 0;
            }
        }

        if (kc == KeyEvent.VK_ESCAPE) {
            if (Settings.LevelEditor) {
                if (Settings.State.equals("level editor settings")) {
                    Settings.State = "level editor";
                    mSelectedOption = 0;
                } else if (Settings.State.equals("level editor rgb")) {
                    Settings.State = "level editor settings";
                    mSelectedOption = 5;
                } else if (Settings.State.equals("level editor bg speed")) {
                    Settings.State = "level editor settings";
                    mSelectedOption = 6;
                } else if (Settings.State.equals("pause options") || Settings.State.equals("save")) {
                    Settings.State = "pause";
                    mSelectedOption = 0;
                }
            } else {
                if (!Settings.State.equals("game") && !Settings.State.equals("pause") && !Settings.State.equals("pause options") && !Settings.State.equals("save")) {
                    {
                        if (Settings.State.equals("level editor create folder")) {
                            SaveFile.mGameNumber = 0;
                        } else if (Settings.State.equals("credits") || Settings.State.equals("debug menu")) {
                            Settings.State = "options";
                        } else {
                            Settings.State = "title";
                            mSelectedOption = 0;
                        }
                    }
                } else if (Settings.State.equals("pause options") || Settings.State.equals("save")) {
                    Settings.State = "pause";
                }
                mSelectedOption = 0;
            }
        }
        if (kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_RIGHT) {
            if (Settings.State.equals("options") || Settings.State.equals("pause options")) {
                if (mSelectedOption == 0) {
                    if (Settings.Music) {
                        Settings.Music = false;
                        Music.stopMusic();
                    } else {
                        Settings.Music = true;
                        Music.startMusic();
                    }
                }
                if (mSelectedOption == 1) {
                    Settings.Sound = !Settings.Sound;
                }
                new SoundEffect("res/sfx/option.wav").start();
            } else if (Settings.State.equals("new") || Settings.State.equals("level editor load game")) {
                if (kc == KeyEvent.VK_LEFT && SaveFile.mGameNumber > 0) {
                    SaveFile.mGameNumber--;
                } else if (kc == KeyEvent.VK_RIGHT && SaveFile.mGameNumber < mNumberOfOptions) {
                    SaveFile.mGameNumber++;
                }
            } else if (Settings.State.equals("level editor set game")) {
                if (kc == KeyEvent.VK_LEFT && SaveFile.mGameNumber > 0) {
                    SaveFile.mGameNumber--;
                } else if (kc == KeyEvent.VK_RIGHT && SaveFile.mGameNumber < mNumberOfOptions) {
                    SaveFile.mGameNumber++;
                }
            } else if (Settings.State.equals("level editor load level")) {
                if (kc == KeyEvent.VK_LEFT && mSelectedOption > 0) {
                    mSelectedOption--;
                } else if (kc == KeyEvent.VK_RIGHT && mSelectedOption < mNumberOfOptions) {
                    mSelectedOption++;
                }

            } 
            else if(Settings.State.equals("debug menu"))
            {
                int value;
                int minOption;
                int maxOption;
                switch (mSelectedOption) {
                    case 5: value = Settings.GameSpeed; minOption = 1; maxOption = 10; break;
                    case 6: value = Settings.MusicTest; minOption = 1; maxOption = 10; break;
                    case 7: value = Settings.SoundTest; minOption = 1; maxOption = 10; break;
                    case 12: value = Settings.MainCharacter; minOption = 1; maxOption = SaveFile.getFolderCount("res/chr/"); break;
                    default: value = 0; minOption = 0; maxOption = 0; break;
                }
                if (kc == KeyEvent.VK_LEFT && value > minOption) {
                    value--;
                } else if (kc == KeyEvent.VK_RIGHT && value < maxOption) {
                    value++;
                }
                switch (mSelectedOption)
                {
                    case 0: Settings.LevelSelect = !Settings.LevelSelect; break;
                    case 1: Settings.Invincible = !Settings.Invincible; break;
                    case 2: Settings.FreezeTime = !Settings.FreezeTime; break;
                    case 3: Settings.InfiniteLives = !Settings.InfiniteLives; break;
                    case 4: Settings.Powerups = !Settings.Powerups; break;
                    case 5: Settings.GameSpeed = value; break;
                    case 6: Settings.MusicTest = value; break;
                    case 7: Settings.SoundTest = value; break;
                    case 8: Settings.Animation = !Settings.Animation; break;
                    case 9: Settings.Background = !Settings.Background; break;
                    case 10: Settings.RemoveEnemies = !Settings.RemoveEnemies; break;
                    case 11: Settings.DebugMode = !Settings.DebugMode; break;
                    case 12: Settings.MainCharacter = value; setCharacter(); Main.mGamePanel.createPlayer(); break;
                    case 13: Settings.EditMainGame = !Settings.EditMainGame; break;      
                    default: break;
                }           
            }
        }
    }
}
