package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Renderer สำหรับหน้า Pause (JavaFX)
 * @author Thakorn
 */
public class PausedRenderer implements GameRendererStrategy {

    private final PlayingRenderer playingRenderer = new PlayingRenderer();

    /**
     * render menu when paused
     * @param gc GraphicsContext (แทน Graphics)
     * @param snake1 งูผู้เล่น 1 (อาจเป็น null)
     * @param snake2 งูผู้เล่น 2 (อาจเป็น null)
     * @param food อาหาร (อาจเป็น null)
     * @param additionalData ข้อมูลเพิ่มเติม (เช่น ข้อความผู้ชนะ)
     */
    @Override
    public void render(GraphicsContext gc, Snake snake1, Snake snake2, Food food, String additionalData) {
        // วาดเกมพื้นหลังไว้ก่อน
        playingRenderer.render(gc, snake1, snake2, food, additionalData);

        // พื้นหลังโปร่งแสง
        gc.setFill(Constants.OVERLAY_COLOR);
        gc.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // ข้อความ PAUSED
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", 80));
        Text pausedText = new Text(Constants.PAUSED_TEXT);
        pausedText.setFont(gc.getFont());
        double textX = (Constants.BOARD_WIDTH - pausedText.getLayoutBounds().getWidth()) / 2;
        gc.fillText(Constants.PAUSED_TEXT, textX, Constants.BOARD_HEIGHT / 2 - 50);

        // คำแนะนำ Continue
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 30));
        Text continueText = new Text(Constants.CONTINUE_TEXT);
        continueText.setFont(gc.getFont());
        textX = (Constants.BOARD_WIDTH - continueText.getLayoutBounds().getWidth()) / 2;
        gc.fillText(Constants.CONTINUE_TEXT, textX, Constants.BOARD_HEIGHT / 2 + 30);

        // คำแนะนำ Menu
        gc.setFont(Font.font("Arial", 20));
        Text menuText = new Text(Constants.MENU_TEXT);
        menuText.setFont(gc.getFont());
        textX = (Constants.BOARD_WIDTH - menuText.getLayoutBounds().getWidth()) / 2;
        gc.fillText(Constants.MENU_TEXT, textX, Constants.BOARD_HEIGHT / 2 + 70);
    }
}