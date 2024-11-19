import javax.swing.*;

public class Main extends JFrame {
	public static final int SCREEN_WIDTH = 1500;
	public static final int SCREEN_HEIGHT = 720;

	Menu frameMenu;
	Game game;
	WaitingRoom waitingRoom;

	private String background_music = "src/audio/music.wav";

	Main() {
		setTitle("marble");
		setUndecorated(true);
		setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setLocation(20, 50);
		setVisible(false);

		PlayMusic.load_backgroundAudio(background_music);

		frameMenu = new Menu(); // 시작화면
		frameMenu.waitForClose();

		startWaitingRoom();
	}

	public void startWaitingRoom(String mode, String nickname) {
		frameMenu.setVisible(false);
		waitingRoom = new WaitingRoom(mode, nickname);
		waitingRoom.waitForClose();
		startGame(4);
	}

	public void startGame(int numPlayer) {
		waitingRoom.setVisible(false);
		game = new Game(this, numPlayer);
		game.start();
	}

	public void showMenu() {
		if (game != null) {
			game.close();
		}
		frameMenu.setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
