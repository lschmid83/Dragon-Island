#include "Sprite.h"

/**
 * Constructs the Sprite
 * @param index The index of the sprite
 * @param direction The direction 'l'-left, 'r'-right
 * @param x The start x coordinate
 * @param y The start y coordinate
 */   
Sprite::Sprite(int index, char direction, int x, int y)
{
    mIndex = index;
    mDirection = direction;
    mX = x;
	mY = y;
	mClimbing = false;
	mRotate = false;
	mFlicker = false;
	mFrameIndex = 0;
	mFrameTimer = 0;
	mMaxX = 10000;
	mMinX = 0;
	mSpeed = 0;
}

/**
 * Loads a sprite sheet and frame information
 * @param path The path of the sprite sheet and .ini file
 */  
bool Sprite::loadSpriteSheet(char* path) {
   
	char tmp[100];
	sprintf(tmp, "%s.ini", path);	
	ifstream file(tmp); 

	if (file.is_open())
	{

		const int MAXLINE=512;
		char line[MAXLINE];
	
		file.getline(line, MAXLINE);
	    mType = atoi(line);
	
		file.getline(line, MAXLINE);
	
		file.getline(line, MAXLINE);
	    mHitTimelimit = atoi(line);
	
		file.getline(line, MAXLINE);
	    mFireInvunerable = atoi(line);
	
		file.getline(line, MAXLINE);
	    mJumpSpeed  = atoi(line);
	
		file.getline(line, MAXLINE);
	    mFallSpeed  = atoi(line);
	
		file.getline(line, MAXLINE);
	    mFrameWidth = atoi(line);
	
		file.getline(line, MAXLINE);
	    mFrameHeight = atoi(line);
	
		file.getline(line, MAXLINE);
	    mFrameCount = atoi(line);
	
		file.getline(line, MAXLINE);
	    mAnimationSpeed = atoi(line);
	
		string ln;
		while (file.good())
		{
	        Frame* f = new Frame();
			getline(file, ln);
	
			vector<string> v;   
			split(ln, ',', v);
	
			f->State = v[0];
	        f->Start = atoi(v[1].c_str());
	        f->End = atoi(v[2].c_str());
	        f->bX = atoi(v[3].c_str());
	        f->bY = atoi(v[4].c_str());
	        f->bW = atoi(v[5].c_str());
	        f->bH = atoi(v[6].c_str());
				
			mFrameInfo.push_back(*f);	
	
		}
		file.close();
		sprintf(tmp, "%s.png", path);
		mSpriteSheet = new SpriteSheet(tmp, mFrameWidth, mFrameHeight, mFrameCount);
		return true;
	}
	else
		return false;

}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void Sprite::split(const string& s, char c, vector<string>& v) 
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
 * Returns the file number of the sprite    
 * @return The index of the sprite
 */   
int Sprite::getIndex() {
    return mIndex;
}

/**
 * Sets the file number of the sprite
 * @param index The index of the sprite
 */    
void Sprite::setIndex(int index) {
    mIndex = index;
}

/**
 * Returns the type of sprite
 * @return The type of sprite
 */   
int Sprite::getType() {
    return mType;
}

/**
 * Sets the amount of time the sprite pauses for after being hit
 * @param hitTimelimit The amount of time
 */   
void Sprite::setHitTimelimit(int hitTimelimit) {
    mHitTimelimit = hitTimelimit;
}

/**
 * Returns the amount of time the sprite pauses for after being hit
 * @return The amount of time
 */
int Sprite::getHitTimelimit() {
    return mHitTimelimit * 50;
}

/**
 * Sets the animation state of the player (stand, walk, run, jump, fall, crouch)
 * @param state The animation state
 */  
bool Sprite::setAnimationState(string state) {
    for (int i = 0; i < mFrameInfo.size(); i++) {
        if (mFrameInfo[i].State == state) {
            frame = mFrameInfo[i];
            frame.index = i;
            return true;
        }
    }

    frame.State = state;
    return false;
}

/**
 * Sets the animation index state of the player 
 * 0 = stand, 1 = walk, 2 = run, 3 = jump, 4 = fall 5 = crouch
 * @param index The animation index
 */
bool Sprite::setAnimationStateIndex(int index) {
    frame = mFrameInfo[index];
    return true;
}

/**
 * Returns the animation state of the sprite 
 * @return The animation state of the sprite
 */
string Sprite::getAnimationState() {
    return frame.State;
}

/**
 * Returns the index of the animation frame   
 * @return The index of the animation frame
 */
