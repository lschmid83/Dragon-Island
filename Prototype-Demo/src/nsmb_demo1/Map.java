package nsmb_demo1;

import java.awt.Color;


/**
 * Store level map information for objects in 2-Dimensional arrays and create
 * demo and title screen maps.
 * <BR><BR>
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class Map {
    public int object[][];  //object
    //0 - empty
    //1 - coin
    //2 - red coin
    //5 - brown brick
    //6 - blue brick
    //7 - yellow ? block
    //8 - blue ? block
    //10 - left
    //11 - right
    //12 - center
    //13 - slope
    public int state[][];   //state of object e.g ? block
    //0 - normal
    //1 - colision
    //2 - hit
    //3 - collected
    public int item[][];    //item hidden in block
    //0 - empty
    //1 - coin
    //2 - mushroom
    //3 - fire
    //4 - giant
    //5 - star
    //6 - 1up
    public int itemCount[][];   //number of items left in block
    public int offset[][];  //colision x,y offset
    public boolean draw[][];    //grid squares which are drawn
    public int terrain; //state of terrain e.g.
    //0 - grass
    //1 - rock
    //2 - desert
    public int time;    //time
    public static int width;    //screnn width
    public static int height;   //screen height
    public int worldNo;	//world number
    public int levelNo;	//level number
    public int blockSize; //block size of map items
    public String path;	//level .map file path
    public int frame;	//draw frame of map objects 1-4
    public Color bgColor;
    public static int bg = 0;
    
    /**
     * Construct Map
     */       
    public Map(String new_path) {

        path = new_path;
        time = 300;
        worldNo = 1;
        levelNo = 1;
        width = 4820;
        height = 600;
        blockSize = 16;

        int w, h;

        w = (width / blockSize) + 10; //allow for 10 extra blocks to stop array index out of range bug

        h = (height / blockSize) + 10;

        object = new int[w][h];
        state = new int[w][h];
        item = new int[w][h];
        itemCount = new int[w][h];
        offset = new int[w][h];
        draw = new boolean[w][h];

    }

    public void titleMap() {
        width = 480;
        height = 272;

        bg = 0;

        Background.layers = 3;
        Background.layerDraw[0] = true;
        Background.layerDraw[1] = true;
        Background.layerDraw[2] = true;

        Background.layerY[0] = 162;
        Background.layerY[1] = 120;
        Background.layerY[2] = 0;

        Background.layerSpeed[0] = 0; //hills

        Background.layerSpeed[1] = 9; //clouds

        Background.layerSpeed[2] = 4; //clouds

        bgColor = new Color(84, 164, 212);
    }

    public void createDemoMap(int worldNo) {
        width = 4820;
        height = 592;

        //Background.noOfLayers = 3;

        bg = worldNo;

        if (bg == 1) //World 1 Hill
        {
            Background.layerDraw[0] = true;
            Background.layerDraw[1] = true;
            Background.layerDraw[2] = true;

            Background.layerY[0] = 478;
            Background.layerY[1] = 430;
            Background.layerY[2] = 320;

            Background.layerSpeed[0] = 0; //hills

            Background.layerSpeed[1] = 4; //clouds

            Background.layerSpeed[2] = 9; //clouds

            bgColor = new Color(84, 164, 212);
        } else if (bg == 2) //World 2 Desert
        {
            Background.layerDraw[0] = true;
            Background.layerDraw[1] = true;
            Background.layerDraw[2] = true;

            Background.layerY[0] = height - 79;
            Background.layerY[1] = height - 116;
            Background.layerY[2] = height - 234;

            Background.layerSpeed[0] = 0; //hills

            Background.layerSpeed[1] = 0; //clouds

            Background.layerSpeed[2] = 0; //clouds

            bgColor = new Color(224, 184, 120);
        } else if (bg == 3) //World 3 Mountain
        {
            Background.layerDraw[0] = true;
            Background.layerDraw[1] = true;
            Background.layerDraw[2] = true;

            Background.layerY[0] = height - 192;
            Background.layerY[1] = height - 282;//443;

            Background.layerY[2] = height - 1000;//350;//238;

            Background.layerSpeed[0] = 0; //hills

            Background.layerSpeed[1] = 15; //clouds

            Background.layerSpeed[2] = 0; //clouds

            bgColor = new Color(88, 152, 148);
        } else if (bg == 4) //World 4 Forest
        {
            Background.layerDraw[0] = true;
            Background.layerDraw[1] = false;
            Background.layerDraw[2] = false;

            Background.layerY[0] = height - 192;
            Background.layerY[1] = height - 1000;//443;

            Background.layerY[2] = height - 1000;//350;//238;

            Background.layerSpeed[0] = 0; //hills
            //Background.layerSpeed[1] = 15; //clouds
            //Background.layerSpeed[2] = 0; //clouds

            bgColor = new Color(68, 104, 96);
        } else if (bg == 5) //World 5 Snow
        {
            Background.layerDraw[0] = true;
            Background.layerDraw[1] = false;
            Background.layerDraw[2] = false;

            Background.layerY[0] = height - 192;
            Background.layerY[1] = height - 1000;//443;

            Background.layerY[2] = height - 1000;//350;//238;

            Background.layerSpeed[0] = 0; //hills
            //Background.layerSpeed[1] = 4; //clouds
            //Background.layerSpeed[2] = 0; //clouds

            bgColor = new Color(80, 124, 152);
        }
        createObjects();
    }

    public void createObjects() {

        //objects in map

        //floor
        for (int x = 0; x < 300; x++) {
            if (x % 14 != 0 || x == 0) {
                object[x][36] = 5;	//brick

                state[x][36] = 0;    //normal

                item[x][36] = 1;	//none

                itemCount[x][36] = 1;
                offset[x][36] = 0;  //normal

                draw[x][36] = true;	//draw

            }

        }

        //floor
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 37; y++) {

                if ((x % 10 != 0) && (y == 32 || y == 29 || y == 25 || y == 21) && x > 5) {
                    if (y == 21 && x < 40) {
                    } else {

                        if (x % 5 != 0) {
                            object[x][y] = 5;	//brick

                            itemCount[x][y] = 1;
                        } else {
                            object[x][y] = 7;	//? block

                            itemCount[x][y] = 10;
                        }

                        if ((x == 3 && y == 10) || (x == 26 && y == 10)) {
                            object[x][y] = 0;
                        }
                        if (x > 50 && x % 7 == 0) {
                            object[x][y] = 0;
                            object[x - 1][y] = 0;
//
                        }

                        state[x][y] = 0;   //normal

                        //if(x % 3 != 0)
                        //	item[x][y] =1;	//brick
                        //else
                        item[x][y] = 1;	//? block


                        offset[x][y] = 0;  //normal

                        draw[x][y] = true;	//draw

                    }
                }


                if ((x % 10 != 0) && x > 40 && x < 110 && y > 15 && y < 19 && x > 5) {
                    object[x][y] = 1;	//coins

                    itemCount[x][y] = 0;


                    if ((x == 3 && y == 10) || (x == 26 && y == 10)) {
                        object[x][y] = 0;
                    }
                    state[x][y] = 0;   //normal

                    //if(x % 3 != 0)
                    //	item[x][y] =1;	//brick
                    //else
                    item[x][y] = 1;	//? block


                    offset[x][y] = 0;  //normal

                    draw[x][y] = true;	//draw

                }


            }

        }


    }

    public void demoLevelMap2() {
        //map settings read from map

        width = 4000;
        height = 592;

        //Background.layerDraw[0] = true;
        //Background.layerDraw[1] = true;
        //Background.layerDraw[2] = false;

        Background.layerY[0] = 478;
        Background.layerY[1] = 331;
        Background.layerY[2] = 0;

        Background.layerSpeed[0] = 0; //hills

        Background.layerSpeed[1] = 5; //clouds

        Background.layerSpeed[2] = 0; //nothing


        //objects in map

        //floor
        for (int x = 0; x < 300; x++) {
            for (int y = 16; y < 17; y++) {
                if (x % 8 != 0 || x == 0) {
                    object[x][y] = 5;	//brick

                    state[x][y] = 0;    //normal

                    item[x][y] = 0;	//none

                    offset[x][y] = 0;  //normal

                    draw[x][y] = true;	//draw

                }
            }
        }


        //floor
        for (int x = 0; x < 300; x++) {
            for (int y = 22; y < 23; y++) {
                if (x % 8 != 0 || x == 0) {
                    object[x][y] = 5;	//brick

                    state[x][y] = 0;    //normal

                    item[x][y] = 0;	//none

                    offset[x][y] = 0;  //normal

                    draw[x][y] = true;	//draw

                }
            }
        }


        //floor
        for (int x = 0; x < 300; x++) {
            for (int y = 28; y < 29; y++) {
                if (x % 8 != 0 || x == 0) {
                    object[x][y] = 5;	//brick

                    state[x][y] = 0;    //normal

                    item[x][y] = 0;	//none

                    offset[x][y] = 0;  //normal

                    draw[x][y] = true;	//draw

                }
            }
        }


        //floor
        for (int x = 0; x < 300; x++) {
            for (int y = 33; y < 34; y++) {
                if (x % 8 != 0 || x == 0) {
                    object[x][y] = 5;	//brick

                    state[x][y] = 0;    //normal

                    item[x][y] = 0;	//none

                    offset[x][y] = 0;  //normal

                    draw[x][y] = true;	//draw

                }
            }
        }

        //level:
        for (int x = 4; x < 126; x++) {
            //coins
            for (int y = 3; y < 8; y += 2) {
                object[x][y] = 1;	//coin

                state[x][y] = 0;   //normal

                item[x][y] = 0;	//none

                offset[x][y] = 0;  //normal

                draw[x][y] = true;	//draw

            }

            //coins
            for (int y = 11; y < 12; y++) {
                if (y == 11) {
                    //object[x][y] = 1;	//coin
                    //state [x][y] = 0;   //normal
                    //item[x] [y] = 1;	//none
                    //offset [x][y] = 0;  //normal
                    //draw [x][y] = true;	//draw
                }
            }

            //bricks
            for (int y = 10; y < 13; y += 2) {
                if ((x % 10 != 0 || y != 10)) {
                    if (x % 5 != 0) {
                        object[x][y] = 5;	//brick

                        itemCount[x][y] = 1;
                    } else {
                        object[x][y] = 7;	//? block

                        itemCount[x][y] = 10;
                    }

                    if ((x == 3 && y == 10) || (x == 26 && y == 10)) {
                        object[x][y] = 0;
                    }
                    state[x][y] = 0;   //normal

                    //if(x % 3 != 0)
                    //	item[x][y] =1;	//brick
                    //else
                    item[x][y] = 1;	//? block
                    offset[x][y] = 0;  //normal
                    draw[x][y] = true;	//draw
                }
            }
        }
    }
}
