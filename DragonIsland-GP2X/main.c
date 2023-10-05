#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "Settings.h"
#include <SDL.h>

#include "main.h"

bool Settings::Fullscreen;

/* GP2X button mapping */
enum MAP_KEY
{
	VK_UP         , // 0
	VK_UP_LEFT    , // 1
	VK_LEFT       , // 2
	VK_DOWN_LEFT  , // 3
	VK_DOWN       , // 4
	VK_DOWN_RIGHT , // 5
	VK_RIGHT      , // 6
	VK_UP_RIGHT   , // 7
	VK_START      , // 8
	VK_SELECT     , // 9
	VK_FL         , // 10
	VK_FR         , // 11
	VK_FA         , // 12
	VK_FB         , // 13
	VK_FX         , // 14
	VK_FY         , // 15
	VK_VOL_UP     , // 16
	VK_VOL_DOWN   , // 17
	VK_TAT          // 18
};

bool mouseDown = false;
/* The screen surface, joystick device */
SDL_Surface* screen = NULL;
SDL_Joystick* joy = NULL;

GamePanel* main::mGamePanel;
EditorPanel* main::mEditorPanel;
SoundEffect* main::mSoundEffect;
Settings* mSettings = NULL;
int frame = 0;

void setControl(SDL_Event e, bool pressed);
string convertInt(int number);

void Terminate(void)
{	
	delete main::mGamePanel;
	delete main::mEditorPanel;
	delete main::mMusic;
	SDL_Quit();
#ifdef GP2X
	chdir("/usr/gp2x");
	execl("/usr/gp2x/gp2xmenu", "/usr/gp2x/gp2xmenu", NULL);
#endif
}

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
    mEditorPanel = NULL;
    Settings::LevelEditor = false;
}

int main (int argc, char *argv[])
{
	int done;

	/* Initialize SDL */
	if (SDL_Init (SDL_INIT_VIDEO | SDL_INIT_AUDIO | SDL_INIT_JOYSTICK) < 0) {
		fprintf (stderr, "Couldn't initialize SDL: %s\n", SDL_GetError ());
		exit (1);
	}
	atexit (Terminate);

	SDL_ShowCursor(SDL_DISABLE);

	/* Set 320x240 16-bits video mode */
	//screen = SDL_SetVideoMode(480, 272, 24, SDL_FULLSCREEN | SDL_HWSURFACE);  //SDL_SetVideoMode (480, 272, 16, SDL_SWSURFACE);
	screen = SDL_SetVideoMode(480, 272, 24, SDL_HWSURFACE);  //SDL_SetVideoMode (480, 272, 16, SDL_SWSURFACE);

	if (screen == NULL) {
		fprintf (stderr, "Couldn't set 320x240x16 video mode: %s\n", SDL_GetError ());
		exit (2);
	}

	/* Check and open joystick device */
	if (SDL_NumJoysticks() > 0) {
		joy = SDL_JoystickOpen(0);
		if(!joy) {
			fprintf (stderr, "Couldn't open joystick 0: %s\n", SDL_GetError ());
		}
	}

	int audio_rate = 22050;
	Uint16 audio_format = AUDIO_S16SYS;
	int audio_channels = 2;
	int audio_buffers = 4096;
	 
	if(Mix_OpenAudio(audio_rate, audio_format, audio_channels, audio_buffers) != 0) {
		fprintf(stderr, "Unable to initialize audio: %s\n", Mix_GetError());
		//exit(1);
	}

	mSettings = new Settings();
	main::mGamePanel = new GamePanel();
	main::mSoundEffect = new SoundEffect();

#ifdef GP2X
	/* Only use GP2X code here */
#endif

#ifdef WIN32
	/* Only use Windows code here */
	SDL_WM_SetCaption("Dragon Island", "Dragon Island");
	SDL_ShowCursor(0);  // Hide the mouse cursor
#endif
	done = 0;
	while (!done)
	{
		if(frame < 100000)
			frame++;
		else
		{
			frame=0;
			SDL_Event event;

			/* Check for events */
			while (SDL_PollEvent (&event))
			{
				switch (event.type)
				{
					case SDL_KEYDOWN:
					{
						/* if press Ctrl + C, terminate program */
						if ( (event.key.keysym.sym == SDLK_c) && (event.key.keysym.mod & (KMOD_LCTRL | KMOD_RCTRL)) )
							done = 1;
						main::mGamePanel->keyPressed(event);
						if (Settings::State == "level editor")
							main::mEditorPanel->keyPressed(event);
						if(event.key.keysym.sym == 292) //F11 (Fullscreen)
						{
							Settings::Fullscreen = !Settings::Fullscreen;
							if(Settings::Fullscreen)
								screen = SDL_SetVideoMode(480, 272, 24, SDL_FULLSCREEN | SDL_HWSURFACE);
							else
								screen = SDL_SetVideoMode(480, 272, 24, SDL_HWSURFACE);	
						}
						//cout << SDL_GetKeyName(event.key.keysym.sym) << endl;
						break;
					}	
					case SDL_KEYUP:
						main::mGamePanel->keyReleased(event);
						if (Settings::State == "level editor")
							main::mEditorPanel->keyReleased(event);
						break;
					case SDL_JOYBUTTONDOWN:
						/* if press Start button, terminate program */
						if ( event.jbutton.button == VK_START )
							done = 1;
						break;
					case SDL_JOYBUTTONUP:
						break;
					case SDL_QUIT:
						done = 1;
						break;
					case SDL_MOUSEMOTION:
					{		
						if(mouseDown)
						{
							main::mGamePanel->mouseDragged(event);						
						}
						else
							main::mGamePanel->mouseMoved(event);
                		break;
					}
					case SDL_MOUSEBUTTONDOWN:
					{
						mouseDown = true;
						main::mGamePanel->mousePressed(event);
						break; 
					}
					case SDL_MOUSEBUTTONUP:
					{
						mouseDown = false;
						main::mGamePanel->mouseReleased(event);
					}
					default:
						break;
	
				}
			}
	
			/* Processing */
			main::mGamePanel->paintComponent(screen);
		
		    SDL_Delay(3); 

			SDL_Flip(screen);
		}
	}

	return 0;
}
