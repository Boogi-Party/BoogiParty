	//src/Game/Map4_GBBGame.java
	package Game;
	import client.ClientThread;
	import client.Main;
	import client.PlayMusic;

	import java.awt.*;
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;

	import javax.sound.sampled.Clip;
	import javax.swing.*;

	public class Map4_GBBGame extends JFrame implements MiniGame{ //ctrl shift o ////외부에서는 이 배열 접근하지 못하게 private
		@Override
		public void update(String msg) {
			String [] parts = msg.split("/");
			String winner = "";
			if (parts[0].equals("ME_WIN")) {
				winner = player.getName();
			}
			else if (parts[0].equals("COM_WIN")) {
				winner = "com";
			}
			else {
				winner = "Draw";
			}
			//msg에 ME_WIN/COM_WIN/SAME 넘어오면 GamePanel에 표시.
			gamePanel.updateResult(winner, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		}
		@Override
		public void end() {
			// 결과에 따라 플레이어 코인 조정
			if (gamePanel.getResult().equals(player.getName())) {
				player.setCoin(player.getCoin() + 10);
			} else if (gamePanel.getResult().equals("com")) {
				player.setCoin(player.getCoin() - 10);
			}

			// JDialog에 GamePanel 표시
			JDialog dialog = new JDialog(this, "게임 결과", false); // 비모달 설정
			dialog.setLayout(new BorderLayout());
			dialog.add(gamePanel, BorderLayout.CENTER); // GamePanel을 JDialog에 추가
			dialog.setSize(400, 300); // 다이얼로그 크기 설정
			dialog.setLocationRelativeTo(this); // 화면 중앙 배치
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);

			// 일정 시간 후 다이얼로그 종료
			Timer timer = new Timer(2000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose(); // 다이얼로그 닫기
					dispose(); // JFrame 종료
				}
			});
			timer.setRepeats(false); // 반복 방지
			timer.start();
		}


		//속성
		private ImageIcon[] gbbImage;
				 //ctrl shift o
		//ImageIcon playerIcon1 = new ImageIcon(client.Main.class.getResource("images/bo.jpg"));
		private static String SAME="same!";   //static 하면 객체생성 전부터 관리할 수 있음
		private static String ME_win= "ME!!!";
		private static String COM_win="COMPUTER!!!";
		public Clip clip;

		private MenuPanel menuPanel;   //객체만들고 new// MenuPanel클래스 객체가 만들어짐.
		private GamePanel gamePanel;
		//private Player player;  // 멤버 변수로 선언
		private boolean isPlayer = false;
		Player player;
		ClientThread clientThread;
		//생성자
		public Map4_GBBGame(Player player, boolean isPlayer, ImageIcon[] gbbImage, JFrame parentFrame, ClientThread clientThread){
			super("미니게임- 가위바위보"); //title만들기. super class호출해서 넘겨줌\
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.isPlayer =	isPlayer;
			this.player = player;
			this.clientThread = clientThread;
			this.gbbImage = gbbImage;

			menuPanel = new MenuPanel();
			gamePanel = new GamePanel();

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			add(menuPanel, BorderLayout.NORTH);
			add(gamePanel, BorderLayout.CENTER);

			setSize(400, 400);

			// 부모 JFrame의 가운데에 위치시키기
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
		}

		class MenuPanel extends JPanel{ //ctrl shift o
			//MenuPanel도 클래스이니까 속성+생성자+메소드
			private JButton[] gbbBtn = new JButton[3];

			public MenuPanel() {
				setBackground(Color.gray);
				for(int i=0; i<3; i++) {
					gbbBtn[i] = new JButton(gbbImage[i]);
					add(gbbBtn[i]);

					if (isPlayer) {
						gbbBtn[i].addActionListener(new MyActionListener(i));
					}
				}
			}
		}

		class MyActionListener implements ActionListener{
			int choice;
			public MyActionListener(int choice) {
				this.choice = choice;
			}
			public void actionPerformed(ActionEvent e) {
				clientThread.sendMessage("MINI_GAME_START/4/" + choice);
				// 모든 버튼 비활성화
				for (JButton gameButton : menuPanel.gbbBtn) {
					gameButton.setEnabled(false);
				}
				clientThread.sendMessage("MINI_GAME_END/" + 4 + "/" + player.getID());
			}
		}

		class GamePanel extends JPanel {
			// 속성
			private JLabel me = new JLabel(player.getName()); // 플레이어의 선택 이미지
			private JLabel com = new JLabel("computer"); // 컴퓨터의 선택 이미지
			private JLabel win = new JLabel("winner"); // 승리자 표시
			private String result = ""; // 게임 결과를 저장 ("ME_WIN", "COM_WIN", "SAME")

			// 생성자
			public GamePanel() {
				setBackground(Color.YELLOW);
				add(me);
				add(com);
				add(win);
				win.setForeground(Color.RED); // 글자색 foreground
			}

			// 결과 업데이트 메서드
			// GamePanel 클래스 내부
			public void updateResult(String result, int myImageIdx, int comImageIdx) {
				this.result = result; // 결과 저장

				// 플레이어와 컴퓨터의 선택에 해당하는 이미지를 설정
				Icon myImage = gbbImage[myImageIdx];
				Icon comImage = gbbImage[comImageIdx];

				// UI 업데이트
				draw(myImage, comImage, result);
			}


			// UI에 결과를 그리는 메서드
			public void draw(Icon myImage, Icon comImage, String resultMessage) {
				me.setIcon(myImage); // 플레이어 이미지 설정
				com.setIcon(comImage); // 컴퓨터 이미지 설정
				if (resultMessage.equals("Draw")){
					win.setText("Draw!");
				}
				else {
					win.setText(resultMessage + "Win!!"); // 결과 메시지 설정
				}
			}

			// 게임 결과를 반환하는 메서드
			public String getResult() {
				return result;
			}
		}

	}