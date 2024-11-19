import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerThread extends Thread {

    private static final int PORT = 12345; // 포트 번호
    private ServerSocket serverSocket;
    public static List<Socket> clients = new ArrayList<>(); // 클라이언트 소켓 목록

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                // 클라이언트 연결 대기
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // 클라이언트를 처리할 쓰레드 시작
                new ClientHandler(clientSocket).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
