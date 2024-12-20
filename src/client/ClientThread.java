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
            updateHostName();
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
                String[] parts = message.split("/", 4);
//                System.out.println("Command: " + parts[0] + ", PlayerIdx: " + parts[1] + ", GameType: " + parts[2] + ", State: " + parts[3]);

                String command = parts[0];

                if ("USER_UPDATE".equals(command)) {
                    if (parts.length > 1) {
                        String[] userNames = parts[1].split(",");
                        updateUserPanel(userNames);
                    } else {
                        updateUserPanel(new String[0]);
                    }
                }
                else if ("USER_MSG".equals(command)) {
                    if (parts.length > 1) {
                        waitingRoom.appendText(parts[1]); // 채팅창에 메시지 출력
                    }
                }
                else if ("NEW_HOST".equals(command)) {
                    hostname = parts[1];
                    updateHostName();
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
                else if ("MINI_GAME_STATE".equals(command)) {
                    int playerIdx = Integer.parseInt(parts[1]); // 미니게임을 수행하는 플레이어
                    int gameType = Integer.parseInt(parts[2]); // 미니게임 유형
                    String state = parts[3]; // 미니게임 상태 (START, END 등)

                    if ("START".equals(state)) {
                        gameGUI.miniGameStart(playerIdx, gameType);
                    } else if ("END".equals(state)) {
                        gameGUI.endMiniGame(playerIdx, gameType); // 미니게임 종료 처리
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
            for (int i = 0; i < waitingRoom.usernames.size(); i++) {
                if (i < users.length) {
                    if (users[i].equals(hostname)) {
                        waitingRoom.usernames.get(i).setText(users[i] + " 👑");
                    }
                    else {
                        waitingRoom.usernames.get(i).setText(users[i]);
                    }
                } else {
                    waitingRoom.usernames.get(i).setText("Empty");
                }
            }
            waitingRoom.repaint();
        });
    }

    public void closeConnection() {
        running = false; // 쓰레드 종료 신호
        sendMessage("EXIT_ROOM");
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

    private void updateHostName() {
        waitingRoom.hostname = hostname;
    }
}
