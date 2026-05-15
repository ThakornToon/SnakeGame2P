import SnakeGame2P.controller.PowerBoostManager;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ PowerBoostManager (JavaFX Version)
 */
public class PowerBoostManagerTest {

    private PowerBoostManager manager;
    private Snake snake;
    private Snake snake2;

    @BeforeEach
    void setUp() {
        manager = new PowerBoostManager();
        snake = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);
        snake2 = new Snake(600, 200, Direction.LEFT,
                Color.ORANGE, Color.RED, Color.PINK);

        // ทำให้งูยาวขึ้นพอที่จะทดสอบเสริมพลัง
        for (int i = 0; i < 5; i++) {
            snake.grow(); // length = 8
            snake2.grow(); // length = 8
        }
    }

    @Test
    void testActivatePowerBoost() {
        assertFalse(snake.isPowerBoostActive(), "Initially power boost should be inactive");

        manager.activatePowerBoost(snake);
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active after activation");
    }

    @Test
    void testActivatePowerBoost_WhenTooShort() {
        Snake shortSnake = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);

        manager.activatePowerBoost(shortSnake);
        assertFalse(shortSnake.isPowerBoostActive(),
                "Should not activate power boost when length <= 3");
    }

    @Test
    void testDeactivatePowerBoost() {
        manager.activatePowerBoost(snake);
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active");

        manager.deactivatePowerBoost(snake);
        assertFalse(snake.isPowerBoostActive(), "Power boost should be inactive after deactivation");
    }

    @Test
    void testHandlePowerBoosts_ActiveButNotTimeToShrink() {
        manager.activatePowerBoost(snake);
        long beforeShrinkTime = snake.getLastShrinkTime();
        int beforeLength = snake.getLength();

        manager.handlePowerBoosts(snake, snake2);

        assertEquals(beforeLength, snake.getLength(),
                "Snake should not shrink without time passing");
        assertEquals(beforeShrinkTime, snake.getLastShrinkTime(),
                "Last shrink time should not change");
    }

    @Test
    void testHandlePowerBoosts_ShrinkWhenActive() {
        manager.activatePowerBoost(snake);
        int beforeLength = snake.getLength();

        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);

        manager.handlePowerBoosts(snake, snake2);

        assertTrue(snake.getLength() < beforeLength,
                "Snake should shrink after interval passed");
    }

    @Test
    void testHandlePowerBoosts_MultipleShrinks() {
        manager.activatePowerBoost(snake);
        int initialLength = snake.getLength();

        // หดครั้งที่ 1
        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        manager.handlePowerBoosts(snake, snake2);
        int afterFirstShrink = snake.getLength();
        assertTrue(afterFirstShrink < initialLength,
                "First shrink should reduce length");

        // หดครั้งที่ 2
        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        manager.handlePowerBoosts(snake, snake2);
        int afterSecondShrink = snake.getLength();
        assertTrue(afterSecondShrink < afterFirstShrink,
                "Second shrink should reduce length further");
    }

    @Test
    void testHandlePowerBoosts_AutoDeactivateAtLength3() {
        manager.activatePowerBoost(snake);

        // หดจนเหลือ 3
        while (snake.getLength() > 3) {
            snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
            manager.handlePowerBoosts(snake, snake2);
        }

        assertEquals(3, snake.getLength(), "Length should be 3");
        assertFalse(snake.isPowerBoostActive(),
                "Power boost should auto-deactivate at length 3");

        long lastShrinkTime = snake.getLastShrinkTime();

        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        manager.handlePowerBoosts(snake, snake2);

        assertEquals(3, snake.getLength(), "Length should not go below 3");
        assertEquals(lastShrinkTime, snake.getLastShrinkTime(),
                "Last shrink time should not change when power boost is inactive");
    }

    @Test
    void testHandlePowerBoosts_WithTwoSnakes() {
        manager.activatePowerBoost(snake);
        manager.activatePowerBoost(snake2);

        int snake1Length = snake.getLength();
        int snake2Length = snake2.getLength();

        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        snake2.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);

        manager.handlePowerBoosts(snake, snake2);

        assertTrue(snake.getLength() < snake1Length, "Snake1 should shrink");
        assertTrue(snake2.getLength() < snake2Length, "Snake2 should shrink");
    }
}