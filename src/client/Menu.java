package client;
//src/client/Menu.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JPanel {
    private JTextField nicknameField;
    private String nickname;
    Main parent;
    JPanel jpanel;

    public Menu(Main main) {
        // 프레임 설정
        setSize(800, 600);

        this.parent = main;
        //Game 실행 시 스크린 크기 변해서 필요함
        parent.setScreenNotGameSize();

        // 패널 및 레이아웃 설정
        jpanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 게임 이름
        JLabel gameLabel = new JLabel("Marble Game");
        gameLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        jpanel.add(gameLabel, gbc);

        // 닉네임 입력 필드
        JLabel nicknameLabel = new JLabel("NickName:");
        nicknameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nicknameField = new JTextField();
        nicknameField.setFont(new Font("Arial", Font.PLAIN, 20));
        nicknameField.setPreferredSize(new Dimension(200, 40)); // 좌우 폭 조정
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        jpanel.add(nicknameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel.add(nicknameField, gbc);

        // 버튼 패널 (가로 배치)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 가로 배치, 버튼 간 간격 20px

        JButton joinButton = new JButton("Join Game");
        joinButton.setFont(new Font("Arial", Font.BOLD, 24));
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGame();
            }
        });

        buttonPanel.add(joinButton);

        // 버튼 패널 추가
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        jpanel.add(buttonPanel, gbc);

        // 프레임에 패널 추가
        add(jpanel);

        // 화면 표시
        setVisible(true);
    }

    private void joinGame() {
        nickname = nicknameField.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a nickname.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 새 패널 추가
        RoomList roomList = new RoomList(nickname, parent);

        parent.setPanel(roomList);

        // UI 갱신
        revalidate();
        repaint();
    }

}
