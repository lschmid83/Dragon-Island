package nsmb_demo1;

import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Toolkit.*;


/**
 * Draw the menu and title screen and status information for the player
 * including coins, time and lives.
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */

public class Screen {
    private BufferedImage menu_bg;
    private BufferedImage title;
    private BufferedImage license;
    private Sprite onOff;
    private Sprite theme;
    private Sprite mainMenu[] = new Sprite[7];
    private Sprite number;
    private Sprite icon;
    private int character;
    private int noPlayers;
    private Graphics2D g2;
    private int screen;
    private int option;
    private int timer;
    private float alpha;
    private int time;


    //construct new objects class
    public Screen() {
        try //load graphics
        {
            theme = new Sprite("images/gui/theme.png", 16, 19);
            onOff = new Sprite("images/gui/on off.png", 21, 11);
            mainMenu[0] = new Sprite("images/gui/main.png", 224, 35);
            mainMenu[1] = new Sprite("images/gui/file.png", 224, 35);
            mainMenu[2] = new Sprite("images/gui/erase.png", 224, 35);
            mainMenu[4] = new Sprite("images/gui/options.png", 224, 35);
            number = new Sprite("images/img/numbers.png", 10, 17);
            icon = new Sprite("images/img/status.png", 50, 17);

            URL url = this.getClass().getClassLoader().
                    getResource("images/gui/license.png"); //get image path

            license = ImageIO.read(url);

            url = this.getClass().getClassLoader().
                    getResource("images/gui/menu bg.png");
            menu_bg = ImageIO.read(url);

            url = this.getClass().getClassLoader().
                    getResource("images/gui/title.png");
            title = ImageIO.read(url);

        } catch (Exception e) {
            System.out.println("Screen gfx resource not found!");
            return;
        }

    }


    //draw players lives
    public void drawLives(Graphics g, int playerNo, int x, int y) {
        
        int tmp = Main.frame.canvas.player[playerNo].lives;
        int digit[] = new int[2];

        for (int i = 1; i >= 0; i--) {
            digit[i] = tmp % 10;   //put digits into array
            tmp = tmp / 10;
        }

        if(noPlayers==1)
            g.drawImage(icon.draw(character), x, y, null);
        else
            g.drawImage(icon.draw(playerNo), x, y, null); 
        g.drawImage(number.draw(digit[0]), x + 28, y, null);
        g.drawImage(number.draw(digit[1]), x + 36, y, null);
    }

    //draw players coins
    public void drawCoins(Graphics g, int playerNo, int x, int y) {
        int tmp = GamePanel.player[playerNo].coins;
        int digit[] = new int[2];

        for (int i = 1; i >= 0; i--) {
            digit[i] = tmp % 10;   //put digits into array

            tmp = tmp / 10;
        }

        g.drawImage(icon.draw(2), x, y, null);
        g.drawImage(number.draw(digit[0]), x + 28, y, null);
        g.drawImage(number.draw(digit[1]), x + 36, y, null);
    }

    //draw players score
    public void drawScore(Graphics g, int playerNo, int x, int y) {
        int tmp = GamePanel.player[playerNo].score;
        int digit[] = new int[8];

        for (int i = 7; i >= 0; i--) {
            digit[i] = tmp % 10;   //put digits into array
            tmp = tmp / 10;
        }

        g.drawImage(icon.draw(3), x, y, null);
        g.drawImage(number.draw(digit[0]), x + 50, y, null);
        g.drawImage(number.draw(digit[1]), x + 58, y, null);
        g.drawImage(number.draw(digit[2]), x + 66, y, null);
        g.drawImage(number.draw(digit[3]), x + 74, y, null);
        g.drawImage(number.draw(digit[4]), x + 82, y, null);
        g.drawImage(number.draw(digit[5]), x + 90, y, null);
        g.drawImage(number.draw(digit[6]), x + 98, y, null);
        g.drawImage(number.draw(digit[7]), x + 106, y, null);

    }

