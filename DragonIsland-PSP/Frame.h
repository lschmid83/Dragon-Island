#ifndef __Frame_H__
#define __Frame_H__

#pragma once

#include <iostream>
#include <fstream>
#include <string.h>

using namespace std;  

/**
 * @class Frame
 * @brief This class stores frame information for the animation
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Frame
{
public:
	int Start;  //start frame
	int End;    //end frame
	int bX;     //bounding box coordinates
	int bY;
	int bW;     //bounding box dimensions
	int bH;
	string State;  //animation (walk, run, jump)
	int index;
};

#endif
