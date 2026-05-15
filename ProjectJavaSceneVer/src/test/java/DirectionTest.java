import SnakeGame2P.utils.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class สำหรับ Direction Enum
 * ทดสอบการทำงานของทิศทางและการตรวจสอบทิศตรงข้าม
 *
 * เหตุผลที่ต้องทดสอบ:
 * 1. ตรวจสอบ logic การห้ามเลี้ยวกลับทาง
 * 2. ตรวจสอบความถูกต้องของ enum values
 */
public class DirectionTest {

    @Test
    void testEnumValues() {
        // ทดสอบ: Enum ควรมีค่าครบทั้ง 4 ทิศทาง
        // เหตุผล: ตรวจสอบว่า Direction enum ถูกต้อง
        Direction[] directions = Direction.values();
        assertEquals(5, directions.length, "Should have 5 enum values (including NONE)");

        assertTrue(contains(directions, Direction.UP), "Should contain UP");
        assertTrue(contains(directions, Direction.DOWN), "Should contain DOWN");
        assertTrue(contains(directions, Direction.LEFT), "Should contain LEFT");
        assertTrue(contains(directions, Direction.RIGHT), "Should contain RIGHT");
        assertTrue(contains(directions, Direction.NONE), "Should contain NONE");
    }

    @Test
    void testIsOpposite_CorrectPairs() {
        // ทดสอบ: คู่ทิศทางตรงข้ามควรถูกต้อง
        // เหตุผล: ตรวจสอบว่า UP ตรงข้ามกับ DOWN เท่านั้น

        // UP
        assertTrue(Direction.UP.isOpposite(Direction.DOWN), "UP should be opposite of DOWN");
        assertFalse(Direction.UP.isOpposite(Direction.UP), "UP should not be opposite of UP");
        assertFalse(Direction.UP.isOpposite(Direction.LEFT), "UP should not be opposite of LEFT");
        assertFalse(Direction.UP.isOpposite(Direction.RIGHT), "UP should not be opposite of RIGHT");

        // DOWN
        assertTrue(Direction.DOWN.isOpposite(Direction.UP), "DOWN should be opposite of UP");
        assertFalse(Direction.DOWN.isOpposite(Direction.DOWN), "DOWN should not be opposite of DOWN");

        // LEFT
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT), "LEFT should be opposite of RIGHT");
        assertFalse(Direction.LEFT.isOpposite(Direction.LEFT), "LEFT should not be opposite of LEFT");

        // RIGHT
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT), "RIGHT should be opposite of LEFT");
        assertFalse(Direction.RIGHT.isOpposite(Direction.RIGHT), "RIGHT should not be opposite of RIGHT");
    }

    @Test
    void testIsOpposite_WithNone() {
        // ทดสอบ: NONE ไม่ควรตรงข้ามกับทิศใด
        // เหตุผล: NONE ใช้สำหรับสถานะพิเศษ ไม่ใช่ทิศทางการเคลื่อนที่

        assertFalse(Direction.NONE.isOpposite(Direction.UP), "NONE should not be opposite of UP");
        assertFalse(Direction.NONE.isOpposite(Direction.DOWN), "NONE should not be opposite of DOWN");
        assertFalse(Direction.NONE.isOpposite(Direction.LEFT), "NONE should not be opposite of LEFT");
        assertFalse(Direction.NONE.isOpposite(Direction.RIGHT), "NONE should not be opposite of RIGHT");
        assertFalse(Direction.NONE.isOpposite(Direction.NONE), "NONE should not be opposite of NONE");
    }

    private boolean contains(Direction[] directions, Direction target) {
        for (Direction d : directions) {
            if (d == target) return true;
        }
        return false;
    }
}