int Sprite::getAnimationStateIndex() {
    return frame.index;
}

/**
 * Draws the sprite at the x,y coordinates with the current animation and rotation
 * @param g The graphics context
 * @param cam The camera position
 */   
void Sprite::draw(SDL_Surface* g, Point* cam)
{
    if (!getClimb()) {
        advanceFrame();
    }

    if (!mRotate) {
        if (!mFlicker) {
            //g.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), mX - cam.x, mY + 1 - cam.y, null);
			mSpriteSheet->drawFrame(g, mX - cam->x, mY + 1 - cam->y,  mFrameIndex, mDirection);	
        } else {
            if (mFrameTimer % 5 == 0) {
                //g.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), mX - cam.x, mY + 1 - cam.y, null);
				mSpriteSheet->drawFrame(g, mX - cam->x, mY + 1 - cam->y,  mFrameIndex, mDirection);
            }
        }
    } else //rotate sprite
    {
        //AffineTransform at = new AffineTransform();
        //at.translate(mX - cam.x, mY - cam.y); //translate coordinates to angle
        //at.rotate(Math.toRadians(mAngle), mFrameWidth / 2, mFrameHeight / 2);

        if (mAngle < 360) {
            mAngle += mAnimationSpeed; //rotate sprite at the animation speed
        } else {
            mAngle = 0;
        }

        //Graphics2D g2d = (Graphics2D) g;
        if (getType() >= 20) 
        {
            //g2d.drawImage(mSpriteSheet.getFrame(mFrameIndex, mDirection), at, null);
        } else //draw powerup
        {
            //g2d.drawImage(mSpriteSheet.getFrame(mFrameIndex, 'l'), at, null);
        }

		mSpriteSheet->drawFrame(g, mX - cam->x, mY + 1 - cam->y,  mFrameIndex, mDirection);
    }

    if (Settings::DrawBounds) //draw debug sprite bounds rect in debug mode
    {
		//http://sdl.perl.org/SDL-GFX-Primitives.html
		//rectangleRGBA(g, getLeft() - cam->x, getUp() - cam->y, getLeft() - cam->x + getWidth(), getUp() - cam->y + getHeight(), 255, 0, 0, 255);
 		rectangleRGBA(g, getBounds()->x - cam->x, getBounds()->y - cam->y, getBounds()->x - cam->x + getBounds()->w, getBounds()->y - cam->y + getBounds()->h, 255, 0, 0, 255);
	 }
}

/**
 * Advances the frame of animation when the frame timer reaches the animation speed
 */
void Sprite::advanceFrame() {
    if (Settings::Animation) {
        if (mFrameTimer < mAnimationSpeed) {
            mFrameTimer++;
        } else {
            if (mFrameIndex < frame.Start) {
                mFrameIndex = frame.Start;
            } else if (mFrameIndex < frame.End) {
                mFrameIndex++;
            } else if (mFrameIndex >= frame.End) {
                mFrameIndex = frame.Start;
            }
            mFrameTimer = 0;
        }
    }
}

/**
 * Move the sprite in the set direction
 */
void Sprite::move() {


    if (getDirection() == 'r') {
        if (getRight() < mMaxX) {
            mX += mSpeed;
        }
    } else {
        if (getLeft() > mMinX) {
            mX -= mSpeed;
        }
    }

    if (mJumping) {
        if (getDown() > mMinY) {
            mY -= mJumpSpeed;
        } else {
            mJumping = false;
        }
    } else {
        if (!mClimbing) {
            if (getDown() + mFallSpeed < mMaxY) {
                mY += mFallSpeed;
            } else {
                mY = mMaxY - (frame.bY + frame.bH);
            }
        }
    }

}

/**
 * Decreases the y coordinate by the speed
 * @param speed The movement speed
 */
void Sprite::moveUp(int speed) {
    mY -= speed;
}

/**
 * Increases the y coordinate by the speed
 * @param speed The movement speed
 */   
void Sprite::moveDown(int speed) {
    mY += speed;
}

/**
 * Decreases the x coordinate by the speed
 * @param speed The movement speed
 */
void Sprite::moveLeft(int speed) {
    mX -= speed;
}

/** 
 * Increases the x coordinate by the speed
 * @param speed The movement speed
 */
void Sprite::moveRight(int speed) {
    mX += speed;
}

/**
 * Draws the sprite at the x,y coordinate with the current frame of animation
 * @param gl The graphics context
 * @param cam The camera coordinate
 * @param direction The direction
 * @param x The x coordinate 
 * @param y The y coordinate
 */
