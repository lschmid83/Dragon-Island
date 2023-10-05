#include "EditorPanel.h"

/**
 * Constructs the EditorPanel
 */
EditorPanel::EditorPanel() {
    mSelectionDescription = "Select Object";
	mAnimationFrame = 0;
	mControlTimer = 0;
	mMouseX = 0;
	mMouseY = 0;
}

/**
 * Loads a level file for editing
 * @param path The level file path e.g "lvl/Main Game/0.0.0.lvl" (title screen)
 */
void EditorPanel::loadLevel(string path) {

	char str[100];
    sprintf (str, "mnt/sd/Dragon Island/chr/%d/0.png", Settings::MainCharacter);
    mPlayer = new SpriteSheet(str, 32, 32, 29);

    mLevelInfo = new Level();
    sprintf (str, "%s", path.c_str());	
	mLevelInfo->loadLevel(str);

    mClick = 0;
    mEditorState = 0;
    mMouseSelectionRect = new Rectangle();
    mTile = new Tile();
    mLevelHeader = mLevelInfo->getHeader();
    mLevelMap = mLevelInfo->toMap();
    mMouseTileRect = new Rectangle(0, 0, 16, 16);
    mMouseTileRect->setLocation(mLevelHeader->Width, mLevelHeader->Height);
    mCamera = new Point(0, (mLevelHeader->StartY * 16) - 224);
    mEntities = new EntityCollection();
    mTileset = new Tileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32);
    setMusic(mLevelHeader->Music);
    setBackground(mLevelHeader->Bg);
    mMouseCursorRect = new Rectangle(0, 0, 5, 5);
    mMouseTileRect->setLocation(-100, -100);
    mMouseCursorRect->setLocation(-100, -100);
    mFontConsoleFont = new GameFont("speech");
    mControls = new Control();

}

/**
 * Saves a level which has been created using the level editor
 * @param path The file output path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
 */
void EditorPanel::saveLevel(string path) {
	char str[100];
    sprintf (str, "%s%d.%d.%d.lvl", path.c_str(), mLevelHeader->World, mLevelHeader->Level, mLevelHeader->Area);
	mLevelInfo->saveLevel(str);
}

/**
 * Sets the level header information
 * @param header The level header
 */
void EditorPanel::setHeader(Header* header) {
    mLevelInfo->setHeader(header);
    mLevelHeader = mLevelInfo->getHeader();
    mBackground->setWidth(mLevelHeader->Width);
    mBackground->setHeight(mLevelHeader->Height);
}

/**
 * Returns the level header information
 * @return The level header
 */
Header* EditorPanel::getHeader() {
    return mLevelInfo->getHeader();
}

/**
 * Sets the tileset resource folder, update the level header and recreate
 * objects with the new tileset
 * @param tileset0 The block tileset (res/block)
 * @param tileset16 The terrain tileset (res/terrain)
 * @param tileset32 The scenery tileset (res/scenery)
 */
void EditorPanel::setTileset(int tileset0, int tileset16, int tileset32) {
    mLevelHeader = mLevelInfo->getHeader();
    mLevelHeader->Tileset0 = tileset0;
    mLevelHeader->Tileset16 = tileset16;
    mLevelHeader->Tileset32 = tileset32;
    mTileset = new Tileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32);
    mLevelInfo->setHeader(mLevelHeader);
}

/**
 * Sets the background and update the level header
 * @param new_bgImg[] The background image resource (res/bgr) 0-near, 1-middle, 2-far
 */
void EditorPanel::setBackground(int bgImg[3]) {
    mBackground = new Background(bgImg, mLevelHeader->Width, mLevelHeader->Height, mLevelHeader->BgSpeed, mLevelInfo->getHeader()->BgAlign);
    if (bgImg[2] == 0) //far layer not set use header background color
    {
        mBackground->setColor(&mLevelHeader->BgColor);
    } else
    {
        mLevelHeader->BgColor = *mBackground->getColor();
    }
    mBackground->setLayerSpeed(0, mLevelInfo->getHeader()->BgSpeed[0]);
    mBackground->setLayerSpeed(1, mLevelInfo->getHeader()->BgSpeed[1]);
    mBackground->setLayerSpeed(2, mLevelInfo->getHeader()->BgSpeed[2]);
}

