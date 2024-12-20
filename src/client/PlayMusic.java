package client;
//src/client/PlayMusic.java

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;

public class PlayMusic {
	private static Clip clip;
	private static boolean isPlaying = false;
	private static boolean isAction = true;

	public static void load_backgroundAudio(String pathName) {
		try {
			File audioFile = new File(pathName);
			final AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

			clip = AudioSystem.getClip();
			clip.addLineListener(new LineListener() {
				public void update(LineEvent e) {
					if (e.getType() == LineEvent.Type.STOP) {
						try {
							audioStream.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			clip.open(audioStream);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		play_Background_Audio();
	}

	public static void play_actionSound(String pathName) {
	    if (isAction) {
	        SwingUtilities.invokeLater(() -> {
	            try {
	                Clip clip = AudioSystem.getClip();
	                File audioFile = new File(pathName);
	                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
	                clip.open(audioStream);
	                clip.start();
	            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
	                e.printStackTrace();
	            }
	        });
	    }
	}
	
	public static void play_Background_Audio() {
		if (clip != null && !isPlaying) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
			isPlaying = true;
		}
	}
	
	public static void play_action_Audio() {
		isAction = true;
	}

	public static void stop_Background_Audio() {
		if (clip != null && isPlaying) {
			clip.stop();
			isPlaying = false;
		}
	}
	
	public static void stop_action_Audio() {
		isAction = false;
	}
	
	public static boolean check_background_Play() {
		return isPlaying;
	}
	
	public static boolean check_action_Play() {
		return isAction;
	}
}
