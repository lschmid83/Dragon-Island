package com.citex.dragonisland.core.drawing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.sprite.SpriteSheet;
import com.citex.dragonisland.core.util.Drawing;

/**
 * GameFont.java
 * This class loads a font character set and draws strings of text.
 * Copyright (C) 2023 Lawrence Schmid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

public class GameFont {

	/** Font sprite sheets. */
    private SpriteSheet[] mSpriteSheet = new SpriteSheet[2]; 
    
    /** Character width. */
    private int[][] mCharacterWidth = new int[2][47];

    /**
     * Initialises a GameFont object.
     * @param gl Graphics context.
     * @param path Folder path of the fonts sprite sheets.
     * @throws IOException 
     */
    public GameFont(Object gl, String path) throws IOException {
    	
    	// Initialise the sprite sheets.
    	mSpriteSheet = new SpriteSheet[2];
    	mSpriteSheet[0] = new SpriteSheet(gl, path + "/0.png", 16, 16, 47);
    	mSpriteSheet[1] = new SpriteSheet(gl, path + "/1.png", 16, 16, 47);

    	// Loop through the character sets. 
        for (int i = 0; i < 2; i++) {        	
            
        	// Initialise input stream.
        	InputStream is;
      		is = MainActivity.mAssetManager.open(Settings.ResourcePath + path + "/" + i + ".ini");

        	// Open the input stream.
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));  
            
        	// Read character width array.
            String value;
            try {
                for (int a = 0; a < 46; a++) {
                    value = br.readLine();
                    mCharacterWidth[i][a] = Integer.parseInt(value.split(",")[1]);
                }
            } catch (Exception e) {
                br.close();
            }
        }
 
    }

    /**
     * Draws a string of text.
     * @param charset - The character set e.g. lower-case or capital letters
     * @param x - The x drawing coordinate
     * @param y - The y drawing coordinate
     */
    public void drawString(Object g, int charset, String str, float x, float y) {
        
    	
    	str = str.replace(',', '`');
    	int spacing = 0;
    	if(str != null) {
	        for (int i = 0; i < str.length(); i++) {
	            int frameNumber = getFrameNumber(str.charAt(i));
	            if (frameNumber != 46) {
	                mSpriteSheet[charset].drawFrame(g, frameNumber, (int)x + spacing, (int)y);
	                spacing += mCharacterWidth[charset][frameNumber] + 1;
	            } else {
	                spacing += 5;
	            }
	        }
    	}
    }

    
    /**
     * Draws a string of text with a background.
     * @param g Graphics context.
     * @param str String of text to draw.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param background Background color.
     */
    public void drawString(Object g, String str, int x, int y, Color background) {
    	
    	// Draw background.
   		Drawing.fillRect(g, x - 1, y -1, getStringWidth(str) + 1, 9);
	
    	// Draw string.
    	drawString(g, str, x, y);
    
    }
    
    /**
     * Draws a string of text.
     * @param g - Graphics context.
     * @param str - String of text to draw.
     * @param x - X coordinate.
     * @param y - Y coordinate.
     */
    public void drawString(Object g, String str, int x, int y) {
        
    	str = str.replace(',', '`');
        int spacing = 0;
        int characterSet = 0;
        for (int i = 0; i < str.length(); i++) {
            int frameNumber = getFrameNumber(str.charAt(i));
            if (Character.isLowerCase(str.charAt(i))) {
            	characterSet = 0;
            } else {
            	characterSet = 1;
            }
            if (frameNumber != 46) {
                mSpriteSheet[characterSet].drawFrame(g, frameNumber, x + spacing, y);
                spacing += mCharacterWidth[characterSet][frameNumber] + 1;
            } else {
                spacing += 5;
            }
        }

    }
    
    /**
     * Draws a character.
     * @param g - Graphics context.
     * @param frameNumber - Frame number of the character.
     * @param x - X coordinate.
     * @param y - Y coordinate.
     */  
    public void drawCharacter(Object g, int charset, int frameNumber, float x, float y) {
           	
    	if (frameNumber != 46)
            mSpriteSheet[charset].drawFrame(g, frameNumber, (int)x, (int)y);
	
    }

    /**
     * Returns the width of text given by the specified string for the font charset in pixels
     * @return The width of the text given by the specified string
     */
    public int getStringWidth(String str) {
        
    	str = str.replace(',', '`');
    	int spacing = 0;
        if(str != null) {
	        for (int i = 0; i < str.length(); i++) {
	            int frameNumber = getFrameNumber(str.charAt(i));
	            if (frameNumber != 46) {
	                spacing += mCharacterWidth[0][frameNumber] + 1;
	            } else {
	                spacing += 5;
	            }
	        }
        }
        return spacing;
    }

    
    /**
     * Gets a character from a frame number.
     * @param frame Frame number.
     * @return Character object
     */
    public char getCharacter(int frame) {
    	
    	 switch (frame) {
	        case 0: return '0';
	        case 1: return '1';
	        case 2: return '2';
	        case 3: return '3';
	        case 4: return '4';
	        case 5: return '5';
	        case 6: return '6';
	        case 7: return '7';
	        case 8: return '8';
	        case 9: return '9';
	        case 10: return 'A';
	        case 11: return 'B';
	        case 12: return 'C';
	        case 13: return 'D';
	        case 14: return 'E';
	        case 15: return 'F';
	        case 16: return 'G';
	        case 17: return 'H';
	        case 18: return 'I';
	        case 19: return 'J';
	        case 20: return 'K';
	        case 21: return 'L';
	        case 22: return 'M';
	        case 23: return 'N';
	        case 24: return 'O';
	        case 25: return 'P';
	        case 26: return 'Q';
	        case 27: return 'R';
	        case 28: return 'S';
	        case 29: return 'T';
	        case 30: return 'U';
	        case 31: return 'V';
	        case 32: return 'W';
	        case 33: return 'X';
	        case 34: return 'Y';
	        case 35: return 'Z';
	        case 36: return '-';
	        case 37: return '?';
	        case 38: return '!';
	        case 39: return '.';
	        case 40: return '@';
	        case 41: return '/';
	        case 42: return '`';
	        case 43: return ':';
	        case 44: return '[';
	        case 45: return ']';
	        default: return 46; 
     }
    	
    }
    
    /**
     * Returns the frame index of a character in the sprite sheet
     * @param letter The character
     * @return The index of the frame in the sprite sheet
     */
    public int getFrameNumber(char letter) {
        letter = Character.toUpperCase(letter);
        switch (letter) {
	        case '0': return 0;
	        case '1': return 1;
	        case '2': return 2;
	        case '3': return 3;
	        case '4': return 4;
	        case '5': return 5;
	        case '6': return 6;
	        case '7': return 7;
	        case '8': return 8;
	        case '9': return 9;
	        case 'A': return 10;
	        case 'B': return 11;
	        case 'C': return 12;
	        case 'D': return 13;
	        case 'E': return 14;
	        case 'F': return 15;
	        case 'G': return 16;
	        case 'H': return 17;
	        case 'I': return 18;
	        case 'J': return 19;
	        case 'K': return 20;
	        case 'L': return 21;
	        case 'M': return 22;
	        case 'N': return 23;
	        case 'O': return 24;
	        case 'P': return 25;
	        case 'Q': return 26;
	        case 'R': return 27;
	        case 'S': return 28;
	        case 'T': return 29;
	        case 'U': return 30;
	        case 'V': return 31;
	        case 'W': return 32;
	        case 'X': return 33;
	        case 'Y': return 34;
	        case 'Z': return 35;
	        case '-': return 36;
	        case '?': return 37;
	        case '!': return 38;
	        case '.': return 39;
	        case '@': return 40;
	        case '/': return 41;
	        case '`': return 42;
	        case ':': return 43;
	        case '[': return 44;
	        case ']': return 45;
	        default: return 46; 
        }
    }
    
    /**
     * Destroys the textures and releases the hardware buffers
     * @param gl The GL context
     */
    public void destroy(GL10 gl)
    {
        mSpriteSheet[0].destroy(gl);
        mSpriteSheet[1].destroy(gl);
    }  
    
}
