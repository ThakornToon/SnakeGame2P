import SnakeGame2P.model.entity.Bullet;
import SnakeGame2P.model.entity.Snake;
import SnakeGame2P.model.entity.GameObject;
import SnakeGame2P.utils.Constants;
import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับคลาส Bullet (กระสุน)
 * ทดสอบการทำงานทั้งหมดของกระสุน รวมถึงการเคลื่อนที่ การตรวจจับขอบจอ และการชนกับเป้าหมาย
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบว่ากระสุนถูกสร้างด้วยค่าเริ่มต้นที่ถูกต้องและเชื่อมโยงกับผู้ยิง
 * 2. ตรวจสอบการเคลื่อนที่ตรงตามทิศทางและความเร็ว
 * 3. ตรวจสอบการตรวจจับการหลุดขอบเขตหน้าจอ
 * 4. ตรวจสอบการชนกับงูเป้าหมาย (ทั้งหัวและลำตัว) และการชนกับ GameObject อื่น
 */
public class BulletTest {

    private Snake shooter;
    private Snake target;
    private Bullet bullet;

    @BeforeEach
    void setUp() {
        shooter = new Snake(200, 200, Direction.RIGHT,
                Color.GREEN, Color.DARK_GRAY, Color.CYAN);
        target = new Snake(400, 200, Direction.LEFT,
                Color.ORANGE, Color.RED, Color.PINK);
        bullet = new Bullet(200, 200, Direction.RIGHT, shooter, Color.GREEN);
    }

    @Test
    void testBulletInitialization() {
        // ทดสอบ: ค่าเริ่มต้นของกระสุนควรถูกต้องตามที่กำหนดใน Constructor
        // เหตุผล: ตรวจสอบพิกัดเริ่มต้น ทิศทาง ผู้ยิง และสถานะขอบเขต
        assertEquals(200, bullet.getX(), "Initial X should be 200");
        assertEquals(200, bullet.getY(), "Initial Y should be 200");
        assertEquals(Direction.RIGHT, bullet.getDirection(), "Initial direction should be RIGHT");
        assertSame(shooter, bullet.getShooter(), "Shooter should match the constructor argument");
        assertFalse(bullet.isOutOfBounds(), "Bullet at (200,200) should be in bounds");
    }

    @Test
    void testBulletMove() {
        // ทดสอบ: การเคลื่อนที่ของกระสุนตามทิศทางต่างๆ
        // เหตุผล: ตรวจสอบว่าเมธอด move() ขยับกระสุนทีละ UNIT_SIZE ได้ถูกต้องทุกทิศทาง

        // เคลื่อนที่ไปทางขวา 1 ครั้ง
        bullet.move();
        assertEquals(200 + Constants.UNIT_SIZE, bullet.getX(), "X should increase by UNIT_SIZE");
        assertEquals(200, bullet.getY(), "Y should remain the same");

        // เปลี่ยนทิศทางเป็น UP
        bullet.setDirection(Direction.UP);
        assertEquals(Direction.UP, bullet.getDirection());
        bullet.move();
        assertEquals(200 - Constants.UNIT_SIZE, bullet.getY(), "Y should decrease by UNIT_SIZE");

        // เปลี่ยนทิศทางเป็น DOWN
        bullet.setDirection(Direction.DOWN);
        bullet.move();
        assertEquals(200, bullet.getY(), "Y should return to 200");

        // เปลี่ยนทิศทางเป็น LEFT
        bullet.setDirection(Direction.LEFT);
        bullet.move();
        assertEquals(200, bullet.getX(), "X should decrease back to 200");
    }

