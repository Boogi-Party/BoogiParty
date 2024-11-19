import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Map4_GBBGame extends JFrame { //ctrl shift o ////외부에서는 이 배열 접근하지 못하게 private
	//속성
	private ImageIcon[] gbbImage = {new ImageIcon(Main.class.getResource("images/gawi.jpg")), //image관리하는 component //이미지 3장이니까 배열. 레퍼런스 변수 선언
							new ImageIcon(Main.class.getResource("images/bawi.jpg")),   
							new ImageIcon(Main.class.getResource("images/bo.jpg")) } ;
			 //ctrl shift o
	//ImageIcon playerIcon1 = new ImageIcon(Main.class.getResource("images/bo.jpg"));
	private static String SAME="same!";   //static 하면 객체생성 전부터 관리할 수 있음
	private static String ME_win= "ME!!!";
	private static String COM_win="COMPUTER!!!";
	public Clip clip;
	
	private MenuPanel menuPanel =new MenuPanel();   //객체만들고 new// MenuPanel클래스 객체가 만들어짐.
	private GamePanel gamePanel =new GamePanel();
	private Player player;  // 멤버 변수로 선언
	//생성자
	public Map4_GBBGame(Player player){ //Panel객체 만들었으니까, 배치를 Map4_GBBGame 생성자에서 하자.
		super("미니게임- 가위바위보"); //title만들기. super class호출해서 넘겨줌.
		this.player = player;  // 생성자에서 초기화
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container c = getContentPane(); //Frame에 도화지 한 장 가져와서 c로 설정하자  //Container class 필요. import
		c.setLayout(new BorderLayout());
		
		//component, panel 부착은 add.
		add(menuPanel,BorderLayout.NORTH);
		add(gamePanel,BorderLayout.CENTER);

		setSize(400,400);
		setLocation(13, 50);
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
				
				gbbBtn[i].addActionListener(new MyActionListener()); //객체생성해서 리스너에게 줌. 그걸 버튼에 단다.//기본생성자호출.
			}
		}
		
	}	
	//마우스 이벤트 처리(ActionListener)를 위한 클래스를 구현
	class MyActionListener implements ActionListener{ //import 
		 //ActionListener가 가지고있는 추상클래스//이미 만들어져있음.
		public void actionPerformed(ActionEvent e) { //이미지가 클릭되었을 때 이 함수 호출.
			JButton btn = (JButton)e.getSource();//버튼의 정보를 가져오자. 이벤트가 일어난 객체의 정보를 가져온다. 최상위 클래스로부터.
			//Object로부터 쭉 정보 가져옴.//(JButton)라고 캐스팅해주면 Object로부터 모두가 아니라 JButton에 해당하는 이벤트내용만 가져옴.
			
			int comPart = (int)(Math.random()*3);//컴퓨터 숫자 하나 가짐. 난수. 0~1사이 숫자.//*3하고 정수로 캐스팅하면 0~2를 만들 수 있음.
			//0 <= x < 3     0<= x < 1 
			String win = "SAMESAME!";
			
			//내가 이겼다.
			if(btn.getIcon()==gbbImage[0]&&comPart == 2 || 
			   btn.getIcon()==gbbImage[1]&&comPart == 0 ||
			   btn.getIcon()==gbbImage[2]&&comPart == 1 ) //image 아이콘 값을 읽어오자 //0번 가위  1번 바위 2번 보
				{ 	
					win = ME_win;
					PlayMusic.play_actionSound("src/audio/GBBSuccess.wav");
					player.setCoin(player.getCoin() + 10);
					//JOptionPane.showMessageDialog(null, "Coin +10", "알림", JOptionPane.INFORMATION_MESSAGE);
			
				}
			//컴퓨터가 이겼다.
			else if(btn.getIcon()==gbbImage[0]&&comPart == 1 || 
					btn.getIcon()==gbbImage[1]&&comPart == 2 ||
					btn.getIcon()==gbbImage[2]&&comPart == 0 ) //image 아이콘 값을 읽어오자 //0번 가위  1번 바위 2번 보
					{
						win = COM_win;
						PlayMusic.play_actionSound("src/audio/GBBFail.wav");
						player.setCoin(player.getCoin() - 10);
						//JOptionPane.showMessageDialog(null, "Coin -10", "알림", JOptionPane.INFORMATION_MESSAGE);
					}
			else {
				win = SAME;
				PlayMusic.play_actionSound("src/audio/GBBDraw.wav");
				}
			 // 모든 버튼 비활성화
            for (JButton gameButton : menuPanel.gbbBtn) {
                gameButton.setEnabled(false);
            }
			gamePanel.draw(btn.getIcon(),gbbImage[comPart],win);
		}
	}
	
	class GamePanel extends JPanel{ 
		//속성+생성자+메소드(draw) //마우스 클릭이 되었을 때 띄운다.
		private JLabel me = new JLabel("me"); //import
		private JLabel com = new JLabel("computer");
		private JLabel win = new JLabel("winner");
		
		public GamePanel() {
			setBackground(Color.YELLOW);
			add(me);add(com);add(win);
			win.setForeground(Color.RED); //글자색 foreground
		}
		
		public void draw(Icon myImage,Icon comImage,String m) { //두 이미지를 출력 //Icon 받아야하니가 import
			me.setIcon(myImage);
			com.setIcon(comImage);
			win.setText(m);
		}
		
		
	}
	

}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
