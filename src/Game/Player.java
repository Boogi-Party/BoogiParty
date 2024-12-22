package Game;
//src/Game/Player.java
public class Player {
	int ID;
	int coin;
	int position;
	String name;
	int roundMap;

	// ArrayList<String> chance;

	Player(int _id, String name) {
		ID = _id;
		coin = 300;
		position = 0;
		this.name = name;
		roundMap = 0;
	}

	public void increPosition(GameGUI gameGUI) {
		position++;
		//한바퀴 돌았을 때 위치초기화, 코인지급, 바퀴수 증가
		if (position == 16) {
			position = 0;
			coin += 750;
			roundMap++;
			gameGUI.updateCoinLabel(ID);
			gameGUI.updateLapLabel(ID);
		}
	}

	public void resetPosition() {
		position = 0;
	}
	
	public int getID() {
		return ID;
	}


	public int getPosition() {
		return position;
	}
	
	public void setPosition(int point) {
		position = point;
	}
	
	public String getName() {
		return name;
	}
	public void setCoin(int coin) { //setter
		if (coin <= 0) {
			coin = 0;
		}
		this.coin = coin;
	}
	public int getCoin() { //getter
		return coin;
	}

	
	public int get_roundMap() {
		return roundMap;
	}
}
