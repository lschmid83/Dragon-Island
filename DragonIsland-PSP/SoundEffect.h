#ifndef __SoundEffect_H__
#define __SoundEffect_H__

#pragma once

#include "Settings.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <oslib/oslib.h>

using namespace std;  

/**
 * This class loads and plays the games wav sounds effects
 *
 * @version 1.0
 * @modified 31/8/10
 * @author Lawrence Schmid<BR><BR>
 *
 * Not for duplication or distribution without the permission of the author
 */
class SoundEffect
{
public:
	SoundEffect();
	void play(string filename, int repeat);
	~SoundEffect();

private:
	/** The file path of the wav sound effect */
    char* mFilePath;
    /** The number of times to loop the sound effectg */
    int mLoop;
	OSL_SOUND* mSound[13];
};

#endif
