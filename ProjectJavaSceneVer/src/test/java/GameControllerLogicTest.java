import SnakeGame2P.controller.GameController;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Logic สำคัญใน GameController
 *
 * เหตุผล:
 * - ต้องมั่นใจว่าการตัดสินผู้ชนะ การเปลี่ยนสถานะ และการทำงานต่างๆ ถูกต้อง
 */
public class GameControllerLogicTest {

    /** ตัวควบคุมเกมจริง */
    private GameController controller;

    /** GamePanel ที่ปรับให้ทดสอบได้ (ไม่โหลด UI จริง) */
    private TestableGamePanel testPanel;

    /** AudioManager ที่ปรับให้ทดสอบได้ (ไม่เล่นเสียงจริง) */
    private TestableAudioManager testAudioManager;

    /** ตัวช่วยเข้าถึง private members ของ GameController */
    private GameControllerAccessor accessor;

    /**
     * ตั้งค่าสภาพแวดล้อมก่อนทดสอบแต่ละ method
     *
     * 1. สร้าง TestableAudioManager (ไม่เล่นเสียงจริง)
     * 2. สร้าง TestableGamePanel (ไม่แสดง GUI จริง)
     * 3. สร้าง GameController จริง (เพื่อทดสอบ logic)
     * 4. สร้าง Accessor สำหรับเข้าถึง private members
     */
    @BeforeEach
    void setUp() {
        testAudioManager = new TestableAudioManager();
        testPanel = new TestableGamePanel(testAudioManager);
        controller = new GameController(testPanel, testAudioManager);
        accessor = new GameControllerAccessor(controller);
    }

    // ========== Test Methods - ทดสอบการตัดสินผู้ชนะ ==========

    /**
     * ทดสอบกรณีหัวชนหัว: ผู้เล่น 1 เสริมพลัง ควรชนะ
     */
    @Test
    void testDetermineHeadOnHeadWinner_Snake1PowerBoost() {
        // Arrange - เตรียมข้อมูล
        controller.startGame();
        Snake snake1 = accessor.getSnake1();
        Snake snake2 = accessor.getSnake2();

        snake1.grow(); // ทำให้งูยาวพอ (length > 3)
        snake1.activatePowerBoost();
        snake2.setPosition(snake1.getHead().x, snake1.getHead().y);

        // เรียก method ที่ต้องการทดสอบ
        String winner = accessor.callDetermineHeadOnHeadWinner();

        assertTrue(winner.contains("ผู้เล่น 1") && winner.contains("เสริมพลัง"),
                "ผู้เล่น 1 ที่เสริมพลังควรชนะ แต่ได้: " + winner);
    }

    /**
     * ทดสอบกรณีหัวชนหัว: ผู้เล่น 2 เสริมพลัง ควรชนะ
     */
    @Test
    void testDetermineHeadOnHeadWinner_Snake2PowerBoost() {
        controller.startGame();
        Snake snake2 = accessor.getSnake2();

        snake2.grow();
        snake2.activatePowerBoost();

        String winner = accessor.callDetermineHeadOnHeadWinner();

        assertTrue(winner.contains("ผู้เล่น 2") && winner.contains("เสริมพลัง"),
                "ผู้เล่น 2 ที่เสริมพลังควรชนะ แต่ได้: " + winner);
    }

