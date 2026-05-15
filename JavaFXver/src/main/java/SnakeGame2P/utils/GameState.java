package SnakeGame2P.utils;

/**
 * Enum สำหรับบอกสถานะของเกม
 * ใช้ควบคุมการทำงานและการแสดงผล
 * @author Thakorn
 */
public enum GameState {
    /** อยู่ในหน้าเมนูหลัก */
    MENU,

    /** กำลังเล่นเกมอยู่ */
    PLAYING,

    /** หยุดเกมชั่วคราว */
    PAUSED,

    /** เกมจบแล้ว (มีผู้ชนะ) */
    GAME_OVER
}