/**
 * Sets the speed of a background layer
 * @param layer[] The background layer 0-near, 1-middle 2-far
 * @param speed The horizontal scroll speed 0 - No scroll >0 - Set speed
 */
void EditorPanel::setBackgroundSpeed(int layer, int speed) {
    mBackground->setLayerSpeed(layer, speed);
}

/**
 * Sets the alignment of the background
 * @param align The alignment of the background 0-Repeat x axis / 1-Repeat y axis (far layer)
 */
void EditorPanel::setBackgroundAlign(int align) {
    mBackground->setAlign(align);
}

/**
 * Sets the background color
 * @param colour The new background color
 */
void EditorPanel::setBackgroundColor(Color* color) {
    mBackground->setColor(color);
}

/**
 * Sets the width of the background
 * @param width The width of the background
 */
void EditorPanel::setBackgroundWidth(int width) {
    mBackground->setWidth(width);
}

/**
 * Sets the height of the background
 * @param height The height of the background
 */
void EditorPanel::setBackgroundHeight(int height) {
    mBackground->setHeight(height);
}

/**
 * Sets the music
 * @param file The music resource file (res/snd)
 */
void EditorPanel::setMusic(int file) {
    if (file != 0 && Settings::Music) {
		char path[100];
        sprintf (path, "mnt/sd/Dragon Island/snd/%d.mp3", mLevelHeader->Music);
        //mMusic = new Music(path);
    } else {
        //if (mMusic != NULL) {
            //Music::closeMusic();
        //}
    }
}

/**
 * Invoked when a key has been pressed<BR>
 * Moves the camera around the map area, scroll through object/sprite selection
 * @param e Event which indicates that a keystroke occurred in a component
*/
void EditorPanel::keyPressed(SDL_Event e) {
    setControl(e, true);
    int kc = e.key.keysym.sym;
    if (kc == 282) {
        changeEditorState();
    } else if (kc == 280) {
        if (mEditorState > 0 && mEditorState < 4) {
            nextObject();
        } else if (mEditorState == 4) {
            nextSprite();
        }
    } else if (kc == 281) {
        if (mEditorState > 0 && mEditorState < 4) {
            previousObject();
        } else if (mEditorState == 4) {
            previousSprite();
        }
    }
}

void EditorPanel::keyReleased(SDL_Event e)
{
	setControl(e, false);
}