    //draw remaining time
    public void drawTime(Graphics g, int x, int y) {
        int tmp = time;
        int digit[] = new int[3];

        for (int i = 2; i >= 0; i--) {
            digit[i] = tmp % 10;   //put digits into array

            tmp = tmp / 10;
        }

        g.drawImage(icon.draw(4), x, y, null);
        g.drawImage(number.draw(digit[0]), x + 14, y, null);
        g.drawImage(number.draw(digit[1]), x + 22, y, null);
        g.drawImage(number.draw(digit[2]), x + 30, y, null);

    }

    //draw status information on graphics surface
    public void drawStatus(Graphics g, int x, int y, int num, int chr) {
        timer++;
        character = chr;
        noPlayers = num;

        if (timer % 70 == 0) {
            if (time > 0) {
                time--;
            } else {
                time = 300;
            }
            timer = 0;
        }

        if (num == 1) { 
            drawLives(g, 0, x, y);
            drawCoins(g, 0, x + 90, y);
            drawScore(g, 0, x + 185, y);
            drawTime(g, x + 345, y);
        } else {
            drawLives(g, 0, x, y);
            drawCoins(g, 0, x + 60, y);
            drawTime(g, x + 170, y);
            drawLives(g, 1, x + 270, y);
            drawCoins(g, 1, x + 330, y);
        }
    }

    //draw license screen
    public void drawLicense(Graphics g) {
        timer++;

        g2 = (Graphics2D) g;

        if (timer == 10) {
            new Audio("audio/sfx/coin.wav").start();
        }
        if (timer < 75) {
            screenFadeIn(g2, 0.05f);
            g2.drawImage(license, 0, 0, null);
        } else if (timer < 110) {
            screenFadeOut(g2);
            g2.drawImage(license, 0, 0, null);
        } else {
            alpha = 1f;
            GamePanel.map = null;
            Main.frame.canvas.setState("title");
            screenFadeIn((Graphics2D) g, 0.05f);
        }
    }


    //draw license screen
    public void drawTitle(Graphics g) {
        if (timer < 480) {
            timer++;
        } else {
            timer = 0;
        }
        g.drawImage(title, (480 / 2) - (title.getWidth() / 2), 20, null);


    }

    //draw license screen
    public void drawMenu(Graphics g) {
        int x = 120;
        int y = 75;

        timer++;

        screenFadeIn((Graphics2D) g, 0.05f);

        g.drawImage(menu_bg, 0, 0, null);
        g.drawImage(mainMenu[screen].draw(12), 0, 0, x, 35, null); //heading 

        g.drawImage(mainMenu[screen].draw(13), x, 0, null);
        g.drawImage(mainMenu[screen].draw(12), x + 224, 0, null);

        for (int i = 0; i < 4; i++) {
            if (option == i) {
                g.drawImage(mainMenu[screen].draw((i * 3) + 1),x,y+(i*40),null);

            } else {
                g.drawImage(mainMenu[screen].draw(i*3), x, y+(i*40), null); 

            }
        }

        if (screen == 4) //options
        {
            if (Settings.autosave) {
                g.drawImage(onOff.draw(0), x + 185, y + 11, null);
            } else {
                g.drawImage(onOff.draw(1), x + 185, y + 11, null);
            }
            g.drawImage(theme.draw(Settings.theme), x + 187, y + 47, null);

            if (Settings.music) {
                g.drawImage(onOff.draw(0), x + 185, y + 91, null);
            } else {
                g.drawImage(onOff.draw(1), x + 185, y + 91, null);
            }
            if (Settings.sound) {
                g.drawImage(onOff.draw(0), x + 185, y + 131, null);
            } else {
                g.drawImage(onOff.draw(1), x + 185, y + 131, null);
            }
        }

    }

