package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Renderer สำหรับหน้าจอขณะเล่น
 */
public class PlayingRenderer implements GameRendererStrategy {

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        if (snake1 == null || snake2 == null) return;

        drawGrid(g);
        drawFood(g, food);
        drawSnake(g, snake1);
        drawSnake(g, snake2);
        drawGameInfo(g, snake1, snake2);
        drawPauseHint(g);
    }

    /**
     * วาดเส้นกริด
     */
    private void drawGrid(Graphics g) {
        g.setColor(Constants.GRID_COLOR);

        for (int i = 0; i < Constants.BOARD_WIDTH / Constants.UNIT_SIZE; i++) {
            g.drawLine(i * Constants.UNIT_SIZE, 0, i * Constants.UNIT_SIZE, Constants.BOARD_HEIGHT);
        }

        for (int i = 0; i < Constants.BOARD_HEIGHT / Constants.UNIT_SIZE; i++) {
            g.drawLine(0, i * Constants.UNIT_SIZE, Constants.BOARD_WIDTH, i * Constants.UNIT_SIZE);
        }
    }

    /**
     * วาดอาหาร
     */
    private void drawFood(Graphics g, Food food) {
        if (food != null) {
            food.draw(g);
        }
    }

    /**
     * วาดงู
     */
    private void drawSnake(Graphics g, Snake snake) {
        snake.draw(g);
    }

    /**
     * วาดข้อมูลเกม
     */
    private void drawGameInfo(Graphics g, Snake snake1, Snake snake2) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        String info1 = String.format("ผู้เล่น 1 (เขียว) คะแนน: %d | ความยาว: %d",
                snake1.getFoodEaten(), snake1.getLength());
        String info2 = String.format("ผู้เล่น 2 (ส้ม) คะแนน: %d | ความยาว: %d",
                snake2.getFoodEaten(), snake2.getLength());

        g.drawString(info1, 220, 30);
        g.drawString(info2, 220, 60);

        // แสดงสถานะเสริมพลัง
        int yOffset = 90;
        if (snake1.isPowerBoostActive()) {
            g.setColor(Color.CYAN);
            g.drawString("⚡ ผู้เล่น 1 กำลังเสริมพลัง! (ตัวจะสั้นลงทุก 0.5 วิ)", 220, yOffset);
            yOffset += 30;
        }
        if (snake2.isPowerBoostActive()) {
            g.setColor(Color.PINK);
            g.drawString("⚡ ผู้เล่น 2 กำลังเสริมพลัง! (ตัวจะสั้นลงทุก 0.5 วิ)", 220, yOffset);
        }
    }

    /**
     * วาดคำแนะนำการ pause
     */
    private void drawPauseHint(Graphics g) {
        g.setColor(new Color(150, 150, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("กด ESC เพื่อหยุดเกมชั่วคราว", 20, 30);
    }
}