#include "GamePanel.h"
 
#include "main.h"

int Settings::LimitCameraX;
int Settings::LimitCameraY;
bool Settings::DebugMenu;
Music* main::mMusic;

/**
 * Constructs the GamePanel
 */
GamePanel::GamePanel() {
	mPlayer = NULL;
    SaveFile::refreshGameList();
    SaveFile::refreshLevelList();
    mUserInterface = new UserInterface();
    mKeyBuffer = "";
    showTitleScreen();                                    
}

/**
 * Shows the title screen
 */
void GamePanel::showTitleScreen() {

	mOnBonus = false;
    Settings::State = "title";
    mEarthquake = false;
    mScriptMode = 0;
    loadLevel("Main Game", 0, 0, 0); 
	setMusic(mLevelHeader->Music);
    mPlayer = new Player(Settings::MainCharacter, 0, 0, 3, 0, 0, 0, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);

}  

/**
 * Starts a new game and loads the first level
 */
void GamePanel::newGame() {

	char tmp[100];
	sprintf(tmp, "mnt/sd/Dragon Island/lvl/%s/0.0.1.ls", SaveFile::GameName[SaveFile::GameFolder].c_str());	
	ifstream file(tmp); 
	if(file.good()) //open intro level
	{	
        file.close();
		file.clear();
        Settings::State = "game";
        //mScreenTransition = 2;
        loadLevel(SaveFile::GameName[SaveFile::GameFolder], 0, 0, 1);
    }
	else {
		closeLevel(); 
        if(!loadLevel(SaveFile::GameName[SaveFile::GameFolder], 1, 1, 1))  
		{ 
			showTitleScreen();
		}
		else
		{
			Settings::State = "level open";	
        	Settings::Paused = true; 
	     	mPlayer = new Player(Settings::MainCharacter, 0, 0, 3, 0, 0, mLevelInfo->getHeader()->TimeLimit, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);
		    loadScript();
		    updateSaveGameFile();

		}
    }
}

/**
 * Creates the player object with the selected character
 */
void GamePanel::createPlayer() {
    mPlayer = new Player(Settings::MainCharacter, 0, 0, 3, 0, 0, 0, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);
}   

void GamePanel::closeLevel()
{
	delete mPlayer;
	delete mLevelMap;
	delete mLevelInfo;	
	delete mLevelHeader;
	delete mCamera;
    delete mBackground;	
	delete mMapTileset;
	mEntities.clear();
	main::mMusic->stop();
}

/**
 * Loads the level and creates the player, tileset, entities and background
 * @param gameFolder The folder containing the levels
 * @param world The world number of the level
 * @param level The level number of the level
 * @param area The area number of the level
 * @return True if the level exists
 */
bool GamePanel::loadLevel(string gameFolder, int world, int level, int area) {

    mLevelInfo = new Level();
	char path[100];
	sprintf(path, "mnt/sd/Dragon Island/lvl/%s/%d.%d.%d.lvl", gameFolder.c_str(), world, level, area);	
 	if (mLevelInfo->loadLevel(path)) {

        mLevelHeader = mLevelInfo->getHeader();
        mLevelMap = mLevelInfo->toMap();

        mCamera = new Point(0, 0);

        mMapTileset = new Tileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32);
        mBackground = new Background(mLevelHeader->Bg, mLevelHeader->Width, mLevelHeader->Height, mLevelHeader->BgSpeed, mLevelHeader->BgAlign); 
		mBackground->setColor(&mLevelHeader->BgColor);
		
        //setMusic(mLevelHeader.Music);
        for (int i = 0; i < mLevelInfo->getEntityCount(); i++) {
            EntityDescription* s = mLevelInfo->getEntityDescription(i);
			//cout << s.Y << endl;
			if (!Settings::RemoveEnemies) {
				Entity* e = new Entity(s->Tile, 'l', s->X * 16, s->Y * 16);
				mEntities.push_back(e);	
            } else //remove enemies
            {
                if (s->Tile < 20) {
					Entity* e = new Entity(s->Tile, 'l', s->X * 16, s->Y * 16);
					mEntities.push_back(e);
                }
            }
        }

        //find castle side extension		
        mCastlePosition = new Point(-100, -100); //set offscreen
        for (int i = 0; i < mLevelInfo->getObjectCount(); i++) {
            if (mLevelInfo->getTileDescription(i).Tile == 2 && mLevelInfo->getTileDescription(i).Tileset == 0) {
                mCastlePosition = new Point(mLevelInfo->getTileDescription(i).X * 16, mLevelInfo->getTileDescription(i).Y * 16);
            }
        }

        return true;
    } else //level not found
    {
        return false;
    }
}

