import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class WaitingRoom extends JFrame {
    private boolean closed = false;
    private String nickname;
    private int num;
    private ArrayList<JLabel> usernames = new ArrayList<>();
    private JTextArea chatArea;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket;
    JPanel rightPanel;
    JPanel leftPanel;

    public WaitingRoom(String nickname) {
        this.nickname = nickname;
        this.num = CentralChatServer.clientCount;
        setTitle("Waiting Room");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2)); // 1행 2열 레이아웃
        add(mainPanel, BorderLayout.CENTER);

        // 왼쪽 패널 (User 패널)
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(CentralChatServer.clientCount, 1, 10, 10)); // 클라이언트 수에 맞춤
        leftPanel.setBackground(new Color(200, 200, 255)); // 연한 파란색 배경
        mainPanel.add(leftPanel);

        // 오른쪽 패널 (채팅창 및 버튼)
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(new Color(255, 240, 200)); // 연한 주황색 배경
        mainPanel.add(rightPanel);

        // 유저 패널 동적 추가 (왼쪽)
        for (int i = 0; i < 4; i++) {
            JPanel userPanel = new JPanel();
            userPanel.setBackground(new Color(220, 220, 255)); // 연한 색
            userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JLabel userLabel = new JLabel("User " + i);
            usernames.add(userLabel);
            userPanel.add(userLabel); // 유저 이름 (추후 업데이트 가능)
            leftPanel.add(userPanel);
        }

        // 채팅창 추가 (오른쪽 CENTER)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(0, 0)); // BorderLayout에서는 크기 자동 조정
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // 텍스트 입력 필드 및 버튼 패널 (오른쪽 SOUTH)
        JPanel inputPanel = new JPanel(new BorderLayout()); // 입력 필드와 버튼을 포함하는 패널

        // 텍스트 입력 필드
        JTextField chatInputField = new JTextField();
        chatInputField.setFont(new Font("Arial", Font.PLAIN, 16));
        chatInputField.setPreferredSize(new Dimension(0, 30)); // 높이 조정
        inputPanel.add(chatInputField, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 버튼 중앙 정렬
        buttonPanel.setPreferredSize(new Dimension(0, 50)); // 높이 조정

        // "Ready" 또는 "Start Game" 버튼 추가
        JButton actionButton;
        if (num == 0) {
            actionButton = new JButton("Start Game");
            actionButton.addActionListener(e -> startGame());
        } else {
            actionButton = new JButton("Ready");
            actionButton.addActionListener(e -> {
                chatArea.append(nickname + " is ready.\n"); // 채팅창에 Ready 상태 표시
            });
        }
        actionButton.setFont(new Font("Arial", Font.BOLD, 16));
        buttonPanel.add(actionButton);

        // 버튼 패널을 inputPanel의 SOUTH에 추가
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // inputPanel을 오른쪽 패널의 SOUTH에 추가
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        // 텍스트 입력 필드 이벤트 리스너
        chatInputField.addActionListener(e -> {
            String message = nickname + ": " + chatInputField.getText();
            sendMessage(message);
            chatInputField.setText(""); // 입력 필드 비우기
        });

        // 서버 연결
        connectToServer();

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

    // 서버 연결 메서드
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 9999); // 서버 소켓 연결
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            // 클라이언트 쓰레드 시작
            new ClientChatThread().start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ClientChatThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = dis.readUTF(); // 메시지 수신
                    String[] parts = message.split("/");

                    String command = parts[0]; // "/" 앞부분: "update_user_panel"
                    int n = Integer.parseInt(parts[1]); // "/" 뒷부분: 정수 5

                    if (command.equals("update_user_panel")) {
                        for(int i=0; i<n; i++) {
                            System.out.println(i);
                            usernames.get(i).setText("babo");
                            repaint();
                        }
                    }
                    else {
                        System.out.println("not equal");
                        appendText(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    public void appendText(String msg) {
        chatArea.append(msg + "\n");
        chatArea.setCaretPosition(chatArea.getText().length());
    }

    public void sendMessage(String msg) {
        try {
            dos.writeUTF(msg);
        } catch (IOException e) {
            appendText("dos.write() error");
            try {
                dos.close();
                dis.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
