#include "UserInterface.h"

#include "main.h"

SaveFile* Settings::File;
bool Settings::Paused;
bool Settings::RemoveEnemies;
bool Settings::EditMainGame;
int Settings::MainCharacter;
bool Settings::Music;
bool Settings::Sound;
bool Settings::LevelSelect;
bool Settings::Invincible;
bool Settings::FreezeTime;
bool Settings::InfiniteLives;
bool Settings::Powerups;
int Settings::GameSpeed;
int Settings::MusicTest;
int Settings::SoundTest;
bool Settings::Animation;
bool Settings::Background;
bool Settings::DrawBounds;
bool Settings::LevelEditor;
string Settings::GameVersion;
int UserInterface::mTimer;

int mLetter;

/**
 * Constructs the UserInterface
 */   
UserInterface::UserInterface() {
    mGameFont = new GameFont("main");
    mSpeechFont = new GameFont("speech");
    mNumbers = new SpriteSheet("res/gui/numbers.png", 16, 16, 10);
    mStatusIcons = new SpriteSheet("res/gui/status.png", 50, 17, 4);
    mSpeechBubble = new SpriteSheet("res/gui/speech.png", 16, 16, 7);
    mTitleLogo = new Image("res/gui/title.png", OSL_PF_4444);
    mPauseBackground = new Image("res/gui/pause.png", OSL_PF_4444);
    mLevelOpening = new Image("res/gui/level.png", OSL_PF_8888);
    transition[0] = new Image("res/gui/transition1.png", OSL_PF_4444);
    transition[1] = new Image("res/gui/transition2.png", OSL_PF_4444);
    setCharacter();
    
	char tmp[100];
	sprintf(tmp, "res/credits.txt");	
	ifstream file(tmp); 
	
	const int MAXLINE=512;
	char line[MAXLINE];
    
	while (file.good())
	{
		file.getline(line, MAXLINE);
		mCreditsText.push_back(line);
	}

    mLevelOpenPause = 100;  
    mScreenTransitionMode = 1;
    mSelectedOption = 0;
	showTransition = false;
	mLetter = 10;

}

/**
 * Loads the main character icon based on the MainCharacter setting
 */
void UserInterface::setCharacter()
{
	char tmp[100];
	sprintf(tmp, "res/chr/%d/icon.png", Settings::MainCharacter);	
    mCharacterIcon = new Image(tmp, OSL_PF_4444);
}

/**
 * Draws the amount of remaining lives 
 * @param g The graphics context
 * @param amount The amount of lives
 * @param x The x coordinate
 * @param y The y coordinate
 */   
void UserInterface::drawLives(int amount, int x, int y) {

    int tmp = amount;
    int digit[2];

    for (int i = 1; i >= 0; i--) {
        digit[i] = tmp % 10; //put digits into array
        tmp = tmp / 10;
    }

	mCharacterIcon->draw(x - 10, y + 1);
	mStatusIcons->drawFrame(x, y, 0, 'l');
	mNumbers->drawFrame(x + 28, y + 3, digit[0], 'l');
	mNumbers->drawFrame(x + 36, y + 3, digit[1], 'l');
}

/**
 * Draws the amount of coins 
 * @param g The graphics context
 * @param amount The amount of coins
 * @param x The x coordinate
 * @param y The y coordinate
 */
void UserInterface::drawCoins(int amount, int x, int y) {
    int tmp = amount;
    int digit[2];

    for (int i = 1; i >= 0; i--) {
        digit[i] = tmp % 10; 
        tmp = tmp / 10;
    }

	mStatusIcons->drawFrame(x, y, 1, 'l');
	mNumbers->drawFrame(x + 28, y + 3, digit[0], 'l');
	mNumbers->drawFrame(x + 36, y + 3, digit[1], 'l');
}

/**
 * Draws the score 
 * @param g The graphics context
 * @param amount The amount of points
 * @param x The x coordinate
 * @param y The y coordinate
 */     
void UserInterface::drawScore(int amount, int x, int y) {
    int tmp = amount;
    int digit[8];

    for (int i = 7; i >= 0; i--) {
        digit[i] = tmp % 10; 
        tmp = tmp / 10;
    }
	
	mStatusIcons->drawFrame(x, y, 2, 'l');
	mNumbers->drawFrame(x + 50, y + 3, digit[0], 'l');
	mNumbers->drawFrame(x + 58, y + 3, digit[1], 'l');
	mNumbers->drawFrame(x + 66, y + 3, digit[2], 'l');
	mNumbers->drawFrame(x + 74, y + 3, digit[3], 'l');
	mNumbers->drawFrame(x + 82, y + 3, digit[4], 'l');
	mNumbers->drawFrame(x + 90, y + 3, digit[5], 'l');
	mNumbers->drawFrame(x + 98, y + 3, digit[6], 'l');
	mNumbers->drawFrame(x + 106, y + 3, digit[6], 'l');
}

