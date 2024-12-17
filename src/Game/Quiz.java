package Game;

import client.PlayMusic;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.sound.sampled.Clip;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Quiz extends JFrame {
    private JLabel questionLabel;
    private JTextField answerField;
    private JButton submitButton;
    private Clip clip;
    // 현재 퀴즈 인덱스
    private int currentQuizIndex;   
    
    public Quiz(Player player) {
        // 프레임 초기화
        setTitle("퀴즈 프로그램");
        setSize(400, 250);
        setLocation(900, 520);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLocationRelativeTo(null);
        setVisible(true);
        
      

        // 레이아웃 설정
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // 질문 레이블 추가
        questionLabel = new JLabel();
        questionLabel.setPreferredSize(new Dimension(400, 100));
        add(questionLabel);

        // 정답 입력 필드 추가
        answerField = new JTextField();
        answerField.setPreferredSize(new Dimension(400, 150));
        add(answerField);

        // 제출 버튼 추가
        submitButton = new JButton("제출");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer(player);
            }
        });
        add(submitButton);

        // 초기 퀴즈 설정
        setRandomQuiz();
    }
 
    private void setRandomQuiz() {
        // 랜덤으로 퀴즈 선택
        Random random = new Random();
        currentQuizIndex = random.nextInt(quizList.length);
        questionLabel.setText(quizList[currentQuizIndex]);

        // 정답 입력 필드 초기화
        answerField.setText("");
    }
    
   
    // 퀴즈 리스트 배열
    private String[] quizList = {
    		"한성빌딩과 가장 가까운 역은?",
    		"현재 강의는 객체지향언어2 '몇' 분반이다(대문자)",
    		"객체지향언어2를 가장 잘 가르치는 교수님은 '000' 교수님이다.",
    		"현 한성대 총장은 '000' 총장이다",
    		"[OX퀴즈]한성대는 남아공의 한 대학교와 자매결연을 맺고 있다.",
    		"한성대의 교수는?",
    		"한성대의 교목은?",
    		"상상부기가 가장 좋아하는음식은?",
    		"한성대학교 교가에 등장하는 산은 '00'산이다",
    		"해당 객체지향언어2 수업은 공학관 '000'호에서 진행된다.",
    		"한성대학교에 있는 정자의 이름은?",
    		"객체지향언어2는 전필인가 전선인가?",
    		"객체지향언어2 팀플에 대한 미팅은 'N'차까지 진행되었다",
    		"객체지향언어2 강의의 교재는 '000' 교수님이 제작하셨다",
    		"지금 발표를 하고 있는 팀의 팀명은?",
    		"[OX퀴즈]나는 지금 실행 중인 부루마블 게임이 매우 흡족하다.",
    };



    void checkAnswer(Player player) {
        String userAnswer = answerField.getText();
        String correctAnswer = getCorrectAnswer();

        if (userAnswer.equals(correctAnswer)) {
        	PlayMusic.play_actionSound("src/audio/QuizCorrect.wav");
            JOptionPane.showMessageDialog(this, "정답입니다!", "알림", JOptionPane.INFORMATION_MESSAGE);
           
            player.coin += 10;
        } else {
        	PlayMusic.play_actionSound("src/audio/QuizFail.wav");
            JOptionPane.showMessageDialog(this, "틀렸습니다. 정답은 " + correctAnswer + " 입니다.", "알림", JOptionPane.ERROR_MESSAGE);
            
        }
        dispose(); // 현재 창 닫기
        // 다음 랜덤 퀴즈 설정
        //setRandomQuiz();

        // 점수 업데이트
        //updateScore();
    }
    

   

    private String getCorrectAnswer() {
        // 현재 퀴즈의 정답을 반환
        switch (currentQuizIndex) {
            case 0:
                return "신설동역";
            case 1:
                return "B";
            case 2:
                return "유상미";
            case 3:
                return "이창원";
            case 4:
            	return "O";
            case 5:
            	return "거북";
            case 6:
            	return "삼학송";
            case 7:
            	return "상추";
            case 8:
            	return "북악";
            case 9:
            	return "202";
            case 10:
            	return "의화정";
            case 11:
            	return "전선";
            case 12:
            	return "3";
            case 13:
            	return "황기태";
            case 14:
            	return "똑똑이들";
            case 15:
            	return "O";
            default:
                return "";

        }
    }

	
}
