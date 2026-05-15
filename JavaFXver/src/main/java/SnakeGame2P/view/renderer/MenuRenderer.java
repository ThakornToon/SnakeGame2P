package SnakeGame2P.view.renderer;

import SnakeGame2P.Main;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Renderer สำหรับหน้าเมนู (JavaFX)
 * @author Thakorn
 */
public class MenuRenderer implements GameRendererStrategy {

    /**
     * render the menu
     * @param gc GraphicsContext (แทน Graphics)
     * @param snake1 งูผู้เล่น 1 (อาจเป็น null)
     * @param snake2 งูผู้เล่น 2 (อาจเป็น null)
     * @param food อาหาร (อาจเป็น null)
     * @param additionalData ข้อมูลเพิ่มเติม (เช่น ข้อความผู้ชนะ)
     */
    @Override
    public void render(GraphicsContext gc, Snake snake1, Snake snake2, Food food, String additionalData) {
        // พื้นหลังสีดำ
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);

        // วาดชื่อเกม
        gc.setFill(Color.rgb(50, 200, 50));
        gc.setFont(Main.thaiFontLarge);

        Text titleText = new Text(Constants.GAME_TITLE);
        titleText.setFont(gc.getFont());
        double titleWidth = titleText.getLayoutBounds().getWidth();
        double titleX = (Constants.BOARD_WIDTH - titleWidth) / 2;
        gc.fillText(Constants.GAME_TITLE, titleX, 150);

        // วาดงูตัวอย่าง
        drawExampleSnakes(gc);

        // วาดอาหารตัวอย่าง
        gc.setFill(Constants.FOOD_COLOR);
        gc.fillOval(500, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);

        // แสดงคำแนะนำการเล่น
        gc.setFill(Color.WHITE);
        gc.setFont(Main.thaiFont);
        gc.fillText("ผู้เล่น 1: WASD + SHIFT (เสริมพลัง)", 350, 600);
        gc.fillText("ผู้เล่น 2: ลูกศร + M (เสริมพลัง)", 350, 630);

        // แสดงชื่อเพลง
        gc.setFill(Color.rgb(100, 100, 100));
        gc.setFont(Main.thaiFontSmall);
        gc.fillText("เพลง: Start-Menu.wav (เมนู), Spider-Dance.wav (เล่น), Home-Music-Box.wav (จบเกม)", 300, 680);
    }

    /** วาดงูตัวอย่าง */
    private void drawExampleSnakes(GraphicsContext gc) {
        // งูเขียว
        gc.setFill(Constants.SNAKE1_BODY_COLOR);
        gc.fillRect(300, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        gc.fillRect(325, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        gc.fillRect(350, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);

        // งูส้ม
        gc.setFill(Constants.SNAKE2_BODY_COLOR);
        gc.fillRect(650, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        gc.fillRect(625, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
        gc.fillRect(600, 450, Constants.UNIT_SIZE, Constants.UNIT_SIZE);
    }
}