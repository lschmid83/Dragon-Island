package com.citex.dragonisland.java.drawing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.applet.Main;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.game.Settings;

/**
 * This class loads a image with an alpha channel.
 * @author Lawrence Schmid.
 */
public class Image extends BufferedImage implements IBufferedImage {

	/** Buffered image with alpha channel. */
    private BufferedImage mBufferedImage;
    
    /** Should the image be flipped horizontally. */
    private boolean mFlipImage;

    /**
     * Initialises an empty image with a width and height.
     * @param width Width of image.
     * @param height Height of image.
     * @param imageType Represents the type of image.
     */
    public Image(int width, int height, int imageType) {
    	
    	super(width, height, imageType);
    	mFlipImage = false;
    }
        
    /**
     * Initialises an image from a file path.
     * @param path File path.
     * @throws IOException 
     */
    public Image(String path) throws IOException {
    	
    	super(1, 1, TYPE_INT_ARGB);
    	mFlipImage = false;
    	InputStream is = null;
    	
        try {
        	
        	// Load image file from resources.
         	is = Main.class.getClassLoader().getResourceAsStream(Settings.ResourcePath + path); 
         	mBufferedImage = ImageIO.read(is);  

        } finally {
        	
        	// Close input stream.
        	if(is != null)
        		is.close();
        }
    }

    /**
     * Returns the image containing an alpha channel
     * @return The image with an alpha channel
     */
    public IBufferedImage getImage() {
        
    	try {
	    	Image img = new Image(mBufferedImage.getWidth(), mBufferedImage.getHeight(), TYPE_INT_ARGB);
	        Graphics2D g = this.createGraphics();
	        g.drawImage(mBufferedImage, 0, 0, null);	
	        img.mBufferedImage = mBufferedImage;
	    	return img;
    	}
    	catch(Exception e) {
    		
    		return null;
    	}
    }
    
    /**
     * Returns a subimage defined by a specified rectangular region. 
     * The returned BufferedImage shares the same data array as the original image. 
	 * @param x - the X coordinate of the upper-left corner of the specified rectangular region
     * @param y - the Y coordinate of the upper-left corner of the specified rectangular region
     * @param w - the width of the specified rectangular region
     * @param h - the height of the specified rectangular region 
     * @return A BufferedImage that is the subimage of this BufferedImage. 
     */
    public IBufferedImage getSubImage(Object gl, int x, int y, int w, int h)
    {
    	try 
    	{
	    	Image img = new Image(w, h, TYPE_INT_ARGB);
	        Graphics2D g = this.createGraphics();
	        g.drawImage(mBufferedImage.getSubimage(x, y, w, h), 0, 0, null);
	        img.mBufferedImage = mBufferedImage.getSubimage(x, y, w, h);
	    	return img;   
    	}
    	catch(Exception e) {
    		
    		return null;
    	}

    }   
       

    /**
     * Returns the width of the sprite
     * @return The width
     */
    public int getWidth() {
        return mBufferedImage.getWidth();
    }

    /**
     * Returns the height of the sprite
     * @return The height
     */
    public int getHeight() {
        return mBufferedImage.getHeight();
    }   
  

    
    /**
     * Draws the sprite at the x,y coordinates.
     * @param g The graphics context.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */    
    public void draw(Object g, float x, float y) {
    	((Graphics)g).drawImage(mBufferedImage, (int)x, (int)y, null);
    }
        
    
    
    /**
     * Draws a tiled image to a set width and height.
     */
    public void draw(Object g, float x, float y, float w, float h) {
        
    	if(w < 0 || h < 0)
    		return;
    	
    	// Loop through width.
    	for(int a = 0; a < w / mBufferedImage.getWidth(); a++) {
    		
    		// Loop through height.
    		for(int b = 0; b < h / mBufferedImage.getHeight(); b++) {
    		
    			int subW = mBufferedImage.getWidth();
    			int subH = mBufferedImage.getHeight();
    			
    			BufferedImage tile = mBufferedImage;
    			
    			// Crop width.
    			if(a == (int)(w / mBufferedImage.getWidth())) {
	    			
    				if(w % (a * mBufferedImage.getWidth()) != 0) {
	    				subW = (int)(w - a * mBufferedImage.getWidth()) + 1;	
	    		    }
    				
        			if(subW < 1) {
        				subW = 1;
        			}
        			
        			tile = mBufferedImage.getSubimage(0, 0, subW, subH);
    			}
    			    			
    			// Crop height.
    			if(b == (int)(h / mBufferedImage.getHeight())) {
	    			
    				if(h % (b * mBufferedImage.getHeight()) != 0) {
	    				subH = (int)(h - b * mBufferedImage.getHeight()) + 1;	
	    		    }
    				
        			if(subH < 1) {
        				subH = 1;
        			}
        			
        			tile = mBufferedImage.getSubimage(0, 0, subW, subH);
    			}
    			
   				((Graphics)g).drawImage(tile, (int)x + a * mBufferedImage.getWidth(), (int)y + b * mBufferedImage.getHeight(), null);   	
    		}

    	}
    
    }
    
    public void draw(Object g, char direction, float x, float y) {
    	
        try {
            if (direction == 'r') //flip image horizontal
            {
                ((Graphics)g).drawImage(mBufferedImage, (int)x + mBufferedImage.getWidth(), (int)y, -mBufferedImage.getWidth(), mBufferedImage.getHeight(), null);
            } else {
            	((Graphics)g).drawImage(mBufferedImage, (int)x, (int)y, null);
            }
        } catch (Exception e) //frame out of range
        {
            
        }   	
    }
    
    
    
    public void flipImage(boolean horizontal) {
        
    	
    	if (horizontal && mFlipImage == false) //flip horizontally
        {
    		// Flip the image horizontally
    		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    	tx.translate(-mBufferedImage.getWidth(null), 0);
	    	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    	mBufferedImage = op.filter(mBufferedImage, null);  		
    		
            mFlipImage = true;
        } else if (!horizontal && mFlipImage == true) //restore to original
        {

    		// Flip the image horizontally
    		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
	    	tx.translate(-mBufferedImage.getWidth(null), 0);
	    	AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	    	mBufferedImage = op.filter(mBufferedImage, null);  	      	
        	
            mFlipImage = false;
        }
        
    }
    
    
    public void draw(Object g, char direction, float angle, float x, float y, float centerX, float centerY) {
    	

        if(direction == 'r') {
        	flipImage(true);	
        }
        else {
        	flipImage(false);		        	
        }    	
                
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(Math.toRadians(angle), centerX, centerY);
        
   	
        try {
          	((Graphics2D)g).drawImage(mBufferedImage, at, null);
        } catch (Exception e) //frame out of range
        {
            
        }   	
    }  
    
 
    /**
     * Destroys the textures and release hardware buffers
     * @param gl The GL context
     */
    public void destroy(GL10 gl) {
 
    }
    
    public Object clone() {
    return null;
    }
    
    public void setVertices(GL10 gl, float[] clip) {
    
    }
}
