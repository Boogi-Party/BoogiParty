package Game;
//src/Game/MiniGame.java
public interface MiniGame {
    // 미니게임 종료 후 호출될 메서드
    void onMiniGameEnd(); // 미니게임 종료 후 수행할 작업
    boolean isGameEnded(); // 새로 추가
}
