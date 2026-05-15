package SnakeGame2P.controller;

import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;

/**
 * คลาสจัดการการเสริมพลังแยกออกมา
 * @author Unnawas
 */
public class PowerBoostManager {

    /**
     * จัดการการเสริมพลังของงูทั้งสองตัว
     * @param snake1 งูผู้เล่น 1
     * @param snake2 งูผู้เล่น 2
     */
    public void handlePowerBoosts(Snake snake1, Snake snake2) {
        long currentTime = System.currentTimeMillis();

        handleSingleSnakePowerBoost(snake1, currentTime);
        handleSingleSnakePowerBoost(snake2, currentTime);
    }

    /**
     * จัดการการเสริมพลังของงูตัวเดียว
     * @param snake งูที่ต้องการจัดการ
     * @param currentTime เวลาปัจจุบัน
     */
    private void handleSingleSnakePowerBoost(Snake snake, long currentTime) {
        if (snake.isPowerBoostActive()) {
            // ถ้าความยาวเหลือ 3 ให้ยกเลิกการเสริมพลังทันที
            if (snake.getLength() <= 3) {
                snake.deactivatePowerBoost();
            }
            // ถ้ายังเสริมพลังได้และถึงเวลาที่ต้องสั้นลง
            else if (currentTime - snake.getLastShrinkTime() > Constants.SPEED_BOOST_SHRINK_INTERVAL) {
                snake.applyPowerBoostEffect();
                // หลังจากหดแล้ว ถ้าเหลือ 3 ให้ยกเลิกการเสริมพลัง
                if (snake.getLength() <= 3) {
                    snake.deactivatePowerBoost();
                }
            }
        }
    }

    /**
     * เปิดใช้งานการเสริมพลัง (เมื่อกดปุ่ม)
     * @param snake งูที่จะเปิดใช้งาน
     */
    public void activatePowerBoost(Snake snake) {
        if (!snake.isPowerBoostActive() && snake.getLength() > 3) {
            snake.activatePowerBoost();
        }
    }

    /**
     * ปิดใช้งานการเสริมพลัง (เมื่อปล่อยปุ่ม)
     * @param snake งูที่จะปิดใช้งาน
     */
    public void deactivatePowerBoost(Snake snake) {
        snake.deactivatePowerBoost();
    }
}