/**
 * Advances to the next level specified by the header information
 */
void GamePanel::advanceLevel() {


    Settings::State = "game";	
	closeLevel();

    Settings::LimitCameraX = 240;
    Settings::LimitCameraY = 136;

    if (mLevelHeader->TimeLimit == 0) //no timelimit set
    {
        mLevelHeader->TimeLimit = mPlayer->getTime();
    }

    if (loadLevel(SaveFile::GameName[SaveFile::GameFolder], mLevelHeader->EndWorld, mLevelHeader->EndLevel, mLevelHeader->EndArea)) {
        if (mOnBonus) //restart player at bonus position
        {
            int state = mLevelInfo->getHeader()->BonusState; //set start state to opposite of bonus state
            if (state == 2) {
                state = 3;
            } else if (state == 3) {
                state = 2;
            } else if (state == 4) {
                state = 5;
            } else if (state == 5) {
                state = 4;
            }
            mPlayer = new Player(Settings::MainCharacter, mPlayer->getSize(), mPlayer->getInvincible(), mPlayer->getLives(), mPlayer->getCoins(), mPlayer->getScore(), mLevelInfo->getHeader()->TimeLimit, state, mLevelHeader->BonusX * 16, mLevelHeader->BonusY * 16);
            mLevelInfo->getHeader()->BonusState = 99; //stop player entering pipe again
            loadScript();
            mOnBonus = false;
        } else //new level
        {      
        	Settings::State = "level open";
            mPlayer = new Player(Settings::MainCharacter, mPlayer->getSize(), 0, mPlayer->getLives(), mPlayer->getCoins(), mPlayer->getScore(), mLevelInfo->getHeader()->TimeLimit, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);
            loadScript();
            updateSaveGameFile();
        }
    } else {	
        //System.out.println("Level does not exist");
        showTitleScreen();
    }
}

/**
 * Advances to the bonus level
 */
void GamePanel::advanceBonus() {
    if (loadLevel(SaveFile::GameName[SaveFile::GameFolder], mLevelHeader->World, mLevelHeader->Level, 0)) {
        if (mLevelHeader->TimeLimit == 0) //no timelimit set
        {
            mLevelHeader->TimeLimit = mPlayer->getTime(); //continue time from previous level
        }
        mPlayer = new Player(Settings::MainCharacter, mPlayer->getSize(), mPlayer->getInvincible(), mPlayer->getLives(), mPlayer->getCoins(), mPlayer->getScore(), mLevelInfo->getHeader()->TimeLimit, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);
        setMusic(mLevelHeader->Music);
        loadScript();
    } else {
        //System.out.println("Bonus does not exist");
        showTitleScreen();
    }
} 

/**
 * Resets the level
 */
void GamePanel::resetLevel() {

    Settings::State = "game";  
	closeLevel(); 
    if(loadLevel(SaveFile::GameName[SaveFile::GameFolder], mLevelHeader->World, mLevelHeader->Level, mLevelHeader->Area))
    {
    	mPlayer = new Player(Settings::MainCharacter, 0, 0, mPlayer->getLives(), mPlayer->getCoins(), mPlayer->getScore(), mLevelInfo->getHeader()->TimeLimit, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);  	
    	setMusic(mLevelHeader->Music);
    	loadScript();
    	updateSaveGameFile();
    }
    else
    	showTitleScreen();
}     

