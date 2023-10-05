#include "Player.h"

#include "main.h"

string Settings::State; 
GamePanel* main::mGamePanel;

/**
 * Constructs the player
 * @param character The folder number of the players sprite sheet
 * @param size The size of the player 0-small, 1-large, 2-fire powerup
 * @param invincible The invincible state 0-not invincible, 1-invincible
 * @param lives The amount of lives
 * @param coins The amount of coins
 * @param score The total score
 * @param mTime The time limit
 * @param startState The start state 0-stand left, 1-stand right, 2-up pipe 3-down pipe 4-left pipe 5-right pipe
 * @param x The x coordinate
 * @param y The y coordinate
 */    
Player::Player(int character, int size, int invincible,
        int lives, int coins, int score, int time, int startState,
        int x, int y) : Sprite(character, 'r', x, y) {

    mControls = new Control();

	char path[100];
    sprintf (path, "res/chr/%d/%d", getIndex(), size);	
    
	//construct sprite
    Sprite::loadSpriteSheet(path);
    setAnimationState("stand");
    setX(x);
    setY(y);
    setMinY(getDown());
  
    mSize = size;
    mInvincible = invincible;
    mLives = lives;
    mScore = score;
    mCoins = coins;
    mTime = time;
	mEndState = 0;
	mFlagState = 0;
    mPipeState = 0;
	mTimer = 0;
	mFireTimer = 0;
	setSpeed(0);

    mPointFont = new GameFont("score");

    if (startState == 0) //left
    {
        setDirection('l');
    } else if (startState == 1) //right
    {
        setDirection('r');
    } else if (startState == 2) //move up
    {
        setMinY(getUp());
        setY(getY() + getFrameHeight());
        setDirection('r');
        mPipeState = 2;
    } else if (startState == 3) //move down
    {
        setMaxY(getDown());
        setY(getY() - getFrameHeight());
        setDirection('r');
        mPipeState = 3;
    } else if (startState == 4) //walk left
    {
        setMinX(getLeft());
        setX(getX() + getFrameWidth());
        setY(getY() - 2);
        setDirection('l');
        mPipeState = 4;
    } else if (startState == 5) //walk right
    {
        setMaxX(getRight());
        setX(getX() - getFrameWidth());
        setY(getY() - 2);
        setDirection('r');
        mPipeState = 5;
    }
    //mControls = new Control();
    sprintf (path, "res/chr/%d/3.png", getIndex());	
    mStars = new SpriteSheet(path, 48, 48, 20);
    mEndState = 0;
    mExitLevel = false;
    mExitBonus = false;

}

/**
 * Sets the size of the player
 * @param size The new size 0-small, 1-large, 2-fire powerup
 */  
void Player::setSize(int size) {
    string str = getAnimationState();
    mSize = size;
	char path[100];
    sprintf (path, "res/chr/%d/%d", getIndex(), size);	
    Sprite::loadSpriteSheet(path);
    setAnimationState(str);
}

/**
 * Gets the size of the player
 * @return The size of the player
 */    
int Player::getSize() {
    return mSize;
}

/**
 * Sets the invincible status of the player
 * @param The invincible status of the player 0-normal 1-invincible
 */    
void Player::setInvincible(int invincible) {
    mInvincible = invincible;
}

/**
 * Gets the invincible status of the player
 * @return The invincible status of the player
 */ 
int Player::getInvincible() {
    return mInvincible;
}

/** Sets the sprite sheet
 * @param character The folder number of the sprite sheet
 */
void Player::setIndex(int character) {
    Sprite::setIndex(character);
    setSize(mSize);
}

/**
 * Gets the string value of a integer number
 * @return The string value of the int
 */
string Player::convertInt(int number)
{
	//http://www.cplusplus.com/forum/beginner/7777/
    if (number == 0)
        return "0";
    string temp="";
    string returnvalue="";
    while (number>0)
    {
        temp+=number%10+48;
        number/=10;
    }
    for (int i=0;i<(int)temp.length();i++)
        returnvalue+=temp[temp.length()-i-1];
    return returnvalue;
}

/**
 * Draws the player on the graphics surface
 * @param g The graphics context
 * @param cam The camera coordinates
 */    
