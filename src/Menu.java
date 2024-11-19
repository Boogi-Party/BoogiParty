import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Menu extends JFrame {

    private JTextField nicknameField;
    private boolean closed = false;
    private String nickname;

    public Menu() {
        // 프레임 설정
        setTitle("Marble Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 패널 및 레이아웃 설정
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 게임 이름
        JLabel gameLabel = new JLabel("Marble Game");
        gameLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(gameLabel, gbc);

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
        panel.add(nicknameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nicknameField, gbc);

        // 버튼 패널 (가로 배치)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 가로 배치, 버튼 간 간격 20px
        JButton createButton = new JButton("Create Game");
        createButton.setFont(new Font("Arial", Font.BOLD, 24));
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGame();
            }
        });

        JButton joinButton = new JButton("Join Game");
        joinButton.setFont(new Font("Arial", Font.BOLD, 24));
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinGame();
            }
        });

        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);

        // 버튼 패널 추가
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        // 프레임에 패널 추가
        add(panel);

        // 화면 표시
        setVisible(true);
    }

    private void createGame() {
        nickname = nicknameField.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a nickname.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 시작 화면 닫기
        dispose();
        closed = true;
    }

    public String waitForClose() {
        while (!closed) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }

    private void joinGame() {
        nickname = nicknameField.getText();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a nickname.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 시작 화면 닫기
        dispose();
        closed = true;
    }

}
