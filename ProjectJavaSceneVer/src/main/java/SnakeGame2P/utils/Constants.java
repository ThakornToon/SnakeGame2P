package SnakeGame2P.utils;

import java.awt.*;

/**
 * คลาสสำหรับเก็บค่าคงที่ทั้งหมดที่ใช้ในเกม
 */
public class Constants {

    // ========== ขนาดและความเร็วของเกม ==========
    public static final int BOARD_WIDTH = 1000;
    public static final int BOARD_HEIGHT = 700;
    public static final int UNIT_SIZE = 25;
    public static final int GAME_TICK_DELAY = 65;
    public static final int SPEED_BOOST_SHRINK_INTERVAL = 500;

    // ========== การจับเวลาและกระสุน ==========
    public static final int GAME_DURATION_SEC = 180;
    public static final int BULLET_LENGTH_COST = 3;
    public static final int BULLET_SCORE_PENALTY = 2;
    public static final int BULLET_SPEED_MULTIPLIER = 2; // 2 ช่อง (UNIT_SIZE * 2) ต่อติ๊ก
    public static final int BULLET_COOLDOWN_MS = 500; // ดีเลย์การยิงกระสุน 500 มิลลิวินาที

    // ========== สีที่ใช้ในเกม ==========
    public static final Color SNAKE1_HEAD_COLOR = new Color(0, 255, 0);
    public static final Color SNAKE1_BODY_COLOR = new Color(0, 150, 0);
    public static final Color SNAKE1_BOOST_HEAD_COLOR = new Color(0, 255, 255);
    public static final Color SNAKE2_HEAD_COLOR = new Color(255, 200, 0);
    public static final Color SNAKE2_BODY_COLOR = new Color(255, 140, 0);
    public static final Color SNAKE2_BOOST_HEAD_COLOR = new Color(255, 100, 255);
    public static final Color FOOD_COLOR = Color.RED;
    public static final Color GHOST_FOOD_COLOR = new Color(186, 85, 211); // Medium Orchid (Purple/Magenta)
    public static final int GHOST_MODE_DURATION = 5000; // 5 วินาที
    public static final Color SHIELD_FOOD_COLOR = new Color(255, 215, 0); // สีทอง Gold
    public static final Color FREEZE_FOOD_COLOR = new Color(0, 191, 255); // สีฟ้า Deep Sky Blue
    public static final int FREEZE_DURATION = 3000; // แช่แข็ง 3 วินาที
    public static final Color GRID_COLOR = new Color(30, 30, 30);
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 180);

    // ========== ความยาวขั้นต่ำของงู ==========
    public static final int SNAKE_INITIAL_LENGTH = 3;

    // ========== ระบบ Combo, Expiry และ Speed Phase ==========
    /** Food auto-respawns after this many ms if not eaten. */
    public static final long FOOD_EXPIRE_MS = 8000;
    /** Eating within this window (ms) after the previous eat extends the combo. */
    public static final long COMBO_WINDOW_MS = 3000;
    /** Remaining seconds at which the tick delay halves ("sudden death" phase). */
    public static final int FAST_PHASE_THRESHOLD_SEC = 60;

    // ========== ตำแหน่งเริ่มต้นของงู ==========
    public static final int SNAKE1_START_X = 200;
    public static final int SNAKE1_START_Y = 350;
    public static final int SNAKE2_START_X = 800;
    public static final int SNAKE2_START_Y = 350;

    // ========== ข้อความต่างๆ ==========
    public static final String GAME_TITLE = "🐍 เกมงู 2 ผู้เล่น 🐍";
    public static final String PAUSED_TEXT = "⏸ PAUSED";
    public static final String CONTINUE_TEXT = "กด ESC เพื่อเล่นต่อ";
    public static final String MENU_TEXT = "กด R เพื่อกลับไปหน้าเมนู";


    // ========== ไฟล์เสียง ==========
    public static final String SOUND_MENU = "/Start-Menu.wav";
    public static final String SOUND_BACKGROUND = "/Spider-Dance.wav";
    public static final String SOUND_GAME_OVER = "/Home-_Music-Box_.wav";
    public static final String SOUND_EAT = "/apple-bite.wav";
}