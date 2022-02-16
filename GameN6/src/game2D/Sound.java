package game2D;

import java.io.*;
import javax.sound.sampled.*;


public class Sound extends Thread {

	String filename;	// The name of the file to play
	boolean finished;	// A flag showing that the thread has finished
	int map;
	
	public Sound(String fname, int m) {
		filename = fname;
		finished = false;
		map = m;
		
	}

	/**
	 * run will play the actual sound but you should not call it directly.
	 * You need to call the 'start' method of your sound object (inherited
	 * from Thread, you do not need to declare your own). 'run' will
	 * eventually be called by 'start' when it has been scheduled by
	 * the process scheduler.
	 * Student : 2721301
	 */
	public void run() {
		try {
			File file = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(file);
			AudioFormat	format = stream.getFormat();
			
			EchoFilterStream filtered = new EchoFilterStream(stream);
			AudioInputStream f = new AudioInputStream(filtered,format,stream.getFrameLength());
			
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = (Clip)AudioSystem.getLine(info);
			if (map==1) {
				clip.open(f);
			}else {
				clip.open(stream);
			}
			clip.start();
			Thread.sleep(100);
			while (clip.isRunning()) { Thread.sleep(100); }
			clip.close();
		}
		catch (Exception e) {	}
		finished = true;

	}
}
