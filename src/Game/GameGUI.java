package Game;

import client.ClientThread;
import client.Main;
import client.PlayMusic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class GameGUI extends JPanel {
	Color[] playerColors = {
			new Color(173, 216, 230), // Light Blue
			new Color(152, 251, 152), // Pale Green
			new Color(255, 182, 193), // Light Pink
			new Color(230, 230, 250)  // Lavender
	};

	private Image screenImage;
	private Image background = new ImageIcon(Main.class.getResource("/images/Board/board.png")).getImage();
	private Image rollingDice = new ImageIcon(Main.class.getResource("/images/rollingDice_3.gif")).getImage();
	private ImageIcon[] imagePlayer;

	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("/images/menuBar.png")));

	public JLabel[] playerLabel;
	private JLabel[] diceNumber;

	public boolean rollDice = false;

	ArrayList<JLabel> playerId = new ArrayList<>();
	ArrayList<JLabel> playerCoin = new ArrayList<>();

	JButton rollDiceButton;

	ArrayList<Player> playerList = new ArrayList<>();
	private int mouseX, mouseY;
	ClientThread clientThread;

	int numPlayer;
	private int playerIdx;
	private JButton play_backgroundMusic_Button;
	private JButton play_actionSound_Button;
	//public SoundEffect soundEffect; //효과음
	public Clip clip;
	Main parent;
	PointManager pointManager;

	public GameGUI(ClientThread clientThread, Main parent, int numPlayer, String[] playerInfo) {
		this.clientThread = clientThread;
		this.parent = parent;
		this.numPlayer = numPlayer;
		this.pointManager = new PointManager();

		parent.setScreenGameSize();
		setSize(1500, 720);
		setBackground(Color.PINK);

		setLayout(null);

		// 추가 패널
		JPanel extraPanel = new JPanel();

		for (int i = 0; i < playerInfo.length; i++) {
			String[] infoParts = playerInfo[i].split(":");
			String playerName = infoParts[0];
			if (clientThread.getNickname().equals(playerName)) {
				playerIdx = Integer.parseInt(infoParts[1]) - 1;
			}

			Player player = new Player(i, playerName);
			playerList.add(player);

			JPanel playerPanel = new JPanel();

			// 예시: 플레이어 별 정보 설정
			JLabel playerImg = new JLabel();
			ImageIcon playerIcon = new ImageIcon(Main.class.getResource("/images/Board/player"+i+"_info.png"));
			playerImg.setIcon(playerIcon);
			playerImg.setBounds(10, 10, 80, 80);
			playerImg.setHorizontalAlignment(JLabel.CENTER);
			playerImg.setBorder(new LineBorder(new Color(254, 246, 213)));

			JLabel playerIdLabel = new JLabel();
			playerIdLabel.setBounds(100, 10, 100, 20);
			playerIdLabel.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			playerId.add(playerIdLabel);

			updateID_Label(i);

			JLabel playerCoinLabel = new JLabel();
			playerCoinLabel.setBounds(100, 50, 80, 20);
			playerCoinLabel.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			playerCoin.add(playerCoinLabel);

			playerPanel.add(playerImg);
			playerPanel.add(playerIdLabel);
			playerPanel.add(playerCoinLabel);

			playerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			playerPanel.setBounds(1280, 100 * i, 220, 100);
			playerPanel.setBackground(playerColors[i]);
			playerPanel.setLayout(null);

			add(playerPanel);
			playerPanel.setVisible(true);
		}

		extraPanel.setBorder(new LineBorder(new Color(0, 0, 0)));

		extraPanel.setBounds(1280, 400, 220, 320);
		extraPanel.setBackground(new Color(255, 255, 255));
		extraPanel.setLayout(null);

		JButton button_store = new JButton("상점가기");

		play_backgroundMusic_Button = new JButton("Background Play/Pause");
		play_backgroundMusic_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PlayMusic.check_background_Play()) {
					PlayMusic.stop_Background_Audio();
					play_backgroundMusic_Button.setText("Background Play");
				} else {
					PlayMusic.play_Background_Audio();
					play_backgroundMusic_Button.setText("Background Pause");
				}
			}
		});

		play_actionSound_Button = new JButton("Action Play/Pause");
		play_actionSound_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PlayMusic.check_action_Play()) {
					PlayMusic.stop_action_Audio();
					play_actionSound_Button.setText("Action Play");
				} else {
					PlayMusic.play_action_Audio();
					play_actionSound_Button.setText("Action Pause");
				}
			}
		});

		extraPanel.setLayout(null); // 배치관리자를 null로 설정
		button_store.setBounds(10, 10, 200, 30); // x, y, width, height
		play_backgroundMusic_Button.setBounds(10, 100, 200, 30);
		play_actionSound_Button.setBounds(10, 150, 200, 30);

		extraPanel.add(button_store);
		extraPanel.add(play_backgroundMusic_Button);
		extraPanel.add(play_actionSound_Button);

		button_store.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame newFrame = new JFrame("상점");
				//newFrame.setSize(400, 400);//setLocation(900, 520);
				newFrame.setSize(400, 250);
				newFrame.setLocation(900, 520);
				JRadioButton radioButton1 = new JRadioButton("[ Jump +5 ] : Coin:100");
				JRadioButton radioButton2 = new JRadioButton("[ Atack opponent!! ] : Coin:200");

				ButtonGroup group = new ButtonGroup();
				group.add(radioButton1);
				group.add(radioButton2);

				JPanel panel = new JPanel();
				panel.add(radioButton1);
				panel.add(radioButton2);

				JButton submitButton = new JButton("Submit");



				submitButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (radioButton1.isSelected()) {
							Player nowPlayer = playerList.get(playerIdx);
							if (nowPlayer.getCoin() >= 100) {
								nowPlayer.useCoin(100);
								JOptionPane.showMessageDialog(null,
										"[ 아이템 사용 ]\n" + nowPlayer.getName() + "의 보유 코인 : " + nowPlayer.getCoin());
								nowPlayer.set_use_item(1);
							} else {
								JOptionPane.showMessageDialog(null,
										"[ 코인 부족 ]\n" + nowPlayer.getName() + "의 보유 코인 : " + nowPlayer.getCoin());
							}

						} else if (radioButton2.isSelected()) { // attack
							Player nowPlayer = playerList.get(playerIdx);
							if (nowPlayer.getCoin() >= 200) { // 200으로 수정
								nowPlayer.useCoin(200);// 200으로 수정
								JOptionPane.showMessageDialog(null,
										"[ 아이템 사용 ]\n" + nowPlayer.getName() + "의 보유 코인 : " + nowPlayer.getCoin());
								nowPlayer.set_use_item(2);
							} else {
								JOptionPane.showMessageDialog(null,
										"[ 코인 부족 ]\n" + nowPlayer.getName() + "의 보유 코인 : " + nowPlayer.getCoin());
							}
						}
						group.clearSelection();
						newFrame.dispose();
					}
				});
				panel.add(submitButton);

				newFrame.add(panel);
				//newFrame.setLocationRelativeTo(controller);
				newFrame.setVisible(true);

			}

		});

		extraPanel.add(button_store);

		add(extraPanel);

		extraPanel.setVisible(true);

		revalidate();
		repaint();

		//pointManager = game.pointManager;
		//playerList = game.playerList;
		//setLayout(null);
		//setBounds(0, 0, client.Main.SCREEN_WIDTH, client.Main.SCREEN_HEIGHT);
		//setBounds(0, 0, 1280, 720);

		imagePlayer = new ImageIcon[4];
		for (int i = 0; i < 4; i++) {
			imagePlayer[i] = new ImageIcon(Main.class.getResource("/images/Board/player" + i + ".png"));
		}

		diceNumber = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			diceNumber[i] = new JLabel(new ImageIcon(Main.class.getResource("/images/Board/dice" + (i + 1) + ".png")));
			diceNumber[i].setBounds(540, 240, 200, 220);
			diceNumber[i].setVisible(false);
			add(diceNumber[i]);
		}

		/********* SHOW PLAYER ICONS *********/
		playerLabel = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			Point point = pointManager.getPlayerPoint(i, 0);
			playerLabel[i] = new JLabel(imagePlayer[i]);
			playerLabel[i].setLocation(point.x, point.y);
			playerLabel[i].setSize(30, 30);
			playerLabel[i].setVisible(false);
			add(playerLabel[i]);
		}
		for (int i = 0; i < numPlayer; i++) {
			playerLabel[i].setVisible(true);
		}

		rollDiceButton = new JButton();
		rollDiceButton.setBounds(540, 240, 200, 176);
		rollDiceButton.setBorderPainted(false);
		rollDiceButton.setContentAreaFilled(false);
		rollDiceButton.setFocusPainted(false);
		rollDiceButton.setIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButton.png")));
		rollDiceButton.setRolloverIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButtonEntered.png")));
		rollDiceButton.setPressedIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButtonPressed.png")));
		rollDiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clientThread.sendMessage("ROLL_DICE/" + playerIdx);
			}
		});
		add(rollDiceButton);

		menuBar.setBounds(0, 0, 1280, 30);
		menuBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});

		add(menuBar);
		setComponentZOrder(menuBar, 1);

		JButton closeButton = new JButton();
		closeButton.setBounds(1245, 0, 30, 30);
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusPainted(false);
		closeButton.setIcon(new ImageIcon(Main.class.getResource("/images/MainMenu/closeButton.png")));
		closeButton.setRolloverIcon(new ImageIcon(Main.class.getResource("/images/MainMenu/closeButtonEntered.png")));
		closeButton.setPressedIcon(new ImageIcon(Main.class.getResource("/images/MainMenu/closeButtonPressed.png")));
		add(closeButton);
		setComponentZOrder(closeButton, 0);

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	public void updateCoinLabel(int i) {
		playerCoin.get(i).setText("Coin : " + playerList.get(i).getCoin());
	}

	public void updateID_Label(int i) {
		playerId.get(i).setText("ID : " + playerList.get(i).getName());
	}

	//주사위 굴러가는스레드
	public void rollDiceMotion(int idx, int dice) {

		new Thread(new Runnable() {
			public void run() {
				rollDice = true;
				rollDiceButton.setVisible(false);
				PlayMusic.play_actionSound("src/audio/Roll-Dice-Sound.wav");
				//client.Main.class.getResource("audio/Roll-Dice-Sound.wav"))
				//game.playSound(client.Main.class.getResource("audio/Roll-Dice-Sound.wav").toString(),false);
				try {
					Thread.sleep(600);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//client.PlayMusic.loadAudio(Roll_music);
				//game.playSound("src/audio/Roll-Dice-Sound.wav",false);

				offRollingDice();
				onDiceNumber(dice);

				for (int i = 0; i < dice; i++) {
					move(idx);
				}
				offDiceNumber(dice);

				reachGround(idx);
				sameGround(idx);

			}
		}).start();
	}


	public void move(int idx) {
		Player player = playerList.get(idx);
		Point prePoint = pointManager.getPlayerPoint(idx, player.getPosition());
		player.increPosition();
		Point nextPoint = pointManager.getPlayerPoint(idx, player.getPosition());
		for (int i = 0; i < 20; i++) {
			Point interPoint = new Point((prePoint.x * (20 - i) + nextPoint.x * i) / 20,
					(prePoint.y * (20 - i) + nextPoint.y * i) / 20);
			try {
				playerMove(player, interPoint);
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		playerMove(player, nextPoint);
	}


	public void playerMove(Player _nowPlayer, Point _interPoint) {
		JLabel label = playerLabel[_nowPlayer.getID()];
		label.setLocation(_interPoint);
	}

	public void reachGround(int idx) {
		Player player = playerList.get(idx);
		if (player.getPosition() == 4) {
			new Map4_GBBGame(player);
		} else if (player.getPosition() == 8) {
			new Map8_GamblingWIthThread(player);
		} else if (player.getPosition() == 12) {
			new Map12_BulletGameFrame(player);
		} else if (player.getPosition() == 0) {

		} else if (player.getPosition() == 2 || player.getPosition() == 6 || player.getPosition() == 10
				|| player.getPosition() == 14) {
			new Quiz(player);
			//new Game.Map8_GamblingWIthThread(player);
		}
	}

	public void sameGround(int idx) {
		Player player = playerList.get(idx);
		int nowPlayer_arrive = player.getPosition(); // 현재 플레이어의 위치값 저장
		for (int i = 0; i < numPlayer; i++) { // 플레이어 수 만큼 반복
			if (idx != i) { // 만약 i가 현재 플레이어의 인덱스를 지칭하는 게 아닐 경우
				int check_overlap = playerList.get(i).getPosition();
				if (nowPlayer_arrive == check_overlap) { // 만약 기존 플레이어가 잡힌 경우
					//JOptionPane.showMessageDialog(null, "' " + playerList.get(i).getName() + " '" + "를 잡았다!");
					overlap_move(playerList.get(i));
					break;
				}
			}
		}
	}

	public void overlap_move(Player player) { // 만약 같은 위치에 있는 경우 기존 플레이어 시작으로 보냄
		int ID = player.getID();
		System.out.println("reset player : " + ID);
		Point catchMove = pointManager.getPlayerPoint(ID, player.getPosition());

		for (int i = 0; i < 10; i++) {
			Point interPoint = new Point(catchMove.x, (catchMove.y - (i * 10)));
			try {
				playerMove(player, interPoint);
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		player.resetPosition();
		Point startPoint = pointManager.getPlayerPoint(ID, 0);
		PlayMusic.play_actionSound("src/audio/SameGround.wav");
		playerMove(player, startPoint);
	}


	public void offRollingDice() {
		rollDice = false;
	}

	public void onDiceNumber(int _diceNum) {
		diceNumber[_diceNum - 1].setVisible(true);

	}

	public void offDiceNumber(int _diceNum) {
		diceNumber[_diceNum - 1].setVisible(false);
		rollDiceButton.setVisible(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		//paintComponent(g); // 기존 컴포넌트 그리기
		//screenImage = createImage(client.Main.SCREEN_WIDTH, client.Main.SCREEN_HEIGHT);
		screenImage = createImage(1280, 720);
		screenDraw((Graphics2D) screenImage.getGraphics());
		g.drawImage(screenImage, 0, 0, null);
	}

	public void screenDraw(Graphics2D g) {
		g.drawImage(background, 0, 0, null);
		if (rollDice)
			g.drawImage(rollingDice, 540, 220, null);
		paintComponents(g);
		repaint();
	}



}