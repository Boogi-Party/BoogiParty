import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket clientSocket;  // 클라이언트 소켓
    private BufferedReader in;    // 클라이언트로부터의 입력 스트림
    private PrintWriter out;      // 클라이언트로의 출력 스트림

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // 클라이언트와 통신을 위한 스트림 설정
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 클라이언트와 통신 시작
            out.println("Welcome to the Marble Game Server!");

            String clientMessage;
            while ((clientMessage = in.readLine()) != null) { // 클라이언트로부터 메시지 수신
                System.out.println("Received: " + clientMessage); // 서버 콘솔에 출력
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 연결 종료 시 소켓 닫기
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}