void Player::draw(Point* cam) {

    Sprite::draw(cam); //draw the sprite

    //draw collected points
    for (int i = 0; i < (int)mDrawPoints.size(); i++) {
        mDrawPoints[i]->timer++;
		if (mDrawPoints[i]->timer < 30)
        	mPointFont->drawString(0, convertInt(mDrawPoints[i]->value), mDrawPoints[i]->x - cam->x, mDrawPoints[i]->y - cam->y);
        if (mDrawPoints[i]->timer % 2 == 0) {
            if (mDrawPoints[i]->timer < 30) {
                mDrawPoints[i]->y--; //scroll up
            } else {
				mDrawPoints.erase(mDrawPoints.begin() + i);
            }
        }
    }

    //set the amount of time left
    if (mFlagState == 0 && !Settings::FreezeTime) {
        mTimer++;
        if (mTimer % 70 == 0) {
            if (mTime > 0) {
                mTime--;
            } else //time up
            {
                if (Settings::State == "game") {
                    enemyCollision(); //kill player
                }
            }
        }
    }

    //set the exit flag if the player has reached the bonus/end of level
    bool exit = false;
    if (mPipeState < 2) //normal direction
    {
        move(); //move the player
    } else if (mPipeState == 2) {
        if (getUp() > getMinY()) {
			setAnimationState("stand");
            moveUp(1); //up pipe
        } else {
            exit = true;
        }
    } else if (mPipeState == 3) {
        if (getDown() < getMaxY()) {
			setAnimationState("stand");
            moveDown(1); //down pipe
        } else {
            exit = true;
        }
    } else if (mPipeState == 4) {
        if (getLeft() > getMinX()) {
			setAnimationState("stand");
            moveLeft(1); //left pipe
        } else {
            exit = true;
        }
    } else if (mPipeState == 5) {
        if (getRight() < getMaxX()) {
			setAnimationState("stand");
            moveRight(1); //right pipe
        } else {
            exit = true;
        }
    }
    if (exit) //exit bonus/level position reached
    {
        if (mExitLevel) {
            mEndState = 7; //GamePanel -> AdvanceLevel()
        } else if (mExitBonus) {
            mEndState = 8; //GamePanel -> AdvanceBonus()
        }
        mPipeState = 0;
    }

    //reached end of level flag pole
    if (mFlagState == 1) {
        setAnimationState("flag");
        setFallSpeed(3);
    }
    if (mFlagState == 2) //jump off flag pole
    {
		mTimer++;
		//if (mTimer > 100) {
			setSpeed(4);
			setJumpSpeed(6);
			setFallSpeed(7);
			setJump(true);
			if(mSize == 0)
				setMinY(getUp()-20);
			else
				setMinY(getUp()-10);
			mFlagState = 3;
			mTimer = 0;
		//}
    } else if (mFlagState == 3) //stand while remaining time is counted
    {
		if (getDown() == getMaxY()) {
			setSpeed(0);
			setDirection('r');
			setAnimationState("victory");
			mTimer++;
			setJump(false);
			if (mTimer > 100) //walk to castle
			{
				setSpeed(2);
				mFlagState = 4;
				setAnimationState("walk");
				setMaxX(getX() + 150);
				mTimer = 0;
			}
		}
    } else if (mFlagState > 3) //count remaining time
    {
		if (getRight() >= getMaxX() && mFlagState < 5) //raise castle flag (ln.519)
		{
			mFlagState = 5;
		}
		if (mTime >= 0) {
			mTime-=2;
			addScore(20);
		} else //finished counting time
		{
			mTimer++;
			if (mTimer > 100) {
				mEndState = 7; //end of level
				mFlagState = 0;
			}
		}
    }

    //set player movement based on controls if not dead, on flag or in pipe
    if (getAnimationState() != "die" && mFlagState == 0 && mPipeState < 2) {
        setMovement();
        if (mInvincible == 1) //invincibility powerup
        {
            mIvincibleTimer++;
            if (mIvincibleTimer % 5 == 0) {
                if (mInvincibleFrame < 10) {
                    mInvincibleFrame++;
                } else {
                    mInvincibleFrame = 0;
                }
            }
            //align animation over player
            if (getDirection() == 'r') {
				mStars->drawFrame(getCenter()->x - 28 - cam->x, getUp() - 16 - cam->y, mInvincibleFrame, 'r');
            } else {
				mStars->drawFrame(getCenter()->x - 19 - cam->x, getUp() - 16 - cam->y, mInvincibleFrame, 'r');
            }
            if (mIvincibleTimer > 1000) {
                mIvincibleTimer = 0;
                setInvincible(0);
            }
        } else if (mInvincible == 2) //temporary invincibility after enemy collision
        {
            mIvincibleTimer++;
            setFlicker(true);
            if (mIvincibleTimer > 100) {
                mIvincibleTimer = 0;
                setInvincible(0);
                setFlicker(false);
            }
        }
    }

}

