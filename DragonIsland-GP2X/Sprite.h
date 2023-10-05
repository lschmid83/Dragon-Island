#ifndef __Sprite_H__
#define __Sprite_H__

#pragma once

#include "Image.h"
#include "SpriteSheet.h"
#include "Point.h"
#include "Rectangle.h"
#include "Settings.h"
#include "Frame.h"
#include <SDL/SDL_rotozoom.h> 
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include "SDL_gfxPrimitives.h"

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
	Sprite::Sprite(int index, char direction, int x, int y);
	bool Sprite::loadSpriteSheet(char* path);
	void Sprite::split(const string& s, char c, vector<string>& v);
	int Sprite::getIndex();
    void Sprite::setIndex(int index);
	int Sprite::getType();
    void Sprite::setHitTimelimit(int hitTimelimit);
    int Sprite::getHitTimelimit();
    bool Sprite::setAnimationState(string state);
    bool Sprite::setAnimationStateIndex(int index);
	string Sprite::getAnimationState();
	int Sprite::getAnimationStateIndex();
	void Sprite::draw(SDL_Surface* g, Point* cam);
	void Sprite::advanceFrame();
	void Sprite::move();
	void Sprite::moveUp(int speed);
	void Sprite::moveDown(int speed);
	void Sprite::moveLeft(int speed);
	void Sprite::moveRight(int speed);
	void Sprite::drawXY(SDL_Surface* g, Point* cam, char dir, int x, int y);
	void Sprite::setFlicker(bool flicker);
	void Sprite::setRotate(bool rotate);
	bool Sprite::getRotate();
	SDL_Surface* Sprite::getFrame(char direction);
	SDL_Surface* Sprite::getFrame(int frameIndex, char direction);
	SDL_Surface* Sprite::getFrame(int frameIndex, char direction, int height);
	void Sprite::setMinY(int minY);
	int Sprite::getMinY();
	void Sprite::setMaxY(int maxY);
	int Sprite::getMaxY();
	void Sprite::setMinX(int minX);
	int Sprite::getMinX();
	void Sprite::setMaxX(int maxX);
	int Sprite::getMaxX();
	Point* Sprite::getCenter();
	void Sprite::setX(int x);
	int Sprite::getX();
	void Sprite::setY(int y);
	int Sprite::getY();
	int Sprite::getWidth();
	int Sprite::getHeight();
	Rectangle* Sprite::getBounds();
	int Sprite::getLeft();
	int Sprite::getRight();
	int Sprite::getUp();
	int Sprite::getDown();
	void Sprite::setDirection(char direction);
	char Sprite::getDirection();
	int Sprite::getSpeed();
	void Sprite::setSpeed(int speed);
	void Sprite::setClimb(bool climbing);
	bool Sprite::getClimb();
    void Sprite::setJump(bool jumping);
	bool Sprite::getJump();
	void Sprite::setFallSpeed(int fallSpeed);
	int Sprite::getFrameCount();
	int Sprite::getFrameIndex();
    void Sprite::setFrameIndex(int index);
	int Sprite::getFrameHeight();
	int Sprite::getFrameWidth();
	int Sprite::getFireInvunerable();
	void Sprite::setFireInvunerbility(int fireInvunerable);
	void Sprite::setJumpSpeed(int jumpSpeed);
	Sprite::~Sprite();

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
    vector<Frame> mFrameInfo; //<Frame>
    /** The width of a frame of animation */
    int mFrameWidth;
    /** The height of a frame of animation */
    int mFrameHeight;
    /** The number of frame in the sprite sheet */
    int mFrameCount;
    /** The current frame information */
    Frame frame;
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
