//src/Game/GameGUI.java
package Game;

import client.*;

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
public class GameGUI extends JPanel implements MyPanel {
	private static final ImageIcon[] gbbImage;

	static {
		gbbImage = new ImageIcon[]{
				new ImageIcon(Main.class.getResource("/images/gawi.jpg")),
				new ImageIcon(Main.class.getResource("/images/bawi.jpg")),
				new ImageIcon(Main.class.getResource("/images/bo.jpg"))
		};
	}

    Color[] playerColors = {
            new Color(173, 216, 230), // Light Blue
            new Color(152, 251, 152), // Pale Green
            new Color(255, 182, 193), // Light Pink
            new Color(230, 230, 250)  // Lavender
    };

    private Image screenImage;
    private Image background = new ImageIcon(Main.class.getResource("/images/Board/board7.png")).getImage();
    private Image rollingDice = new ImageIcon(Main.class.getResource("/images/rollingDice_3.gif")).getImage();
    private ImageIcon[] imagePlayer;

    private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("/images/menuBar.png")));


	public JLabel[] playerLabel;
	private JLabel[] diceNumber;

	public boolean rollDice = false;

	ArrayList<JLabel> playerId = new ArrayList<>();
	ArrayList<JLabel> playerCoin = new ArrayList<>();
	ArrayList<JPanel> playerPanels = new ArrayList<>();
	ArrayList<SpeechBubble> playerBubbles = new ArrayList<>();
	ArrayList<JLabel> playerLaps = new ArrayList<>();

	JButton rollDiceButton;

	ArrayList<Player> playerList = new ArrayList<>();
	private int mouseX, mouseY;
	ClientThread clientThread;
	private Timer currentTimer;

	int numPlayer;
	private int playerIdx;
	private int nowPlayerIdx = 0;
	private JButton play_backgroundMusic_Button;
	private JButton play_actionSound_Button;
	//public SoundEffect soundEffect; //효과음
	public Clip clip;
	Main parent;
	PointManager pointManager;

	private JTextField chatInput;

	WaitingRoom waitingRoom;

	JLabel nowPlayerLabel;
  	public MiniGame miniGame; // 여기 추가
	public Quiz quiz;

	public GameGUI(ClientThread clientThread, Main parent, int numPlayer, String[] playerInfo, WaitingRoom waitingRoom) {
		this.waitingRoom = waitingRoom;
		this.clientThread = clientThread;
		this.parent = parent;
		this.numPlayer = numPlayer;
		this.pointManager = new PointManager();
		miniGame = null;
		quiz = null;

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
			ImageIcon playerIcon = new ImageIcon(Main.class.getResource("/images/Board/player" + i + "_info.png"));
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
//			playerCoinLabel.setBounds(100, 50, 80, 20);
			playerCoinLabel.setBounds(100, 50, 100, 20);

			playerCoinLabel.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			playerCoinLabel.setText("0");
			playerCoin.add(playerCoinLabel);
			updateCoinLabel(i);

			// ** 라운드 수를 표시하는 JLabel 추가 **
			JLabel playerLapLabel = new JLabel();
			playerLapLabel.setBounds(200, 50, 80, 20); // 적절한 위치 설정
			playerLapLabel.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			playerLapLabel.setText("0 Laps"); // 초기 값은 "0 Laps"로 설정
			playerLaps.add(playerLapLabel); // 라운드 수 리스트에 추가

			// 필요시 라운드 업데이트 함수 호출
			updateLapLabel(i);

			playerPanel.add(playerImg);
			playerPanel.add(playerIdLabel);
			playerPanel.add(playerCoinLabel);
			playerPanel.add(playerLapLabel); // 라운드 수 JLabel 추가

			playerPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
			playerPanel.setBounds(1200, 100 * i, 300, 100);
			playerPanel.setBackground(playerColors[i]);
			playerPanel.setLayout(null);

			playerPanels.add(playerPanel);
			add(playerPanel);
			playerPanel.setVisible(true);

			SpeechBubble playerBubble = new SpeechBubble("");
			playerBubble.setSize(200, 50); // 기본 크기
			playerBubble.setVisible(false);

			playerBubbles.add(playerBubble);
			add(playerBubble); // 기존 컴포넌트 위에 추가
			setComponentZOrder(playerBubble, 0); // 말풍선이 다른 컴포넌트들 위로 오도록 설정
		}

		extraPanel.setBorder(new LineBorder(new Color(0, 0, 0)));

		extraPanel.setBounds(1200, 400, 300, 320);
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
//		play_backgroundMusic_Button.setBounds(10, 100, 200, 30);
		play_backgroundMusic_Button.setBounds(10, 50, 200, 30);

