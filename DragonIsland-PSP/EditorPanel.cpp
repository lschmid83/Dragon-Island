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
	mMouseDragged = false;
}

/**
 * Loads a level file for editing
 * @param path The level file path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
 */
void EditorPanel::loadLevel(string path) {

	char str[100];
    sprintf (str, "res/chr/%d/0.png", Settings::MainCharacter);
    mPlayer = new SpriteSheet(str, 32, 32, 29);
    mLevelInfo = new Level();
    sprintf (str, "%s", path.c_str());	
	mLevelInfo->loadLevel(str);
    mClick = 0;
    mEditorState = 0;
    mMouseSelectionRect = new Rectangle();
    mTile = new Tile();
    mLevelHeader = mLevelInfo->getHeader();
    mLevelMap = new Map(mLevelInfo->getHeader());	
    mLevelMap = mLevelInfo->toMap(mLevelMap);
    mMouseTileRect = new Rectangle(0, 0, 16, 16);
    mMouseTileRect->setLocation(mLevelHeader->Width, mLevelHeader->Height);
    mCamera = new Point(0, (mLevelHeader->StartY * 16) - 224);
    mEntities = new EntityCollection();
    mTileset = NULL;
    mTileset = new Tileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32);
    setMusic(mLevelHeader->Music);
    mBackground = NULL;
    setBackground(mLevelHeader->Bg);
    mMouseCursorRect = new Rectangle(0, 0, 5, 5);
    mMouseTileRect->setLocation(-100, -100);
    mMouseCursorRect->setLocation(-100, -100);
    mFontConsoleFont = new GameFont("speech");
    mControls = new Control();
    mX = 480 / 2;
    mY = 272 / 2;
    mMouseButton = 0;
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
    if(mTileset)
		delete mTileset;
    mTileset = new Tileset(mLevelHeader->Tileset0, mLevelHeader->Tileset16, mLevelHeader->Tileset32);
    mLevelInfo->setHeader(mLevelHeader);
}

/**
 * Sets the background and update the level header
 * @param new_bgImg[] The background image resource (res/bgr) 0-near, 1-middle, 2-far
 */
