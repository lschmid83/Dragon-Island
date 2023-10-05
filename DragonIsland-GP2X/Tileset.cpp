#include "Tileset.h"

/**
 * Constructs the Tileset
 * @param tileset1 The folder number of the block tile set
 * @param tileset2 The folder number of the terrain tile set
 * @param tileset3 The folder number of the scenery tile set
 */    
Tileset::Tileset(int tileset1, int tileset2, int tileset3) {

    mTilesetIndex[0] = tileset1;
    mTilesetIndex[1] = tileset2;
    mTilesetIndex[2] = tileset3;
    initDefinitions();

	mFrame = 0;
	mFrameTimer = 0;

    //tileset 1 (block)
	char path[100];
    sprintf (path, "mnt/sd/Dragon Island/obj/block/%d/tileset.png", mTilesetIndex[0]);	
    mBlockTileset = new Image(path);
    mBlockTile = new SDL_Surface[66];
    for (int i = 1; i < 4; i++) { 
		int w = atoi(getTileDescription(i, 0)[3].c_str()) * 16;
        int h = atoi(getTileDescription(i, 0)[4].c_str()) * 16;
        int x = atoi(getTileDescription(i, 0)[6].c_str());
        int y = atoi(getTileDescription(i, 0)[7].c_str());
		mBlockTile[i] = *mBlockTileset->getSubimage(x, y, w, h);
    }
    
    //smashed block
    mSmashedBlock = new SDL_Surface[4];
    mSmashedBlock[0] = *mBlockTileset->getSubimage(0, 112, 16, 16); 
    mSmashedBlock[1] = *mBlockTileset->getSubimage(0, 128, 16, 16); 
    mSmashedBlock[2] = *mBlockTileset->getSubimage(0, 144, 16, 16); 
    mSmashedBlock[3] = *mBlockTileset->getSubimage(0, 160, 16, 16); 	
    
    //coin
    mCoin = new SDL_Surface[4];
    mCoin[0] = *mBlockTileset->getSubimage(16, 112, 16, 16); 
    mCoin[1] = *mBlockTileset->getSubimage(16, 128, 16, 16); 
    mCoin[2] = *mBlockTileset->getSubimage(16, 144, 16, 16); 
    mCoin[3] = *mBlockTileset->getSubimage(16, 160, 16, 16); 
    
    //brick
    mBrick = new SDL_Surface[4];
    mBrick[0] = *mBlockTileset->getSubimage(32, 112, 16, 16); 
    mBrick[1] = *mBlockTileset->getSubimage(32, 128, 16, 16); 
    mBrick[2] = *mBlockTileset->getSubimage(32, 144, 16, 16); 
    mBrick[3] = *mBlockTileset->getSubimage(32, 160, 16, 16); 
    
    //? block
    mQuestionBlock = new SDL_Surface[4];
    mQuestionBlock[0] = *mBlockTileset->getSubimage(48, 112, 16, 16); 
    mQuestionBlock[1] = *mBlockTileset->getSubimage(48, 128, 16, 16); 
    mQuestionBlock[2] = *mBlockTileset->getSubimage(48, 144, 16, 16); 
    mQuestionBlock[3] = *mBlockTileset->getSubimage(48, 160, 16, 16); 
    
    //inivisible block
    mBlockTile[40] = *mBlockTileset->getSubimage(32,96, 16, 16); 
    mBlockTile[41] = *mBlockTileset->getSubimage(32,96, 16, 16);
 
    //warp pipes
    for (int i = 50; i < 66; i++) {
        int w = atoi(getTileDescription(i, 0)[3].c_str()) * 16;
        int h = atoi(getTileDescription(i, 0)[4].c_str()) * 16;
        int x = atoi(getTileDescription(i, 0)[6].c_str());
        int y = atoi(getTileDescription(i, 0)[7].c_str());
        mBlockTile[i] = *mBlockTileset->getSubimage(x, y, w, h);
    }

    //tileset 2 (terrain)
    sprintf (path, "mnt/sd/Dragon Island/obj/terrain/%d/tileset.png", mTilesetIndex[1]);	
    mTerrainTileset = new Image(path);
    mTerrainTile = new SDL_Surface[14];
    for (int i = 1; i < 14; i++) {
        int w = atoi(getTileDescription(i, 16)[3].c_str()) * 16;
        int h = atoi(getTileDescription(i, 16)[4].c_str()) * 16;
        int x = atoi(getTileDescription(i, 16)[6].c_str());
        int y = atoi(getTileDescription(i, 16)[7].c_str());
        mTerrainTile[i] = *mTerrainTileset->getSubimage(x, y, w, h);
    }

    //tileset 3 (scenery)        
    sprintf (path, "mnt/sd/Dragon Island/obj/scenery/%d/tileset.png", mTilesetIndex[2]);
    mSceneryTileset = new Image(path);	
    mSceneryTile = new SDL_Surface[6];
    for (int i = 1; i < 6; i++) {
        int w = atoi(getTileDescription(i, 32)[3].c_str()) * 16;
        int h = atoi(getTileDescription(i, 32)[4].c_str()) * 16;
        int x = atoi(getTileDescription(i, 32)[6].c_str());
        int y = atoi(getTileDescription(i, 32)[7].c_str());
        mSceneryTile[i] = *mSceneryTileset->getSubimage(x, y, w, h);
    }
}


