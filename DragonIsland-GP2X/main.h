#ifndef __Main_H__
#define __Main_H__

#pragma once

#include "GamePanel.h"
#include "EditorPanel.h"
#include "Music.h"
#include "SoundEffect.h"

/**
 * @class Main
 * @brief This class creates the main application window containing the game and level editor panels
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class main
{
public:
	/** The game panel */
	static GamePanel* mGamePanel;
	/** The level editor panel */
	static EditorPanel* mEditorPanel;
    /** The music */
    static Music* mMusic;
	/** The sound */
	static SoundEffect* mSoundEffect;
	/** Starts the level editor */
	static void main::startEditor();
	/** Stops the level editor */
	static void main::stopEditor();
};

#endif
