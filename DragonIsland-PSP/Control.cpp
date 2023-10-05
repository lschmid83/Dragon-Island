#include "Control.h"

/**
 * Default Constructor
 */
Control::Control()
{
    mLeftPressed = false;
    mRightPressed = false;
    mUpPressed = false;
    mDownPressed = false;
    mRunPressed = false;
    mRunReleased = true;
    mJumpPressed = false;
    mJumpReleased = true;
    mDirectionHeld = 0;
}