/**
 * Invoked when the mouse cursor has been moved onto a component<BR>
 * Sets the mouse and cursor rectangles x,y coordinates
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseMoved(SDL_Event e) {
    mMouseX = e.motion.x + mCamera->x;
    mMouseY = e.motion.y + mCamera->y;
    mMouseX = mMouseX / 16;
    mMouseY = mMouseY / 16;
    mMouseTileRect->setLocation(e.motion.x / 16 * 16, e.motion.y / 16 * 16);
    mMouseCursorRect->setLocation(e.motion.x, e.motion.y);
}

/**
 * Invoked when a mouse button is pressed on a component and then dragged<BR>
 * Sets the size of the selection rectangle
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseDragged(SDL_Event e) {
    mMouseCursorRect->setLocation(e.motion.x, e.motion.y);
    if (mEditorState > 0 && mEditorState < 4 && mMouseButton == 1) {
        mMouseX2 = e.motion.x + mCamera->x;
        mMouseY2 = e.motion.y + mCamera->y;
        mMouseX2 = mMouseX2 / 16 + 1;
        mMouseY2 = mMouseY2 / 16 + 1;
        if (mMouseX2 <= mMouseX) {
            mMouseX2 = mMouseX + 1;
        }
        if (mMouseY2 <= mMouseY) {
            mMouseY2 = mMouseY + 1;
        }
        mMouseSelectionRect->setBounds(mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
    }
}

/**
 * Invoked when a mouse button has been released on a component<BR>
 * Adds an object to the map and sets the size of selection rectangle
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseReleased(SDL_Event e) {

    if (mEditorState > 0 && mEditorState < 4 && mMouseButton == 1) //add object
    {
        if (e.button.button == SDL_BUTTON_LEFT) {
            mLevelInfo->addTile(mTile->tile1, mTile->tile2, mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
            //set box width/height if mouse dragged and x2,y2 are set
            if (mMouseX2 - mMouseX > 0 && mMouseY2 - mMouseY > 0) {
                mMouseSelectionRect->setBounds(mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
            } else //set box width/height to tile size
            {
                mMouseSelectionRect->setBounds(mMouseX, mMouseY, atoi(mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[3].c_str()), atoi(mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[4].c_str()));
            }
            mLevelMap = mLevelInfo->toMap();
            mMouseX2 = 0;
            mMouseY2 = 0;
        }
    }
    mMouseButton = 0;
}

/**
 * Invoked when a mouse button has been pressed on a component<BR>
 * Button1 - Adds / Selects an object or sprite and set position<BR>
 * Button2 - Changes the editor state 0-Select 1-Object 2-Sprite<BR>
 * Button3 - Removes an object or sprite</p>
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mousePressed(SDL_Event e) {
    mMouseX = e.button.x + mCamera->x;
    mMouseY = e.button.y + mCamera->y;
    mMouseX = mMouseX / 16;
    mMouseY = mMouseY / 16;

	if(e.button.button == SDL_BUTTON_LEFT)
    	mMouseButton = 1;
	else if(e.button.button == SDL_BUTTON_MIDDLE)
  		mMouseButton = 2;
	else if(e.button.button == SDL_BUTTON_RIGHT)
  		mMouseButton = 3;

    if (mEditorState == 0 && e.button.button == SDL_BUTTON_LEFT) {
        if (mClick == 0) //object is not selected
        {
            mTile = mLevelMap->getTile(mMouseX, mMouseY); //get tile information about map coordinate

            if (mTile->spriteTile == 0) //selected object
            {
                mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
                if (mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2] != "Null") {
                    mTileInfo = mLevelInfo->getTileDescription(mTile->index);
                    if ((mTile->tile1 >= 1 && mTile->tile1 <= 3) && mTile->tile2 == 0) //castle / extension / flag pole base
                    {
                        mSelectionDescription = "Flag Pole";
                        mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
                        //level.removeCastle();
                        mLevelMap = mLevelInfo->toMap();
                        mMouseTileRect->setSize(15 * 16, 10 * 16);
                        // box.setBounds(header.Width * 16, header.Height * 16, 0, 0);
                    } else {
                        mMouseTileRect->setSize(mTileInfo.Width * 16, mTileInfo.Height * 16);
                        mMouseSelectionRect->setBounds(mTileInfo.X, mTileInfo.Y, mTileInfo.Width, mTileInfo.Height);
                    }
                    mClick = 1;
                } else {
                    mMouseTileRect->setSize(16, 16);
                    mClick = 0;
                    mMouseSelectionRect->setBounds(mLevelHeader->Width * 16, mLevelHeader->Height * 16, 0, 0);
                    mSelectionDescription = "Select Object";
                }
            } else {
                mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->spriteTile)[1];

                if (mLevelInfo->getEntityStringDescription(mTile->spriteTile)[1] !=  "Null") {
                    mEntityInfo = mLevelInfo->getEntityDescription(mTile->spriteIndex);

                    if (mTile->spriteTile >= 7 && mTile->spriteTile <= 9) //castle flag / flag / flag pole
                    {
                        mSelectionDescription = "Flag Pole";
                        //level.removeCastle();
                        mLevelMap = mLevelInfo->toMap();
                        mMouseTileRect->setSize(15 * 16, 10 * 16);
                        mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
                        //box.setBounds(header.Width * 16, header.Height * 16, 0, 0);
                    } else {
                        mMouseTileRect->setSize(mEntityInfo->TileWidth * 16, mEntityInfo->TileHeight * 16);
                        mMouseSelectionRect->setBounds(mEntityInfo->X, mEntityInfo->Y, mEntityInfo->TileWidth, mEntityInfo->TileHeight);
                    }
                    mClick = 1;
                } else {
                    mMouseTileRect->setSize(16, 16);
                    mClick = 0;
                    mMouseSelectionRect->setBounds(mLevelHeader->Width * 16, mLevelHeader->Height * 16, 0, 0);
                    mSelectionDescription = "Select Object";
                }
            }

            //change start state on mouse press
            if ((mMouseX >= mLevelHeader->StartX && mMouseX <= mLevelHeader->StartX + 1)
                    && (mMouseY >= mLevelHeader->StartY && mMouseY <= mLevelHeader->StartY + 1)) {
                if (mLevelHeader->StartState < 5) {
                    mLevelHeader->StartState++;
                } else {
                    mLevelHeader->StartState = 0;
                }
            }

            //change end state on mouse press
            if ((mMouseX >= mLevelHeader->EndX && mMouseX <= mLevelHeader->EndX + 1)
                    && (mMouseY >= mLevelHeader->EndY && mMouseY <= mLevelHeader->EndY + 1)) {
                if (mLevelHeader->EndState < 5) {
                    mLevelHeader->EndState++;
                } else {
                    mLevelHeader->EndState = 0;
                }
            }

            //change bonus state on mouse press
            if ((mMouseX >= mLevelHeader->BonusX && mMouseX <= mLevelHeader->BonusX + 1)
                    && (mMouseY >= mLevelHeader->BonusY && mMouseY <= mLevelHeader->BonusY + 1)) {
                if (mLevelHeader->BonusState < 5) {
                    mLevelHeader->BonusState++;
                } else {
                    mLevelHeader->BonusState = 0;
                }
            }
        } else //object has been selected and moved position
        {
            if (mSelectionDescription == "Flag Pole") //add additional end of level objects
            {
                mLevelInfo->removeCastle();
                mLevelInfo->addEntity(8, mMouseX - 1, mMouseY); //flag
                mLevelInfo->addEntity(9, mMouseX, mMouseY); //flag pole
                mLevelInfo->addTile(3, 0, mMouseX, mMouseY + 9, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //flag pole base
                mLevelInfo->addEntity(7, mMouseX + 10, mMouseY + 1); //castle flag
                mLevelInfo->addTile(1, 0, mMouseX + 8, mMouseY + 4, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle
                mLevelInfo->addTile(2, 0, mMouseX + 11, mMouseY + 7, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle extension
                mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
                mSelectionDescription = "";
            } else {
                if (mTile->spriteTile == 0) //object
                {                    
					mTileInfo.X = mMouseX;
                    mTileInfo.Y = mMouseY;
					mLevelInfo->Level::setTileDescription(mTile->index, mTileInfo);
                    mMouseSelectionRect->setBounds(mTileInfo.X, mTileInfo.Y, mTileInfo.Width, mTileInfo.Height);	
                } else //sprite
                {
                    mEntityInfo->X = mMouseX;
                    mEntityInfo->Y = mMouseY;
                    mLevelInfo->setEntityDescription(mTile->spriteIndex, *mEntityInfo);
                    mMouseSelectionRect->setBounds(mEntityInfo->X, mEntityInfo->Y, mEntityInfo->TileHeight, mEntityInfo->TileHeight);
                }
            }
            mMouseTileRect->setSize(16, 16);

            mClick = 0;
            mLevelMap = mLevelInfo->toMap();
        }
    } else if (mEditorState == 4 && e.button.button == SDL_BUTTON_LEFT) //add sprite
    {
        if (mSelectionDescription == "Flag Pole") //add additional end of level objects
        {
            mLevelInfo->removeCastle();
            mLevelInfo->addEntity(8, mMouseX - 1, mMouseY); //flag
            mLevelInfo->addEntity(9, mMouseX, mMouseY); //flag pole
            mLevelInfo->addTile(3, 0, mMouseX, mMouseY + 9, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //flag pole base
            mLevelInfo->addEntity(7, mMouseX + 10, mMouseY + 1); //castle flag
            mLevelInfo->addTile(1, 0, mMouseX + 8, mMouseY + 4, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle
            mLevelInfo->addTile(2, 0, mMouseX + 11, mMouseY + 7, mMouseX2 - mMouseX, mMouseY2 - mMouseY); //castle extension
            mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
        } else {
            mLevelInfo->addEntity(mTile->tile1, mMouseX, mMouseY);
            mMouseSelectionRect->setBounds(mMouseX, mMouseY, atoi(mLevelInfo->getEntityStringDescription(mTile->tile1)[2].c_str()), atoi(mLevelInfo->getEntityStringDescription(mTile->tile1)[3].c_str()));
        }
        mLevelMap = mLevelInfo->toMap();
    }

    if (e.button.button == SDL_BUTTON_MIDDLE) //change editor state
    {
        changeEditorState();
    }

    if (e.button.button == SDL_BUTTON_RIGHT) //remove object or sprite
    {
        Tile* del = mLevelMap->getTile(mMouseX, mMouseY);
        if (del->spriteTile == 0) //object
        {
            if (mLevelInfo->getTileStringDescription(del->tile1, del->tile2)[2] != "Null") {
                if ((del->tile1 >= 1 && del->tile1 <= 3) && del->tile2 == 0) //castle / extension / flag pole base
                {
                    mLevelInfo->removeCastle();
                } else {
                    mLevelInfo->removeTile(del->index);
                }
            }
        } else //sprite
        {
            if (mLevelInfo->getEntityStringDescription(del->spriteTile)[1] != "Null") {
                if (del->spriteTile >= 7 && del->spriteTile <= 9) //castle flag / flag / flag pole
                {
                    mLevelInfo->removeCastle();
                } else {
                    mLevelInfo->removeEntity(del->spriteIndex);
                }
            }
        }
        mMouseTileRect->setSize(16, 16);
        mClick = 0;
        mLevelMap = mLevelInfo->toMap();
        mMouseSelectionRect->setBounds(mLevelHeader->Width, mLevelHeader->Height, 0, 0);

    }
	mouseWheelMoved(e);
}

/**
 * Invoked when the mouse wheel is rotated<BR>
 * Scrolls through object or sprite selection
 * @param e Event which indicates that the mouse wheel was rotated in a component
 */
