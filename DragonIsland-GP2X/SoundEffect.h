#ifndef __SoundEffect_H__
#define __SoundEffect_H__

#pragma once

#include <SDL/SDL.h>
#include <SDL/SDL_mixer.h>
#include "Settings.h"

#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

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
	SoundEffect::SoundEffect();
	void SoundEffect::play(string filename, int repeat);
	SoundEffect::~SoundEffect();

private:
	/** The file path of the wav sound effect */
    char* mFilePath;
    /** The number of times to loop the sound effectg */
    int mLoop;
	Mix_Chunk* mSound;
};

#endif