/**
 * Returns a subimage defined by a specified rectangular region. 
 * The returned BufferedImage shares the same data array as the original image. 
 * @param x The X coordinate of the upper-left corner of the specified rectangular region
 * @param y The Y coordinate of the upper-left corner of the specified rectangular region
 * @param w The width of the specified rectangular region
 * @param h The height of the specified rectangular region 
 * @return A BufferedImage that is the subimage of this BufferedImage. 
 */
SDL_Surface* Tileset::getSubimage(SDL_Surface* img, int x, int y, int w, int h)
{
	//create clipped image and set transparent color (http://gpwiki.org/index.php/SDL:Tutorials:2D_Graphics)
	SDL_Surface *frame;
	frame = SDL_CreateRGBSurface(SDL_HWSURFACE, w, h, img->format->BitsPerPixel, 
		                         img->format->Rmask, img->format->Gmask, img->format->Bmask, 0 );

	SDL_Surface *optimizedImage = SDL_DisplayFormat(frame); 	
	//SDL_SetColorKey(frame, SDL_SRCCOLORKEY, SDL_MapRGB(img->format, 168, 230, 29) );				
	SDL_Rect srcR;
   	srcR.x = x;
	srcR.y = y;
	srcR.w = w;
	srcR.h = h;
	SDL_BlitSurface(img, &srcR, optimizedImage, NULL);
	return optimizedImage;
}

/**
 * Returns a tile from the specified tileset using the animation frame set in drawMap()
 * @param tileIndex The index of the tile
 * @param tilesetIndex The index of the tileset 0-block, 16-terrain, 32-scenery
 * @param repeatIndex The tile index for repeating terrain
 * @return The tile image data
 */
 SDL_Surface* Tileset::getFrame(int tileIndex, int tilesetIndex, int repeatIndex) {

    //brick, block or coin
    if (Map::isAnimated(tileIndex, tilesetIndex)) {
        if (tileIndex == 10) //coin
        {
            return &mCoin[mFrame];
        } else if (tileIndex >= 20 && tileIndex <= 27) //brick
        {
            return &mBrick[mFrame];
        } else if (tileIndex >= 30 && tileIndex <= 37) //? block
        {
            return &mQuestionBlock[mFrame];
        }
    }
    if (tilesetIndex == 0) {
        return &mBlockTile[tileIndex];
    } else if (tilesetIndex == 16) {
        return &mTerrainTile[tileIndex]; //.getSubimage(0, tile3 * 16, 0, 0);
    } else if (tilesetIndex == 32) {
        return &mSceneryTile[tileIndex];
    } else {
        return NULL;
    }
}

/**
 * Returns a tile from the specified tileset
 * @param tileIndex The index of the tile
 * @param tilesetIndex The index of the tileset 0-block, 16-terrain, 32-scenery
 * @param repeatIndex The tile index for repeating terrain
 * @param frameNumber The frame number
 * @return The tile image data
 */
 SDL_Surface* Tileset::getFrameIndex(int tileIndex, int tilesetIndex, int repeatIndex, int frameNumber) {

    //brick, block or coin
    if (Map::isAnimated(tileIndex, tilesetIndex)) {
        if (tileIndex == 10) //coin
        {
            return &mCoin[mFrame];
        } else if (tileIndex >= 20 && tileIndex <= 27) //brick
        {
            return &mBrick[mFrame];
        } else if (tileIndex >= 30 && tileIndex <= 37) //? block
        {
            return &mQuestionBlock[mFrame];
        }
    }
    if (tilesetIndex == 0) {
        return &mBlockTile[tileIndex];
    } else if (tilesetIndex == 16) {
        return &mTerrainTile[tileIndex]; //.getSubimage(0, tile3*16, 16, 16);
    } else if (tilesetIndex == 32) {
        return &mSceneryTile[tileIndex];
    } else {
        return NULL;
    }
}