/**
 * Draws the amount of time remaining
 * @param g The graphics context
 * @param amount The remaining time
 * @param x The x coordinate
 * @param y The y coordinate
*/
void UserInterface::drawTime(int time, int x, int y) {
    int tmp = time;
    int digit[3];

    for (int i = 2; i >= 0; i--) {
        digit[i] = tmp % 10;   
        tmp = tmp / 10;
    }

	mStatusIcons->drawFrame(x, y, 3, 'l');
	mNumbers->drawFrame(x + 14, y + 3, digit[0], 'l');
	if(time >= 10)
		mNumbers->drawFrame(x + 22, y + 3, digit[1], 'l');
	if(time >= 100)
		mNumbers->drawFrame(x + 30, y + 3, digit[2], 'l');
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
void UserInterface::drawStatus(int x, int y, int lives, int coins, int score, int time) {
    drawLives(lives, x, y);
    drawCoins(coins, x + 90, y);
    drawScore(score, x + 185, y);
    drawTime(time, x + 345, y);
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
void UserInterface::drawSpeech(int x, int y, int w, int h, string text, char direction) {
    if (w < 48) {
        w = 48;
    }
    if (h < 32) {
        h = 32;
    }

	mSpeechBubble->drawFrame(x, y, 0, 'l'); //top left
	mSpeechBubble->drawFrame(x, y + h - 16, 1, 'l'); //bottom left
	if(h > 32)
	{
		mSpeechBubble->drawFrame(x, y + 16, 16, h - 32, 4, 'l'); //left
		mSpeechBubble->drawFrame(x + w - 16, y + 16, 16, h - 32, 4, 'r'); //right
	}
	mSpeechBubble->drawFrame(x + 16, y, w - 32, 16, 2, 'l'); //top
	mSpeechBubble->drawFrame(x + 16, y + h - 16, w - 32, 16, 3, 'l'); //bottom

	mSpeechBubble->drawFrame(x + w - 16, y, 0, 'r'); //top right
	mSpeechBubble->drawFrame(x + w - 16, y + h - 16, 1, 'r'); //bottom right

    mSpeechBubble->drawFrame(x + 16, y + 16, w - 32, h - 32, 5, 'r'); //center

    if (direction == 'l') {
        mSpeechBubble->drawFrame(x + 16, y + h - 8, 6, 'r'); 
    } else {
        mSpeechBubble->drawFrame(x + w - 32, y + h - 8, 6, 'l'); 
    }

    //split the string of text into lines
    vector<string> str;
    split(text, '?', str);
	if(str.size() > 0)
	{
       mSpeechFont->drawString(str[0], x + 11, y + 12);
       mSpeechFont->drawString(str[1], x + 11, y + 26);
	}
	else
    	mSpeechFont->drawString(text, x + 11, y + 12);
}

/**
 * Draw the specified text at specified location
 * @param g The graphics context
 * @param x The x coordinate
 * @param y The y coordinate
 * @param text The text displayed
 * 
*/
void UserInterface::drawString(string text, int x, int y)
{
	mSpeechFont->drawString(text, x, y);
}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void UserInterface::split(const string& s, char c, vector<string>& v) 
{   
	//http://www.blog.highub.com/c-plus-plus/c-parse-split-delimited-string/
	string::size_type i = 0;   
	string::size_type j = s.find(c);   
	while (j != string::npos)
    {      
		v.push_back(s.substr(i, j-i));      
		i = ++j;      
		j = s.find(c, j);      
		if (j == string::npos)         
			v.push_back(s.substr(i, s.length( )));   
	}
}

/**
 * Draws a screen transition overlay
 * @param g The graphics context
 */
void UserInterface::drawScreenTransition() {
    if (transitionTimer > 540) {
        transitionTimer = 0;
        showTransition = false;
    } else {
        transitionTimer += 10;
        if (mScreenTransitionMode == 0) {
			transition[0]->draw(transitionTimer, 0);		
			oslDrawFillRect(transitionTimer + 32, 0, 480, 272, RGB(0, 0, 0));
        } else {
			transition[1]->draw(0, transitionTimer);
			oslDrawFillRect(0, transitionTimer + 32, 480, 272, RGB(0, 0, 0));
        }
    }
}

//http://www.cplusplus.com/forum/beginner/7777/
string UserInterface::convertInt(int number)
{
   stringstream ss;//create a stringstream
   ss << number;//add number to the stream
   return ss.str();//return a string with the contents of the stream
}

//http://stackoverflow.com/questions/29383/converting-bool-to-text-in-c
std::string UserInterface::convertBool(bool b)
{    
	if(b)
		return "true";
	else
		return "false";
}

string UserInterface::trim(string str)
{  
	//http://stackoverflow.com/questions/216823/whats-the-best-way-to-trim-stdstring
	return str;
}

/**
 * Draws the title, load game, options, erase game, pause, options, save game, credits
 * level opening, level editor settings etc..
 * @param g The graphics context
 */   
void UserInterface::draw() {

    if (Settings::State == "title") //title screen / main menu
    {
        mNumberOfOptions = 4;
        for (int i = 0; i < 4; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            if (i == 0) {
                mGameFont->drawString(sel, "New", 216, 130);
            } else if (i == 1) {
                mGameFont->drawString(sel, "Load", 216, 150);
            } else if (i == 2) {
                mGameFont->drawString(sel, "Editor", 216, 170);
            } else if (i == 3) {
                mGameFont->drawString(sel, "Options", 216, 190);
            }
        }
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "new" || Settings::State == "level editor load game") //title screen / main menu
    {
        mNumberOfOptions = SaveFile::GameName.size() - 1;
        mGameFont->drawString(1, "Select Game", 186, 130);
        int x = 239 - (mGameFont->getStringWidth(SaveFile::GameName[SaveFile::GameFolder]) / 2);
        mGameFont->drawString(0, SaveFile::GameName[SaveFile::GameFolder], x, 170);
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "level editor load level") 
    {
        mNumberOfOptions = SaveFile::LevelName.size() - 1;
        mGameFont->drawString(1, "Select Level", 185, 130);
        int x = 244 - (mGameFont->getStringWidth(SaveFile::LevelName[mSelectedOption]) / 2);
        mGameFont->drawString(0, SaveFile::LevelName[mSelectedOption], x, 170);
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "level editor set game") {
        mNumberOfOptions = SaveFile::GameName.size();
        mGameFont->drawString(1, "Select Game", 186, 130);
        if (SaveFile::GameFolder < (int)SaveFile::GameName.size()) {
            int x = 240 - (mGameFont->getStringWidth(SaveFile::GameName[SaveFile::GameFolder]) / 2);
            mGameFont->drawString(0, SaveFile::GameName[SaveFile::GameFolder], x, 170);
        } else {
            int x = 239 - (mGameFont->getStringWidth("New Folder") / 2);
            mGameFont->drawString(0, "New Folder", x, 170);
        }
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "level editor create folder") {
        mNumberOfOptions = SaveFile::GameName.size();
        int x = 238 - (mGameFont->getStringWidth("Enter folder name") / 2);
        mGameFont->drawString(1, "Enter folder name", x, 130);
        mGameFont->drawString(0, mNewGameName, 157, 170);
        char let = mGameFont->getFrameLetter(mLetter);
		//http://www.cplusplus.com/forum/beginner/4967/
		stringstream ss;
		string s;
		ss << let;
		ss >> s;
		
		int i = mGameFont->getStringWidth(mNewGameName);
		mGameFont->drawString(0, s, 157 + i, 170);
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "load") //load game
    {
        mNumberOfOptions = 4;
        int a = 0;
        for (int i = 0; i < 4; i++) {
            int b = mGameFont->getStringWidth("  " + Settings::File->getDescription(i));
            if (b > a) {
                a = b;
            }
        }
        for (int i = 0; i < 4; i++) {

            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            int x = 234 - a / 2;

            if (i == 0) {
                mGameFont->drawString(sel, "1", x, 130);
                mGameFont->drawString(sel, Settings::File->getDescription(0), x + 17, 130);
            } else if (i == 1) {
                mGameFont->drawString(sel, "2", x, 150);
                mGameFont->drawString(sel, Settings::File->getDescription(1), x + 17, 150);
            } else if (i == 2) {
                mGameFont->drawString(sel, "3", x, 170);
                mGameFont->drawString(sel, Settings::File->getDescription(2), x + 17, 170);
            } else if (i == 3) {
                mGameFont->drawString(sel, "4", x, 190);
                mGameFont->drawString(sel, Settings::File->getDescription(3), x + 17, 190);
            }
        }
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "level editor menu") {
        mNumberOfOptions = 2;
        for (int i = 0; i < 4; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            if (i == 0) {
                mGameFont->drawString(sel, "New Level", 195, 130);

            } else if (i == 1) {
                mGameFont->drawString(sel, "Load Level", 195, 150);

            }
        }
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "options") //options
    {
        if (Settings::DebugMenu) {
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
                if (Settings::Music) {
                    mGameFont->drawString(sel, "Music On", 205, 130);
                } else {
                    mGameFont->drawString(sel, "Music Off", 205, 130);
                }

            } else if (i == 1) {
                if (Settings::Sound) {
                    mGameFont->drawString(sel, "Sound On", 205, 150);
                } else {
                    mGameFont->drawString(sel, "Sound Off", 205, 150);
                }
            } else if (i == 2) {
                mGameFont->drawString(sel, "Credits", 205, 170);
            } else if (i == 3) {
                if (Settings::DebugMenu) {
                    mGameFont->drawString(sel, "Debug", 205, 190);
                }
            }
        }
		mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "erase") //erase save
    {
        mNumberOfOptions = 2;
        for (int i = 0; i < 4; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            mGameFont->drawString(1, "Erase Save Game", 165, 130);
            if (i == 0) {
                mGameFont->drawString(sel, "Yes", 230, 150);
            } else if (i == 1) {
                mGameFont->drawString(sel, "No", 230, 170);

            }
        }
        mTitleLogo->draw((480 / 2) - (mTitleLogo->getWidth() / 2), 20);

    } else if (Settings::State == "level editor settings") {
        mNumberOfOptions = 25;

        mLevelHeader = main::mEditorPanel->getHeader();

        for (int i = 0; i < 26; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            mGameFont->drawString(1, "Level Settings", 170, 10);
            if (i == 0) {
                mGameFont->drawString(sel, "World", 20, 50);
                mGameFont->drawString(sel, convertInt(mLevelHeader->World), 118, 50);




            } else if (i == 1) {
                mGameFont->drawString(sel, "Level", 20, 70);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Level), 118, 70);
            } else if (i == 2) {
                mGameFont->drawString(sel, "Area", 20, 90);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Area), 118, 90);
            } else if (i == 3) {
                mGameFont->drawString(sel, "Bg Near", 20, 110);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Bg[0]), 118, 110);
            } else if (i == 4) {
                mGameFont->drawString(sel, "Bg Middle", 20, 130);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Bg[1]), 118, 130);
            } else if (i == 5) {
                mGameFont->drawString(sel, "Bg Far", 20, 150);
                if (mLevelHeader->Bg[2] == 0) {
                    mGameFont->drawString(sel, "RGB", 118, 150);
                } else {
                    mGameFont->drawString(sel, convertInt(mLevelHeader->Bg[2]), 118, 150);
                }
            } else if (i == 6) {
                mGameFont->drawString(sel, "Bg Speed", 20, 170);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgSpeed[0]) + "?" + convertInt(mLevelHeader->BgSpeed[1]) + "?" + convertInt(mLevelHeader->BgSpeed[2]), 118, 170);
            } else if (i == 7) {
                mGameFont->drawString(sel, "Bg Align", 20, 190);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgAlign), 118, 190);
            }

            if (i == 8) {
                mGameFont->drawString(sel, "Blocks", 170, 50);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Tileset0), 268, 50);
            }
            if (i == 9) {
                mGameFont->drawString(sel, "Terrain", 170, 70);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Tileset16), 268, 70);
            }
            if (i == 10) {
                mGameFont->drawString(sel, "Scenery", 170, 90);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Tileset32), 268, 90);
            }
            if (i == 11) {
                mGameFont->drawString(sel, "Music", 170, 110);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Music), 268, 110);
            }
            if (i == 12) {
                mGameFont->drawString(sel, "Width", 170, 130);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Width / 16), 268, 130);
            }
            if (i == 13) {
                mGameFont->drawString(sel, "Height", 170, 150);
                mGameFont->drawString(sel, convertInt(mLevelHeader->Height / 16), 268, 150);
            }
            if (i == 14) {
                mGameFont->drawString(sel, "Start X", 170, 170);
                mGameFont->drawString(sel, convertInt(mLevelHeader->StartX), 268, 170);
            }
            if (i == 15) {
                mGameFont->drawString(sel, "Start Y", 170, 190);
                mGameFont->drawString(sel, convertInt(mLevelHeader->StartY), 268, 190);
            }

            if (i == 16) {
                mGameFont->drawString(sel, "End World", 315, 50);
                mGameFont->drawString(sel, convertInt(mLevelHeader->EndWorld), 433, 50);
            }
            if (i == 17) {
                mGameFont->drawString(sel, "End Level", 315, 70);
                mGameFont->drawString(sel, convertInt(mLevelHeader->EndLevel), 433, 70);
            }
            if (i == 18) {
                mGameFont->drawString(sel, "End Area", 315, 90);
                mGameFont->drawString(sel, convertInt(mLevelHeader->EndArea), 433, 90);
            } else if (i == 19) {
                mGameFont->drawString(sel, "Time", 315, 110);
                mGameFont->drawString(sel, convertInt(mLevelHeader->TimeLimit), 433, 110);
            } else if (i == 20) {
                mGameFont->drawString(sel, "End X", 315, 130);
                mGameFont->drawString(sel, convertInt(mLevelHeader->EndX), 433, 130);
            } else if (i == 21) {
                mGameFont->drawString(sel, "End Y", 315, 150);
                mGameFont->drawString(sel, convertInt(mLevelHeader->EndY), 433, 150);
            } else if (i == 22) {
                mGameFont->drawString(sel, "Bonus X", 315, 170);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BonusX), 433, 170);
            } else if (i == 23) {
                mGameFont->drawString(sel, "Bonus Y", 315, 190);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BonusY), 433, 190);
            } else if (i == 24) {
                mGameFont->drawString(sel, "Ok", 433, 240);
            }
            mGameFont->drawString(sel, "Game Folder", 20, 220);
            mGameFont->drawString(sel, SaveFile::GameName[SaveFile::GameFolder], 20, 240);

        }

    } else if (Settings::State == "level editor rgb") {
        mNumberOfOptions = 4;
        mLevelHeader = main::mEditorPanel->getHeader();

        for (int i = 0; i < 26; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            mGameFont->drawString(1, "Background Color", 163, 75);
            if (i == 0) {
                mGameFont->drawString(sel, "Red", 176, 115);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgColor->getRed()), 274, 115);
            } else if (i == 1) {
                mGameFont->drawString(sel, "Green", 176, 135);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgColor->getGreen()), 274, 135);
            } else if (i == 2) {
                mGameFont->drawString(sel, "Blue", 176, 155);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgColor->getBlue()), 274, 155);
            } else if (i == 3) {
                mGameFont->drawString(sel, "Ok", 235, 190);
            }
        }
    } else if (Settings::State == "level editor bg speed") {
        mNumberOfOptions = 4;
        mLevelHeader = main::mEditorPanel->getHeader();

        for (int i = 0; i < 26; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            mGameFont->drawString(1, "Background Speed", 157, 75);
            if (i == 0) {
                mGameFont->drawString(sel, "Near", 176, 115);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgSpeed[0]), 274, 115);
            } else if (i == 1) {
                mGameFont->drawString(sel, "Middle", 176, 135);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgSpeed[1]), 274, 135);
            } else if (i == 2) {
                mGameFont->drawString(sel, "Far", 176, 155);
                mGameFont->drawString(sel, convertInt(mLevelHeader->BgSpeed[2]), 274, 155);
            } else if (i == 3) {
                mGameFont->drawString(sel, "Ok", 235, 190);
            }
        }
    } else if (Settings::State == "pause") {
		mPauseBackground->draw(240 - (mPauseBackground->getWidth() / 2), 136 - (mPauseBackground->getHeight() / 2));
        mNumberOfOptions = 4;
        for (int i = 0; i < 4; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            if (i == 0) {
                mGameFont->drawString(sel, "Continue", 205, 102);
            } else if (i == 1) {
                mGameFont->drawString(sel, "Save", 205, 122);
            } else if (i == 2) {
                mGameFont->drawString(sel, "Options", 205, 142);
            } else if (i == 3) {
                mGameFont->drawString(sel, "Quit", 205, 162);
            }
        }
    } else if (Settings::State == "save") {
		mPauseBackground->draw(240 - (mPauseBackground->getWidth() / 2), 136 - (mPauseBackground->getHeight() / 2));
        mNumberOfOptions = 4;
        for (int i = 0; i < 4; i++) {

            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            if (i == 0) {
                mGameFont->drawString(sel, "1", 145, 102);
                mGameFont->drawString(sel, Settings::File->getDescription(0), 162, 102);
            } else if (i == 1) {
                mGameFont->drawString(sel, "2", 145, 122);
                mGameFont->drawString(sel, Settings::File->getDescription(1), 162, 122);
            } else if (i == 2) {
                mGameFont->drawString(sel, "3", 145, 142);
                mGameFont->drawString(sel, Settings::File->getDescription(2), 162, 142);
            } else if (i == 3) {
                mGameFont->drawString(sel, "4", 145, 162);
                mGameFont->drawString(sel, Settings::File->getDescription(3), 162, 162);
            }
        }
    } else if (Settings::State == "saving") {
		mPauseBackground->draw(240 - (mPauseBackground->getWidth() / 2), 136 - (mPauseBackground->getHeight() / 2));
        mGameFont->drawString(1, "Saving", 210, 130);

        mTimer++;
        if (mTimer > 100) {
            Settings::File->loadFiles(); //update save games
            Settings::State = "pause";
            mTimer = 0;
        }
    } else if (Settings::State == "level open") {
        mLevelHeader = main::mGamePanel->getHeader();
        
        oslDrawFillRect(0, 0, 480, 272, RGB(235, 235, 235));

		mLevelOpening->draw(240 - mLevelOpening->getWidth() / 2, 136 - mLevelOpening->getHeight() / 2 - 10);
		mStatusIcons->drawFrame(214, 164, 0, 'l');
		mCharacterIcon->draw(199, 165);

        int tmp = main::mGamePanel->getPlayerLives();
        int digit[2];

        for (int i = 1; i >= 0; i--) {
            digit[i] = tmp % 10;   
            tmp = tmp / 10;
        }
		
		mNumbers->drawFrame(248, 167, digit[0], 'l');
		mNumbers->drawFrame(258, 167, digit[1], 'l');

		char str[100];
        sprintf (str, "World %d-%d", mLevelHeader->World, mLevelHeader->Level);	
        mGameFont->drawString(0, str, 196, 121);

        mTimer++;
        if (mTimer > mLevelOpenPause) {
        	Settings::State = "game";
            Settings::Paused = false;
            showTransition = true;
            mTimer = 0;
        }

    } else if (Settings::State =="pause options") {
		mPauseBackground->draw(240 - (mPauseBackground->getWidth() / 2), 136 - (mPauseBackground->getHeight() / 2));
        mNumberOfOptions = 2;
        for (int i = 0; i < 4; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }

            if (i == 0) {
                if (Settings::Music) {
                    mGameFont->drawString(sel, "Music On", 200, 120);
                } else {
                    mGameFont->drawString(sel, "Music Off", 200, 120);
                }

            } else if (i == 1) {
                if (Settings::Sound) {
                    mGameFont->drawString(sel, "Sound On", 200, 140);
                } else {
                    mGameFont->drawString(sel, "Sound Off", 200, 140);
                }
            }
        }
    } else if (Settings::State == "credits") {
        mTimer++;
        if (mTimer % 5 == 0) {
            mCreditScrollY--; //scroll credits
        }
        for (int i = 0; i < (int)mCreditsText.size(); i++) {
            mGameFont->drawString(0, mCreditsText[i], 240 - (mGameFont->getStringWidth(mCreditsText[i]) / 2), mCreditScrollY + i * 20);
            if (i == (int)mCreditsText.size() - 1 && mCreditScrollY + i * 20 < -100) //end credits
            {
                mSelectedOption = 0;
                Settings::State = "options";
            }
        }
    } else if (Settings::State == "debug menu") {
        mNumberOfOptions = 15;
        for (int i = 0; i < 26; i++) {
            int sel = 0;
            if (i == mSelectedOption) {
                sel = 1;
            }
            mGameFont->drawString(1, "Debug Menu", 192, 10);
            int colX = 20;
            int optX = colX + 140;
            if (i == 0) {
                mGameFont->drawString(sel, "Level Select", colX, 50);
                mGameFont->drawString(sel, convertBool(Settings::LevelSelect), optX, 50);
            }
            if (i == 1) {
                mGameFont->drawString(sel, "Invincible", colX, 70);
                mGameFont->drawString(sel, convertBool(Settings::Invincible), optX, 70);
            }
            if (i == 2) {
                mGameFont->drawString(sel, "Freeze Time", colX, 90);
                mGameFont->drawString(sel, convertBool(Settings::FreezeTime), optX, 90);
            }
            if (i == 3) {
                mGameFont->drawString(sel, "Infinite Lives", colX, 110);
                mGameFont->drawString(sel, convertBool(Settings::InfiniteLives), optX, 110);
            }
            if (i == 4) {
                mGameFont->drawString(sel, "Powerups", colX, 130);
                mGameFont->drawString(sel, convertBool(Settings::Powerups), optX, 130);
            }
            if (i == 5) {
                mGameFont->drawString(sel, "Game Speed", colX, 150);
                mGameFont->drawString(sel, convertInt(Settings::GameSpeed), optX, 150);
            }
            if (i == 6) {
                mGameFont->drawString(sel, "Music Test", colX, 170);
                mGameFont->drawString(sel, convertInt(Settings::MusicTest), optX, 170);
            }
            if (i == 7) {
                mGameFont->drawString(sel, "Sound Test", colX, 190);
                mGameFont->drawString(sel, convertInt(Settings::SoundTest), optX, 190);
            }

            colX = 231;
            optX = colX + 152;
            if (i == 8) {
                mGameFont->drawString(sel, "Animation", colX, 50);
                mGameFont->drawString(sel, convertBool(Settings::Animation), optX, 50);
            } else if (i == 9) {
                mGameFont->drawString(sel, "Background", colX, 70);
                mGameFont->drawString(sel, convertBool(Settings::Background), optX, 70);
            } else if (i == 10) {
                mGameFont->drawString(sel, "Remove Enemies", colX, 90);
                mGameFont->drawString(sel, convertBool(Settings::RemoveEnemies), optX, 90);
            } else if (i == 11) {
                mGameFont->drawString(sel, "Debug Mode", colX, 110);
                mGameFont->drawString(sel, convertBool(Settings::DrawBounds), optX, 110);
            } else if (i == 12) {
                mGameFont->drawString(sel, "Main Character", colX, 130);
                mGameFont->drawString(sel, convertInt(Settings::MainCharacter), optX, 130);
            } else if (i == 13) {
                mGameFont->drawString(sel, "Edit Main Game", colX, 150);
                mGameFont->drawString(sel, convertBool(Settings::EditMainGame), optX, 150);
            } else if (i == 14) {
                mGameFont->drawString(sel, "Ok", 232, 220);
            }
            mGameFont->drawString(sel, "Game Version", colX, 170);
            mGameFont->drawString(sel, Settings::GameVersion, optX, 170);
            mGameFont->drawString(sel, "Total Memory", colX, 190);
			char mem[100];
			sprintf (mem, "%d", oslGetRamStatus().maxAvailable);
            mGameFont->drawString(sel, mem, optX, 190);
        }
    }
}

