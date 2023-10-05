package nsmb_demo1;

import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;

/**
 * Load background images from resource and draw with scrolling layers.
 * <BR><BR>
 *
 * The background can be drawn to any width or height with the image tiled to
 * fit the screen size using camera coordinates to set the position. <BR><BR>
 *
 * Three images can be used to represent layers of the background for example
 * a landscape with clouds. Background layers can scroll at different speeds.
 *
 * Read .ini file containing r,g,b colour value.
 * @version 1.0 29/05/09
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class Background {

    private BufferedImage layer[] = new BufferedImage[3];
    private int layerScroll[] = new int[3];
    private int timer;
    private int bg;
    private int width;
    private int height;
    private int cX;
    private int cY;
    private int vs;
    public static Color color;
    public static int layerY[] = new int[3];
    public static int layers;
    public static boolean layerDraw[] = new boolean[3];
    public static int layerSpeed[] = new int[3];
       
    /**
     * Construct Background 
     */
    public Background(int new_bg, int new_width, int new_height, 
                      int new_layerSpeed[], int new_layerY[])
    {
        bg = new_bg;
        timer = 0;
        layers = 3;
        width = new_width;
        height = new_height;
        vs = 0;
         
        try //load background from resource
        {
            URL url = this.getClass().getClassLoader().
                    getResource("images/bgr/" + bg + "/0.bmp"); //layer 0
            layer[0] = ImageIO.read(url);         
            url = this.getClass().getClassLoader().
                    getResource("images/bgr/" + bg + "/1.bmp"); //layer 1
            layer[1] = ImageIO.read(url);          
            url = this.getClass().getClassLoader().
                    getResource("images/bgr/" + bg + "/2.bmp"); //layer 2
            layer[2] = ImageIO.read(url);
                       
            //open bg .cfg
            try {
                InputStream is = this.getClass().getClassLoader().
                        getResourceAsStream("images/bgr/" + bg + "/bg.cfg"); 
                InputStreamReader in = new InputStreamReader (is) ; 
                BufferedReader br = new BufferedReader(in);
                int r = Integer.parseInt(br.readLine());
                int g = Integer.parseInt(br.readLine());
                int b = Integer.parseInt(br.readLine());
                int vs = Integer.parseInt(br.readLine());
                color = new Color(r,g,b);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Background resource not found!");
        }
        
 	if(new_layerSpeed == null) //automatic layer speed
	{
		layerSpeed[0] = 0; layerSpeed[1] = 4; layerSpeed[2] = 2;
	}

	if (new_layerY == null) //automatic layer position
	{
		layerY[0] = height - layer[0].getHeight();
		layerY[1] = layerY[0] - layer[1].getHeight();
		layerY[2] = layerY[1] - layer[2].getHeight();
	} 
        
	layerScroll[0] = 0;
	layerScroll[1] = 0;
	layerScroll[2] = 0;
	timer = 0;              
    }

    //Draw background on graphics object with three scrolling or static layers
    public Point draw(Graphics g, int new_cX, int new_cY) {

       // System.out.println(Main.frame.getWidth());
       // System.out.println(Main.frame.getHeight());
        
	cX = new_cX;
	cY = new_cY;

	if(cX >= width - 480)
		cX = width - 480;
	if(cX <= 0)
		cX = 0;
	if(cY >= height - 272)
		cY = height - 272;
	if(cY <= 0)
		cY = 0;
	
	int scrX = cX / 512;  //get the screen number of the x coordinate
	int scrY = cY / 512;  //get the screen number of the y coordinate
	int offX = cX - (scrX * 512); //bg offset for camera
	int offY = cY - (scrY * 512); 

        g.fillRect(0, 0,480, 272);
        
	//scroll layers with layerSpeed > 0
	timer++;
	for(int i = 0; i < 3; i++)
	{
		//if timer is divisible by the layerSpeed
		if(layerSpeed[i] != 0 && timer % layerSpeed[i] == 0) //layerSpeed!=0 important stop division by 0 
		{
			if(layerScroll[i] < 512)
				layerScroll[i]+=1;  //move layer left 1 px
			else
				layerScroll[i] = 0;
		}
	}

	//horizontal bg scrolling 
	if(vs == 0)
	{
		if(layers > 0)
		{
                    //layer 0
                    g.drawImage(layer[0], -offX - layerScroll[0], layerY[0] - cY, null);
                    g.drawImage(layer[0], -offX - layerScroll[0] + 512, layerY[0] - cY, null);
                    g.drawImage(layer[0], -offX - layerScroll[0] + 1024, layerY[0] - cY, null);                   
		}
		

		if(layers > 1)
		{
                    g.drawImage(layer[1], -offX - layerScroll[1], layerY[1] - cY, null);
                    g.drawImage(layer[1], -offX - layerScroll[1] + 512, layerY[1] - cY, null);
                    g.drawImage(layer[1], -offX - layerScroll[1] + 1024, layerY[1] - cY, null); 
		}

		if(layers > 2)
		{
                    //layer 2
                    g.drawImage(layer[2], -offX - layerScroll[2], layerY[2] - cY, null);
                    g.drawImage(layer[2], -offX - layerScroll[2] + 512, layerY[2] - cY, null);
                    g.drawImage(layer[2], -offX - layerScroll[2] + 1024, layerY[2] - cY, null);
		}
	}
	else
	{
            //layer 0
            g.drawImage(layer[0], -offY - layerScroll[0], layerY[0] - cY, null);
            g.drawImage(layer[0], -offY - layerScroll[0] + 512, layerY[0] - cY, null);
            g.drawImage(layer[0], -offY - layerScroll[0] + 1024, layerY[0] - cY, null);                 
	}
        
        if(!Settings.background){
            g.setColor(Color.black);
            g.fillRect(0, 0,480, 272);                
        }
        
        if(Settings.debug)
            drawGrid(g);
       
	return new Point(cX,cY);        
    }
    
    public Color getColor()
    {
        return color;       
    }
    
    //Draw grid    
    public void drawGrid(Graphics g) {

        for (int i = cX/16; i < cX+480/16; i++) //columns
        {
            g.drawLine((16 * i)-cX, 0, (16 * i)-cX, height);
        }
        for (int i = cY/16; i < cY+272/16; i++) //rows
        {   
            g.drawLine(0, (16 * i)-cY, width, (16 * i)-cY);
        }
    }  
}
