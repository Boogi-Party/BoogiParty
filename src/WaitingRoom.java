import javax.swing.*;
import java.awt.*;
import java.net.Socket;

public class WaitingRoom extends JFrame {
    private boolean closed = false;

    public WaitingRoom(String mode, String nickname) {
        setTitle("Waiting Room");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel statusLabel = new JLabel("Waiting for players...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(statusLabel, BorderLayout.CENTER);

        if (mode.equals("create")) {
            startServer(); // 서버 생성
            connectToServer(nickname); // 자기 자신 연결
        } else if (mode.equals("join")) {
            connectToServer(nickname); // 서버에 연결
        }

        // 화면 표시
        setVisible(true);
    }

    private void startServer() {
        // ServerThread 실행 (게임 서버 생성)
        new ServerThread().start();
    }

    private void connectToServer(String nickname) {
        // 클라이언트 소켓 연결
        try {
            Socket socket = new Socket("localhost", 12345);
            System.out.println(nickname + " connected to the server!");

            // 클라이언트 스레드 시작
            new ClientThread(socket, nickname).start();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // 프로그램 종료
        }
    }

    public void waitForClose() {
        while (!closed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeRoom() {
        closed = true;
        dispose();
    }
}