    @Test
    void testBulletOutOfBounds() {
        // ทดสอบ: การตรวจจับกระสุนหลุดออกจากขอบเขตหน้าจอ
        // เหตุผล: เพื่อให้ระบบสามารถลบกระสุนที่วิ่งออกนอกจอทิ้งได้ ป้องกัน memory leak

        // กรณีอยู่ภายในจอ
        Bullet inside = new Bullet(100, 100, Direction.RIGHT, shooter, Color.GREEN);
        assertFalse(inside.isOutOfBounds(), "Bullet at (100,100) should be inside board");

        // กรณีหลุดขอบซ้าย (x < 0)
        Bullet left = new Bullet(-1, 100, Direction.LEFT, shooter, Color.GREEN);
        assertTrue(left.isOutOfBounds(), "Bullet at X < 0 should be out of bounds");

        // กรณีหลุดขอบขวา (x >= BOARD_WIDTH)
        Bullet right = new Bullet(Constants.BOARD_WIDTH, 100, Direction.RIGHT, shooter, Color.GREEN);
        assertTrue(right.isOutOfBounds(), "Bullet at X >= BOARD_WIDTH should be out of bounds");

        // กรณีหลุดขอบบน (y < 0)
        Bullet top = new Bullet(100, -1, Direction.UP, shooter, Color.GREEN);
        assertTrue(top.isOutOfBounds(), "Bullet at Y < 0 should be out of bounds");

        // กรณีหลุดขอบล่าง (y >= BOARD_HEIGHT)
        Bullet bottom = new Bullet(100, Constants.BOARD_HEIGHT, Direction.DOWN, shooter, Color.GREEN);
        assertTrue(bottom.isOutOfBounds(), "Bullet at Y >= BOARD_HEIGHT should be out of bounds");
    }

    @Test
    void testBulletCollidingWithSnake_Hit() {
        // ทดสอบ: การตรวจจับการชนของกระสุนกับตัวงูเป้าหมาย
        // เหตุผล: กระสุนต้องตรวจจับการชนได้ทั้งส่วนหัวและส่วนลำตัวของงู

        // 1. วางกระสุนตรงตำแหน่งหัวของ target
        Point targetHead = target.getHead();
        Bullet hittingHead = new Bullet(targetHead.x, targetHead.y, Direction.RIGHT, shooter, Color.GREEN);
        assertTrue(hittingHead.isCollidingWith(target), "Bullet should collide with target's head");

        // 2. วางกระสุนตรงตำแหน่งลำตัวของ target (index 1)
        int bodyX = target.getXPositions().get(1);
        int bodyY = target.getYPositions().get(1);
        Bullet hittingBody = new Bullet(bodyX, bodyY, Direction.RIGHT, shooter, Color.GREEN);
        assertTrue(hittingBody.isCollidingWith(target), "Bullet should collide with target's body segment");
    }

    @Test
    void testBulletCollidingWithSnake_Miss() {
        // ทดสอบ: กระสุนที่ไม่ตรงกับตำแหน่งงู ต้องไม่ถือว่าชน
        // เหตุผล: ป้องกันการเกิด false positive ในการตรวจจับการชน
        Bullet missing = new Bullet(0, 0, Direction.RIGHT, shooter, Color.GREEN);
        assertFalse(missing.isCollidingWith(target), "Bullet should not collide when coordinates do not match snake");
    }

    @Test
    void testBulletCollidingWithGameObject() {
        // ทดสอบ: การชนของกระสุนกับ GameObject ทั่วไป (เช่น อาหาร)
        // เหตุผล: เพื่อให้กระสุนสามารถทำลายอาหารเมื่อพิกัดทับซ้อนกันได้
        GameObject testObj = new GameObject(300, 300) {
            @Override
            public void draw(Graphics g) {}
        };
        Bullet bulletSamePos = new Bullet(300, 300, Direction.UP, shooter, Color.GREEN);
        Bullet bulletDiffPos = new Bullet(100, 100, Direction.UP, shooter, Color.GREEN);

        assertTrue(bulletSamePos.isCollidingWith(testObj), "Bullet should collide with GameObject at same position");
        assertFalse(bulletDiffPos.isCollidingWith(testObj), "Bullet should not collide with GameObject at different position");
    }
}