void Sprite::drawXY(SDL_Surface* g, Point* cam, char dir, int x, int y) {
	mSpriteSheet->drawFrame(g, x - cam->x, y - cam->y, mFrameIndex, dir);
}

/**
 * Sets the flickering animation state
 * @param flicker Is the sprite flickering
 */
void Sprite::setFlicker(bool flicker) {
    mFlicker = flicker;
}

/**
 * Sets the rotating state
 * @param rotate Is the sprite rotating
 */
void Sprite::setRotate(bool rotate) {
    mRotate = rotate;
}

/**
 * Returns the rotating state of the sprite
 * @return Is the sprite rotating
 */
bool Sprite::getRotate() {
    return mRotate;
}

/**
 * Returns the current frame of animation from the sprite sheet
 * @param direction The direction of the frame
 * @return The image data for the frame of animation
 */
SDL_Surface* Sprite::getFrame(char direction) {
    return mSpriteSheet->getFrame(mFrameIndex, direction);
}

/**
 * Returns a frame of animation from the sprite sheet
 * @param frameIndex The frame index
 * @param direction The direction of the frame
 * @return The image data for the frame of animation
 */
SDL_Surface* Sprite::getFrame(int frameIndex, char direction) {
    return mSpriteSheet->getFrame(frameIndex, direction);
}

/**
 * Returns a frame of animation from the sprite sheet repeated to the height
 * @param frameIndex The frame index
 * @param direction The direction of the frame
 * @param height The height of the repeating sprite
 * @return The image data for the frame of animation
 */
SDL_Surface* Sprite::getFrame(int frameIndex, char direction, int height) {
    //try {
/*
        BufferedImage img = mSpriteSheet.getFrame(frameIndex, direction);
        BufferedImage retImg = new BufferedImage(16, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = retImg.getGraphics();
        int h = height / 16;
        for (int i = 0; i < h + 1; i++) {
            g.drawImage(img, 0, i * 16, null);
        }
        return retImg.getSubimage(0, 0, img.getWidth(), height);
*/
		return mSpriteSheet->getFrame(frameIndex, direction);
    //} catch (Exception e) {
    //    return mSpriteSheet->getFrame(frameIndex, direction);
    //}

}

/**
 * Sets the minimum y coordinate for the sprite movement
 * @param minY The minimum y coordinate
 */
void Sprite::setMinY(int minY) {
    mMinY = minY;
}

/**
 * Returns the minimum y coordinate for the sprite movement
 * @return The y minimum coordinate
 */
int Sprite::getMinY() {
    return mMinY;
}

/**
 * Sets the maximum y coordinate for the sprite movement
 * @param maxY The minimum y coordinate
 */
void Sprite::setMaxY(int maxY) {
    mMaxY = maxY;
}

/**
 * Returns the maximum y coordinate for the sprite movement
 * @return The x maximum coordinate
 */
int Sprite::getMaxY() {
    return mMaxY;
}

/**
 * Sets the minimum x coordinate for the sprite movement
 * @param minX The minimum x coordinate
 */
void Sprite::setMinX(int minX) {
    mMinX = minX;
}

/**
 * Returns the minimum x coordinate for the sprite movement
 * @return The minimum x coordinate
 */   
int Sprite::getMinX() {
    return mMinX;
}

/** 
 * Sets the maximum x coordinate for the sprite movement
 * @param maxX The maximum x coordinate
 */   
void Sprite::setMaxX(int maxX) {
    mMaxX = maxX;
}

/**
 * Returns the maximum x coordinate for the sprite movement
 * @return The maximum x coordinate
 */
int Sprite::getMaxX() {
    return mMaxX;
}

/**
 * Returns the centre point of the sprite 
 * @return The centre point of the sprite
 */
Point* Sprite::getCenter() {
    return new Point(getLeft() + getWidth() / 2, getUp() + getHeight() / 2);
}

/**
 * Sets the x coordinate of the sprite
 * @param x The x coordinate
 */
void Sprite::setX(int x) {
    mX = x;
}

/** 
 * Returns the x coordinate of the sprite 
 * @return The x coordinate of the sprite
 */
int Sprite::getX() {
    return mX;
}

/**
 * Sets the y coordinate of the sprite
 * @param y The y coordinate of the sprite
 */
void Sprite::setY(int y) {
    mY = y;
}

/**
 * Returns the y coordinate of the sprite
 * @return The y coordinate of the sprite
 */
