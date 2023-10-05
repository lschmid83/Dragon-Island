#ifndef __SaveFile_H__
#define __SaveFile_H__

#include "Game.h"
#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

#include <sys/types.h>
#include <dirent.h>
#include <errno.h>
#include <vector>
#include <string>
#include <iostream>
#include <dirent.h>
#include <sys/stat.h>


using namespace std;  

/**
 * @class SaveFile
 * @brief This class reads and writes .sav file containing game progress data
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class SaveFile
{
public:
	SaveFile(string new_path);
	void loadFiles();
	void deleteFile(int fileNumber);
	void split(const string& s, char c, vector<string>& v);
	Game* loadFile(int fileNumber);
	void saveFile(int fileNumber);
	Game* newGame();
	Game* getGame(int fileNumber);
	void setGame(int fileNumber, Game* game);
	string getDescription(int fileNumber);
	static int getDir(string dir, vector<string> files);
	static vector<string> getFileList(string path, string ext);
	static vector<string> getFolderList(string path);
	static int getFileCount(string path, string ext);
	static int getFolderCount(string path);
	static void refreshLevelList();
	static void refreshGameList();
	static bool Exists(const string & name);
	static bool GetAllFiles(const string & name, vector<string> & res, bool recurse, string ext);
	static bool GetAllFolders(const string & name, vector<string> & res, bool recurse);

	
	/** The game data */
    Game* mGame[5];
    /** The file path of the save data */
    string mPath;
	/** The list of level names in the game folder */
	static vector<string> LevelName;
	/** The list of names of game folders containing the levels */
	static vector<string> GameName;
	/** The index of the game folder containing the levels */
	static int GameFolder;
};

#endif
