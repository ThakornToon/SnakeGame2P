package SnakeGame2P.model.behavior;

import SnakeGame2P.utils.Direction;

/**
 * Interface สำหรับวัตถุที่สามารถเคลื่อนที่ได้
 * @author Unnawas
 */
public interface Movable {
    /**
     * เคลื่อนที่ไปข้างหน้า
     */
    void move();

    /**
     * ตั้งค่าทิศทางการเคลื่อนที่
     * direction ทิศทางใหม่
     */
    void setDirection(Direction direction);

    /**
     * ดึงทิศทางปัจจุบัน
     * return Direction ปัจจุบัน
     */
    Direction getDirection();
}