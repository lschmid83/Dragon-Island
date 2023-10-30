/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This file was lifted from the APIDemos sample.  See:
// http://developer.android.com/guide/samples/ApiDemos/src/com/example/android/apis/graphics/index.html
package com.citex.dragonisland.android.drawing;

import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * An implementation of SurfaceView that uses the dedicated surface for
 * displaying an OpenGL animation.  This allows the animation to run in a
 * separate thread, without requiring that it be driven by the update mechanism
 * of the view hierarchy.
 *
 * The application-specific rendering code is delegated to a GLView.Renderer
 * instance.
 * 
 * @author SpriteMethodTest<BR><BR>
 * http://code.google.com/p/apps-for-android/source/browse/trunk/SpriteMethodTest 
 */
public class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    
	/**
	 * Contructs the GLSurfaceView
	 * @param context The MainActivity context
	 */
	public GLSurfaceView(Context context) {
        super(context);
        init();
    }

	/**
	 * Initialize the GLSurfaceView
	 */
    private void init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
    }

    /**
     * Return the SurfaceHolder providing access and control over this SurfaceView's underlying surface.
     * @return The SurfaceHolder
     */
    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    /**
     * Sets the interface used to wrap a GL interface
     * @param glWrapper The GL wrapper
     */
    public void setGLWrapper(GLWrapper glWrapper) {
        mGLWrapper = glWrapper;
    }

    /**
     * Sets the renderer that is responsible for making OpenGL calls to render a frame.
     * @param renderer The GL renderer
     */
    public void setRenderer(Renderer renderer) {
        mGLThread = new GLThread(renderer);
        mGLThread.start();
    }

    /**
     * This is called immediately after the surface is first created
     */
    public void surfaceCreated(SurfaceHolder holder) {
        mGLThread.surfaceCreated();
    }

    /**
     * This is called immediately before a surface is being destroyed.
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return
        mGLThread.surfaceDestroyed();
    }

    /**
     * This is called immediately after any structural changes (format or size) have been made to the surface.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
        mGLThread.onWindowResize(w, h);
    }

    /**
     * Inform the view that the activity is paused.
     */
    public void onPause() {
        mGLThread.onPause();
    }

    /**
     * Inform the view that the activity is resumed.
     */
    public void onResume() {
        mGLThread.onResume();
    }

    /**
     * Queue a runnable to be run on the GL rendering thread. This can be used
     * to communicate with the Renderer on the rendering thread.
     * Must not be called before a renderer has been set.
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r) {
        mGLThread.setEvent(r);
    }  
    
    /**
     * Inform the view that the window focus has changed.
     */
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mGLThread.onWindowFocusChanged(hasFocus);
    }

    /**
     * Set an "event" to be run on the GL rendering thread.
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void setEvent(Runnable r) {
        mGLThread.setEvent(r);
    }
    
    /**
     * Removes an "event" to be run on the GL rendering thread.
     */
    public void clearEvent() {
        mGLThread.clearEvent();
    }

    /**
     * Called when the window has been attached to the window manager
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mGLThread.requestExitAndWait();
    }

    // ----------------------------------------------------------------------

    /**
     * An interface used to wrap a GL interface. 
     */
    public interface GLWrapper {
      GL wrap(GL gl);
    }

    // ----------------------------------------------------------------------

    /**
     * A generic renderer interface.
     */
    public interface Renderer {
        /**
         * @return the EGL configuration specification desired by the renderer.
         */
        int[] getConfigSpec();

        /**
         * Surface created.
         * Called when the surface is created. Called when the application
         * starts, and whenever the GPU is reinitialized. This will
         * typically happen when the device awakes after going to sleep.
         * Set your textures here.
         */
        void surfaceCreated(GL10 gl);

        /**
         * Called when the rendering thread is about to shut down.  This is a
         * good place to release OpenGL ES resources (textures, buffers, etc).
         * @param gl
         */
        void shutdown(GL10 gl);

        /**
         * Surface changed size.
         * Called after the surface is created and whenever
         * the OpenGL ES surface size changes. Set your viewport here.
         * @param gl
         * @param width
         * @param height
         */
        void sizeChanged(GL10 gl, int width, int height);
        /**
         * Draw the current frame.
         * @param gl
         */
        void drawFrame(GL10 gl);
    }

    /**
     * An EGL helper class.
     */
    private class EglHelper {
        public EglHelper() {

        }

        /**
         * Initialize EGL for a given configuration spec.
         * @param configSpec
         */
        public void start(int[] configSpec){
            /*
             * Get an EGL instance
             */
            mEgl = (EGL10) EGLContext.getEGL();

            /*
             * Get to the default display.
             */
            mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            /*
             * We can now initialize EGL for that display
             */
            int[] version = new int[2];
            mEgl.eglInitialize(mEglDisplay, version);

            EGLConfig[] configs = new EGLConfig[1];
            int[] num_config = new int[1];
            mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1,
                    num_config);
            mEglConfig = configs[0];

            /*
            * Create an OpenGL ES context. This must be done only once, an
            * OpenGL context is a somewhat heavy object.
            */
            mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig,
                    EGL10.EGL_NO_CONTEXT, null);

            mEglSurface = null;
        }

        /**
         * Create and return an OpenGL surface
         */
        public GL createSurface(SurfaceHolder holder) {
            /*
             *  The window size has changed, so we need to create a new
             *  surface.
             */
            if (mEglSurface != null) {

                /*
                 * Unbind and destroy the old EGL surface, if
                 * there is one.
                 */
                mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            }

            /*
             * Create an EGL surface we can render into.
             */
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay,
                    mEglConfig, holder, null);

            /*
             * Before we can issue GL commands, we need to make sure
             * the context is current and bound to a surface.
             */
            mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
                    mEglContext);


            GL gl = mEglContext.getGL();
            if (mGLWrapper != null) {
                gl = mGLWrapper.wrap(gl);
            }
            return gl;
        }

        /**
         * Display the current render surface.
         * @return false if the context has been lost.
         */
        public boolean swap() {
            mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);

            /*
             * Always check for EGL_CONTEXT_LOST, which means the context
             * and all associated data were lost (For instance because
             * the device went to sleep). We need to sleep until we
             * get a new surface.
             */
            return mEgl.eglGetError() != EGL11.EGL_CONTEXT_LOST;
        }
        
        public void finish() {
            if (mEglSurface != null) {
                mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_SURFACE,
                        EGL10.EGL_NO_CONTEXT);
                mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
                mEglSurface = null;
            }
            if (mEglContext != null) {
                mEgl.eglDestroyContext(mEglDisplay, mEglContext);
                mEglContext = null;
            }
            if (mEglDisplay != null) {
                mEgl.eglTerminate(mEglDisplay);
                mEglDisplay = null;
            }
        }

        EGL10 mEgl;
        EGLDisplay mEglDisplay;
        EGLSurface mEglSurface;
        EGLConfig mEglConfig;
        EGLContext mEglContext;
    }

    /**
     * A generic GL Thread. Takes care of initializing EGL and GL. Delegates
     * to a Renderer instance to do the actual drawing.
     *
     */
    public class GLThread extends Thread {
        public GLThread(Renderer renderer) {
            super();
            mDone = false;
            mWidth = 0;
            mHeight = 0;
            mRenderer = renderer;
            setName("GLThread");
        }

        /**
         * Calls the run() method of the Runnable object the receiver holds.
         */
        @Override
        public void run() {
            /*
             * When the android framework launches a second instance of
             * an activity, the new instance's onCreate() method may be
             * called before the first instance returns from onDestroy().
             *
             * This semaphore ensures that only one instance at a time
             * accesses EGL.
             */
            try {
                try {
                sEglSemaphore.acquire();
                } catch (InterruptedException e) {
                    return;
                }
                guardedRun();
            } catch (InterruptedException e) {
                // fall thru and exit normally
            } finally {
                sEglSemaphore.release();
            }
        }
        
        private void guardedRun() throws InterruptedException {
            mEglHelper = new EglHelper();
            /*
             * Specify a configuration for our opengl session
             * and grab the first configuration that matches is
             */
            int[] configSpec = mRenderer.getConfigSpec();
            mEglHelper.start(configSpec);

            //GL10 gl = null;
            boolean tellRendererSurfaceCreated = true;
            boolean tellRendererSurfaceChanged = true;

            /*
             * This is our main activity thread's loop, we go until
             * asked to quit.
             */
            while (!mDone) {
                /*
                 *  Update the asynchronous state (window size)
                 */
                int w, h;
                boolean changed;
                boolean needStart = false;
                synchronized (this) {
                    if (mEvent != null) {
                        mEvent.run();
                    }
                    if (mPaused) {
                        mEglHelper.finish();
                        needStart = true;
                    }
                    if(needToWait()) {
                        while (needToWait()) {
                            wait();
                        }
                    }
                    if (mDone) {
                        break;
                    }
                    changed = mSizeChanged;
                    w = mWidth;
                    h = mHeight;
                    mSizeChanged = false;
                }
                if (needStart) {
                    mEglHelper.start(configSpec);
                    tellRendererSurfaceCreated = true;
                    changed = true;
                }
                if (changed) {
                    gl = (GL10) mEglHelper.createSurface(mHolder);
                    tellRendererSurfaceChanged = true;
                }
                if (tellRendererSurfaceCreated) {
                    mRenderer.surfaceCreated(gl);
                    tellRendererSurfaceCreated = false;
                }
                if (tellRendererSurfaceChanged) {
                    mRenderer.sizeChanged(gl, w, h);
                    tellRendererSurfaceChanged = false;
                }
                if ((w > 0) && (h > 0)) {
                    /* draw a frame here */
                    mRenderer.drawFrame(gl);

                    /*
                     * Once we're done with GL, we need to call swapBuffers()
                     * to instruct the system to display the rendered frame
                     */
                    mEglHelper.swap();
                }
             }

            /*
             * clean-up everything...
             */
            if (gl != null) {
                mRenderer.shutdown(gl);
            }

            mEglHelper.finish();
        }

        private boolean needToWait() {
            return (mPaused || (! mHasFocus) || (! mHasSurface) || mContextLost)
                && (! mDone);
        }

        public void surfaceCreated() {
            synchronized(this) {
                mHasSurface = true;
                mContextLost = false;
                notify();
            }
        }

        public void surfaceDestroyed() {
            synchronized(this) {
                mHasSurface = false;
                notify();
            }
        }

        public void onPause() {
            synchronized (this) {
                mPaused = true;
            }
        }

        public void onResume() {
            synchronized (this) {
                mPaused = false;
                notify();
            }
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            synchronized (this) {
                mHasFocus = hasFocus;
                if (mHasFocus == true) {
                    notify();
                }
            }
        }
        public void onWindowResize(int w, int h) {
            synchronized (this) {
                mWidth = w;
                mHeight = h;
                mSizeChanged = true;
            }
        }

        public void requestExitAndWait() {
            // don't call this from GLThread thread or it is a guaranteed
            // deadlock!
            synchronized(this) {
                mDone = true;
                notify();
            }
            try {
                join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Queue an "event" to be run on the GL rendering thread.
         * @param r the runnable to be run on the GL rendering thread.
         */
        public void setEvent(Runnable r) {
            synchronized(this) {
                mEvent = r;
            }
        }

        public void clearEvent() {
            synchronized(this) {
                mEvent = null;
            }
        }
        private boolean mDone;
        private boolean mPaused;
        private boolean mHasFocus;
        private boolean mHasSurface;
        private boolean mContextLost;
        private int mWidth;
        private int mHeight;
        public Renderer mRenderer;
        private Runnable mEvent;
        private EglHelper mEglHelper;
        private GL10 gl;
    }

    private static final Semaphore sEglSemaphore = new Semaphore(1);
    private boolean mSizeChanged = true;

    private SurfaceHolder mHolder;
    private GLThread mGLThread;
    private GLWrapper mGLWrapper;
    
    
}