/**
 * Makes the player bounce off an enemy when hit
 */    
void Player::bounce() {
    setJump(true);
    setMinY(getDown() - 25);
    mControls->mJumpPressed = true;
}

/**
 * Move the player and set animation state based on the control buttons which are pressed
 */
void Player::setMovement() {

    if (getClimb() && mControls->mUpPressed) {
        setAnimationState("climb");
        advanceFrame();
        setMinY(0);
        moveUp(1);
    }

    if (getClimb() && mControls->mDownPressed) {
        setAnimationState("climb");
        advanceFrame();
        moveDown(1);
    }

    //set amount of time the direction button has been pressed
    if ((mControls->mLeftPressed || mControls->mRightPressed) && (getAnimationState() != "crouch" || getJump())) {
        if (getClimb()) {
            advanceFrame();
        }

        if (mControls->mRunPressed) //run
        {
            if (mControls->mDirectionHeld < 80) {
                mControls->mDirectionHeld++;
            }
        } else //walk
        {
            if (mControls->mDirectionHeld < 20) {
                mControls->mDirectionHeld++;
            } else {
                mControls->mDirectionHeld -= 4;
            }
        }
    } else //direction released
    {
        if (mControls->mDirectionHeld > 0) {
            mControls->mDirectionHeld -= 4;
        } else {
            mControls->mDirectionHeld = 0;
        }
    }

    //set speed based on how long the direction button has been held
    if (mControls->mDirectionHeld == 0) //stand
    {
        setSpeed(0);
    } else if (mControls->mDirectionHeld > 0 && mControls->mDirectionHeld < 20) //walk 1
    {
        setSpeed(3);
    } else if (mControls->mDirectionHeld > 20 && mControls->mDirectionHeld < 40) //jog
    {
        setSpeed(4);
    } else if (mControls->mDirectionHeld > 40 && mControls->mDirectionHeld <= 80) //run
    {
        setSpeed(5);
    }

    //set player sprite animation based on speed when on ground level
    if (getDown() == getMaxY() && getAnimationState() != "climb") //on ground level
    {
        if (getSpeed() == 0) {
            setAnimationState("stand");
        } else if (getSpeed() == 3) {
            setAnimationState("walk");
        } else if (getSpeed() == 4) {
            setAnimationState("jog");
        } else if (getSpeed() == 5) {
            setAnimationState("run");
        }
        if (mControls->mDownPressed || mCrouchingUnderBlock) {
            setAnimationState("crouch");
            mCrouchingUnderBlock = true;
        }
    }

    //fireball
    mFireTimer++;
    if (mControls->mRunPressed && mControls->mRunReleased) {
        if (mFireTimer > 40 && mSize == 2) {

            mControls->mRunReleased = false;
            //add fireball
            if ((getAnimationState() != "climb" && getAnimationState() != "fall") && !mCrouchingUnderBlock) {
				main::mSoundEffect->play("fire.wav", 0);
                if (getDirection() == 'r') {
                    main::mGamePanel->addEntity(1, 'r', getRight() - 5, getUp() + getHeight() / 2);
                } else {
                    main::mGamePanel->addEntity(1, 'l', getLeft(), getUp() + getHeight() / 2);
                }
            }
            mFireTimer = 0;
        }
    }
    
    //change direction
    if(!mCrouchingUnderBlock)
    {
		if (mControls->mRightPressed && !mControls->mLeftPressed && getDirection() == 'l') {
			setDirection('r');
		} else if (mControls->mLeftPressed && !mControls->mRightPressed && getDirection() == 'r') {
			setDirection('l');
		}
	}

    //set player state to jump
    if ((mControls->mJumpPressed && mControls->mJumpReleased && getDown() == getMaxY()) || (getClimb() && mControls->mJumpPressed && mControls->mJumpReleased)) {
        mControls->mJumpReleased = false;
        setJump(true);
        setMinY(getDown() - Settings::JumpHeight); //set jump height
        if (getAnimationState() != "crouch") {
			main::mSoundEffect->play("jump.wav", 0);
            setAnimationState("jump");
        }
    }

    //set player state to fall if reaches jump height
    if (getMinY() - getDown() > 65 || (!mControls->mJumpPressed && getJump())) {

        setJump(false);
        if (getAnimationState() != "crouch") {
            setAnimationState("fall");
        }
    }
}

/**
 * Detect collisions between the player and entities
 * @param entities A list of entities
 * @return True if a collision has occurred
 */    
