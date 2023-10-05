#ifndef __UserInterface_H__
#define __UserInterface_H__

#pragma once


#include "Image.h"
#include "SpriteSheet.h"
#include "GameFont.h"
#include "Header.h"
#include "Settings.h"
#include "SaveFile.h"
#include "SoundEffect.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <SDL/SDL_image.h>
#include "SDL.h"
#include <sstream>
#include <unistd.h>
#include<sys/stat.h>
#include<sys/types.h>

#include <stdlib.h>

using namespace std;  

/**
 * @class UserInterface
 * @brief This class draws user interface components such as the title screen,
 * level opening and game status information
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class UserInterface
{
public:
	UserInterface::UserInterface();
	void UserInterface::setCharacter();
	void UserInterface::drawLives(SDL_Surface* g, int amount, int x, int y);
	void UserInterface::drawCoins(SDL_Surface* g, int amount, int x, int y);
	void UserInterface::drawScore(SDL_Surface* g, int amount, int x, int y);
	void UserInterface::drawTime(SDL_Surface* g, int time, int x, int y);
    void UserInterface::drawStatus(SDL_Surface* g, int x, int y, int lives, int coins, int score, int time);
	void UserInterface::drawSpeech(SDL_Surface* g, int x, int y, int w, int h, string text, char direction);
	void UserInterface::drawString(SDL_Surface* g, string text, int x, int y);
	void UserInterface::split(const string& s, char c, vector<string>& v);
	void UserInterface::drawScreenTransition(SDL_Surface* g);
	string UserInterface::convertInt(int number);
	std::string UserInterface::convertBool(bool b);
	string UserInterface::trim(string str);
	void UserInterface::draw(SDL_Surface* g);
	void UserInterface::keyPressed(SDL_Event e);
    /** Is the screen transitions being displayed */
    bool showTransition;

private:
	/** The title screen graphic */
    Image* mTitleLogo;
    /** The small character icon displayed on the level opening screen */
    Image* mCharacterIcon;
    /** The pause options background */
    Image* mPauseBackground;
    /** The level opening background image */
    Image* mLevelOpening;
    /** The screen transition image */
    Image* transition[2];
    /** The sprite sheet containing the numbers used in the game */
    SpriteSheet* mNumbers;
    /** The icons displayed in the status for score, live, time */
    SpriteSheet* mStatusIcons;
    /** The speech bubble sprite sheet used in the script sequences */
    SpriteSheet* mSpeechBubble;
    /** The font used in the level script speech sequences */
    GameFont* mSpeechFont;
    /** The main game font used in the title and pause screens */
    GameFont* mGameFont;
    /** The number of options in a menu */
    int mNumberOfOptions;
    /** The currently selected option in a menu */
    int mSelectedOption;
    /** The time elapsed between while the game is saving and credits scrolling */
    static int mTimer;
    /** The level header information */   
    Header* mLevelHeader;
    /** The currently selected save file */
    int mSelectedSaveFile;
    /** The name string for a new game folder */
    string mNewGameName;
    /** The screen transition mode 0-horizontal, 1-vertical */
    int mScreenTransitionMode;
    /** The time elapsed while showing a screen transition */
    int transitionTimer;
    /** The text displayed in the credits screen */ 
    vector<string> mCreditsText;
    /** The amount the credits have scrolled in the y direction */
    int mCreditScrollY;
    /** The pause while showing the level opening screen */
    int mLevelOpenPause;   
};

#endif
