#include "GameFont.h"

/**
 * Constructs the GameFont
 * @param path The folder containing the fonts sprite sheets
 */
GameFont::GameFont(char* path) {
    char str[100];
    sprintf (str, "mnt/sd/Dragon Island/fnt/%s/0.png", path);	
	mSpriteSheet[0] = new SpriteSheet(str, 16, 16, 40);
    sprintf (str, "mnt/sd/Dragon Island/fnt/%s/1.png", path);	
    mSpriteSheet[1] = new SpriteSheet(str, 16, 16, 40);

    for (int i = 0; i < 2; i++) {   
		sprintf (str, "mnt/sd/Dragon Island/fnt/%s/%d.ini", path, i);  	
		ifstream file(str); 
		string line;
        for (int a = 0; a < 40; a++) 
		{
 			getline(file, line);
			vector<string> v;   
			split(line, ',', v);
			mCharacterWidth[i][a] = atoi(v[1].c_str());
			//cout << mCharacterWidth[i][a];
        }
		file.close();
	}
}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void GameFont::split(const string& s, char c, vector<string>& v) 
{   
	//http://www.blog.highub.com/c-plus-plus/c-parse-split-delimited-string/
	string::size_type i = 0;   
	string::size_type j = s.find(c);   
	while (j != string::npos)
    {      
		v.push_back(s.substr(i, j-i));      
		i = ++j;      
		j = s.find(c, j);      
		if (j == string::npos)         
			v.push_back(s.substr(i, s.length( )));   
	}
}

/**
 * Draws a string of text at the x,y coordinates in a character set
 * @param g - The graphics context
 * @param characterSet - The character set e.g. lowercase or capital letters
 * @param string - The string of text to draw
 * @param x - The x drawing coordinate
 * @param y - The y drawing coordinate
 */
void GameFont::drawString(SDL_Surface* g, int characterSet, string str, int x, int y) {   
	
	int spacing = 0;
    for (int i = 0; i < str.length(); i++) {
        int frameNumber = getLetterFrame(str[i]);
        if (frameNumber != 40) {
            mSpriteSheet[characterSet]->drawFrame(g, x + spacing, y, frameNumber);
			spacing += mCharacterWidth[characterSet][frameNumber] + 1;
        } else {
            spacing += 5;
        }
		delete &frameNumber;
    }
	delete &spacing;

}

/**
 * Draws a string of text at the x,y coordinates detecting the character set
 * @param g - The graphics context
 * @param string - The string of text to draw
 * @param x - The x drawing coordinate
 * @param y - The y drawing coordinate
 */
void GameFont::drawString(SDL_Surface* g, string str, int x, int y) {
    
    int spacing = 0;
    int characterSet = 0;
    for (int i = 0; i < str.length(); i++) {
        int frameNumber = getLetterFrame(str[i]);
        if (islower(str[i])) {
        	characterSet = 0;
        } else {
        	characterSet = 1;
        }
        if (frameNumber != 40) {
            mSpriteSheet[characterSet]->drawFrame(g, x + spacing, y, frameNumber);
            spacing += mCharacterWidth[characterSet][frameNumber] + 1;
        } else {
            spacing += 5;
        }
		delete &frameNumber;
    }
	delete &spacing;
	delete &characterSet;
}

/**
 * Returns the width of text given by the specified string for the font charset in pixels
 * @param string The string to be tested
 * @return The width of the text given by the specified string
 */
int GameFont::getStringWidth(string str) {
    int spacing = 0;
    for (int i = 0; i < str.length(); i++) {
        int frameNumber = getLetterFrame(str[i]);
        if (frameNumber != 40) {
            spacing += mCharacterWidth[0][frameNumber] + 1;
        } else {
            spacing += 5;
        }
		delete &frameNumber;
    }
    return spacing;
	delete &spacing;
}

/**
 * Returns the frame index of a character in the sprite sheet
 * @param letter The character
 * @return The index of the frame in the sprite sheet
 */
int GameFont::getLetterFrame(char letter) {
    letter = toupper(letter);
    switch (letter) {
        case '0': return 0;
        case '1': return 1;
        case '2': return 2;
        case '3': return 3;
        case '4': return 4;
        case '5': return 5;
        case '6': return 6;
        case '7': return 7;
        case '8': return 8;
        case '9': return 9;
        case 'A': return 10;
        case 'B': return 11;
        case 'C': return 12;
        case 'D': return 13;
        case 'E': return 14;
        case 'F': return 15;
        case 'G': return 16;
        case 'H': return 17;
        case 'I': return 18;
        case 'J': return 19;
        case 'K': return 20;
        case 'L': return 21;
        case 'M': return 22;
        case 'N': return 23;
        case 'O': return 24;
        case 'P': return 25;
        case 'Q': return 26;
        case 'R': return 27;
        case 'S': return 28;
        case 'T': return 29;
        case 'U': return 30;
        case 'V': return 31;
        case 'W': return 32;
        case 'X': return 33;
        case 'Y': return 34;
        case 'Z': return 35;
        case '-': return 36;
        case '?': return 37;
        case '!': return 38;
        case '\'': return 39;
        default: return 40;
    }
}

/**
 * Deallocates memory by destroying the GameFont
 */
GameFont::~GameFont()
{
    if(mSpriteSheet[0] != NULL)
        mSpriteSheet[0]->SpriteSheet::~SpriteSheet();
    if(mSpriteSheet[1] != NULL)
        mSpriteSheet[1]->SpriteSheet::~SpriteSheet();
}


