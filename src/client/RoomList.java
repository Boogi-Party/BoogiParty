package client;
//src/client/RoomList.java

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

public class RoomList extends JPanel {
    private ArrayList<String> rooms = new ArrayList<>();
    private String nickname;
    private JPanel roomListPanel; // 방 목록을 표시할 패널
    private Main parent;

    public RoomList(String nickname, Main parent) {
        this.nickname = nickname;
        this.parent = parent;

        // 메인 레이아웃 설정
        setLayout(new BorderLayout());

        // 왼쪽: 방 목록
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 방 목록 초기화
        fetchRooms();

        // 오른쪽: 방 생성, 방 참가, 닉네임 표시
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(200, getHeight()));

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
        add(rightPanel, BorderLayout.EAST);
    }

    private void fetchRooms() {
        rooms.clear();
        // 서버에서 방 정보 가져오기 (테스트용으로 로컬 데이터 사용)
        try (Socket socket = new Socket("localhost", 9999);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_ROOM");
            String response;
            while ((response = in.readLine()) != null) {
                rooms.add(response);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        // 방 목록 초기화
        updateRoomList();
    }

    private void updateRoomList() {
        roomListPanel.removeAll();

        if (rooms.isEmpty()) {
            JLabel emptyLabel = new JLabel("현재 방이 없습니다.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            roomListPanel.add(emptyLabel);
        } else {
            for (String roomInfo : rooms) {
                JPanel roomPanel = createRoomPanel(roomInfo);
                roomListPanel.add(roomPanel);
            }
        }

        roomListPanel.revalidate();
        roomListPanel.repaint();
    }

    private JPanel createRoomPanel(String roomInfo) {
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.setPreferredSize(new Dimension(600, 50));
        roomPanel.setMaximumSize(new Dimension(600, 50));
        roomPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel roomLabel = new JLabel(roomInfo);
        roomLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roomLabel.setHorizontalAlignment(SwingConstants.LEFT);

        roomPanel.add(roomLabel, BorderLayout.CENTER);

        JButton joinButton = new JButton("Join");
        joinButton.setFont(new Font("Arial", Font.BOLD, 14));
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
            // Room ID를 추출
            int roomId = Integer.parseInt(roomInfo.split(",")[0].split(":")[1].trim());

            // 포트 번호 계산 (9500 + Room ID)
            int roomPort = 9500 + roomId;

            // WaitingRoom 화면으로 전환
            parent.setPanel(new WaitingRoom(nickname, roomPort, parent));

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
}
