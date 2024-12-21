package Game;

import client.PlayMusic;
import server.RoomThread;

import java.awt.*;
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

	public void game12() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Press Enter to execute foo()...");
					while (true) {
						String input =
						// 엔터키 (ASCII: 10 또는 13 depending on OS)
						if (input == '\n' || input == '\r') {
							foo(); // 엔터키 입력 시 foo() 실행
							break; // 한 번 실행 후 스레드 종료
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
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
			roomThread.broadcastMiniGameEnd();
		}
	}


}