/**
 * Invoked when a key has been pressed
 * @param e Event which indicates that a keystroke occurred
 */
void UserInterface::keyPressed(OSL_CONTROLLER e) {

    if (Settings::State == "level editor create folder") {
        if (e.pressed.square) {
            if (mNewGameName.length() > 0) {
                mNewGameName = mNewGameName.substr(0, mNewGameName.length() - 1);
            }
        }
    }
    if (e.pressed.up) {
        if (mSelectedOption > 0) {
            mSelectedOption--;
			main::mSoundEffect->play("option.wav", 0);
        }
    }
    if (e.pressed.down) {
        if (mSelectedOption < mNumberOfOptions - 1) {
            mSelectedOption++;
			main::mSoundEffect->play("option.wav", 0);
        }
    }
    
    if(e.pressed.start && Settings::State == "level editor create folder")
    {
		if (mNewGameName.length() > 0) {
			char path[100];
			sprintf (path, "res/lvl/%s", mNewGameName.c_str());	
			//http://www.go4expert.com/forums/showthread.php?t=9031
			if(mkdir(path, S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH)!=-1)    
			{   
				SaveFile::GameName.push_back(mNewGameName);
				SaveFile::GameFolder = SaveFile::GameName.size()-1;
				SaveFile::refreshLevelList();
				main::startEditor();
				main::mEditorPanel->loadLevel("");
				Settings::State = "level editor settings";
		 	}						       
		}
	}
    
    if (e.pressed.cross && Settings::State != "level open") {
		main::mSoundEffect->play("select.wav", 0);

        if (Settings::State == "title") //title
        {
            if (mSelectedOption == 0) {
                Settings::State = "new";
                SaveFile::refreshGameList();
                SaveFile::refreshLevelList();
                SaveFile::GameFolder = 0;
            } else if (mSelectedOption == 1) {
                Settings::State = "load";
            } else if (mSelectedOption == 2) {
                Settings::State = "level editor menu";
                SaveFile::GameFolder = 0;
            } else if (mSelectedOption == 3) {
                Settings::State = "options";
            }
            mSelectedOption = 0;
        } else if (Settings::State == "level editor menu") {
            //remove Main Game from level editor list
            if (!Settings::EditMainGame) {
                for (int i = 0; i < (int)SaveFile::GameName.size(); i++) {
                    if (SaveFile::GameName[i] == "Main Game") {
                        SaveFile::GameName.erase(SaveFile::GameName.begin()+i);
                    }
                }
            }
            if (mSelectedOption == 0) {
                Settings::State = "level editor set game";
                mSelectedOption = 0;
            } else if (mSelectedOption == 1) {
                Settings::State = "level editor load game";
                mSelectedOption = 0;
            }
        } else if (Settings::State == "level editor set game") {
            if (SaveFile::GameFolder == (int)SaveFile::GameName.size()) {
                Settings::State = "level editor create folder";
                mNewGameName = "";
                mLetter = 10;
            } else //create a new level in existing game folder
            {
                SaveFile::refreshLevelList();
                main::startEditor();
                main::mEditorPanel->loadLevel("");
                Settings::State = "level editor settings";
                mSelectedOption = 0;
            }
        } else if (Settings::State == "level editor create folder") {
			char let = mGameFont->getFrameLetter(mLetter);
			if (mNewGameName.length() < 15) 
				mNewGameName += let;
        } else if (Settings::State == "level editor load game") {
            SaveFile::refreshLevelList();
            if (SaveFile::LevelName.size() > 0) {
                Settings::State = "level editor load level";
                mSelectedOption = 0;                  
            }
            else //no levels in the folder
            {
            }
        } else if (Settings::State == "level editor load level") {     
			main::startEditor();
            //main::mGamePanel->closeLevel();
			char path[100];
        	sprintf (path, "res/lvl/%s/%s", SaveFile::GameName[SaveFile::GameFolder].c_str(), SaveFile::LevelName[mSelectedOption].c_str());	           
			main::mEditorPanel->loadLevel(path);
			Settings::LevelEditor = true;
			Settings::State = "level editor";
            mSelectedOption = 0;
        } else if (Settings::State == "new") {
            SaveFile::refreshLevelList();
            if (SaveFile::LevelName.size() > 0) {
				main::mGamePanel->newGame();
				//main::mGamePanel->showTitleScreen();
                mSelectedOption = 0;

            } else //no levels in the folder
            {
            }
        } else if (Settings::State == "load") {
            if (Settings::File->getGame(mSelectedOption) != NULL) //no save data
            {
                //Main.mFrame.mGamePanel.loadSaveGame(mSelectedOption);
                mSelectedOption = 0;
            }
        } else if (Settings::State == "level editor settings") {
            if (mSelectedOption == 5) {
                if (mLevelHeader->Bg[2] == 0) //far layer set to rgb
                {
                    Settings::State = "level editor rgb";
                    mSelectedOption = 0;
                }
            } else if (mSelectedOption == 6) {
                Settings::State = "level editor bg speed";
                mSelectedOption = 0;
            } else if (mSelectedOption == 24) {
                Settings::State = "level editor";
                mSelectedOption = 0;
            }
        } else if (Settings::State == "level editor rgb") {
            if (mSelectedOption == 3) {
                Settings::State = "level editor settings";
                mSelectedOption = 5;
            }
        } else if (Settings::State == "level editor bg speed") {
            if (mSelectedOption == 3) {
                Settings::State = "level editor settings";
                mSelectedOption = 6;
            }
        } else if (Settings::State == "level editor") {
            mSelectedOption = 0;
            Settings::State = "pause";
        } else if (Settings::State == "pause") {
            if (mSelectedOption == 0) {
                if (Settings::LevelEditor) {
                    Settings::State = "level editor";
                } else {
                    Settings::State = "game";
                }
                Settings::Paused = false;
            } else if (mSelectedOption == 1) {

                if (Settings::LevelEditor) {
                    Settings::State = "saving";
					char path[100];
        			sprintf (path, "res/lvl/%s/", SaveFile::GameName[SaveFile::GameFolder].c_str());
                    main::mEditorPanel->saveLevel(path);
                } else {
                    Settings::State = "save";
                }
                mSelectedOption = 0;
            } else if (mSelectedOption == 2) {
                if (Settings::LevelEditor) {
                    Settings::State = "level editor settings";
                } else {
                    Settings::State = "pause options";
                }
                mSelectedOption = 0;
            } else if (mSelectedOption == 3) {
				if(Settings::LevelEditor)
				{
					//main::mEditorPanel->closeLevel();
					main::stopEditor();
				}
                main::mGamePanel->showTitleScreen();                
				Settings::State = "title";
                Settings::Paused = false;
                mSelectedOption = 0;
            }
        } else if (Settings::State == "save") {
            main::mGamePanel->saveGame(mSelectedOption);
            Settings::State = "saving";
            mSelectedOption = 0;
        } else if (Settings::State == "pause options") {
            Settings::State = "pause";
            mSelectedOption = 0;
        } else if (Settings::State == "erase") {
            if (mSelectedOption == 0) {
                Settings::File->deleteFile(mSelectedSaveFile);
                Settings::File->loadFiles();
                Settings::State = "title";
            } else {
                Settings::State = "title";
                mSelectedOption = 0;
            }
        } else if (Settings::State == "options") {
            if (mSelectedOption == 2) //show credits
            {
                mTimer = 0;
                mCreditScrollY = 100;
                Settings::State = "credits";
            } else if (mSelectedOption == 3) //debug menu
            {
                mSelectedOption = 0;
                Settings::State = "debug menu";
            }
        }else if (Settings::State == "credits") {
            mSelectedOption = 0;
            Settings::State = "options";
        } else if(Settings::State == "debug menu")
        {
            if(mSelectedOption == 14)
            {
                mSelectedOption = 0;
                Settings::State = "options";
            }

        }
    }

    if (e.pressed.square) {
        if (Settings::State == "load") {
            Settings::State = "erase";
            mSelectedSaveFile = mSelectedOption;
            mSelectedOption = 0;
        }
    }

    if (e.pressed.triangle) {
        if (Settings::LevelEditor) {
            if (Settings::State == "level editor settings") {
                Settings::State = "level editor";
                mSelectedOption = 0;
            } else if (Settings::State == "level editor rgb") {
                Settings::State = "level editor settings";
                mSelectedOption = 5;
            } else if (Settings::State == "level editor bg speed") {
                Settings::State = "level editor settings";
                mSelectedOption = 6;           
            } else if (Settings::State == "pause options" || Settings::State == "save") {
                Settings::State = "pause";
                mSelectedOption = 0;
            }
        } else {
            if (Settings::State != "game" && Settings::State != "pause" && Settings::State != "pause options" && Settings::State != "save") {
                {
                    if (Settings::State == "level editor create folder") {
						Settings::State = "level editor set game";
						mSelectedOption = 0;				
                        SaveFile::GameFolder = 0;
                    } else if (Settings::State == "level editor set game") {
						Settings::State = "title";
					}                    
                    else if (Settings::State == "credits" || Settings::State == "debug menu") {
                        Settings::State = "options";
                    } else {
                        Settings::State = "title";
                        SaveFile::refreshGameList();
                        SaveFile::refreshLevelList();
                    }
                }
            } else if (Settings::State == "pause options" || Settings::State == "save") {
                Settings::State = "pause";
            }
            mSelectedOption = 0;
        }
    }
    
    if (e.pressed.up)
    {
		if (Settings::State == "level editor create folder")
		{
			if(mLetter > 10)
				mLetter--;
			else
			{
				if(mNewGameName.length() > 0)
					mLetter = 36;
				else
					mLetter = 35;
			}
		}
	}
	
    if (e.pressed.down)
    {
		if (Settings::State == "level editor create folder")
		{
			int limit = 35;
			if(mNewGameName.length() > 0)
				limit = 36;
			else 
				limit = 35;
		
			if(mLetter < limit)
				mLetter++;
			else
				mLetter = 10;
		}
	}
	   
    
    if (e.pressed.left || e.pressed.right) {
        if (Settings::State == "options" || Settings::State == "pause options") {
            if (mSelectedOption == 0) {
                if (Settings::Music) {
                    Settings::Music = false;
                    main::mMusic->pause();
                } else {
                    Settings::Music = true;
                    main::mMusic->Music::play();
                }
            }
            if (mSelectedOption == 1) {
                Settings::Sound = !Settings::Sound;
            }
			main::mSoundEffect->play("option.wav", 0);
        } else if (Settings::State == "new" || Settings::State == "level editor load game") {
            if (e.pressed.left && SaveFile::GameFolder > 0) {
                SaveFile::GameFolder--;
            } else if (e.pressed.right && SaveFile::GameFolder < mNumberOfOptions) {
                SaveFile::GameFolder++;
            }
        } else if (Settings::State == "level editor set game") {
            if (e.pressed.left && SaveFile::GameFolder > 0) {
                SaveFile::GameFolder--;
            } else if (e.pressed.right && SaveFile::GameFolder < mNumberOfOptions) {
                SaveFile::GameFolder++;
            }
        } else if (Settings::State == "level editor load level") {
            if (e.pressed.left && mSelectedOption > 0) {
                mSelectedOption--;
            } else if (e.pressed.right && mSelectedOption < mNumberOfOptions) {
                mSelectedOption++;
            }

        } else if (Settings::State == "level editor rgb") {
            int value;
            if (Settings::State == "level editor rgb") {
                int minOption;
                int maxOption;
                switch (mSelectedOption) {
                    case 0: value = mLevelHeader->BgColor->getRed(); minOption = 0; maxOption = 255; break;
                    case 1: value = mLevelHeader->BgColor->getGreen(); minOption = 0; maxOption = 255; break;
                    case 2: value = mLevelHeader->BgColor->getBlue(); minOption = 0; maxOption = 255; break;
                    default: value = 0; minOption = 0; maxOption = 0; break;
                }
                if (e.pressed.left && value > minOption) {
                    value--;
                } else if (e.pressed.right && value < maxOption) {
                    value++;
                }

                switch (mSelectedOption) {
                    case 0: mLevelHeader->BgColor = new Color(value, mLevelHeader->BgColor->getGreen(), mLevelHeader->BgColor->getBlue()); break;
                    case 1: mLevelHeader->BgColor = new Color(mLevelHeader->BgColor->getRed(), value, mLevelHeader->BgColor->getBlue()); break;
                    case 2: mLevelHeader->BgColor = new Color(mLevelHeader->BgColor->getRed(), mLevelHeader->BgColor->getGreen(), value); break;
                    default: value = 0; break;
                }
                main::mEditorPanel->setBackgroundColor(mLevelHeader->BgColor);
                main::mEditorPanel->setHeader(mLevelHeader);
            }
        } else if (Settings::State == "level editor bg speed") {
            int value;

            if (Settings::State == "level editor bg speed") {
                int minOption;
                int maxOption;
                switch (mSelectedOption)
                {
                    case 0: value = mLevelHeader->BgSpeed[0];  minOption = 0; maxOption = 5; break;
                    case 1: value = mLevelHeader->BgSpeed[1];  minOption = 0; maxOption = 5; break;
                    case 2: value = mLevelHeader->BgSpeed[2]; minOption = 0; maxOption = 5; break;
                    default: value = 0; minOption = 0; maxOption = 0;break;
                }
                if (e.pressed.left && value > minOption) {
                    value--;
                } else if (e.pressed.right && value < maxOption) {
                    value++;
                }

                switch (mSelectedOption) {
                    case 0: mLevelHeader->BgSpeed[0] = value; main::mEditorPanel->setBackgroundSpeed(0, value); break;
                    case 1: mLevelHeader->BgSpeed[1] = value; main::mEditorPanel->setBackgroundSpeed(1, value); break;
                    case 2: mLevelHeader->BgSpeed[2] = value; main::mEditorPanel->setBackgroundSpeed(2, value); break;
                    default: value = 0; break;
                }

                main::mEditorPanel->setHeader(mLevelHeader);
            }
        }
        else if(Settings::State == "debug menu")
        {
            int value;
            int minOption;
            int maxOption;
            switch (mSelectedOption) {
                case 5: value = Settings::GameSpeed; minOption = 1; maxOption = 10; break;
                case 6: value = Settings::MusicTest; minOption = 1; maxOption = 10; break;
                case 7: value = Settings::SoundTest; minOption = 1; maxOption = 10; break;
                case 12: value = Settings::MainCharacter; minOption = 1; maxOption = SaveFile::getFolderCount("res/chr/"); break;
                default: value = 0; minOption = 0; maxOption = 0; break;
            }
            if (e.pressed.left && value > minOption) {
                value--;
            } else if (e.pressed.right && value < maxOption) {
                value++;
            }
            switch (mSelectedOption)
            {
                case 0: Settings::LevelSelect = !Settings::LevelSelect; break;
                case 1: Settings::Invincible = !Settings::Invincible; break;
                case 2: Settings::FreezeTime = !Settings::FreezeTime; break;
                case 3: Settings::InfiniteLives = !Settings::InfiniteLives; break;
                case 4: Settings::Powerups = !Settings::Powerups; break;
                case 5: Settings::GameSpeed = value; break;
                case 6: Settings::MusicTest = value; break;
                case 7: Settings::SoundTest = value; break;
                case 8: Settings::Animation = !Settings::Animation; break;
                case 9: Settings::Background = !Settings::Background; break;
                case 10: Settings::RemoveEnemies = !Settings::RemoveEnemies; break;
                case 11: Settings::DrawBounds = !Settings::DrawBounds; break;
                case 12: Settings::MainCharacter = value; setCharacter(); break; /////Main.mFrame.mGamePanel.createPlayer(); break;
                case 13: Settings::EditMainGame = !Settings::EditMainGame; break;      
                default: break;
            }           
        }
        else if (Settings::State == "level editor settings") {

            int value;
            int minOption;
            int maxOption;
            switch (mSelectedOption) {
                case 0: value = mLevelHeader->World; minOption = 1; maxOption = 99; break;
                case 1: value = mLevelHeader->Level; minOption = 1; maxOption = 99; break;
                case 2: value = mLevelHeader->Area; minOption = 0; maxOption = 99; break;
                case 3: value = mLevelHeader->Bg[0]; minOption = 0; maxOption = SaveFile::getFileCount("res/bgr/near/", "png") -1; break;
                case 4: value = mLevelHeader->Bg[1]; minOption = 0; maxOption = SaveFile::getFileCount("res/bgr/middle/", "png") -1; break;
                case 5: value = mLevelHeader->Bg[2]; minOption = 0; maxOption = SaveFile::getFileCount("res/bgr/far/", "png") -1; break;
                case 6: value = mLevelHeader->BgSpeed[0]; minOption = 0; maxOption = 0; break;
                case 7: value = mLevelHeader->BgAlign; minOption = 0; maxOption = 1; break;
                case 8: value = mLevelHeader->Tileset0; minOption = 1; maxOption = SaveFile::getFolderCount("res/obj/block/"); break;
                case 9: value = mLevelHeader->Tileset16; minOption = 1; maxOption = SaveFile::getFolderCount("res/obj/terrain/"); break;
                case 10: value = mLevelHeader->Tileset32; minOption = 1; maxOption = SaveFile::getFolderCount("res/obj/scenery/"); break;
                case 11: value = mLevelHeader->Music; minOption = 1; maxOption = SaveFile::getFileCount("res/snd/", "mp3"); break;
                case 12: value = mLevelHeader->Width / 16; minOption = 30; maxOption = 999; break;
                case 13: value = mLevelHeader->Height / 16; minOption = 17; maxOption = 999; break;
                case 14: value = mLevelHeader->StartX; minOption = 0; maxOption = mLevelHeader->Width; break;
                case 15: value = mLevelHeader->StartY; minOption = 0; maxOption = mLevelHeader->Height; break;
                case 16: value = mLevelHeader->EndWorld; minOption = 1; maxOption = 99; break;
                case 17: value = mLevelHeader->EndLevel; minOption = 1; maxOption = 99; break;
                case 18: value = mLevelHeader->EndArea; minOption = 1; maxOption = 99; break;
                case 19: value = mLevelHeader->TimeLimit; minOption = 0; maxOption = 999; break;
                case 20: value = mLevelHeader->EndX; minOption = 0; maxOption = mLevelHeader->Width; break;
                case 21: value = mLevelHeader->EndY; minOption = 0; maxOption = mLevelHeader->Height; break;
                case 22: value = mLevelHeader->BonusX; minOption = 0; maxOption = mLevelHeader->Width; break;
                case 23: value = mLevelHeader->BonusY; minOption = 0; maxOption = mLevelHeader->Height; break;
                default: value = 0; minOption = 0; maxOption = 0; break;
            }
            if (e.pressed.left && value > minOption) {
                value--;
            } else if (e.pressed.right && value < maxOption) {
                value++;
            }

            switch (mSelectedOption) {
                case 0: mLevelHeader->World = value; break;
                case 1: mLevelHeader->Level = value; break;
                case 2: mLevelHeader->Area = value; break;
                case 3: if(mLevelHeader->Bg[0] != value) { mLevelHeader->Bg[0] = value; main::mEditorPanel->setBackground(mLevelHeader->Bg); } break;
                case 4: if(mLevelHeader->Bg[1] != value) { mLevelHeader->Bg[1] = value; main::mEditorPanel->setBackground(mLevelHeader->Bg); } break;
                case 5: if(mLevelHeader->Bg[2] != value) { mLevelHeader->Bg[2] = value; main::mEditorPanel->setBackground(mLevelHeader->Bg); } break;
                case 6: break; //set in "level editor bg speed" screen
                case 7: mLevelHeader->BgAlign = value; main::mEditorPanel->setBackgroundAlign(value); break;
                case 8: mLevelHeader->Tileset0 = value; main::mEditorPanel->setTileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32); break;
                case 9: mLevelHeader->Tileset16 = value; main::mEditorPanel->setTileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32); break;
                case 10: mLevelHeader->Tileset32 = value; main::mEditorPanel->setTileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32); break;
                case 11: mLevelHeader->Music = value; main::mEditorPanel->setMusic(value); break;
                case 12: mLevelHeader->Width = value * 16; main::mEditorPanel->setHeader(mLevelHeader); main::mEditorPanel->setBackgroundWidth(value * 16); break;
                case 13: mLevelHeader->Height = value * 16; main::mEditorPanel->setHeader(mLevelHeader); main::mEditorPanel->setBackgroundHeight(value * 16); break;
                case 14: mLevelHeader->StartX = value; break;
                case 15: mLevelHeader->StartY = value; break;
                case 16: mLevelHeader->EndWorld = value; break;
                case 17: mLevelHeader->EndLevel = value; break;
                case 18: mLevelHeader->EndArea = value; break;
                case 19: mLevelHeader->TimeLimit = value; break;
                case 20: mLevelHeader->EndX = value; break;
                case 21: mLevelHeader->EndY = value; break;
                case 22: mLevelHeader->BonusX = value; break;
                case 23: mLevelHeader->BonusY = value; break;
                default: value = 0; break;
            }
            main::mEditorPanel->setHeader(mLevelHeader);

        }
    }
}




