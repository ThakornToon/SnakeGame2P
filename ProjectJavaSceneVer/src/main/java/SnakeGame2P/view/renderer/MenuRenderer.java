package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Renderer สำหรับหน้าเมนู
 */
public class MenuRenderer implements GameRendererStrategy {

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        // พื้นหลังสีดำ
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // วาดชื่อเกม
        g.setColor(new Color(50, 200, 50));
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics metrics = g.getFontMetrics(); // วัดขนาดของข้อความ
        int titleX = (Constants.BOARD_WIDTH - metrics.stringWidth(Constants.GAME_TITLE)) / 2;
        g.drawString(Constants.GAME_TITLE, titleX, 150); // วางตรงกลางพอดี

        // วาดงูตัวอย่าง
        drawExampleSnakes(g);

        // วาดอาหารตัวอย่าง
        g.setColor(Constants.FOOD_COLOR);
        g.fillOval(500, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);

        // แสดงคำแนะนำการเล่น
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("ผู้เล่น 1: WASD + SHIFT (เสริมพลัง)", 350, 600);
        g.drawString("ผู้เล่น 2: ลูกศร + M (เสริมพลัง)", 350, 630);

        // แสดงชื่อเพลง
        g.setColor(new Color(100, 100, 100));
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.drawString("เพลง: Start-Menu.wav (เมนู), Spider-Dance.wav (เล่น), Home-Music-Box.wav (จบเกม)", 300, 680);
    }

    /**
     * วาดงูตัวอย่าง
     */
    private void drawExampleSnakes(Graphics g) {
        // งูเขียว
        g.setColor(Constants.SNAKE1_BODY_COLOR);
        g.fillRect(300, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        g.fillRect(325, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        g.fillRect(350, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);

        // งูส้ม
        g.setColor(Constants.SNAKE2_BODY_COLOR);
        g.fillRect(650, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        g.fillRect(625, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        g.fillRect(600, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
    }
}