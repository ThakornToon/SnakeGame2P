package SnakeGame2P.model.behavior;

import SnakeGame2P.model.entity.Snake;

/**
 * Interface สำหรับวัตถุที่สามารถถูกกินได้
 */
public interface Consumable {
    /**
     * ตรวจสอบว่าถูกงูกินหรือไม่
     * snake งูที่ต้องการตรวจสอบ
     * return true ถ้าถูกกิน
     */
    boolean isEatenBy(Snake snake);

    /**
     * เอฟเฟกต์เมื่อถูกกิน
     * eater งูที่กิน
     * opponent งูฝ่ายตรงข้าม
     */
    void onConsumed(Snake eater, Snake opponent);
}