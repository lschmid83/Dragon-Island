package nsmb_demo1;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;

/**
 * Contains the main game thread which draws player, background, objects and
 * handles keyboard input.
 * <BR><BR>
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class GamePanel extends JPanel implements KeyListener, Runnable {

    private Audio snd;
    private Screen scr;
    private Background bg;
    private Object obj;
    private boolean run;
    private Thread game;
    private int speed;
    private BufferedImage img;
    private Graphics2D sg;
    private Point cam;
    private String state;
    private int world;
    private int level;
    private int num; 
    private int chr;
    private int zoom;
    private int tmr;
    public static Player player[];
    public static Map map;
    
    /**
     * Construct GamePanel 
     */
    public GamePanel() {

        snd = new Audio("");
        scr = new Screen();
        bg = new Background(0, map.width, map.height, null, null);
        obj = new Object("images/obj/");
        player = new Player[2];
        chr = 0;
        this.img = new BufferedImage(480, 272, BufferedImage.TYPE_INT_RGB);
        sg = (Graphics2D) img.getGraphics();     
        cam = new Point(0, 0);
        state = "license";
        speed = 15;
        num = 1;
        world = 1;
        level = 1;
        addKeyListener(this);
        setFocusable(true);
        requestFocus();
    }

    //Start thread
    public void start() {
        game = new Thread(this);
        run = true;
        game.start();
    }

    //Stop thread
    public void stop() {
        run = false;
    }

    //Run thread
    public void run() {
        while (true) {
            if (run) {
                repaint();
            }
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
            }
        }
    }

    public void paintComponent(Graphics g) {
         
        if(Settings.zoom){
            tmr++;
            if(tmr % 3 == 0)
            {
            if(zoom > 0)
                zoom--;
            }
        }
        else
            zoom = 0;
       
        super.paintComponent(g);
        if (state.equals("license")) {
            if (map == null) {
                zoom = 0;
                map = new Map("/map/0.map");
                map.titleMap();
                bg = new Background(map.bg, map.width, map.height, null, null);
            }
            scr.drawLicense(sg);
        } else if (state.equals("title")) {
            if (map == null) {
                if(Settings.zoom)
                zoom=30;
                cam = new Point(0, 0);
                map = new Map("/map/0.map");
                newPlayer();
                map.titleMap();
                sg.setColor(bg.getColor());
                snd.playMusic("audio/snd/0.wav");
            }
            cam = bg.draw(sg, cam.x, cam.y);
            scr.drawTitle(sg);

            for (int i = 1; i >= 0; i--) {
                player[i].groundLevel = 240;
                if (player[i].x + player[i].width > Map.width) {
                    player[i].x = Map.width - player[i].width;
                }
                if (num == 1) {
                    player[1].y = -100;
                }
                player[i].draw(sg, cam.x, cam.y);
            }
        } else if (state.equals("main menu")) {
            scr.drawMenu(sg);
        } else if (state.equals("level open")) {
         
            
        } else if (state.equals("game")) {
            if (map == null) {
                if(Settings.zoom)
                zoom=30;
                cam = new Point(0, 0);
                map = new Map("/map/" + world + "-" + level + ".map");
                newPlayer();
                map.createDemoMap(world);
                bg = new Background(map.bg, map.width, map.height, null, null);
                sg.setColor(bg.getColor());
                snd.playMusic("audio/snd/" + world + ".wav");
            }

            cam.x = player[0].x - 240;
            cam.y = player[0].y - 136;
            cam = bg.draw(sg, cam.x, cam.y);

            obj.drawMap(sg, map, cam.x, cam.y);

            for (int i = 1; i >= 0; i--) {
                if (player[i].x + player[i].width > Map.width) {
                    player[i].x = Map.width - player[i].width;
                }
                if (num == 1) {
                    player[1].x = cam.x;
                    player[1].y = -100;
                }
                else //2 player
                {
                 //keep player 1 in screen area 
                 if (player[1].x < cam.x && player[0].speed>0) {                    
                    player[0].x = player[1].x+241;
                 } 
                 if(player[1].x + player[1].width > cam.x + 480 && 
                    player[0].speed>0) {
                   player[0].x = player[1].x-228;  
                 }
                 
                 //keep player 2 in screen area 
                 if (player[1].x < cam.x && player[1].speed > 0) {                    
                    player[1].x = cam.x;
                 } 
                 if(player[1].x > cam.x + 480 - player[1].width && 
                    player[1].speed > 0) {
                   player[1].x = cam.x + 480 - player[1].width;
                 }                 
                }

                player[i].checkForColision(map);
                player[i].draw(sg, cam.x, cam.y);
            }
            scr.drawStatus(sg, 50, 15, num, chr);
        }

        g.drawImage(img,0-zoom,0-zoom,getWidth()+zoom*2,getHeight()+zoom*2,this);
    }
    //Key pressed events
    public void keyPressed(KeyEvent e) {
        if (!state.equals("game")) {
            scr.keyPressed(e);
        }
        if (e.getKeyCode() == e.VK_ESCAPE) {
            map = null;
            snd.stopMusic();
            newPlayer();

            state = "license";
        }

        if (e.getKeyCode() == e.VK_1) { //1 player
            map = null;
            num = 1;
            if(chr == 0)
                chr=1;
            else
                chr=0;

            newPlayer();
        }
        if (e.getKeyCode() == e.VK_2) { //2 player
            map = null;
            num = 2;
            newPlayer();
        }
        if (e.getKeyCode() == e.VK_3) { //3 debug
            Settings.debug = !Settings.debug;
        }

        if (e.getKeyCode() == e.VK_4) { //world 

            if (world < 8) {
                world++;
            } else {
                world = 1;
            }
            stopMusic();
            map = null;
            newPlayer();
        }
        if (e.getKeyCode() == e.VK_5) {
            if (bg.layers < 3) {
                bg.layers++;
            } else {
                bg.layers = 0;
            }
        }
        if (e.getKeyCode() == e.VK_6) {
            Settings.zoom = !Settings.zoom;
        }        
        if (e.getKeyCode() == e.VK_7) {
            map = null;
            Settings.background = !Settings.background;
        }        
        if (e.getKeyCode() == 61) //+ speed
        {
            if (speed > 1) {
                speed--;
            }
        }
        if (e.getKeyCode() == 45) //- speed
        {
            speed++;
        }
        setControls(e, true);
    }

    //Key released events
    public void keyReleased(KeyEvent e) {
        setControls(e, false);
    }

    public void keyTyped(KeyEvent e) {
    }
    //Set controls for the player
    public void setControls(KeyEvent e, boolean pressed) {
        int keyCode = e.getKeyCode();
    
        //player1
        if (keyCode == e.VK_UP) {
            player[0].jumpPressed = pressed;
        } else if (keyCode == e.VK_LEFT) {
            player[0].leftPressed = pressed;
        } else if (keyCode == e.VK_DOWN) {
            player[0].downPressed = pressed;
        } else if (keyCode == e.VK_RIGHT) {
                player[0].rightPressed = pressed;
        } else if (keyCode == e.VK_SHIFT || keyCode == e.VK_Q) {
                player[0].runPressed = pressed;
        } else if (keyCode == e.VK_SPACE || keyCode == e.VK_W) {
            player[0].jumpPressed = pressed;
        //player2
        } else if (keyCode == e.VK_R) {
            player[1].jumpPressed = pressed;
        } else if (keyCode == e.VK_D) {
            player[1].leftPressed = pressed;
        } else if (keyCode == e.VK_F) {
            player[1].downPressed = pressed;
        } else if (keyCode == e.VK_G) {
            player[1].rightPressed = pressed;
        } else if (keyCode == e.VK_BACK_SLASH) {
            player[1].runPressed = pressed;
        }
    }

    //Create a new player
    public void newPlayer() {
        if(num == 1) //1 player
        {
            if(chr == 0) //mario
                player[0] = new Player("mario", "small", "stand", 3, 0, 0, 'r', 
                                        cam.x + 25, 0);
            else //luigi
                player[0] = new Player("luigi", "small", "stand", 3, 0, 0, 'r', 
                                        cam.x + 25, 0);  
        }
        if(num == 2)
        {
           player[0] = new Player("mario", "small", "stand", 3, 0, 0, 'r', 
                                   cam.x + 25, 0);
        }
        player[1] = new Player("luigi", "small", "stand", 3, 0, 0, 'l', 
                                cam.x + 440, 0);                      
    }

    public void stopMusic() {
        snd.stopMusic();
    }

    public void startMusic() {
        snd.startMusic();
    }

    public String getState() {
        return state;
    }

    public void setState(String new_state) {
        map = null;
        state = new_state;
    }
}    
