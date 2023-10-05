#include "SoundEffect.h"

SoundEffect::SoundEffect()
{
	mSound[0] = oslLoadSoundFileWAV("res/sfx/block.wav", OSL_FMT_NONE);
	mSound[1] = oslLoadSoundFileWAV("res/sfx/coin.wav", OSL_FMT_NONE);
	mSound[2] = oslLoadSoundFileWAV("res/sfx/die.wav", OSL_FMT_NONE);
	mSound[3] = oslLoadSoundFileWAV("res/sfx/fire.wav", OSL_FMT_NONE);
	mSound[4] = oslLoadSoundFileWAV("res/sfx/hit.wav", OSL_FMT_NONE);
	mSound[5] = oslLoadSoundFileWAV("res/sfx/jump.wav", OSL_FMT_NONE);
	mSound[6] = oslLoadSoundFileWAV("res/sfx/kill.wav", OSL_FMT_NONE);
	mSound[7] = oslLoadSoundFileWAV("res/sfx/option.wav", OSL_FMT_NONE);
	mSound[8] = oslLoadSoundFileWAV("res/sfx/powerdown.wav", OSL_FMT_NONE);
	mSound[9] = oslLoadSoundFileWAV("res/sfx/powerup.wav", OSL_FMT_NONE);
	mSound[10] = oslLoadSoundFileWAV("res/sfx/select.wav", OSL_FMT_NONE);
	mSound[11] = oslLoadSoundFileWAV("res/sfx/speech.wav", OSL_FMT_NONE);
	mSound[12] = oslLoadSoundFileWAV("res/sfx/tank.wav", OSL_FMT_NONE);
}

void SoundEffect::play(string filename, int repeat)
{
	if (Settings::Sound) {
		if (filename == "block.wav")
			oslPlaySound(mSound[0], 0);
		else if (filename == "coin.wav")
			oslPlaySound(mSound[1], 0);
		else if (filename == "die.wav")
			oslPlaySound(mSound[2], 0);
		else if (filename == "fire.wav")
			oslPlaySound(mSound[3], 0);
		else if (filename == "hit.wav")
			oslPlaySound(mSound[4], 0);
		else if (filename == "jump.wav")
			oslPlaySound(mSound[5], 1);
		else if (filename == "kill.wav")
			oslPlaySound(mSound[6], 0);
		else if (filename == "option.wav")
			oslPlaySound(mSound[7], 0);
		else if (filename == "powerdown.wav")
			oslPlaySound(mSound[8], 0);
		else if (filename == "powerup.wav")
			oslPlaySound(mSound[9], 0);
		else if (filename == "select.wav")
			oslPlaySound(mSound[10], 0);
		else if (filename == "speech.wav")
			oslPlaySound(mSound[11], 0);
		else if (filename == "tank.wav")
			oslPlaySound(mSound[12], 0);
	}
}