    public void screenFadeIn(Graphics2D g2, float speed) {
        if (alpha + speed <= 1.0f) {
            alpha += speed;
        }

        int rule = AlphaComposite.SRC_OVER;
        AlphaComposite ac = AlphaComposite.getInstance(rule, alpha);
        g2.setComposite(ac);

    }

    public void screenFadeOut(Graphics2D g2) {
        g2.clearRect(0, 0, 480, 272); 	//clear rectangle

        if (alpha - 0.03f > 0f) {
            alpha -= 0.03f;
        } else {
            alpha = 0f;
        }
        int rule = AlphaComposite.SRC_OVER;
        AlphaComposite ac = AlphaComposite.getInstance(rule, alpha);
        g2.setComposite(ac);

    }

    //Key pressed events
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (Main.frame.canvas.getState().equals("title")) {
            if (e.getKeyCode() == e.VK_ENTER) {
                alpha = 0f;
                screen = 0;
                option = 0;
                Main.frame.canvas.setState("main menu");
            }
            if (e.getKeyCode() == e.VK_ESCAPE) {
                Main.frame.canvas.stopMusic();
                timer = 0;
                Main.frame.canvas.setState("license");

            }

        } else if (Main.frame.canvas.getState().equals("main menu")) {
            if (e.getKeyCode() == e.VK_ENTER || e.getKeyCode() == e.VK_SPACE) {
                new Audio("audio/sfx/select.wav").start();

                if (screen == 0) //main menu
                {
                    alpha = 1;
                    screen = option + 1;
                    option = 0;
                } else {
                    alpha = 1;
                    Main.frame.canvas.stopMusic();
                    GamePanel.player[0].x = 20;
                    GamePanel.player[0].y = 0;
                    GamePanel.player[0].lives = 3;
                    GamePanel.player[1].lives = 3;
                    Main.frame.canvas.setState("game");
                }
            }

            if (e.getKeyCode() == e.VK_UP) {
                if (option > 0) {
                    option--;
                    new Audio("audio/sfx/option.wav").start();
                }
            }

            if (e.getKeyCode() == e.VK_DOWN) {
                if (option < 3) {
                    option++;
                    new Audio("audio/sfx/option.wav").start();
                }
            }


            if (e.getKeyCode() == e.VK_LEFT) {
                if (screen == 4) //options screen
                {
                    if (option == 1) //graphics
                    {
                        if (Settings.theme > 0) {
                            Settings.theme--;
                            new Audio("audio/sfx/option.wav").start();
                        }
                    }
                }

            }


            if (e.getKeyCode() == e.VK_RIGHT) {
                if (screen == 4) //options screen
                {
                    if (option == 1) //graphics
                    {
                        if (Settings.theme < 2) {
                            Settings.theme++;
                            new Audio("audio/sfx/option.wav").start();
                        }
                    }
                }

            }

            if (e.getKeyCode() == e.VK_RIGHT || e.getKeyCode() == e.VK_LEFT) {
                if (screen == 4) {
                    if (option == 0) {
                        Settings.autosave = !Settings.autosave;
                    }
                    if (option == 2) {

                        if (Settings.music) {
                            Main.frame.canvas.stopMusic();
                            Settings.music = false;
                        } else {
                            Main.frame.canvas.startMusic();
                            Settings.music = true;
                        }
                    }
                    if (option == 3) {
                        Settings.sound = !Settings.sound;
                    }
                    new Audio("audio/sfx/option.wav").start();
                }
            }

            if (e.getKeyCode() == e.VK_ESCAPE) {
                if (screen != 0) {
                    screen = 0;
                } else {
                    alpha = 1f;
                    Main.frame.canvas.setState("title");
                    screenFadeIn(g2, 0.05f);
                }

                option = 0;
            }
        } else if (Main.frame.canvas.getState().equals("game")) {
            if (e.getKeyCode() == e.VK_ESCAPE) {
                Main.frame.canvas.stopMusic();
                Main.frame.canvas.setState("title");

            }
        }
    }
}
