package com.citex.dragonisland.android.drawing;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL10;

import android.view.KeyEvent;

import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.thread.Main;
import com.citex.dragonisland.core.util.Drawing;

/**
 * An OpenGL ES renderer based on the GLSurfaceView rendering framework. This
 * class is responsible for drawing a list of renderables to the screen every
 * frame. It also manages loading of textures and (when VBOs are used) the
 * allocation of vertex buffer objects.
 *
 * @author SpriteMethodTest<BR><BR>
 * http://code.google.com/p/apps-for-android/source/browse/trunk/SpriteMethodTest
 */

public class GLSurfaceViewRenderer implements GLSurfaceView.Renderer {

	/** Main thread. */
	private Main mMain;
	
	/** Graphics surface */
	private GLSurfaceView mSurface;
	
	/** Width of the devices screen. */
	public static int Width;
	
	/** Height of the devices screen. */
	public static int Height;
	
    /**
     * Initialises a new GLSurfaceViewRenderer object.
     */
    public GLSurfaceViewRenderer(GLSurfaceView surface, Main game) {
    	mMain = game;
    	mSurface = surface;
    }

    /**
     * Called whenever the surface is created.  This happens at startup, and
     * may be called again at runtime if the device context is lost (the screen
     * goes to sleep, etc). This function must fill the contents of vram with
     * texture data and (when using VBOs) hardware vertex arrays.
     * @param gl The GL context
     */
    public void surfaceCreated(GL10 gl) {
        
    	// OpenGL initialisation.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); 
        gl.glDepthMask(false); 
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);

    }

    /**
     * Called when the size of the window changes.
     * @param gl OpenGL context.
     * @param width The width of the window.
     * @param height The height of the window.
     */
    public void sizeChanged(GL10 gl, int width, int height) {
    	
    	Width = width;
    	Height = height;
    	
    	// Set resolution depending on screen size.
		if(GLSurfaceViewRenderer.Width < 480 || GLSurfaceViewRenderer.Height < 272) {
			Settings.ScreenWidth = 400;
			Settings.ScreenHeight = 237;
		}		
    	
    	Drawing.setResolution(gl, Settings.ScreenWidth, Settings.ScreenHeight);
    }

    /**
     * Draws the sprites
     * @param gl The GL context
     */
    public void drawFrame(GL10 gl) {

        if(Settings.State == "license" && mMain.getLicense() != null)
        	mMain.getLicense().drawFrame(gl);
        else if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().drawFrame(gl);
        else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null)
        	mMain.getLevelSelectScreen().drawFrame(gl);         
        else if(Settings.State == "level loading" && mMain.getLevelLoadingScreen() != null)
        	mMain.getLevelLoadingScreen().drawFrame(gl);    
        else if(Settings.State == "game" && mMain.getGame() != null)
        	mMain.getGame().drawFrame(gl);   
        else if(Settings.State == "destroy")
        	mMain.destroy(gl);

    }

    /**
     * Gets the returns a standard configuration.
     */
    public int[] getConfigSpec() {
        
    	// We don't need a depth buffer, and don't care about our color depth.
        int[] configSpec = {EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_NONE};
        return configSpec;
    }

    /**
     * Called when the rendering thread shuts down. This is a good place to
     * release OpenGL ES resources.
     * @param gl OpenGL context.
     */
    public void shutdown(GL10 gl) {

    }
    
    /**
     * Called when the activity has detected the user's press of the back key.
     */
    public void onBackPressed() {
     
        if(Settings.State == "title" && mMain.getTitleScreen() != null)
        	mMain.getTitleScreen().onBackPressed();
        else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null)
        	mMain.getLevelSelectScreen().onBackPressed();     
        else if(Settings.State == "game" && mMain.getGame() != null)
        	mMain.getGame().onBackPressed();  	

    } 

    /**
     * Called when a touch screen event was not handled by any of the views under it.
     */
    public boolean onTouchEvent(float x, float y, boolean pressed) {

    	if(Settings.State == "title" && mMain.getTitleScreen() != null) {
            mMain.getTitleScreen().onTouchEvent(x, y, pressed);
        }
    	else if(Settings.State == "game" && mMain.getGame() != null) {
            mMain.getGame().onTouchEvent(x, y, pressed);
        }
    	else if(Settings.State == "level select" && mMain.getLevelSelectScreen() != null) {
            mMain.getLevelSelectScreen().onTouchEvent(x, y, pressed);
        }
    	
        return true;
    }  
    
    /**
     * Gets the GLSurfaceView.
     * @return GLSurfaceView object.
     */
    public GLSurfaceView getSurface() {
    	return mSurface;
    }

}

