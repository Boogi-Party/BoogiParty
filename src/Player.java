public class Player {
	int ID;
	int coin;
	int position;
	String name;
	int use_item;
	int roundMap;

	// ArrayList<String> chance;

	Player(int _id, String name) {
		ID = _id;
		coin = 500;
		position = 0;
		this.name = name;
		use_item = 0;
		roundMap=0;
	}

	public void increPosition() {
		position++;
		//한바퀴 돌았을 때 위치초기화, 코인지급, 바퀴수 증가
		if (position == 16) {
			position = 0;
			coin += 750;
			roundMap++;
		}
		// position %= 16;
	}

	public void resetPosition() {
		position = 0;
	}

	
	public int getID() {
		return ID;
	}


	public int useCoin(int use) {
		coin -= use;
		return coin;
	}

	public int getPosition() {
		return position;
	}
	
	public int setPosition(int point) {
		position = point;
		return position;
	}
	
	public String getName() {
		return name;
	}
	public void setCoin(int coin) { //setter
	        this.coin = coin;
	}
	public int getCoin() { //getter
		return coin;
	}
	
	public int get_use_item() {
		return use_item;
	}
	
	public int set_use_item(int use) {
		use_item = use;
		return use_item;
	}
	
	public int get_roundMap() {
		return roundMap;
	}
	public int item_plus_Position() {
		 position = position + 5;
		return position;
	}
}
