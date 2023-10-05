#include "EntityCollection.h"

/**
 * Constructs the EntityCollection
 */
EntityCollection::EntityCollection() {
    //entities = new ArrayList<Sprite>();
    for (int i = 0; i < 50; i++) {
        Sprite* s = new Sprite(i, 'l', 0, 0);
		char path[100];
        sprintf (path, "mnt/sd/Dragon Island/spr/%d", i);

		if(!s->loadSpriteSheet(path))
		{
            //s = NULL;
        } else {
            if (s->getType() == 34) {
                s->setAnimationState("fly");
            } else {
                s->setAnimationState("normal");
            }
        }
		mEntities.push_back(s);

    }
}

/**
 * Gets the number of elements in the entity collection
 * @return The size of the entity collection
 */
int EntityCollection::getSize() {
    return mEntities.size();
}

/**
 * Gets a buffered image containing the first frame from the entity sprite
 * @param index The index of the entity in the sprite list
 * @param direction The direction 'l' = left 'r' = right
 * @return A BufferedImage containing the first frame from the entity sprite sheet
 */
SDL_Surface* EntityCollection::getEntity(int index, char direction) {
    return mEntities[index]->getFrame(direction);
}

/**
 * Draws entities which have been defined in the map on the graphics surface using the camera coordinates
 * @param g The graphics context
 * @param map The level map
 * @param cam The camera coordinates
 */
void EntityCollection::draw(SDL_Surface* g, Map* map, Point* cam) {

    for (int i = 1; i < mEntities.size(); i++) {
        //if (mEntities[i] != NULL) {
            mEntities[i]->advanceFrame();
        //}
    }

    //draw objects in the screen area
    for (int x = cam->x / 16 - 10; x < (cam->x + 512) / 16; x++) {
        for (int y = cam->y / 16; y < (cam->y + 300) / 16; y++) {
            Tile* mTile = map->getTile(x, y);
            if (Map::isEnlargeBlock(mTile->tile1, mTile->tile2)) {
                mEntities[10]->drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
            } else if (Map::isFireBlock(mTile->tile1, mTile->tile2)) {
                mEntities[11]->drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
            } else if (Map::isInvincibleBlock(mTile->tile1, mTile->tile2)) {
                mEntities[12]->drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
            } else if (Map::isExtraLifeBlock(mTile->tile1, mTile->tile2)) {
                mEntities[13]->drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
            } else if (Map::isVineBlock(mTile->tile1, mTile->tile2)) {
                mEntities[14]->drawXY(g, cam, 'l', x * 16, (y - 1) * 16);
            } else {
                if (mTile->spriteTile != 0 && mTile->spriteDraw == 1) //sprite
                {
                    mEntities[mTile->spriteTile]->drawXY(g, cam, 'l', x * 16, y * 16);
                }
            }
			//delete mTile;
        }
    }
}

EntityCollection::~EntityCollection()
{
	mEntities.clear();
}

