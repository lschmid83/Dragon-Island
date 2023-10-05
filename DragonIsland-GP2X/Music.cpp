#include "Music.h"

Music::Music(char* path)
{
	if(Settings::Music)
	{
		//http://www.kekkai.org/roger/sdl/mixer/
		music = Mix_LoadMUS(path);
		/* This begins playing the music - the first argument is a
		   pointer to Mix_Music structure, and the second is how many
		   times you want it to loop (use -1 for infinite, and 0 to
		   have it just play once) */
		Mix_PlayMusic(music, -1);
	}
}

void Music::stop() 
{
	Mix_HaltMusic(); 
	//Mix_FreeMusic(music); 
}

void Music::pause()
{
	Mix_HaltMusic(); 
    //Mix_Pause(-1);
}

void Music::play()
{
	if(Settings::Music)
		Mix_PlayMusic(music, -1);
}

Music::~Music()
{
	if(music)
		Mix_FreeMusic(music); 	
}
	
	