    /**
     * ทดสอบกรณีหัวชนหัว: ทั้งคู่ไม่มี/มีเสริมพลัง ตัวใหญ่กว่าชนะ
     *
     * ทดสอบ 2 สถานการณ์:
     * 1. ผู้เล่น 1 ตัวใหญ่กว่า → ควรชนะ
     * 2. ผู้เล่น 2 ตัวใหญ่กว่า → ควรชนะ
     */
    @Test
    void testDetermineHeadOnHeadWinner_BothPowerBoostOrNone_LargerWins() {
        controller.startGame();
        Snake snake1 = accessor.getSnake1();
        Snake snake2 = accessor.getSnake2();

        // กรณีผู้เล่น 1 ตัวใหญ่กว่า
        for (int i = 0; i < 5; i++) snake1.grow();
        String winner = accessor.callDetermineHeadOnHeadWinner();
        assertTrue(winner.contains("ผู้เล่น 1") && winner.contains("ตัวใหญ่กว่า"),
                "ผู้เล่น 1 ที่ตัวใหญ่กว่าควรชนะ แต่ได้: " + winner);

        // กรณีผู้เล่น 2 ตัวใหญ่กว่า
        for (int i = 0; i < 10; i++) snake2.grow();
        winner = accessor.callDetermineHeadOnHeadWinner();
        assertTrue(winner.contains("ผู้เล่น 2") && winner.contains("ตัวใหญ่กว่า"),
                "ผู้เล่น 2 ที่ตัวใหญ่กว่าควรชนะ แต่ได้: " + winner);
    }

    /**
     * ทดสอบกรณีหัวชนหัว: ตัวเท่ากัน ควรเสมอ
     */
    @Test
    void testDetermineHeadOnHeadWinner_Tie() {
        controller.startGame();
        String winner = accessor.callDetermineHeadOnHeadWinner();
        assertTrue(winner.contains("เสมอ"),
                "เมื่อตัวเท่ากันควรเสมอ แต่ได้: " + winner);
    }

    // ========== Test Methods - ทดสอบสถานะเกม ==========

    /**
     * ทดสอบการจบเกม: ควรเปลี่ยนสถานะและเล่นเสียง
     *
     * ตรวจสอบ:
     * - สถานะเปลี่ยนเป็น GAME_OVER
     * - เล่นเสียง game over
     * - ปุ่ม restart แสดง
     */
    @Test
    void testGameOverTrigger() {
        controller.startGame();
        assertEquals(GameState.PLAYING, accessor.getState());

        accessor.callGameOver();

        assertEquals(GameState.GAME_OVER, accessor.getState());
        assertTrue(testAudioManager.wasSoundPlayed(Constants.SOUND_GAME_OVER));
        assertTrue(testPanel.wasRestartButtonVisible());
    }

    /**
     * ทดสอบการหยุดเกมชั่วคราว: กด pause ควรสลับสถานะ
     *
     * ตรวจสอบ: PLAYING → PAUSED → PLAYING
     */
    @Test
    void testPauseFunctionality() {
        controller.startGame();
        assertEquals(GameState.PLAYING, accessor.getState());

        controller.togglePause();
        assertEquals(GameState.PAUSED, accessor.getState());

        testAudioManager.toggleMute();
        assertTrue(testAudioManager.isMuted());

        controller.togglePause();
        assertEquals(GameState.PLAYING, accessor.getState());
    }

    /**
     * ทดสอบการกลับหน้าเมนู: ควรเปลี่ยนสถานะและเล่นเพลงเมนู
     */
    @Test
    void testReturnToMenu() {
        controller.startGame();
        controller.togglePause();
        controller.returnToMenu();

        // ควรเห็นปุ่มเมนู
        assertTrue(testPanel.areMenuButtonsVisible());

        assertEquals(GameState.MENU, accessor.getState());
        assertTrue(testAudioManager.wasBackgroundMusicPlayed(Constants.SOUND_MENU));
    }

    // ========== Test Methods - ทดสอบอาหาร ==========

    /**
     * ทดสอบการเกิดอาหาร: ต้องไม่อยู่บนตัวงู
     *
     * ทดสอบโดยการสุ่มอาหาร 20 ครั้ง
     * ตรวจสอบ: ทุกครั้งที่สุ่ม อาหารต้องไม่ซ้อนทับงู
     */
    @Test
    void testFoodGeneration_NotOnSnake() {
        controller.startGame();

        Snake snake1 = accessor.getSnake1();
        Snake snake2 = accessor.getSnake2();

        for (int i = 0; i < 20; i++) {
            accessor.callGenerateNewFood();
            Food food = accessor.getFood();

            boolean occupied = isFoodOnSnake(food, snake1, snake2);
            assertFalse(occupied, "อาหารไม่ควรเกิดบนตัวงู");
        }
    }

