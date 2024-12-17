package Game;

import client.Main;
import client.PlayMusic;
import server.RoomThread;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

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

	private int use_item;
	// private boolean moved;
	//private String Roll_music = "audio/Roll-Dice-Sound.wav";
	//client.PlayMusic.loadAudio(Roll_music);
	public PointManager pointManager = new PointManager();
	public GameGUI gameGUI;
	ArrayList<Player> playerList;

	RoomThread roomThread;
	int dice;

	public Game(RoomThread roomThread) {
		this.roomThread = roomThread;
		numPlayer =  roomThread.getClients();
	}

	public int getPlayerIdx() {
		return playerIdx;
	}

	@Override
	public void run() {
		playerIdx = 0;
		Player nowPlayer = playerList.get(playerIdx);
		try {
			while (true) {
				//client.PlayMusic.loadAudio(Roll_music);
				nowPlayer = playerList.get(playerIdx);
				int max_roundMan = -1;
				dice = -1;
//				while (dice == -1) {
//					//gameGUI.updateCoinLabel(); // Coin 업데이트
//					if (nowPlayer.get_use_item() == 1) {
//						Item_plus_move(playerList.get(playerIdx));
//						nowPlayer.set_use_item(0);
//						PlayMusic.play_actionSound("src/audio/Jump.wav");
//						sameGround(nowPlayer);
//						reachGround(nowPlayer);
//					} else if (nowPlayer.get_use_item() == 2) {
//						Item_attack_move(playerList.get(playerIdx));
//						nowPlayer.set_use_item(0);
//						PlayMusic.play_actionSound("src/audio/Attack.wav");
//						sameGround(nowPlayer);
//						reachGround(nowPlayer);
//					}
//					Thread.sleep(100);
//					for(int i = 0; i < numPlayer ; i++) {
//						if(playerList.get(i).get_roundMap() == 2) {
//							//모든 플레이어의 현재 바퀴수 확인, 두바퀴 돌면 max RoundMan 바뀌고 우승, 아래 반복문에서 break로 게임 종료?
//							max_roundMan = i;
//							break;
//						}
//					}
//					//한바퀴 돈사람이 승리하는 상태
//					if(max_roundMan != -1) {
//						//client.PlayMusic.play_actionSound("src/audio/Applause.wav",false);
//						PlayMusic.play_actionSound("src/audio/GameVictory.wav");
//						JOptionPane.showMessageDialog(null, "' " + playerList.get(max_roundMan).getName() + " '" + "의 우승!");
//						break;
//
//					}
//				}

				reachGround(nowPlayer);
				sameGround(nowPlayer);
				
				for(int i = 0; i < numPlayer ; i++) {
					if(playerList.get(i).get_roundMap() == 2) {
						max_roundMan = i;
						break;
					}
				}
				if(max_roundMan != -1) {
					//client.PlayMusic.play_actionSound("src/audio/Applause.wav",false);
					//client.PlayMusic.play_actionSound("src/audio/GameVictory.wav",false);
					JOptionPane.showMessageDialog(null, "' " + playerList.get(max_roundMan).getName() + " '" + "의 우승!");
					break;

				}
				
//				playerIdx++;
//				playerIdx %= numPlayer; // 4로 나누면 0 1 2 3
			}
			// @ add game exit message
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// JOptionPane.showMessageDialog(null, nowPlayer.getID() + " Game.Player lost all
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
			//new Game.Map8_GamblingWIthThread(player);
		}
	}

	public void sameGround(Player player) {
		int nowPlayer_arrive = player.getPosition(); // 현재 플레이어의 위치값 저장
		for (int i = 0; i < numPlayer; i++) { // 플레이어 수 만큼 반복
			if (playerIdx != i) { // 만약 i가 현재 플레이어의 인덱스를 지칭하는 게 아닐 경우
				int check_overlap = playerList.get(i).getPosition();
				if (nowPlayer_arrive == check_overlap) { // 만약 기존 플레이어가 잡힌 경우
					
					//JOptionPane.showMessageDialog(null, "' " + playerList.get(i).getName() + " '" + "를 잡았다!");
					
					overlap_move(playerList.get(i));
					break;
				}
			}
		}
	}

	public void processRollDice() {
//		Player currentPlayer = playerList.get(playerIdx);
//		//currentPlayer.setDiceResult(dice); // 주사위 값 저장 (필요시)
//		move(currentPlayer); // 주사위 값만큼 이동
//		sameGround(currentPlayer); // 같은 위치 확인
//		reachGround(currentPlayer); // 위치별 이벤트 처리

		// 다음 플레이어로 턴 변경
		playerIdx = (playerIdx + 1) % numPlayer;
	}

	public int rollDice() {
		dice = new Random().nextInt(6) + 1;
		playerIdx = (playerIdx + 1) % numPlayer;
		//System.out.println("Player updated : " + playerIdx);
		return dice;
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



	public void close() {
		this.interrupt();
	}
}
