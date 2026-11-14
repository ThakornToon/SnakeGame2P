package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import java.awt.*;

/**
 * Renderer สำหรับหน้า Pause
 */
public class PausedRenderer implements GameRendererStrategy {
    private final PlayingRenderer playingRenderer;

    public PausedRenderer(GamePanel panel) {
        this.playingRenderer = new PlayingRenderer(panel);
    }

    @Override
    public void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData) {
        // วาดเกมพื้นหลังไว้ก่อน
        playingRenderer.render(g, snake1, snake2, food, additionalData);

        // พื้นหลังโปร่งแสง
        g.setColor(Constants.OVERLAY_COLOR);
        g.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // ข้อความ PAUSED
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 80));
        // วางข้อความตรงกลาง
        FontMetrics metrics = g.getFontMetrics();
        int textX = (Constants.BOARD_WIDTH - metrics.stringWidth(Constants.PAUSED_TEXT)) / 2;
        g.drawString(Constants.PAUSED_TEXT, textX, Constants.BOARD_HEIGHT / 2 - 50);

        // คำแนะนำ
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        metrics = g.getFontMetrics();
        textX = (Constants.BOARD_WIDTH - metrics.stringWidth(Constants.CONTINUE_TEXT)) / 2;
        g.drawString(Constants.CONTINUE_TEXT, textX, Constants.BOARD_HEIGHT / 2 + 30);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        metrics = g.getFontMetrics();
        textX = (Constants.BOARD_WIDTH - metrics.stringWidth(Constants.MENU_TEXT)) / 2;
        g.drawString(Constants.MENU_TEXT, textX, Constants.BOARD_HEIGHT / 2 + 70);
    }
}