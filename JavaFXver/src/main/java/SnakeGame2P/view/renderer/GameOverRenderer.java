package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Renderer สำหรับหน้า Game Over (JavaFX)
 * @author Thakorn
 */
public class GameOverRenderer implements GameRendererStrategy {
    /**
     * Game renderer
     */
    private final PlayingRenderer playingRenderer = new PlayingRenderer();

    /**
     * render
     * @param gc GraphicsContext (แทน Graphics)
     * @param snake1 งูผู้เล่น 1 (อาจเป็น null)
     * @param snake2 งูผู้เล่น 2 (อาจเป็น null)
     * @param food อาหาร (อาจเป็น null)
     * @param additionalData ข้อมูลเพิ่มเติม (เช่น ข้อความผู้ชนะ)
     */
    @Override
    public void render(GraphicsContext gc, Snake snake1, Snake snake2, Food food, String additionalData) {
        if (snake1 == null || snake2 == null || additionalData == null) return;

        // วาดเกมพื้นหลังไว้ก่อน
        playingRenderer.render(gc, snake1, snake2, food, additionalData);

        // พื้นหลังโปร่งแสง
        gc.setFill(Constants.OVERLAY_COLOR); // Constants.OVERLAY_COLOR now javafx.scene.paint.Color
        gc.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // แสดงผลผู้ชนะ
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40));

        // วัดความกว้างข้อความเพื่อจัดกลาง
        Text winnerText = new Text(additionalData);
        winnerText.setFont(gc.getFont());
        double textWidth = winnerText.getLayoutBounds().getWidth();
        double textX = (Constants.BOARD_WIDTH - textWidth) / 2;

        gc.fillText(additionalData, textX, Constants.BOARD_HEIGHT / 2 - 50);

        // แสดงคะแนน
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 30));
        String score = String.format("ผู้เล่น 1: %d (ยาว %d)  |  ผู้เล่น 2: %d (ยาว %d)",
                snake1.getFoodEaten(), snake1.getLength(),
                snake2.getFoodEaten(), snake2.getLength());

        Text scoreText = new Text(score);
        scoreText.setFont(gc.getFont());
        double scoreWidth = scoreText.getLayoutBounds().getWidth();
        textX = (Constants.BOARD_WIDTH - scoreWidth) / 2;

        gc.fillText(score, textX, Constants.BOARD_HEIGHT / 2 + 20);
    }
}