/**
 * Adds a piece of a block explosion
 * @param x The x coordinate
 * @param y The y coordinate
 * @param direction The direction of the explosion
 */
void Tileset::setBlockExplode(int x, int y, char direction) {
    BlockExplosion* be = new BlockExplosion();
    be->x = x;
    be->y = y;
    be->direction = direction;
	mExplosion.push_back(*be);
}

/**
 * Draws the image
 * @param g The drawing surface
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Tileset::drawImage(SDL_Surface *g, SDL_Surface *img, int x, int y)
{
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = img->w;  src.h = img->h;
  	dst.x = x;  dst.y = y;  dst.w =  img->w;  dst.h = img->h;
  	SDL_BlitSurface(img, &src, g, &dst);
}

/**
 * Draws a tile from the level map on the graphics surface
 * @param gl The graphics context
 * @param x The x coordinate
 * @param y The y coordinate
 * @param cam The camera coordinates
 * @param tile The tile information
 */
void Tileset::draw(SDL_Surface* g, int x, int y, Point* cam, Tile* tile) {

    if (tile->tile1 != 0 && tile->draw == 1 && x > -1) //tile is an object
    {
        x = (x * 16) - cam->x;
        y = (y * 16 - tile->offset) - cam->y;

        if (tile->tile3 > 0) //tiled terrain
        {
            drawImage(g, getFrame(tile->tile1, tile->tile2, tile->tile3 - 1), x, y);
        } else {
            if (tile->state == 1) {
                if (Map::isInvisible(tile->tile1, tile->tile2)) {
                    tile->collision = 1;
                }

                if (tile->offset < 5) {
                    tile->offset++;
                } else {
                    tile->offset = 0;
                    tile->state = 0;
                    if (tile->count > 0 && tile->count != 99) {
                        tile->count--;
                    }

                    if (tile->count == 0) {
                        tile->state = 2;
                    }
                }

                if (Map::isCoinBlock(tile->tile1, tile->tile2) && tile->count > 0) {
                    drawImage(g, getFrame(10, 0, 0), x, y - 16);
                } else if (Map::is10CoinBlock(tile->tile1, tile->tile2) && tile->count > 0) {			
                    drawImage(g, &mCoin[mFrame], x, y - 16);
                }
            } else if (tile->state == 2) {
                tile->tile1 = 40;
            }

            if (!Map::isInvisible(tile->tile1, tile->tile2)) {
                drawImage(g, getFrame(tile->tile1, tile->tile2, 0), x, y);
            } else {
                if (tile->state == 2) {
                    drawImage(g, getFrame(tile->tile1, tile->tile2, 0), x, y);
                }
            }
        }
    }
}

/**
 * Draws pieces of exploding block on the screen
 * @param g The Graphics context
 * @param map The level map
 * @param cam The camera coordinates
 */    
void Tileset::drawExplosions(SDL_Surface* g, Map* map, Point* cam) {

    for (int i = 0; i < mExplosion.size(); i++) {
        mExplosion[i].tmr++;
        if (mExplosion[i].y < map->getHeight()) {
            if (mExplosion[i].direction == 'r') {
                mExplosion[i].x++;
                if (mExplosion[i].tmr < 15) {
                    mExplosion[i].y--;
                } else {
                    if (mExplosion[i].y < map->getHeight()) {
                        mExplosion[i].y += 3;
                    } else {
						mExplosion.erase(mExplosion.begin() + i);
                    }
                }
            } else {
                mExplosion[i].x--;
                if (mExplosion[i].tmr < 15) {
                    mExplosion[i].y--;
                } else {
                    if (mExplosion[i].y < map->getHeight()) {
                        mExplosion[i].y += 3;
                    } else {
                      	mExplosion.erase(mExplosion.begin() + i);
                    }
                }
            }
            //draw block explosion
			drawImage(g, &mSmashedBlock[mFrame], mExplosion[i].x - cam->x, mExplosion[i].y - cam->y);
        }
    }

}

/**
 * Draws the tiles defined in the level map within the camera area
 * @param gl The graphics context
 * @param map The level map
 * @param cam The camera coordinates
 */   
void Tileset::drawMap(SDL_Surface* g, Map* map, Point* cam) {

	//set frame for animated tiles
    if (Settings::Animation) {
        mFrameTimer++;
        if (mFrameTimer == 20) {
            if (mFrame < 3) {
                mFrame++;
            } else {
                mFrame = 0;
            }
            mFrameTimer = 0;
        }
    }

    //draw objects in screen area
    for (int x = (cam->x - 100) / 16; x < (cam->x + 512) / 16; x++) {
        for (int y = (cam->y - 100) / 16; y < (cam->y + 300) / 16; y++) {
            mTileInfo = map->getTile(x, y);
            draw(g, x, y, cam, mTileInfo);
        }
    }

}

