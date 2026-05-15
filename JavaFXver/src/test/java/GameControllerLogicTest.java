import SnakeGame2P.controller.GameController;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.Food;
import SnakeGame2P.utils.GameState;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.view.GamePanel;
import SnakeGame2P.audio.AudioManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Logic สำคัญใน GameController
 * ใช้ TestFX เพื่อ initialize JavaFX Toolkit
 */
@ExtendWith(ApplicationExtension.class)
public class GameControllerLogicTest {

    private GameController controller;
    private TestableGamePanel testPanel;
    private TestableAudioManager testAudioManager;
    private GameControllerAccessor accessor;
    private Scene testScene;

    @Start
    public void start(Stage stage) {
        // JavaFX Toolkit จะถูก initialize อัตโนมัติเมื่อ method นี้ถูกเรียก
        testAudioManager = new TestableAudioManager();
        testPanel = new TestableGamePanel(testAudioManager);
        testScene = new Scene(new Pane(), Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        controller = new GameController(testPanel, testScene, testAudioManager);
        accessor = new GameControllerAccessor(controller);

        // ไม่ต้องแสดง stage จริง
        // stage.setScene(testScene);
        // stage.show();
    }

    @BeforeEach
    void setUp() {
        // รีเซ็ตสถานะก่อนทดสอบแต่ละครั้ง
        if (controller != null) {
            // ไม่ต้องทำอะไรเพิ่ม
        }
    }

    // ========== Test Methods ==========

    @Test
    void testStartGameChangesState() {
        assertEquals(GameState.MENU, accessor.getState());

        controller.startGame();

        assertEquals(GameState.PLAYING, accessor.getState());
        assertFalse(testPanel.isMenuVisible(), "Menu buttons should be hidden");
        assertFalse(testPanel.isRestartVisible(), "Restart button should be hidden");
        assertTrue(testAudioManager.wasBackgroundMusicPlayed(Constants.SOUND_BACKGROUND),
                "Should play background music");
    }

    @Test
    void testRestartGame() {
        controller.startGame();
        controller.restartGame();

        assertEquals(GameState.MENU, accessor.getState());
        assertTrue(testPanel.isMenuVisible(), "Menu buttons should be visible");
        assertTrue(testAudioManager.wasBackgroundMusicPlayed(Constants.SOUND_MENU),
                "Should play menu music");
    }

    @Test
    void testPauseFunctionality() {
        controller.startGame();
        assertEquals(GameState.PLAYING, accessor.getState());

        controller.togglePause();
        assertEquals(GameState.PAUSED, accessor.getState());

        controller.togglePause();
        assertEquals(GameState.PLAYING, accessor.getState());
    }

    @Test
    void testReturnToMenu() {
        controller.startGame();
        controller.togglePause();
        controller.returnToMenu();

        assertTrue(testPanel.isMenuVisible(), "Menu buttons should be visible");
        assertEquals(GameState.MENU, accessor.getState());
        assertTrue(testAudioManager.wasBackgroundMusicPlayed(Constants.SOUND_MENU),
                "Should play menu music");
    }

    @Test
    void testGameOverTrigger() {
        controller.startGame();
        assertEquals(GameState.PLAYING, accessor.getState());

        accessor.callGameOver();

        assertEquals(GameState.GAME_OVER, accessor.getState());
        assertTrue(testAudioManager.wasBackgroundMusicPlayed(Constants.SOUND_GAME_OVER),
                "Should play game over sound");
        assertTrue(testPanel.isRestartVisible(), "Restart button should be visible");
    }

    @Test
    void testCollisionOutOfBounds_Snake1Dies() {
        controller.startGame();
        Snake snake1 = accessor.getSnake1();

        // ย้ายงูออกนอกขอบทางขวา
        try {
            Field xField = Snake.class.getDeclaredField("xPositions");
            xField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var xPos = (java.util.List<Integer>) xField.get(snake1);

            xPos.set(0, Constants.BOARD_WIDTH + Constants.UNIT_SIZE);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        accessor.callCheckCollisions();

        assertEquals(GameState.GAME_OVER, accessor.getState());
        String winner = accessor.getWinnerText();
        assertTrue(winner.contains("ผู้เล่น 2") || winner.contains("Player 2"),
                "Snake1 out of bounds should make Player 2 win");
    }

    @Test
    void testCollisionSelf_Snake1Dies() {
        controller.startGame();
        Snake snake1 = accessor.getSnake1();

        try {
            // ทำให้งูยาวขึ้นก่อน
            for (int i = 0; i < 5; i++) {
                snake1.grow();
            }

            Field xField = Snake.class.getDeclaredField("xPositions");
            Field yField = Snake.class.getDeclaredField("yPositions");
            xField.setAccessible(true);
            yField.setAccessible(true);

            @SuppressWarnings("unchecked")
            var xPos = (java.util.List<Integer>) xField.get(snake1);
            @SuppressWarnings("unchecked")
            var yPos = (java.util.List<Integer>) yField.get(snake1);

            // ให้หัวอยู่ที่ตำแหน่งเดียวกับลำตัวส่วนที่ 2
            xPos.set(0, xPos.get(2));
            yPos.set(0, yPos.get(2));

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        accessor.callCheckCollisions();

        assertEquals(GameState.GAME_OVER, accessor.getState());
        String winner = accessor.getWinnerText();
        assertTrue(winner.contains("ผู้เล่น 2") || winner.contains("Player 2"),
                "Snake1 self collision should make Player 2 win");
    }

    @Test
    void testFoodGeneration_NotOnSnake() {
        controller.startGame();

        Snake snake1 = accessor.getSnake1();
        Snake snake2 = accessor.getSnake2();

        for (int i = 0; i < 20; i++) {
            accessor.callGenerateNewFood();
            Food food = accessor.getFood();

            boolean occupied = isFoodOnSnake(food, snake1, snake2);
            assertFalse(occupied, "อาหารไม่ควรเกิดบนตัวงู รอบที่ " + (i + 1));
        }
    }

    @Test
    void testFoodEatenBySnake1() {
        controller.startGame();

        Snake snake1 = accessor.getSnake1();
        Food food = accessor.getFood();

        int oldLength = snake1.getLength();
        int oldFoodEaten = snake1.getFoodEaten();

        // ย้ายอาหารไปที่หัวงู
        food.setPosition((int) snake1.getHead().getX(), (int) snake1.getHead().getY());

        accessor.callCheckFoodCollision();

        assertEquals(oldLength + 1, snake1.getLength(), "Snake should grow");
        assertEquals(oldFoodEaten + 1, snake1.getFoodEaten(), "Food eaten count should increase");
        assertTrue(testAudioManager.wasSoundPlayed(Constants.SOUND_EAT), "Should play eat sound");
    }

    // ========== Helper Methods ==========

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

    private static class TestableGamePanel extends GamePanel {
        private boolean restartVisible = false;
        private boolean menuVisible = true;

        public TestableGamePanel(AudioManager audioManager) {
            super(audioManager);
        }

        @Override
        public void setRestartVisible(boolean visible) {
            this.restartVisible = visible;
        }

        @Override
        public void setMenuVisible(boolean visible) {
            this.menuVisible = visible;
        }

        public boolean isRestartVisible() {
            return restartVisible;
        }

        public boolean isMenuVisible() {
            return menuVisible;
        }

        @Override
        public void requestFocus() {
            // ไม่ต้อง focus จริง
        }

        @Override
        public void updateMuteButton(boolean muted) {
            // ไม่ต้องทำ
        }
    }

    private static class TestableAudioManager extends AudioManager {
        private boolean muted = false;
        private String lastPlayedSound = null;
        private String lastPlayedBackground = null;

        public TestableAudioManager() {
            // ไม่โหลดเสียงจริง
        }

        @Override
        protected void loadSounds() {
            // ไม่โหลดอะไร
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
            // ไม่ต้องทำ
        }

        @Override
        public void toggleMute() {
            muted = !muted;
        }

        @Override
        public boolean isMuted() {
            return muted;
        }

        public boolean wasSoundPlayed(String path) {
            return path.equals(lastPlayedSound);
        }

        public boolean wasBackgroundMusicPlayed(String path) {
            return path.equals(lastPlayedBackground);
        }
    }

    private static class GameControllerAccessor {
        private final GameController controller;

        public GameControllerAccessor(GameController controller) {
            this.controller = controller;
        }

        public Snake getSnake1() { return getField("snake1"); }
        public Snake getSnake2() { return getField("snake2"); }
        public Food getFood() { return getField("food"); }
        public GameState getState() { return getField("currentState"); }
        public String getWinnerText() { return getField("winnerText"); }

        @SuppressWarnings("unchecked")
        private <T> T getField(String name) {
            try {
                Field field = GameController.class.getDeclaredField(name);
                field.setAccessible(true);
                return (T) field.get(controller);
            } catch (Exception e) {
                fail("ไม่สามารถเข้าถึง field: " + name + " - " + e.getMessage());
                return null;
            }
        }

        public void callDetermineHeadOnHeadWinner() {
            invokeMethod("determineHeadOnHeadWinner");
        }

        public void callGenerateNewFood() {
            invokeMethod("generateNewFood");
        }

        public void callGameOver() {
            invokeMethod("gameOver");
        }

        public void callCheckCollisions() {
            invokeMethod("checkCollisions");
        }

        public void callCheckFoodCollision() {
            invokeMethod("checkFoodCollision");
        }

        private void invokeMethod(String name, Object... args) {
            try {
                Method method = findMethod(name);
                method.setAccessible(true);
                method.invoke(controller, args);
            } catch (Exception e) {
                fail("ไม่สามารถเรียก method: " + name + " - " + e.getMessage());
            }
        }

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