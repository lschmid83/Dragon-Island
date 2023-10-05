#include "Level.h"

/**
 * Constructs the Level 
 * @param path the file input path e.g "res/lvl/Main Game/0.0.0.lvl" (title screen)
 */
Level::Level(char* path) {
    loadLevel(path);
} 

/**
 * Default constructor
 */
Level::Level() {
}

/**
 * Returns the level header information
 * @return The level header
 */
Header* Level::getHeader() {
    return mHeader;
}

/**
 * Sets the level header information
 * @param header The level header
 */
void Level::setHeader(Header* header) {
    mHeader = header;
}

/**
 * Returns tile information
 * @param index The index of the tile
 * @return The level tile information
 */
TileDescription Level::getTileDescription(int index) {
    try {
    	return mTiles[index];
    } catch (int e) {
        return *new TileDescription();
   }
}

/**
 * Sets tile information
 * @param index The index of the tile
 * @param obj The level tile information
 */
void Level::setTileDescription(int index, TileDescription obj) {   
	mTiles.erase(mTiles.begin() + index);
    mTiles.insert(mTiles.begin() + index, obj);
}


/**
 * Removes a tile from the level
 * @param index The index of the tile
 */
void Level::removeTile(int index) {
    mTiles.erase(mTiles.begin() + index);
    mHeader->ObjectCount = mTiles.size();
}

/**
 * Adds a tile to the level
 * @param tileIndex The index for the tile
 * @param tilesetNumber The folder containing the tiles 0-block, 16-terrain, 32-scenery
 * @param x The x coordinate
 * @param y The y coordinate
 * @param w The width of the tile
 * @param h The height of the tile
 */
void Level::addTile(int tileIndex, int tilesetNumber, int x, int y, int w, int h) {
    vector<string>desc = getTileStringDescription(tileIndex, tilesetNumber);
    if (desc[2] != "Null") {
        TileDescription* obj = new TileDescription();
        obj->X = x;
        obj->Y = y;
        obj->Tile = tileIndex;
        obj->Tileset = tilesetNumber;
        obj->Name = desc[2];
        obj->TileWidth = atoi(desc[3].c_str());
        obj->TileHeight = atoi(desc[4].c_str());
        obj->Collision = atoi(desc[5].c_str());
        obj->Width = w;
        obj->Height = h;
        if (w < obj->TileWidth) {
            obj->Width = obj->TileWidth;
        }
        if (h < obj->TileHeight) {
            obj->Height = obj->TileHeight;
        }
		mTiles.push_back(*obj);
        mHeader->ObjectCount = mTiles.size();
    }
}

/**
 * Returns a entity description
 * @param index The index of the entity
 * @return The entity description
 */
EntityDescription* Level::getEntityDescription(int index) {
    return &mEntities[index];
}

/**
 * Sets a entity description
 * @param index The index of the entity
 * @param entity The entity description
 */
void Level::setEntityDescription(int index, EntityDescription entity) {
	mEntities.erase(mEntities.begin() + index);
    mEntities.insert(mEntities.begin() + index, entity);  
}

/**
 * Removes a entity
 * @param index The index of the entity
*/
void Level::removeEntity(int index) {   
	mEntities.erase(mEntities.begin() + index);
    mHeader->EntityCount = mEntities.size();
}

/**
 * Adds a entity to the level
 * @param index The image resource index for the sprite sheet
 * @param x The x coordinate
 * @param y The y coordinate
 */
void Level::addEntity(int index, int x, int y) {
    vector<string> desc = getEntityStringDescription(index);
    if (desc[2] != "Null") {
        EntityDescription* spr = new EntityDescription();
        spr = new EntityDescription();
        spr->Tile = index;
        spr->Name = desc[1];
        spr->TileWidth =  atoi(desc[2].c_str());
        spr->TileHeight = atoi(desc[3].c_str());
        spr->X = x;
        spr->Y = y;
        mEntities.push_back(*spr);
        mHeader->EntityCount = mEntities.size();
    }
}

/**
 * Returns the number of tiles in the level
 * @return The number of tiles in the level
 */
int Level::getObjectCount() {
    return mTiles.size();
}

