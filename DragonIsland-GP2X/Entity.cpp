#include "Entity.h"

/**
 * Constructs the Entity
 * @param index The entity sprite sheet file number (res/spr/)
 * @param direction The start direction 'l' = left 'r' = right
 * @param x The start x coordinate
 * @param y The start y coordinate
 */
Entity::Entity(int index, char direction, int x, int y) : Sprite(index, direction, x, y) {

	char path[100];
    sprintf (path, "mnt/sd/Dragon Island/spr/%d", getIndex());	
    Entity::loadSpriteSheet(path);
    setAnimationState("normal");
    setX(x);
    setY(y);
	setJump(false);
	setClimb(false);	
	mHitTimer = 0;
	mSlowDownTimer = 0;

    if (getType() == 1) //fireball
    {
        setSpeed(3);
        setY(getY() - getHeight());
        setMinY(getDown());
    }

    if (getType() >= 10 && getType() <= 13) //mushroom, fire, extra life
    {
        setY(getY() - getHeight());
        setSpeed(0);
    }
    if (getType() == 12) //star
    {
        setSpeed(1);
        setRotate(true);
        setMinY(getUp() - getHeight());
    }
    if (getType() == 14) //vine
    {
        setSpeed(0);
        setJump(true);
        setJumpSpeed(1);
        setMinY(-128);
        setMaxY(getDown());
        mStartY = getY();
        setFrameIndex(1);
    }

    if (getType() == 20) //piranha plant
    {
        setSpeed(0);
        setAnimationState("up");
    }

    if (getType() == 30) //moving 1-hit kill enemy
    {
        setSpeed(2);
    } else if (getType() == 31) //moving normal enemy
    {
        setSpeed(1);
    } else if (getType() == 32) //moving gets up
    {
        setSpeed(1);
    } else if (getType() == 33) //moving shell enemy
    {
        setSpeed(1);

    } else if (getType() == 34) //flying vertical shell enemy
    {
        setSpeed(0);
        setAnimationState("fly");
        setJump(true);
        setMinY(getUp() - 100);
    } else if (getType() == 35) //flying horizontal enemy
    {
        setSpeed(2);
        setMaxY(getDown());
        mStartX = getX();
        mStartY = getY();
    } else if (getType() == 36) //jumping enemy
    {
        setSpeed(0);
        setAnimationState("jump");
        setJump(true);
        setMinY(getUp() - 64);
    } else if (getType() == 37) //falling enemy
    {
        setSpeed(0);
        setMinY(getUp());
        setMaxY(getDown());
        mStartY = getUp();
    } else if (getType() == 4) //walk left enemy e.g intro tank
    {
        setSpeed(1);
        mStartX = getX();
    } else if (getType() == 8) //end of level flag
    {
        setSpeed(0);
        setMaxY(getDown());
    }
}

/**
 * Draws the image
 * @param g The drawing surface
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Entity::drawImage(SDL_Surface *g, SDL_Surface *img, int x, int y)
{
  	SDL_Rect src, dst;
  	src.x = 0;  src.y = 0;  src.w = img->w;  src.h = img->h;
  	dst.x = x;  dst.y = y;  dst.w =  img->w;  dst.h = img->h;
  	SDL_BlitSurface(img, &src, g, &dst);
}

/**
 * Draws the entity on the graphics surface at the camera position
 * @param g The graphics context
 * @param cam The camera x,y coordinates
 */
