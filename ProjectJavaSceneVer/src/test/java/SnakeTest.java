import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Snake
 * ทดสอบการทำงานทั้งหมดของงู รวมถึงการเคลื่อนที่ การเจริญเติบโต การเสริมพลัง
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ต้องมั่นใจว่าการเคลื่อนที่ถูกต้อง
 * 2. ตรวจสอบการจัดการสถานะต่างๆ (เสริมพลัง, การชน)
 * 3. ป้องกัน bug จากการเลี้ยวกลับทาง
 */
public class SnakeTest {

    private Snake snake;
    private static final int START_X = 400;
    private static final int START_Y = 300;

    @BeforeEach
    void setUp() {
        snake = new Snake(START_X, START_Y, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
    }

    @Test
    void testInitialState() {
        // ทดสอบ: สถานะเริ่มต้นของงูควรถูกต้อง
        // เหตุผล: ตรวจสอบ constructor ทำงานถูกต้อง
        assertEquals(3, snake.getLength(), "Initial length should be 3");
        assertEquals(0, snake.getFoodEaten(), "Initial food eaten should be 0");
        assertEquals(Direction.RIGHT, snake.getDirection(), "Initial direction should be RIGHT");
        assertFalse(snake.isPowerBoostActive(), "Power boost should be inactive initially");

        // ตรวจสอบตำแหน่งเริ่มต้น (หัวอยู่ขวาสุด)
        List<Integer> xPos = snake.getXPositions();
        assertEquals(START_X, xPos.get(0), "Head X should be at start X");
        assertEquals(START_X - Constants.UNIT_SIZE, xPos.get(1), "Body 1 X should be left of head");
        assertEquals(START_X - 2 * Constants.UNIT_SIZE, xPos.get(2), "Body 2 X should be more left");
    }

    @Test
    void testMoveRight() {
        // ทดสอบ: เคลื่อนที่ไปทางขวา
        // เหตุผล: ตรวจสอบการเคลื่อนที่พื้นฐาน
        Point oldHead = snake.getHead();
        snake.move();

        Point newHead = snake.getHead();
        assertEquals(oldHead.x + Constants.UNIT_SIZE, newHead.x,
                "Head should move right by UNIT_SIZE");
        assertEquals(oldHead.y, newHead.y, "Y should not change when moving right");
    }

    @Test
    void testMoveLeft() {
        // ทดสอบ: เคลื่อนที่ไปทางซ้าย
        snake.setDirection(Direction.UP);    // เปลี่ยนขึ้นก่อน (ไม่ตรงข้าม)
        snake.move();                         // เคลื่อนที่ขึ้น
        snake.setDirection(Direction.LEFT);   // เปลี่ยนซ้าย (ไม่ตรงข้ามกับ UP)
        Point oldHead = snake.getHead();
        snake.move();

        Point newHead = snake.getHead();
        assertEquals(oldHead.x - Constants.UNIT_SIZE, newHead.x,
                "Head should move left by UNIT_SIZE");
        assertEquals(oldHead.y, newHead.y, "Y should not change when moving left");
    }

    @Test
    void testMoveUp() {
        // ทดสอบ: เคลื่อนที่ขึ้น
        snake.setDirection(Direction.UP);
        Point oldHead = snake.getHead();
        snake.move();

        Point newHead = snake.getHead();
        assertEquals(oldHead.y - Constants.UNIT_SIZE, newHead.y,
                "Head should move up by UNIT_SIZE");
    }

    @Test
    void testMoveDown() {
        // ทดสอบ: เคลื่อนที่ลง
        snake.setDirection(Direction.DOWN);
        Point oldHead = snake.getHead();
        snake.move();

        Point newHead = snake.getHead();
        assertEquals(oldHead.y + Constants.UNIT_SIZE, newHead.y,
                "Head should move down by UNIT_SIZE");
    }

    @Test
    void testCannotReverseDirection() {
        // ทดสอบ: ไม่สามารถเลี้ยวกลับทางตรงข้ามได้ทันที
        // เหตุผล: ป้องกันงูชนตัวเองทันที
        snake.setDirection(Direction.LEFT); // พยายามเลี้ยวซ้ายจากขวา
        assertEquals(Direction.RIGHT, snake.getDirection(),
                "Should not be able to reverse direction immediately");

        snake.setDirection(Direction.UP); // เลี้ยวขึ้นได้
        assertEquals(Direction.UP, snake.getDirection(),
                "Should be able to turn to non-opposite direction");
    }

    @Test
    void testGrow() {
        // ทดสอบ: การกินอาหารควรทำให้งูยาวขึ้น
        // เหตุผล: ตรวจสอบการเพิ่มความยาว
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
        // ทดสอบ: การใช้ power boost ควรทำให้งูสั้นลง (แต่ไม่ต่ำกว่า 3)
        // เหตุผล: ตรวจสอบการลดความยาวและขีดจำกัด

        // ขั้นตอนที่ 1: ทำให้งูยาวขึ้นก่อนเพื่อทดสอบการหด
        for (int i = 0; i < 5; i++) {
            snake.grow(); // ตอนนี้ความยาวควรเป็น 8 (3 + 5)
        }
        assertEquals(8, snake.getLength(), "Initial length after growing should be 8");

        // ขั้นตอนที่ 2: เปิดโหมดเสริมพลัง
        snake.activatePowerBoost();
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active");

        // ขั้นตอนที่ 3: ทดสอบการหดครั้งแรก
        snake.applyPowerBoostEffect(); // หดครั้งที่ 1: 8 -> 7
        assertEquals(7, snake.getLength(), "Length should be 7 after first shrink");

        // ขั้นตอนที่ 4: ทดสอบการหดครั้งที่สอง
        snake.applyPowerBoostEffect(); // หดครั้งที่ 2: 7 -> 6
        assertEquals(6, snake.getLength(), "Length should be 6 after second shrink");

        // ขั้นตอนที่ 5: ทดสอบการหดครั้งที่สาม
        snake.applyPowerBoostEffect(); // หดครั้งที่ 3: 6 -> 5
        assertEquals(5, snake.getLength(), "Length should be 5 after third shrink");

        // ขั้นตอนที่ 6: ทดสอบการหดครั้งที่สี่
        snake.applyPowerBoostEffect(); // หดครั้งที่ 4: 5 -> 4
        assertEquals(4, snake.getLength(), "Length should be 4 after fourth shrink");

        // ขั้นตอนที่ 7: ทดสอบการหดครั้งที่ห้า (ควรเหลือ 3 และปิดโหมดอัตโนมัติ)
        snake.applyPowerBoostEffect(); // หดครั้งที่ 5: 4 -> 3
        assertEquals(3, snake.getLength(), "Length should be 3 after fifth shrink");

        // ตรวจสอบว่าเมื่อเหลือ 3 แล้ว power boost ควรปิดอัตโนมัติ
        assertFalse(snake.isPowerBoostActive(),
                "Power boost should deactivate when length reaches 3");

        // ขั้นตอนที่ 8: ทดสอบการหดเมื่อเหลือ 3 แล้ว (ไม่ควรหดอีก)
        snake.applyPowerBoostEffect(); // พยายามหดอีก
        assertEquals(3, snake.getLength(),
                "Length should not go below 3");
    }

    @Test
    void testIsCollidingWithSelf_NoCollision() {
        // ทดสอบ: งูไม่ชนตัวเองเมื่อเคลื่อนที่ปกติ
        // เหตุผล: ตรวจสอบว่าไม่แจ้งการชนผิดพลาด
        for (int i = 0; i < 5; i++) {
            snake.move();
            assertFalse(snake.isCollidingWithSelf(),
                    "Snake should not collide with itself in normal movement");
        }
    }

    @Test
    void testIsOutOfBounds() {
        // ทดสอบ: การตรวจจับออกนอกขอบจอ
        // เหตุผล: ตรวจสอบว่างูตายเมื่อออกนอกจอ
        assertFalse(snake.isOutOfBounds(),
                "Snake at start position should be in bounds");

        // เคลื่อนที่ออกนอกจอทางขวา
        while (snake.getHead().x < Constants.BOARD_WIDTH) {
            snake.move();
        }

        assertTrue(snake.isOutOfBounds(),
                "Snake should be out of bounds after moving past right edge");
    }

    @Test
    void testIsBodyAt() {
        // ทดสอบ: การตรวจสอบว่ามีลำตัวที่ตำแหน่งที่กำหนดหรือไม่
        // เหตุผล: ใช้สำหรับตรวจสอบการชนกับงูตัวอื่น
        Point bodyPosition = new Point(
                snake.getXPositions().get(1),
                snake.getYPositions().get(1)
        );

        assertTrue(snake.isBodyAt(bodyPosition),
                "Should detect body at position of body segment");

        Point emptyPosition = new Point(0, 0);
        assertFalse(snake.isBodyAt(emptyPosition),
                "Should not detect body at empty position");
    }

    @Test
    void testGetHead() {
        // ทดสอบ: การดึงตำแหน่งหัว
        // เหตุผล: ตรวจสอบว่า getHead คืนค่าตำแหน่งหัวที่ถูกต้อง
        Point head = snake.getHead();
        assertEquals(snake.getXPositions().getFirst(), head.x,
                "Head X should match first X position");
        assertEquals(snake.getYPositions().getFirst(), head.y,
                "Head Y should match first Y position");
    }

    @Test
    void testGetXPositions_ReturnsCopy() {
        // ทดสอบ: getXPositions ควรคืนค่าเป็น copy
        // เหตุผล: ป้องกันการแก้ไขจากภายนอก
        List<Integer> positions = snake.getXPositions();
        positions.set(0, 999);

        assertNotEquals(999, snake.getXPositions().get(0),
                "Original snake positions should not be affected by external changes");
    }

    @Test
    void testGetYPositions_ReturnsCopy() {
        // ทดสอบ: getYPositions ควรคืนค่าเป็น copy
        // เหตุผล: ป้องกันการแก้ไขจากภายนอก
        List<Integer> positions = snake.getYPositions();
        positions.set(0, 999);

        assertNotEquals(999, snake.getYPositions().get(0),
                "Original snake positions should not be affected by external changes");
    }
}