/**
 * Updates the save game information
 */
void GamePanel::updateSaveGameFile() {
    mSaveGameInfo = new Game();
    mSaveGameInfo->Game = SaveFile::GameFolder;
    mSaveGameInfo->World = mLevelHeader->World;
    mSaveGameInfo->Level = mLevelHeader->Level;
    mSaveGameInfo->Character = Settings::MainCharacter;
    mSaveGameInfo->Size = mPlayer->getSize();
    mSaveGameInfo->Lives = mPlayer->getLives();
    mSaveGameInfo->Coins = mPlayer->getCoins();
    mSaveGameInfo->Score = mPlayer->getScore();
}

/**
 * Saves the game progress
 * @param saveFileNumber The save game file number
 */
void GamePanel::saveGame(int saveFileNumber) {
    Settings::File->setGame(saveFileNumber, mSaveGameInfo);
    Settings::File->saveFile(saveFileNumber);
}
 
/**
 * Loads a save game
 * @param saveFileNumber The file number containing the save game information
 */
void GamePanel::loadSaveGame(int saveFileNumber) {
    Game* g = Settings::File->getGame(saveFileNumber);
    Settings::State = "level open";  
    if(loadLevel(SaveFile::GameName[g->Game], g->World, g->Level, 1))
    {
        mPlayer = new Player(g->Character, g->Size, 0, g->Lives, g->Coins, g->Score, mLevelInfo->getHeader()->TimeLimit, mLevelHeader->StartState, mLevelHeader->StartX * 16, mLevelHeader->StartY * 16);
        loadScript();
        updateSaveGameFile();
    }
    else
    	showTitleScreen();
}  

/**
 * Sets the level music
 * @param file The mp3 file number
 */
void GamePanel::setMusic(int file) {
    if (file != 0 && Settings::Music) {
		char path[100];
		sprintf(path, "mnt/sd/Dragon Island/snd/%d.ogg", file);	
        if (main::mMusic != NULL) {
            main::mMusic->stop();
        }
        main::mMusic = new Music(path);
    } else {
        if (main::mMusic != NULL) {
            main::mMusic->stop();
        }
    }
}   

/**
 * Adds a block explosion
 * @param x The x coordinate
 * @param y The y coordinate
 */
void GamePanel::addBlockExplosion(int x, int y) {
    x = x * 16;
    y = y * 16;
    mMapTileset->setBlockExplode(x, y, 'l');
    mMapTileset->setBlockExplode(x + 16, y, 'r');
    mMapTileset->setBlockExplode(x, y + 16, 'l');
    mMapTileset->setBlockExplode(x + 16, y + 16, 'r');
}   

/**
 * Adds a new entity to the collection
 * @param sprite the entities sprite sheet
 * @param direction the start direction 'l' = left 'r' = right
 * @param x the x coordinate
 * @param y the y coordinate
 */
void GamePanel::addEntity(int sprite, char direction, int x, int y) {
	Entity* e = new Entity(sprite, direction, x, y);
	mEntities.push_back(e);
}

/**
 * Returns the level header information
 * @return The level header
 */
Header* GamePanel::getHeader() {
    return mLevelHeader;
}  

/**
 * Returns the players lives count
 * @return The players lives count
 */
int GamePanel::getPlayerLives() {
    return mPlayer->getLives();
}

/**
 * Plays back player movements from a level script
 * @param gameFolder the folder containing the levels
 * @param world the world number of the level
 * @param level the level number of the level
 * @param area the area number of the level
 */
void GamePanel::playbackMovement(string gameFolder, int world, int level, int area) {
 	char path[100];
	sprintf(path, "mnt/sd/Dragon Island/rec/%s.%d.%d.%d.rec", gameFolder.c_str(), world, level, area);  
    mRecordScript.open(path);
}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void GamePanel::split(const string& s, char c, vector<string>& v) 
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
 * Loads a script containing commands which run in a time sequence
 */