void Entity::draw(SDL_Surface* g, Point* cam) {
    if (getAnimationState() != "dead") {
        Sprite::draw(g, cam);
    }

    if(getType() != 4)
    {
        if(mSlowDownTimer > 1)
        {
        	move();
        	mSlowDownTimer = 0;
        }
        else
        	 mSlowDownTimer++;
    }
    else
    	move();

    if (getAnimationState() == "hit" && getAnimationState() != "die") //draw hit enemy with fireball collision = 2
    {
        setSpeed(0);
        if (mHitTimer < getHitTimelimit()) {
            mHitTimer++;
        } else {
            setAnimationState("normal");
            setSpeed(1);
            mHitTimer = 0;
        }
    }

    //set enemy to kick across the screen
    if (getAnimationState() == "kick") {
        mHitTimer = 0;
        setSpeed(4);
    }

    //set enemy as dead after 0.5 seconds  
    if (getAnimationState() == "die" && getType() != 35) {

        if (getType() == 31) //one hit kill enemy
        {
            setSpeed(0);
            if (mHitTimer < 50) {
                mHitTimer++;
            } else {
                setAnimationState("dead");
                mHitTimer = 0;
            }
        }

        if (getType() == 1)//fireball
        {
            setSpeed(0);
            if (mHitTimer < 20) {
                mHitTimer++;
            } else {
                setAnimationState("dead");
            }
        }
    }

    //stop powerup jumping from block
    if ((getType() >= 10 && getType() < 20) && (getType() != 12 && getType() != 14) && (getMaxY() - getDown() > 5)) {
        setJump(false);
        if (getType() != 11) //start moving if not fire powerup or vine
        {
            setSpeed(1);
        }
    }

    //make star jump
    if (getType() == 12) {
        if (getUp() == getMinY()) {
            setJump(false);
        }
    }

    //flying enemy
    if (getType() == 34 && getAnimationState() == "fly") {
        if (getUp() < getMinY()) {
            setJump(false);
            setMaxY(1000);
        }
        if (getDown() >= getMaxY()) {
            setJump(true);
        }
    }

    //draw growing vine under piranha plant
    if (getType() == 14) {
        if (getJump() && getUp() == getMinY()) {
            setJump(false);
            setAnimationState("normal");
            setMaxY(getDown());
        }

        if (mStartY - getDown() + 15 > 5) {
            
			drawImage(g, getFrame(0, 'l', mStartY - getDown() + 15), getX() - cam->x, getDown() - cam->y);
        }

        //draw bounds including growing vine
        if (Settings::DrawBounds) {
            //g.drawRect(getVineBounds().x - cam.x, getVineBounds().y - cam.y - 16, getVineBounds().width, getVineBounds().height);
        }
    }

    if (getType() == 20) {
        if (getAnimationState() == "up" && getFrameIndex() == getFrameCount()) {
            setAnimationState("normal");
        } else if (getAnimationState() ==  "normal" && getFrameIndex() == getFrameCount()) {
            setAnimationState("down");
        } else if (getAnimationState() == "down" && getFrameIndex() == getFrameCount()) {
            setAnimationState("up");
        }
    }

    //reset bullet if it reaches end of screen or is hit
    if (getType() == 35) {
        setMinX(cam->x - 100);

        if (getX() <= getMinX()) {
            setX(mStartX);
        }

        if (getAnimationState() == "die") {
            if (getDown() == getMaxY()) {
                setAnimationState("normal");
                setX(mStartX);
                setY(mStartY);
                setSpeed(2);
                setMaxY(getDown());
            }
        }
    }

    //jump up and down lava ball
    if (getType() == 36) {
        if (getUp() < getMinY() && getJump()) {
            setJump(false);
            setMaxY(1000);
            setY(getMinY() - 50);
            setFrameIndex(0);
            setAnimationState("fall");
        }
        if (getDown() >= getMaxY()) {
            setJump(true);
            setAnimationState("jump");
        }
    }

    //falling block set state to normal and hold in place
    if (getType() == 37) {
        if (getJump() && getUp() == getMinY()) {
            setJump(false);
            setAnimationState("normal");
            setMaxY(getDown());
        }
    }
}

/**
 * Returns a bounding box which includes the growing vine
 * @return The bounding box surrounding the growing vine
 */
Rectangle* Entity::getVineBounds() {
    return new Rectangle(getX() + 6, getDown() + 4, 1, mStartY - getDown() + 25);
}

/**
 * Detects collisions between the entity and map objects
 * @param map The level map
 * @return True if the entity has collided with a map object
 */
