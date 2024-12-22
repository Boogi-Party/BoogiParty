package Game;
//src/Game/Quiz.java

import client.ClientThread;
import client.PlayMusic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.swing.*;

public class Quiz extends JFrame {
    private JLabel questionLabel;
    private JTextField answerField;
    private JButton submitButton;
    private Clip clip;
    // 현재 퀴즈 인덱스
    private int currentQuizIndex;
    ClientThread clientThread;
    Player player;

    public Quiz(Player player, JFrame parentFrame, boolean isPlayer, ClientThread clientThread, String question) {
        this.clientThread = clientThread;
        this.player = player;
        // 프레임 초기화
        setTitle("퀴즈 프로그램");
        setSize(400, 250);
        setVisible(true);

        if (parentFrame != null) {
            int parentX = parentFrame.getX();
            int parentY = parentFrame.getY();
            int parentWidth = parentFrame.getWidth();
            int parentHeight = parentFrame.getHeight();

            int x = parentX + (parentWidth - getWidth()) / 2;
            int y = parentY + (parentHeight - getHeight()) / 2;
            setLocation(x, y);
        }

        // 레이아웃 설정
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // 질문 레이블 추가
        questionLabel = new JLabel();
        questionLabel.setPreferredSize(new Dimension(400, 100));
        add(questionLabel);

        // 정답 입력 필드 추가
        answerField = new JTextField();
        answerField.setPreferredSize(new Dimension(400, 150));

        submitButton = new JButton("제출");
        // **엔터키 입력 처리 추가**
            answerField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 제출 버튼과 동일한 동작 수행
                    if (isPlayer && !answerField.getText().isEmpty()) {
                        clientThread.sendMessage("ANSWER/" + answerField.getText());
                    }
                    answerField.setText("");
                }
            });

            // 제출 버튼 추가

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isPlayer && !answerField.getText().isEmpty()) {
                        clientThread.sendMessage("ANSWER/" + answerField.getText());
                    }
                    answerField.setText("");
                }
            });

        add(answerField);
        add(submitButton);
        questionLabel.setText(question);

        // 정답 입력 필드 초기화
        answerField.setText("");
    }

    public void end(String msg) {
        String [] parts = msg.split("/");
        String message = "";
        if (parts[0].equals("CORRECT")) {
            PlayMusic.play_actionSound("src/audio/QuizCorrect.wav");
            message = "정답입니다! 정답 : " + parts[1];
            player.setCoin(player.getCoin() + 50);
        }
        else {
            message = "틀렸습니다... 정답 : " + parts[1] + "입력 :" + parts[2];
            PlayMusic.play_actionSound("src/audio/QuizFail.wav");
        }

        JDialog dialog = new JDialog(this, "게임 결과", false);
        dialog.setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        dialog.add(label, BorderLayout.CENTER);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 기본 닫기 설정
        dialog.setVisible(true);

        // 일정 시간 후에 다이얼로그와 프레임 종료
        Timer timer = new Timer(2000, e -> {
            dialog.dispose(); // 다이얼로그 닫기
            dispose(); // JFrame 종료
        });
        timer.setRepeats(false); // 타이머 반복 방지
        timer.start();
    }
}
