package client;

import server.RoomThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class RoomList extends JPanel implements MyPanel {
    private ArrayList<String> rooms = new ArrayList<>();
    private String nickname;
    private JPanel roomListPanel; // 방 목록을 표시할 패널
    private Main parent;

    public RoomList(String nickname, Main parent) {
        this.nickname = nickname;
        this.parent = parent;

        // 배경 패널 추가
        setLayout(new BorderLayout());
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // 왼쪽: 방 목록
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        roomListPanel.setOpaque(false); // 투명하게 설정

        // 반투명 배경 패널 추가
        JPanel roomListBackground = new JPanel(new BorderLayout());
        roomListBackground.setBackground(new Color(0, 0, 0, 100)); // 검정색 반투명
        roomListBackground.add(roomListPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(roomListBackground);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // 방 목록 초기화
        fetchRooms();

        // 오른쪽: 방 생성, 방 참가, 닉네임 표시
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(200, getHeight()));
        rightPanel.setBackground(new Color(0, 0, 0, 100)); // 반투명한 배경 설정

        JButton createRoomButton = new JButton("방 만들기");
        createRoomButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGame();
            }
        });

        JLabel nicknameLabel = new JLabel("닉네임: " + nickname);
        nicknameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정

        JButton refreshButton = new JButton("새로고침");
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchRooms();
            }
        });

        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(createRoomButton);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(nicknameLabel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(refreshButton);
        backgroundPanel.add(rightPanel, BorderLayout.EAST);
    }

    private void fetchRooms() {
        rooms.clear();
        // 서버에서 방 정보 가져오기 (테스트용으로 로컬 데이터 사용)
        try (Socket socket = new Socket("localhost", 9999);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ROOM");
            String response = in.readLine();

            String [] lists = response.split("/");

            for (String room : lists) {
                if (!room.isEmpty()){
                    rooms.add(room);
                }
            }

        } catch (IOException e) {

        }

        // 방 목록 초기화
        updateRoomList();
    }

    private void updateRoomList() {
        super.repaint();
        roomListPanel.removeAll(); // 기존의 모든 컴포넌트를 삭제

        // 방이 없는 경우
        if (rooms.isEmpty()) {
            JLabel emptyLabel = new JLabel("현재 방이 없습니다.");
            emptyLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 50));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setVerticalAlignment(SwingConstants.CENTER); // 세로 가운데 정렬
            emptyLabel.setForeground(Color.WHITE); // 텍스트 색상 흰색으로 설정

            emptyLabel.setVisible(true);

            // 빈 화면을 정렬할 수 있도록 레이아웃 설정
            roomListPanel.setLayout(new BorderLayout());
            roomListPanel.add(emptyLabel, BorderLayout.CENTER); // 중앙에 배치
        } else {
            // 방 목록이 있는 경우, 각 방을 패널에 추가
            roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS)); // 세로로 배치
            for (String roomInfo : rooms) {
                JPanel roomPanel = createRoomPanel(roomInfo);
                roomListPanel.add(roomPanel);
            }
        }

        roomListPanel.revalidate();  // 레이아웃 다시 계산
        roomListPanel.repaint();     // 화면 다시 그리기
    }

    private JPanel createRoomPanel(String roomInfo) {
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.setPreferredSize(new Dimension(600, 50));
        roomPanel.setMaximumSize(new Dimension(600, 50));
        roomPanel.setOpaque(false); // 투명하게 설정
        roomPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        JLabel roomLabel = new JLabel(roomInfo);
        roomLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roomLabel.setHorizontalAlignment(SwingConstants.LEFT);
        roomLabel.setForeground(Color.WHITE); // 텍스트 색상을 흰색으로 설정

        roomPanel.add(roomLabel, BorderLayout.CENTER);

        JButton joinButton = new JButton("Join");
        joinButton.setFont(new Font("Arial", Font.BOLD, 14));
        joinButton.setBackground(new Color(30, 144, 255)); // 배경색 설정
//        joinButton.setFocusPainted(false); // 포커스 테두리 비활성화
        joinButton.addActionListener(e -> joinGame(roomInfo)); // Join 이벤트 연결
        roomPanel.add(joinButton, BorderLayout.EAST);
        return roomPanel;
    }

    private void createGame() {
        try (Socket socket = new Socket("localhost", 9999);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 서버에 방 생성 요청
            out.println("CREATE_ROOM");
            out.println(nickname); // 호스트 이름(닉네임) 전송

            // 서버 응답 처리
            String response = in.readLine();

            WaitingRoom waitingRoom = new WaitingRoom(nickname, extractPortFromResponse(response), parent);
            // 화면 전환
            parent.setPanel(waitingRoom);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void joinGame(String roomInfo) {
        try {
            fetchRooms();
            // Room ID를 추출
            int roomId = Integer.parseInt(roomInfo.split(",")[0].split(":")[1].trim());

            // 포트 번호 계산 (9500 + Room ID)
            int roomPort = 9500 + roomId;
            for (String room : rooms) {
                System.out.println(room);
                if (Integer.parseInt(room.split(",")[0].split(":")[1].trim()) == roomPort - 9500) {
                    // WaitingRoom 화면으로 전환
                    if (Integer.parseInt(room.split(",")[2].split(":")[1].trim()) <= 3) {
                        parent.setPanel(new WaitingRoom(nickname, roomPort, parent));
                    }
                }
            }


        } catch (Exception e) {
            fetchRooms();
        }
    }

    private int extractPortFromResponse(String response) {
        String[] parts = response.split(", ");
        for (String part : parts) {
            if (part.startsWith("Port=")) {
                return Integer.parseInt(part.substring(5));
            }
        }
        throw new IllegalArgumentException("Invalid response format: " + response);
    }

    @Override
    public void exitRoom() {

    }

    // 배경 패널 클래스
    class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel() {
            background = new ImageIcon(Main.class.getResource("/images/startBackground0.png")).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
