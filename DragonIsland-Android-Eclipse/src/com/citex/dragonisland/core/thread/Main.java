package com.citex.dragonisland.core.thread;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.swing.JFrame;

import com.citex.dragonisland.android.MainActivity;
import com.citex.dragonisland.android.drawing.GLSurfaceViewRenderer;
import com.citex.dragonisland.core.audio.IMusic;
import com.citex.dragonisland.core.audio.ISoundEffect;
import com.citex.dragonisland.core.drawing.Background;
import com.citex.dragonisland.core.drawing.Debug;
import com.citex.dragonisland.core.drawing.GameFont;
import com.citex.dragonisland.core.drawing.IBufferedImage;
import com.citex.dragonisland.core.drawing.Point;
import com.citex.dragonisland.core.game.Control;
import com.citex.dragonisland.core.game.GameFolder;
import com.citex.dragonisland.core.game.GameMode;
import com.citex.dragonisland.core.game.SaveFile;
import com.citex.dragonisland.core.game.Settings;
import com.citex.dragonisland.core.game.Transition;
import com.citex.dragonisland.core.level.Level;
import com.citex.dragonisland.core.panel.LevelEditor;
import com.citex.dragonisland.core.panel.Game;
import com.citex.dragonisland.core.panel.LevelLoading;
import com.citex.dragonisland.core.panel.LevelSelect;
import com.citex.dragonisland.core.panel.License;
import com.citex.dragonisland.core.panel.TitleScreen;
import com.citex.dragonisland.core.sprite.SpriteSheet;
import com.citex.dragonisland.core.sprite.entity.IEntity;
import com.citex.dragonisland.core.sprite.player.Player;
import com.citex.dragonisland.core.tileset.Tileset;
import com.citex.dragonisland.core.util.FileIO;
import com.citex.dragonisland.core.util.Sound;

/** 
 * This class stores resources and manages game threads.
 * @author Lawrence Schmid
 */
public class Main implements Runnable {

    /** Images. */
    private ArrayList<IBufferedImage> mImage;  	
	
    /** Game fonts. */
    private ArrayList<GameFont> mFont;     
    
    /** Entity sprite sheets. */
    private ArrayList<IEntity> mEntity;
   
    /** Players. */
    private ArrayList<Player> mPlayer;
    
    /** GUI sprite sheets. */
    private ArrayList<SpriteSheet> mGui;   
    
    /** Tile set. */
    private Tileset mTileset;
    
    /** Level data. */
    private Level mLevel;
    
    /** Background. */
    private Background mBackground;
   
    /** Screen transition */
    private Transition mTransition;
    
    /** Text displayed in the credits screen. */ 
    private ArrayList<String> mCredits;
    
    /** Save game. */
    private SaveFile mSaveFile;
    
    /** Main thread. */    
    private Thread mThread;	   
    
    /** License thread. */
    private License mLicense;
    
    /** Title screen thread. */
    private TitleScreen mTitleScreen; 
    
    /** Level loading thread. */
    private LevelLoading mLevelLoading;
    
    /** Level select. */
    private LevelSelect mLevelSelect;
    
    /** Game thread. */
    private Game mGame;  
    
    /** Editor thread. */
    private LevelEditor mEditor;  
    
    /** Game folder index. */
    private int mGameFolder;
    
    /** Controls. */
    private Control mControl;
    
    /** Camera position. */ 
    private Point mCamera;  
    
    /** Debug.  */
    private Debug mDebug;
    
    /** Music. */
    private IMusic mMusic;
    
    /** Sound effects. */
    private ISoundEffect mSoundEffect;
        
    /** Indicates if the thread has finished executing. */
    private boolean mFinished; 
    
    /** Graphics surface. */
    private Object mSurface;
    
    /** MainActivity context. */
    private MainActivity mActivity;
    
    /** Game frame. */
    private Object mFrame;
    
    /** Level path. */
    private String mLevelPath;
    
    /** Indicates if the thread is running. */
    private boolean mRunning;

    /** 
     * Initialises a Main thread.
     * @param surface Graphics surface.
     */
    public Main(Object activity, Object surface, Object frame) {  
    	
    	// Initialise the main thread.
    	mSurface = surface;	
    	mFrame = frame;
    	mImage = new ArrayList<IBufferedImage>();
    	mFont = new ArrayList<GameFont>();
    	mEntity = new ArrayList<IEntity>();
    	mPlayer = new ArrayList<Player>();
    	mGui = new ArrayList<SpriteSheet>();	
    	mControl = new Control();
    	mMusic = Sound.getMusicPlayer();
    	
    	mDebug = new Debug(this);
    	
    	if(Settings.Mode == GameMode.ANDROID) 
    		mActivity = (MainActivity)activity;
    	
    	// Start the main thread.
    	start(); 		
    }
    
