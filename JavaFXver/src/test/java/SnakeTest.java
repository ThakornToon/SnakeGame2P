import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Snake (JavaFX Version)
 */
public class SnakeTest {

    private Snake snake;
    private static final int START_X = 400;
    private static final int START_Y = 300;

    @BeforeEach
    void setUp() {
        snake = new Snake(START_X, START_Y, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);
    }

    @Test
    void testInitialState() {
        assertEquals(3, snake.getLength(), "Initial length should be 3");
        assertEquals(0, snake.getFoodEaten(), "Initial food eaten should be 0");
        assertEquals(Direction.RIGHT, snake.getDirection(), "Initial direction should be RIGHT");
        assertFalse(snake.isPowerBoostActive(), "Power boost should be inactive initially");

        List<Integer> xPos = snake.getXPositions();
        assertEquals(START_X, xPos.get(0), "Head X should be at start X");
        assertEquals(START_X - Constants.UNIT_SIZE, xPos.get(1), "Body 1 X should be left of head");
        assertEquals(START_X - 2 * Constants.UNIT_SIZE, xPos.get(2), "Body 2 X should be more left");
    }

    @Test
    void testMoveRight() {
        Point2D oldHead = snake.getHead();
        snake.move();

        Point2D newHead = snake.getHead();
        assertEquals(oldHead.getX() + Constants.UNIT_SIZE, newHead.getX(),
                "Head should move right by UNIT_SIZE");
        assertEquals(oldHead.getY(), newHead.getY(), "Y should not change when moving right");
    }

    @Test
    void testMoveLeft() {
        snake.setDirection(Direction.UP);
        snake.move();
        snake.setDirection(Direction.LEFT);
        Point2D oldHead = snake.getHead();
        snake.move();

        Point2D newHead = snake.getHead();
        assertEquals(oldHead.getX() - Constants.UNIT_SIZE, newHead.getX(),
                "Head should move left by UNIT_SIZE");
        assertEquals(oldHead.getY(), newHead.getY(), "Y should not change when moving left");
    }

    @Test
    void testMoveUp() {
        snake.setDirection(Direction.UP);
        Point2D oldHead = snake.getHead();
        snake.move();

        Point2D newHead = snake.getHead();
        assertEquals(oldHead.getY() - Constants.UNIT_SIZE, newHead.getY(),
                "Head should move up by UNIT_SIZE");
    }

    @Test
    void testMoveDown() {
        snake.setDirection(Direction.DOWN);
        Point2D oldHead = snake.getHead();
        snake.move();

        Point2D newHead = snake.getHead();
        assertEquals(oldHead.getY() + Constants.UNIT_SIZE, newHead.getY(),
                "Head should move down by UNIT_SIZE");
    }

    @Test
    void testCannotReverseDirection() {
        snake.setDirection(Direction.LEFT);
        assertEquals(Direction.RIGHT, snake.getDirection(),
                "Should not be able to reverse direction immediately");

        snake.setDirection(Direction.UP);
        assertEquals(Direction.UP, snake.getDirection(),
                "Should be able to turn to non-opposite direction");
    }

    @Test
    void testGrow() {
        int oldLength = snake.getLength();
        int oldFoodEaten = snake.getFoodEaten();

        snake.grow();

        assertEquals(oldLength + 1, snake.getLength(),
                "Length should increase by 1 after eating");
        assertEquals(oldFoodEaten + 1, snake.getFoodEaten(),
                "Food eaten count should increase by 1");
    }

    @Test
    void testShrink() {
        // ทำให้งูยาวขึ้น
        for (int i = 0; i < 5; i++) {
            snake.grow();
        }
        assertEquals(8, snake.getLength(), "Initial length after growing should be 8");

        snake.activatePowerBoost();
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active");

        snake.applyPowerBoostEffect();
        assertEquals(7, snake.getLength(), "Length should be 7 after first shrink");

        snake.applyPowerBoostEffect();
        assertEquals(6, snake.getLength(), "Length should be 6 after second shrink");

        snake.applyPowerBoostEffect();
        assertEquals(5, snake.getLength(), "Length should be 5 after third shrink");

        snake.applyPowerBoostEffect();
        assertEquals(4, snake.getLength(), "Length should be 4 after fourth shrink");

        snake.applyPowerBoostEffect();
        assertEquals(3, snake.getLength(), "Length should be 3 after fifth shrink");

        assertFalse(snake.isPowerBoostActive(),
                "Power boost should deactivate when length reaches 3");

        snake.applyPowerBoostEffect();
        assertEquals(3, snake.getLength(),
                "Length should not go below 3");
    }

    @Test
    void testIsCollidingWithSelf_NoCollision() {
        for (int i = 0; i < 5; i++) {
            snake.move();
            assertFalse(snake.isCollidingWithSelf(),
                    "Snake should not collide with itself in normal movement");
        }
    }

    @Test
    void testIsOutOfBounds() {
        assertFalse(snake.isOutOfBounds(),
                "Snake at start position should be in bounds");

        while (snake.getHead().getX() < Constants.BOARD_WIDTH) {
            snake.move();
        }

        assertTrue(snake.isOutOfBounds(),
                "Snake should be out of bounds after moving past right edge");
    }

    @Test
    void testIsBodyAt() {
        Point2D bodyPosition = new Point2D(
                snake.getXPositions().get(1),
                snake.getYPositions().get(1)
        );

        assertTrue(snake.isBodyAt(bodyPosition),
                "Should detect body at position of body segment");

        Point2D emptyPosition = new Point2D(0, 0);
        assertFalse(snake.isBodyAt(emptyPosition),
                "Should not detect body at empty position");
    }

    @Test
    void testGetHead() {
        Point2D head = snake.getHead();
        assertEquals(snake.getXPositions().get(0), (int) head.getX(),
                "Head X should match first X position");
        assertEquals(snake.getYPositions().get(0), (int) head.getY(),
                "Head Y should match first Y position");
    }

    @Test
    void testGetXPositions_ReturnsCopy() {
        List<Integer> positions = snake.getXPositions();
        positions.set(0, 999);

        assertNotEquals(999, snake.getXPositions().get(0),
                "Original snake positions should not be affected by external changes");
    }

    @Test
    void testGetYPositions_ReturnsCopy() {
        List<Integer> positions = snake.getYPositions();
        positions.set(0, 999);

        assertNotEquals(999, snake.getYPositions().get(0),
                "Original snake positions should not be affected by external changes");
    }
}