void EditorPanel::mouseWheelMoved(SDL_Event e) {

	//http://www.gamedev.net/topic/440033-how-to-get-mousewheel-event-in-sdl/
    if (mEditorState > 0 && mEditorState < 4) {
        if (e.button.button == SDL_BUTTON_WHEELUP) //scroll up
        {
            nextObject();
        } else if (e.button.button == SDL_BUTTON_WHEELDOWN) //scroll down
        {
            previousObject();
        }
    } else if (mEditorState == 4) {
        if (e.button.button == SDL_BUTTON_WHEELUP)  //scroll up
        {
            nextSprite();
        } else if (e.button.button == SDL_BUTTON_WHEELDOWN) //scroll down
        {
            previousSprite();
        }
    }
}

/**
 * Changes the editor state 0-Tile Selection 1-Terrain, 2-Block, 3-Scenery 4-Entities
 */
void EditorPanel::changeEditorState() {
    mTile->tile1 = 1;
    if (mEditorState < 4) {
        mEditorState++;
    } else {
        mEditorState = 0;
    }
    switch (mEditorState) {
        case 1: mTile->tile2 = 16; break;
        case 2: mTile->tile2 = 0; mTile->tile1 = 20; break;
        case 3: mTile->tile2 = 32; break;
    }
    if (mEditorState == 4) {
        mTile->tile1 = 30;
        mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->tile1)[1];
    } else {
        if (mEditorState != 0) {
            mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
        } else {
            mSelectionDescription = "Select Object";
        }
    }
}

