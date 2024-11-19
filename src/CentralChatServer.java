import java.io.*;
import java.net.*;
import java.util.*;

public class CentralChatServer {
    private static final int PORT = 9999; // 서버 포트 번호
    static final List<ServerThread> clients = new ArrayList<>(); // 클라이언트 리스트
    private static boolean running = true; // 서버 실행 상태
    static int clientCount = 0;

    public static void main(String[] args) {
        System.out.println("Central Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Central Server is running...");

            // 서버 실행 루프
            while (running) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                DataInputStream is = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream os = new DataOutputStream(clientSocket.getOutputStream());

                // 새로운 클라이언트 핸들러 생성 및 실행
                ServerThread thread = new ServerThread(clientSocket, Integer.toString(clientCount), is, os );
                synchronized (clients) {
                    clients.add(thread); // 클라이언트를 리스트에 추가
                }
                thread.start(); // 클라이언트 핸들러를 쓰레드로 실행
                clientCount++;
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            System.out.println("Server shutting down...");
        }
    }
}

class ServerThread extends Thread {
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream is;
    final DataOutputStream os;
    Socket s;
    boolean active;

    public ServerThread(Socket s, String name, DataInputStream is, DataOutputStream os) {
        this.is = is;
        this.os = os;
        this.name = name;
        this.s = s;
        this.active = true;
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                message = is.readUTF();        // 어떤 클라이언트로 부터 들어오는 데이터를 읽어들여서
                System.out.println(message);   // (일단 서버의 콘솔장에 출력해서 확인하고)
                for (ServerThread t : CentralChatServer.clients) {        // ArrayList에 등록되어 있는 모든 사용자에게 순서대로 그 메시지 전달
                    t.os.writeUTF(this.name + " : " + message);   // t 사용자와 통신하는 스레드 안의 os.writeUTF()를 호출하여 메시지 전달
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            this.is.close();
            this.os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
