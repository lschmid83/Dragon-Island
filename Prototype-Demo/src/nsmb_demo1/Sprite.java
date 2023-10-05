package nsmb_demo1;

import java.awt.image.*;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Load sprite sheet from a resource file and extract frames of animation.
 *
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */


public class Sprite {

    private BufferedImage sheet;    //sprite sheet
    private BufferedImage frame[]; //buffered frames
    private int w,h; //frame dimensions
    private int sW,sH; //sheet dimensions
    private int frames; //no. frames in the sheet

    //construct sprite sheet

    public Sprite(String filename, int new_w, int new_h) {
        w = new_w;
        h = new_h;

        try //loading image from jar archive
        {
            URL url = this.getClass().getClassLoader().getResource(filename);
            sheet = ImageIO.read(url);
            sW = sheet.getWidth(null);
            sH = sheet.getHeight(null);

        } catch (Exception e) {
            System.out.println("Sprite resource not found!");
            return;
        }

        while (frames < 1) //loop until java has the size of the sheet 
        {
            frames = (sW / w) * (sH / h); //no. of frames in the sheet

        }
        frame = new BufferedImage[frames];

        for (int i = 0; i < frames; i++) {
            frame[i] = getFrame(i); //store each frame in the array
        }
    }

    //Return a frame from the sprite sheet
    public BufferedImage getFrame(int frame) {
        //calculate the col and row for a frame in the spreadsheet (512)
        int rows = sH / h; //rows 
        int col = frame / rows; //columns 
        int row = frame - (col * rows); //row number 
        return sheet.getSubimage((col * w + (col * 1)) + 1, 
                                ((row * h) + (row * 1) + 1), w, h);
    }

    //Return a frame from buffer
    public BufferedImage draw(int frame) {
        return this.frame[frame]; //return frame from array

    }
}