//		play_actionSound_Button.setBounds(10, 150, 200, 30);
		play_actionSound_Button.setBounds(10, 90, 200, 30);


		extraPanel.add(button_store);
		extraPanel.add(play_backgroundMusic_Button);
		extraPanel.add(play_actionSound_Button);

		button_store.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("nowplayer : " + nowPlayerIdx + "me : " + playerIdx);
				if (nowPlayerIdx == playerIdx) {
					JFrame newFrame = new JFrame("상점");
					//newFrame.setSize(400, 400);//setLocation(900, 520);
					newFrame.setSize(400, 250);
					newFrame.setLocation(900, 520);

					JRadioButton radioButton1 = new JRadioButton("[ Jump +5 ] : Coin:100");
					JRadioButton radioButton2 = new JRadioButton("[ Atack opponent!! ] : Coin:200");

					ButtonGroup group = new ButtonGroup();
					group.add(radioButton1);
					group.add(radioButton2);

					if (playerList.get(playerIdx).getCoin() < 100) {
						radioButton1.setEnabled(false);
					}

					if (playerList.get(playerIdx).getCoin() < 200) {
						radioButton2.setEnabled(false);
					}
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
									clientThread.sendMessage("USE_ITEM/" + playerIdx + "/1");
								}

							} else if (radioButton2.isSelected()) { // attack
								Player nowPlayer = playerList.get(playerIdx);
								if (nowPlayer.getCoin() >= 200) { // 200으로 수정
									clientThread.sendMessage("USE_ITEM/" + playerIdx + "/2");
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
			}
		});

		extraPanel.add(button_store);

		add(extraPanel);

		extraPanel.setVisible(true);

		revalidate();
		repaint();

		imagePlayer = new ImageIcon[4];
		for (int i = 0; i < 4; i++) {
			imagePlayer[i] = new ImageIcon(Main.class.getResource("/images/Board/player" + i + ".png"));
		}

		diceNumber = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			diceNumber[i] = new JLabel(new ImageIcon(Main.class.getResource("/images/Board/dice" + (i + 1) + "_v2.png")));
//			diceNumber[i].setBounds(540, 240, 200, 220);
//			diceNumber[i].setBounds(540, 190, 200, 220);
			diceNumber[i].setBounds(540, 160, 200, 220);


			diceNumber[i].setVisible(false);
			add(diceNumber[i]);
		}

		playerLabel = new JLabel[4];
		nowPlayerLabel = new JLabel(imagePlayer[0]);
		nowPlayerLabel.setLocation(0, 10);
		nowPlayerLabel.setSize(50, 50);

		nowPlayerLabel.setVisible(false);
		add(nowPlayerLabel);
		nowPlayerLabel.setVisible(true);

		/********* SHOW PLAYER ICONS *********/
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
//		rollDiceButton.setBounds(540, 240, 200, 176);
		rollDiceButton.setBounds(540, 190, 200, 176);

		rollDiceButton.setBorderPainted(false);
		rollDiceButton.setContentAreaFilled(false);
		rollDiceButton.setFocusPainted(false);
		rollDiceButton.setIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButton.png")));
		rollDiceButton.setRolloverIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButtonEntered.png")));
		rollDiceButton.setPressedIcon(new ImageIcon(Main.class.getResource("/images/Board/rollDiceButtonPressed.png")));
		rollDiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (miniGame == null && quiz == null && nowPlayerIdx == playerIdx) {
					clientThread.sendMessage("ROLL_DICE/" + playerIdx);
				}
			}
		});
		add(rollDiceButton);
		// 채팅창 패널 생성
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBounds(1020, 400, 250, 200); // 위치와 크기 설정
		chatPanel.setBorder(new LineBorder(Color.BLACK));


// 채팅 입력 필드 추가 (화면 하단 중앙)
		chatInput = new JTextField();
//		chatInput.setBounds(490, 640, 300, 30);  // 위치 및 크기 설정
		chatInput.setBounds(890, 490, 300, 30);  // 위치 및 크기 설정

		add(chatInput);

