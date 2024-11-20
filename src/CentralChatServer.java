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
                // 새로운 클라이언트 핸들러 생성 및 실행
                ServerThread thread = new ServerThread(clientSocket, Integer.toString(clientCount));
                synchronized (clients) {
                    clients.add(thread); // 클라이언트를 리스트에 추가
                }
                thread.start(); // 클라이언트 핸들러를 쓰레드로 실행
                clientCount++;
                System.out.println("Now client : " + clientCount);
                for(ServerThread s : clients) {
                    s.drawUserPanel(clientCount);
                }
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
    public String name;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    Socket s;
    boolean active;

    public ServerThread(Socket s, String name) {
        this.name = name;
        this.s = s;
        this.active = true;

        try {
            is = s.getInputStream();
            dis = new DataInputStream(is);
            os = s.getOutputStream();
            dos = new DataOutputStream(os);
        }catch (Exception e) {

        }
    }
    // 클라이언트로 메시지 전송
    public void WriteOne(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            try {
                dos.close();
                dis.close();
                s.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            CentralChatServer.clients.remove(this); // 에러가난 현재 객체를 벡터에서 지운다
        }
    }



    //모든 다중 클라이언트에게 순차적으로 채팅 메시지 전달
    public void WriteAll(String str) {
        for (int i = 0; i < CentralChatServer.clients.size(); i++) {
            ServerThread user = CentralChatServer.clients.get(i);     // get(i) 메소드는 user_vc 컬렉션의 i번째 요소를 반환
            user.WriteOne(str);
        }
    }

    public void drawUserPanel(int clientCount) {
        try {
            dos.writeUTF("update_user_panel/"+clientCount);
        } catch (IOException e) {
            try {
                dos.close();
                dis.close();
                s.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            CentralChatServer.clients.remove(this); // 에러가난 현재 객체를 벡터에서 지운다
        }
    }

    @Override
    public void run() {
        String message;
        while (true) {
            try {
                message = dis.readUTF();        // 어떤 클라이언트로 부터 들어오는 데이터를 읽어들여서
                message = message.trim();
                System.out.println(message);   // (일단 서버의 콘솔장에 출력해서 확인하고)
                WriteAll(message+ "\n"); // Write All
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