void GamePanel::loadScript() {
 	
	char path[100];
	sprintf(path, "mnt/sd/Dragon Island/lvl/%s/%d.%d.%d.ls", SaveFile::GameName[SaveFile::GameFolder].c_str(), mLevelInfo->getHeader()->World, mLevelInfo->getHeader()->Level, mLevelInfo->getHeader()->Area);  	
	ifstream input(path); 
	if(input.good())
	{
        mPlaybackScript.clear();
		string ln;
		while (input.good())
		{                
			ScriptCommand* sc = new ScriptCommand();
			getline(input, ln);
	
			vector<string> v;   
			split(ln, ',', v);
	
            sc->time = atoi(v[0].c_str());
            sc->command = v[1];
            sc->text = v[2];
            sc->x = atoi(v[3].c_str());
            sc->y = atoi(v[4].c_str());
            sc->width = atoi(v[5].c_str());
            sc->height = atoi(v[6].c_str());
            sc->direction = v[7][0];
            
			mPlaybackScript.push_back(*sc);
        }

        Settings::LimitCameraX = 75;
        Settings::LimitCameraY = 136;

        mScriptTimer = 0;
        mScriptMode = 3;

        playbackMovement(SaveFile::GameName[SaveFile::GameFolder], mLevelInfo->getHeader()->World, mLevelInfo->getHeader()->Level, mLevelInfo->getHeader()->Area);

        input.close();

    }
	else {
        Settings::LimitCameraX = 240;
        Settings::LimitCameraY = 136;
        mScriptMode = 0;
    }
}

/**
 * Draws the player, background, tileset and entities, handles collision detection
 * and control the state of the game
 * @param gl The graphics context
 */
