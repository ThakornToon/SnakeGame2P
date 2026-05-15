import SnakeGame2P.model.entity.GameObject;
import SnakeGame2P.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ GameObject abstract class (JavaFX Version)
 */
public class GameObjectTest {

    // สร้าง concrete class สำหรับทดสอบ abstract class
    private static class TestGameObject extends GameObject {
        public TestGameObject(int x, int y) {
            super(x, y);
        }

        @Override
        public void draw(GraphicsContext gc) {
            // ไม่ต้อง implement จริงสำหรับ test
        }
    }

    private TestGameObject obj1;
    private TestGameObject obj2;

    @BeforeEach
    void setUp() {
        obj1 = new TestGameObject(100, 100);
        obj2 = new TestGameObject(200, 200);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(100, obj1.getX(), "X position should be 100");
        assertEquals(100, obj1.getY(), "Y position should be 100");

        Point2D position = obj1.getPosition();
        assertEquals(100, position.getX(), "Position X should be 100");
        assertEquals(100, position.getY(), "Position Y should be 100");
    }

    @Test
    void testSetPosition() {
        obj1.setPosition(300, 400);
        assertEquals(300, obj1.getX(), "X should be updated to 300");
        assertEquals(400, obj1.getY(), "Y should be updated to 400");

        Point2D position = obj1.getPosition();
        assertEquals(300, position.getX(), "Position X should be 300");
        assertEquals(400, position.getY(), "Position Y should be 400");
    }

    @Test
    void testIsCollidingWith_SamePosition() {
        obj2.setPosition(100, 100);
        assertTrue(obj1.isCollidingWith(obj2),
                "Objects at same position should collide");
    }

    @Test
    void testIsCollidingWith_DifferentPosition() {
        assertFalse(obj1.isCollidingWith(obj2),
                "Objects at different positions should not collide");
    }

    @Test
    void testGetPositionReturnsImmutable() {
        Point2D pos = obj1.getPosition();
        // Point2D in JavaFX is immutable - cannot be modified
        // This test verifies that getPosition returns a Point2D
        assertNotNull(pos, "getPosition should return a Point2D");
        assertEquals(100, pos.getX(), "Position X should be correct");
        assertEquals(100, pos.getY(), "Position Y should be correct");

        // Verify that the original object is unchanged after using the returned Point2D
        assertEquals(100, obj1.getX(), "Original X should remain unchanged");
        assertEquals(100, obj1.getY(), "Original Y should remain unchanged");
    }

    @Test
    void testMultipleSetPosition() {
        obj1.setPosition(50, 60);
        assertEquals(50, obj1.getX(), "First set X should be 50");
        assertEquals(60, obj1.getY(), "First set Y should be 60");

        obj1.setPosition(70, 80);
        assertEquals(70, obj1.getX(), "Second set X should be 70");
        assertEquals(80, obj1.getY(), "Second set Y should be 80");
    }

    @Test
    void testPositionBoundary() {
        obj1.setPosition(0, 0);
        assertEquals(0, obj1.getX(), "X should be 0");
        assertEquals(0, obj1.getY(), "Y should be 0");

        obj1.setPosition(Constants.BOARD_WIDTH, Constants.BOARD_HEIGHT);
        assertEquals(Constants.BOARD_WIDTH, obj1.getX(), "X should be at board width");
        assertEquals(Constants.BOARD_HEIGHT, obj1.getY(), "Y should be at board height");
    }
}