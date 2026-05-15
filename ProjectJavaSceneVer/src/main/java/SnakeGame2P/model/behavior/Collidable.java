package SnakeGame2P.model.behavior;

import java.awt.*;

/**
 * Interface สำหรับวัตถุที่สามารถชนกันได้
 */
public interface Collidable {
    /**
     * ตรวจสอบว่าชนกับวัตถุอื่นหรือไม่
     * other วัตถุอื่น
     * return true ถ้าชนกัน
     */
    boolean isCollidingWith(Collidable other);

    /**
     * ดึงตำแหน่งของวัตถุ
     * return Point ตำแหน่ง
     */
    Point getPosition();
}