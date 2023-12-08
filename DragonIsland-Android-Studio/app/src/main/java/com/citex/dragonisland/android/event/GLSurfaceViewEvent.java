package com.citex.dragonisland.android.event;

import com.citex.dragonisland.android.drawing.GLSurfaceView;
import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.core.thread.Main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.KeyEvent;

/**
 * Subclass of GLSurfaceView which receives keyboard and touch events.
 *
 * @author SpriteMethodTest<BR><BR>
 * http://code.google.com/p/apps-for-android/source/browse/trunk/SpriteMethodTest
 */
public class GLSurfaceViewEvent extends GLSurfaceView {

	/** OpenGL renderer. */
    private GLSurfaceViewRenderer mRenderer;
    
    /** Game thread. */
    private Main mMain;
    
    /**
     * Construct the GLSurfaceViewEvent handler
     * @param context The MainActiviy context
     */
    public GLSurfaceViewEvent(Context context) {
        
    	super(context);
 
        // Initialise the main thread.
		mMain = new Main(context, this, null);
 
		// Set the OpenGL renderer.
		mRenderer = new GLSurfaceViewRenderer(this, mMain); 
        setRenderer(mRenderer);
        
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    /**
     * Called when a touch screen motion event occurs
     * @param event The motion event that occurred 
     * return If you handled the event, return true 
     */
    @SuppressWarnings("deprecation")
	@SuppressLint("ClickableViewAccessibility") 
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
    	    	
    	String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
						   "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		
		sb.append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ");
			sb.append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		String ac = sb.toString();

		if (ac.equals("DOWN")) {
			mRenderer.onTouchEvent(event.getX(), event.getY(), true);
		}
		else if (ac.equals("UP")) {
			mRenderer.onTouchEvent(event.getX(), event.getY(), false);
		}	
    	else if (ac.equals("MOVE")) {
			mRenderer.onTouchEvent(event.getX(), event.getY(), true);
    	}
		else if (ac.equals("POINTER_UP(pid 0)")) {
			mRenderer.onTouchEvent(event.getX(0), event.getY(0), false);
		} 
		else if (ac.equals("POINTER_UP(pid 1)")) {
			mRenderer.onTouchEvent(event.getX(1), event.getY(1), false);
		}	
		else if (ac.equals("POINTER_DOWN(pid 0)")) {
			mRenderer.onTouchEvent(event.getX(0), event.getY(0), true);
		}
		else if (ac.equals("POINTER_DOWN(pid 1)")) {
			mRenderer.onTouchEvent(event.getX(1), event.getY(1), true);
		} 
	   			
        return true;
    }

    /**
     * Called when the activity has detected the user's press of the back key.
     */
    public void onBackPressed() {
    	mRenderer.onBackPressed();
    } 
    
    /**
     * Gets the GLSurfaceView renderer.
     * @return GLSurfaceViewRenderer object.
     */
    public GLSurfaceViewRenderer getRenderer() {
    	return mRenderer;
    }
    
    /**
     * Gets main thread.
     * @return
     */
    public Main getMain() {
    	return mMain;
    }
}