/**
 * Returns the number of entities in the level
 * @return The number of entities in the level
 */
int Level::getEntityCount() {
    return mEntities.size();
}

/**
 * Removes the end of level castle and associated objects and entities
 */
void Level::removeCastle() {
    for (int i = 0; i < mTiles.size(); i++) {
        TileDescription obj = mTiles[i];
        if (obj.Name == "Castle" || obj.Name == "Flag Pole Base" || obj.Name =="Castle Extension") {
            mTiles.erase(mTiles.begin() + i);
            i--; 
        }
    }
    for (int i = 0; i < mEntities.size(); i++) {
        EntityDescription spr = mEntities[i];
        if (spr.Name == "End Flag" || spr.Name == "Castle Flag" || spr.Name == "Flag Pole") {
            mEntities.erase(mEntities.begin() + i);
            i--;
        }
    }
    mHeader->ObjectCount = mTiles.size();
    mHeader->EntityCount = mEntities.size();
}

/**
 * Returns the level map
 * @return The level map containing object and entities
 */
Map* Level::toMap() {
	
	map = new Map(mHeader);	

    for (int i = 0; i < mEntities.size(); i++) {
		map->setEntityTile(i, &mEntities[i]);
    }

    for (int i = 0; i < mTiles.size(); i++) {
   		map->setObjectTile(i, &mTiles[i]);
    }
    return map;
}

/**
 * Loads the level header, tile and entity information
 * @param world The world number
 * @param level The level number
 * @param area The area number
 * @return True if the level exists
 */
