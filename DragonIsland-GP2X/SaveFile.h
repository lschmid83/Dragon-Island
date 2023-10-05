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
	SaveFile::SaveFile(string new_path);
	void SaveFile::loadFiles();
	void SaveFile::deleteFile(int fileNumber);
	void SaveFile::split(const string& s, char c, vector<string>& v);
	Game* SaveFile::loadFile(int fileNumber);
	void SaveFile::saveFile(int fileNumber);
	Game* SaveFile::newGame();
	Game* SaveFile::getGame(int fileNumber);
	void SaveFile::setGame(int fileNumber, Game* game);
	string SaveFile::getDescription(int fileNumber);
	static int SaveFile::getDir(string dir, vector<string> files);
	static vector<string> SaveFile::getFileList(string path, string ext);
	static vector<string> SaveFile::getFolderList(string path);
	static int SaveFile::getFileCount(string path, string ext);
	static int SaveFile::getFolderCount(string path);
	static void SaveFile::refreshLevelList();
	static void SaveFile::refreshGameList();
	static bool SaveFile::Exists(const string & name);
	static bool SaveFile::GetAllFiles(const string & name, vector<string> & res, bool recurse, string ext);
	static bool SaveFile::GetAllFolders(const string & name, vector<string> & res, bool recurse);

	
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