// 채팅 입력 필드 이벤트 리스너
		chatInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = chatInput.getText().trim();

				if (message.length() == 0 || message.length() > 10) {
					chatInput.setText("");
					return; // 메시지 전송 중단
				}
				clientThread.sendMessage("IN_GAME_MSG/" + playerIdx + "/" + message);
				chatInput.setText(""); // 입력 필드 초기화
			}
		});

	}

	// 채팅 메시지 전송 메서드
	public void renderChatMessage(int idx, String msg) {
		showBubble(playerBubbles.get(idx), msg, playerPanels.get(idx).getLocation());

	}

	// 말풍선 표시 메서드
	private void showBubble(SpeechBubble bubble, String message, Point p) {
		// 텍스트 줄바꿈 적용
		String formattedMessage = formatMessageWithLineBreaks(message, 20);
		bubble.setText(formattedMessage);

		// 위치와 크기 설정
		bubble.setBounds(p.x - 200, p.y, 200, 50);

		// 말풍선 표시
		bubble.setVisible(true);

		// Z-Order 갱신 및 다시 그리기
		setComponentZOrder(bubble, 0);
		bubble.repaint();
		revalidate();

		// 기존 타이머가 존재하면 종료
		if (currentTimer != null && currentTimer.isRunning()) {
			currentTimer.stop();
		}

		// 새로운 타이머 시작
		currentTimer = new Timer(3000, e -> {
			bubble.setVisible(false);
		});
		currentTimer.setRepeats(false);
		currentTimer.start();
	}


	// 텍스트 줄바꿈 메서드
	private String formatMessageWithLineBreaks(String message, int maxLineLength) {
		StringBuilder formatted = new StringBuilder();
		int lineLength = 0;

		for (char c : message.toCharArray()) {
			formatted.append(c);
			lineLength++;

			if (lineLength >= maxLineLength) {

				lineLength = 0;
			}
		}

		return formatted.toString();
	}

	public void updateCoinLabel(int i) {
		playerCoin.get(i).setText("Coin : " + playerList.get(i).getCoin());
	}

	public void updateID_Label(int i) {
		playerId.get(i).setText("ID : " + playerList.get(i).getName());
	}

	public void updateLapLabel(int i) {
		playerLaps.get(i).setText(playerList.get(i).get_roundMap() + " Laps"); // 라벨에 업데이트
	}

	//주사위 굴러가는스레드
	public void rollDiceMotion(int idx, int dice) {
		Thread DiceThread = new Thread(new Runnable() {
			public void run() {
				rollDice = true;
				rollDiceButton.setVisible(false);
				PlayMusic.play_actionSound("src/audio/Roll-Dice-Sound.wav");

				try {
					Thread.sleep(600);
				} catch (Exception e) {
					e.printStackTrace();
				}

				offRollingDice();
				onDiceNumber(dice);

				for (int i = 0; i < dice; i++) {
					boolean flag = move(idx);
					if (!flag) {
						return;
					}
				}

				offDiceNumber(dice);

				reachGround(idx);

				sameGround(idx);
			}
		});
		DiceThread.start();
	}

	public void increaseOrder() {
		nowPlayerIdx++;
		nowPlayerIdx %= numPlayer;
		nowPlayerLabel.setIcon(imagePlayer[nowPlayerIdx]);
	}

	public boolean move(int idx) {
		Player player = playerList.get(idx);
		Point prePoint = pointManager.getPlayerPoint(idx, player.getPosition());
		player.increPosition(this);
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

		if (player.get_roundMap() >= 1) {
			if (player.getID() == playerIdx) {
				clientThread.sendMessage("GAME_OVER/" + player.getName());
			}
			return false;
		}

		playerMove(player, nextPoint);
		return true;
	}

	public void playerMove(Player _nowPlayer, Point _interPoint) {
		JLabel label = playerLabel[_nowPlayer.getID()];
		label.setLocation(_interPoint);
	}

	public void reachGround(int idx) {
		Player player = playerList.get(idx);
		if (player.getPosition() == 4) {
			if (idx == playerIdx) {
				clientThread.sendMessage("MINI_GAME/" + idx + "/4");
			}
		} else if (player.getPosition() == 8) {
			if (idx == playerIdx) {
				clientThread.sendMessage("MINI_GAME/" + idx + "/8");
			}
		} else if (player.getPosition() == 12) {
			if (idx == playerIdx) {
				clientThread.sendMessage("MINI_GAME/" + idx + "/12");
			}
		} else if (player.getPosition() == 2 || player.getPosition() == 6 || player.getPosition() == 10
					|| player.getPosition() == 14) {
			if (idx == playerIdx) {
				clientThread.sendMessage("QUIZ/" + idx);
			}
		}
		else {
			increaseOrder();
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


	public void item_plus_move(int idx) {
		Player player = playerList.get(idx);
		player.setCoin(player.getCoin() - 100);
		updateCoinLabel(idx);
		rollDiceMotion(idx, 5);
	}

	public void item_attack_move(int idx) {
		int max=0;
		int maxPlayerIdx = 0;

		Player player = playerList.get(idx);
		player.setCoin(player.getCoin() - 200);
		updateCoinLabel(idx);

		for(int i = 0; i < numPlayer ; i++) { // 플레이어 수 만큼 반복
			if(idx != i) { // 만약 i가 현재 플레이어의 인덱스를 지칭하는 게 아닐 경우
				if(max < playerList.get(i).getPosition()) {
					max = playerList.get(i).getPosition();
					maxPlayerIdx = i;
				}
			}
		}

		int ID = player.getID();
		Point jumpMove = pointManager.getPlayerPoint(ID, player.getPosition());

		for (int i = 0; i < 10; i++) {
			Point interPoint = new Point((jumpMove.x + (i * 10)), (jumpMove.y - (i * 10)));
			try {
				playerMove(player, interPoint);
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		player.setPosition(max);
		Point jumpPoint = pointManager.getPlayerPoint(ID, max);
		playerMove(player, jumpPoint);
		overlap_move(playerList.get(maxPlayerIdx));
		increaseOrder();
	}


	  public void miniGameStart(int idx, int gameType) {
		Player player = playerList.get(idx);
		if (gameType == 4) {
			miniGame = new Map4_GBBGame(player, idx == playerIdx, gbbImage, parent, clientThread);
		} else if (gameType == 8) {
			miniGame = new Map8_GamblingWIthThread(player, idx == playerIdx, parent, clientThread);
		} else if (gameType == 12) {
			miniGame = new Map12_BulletGameFrame(player, idx == playerIdx, parent, clientThread);
		}
	}

	public void quizStart(int idx, String question) {
		Player player = playerList.get(idx);
		quiz = new Quiz(player, parent, idx == playerIdx, clientThread, question);
	}

	public void endQuiz(String msg) {
		increaseOrder();
		quiz.end(msg);
		for(int i=0 ;i<playerList.size(); i++) {
			updateCoinLabel(i);
		}
		quiz = null;
	}

	public void endGame() {
			increaseOrder();
			miniGame.end();
			for(int i=0 ;i<playerList.size(); i++) {
				updateCoinLabel(i);
			}
			miniGame = null;
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
		screenImage = createImage(1200, 720);
		screenDraw((Graphics2D) screenImage.getGraphics());
		g.drawImage(screenImage, 0, 0, null);
	}

	public void screenDraw(Graphics2D g) {
//		g.drawImage(background, 0, 0, null);
		g.drawImage(background, 0, -50, null);

		if (rollDice)
//			g.drawImage(rollingDice, 540, 220, null);
			g.drawImage(rollingDice, 540, 170, null);

		paintComponents(g);
		repaint();
	}

	public void exitGame(String nickname) {
		// 커스텀 메시지 창 생성
		// 사용자 확인 버튼 없이 메시지만 보여주는 비모달 JDialog 생성
		JDialog dialog = new JDialog(parent, "게임 종료", false); // 비모달 설정 (false)
		dialog.setLayout(new BorderLayout());
		JLabel label = new JLabel(nickname + "님의 우승!!", SwingConstants.CENTER);
		label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		dialog.add(label, BorderLayout.CENTER);
		dialog.setSize(300, 150);
		dialog.setLocationRelativeTo(parent);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 기본 닫기 설정
		PlayMusic.play_actionSound("src/audio/GameVictory.wav");
		dialog.setVisible(true);

		// 일정 시간 후에 다이얼로그와 프레임 종료
		Timer timer = new Timer(5000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose(); // 다이얼로그 닫기
				parent.setScreenNotGameSize();
				parent.setPanel(waitingRoom);
			}
		});
		timer.setRepeats(false); // 타이머 반복 방지
		timer.start();
	}


	@Override
	public void exitRoom() {

	}
}