bool Entity::detectMapCollision(Map* map) {

    bool collision = false;
    //enemy is rotating as it was hit by invincible player
    if (getRotate() && getType() >= 20) {
        if (getDown() > map->getHeight() + 200) {
            setAnimationState("dead");
        }
        return collision;
    }

    //stop flag from falling
    if (getType() == 8) {
        if (getAnimationState() == "normal") {
            return collision; //exit function skip collision detection
        }
    }

    //stop block from falling 
    if (getType() == 37) {
        if (getAnimationState() != "fall") {
            return collision;
        }
    }

    //jumping enemy lava ball has no collision detection
    if (getType() == 36) {
        setMaxY(map->getHeight() + 200);
        return collision;
    }

    //bullet has no collision detection
    if (getType() == 35) {
        if (getAnimationState() == "normal") {
            return collision;
        } else //if dead set to fall off map
        {
            setMaxY(map->getHeight() + getHeight());
            return collision;
        }
    }

    //down collision
    if (map->getTile(getCenter()->x / 16, getDown() / 16)->collision == 1) {
        setMaxY(getDown() / 16 * 16);
        collision = true;

        //falling block reset to normal and rise to start position
        if (getType() == 37) {
            if (mHitTimer > getHitTimelimit()) {
                setMinY(mStartY);
                setJump(true);
                setAnimationState("normal");
                mHitTimer = 0;
            } else {
                mHitTimer++;
            }
        }

        //fireball
        if (getType() == 1) {
            setMinY(getDown() - 22);
            setJump(true);
        }

        //star 
        if (getType() == 12) {
            setMinY(getDown() - 50);
            setJump(true);
        }

    } else {
        if (getType() != 14) {
            setMaxY(map->getHeight() + 200);
        }
    }

    //up collision
    if (map->getTile(getCenter()->x / 16, getUp() / 16)->collision == 1) {
        //draw growing vine under piranha plant
        if (getType() == 14) {
            setMinY((getUp() / 16 * 16));
            setJump(false);
            collision = true;
            setJump(false);
            setAnimationState("normal");
            setMaxY(getDown());
        } else {
            setMinY(getUp() / 16 * 16);
            setJump(false);
            setMaxY(1000);
            collision = true;
        }
        return true;
    }

    //right collision
    if (map->getTile(getRight() / 16, getUp() / 16)->collision == 1 || //small
            map->getTile(getRight() / 16, getCenter()->y / 16)->collision == 1) //large
    {
        if (getType() == 1) //remove fireball
        {
            setAnimationState("die");
            setMaxY(getDown());
        } else //turn left
        {
            setDirection('l');
            collision = true;
        }
    } else {
        setMaxX(map->getWidth() + getFrameWidth());
    }

    //left collision
    if (map->getTile(getLeft() / 16, getUp() / 16)->collision == 1
            || map->getTile(getLeft() / 16, getCenter()->y / 16)->collision == 1) {
        if (getType() == 1) {
            setAnimationState("die"); //remove fireball
            setMaxY(getDown());
        } else if (getType() == 4) {
            setMinX(getLeft()); //stop intro tank
            setAnimationState("normal");
        } else if (getType() >= 10) //turn right
        {
            setDirection('r');
            collision = true;
        }

    } else {
        if (getType() == 4) {
            setAnimationState("moving"); //start intro tank
        }
        setMinX(-100);
    }

    //change direction if empty or invisible block
    if ((map->getTile(getLeft() / 16, getDown() / 16)->collision == 0 || map->getTile(getLeft() / 16, getDown() / 16)->collision == 2)
            && getAnimationState() != "kick" && getAnimationState() != "fly" && getType() >= 20) {
        setDirection('r');
        collision = true;
    }
    if ((map->getTile(getRight() / 16, getDown() / 16)->collision == 0 || map->getTile(getRight() / 16, getDown() / 16)->collision == 2)
            && getAnimationState() != "kick" && getAnimationState() != "fly" && getType() >= 20) {
        setDirection('l');
        collision = true;
    }
    return collision;
}

Entity::~Entity() {

}
