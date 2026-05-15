package SnakeGame2P.view.renderer;

import SnakeGame2P.Main;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Renderer สำหรับหน้าจอขณะเล่น (JavaFX)
 * @author Thakorn
 */
public class PlayingRenderer implements GameRendererStrategy {

    @Override
    public void render(GraphicsContext gc, Snake snake1, Snake snake2, Food food, String additionalData) {
        if (snake1 == null || snake2 == null) return;

        drawGrid(gc);
        drawFood(gc, food);
        drawSnake(gc, snake1);
        drawSnake(gc, snake2);
        drawPauseHint(gc);
        drawGameInfo(gc, snake1, snake2);
    }

    /** วาดเส้นกริด */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Constants.GRID_COLOR); // Constants.GRID_COLOR should be javafx.scene.paint.Color

        for (int i = 0; i < Constants.BOARD_WIDTH / Constants.UNIT_SIZE; i++) {
            gc.strokeLine(i * Constants.UNIT_SIZE, 0, i * Constants.UNIT_SIZE, Constants.BOARD_HEIGHT);
        }

        for (int i = 0; i < Constants.BOARD_HEIGHT / Constants.UNIT_SIZE; i++) {
            gc.strokeLine(0, i * Constants.UNIT_SIZE, Constants.BOARD_WIDTH, i * Constants.UNIT_SIZE);
        }
    }

    /** วาดอาหาร */
    private void drawFood(GraphicsContext gc, Food food) {
        if (food != null) {
            food.draw(gc); // Food.draw() needs GraphicsContext version
        }
    }

    /** วาดงู */
    private void drawSnake(GraphicsContext gc, Snake snake) {
        snake.draw(gc); // Snake.draw() needs GraphicsContext version
    }

    /** วาดข้อมูลเกม */
    private void drawGameInfo(GraphicsContext gc, Snake snake1, Snake snake2) {
        gc.setFill(Color.WHITE);
        gc.setFont(Main.thaiFont);

        String info1 = String.format("ผู้เล่น 1 (เขียว) คะแนน: %d | ความยาว: %d",
                snake1.getFoodEaten(), snake1.getLength());
        String info2 = String.format("ผู้เล่น 2 (ส้ม) คะแนน: %d | ความยาว: %d",
                snake2.getFoodEaten(), snake2.getLength());

        gc.fillText(info1, 220, 30);
        gc.fillText(info2, 220, 60);

        // แสดงสถานะเสริมพลัง
        int yOffset = 90;
        if (snake1.isPowerBoostActive()) {
            gc.setFill(Color.CYAN);
            gc.fillText("⚡", 220, yOffset);
            gc.fillText("ผู้เล่น 1 กำลังเสริมพลัง! (ตัวจะสั้นลงทุก 0.5 วิ)", 245, yOffset);            yOffset += 30;
        }
        if (snake2.isPowerBoostActive()) {
            gc.setFill(Color.PINK);
            gc.fillText("⚡", 220, yOffset);
            gc.fillText("ผู้เล่น 2 กำลังเสริมพลัง! (ตัวจะสั้นลงทุก 0.5 วิ)", 245, yOffset);        }
    }

    /** วาดคำแนะนำการ pause */
    private void drawPauseHint(GraphicsContext gc) {
        gc.setFill(Color.rgb(150, 150, 150));
        gc.setFont(Main.thaiFontSmall);
        gc.fillText("กด ESC เพื่อหยุดเกมชั่วคราว", 20, 30);
    }
}