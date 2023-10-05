#ifndef __Music_H__
#define __Music_H__

#pragma once

#include "SDL.h"
#include "SDL_mixer.h"
#include "Settings.h"

/**
 * @class Music
 * @brief This class plays mp3 music
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class Music
{
public:
	Music::Music(char* path);
	void Music::stop(); 
	void Music::pause();
	void Music::play();
	Music::~Music();
private:
	/* Mix_Music actually holds the music information.  */
	Mix_Music *music;
};

#endif