    /**
     * Starts the thread.
     */
    public void start() {
    	
    	mThread = new Thread(this);
        mThread.start();
    }       
    
    /**
     * Runs the thread.
     */
    public void run() {
	
    	// Read settings.
    	try {
    		
    		if(Settings.Mode != GameMode.APPLET)
    			Settings.loadSettings(Settings.InternalStorageFolder + "settings.dat");
    		
    	} catch(Exception e) {
    		//e.printStackTrace();
    	} 

    	// Initialise the license screen.
   		mLicense = new License(this, mSurface);  	
   		mRunning = true;
    	
    	while(mRunning) {
    		
    		if(Settings.State == "title screen") {

    	  		// Set music.
    			getMusicPlayer().stop();
       	  		getMusicPlayer().play("snd/" + getLevel().getHeader().music + ".mp3");

    	   		// Show screen transition.
    	   		mTransition.start();
    			
    			// Initialise the player.
   				getCurrentPlayer().init(getLevel().getHeader(), null);
    			
     	    	// Initialise the title screen.
   				mTitleScreen = new TitleScreen(this, mSurface, true);

    		} else if(Settings.State == "load level") {
    			
    			// Stop music.
       	  		getMusicPlayer().stop();
     			
    			// Display the level opening screen.
    			boolean mShowLevelOpening = false;
    			if(mLevelPath == null)
    				mLevelPath = "0.0.0.lvl"; 
    			
    			if((mLevelPath != "0.0.0.lvl" && mLevelPath != "0.0.1.lvl" && 
    			    mLevelPath.substring(mLevelPath.length() - 5, mLevelPath.length() - 4).equals("1")) && 
    			    !getCurrentPlayer().isOnBonus())
    				mShowLevelOpening = true;

    	    	// Initialise the level loading screen.
    	  		mLevelLoading = new LevelLoading(this, mSurface, mLevelPath, mShowLevelOpening);

    		} else if(Settings.State == "start level select") { 
	
       			// Sleep.
    			//try {
    			//	Thread.sleep(100);
    			//} catch (InterruptedException e) {}

    			// Play music.
       	  		getMusicPlayer().play("snd/" + getLevel().getHeader().music + ".mp3");
    			    			
    			// Show screen transition.
    	   		mTransition.start();
    			
    			// Initialise the level select screen.
    			mLevelSelect = new LevelSelect(this, mSurface, true);
  
    		} else if(Settings.State == "start game") {

    			// Set music.
       	  		getMusicPlayer().stop();
    	  		getMusicPlayer().play("snd/" + getLevel().getHeader().music + ".mp3");

    	   		// Show screen transition.
   	   			mTransition.start();
    			
    			// Initialise the player.
   				getCurrentPlayer().init(getLevel().getHeader(), getSaveFile().getSaveGame(getGameFolderIndex()));

    			// Initialise the main game.
    	   		mGame = new Game(this, mSurface, true);

    			
    		} else if(Settings.State == "start editor") {
    			   
    	   		// Show screen transition.
   	   			mTransition.start();
    			
     			// Initialise the level editor.
    	   		try {
    	   			mEditor = new LevelEditor(this, mSurface, mLevelPath, true);
    	   		}
    	   		catch(IOException e) {}
 
    		}	
    		
    		// Pause before updates.
    		try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}    		
    	}
    }

    /**
     * Gets the Android MainActivity.
     * @return MainActivity object.
     */
    public MainActivity getActivity() {
    	return mActivity;    	
    }

    /**
     * Gets all of the game images.
     * @return List of objects which implement IBufferedImage.
     */
    public ArrayList<IBufferedImage> getImages() {
    	return mImage;    	
    }
    
    /**
     * Gets a game image.
     * @param index Index of the image.
     * @return Object which implements IBufferedImage.
     */
    public IBufferedImage getImage(int index) {
    	return mImage.get(index);    	
    }    
    
    /**
     * Sets a game image.
     * @param Object which implements IBufferedImage.
     */
    public void setImage(IBufferedImage image) {
    	mImage.add(image);	
    }

    /**
     * Gets a game font.
     * @param index Index of the font.
     * @return GameFont object.
     */
    public GameFont getGameFont(int index) {
    	return mFont.get(index);    	
    }
 
    /**
     * Sets a game font.
     * @param gameFont GameFont object.
     */
    public void setGameFont(GameFont gameFont) {
    	mFont.add(gameFont);	
    }
      
    /**
     * Gets a sprite.
     * @param index Index of the sprite.
     * @return Object which implements IEntity.
     */
    public IEntity getEntity(int index) {
    	return mEntity.get(index);    	
    } 
    
    /**
     * Gets the entity collection.    
     * @return ArrayList of objects which implement IEntity.
     */
    public ArrayList<IEntity> getEntities() {
    	return mEntity;    	
    }
    
    /**
     * Sets a sprite.
     * @param spriteSheet Object which implements IEntity.
     */
    public void setEntitySprite(IEntity spriteSheet) {
    	mEntity.add(spriteSheet);
    } 
    
    /**
     * Gets the current player.
     * @return Player object.
     */
    public Player getCurrentPlayer() {
    	return mPlayer.get(0);
    }
    
    /**
     * Gets a player.
     * @param index Index of the sprite.
     * @return Player object.
     */
    public ArrayList<Player> getPlayers() {
    	return mPlayer;    	
    }   
    
    /**
     * Sets a player.
     * @param spriteSheet Player object.
     */
    public void setPlayerSprite(Player player) {
    	mPlayer.add(player);
    }    
    
    /**
     * Gets a sprite.
     * @param index Index of the sprite.
     * @return SpriteSheet object.
     */
    public SpriteSheet getGuiSprite(int index) {
    	return mGui.get(index);    	
    }   
    
    /**
     * Sets a sprite.
     * @param spriteSheet SpriteSheet object.
     */
    public void setGuiSprite(SpriteSheet spriteSheet) {
    	mGui.add(spriteSheet);
    }     

    /**
     * Gets a tileset.
     * @return Tileset object.
     */
    public Tileset getTileset() {
    	return mTileset;    	
    }   
    
    /**
     * Sets a tileset.
     * @param spriteSheet Tileset object.
     */
    public void setTileset(Tileset tileset) {
    	mTileset = tileset;
    }   
    
    /**
     * Gets a background.
     * @return Background object.
     */
    public Background getBackground() {
    	return mBackground;    	
    }   
    
    /**
     * Sets a background.
     * @param background Background object.
     */
    public void setBackground(Background background) {
    	mBackground = background;
    }     
    
    /**
     * Gets level data.
     * @return Background object.
     */
    public Level getLevel() {
    	return mLevel;    	
    }   
    
    /**
     * Sets a level data.
     * @param background Background object.
     */
    public void setLevel(Level level) {
    	mLevel = level;
    }     
    
    /**
     * Gets screen transition.
     * @return Background object.
     */
    public Transition getScreenTransition() {
    	return mTransition;    	
    }   
    
    /**
     * Sets a screen transition.
     * @param background Transition object.
     */
    public void setSaveFile(SaveFile saveFile) {
    	mSaveFile = saveFile;
    }  
    
    /**
     * Gets the save game data.
     * @return SaveGame object.
     */
    public SaveFile getSaveFile() {
    	return mSaveFile;    	
    }   
    
    /**
     * Sets a screen transition.
     * @param background Transition object.
     */
    public void setScreenTransition(Transition transition) {
    	mTransition = transition;
    }    
            
    /**
     * Indicates if the thread has finished executing.
     * @return True if finished; otherwise false.
     */
    public boolean isFinished() {
    	return mFinished;
    }  
    
    /**
     * Gets the license thread.
     * @return License object.
     */
    public License getLicense() {
    	return mLicense;
    }
    
    /**
     * Gets the title screen thread.
     * @return TitleScreen object.
     */
    public TitleScreen getTitleScreen() {
    	return mTitleScreen;
    }
    
    /**
     * Gets the graphics surface.
     * @return Object which can be cast to JPanel or GLSurfaceView.
     */
    public Object getSurface() {
    	return mSurface;
    }
    
    /**
     * Gets the controls.
     * @return Control object.
     */
    public Control getControls() {
    	return mControl;
    }

    /**
     * Sets the controls.
     * @param event MotionEvent object.
     */
    public void setControl(float x, float y, boolean pressed) {
    	mControl.setControl(x, y, pressed);
    }
    
    /**
     * Sets the game folder.
     * @param index Index of the game folder.
     */
    public void setGameFolder(int index) {
    	
    	if(mGameFolder < mSaveFile.getGameFolders(true).size())
    		mGameFolder = index;    		
    	else
    		mGameFolder = 0;	
    }
    
    /**
     * Gets game folder index.
     * @return Game folder index.
     */
    public int getGameFolderIndex() {
    	return mGameFolder;
    }
    
    /**
     * Gets the current game folder.
     * @return GameFolder object.
     */
    public GameFolder getGameFolder() {
    	return mSaveFile.getGameFolders(true).get(mGameFolder);	
    }
        
    /**
     * Gets the game frame.
     * @return JFrame object.
     */
    public JFrame getGameFrame() {
    	return (JFrame)mFrame;   	
    }
    
    /**
     * Gets the level loading thread.
     * @return LevelLoading object.
     */
    public LevelLoading getLevelLoadingScreen() {
    	return mLevelLoading;
    }
        
    /**
     * Gets the level select thread.
     * @return LevelSelect object.
     */
    public LevelSelect getLevelSelectScreen() {
    	return mLevelSelect;
    }
      
    
    /**
     * Gets the main game thread.
     * @return Game object.
     */
    public Game getGame() {
    	return mGame;
    }
    
    /**
     * Gets the editor thread.
     * @return Game object.
     */
    public LevelEditor getEditor() {
    	return mEditor;
    }
    
    /**
     * Gets the debug object.
     * @return Debug object.
     */
    public Debug getDebug() {
    	return mDebug;
    }
    
    /**
     * Gets the camera.
     * @return Point object.
     */
    public Point getCamera() {
    	return mCamera;
    }
    
    /**
     * Sets the camera.
     * @param camera Point object.
     */
    public void setCamera(Point camera) {
    	mCamera = camera;
    } 
    
    /**
     * Sets the level loading path.
     * @param path Level file path.
     */
    public void setLevelPath(String path) {
    	mLevelPath = path;    	
    }
        
    /**
     * Gets the music player.
     * @return Object which implements IMusic.
     */
    public IMusic getMusicPlayer() {
    	return mMusic;
    } 
    
    /**
     * Sets the sound effects.
     * @param soundEffect Object which implements ISoundEffect.
     */
    public void setSoundEffects(ISoundEffect soundEffect) {
    	mSoundEffect = soundEffect;
    }
    
    /**
     * Gets the sound effects.
     * @return Object which implements ISoundEffect.
     */
    public ISoundEffect getSoundEffects() {
    	return mSoundEffect;    	
    }
    
    /**
     * Gets screen transition.
     * @return Transition object.
     */
    public Transition getTransition() {
    	return mTransition;
    }
    
    /**
     * Gets the credits.
     * @return List of credits text.
     */
    public ArrayList<String> getCredits() {
    	return mCredits;    	
    }
    
    /**
     * Sets the credits.
     * @param credits List of credits text.
     */
    public void setCredits(ArrayList<String> credits) {
    	mCredits = credits;    	
    }
    
    /**
     * Ends the thread.
     */
    public void kill() {
    	mRunning = false;
    }
    
    /**
     * Destroys all resources.
     */
    public void destroy(GL10 gl) {
   
    	// Destroy images.
    	for(IBufferedImage image : mImage) {	
    		if(image != null)
    			image.destroy(gl);
    	}
    		
    	// Destroy fonts.
    	for(GameFont font : mFont) {
    		if(font != null)
    			font.destroy(gl);
    	}
    	
    	// Destroy entities.
    	for(IEntity entity : mEntity) {
    		if(entity != null)
    			entity.destroy(gl);
    	}
    	
    	// Destroy cloned entities.
    	if(mGame != null)
    		mGame.destroy(gl);
    	
    	// Destroy player.
    	for(Player player : mPlayer) {
    		if(player != null)
    			player.destroy(gl);
    	}
    	
    	// Destroy sprite sheets.
    	for(SpriteSheet spriteSheet : mGui) {
    		if(spriteSheet != null)
    			spriteSheet.destroy(gl);
    	}
    	
    	// Destroy tileset.
    	if(mTileset != null)
    		mTileset.destroy(gl);
    	
    	// Destroy background.
    	if(mBackground != null)
    		mBackground.destroy(gl);
    	
    	// Destroy transition.
    	if(mTransition != null)
    		mTransition.destroy(gl);
    	
    	// Destroy sound effects.
    	mSoundEffect.destroy();

    }
 
}
