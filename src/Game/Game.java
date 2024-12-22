package Game;

import client.PlayMusic;
import server.RoomThread;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.*;

public class Game  {
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

	public int rollDice() {
		//dice = new Random().nextInt(6) + 1;
		dice = 4;
		playerIdx = (playerIdx + 1) % numPlayer;
		//System.out.println("Player updated : " + playerIdx);
		return dice;
	}

	public void game4(int choice) {
		int comPart = (int)(Math.random()*3);//컴퓨터 숫자 하나 가짐. 난수. 0~1사이 숫자.//*3하고 정수로 캐스팅하면 0~2를 만들 수 있음.
		//0 <= x < 3     0<= x < 1
		String msg = "SAMESAME!";

		if (choice == 0 && comPart == 2 ||
				choice == 1 && comPart == 0 ||
				choice == 2 && comPart == 1 ) //image 아이콘 값을 읽어오자 //0번 가위  1번 바위 2번 보
		{
			msg = "ME_WIN";
			PlayMusic.play_actionSound("src/audio/GBBSuccess.wav");
			//JOptionPane.showMessageDialog(null, "Coin +10", "알림", JOptionPane.INFORMATION_MESSAGE);

		}
		//컴퓨터가 이겼다.
		else if(choice == 0 && comPart == 1 ||
				choice == 1 && comPart == 2 ||
				choice == 2 && comPart == 0 ) //image 아이콘 값을 읽어오자 //0번 가위  1번 바위 2번 보
		{
			msg = "COM_WIN";
			PlayMusic.play_actionSound("src/audio/GBBFail.wav");
		}
		else {
			msg = "SAME";
			PlayMusic.play_actionSound("src/audio/GBBDraw.wav");
		}
		roomThread.broadcastGBB(msg+"/" + choice + "/" + comPart);
	}

	public void game8() {
		gt = new GamblingThread(); //세번째 속성으로 th객체 만듦.
		gt.start();
	}


	public void game8end() {
		gt.gamble();
	}

	public void game12(DataInputStream dis) {
		int shotCount = 0;
		while (true) {
			try {
				String s = dis.readUTF();
				if (s.isEmpty()) {
					System.out.println("bullet shot");
					shotCount++;
					roomThread.broadcastGame12("DRAW_BULLET");

					PlayMusic.play_actionSound("src/audio/M1-Sound.wav");
				}
				else if (s.equals("HIT")) {
					roomThread.broadcastGame12("HIT");
				}
				else if (s.equals("END")) {
					roomThread.broadcastMiniGameEnd();
					break;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}

	class GamblingThread extends Thread{
		//속성
		private int delay = 200;
		private boolean gambling = false;

		public void gamble() {
			gambling = !gambling;
		}

		public void run() {
			int x1 = 0, x2 = 0, x3 = 0;
			//0.2초 간격으로 난수 생성, 클라이언트 GUI에 렌더링
			//플레이어가 클릭 시 gamble()실행하여 게임 종료
			while(!gambling) {
				try {
					x1 =(int)(Math.random()*2);
					x2 =(int)(Math.random()*2);
					x3 =(int)(Math.random()*2);

					roomThread.broadcastGambling(x1, x2, x3, "");

					sleep(delay);
				} catch(InterruptedException e) {
					return;
				}
			}
			//게임 종료 결과 렌더링
			roomThread.broadcastMiniGameEnd();
		}
	}

}
