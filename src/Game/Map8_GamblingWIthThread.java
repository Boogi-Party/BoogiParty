package Game;
//src/Game/Map8_Gamblinc.java

import client.ClientThread;
import client.PlayMusic;

import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.Clip;
import javax.swing.*;//아스테리스크 쓰면 다른거 다 import


public class Map8_GamblingWIthThread extends JFrame implements MiniGame{
	@Override
	public void update(String msg) {
		String [] parts = msg.split("/");

		gamePanel.setPanel(parts[0], parts[1], parts[2]);
	}

	@Override
	public void end() {
		// 게임 판정
		String message;
		if (gamePanel.getResult()) {
			PlayMusic.play_actionSound("src/audio/GamblingSuccess.wav");
			player.setCoin(player.getCoin() + 500);
			message = player.getName() + " : 잭팟입니다! +500코인 획득!";
		} else {
			PlayMusic.play_actionSound("src/audio/GamblingFail.wav");
			player.setCoin(player.getCoin() - 100);
			message = player.getName() + " : 꽝! -100코인";
		}

		// 사용자 확인 버튼 없이 메시지만 보여주는 비모달 JDialog 생성
		JDialog dialog = new JDialog(Map8_GamblingWIthThread.this, "게임 결과", false); // 비모달 설정 (false)
		dialog.setLayout(new BorderLayout());
		JLabel label = new JLabel(message, SwingConstants.CENTER);
		label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		dialog.add(label, BorderLayout.CENTER);
		dialog.setSize(300, 150);
		dialog.setLocationRelativeTo(Map8_GamblingWIthThread.this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 기본 닫기 설정
		dialog.setVisible(true);

		// 일정 시간 후에 다이얼로그와 프레임 종료
		Timer timer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose(); // 다이얼로그 닫기
				dispose(); // JFrame 종료
			}
		});
		timer.setRepeats(false); // 타이머 반복 방지
		timer.start();
	}

	private Player player;  // 멤버 변수로 선언
	private boolean isPlayer;
	public Clip clip;
	ClientThread clientThread;
	public GamePanel gamePanel;

	public Map8_GamblingWIthThread(Player player, boolean isPlayer, JFrame parentFrame, ClientThread clientThread) { //생성자.
		super("미니게임- 도전 겜블링");
		this.player = player;
		this.isPlayer = isPlayer;
		this.clientThread = clientThread;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		gamePanel = new GamePanel();
		setContentPane(gamePanel); //패널 부착
		setSize(320,200);
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
		setVisible(true);
		System.out.println("Map8 도박 게임 시작");
		//setLocation(400,300);//기본은 (0,0)
		
	}
	class GamePanel extends JPanel { //패널을 하나 만듬.
		//속성
		private JLabel [] label  = new JLabel[3];  //label이라는 배열 레퍼런스 변수 선언하고 배열생성해서 연결. 배열을 3개만듬
		private JLabel result = new JLabel("마우스 클릭해서 게임을 시작...");
		//3개의 라벨과 result를 부착하자. 배치관리자 없앴으니까.	

		public void setPanel(String x1, String x2, String x3) {
			label[0].setText(x1);
			label[1].setText(x2);
			label[2].setText(x3);
		}

		public boolean getResult() {
			return gamePanel.label[0].getText().equals(label[1].getText()) && label[1].getText().equals(label[2].getText());
		}
		 //생성자
		public GamePanel() {
			setLayout(null); //원래 구현된 기본배치관리자 사용하지 않는다.
			
			for(int i=0;i<label.length;i++) {
				label[i] = new JLabel("0");  //JLabel개게생성
				label[i].setSize(60,30); //가로 60 세로30 라벨.
				label[i].setLocation(30+80*i,40); //3개가 연속적으로 위치를 배정받음.
				label[i].setOpaque(true);//배경색을 설정하기위해서 따라다니는 옵션.
				label[i].setBackground(Color.MAGENTA);
				label[i].setForeground(Color.black); //글자색
				label[i].setFont(new Font("Malgun Gothic",Font.ITALIC,15));//Font 클래스 사용해서 JLabel글자크기 조절
				
				add(label[i]); //화면에다가 부착
			}
			
			result.setSize(200,30);
			result.setLocation(30,120);
			add(result); //화면에다가 부착


			if (isPlayer) {
				// 게임 시작 여부를 추적하는 플래그
				final boolean[] gameStarted = {false}; // 초기값: 게임이 시작되지 않음

				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						if (!gameStarted[0]) {
							// 첫 번째 클릭: 게임 시작 메시지 전송
							clientThread.sendMessage("MINI_GAME_START/" + 8);
							gameStarted[0] = true; // 게임이 시작되었음을 플래그로 설정
						} else {
							// 두 번째 클릭 이후: 게임 종료 메시지 전송
							clientThread.sendMessage("MINI_GAME_END/" + 8 + "/" + player.getID());
						}
					}
				});
			}
			//addKeyListener 추가하면 //Enter키를 입력받아서 할 수도 있음
		}

			}


}
