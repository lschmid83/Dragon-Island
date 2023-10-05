#ifndef __Control_H__
#define __Control_H__

/**
 * @class Control
 * @brief This class stores keyboard control variables
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Control
{
public:
	Control::Control();
	/** The left key is pressed (left arrow) */
    bool mLeftPressed;
    /** The right key is pressed (right arrow) */
    bool mRightPressed;
    /** The up key is pressed (up arrow)*/
    bool mUpPressed;
    /** The down key is pressed (down arrow) */
    bool mDownPressed;
    /** The run key is pressed (q) */
    bool mRunPressed;
    /** The run key is released (q) */
    bool mRunReleased;
    /** The jump key is pressed (w) */
    bool mJumpPressed;
    /** The jump key is released (w) */
    bool mJumpReleased;
    /** The amount of time the direction key is held */
    int mDirectionHeld;
};

#endif
