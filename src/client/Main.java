package client;

import Game.Game;
import Game.GameGUI;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends JFrame {
	client.Menu frameMenu;
	Game game;
	JPanel screen;

	private String background_music = "src/audio/music.wav";

	int width, height;

	public void setSize() {
		setSize(800, 600);
	}

	Main() {
		setTitle("marble");
		//setUndecorated(true);
		this.width=800;
		this.height=600;
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout()); // 레이아웃을 BorderLayout으로 설정
		setLocation(20, 50);

		PlayMusic.load_backgroundAudio(background_music);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				screen.setSize(width, height);

				// 패널 다시 그리기
				screen.revalidate();
				screen.repaint();
			}
		});

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

	public void setScreenGameSize() {
		setLayout(null);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(dimension.width / 2, dimension.height / 2);
	}

	public void setScreenNotGameSize() {
		setLayout(new BorderLayout());
		setSize(800, 600);
	}

	public static void main(String[] args) {
		new Main();
	}
}
