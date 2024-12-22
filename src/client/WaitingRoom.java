package client;
//src/client/WaitingRoom.java

import Game.GameGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WaitingRoom extends JPanel {
    private String nickname;
    protected ArrayList<JLabel> usernames = new ArrayList<>();
    protected JTextArea chatArea;
    private ClientThread clientThread;
    private JTextField chatInputField;

    JPanel rightPanel;
    JPanel leftPanel;
    ArrayList<JLabel> userLabels = new ArrayList<>();
    protected String hostname; // 호스트 여부
    private Main parent;

    private boolean isReady = false; // 게임 준비 상태
    private JButton readyButton; // 준비 버튼
    protected JButton gameStartButton; // 게임 시작 버튼
    JButton exitButton;

    public WaitingRoom(String nickname, int port, Main parent) {
        clientThread = new ClientThread(this, port, nickname);
        this.nickname = nickname;
        this.hostname = hostname; // 호스트 여부 설정
        this.parent = parent;

        parent.setScreenNotGameSize();
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2)); // 1행 2열 레이아웃
        add(mainPanel, BorderLayout.CENTER);

        // 왼쪽 패널 (User 패널)
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 클라이언트 수에 맞춤
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
            userLabels.add(userLabel);
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
        chatInputField = new JTextField();
        chatInputField.setFont(new Font("Nanum Gothic", Font.PLAIN, 16));
        chatInputField.setPreferredSize(new Dimension(0, 30)); // 높이 조정
        inputPanel.add(chatInputField, BorderLayout.CENTER);

        JButton sendButton = new JButton("보내기");
        sendButton.setPreferredSize(new Dimension(80, 30));

        // 텍스트 입력 필드 이벤트 리스너 (Enter 키)
        chatInputField.addActionListener(sendMessageAction);
        // "보내기" 버튼 이벤트 리스너
        sendButton.addActionListener(sendMessageAction);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 버튼 중앙 정렬
        buttonPanel.setPreferredSize(new Dimension(0, 50)); // 높이 조정

        // "나가기" 버튼 추가
        exitButton  = new JButton("나가기");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitRoom();
            }
        });

        buttonPanel.add(exitButton);

        // 버튼 패널을 inputPanel의 SOUTH에 추가
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
        inputPanel.add(chatInputField, BorderLayout.CENTER); // 텍스트 필드
        inputPanel.add(sendButton, BorderLayout.EAST);       // 보내기 버튼 추가

        // inputPanel을 오른쪽 패널의 SOUTH에 추가
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        clientThread.connectToServer();


        // 게임 시작 버튼 추가
        if (hostname.equals(nickname)) {
            gameStartButton = new JButton("게임 시작");
            gameStartButton.setPreferredSize(new Dimension(100, 40));
            gameStartButton.setEnabled(false); // 초기에는 비활성화
            gameStartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clientThread.sendMessage("START_GAME");
                }
            });
            buttonPanel.add(gameStartButton);
        }
        else {
            // 준비 상태 버튼 추가
            readyButton = new JButton("준비");
            readyButton.setPreferredSize(new Dimension(100, 40));
            readyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleReadyState();
                }
            });
            buttonPanel.add(readyButton);
        }

        // 화면 표시
        //setVisible(true);
    }
    private void toggleReadyState() {
        isReady = !isReady;
        readyButton.setText(isReady ? "준비 해제" : "준비");
        if (isReady) {
            exitButton.setEnabled(false);
        } else {
            exitButton.setEnabled(true);
        }
        clientThread.sendMessage("READY_STATE/" + nickname + "/" + (isReady ? "READY" : "NOT_READY"));
    }

    private void exitRoom() {
        try {
            clientThread.sendMessage("UPDATE_HOST");

            // 클라이언트 스레드 종료
            synchronized (this) {
                clientThread.closeConnection();

                // UI 갱신
                SwingUtilities.invokeLater(() -> {
                   parent.setPanel(new RoomList(nickname, parent));
                });
            }
        } catch (Exception e) {
            System.err.println("Error during exitRoom: " + e.getMessage());
        }
    }

    // 액션 이벤트 리스너 (Enter 키와 버튼 클릭 공유)
    ActionListener sendMessageAction = e -> {
        String message = chatInputField.getText();
        if (!chatInputField.getText().trim().isEmpty()) { // 빈 메시지 방지
            clientThread.sendMessage(message);
            chatInputField.setText(""); // 입력 필드 비우기
            chatInputField.requestFocus(); // 포커스 유지
        }
    };

    public void appendText(String msg) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(msg + "\n"); // 메시지 추가
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // 스크롤 자동 이동
        });
    }

    protected GameGUI gameStart(int numPlayer, String[] playerInfo) {
        GameGUI gameGUI = new GameGUI(clientThread, parent, numPlayer, playerInfo, this);
        parent.setPanel(gameGUI);
        return gameGUI;
    }
}