/**
 * Draws the tiles defined in the level editor map within the camera area
 * @param gl The graphics context
 * @param map The level map
 * @param cam The camera coordinates
 */      
void Tileset::drawEditorMap(SDL_Surface* g, Map* map, Point* cam) {
    //set animation frame for objects in the scene
    if (Settings::Animation) {
        mFrameTimer++;
        if (mFrameTimer == 10) {
            if (mFrame < 3) {
                mFrame++;
            } else {
                mFrame = 0;
            }
            mFrameTimer = 0;
        }
    }

    //draw objects in screen area
    for (int x = (cam->x - 100) / 16; x < (cam->x + 512) / 16; x++) {
        for (int y = (cam->y - 100) / 16; y < (cam->y + 300) / 16; y++) {
            mTileInfo = map->getTile(x, y);
            draw(g, x, y, cam, mTileInfo);
            if (Map::isCoinBlock(mTileInfo->tile1, mTileInfo->tile2)) {
                drawImage(g, getFrame(10, 0, 0), x * 16 - cam->x, (y - 1) * 16 - cam->y);
            } else if (Map::is10CoinBlock(mTileInfo->tile1, mTileInfo->tile2)) {
                drawImage(g, getFrameIndex(10, 0, 0, 3), x * 16 - cam->x, (y - 1) * 16 - cam->y);
            }
        }
    }

}

/**
 * Initialise the tileset information
 */
void Tileset::initDefinitions() {

	//tileset 1
	char tmp[100];
	sprintf(tmp, "mnt/sd/Dragon Island/obj/block/%d/tileset.ini", mTilesetIndex[0]);	
	ifstream file(tmp); 
	
	const int MAXLINE=512;
	char line[MAXLINE];
    
	while (file.good())
	{
		file.getline(line, MAXLINE);
		mTileDefintions.push_back(line);
	}

	file.close();
	file.clear();

    //tileset 2
	sprintf(tmp, "mnt/sd/Dragon Island/obj/terrain/%d/tileset.ini", mTilesetIndex[1]);	
	file.open(tmp); 
    
	while (file.good())
	{
		file.getline(line, MAXLINE);
		mTileDefintions.push_back(line);
	}

	file.close();
	file.clear();

    //tileset 3
	sprintf(tmp, "mnt/sd/Dragon Island/obj/scenery/%d/tileset.ini", mTilesetIndex[2]);	
	file.open(tmp); 

	while (file.good())
	{
		file.getline(line, MAXLINE);
		mTileDefintions.push_back(line);
	}

	file.close();
	file.clear();
}

/**
 * Returns the description of a tile
 * @param index The index of the tile
 * @return The tile description
 */   
vector<string> Tileset::getTileDescription(int index) {
	vector<string> v;   
	split(mTileDefintions[index], ',', v);    
	return v;
}


/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void Tileset::split(const string& s, char c, vector<string>& v) 
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
 * Returns the size of the tile descriptions array
 * @return The size of tile descriptions array
 */   
int Tileset::getSizeOfTileDescriptions() {
    return mTileDefintions.size();
}

/**
 * Returns a string array containing a detailed description of the tile
 * @param tileIndex The tile index
 * @param tilesetIndex The tileset index 0-block, 16-object 32-scenery
 * @return The ile description array [0]-index [1]-tileset [3]-name [4]-width [5]-height [6]-collision flag [7]-tileX [8]-tileY
 */   
vector<string> Tileset::getTileDescription(int tileIndex, int tilesetIndex) {
    
    for (int i = 0; i < mTileDefintions.size(); i++) //array of definitions
    {
        //split str into tile number, description and tile width/height
		vector<string> desc;		
		split(mTileDefintions[i], ',', desc);  
        if (atoi(desc[0].c_str()) == tileIndex && atoi(desc[1].c_str())  == tilesetIndex) //tile match
        {
            return desc;
        }
    }
	vector<string> desc;
    desc[2] = "Null";
    return desc;
}

/**
 * Deallocates memory by destroying the Tileset
 */
Tileset::~Tileset()
{
	//SDL_FreeSurface(mBlockTile);
    delete mBlockTileset;

	//SDL_FreeSurface(mTerrainTile);
	delete mTerrainTileset;

	//SDL_FreeSurface(mSceneryTile);
    delete mSceneryTileset;
}
