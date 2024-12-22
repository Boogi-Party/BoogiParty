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
import java.util.Random;
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
		dice = new Random().nextInt(6) + 1;
		//dice = 4;
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

	// 퀴즈 리스트 배열
	private String[] quizList = {
			"한성빌딩과 가장 가까운 역은?",
			"현재 강의는 객체지향언어2 '몇' 분반이다(대문자)",
			"객체지향언어2를 가장 잘 가르치는 교수님은 '000' 교수님이다.",
			"현 한성대 총장은 '000' 총장이다",
			"[OX퀴즈]한성대는 남아공의 한 대학교와 자매결연을 맺고 있다.",
			"한성대의 교수는?",
			"한성대의 교목은?",
			"상상부기가 가장 좋아하는음식은?",
			"한성대학교 교가에 등장하는 산은 '00'산이다",
			"해당 객체지향언어2 수업은 공학관 '000'호에서 진행된다.",
			"한성대학교에 있는 정자의 이름은?",
			"객체지향언어2는 전필인가 전선인가?",
			"객체지향언어2 팀플에 대한 미팅은 'N'차까지 진행되었다",
			"객체지향언어2 강의의 교재는 '000' 교수님이 제작하셨다",
			"지금 발표를 하고 있는 팀의 팀명은?",
			"[OX퀴즈]나는 지금 실행 중인 부루마블 게임이 매우 흡족하다.",
	};

	int currentQuizIndex;

	public String getQuiz() {
		Random random = new Random();
		currentQuizIndex = random.nextInt(quizList.length);

		return quizList[currentQuizIndex];
	}

	public String getAnswer() {
		return getCorrectAnswer(currentQuizIndex);
	}

	public void quiz(DataInputStream dis, String answer) throws IOException {
		String input = dis.readUTF();

		if (input.equals(answer)) {
			roomThread.broadcastQuizOver("CORRECT/" + answer + "/");
		}
		else {
			roomThread.broadcastQuizOver("WRONG/" + answer + "/" + input);
		}
	}

	private String getCorrectAnswer(int currentQuizIndex) {
		// 현재 퀴즈의 정답을 반환
		switch (currentQuizIndex) {
			case 0:
				return "신설동역";
			case 1:
				return "B";
			case 2:
				return "유상미";
			case 3:
				return "이창원";
			case 4:
				return "O";
			case 5:
				return "거북";
			case 6:
				return "삼학송";
			case 7:
				return "상추";
			case 8:
				return "북악";
			case 9:
				return "202";
			case 10:
				return "의화정";
			case 11:
				return "전선";
			case 12:
				return "3";
			case 13:
				return "황기태";
			case 14:
				return "똑똑이들";
			case 15:
				return "O";
			default:
				return "";

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