void GamePanel::paintComponent(SDL_Surface* g) {

    if (main::mEditorPanel != NULL) //level editor
    {
        main::mEditorPanel->paintComponent(g);
    } 
	else  //main game
    {
        if (!Settings::Paused) {
            //set camera and draw background
            mCamera->x = mPlayer->getLeft() - Settings::LimitCameraX;
            mCamera->y = mPlayer->getDown() - Settings::LimitCameraY;
            mCamera = mBackground->draw(g, mCamera);

            //draw objects 
            mMapTileset->drawMap(g, mLevelMap, mCamera);

            //play back player movement .rec
            if ((mRecordScript != NULL && mScriptMode > 0) && !mUserInterface->showTransition) {
                if (mScriptMode > 1 && mScriptMode < 4) //play
                {
                    if(mRecordScript.good())
					{
                        //set player movement
                        mScriptTimer++; //advance capture / playback  timer
						string ln;
						getline(mRecordScript, ln);
			
						vector<string> v;   
						split(ln, ',', v);	

                        mPlayer->setX(atoi(v[0].c_str()));
                        mPlayer->setY(atoi(v[1].c_str()));
                        mPlayer->setDirection(v[2][0]);
                        mPlayer->setAnimationStateIndex(atoi(v[3].c_str()));
                    }
					else //end of .rec file
                    {
                        mRecordScript.close();
                        if (mScriptMode == 3) {
                            advanceLevel(); //end of script
                        }
                        mScriptMode = 0;
                    }
                }

                if (mScriptMode == 3) {
                    //read command script file
                    for (int i = 0; i < mPlaybackScript.size(); i++) {
                        if ((mScriptTimer == mPlaybackScript[i].time) || mPlaybackScript[i].timer > 0) 
                        {
                            ScriptCommand sc = mPlaybackScript[i];

                            if (sc.command =="Earthquake") {
                                mEarthquake = !mEarthquake; //start/stop quake
                            } else if (sc.command == "Sound") {
								main::mSoundEffect->play("tank.wav", sc.x);
                            } else if (sc.command == "Speech") {
                                mUserInterface->drawSpeech(g, sc.x, sc.y, sc.width, sc.height, sc.text, sc.direction);
                                if (mPlaybackScript[i].timer < mScriptCommandPause) {
                                    if (mPlaybackScript[i].timer == 0) {
										main::mSoundEffect->play("speech.wav", 0);
                                    }
                                    mPlaybackScript[i].timer++;
                                } else {
                                    mPlaybackScript[i].timer = 0;
                                }
                            } else if (sc.command == "Enemy") {
                                addEntity(atoi(sc.text.c_str()), sc.direction, sc.x, sc.y);
                            } else if (sc.command == "Block") {
                                mLevelInfo->addTile(1, 0, sc.x, sc.y, sc.width, sc.height);
                                mLevelMap = mLevelInfo->toMap();
                            } else if (sc.command == "Collision") {

                                if (mLevelMap->getTile(sc.x, sc.y)->collision == 0) {
                                    mLevelMap->setCollisionTile(sc.x, sc.y, 1);
                                } else {
                                    mLevelMap->removeTile(sc.x, sc.y);
                                }
                            } else if (sc.command == "Explosion") {
								main::mSoundEffect->play("block.wav", 0);
                                mLevelMap->removeTile(sc.x, sc.y);
                                addBlockExplosion(sc.x, sc.y);
                            } else if (sc.command == "Advance Level") {
                                advanceLevel();
                            }
                        }
                    }
                }
            }

            //loop through enemies array list
            for (int i = 0; i < mEntities.size(); i++) {
                if (mEntities[i]->getX() > mCamera->x - 250
                        && mEntities[i]->getX() < mCamera->x + 580) //enemy is on screen
                {
                    mEntities[i]->detectMapCollision(mLevelMap); //detect map collisions
                    mEntities[i]->draw(g, mCamera); //paint / move enemy
                }

		        if (mEntities[i]->getAnimationState() == "dead" && mEntities[i]->getType() != 1) //remove dead sprite except fireball
		        {	
					mEntities.erase(mEntities.begin() + i);
		        }
            }

            //draw map object explosions
            mMapTileset->drawExplosions(g, mLevelMap, mCamera);

            //detect collisions between player and entities
            mPlayer->detectEntityCollision(mEntities);

            //detect collisions between player and map objects
            mPlayer->detectMapCollision(mLevelMap);

            //draw player
            mPlayer->draw(g, mCamera);

            if (mPlayer->getPipeState() > 1) //redraw map objects
            {
                mMapTileset->drawMap(g, mLevelMap, mCamera); //draw pipe over player
            }

            //draw castle extension over player
			mMapTileset->drawImage(g, mMapTileset->getFrame(2, 0, 0), mCastlePosition->x - mCamera->x, mCastlePosition->y - mCamera->y );
          
            //advance level if player has reached end state
            if (mPlayer->getEndState() == 7) {
                advanceLevel();
            } else if (mPlayer->getEndState() == 8) //goto bonus level if player has reached bonus level
            {
                advanceBonus();
                mOnBonus = true;
            }

            //player has died falling off map
            if (mPlayer->getUp() > mLevelMap->getHeight() + mPlayer->getHeight()) {
                if (mPlayer->getAnimationState() != "die") {
                    //new SoundEffect("res/sfx/die.wav").start();
                }
                mPlayer->removeLife(); //lose life
                if (mPlayer->getLives() > 0) {
                    resetLevel(); //reset level
                } else {
                    showTitleScreen(); //lost all lives reset to title screen
                }
            }

            if (mPlayer->getLives() < 0) {
                showTitleScreen();
            }
        }

    }

    //draw status bar
    if (Settings::State == "game") {
        if (mScriptMode < 3 && !Settings::DrawBounds) //not scripted playback
        {
            mUserInterface->drawStatus(g, 50, 15, mPlayer->getLives(), mPlayer->getCoins(), mPlayer->getScore(), mPlayer->getTime());
        }
    } else {
        mUserInterface->draw(g); //draw screen menu
    }

    //debug mode bounding rectangle around sprite and debug text on screen
    if (Settings::DrawBounds && Settings::State == "game") {
		char str[100];
        sprintf (str, "Level: %d-%d-%d",  mLevelInfo->getHeader()->World, mLevelInfo->getHeader()->Level,  mLevelInfo->getHeader()->Area);	
		mUserInterface->drawString(g, str, 15, 15);

        sprintf (str, "Tileset: %d-%d-%d", mLevelInfo->getHeader()->Tileset0, mLevelInfo->getHeader()->Tileset16, mLevelInfo->getHeader()->Tileset32);	
		mUserInterface->drawString(g, str, 15, 30);

        sprintf (str, "Background: %d-%d-%d",  mLevelInfo->getHeader()->Bg[0],  mLevelInfo->getHeader()->Bg[1], mLevelInfo->getHeader()->Bg[2]);	
		mUserInterface->drawString(g, str, 15, 45);

        sprintf (str, "Music: %d", mLevelInfo->getHeader()->Music);	
		mUserInterface->drawString(g, str, 15, 60);

        sprintf (str, "Map Width: %d px", mLevelInfo->getHeader()->Width);	
		mUserInterface->drawString(g, str, 15, 75);

        sprintf (str, "Map Height: %d px", mLevelInfo->getHeader()->Height);	
		mUserInterface->drawString(g, str, 15, 90);

        sprintf (str, "Character: %d", Settings::MainCharacter);	
		mUserInterface->drawString(g, str, 150, 15);

        sprintf (str, "Coordinates: x=%d y=%d", mPlayer->getBounds()->x,  mPlayer->getBounds()->y);	
		mUserInterface->drawString(g, str, 150, 30);

		sprintf (str, "Enemy: x=%d y=%d", mEntities[0]->getBounds()->x, mEntities[0]->getBounds()->y);	
		mUserInterface->drawString(g, str, 150, 45);

        sprintf (str, "Animation: %s", mPlayer->getAnimationState().c_str());	
		mUserInterface->drawString(g, str, 150, 60);

        sprintf (str, "Time: %d",  mPlayer->getTime());	
		mUserInterface->drawString(g, str, 150, 75);

        sprintf (str, "Script Timer: %d", mScriptTimer);	
		mUserInterface->drawString(g, str, 150, 90);
    }

    if (mUserInterface->showTransition) {
        mUserInterface->drawScreenTransition(g);
    }

    if (mEarthquake) {
        if (mEarthquakeTimer < 2) {
            mEarthquakeTimer++;
        } else {
            mEarthquakeTimer = 0;
        }
        //g.drawImage(back_buffer, -mEarthquakeTimer, -mEarthquakeTimer, getWidth() + 2, getHeight() + 1, this); //move x,y
    } else {
        //g.drawImage(back_buffer, 0, 0, getWidth() + 2, getHeight() + 1, this); //draw back buffer
    }
}
  
