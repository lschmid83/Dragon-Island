#ifndef __ScriptCommand_H__
#define __ScriptCommand_H__

#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>

using namespace std;  

/**
 * @class ScriptCommand
 * @brief This class stores level script commands
 * @version 1.0
 * @author Lawrence Schmid<BR><BR>
 * Not for duplication or distribution without the permission of the author
 */
class ScriptCommand
{
public:
	int time;
	string command;
	string text;
	int x;
	int y;
	int width;
	int height;
	char direction;
	int timer;
};

#endif
