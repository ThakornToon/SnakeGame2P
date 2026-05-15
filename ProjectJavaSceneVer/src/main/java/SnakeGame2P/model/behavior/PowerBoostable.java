package SnakeGame2P.model.behavior;

/**
 * Interface สำหรับวัตถุที่สามารถเสริมพลังได้
 */
public interface PowerBoostable {
    /**
     * เปิดใช้งานโหมดเสริมพลัง
     */
    void activatePowerBoost();

    /**
     * ปิดใช้งานโหมดเสริมพลัง
     */
    void deactivatePowerBoost();

    /**
     * ตรวจสอบว่าอยู่ในโหมดเสริมพลังหรือไม่
     *
     * return true ถ้าอยู่ในโหมดเสริมพลัง
     */
    boolean isPowerBoostActive();

    /**
     * ใช้เอฟเฟกต์เสริมพลัง (ตัวสั้นลง)
     */
    void applyPowerBoostEffect();
}
