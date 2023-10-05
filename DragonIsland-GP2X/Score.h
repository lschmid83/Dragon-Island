#ifndef __Score_H__
#define __Score_H__

/**
 * @class Score
 * @brief This class stores information about the points displayed on the screen
 * when the player destroys an enemy or collects a powerup
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Score
{
public:
	Score::Score(int x, int y, int value);
	/** The x coordinate */
	int x;
	/** The y coordinate */
	int y;
	/** The value */
	int value;
	/** The time elapsed since the score was displayed */
	int timer;
};

#endif
