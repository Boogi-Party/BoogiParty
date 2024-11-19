import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
    private Socket socket;
    private String nickname;
    private BufferedReader in;
    private PrintWriter out;

    public ClientThread(Socket socket, String nickname) {
        this.socket = socket;
        this.nickname = nickname;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 서버에 닉네임 전송
            out.println(nickname + " joined the game!");

            // 메시지 수신 및 출력
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server: " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