bool Level::loadLevel(char* path) {


    bool result = false;
	//delete mHeader;
	//delete &mTiles;
	//delete &mEntities;
    mHeader = new Header();
    //mTiles = new vector<TileDescription>;
    //mEntities = new vector<EntityDescription>;
    //mEntitiyDefinitions = new vector<string>;
    //mTileDefinitions = new vector<string>;

	ifstream file(path); 
	if (file.is_open())
	{

		const int MAXLINE=512;
		char line[MAXLINE];
	
		file.getline(line, MAXLINE);
	    mHeader->World = atoi(line);		
		//cout << mHeader->World << endl;

		file.getline(line, MAXLINE);
	    mHeader->Level = atoi(line);		
		//cout << mHeader->Level << endl;

		file.getline(line, MAXLINE);
	    mHeader->Area = atoi(line);		
		//cout << mHeader->Area << endl;

		char path[100];
        sprintf (path, "World %d-%d Area %d",  mHeader->World, mHeader->Level,  mHeader->Area);
        mHeader->Name = path;
		//cout << mHeader->Name << endl;

		file.getline(line, MAXLINE);
	    mHeader->TimeLimit = atoi(line);		
		//cout << mHeader->TimeLimit << endl;

		file.getline(line, MAXLINE);
	    mHeader->Bg[0] = atoi(line);		
		//cout << mHeader->Bg[0] << endl;
			
		file.getline(line, MAXLINE);
	    mHeader->Bg[1] = atoi(line);		
		//cout << mHeader->Bg[1] << endl;

		file.getline(line, MAXLINE);
	    mHeader->Bg[2] = atoi(line);		
		//cout << mHeader->Bg[2] << endl;

		file.getline(line, MAXLINE);
	    int r = atoi(line);		
		//cout << r << endl;

		file.getline(line, MAXLINE);
	    int g = atoi(line);		
		//cout << g << endl;

		file.getline(line, MAXLINE);
	    int b = atoi(line);		
		//cout << b << endl;

        mHeader->BgColor = *new Color(r, g, b);

		file.getline(line, MAXLINE);
	    mHeader->BgSpeed[0] = atoi(line);		
		//cout << mHeader->BgSpeed[0] << endl;

		file.getline(line, MAXLINE);
	    mHeader->BgSpeed[1] = atoi(line);		
		//cout << mHeader->BgSpeed[1] << endl;

		file.getline(line, MAXLINE);
	    mHeader->BgSpeed[2] = atoi(line);		
		//cout << mHeader->BgSpeed[2] << endl;	

		file.getline(line, MAXLINE);
	    mHeader->BgAlign = atoi(line);		
		//cout << mHeader->BgAlign << endl;

		file.getline(line, MAXLINE);
	    mHeader->Music = atoi(line);		
		//cout << mHeader->Music << endl;

		file.getline(line, MAXLINE);
	    mHeader->Tileset0 = atoi(line);		
		//cout << mHeader->Tileset0 << endl;

		file.getline(line, MAXLINE);
	    mHeader->Tileset16 = atoi(line);		
		//cout << mHeader->Tileset16 << endl;

		file.getline(line, MAXLINE);
	    mHeader->Tileset32 = atoi(line);		
		//cout << mHeader->Tileset32 << endl;

		file.getline(line, MAXLINE);
	    mHeader->Width = atoi(line) * 16;		
		//cout << mHeader->Width << endl;

		file.getline(line, MAXLINE);
	    mHeader->Height = atoi(line) * 16;		
		//cout << mHeader->Height << endl;

		file.getline(line, MAXLINE);
	    mHeader->ObjectCount = atoi(line);		
		//cout << mHeader->ObjectCount << endl;

		file.getline(line, MAXLINE);
	    mHeader->EntityCount = atoi(line);		
		//cout << mHeader->EntityCount << endl;

		file.getline(line, MAXLINE);
	    mHeader->StartState = atoi(line);		
		//cout << mHeader->StartState << endl;	

		file.getline(line, MAXLINE);
	    mHeader->StartX = atoi(line);		
		//cout << mHeader->StartX << endl;	

		file.getline(line, MAXLINE);
	    mHeader->StartY = atoi(line);		
		//cout << mHeader->StartY << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndState = atoi(line);		
		//cout << mHeader->EndState << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndX = atoi(line);		
		//cout << mHeader->EndX << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndY = atoi(line);		
		//cout << mHeader->EndY << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndWorld = atoi(line);		
		//cout << mHeader->EndWorld << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndLevel = atoi(line);		
		//cout << mHeader->EndLevel << endl;	

		file.getline(line, MAXLINE);
	    mHeader->EndArea = atoi(line);		
		//cout << mHeader->EndArea << endl;	

		file.getline(line, MAXLINE);
	    mHeader->BonusX = atoi(line);		
		//cout << mHeader->BonusX << endl;	

		file.getline(line, MAXLINE);
	    mHeader->BonusY = atoi(line);		
		//cout << mHeader->BonusY << endl;	

		file.getline(line, MAXLINE);
	    mHeader->BonusState = atoi(line);		
		//cout << mHeader->BonusState << endl;	

        //other other unused bytes
        for (int i = 0; i < 6; i++) {
            file.getline(line, MAXLINE);
        }
		
		initDefinitions();		
		vector<string> def;
 		
		//read object data
        TileDescription* obj;
        for (int i = 0; i < mHeader->ObjectCount; i++) {
            obj = new TileDescription();
			
			string ln;
			getline(file, ln);

			vector<string> v;   
			split(ln, ',', v);

       		obj->Tile = atoi(v[0].c_str());
			obj->Tileset = atoi(v[1].c_str());

            def = getTileStringDescription(obj->Tile, obj->Tileset);

            obj->Name = def[2];
            obj->TileWidth = atoi(def[3].c_str());
            obj->TileHeight = atoi(def[4].c_str());
            obj->Collision = atoi(def[5].c_str());

			obj->X = atoi(v[2].c_str());
			obj->Y = atoi(v[3].c_str());
			obj->Width = atoi(v[4].c_str());
			obj->Height = atoi(v[5].c_str());
/*
			cout << obj->Tile << "," << 
					obj->Tileset << "," <<
					obj->Name << "," <<
					obj->TileWidth << "," <<
					obj->TileHeight << "," <<
					obj->Collision << "," <<
					obj->X << "," <<
					obj->Y << "," <<
					obj->Width << "," <<
					obj->Height << "," << endl;
*/

            if (def[2] != "Null") {
                mTiles.push_back(*obj);
            }
        }
     
  
		mHeader->ObjectCount = mTiles.size();

  		//read sprite data
        EntityDescription* spr;
        for (int i = 0; i < mHeader->EntityCount; i++) {
            spr = new EntityDescription();

			string ln;
			getline(file, ln);

			vector<string> v;   
			split(ln, ',', v);

       		spr->Tile = atoi(v[0].c_str());

            def = getEntityStringDescription(spr->Tile);
            spr->Name = def[1];
            spr->TileWidth = atoi(def[2].c_str());
            spr->TileHeight = atoi(def[3].c_str());
            spr->Collision = 1;   
       		spr->X = atoi(v[1].c_str());
       		spr->Y = atoi(v[2].c_str());

            if (def[1] != "Null") {
				mEntities.push_back(*spr);
            }
/*
			cout << spr->Tile << "," << 
					spr->Name << "," <<
					spr->TileWidth << "," <<
					spr->TileHeight << "," <<
					spr->Collision << "," <<
					spr->X << "," <<
					spr->Y << "," << endl;
*/
        }
        mHeader->EntityCount = mEntities.size();
		file.close();
		file.clear();

		result = true;
	}
	else
	{
        newLevel();			
	}


    return result;
}