/**
 * Invoked when a key has been pressed<BR>
 * Sets the player controls, pause the game or pass event the level editor panel
 * @param e event which indicates that a keystroke occurred in a component
*/
void GamePanel::keyPressed(SDL_Event e) {
    int keyCode = e.key.keysym.sym; //http://falconpl.org/project_docs/sdl/class_SDLK.html
    //cout<< keyCode << endl;

    if (Settings::State == "title") {

        if (keyCode == 273) {
            mKeyBuffer += 'u';
        } else if (keyCode == 274) {
            mKeyBuffer += 'd';
        } else if (keyCode == 276) {
            mKeyBuffer += 'l';
        } else if (keyCode == 275) {
            mKeyBuffer += 'r';
		} else if (keyCode == 98) {
            mKeyBuffer += 'b';
		} else if (keyCode == 97) {
            mKeyBuffer += 'a';
        }
        if (mKeyBuffer == "uuddlrlrba") //enable debug mode
        {
            Settings::DebugMenu = !Settings::DebugMenu;
            mKeyBuffer = "";
        }
    } else {
        mKeyBuffer = "";
    }
    
	if (keyCode == 279 && Settings::LevelSelect && Settings::State == "game") {
        advanceLevel();
    }

    if (Settings::State == "game" && mScriptMode < 2) {
        setControl(e, true);
    }

    if (keyCode == 13 && !mUserInterface->showTransition) {
        if (Settings::State == "game" || Settings::State == "level editor") {
			main::mSoundEffect->play("select.wav", 0);
            if (mScriptMode == 0) {
                Settings::Paused = true;
                Settings::State = "pause";
            } else //advance intro script
            {
                advanceLevel();
                mEarthquake = false;
            }
        } else {
            mUserInterface->keyPressed(e);
        }
    } else {
        if (Settings::State != "game" && Settings::State != "level editor") {
            mUserInterface->keyPressed(e);
        }
    }

}

