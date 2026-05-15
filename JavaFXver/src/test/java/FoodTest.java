import SnakeGame2P.model.entity.Food;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Food (JavaFX Version)
 */
public class FoodTest {

    private Food food;
    private Snake snake1;
    private Snake snake2;

    @BeforeEach
    void setUp() {
        food = new Food();
        snake1 = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);
        snake2 = new Snake(600, 200, Direction.LEFT,
                Color.ORANGE, Color.RED, Color.PINK);
    }

    @Test
    void testFoodCreation() {
        assertTrue(food.getX() % Constants.UNIT_SIZE == 0,
                "Food X should be multiple of UNIT_SIZE");
        assertTrue(food.getY() % Constants.UNIT_SIZE == 0,
                "Food Y should be multiple of UNIT_SIZE");

        assertTrue(food.getX() >= 0 && food.getX() < Constants.BOARD_WIDTH,
                "Food X should be within board width");
        assertTrue(food.getY() >= 0 && food.getY() < Constants.BOARD_HEIGHT,
                "Food Y should be within board height");
    }

    @RepeatedTest(20)
    void testGenerateNewPosition() {
        Food testFood = new Food();
        int firstX = testFood.getX();
        int firstY = testFood.getY();

        testFood.generateNewPosition();

        boolean positionChanged = (firstX != testFood.getX()) || (firstY != testFood.getY());
        assertTrue(positionChanged, "Generate new position should usually change position");
    }

    @Test
    void testIsEatenBy_HeadCollision() {
        Point2D headPos = snake1.getHead();
        food.setPosition((int) headPos.getX(), (int) headPos.getY());

        assertTrue(food.isEatenBy(snake1),
                "Food should be eaten when snake head is at food position");
        assertFalse(food.isEatenBy(snake2),
                "Food should not be eaten by other snake");
    }

    @Test
    void testIsEatenBy_BodyCollision() {
        Point2D bodyPos = new Point2D(
                snake1.getXPositions().get(1),
                snake1.getYPositions().get(1)
        );
        food.setPosition((int) bodyPos.getX(), (int) bodyPos.getY());

        assertFalse(food.isEatenBy(snake1),
                "Food should not be eaten when colliding with body");
    }

    @Test
    void testOnConsumed_Effect() {
        int oldLength = snake1.getLength();
        int oldFoodEaten = snake1.getFoodEaten();

        food.onConsumed(snake1);

        assertEquals(oldLength + 1, snake1.getLength(),
                "Snake should grow after consuming food");
        assertEquals(oldFoodEaten + 1, snake1.getFoodEaten(),
                "Food eaten count should increase");
    }

    @Test
    void testIsOccupiedBy_WithBothSnakes() {
        // กรณีไม่ซ้อนทับ
        food.setPosition(0, 0);
        assertFalse(food.isOccupiedBy(snake1, snake2),
                "Food should not be occupied when at empty position");

        // กรณีซ้อนทับกับงูตัวที่ 1
        Point2D headPos = snake1.getHead();
        food.setPosition((int) headPos.getX(), (int) headPos.getY());
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake1 head");

        // กรณีซ้อนทับกับงูตัวที่ 2
        headPos = snake2.getHead();
        food.setPosition((int) headPos.getX(), (int) headPos.getY());
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake2 head");

        // กรณีซ้อนทับกับลำตัว
        Point2D bodyPos = new Point2D(
                snake1.getXPositions().get(1),
                snake1.getYPositions().get(1)
        );
        food.setPosition((int) bodyPos.getX(), (int) bodyPos.getY());
        assertTrue(food.isOccupiedBy(snake1, snake2),
                "Food should be occupied when on snake body");
    }
}