/** 
 * Gets the string description of the players animation state 
 * @param state The index of the players animation
 * @return The string description of the players animation state
 */
string EditorPanel::getStateDesc(int state) {
    switch (state) {
        case 0:
            return "Stand Left";
        case 1:
            return "Stand Right";
        case 2:
            return "  Up Pipe ";
        case 3:
            return " Down Pipe ";
        case 4:
            return "Left Pipe";
        case 5:
            return " Right Pipe";
        default:
            return "          ";
    }
}

/**
 * Moves to the next object in the tileset
 */
void EditorPanel::nextObject() {
    mTile->tile1++;

    mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
    while (mSelectionDescription == "Null") {
        if (mTile->tile1 < 100) {
            mTile->tile1++;
        } else {
            if (mTile->tile2 == 0) {
                mTile->tile1 = 10;
            } else {
                mTile->tile1 = 0;
            }
        }
        mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
    }
}

/**
 * Return to the previous object in the tileset
 */
void EditorPanel::previousObject() {
    mTile->tile1--;
    mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
    int minTile = 0;
    if (mTile->tile2 == 0) {
        minTile = 10;
    }
    while (mSelectionDescription == "Null") {
        if (mTile->tile1 > minTile) {
            mTile->tile1--;
        } else {
            mTile->tile1 = 100;
        }
        mSelectionDescription = mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[2];
    }
}

