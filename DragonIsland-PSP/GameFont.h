#ifndef __GameFont_H__
#define __GameFont_H__

#pragma once

#include "SpriteSheet.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <stdio.h> //http://www.cplusplus.com/reference/clibrary/cctype/islower/
#include <ctype.h>


using namespace std;  

/**
 * @class GameFont
 * @brief This class loads two sprite sheets for a characters set and draws text
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class GameFont
{
public:
	GameFont(char* path);
	void split(const string& s, char c, vector<string>& v);
	void drawString(int characterSet, string str, int x, int y);
	void drawString(string str, int x, int y);
	int getStringWidth(string str);
	int getLetterFrame(char letter);
	char getFrameLetter(int frame);
	~GameFont();

private:
	/** The sprite sheets containing the image data */
    SpriteSheet* mSpriteSheet[2];
    /** The character width of each letter */
    int mCharacterWidth[2][40];
};

#endif
