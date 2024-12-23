package client;
//src/client/Main.java
import Game.Game;
import Game.GameGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

public class Main extends JFrame {
	public static final int SCREEN_WIDTH = 1500;
	public static final int SCREEN_HEIGHT = 720;
	MyPanel screen;
	String nickname;

	private String background_music = "src/audio/music.wav";

	Main() {
		setTitle("Boogi Party");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout());
		setLocation(20, 50);

//		PlayMusic.load_backgroundAudio(background_music);

		setPanel(new Menu(this));

		// JFrame 종료 이벤트에 삭제 요청 추가
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// JFrame이 닫힐 때 서버에 삭제 요청 전송
				sendDeleteRequestToServer(nickname);
				screen.exitRoom();
			}
		});

		setVisible(true);
	}

	public void setPanel(MyPanel jPanel) {
		getContentPane().removeAll(); // 기존 패널 제거
		this.screen = jPanel;
		getContentPane().add((Component) screen, BorderLayout.CENTER); // 중앙에 패널 추가

		// UI 갱신
		revalidate();
		repaint();
	}

	public void setScreenGameSize() {
		setLayout(null);
//		setSize(1500, 720);
//		setSize(1500, 620);
		setSize(1500, 590);
	}

	public void setNickname(String name) {
		nickname = name;
	}

	public void setScreenNotGameSize() {
		setLayout(new BorderLayout());
		setSize(800, 600);
	}

	private void sendDeleteRequestToServer(String nickname) {
		try (Socket socket = new Socket("localhost", 9999); // 서버 주소와 포트
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			// 서버에 삭제 요청 전송
			out.println("REMOVE_NAME/" + nickname);
			System.out.println("Sent delete request for nickname: " + nickname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}