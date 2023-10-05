#include "Music.h"

Music::Music(char* path)
{
	//if(Settings::Music)
	//{
		MP3_Init(2);
		MP3_Load(path);
	//}
}

void Music::stop() 
{
	//if(Settings::Music)
	//{
		MP3_Stop();
		//MP3_FreeTune();
	//}
}

void Music::pause()
{
	//if(Settings::Music)
	MP3_Pause();
}

void Music::play()
{
	MP3_Play();
}

Music::~Music()
{
	//if(Settings::Music)
	//{
		//MP3_Stop();
		MP3_FreeTune();
		//MP3_End();
	//}
}
	
	
