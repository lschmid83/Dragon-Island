#include "Header.h"

/**
 * Default Constructor
 */
Header::Header()
{

}

/**
 * Deallocates memory by destroying the Header
 */
Header::~Header()
{
	delete &Name;
	delete &World;
	delete &Level;
	delete &Area;
	delete &TimeLimit;
//	delete &Bg[0];
///	delete &Bg[1];
//	delete &Bg[2];
	delete &BgColor;
//	delete &BgSpeed[0];
//	delete &BgSpeed[1];
//	delete &BgSpeed[2];
	delete &BgAlign;
	delete &Music;
	delete &Tileset0;
	delete &Tileset16;
	delete &Tileset32;
	delete &Width;
	delete &Height;
	delete &ObjectCount;
	delete &EntityCount;
	delete &StartState;
	delete &StartX;
	delete &StartY;
	delete &EndState;
	delete &EndX;
	delete &EndY;
	delete &EndWorld;
	delete &EndLevel;
	delete &EndArea;
	delete &BonusX;
	delete &BonusY;
	delete &BonusState;
}
