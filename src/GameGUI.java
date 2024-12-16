import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameGUI extends JPanel {
	private Image screenImage;
	private Image background = new ImageIcon(Main.class.getResource("images/Board/board.png")).getImage();
	private Image rollingDice = new ImageIcon(Main.class.getResource("images/rollingDice_3.gif")).getImage();
	private ImageIcon[] imagePlayer;

	private JButton closeButton;
	private JButton rollDiceButton;
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("images/menuBar.png")));

	//private String Roll_music = "audio/Roll-Dice-Sound.wav";
	
	// private JLabel rollingDice;
	public JLabel[] playerLabel;
	private JLabel[] diceNumber;

	public boolean rollDice = false;
	

	PointManager pointManager;
	int numPlayer;
	ArrayList<Player> playerList;
	private int mouseX, mouseY;
	private Game game;
	
	GameGUI(Game _game) {
		game = _game;
		pointManager = game.pointManager;
		numPlayer = game.numPlayer;
		playerList = game.playerList;
		setLayout(null);
		//setBounds(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		setBounds(0, 0, 1280, 720);
		setBackground(Color.CYAN);
		imagePlayer = new ImageIcon[4];
		for (int i = 0; i < 4; i++) {
			imagePlayer[i] = new ImageIcon(Main.class.getResource("images/Board/player" + i + ".png"));
		}

		diceNumber = new JLabel[6];
		for (int i = 0; i < 6; i++) {
			diceNumber[i] = new JLabel(new ImageIcon(Main.class.getResource("images/Board/dice" + (i + 1) + ".png")));
			diceNumber[i].setBounds(540, 240, 200, 220);
			diceNumber[i].setVisible(false);
			add(diceNumber[i]);
		}

		/********* SHOW PLAYER ICONS *********/
		playerLabel = new JLabel[4];
		for (int i = 0; i < 4; i++) {
			Point point = pointManager.getPlayerPoint(i, 0);
			playerLabel[i] = new JLabel(imagePlayer[i]);
			playerLabel[i].setLocation(point.x, point.y);
			playerLabel[i].setSize(30, 30);
			playerLabel[i].setVisible(false);
			add(playerLabel[i]);
		}
		for (int i = 0; i < numPlayer; i++) {
			playerLabel[i].setVisible(true);
		}

		rollDiceButton = new JButton();
		rollDiceButton.setBounds(540, 240, 200, 176);
		rollDiceButton.setBorderPainted(false);
		rollDiceButton.setContentAreaFilled(false);
		rollDiceButton.setFocusPainted(false);
		rollDiceButton.setIcon(new ImageIcon(Main.class.getResource("images/Board/rollDiceButton.png")));
		rollDiceButton.setRolloverIcon(new ImageIcon(Main.class.getResource("images/Board/rollDiceButtonEntered.png")));
		rollDiceButton.setPressedIcon(new ImageIcon(Main.class.getResource("images/Board/rollDiceButtonPressed.png")));
		rollDiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						onRollingDice();
						PlayMusic.play_actionSound("src/audio/Roll-Dice-Sound.wav");
						//Main.class.getResource("audio/Roll-Dice-Sound.wav"))
						//game.playSound(Main.class.getResource("audio/Roll-Dice-Sound.wav").toString(),false);
						try {
							Thread.sleep(600);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//PlayMusic.loadAudio(Roll_music);
						game.rollDice();
						//game.playSound("src/audio/Roll-Dice-Sound.wav",false);
						
						
						
					}
				}).start();
			}
		});
		add(rollDiceButton);

		menuBar.setBounds(0, 0, 1280, 30);
		menuBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
	
		add(menuBar);
		setComponentZOrder(menuBar, 1);

		closeButton = new JButton();
		closeButton.setBounds(1245, 0, 30, 30);
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setFocusPainted(false);
		closeButton.setIcon(new ImageIcon(Main.class.getResource("images/MainMenu/closeButton.png")));
		closeButton.setRolloverIcon(new ImageIcon(Main.class.getResource("images/MainMenu/closeButtonEntered.png")));
		closeButton.setPressedIcon(new ImageIcon(Main.class.getResource("images/MainMenu/closeButtonPressed.png")));
		add(closeButton);
		setComponentZOrder(closeButton, 0);

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
	}

	public void onRollingDice() {
		rollDice = true;
		rollDiceButton.setVisible(false);
	}

	public void offRollingDice() {
		rollDice = false;
	}

	public void onDiceNumber(int _diceNum) {
		diceNumber[_diceNum - 1].setVisible(true);

	}

	public void offDiceNumber(int _diceNum) {
		diceNumber[_diceNum - 1].setVisible(false);
		rollDiceButton.setVisible(true);
	}

	@Override
	public void paint(Graphics g) {
		//screenImage = createImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		screenImage = createImage(1280, 720);
		screenDraw((Graphics2D) screenImage.getGraphics());
		g.drawImage(screenImage, 0, 0, null);
	}

	public void screenDraw(Graphics2D g) {
		g.drawImage(background, 0, 0, null);
		if (rollDice)
			g.drawImage(rollingDice, 540, 220, null);
		paintComponents(g);
		repaint();
	}

	public void playerMove(Player _nowPlayer, Point _interPoint) {
		JLabel label = playerLabel[_nowPlayer.getID()];
		label.setLocation(_interPoint);

	}
}