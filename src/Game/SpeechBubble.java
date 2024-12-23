package Game;

import javax.swing.*;
import java.awt.*;

// 말풍선 커스텀 패널
class SpeechBubble extends JPanel {
    private String text;

    public SpeechBubble(String text) {
        this.text = text;
        setOpaque(true); // 배경을 불투명하게 설정
//        this.setBackground(new Color(192,205,239));
        this.setBackground(new Color(128,198,207));
    }

    public void setText(String text) {
        this.text = text;
        repaint(); // 텍스트 변경 시 다시 그리기
    }
    @Override
    protected void paintComponent(Graphics g) {
        // 배경 초기화
        super.paintComponent(g); // 부모 컴포넌트 배경 정리

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. 말풍선 몸체
        g2d.setColor(new Color(255, 255, 204)); // 밝은 노란색
        g2d.fillRoundRect(0, 0, width - 20, height - 20, 20, 20);

        // 2. 말풍선 꼬리
        int tailX = width - 30;
        int tailY = height - 20;
        int[] xPoints = {tailX, tailX + 10, tailX + 20};
        int[] yPoints = {tailY, tailY + 10, tailY};
        g2d.fillPolygon(xPoints, yPoints, 3);

        // 3. 경계선
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, width - 20, height - 20, 20, 20);
        g2d.drawPolygon(xPoints, yPoints, 3);

        // 4. 텍스트
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Nanum Gothic", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (width - 20 - fm.stringWidth(text)) / 2;
        int textY = (height - 20 - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

}
