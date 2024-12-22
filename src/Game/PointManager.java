package Game;
//src/Game/PointManager.java

import java.awt.Point;

class  PointManager {
	
	Point[][] playerPoint;
	int yOffset = -40; // 맵을 위로 이동시키는 y 오프셋
	
	int[] coordVal = { 667, 531, 535, 511, 445, 459, 368, 408, 283, 343, 324, 264, 403, 212, 483, 161, 604, 108, 732,
			135, 816, 186, 896, 235, 980, 299, 943, 378, 860, 430, 744, 482, 
			
			624, 548, 516, 499, 428, 447, 351, 397,
			251, 326, 343, 252, 420, 202, 502, 151, 634, 87, 749, 146, 833, 198, 913, 247, 1006, 318, 927, 391, 843,
			442, 758, 493, 
			
			624, 505, 498, 488, 412, 436, 334, 387, 308, 318, 361, 244, 437, 193, 519, 144, 633, 126,
			766, 160, 850, 210, 930, 261, 952, 318, 911, 400, 825, 453, 741, 505, 
			
			590, 529, 
			479, 476, 395, 425, 316,373, 283, 301, 
			377, 235, 454, 181, 536, 134, 662, 105, 
			743, 173, 857, 219, 900, 253  // 947, 273 
			
			, 980, 335, 
			895, 413, 808, 462, 724, 516 };

	PointManager() {
		playerPoint = new Point[4][16];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 16; j++) {
				int x = coordVal[i * 32 + j * 2];
				int y = coordVal[i * 32 + j * 2 + 1] + yOffset; // y 오프셋 추가
				playerPoint[i][j] = new Point(x, y);
//				playerPoint[i][j] = new Point(coordVal[i * 32 + j * 2], coordVal[i * 32 + j * 2 + 1]);
			}
		}
	}

	Point getPlayerPoint(int playerNum, int position) {

		Point originalPoint = playerPoint[playerNum][position];
		return new Point(originalPoint.x, originalPoint.y + yOffset);
//		return playerPoint[playerNum][position];
	}

}