/**
 * Invoked when a key has been released
 * @param e event which indicates that a keystroke occurred in a component
*/
void GamePanel::keyReleased(SDL_Event e) {
	setControl(e, false);
}

/**
 * Sets the controller flags for the player
 * @param e event which indicates that a keystroke occurred in a component
 * @param pressed True if a key is pressed
 */
void GamePanel::setControl(SDL_Event e, bool pressed) {
    int keyCode = e.key.keysym.sym; //http://falconpl.org/project_docs/sdl/class_SDLK.html
	//cout << keyCode << endl;
	if (mPlayer != NULL) {
        if (keyCode == 119 || keyCode == 32) { //w or space
            mPlayer->mControls.mJumpPressed = pressed;
        } else if (keyCode == 273) { //up
            mPlayer->mControls.mUpPressed = pressed;
        } else if (keyCode == 276) { //left
            mPlayer->mControls.mLeftPressed = pressed;
        } else if (keyCode == 274) { //down
            mPlayer->mControls.mDownPressed = pressed;
        } else if (keyCode == 275) { //right
            mPlayer->mControls.mRightPressed = pressed;
        } else if (keyCode == 304 || keyCode == 113) { //shift or q
            mPlayer->mControls.mRunPressed = pressed;
        }

	    if (!mPlayer->mControls.mRunPressed) {	
	        mPlayer->mControls.mRunReleased = true;
	    }
    }
}

/**
 * Invoked when a mouse button has been pressed on a component<BR>
 * Pass event to the level editor panel
 * @param e event which indicates that a mouse action occurred in a component
 */
void GamePanel::mouseMoved(SDL_Event e) {
    if (Settings::State == "level editor") {
        main::mEditorPanel->mouseMoved(e);
    }
}

/**
 * Invoked when a mouse button is pressed on a component and then dragged<BR>
 * Pass event to the level editor panel
 * @param e event which indicates that a mouse action occurred in a component
 */
void GamePanel::mouseDragged(SDL_Event e) {
    if (Settings::State == "level editor") {
        main::mEditorPanel->mouseDragged(e);
    }
}

/**
 * Invoked when a mouse button has been released on a component<BR>
 * Pass event to the level editor panel
 * @param e event which indicates that a mouse action occurred in a component
 */
void GamePanel::mouseReleased(SDL_Event e) {
    if (Settings::State == "level editor") {
        main::mEditorPanel->mouseReleased(e);
    }
}

/**
 * Invoked when a mouse button has been pressed on a component<BR>
 * Pass event to the level editor panel
 * @param e event which indicates that a mouse action occurred in a component
 */
void GamePanel::mousePressed(SDL_Event e) {
    if (Settings::State == "level editor") {
        main::mEditorPanel->mousePressed(e);
    }
}

/**
 * Invoked when the mouse wheel is rotated<BR>
 * Pass event to the level editor panel
 * @param e event which indicates that the mouse wheel was rotated in a component
 */
void GamePanel::mouseWheelMoved(SDL_Event e) {
    if (Settings::State == "level editor") {
        main::mEditorPanel->mouseWheelMoved(e);
    }
}

/**
 * Deallocates memory by destroying the GamePanel
 */
GamePanel::~GamePanel()
{
	closeLevel();
}