/**
 * Moves forward to the next sprite
 */
void EditorPanel::nextSprite() {
    mTile->tile1++;
    mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->tile1)[1];
    while (mSelectionDescription == "Null") {
        if (mTile->tile1 < mEntities->getSize()) {
            mTile->tile1++;
        } else {
            mTile->tile1 = 9;
        }
        mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->tile1)[1];
    }
}

/**
 * Return to the previous sprite
 */
void EditorPanel::previousSprite() {

    mTile->tile1--;
    if (mLevelInfo->getEntityStringDescription(mTile->tile1)[1] == "End Flag") {
        mTile->tile1 = mEntities->getSize();
    }
    mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->tile1)[1];
    while (mSelectionDescription == "Null") {
        if (mTile->tile1 > 0) {
            mTile->tile1--;
        } else {
            mTile->tile1 = mEntities->getSize();
        }
        mSelectionDescription = mLevelInfo->getEntityStringDescription(mTile->tile1)[1];
    }
}

/**
 * Draws the level editor graphics object/entity collection and background
 * @param g The graphics context
 */
void EditorPanel::paintComponent(SDL_Surface* g) {


	if(mControlTimer < 3)
		mControlTimer++;
	else
	{
		if (mControls->mLeftPressed) {
	        mCamera->x -= 16;
	    } else if (mControls->mRightPressed) {
	        mCamera->x += 16;
	    } else if (mControls->mUpPressed) {
	        mCamera->y -= 16;
	    } else if (mControls->mDownPressed) {
	        mCamera->y += 16;
	    }
		mControlTimer = 0;
	}

    mBackground->draw(g, mCamera);
    mTileset->drawEditorMap(g, mLevelMap, mCamera);
    mEntities->draw(g, mLevelMap, mCamera);

    drawGrid(g);
    if (mEditorState == 0) {
        if (Settings::State == "level editor") {
			//rectangleRGBA(g, mMouseTileRect->x, mMouseTileRect->y, mMouseTileRect->w, mMouseTileRect->h, 255, 0, 0, 120);
        }
    } else if (mEditorState > 0 && mEditorState < 4) {
        if (Map::isTiledTerrain(mTile->tile1, mTile->tile2)) {
			mTileset->drawImage(g, mTileset->getFrame(mTile->tile1, mTile->tile2, mTile->tile3), mMouseTileRect->x, mMouseTileRect->y);
        } else {
            if (Map::isCoinBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mTileset->getFrame(10, 0, 0), mMouseTileRect->x, mMouseTileRect->y - 16);
            } else if (Map::is10CoinBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mTileset->getFrameIndex(10, 0, 0, 3), mMouseTileRect->x, mMouseTileRect->y - 16);	
            } else if (Map::isEnlargeBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mEntities->getEntity(10, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
            } else if (Map::isFireBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mEntities->getEntity(11, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
            } else if (Map::isInvincibleBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mEntities->getEntity(12, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
            } else if (Map::isExtraLifeBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mEntities->getEntity(13, 'l'), mMouseTileRect->x,  mMouseTileRect->y - 16);
            } else if (Map::isVineBlock(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(g, mEntities->getEntity(14, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
            }
			mTileset->drawImage(g, mTileset->getFrame(mTile->tile1, mTile->tile2, mTile->tile3), mMouseTileRect->x, mMouseTileRect->y);
        }
    } else if (mEditorState == 4) {
		mTileset->drawImage(g, mEntities->getEntity(mTile->tile1, 'l'), mMouseTileRect->x, mMouseTileRect->y);

    }

    //drag catle and flag pole
    if (mSelectionDescription == "Flag Pole" && mEditorState == 4)
    {
        //flag
		mTileset->drawImage(g, mEntities->getEntity(8, 'l'), mMouseTileRect->x - (1 * 16), mMouseTileRect->y);
        //flag pole
		mTileset->drawImage(g, mEntities->getEntity(9, 'l'), mMouseTileRect->x, mMouseTileRect->y);
        //flag pole base
		mTileset->drawImage(g, mTileset->getFrame(3, 0, 0), mMouseTileRect->x, mMouseTileRect->y + (9 * 16));
        //castle flag
		mTileset->drawImage(g, mEntities->getEntity(7, 'l'), mMouseTileRect->x + (10 * 16), mMouseTileRect->y + (1 * 16));
        //castle
		mTileset->drawImage(g, mTileset->getFrame(1, 0, 0), mMouseTileRect->x + (8 * 16), mMouseTileRect->y + (4 * 16));

    }

    if (Settings::Animation) {
        if (mAnimationTimer < 10) {
            mAnimationTimer++;
        } else {
            if (mAnimationFrame < 5) {
                mAnimationFrame++;
            } else {
                mAnimationFrame = 0;
            }
            mAnimationTimer = 0;
        }
    }

    if (Settings::State == "level editor") {
		rectangleRGBA(g,  mMouseSelectionRect->x * 16 - mCamera->x, mMouseSelectionRect->y * 16 - mCamera->y, mMouseSelectionRect->x * 16 - mCamera->x + mMouseSelectionRect->w * 16, mMouseSelectionRect->y * 16 - mCamera->y + mMouseSelectionRect->h * 16, 255, 0, 0, 120);  	
		boxRGBA(g, mMouseCursorRect->x, mMouseCursorRect->y,  mMouseCursorRect->x + 5, mMouseCursorRect->y + 5, 255, 0, 0, 120);
    }

    //g.setColor(new Color(165, 50, 33));

    //start posiiton
	mTileset->drawImage(g, mPlayer->getFrame(mAnimationFrame, 'r'), mLevelHeader->StartX * 16 - mCamera->x + 2, mLevelHeader->StartY * 16 - mCamera->y);
	mFontConsoleFont->drawString(g, "Start", mLevelHeader->StartX * 16 - mCamera->x + 5, mLevelHeader->StartY * 16 - mCamera->y - 17);
    int startPosX = (mLevelHeader->StartX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->StartState)) / 2;
    mFontConsoleFont->drawString(g, getStateDesc(mLevelHeader->StartState), startPosX + 17, mLevelHeader->StartY * 16 - mCamera->y - 5);

    //end position
    if (mLevelHeader->EndX != 0 && mLevelHeader->EndY != 0) {
		mTileset->drawImage(g, mPlayer->getFrame(mAnimationFrame, 'r'), mLevelHeader->EndX * 16 - mCamera->x + 2, mLevelHeader->EndY * 16 - mCamera->y);
        mFontConsoleFont->drawString(g, "End", mLevelHeader->EndX * 16 - mCamera->x + 7, mLevelHeader->EndY * 16 - mCamera->y - 17);
        int endPosX = (mLevelHeader->EndX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->EndState)) / 2;
        mFontConsoleFont->drawString(g, getStateDesc(mLevelHeader->EndState), endPosX + 14, mLevelHeader->EndY * 16 - mCamera->y - 5);
    }

    //bonus position
    if (mLevelHeader->BonusX != 0 && mLevelHeader->BonusY != 0) {
		mTileset->drawImage(g, mPlayer->getFrame(mAnimationFrame, 'r'), mLevelHeader->BonusX * 16 - mCamera->x + 2, mLevelHeader->BonusY * 16 - mCamera->y);
        mFontConsoleFont->drawString(g, "Bonus", mLevelHeader->BonusX * 16 - mCamera->x + 2, mLevelHeader->BonusY * 16 - mCamera->y - 17);
        int bonusPosX = (mLevelHeader->BonusX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->BonusState)) / 2;
        mFontConsoleFont->drawString(g, getStateDesc(mLevelHeader->BonusState), bonusPosX + 15, mLevelHeader->BonusY * 16 - mCamera->y - 5);
    }

    if (Settings::State == "level editor") {
        if (mSelectionDescription == "null" || mSelectionDescription == "Select Object") {
			char str[100];
        	sprintf (str, "x-%d y-%d", (mMouseX + 1), (mMouseY + 1));	
            mFontConsoleFont->drawString(g, str, 5, 6);
            mFontConsoleFont->drawString(g, "Left Button   - Select Object", 61, 6);
            mFontConsoleFont->drawString(g, "Right Button  - Remove Object", 61, 17);
            mFontConsoleFont->drawString(g, "Middle Button", 61, 28);
            mFontConsoleFont->drawString(g, "- Select Tileset", 135, 28);
        } else {
			char str[100];
        	sprintf (str, "x-%d y-%d %s", (mMouseX + 1), (mMouseY + 1), mSelectionDescription.c_str());
            mFontConsoleFont->drawString(g, str, 5, 6);
        }
    }
}

