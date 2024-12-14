package server;

import client.RoomThread;

import java.io.*;
import java.net.*;
import java.util.*;

public class CentralChatServer {
    private static final int PORT = 9999;
    private static final ArrayList<RoomThread> rooms = new ArrayList<>();
    private static int count = 0;

    public static void main(String[] args) {
        System.out.println("Central Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Central Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            System.out.println("Server shutting down...");
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String command = in.readLine();

            if ("CREATE_ROOM".equalsIgnoreCase(command)) {
                // 방 생성 요청 처리
                RoomThread roomThread = createRoom();
                out.println("Room Created: ID=" + roomThread.getRoomId() + ", Port=" + roomThread.getPort());
            } else if ("GET_ROOM".equalsIgnoreCase(command)) {
                // 방 목록 요청 처리
                String roomList = getRoomList();
                out.println(roomList);
            } else if (command.startsWith("JOIN_ROOM")) {
                // 방 참가 요청 처리
                int roomId = Integer.parseInt(command.split(" ")[1]);
                int roomPort = 9500 + roomId; // 포트 번호 계산
                out.println("Port=" + roomPort);
            } else if (command.startsWith("REMOVE_ROOM")) {
                // 방 삭제 요청  처리
                int roomId = Integer.parseInt(command.split(" ")[1]);
                removeRoom(roomId);
                out.println("Room Removed: ID=" + roomId);
            } else {
                // 알 수 없는 명령어 처리
                out.println("Unknown Command");
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    private static RoomThread createRoom() {
        RoomThread roomThread = new RoomThread(count++, CentralChatServer::onEmptyRoom);
        synchronized (rooms) {
            rooms.add(roomThread);
        }
        roomThread.start();

        System.out.println("Now Rooms: " + rooms.size());
        return roomThread;
    }

    private static void removeRoom(int roomId) {
        synchronized (rooms) {
            rooms.removeIf(room -> room.getRoomId() == roomId);
            System.out.println("Room Removed: ID=" + roomId + ", Remaining Rooms: " + rooms.size());
        }
    }

    private static String getRoomList() {
        StringBuilder roomListBuilder = new StringBuilder();
        synchronized (rooms) {
            for (RoomThread room : rooms) {
                roomListBuilder.append("Room ID: ").append(room.getRoomId())
                        .append(", Room Name: ").append(room.getRoomName())
                        .append(", Clients: ").append(room.getClients())
                        .append("\n");
            }
        }
        return roomListBuilder.toString().trim();
    }

    // 방이 비었을 때 호출되는 콜백 메서드
    private static void onEmptyRoom(int roomId) {
        System.out.println("Room ID " + roomId + " is empty. Removing...");
        removeRoom(roomId);
    }
}
