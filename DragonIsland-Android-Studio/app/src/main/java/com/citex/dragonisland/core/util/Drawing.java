package com.citex.dragonisland.core.util;

import javax.microedition.khronos.opengles.GL10;

import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.core.game.GameMode;

import com.citex.dragonisland.core.game.Settings;

/**
 * Drawing.java
 * Helper functions for drawing.
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

public class Drawing {

	/**
	 * Draws a filled rectangle.
	 * @param g Graphics context.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param w Width.
	 * @param h Height.
	 */
	public static void fillRect(Object g, float x, float y, float w, float h) {

		// Calculate the percentage of the screen height.
		x = x / Settings.ScreenWidth;
		y = y / Settings.ScreenHeight;
		w = w / Settings.ScreenWidth;
		h = h / Settings.ScreenHeight;

		// Fill the screen below the transition graphic.
		((GL10)g).glScissor((int)(x * GLSurfaceViewRenderer.Width), (int)GLSurfaceViewRenderer.Height - (int)(y * GLSurfaceViewRenderer.Height) - (int)(h * GLSurfaceViewRenderer.Height), (int)(w * GLSurfaceViewRenderer.Width), (int)(h * GLSurfaceViewRenderer.Height));
		((GL10)g).glEnable(GL10.GL_SCISSOR_TEST);

		((GL10)g).glClearColor(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1.0f);

		((GL10)g).glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // (or whatever buffer you want to clear)
		((GL10)g).glDisable(GL10.GL_SCISSOR_TEST);

	}

	/**
	 * Sets the OpenGL ES screen resolution.
	 * @param gl Graphics context.
	 * @param width Width of graphics surface.
	 * @param height Height of graphics surface.
	 */
	public static void setResolution(GL10 gl, int width, int height) {

		// Set the viewport.
	    gl.glViewport(0, 0, GLSurfaceViewRenderer.Width, GLSurfaceViewRenderer.Height);

        // Set our projection matrix. 
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(height, 0.0f, width, 0.0f, 0.0f, 1.0f);

        // Scale the screen to the resolution.
        gl.glScalef((float)height / width, (float)width / height, 1);
        
        // Rotate the screen so top left is 0,0
        gl.glTranslatef(width, 0.0f, 0.0f);
        gl.glRotatef(-270.0f, 0.0f, 0.0f, 1.0f);
        
        // Enable shader.
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
        gl.glEnable(GL10.GL_TEXTURE_2D);
	}
	
}