void EditorPanel::setBackground(int bgImg[3]) {
	
	if(mBackground)
		delete mBackground;
	mBackground = new Background(bgImg, mLevelHeader->Width, mLevelHeader->Height, mLevelHeader->BgSpeed, mLevelInfo->getHeader()->BgAlign);
	if (bgImg[2] == 0) //far layer not set use header background color
	{
		mBackground->setColor(mLevelHeader->BgColor);
	} else
	{
		mLevelHeader->BgColor = mBackground->getColor();
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
    delete mLevelMap;
	mLevelMap = new Map(mLevelInfo->getHeader());	
	mLevelMap = mLevelInfo->toMap(mLevelMap);	
}

/**
 * Sets the music
 * @param file The music resource file (res/snd)
 */
void EditorPanel::setMusic(int file) {
    if (file != 0 && Settings::Music) {
		char path[100];
        sprintf (path, "res/snd/%d.mp3", mLevelHeader->Music);
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
void EditorPanel::keyPressed(OSL_CONTROLLER e) {
    if(Settings::State == "level editor")
    {
		mouseMoved(e);
		mousePressed(e);
		mouseWheelMoved(e);
		mouseDragged(e);
		if(!e.held.circle)
			setControl(e, true);
	}
	
	if(e.held.circle && e.pressed.up)
	{
		mLevelInfo->moveUp();
		mLevelMap->clear();
		mLevelMap = mLevelInfo->toMap(mLevelMap);	
	}

	if(e.held.circle && e.pressed.down)
	{
		mLevelInfo->moveDown();
		mLevelMap->clear();
		mLevelMap = mLevelInfo->toMap(mLevelMap);	
	}
	
	if(e.held.circle && e.pressed.left)
	{
		mLevelInfo->moveLeft();
		mLevelMap->clear();
		mLevelMap = mLevelInfo->toMap(mLevelMap);	
	}
	
	if(e.held.circle && e.pressed.right)
	{
		mLevelInfo->moveRight();
		mLevelMap->clear();
		mLevelMap = mLevelInfo->toMap(mLevelMap);	
	}						
}

/**
 * Invoked when a key has been released
 * @param e Event which indicates that a keystroke occurred in a component
 */
void EditorPanel::keyReleased(OSL_CONTROLLER e)
{
	setControl(e, false);
}

/**
 * Invoked when the mouse cursor has been moved onto a component<BR>
 * Sets the mouse and cursor rectangles x,y coordinates
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseMoved(OSL_CONTROLLER e) {

	try
	{
		for (int i=32;i<=120;i+=48)
		{
			if (osl_keys->analogX > i) mX+=2;
			if (osl_keys->analogX < -i)	mX-=2;
			if (osl_keys->analogY > i) mY+=2;
			if (osl_keys->analogY < -i) mY-=2;
		}	
		if(!e.held.cross || mEditorState == 0)
		{	
			if(mX > 480 - 5) mX = 480 - 5;
			if(mY > 272 - 5) mY = 272 - 5;
		}
		else
		{
			if(mX > 500) mX = 500;
			if(mY > 300) mY = 300;		
		}
		if(mX < 0) mX = 0;
		if(mY < 0) mY = 0;
		
		if(!e.held.cross || mEditorState == 0)
		{
			mMouseX = mX + mCamera->x;
			mMouseY = mY + mCamera->y;
			mMouseX = mMouseX / 16;
			mMouseY = mMouseY / 16;    
			mMouseTileRect->setLocation(mX / 16 * 16, mY / 16 * 16);
			mMouseCursorRect->setLocation(mX, mY);
		}
		else   
			mMouseCursorRect->setLocation(mX / 16, mY / 16);
	}
	catch (int e) {}
}

/**
 * Invoked when a mouse button is pressed on a component and then dragged<BR>
 * Sets the size of the selection rectangle
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseDragged(OSL_CONTROLLER e) {

	try
	{
		if (mEditorState > 0 && mEditorState <= 4 && mMouseButton == 1) {
			mMouseX2 = mX + mCamera->x + 1;
			mMouseY2 = mY + mCamera->y + 1;
			if (mMouseX2 <= mMouseX) {
				mMouseX2 = mMouseX + 1;
			}
			if (mMouseY2 <= mMouseY) {
				mMouseY2 = mMouseY + 1;
			}
			mMouseSelectionRect->setBounds(mMouseX, mMouseY, ((mX + mCamera->x) / 16) - mMouseX, ((mY + mCamera->y) / 16) - mMouseY);
			mMouseDragged = true;
		}
		else
		{
			if (mEditorState > 0)
			{
				if(mMouseDragged)
				{
					mouseReleased(e);	
				}
				if(mEditorState != 0)
					mMouseSelectionRect->setBounds(mLevelHeader->Width * 16, mLevelHeader->Height * 16, mLevelHeader->Width * 16, mLevelHeader->Height * 16);
			}
		}
	}
	catch (int e) {}

}


/**
 * Invoked when a mouse button has been released on a component<BR>
 * Adds an object to the map and sets the size of selection rectangle
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mouseReleased(OSL_CONTROLLER e) {
	
	try
	{
		mMouseDragged = false;
		if (mEditorState > 0 && mEditorState < 4) //add object
		{
			int tw = (mMouseSelectionRect->x * 16 - mCamera->x + mMouseSelectionRect->w * 16) - (mMouseSelectionRect->x * 16 - mCamera->x);
			int th = (mMouseSelectionRect->y * 16 - mCamera->y + mMouseSelectionRect->h * 16) - (mMouseSelectionRect->y * 16 - mCamera->y);
			mLevelInfo->addTile(mTile->tile1, mTile->tile2, mMouseSelectionRect->x, mMouseSelectionRect->y, tw / 16, th / 16);
			//set box width/height if mouse dragged and x2,y2 are set
			if (mMouseX2 - mMouseX > 0 && mMouseY2 - mMouseY > 0) {
				mMouseSelectionRect->setBounds(mMouseX, mMouseY, mMouseX2 - mMouseX, mMouseY2 - mMouseY);
			} else //set box width/height to tile size
			{
				mMouseSelectionRect->setBounds(mMouseX, mMouseY, atoi(mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[3].c_str()), atoi(mLevelInfo->getTileStringDescription(mTile->tile1, mTile->tile2)[4].c_str()));
			}
			mLevelMap->clear();
			mLevelMap = mLevelInfo->toMap(mLevelMap);
		}
	}
    catch (int e) {}
}

/**
 * Invoked when a mouse button has been pressed on a component<BR>
 * Button1 - Adds / Selects an object or sprite and set position<BR>
 * Button2 - Changes the editor state 0-Select 1-Object 2-Sprite<BR>
 * Button3 - Removes an object or sprite</p>
 * @param e Event which indicates that a mouse action occurred in a component
 */
void EditorPanel::mousePressed(OSL_CONTROLLER e) {

	try
	{		
		if(e.held.cross)
			mMouseButton = 1;
		else if(e.held.circle)
			mMouseButton = 2;
		else if(e.held.triangle)
			mMouseButton = 3;
		else
			mMouseButton = 0;
		

		if (mEditorState == 0 && e.pressed.cross) {
	
			if (mClick == 0) //object is not selected
			{
				try
				{				
					tile = mLevelMap->getTile(mMouseX, mMouseY); //get tile information 

					if (tile->spriteTile == 0) //selected object
					{
						mSelectionDescription = mLevelInfo->getTileStringDescription(tile->tile1, tile->tile2)[2];
						if (mLevelInfo->getTileStringDescription(tile->tile1, tile->tile2)[2] != "Null") {
							tileInfo = mLevelInfo->getTileDescription(tile->index);
							if ((tile->tile1 >= 1 && tile->tile1 <= 3) && tile->tile2 == 0) //castle / extension / flag pole base
							{
								mSelectionDescription = "Flag Pole";
								mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
								mLevelInfo->removeCastle();
								mLevelMap->clear();
								mLevelMap = mLevelInfo->toMap(mLevelMap);
								mMouseTileRect->setSize(15 * 16, 10 * 16);
							} else {
								mMouseTileRect->setSize(tileInfo->Width * 16, tileInfo->Height * 16);
								mMouseSelectionRect->setBounds(tileInfo->X, tileInfo->Y, tileInfo->Width, tileInfo->Height);
							}
							mClick = 1;
						} else {
							mMouseTileRect->setSize(16, 16);
							mClick = 0;
							mMouseSelectionRect->setBounds(mLevelHeader->Width * 16, mLevelHeader->Height * 16, 0, 0);
							mSelectionDescription = "Select Object";
						}
					} else {
						mSelectionDescription = mLevelInfo->getEntityStringDescription(tile->spriteTile)[1];

						if (mLevelInfo->getEntityStringDescription(tile->spriteTile)[1] !=  "Null") {
							entityInfo = mLevelInfo->getEntityDescription(tile->spriteIndex);

							if (tile->spriteTile >= 7 && tile->spriteTile <= 9) //castle flag / flag / flag pole
							{
								mSelectionDescription = "Flag Pole";
								mLevelInfo->removeCastle();
								mLevelMap->clear();
								mLevelMap = mLevelInfo->toMap(mLevelMap);
								mMouseTileRect->setSize(15 * 16, 10 * 16);
								mMouseSelectionRect->setBounds(mMouseX, mMouseY, 15, 10);
							} else {
								mMouseTileRect->setSize(entityInfo->TileWidth * 16, entityInfo->TileHeight * 16);
								mMouseSelectionRect->setBounds(entityInfo->X, entityInfo->Y, entityInfo->TileWidth, entityInfo->TileHeight);
							}
							mClick = 1;
						} else {
							mMouseTileRect->setSize(16, 16);
							mClick = 0;
							mMouseSelectionRect->setBounds(mLevelHeader->Width * 16, mLevelHeader->Height * 16, 0, 0);
							mSelectionDescription = "Select Object";
						}
					}
				}
				catch (int e) {}

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
			
				try
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
						if (tile->spriteTile == 0) //object
						{  
               
							tileInfo->X = mMouseX;
							tileInfo->Y = mMouseY;
							mLevelInfo->Level::setTileDescription(tile->index, tileInfo);
							mMouseSelectionRect->setBounds(tileInfo->X, tileInfo->Y, tileInfo->Width, tileInfo->Height);	
						} else //sprite
						{
							mEntityInfo->X = mMouseX;
							mEntityInfo->Y = mMouseY;
							mLevelInfo->setEntityDescription(tile->spriteIndex, entityInfo);
							mMouseSelectionRect->setBounds(entityInfo->X, entityInfo->Y, entityInfo->TileHeight, entityInfo->TileHeight);
						}
					}
					mMouseTileRect->setSize(16, 16);
					mClick = 0;
					mLevelMap->clear();
					mLevelMap = mLevelInfo->toMap(mLevelMap);
				}
				catch (int e) {}
			}
		} else if (mEditorState == 4 && e.pressed.cross) //add sprite
		{
			if (mSelectionDescription == "Flag Pole") //add additional end of level objects
			{
				//mLevelInfo->removeCastle();
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
			//mLevelMap->clear();
			mLevelMap = mLevelInfo->toMap(mLevelMap);
		}
	

		if (e.pressed.triangle) //change editor state
		{
			changeEditorState();
		}

		if (e.pressed.circle) //remove object or sprite
		{	
			try
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
				//mLevelMap = mLevelInfo->toMap(mLevelMap);
				mLevelMap->clear();
				mLevelMap = mLevelInfo->toMap(mLevelMap);
				mMouseSelectionRect->setBounds(mLevelHeader->Width, mLevelHeader->Height, 0, 0);
			}
			catch(int e) {}
		}
	}
	catch (int e) {}
}

/**
 * Invoked when the mouse wheel is rotated<BR>
 * Scrolls through object or sprite selection
 * @param e Event which indicates that the mouse wheel was rotated in a component
 */
void EditorPanel::mouseWheelMoved(OSL_CONTROLLER e) {

    if (mEditorState > 0 && mEditorState < 4) {
        if (e.pressed.R)
        {
            nextObject();
        } else if (e.pressed.L) 
        {
            previousObject();
        }
    } else if (mEditorState == 4) {
        if (e.pressed.R) 
        {
            nextSprite();
        } else if (e.pressed.L) 
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
void EditorPanel::paintComponent() {

	try
	{
		if(mControlTimer < 3)
			mControlTimer++;
		else
		{
			if (mControls->mLeftPressed) {
				mCamera->x -= 32;
			} else if (mControls->mRightPressed) {
				mCamera->x += 32;
			} else if (mControls->mUpPressed) {
				mCamera->y -= 32;
			} else if (mControls->mDownPressed) {
				mCamera->y += 32;
			}
			mControlTimer = 0;
		}

		mBackground->draw(mCamera);  
		mTileset->drawEditorMap(mLevelMap, mCamera);
		mEntities->draw(mLevelMap, mCamera);
		drawGrid(); 

		if (mEditorState == 0) {
			if (Settings::State == "level editor") {
				//oslDrawRect(mMouseTileRect->x, mMouseTileRect->y, mMouseTileRect->w, mMouseTileRect->h, RGBA(255, 0, 0, 120));
			}
		} else if (mEditorState > 0 && mEditorState < 4) {
			if (Map::isTiledTerrain(mTile->tile1, mTile->tile2)) {
				mTileset->drawImage(mTileset->getFrame(mTile->tile1, mTile->tile2, mTile->tile3), mMouseTileRect->x, mMouseTileRect->y);
			} else {
				if (Map::isCoinBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mTileset->getFrame(10, 0, 0), mMouseTileRect->x, mMouseTileRect->y - 16);
				} else if (Map::is10CoinBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mTileset->getFrameIndex(10, 0, 0, 3), mMouseTileRect->x, mMouseTileRect->y - 16);	
				} else if (Map::isEnlargeBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mEntities->getEntity(10, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
				} else if (Map::isFireBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mEntities->getEntity(11, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
				} else if (Map::isInvincibleBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mEntities->getEntity(12, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
				} else if (Map::isExtraLifeBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mEntities->getEntity(13, 'l'), mMouseTileRect->x,  mMouseTileRect->y - 16);
				} else if (Map::isVineBlock(mTile->tile1, mTile->tile2)) {
					mTileset->drawImage(mEntities->getEntity(14, 'l'), mMouseTileRect->x, mMouseTileRect->y - 16);
				}
				mTileset->drawImage(mTileset->getFrame(mTile->tile1, mTile->tile2, mTile->tile3), mMouseTileRect->x, mMouseTileRect->y);
			}
		} else if (mEditorState == 4) {
			
			OSL_IMAGE* img = mEntities->getEntity(mTile->tile1, 'l');		
			mTileset->drawImage(img, mMouseTileRect->x, mMouseTileRect->y);
			oslDeleteImage(img);

		}

		//drag catle and flag pole
		if (mSelectionDescription == "Flag Pole" && mEditorState == 4)
		{
			//flag
			mTileset->drawImage(mEntities->getEntity(8, 'l'), mMouseTileRect->x - (1 * 16), mMouseTileRect->y);
			//flag pole
			mTileset->drawImage(mEntities->getEntity(9, 'l'), mMouseTileRect->x, mMouseTileRect->y);
			//flag pole base
			mTileset->drawImage(mTileset->getFrame(3, 0, 0), mMouseTileRect->x, mMouseTileRect->y + (9 * 16));
			//castle flag
			mTileset->drawImage(mEntities->getEntity(7, 'l'), mMouseTileRect->x + (10 * 16), mMouseTileRect->y + (1 * 16));
			//castle
			mTileset->drawImage(mTileset->getFrame(1, 0, 0), mMouseTileRect->x + (8 * 16), mMouseTileRect->y + (4 * 16));
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

		//start posiiton
		OSL_IMAGE* img = mPlayer->getFrame(mAnimationFrame, 'r');
		oslDrawImageXY(img, mLevelHeader->StartX * 16 - mCamera->x + 2, mLevelHeader->StartY * 16 - mCamera->y);
		oslDeleteImage(img);
		mFontConsoleFont->drawString("Start", mLevelHeader->StartX * 16 - mCamera->x + 5, mLevelHeader->StartY * 16 - mCamera->y - 17);
		int startPosX = (mLevelHeader->StartX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->StartState)) / 2;
		mFontConsoleFont->drawString(getStateDesc(mLevelHeader->StartState), startPosX + 17, mLevelHeader->StartY * 16 - mCamera->y - 5);

		//end position
		if (mLevelHeader->EndX != 0 && mLevelHeader->EndY != 0) 
		{
			OSL_IMAGE* img = mPlayer->getFrame(mAnimationFrame, 'r');
			oslDrawImageXY(img, mLevelHeader->EndX * 16 - mCamera->x + 2, mLevelHeader->EndY * 16 - mCamera->y);
			oslDeleteImage(img);
			mFontConsoleFont->drawString("End", mLevelHeader->EndX * 16 - mCamera->x + 7, mLevelHeader->EndY * 16 - mCamera->y - 17);
			int endPosX = (mLevelHeader->EndX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->EndState)) / 2;
			mFontConsoleFont->drawString(getStateDesc(mLevelHeader->EndState), endPosX + 14, mLevelHeader->EndY * 16 - mCamera->y - 5);
		}

		//bonus position
		if (mLevelHeader->BonusX != 0 && mLevelHeader->BonusY != 0) 
		{
			OSL_IMAGE* img = mPlayer->getFrame(mAnimationFrame, 'r');
			oslDrawImageXY(img, mLevelHeader->BonusX * 16 - mCamera->x + 2, mLevelHeader->BonusY * 16 - mCamera->y);
			oslDeleteImage(img);
			mFontConsoleFont->drawString("Bonus", mLevelHeader->BonusX * 16 - mCamera->x + 2, mLevelHeader->BonusY * 16 - mCamera->y - 17);
			int bonusPosX = (mLevelHeader->BonusX * 16 - mCamera->x) - mFontConsoleFont->getStringWidth(getStateDesc(mLevelHeader->BonusState)) / 2;
			mFontConsoleFont->drawString(getStateDesc(mLevelHeader->BonusState), bonusPosX + 15, mLevelHeader->BonusY * 16 - mCamera->y - 5);
		}

		if (Settings::State == "level editor") {
			if (mSelectionDescription == "null" || mSelectionDescription == "Select Object") {
				char str[100];
				sprintf (str, "x-%d y-%d", (mMouseX + 1), (mMouseY + 1));	
				//mFontConsoleFont->drawString(str, 5, 6);
				mFontConsoleFont->drawString("Left Button   - Select Object", 61, 6);
				mFontConsoleFont->drawString("Right Button  - Remove Object", 61, 17);
				mFontConsoleFont->drawString("Middle Button", 61, 28);
				mFontConsoleFont->drawString("- Select Tileset", 135, 28);
			} else {
				char str[100];
				sprintf (str, "x-%d y-%d %s", (mMouseX + 1), (mMouseY + 1), mSelectionDescription.c_str());
				//mFontConsoleFont->drawString(str, 5, 6);
			}
		}
	 
		if (Settings::State == "level editor") 
		{
			oslDrawRect(mMouseSelectionRect->x * 16 - mCamera->x, mMouseSelectionRect->y * 16 - mCamera->y, mMouseSelectionRect->x * 16 - mCamera->x + mMouseSelectionRect->w * 16, mMouseSelectionRect->y * 16 - mCamera->y + mMouseSelectionRect->h * 16, RGBA(255, 0, 0, 120));  	

			if(mMouseButton == 1 && mEditorState != 0)
				oslDrawFillRect(mMouseCursorRect->x * 16, mMouseCursorRect->y * 16,  mMouseCursorRect->x * 16 + 5, mMouseCursorRect->y * 16 + 5, RGBA(255, 0, 0, 240));	
			else	
				oslDrawFillRect(mMouseCursorRect->x, mMouseCursorRect->y,  mMouseCursorRect->x + 5, mMouseCursorRect->y + 5, RGBA(255, 0, 0, 240));	
		}   
	}
	catch (int e) {}
}

/**
 * Draws a 16x16 line grid overlay
 * @param g The graphics context
 */
void EditorPanel::drawGrid() {

    for (int i = mCamera->x / 16; i < mCamera->x + 480 / 16; i++) //horizontal
    {
		oslDrawLine((16 * i) - mCamera->x, 0, (16 * i) - mCamera->x, mLevelHeader->Height, RGBA(255, 255, 255, 50));
	}
    for (int i = mCamera->y / 16; i < mCamera->y + 272 / 16; i++) //vertical
    {
		oslDrawLine(0, (16 * i) - mCamera->y, mLevelHeader->Width, (16 * i) - mCamera->y, RGBA(255, 255, 255, 50));
	}
}

/**
 * Sets the controller flags for the player
 * @param e event which indicates that a keystroke occurred in a component
 * @param pressed True if a key is pressed
 */
void EditorPanel::setControl(OSL_CONTROLLER e, bool pressed) {
	
	if(e.held.cross)
		mControls->mJumpPressed = true;
	else
	{
		mControls->mJumpReleased = true;
		mControls->mJumpPressed = false;
	}

	if(e.held.up && !e.held.cross && Settings::State != "level editor settings")
		mControls->mUpPressed = true;
	else
		mControls->mUpPressed = false;

	if(e.held.down && !e.held.cross && Settings::State != "level editor settings")
		mControls->mDownPressed = true;
	else
		mControls->mDownPressed = false;

	if(e.held.left && !e.held.cross && Settings::State != "level editor settings")
		mControls->mLeftPressed = true;
	else
		mControls->mLeftPressed = false;

	if(e.held.right && !e.held.cross && Settings::State != "level editor settings")
		mControls->mRightPressed = true;
	else
		mControls->mRightPressed = false;

	if(e.held.square)
		mControls->mRunPressed = true;
	else
	{
		mControls->mRunPressed = false;
		mControls->mRunReleased = true;
	}
}

EditorPanel::~EditorPanel()
{
	if(mPlayer)		
		delete mPlayer;
	if(mLevelInfo)
		delete mLevelInfo;	
	if(mMouseSelectionRect)
		delete mMouseSelectionRect;
	if(mLevelHeader)
		delete mLevelHeader;
	if(mLevelMap)
		delete mLevelMap;
	if(mMouseTileRect)
		delete mMouseTileRect;	
	if(mCamera)
		delete mCamera;
	if(mEntities)
		delete mEntities;
	if(mTileset)
		delete mTileset;
	if(mBackground)
		delete mBackground;	
	if(mMouseCursorRect)
		delete mMouseCursorRect;
	if(mFontConsoleFont)
		delete mFontConsoleFont;
	if(mControls)
		delete mControls;
}
