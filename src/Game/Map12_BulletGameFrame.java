package Game;
//src/Game/Map12_BulletGameFrame.java

import client.Main;
import client.PlayMusic;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Map12_BulletGameFrame extends JFrame implements MiniGame {
	private boolean gameEnded = false; // 게임 종료 상태를 추적하는 플래그

	@Override
	public void onMiniGameEnd() {
		System.out.println("Map12 돼지 사냥 게임 종료");
	}
	@Override
	public boolean isGameEnded() {
		return gameEnded; // 현재 게임 종료 상태 반환
	}

	@Override
	public void update(String msg) {

	}

	@Override
	public void end() {

	}

	private int hit_cnt = 0;
	//private SoundEffect soundEffect ;
	public Clip clip;

	private Player player; // 멤버 변수로 선언
	private boolean isPlayer;
		
	public Map12_BulletGameFrame(Player player, boolean isPlayer, JFrame  parentFrame) {
		this.player = player;
		this.isPlayer = isPlayer;
		setTitle("미니게임- 돼지 사냥");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		GamePanel p = new GamePanel();
		//SoundEffect soundEffect = new SoundEffect();
		
		
		setContentPane(p);
		setSize(300, 300); //1280 - 300 980
		if (parentFrame != null) {
			int parentX = parentFrame.getX();
			int parentY = parentFrame.getY();
			int parentWidth = parentFrame.getWidth();
			int parentHeight = parentFrame.getHeight();

			// 자식 JFrame의 위치 계산
			int x = parentX + (parentWidth - getWidth()) / 2;
			int y = parentY + (parentHeight - getHeight()) / 2;
			setLocation(x, y);
		}
		//setLocationRelativeTo(null); // 화면 가운데에 창 위치
		setVisible(true);
		p.startGame();
		System.out.println("Map12 돼지 사냥 게임 시작");
	}



	class GamePanel extends JPanel {
		private TargetThread targetThread = null;
		private BulletThread bulletThread = null;
		private JLabel baseLabel = new JLabel();
		private JLabel bulletLabel = new JLabel();
		private JLabel targetLabel;
		private JLabel shotCountLabel = new JLabel(); // 발사 횟수를 표시하는 레이블 추가
		private int shotCount = 0; // 발사 횟수 변수 추가

		public GamePanel() {
			setLayout(null);

			baseLabel.setSize(40, 40);
			baseLabel.setOpaque(true);
			baseLabel.setBackground(Color.BLACK);

			ImageIcon img = new ImageIcon(Main.class.getResource("/images/pig.png"));
			//client.Main.class.getResource("images/Board/player0_info.png")
			//ImageIcon img = new ImageIcon("./src/images/pig.png");
			//new ImageIcon(client.Main.class.getResource("images/Board/player0_info.png"));
			targetLabel = new JLabel(img);
			targetLabel.setSize(img.getIconWidth(), img.getIconWidth());

			bulletLabel.setSize(10, 10);
			bulletLabel.setOpaque(true);
			bulletLabel.setBackground(Color.RED);

			// 발사 횟수를 표시하는 레이블 초기화
			shotCountLabel.setText("Shot Count: 0");
			shotCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
			shotCountLabel.setForeground(Color.BLACK);
			shotCountLabel.setSize(100, 20);
			shotCountLabel.setLocation(10, 10);

			add(baseLabel);
			add(targetLabel);
			add(bulletLabel);
			add(shotCountLabel);

			if (isPlayer) {
				this.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						baseLabel.setFocusable(true);
						baseLabel.requestFocus();
					}
				});
			}
		}

		public void startGame() {
			baseLabel.setLocation(getWidth() / 2 - 20, getHeight() - 40);
			bulletLabel.setLocation(getWidth() / 2 - 5, getHeight() - 50);
			targetLabel.setLocation(0, 0);

			targetThread = new TargetThread(targetLabel);
			targetThread.start();

			baseLabel.setFocusable(true);
			baseLabel.requestFocus();

			if (isPlayer) {
				baseLabel.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyChar() == '\n') {
							if (bulletThread == null || !bulletThread.isAlive()) {
								bulletThread = new BulletThread(bulletLabel, targetLabel, targetThread);
								bulletThread.start();
								//soundEffect.playSound("src/audio/M1-Sound.wav",false);

								shotCount++;
								updateShotCountLabel();
								PlayMusic.play_actionSound("src/audio/M1-Sound.wav");
								if (shotCount == 3) {
									Timer timer = new Timer(2000, new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											PlayMusic.play_actionSound("src/audio/PigEnd.wav");
											JOptionPane.showMessageDialog(null,
													"사격 완료\n명중 : " + hit_cnt + "발 / 보상 : " + (hit_cnt * 20), "알림",
													JOptionPane.INFORMATION_MESSAGE);

											player.setCoin(player.getCoin() + 20 * hit_cnt);
											dispose();
										}
									});
									timer.setRepeats(false); // 타이머가 한 번만 실행되도록 설정
									timer.start(); // 타이머 시작
								}

							}
						}
					}
				});
			}
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

					if (System.currentTimeMillis() % 500 == 0) {
						int newSpeed = (int) (Math.random() * 20) + 1;
						if (x < getWidth())
							target.setLocation(x + newSpeed, y);
					}

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
			private JComponent bullet, target;
			private Thread targetThread;

			public BulletThread(JComponent bullet, JComponent target, Thread targetThread) {
				this.bullet = bullet;
				this.target = target;
				this.targetThread = targetThread;
			}

			@Override
			public void run() {
				while (true) {
					if (hit()) {
						bullet.setLocation(bullet.getParent().getWidth() / 2 - 5, bullet.getParent().getHeight() - 50);
						
						
						// player.setCoin(player.getCoin() + 7);
						hit_cnt++;
						PlayMusic.play_actionSound("src/audio/Pig.wav");
						JOptionPane.showMessageDialog(null, "명중", "알림", JOptionPane.INFORMATION_MESSAGE);
						break;
					} else {
						int x = bullet.getX();
						int y = bullet.getY() - 5;
						if (y < 0) {
							bullet.setLocation(bullet.getParent().getWidth() / 2 - 5,
									bullet.getParent().getHeight() - 50);
							bullet.getParent().repaint();
							return;
						}
						bullet.setLocation(x, y);
						bullet.getParent().repaint();
					}
					try {
						sleep(20);
					} catch (InterruptedException e) {
					}
				}
			}

			private boolean hit() {
				if (targetContains(bullet.getX(), bullet.getY())
						|| targetContains(bullet.getX() + bullet.getWidth() - 1, bullet.getY())
						|| targetContains(bullet.getX() + bullet.getWidth() - 1, bullet.getY() + bullet.getHeight() - 1)
						|| targetContains(bullet.getX(), bullet.getY() + bullet.getHeight() - 1))
					return true;
				else
					return false;
			}

			private boolean targetContains(int x, int y) {
				if (((target.getX() <= x) && (target.getX() + target.getWidth() - 1 >= x))
						&& ((target.getY() <= y) && (target.getY() + target.getHeight() - 1 >= y))) {
					return true;
				} else
					return false;
			}
		}


		
		// 발사 횟수를 표시하는 레이블 업데이트 메서드
		private void updateShotCountLabel() {
			shotCountLabel.setText("Shot Count: " + shotCount + "/ 3");
		}
	}
}