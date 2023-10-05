#include "main.h"

#include "GameFont.h"

using namespace std;  

PSP_MODULE_INFO("Dragon Island", 0, 1, 1);
PSP_MAIN_THREAD_ATTR(THREAD_ATTR_USER | THREAD_ATTR_VFPU);

/** Game settings */
Settings* mSettings;

/**
 * Starts the level editor
 */
void main::startEditor() {
    mEditorPanel = new EditorPanel();
    Settings::LevelEditor = true;
}

/**
 * Stops the level editor
 */
void main::stopEditor() {
	delete mEditorPanel;
    //mEditorPanel = NULL;
    Settings::LevelEditor = false;
}

int main(int argc, char* argv[])
{
	//Initialization
	oslInit(0);
	oslInitGfx(OSL_PF_5650, 1);
	oslInitConsole();
	oslInitAudio();
	oslSetScreenClipping(0, 0, 480, 272);
	scePowerSetClockFrequency(333, 333, 166);
	pspAudioInit();
	//Enable the no-fail feature
	oslSetQuitOnLoadFailure(1);

	mSettings = new Settings();

    Settings::File->refreshGameList();
    Settings::File->refreshLevelList();

	main::mGamePanel = new GamePanel();
	main::mSoundEffect = new SoundEffect();

	while (!osl_quit)
	{
		oslStartDrawing();
		
		char mem[100];
		sprintf (mem, "Memory: %d", oslGetRamStatus().maxAvailable);

		oslReadKeys();

		main::mGamePanel->keyPressed(*osl_keys);		
		main::mGamePanel->paintComponent();
		
		//oslSetTextColor(RGBA(255,0,0,255));
		//oslSetBkColor(RGBA(0,0,0,0));

		//oslPrintf_xy(1,2,mem);
		
		//oslPrintf_xy(1,5,SaveFile::LevelName[0].c_str());
		//oslPrintf_xy(1,5,SaveFile::GameName[SaveFile::GameFolder].c_str());
		//gf->drawString(1, "Hello World", 50, 50);

		oslEndDrawing();
		oslSyncFrame();
	}

	oslEndGfx();
	oslQuit();
	return 0;
}