bool Player::detectEntityCollision(vector<Entity*> entities) {

    bool collision = false;

    if (getAnimationState() == "die") {
        return collision;
    }

    for (int a = 0; a < (int)entities.size(); a++) {

        Entity* e = entities[a];

        if (e->getType() == 1) //fireball
        {		
            //test for collisions between fireball and enemies
            for (int b = 0; b < (int)entities.size(); b++) {

                if (a != b && e->getBounds()->intersects(entities[b]->getBounds())) {

                    if (entities[b]->getFireInvunerable() == 0) //kill enemy with fire
                    {
                        if (!entities[b]->getRotate() && entities[b]->getType() >= 20 && entities[b]->getType() < 50) {
                            //remove fireball
                            entities[a]->setAnimationState("die");
                            entities[a]->setJump(false);
                            entities[a]->setSpeed(0);
                            entities[a]->setMaxY(entities[a]->getDown());
                            //add points
                            addScore(100);
                            drawPoints(100, entities[b]->getLeft() + 1, entities[b]->getUp() - 5);
                            //spin enemy off screen
                            entities[b]->setRotate(true);
                            entities[b]->setJump(true);
                            entities[b]->setSpeed(3);
                            entities[b]->setJumpSpeed(6);
                            entities[b]->setFallSpeed(7);
                            if (getDirection() == 'r') {
                                entities[b]->setDirection('r');
                            } else {
                                entities[b]->setDirection('l');
                            }
                            entities[b]->setMinY(entities[b]->getDown()- 10);
                            entities[b]->setMaxY(200000);
                        }
                    } else //enemy is invunerable to fire
                    {
                        //stop fireball moving jump and remove
                        entities[a]->setAnimationState("die");
                        entities[a]->setJump(false);
                        entities[a]->setSpeed(0);
                        entities[a]->setMaxY(entities[a]->getDown());
                        return collision;
                    }

                }
            }
        }

        //stop player jumping over flag pole
        if (e->getType() == 9 && mFlagState == 0) {
            if (getDown() <= e->getUp()) {
                if (getRight() >= e->getLeft()) {
                    setX(e->getLeft() - getWidth() - 8);
                }
            }
        }

        //castle flag
        if (e->getType() == 7) {
            if (mFlagState == 5) //user has reached the end of the level
            {
                entities[a]->setAnimationState("raise");
                if (e->getFrameIndex() >= e->getFrameCount()) {
                    mFlagState = 6;
                }
            } else if (mFlagState == 6) {
                entities[a]->setAnimationState("flying");
            }
        }

        //growing vine
        Rectangle* pb = getBounds(); //player bounds
        Rectangle* vb = e->getVineBounds(); //vine bounds
        if (pb->intersects(vb) && e->getType() == 14) //player intersects vine
        {
            //climb vine
            if (mControls->mUpPressed || getAnimationState() == "climb") {
				if(getAnimationState() != "climb")
					setX(e->getX()-9);
				setAnimationState("climb"); //set climb animation
				setJump(false);
				setClimb(true); 
            }
        } else {
            //set fall state
			if ((!getClimb() && !getJump()) && getDown() != getMaxY() && getAnimationState() != "jump" && mFlagState > 1) {
                setAnimationState("fall");
            }
            setClimb(false);
        }
        delete vb;

        //collision between player and entity
        Rectangle* eb = e->getBounds(); 
        if (pb->intersects(eb)) {

            //remove enemy on collision
            if (e->getAnimationState() == "die" || e->getAnimationState() == "dead") {
                if (e->getAnimationState() == "dead" && e->getType() != 1) {
                   entities.erase (entities.begin()+a);
                }
                return collision;
            }

            collision = true;
            if (e->getType() == 9) //flag pole
            {
                for (int b = 0; b < (int)entities.size(); b++) {
                    if (entities[b]->getType() == 8) //end flag
                    {

                        if (getY() + getHeight() / 2 >= entities[b]->getUp()) {
                            entities[b]->setAnimationState("fall");
                        }

                        if (mFlagState == 0) //drop flag
                        {
                            setX(getX() + getWidth() / 4 - 1);
                            mFlagState = 1;
                            setAnimationState("flag");
                            setSpeed(0);
                            setFallSpeed(1);
                            return collision;
                        } else if (mFlagState == 1) //flag reached base
                        {
                            if (getDown() == entities[b]->getMaxY() && entities[b]->getAnimationState() == "fall") {
                                mFlagState = 2;
                                return collision;
                            }
                        }
                    }
                }
            }

            //hit enemy while invincible
            if (mInvincible == 1 && e->getType() >= 20 && e->getType() <= 50) {
                if (!e->getRotate()) {
					main::mSoundEffect->play("kill.wav", 0);
                    if (getDirection() == 'r') {
                        entities[a]->setDirection('r');
                    } else {
                        entities[a]->setDirection('l');
                    }
                    entities[a]->setMaxX(200000);
                    entities[a]->setRotate(true); //spin off screen
                    entities[a]->setJump(true);
                    entities[a]->setSpeed(1);
                    entities[a]->setJumpSpeed(2);
                    entities[a]->setFallSpeed(4);
                    entities[a]->setMinY(e->getUp() - 8);
                    entities[a]->setMaxY(200000);
                    collision = true;
                }
                return collision;
            }

            if (e->getType() == 10) //mushroom
            {
				main::mSoundEffect->play("powerup.wav", 0);
                addScore(1000);
                drawPoints(1000, getLeft() + 16, getUp() - 5);
                if (mSize == 0) {
                    setSize(1);
                }
				entities[a]->setAnimationState("dead");
            } else if (e->getType() == 11) //fire
            {
				main::mSoundEffect->play("powerup.wav", 0);
                addScore(1000);
                if (getDirection() == 'r') {
                    drawPoints(1000, getLeft() + 16, getUp() - 5);
                } else {
                    drawPoints(1000, getLeft() - 16, getUp() - 5);
                }
                setSize(2);
                mControls->mRunReleased = false;
				entities[a]->setAnimationState("dead");
            } else if (e->getType() == 12) //star
            {
				main::mSoundEffect->play("powerup.wav", 0);
                addScore(1000);
                if (getDirection() == 'r') {
                    drawPoints(1000, getLeft() + 16, getUp() - 5);
                } else {
                    drawPoints(1000, getLeft() - 16, getUp() - 5);
                }
                setInvincible(1);
				entities[a]->setAnimationState("dead");
            } else if (e->getType() == 13) //life
            {
                addScore(1000);
                if (getDirection() == 'r') {
                    drawPoints(1000, getLeft() + 16, getUp() - 5);
                } else {
                    drawPoints(1000, getLeft() - 16, getUp() - 5);
                }
                addLife();
				entities[a]->setAnimationState("dead");
            } else if (e->getType() == 20) //piranha plant
            {
                //detect collision between frame range
                if (e->getAnimationState() == "normal") {
                    enemyCollision();
                }
            }

            //1 hit kill enemy type
            if (e->getType() == 30 || e->getType() == 36 || e->getType() == 37) {
                enemyCollision();
            }

            //normal 1 hit kill enemy / bullet enemy
            if (e->getType() == 31 || e->getType() == 35) {

                if (getDown() < e->getUp() + 10) {
					main::mSoundEffect->play("kill.wav", 0);
                    bounce();
                    addScore(200);
                    drawPoints(200, e->getLeft() + 7, e->getUp());
                    entities[a]->setAnimationState("die");
                } else {
                    enemyCollision();
                }
            }

            //1 hit get up enemy
            if (e->getType() == 32 && e->getAnimationState() != "hit") {
                if (getDown() < e->getUp() + 10) {
                    bounce();
                    //addScore(1000);
					main::mSoundEffect->play("hit.wav", 0);
                    entities[a]->setAnimationState("hit");
                } else {
                    enemyCollision();
                }
            }

            //flying or normal shell enemy
            if (e->getType() == 33 || e->getType() == 34) {
                if (e->getAnimationState() == "normal" || e->getAnimationState() =="kick") {
                    //set hit state
                    if (getDown() < e->getUp() + 10) {
                        bounce();
                        //addScore(1000);
				   	    main::mSoundEffect->play("hit.wav", 0);
                        entities[a]->setAnimationState("hit");
                    } else {
                        enemyCollision();
                    }
                } else if (e->getAnimationState() == "hit") {
					main::mSoundEffect->play("hit.wav", 0);
                    //bounce off shell
                    if (getDown() < e->getUp() + 10) {
                        bounce();
                    }
                    //kick shell
                    if (getCenter()->x > e->getCenter()->x) {
                        entities[a]->setDirection('l');
                        entities[a]->setX(e->getX() - 10);
                    } else {
                        entities[a]->setDirection('r');
                        entities[a]->setX(e->getX() + 10);
                    }
                    entities[a]->setAnimationState("kick");
                } else if (e->getAnimationState() == "fly") {
                    //stop flying
                    if (getDown() < e->getUp() + 5) {
                        bounce();
                        //addScore(1000);
						main::mSoundEffect->play("hit.wav", 0);
                        entities[a]->setAnimationState("normal");
                        entities[a]->setY(getY() + 20);
                        entities[a]->setSpeed(1);
                        entities[a]->setJump(false);
                    } else {
                        enemyCollision();
                    }
                }
            }
        }
        delete pb;
        delete eb;

        //stop piranha plant if player is near to pipe
        //if (e.getType() == 20 && e.getFrameIndex() <= 15) {
        //if (getRight() + getWidth() >= e.getLeft() &&
        //getLeft() - getWidth() <= e.getRight()) {
        //enm.get(a).setFrameIndex(10);
        //}
        //}

        //make falling enemy drop
        if (e->getType() == 37) {
            if (getRight() + 5 >= e->getLeft()
                    && getLeft() - 5 <= e->getRight()) {
                entities[a]->setAnimationState("fall");
                entities[a]->setMaxY(10000);
            }
        }
    }

    return collision;
}

