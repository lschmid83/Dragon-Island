package nsmb_demo1;

import java.awt.*;

/**
 * Store graphics for objects in Sprite class and draw using tile information
 * from the Map. <BR><BR>
 *
 * @version 1.0 20/2/09
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */

public class Object {

    private String path;
    private int frame;
    private int frameTmr;
    private int drawX;
    private int drawY;
    private int camX;
    private int camY;
    private Sprite block;
    private Sprite object;
    private Sprite terrain;

    /**
     * Construct Object
     */
    public Object(String new_path) {
	frame = 0;
	frameTmr = 0;
	path = new_path;
	block = new Sprite(path + "block.png", 16, 16);        
    }

    //Draw animated object on the screen at x,y with offset 
    public void draw(Graphics g, int object, int frame, int x, int y, int offset) {
 	frame = frame + (object * 9);
	
	//position of objects on the map
	x = (x * 16);		
	y = (y * 16) - offset; //offset of block when hit

	drawX = x - camX;
	drawY = y - camY;
        
        g.drawImage(block.draw(frame), drawX, drawY, null);
    }

    public void drawMap(Graphics g, Map map, int new_camX, int new_camY) {
        
 	camX = new_camX;
	camY = new_camY;

	//set animation frame for objects in the scene
	frameTmr++;

	if(frameTmr == 6)
	{
		if(frame < 3)
			frame++;
		else
			frame = 0;
		frameTmr = 0;
	}

	int drawLeft;

	if ((camY / 16) - 1 > 0)
		drawLeft = (camY / 16) - 1;
	else
		drawLeft = (camY / 16);


	for(int x = camX / 16; x < (camX + 512) / 16; x++) // only draw objects in screen area
	{
		for(int y = drawLeft; y < ((camY + 272) / 16) + 2; y++)
		{
			if (map.object[x][y] == 1) //coin
			{
				if(map.state[x][y] != 4)
					draw(g, map.object[x][y], frame, x, y, map.offset[x][y]);
				else if(map.state[x][y] == 4)
				{
					if(map.offset[x][y] < 3)
						map.offset[x][y]++;
					else
					{
						map.offset[x][y] = 0;
						map.state[x][y]  = 0;
						map.object[x][y] = 0; //remove coin from map
					}
					draw(g, map.object[x][y], map.frame + 4, x, y, 0);
				}
				//else if (map.state[x][y] == 2)
				//	draw(g,map.object[x][y], 4, x, y, map.offset[x][y]);

			}

			if (map.object[x][y] >= 5 && map.object[x][y] <= 8) //block / brick
			{
				if(map.state[x][y] == 0) //normal state or hit
				{
					draw(g, map.object[x][y], frame, x, y, map.offset[x][y]);
					//map.object[x][y-1] = 0;
					map.offset[x][y] = 0;

				}
				else if(map.state[x][y] == 1) //colision state
				{
					if (map.item[x][y] == 1) //coin brick
					{
						map.state[x][y-1] = 1;
						map.object[x][y-1] = 1;
						if(map.offset[x][y-1] == 0)
							map.offset[x][y-1] = 3;

						if(map.offset[x][y] < 4)  //make block jump
						{
                                                    
                                                    
                                                  ////  Main.frame.gPanel.player[1].y-=10;
                                                    
                                                    
							map.offset[x][y]++;
							map.offset[x][y-1] ++;
						}
						else
						{
							map.offset[x][y] = 0;
							map.offset[x][y-1] = 3;

							//if(map.item[x][y] == 99)   //empty hit brick
							//	map.state[x][y] = 2;
							if (map.item[x][y] == 1); //contains coin
							{
								map.state[x][y] = 0;
								map.state[x][y-1] = 0;
								map.object[x][y-1] = 0;
							}
						}
					}
					draw(g, map.object[x][y], frame, x, y, map.offset[x][y]);
				}
				else if (map.state[x][y] == 2) //empty e.g. red brick
				{
					draw(g, map.object[x][y], 4, x, y, map.offset[x][y]);
					//map.object[x][y-1] = 0;
					map.offset[x][y] =0;
				}
			}
		}
	}  
    }
}
