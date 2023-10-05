#ifndef __Entity_H__
#define __Entity_H__

#pragma once

#include "Map.h"
#include "Sprite.h"
#include <oslib/oslib.h>

/**
 * @class Entity
 * @brief This class draws a non-playable entity sprite and
 * detects collisions between level map tiles and entities
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Entity : public Sprite
{
public:
	Entity(int index, char direction, int x, int y);
	void drawImage(OSL_IMAGE* img, int x, int y);
	void draw(Point* cam);
	Rectangle* getVineBounds();
	bool detectMapCollision(Map* map);
	~Entity();

private:
	/** The start y coordinate */
    int mStartY;
    /** The start x coordinate */
    int mStartX;
    /** The amount of time elapsed since the entity changed state */
    int mHitTimer;
	/** The amount of time elapsed since the last frame draw */
    float mSlowDownTimer;
};

#endif