/**
 * Sets the player size or removes a life if the player collides with an enemy
 */
void Player::enemyCollision() {
    if (!Settings::Invincible) {
        if ((getAnimationState() != "die" && mInvincible == 0) || mTime < 1 && getAnimationState() != "die") {
            if (mSize == 0) //small
            {
				main::mSoundEffect->play("die.wav", 0);
                setAnimationState("die");
                setSpeed(0);
                setMinY(getDown() - 65);
                setMaxY(10000);
                setJump(true);
                //removeLife();
            } else if (mSize == 1) //large
            {
				main::mSoundEffect->play("powerdown.wav", 0);
                setSize(0);
                setInvincible(2);
            } else if (mSize == 2) //fire
            {
				main::mSoundEffect->play("powerdown.wav", 0);
                setSize(1);
                setInvincible(2);
            }
        }
    }
}

/**
 * Detects collisions between the player and map tiles
 * @param map The level map
 * @return True if a collision occurred
 */ 
bool Player::detectMapCollision(Map* map) {
   bool collision = false;

    if (getAnimationState() == "die" || (mPipeState > 1 || mFlagState > 3)) //dying, in pipe or on flag
    {
        return collision; //skip map collision detection
    }

    //bonus / end level warp pipe 
    int exitX = 0;
    int exitY = 0;
    int exitState = 0;
    //exit pipe condition (start bonus or end level)
    if ((getCenter()->x / 16 == map->getBonusX() || getCenter()->x / 16 == map->getBonusX()+1) && getY()/ 16 == map->getBonusY()) //bonus level
    {
        exitX = map->getBonusX();
        exitY = map->getBonusY();
        exitState = map->getBonusState();
        mExitBonus = true;
        collision = true;
        setAnimationState("stand");
    } else if (getCenter()->x / 16 >= map->getEndX() && getY() / 16 == map->getEndY()) //end level
    {
        exitX = map->getEndX();
        exitY = map->getEndY();
        exitState = map->getEndState();
        mExitLevel = true;
        collision = true;
        setAnimationState("stand");
    }
    //realign and set pipe state to auto move player through pipe
    if (mControls->mUpPressed && exitState == 2) //up pipe
    {
        mPipeState = 2;
        setX(exitX * 16);
        setY(exitY * 16);
        setMinY((getUp() - 2) - getFrameHeight());
        return collision;
    }
    if (mControls->mDownPressed && exitState == 3) //down pipe
    {
        mPipeState = 3;
        setX(exitX * 16);
        setY(exitY * 16);
        setMaxY(getDown() + getFrameHeight());
        return collision;
    } else if (mControls->mLeftPressed && exitState == 4) //left pipe
    {
        mPipeState = 4;
        setX(exitX * 16);
        setY(exitY * 16 - 2);
        setDirection('l');
        setMinX(getLeft() - getFrameWidth());
        return collision;
    } else if (mControls->mRightPressed && exitState == 5) //right pipe
    {
        mPipeState = 5;
        setX(exitX * 16);
        setY(exitY * 16 - 2);
        setDirection('r');
        setMaxX(getRight() + getFrameWidth());
        return collision;
    } else {
        mExitBonus = false; //reset exit variables
        mExitLevel = false;
        collision = false;
    }

    //coin collision 
    //small
    if (map->getTile(getLeft() / 16, getUp() / 16)->tile1 == 10) {
        addCoin();
        map->getTile(getLeft() / 16, getUp() / 16)->tile1 = 0;
    }
    if (map->getTile(getRight() / 16, getUp() / 16)->tile1 == 10) {
        addCoin();
        map->getTile(getRight() / 16, getUp() / 16)->tile1 = 0;
    }
    //large
    if (map->getTile(getLeft() / 16, getCenter()->y / 16)->tile1 == 10) {
        addCoin();
        map->getTile(getLeft() / 16, getCenter()->y / 16)->tile1 = 0;
    }
    if (map->getTile(getRight() / 16, getCenter()->y / 16)->tile1 == 10) {
        addCoin();
        map->getTile(getRight() / 16, getCenter()->y / 16)->tile1 = 0;
    }


    //up collision player >= 1 for invisible block
    Point* colUp = NULL;
    if (map->getTile(getRight() / 16, getUp() / 16)->collision >= 1 && getJump()) {
        colUp = new Point(getCenter()->x / 16, getUp() / 16);
        if (map->getTile(colUp->x, colUp->y)->collision != 0) { 
            collision = true;
            setJump(false);
        }
    } else if (map->getTile(getLeft() / 16, getUp() / 16)->collision >= 1 && getJump()) {
        colUp = new Point(getCenter()->x / 16, getUp() / 16);
        if (map->getTile(colUp->x, colUp->y)->collision != 0) {
            collision = true;
            setJump(false);
        }
    }


 //if (map->getTile(getRight() / 16, getUp() / 16)->collision == 1 || //small
  //      (getSize() > 0 && map->getTile(getRight() / 16, (getUp() - 5)/ 16)->collision == 1 && getMaxY() == getDown()) && getAnimationState() != "crouch") //stop large player walking under blocks


    //right collision 
    if (map->getTile(getRight() / 16, getUp() / 16)->collision == 1 || //small
        (getSize() > 0 && map->getTile(getRight() / 16, getCenter()->y / 16)->collision == 1)) //stop large player walking under blocks
    {     
        if(!collision) //player has not collided with up block
        {  
            setMaxX((getRight() / 16) * 16);
            setX((getRight() / 16) * 16 - 25);
            collision = true;
        }
    } else {
        setMaxX(map->getWidth());
    }

    //left collision
    if (map->getTile(getLeft() / 16, getUp() / 16)->collision == 1 || 
        map->getTile(getLeft() / 16, getCenter()->y / 16)->collision == 1) {
        if(!collision) //player has not collided with up block
        {  
            setMinX((getLeft() / 16) * 16);
            setX((getLeft() / 16) * 16 + 8);
            collision = true;
        }
    } else {
        setMinX(0);
    }
 
    //down collision
    if ((map->getTile((getRight() - 5) / 16, (getDown() + getFallSpeed()) / 16)->collision == 1
            || map->getTile((getLeft() + 5) / 16, (getDown() + getFallSpeed()) / 16)->collision == 1)) {
        if (getClimb() && !mControls->mUpPressed) {
            setClimb(false);
        }
        if ((getMaxY() != getDown() && !getJump())) //set ground level
        {
            setMaxY((getDown()+ getFallSpeed()) / 16 * 16);
            mControls->mJumpPressed = false;
            setAnimationState("stand");
        }
        collision = true;
    } else //fall
    {
        if (!getJump()) {
            if (getAnimationState() != "jog" || getLeft() < 1) {
                setMaxY(map->getHeight() + 100);
            } else if (getDown() == getMaxY()) //run across blocks
            {
                if ((map->getTile((getCenter()->x + 16) / 16, getDown() / 16)->collision == 0
                        || map->getTile((getCenter()->x + 16) / 16, getDown() / 16)->collision == 2) //no collision
                        && getLeft() > 5) {
                    setMaxY(map->getHeight() + 100);
                } else //collision
                {
                    setMaxY(getDown() / 16 * 16);
                }
            }
        }
    }

    //collision with block 
    if (colUp != NULL) {
        Tile* t = map->getTile(colUp->x, colUp->y);
        if (Map::isBlock(t->tile1, t->tile2)) {
            if (t->state == 0) {
                collision = true;
                map->getTile(colUp->x, colUp->y)->state = 1;
                if (Map::isEmptyBlock(t->tile1, t->tile2) || t->count == 0) //empty block
                {
                    if (getSize() > 0) {
                        //SoundEffect* s = new SoundEffect("mnt/sd/Dragon Island/sfx/block.wav");
						//s->start();
                        main::mGamePanel->addBlockExplosion(colUp->x, colUp->y);
                        map->removeTile(colUp->x, colUp->y);
                    }
                }
                //block containing item
                if (t->count > 0) {
                    if (Map::isCoinBlock(t->tile1, t->tile2) || Map::is10CoinBlock(t->tile1, t->tile2)) {
                        addCoin();
                    } else if (Map::isEnlargeBlock(t->tile1, t->tile2)) {
                        if(Settings::Powerups)
                            main::mGamePanel->addEntity(10, 'r', colUp->x * 16, colUp->y * 16 - 5);
                    } else if (Map::isFireBlock(t->tile1, t->tile2)) {
                        if(Settings::Powerups)
                            main::mGamePanel->addEntity(11, 'r', colUp->x * 16, colUp->y * 16 - 5);
                    } else if (Map::isInvincibleBlock(t->tile1, t->tile2)) {
                        if(Settings::Powerups)
                            main::mGamePanel->addEntity(12, 'r', colUp->x * 16, colUp->y * 16 - 5);
                    } else if (Map::isExtraLifeBlock(t->tile1, t->tile2)) {
                        if(Settings::Powerups)
                            main::mGamePanel->addEntity(13, 'r', colUp->x * 16, colUp->y * 16 - 5);
                    } else if (Map::isVineBlock(t->tile1, t->tile2)) {
                        main::mGamePanel->addEntity(14, 'r', colUp->x * 16, (colUp->y * 16) - 15); //-5??
                    }
                }
            }
        }
    }


    //crouching under block
    if (getSize() >= 1 && (map->getTile(getLeft() / 16, (getUp() - 5) / 16)->collision == 1 || map->getTile(getRight() / 16, (getUp() - 5) / 16)->collision == 1) && getAnimationState() == "crouch") {
        setSpeed(0);
        if (getDirection() == 'r') {
            setX(getX() + 3);
        } else {
            setX(getX() - 3);
        }
        mCrouchingUnderBlock = true;
    } else {
        mCrouchingUnderBlock = false;
    }
    
    return collision;
 }

