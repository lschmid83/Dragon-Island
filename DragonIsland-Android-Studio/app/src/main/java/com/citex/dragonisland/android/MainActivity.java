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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.citex.dragonisland.android.drawing.GLSurfaceView;
import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.android.event.GLSurfaceViewEvent;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.Settings;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * MainActivity.java
 * This class is the main activity context.
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

    /** View which displays AdMob advertisements. */
    public static AdView mAdView;

    /** Is the advertising visible. */
    public boolean mAdVisible;
    
    /**
     * Called when the activity is starting. 
     * @param savedInstanceState Contains saved instance state data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) { 

		super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

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


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdVisible = false;

        // Create an ad.
        mAdView = new AdView(mActivity);
        mAdView.setAdUnitId("ca-app-pub-1622304112165026/1588105391");
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setBackgroundColor(Color.TRANSPARENT);

        // Create an ad request and get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
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
        LayoutParams params = new LayoutParams(width + 90, (int)(height - 30));
        mActivity.addContentView(rl, params);

        mAdView.setEnabled(true);
        rl.addView(mAdView);
        rl.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        rl.bringToFront();
        mAdView.setVisibility(AdView.VISIBLE);

        mAdVisible = true;

    }
    
    /**
     * Removes an AdMob ad banner.
     */
    public void removeAdvertisement() {

        if(mAdVisible) {

            // Remove the AdMob panel.
            mAdView.setEnabled(false);
            mAdView.setVisibility(View.GONE);

            ViewGroup vg = (ViewGroup)(mAdView.getParent());
            vg.removeView(mAdView);

            RelativeLayout rl = new RelativeLayout(mActivity);
            View admobAds = (View) findViewById(1);
            rl.removeView(admobAds);

            mAdVisible = false;

        }
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
        PendingIntent intent = PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
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

        // Pause adView.
        if (mAdView != null)
            mAdView.pause();

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

        // Resume advertisements.
        if (mAdView != null)
            mAdView.resume();

        // Resume GL surface view.
    	if(mGLSurfaceView != null)
    		mGLSurfaceView.onResume();
    	
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {

        // Destroy advertisements.
        if (mAdView != null)
            mAdView.destroy();

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