/**
 * Draws a 16x16 line grid overlay
 * @param g The graphics context
 */
void EditorPanel::drawGrid(SDL_Surface* g) {

    for (int i = mCamera->x / 16; i < mCamera->x + 480 / 16; i++) //horizontal
    {
		 lineRGBA(g, (16 * i) - mCamera->x, 0, (16 * i) - mCamera->x, mLevelHeader->Height, 255, 255, 255, 50);
    }
    for (int i = mCamera->y / 16; i < mCamera->y + 272 / 16; i++) //vertical
    {
		lineRGBA(g, 0, (16 * i) - mCamera->y, mLevelHeader->Width, (16 * i) - mCamera->y, 255, 255, 255, 50);
    }
}

/**
 * Sets the controller flags for the player
 * @param e event which indicates that a keystroke occurred in a component
 * @param pressed True if a key is pressed
 */
void EditorPanel::setControl(SDL_Event e, bool pressed) {
    int keyCode = e.key.keysym.sym; //http://falconpl.org/project_docs/sdl/class_SDLK.html
	//cout << keyCode << endl;
        if (keyCode == 119 || keyCode == 32) { //w or space
            mControls->mJumpPressed = pressed;
        } else if (keyCode == 273) { //up
            mControls->mUpPressed = pressed;
        } else if (keyCode == 276) { //left
            mControls->mLeftPressed = pressed;
        } else if (keyCode == 274) { //down
            mControls->mDownPressed = pressed;
        } else if (keyCode == 275) { //right
            mControls->mRightPressed = pressed;
        } else if (keyCode == 304 || keyCode == 113) { //shift or q
            mControls->mRunPressed = pressed;
        }
}

EditorPanel::~EditorPanel()
{
    delete mPlayer;
    delete mEntities;
    delete mBackground;
    delete mTileset;
    delete mFontConsoleFont;
    delete mLevelInfo;
    delete mLevelHeader;
    delete mLevelMap;
}




