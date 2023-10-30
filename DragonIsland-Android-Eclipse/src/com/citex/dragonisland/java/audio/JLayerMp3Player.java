package com.citex.dragonisland.java.audio;

import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

/**
 * This class and plays MP3 files.
 * @author Lawrence Schmid
 */
public class JLayerMp3Player {
	
	/** Audio bitstream. */
	private Bitstream mBitstream;
	
	/** Audio decoder. */
	private Decoder mDecoder;
	
	/** AudioDevice the audio samples are written to. */
	private AudioDevice mAudio;
	
	/** Listener for the playback process */
	private PlaybackListener mListener;
	
	/** Indicates if the file is playing. */
	private boolean mPlaying = true;

	/**
	 * Creates a new Player</code> instance.
	 */
	public JLayerMp3Player(InputStream stream) throws JavaLayerException {
		this(stream, null);
	}

	public JLayerMp3Player(InputStream stream, AudioDevice device)
			throws JavaLayerException {
		mBitstream = new Bitstream(stream);

		if (device != null)
			mAudio = device;
		else
			mAudio = FactoryRegistry.systemRegistry().createAudioDevice();
		mAudio.open(mDecoder = new Decoder());
	}

	public void play() throws JavaLayerException {
		play(Integer.MAX_VALUE);
	}

	/**
	 * Plays a number of MPEG audio frames.
	 * 
	 * @param frames
	 *            The number of frames to play.
	 * @return true if the last frame was played, or false if there are more
	 *         frames.
	 */
	public boolean play(int frames) throws JavaLayerException {
		boolean ret = true;

		// report to listener
		if (mListener != null)
			mListener.playbackStarted(createEvent(PlaybackEvent.STARTED));

		// http://www.informit.com/guides/content.aspx?g=java&seqNum=290 - play/pause method
		while (frames-- > 0 && ret) {
			if (this.mPlaying) {
				ret = decodeFrame();
			} else {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}

		if (!ret)
		{
			// last frame, ensure all data flushed to the audio device.
			AudioDevice out = mAudio;
			if (out != null) {
				// System.out.println(audio.getPosition());
				out.flush();
				// System.out.println(audio.getPosition());
				synchronized (this) {
					// complete = (!closed);
					close();
				}
				// report to listener
				if (mListener != null)
					mListener.playbackFinished(createEvent(out,
							PlaybackEvent.STOPPED));
			}
		}
		return ret;
	}

	public void start()
	{
		mPlaying = true;
	}
	
	public void pause()
	{
		mPlaying = false;
	}
	
	/**
	 * Cloases this player. Any audio currently playing is stopped immediately.
	 */
	public synchronized void close() {
		AudioDevice out = mAudio;
		if (out != null) {
			// closed = true;
			mAudio = null;
			// this may fail, so ensure object state is set up before
			// calling this method.
			out.close();
			// lastPosition = out.getPosition();
			try {
				mBitstream.close();
			} catch (BitstreamException ex) {
			}
		}
	}

	/**
	 * Decodes a single frame.
	 * @return true if there are no more frames to decode, false otherwise.
	 */
	protected boolean decodeFrame() throws JavaLayerException {
		try {
			AudioDevice out = mAudio;
			if (out == null)
				return false;

			Header h = mBitstream.readFrame();
			if (h == null)
				return false;

			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer) mDecoder.decodeFrame(h,
					mBitstream);

			synchronized (this) {
				out = mAudio;
				if (out != null) {
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}
			}

			mBitstream.closeFrame();
		} catch (RuntimeException ex) {
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}

	/**
	 * skips over a single frame
	 * @return false if there are no more frames to decode, true otherwise.
	 */
	protected boolean skipFrame() throws JavaLayerException {
		Header h = mBitstream.readFrame();
		if (h == null)
			return false;
		mBitstream.closeFrame();
		return true;
	}

	/**
	 * Plays a range of MPEG audio frames
	 * @param start The first frame to play
	 * @param end The last frame to play
	 * @return true if the last frame was played, or false if there are more frames.
	 */
	public boolean play(final int start, final int end)
			throws JavaLayerException {
		boolean ret = true;
		int offset = start;
		while (offset-- > 0 && ret)
			ret = skipFrame();
		return play(end - start);
	}

	/**
	 * Constructs a <code>PlaybackEvent</code>
	 */
	private PlaybackEvent createEvent(int id) {
		return createEvent(mAudio, id);
	}

	/**
	 * Constructs a <code>PlaybackEvent</code>
	 */
	private PlaybackEvent createEvent(AudioDevice dev, int id) {
		return new PlaybackEvent(this, id, dev.getPosition());
	}

	/**
	 * sets the <code>PlaybackListener</code>
	 */
	public void setPlayBackListener(PlaybackListener listener) {
		this.mListener = listener;
	}

	/**
	 * gets the <code>PlaybackListener</code>
	 */
	public PlaybackListener getPlayBackListener() {
		return mListener;
	}

	/**
	 * closes the player and notifies <code>PlaybackListener</code>
	 */
	public void stop() {
		mListener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
		close();
	}
}