    // ========== Helper Methods ==========

    /**
     * ตรวจสอบว่าอาหารซ้อนทับกับงูหรือไม่
     *
     * food อาหารที่ต้องการตรวจสอบ
     * snakes รายการงูที่ต้องการตรวจสอบ (ส่งกี่ตัวก็ได้)
     * return true ถ้าอาหารซ้อนทับกับงูตัวใดตัวหนึ่ง
     */
    private boolean isFoodOnSnake(Food food, Snake... snakes) {
        for (Snake snake : snakes) {
            for (int j = 0; j < snake.getLength(); j++) {
                if (food.getX() == snake.getXPositions().get(j) &&
                        food.getY() == snake.getYPositions().get(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ========== Testable Components ==========

    /**
     * Testable GamePanel - version ที่ปรับให้ทดสอบได้
     *
     * จำลองเฉพาะส่วนที่จำเป็นสำหรับ test
     * - เก็บสถานะการแสดงผลปุ่ม
     * - ไม่ต้องแสดง GUI จริง
     */
    private static class TestableGamePanel extends GamePanel {
        /** สถานะการแสดงปุ่ม restart */
        private boolean restartButtonVisible = false;

        /** สถานะการแสดงปุ่มเมนู */
        private boolean menuButtonsVisible = true;

        /** สถานะปิดเสียง (สำหรับตรวจสอบ) */
        private boolean muted = false;

        public TestableGamePanel(AudioManager audioManager) {
            super(audioManager);
            // ไม่ต้องสร้างปุ่มจริง เพราะเราแค่ต้องการเก็บสถานะ
        }

        @Override
        public void setRestartButtonVisible(boolean visible) {
            this.restartButtonVisible = visible;
        }

        @Override
        public void setMenuButtonsVisible(boolean visible) {
            this.menuButtonsVisible = visible;
        }

        @Override
        public void updateMuteButton(boolean muted) {
            this.muted = muted;
        }

        /** ตรวจสอบว่าปุ่ม restart เคยถูกแสดงหรือไม่ */
        public boolean wasRestartButtonVisible() {
            return restartButtonVisible;
        }

        /** ตรวจสอบว่าปุ่มเมนูกำลังแสดงอยู่หรือไม่ */
        public boolean areMenuButtonsVisible() {
            return menuButtonsVisible;
        }

        @Override
        public boolean requestFocusInWindow() {
            return true; // ไม่ต้อง focus จริง
        }
    }

    /**
     * Testable AudioManager - version ที่ไม่เล่นเสียงจริง
     *
     * แทนที่จะโหลดและเล่นไฟล์เสียงจริง
     * เราแค่บันทึกว่า method ไหนถูกเรียก ด้วยไฟล์อะไร
     * เพื่อใช้ตรวจสอบใน test
     */
    private static class TestableAudioManager extends AudioManager {
        /** สถานะปิดเสียง */
        private boolean muted = false;

        /** ไฟล์เสียงล่าสุดที่ถูกขอให้เล่น */
        private String lastPlayedSound = null;
        private String lastPlayedBackground = null;

        public TestableAudioManager() {
            // ไม่โหลดเสียงจริง
        }

        @Override
        protected void loadSounds() {
            // ไม่โหลดอะไรเลย เพื่อป้องกัน error "Audio data is too big"
        }

        @Override
        public void playSound(String path) {
            this.lastPlayedSound = path;
        }

        @Override
        public void playBackgroundMusic(String path) {
            this.lastPlayedBackground = path;
        }

        @Override
        public void stopAllMusic() {
            // ไม่ต้องทำอะไร เพราะไม่ได้เล่นจริง
        }

        @Override
        public void toggleMute() {
            muted = !muted;
        }

        @Override
        public boolean isMuted() {
            return muted;
        }

        /** ตรวจสอบว่าเคยเรียก playSound ด้วย path นี้หรือไม่ */
        public boolean wasSoundPlayed(String path) {
            return path.equals(lastPlayedSound);
        }

        /** ตรวจสอบว่าเคยเรียก playBackgroundMusic ด้วย path นี้หรือไม่ */
        public boolean wasBackgroundMusicPlayed(String path) {
            return path.equals(lastPlayedBackground);
        }
    }

    /**
     * GameController Accessor - ตัวช่วยเข้าถึง private members
     *
     * ใช้ Reflection เพื่อเข้าถึง fields และ methods ที่เป็น private
     * ใน GameController โดยไม่ต้อง extend หรือแก้ไขโค้ดหลัก
     *
     */
    private static class GameControllerAccessor {
        /** Instance ของ GameController ที่ต้องการเข้าถึง */
        private final GameController controller;

        public GameControllerAccessor(GameController controller) {
            this.controller = controller;
        }

        // ===== ดึงค่าตัวแปร =====

        /** ดึงงูผู้เล่น 1 */
        public Snake getSnake1() {
            return getField("snake1");
        }

        /** ดึงงูผู้เล่น 2 */
        public Snake getSnake2() {
            return getField("snake2");
        }

        /** ดึงอาหาร */
        public Food getFood() {
            return getField("food");
        }

        /** ดึงสถานะเกมปัจจุบัน */
        public GameState getState() {
            return getField("currentState");
        }

        /** ดึงข้อความผู้ชนะ */
        public String getWinnerText() {
            return getField("winnerText");
        }

        /**
         * Generic method สำหรับดึงค่า field
         * name ชื่อ field
         * return ค่าใน field (cast เป็น T)
         */
        @SuppressWarnings("unchecked")
        private <T> T getField(String name) {
            try {
                Field field = GameController.class.getDeclaredField(name);
                field.setAccessible(true); // ให้เข้าถึง private field ได้
                return (T) field.get(controller);
            } catch (Exception e) {
                fail("ไม่สามารถเข้าถึง field: " + name + " - " + e.getMessage());
                return null;
            }
        }


        // ===== เรียก method ที่เป็น private =====

        /**
         * เรียก method determineHeadOnHeadWinner และคืนค่าผู้ชนะ
         * return ข้อความผู้ชนะ
         */
        public String callDetermineHeadOnHeadWinner() {
            invokeMethod("determineHeadOnHeadWinner");
            return getWinnerText();
        }

        /** เรียก method generateNewFood */
        public void callGenerateNewFood() {
            invokeMethod("generateNewFood");
        }

        /** เรียก method gameOver */
        public void callGameOver() {
            invokeMethod("gameOver");
        }

        /**
         * Generic method สำหรับเรียก method
         * name ชื่อ method
         * args อาร์กิวเมนต์ที่ส่งให้ method (ถ้ามี)
         */
        private void invokeMethod(String name, Object... args) {
            try {
                Method method = findMethod(name);
                assert method != null;
                method.setAccessible(true); // ให้เรียก private method ได้
                method.invoke(controller, args);
            } catch (Exception e) {
                fail("ไม่สามารถเรียก method: " + name + " - " + e.getMessage());
            }
        }

        /**
         * ค้นหา method จากชื่อ (ไม่ต้องระบุ parameter types)
         * name ชื่อ method
         * return Method object
         */
        private Method findMethod(String name) {
            for (Method m : GameController.class.getDeclaredMethods()) {
                if (m.getName().equals(name)) {
                    return m;
                }
            }
            fail("ไม่พบ method: " + name);
            return null;
        }
    }
}