#ifndef __Sprite_H__
#define __Sprite_H__

#pragma once

#include "Image.h"
#include "SpriteSheet.h"
#include "Point.h"
#include "Rectangle.h"
#include "Settings.h"
#include "Frame.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;  

/**
 * @class Sprite
 * @brief  This class load and draws a sprite containing frames of animation 
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Sprite
{
public:
	Sprite(int index, char direction, int x, int y);
	bool loadSpriteSheet(char* path);
	void split(const string& s, char c, vector<string>& v);
	int getIndex();
    void setIndex(int index);
	int getType();
    void setHitTimelimit(int hitTimelimit);
    int getHitTimelimit();
    bool setAnimationState(string state);
    bool setAnimationStateIndex(int index);
	string getAnimationState();
	int getAnimationStateIndex();
	void draw(Point* cam);
	void advanceFrame();
	void move();
	void moveUp(int speed);
	void moveDown(int speed);
	void moveLeft(int speed);
	void moveRight(int speed);
	void drawXY(Point* cam, char dir, int x, int y);
	void setFlicker(bool flicker);
	void setRotate(bool rotate);
	bool getRotate();
	OSL_IMAGE* getFrame(char direction);
	OSL_IMAGE* getFrame(int frameIndex, char direction);
	OSL_IMAGE* getFrame(int frameIndex, char direction, int height);
	void setMinY(int minY);
	int getMinY();
	void setMaxY(int maxY);
	int getMaxY();
	void setMinX(int minX);
	int getMinX();
	void setMaxX(int maxX);
	int getMaxX();
	Point* getCenter();
	void setX(int x);
	int getX();
	void setY(int y);
	int getY();
	int getWidth();
	int getHeight();
	Rectangle* getBounds();
	int getLeft();
	int getRight();
	int getUp();
	int getDown();
	void setDirection(char direction);
	char getDirection();
	int getSpeed();
	void setSpeed(int speed);
	void setClimb(bool climbing);
	bool getClimb();
    void setJump(bool jumping);
	bool getJump();
	void setFallSpeed(int fallSpeed);
	int getFallSpeed();
	int getFrameCount();
	int getFrameIndex();
    void setFrameIndex(int index);
	int getFrameHeight();
	int getFrameWidth();
	int getFireInvunerable();
	void setFireInvunerbility(int fireInvunerable);
	void setJumpSpeed(int jumpSpeed);
	~Sprite();

private:
	/** The sprite sheet containing the animation */
    SpriteSheet* mSpriteSheet;
    /** The x coordinate */
    int mX;
    /** The y coordinate */
    int mY;
    /** The direction 'l'-left 'r'-right */
    char mDirection;
    /** The movement speed */
    int mSpeed;
    /** The minimum x coordinate */
    int mMinX;
    /** The maximum x coordinate */ 
    int mMaxX;
    /** The minimum y coordinate */
    int mMinY;
    /** The maximum y coordinate */
    int mMaxY;
    /** The index of the sprite */
    int mIndex;
    /** The type of entity 0-player, 1-collectable, 2-1 hit kill enemy, 3-walking enemy, 3-shell enemy, 4-jumping enemy, 5-falling enemy */
    int mType; 
    /** Is the sprite jumping */
    bool mJumping;
    /** Is the sprite climbing */
    bool mClimbing;
    /** The animation frame information */
    vector<Frame*> mFrameInfo; //<Frame>
    /** The width of a frame of animation */
    int mFrameWidth;
    /** The height of a frame of animation */
    int mFrameHeight;
    /** The number of frame in the sprite sheet */
    int mFrameCount;
    /** The current frame information */
    Frame* frame;
    /** The time elapsed between advancing frame */
    int mFrameTimer;
    /** The animation speed of the sprite */      
    int mAnimationSpeed;
    /** The index of the current frame of animation */
    int mFrameIndex;
    /** The time elapsed before the enemy 'wakes' from being hit */
    int mHitTimelimit;
    /** Is the sprite invunerable to fire */
    int mFireInvunerable;
    /** The amount of change to y coordinate when jumping */
    int mJumpSpeed;
    /** The amount of change to y coordinate when falling */
    int mFallSpeed;
    /** Is the sprite flickering when invincible after being hit */   
    bool mFlicker;
    /** Is the sprite rotating */
    bool mRotate;
    /** The angle of rotation */
    int mAngle;
};

#endif
