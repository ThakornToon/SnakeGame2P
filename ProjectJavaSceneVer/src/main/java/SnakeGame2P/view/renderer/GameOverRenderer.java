package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import java.awt.*;

/**
 * Renderer สำหรับหน้า Game Over
 */
public class GameOverRenderer implements GameRendererStrategy {
    private final PlayingRenderer playingRenderer = new PlayingRenderer();

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        if (snake1 == null || snake2 == null || additionalData == null) return;

        // วาดเกมพื้นหลังไว้ก่อน
        playingRenderer.render(g, snake1, snake2, food, additionalData);

        // พื้นหลังโปร่งแสง
        g.setColor(Constants.OVERLAY_COLOR);
        g.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // แสดงผลผู้ชนะ
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        // วางตรงกลาง
        FontMetrics metrics = g.getFontMetrics();
        int textX = (Constants.BOARD_WIDTH - metrics.stringWidth(additionalData)) / 2;
        g.drawString(additionalData, textX, Constants.BOARD_HEIGHT / 2 - 50);

        // แสดงคะแนน
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String score = String.format("ผู้เล่น 1: %d (ยาว %d)  |  ผู้เล่น 2: %d (ยาว %d)",
                snake1.getFoodEaten(), snake1.getLength(),
                snake2.getFoodEaten(), snake2.getLength());
        // วางตรงกลาง
        metrics = g.getFontMetrics();
        textX = (Constants.BOARD_WIDTH - metrics.stringWidth(score)) / 2;
        g.drawString(score, textX, Constants.BOARD_HEIGHT / 2 + 20);
    }
}