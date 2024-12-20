package Game;

import server.RoomThread;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Game extends Thread {
	public int numPlayer;
	private int playerIdx;

	private int use_item;
	public PointManager pointManager = new PointManager();
	public GameGUI gameGUI;
	ArrayList<Player> playerList;

	private volatile boolean isMiniGameRunning;

	RoomThread roomThread;
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
		//dice = new Random().nextInt(6) + 1;
		dice = 4;
		playerIdx = (playerIdx + 1) % numPlayer;
		//System.out.println("Player updated : " + playerIdx);
		return dice;
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
}
