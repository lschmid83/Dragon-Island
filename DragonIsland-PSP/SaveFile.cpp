#include "SaveFile.h"

vector<string> SaveFile::GameName;
int SaveFile::GameFolder;
vector<string> SaveFile::LevelName;

/**
 * Constructs the SaveFile
 */
SaveFile::SaveFile(string path) {
    mPath = path;
    loadFiles();
}

/**
 * Loads all of the save game files
 */
void SaveFile::loadFiles() {
	//mGame = new Game[5];
    for (int i = 0; i < 4; i++) {
        mGame[i] = loadFile(i);
    }
}

/**
 * Deletes a save file from the memory card
 * @param fileNumber The file number to delete
 */
void SaveFile::deleteFile(int fileNumber) {
	char path[100];
    sprintf (path, "%s%d.sav", mPath.c_str(), fileNumber);	
	remove(path);
}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void SaveFile::split(const string& s, char c, vector<string>& v) 
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
 * Loads a save file
 * @param fileNumber The file number
 * @return The game progress data
 */   
Game* SaveFile::loadFile(int fileNumber) {

	char path[100];
    sprintf (path, "%s%d.sav", mPath.c_str(), fileNumber);		
	ifstream file(path); 

	if(file.good())
	{
		string ln;	        
		Game* g = new Game();
		while (file.good())
		{
			getline(file, ln);
	
			vector<string> v;   
			split(ln, ',', v);
	
	        g->Game = atoi(v[0].c_str());
	        g->World = atoi(v[1].c_str());
	        g->Level = atoi(v[2].c_str());
	        g->Character = atoi(v[3].c_str());
	        g->Size = atoi(v[4].c_str());
	        g->Lives = atoi(v[5].c_str());
	        g->Coins = atoi(v[6].c_str());
	        g->Score =atoi(v[7].c_str());
		}
		file.close();
		file.clear();
		return g;
	}
	else {
		return NULL;
	}
}

/**
 * Saves the game progress
 * @param fileNumber The file number
 */  
void SaveFile::saveFile(int fileNumber) {

	char path[100];
    sprintf (path, "%s%d.sav", mPath.c_str(), fileNumber);		
	ofstream file(path); 

	if(file.good())
	{
		file << mGame[fileNumber]->Game << "," << mGame[fileNumber]->World << "," 
             << mGame[fileNumber]->Level << "," << mGame[fileNumber]->Character << "," 
             << mGame[fileNumber]->Size << "," << mGame[fileNumber]->Lives << ","  
             << mGame[fileNumber]->Coins << "," << mGame[fileNumber]->Score << endl; 		
	}
	file.close();
	file.clear();
}

/**
 * Creates a new save game file
 * @return The save game file
 */  
Game* SaveFile::newGame() {
    Game* g = new Game();
    g->Game = 1;
    g->World = 1;
    g->Level = 1;
    g->Character = 1;
    g->Size = 0;
    g->Lives = 3;
    g->Coins = 0;
    g->Score = 0;
    return g;
}

/**
 * Gets the game progress data for a save file
 * @param fileNumber
 * @return The game progress information
 */   
Game* SaveFile::getGame(int fileNumber) {
    return mGame[fileNumber];
}

/**
 * Sets the game progress file data for a save file
 * @param fileNumber The file number
 * @param game The game progress data
 */   
void SaveFile::setGame(int fileNumber, Game* game) {
    mGame[fileNumber] = game;
}

/**
 * Gets the string description of the save game file
 * @param fileNumber The file number
 * @return The string description of the file
 */   
string SaveFile::getDescription(int fileNumber) {
    if (mGame[fileNumber] == NULL) {
        return "Empty Game";
    } else {
		char desc[100];
    	sprintf (desc, "%s %d %d", SaveFile::GameName[mGame[fileNumber]->Game].c_str(), mGame[fileNumber]->World, mGame[fileNumber]->Level);	
        return desc;
    }
}

bool SaveFile::Exists(const string & name){
    if (DIR * dir = opendir(name.c_str())){
	   closedir(dir);
	   return true;
    }
    return false;
}

//name should include the final slash
bool SaveFile::GetAllFiles(const string & name, vector<string> & res, bool recurse, string ext){
    if (DIR * dir = opendir(name.c_str())){
		struct dirent * dp;
	   	if (!(dp = readdir(dir))) return false;  //.
	   	if (!(dp = readdir(dir))) return false;  //..
	   	while((dp = readdir(dir))){		  
			const string str = name + dp->d_name;
		  	if (Exists(str + "/")){
				if(str.substr(str.size()-3, str.size()) == ext) 
				{
					res.push_back(dp->d_name);
				}
		  	}
	   	}
	   	closedir(dir);
	   	return true;
    }
    return false;
}

/**
 * Gets a list of the files at the specified path
 * @param path The file path
 * @return The list of files at the specified path
 */
vector<string> SaveFile::getFileList(string path, string ext) {

	vector<string> list = vector<string>();
    vector<string> files = vector<string>();
    GetAllFiles(path, files, false, ext);
	return files;
}

bool SaveFile::GetAllFolders(const string & name, vector<string> & res, bool recurse){
	//http://www.codeguru.com/forum/showthread.php?t=507714
    if (DIR * dir = opendir(name.c_str())){
		struct dirent * dp;
	   	if (!(dp = readdir(dir))) return false;  //.
	   	if (!(dp = readdir(dir))) return false;  //..
	   	while((dp = readdir(dir))){		  
			const string str = name + dp->d_name;
		  	if (Exists(str + "/")){
					res.push_back(dp->d_name);
		  	}
	   	}
	   	closedir(dir);
	   	return true;
    }
    return false;
}

/**
 * Gets a list of folders at the specified path
 * @param path The file path
 * @return The number of folders at the specified path
 */
vector<string> SaveFile::getFolderList(string path) {
	
	vector<string> list = vector<string>();
    vector<string> files = vector<string>();
    SaveFile::SaveFile::GetAllFolders(path, files, false);
	return files;
}	

/** 
 * Gets the number of files at the specified path
 * @param path The file path
 * @return The number of files at the specified path
 */
int SaveFile::getFileCount(string path, string ext) {
	vector<string>files = getFileList(path, ext); 
    return files.size();
}

/**
 * Gets the number of folders at the specified path
 * @param path The file path
 * @return The number of folders at the specified path
 */
int SaveFile::getFolderCount(string path) {
	vector<string>folders = getFolderList(path); 
    return folders.size();
}

/**
 * Sets the list of levels in the selected game folder
 */
void SaveFile::refreshLevelList() {
    if (GameName[0] != "Empty") {
        LevelName = SaveFile::getFileList("res/lvl/" + GameName[GameFolder] + "/", "lvl");
    }
}

/**
 * Sets the list of game folders in the level folder 
 */
void SaveFile::refreshGameList() {
    GameName = SaveFile::getFolderList("res/lvl/");
    if (GameName.size() == 0) {
		GameName.push_back("Empty");
    }
}

