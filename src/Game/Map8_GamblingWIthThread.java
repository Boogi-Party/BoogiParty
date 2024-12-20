package Game;
//src/Game/Map8_Gamblinc.java

import client.PlayMusic;

import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.Clip;
import javax.swing.*;//아스테리스크 쓰면 다른거 다 import


public class Map8_GamblingWIthThread extends JFrame {
	
	private Player player;  // 멤버 변수로 선언
	private boolean isPlayer;
	public Clip clip;
	public Map8_GamblingWIthThread(Player player, boolean isPlayer, JFrame parentFrame) { //생성자.
		super("미니게임- 도전 겜블링");
		this.player = player;
		this.isPlayer = isPlayer;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setContentPane(new GamePanel()); //패널 부착
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
		//setLocation(400,300);//기본은 (0,0)
		
	}
	class GamePanel extends JPanel { //패널을 하나 만듬.
		//속성
		private JLabel [] label  = new JLabel[3];  //label이라는 배열 레퍼런스 변수 선언하고 배열생성해서 연결. 배열을 3개만듬
		private JLabel result = new JLabel("마우스 클릭해서 게임을 시작...");
		//3개의 라벨과 result를 부착하자. 배치관리자 없앴으니까.	
		private GamblingThread th=new GamblingThread(label,result); //세번째 속성으로 th객체 만듦.
		
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
			
			th.start(); //thread 가동. 자동으로 run메소드를 부른다.

			if (isPlayer) {
				addMouseListener(new MouseAdapter() {//이벤트객체를 만들어서 여기에 넣어라.
					//리스너에 경우는 추상메소드가 있어서 구현을 해야함. 근데 Adapter의 경우는 클래스이기 때문에 기본적인 코드가 있어서
					//우리가 쓰고자하는거 하나만 오버라이딩하면됨.
					public void mousePressed(MouseEvent e) { //
						if (th.isReady())
							th.startGambling(); //마우스에 의해 isReady에 의해 false가 true가 되고 startGambling으로 스레드꺠운다.
					}
				});
			}
			//addKeyListener 추가하면 //Enter키를 입력받아서 할 수도 있음
		}
	}
	class GamblingThread extends Thread{
		//속성
		private JLabel [] label;
		private JLabel result; 
		private int delay = 200; //얼만큼 지연할 것인가 //잠깐 wait할 때 delay를 할것이다. // 0.2초간격 200ms
		private boolean gambling = false;
		
		boolean isReady() {
			return !gambling; //흐름 조작을 하자. 호출될 때마다 한번씩 반대가 되는 것이다.
			//마우스로 클릭하니까. 
		}
		
		//생성자
		public GamblingThread(JLabel [] label,JLabel result) {
			this.label = label;
			this.result = result;
		}
		
		synchronized public void waitGambling() {    //wait이랑 notify랑 왔다갔다. 싱크로나이즈. 동기화
			if(!gambling)
				try {
					this.wait(); //너 스레드야 wait해라.
				}catch(InterruptedException e) {return;}
		
		}
		synchronized public void startGambling() { //wait해서 잠자고 있는 거 깨운다.
			gambling = true;  //깨워주는 건 CPU가 일을 계속하게하는거니까 interrupt 고려안해도 됨.
			this.notify(); //일어나라.
		}
		//run 메소드 오버라이딩. 스래드 객체가 start메소드를 호출하면 의해서 run이 실행되도록 세팅이되어있음.
		public void run() {//게임의 시작은 여기서부터.
			
			//내가 클릭할 때까지 멈춰있어. wait명령.
			waitGambling(); //너좀 잠깐 멈춰있어라 하는 함수. //이것 떄문에 run실행될 때 아래 무한루프 안돌아감.
			//가동할 수 있게 깨워주어라. 클릭하는 이벤트가 겜블리을 깨워주면 된다.
			
			
			while(true) {
				
				try {//예외처리 상황을 고려해줘야함
					int x1 =(int)(Math.random()*2);//0~4   //숫자 세개를 난수로 만들어준다.
					int x2 =(int)(Math.random()*2); 
					int x3 =(int)(Math.random()*2);
					
					label[0].setBackground(Color.yellow);
					sleep(200); //슬립. 슬립하면 Interrupt발생. catch문 수행하러 이동.
					label[0].setText(Integer.toString(x1)); //정수클래스에 toString메소드. 정수를 문자로 변형.
					
					label[1].setBackground(Color.yellow);
					sleep(200); //슬립. 슬립하면 Interrupt발생. catch문 수행하러 이동.
					label[1].setText(Integer.toString(x2)); //정수클래스에 toString메소드. 정수를 문자로 변형.
					
					label[2].setBackground(Color.yellow);
					sleep(200); //슬립. 슬립하면 Interrupt발생. catch문 수행하러 이동.
					label[2].setText(Integer.toString(x3)); //정수클래스에 toString메소드. 정수를 문자로 변형.
					
					//게임 판정
					if(x1 == x2 && x2==x3) {
						result.setText("잭팟!!!!!!!!");
						PlayMusic.play_actionSound("src/audio/GamblingSuccess.wav");
						player.setCoin(player.getCoin() + 500);
						
						JOptionPane.showMessageDialog(null, "Coin +500", "알림", JOptionPane.INFORMATION_MESSAGE);
						gambling = false;
						break;}
					else {
						result.setText("꽝!");
						PlayMusic.play_actionSound("src/audio/GamblingFail.wav");
						player.setCoin(player.getCoin() - 100);
						JOptionPane.showMessageDialog(null, "Coin -100", "알림", JOptionPane.INFORMATION_MESSAGE);
						gambling = false;
					break;}
					//한 세트 끝났으니 작업멈춘다
					//gambling = false;
					
				}catch(InterruptedException e){return;}     //cpu명령 수행하다가 sleep하면 인터럽트발생. 멈춰서 다른걸 실행

				
			}
		}
	}

}
