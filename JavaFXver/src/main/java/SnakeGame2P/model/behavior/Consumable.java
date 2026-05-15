package SnakeGame2P.model.behavior;

import SnakeGame2P.model.entity.Snake;

/**
 * Interface สำหรับวัตถุที่สามารถถูกกินได้
 * @author Unnawas
 */
public interface Consumable {
    /**
     * ตรวจสอบว่าถูกงูกินหรือไม่
     * @param snake งูที่ต้องการตรวจสอบ
     * return true ถ้าถูกกิน
     */
    boolean isEatenBy(Snake snake);

    /**
     * เอฟเฟกต์เมื่อถูกกิน เช่น งูยาวขึ้น
     * @param snake งูที่กิน
     */
    void onConsumed(Snake snake);
}