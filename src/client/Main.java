package client;
//src/client/Main.java
import Game.Game;
import Game.GameGUI;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends JFrame {
	public static final int SCREEN_WIDTH = 1500;
	public static final int SCREEN_HEIGHT = 720;

	client.Menu frameMenu;
	Game game;
	JPanel screen;

	private String background_music = "src/audio/music.wav";

	Main() {
		setTitle("marble");
		//setUndecorated(true);
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout()); // 레이아웃을 BorderLayout으로 설정
		setLocation(20, 50);

		PlayMusic.load_backgroundAudio(background_music);

		setPanel(new Menu(this));

		setVisible(true);
	}

	public void setPanel(JPanel jPanel) {
		getContentPane().removeAll(); // 기존 패널 제거
		this.screen = jPanel;
		getContentPane().add(screen, BorderLayout.CENTER); // 중앙에 패널 추가

		// UI 갱신
		revalidate();
		repaint();
	}

	public void startGame(int numPlayer) {
		removeAll();
		frameMenu.setVisible(false);
		//game = new Game(this, numPlayer);
		game.start();
	}

	public void showMenu() {
		if (game != null) {
			game.close();
		}
		frameMenu.setVisible(true);
	}
	public void setScreenGameSize() {
		setLayout(null);
		setSize(1500, 720);
	}

	public void setScreenNotGameSize() {
		setLayout(new BorderLayout());
		setSize(800, 600);
	}

	public static void main(String[] args) {
		new Main();
	}
}
