package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JTextField nicknameField;
    private String nickname;
    Main parent;

    public Menu(Main main) {
        this.parent = main;

        // 프레임 설정
        parent.setScreenNotGameSize();

        // 배경 패널 추가
        setLayout(new BorderLayout());
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        add(backgroundPanel, BorderLayout.CENTER);

        // 레이아웃 설정
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(20, 20, 20, 20);

        // 게임 이름 로고 이미지 추가 (크기 조정)
        ImageIcon originalIcon = new ImageIcon(Main.class.getResource("/images/BoogiParty.png"));
        Image resizedImage = originalIcon.getImage().getScaledInstance(400, 330, Image.SCALE_SMOOTH); // 크기 조정
        JLabel logoLabel = new JLabel(new ImageIcon(resizedImage));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(logoLabel, gbc);

        // 닉네임 입력 필드


        ImageIcon nicknameIcon = new ImageIcon(Main.class.getResource("/images/Nickname.png"));
        Image resizedNicknameImage = nicknameIcon.getImage().getScaledInstance(150, 60, Image.SCALE_SMOOTH); // 크기 조정
        JLabel nicknameLabel = new JLabel(new ImageIcon(resizedNicknameImage)); // 텍스트 대신 이미지 사용

        nicknameField = new JTextField();
        nicknameField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        nicknameField.setPreferredSize(new Dimension(200, 50));
        nicknameField.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255), 2, true));

        // GridBagConstraints 설정
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST; // 입력 필드 왼쪽에 배치
        gbc.insets = new Insets(0, 0, 0, 5); // 오른쪽 여백을 5px로 설정 (nicknameField와의 간격)
        backgroundPanel.add(nicknameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // 입력 필드 오른쪽에 배치
        gbc.insets = new Insets(0, 5, 0, 0); // 왼쪽 여백을 5px로 설정 (nicknameLabel과의 간격)
        backgroundPanel.add(nicknameField, gbc);

        // 버튼
        // Join Game 버튼 대신 이미지 추가
        ImageIcon joinIcon = new ImageIcon(Main.class.getResource("/images/JoinButton.png"));
        Image resizedJoinImage = joinIcon.getImage().getScaledInstance(250, 80, Image.SCALE_SMOOTH); // 크기 조정
        JLabel joinLabel = new JLabel(new ImageIcon(resizedJoinImage)); // 이미지 사용
        joinLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 클릭 가능한 커서 설정
        joinLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                joinGame(); // 클릭 시 joinGame() 메서드 호출
            }
        });

// Join Game 버튼 위치에 이미지 추가
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 0, 40, 0); // 상단 여백 설정
        backgroundPanel.add(joinLabel, gbc);

        setVisible(true);
    }

    private void joinGame() {
        nickname = nicknameField.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a nickname.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        RoomList roomList = new RoomList(nickname, parent);
        parent.setPanel(roomList);
        revalidate();
        repaint();
    }

    // 배경 패널 클래스
    class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel() {
            background = new ImageIcon(Main.class.getResource("/images/startBackground0.png")).getImage();
            if (background == null) {
                System.out.println("Image not loaded: /images/startBackground0.png");
            }
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