/**
 * Initialises a new level
 */
void Level::newLevel() {
    mHeader->World = 1;
    mHeader->Level = 1;
    mHeader->Area = 1;
    mHeader->EndWorld = mHeader->World;
    mHeader->EndLevel = mHeader->Level + 1;
    mHeader->EndArea = 1;
    mHeader->Bg[0] = 1;
    mHeader->Bg[1] = 1;
    mHeader->Bg[2] = 1;
	mHeader->BgSpeed[0] = 0;
	mHeader->BgSpeed[1] = 0;
	mHeader->BgSpeed[2] = 1;
    mHeader->TimeLimit = 300;
    mHeader->Tileset0 = 1;
    mHeader->Tileset16 = 1;
    mHeader->Tileset32 = 1;
    mHeader->Music = 1;
    mHeader->Width = 480;
    mHeader->Height = 272;
    mHeader->BonusX = 0;
    mHeader->BonusY = 0;
    mHeader->StartX = 2;
    mHeader->StartY = 14;
    mHeader->StartState = 1;
    mHeader->ObjectCount = mTiles.size();
    mHeader->EntityCount = mEntities.size();
    initDefinitions();
}

/**
 * Saves the level header, tile and entity information
 * @param path The file output path
 */
void Level::saveLevel(char* path) {
 	
	ofstream file (path);
	if (file.is_open())
	{
		file << mHeader->World << endl; 
		file << mHeader->Level << endl;
		file << mHeader->Area << endl;
   		file << mHeader->TimeLimit << endl;
        file << mHeader->Bg[0] << endl;
        file << mHeader->Bg[1] << endl;
        file << mHeader->Bg[2] << endl;
        file << mHeader->BgColor.getRed() << endl;
        file << mHeader->BgColor.getGreen() << endl;
        file << mHeader->BgColor.getBlue() << endl;
        file << mHeader->BgSpeed[0] << endl;
        file << mHeader->BgSpeed[1] << endl;
        file << mHeader->BgSpeed[2] << endl;
        file << mHeader->BgAlign << endl;
        file << mHeader->Music << endl;
        file << mHeader->Tileset0 << endl;
        file << mHeader->Tileset16 << endl;
        file << mHeader->Tileset32 << endl;
        file << mHeader->Width / 16 << endl;
        file << mHeader->Height / 16 << endl;
        file << mHeader->ObjectCount << endl;
        file << mHeader->EntityCount << endl;
        file << mHeader->StartState << endl;
        file << mHeader->StartX << endl;
        file << mHeader->StartY << endl;
        file << mHeader->EndState << endl;
        file << mHeader->EndX << endl;
        file << mHeader->EndY << endl;
        file << mHeader->EndWorld << endl;
        file << mHeader->EndLevel << endl;
        file << mHeader->EndArea << endl;
        file << mHeader->BonusX << endl;
        file << mHeader->BonusY << endl;
        file << mHeader->BonusState << endl;
        //other other unused bytes
        for (int i = 0; i < 6; i++) {
        	file << 0 << endl;
        }
        //write object data
        TileDescription obj;
        for (int i = 0; i < mTiles.size(); i++) {
            obj = mTiles[i];
		    file << obj.Tile << "," << obj.Tileset << "," << obj.X << "," << obj.Y << "," << obj.Width << "," << obj.Height << endl;	
        }
        //write sprite data
        EntityDescription spr;
        for (int i = 0; i < mEntities.size(); i++) {
            spr = mEntities[i];
			file << spr.Tile << "," << spr.X << "," << spr.Y << endl;
        }
		file.close();
		file.clear();		
	}
	else 
		cout << "Unable to open file";
}

