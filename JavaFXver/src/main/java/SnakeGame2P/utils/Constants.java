package SnakeGame2P.utils;

import javafx.scene.paint.Color;

/**
 * @author Thakorn
 */
public class Constants {

    // ========== ขนาดและความเร็วของเกม ==========
    public static final int BOARD_WIDTH = 1000;
    public static final int BOARD_HEIGHT = 700;
    public static final int UNIT_SIZE = 25;
    public static final int NORMAL_SPEED_DELAY = 130;
    public static final int SPEED_BOOST_SHRINK_INTERVAL = 500;


    // ========== สีที่ใช้ในเกม ==========
    public static final Color SNAKE1_HEAD_COLOR = Color.rgb(0, 255, 0);
    public static final Color SNAKE1_BODY_COLOR = Color.rgb(0, 150, 0);
    public static final Color SNAKE1_BOOST_HEAD_COLOR = Color.rgb(0, 255, 255);
    public static final Color SNAKE2_HEAD_COLOR = Color.rgb(255, 200, 0);
    public static final Color SNAKE2_BODY_COLOR = Color.rgb(255, 140, 0);
    public static final Color SNAKE2_BOOST_HEAD_COLOR = Color.rgb(255, 100, 255);
    public static final Color FOOD_COLOR = Color.rgb(255, 0, 0);
    public static final Color GRID_COLOR = Color.rgb(30, 30, 30);
    public static final Color OVERLAY_COLOR = Color.rgb(0, 0, 0, 0.7);


    // ========== ตำแหน่งเริ่มต้นของงู ==========
    public static final int SNAKE1_START_X = 200;
    public static final int SNAKE1_START_Y = 350;
    public static final int SNAKE2_START_X = 800;
    public static final int SNAKE2_START_Y = 350;


    // ========== ข้อความต่างๆ ==========
    public static final String GAME_TITLE = "🐍 Snake Game 2P 🐍";
    public static final String PAUSED_TEXT = "⏸ PAUSED";
    public static final String CONTINUE_TEXT = "ESC to Continue";
    public static final String MENU_TEXT = "R to return to menu";


    // ========== ไฟล์เสียง ==========
    public static final String SOUND_MENU = "/Start-Menu.wav";
    public static final String SOUND_BACKGROUND = "/Spider-Dance.wav";
    public static final String SOUND_GAME_OVER = "/Home-_Music-Box_.wav";
    public static final String SOUND_EAT = "/apple-bite.wav";
}