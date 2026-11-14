import SnakeGame2P.controller.PowerBoostManager;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ PowerBoostManager
 * ทดสอบการจัดการเสริมพลังแยกจาก GameController
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบการเปิด/ปิดเสริมพลังตามเวลา
 * 2. ตรวจสอบการหดตัวเมื่อถึงเวลา
 * 3. ตรวจสอบการป้องกันการเสริมพลังเมื่อตัวสั้นเกินไป
 */
public class PowerBoostManagerTest {

    private PowerBoostManager manager;
    private Snake snake;
    private Snake snake2;

    @BeforeEach
    void setUp() {
        manager = new PowerBoostManager();
        snake = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
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
        // ทดสอบ: การเปิดใช้งานเสริมพลัง
        // เหตุผล: ตรวจสอบว่า manager เปิดโหมดให้งูได้ถูกต้อง
        assertFalse(snake.isPowerBoostActive(), "Initially power boost should be inactive");

        manager.activatePowerBoost(snake);
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active after activation");
    }

    @Test
    void testActivatePowerBoost_WhenTooShort() {
        // ทดสอบ: ไม่สามารถเปิดเสริมพลังเมื่อตัวสั้นเกินไป
        // เหตุผล: ต้องป้องกันการเสริมพลังเมื่อ length <= 3

        // ทำให้งูสั้นลงเหลือ 3 โดยสร้างใหม่ (งูใหม่เริ่มที่ length 3)
        Snake shortSnake = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);

        manager.activatePowerBoost(shortSnake);
        assertFalse(shortSnake.isPowerBoostActive(),
                "Should not activate power boost when length <= 3");
    }

    @Test
    void testDeactivatePowerBoost() {
        // ทดสอบ: การปิดใช้งานเสริมพลัง
        // เหตุผล: ตรวจสอบว่า manager ปิดโหมดให้งูได้ถูกต้อง
        manager.activatePowerBoost(snake);
        assertTrue(snake.isPowerBoostActive(), "Power boost should be active");

        manager.deactivatePowerBoost(snake);
        assertFalse(snake.isPowerBoostActive(), "Power boost should be inactive after deactivation");
    }

    @Test
    void testHandlePowerBoosts_ActiveButNotTimeToShrink() {
        // ทดสอบ: เมื่อเสริมพลังแต่ยังไม่ถึงเวลาหด
        // เหตุผล: ควรยังไม่หดตัวจนกว่าจะครบ interval

        manager.activatePowerBoost(snake);
        long beforeShrinkTime = snake.getLastShrinkTime();
        int beforeLength = snake.getLength();

        // เรียก handlePowerBoosts โดยไม่ต้องรอเวลา
        manager.handlePowerBoosts(snake, snake2);

        assertEquals(beforeLength, snake.getLength(),
                "Snake should not shrink without time passing");
        assertEquals(beforeShrinkTime, snake.getLastShrinkTime(),
                "Last shrink time should not change");
    }

    @Test
    void testHandlePowerBoosts_ShrinkWhenActive() {
        // ทดสอบ: การหดตัวเมื่อเสริมพลัง (จำลองเวลาโดยการแก้ไข lastShrinkTime)
        // เหตุผล: ควรหดตัวเมื่อถึงเวลา

        manager.activatePowerBoost(snake);
        int beforeLength = snake.getLength();

        // แก้ไข lastShrinkTime ให้เก่ากว่าปัจจุบันมากกว่า interval
        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);

        manager.handlePowerBoosts(snake, snake2);

        assertTrue(snake.getLength() < beforeLength,
                "Snake should shrink after interval passed");
    }

    @Test
    void testHandlePowerBoosts_MultipleShrinks() {
        // ทดสอบ: การหดตัวหลายครั้งเมื่อเสริมพลังค้างไว้
        // เหตุผล: ควรหดตัวต่อเนื่องทุกๆ interval

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
        // ทดสอบ: การปิดโหมดอัตโนมัติเมื่อเหลือความยาว 3
        // เหตุผล: ป้องกันไม่ให้งูสั้นเกินไป

        manager.activatePowerBoost(snake);

        // หดจนเหลือ 3
        while (snake.getLength() > 3) {
            snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
            manager.handlePowerBoosts(snake, snake2);
        }

        assertEquals(3, snake.getLength(), "Length should be 3");
        assertFalse(snake.isPowerBoostActive(),
                "Power boost should auto-deactivate at length 3");

        // จำลองเวลาผ่านไปอีกครั้ง แล้วบันทึกค่าก่อนเรียก handle
        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        long lastShrinkTime = snake.getLastShrinkTime();

        // เรียก handle อีกครั้ง (power boost ไม่ทำงานแล้ว ไม่ควรมีอะไรเปลี่ยนแปลง)
        manager.handlePowerBoosts(snake, snake2);

        // ตรวจสอบว่าความยาวไม่เปลี่ยนแปลง
        assertEquals(3, snake.getLength(), "Length should not go below 3");

        // ตรวจสอบว่า lastShrinkTime ไม่เปลี่ยนแปลง (เพราะ power boost ไม่ทำงานแล้ว)
        assertEquals(lastShrinkTime, snake.getLastShrinkTime(),
                "Last shrink time should not change when power boost is inactive");
    }
    @Test
    void testHandlePowerBoosts_WithTwoSnakes() {
        // ทดสอบ: จัดการงูสองตัวพร้อมกัน
        // เหตุผล: ตรวจสอบว่า manager จัดการงูทั้งสองตัวได้

        manager.activatePowerBoost(snake);
        manager.activatePowerBoost(snake2);

        int snake1Length = snake.getLength();
        int snake2Length = snake2.getLength();

        // แก้ไข lastShrinkTime ทั้งสองตัว
        snake.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);
        snake2.setLastShrinkTime(System.currentTimeMillis() - Constants.SPEED_BOOST_SHRINK_INTERVAL - 1000);

        manager.handlePowerBoosts(snake, snake2);

        assertTrue(snake.getLength() < snake1Length, "Snake1 should shrink");
        assertTrue(snake2.getLength() < snake2Length, "Snake2 should shrink");
    }
}