/**
 * Returns the description string of a tile
 * @param tileIndex The index of tile
 * @param tilesetindex The tileset index 0-block, 16-terrain, 32-scenery
 */
vector<string> Level::getTileStringDescription(int tileIndex, int tilesetIndex) {

    for (int i = 0; i < mTileDefinitions.size(); i++) //array of definitions
    {
        //split str into tile number, description and tile width/height
		vector<string> desc;
		split(mTileDefinitions[i], ',', desc);
        if (atoi(desc[0].c_str()) == tileIndex && atoi(desc[1].c_str()) == tilesetIndex) //tile match
        {
            return desc;
        }
    }    

	vector<string> desc;
    desc.push_back("Null");
    desc.push_back("Null");
    desc.push_back("Null");
    return desc;
}

/**
 * Returns the description string of a entity
 * @param tile The index of the entity
*/
vector<string> Level::getEntityStringDescription(int tile) {

    for (int i = 0; i < mEntitiyDefinitions.size(); i++) {
    	vector<string> desc;
		split(mEntitiyDefinitions[i], ',', desc);
        if (atoi(desc[0].c_str()) == tile) {
            return desc;
        }
    }
	vector<string> desc;
    desc.push_back("Null");
    desc.push_back("Null");
    desc.push_back("Null");
    return desc;

}

string Level::convertInt(int number)
{
   stringstream ss;//create a stringstream
   ss << number;//add number to the stream
   return ss.str();//return a string with the contents of the stream
}

/**
 * Splits a string by a character delimiter (e.g ',' - csv) into a vector of tokens
 * @param s The string to split
 * @param c The delimiter character
 * @param v The vector containing the split string
 */  
void Level::split(const string& s, char c, vector<string>& v) 
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
 * Initialises the object definitions
 */
void Level::initDefinitions() {
       
	//tileset 1
	char path[100];    
	string line;    
    sprintf (path, "mnt/sd/Dragon Island/obj/block/%d/tileset.ini", mHeader->Tileset0);	    
   
	ifstream file(path); 
	string ln;
	
	while (file.good())
	{    
		getline(file, ln);
		mTileDefinitions.push_back(ln);
		//cout << ln << endl;
	}
	file.close();
	file.clear();

	//tileset 2
    sprintf (path, "mnt/sd/Dragon Island/obj/terrain/%d/tileset.ini", mHeader->Tileset0);	    
   	file.open(path);  //http://cboard.cprogramming.com/cplusplus-programming/109982-simple-ifstream-question.html
	
	while (file.good())
	{    
		getline(file, ln);
		mTileDefinitions.push_back(ln);
	}
	file.close();
	file.clear();	


	//tileset 3
    sprintf (path, "mnt/sd/Dragon Island/obj/scenery/%d/tileset.ini", mHeader->Tileset0);	    
   	file.open(path); 
	
	while (file.good())
	{    
		getline(file, ln);
		mTileDefinitions.push_back(ln);
	}
	file.close(); 
	file.clear();   


	//sprite
    sprintf (path, "mnt/sd/Dragon Island/spr/sprite.ini");	    
    file.open(path); 
	
	while (file.good())
	{    
		getline(file, ln);
		mEntitiyDefinitions.push_back(ln);
	}
	file.close();
	file.clear();

}

Level::~Level()
{
	//delete mHeader;
    //delete &mTiles;
    //delete mEntities;
    //delete &mEntitiyDefinitions;
    //delete &mTileDefinitions;	
	//delete &map;
}






