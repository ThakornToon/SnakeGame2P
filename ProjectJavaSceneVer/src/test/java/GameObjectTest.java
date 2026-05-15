import SnakeGame2P.model.entity.GameObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ GameObject abstract class
 * ทดสอบการทำงานพื้นฐานของ GameObject และการ implement Collidable
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบว่า GameObject ทำงานถูกต้องตาม base class
 * 2. ทดสอบการทำงานของ Collidable interface
 * 3. ตรวจสอบการจัดการตำแหน่งและสี
 */
public class GameObjectTest {

    // สร้าง concrete class สำหรับทดสอบ abstract class
    private static class TestGameObject extends GameObject {
        public TestGameObject(int x, int y) {
            super(x, y);
        }

        @Override
        public void draw(Graphics g) {
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
        // ทดสอบ: การสร้าง object และ getters ควรทำงานถูกต้อง
        // เหตุผล: ตรวจสอบว่า constructor กำหนดค่าเริ่มต้นถูกต้อง
        assertEquals(100, obj1.getX(), "X position should be 100");
        assertEquals(100, obj1.getY(), "Y position should be 100");
        assertEquals(new Point(100, 100), obj1.getPosition(), "Position should be (100,100)");
    }

    @Test
    void testSetPosition() {
        // ทดสอบ: การเปลี่ยนตำแหน่งควรทำงานถูกต้อง
        // เหตุผล: ตรวจสอบว่า method setPosition เปลี่ยนค่าจริง
        obj1.setPosition(300, 400);
        assertEquals(300, obj1.getX(), "X should be updated to 300");
        assertEquals(400, obj1.getY(), "Y should be updated to 400");
        assertEquals(new Point(300, 400), obj1.getPosition(), "Position should be updated");
    }

    @Test
    void testIsCollidingWith_SamePosition() {
        // ทดสอบ: วัตถุที่ตำแหน่งเดียวกันควรชนกัน
        // เหตุผล: ตรวจสอบ logic การชนพื้นฐาน
        obj2.setPosition(100, 100);
        assertTrue(obj1.isCollidingWith(obj2),
                "Objects at same position should collide");
    }

    @Test
    void testIsCollidingWith_DifferentPosition() {
        // ทดสอบ: วัตถุคนละตำแหน่งไม่ควรชนกัน
        // เหตุผล: ตรวจสอบว่าไม่แจ้งการชนผิดพลาด
        assertFalse(obj1.isCollidingWith(obj2),
                "Objects at different positions should not collide");
    }

    @Test
    void testGetPositionReturnsCopy() {
        // ทดสอบ: getPosition ควรคืนค่าเป็น copy ไม่ใช่ reference เดิม
        // เหตุผล: ป้องกันการแก้ไขตำแหน่งจากภายนอกโดยไม่ตั้งใจ
        Point pos = obj1.getPosition();
        pos.setLocation(999, 999);

        assertEquals(100, obj1.getX(),
                "Original object should not be affected by changes to returned Point");
        assertEquals(100, obj1.getY(),
                "Original object should not be affected by changes to returned Point");
    }
}