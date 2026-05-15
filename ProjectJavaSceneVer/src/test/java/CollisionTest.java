import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับทดสอบการชนในสถานการณ์ต่างๆ เพิ่มเติม
 */
public class CollisionTest {

    @BeforeEach
    void setUp() {
        // ไม่ต้อง setup อะไร เพราะแต่ละ test สร้าง Snake ใหม่
    }

    @Test
    void testCollisionWithSelf_HeadToBodyLogic() {
        // ทดสอบ: ตรวจสอบว่า isCollidingWithSelf() ทำงานถูกต้อง
        // โดยการสร้างสถานการณ์ที่หัวอยู่ที่ตำแหน่งเดียวกับลำตัวส่วนใดส่วนหนึ่ง

        Snake snake = new Snake(400, 300, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);

        // ดึง reference ของตำแหน่งต่างๆ โดยใช้ reflection
        try {
            java.lang.reflect.Field xField = Snake.class.getDeclaredField("xPositions");
            java.lang.reflect.Field yField = Snake.class.getDeclaredField("yPositions");
            xField.setAccessible(true);
            yField.setAccessible(true);

            List<Integer> xPos = (List<Integer>) xField.get(snake);
            List<Integer> yPos = (List<Integer>) yField.get(snake);

            // ปกติ: หัว index 0 ที่ (400,300), ลำตัว index 1 ที่ (375,300), index 2 ที่ (350,300)

            // ทดสอบกรณีที่ 1: หัวอยู่ที่ตำแหน่งเดียวกับหัวตัวเอง (ไม่นับเป็นการชน)
            assertFalse(snake.isCollidingWithSelf(),
                    "Snake should not count head with itself as collision");

            // ทดสอบกรณีที่ 2: หัวอยู่ที่ตำแหน่งเดียวกับส่วนอื่นๆ
            for (int i = 2; i < xPos.size(); i++) {
                Point otherBodyPos = new Point(xPos.get(i), yPos.get(i));
                xPos.set(0, otherBodyPos.x);
                yPos.set(0, otherBodyPos.y);

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
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
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
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
        Snake snake2 = new Snake(300, 200, Direction.RIGHT,
                Color.ORANGE, Color.RED, Color.PINK);

        // เคลื่อนที่ snake1 ไปที่ตำแหน่งที่คาดว่าเป็นลำตัว snake2
        // แต่เนื่องจาก snake2 มีส่วนซ้ำกัน เราจะตรวจสอบด้วย isBodyAt โดยตรง

        // ดูลำตัว snake2 ตำแหน่งที่ 1 (index 1)
        Point bodyPos = new Point(
                snake2.getXPositions().get(1),
                snake2.getYPositions().get(1)
        );

        // ย้าย snake1 ไปที่ตำแหน่งนั้น
        try {
            java.lang.reflect.Field xField = Snake.class.getDeclaredField("xPositions");
            java.lang.reflect.Field yField = Snake.class.getDeclaredField("yPositions");
            xField.setAccessible(true);
            yField.setAccessible(true);

            List<Integer> xPos1 = (List<Integer>) xField.get(snake1);
            List<Integer> yPos1 = (List<Integer>) yField.get(snake1);

            xPos1.set(0, bodyPos.x);
            yPos1.set(0, bodyPos.y);

        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }

        assertTrue(snake2.isBodyAt(snake1.getHead()),
                "Snake1 head should be at snake2 body position");
        assertTrue(snake1.isCollidingWithSnake(snake2),
                "Snake1 should collide with snake2 when head hits body");
    }
}