int Sprite::getY() {
    return mY;
}

/**
 * Returns the width of the sprite 
 * @return The width of the sprite
 */
int Sprite::getWidth() {
    return frame.bW;
}

/**
 * Returns the height of the sprite
 * @return The height of the sprite
 */
int Sprite::getHeight() {
    return frame.bH;
}

/**
 * Returns the sprite collision bounding box
 * @return The boundaries of the sprite
 */ 
Rectangle* Sprite::getBounds() {
    return new Rectangle(getLeft(), getUp(), getWidth(), getHeight());
}

/**
 * Returns the left coordinate of the sprite
 * @return The left coordinate of the sprite
 */ 
int Sprite::getLeft() {
    return mX + frame.bX;
}

/**
 * Returns the right coordinate of the sprite
 * @return The right coordinate of the sprite
 */   
int Sprite::getRight() {
    return mX + frame.bX + frame.bW;
}

/**
 * Returns the up coordinate of the sprite
 * @return The up coordinate of the sprite
 */  
int Sprite::getUp() {
    return mY + frame.bY;
}

/**
 * Returns the down coordinate of the sprite
 * @return The down coordinate of the sprite
 */  
int Sprite::getDown() {
    return mY + frame.bY + frame.bH;
}

/**
 * Sets the direction of the sprite 
 * @param direction The direction of the sprite 'l'-left, 'r'-right
 */
void Sprite::setDirection(char new_direction) {
    mDirection = new_direction;
}

/**
 * Returns the direction of the sprite 
 * @return The direction of the sprite 'l'-left, 'r'-right
 */
char Sprite::getDirection() {
    return mDirection;
}

/**
 * Returns the movement speed of the sprite 
 * @return The movement speed
 */
int Sprite::getSpeed() {
    return mSpeed;
}

/** 
 * Sets the movement speed of the sprite
 * @param speed The movement speed
 */
void Sprite::setSpeed(int new_speed) {
    mSpeed = new_speed;
}

/**
 * Sets the climbing state of the sprite
 * @param climbing Is the sprite climbing
 */
void Sprite::setClimb(bool new_climbing) {
    mClimbing = new_climbing;
}

/**
 * Returns the climbing state of the sprite
 * @return True if the sprite is climbing
 */
bool Sprite::getClimb() {
    return mClimbing;
}

/** 
 * Sets the jumping state of the sprite
 * @param jumping Is the sprite jumping
 */
void Sprite::setJump(bool new_jumping) {
    mJumping = new_jumping;
}

/**
 * Gets the jumping sprite of the sprite
 * @return True if the sprite is jumping
 */
bool Sprite::getJump() {
    return mJumping;
}

/**
 * Sets the falling speed of the sprite
 * @param fallSpeed The change in y coordinate when the sprite is falling
 */
void Sprite::setFallSpeed(int fallSpeed) {
    mFallSpeed = fallSpeed;
}

/**
 * Returns the number of frames in the current animation
 * @return The number of frames
 */
int Sprite::getFrameCount() {
    return frame.End;
}

/**
 * Returns the index of the current frame of animation
 * @return The index of the current frame of animation
 */   
int Sprite::getFrameIndex() {
    return mFrameIndex;
}

/**
 * Sets the current frame index
 * @param index The frame index
 */
void Sprite::setFrameIndex(int new_index) {
    mFrameIndex = new_index;
}

/**
 * Returns the frame height 
 * @return The frame height
 */  
int Sprite::getFrameHeight() {
    return mFrameHeight;
}

/**
 * Returns the frame width
 * @return The frame width
 */
int Sprite::getFrameWidth() {
    return mFrameWidth;
}

/**
 * Returns the fire invulnerability state of the sprite
 * @return The fire invulnerability 
 */
int Sprite::getFireInvunerable() {
    return mFireInvunerable;
}

/**
 * Sets the fire invulnerability state of the sprite
 * @param fireInvunerable The fire invulnerability 
 */
void Sprite::setFireInvunerbility(int fireInvunerable) {
    mFireInvunerable = fireInvunerable;
}


/**
 * Sets the jumping speed
 * @param jumpSpeed The amount to decrease the y coordinate when jumping
 */
void Sprite::setJumpSpeed(int jumpSpeed) {
    mJumpSpeed = jumpSpeed;
}

/**
 * Deallocates memory by destroying the Background
 */
Sprite::~Sprite()
{
	if(mSpriteSheet)
		mSpriteSheet->~SpriteSheet();
}


