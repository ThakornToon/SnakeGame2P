package SnakeGame2P.utils;

/**
 * Enum สำหรับบอกทิศทางการเคลื่อนที่ของงู
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE;

    Direction() {

    }

    /**
     * ตรวจสอบว่าเป็นทิศทางตรงกันข้ามหรือไม่
     * other ทิศทางที่ต้องการเปรียบเทียบ
     * return true ถ้าเป็นทิศตรงข้าม
     */
    public boolean isOpposite(Direction other) {
        return (this == UP && other == DOWN) ||
                (this == DOWN && other == UP) ||
                (this == LEFT && other == RIGHT) ||
                (this == RIGHT && other == LEFT);
    }
}