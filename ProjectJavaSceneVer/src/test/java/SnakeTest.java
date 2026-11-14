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
 * ทดสอบการทำงานทั้งหมดของงู รวมถึงการเคลื่อนที่ การเจริญเติบโต การเสริมพลัง และสถานะพิเศษต่างๆ
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ต้องมั่นใจว่าการเคลื่อนที่ถูกต้อง
 * 2. ตรวจสอบการจัดการสถานะต่างๆ (เสริมพลัง, การชน)
 * 3. ป้องกัน bug จากการเลี้ยวกลับทาง
 * 4. ตรวจสอบระบบคอมโบและคะแนนโบนัส
 * 5. ตรวจสอบสถานะพิเศษ (Shield, Ghost Mode, Freeze) และการวาร์ปหัวงู
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

    @Test
    void testDecreaseScore() {
        // ทดสอบการลดคะแนน: คะแนนต้องลดลงตามจำนวนและสามารถติดลบได้ตามกติกาข้อ 4
        snake.decreaseScore(2);
        assertEquals(-2, snake.getFoodEaten(), "Score should decrease to -2");

        snake.decreaseScore(3);
        assertEquals(-5, snake.getFoodEaten(), "Score should decrease to -5");
    }

    @Test
    void testShrinkSafely() {
        // ทดสอบการลดความยาวเมื่อโดนยิง:
        // 1. เริ่มต้นความยาว 3: ไม่สามารถหดได้อีกเพื่อรักษาระดับขนาดต่ำสุด
        assertFalse(snake.shrinkSafely(), "Should not shrink below starting length 3");
        assertEquals(3, snake.getLength(), "Length should remain 3");

        // 2. ขยายขนาดให้ยาวเป็น 5: ต้องหดได้สำเร็จทีละ 1 หน่วยจนกระทั่งเหลือ 3
        snake.grow(); // 4
        snake.grow(); // 5
        assertEquals(5, snake.getLength());

        assertTrue(snake.shrinkSafely(), "First shrink should succeed");
        assertEquals(4, snake.getLength(), "Length should decrease to 4");

        assertTrue(snake.shrinkSafely(), "Second shrink should succeed");
        assertEquals(3, snake.getLength(), "Length should decrease to 3");

        assertFalse(snake.shrinkSafely(), "Third shrink should fail because it reached minimum size");
        assertEquals(3, snake.getLength(), "Length should remain 3");
    }

    @Test
    void testCanShootConstraints() {
        // ทดสอบเงื่อนไขการยิงกระสุน:
        // 1. เริ่มต้นความยาว 3: ไม่ควรยิงได้ (เพราะถ้ายิงจะสั้นลงเหลือ 0 ซึ่งต่ำกว่าความยาวเริ่มต้น)
        assertFalse(snake.canShoot(), "Should not be able to shoot with length 3");
        assertFalse(snake.shoot(), "Shoot action should fail with length 3");

        // 2. ขยายขนาดให้ยาวเป็น 5: ก็ยังยิงไม่ได้ (เพราะถ้ายาว 5 ลด 3 จะเหลือ 2 ซึ่งต่ำกว่าความยาวเริ่มต้น 3)
        snake.grow(); // 4
        snake.grow(); // 5
        assertFalse(snake.canShoot(), "Should not be able to shoot with length 5");
        assertFalse(snake.shoot(), "Shoot action should fail with length 5");

        // 3. ขยายขนาดให้ยาวเป็น 6: ต้องยิงได้สำเร็จ (ยาว 6 ลด 3 เหลือ 3 ซึ่งเท่ากับความยาวเริ่มต้นพอดี)
        snake.grow(); // 6
        assertTrue(snake.canShoot(), "Should be able to shoot with length 6");
        assertTrue(snake.shoot(), "Shoot action should succeed with length 6");
        assertEquals(3, snake.getLength(), "Length should reduce by 3 after shoot");
    }

    @Test
    void testShootCooldown() throws InterruptedException {
        // ทดสอบระบบคูลดาวน์กระสุน:
        // ขยายขนาดให้ยาว 9 เพื่อให้ยิงได้ 2 นัด
        snake.grow(); // 4
        snake.grow(); // 5
        snake.grow(); // 6
        snake.grow(); // 7
        snake.grow(); // 8
        snake.grow(); // 9

        // ยิงนัดที่ 1: ต้องสำเร็จ
        assertTrue(snake.shoot(), "First shot should succeed");

        // ยิงทันทีนัดที่ 2: ต้องล้มเหลวเนื่องจากติดคูลดาวน์
        assertFalse(snake.canShoot(), "Second shot should be blocked by cooldown");
        assertFalse(snake.shoot(), "Second shoot action should fail due to cooldown");

        // รอ 500 ms (คูลดาวน์สิ้นสุด)
        Thread.sleep(Constants.BULLET_COOLDOWN_MS + 10);

        // ยิงนัดที่ 2 หลังผ่านพ้นคูลดาวน์: ต้องสำเร็จ
        assertTrue(snake.canShoot(), "Second shot should be available after cooldown");
        assertTrue(snake.shoot(), "Second shot should succeed after cooldown");
        assertEquals(3, snake.getLength(), "Length should be 3 after two shots");
    }

    @Test
    void testComboSystem() {
        // ทดสอบ: ระบบคอมโบเมื่อกินอาหารต่อเนื่องภายในเวลาที่กำหนด
        // เหตุผล: ตรวจสอบการสะสมตัวคูณคอมโบ และต้องไม่เกินเพดานสูงสุดที่ 5x
        assertEquals(0, snake.getComboCount(), "Initial combo should be 0");

        snake.grow();
        assertEquals(1, snake.getComboCount(), "First eat sets combo to 1");

        snake.grow();
        assertEquals(2, snake.getComboCount(), "Second eat within window increases combo to 2");

        snake.grow();
        assertEquals(3, snake.getComboCount(), "Third eat within window increases combo to 3");

        snake.grow();
        snake.grow();
        assertEquals(5, snake.getComboCount(), "Fifth eat within window reaches max combo of 5");

        snake.grow();
        assertEquals(5, snake.getComboCount(), "Combo should cap at 5");
    }

    @Test
    void testAddBonusScore() {
        // ทดสอบ: การเพิ่มคะแนนโบนัสพิเศษ
        // เหตุผล: ตรวจสอบว่า addBonusScore เพิ่มคะแนน foodEaten ได้ถูกต้อง
        int initialScore = snake.getFoodEaten();
        snake.addBonusScore(3);
        assertEquals(initialScore + 3, snake.getFoodEaten(), "Score should increase by bonus amount");
    }

    @Test
    void testShieldActivationAndDeactivation() {
        // ทดสอบ: การเปิดและปิดใช้งานเกราะป้องกัน (Shield)
        // เหตุผล: ตรวจสอบการจัดการสถานะ shieldActive สำหรับป้องกันการชน
        assertFalse(snake.isShieldActive(), "Shield should be inactive initially");

        snake.activateShield();
        assertTrue(snake.isShieldActive(), "Shield should be active after activation");

        snake.deactivateShield();
        assertFalse(snake.isShieldActive(), "Shield should be inactive after deactivation");
    }

    @Test
    void testGhostModeActivationAndDeactivation() {
        // ทดสอบ: การเปิดและปิดใช้งานโหมดล่องหน (Ghost Mode)
        // เหตุผล: ตรวจสอบการจัดการสถานะ ghostModeActive และเวลาที่เหลืออยู่
        assertFalse(snake.isGhostModeActive(), "Ghost mode should be inactive initially");

        snake.activateGhostMode(3000);
        assertTrue(snake.isGhostModeActive(), "Ghost mode should be active after activation");
        assertTrue(snake.getGhostModeRemainingTime() > 0, "Remaining time should be positive");

        snake.deactivateGhostMode();
        assertFalse(snake.isGhostModeActive(), "Ghost mode should be inactive after deactivation");
        assertEquals(0, snake.getGhostModeRemainingTime(), "Remaining time should be 0");
    }

    @Test
    void testFreezeActivationAndDeactivation() {
        // ทดสอบ: การแช่แข็งและยกเลิกการแช่แข็ง (Freeze)
        // เหตุผล: ตรวจสอบการจัดการสถานะ frozen และการนับเวลาแช่แข็ง
        assertFalse(snake.isFrozen(), "Snake should not be frozen initially");

        snake.freeze(3000);
        assertTrue(snake.isFrozen(), "Snake should be frozen after freeze()");
        assertTrue(snake.getFreezeRemainingTime() > 0, "Freeze remaining time should be positive");

        snake.unfreeze();
        assertFalse(snake.isFrozen(), "Snake should not be frozen after unfreeze()");
        assertEquals(0, snake.getFreezeRemainingTime(), "Freeze remaining time should be 0");
    }

    @Test
    void testTeleportHead() {
        // ทดสอบ: การย้ายตำแหน่งหัวงูทันที (teleportHead)
        // เหตุผล: ใช้สำหรับระบบวาร์ปเมื่อเกราะแตกกรณีชนขอบจอ โดยลำตัวส่วนอื่นต้องไม่ขยับตาม
        int oldBody1X = snake.getXPositions().get(1);
        int oldBody1Y = snake.getYPositions().get(1);

        snake.teleportHead(950, 650);

        assertEquals(950, snake.getHead().x, "Head X should be teleported to 950");
        assertEquals(650, snake.getHead().y, "Head Y should be teleported to 650");
        assertEquals(950, snake.getX(), "GameObject X should match teleported head X");
        assertEquals(650, snake.getY(), "GameObject Y should match teleported head Y");

        // ลำตัวตำแหน่งอื่นต้องไม่ขยับ
        assertEquals(oldBody1X, snake.getXPositions().get(1), "Body segment 1 X should not change");
        assertEquals(oldBody1Y, snake.getYPositions().get(1), "Body segment 1 Y should not change");
    }
}