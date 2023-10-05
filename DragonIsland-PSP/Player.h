#ifndef __Player_H__
#define __Player_H__

#pragma once

#include "Sprite.h"
#include "SpriteSheet.h"
#include "Control.h"
#include "GameFont.h"
#include "Entity.h"
#include "Map.h"
#include "SoundEffect.h"
#include "Score.h"
#include "Settings.h"
#include <vector>

/**
 * @class Player
 * @brief This class draws a user controllable sprite and detects collisions 
 * between level map tiles and entities
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Player : public Sprite
{
public:
	Player(int character, int size, int invincible, int lives, int coins, int score, int time, int startState, int x, int y);
	void setSize(int size);
	int getSize();
	void setInvincible(int invincible);
	int getInvincible();
	void setIndex(int character);
	string convertInt(int number);
	void draw(Point* cam);
	void bounce();
	void setMovement();
	bool detectEntityCollision(vector<Entity*> entities);
	void enemyCollision();
	bool detectMapCollision(Map* map);
	int getCoins();
	int getLives();
	int getScore();
	void addCoin();
	void addScore(int value);
	void drawPoints(int value, int x, int y);
	void addLife();
	int getTime();
	void removeLife();
	Control *getControl();
	int getPipeState();
	int getEndState();
	bool setAnimationStateIndex(int index); 
	~Player();  
	/** The controller state of the player */
    Control* mControls;

private:
	/** The number of lives remaining */
    int mLives;
    /** The total score count */
    int mScore;
    /** The total coin count */
    int mCoins;
    /** The remaining time */
    int mTime;
    /** The invincible status 0-not invincible 1-invincible */
    int mInvincible;
    /** The time elapsed since the player became invincible */
    int mIvincibleTimer;
    /** The drawing frame for the invincible sprite sheet */
    int mInvincibleFrame;
    /** The elapsed time before releasing a fire ball */
    int mFireTimer;
    /** The size of the player 0-small 1-large 2-fire powerup */
    int mSize;
    /** The time elapsed between clock ticks */
    int mTimer;
    /** The end of level flag state */
    int mFlagState;
    /** The warp pipe state 2-up 3-down 4-left 5-right */
    int mPipeState;
    /** The end state of the player 7-exit level 8-exit bonus */
    int mEndState;
    /** Player has exited level through a pipe */
    bool mExitBonus;
    /** The exit level state */
    bool mExitLevel;
    /** The player is crouching trapped under block */
    bool mCrouchingUnderBlock;
    /** Draw points for killing enemy or collecting powerup */
    vector<Score*> mDrawPoints;
    /** The image displayed over player when invincible */
    SpriteSheet* mStars;
    /** The font used to display points */
    GameFont* mPointFont;
};

#endif
