package Game;

import client.ClientThread;
import client.Main;
import client.PlayMusic;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Map12_BulletGameFrame extends JFrame implements MiniGame {
	public Clip clip;
	private Player player;
	private boolean isPlayer;
	private ClientThread clientThread;
	private GamePanel gamePanel;
	private int shotCount = 0;

	public Map12_BulletGameFrame(Player player, boolean isPlayer, JFrame parentFrame, ClientThread clientThread) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.player = player;
		this.isPlayer = isPlayer;
		this.clientThread = clientThread;
		setTitle("미니게임- 돼지 사냥");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		gamePanel = new GamePanel();
		setContentPane(gamePanel);
		setSize(300, 300);

		if (parentFrame != null) {
			int parentX = parentFrame.getX();
			int parentY = parentFrame.getY();
			int parentWidth = parentFrame.getWidth();
			int parentHeight = parentFrame.getHeight();

			int x = parentX + (parentWidth - getWidth()) / 2;
			int y = parentY + (parentHeight - getHeight()) / 2;
			setLocation(x, y);
		}

		setVisible(true);
		gamePanel.startGame();
		System.out.println("Map12 돼지 사냥 게임 시작");

		if (isPlayer) {
			clientThread.sendMessage("MINI_GAME_START/12");
		}
	}

	@Override
	public void update(String msg) {
		if (msg.equals("DRAW_BULLET")) {
			PlayMusic.play_actionSound("src/audio/M1-Sound.wav");
			shotCount++;
			gamePanel.drawBullet();
			gamePanel.updateShotCountLabel();
		} else if (msg.equals("HIT")) {
			gamePanel.resetTargetPosition();
			PlayMusic.play_actionSound("src/audio/Pig.wav");
			gamePanel.incrementHitCount();
		}
	}

	@Override
	public void end() {
		PlayMusic.play_actionSound("src/audio/PigEnd.wav");
		int hitCount = gamePanel.getHitCount();
		// 게임 판정
		String message;
		player.setCoin(player.getCoin() + hitCount * 50);
		message = player.getName() + " : " + hitCount + "마리 사냥 : " + hitCount * 50 + "코인 획득!";

		JDialog dialog = new JDialog(this, "게임 결과", false);
		dialog.setLayout(new BorderLayout());
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		dialog.add(label, BorderLayout.CENTER);
		dialog.setSize(300, 150);
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 기본 닫기 설정
		dialog.setVisible(true);

		// 일정 시간 후에 다이얼로그와 프레임 종료
		Timer timer = new Timer(2000, e -> {
			dialog.dispose(); // 다이얼로그 닫기
			dispose(); // JFrame 종료
		});
		timer.setRepeats(false); // 타이머 반복 방지
		timer.start();
	}

	class GamePanel extends JPanel {
		private TargetThread targetThread;
		private JLabel baseLabel = new JLabel();
		private JLabel targetLabel;
		private JLabel shotCountLabel = new JLabel();
		private int hitCount = 0;

		public GamePanel() {
			setLayout(null);

			baseLabel.setSize(40, 40);
			baseLabel.setOpaque(true);
			baseLabel.setBackground(Color.BLACK);

			ImageIcon img = new ImageIcon(Main.class.getResource("/images/pig.png"));
			targetLabel = new JLabel(img);
			targetLabel.setSize(img.getIconWidth(), img.getIconHeight());

			shotCountLabel.setText("Shot Count: 0");
			shotCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
			shotCountLabel.setForeground(Color.BLACK);
			shotCountLabel.setSize(100, 20);
			shotCountLabel.setLocation(10, 10);

			add(baseLabel);
			add(targetLabel);
			add(shotCountLabel);

			if (isPlayer) {
				this.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						baseLabel.setFocusable(true);
						baseLabel.requestFocus();
					}
				});

				baseLabel.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyChar() == '\n' && shotCount < 3) {
							clientThread.sendMessage("");
						}
					}
				});
			}
		}

		public int getHitCount() {
			return hitCount;
		}

		public void incrementHitCount() {
			hitCount++;
		}

		public void startGame() {
			baseLabel.setLocation(getWidth() / 2 - 20, getHeight() - 40);
			targetLabel.setLocation(0, 0);

			baseLabel.setFocusable(true);
			baseLabel.requestFocus();
			targetThread = new TargetThread(targetLabel);
			targetThread.start();
		}

		public void drawBullet() {
			JLabel bulletLabel = new JLabel();
			bulletLabel.setSize(10, 10);
			bulletLabel.setOpaque(true);
			bulletLabel.setBackground(Color.RED);
			bulletLabel.setLocation(getWidth() / 2 - 5, getHeight() - 50);
			add(bulletLabel);
			repaint();
			new BulletThread(bulletLabel, targetLabel).start();
		}

		public void resetTargetPosition() {
			targetLabel.setLocation(0, 0);
		}

		public void updateShotCountLabel() {
			shotCountLabel.setText("Shot Count: " + shotCount + "/ 3");
		}

		class TargetThread extends Thread {
			private JComponent target;

			public TargetThread(JComponent target) {
				this.target = target;
				target.setLocation(0, 0);
				target.getParent().repaint();
			}

			@Override
			public void run() {
				while (true) {
					int x = target.getX() + (int) (Math.random() * 10);
					int y = target.getY();
					if (x > getWidth())
						target.setLocation(0, 0);
					else
						target.setLocation(x, y);

					target.getParent().repaint();
					try {
						sleep(20);
					} catch (InterruptedException e) {
						target.setLocation(0, (int) (Math.random() * getHeight()));
						target.getParent().repaint();
						break;
					}
				}
			}
		}

		class BulletThread extends Thread {
			private JComponent bullet;
			private JComponent target;

			public BulletThread(JComponent bullet, JComponent target) {
				this.bullet = bullet;
				this.target = target;
			}

			@Override
			public void run() {
				while (true) {
					if (hit() ) {
						if (isPlayer) {
							clientThread.sendMessage("HIT");
						}
						SwingUtilities.invokeLater(() -> bullet.setVisible(false));
						break;
					} else {
						int x = bullet.getX();
						int y = bullet.getY() - 5;
						if (y < 0) {
							SwingUtilities.invokeLater(() -> bullet.setVisible(false));
							break;
						}
						SwingUtilities.invokeLater(() -> bullet.setLocation(x, y));
					}
					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (shotCount >= 3) {
					clientThread.sendMessage("END");
				}
			}

			private boolean hit() {
				return target.getBounds().intersects(bullet.getBounds());
			}
		}
	}
}
