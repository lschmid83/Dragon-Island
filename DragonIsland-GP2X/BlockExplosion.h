#ifndef __BlockExplosion_H__
#define __BlockExplosion_H__

/**
 * This class stores the coordinates and direction of a piece of exploding block
 *
 * @version 1.0
 * @modified 21/04/11
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author
 */
class BlockExplosion {
public:
	BlockExplosion::BlockExplosion();
    int x;
    int y;
    int tmr;
    int maxY;
    char direction;
};

#endif
