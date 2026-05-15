package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;

import javafx.scene.canvas.GraphicsContext;

/**
 * Strategy interface for rendering (JavaFX version)
 * @author Thakorn
 */
public interface GameRendererStrategy {

    /**
     * วาดหน้าจอตามสถานะ
     *
     * @param gc GraphicsContext (แทน Graphics)
     * @param snake1 งูผู้เล่น 1 (อาจเป็น null)
     * @param snake2 งูผู้เล่น 2 (อาจเป็น null)
     * @param food อาหาร (อาจเป็น null)
     * @param additionalData ข้อมูลเพิ่มเติม (เช่น ข้อความผู้ชนะ)
     */
    void render(GraphicsContext gc,
                Snake snake1,
                Snake snake2,
                Food food,
                String additionalData);
}