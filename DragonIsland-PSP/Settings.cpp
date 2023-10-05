#include "Settings.h"

int Settings::JumpHeight;
int Settings::SaveFileNumber;
bool Settings::Fullscreen;

Settings::Settings()
{
	Settings::Music = true;
	Settings::Sound = true;
	Settings::DebugMenu = true;
	Settings::LevelSelect = true;
	Settings::Invincible = false;
	Settings::FreezeTime = false;
	Settings::InfiniteLives = false;
	Settings::Powerups = true;
	Settings::Animation = true;
	Settings::Background = true;
	Settings::RemoveEnemies = false;
	Settings::MainCharacter = 1;
	Settings::JumpHeight = 65;
	Settings::LimitCameraX = 240;
	Settings::LimitCameraY = 136;
	Settings::State = "title";
	Settings::LevelEditor = false;
	Settings::Paused = false;
	Settings::File = new SaveFile("res/sav/");
	Settings::SaveFileNumber = 0;
	Settings::GameSpeed = 10;
	Settings::DrawBounds = false;
	Settings::EditMainGame = true;
	Settings::GameVersion = "1-0";
	Settings::MusicTest = 0;
	Settings::SoundTest = 0;
	Settings::File->SaveFile::getFolderList("res/lvl/");
	Settings::Fullscreen = false;
}
