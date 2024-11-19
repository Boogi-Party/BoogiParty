import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class WaitingRoom extends JFrame {
    private boolean closed = false;
    private boolean isRoomCreator; // 방 생성자인지 여부
    private String nickname;
    private int num;
    public WaitingRoom(String nickname) {
        this.nickname = nickname;
        setTitle("Waiting Room");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2)); // 1행 2열 레이아웃
        add(mainPanel, BorderLayout.CENTER);

        // 왼쪽 패널 (User 패널)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4개의 유저 패널
        leftPanel.setBackground(new Color(200, 200, 255)); // 연한 파란색 배경
        mainPanel.add(leftPanel);

        // 오른쪽 패널 (채팅창 및 버튼)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(255, 240, 200)); // 연한 주황색 배경
        mainPanel.add(rightPanel);

        // 유저 패널 4개 추가 (왼쪽)
        for (int i = 1; i <= 4; i++) {
            JPanel userPanel = new JPanel();
            userPanel.setBackground(new Color(220, 220, 255)); // 연한 색
            userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            userPanel.add(new JLabel("User " + i)); // 유저 이름 (추후 업데이트 가능)
            leftPanel.add(userPanel);
        }

        // 채팅창 추가 (오른쪽)
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // 버튼 패널 (오른쪽 하단)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // "Ready" 또는 "Start Game" 버튼 추가
        JButton actionButton;
        if (isRoomCreator) {
            actionButton = new JButton("Start Game");
            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame();
                }
            });
        } else {
            actionButton = new JButton("Ready");
            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chatArea.append(nickname + " is ready.\n"); // 채팅창에 Ready 상태 표시
                }
            });
        }
        actionButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(actionButton);

        // 화면 표시
        setVisible(true);
    }

    // 게임 시작 메서드
    private void startGame() {
        JOptionPane.showMessageDialog(this, "Game is starting!", "Info", JOptionPane.INFORMATION_MESSAGE);
        closeRoom();
    }

    // 대기실 닫기
    public void closeRoom() {
        closed = true;
        dispose();
    }

    // 대기 종료 확인
    public void waitForClose() {
        while (!closed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void connectToServer() {
        try {
            // 서버 소켓에 연결
            Socket socket = new Socket("localhost", 9999); // 로컬 호스트로 연결
            System.out.println(nickname + " connected to server!");

            // 클라이언트 쓰레드 시작
            new ClientThread(socket, nickname).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
