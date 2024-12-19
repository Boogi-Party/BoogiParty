package server;

import Game.Game;

import java.io.*;
import java.net.*;
import java.util.*;

public class RoomThread extends Thread {
    private final int roomId;
    private final List<UserThread> clients = new ArrayList<>();
    private boolean isRoomActive = true;
    String hostname;
    private final Map<String, Boolean> readyStates = new HashMap<>();

    private Game game;
    private boolean isGameRunning = false;
    //private GameSession gameSession; // 게임 로직을 관리하는 객체

    private final OnEmptyRoomCallback callback;

    public interface OnEmptyRoomCallback {
        void onEmptyRoom(int roomId);
    }

    public RoomThread(int roomId, OnEmptyRoomCallback callback, String hostname) {
        this.roomId = roomId;
        this.callback = callback;
        this.hostname = hostname; // 호스트 이름 저장
    }

    public String getHostname() {
        return hostname;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getPort() {
        return 9500 + roomId;
    }

    public String getRoomName() {
        return "Room" + roomId;
    }

    public int getClients() {
        synchronized (clients) {
            return clients.size();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(getPort())) {
            while (isRoomActive) {
                Socket clientSocket = serverSocket.accept();
                UserThread userThread = new UserThread(clientSocket);

                synchronized (clients) {
                    clients.add(userThread);
                }
                userThread.start();
                broadcastUserList();
            }
        } catch (IOException e) {
            if (isRoomActive) {
                System.err.println("Error in RoomThread: " + e.getMessage());
            }
        } finally {
            closeRoom();
        }
    }


    private void broadcastUserList() {
        synchronized (clients) {
            StringBuilder userListBuilder = new StringBuilder("USER_UPDATE/");
            for (UserThread user : clients) {
                userListBuilder.append(user.getUserName()).append(",");
            }
            if (!clients.isEmpty()) {
                userListBuilder.deleteCharAt(userListBuilder.length() - 1);
            }

            for (UserThread user : clients) {
                user.sendMessage(userListBuilder.toString());
            }

            if (hostname == null && !clients.isEmpty()) {
                hostname = clients.get(0).getUserName(); // 첫 번째 유저를 방장으로 지정
            }
        }
    }

    private void broadcastGameStart() {
        game = new Game(this);
        synchronized (clients) {
            int playerCount = clients.size();
            StringBuilder startMessage = new StringBuilder("GAME_START/");
            startMessage.append(playerCount).append("/");

            for (int i = 0; i < clients.size(); i++) {
                UserThread user = clients.get(i);
                startMessage.append(user.getUserName())
                        .append(":")
                        .append(i + 1)
                        .append(",");
            }

            // 마지막 쉼표 제거
            if (clients.size() > 0) {
                startMessage.deleteCharAt(startMessage.length() - 1);
            }

            // 각 클라이언트에게 전송
            for (UserThread user : clients) {
                user.sendMessage(startMessage.toString());
            }
            System.out.println("Game started with message: " + startMessage);

            game.start();
        }
    }

    private void broadcastRollDice(String playerNum, int dice) {
        synchronized (clients) {
            for (UserThread user : clients) {
                user.sendMessage("ROLL_DICE/" + playerNum + "/" + dice);
            }
        }
    }

    private void broadcastMiniGame(String playerNum, String gameType) {
        synchronized (clients) {
            for (UserThread user : clients) {
                user.sendMessage("MINI_GAME/" + playerNum + "/" + gameType);
            }
        }
    }

    private void closeRoom() {
        isRoomActive = false;
        synchronized (clients) {
            for (UserThread user : clients) {
                user.closeConnection();
            }
            clients.clear();
        }
        callback.onEmptyRoom(roomId); // 서버에 방이 비었다고 알림
    }

    protected class UserThread extends Thread {
        private final Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private String userName;

        public UserThread(Socket socket) {
            this.socket = socket;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                this.userName = dis.readUTF(); // 첫 메시지는 사용자 이름
                dos.writeUTF(hostname);
                sendInitialReadyStates(); // 기존 상태 전송
                broadcastMessage(userName + "님이 입장하셨습니다."); // 입장 메시지만 보내기
            } catch (IOException e) {
                System.err.println("Error initializing UserThread: " + e.getMessage());
            }
        }

        public String getUserName() {
            return userName;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = dis.readUTF();
                    String[] parts = message.split("/", 3);

                    String command = parts[0];

                    if (command.equals("EXIT_ROOM")) {
                        break;
                    }
                    else if (command.equals("START_GAME")) {
                        broadcastGameStart(); // 모든 클라이언트에 게임 시작 명령
                    }
                    else if (command.equals("READY_STATE")) {
                        if (parts.length == 3) {
                            String user = parts[1];
                            String state = parts[2];
                            broadcastReadyState(user, state);
                        }
                    }
                    else if (command.equals("ROLL_DICE")) {
                        //System.out.println("now player : " + game.getPlayerIdx());
                        if (game.getPlayerIdx() == Integer.parseInt(parts[1]) && !game.getIsMiniGameRunning()) {
                            int dice = game.rollDice();

                            broadcastRollDice(parts[1], dice);
                         }
                    }
                    else if (command.equals("MINI_GAME")) {
                        broadcastMiniGame(parts[1], parts[2]);
                    }
                    else {
                        broadcastMessage(userName + " : " +message); // 메시지 브로드캐스트
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection lost for user: " + userName);
            } finally {
                broadcastMessage(userName + "님이 퇴장하셨습니다."); // 입장 메시지만 보내기
                closeConnection();
            }
        }
        private void sendInitialReadyStates() {
            synchronized (readyStates) {
                for (Map.Entry<String, Boolean> entry : readyStates.entrySet()) {
                    String user = entry.getKey();
                    boolean isReady = entry.getValue();
                    sendMessage("READY_STATE/" + user + "/" + (isReady ? "READY" : "NOT_READY"));
                }
            }
        }


        private void updateHost() {
            synchronized (clients) {
                String fullMessage = "NEW_HOST/" + hostname;
                for (UserThread user : clients) {
                    user.sendMessage(fullMessage); // 메시지를 각 클라이언트에 전송
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                String fullMessage = "USER_MSG/" + message;
                for (UserThread user : clients) {
                    user.sendMessage(fullMessage); // 메시지를 각 클라이언트에 전송
                }
            }
        }

        public void sendMessage(String message) {
            try {
                dos.writeUTF(message);
            } catch (IOException ignored) {
            }
        }

        public void closeConnection() {
            synchronized (clients) {
                clients.remove(this); // 현재 유저 제거
                broadcastUserList();

                // 방장이 나간 경우 새로운 방장 설정
                if (this.userName.equals(hostname)) {
                    if (!clients.isEmpty()) {
                        hostname = clients.get(0).getUserName(); // 남은 첫 번째 유저를 방장으로 지정
                        updateHost();
                        broadcastUserList();
                    } else {
                        hostname = null; // 모든 유저가 나간 경우
                    }
                }
                if (clients.isEmpty()) {
                    closeRoom(); // 방이 비었을 때 호출
                }
            }
            try {
                socket.close();
                dis.close();
                dos.close();
            } catch (IOException ignored) {
            }
        }

        private void broadcastReadyState(String user, String state) {
            synchronized (clients) {
                // READY 상태 저장
                boolean isReady = "READY".equalsIgnoreCase(state);
                readyStates.put(user, isReady);

                // 모든 클라이언트에게 준비 상태 브로드캐스트
                String message = "READY_STATE/" + user + "/" + state;
                for (UserThread client : clients) {
                    client.sendMessage(message);
                }

                // 모든 클라이언트가 준비 상태인지 확인
                if (allReady()) {
                    // 호스트에게 게임 시작 버튼 활성화 메시지 전송
                    for (UserThread client : clients) {
                        if (client.getUserName().equals(hostname)) {
                            client.sendMessage("ENABLE_GAME_START");
                        }
                    }
                } else {
                    // 비활성화 상태 전송 (모든 클라이언트 준비되지 않은 경우)
                    for (UserThread client : clients) {
                        if (client.getUserName().equals(hostname)) {
                            client.sendMessage("DISABLE_GAME_START");
                        }
                    }
                }
            }
        }
        private boolean allReady() {
            synchronized (readyStates) {
                // 호스트 제외 모든 사용자가 준비 상태인지 확인
                for (UserThread client : clients) {
                    if (!client.getUserName().equals(hostname) && !readyStates.getOrDefault(client.getUserName(), false)) {
                        return false;
                    }
                }
                return true;
            }
        }



    }
}
