import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับทดสอบการชนในสถานการณ์ต่างๆ เพิ่มเติม (JavaFX Version)
 */
public class CollisionTest {

    @BeforeEach
    void setUp() {
        // ไม่ต้อง setup อะไร เพราะแต่ละ test สร้าง Snake ใหม่
    }

    @Test
    void testCollisionWithSelf_HeadToBodyLogic() {
        // ทดสอบ: ตรวจสอบว่า isCollidingWithSelf() ทำงานถูกต้อง
        Snake snake = new Snake(400, 300, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);

        try {
            Field xField = Snake.class.getDeclaredField("xPositions");
            Field yField = Snake.class.getDeclaredField("yPositions");
            xField.setAccessible(true);
            yField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Integer> xPos = (List<Integer>) xField.get(snake);
            @SuppressWarnings("unchecked")
            List<Integer> yPos = (List<Integer>) yField.get(snake);

            // ทดสอบกรณีที่ 1: หัวอยู่ที่ตำแหน่งเดียวกับหัวตัวเอง (ไม่นับเป็นการชน)
            assertFalse(snake.isCollidingWithSelf(),
                    "Snake should not count head with itself as collision");

            // ทดสอบกรณีที่ 2: หัวอยู่ที่ตำแหน่งเดียวกับส่วนอื่นๆ
            for (int i = 2; i < xPos.size(); i++) {
                Point2D otherBodyPos = new Point2D(xPos.get(i), yPos.get(i));
                xPos.set(0, (int) otherBodyPos.getX());
                yPos.set(0, (int) otherBodyPos.getY());

                assertTrue(snake.isCollidingWithSelf(),
                        "Snake should collide when head at body position " + i);
            }

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    void testCollisionWithOtherSnake_HeadToHead() {
        // ทดสอบ: หัวชนหัว
        Snake snake1 = new Snake(300, 200, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);
        Snake snake2 = new Snake(300, 200, Direction.LEFT,
                Color.ORANGE, Color.RED, Color.PINK);

        assertTrue(snake1.isCollidingWithSnake(snake2),
                "Snake1 should collide with snake2 when heads are at same position");
        assertTrue(snake2.isCollidingWithSnake(snake1),
                "Snake2 should collide with snake1 when heads are at same position");
    }

    @Test
    void testCollisionWithOtherSnake_HeadToBody() {
        // ทดสอบ: หัวชนลำตัว
        Snake snake1 = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARKGRAY, Color.CYAN);
        Snake snake2 = new Snake(300, 200, Direction.RIGHT,
                Color.ORANGE, Color.RED, Color.PINK);

        // ดูลำตัว snake2 ตำแหน่งที่ 1 (index 1)
        Point2D bodyPos = new Point2D(
                snake2.getXPositions().get(1),
                snake2.getYPositions().get(1)
        );

        // ย้าย snake1 ไปที่ตำแหน่งนั้น
        try {
            Field xField = Snake.class.getDeclaredField("xPositions");
            Field yField = Snake.class.getDeclaredField("yPositions");
            xField.setAccessible(true);
            yField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Integer> xPos1 = (List<Integer>) xField.get(snake1);
            @SuppressWarnings("unchecked")
            List<Integer> yPos1 = (List<Integer>) yField.get(snake1);

            xPos1.set(0, (int) bodyPos.getX());
            yPos1.set(0, (int) bodyPos.getY());

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        assertTrue(snake2.isBodyAt(snake1.getHead()),
                "Snake1 head should be at snake2 body position");
        assertTrue(snake1.isCollidingWithSnake(snake2),
                "Snake1 should collide with snake2 when head hits body");
    }
}