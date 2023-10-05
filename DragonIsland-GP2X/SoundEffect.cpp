#include "SoundEffect.h"

SoundEffect::SoundEffect()
{
	   mSound = new Mix_Chunk[16];
	   mSound[0] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/block.wav");
	   mSound[1] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/coin.wav");
	   mSound[2] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/die.wav");
	   mSound[3] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/fire.wav");
	   mSound[4] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/hit.wav");
	   mSound[5] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/jump.wav");
	   mSound[6] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/kill.wav");
	   mSound[7] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/option.wav");
	   mSound[8] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/powerdown.wav");
	   mSound[9] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/powerup.wav");
	   mSound[10] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/select.wav");
	   mSound[11] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/speech.wav");
	   mSound[12] = *Mix_LoadWAV("mnt/sd/Dragon Island/sfx/tank.wav");
}

void SoundEffect::play(string filename, int repeat)
{
	int channel;
	if (Settings::Sound) {
		if (filename == "block.wav")
			channel = Mix_PlayChannel(-1, &mSound[0], repeat);
		else if (filename == "coin.wav")
			channel = Mix_PlayChannel(-1, &mSound[1], repeat);
		else if (filename == "die.wav")
			channel = Mix_PlayChannel(-1, &mSound[2], repeat);
		else if (filename == "fire.wav")
			channel = Mix_PlayChannel(-1, &mSound[3], repeat);
		else if (filename == "hit.wav")
			channel = Mix_PlayChannel(-1, &mSound[4], repeat);
		else if (filename == "jump.wav")
			channel = Mix_PlayChannel(-1, &mSound[5], repeat);
		else if (filename == "kill.wav")
			channel = Mix_PlayChannel(-1, &mSound[6], repeat);
		else if (filename == "option.wav")
			channel = Mix_PlayChannel(-1, &mSound[7], repeat);
		else if (filename == "powerdown.wav")
			channel = Mix_PlayChannel(-1, &mSound[8], repeat);
		else if (filename == "powerup.wav")
			channel = Mix_PlayChannel(-1, &mSound[9], repeat);
		else if (filename == "select.wav")
			channel = Mix_PlayChannel(-1, &mSound[10], repeat);
		else if (filename == "speech.wav")
			channel = Mix_PlayChannel(-1, &mSound[11], repeat);
		else if (filename == "tank.wav")
			channel = Mix_PlayChannel(-1, &mSound[12], repeat);		

		if(channel == -1) {
			fprintf(stderr, "Unable to play WAV file: %s\n", Mix_GetError());
		}
	}

}