/**
 * Gets the total amount of coins the player has collected
 * @return The coin count
 */
int Player::getCoins() {
    return mCoins;
}

/**
 * Gets the total amount of remaining lives the player has
 * @return The lives count
 */
int Player::getLives() {
    return mLives;
}

/**
 * Gets the total score count for the player
 * @return The score count
 */   
int Player::getScore() {
    return mScore;
}

/**
 * Increases the coin count by one
 */
void Player::addCoin() {
    if (mCoins < 99) {
		main::mSoundEffect->play("coin.wav", 0);
        addScore(100);
        mCoins++;
    } else {
        mLives++;
        mCoins = 0;
    }
}

/**
 * Add the amount to the score count
 * @param value The value to add to the score count
 */
void Player::addScore(int value) {
    mScore += value;
}

/** 
 * Draw the points on the graphics surface
 * @param value The numeric value of the points collected
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Player::drawPoints(int value, int x, int y) {
	Score* scr = new Score(x, y, value);
	scr->timer = 0;
    mDrawPoints.push_back(scr);	
}

/**
 * Increases the lives count by one
 */
void Player::addLife() {
    mLives++;
}

/**
 * Gets the remaining time limit
 * @return The remaining time
 */
int Player::getTime() {
    return mTime;
}

/**
 * Decreases the lives count by one
 */   
void Player::removeLife() {

    if (!Settings::InfiniteLives) {
        mLives--;
    }
}

/**
 * Returns the players controller state
 * @return The players controller state
 */   
Control* Player::getControl() {

    if (!mControls->mRunPressed) {
        mControls->mRunReleased = true;
    }
    return mControls;
}

/** Gets the pipe state of the player */
int Player::getPipeState() {
    return mPipeState;
}

/** Gets the end state of the player */
int Player::getEndState() {
    return mEndState;
}

/**
 * Sets the state of the player
 * @param index The player animation state index 0-stand, 1-walk, 2-run, 3-jump, 4-fall, 5-crouch
 */
bool Player::setAnimationStateIndex(int index) {
    if (index == 4) {
        if (getDown() == getMaxY()) {
			main::mSoundEffect->play("jump.wav", 0);
        }
    }
    Sprite::setAnimationStateIndex(index);
    return true;
}

/**
 * Deallocates memory by destroying the Player
 */
Player::~Player() {
    delete mStars;
    delete mPointFont;
	if(!mDrawPoints.empty())
	{
		for(int i=0; i<(int)mDrawPoints.size(); i++)
			delete mDrawPoints[i];
		mDrawPoints.clear(); 
	}       
	delete mControls;
}
