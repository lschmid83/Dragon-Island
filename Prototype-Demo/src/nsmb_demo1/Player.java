package nsmb_demo1;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;


/**
 * Draw the player sprite in different states and detect collisions with
 * objects in the map.
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class Player {

    private Sprite gfx;                     //player sprite sheet   
    private int frame, first, last;         //frame limits for animation
    private Point offset = new Point(0, 0); //sprite offset in sheet
    public int x, y;                        //coordinates
    public int dX, dY;                      //drawing coordinates
    public int width, height;               //size of character
    public String character;                //gfx folder
    public String state;                    //animation e.g walk, run
    public String powerup;                  //powerup mushroom, fire
    public char direction;                  //direction
    public int velocity;                    //velocity
    public int speed;                       //player approximate speed 
    public int lives;                       //number of lives
    public int score;                       //score
    public int coins;                       //number of coins
    public int gravity;                     //gravity
    public int startJumpY = 0;              //start coordinate of jump
    public int groundLevel;                 //ground level y
    public int jumpHeight;                  //current jump height
    public int maxJump;                     //max jump height

    //key event flags
    public boolean runPressed = false;
    public boolean jumpPressed = false;
    public boolean firePressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;
    private boolean jumpReleased = true;        
    public boolean hitTwoBlocks = false;
    public boolean colision = false;            //set to true on colision
    public int colisionX = 1,  colisionY = 1; 	//coordinates for last colision 
    public int colisionTimer = 0;		//time since last colision
    public boolean colideLeft = false;
    public boolean colideRight = false;
    public boolean colideUp;
    public boolean colideDown;
    public int up,down,left,right;              //location of player in grid
    private Map map = new Map("");              //level map

    //construct new player with given parameters
    public Player(String new_character, String new_powerup,
            String new_state, int new_lives, int new_score, int new_coins,
            char new_direction, int new_x, int new_y) {
        powerup = new_powerup;
        setCharacter(new_character);
        state = "walk";
        setState(new_state);
        lives = new_lives;
        score = new_score;
        coins = new_coins;
        direction = new_direction;
        maxJump = 65;
        gravity = 6;
        groundLevel = 2000;
        x = new_x;
        y = new_y;
    }

    //set character and load graphics
    public void setCharacter(String new_character) {
        character = new_character;
        gfx = new Sprite("images/chr/"+character+"/"+powerup+".png",32,32);
    }

    public void setPowerup(String new_powerup) {
        powerup = new_powerup;
        gfx = new Sprite("images/chr/"+character+"/"+powerup+".png", 32, 32);
    }

    //get player colision bounds
    public Rectangle getBounds() {
        return new Rectangle(x, y - height,width, height);
    }

    public int getLeft() {
        return x / map.blockSize;
    }

    public int getRight() {
        return (x + width) / map.blockSize;
    }

    public int getUp() {
        return (y - height) / map.blockSize;
    }

    public int getDown() {
        return y / map.blockSize;
    }

    public int getX() {
        return (x + (width / 2)) / map.blockSize;
    }

    public int getY() {
        return (y - (height / 2)) / map.blockSize;
    }

    //set the first and last frame in the sprite sheet
    public void setState(String new_state) {
        if (!state.equals(new_state)) {
            state = new_state;
            if (powerup.equals("small")) {
                width = 11;
                height = 15;
                offset.x = 11;
                offset.y = 10;
                if (state.equals("stand")) {
                    first = 0;
                    last = 33;
                } else if (state.equals("walk")) {
                    first = 34;
                    last = 50;
                } else if (state.equals("jog")) {
                    first = 51;
                    last = 67;
                } else if (state.equals("run")) {
                    first = 68;
                    last = 81;
                } else if (state.equals("jump")) {
                    first = 87;
                    last = 110;
                } else if (state.equals("fall")) {
                    first = 110;
                    last = 124;
                } else if (state.equals("skid")) {
                    first = 125;
                    last = 142;
                } else if (state.equals("crouch")) {
                    first = 143;
                    last = 143;
                    height = 9;
                    offset.y = 16;
                }
            }
            if (frame < first) {
                frame = first;  //start at first frame of animation
            }
        }
    }

    //make player face opposite direction
    private void changeDirection() {
        if (direction == 'l') {
            direction = 'r';
        } else {
            direction = 'l';
        }
    }

    //set players speed 
    private void setPlayerSpeed() {
        //set velocity and screen
        if ((leftPressed || rightPressed) && !(state.equals("skid") ||
                state.equals("crouch"))) {
            if (runPressed) {
                if (velocity < 60) {
                    velocity++;
                }
            } else {
                if (velocity < 39) //fast walking speed
                {
                    velocity++;
                } else {
                    velocity--;    //player is running after button released
                }
            }
        } else {
            if (velocity > 0) {
                velocity -= 3;

            } else {
                velocity = 0;
            }
        }

        if (velocity == 0) //stand
        {
            speed = 0;
        } else if (velocity > 0 && velocity < 20) //walk 1
        {
            speed = 1;
        } else if (velocity >= 20 && velocity < 40) //walk 2
        {
            speed = 2;
        } else if (velocity >= 40 && velocity < 60) //jog
        {
            speed = 3;
        } else if (velocity >= 60) //run
        {
            speed = 4;
        }
        if (state.equals("stand") || state.equals("walk") ||
                state.equals("jog") || state.equals("run")) {
            if (speed == 0) {
                setState("stand");
            } else if (speed == 1) {
                setState("walk");
            } else if (speed == 2) {
                setState("walk");
            } else if (speed == 3) {
                setState("jog");
            } else if (speed == 4) {
                setState("run");
            }
        }

        if (state.equals("skid")) {
            if (speed == 0) {
                setState("walk");
                if (leftPressed || rightPressed) {
                    changeDirection();
                }
            }
        }

    }

    //set players direction
    private void setPlayerDirection() {
        if (!jumpPressed && !state.equals("fall")) {
            jumpReleased = true;
        }
        if (leftPressed && !state.equals("skid")) {
            if (y == groundLevel) {
                if (direction == 'r' && (state.equals("run") || 
                    state.equals("jog"))) {
                    setState("skid");
                    new Audio("audio/sfx/skid.wav").start();
                } else {
                    direction = 'l';
                }
            } else //fall
            {
                direction = 'l';
            }
        }

        if (rightPressed && !state.equals("skid")) {
            if (y == groundLevel) {
                if (direction == 'l' && (state.equals("run") || 
                    state.equals("jog"))) {
                    new Audio("audio/sfx/skid.wav").start();
                    setState("skid");
                } else {
                    direction = 'r';
                }
            } else {
                direction = 'r';
            }
        }

        //duck
        if (state.equals("stand") || state.equals("walk") || state.equals("jog") 
           || state.equals("run") || state.equals("crouch")) {
            if (downPressed && y == groundLevel) {
                setState("crouch");
            } else {
                if (state.equals("crouch")) {
                    setState("stand");
                }
            }
        }

        //jump
        if (jumpPressed && !state.equals("fall")) {
            //startJumpY = y;
            jumpHeight = maxJump;
            if (jumpReleased && y == groundLevel) {
                new Audio("audio/sfx/jump.wav").start();
                if (!downPressed) {
                    setState("jump");
                } else //crouching
                {
                    state = "jump";
                }
            }
            jumpReleased = false;
        } else {
            if (y < groundLevel - maxJump / 2) {
                jumpHeight = maxJump / 2;
            } else {
                jumpHeight = maxJump;
            }
        }
    }

    //moves the player around the screen
    public void setPlayerMovement() {
        if (state.equals("stand") || state.equals("walk") || state.equals("jog") 
            || state.equals("run") || state.equals("skid") || 
            state.equals("crouch")) {
            if (y < groundLevel) {
                y += gravity;
            } else {
                //height = 15;
                startJumpY = y;
                y = groundLevel;
            }
        }

        if (state.equals("jump")) {
            if (y > groundLevel - jumpHeight) {
                y -= gravity - 1;
            } else {
                if (!downPressed) {
                    setState("fall");
                } else //crouching
                {
                    state = "fall";
                }
            }
        }

        if (state.equals("fall")) {
            if (y < groundLevel) //gravity
            {
                height = 25;
                y += gravity;

            } else {
                height = 15;
                startJumpY = y;
                y = groundLevel;
                setState("walk");
            }
        }

        if (direction == 'l' && x - speed > -2) {

            if (!colideLeft) {
                x -= speed;
            }
        } else if (direction == 'r') {
            if (!colideRight) {
                x += speed;
            }
        }

    }

    //draw player on graphics surface
    public void draw(Graphics g, int cX, int cY) {
        setPlayerDirection();
        setPlayerSpeed();
        setPlayerMovement();

        if (colision) {
            if (colisionTimer > 30) {
                map.itemCount[colisionX][colisionY] = 0;

                if (hitTwoBlocks) {
                    map.itemCount[colisionX - 1][colisionY] = 0;
                }
                colision = false;
                colisionTimer = 0;
            }
            colisionTimer++;
        }

        BufferedImage buffer = gfx.draw(frame); //put frame into buffer

        if (frame < last) {
            frame++;
        } else //last frame
        {

            if (state.equals("fall") || state.equals("jump")) {
                frame = last;
            } else if (state.equals("crouch")) {
                frame = last;
            } else if (state.equals("skid")) {
                frame = last - 10; //fix

            } else {
                frame = first;
            }
        }

        dX = x - cX;
        dY = (y - cY);
        dX = dX - offset.x;
        dY = dY - (offset.y + height);

        if (direction == 'r') {
            g.drawImage(buffer, dX, dY, null);
        } else {
            AffineTransform transform = new AffineTransform();
            transform.setToTranslation(dX, dY);
            transform.scale(-1, 1); //mirror image
            transform.translate(-34, 0);
            Graphics2D g2 = (Graphics2D) g; //Graphics2D
            g2.drawImage(buffer, transform, null);
        }

        if (Settings.debug) {
            g.drawRect(getBounds().x - cX, getBounds().y - cY,
                    getBounds().width, getBounds().height);
        }

        //grid position
        up = getUp();        down = getDown();
        left = getLeft();    right = getRight();
    }

    //colision detection
    public void checkForColision(Map new_map) {
        if (up > 0 && new_map != null) //player is on the screen
        {
            map = new_map;

            if (map.object[right][getY()] >= 5) 
            {
                colideRight = true;
            } else {
                colideRight = false;
            }
            if (map.object[left][getY()] >= 5)
            {
                colideLeft = true;
            } else {
                colideLeft = false;
            }
            if (map.object[getX()][up] >= 5) {
                colideUp = true;
            } else {
                colideUp = false;
            }
            if (map.object[getX()][down] >= 5) {
                colideDown = true;
            } else {
                colideDown = false;
            }
            if (map.object[left][up] != 0) {
                if (map.object[left][up] >= 5) {
                    if (colideUp) //hit block
                    {
                        if (state.equals("jump")) {
                            setState("fall");
                        }
                    } else {
                        if (speed < 3 && state.equals("jump")) {
                            this.x = ((left + 1) * 16); //corner
                        }
                    }
                }
                colide(left, up);
            }

            if (map.object[right][up] != 0) {
                if (map.object[right][up] >= 5) {
                    if (colideUp) //block
                    {
                        if (state.equals("jump")) {
                            setState("fall");
                        }
                    } else {
                        if (speed < 3 && state.equals("jump")) {
                            this.x = ((right - 1) * 16) + 3; //corner realign 
                        }
                    }
                }
                colide(right, up);
            }

            if (map.object[right][down] != 0) {
                if (map.object[right][down] != 1 && !state.equals("jump") && 
                    !colideRight) {
                    groundLevel = down * 16;
                } else if (map.object[left][down] == 1 && !state.equals("jump")) 
                {
                    colide(right, down);
                }
            } else //empty map block 
            {
                if (!state.equals("jump")) {
                    if (y < map.height + 16) {
                        if (state.equals("run") || state.equals("jog")) {
                            if (map.object[getX() + 1][down] == 0 || 
                                map.object[getX() - 1][down] == 0) {
                                groundLevel = map.height + 16;
                            }
                        } else {
                            groundLevel = map.height + 16;
                        }

                    } else {
                        new Audio("audio/sfx/death.wav").start();

                        if (lives > 1) {
                            lives--;
                            direction = 'r';
                            coins = 0;
                            x = 20;
                            y = 0;
                        } else {
                            Main.frame.canvas.setState("license");
                        }
                    }
                }
            }

            if (map.object[left][down] != 0) {
                if (map.object[left][down] != 1 && !state.equals("jump") && 
                    !colideLeft) {
                    groundLevel = down * 16;
                } else if (map.object[right][down] == 1 && 
                        !state.equals("jump")) 
                {
                    colide(left, down);
                }
            }
        }
    }

    public void colide(int x, int y) {
        if (map.object[x][y] == 1 && map.state[x][y] == 0) //coin
        {
            addCoin(1);
            map.state[x][y] = 4; //collected
        }
        
        if (colideUp) {

            if (map.state[getX()][y] == 0 && map.itemCount[getX()][y] > 0)
            {
                if (colisionX == getX() && colisionY == y) //same block 
                {
                    if (map.object[getX()][y - 1] == 1) //coin
                    {
                        addCoin(1);
                        map.state[getX()][y - 1] = 4;
                    }

                    if (left != getX() && map.state[left][y] == 0) //2
                    {
                        if (map.object[left][y - 1] == 1) //coin
                        {
                            addCoin(1);
                            map.state[left][y - 1] = 4;
                        }
                        map.itemCount[left][y]--;
                        map.state[left][y] = 1;

                        hitTwoBlocks = true;
                           
                        if (map.itemCount[left][y] >= 0) 
                        {
                            addCoin(1);
                        } else if (map.itemCount[left][y] < -1) //no items 
                        {
                            map.state[left][y] = 2;
                        }
                    } else {
                        hitTwoBlocks = false;
                    }
                    addCoin(1);
                    map.itemCount[getX()][y]--;
                    map.state[getX()][y] = 1;

                    colisionTimer = 0;
                    colision = true;
                } else {
                    map.itemCount[colisionX][colisionY] = 0; //remove items 
                }
            } else if(map.state[getX()][y] == 0 && map.itemCount[getX()][y]<1){
                map.state[getX()][y] = 2;

                if (map.state[left][y] == 0 && map.itemCount[left][y] < 1) 
                {
                    map.state[left][y] = 2;
                }
            }
            colisionX = getX();
            colisionY = y;
        }

        if (colideDown) {
            groundLevel = down * 16; 
        }
    }

    public void addCoin(int amount) {
        new Audio("audio/sfx/coin.wav").start();
        if (coins + amount < 100) {
            addScore(100);
            coins += amount;
        } else {
            coins = 0;
            addLives(1);
        }
    }

    public void addScore(int amount) {
        if (score + amount < 999999) {
            score += amount;
        } else {
            addLives(1);
            score = 0;
        }
    }

    public void addLives(int amount) {
        lives += amount;
    }
}
