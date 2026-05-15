package SnakeGame2P.view.renderer;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import java.awt.*;

/**
 * Interface สำหรับ Strategy ในการวาดหน้าจอต่างๆ
 * ใช้หลักการ Polymorphism ผ่าน Interface และ Strategy Pattern
 */
public interface GameRendererStrategy {
    /**
     * วาดหน้าจอตามสถานะ
     * g Graphics object
     * snake1 งูผู้เล่น 1 (อาจเป็น null)
     * snake2 งูผู้เล่น 2 (อาจเป็น null)
     * food อาหาร (อาจเป็น null)
     * additionalData ข้อมูลเพิ่มเติม (เช่น ข้อความผู้ชนะ)
     */
    void render(Graphics g, Snake snake1, Snake snake2, Food food, String additionalData);
}