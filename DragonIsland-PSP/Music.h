#ifndef __Music_H__
#define __Music_H__

#pragma once

#include "Settings.h"
#include "mp3player.h"

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
	Music(char* path);
	void stop(); 
	void pause();
	void play();
	~Music();
private:
	/* Mix_Music actually holds the music information.  */
	//Mix_Music *music;
};

#endif
