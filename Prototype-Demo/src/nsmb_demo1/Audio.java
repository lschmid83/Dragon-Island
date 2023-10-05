package nsmb_demo1;

import java.applet.*;
import java.io.IOException;
import javax.sound.sampled.*;
import java.io.InputStream;

/**
 * Load .wav audio resources and play sound effects and music loops.
 * 
 * @version 1.0 29/05/08
 * @author Lawrence Schmid
 *
 * Not for duplication or distribution without the permission of the author.
 */

public class Audio extends Thread {

    private AudioClip audio;
    private String filename;
    private InputStream is;

    /**
     * Construct Audio
     */
    public Audio(String wavfile) {
        filename = wavfile;
    }

    //Play music
    public void playMusic(String file) {
        if (audio == null && Settings.music) {
            audio = Applet.newAudioClip(this.getClass().getClassLoader().
                    getResource(file));
            audio.loop();
        }
    }

    //Stop music
    public void stopMusic() {
        if (audio != null) {
            audio.stop();
            audio = null;
        }
    }

    //Start music
    public void startMusic() {
        if (audio != null) {
            audio.loop();
        }
    }

    /**
     * Run thread and play sound effect
     */
    public void run() {
        is = this.getClass().getClassLoader().getResourceAsStream(filename);
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(is);
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            System.out.println("Sound effect resource not found!");
        } catch (IOException e1) {
            System.out.println("Sound effect resource not found!");
            e1.printStackTrace();
        }
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (Settings.sound) {
            auline.start();
        }
        int nBytesRead = 0;
        byte[] abData = new byte[128];

        try {
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            auline.drain();
            auline.close();
        }
    }
}
