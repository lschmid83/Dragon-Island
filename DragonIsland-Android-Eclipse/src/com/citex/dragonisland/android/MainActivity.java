package com.citex.dragonisland.android;

import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.citex.dragonisland.android.drawing.GLSurfaceView;
import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.android.event.GLSurfaceViewEvent;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;

/**
 * This class is the main activity context.
 * @author Lawrence Schmid
 */
public class MainActivity extends Activity {
	
    /** Surface for displaying OpenGL rendering. */
	private GLSurfaceView mGLSurfaceView;
	
	/** Provides access to an application's raw asset files. */
    public static AssetManager mAssetManager;
    
    /** Interface to global information about an application environment. */
    public static Context mContext;
    
    /** Main activity. */
    public static Activity mActivity;	

    /** Graphics renderer. */
    public GLSurfaceViewRenderer mRenderer;
    
    /**
     * Called when the activity is starting. 
     * @param savedInstanceState Contains saved instance state data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { 

		super.onCreate(savedInstanceState);
    	
    	// Restart activity on resume.
    	if(savedInstanceState != null) {    		
    		finish(); 
    		restart(mContext, 1);
    	}
    	
    	// Initialise settings.
    	Settings.Mode = GameMode.ANDROID;
    	Settings.InternalStorageFolder = Environment.getExternalStorageDirectory() + "/Android/data/com.citex.dragonisland/";
    	    	
		mContext = this;
		mActivity = this;
		mAssetManager = this.getAssets();
		mGLSurfaceView = new GLSurfaceViewEvent(this);

		setContentView(mGLSurfaceView);
    }
 
    /**
     * Adds an AdMob ad banner.
     */
    public void addAdvertisement() {

		// Create a DisplayMetrics object to screen dimensions.	
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics);  
		int width = metrics.widthPixels;  
		int height = metrics.heightPixels; 

		// Add the AdMob panel.
		RelativeLayout rl = new RelativeLayout(mActivity);
		LayoutParams params = new LayoutParams(width, (int)(height - 30)); 
		mActivity.addContentView(rl, params);
    	
    }
    
    /**
     * Restarts the activity.
     * @param context Context object.
     * @param delay Delay before restarting.
     */
    public static void restart(Context context, int delay) {
        
    	// Set minimum delay.
    	if (delay == 0)
            delay = 1;
        
    	// Initialise the restart intent.
        Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        
        // Schedule the intent to restart.
        PendingIntent intent = PendingIntent.getActivity(context, 0, restartIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        
        // Exit app.
        System.exit(2);
    }
    
    /**
     * Called when the activity has detected the user's press of the back key.
     */
    @Override
    public void onBackPressed() {
    	((GLSurfaceViewEvent)mGLSurfaceView).onBackPressed();
    }  
    
    /** Called when leaving the activity */
    @Override
    public void onPause() {

    	// Pause GL surface view.
    	if(mGLSurfaceView != null)
    		mGLSurfaceView.onPause();
		
    	// Save settings.
    	try {
			Settings.saveSettings(Settings.InternalStorageFolder + "settings.dat");
		} catch (IOException e) {
			e.printStackTrace();
		}

  		// Stop music.
    	((GLSurfaceViewEvent)mGLSurfaceView).getMain().getMusicPlayer().stop();
	
    	// Kill main thread.
    	((GLSurfaceViewEvent)mGLSurfaceView).getMain().kill();
    	
    	// Destroy all resources.
    	Settings.State = "destroy";	
		
		super.onPause();
		
	    System.exit(2);    
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        
    	super.onResume();
    	
        // Resume GL surface view.
    	if(mGLSurfaceView != null)
    		mGLSurfaceView.onResume();
    	
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
           	
    	// Save settings.
    	try {
			Settings.saveSettings(Settings.InternalStorageFolder + "settings.dat");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
  		// Stop music.
    	((GLSurfaceViewEvent)mGLSurfaceView).getMain().getMusicPlayer().stop();
	
    	// Kill main thread.
    	((GLSurfaceViewEvent)mGLSurfaceView).getMain().kill();
    	
    	// Destroy all resources.
    	Settings.State = "destroy";	

        super.onDestroy();
        
        System.exit(2);
    }   

    /**
     * Gets the GLSurfaceView.
     * @return GLSurfaceView object.
     */
    public GLSurfaceViewEvent getSurface() {
    	return (GLSurfaceViewEvent)mGLSurfaceView;
    }

}