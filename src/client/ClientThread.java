//src/client/ClientThread.java
package client;
import Game.GameGUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    JTextArea chatArea;
    int port;
    protected String nickname;
    String hostname;

    WaitingRoom waitingRoom;
    GameGUI gameGUI;

    private volatile boolean running = true; // 쓰레드 실행 플래그

    public String getNickname() {
        return nickname;
    }

    public ClientThread(WaitingRoom waitingRoom, int port, String nickname) {
        this.waitingRoom = waitingRoom;
        this.chatArea = waitingRoom.chatArea;
        this.port = port;
        this.nickname = nickname;
    }

    protected void connectToServer() {
        try {
            socket = new Socket("localhost", port);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            dos.writeUTF(nickname);
            hostname = dis.readUTF();
            waitingRoom.setHostname(hostname);

            // 클라이언트 쓰레드 시작
            this.start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(waitingRoom, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            waitingRoom.appendText("dos.write() error");
            closeConnection(); // 연결 종료
        }
    }

    @Override
    public void run() {
        while (running) { // 플래그를 확인
            try {
                String message = dis.readUTF(); // 메시지 수신
                String[] parts = message.split("/");
//                System.out.println("Command: " + parts[0] + ", PlayerIdx: " + parts[1] + ", GameType: " + parts[2] + ", State: " + parts[3]);

                String command = parts[0];

                if ("USER_UPDATE".equals(command)) {
                    String[] userNames = parts[1].split(",");
                    for (String name : userNames) {
                        System.out.println(name);

                    }
                    updateUserPanel(userNames);

                }
                else if ("USER_MSG".equals(command)) {
                    if (parts.length > 1) {
                        waitingRoom.appendText(parts[1]); // 채팅창에 메시지 출력
                    }
                }
                else if ("NEW_HOST".equals(command)) {
                    hostname = parts[1];
                    waitingRoom.setHostname(hostname);
                }
                else if ("READY_STATE".equals(command)) {
//                    if (parts.length == 3) {
                        String user = parts[1];
                        String state = parts[2];
                        boolean isReady = "READY".equalsIgnoreCase(state);
                        updateReadyState(user, isReady); // UI 업데이트
//                    }
                }
                else if ("ENABLE_GAME_START".equals(command)) {
                    enableGameStartButton(true); // 게임 시작 버튼 활성화
                }
                else if ("DISABLE_GAME_START".equals(command)) {
                    enableGameStartButton(false); // 게임 시작 버튼 비활성화
                }
                else if ("GAME_START".equals(command)) {
                    int numPlayer = Integer.parseInt(parts[1]);
                    String[] playerInfo = parts[2].split(","); // 플레이어 정보 배열
                    startGame(numPlayer, playerInfo);
                }
                //여기에 게임 로직 구현~!~!#~!@#@#$#!@$@#
                else if ("ROLL_DICE".equals(command)) {
                    gameGUI.rollDiceMotion(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }
                else if ("MINI_GAME".equals(command)) {
                    gameGUI.miniGameStart(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }

                else if ("IN_GAME_MSG".equals(command)) {
                    gameGUI.renderChatMessage(Integer.parseInt(parts[1]), parts[2]);
                }
                else if ("GAMBLING".equals(command)) {
                    gameGUI.miniGame.update(parts[1] + "/" + parts[2] + "/" + parts[3]);
                }
                else if ("GBB".equals(command)) {
                    gameGUI.miniGame.update(parts[1] + "/" + parts[2] + "/" + parts[3]);
                }
                else if ("MINI_GAME_END".equals(command)) {
                    gameGUI.endGame();
                }
                else if ("DRAW_BULLET".equals(command)) {
                    gameGUI.miniGame.update("DRAW_BULLET");
                }
                else if ("HIT".equals(command)) {
                    gameGUI.miniGame.update("HIT");
                }
                else if ("LAP".equals(command)) {
                    gameGUI.updateLapLabel(Integer.parseInt(parts[1]));
                }
                else if ("GAME_OVER".equals(command)) {
                    gameGUI.exitGame();
                }
                else if ("QUIZ".equals(command)) {
                    gameGUI.quizStart(Integer.parseInt(parts[1]), parts[2]);
                }
                else if ("QUIZ_OVER".equals(command)) {
                    String msg = parts[1] + "/" + parts[2];
                    if (parts.length == 4) {
                        msg += "/" + parts[3];
                    }
                    gameGUI.endQuiz(msg);
                }
                else if ("ITEM_USE".equals(command)) {
                    if (parts[2].equals("1")) {
                        gameGUI.item_plus_move(Integer.parseInt(parts[1]));
                    }
                    else if (parts[2].equals("2")) {
                        gameGUI.item_attack_move(Integer.parseInt(parts[1]));
                    }
                }
                else {
                    waitingRoom.appendText(message); // 일반 메시지 출력
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
                break; // 루프 종료
            }
        }
        cleanupResources();
    }

    private void enableGameStartButton(boolean enabled) {
        if (hostname.equals(nickname)) {
            SwingUtilities.invokeLater(() -> {
                waitingRoom.gameStartButton.setEnabled(enabled);
            });
        }
    }

    private void updateReadyState(String user, boolean isReady) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < waitingRoom.usernames.size(); i++) {
                JLabel label = waitingRoom.usernames.get(i);
                if (label.getText().contains(user)) {
                    JPanel userPanel = (JPanel) waitingRoom.leftPanel.getComponent(i);
                    userPanel.setBackground(isReady ? new Color(144, 238, 144) : new Color(220, 220, 255));
                    label.setText(user + (isReady ? " (준비됨)" : ""));
                    break;
                }
            }
            waitingRoom.repaint();
        });
    }
    private void updateUserPanel(String[] users) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 4; i++) {
                if (i < users.length) {
                    if (users[i].equals(hostname)) {
                        waitingRoom.usernames.get(i).setText(users[i] + " 👑");
                        waitingRoom.setHostname(hostname);
                    }
                    else {
                        waitingRoom.usernames.get(i).setText(users[i]);
                    }
                    // 폰트 크기 조절 (예: Nanum Gothic, 크기 16)
                    waitingRoom.updateButtonPanel();
                    waitingRoom.usernames.get(i).setFont(new Font("Nanum Gothic", Font.BOLD, 16));
                } else {
                    waitingRoom.usernames.get(i).setText("<Empty>");
                    JPanel userPanel = (JPanel) waitingRoom.leftPanel.getComponent(i);
                    userPanel.setBackground(new Color(220, 220, 255));
                    // 폰트 크기 조절 (예: Nanum Gothic, 크기 16)
//                    waitingRoom.usernames.get(i).setFont(new Font("Nanum Gothic", Font.BOLD, 16));
                    waitingRoom.usernames.get(i).setFont(new Font("Nanum Gothic", Font.ITALIC, 16));

                }
            }
            waitingRoom.repaint();
        });
    }

    public void closeConnection() {
        sendMessage("EXIT_ROOM/" + nickname);
        running = false; // 쓰레드 종료 신호
        cleanupResources();
    }

    private void cleanupResources() {
        try {
            if (socket != null) socket.close();
            if (dis != null) dis.close();
            if (dos != null) dos.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    private void startGame(int numPlayer, String[] playerInfo) {
        this.gameGUI = waitingRoom.gameStart(numPlayer, playerInfo);
    }

}