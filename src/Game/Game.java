package Game;

import client.PlayMusic;
import server.RoomThread;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Game extends Thread {
	private boolean isDiceOne = true; // 주사위 상태를 결정하는 변수


	public int numPlayer;
	private int playerIdx;

	private int use_item;
	public PointManager pointManager = new PointManager();
	public GameGUI gameGUI;
	ArrayList<Player> playerList;

	private volatile boolean isMiniGameRunning;

	RoomThread roomThread;
	GamblingThread gt;
	int dice;

	public Game(RoomThread roomThread) {
		this.roomThread = roomThread;
		numPlayer =  roomThread.getClients();
		isMiniGameRunning = false;
	}

	public boolean getIsMiniGameRunning() {
		return isMiniGameRunning;
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

	public int rollDice() {
		dice = new Random().nextInt(6) + 1;

//		dice = isDiceOne ? 1 : 2; // 주사위 값 설정
//		isDiceOne = !isDiceOne;   // 다음 호출 시 값을 토글
//		isDiceOne = false;

//		dice = 4;
		playerIdx = (playerIdx + 1) % numPlayer;
		//System.out.println("Player updated : " + playerIdx);
		return dice;

	}



	public ArrayList<Integer> getrandom() {
		ArrayList<Integer> results = new ArrayList<>();

		results.add((int)(Math.random()*2));
		results.add((int)(Math.random()*2));
		results.add((int)(Math.random()*2));

		return results;
	}


	public int reachGround(int idx) {
		Player player = playerList.get(idx);
		if (player.getPosition() == 4) {
			return 4;
		} else if (player.getPosition() == 8) {
			return 8;
		} else if (player.getPosition() == 12) {
			return 12;
		}
		return 0;
	}


	public void startMinigame() {
		isMiniGameRunning = true;

		isMiniGameRunning = false;
	}

	public void close() {
		this.interrupt();
	}

	public void game8() {
		gt = new GamblingThread(); //세번째 속성으로 th객체 만듦.
		gt.start();
	}

	public void game8end() {
		gt.gamble();
	}

	class GamblingThread extends Thread{
		//속성
		private int delay = 200; //얼만큼 지연할 것인가 //잠깐 wait할 때 delay를 할것이다. // 0.2초간격 200ms
		private boolean gambling = false;

		public void gamble() {
			gambling = !gambling; //흐름 조작을 하자. 호출될 때마다 한번씩 반대가 되는 것이다.
			//마우스로 클릭하니까.
		}

		public void run() {//게임의 시작은 여기서부터.
			int x1 = 0, x2 = 0, x3 = 0;
			while(!gambling) {
				try {//예외처리 상황을 고려해줘야함
					x1 =(int)(Math.random()*2);//0~4   //숫자 세개를 난수로 만들어준다.
					x2 =(int)(Math.random()*2);
					x3 =(int)(Math.random()*2);

					roomThread.broadcastGambling(x1, x2, x3, "");

					sleep(delay); //슬립. 슬립하면 Interrupt발생. catch문 수행하러 이동.
				} catch(InterruptedException e) {
					return;
				}
			}
			roomThread.broadcastGambling(x1, x2, x3, "END");
		}
	}
}
