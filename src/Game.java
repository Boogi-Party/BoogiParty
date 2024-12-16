
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
// 여기 추가해야 됨

public class Game extends Thread {
	public int numPlayer;
	private int playerIdx;
	private int dice;
	private int use_item;
	// private boolean moved;
	//private String Roll_music = "audio/Roll-Dice-Sound.wav";
	//PlayMusic.loadAudio(Roll_music);
	public PointManager pointManager = new PointManager();
	public GameGUI gameGUI;
	ArrayList<Player> playerList;

	private JButton play_backgroundMusic_Button;
	private JButton play_actionSound_Button;
	//public SoundEffect soundEffect; //효과음
	public Clip clip;
	
	Main controller;

	// player Info
	JPanel player1Info, player2Info, player3Info, player4Info, extraPanel;
	JLabel player1Img, player2Img, player3Img, player4Img;
	JLabel player1Coin, player2Coin, player3Coin, player4Coin;
	JLabel player1Id, player2Id, player3Id, player4Id;

	Game(Main c, int numPlayer) {
		
		// 추가 패널 
		extraPanel = new JPanel();

		// info 구성
		player1Info = new JPanel();
		player2Info = new JPanel();
		player3Info = new JPanel();
		player4Info = new JPanel();
		//
		player1Img = new JLabel();
		player2Img = new JLabel();
		player3Img = new JLabel();
		player4Img = new JLabel();
		//

		player1Id = new JLabel();
		player2Id = new JLabel();
		player3Id = new JLabel();
		player4Id = new JLabel();

		player1Coin = new JLabel(); // scoreLabel = new JLabel("Coin: 0");
		player2Coin = new JLabel();
		player3Coin = new JLabel();
		player4Coin = new JLabel();

		player1Info.setBorder(new LineBorder(new Color(0, 0, 0)));
		player2Info.setBorder(new LineBorder(new Color(0, 0, 0)));
		player3Info.setBorder(new LineBorder(new Color(0, 0, 0)));
		player4Info.setBorder(new LineBorder(new Color(0, 0, 0)));
		extraPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		//
		// player1Img.setIcon(new ImageIcon("images/Board/player0.png"));
		// player1Img = new JLabel();
		ImageIcon playerIcon1 = new ImageIcon(Main.class.getResource("images/Board/player0_info.png"));
		player1Img.setIcon(playerIcon1);

		ImageIcon playerIcon2 = new ImageIcon(Main.class.getResource("images/Board/player1_info.png"));
		player2Img.setIcon(playerIcon2);

		ImageIcon playerIcon3 = new ImageIcon(Main.class.getResource("images/Board/player2_info.png"));
		player3Img.setIcon(playerIcon3);
		ImageIcon playerIcon4 = new ImageIcon(Main.class.getResource("images/Board/player3_info.png"));
		player4Img.setIcon(playerIcon4);

		// player2Img.setIcon(new ImageIcon("images/Board/player1.png"));
		// player3Img.setIcon(new ImageIcon("images/Board/player2.png"));
		// player4Img.setIcon(new ImageIcon("images/Board/player3.png"));
		//
		this.numPlayer = numPlayer;
		playerIdx = 0;
		playerList = new ArrayList<Player>();

		//// 플레이어1 이미지, 아이디, 보유 코인
		playerList.add(new Player(0, "상상부기"));
		player1Img.setBounds(10, 10, 80, 80);
		player1Img.setHorizontalAlignment(JLabel.CENTER);
		player1Img.setBorder(new LineBorder(new Color(254, 246, 213)));
		player1Img.setVisible(true);
		player1Id.setBounds(100, 10, 100, 20);
		player1Id.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
		player1Id.setVisible(true);
		player1Coin.setBounds(100, 50, 80, 20);
		player1Coin.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
		// 플레이어2 이미지, 아이디, 보유 코인
		playerList.add(new Player(1, "꼬꼬&꾸꾸"));
		player2Img.setBounds(10, 10, 80, 80);
		player2Img.setHorizontalAlignment(JLabel.CENTER);
		player2Img.setBorder(new LineBorder(new Color(254, 246, 213)));
		player2Img.setVisible(true);
		player2Id.setBounds(100, 10, 100, 20);
		player2Id.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
		player2Coin.setBounds(100, 50, 80, 20);
		player2Coin.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));

		if (numPlayer >= 3) {
			playerList.add(new Player(2, "상찌"));
			// 플레이어3 이미지, 아이디, 보유 코인
			player3Img.setBounds(10, 10, 80, 80);
			player3Img.setHorizontalAlignment(JLabel.CENTER);
			player3Img.setBorder(new LineBorder(new Color(254, 246, 213)));
			player3Img.setVisible(true);
			player3Id.setBounds(100, 10, 100, 20);
			player3Id.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			player3Coin.setBounds(100, 50, 80, 20);
			player3Coin.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
		}
		if (numPlayer == 4) {
			// 플레이어4 이미지, 아이디, 보유 코인
			playerList.add(new Player(3, "한성냥이"));
			player4Img.setBounds(10, 10, 80, 80);
			player4Img.setHorizontalAlignment(JLabel.CENTER);
			player4Img.setBorder(new LineBorder(new Color(254, 246, 213)));
			player4Img.setVisible(true);
			player4Id.setBounds(100, 10, 100, 20);
			player4Id.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));
			player4Coin.setBounds(100, 50, 80, 20);
			player4Coin.setFont(new Font("CookieRun BLACK", Font.BOLD, 14));

		}

		// moved = false;
		controller = c; // Main의 JFrame 이 controller이다. 여기다가 info panel도 붙여야한다.
		gameGUI = new GameGUI(this);
		gameGUI.setVisible(true);
		// Player Info
		player1Info.setBounds(1280, 0, 220, 100);
		// player1Info.setBackground(new Color(254,236,203)); //색상
		player1Info.setBackground(new Color(173, 216, 230));
		player1Info.setLayout(null);

		player2Info.setBounds(1280, 100, 220, 100);
		// player2Info.setBackground(new Color(153,153,255));
		player2Info.setBackground(new Color(152, 251, 152));
		player2Info.setLayout(null);

		player3Info.setBounds(1280, 200, 220, 100);
		// player3Info.setBackground(new Color(153,255,255));
		player3Info.setBackground(new Color(255, 182, 193));
		player3Info.setLayout(null);

		player4Info.setBounds(1280, 300, 220, 100);
		// player4Info.setBackground(new Color(102,255,51));
		player4Info.setBackground(new Color(230, 230, 250));
		player4Info.setLayout(null);

		extraPanel.setBounds(1280, 400, 220, 320);
		extraPanel.setBackground(new Color(255, 255, 255));
		extraPanel.setLayout(null);

		// 부착
		controller.add(gameGUI);
		controller.add(player1Info); // main JFrame에 player1Info JPanel 부착
		controller.add(player2Info);
		controller.add(player3Info);
		controller.add(player4Info);
		controller.add(extraPanel);
		// controller.setComponentZOrder(player1Info, 5);
		//
		player1Info.add(player1Img); // player1Info JPanel에 player1Img label 부착
		player2Info.add(player2Img);
		player3Info.add(player3Img);
		player4Info.add(player4Img);
		//
		player1Info.add(player1Id);
		player2Info.add(player2Id);
		player3Info.add(player3Id);
		player4Info.add(player4Id);
		//
		player1Info.add(player1Coin);
		player2Info.add(player2Coin);
		player3Info.add(player3Coin);
		player4Info.add(player4Coin);
		// gameGUI.setVisible(false);

		//
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
		// extraPanel.add(button_exit);

	}

	@Override
	public void run() {
		Player nowPlayer = playerList.get(playerIdx);
		updateID_Label();
		updateCoinLabel();
		try {
			while (true) {
				//PlayMusic.loadAudio(Roll_music);
				nowPlayer = playerList.get(playerIdx);
				int max_roundMan = -1;
				dice = -1;
				while (dice == -1) {
					updateCoinLabel(); // Coin 업데이트
					if (nowPlayer.get_use_item() == 1) {
						Item_plus_move(playerList.get(playerIdx));
						nowPlayer.set_use_item(0);
						PlayMusic.play_actionSound("src/audio/Jump.wav");
						sameGround(nowPlayer);
						reachGround(nowPlayer);
					} else if (nowPlayer.get_use_item() == 2) {
						Item_attack_move(playerList.get(playerIdx));
						nowPlayer.set_use_item(0);
						PlayMusic.play_actionSound("src/audio/Attack.wav");
						sameGround(nowPlayer);
						reachGround(nowPlayer);
					}
					Thread.sleep(100);
					for(int i = 0; i < numPlayer ; i++) {
						if(playerList.get(i).get_roundMap() == 2) {
							//모든 플레이어의 현재 바퀴수 확인, 두바퀴 돌면 max RoundMan 바뀌고 우승, 아래 반복문에서 break로 게임 종료?
							max_roundMan = i;
							break;
						}
					}
					//한바퀴 돈사람이 승리하는 상태
					if(max_roundMan != -1) {
						//PlayMusic.play_actionSound("src/audio/Applause.wav",false);
						PlayMusic.play_actionSound("src/audio/GameVictory.wav");
						JOptionPane.showMessageDialog(null, "' " + playerList.get(max_roundMan).getName() + " '" + "의 우승!");
						break;
						
					}
				}
				//int max_roundMan = -1;
				
				
				gameGUI.offRollingDice();
				gameGUI.onDiceNumber(dice);
				for (int i = 0; i < dice; i++) {
					move(nowPlayer);
				}
				gameGUI.offDiceNumber(dice);
				reachGround(nowPlayer);
				sameGround(nowPlayer);
				
//				for(int i = 0; i < numPlayer ; i++) {
//					if(playerList.get(i).get_roundMap() == 2) {
//						max_roundMan = i;
//						break;
//					}
//				}
//				if(max_roundMan != -1) {
//					//PlayMusic.play_actionSound("src/audio/Applause.wav",false);
//					PlayMusic.play_actionSound("src/audio/GameVictory.wav",false);
//					JOptionPane.showMessageDialog(null, "' " + playerList.get(max_roundMan).getName() + " '" + "의 우승!");
//					break;
//					
//				}
				
				playerIdx++;
				playerIdx %= numPlayer; // 4로 나누면 0 1 2 3
			}
			// @ add game exit message
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// JOptionPane.showMessageDialog(null, nowPlayer.getID() + " Player lost all
		// money!");
		System.exit(0);
	}

	public void reachGround(Player player) {
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
			//new Map8_GamblingWIthThread(player);
		}
	}

	public void sameGround(Player player) {
		int nowPlayer_arrive = player.getPosition(); // 현재 플레이어의 위치값 저장
		for (int i = 0; i < numPlayer; i++) { // 플레이어 수 만큼 반복
			if (playerIdx != i) { // 만약 i가 현재 플레이어의 인덱스를 지칭하는 게 아닐 경우
				int check_overlap = playerList.get(i).getPosition();
				if (nowPlayer_arrive == check_overlap) { // 만약 기존 플레이어가 잡힌 경우
					
					JOptionPane.showMessageDialog(null, "' " + playerList.get(i).getName() + " '" + "를 잡았다!");
					
					overlap_move(playerList.get(i));
					break;
				}
			}
		}
	}

	public void updateCoinLabel() {
		player1Coin.setText("Coin : " + playerList.get(0).getCoin()); // Player의 Coin값 읽어옴.
		player2Coin.setText("Coin : " + playerList.get(1).getCoin());
		if (numPlayer >= 3) {
			player3Coin.setText("Coin : " + playerList.get(2).getCoin());
		}
		if (numPlayer == 4) {
			player4Coin.setText("Coin : " + playerList.get(3).getCoin());
		}
	}

	public void updateID_Label() {
		player1Id.setText("ID : " + playerList.get(0).getName()); // Player의 ID 값 읽어옴.
		player2Id.setText("ID : " + playerList.get(1).getName());
		if (numPlayer >= 3) {
			player3Id.setText("ID : " + playerList.get(2).getName());
		}
		if (numPlayer == 4) {
			player4Id.setText("ID : " + playerList.get(3).getName());
		}
	}

	public void move(Player player) {
		int ID = player.getID();
		Point prePoint = pointManager.getPlayerPoint(ID, player.getPosition());
		player.increPosition();
		Point nextPoint = pointManager.getPlayerPoint(ID, player.getPosition());
		for (int i = 0; i < 20; i++) {
			Point interPoint = new Point((prePoint.x * (20 - i) + nextPoint.x * i) / 20,
					(prePoint.y * (20 - i) + nextPoint.y * i) / 20);
			try {
				gameGUI.playerMove(player, interPoint);
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		gameGUI.playerMove(player, nextPoint);
	}

	public void overlap_move(Player player) { // 만약 같은 위치에 있는 경우 기존 플레이어 시작으로 보냄
		int ID = player.getID();
		Point catchMove = pointManager.getPlayerPoint(ID, player.getPosition());

		for (int i = 0; i < 10; i++) {
			Point interPoint = new Point(catchMove.x, (catchMove.y - (i * 10)));
			try {
				gameGUI.playerMove(player, interPoint);
				Thread.sleep(10);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		player.resetPosition();
		Point startPoint = pointManager.getPlayerPoint(ID, 0);
		PlayMusic.play_actionSound("src/audio/SameGround.wav");
		gameGUI.playerMove(player, startPoint);
	}

	public void Item_plus_move(Player player) {
		int ID = player.getID();
		int current_Position = player.getPosition();
		Point catchMove2 = pointManager.getPlayerPoint(ID, current_Position);

		for (int i = 0; i < 10; i++) {
			Point interPoint = new Point((catchMove2.x - (i * 10)), (catchMove2.y - (i * 10)));
			try {
				gameGUI.playerMove(player, interPoint);
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		Point plusPoint = pointManager.getPlayerPoint(ID, player.item_plus_Position());
		gameGUI.playerMove(player, plusPoint);
	}
	
	//Item_attack_move
	public void Item_attack_move(Player player) {
		int max=0;
		int maxPlayerIdx = 0;
		
		for(int i = 0; i < numPlayer ; i++) { // 플레이어 수 만큼 반복
    		if(playerIdx != i) { // 만약 i가 현재 플레이어의 인덱스를 지칭하는 게 아닐 경우
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
				gameGUI.playerMove(player, interPoint);
				Thread.sleep(20);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		player.setPosition(max);
		Point jumpPoint = pointManager.getPlayerPoint(ID, max);
		gameGUI.playerMove(player, jumpPoint);
		overlap_move(playerList.get(maxPlayerIdx));
	}

	public void rollDice() {
		//dice = new Random().nextInt(6) + 1;
		
		 dice = 5;
	}

	public void close() {
		this.interrupt();
	}
}
